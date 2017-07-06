package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Filter;

import lombok.val;

import com.btc.prosport.api.model.SubwayStation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class SubwayAdapter extends SimpleSpinnerAdapter<SubwayStation> {
    public SubwayAdapter(@NonNull final Context context) {
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

    public void setSubways(final Collection<SubwayStation> subways) {
        synchronized (_lock) {
            _originalValues = subways;
        }
    }

    @Override
    public void sort(
        @NonNull final Comparator<? super SubwayStation> comparator) {
        super.sort(comparator);
    }

    @Override
    public long getItemId(final int position) {
        val item = getItem(position);

        if (item != null) {
            return item.getId();
        }
        return -1;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence prefix) {
                final FilterResults results = new FilterResults();

                final ArrayList<SubwayStation> list;
                synchronized (_lock) {
                    if (_originalValues != null) {
                        list = new ArrayList<>(_originalValues);
                    } else {
                        return results;
                    }
                }

                if ((prefix == null || prefix.length() == 0)) {
                    results.values = list;
                    results.count = list.size();
                } else {
                    final String prefixString = prefix.toString().toLowerCase();

                    final ArrayList<SubwayStation> newValues = new ArrayList<>();

                    for (final val value : list) {
                        final String valueText;
                        if (value != null) {
                            val name = value.getName();
                            if (name != null) {
                                valueText = name.toLowerCase();
                                if (valueText.startsWith(prefixString)) {
                                    newValues.add(value);
                                } else {
                                    final String[] words = valueText.split(" ");
                                    for (final val word : words) {
                                        if (word.startsWith(prefixString)) {
                                            newValues.add(value);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    results.values = newValues;
                    results.count = newValues.size();
                }

                return results;
            }

            @Override
            protected void publishResults(
                final CharSequence constraint, final FilterResults results) {
                if (results.count > 0) {
                    //noinspection unchecked
                    val filteredList = (ArrayList<SubwayStation>) results.values;
                    setNotifyOnChange(false);
                    clear();
                    addAll(filteredList);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(final Object resultValue) {
                return ((SubwayStation) resultValue).getName();
            }
        };
    }

    private final Object _lock = new Object();

    private Collection<SubwayStation> _originalValues;
}
