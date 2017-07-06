package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.ViewBinder;

public class SimpleViewHolder implements ViewBinder {
    public View itemView;

    public SimpleViewHolder(final View itemView) {
        this.itemView = itemView;
    }

    @Override
    public void bindViews() {
        unbindViews();

        _unbinder = ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindViews(@NonNull final View source) {
        Contracts.requireNonNull(source, "source == null");

        unbindViews();

        _unbinder = ButterKnife.bind(this, source);
    }

    @Override
    public void unbindViews() {
        if (_unbinder != null) {
            _unbinder.unbind();
        }
    }

    private Unbinder _unbinder;
}