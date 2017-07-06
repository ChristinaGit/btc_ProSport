package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import lombok.val;

import butterknife.BindView;

import com.btc.common.contract.Contracts;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder
    .SimpleViewHolder;

import java.util.List;

public class SimpleSpinnerAdapter<T> extends ArrayAdapter<T> {
    private static final int _DROPDOWN_ITEM = android.R.layout.simple_spinner_dropdown_item;

    public SimpleSpinnerAdapter(
        @NonNull final Context context) {
        super(context, _DROPDOWN_ITEM);

        _inflater = LayoutInflater.from(context);
    }

    public SimpleSpinnerAdapter(
        @NonNull final Context context, @NonNull final List<T> objects) {
        super(context, _DROPDOWN_ITEM, objects);

        _inflater = LayoutInflater.from(context);
    }

    @Nullable
    public String getLabel(final int position) {
        val item = getItem(position);
        if (item != null) {
            return item.toString();
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(
        final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        return createViewFromResource(_inflater, position, convertView, parent, _DROPDOWN_ITEM);
    }

    @Override
    public void setDropDownViewResource(@LayoutRes final int resource) {
        super.setDropDownViewResource(resource);

        throw new UnsupportedOperationException();
    }

    @NonNull
    private final LayoutInflater _inflater;

    private LayoutInflater _dropDownInflater;

    @NonNull
    private View createViewFromResource(
        @NonNull final LayoutInflater inflater,
        final int position,
        @Nullable final View convertView,
        @NonNull final ViewGroup parent,
        final int resource) {
        Contracts.requireNonNull(inflater, "inflater == null");
        Contracts.requireNonNull(parent, "parent == null");

        final LabelViewHolder labelViewHolder;
        final View itemView;

        if (convertView == null) {
            itemView = inflater.inflate(resource, parent, false);
            labelViewHolder = new LabelViewHolder(itemView);
            itemView.setTag(labelViewHolder);
        } else {
            itemView = convertView;
            //noinspection unchecked
            labelViewHolder = (LabelViewHolder) itemView.getTag();
        }

        labelViewHolder.label.setText(getLabel(position));

        return itemView;
    }

    public class LabelViewHolder extends SimpleViewHolder {
        public LabelViewHolder(final View itemView) {
            super(itemView);

            bindViews();
        }

        @SuppressWarnings({"InstanceVariableNamingConvention", "NullableProblems"})
        @BindView(android.R.id.text1)
        @NonNull
        TextView label;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setDropDownViewTheme(@Nullable final Resources.Theme theme) {
        if (theme == null) {
            _dropDownInflater = null;
        } else if (theme == _inflater.getContext().getTheme()) {
            _dropDownInflater = _inflater;
        } else {
            final val context = new ContextThemeWrapper(_inflater.getContext(), theme);
            _dropDownInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public View getDropDownView(
        final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        val inflater = _dropDownInflater == null ? _inflater : _dropDownInflater;
        return createViewFromResource(inflater, position, convertView, parent, _DROPDOWN_ITEM);
    }

    @Override
    @Nullable
    public Resources.Theme getDropDownViewTheme() {
        return _dropDownInflater == null ? null : _dropDownInflater.getContext().getTheme();
    }
}
