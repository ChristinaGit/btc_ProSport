package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import lombok.experimental.Accessors;

import butterknife.BindView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

@Accessors(prefix = "_")
public class PlaceViewHolder extends ExtendedRecyclerViewHolder {
    @BindView(R.id.address_input)
    @NonNull
    public TextInputEditText addressInput;

    @BindView(R.id.address_input_layout)
    @NonNull
    public TextInputLayout addressInputLayout;

    @BindView(R.id.coordinates_input)
    @NonNull
    public TextInputEditText coordinatesInput;

    @BindView(R.id.coordinates_input_layout)
    @NonNull
    public TextInputLayout coordinatesInputLayout;

    @BindView(R.id.map_error)
    @NonNull
    public TextView mapError;

    @BindView(R.id.map_layout)
    @NonNull
    public FrameLayout mapLayout;

    @BindView(R.id.map_progressbar)
    @NonNull
    public ContentLoaderProgressBar mapProgressBar;

    @BindView(R.id.map_view)
    @NonNull
    public ImageView mapView;

    public PlaceViewHolder(
        @NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        bindViews(itemView);
    }
}
