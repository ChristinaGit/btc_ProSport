package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lombok.experimental.Accessors;

import butterknife.BindView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

@Accessors(prefix = "_")
public class TitleViewHolder extends ExtendedRecyclerViewHolder {
    @BindView(R.id.title)
    @NonNull
    public TextView _titleView;

    public TitleViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        bindViews(itemView);
    }
}
