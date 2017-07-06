package com.btc.prosport.player.core.adapter.playgroundsList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.recyclerView.adapter.ModifiableRecyclerViewListAdapter;
import com.btc.prosport.api.model.PlaygroundPreview;
import com.btc.prosport.player.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public final class PlaygroundsAdapter
    extends ModifiableRecyclerViewListAdapter<PlaygroundPreview, PlaygroundViewHolder> {
    @NonNull
    public final Event<IdEventArgs> getReservePlaygroundEvent() {
        return _reservePlaygroundEvent;
    }

    @Override
    public void onBindInnerItemViewHolder(
        @NonNull final PlaygroundViewHolder holder, final int position) {
        Contracts.requireNonNull(holder, "holder == null");

        final val playground = getItem(position);

        final val context = holder.getContext();

        holder.playgroundReserveActionView.setTag(R.id.tag_view_playground_id, playground.getId());

        holder.playgroundNameView.setText(playground.getName());

        final val covering = playground.getCovering();
        final val coveringName = covering == null ? null : covering.getName();
        if (!TextUtils.isEmpty(coveringName)) {
            holder.playgroundCoveringView.setVisibility(View.VISIBLE);
            final val formattedCoveringName =
                context.getString(R.string.playgrounds_list_playground_covering_format,
                                  coveringName);
            holder.playgroundCoveringView.setText(formattedCoveringName);
        } else {
            holder.playgroundCoveringView.setVisibility(View.GONE);
            holder.playgroundCoveringView.setText(null);
        }

        final val formattedSize = getPlaygroundFormattedSize(context, playground);
        if (!TextUtils.isEmpty(formattedSize)) {
            holder.playgroundSizeView.setText(formattedSize);

            holder.playgroundSizeView.setVisibility(View.VISIBLE);
        } else {
            holder.playgroundSizeView.setText(null);

            holder.playgroundSizeView.setVisibility(View.GONE);
        }

        if (_loadPhotoErrorIcon == null) {
            final val loadPhotoErrorIcon =
                ContextCompat.getDrawable(context, R.drawable.ic_material_error);

            final int loadPhotoErrorIconColor =
                ContextCompat.getColor(context, R.color.foreground_dark);
            DrawableCompat.setTint(loadPhotoErrorIcon, loadPhotoErrorIconColor);

            _loadPhotoErrorIcon = loadPhotoErrorIcon;
        }

        final val photo = playground.getPhoto();
        final val photoUri = photo == null ? null : photo.getUri();

        if (!TextUtils.isEmpty(photoUri)) {
            holder.playgroundPhotoView.setVisibility(View.VISIBLE);

            Glide
                .with(context)
                .load(photoUri)
                .error(_loadPhotoErrorIcon)
                .fallback(_loadPhotoErrorIcon)
                .animate(R.anim.fade_in_long)
                .centerCrop()
                .into(holder.playgroundPhotoView);
        } else {
            holder.playgroundPhotoView.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public PlaygroundViewHolder onCreateInnerItemViewHolder(
        final ViewGroup parent, final int viewType) {
        final val view = inflateView(R.layout.fragment_playgrounds_list_item, parent);

        final val holder = new PlaygroundViewHolder(view);

        holder.playgroundReserveActionView.setOnClickListener(_reservePlaygroundOnClick);

        return holder;
    }

    @Getter(onMethod = @__(@Override))
    @NonNull
    private final List<PlaygroundPreview> _items = new ArrayList<>();

    @NonNull
    private final ManagedEvent<IdEventArgs> _reservePlaygroundEvent = Events.createEvent();

    @NonNull
    private final View.OnClickListener _reservePlaygroundOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final val playgroundId = (long) v.getTag(R.id.tag_view_playground_id);
            _reservePlaygroundEvent.rise(new IdEventArgs(playgroundId));
        }
    };

    @Nullable
    private Drawable _loadPhotoErrorIcon;

    @Nullable
    private String getPlaygroundFormattedSize(
        @NonNull final Context context, @NonNull final PlaygroundPreview playground) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(playground, "playground == null");

        final String formattedSize;

        final val width = playground.getWidth();
        final val length = playground.getLength();

        final val partWidth = playground.getPartWidth();
        final val partLength = playground.getPartLength();

        if (width != null && width > 0 && length != null && length > 0) {
            if (partWidth != null && partWidth > 0 && partLength != null && partLength > 0) {
                formattedSize =
                    context.getString(R.string.playgrounds_list_playground_part_size_format,
                                      width,
                                      length,
                                      partWidth,
                                      partLength);
            } else {
                formattedSize = context.getString(R.string.playgrounds_list_playground_size_format,
                                                  width,
                                                  length);
            }
        } else {
            formattedSize = null;
        }

        return formattedSize;
    }
}
