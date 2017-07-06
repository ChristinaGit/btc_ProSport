package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Spinner;

import lombok.experimental.Accessors;

import butterknife.BindView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

@Accessors(prefix = "_")
public class CityViewHolder extends ExtendedRecyclerViewHolder {
    @BindView(R.id.city_spinner)
    @NonNull
    public Spinner citySpinner;

    public CityViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        bindViews(itemView);
    }
}
