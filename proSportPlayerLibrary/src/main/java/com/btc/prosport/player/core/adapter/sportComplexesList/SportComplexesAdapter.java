package com.btc.prosport.player.core.adapter.sportComplexesList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.recyclerView.adapter.PaginationRecyclerViewListAdapter;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.api.model.SportComplexPreview;
import com.btc.prosport.core.utility.ProSportFormat;
import com.btc.prosport.player.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public final class SportComplexesAdapter
    extends PaginationRecyclerViewListAdapter<SportComplexPreview> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SportComplexesAdapter.class);

    public SportComplexesAdapter() {
    }

    @NonNull
    public final Event<IdEventArgs> getViewSportComplexDetailsEvent() {
        return _viewSportComplexDetailsEvent;
    }

    @Override
    public void onBindInnerItemViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {
        Contracts.requireNonNull(holder, "holder == null");

        final val sportComplexViewHolder = (SportComplexViewHolder) holder;
        final val sportComplex = getItem(position);

        sportComplexViewHolder.itemView.setTag(R.id.tag_view_sport_complex_id,
                                               sportComplex.getId());

        final val context = sportComplexViewHolder.getContext();
        sportComplexViewHolder.sportComplexNameView.setText(sportComplex.getName());

        final val formattedSubwayStations =
            ProSportFormat.getFormattedSubwayStations(sportComplex.getSubwayStations());
        if (!TextUtils.isEmpty(formattedSubwayStations)) {
            sportComplexViewHolder.sportComplexSubwayStationsView.setText(formattedSubwayStations);
            sportComplexViewHolder.sportComplexSubwayStationsView.setVisibility(View.VISIBLE);
        } else {
            sportComplexViewHolder.sportComplexSubwayStationsView.setText(null);
            sportComplexViewHolder.sportComplexSubwayStationsView.setVisibility(View.GONE);
        }

        final val distance = sportComplex.getDistance();
        if (distance != null) {
            final val formattedDistance = getFormattedDistance(context, distance);
            sportComplexViewHolder.sportComplexDistanceView.setText(formattedDistance);
            sportComplexViewHolder.sportComplexDistanceView.setVisibility(View.VISIBLE);
        } else {
            sportComplexViewHolder.sportComplexDistanceView.setText(null);
            sportComplexViewHolder.sportComplexDistanceView.setVisibility(View.GONE);
        }

        final val minimumPrice = sportComplex.getMinimumPrice();
        if (minimumPrice != null) {
            final val formattedMinimumPrice = getFormattedMinimumPrice(context, minimumPrice);
            sportComplexViewHolder.sportComplexMinimumPriceView.setText(formattedMinimumPrice);
            sportComplexViewHolder.sportComplexMinimumPriceView.setVisibility(View.VISIBLE);
        } else {
            sportComplexViewHolder.sportComplexMinimumPriceView.setText(null);
            sportComplexViewHolder.sportComplexMinimumPriceView.setVisibility(View.GONE);
        }

        if (_loadPhotoErrorIcon == null) {
            final val loadPhotoErrorIcon =
                ContextCompat.getDrawable(context, R.drawable.ic_material_error);

            final int loadPhotoErrorIconColor =
                ContextCompat.getColor(context, R.color.foreground_dark);
            DrawableCompat.setTint(loadPhotoErrorIcon, loadPhotoErrorIconColor);

            _loadPhotoErrorIcon = loadPhotoErrorIcon;
        }

        final val photo = sportComplex.getPhoto();
        final val photoUri = photo == null ? null : photo.getUri();

        if (!TextUtils.isEmpty(photoUri)) {
            sportComplexViewHolder.sportComplexPhotoView.setVisibility(View.VISIBLE);

            Glide
                .with(context)
                .load(photoUri)
                .error(_loadPhotoErrorIcon)
                .fallback(_loadPhotoErrorIcon)
                .animate(R.anim.fade_in_long)
                .centerCrop()
                .into(sportComplexViewHolder.sportComplexPhotoView);
        } else {
            sportComplexViewHolder.sportComplexPhotoView.setVisibility(View.GONE);
        }
    }

    @Override
    @NonNull
    public ExtendedRecyclerViewHolder onCreateInnerItemViewHolder(
        final ViewGroup parent, final int viewType) {
        final val view = inflateView(R.layout.fragment_sport_complexes_list_item, parent);

        final val holder = new SportComplexViewHolder(view);

        holder.itemView.setOnClickListener(_viewSportComplexDetailsOnClick);

        setupLocationIcon(holder.sportComplexDistanceView);

        return holder;
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateLoadingViewHolder(
        final ViewGroup parent, final int viewType) {
        final val view = inflateView(R.layout.layout_list_item_loading, parent);

        return new SportComplexesLoadingViewHolder(view);
    }

    protected void setupLocationIcon(@NonNull final TextView textView) {
        Contracts.requireNonNull(textView, "textView == null");

        if (_locationIcon == null) {
            final val context = textView.getContext();
            final val locationIcon =
                ContextCompat.getDrawable(context, R.drawable.ic_material_location_on);
            if (locationIcon != null) {
                DrawableCompat.setTint(locationIcon, textView.getTextColors().getDefaultColor());
            }

            _locationIcon = locationIcon;
        }

        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(_locationIcon, null, null, null);
    }

    @Getter(onMethod = @__(@Override))
    @NonNull
    private final List<SportComplexPreview> _items = new ArrayList<>();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewSportComplexDetailsEvent = Events.createEvent();

    @NonNull
    private final View.OnClickListener _viewSportComplexDetailsOnClick =
        new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final long sportComplexId = (long) v.getTag(R.id.tag_view_sport_complex_id);
                _viewSportComplexDetailsEvent.rise(new IdEventArgs(sportComplexId));
            }
        };

    @Nullable
    private Drawable _loadPhotoErrorIcon;

    @Nullable
    private Drawable _locationIcon;

    @NonNull
    private String getFormattedDistance(@NonNull final Context context, final long distance) {
        Contracts.requireNonNull(context, "context == null");

        final String formattedDistance;

        if (distance >= 10_000) {
            formattedDistance = context.getString(R.string.sport_complexes_list_distance_far_format,
                                                  distance / 1000);
        } else if (distance > 1_000) {
            formattedDistance =
                context.getString(R.string.sport_complexes_list_distance_format, distance / 1000d);
        } else {
            formattedDistance =
                context.getString(R.string.sport_complexes_list_distance_near_format, distance);
        }

        return formattedDistance;
    }

    @NonNull
    private String getFormattedMinimumPrice(@NonNull final Context context, final int minPrice) {
        return context.getString(R.string.sport_complexes_list_minimum_price_format, minPrice);
    }
}
