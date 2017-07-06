package com.btc.prosport.manager.core.adapter.orderFilter.item;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;

@Accessors(prefix = "_")
public class FilterItem {
    public FilterItem(final long id, @NonNull final String name, @DrawableRes final int icon) {
        Contracts.requireNonNull(name, "name == null");

        _id = id;
        _name = name;
        _icon = icon;
    }

    @Getter
    @DrawableRes
    private final int _icon;

    @Getter
    private final long _id;

    @Getter
    @NonNull
    private final String _name;
}
