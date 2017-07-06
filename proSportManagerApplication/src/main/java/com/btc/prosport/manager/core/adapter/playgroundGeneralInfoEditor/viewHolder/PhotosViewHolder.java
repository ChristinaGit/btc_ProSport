package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import lombok.experimental.Accessors;

import butterknife.BindView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

@Accessors(prefix = "_")
public class PhotosViewHolder extends ExtendedRecyclerViewHolder {
    @BindView(R.id.photos_list)
    @NonNull
    public RecyclerView photosList;

    public PhotosViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        bindViews(itemView);
    }
}
