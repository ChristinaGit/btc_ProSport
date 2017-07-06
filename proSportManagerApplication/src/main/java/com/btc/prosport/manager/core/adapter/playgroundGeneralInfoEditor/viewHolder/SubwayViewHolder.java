package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.AutoCompleteTextView;

import lombok.Getter;
import lombok.experimental.Accessors;

import butterknife.BindView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoAdapter;

@Accessors(prefix = "_")
public class SubwayViewHolder extends ExtendedRecyclerViewHolder {
    @BindView(R.id.subway_input)
    @NonNull
    public AutoCompleteTextView _subwayInput;

    @BindView(R.id.subway_input_layout)
    public TextInputLayout subwayInputLayout;

    public SubwayViewHolder(
        @NonNull final View itemView,
        @NonNull final GeneralInfoAdapter.SubwayOnItemClick subwayOnItemClick) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));
        Contracts.requireNonNull(subwayOnItemClick, "subwayItemSelected == null");

        _subwayOnItemClick = subwayOnItemClick;

        bindViews(itemView);
    }

    @Getter
    @NonNull
    private final GeneralInfoAdapter.SubwayOnItemClick _subwayOnItemClick;
}
