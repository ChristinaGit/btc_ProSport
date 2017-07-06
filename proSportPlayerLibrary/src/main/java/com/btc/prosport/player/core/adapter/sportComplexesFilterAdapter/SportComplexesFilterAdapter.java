package com.btc.prosport.player.core.adapter.sportComplexesFilterAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.listView.adapter.ArrayAdapter;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.SportComplexesFilter;

import java.util.ArrayList;
import java.util.Arrays;

public final class SportComplexesFilterAdapter extends ArrayAdapter<SportComplexesFilter> {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(SportComplexesFilterAdapter.class);

    public SportComplexesFilterAdapter(@NonNull final Context context) {
        super(
            Contracts.requireNonNull(context, "context == null"),
            R.layout.layout_spinner_item,
            new ArrayList<>(Arrays.asList(SportComplexesFilter.values(false))));

        setNotifyOnChange(false);
        setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
    }

    public void setAllowUserSpecificFilters(final boolean allowUserSpecificFilters) {
        if (_allowUserSpecificFilters != allowUserSpecificFilters) {
            _allowUserSpecificFilters = allowUserSpecificFilters;

            clear();
            addAll(SportComplexesFilter.values(allowUserSpecificFilters));

            notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    protected CharSequence getItemName(@Nullable final SportComplexesFilter item) {
        if (item == SportComplexesFilter.ALL) {
            return getContext().getString(R.string.sport_complexes_viewer_filer_all);
        } else if (item == SportComplexesFilter.FAVORITES) {
            return getContext().getString(R.string.sport_complexes_viewer_filer_favorites);
        } else {
            return super.getItemName(item);
        }
    }

    @Getter
    private boolean _allowUserSpecificFilters = false;
}
