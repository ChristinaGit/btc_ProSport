package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.val;

import com.btc.prosport.api.model.City;

import java.util.Comparator;

public class CityAdapter extends SimpleSpinnerAdapter<City> {
    public CityAdapter(@NonNull final Context context) {
        super(context);
    }

    @Nullable
    @Override
    public String getLabel(final int position) {
        val item = getItem(position);

        if (item != null) {
            return item.getName();
        }
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void sort(
        @NonNull final Comparator<? super City> comparator) {
        super.sort(comparator);
    }

    // TODO: Check, if adapter hasn`t items, position = -1.
    @Override
    public long getItemId(final int position) {

        if (position >= 0) {
            val item = getItem(position);

            if (item != null) {
                return item.getId();
            }
        }
        return -1;
    }
}
