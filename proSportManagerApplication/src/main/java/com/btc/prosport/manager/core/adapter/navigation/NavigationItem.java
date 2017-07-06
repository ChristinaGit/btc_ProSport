package com.btc.prosport.manager.core.adapter.navigation;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;

@Accessors(prefix = "_")
public final class NavigationItem {
    public NavigationItem(
        final int iconId, final int titleId, @NonNull final Runnable navigateAction) {
        Contracts.requireNonNull(navigateAction, "navigateAction == null");

        _iconId = iconId;
        _titleId = titleId;
        _navigateAction = navigateAction;
    }

    @Getter
    @DrawableRes
    private final int _iconId;

    @Getter
    @NonNull
    private final Runnable _navigateAction;

    @Getter
    @StringRes
    private final int _titleId;
}
