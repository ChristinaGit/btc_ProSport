package com.btc.prosport.manager.core.adapter.orderFilter.item;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.prosport.manager.core.FilterType;

import java.util.List;

@Accessors(prefix = "_")
public class FilterGroupItem {
    public FilterGroupItem(
        @NonNull final FilterType filterType,
        @NonNull final String title,
        @NonNull final List<FilterItem> filterItems) {
        Contracts.requireNonNull(filterType, "filterType == null");
        Contracts.requireNonNull(title, "title == null");
        Contracts.requireNonNull(filterItems, "filterItems == null");

        _filterType = filterType;
        _title = title;
        _filterItems = filterItems;
    }

    @Getter
    @NonNull
    private final List<FilterItem> _filterItems;

    @Getter
    @NonNull
    private final FilterType _filterType;

    @Getter
    @NonNull
    private final String _title;
}
