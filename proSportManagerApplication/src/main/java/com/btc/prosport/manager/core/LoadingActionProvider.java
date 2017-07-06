package com.btc.prosport.manager.core;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.prosport.manager.R;

@Accessors(prefix = "_")
public class LoadingActionProvider extends ActionProvider {
    private static final String _LOG_TAG = ConstantBuilder.logTag(LoadingActionProvider.class);

    public LoadingActionProvider(@NonNull final Context context) {
        super(Contracts.requireNonNull(context, "context == null"));
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public View onCreateActionView(final MenuItem forItem) {
        super.onCreateActionView(forItem);

        final val view =
            LayoutInflater.from(getContext()).inflate(R.layout.layout_action_loading, null, false);
        final val progressBarView = (ProgressBar) view.findViewById(R.id.progress);

        DrawableCompat.setTint(progressBarView.getIndeterminateDrawable(), Color.WHITE);

        return view;
    }
}
