package com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;

import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public class AddPhotoViewHolder extends ExtendedRecyclerViewHolder {
    @BindView(R.id.add_photo_button)
    @NonNull
    public ImageButton addPhotoButton;

    public AddPhotoViewHolder(@NonNull final View itemView) {
        super(itemView);

        bindViews(itemView);
    }
}
