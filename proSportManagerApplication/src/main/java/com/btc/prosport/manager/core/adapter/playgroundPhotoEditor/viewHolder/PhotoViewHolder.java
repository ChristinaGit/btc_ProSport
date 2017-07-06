package com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.viewHolder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import butterknife.BindView;

import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

@Accessors(prefix = "_")
public class PhotoViewHolder extends ExtendedRecyclerViewHolder {
    @BindView(R.id.photo)
    @NonNull
    public ImageView photoView;

    @BindView(R.id.remove_button)
    @NonNull
    public ImageView removeButton;

    public PhotoViewHolder(@NonNull final View itemView) {
        super(itemView);

        bindViews(itemView);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                val removePhotoListener = getRemovePhotoListener();
                if (removePhotoListener != null) {
                    removePhotoListener.onRemove(getAdapterPosition());
                }
            }
        });
    }

    @Getter
    @Setter
    @Nullable
    RemovePhotoListener _removePhotoListener;

    public interface RemovePhotoListener {
        void onRemove(int adapterPosition);
    }
}
