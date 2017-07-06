package com.btc.prosport.manager.core.adapter.playgroundPhotoEditor;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.view.recyclerView.adapter.RecyclerViewAdapter;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.item.AddPhotoItem;
import com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.item.PhotoItem;
import com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.viewHolder.AddPhotoViewHolder;
import com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.viewHolder.PhotoViewHolder;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public class PhotosAdapter extends RecyclerViewAdapter<ExtendedRecyclerViewHolder> {
    public static final int VIEW_TYPE_PHOTO;

    public static final int VIEW_TYPE_ADD_PHOTO;

    static {
        int viewTypeIndexer = 0;

        VIEW_TYPE_PHOTO = ++viewTypeIndexer;
        VIEW_TYPE_ADD_PHOTO = ++viewTypeIndexer;
    }

    public void addPhotoItems(
        @NonNull final List<PhotoItem> photoItems, final boolean notifyAdapter) {
        Contracts.requireNonNull(photoItems, "photoItems == null");

        val currentPhotoItems = getPhotoItems();
        final int positionStart = currentPhotoItems.size();

        currentPhotoItems.addAll(photoItems);

        if (notifyAdapter) {
            notifyItemRangeInserted(positionStart, photoItems.size());
        }
    }

    @NonNull
    public Item getItem(final int position) {
        val photoItems = getPhotoItems();
        if (photoItems.size() > position) {
            return photoItems.get(position);
        } else {
            return new AddPhotoItem();
        }
    }

    public NoticeEvent getPickPhotoEvent() {
        return _pickPhotoEvent;
    }

    @Override
    public ExtendedRecyclerViewHolder onCreateViewHolder(
        final ViewGroup parent, final int viewType) {

        final ExtendedRecyclerViewHolder viewHolder;
        if (VIEW_TYPE_PHOTO == viewType) {
            viewHolder = onCreatePhotoViewHolder(parent);
        } else if (VIEW_TYPE_ADD_PHOTO == viewType) {
            viewHolder = onCreateAddPhotoViewHolder(parent);
        } else {
            throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {

        final val item = getItem(position);

        if (VIEW_TYPE_PHOTO == holder.getItemViewType()) {
            onBindPhotoViewHolder((PhotoViewHolder) holder, (PhotoItem) item);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return getItem(position).getItemType();
    }

    @Override
    public int getItemCount() {
        return getPhotoItems().size() + 1;
    }

    public void removePhotoItems(final int adapterPosition) {
        getPhotoItems().remove(adapterPosition);

        notifyItemRemoved(adapterPosition);
    }

    protected void onBindPhotoViewHolder(
        @NonNull final PhotoViewHolder photoViewHolder, @NonNull final PhotoItem photoItem) {
        Contracts.requireNonNull(photoViewHolder, "photoViewHolder == null");
        Contracts.requireNonNull(photoItem, "photoItem == null");

        Glide
            .with(photoViewHolder.getContext())
            .load(photoItem.getUri())
            .centerCrop()
            .animate(R.anim.fade_in_long)
            .into(photoViewHolder.photoView);
    }

    @NonNull
    protected AddPhotoViewHolder onCreateAddPhotoViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        val inflater = LayoutInflater.from(parent.getContext());

        val view = inflater.inflate(R.layout.fragment_playground_general_info_editor_add_photo_item,
                                    parent,
                                    false);

        val addPhotoViewHolder = new AddPhotoViewHolder(view);
        addPhotoViewHolder.addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                _pickPhotoEvent.rise();
            }
        });

        return addPhotoViewHolder;
    }

    @NonNull
    protected PhotoViewHolder onCreatePhotoViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        val inflater = LayoutInflater.from(parent.getContext());

        val view = inflater.inflate(R.layout.fragment_playground_general_info_editor_photo_item,
                                    parent,
                                    false);

        val photoViewHolder = new PhotoViewHolder(view);
        photoViewHolder.setRemovePhotoListener(new PhotoViewHolder.RemovePhotoListener() {
            @Override
            public void onRemove(final int adapterPosition) {
                removePhotoItems(adapterPosition);
            }
        });
        return photoViewHolder;
    }

    @NonNull
    private final ManagedNoticeEvent _pickPhotoEvent = Events.createNoticeEvent();

    @NonNull
    @Getter
    @Setter
    private List<PhotoItem> _photoItems = new ArrayList<>();
}
