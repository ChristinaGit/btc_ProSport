package com.btc.prosport.player.core.adapter.sportComplexDetails;

import android.graphics.PorterDuff;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.eventArgs.PositionEventArgs;
import com.btc.common.extension.gilde.viewTarget.TextViewTarget;
import com.btc.common.extension.view.recyclerView.adapter.RecyclerViewAdapter;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration;
import com.btc.common.extension.view.recyclerView.decoration.SpacingItemDecoration;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.common.utility.ThemeUtils;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.adapter.photos.PhotosAdapter;
import com.btc.prosport.player.core.adapter.photos.PhotosAdapter.OnPhotoClickListener;
import com.btc.prosport.player.core.adapter.sportComplexDetails.item.SportComplexAttributeItem;
import com.btc.prosport.player.core.adapter.sportComplexDetails.item.SportComplexContactItem;
import com.btc.prosport.player.core.adapter.sportComplexDetails.item.SportComplexDescriptionItem;
import com.btc.prosport.player.core.adapter.sportComplexDetails.item.SportComplexDetailsItem;
import com.btc.prosport.player.core.adapter.sportComplexDetails.item.SportComplexInventoryItem;
import com.btc.prosport.player.core.adapter.sportComplexDetails.item.SportComplexPhotosItem;
import com.btc.prosport.player.core.adapter.sportComplexDetails.item.SportComplexPropertyItem;
import com.btc.prosport.player.core.adapter.sportComplexDetails.viewHolder
    .SportComplexAttributeViewHolder;
import com.btc.prosport.player.core.adapter.sportComplexDetails.viewHolder
    .SportComplexContactViewHolder;
import com.btc.prosport.player.core.adapter.sportComplexDetails.viewHolder
    .SportComplexDescriptionViewHolder;
import com.btc.prosport.player.core.adapter.sportComplexDetails.viewHolder
    .SportComplexInventoryViewHolder;
import com.btc.prosport.player.core.adapter.sportComplexDetails.viewHolder
    .SportComplexPhotosViewHolder;
import com.btc.prosport.player.core.adapter.sportComplexDetails.viewHolder
    .SportComplexPropertyViewHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public final class SportComplexDetailsAdapter
    extends RecyclerViewAdapter<ExtendedRecyclerViewHolder> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SportComplexDetailsAdapter.class);

    public static final int VIEW_TYPE_SPORT_COMPLEX_PHOTOS;

    public static final int VIEW_TYPE_SPORT_COMPLEX_DESCRIPTION;

    public static final int VIEW_TYPE_SPORT_COMPLEX_PROPERTY;

    public static final int VIEW_TYPE_SPORT_COMPLEX_CONTACT;

    public static final int VIEW_TYPE_SPORT_COMPLEX_ATTRIBUTE;

    public static final int VIEW_TYPE_SPORT_COMPLEX_INVENTORY;

    public static final int[] VIEW_TYPE_ORDER;

    static {
        VIEW_TYPE_SPORT_COMPLEX_PHOTOS = newViewType();
        VIEW_TYPE_SPORT_COMPLEX_DESCRIPTION = newViewType();
        VIEW_TYPE_SPORT_COMPLEX_PROPERTY = newViewType();
        VIEW_TYPE_SPORT_COMPLEX_CONTACT = newViewType();
        VIEW_TYPE_SPORT_COMPLEX_ATTRIBUTE = newViewType();
        VIEW_TYPE_SPORT_COMPLEX_INVENTORY = newViewType();

        VIEW_TYPE_ORDER = new int[]{
            VIEW_TYPE_SPORT_COMPLEX_PHOTOS,
            VIEW_TYPE_SPORT_COMPLEX_DESCRIPTION,
            VIEW_TYPE_SPORT_COMPLEX_PROPERTY,
            VIEW_TYPE_SPORT_COMPLEX_CONTACT,
            VIEW_TYPE_SPORT_COMPLEX_ATTRIBUTE,
            VIEW_TYPE_SPORT_COMPLEX_INVENTORY};
    }

    @CallSuper
    public void clear() {
        getSportComplexAttributeItems().clear();
        getSportComplexContactItems().clear();
        getSportComplexPropertyItems().clear();
        setSportComplexPhotosItem(null);
        setSportComplexDescriptionItem(null);
        setSportComplexInventoryItem(null);
    }

    @NonNull
    public SportComplexDetailsItem getItem(final int position, final int viewType) {
        SportComplexDetailsItem item = null;

        if (VIEW_TYPE_SPORT_COMPLEX_PHOTOS == viewType) {
            if (position == 0) {
                item = getSportComplexPhotosItem();
            }
        } else if (VIEW_TYPE_SPORT_COMPLEX_DESCRIPTION == viewType) {
            item = getSportComplexDescriptionItem();
        } else if (VIEW_TYPE_SPORT_COMPLEX_PROPERTY == viewType) {
            item = getSportComplexPropertyItems().get(position);
        } else if (VIEW_TYPE_SPORT_COMPLEX_CONTACT == viewType) {
            item = getSportComplexContactItems().get(position);
        } else if (VIEW_TYPE_SPORT_COMPLEX_ATTRIBUTE == viewType) {
            item = getSportComplexAttributeItems().get(position);
        } else if (VIEW_TYPE_SPORT_COMPLEX_INVENTORY == viewType) {
            item = getSportComplexInventoryItem();
        } else {
            throw new IllegalArgumentException("Unknown view type: " + viewType);
        }

        if (item == null) {
            throw new IllegalArgumentException(
                "Item not found. Position: " + position + "; View type: " + viewType);
        }

        return item;
    }

    @NonNull
    public SportComplexDetailsItem getItem(final int position) {
        SportComplexDetailsItem item = null;

        int currentItemCount = 0;
        for (final int viewType : VIEW_TYPE_ORDER) {
            final int typedItemCount = getItemCount(viewType);
            currentItemCount += typedItemCount;

            if (position < currentItemCount) {
                item = getItem(position - currentItemCount + typedItemCount, viewType);
                break;
            }
        }

        if (item == null) {
            throw new IllegalArgumentException("Item not found. Position: " + position);
        }

        return item;
    }

    @CallSuper
    public int getItemCount(final int viewType) {
        final int itemCount;

        if (VIEW_TYPE_SPORT_COMPLEX_PHOTOS == viewType) {
            final val sportComplexPhotosItem = getSportComplexPhotosItem();
            if (sportComplexPhotosItem != null) {
                final val sportComplexPhotosUris = sportComplexPhotosItem.getPhotosUris();
                if (sportComplexPhotosUris != null && !sportComplexPhotosUris.isEmpty()) {
                    itemCount = 1;
                } else {
                    itemCount = 0;
                }
            } else {
                itemCount = 0;
            }
        } else if (VIEW_TYPE_SPORT_COMPLEX_DESCRIPTION == viewType) {
            final val sportComplexDescriptionItem = getSportComplexDescriptionItem();
            if (sportComplexDescriptionItem != null) {
                itemCount = 1;
            } else {
                itemCount = 0;
            }
        } else if (VIEW_TYPE_SPORT_COMPLEX_INVENTORY == viewType) {
            final val sportComplexInventoryItem = getSportComplexInventoryItem();
            if (sportComplexInventoryItem != null) {
                itemCount = 1;
            } else {
                itemCount = 0;
            }
        } else if (VIEW_TYPE_SPORT_COMPLEX_PROPERTY == viewType) {
            itemCount = getSportComplexPropertyItems().size();
        } else if (VIEW_TYPE_SPORT_COMPLEX_CONTACT == viewType) {
            itemCount = getSportComplexContactItems().size();
        } else if (VIEW_TYPE_SPORT_COMPLEX_ATTRIBUTE == viewType) {
            itemCount = getSportComplexAttributeItems().size();
        } else {
            throw new IllegalArgumentException("Unknown view type: " + viewType);
        }

        return itemCount;
    }

    @NonNull
    public Event<PositionEventArgs> getViewSportComplexPhotosEvent() {
        return _viewSportComplexPhotosEvent;
    }

    @Override
    public ExtendedRecyclerViewHolder onCreateViewHolder(
        final ViewGroup parent, final int viewType) {
        final ExtendedRecyclerViewHolder viewHolder;

        if (VIEW_TYPE_SPORT_COMPLEX_PHOTOS == viewType) {
            viewHolder = onCreateSportComplexPhotosViewHolder(parent);
        } else if (VIEW_TYPE_SPORT_COMPLEX_DESCRIPTION == viewType) {
            viewHolder = onCreateSportComplexDescriptionViewHolder(parent);
        } else if (VIEW_TYPE_SPORT_COMPLEX_PROPERTY == viewType) {
            viewHolder = onCreateSportComplexPropertyViewHolder(parent);
        } else if (VIEW_TYPE_SPORT_COMPLEX_CONTACT == viewType) {
            viewHolder = onCreateSportComplexContactViewHolder(parent);
        } else if (VIEW_TYPE_SPORT_COMPLEX_ATTRIBUTE == viewType) {
            viewHolder = onCreateSportComplexAttributeViewHolder(parent);
        } else if (VIEW_TYPE_SPORT_COMPLEX_INVENTORY == viewType) {
            viewHolder = onCreateSportComplexInventoryViewHolder(parent);
        } else {
            throw new IllegalArgumentException("Unknown view type: " + viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ExtendedRecyclerViewHolder holder, final int position) {
        final val item = getItem(position);

        final int viewType = item.getItemType();

        if (VIEW_TYPE_SPORT_COMPLEX_PHOTOS == viewType) {
            onBindSportComplexPhotosViewHolder((SportComplexPhotosViewHolder) holder,
                                               (SportComplexPhotosItem) item,
                                               position);
        } else if (VIEW_TYPE_SPORT_COMPLEX_DESCRIPTION == viewType) {
            onBindSportComplexDescriptionViewHolder((SportComplexDescriptionViewHolder) holder,
                                                    (SportComplexDescriptionItem) item,
                                                    position);
        } else if (VIEW_TYPE_SPORT_COMPLEX_PROPERTY == viewType) {
            onBindSportComplexPropertyViewHolder((SportComplexPropertyViewHolder) holder,
                                                 (SportComplexPropertyItem) item,
                                                 position);
        } else if (VIEW_TYPE_SPORT_COMPLEX_CONTACT == viewType) {
            onBindSportComplexContactViewHolder((SportComplexContactViewHolder) holder,
                                                (SportComplexContactItem) item,
                                                position);
        } else if (VIEW_TYPE_SPORT_COMPLEX_ATTRIBUTE == viewType) {
            onBindSportComplexAttributeViewHolder((SportComplexAttributeViewHolder) holder,
                                                  (SportComplexAttributeItem) item,
                                                  position);
        } else if (VIEW_TYPE_SPORT_COMPLEX_INVENTORY == viewType) {
            onBindSportComplexInventoryViewHolder((SportComplexInventoryViewHolder) holder,
                                                  (SportComplexInventoryItem) item,
                                                  position);
        } else {
            throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return getItem(position).getItemType();
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;

        for (final int viewType : VIEW_TYPE_ORDER) {
            itemCount += getItemCount(viewType);
        }

        return itemCount;
    }

    protected void onBindSportComplexAttributeViewHolder(
        @NonNull final SportComplexAttributeViewHolder holder,
        @NonNull final SportComplexAttributeItem item,
        final int position) {
        Contracts.requireNonNull(holder, "holder == null");
        Contracts.requireNonNull(item, "item == null");

        final val sportComplexAttributeItems = getSportComplexAttributeItems();
        final int itemIndex = sportComplexAttributeItems.indexOf(item);

        final boolean hideDivider = itemIndex != sportComplexAttributeItems.size() - 1;
        DividerItemDecoration.setDecorationMode(holder.itemView,
                                                hideDivider
                                                ? DividerItemDecoration.DecorationMode.NONE
                                                : DividerItemDecoration.DecorationMode.ALL);

        holder.attributeView.setText(item.getAttribute());

        final int iconSize = holder
            .getContext()
            .getResources()
            .getDimensionPixelSize(R.dimen.sport_complex_details_attribute_icon_size);

        byte[] iconData;

        try {
            iconData = Base64.decode(item.getIcon(), Base64.DEFAULT);
        } catch (final IllegalArgumentException error) {
            Log.w(_LOG_TAG, "Failed to decode base64 icon", error);
            iconData = null;
        }

        Glide
            .with(holder.getContext())
            .load(iconData)
            .fitCenter()
            .override(iconSize, iconSize)
            .into(new IconTextViewTarget(holder.attributeView,
                                         TextViewTarget.DrawablePosition.START));
    }

    protected void onBindSportComplexContactViewHolder(
        @NonNull final SportComplexContactViewHolder holder,
        @NonNull final SportComplexContactItem item,
        final int position) {
        Contracts.requireNonNull(holder, "holder == null");
        Contracts.requireNonNull(item, "item == null");

        final val sportComplexContactItems = getSportComplexContactItems();
        final int itemIndex = sportComplexContactItems.indexOf(item);

        final boolean hideDivider = itemIndex != sportComplexContactItems.size() - 1;
        DividerItemDecoration.setDecorationMode(holder.itemView,
                                                hideDivider
                                                ? DividerItemDecoration.DecorationMode.NONE
                                                : DividerItemDecoration.DecorationMode.ALL);

        final val info = item.getInfo();

        if (info instanceof Spanned) {
            holder.contactView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            holder.contactView.setMovementMethod(null);
        }

        holder.contactView.setText(info);

        final val context = holder.getContext();
        final val icon = ContextCompat.getDrawable(context, item.getIconId());
        final val tintList =
            ThemeUtils.resolveColorStateList(context, android.R.attr.textColorSecondary);
        if (tintList != null) {
            DrawableCompat.setTint(icon, tintList.getDefaultColor());
        }
        holder.contactView.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
    }

    protected void onBindSportComplexInventoryViewHolder(
        @NonNull final SportComplexInventoryViewHolder holder,
        @NonNull final SportComplexInventoryItem item,
        final int position) {
        Contracts.requireNonNull(holder, "holder == null");
        Contracts.requireNonNull(item, "item == null");

        DividerItemDecoration.setDecorationMode(holder.itemView,
                                                DividerItemDecoration.DecorationMode.NONE);

        final val context = holder.getContext();

        //@formatter:off
        final val inventoryChargeableHint = context.getString(
            item.isInventoryChargeable()
            ? R.string.sport_complex_viewer_inventory_chargeable
            : R.string.sport_complex_viewer_inventory_not_chargeable);
        //@formatter:on
        holder.inventoryChargeableView.setText(inventoryChargeableHint);
        holder.inventoryDescriptionView.setText(item.getInventoryDescription());
    }

    protected void onBindSportComplexPhotosViewHolder(
        @NonNull final SportComplexPhotosViewHolder holder,
        @NonNull final SportComplexPhotosItem item,
        final int position) {
        Contracts.requireNonNull(holder, "holder == null");
        Contracts.requireNonNull(item, "item == null");

        final val photosAdapter = holder.getTag(PhotosAdapter.class, R.id.tag_view_cache);
        if (photosAdapter != null) {
            final val photosUris = photosAdapter.getPhotosUris();
            photosUris.clear();
            final val sportComplexPhotosUris = item.getPhotosUris();
            if (sportComplexPhotosUris != null) {
                photosUris.addAll(sportComplexPhotosUris);
            }

            holder.photosIndicatorView.setIndicatorCount(photosAdapter.getCount());

            photosAdapter.setOnPhotoClickListener(_viewSportComplexPhotosOnClick);
        }
        holder.photosView.setAdapter(photosAdapter);

        final int currentItem = holder.photosView.getCurrentItem();
        holder.photosIndicatorView.setSelectedItem(currentItem);
    }

    protected void onBindSportComplexPropertyViewHolder(
        @NonNull final SportComplexPropertyViewHolder holder,
        @NonNull final SportComplexPropertyItem item,
        final int position) {
        Contracts.requireNonNull(holder, "holder == null");
        Contracts.requireNonNull(item, "item == null");

        holder.propertyNameView.setText(item.getPropertyName());
        holder.propertyValueView.setText(item.getPropertyValue());
    }

    @NonNull
    protected SportComplexAttributeViewHolder onCreateSportComplexAttributeViewHolder(
        @NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.fragment_sport_complex_viewer_attribute_item, parent);
        return new SportComplexAttributeViewHolder(view);
    }

    @NonNull
    protected SportComplexContactViewHolder onCreateSportComplexContactViewHolder(
        @NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.fragment_sport_complex_viewer_contact_item, parent);
        return new SportComplexContactViewHolder(view);
    }

    @NonNull
    protected SportComplexDescriptionViewHolder onCreateSportComplexDescriptionViewHolder(
        @NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view =
            inflateView(R.layout.fragment_sport_complex_viewer_description_item, parent);
        return new SportComplexDescriptionViewHolder(view);
    }

    @NonNull
    protected SportComplexPhotosViewHolder onCreateSportComplexPhotosViewHolder(
        @NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.fragment_sport_complex_viewer_photos_item, parent);
        final val holder = new SportComplexPhotosViewHolder(view);

        holder.photosView.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                super.onPageSelected(position);

                holder.photosIndicatorView.setSelectedItem(position);
            }
        });

        SpacingItemDecoration.setDecorationMode(holder.itemView,
                                                SpacingItemDecoration.DecorationMode.NONE);
        holder.setTag(R.id.tag_view_cache, new PhotosAdapter());

        return holder;
    }

    @NonNull
    protected SportComplexPropertyViewHolder onCreateSportComplexPropertyViewHolder(
        @NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.fragment_sport_complex_viewer_property_item, parent);
        return new SportComplexPropertyViewHolder(view);
    }

    @Getter
    @NonNull
    private final List<SportComplexAttributeItem> _sportComplexAttributeItems = new ArrayList<>();

    @Getter
    @NonNull
    private final List<SportComplexContactItem> _sportComplexContactItems = new ArrayList<>();

    @Getter
    @NonNull
    private final List<SportComplexPropertyItem> _sportComplexPropertyItems = new ArrayList<>();

    @NonNull
    private final ManagedEvent<PositionEventArgs> _viewSportComplexPhotosEvent =
        Events.createEvent();

    @NonNull
    private final OnPhotoClickListener _viewSportComplexPhotosOnClick = new OnPhotoClickListener() {
        @Override
        public void onPhotoClick(final int position) {
            _viewSportComplexPhotosEvent.rise(new PositionEventArgs(position));
        }
    };

    @Nullable
    @Getter
    @Setter
    private SportComplexDescriptionItem _sportComplexDescriptionItem;

    @Nullable
    @Getter
    @Setter
    private SportComplexInventoryItem _sportComplexInventoryItem;

    @Nullable
    @Getter
    @Setter
    private SportComplexPhotosItem _sportComplexPhotosItem;

    private void onBindSportComplexDescriptionViewHolder(
        @NonNull final SportComplexDescriptionViewHolder holder,
        @NonNull final SportComplexDescriptionItem item,
        final int position) {
        Contracts.requireNonNull(holder, "holder == null");
        Contracts.requireNonNull(item, "item == null");

        holder.descriptionView.setText(item.getDescription());
    }

    private SportComplexInventoryViewHolder onCreateSportComplexInventoryViewHolder(
        @NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.fragment_sport_complex_viewer_inventory_item, parent);
        return new SportComplexInventoryViewHolder(view);
    }

    private static final class IconTextViewTarget extends TextViewTarget {
        public IconTextViewTarget(
            @NonNull final TextView view, @NonNull final DrawablePosition drawablePosition) {
            super(Contracts.requireNonNull(view, "view == null"),
                  Contracts.requireNonNull(drawablePosition, "drawablePosition == null"));
        }

        @Override
        public void onResourceReady(
            final GlideDrawable resource,
            final GlideAnimation<? super GlideDrawable> glideAnimation) {
            super.onResourceReady(resource, glideAnimation);

            final val context = getView().getContext();
            final val tintList =
                ThemeUtils.resolveColorStateList(context, android.R.attr.textColorSecondary);
            if (tintList != null) {
                resource.setColorFilter(tintList.getDefaultColor(), PorterDuff.Mode.SRC_IN);
            }
        }
    }
}
