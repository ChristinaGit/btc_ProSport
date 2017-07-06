package com.btc.prosport.manager.core.adapter.coveringEditor.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.BindView;

import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public class CoveringViewHolder extends ExtendedRecyclerViewHolder {
    @BindView(R.id.covering)
    @NonNull
    public TextView coveringNameView;

    @BindView(R.id.covering_off_button)
    @NonNull
    public RadioButton coveringOffButton;

    public CoveringViewHolder(@NonNull final View itemView) {
        super(itemView);

        bindViews(itemView);
    }
}
