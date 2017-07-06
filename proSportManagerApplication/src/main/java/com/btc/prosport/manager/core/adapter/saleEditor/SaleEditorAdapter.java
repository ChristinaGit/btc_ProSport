package com.btc.prosport.manager.core.adapter.saleEditor;

import android.content.DialogInterface;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.common.utility.ContextUtils;
import com.btc.common.utility.ImeUtils;
import com.btc.common.utility.listener.SimpleTextWatcher;
import com.btc.prosport.api.model.utility.SaleType;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.repeatableIntervals.RepeatableIntervalsAdapter;
import com.btc.prosport.manager.core.adapter.saleEditor.viewHolder.HeaderViewHolder;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

@Accessors(prefix = "_")
public class SaleEditorAdapter extends RepeatableIntervalsAdapter {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SaleEditorAdapter.class);

    public static final int VIEW_TYPE_PLACE = newViewType();

    public static final int VIEW_TYPE_SALE_TYPE = newViewType();

    public static final int VIEW_TYPE_SALE_VALUE = newViewType();

    private static final int[] FULL_HEADER_ITEMS_VIEW_TYPES = new int[]{
        VIEW_TYPE_PLACE, VIEW_TYPE_SALE_TYPE, VIEW_TYPE_SALE_VALUE};

    private static final int[] SHORT_HEADER_ITEMS_VIEW_TYPES =
        new int[]{VIEW_TYPE_SALE_TYPE, VIEW_TYPE_SALE_VALUE};

    public static final SaleType DEFAULT_SALE_TYPE = SaleType.RELATIVE;

    private static final int[] SALE_TYPES_NAMES_IDS = {
        R.string.sale_editor_sale_type_relative,
        R.string.sale_editor_sale_type_absolute,
        R.string.sale_editor_sale_type_new_price};

    @NonNull
    public final NoticeEvent getPickPlaceEvent() {
        return _pickPlaceEvent;
    }

    @NonNull
    public final SaleType getSaleType() {
        return ObjectUtils.defaultIfNull(_saleType, DEFAULT_SALE_TYPE);
    }

    public final void setSaleType(@Nullable final SaleType saleType) {
        if (!Objects.equals(getSaleType(), saleType)) {
            _saleType = saleType;

            notifyItemChanged(ArrayUtils.indexOf(getHeaderViewTypes(), VIEW_TYPE_SALE_TYPE));
            notifyItemChanged(ArrayUtils.indexOf(getHeaderViewTypes(), VIEW_TYPE_SALE_VALUE));
        }
    }

    public final void setPlace(@Nullable final CharSequence place) {
        _place = place;

        notifyPlaceChanged();
    }

    public final void setPlaceSelectionEnabled(final boolean placeSelectionEnabled) {
        if (_placeSelectionEnabled != placeSelectionEnabled) {
            _placeSelectionEnabled = placeSelectionEnabled;

            notifyDataSetChanged();
        }
    }

    public final void setSaleValue(@Nullable final Integer saleValue) {
        if (!Objects.equals(_saleValue, saleValue)) {
            _saleValue = saleValue;

            notifyItemChanged(ArrayUtils.indexOf(getHeaderViewTypes(), VIEW_TYPE_SALE_VALUE));
        }
    }

    @Override
    public int getHeaderItemCount() {
        return super.getHeaderItemCount() + getHeaderViewTypes().length;
    }

    @Override
    protected int getHeaderItemViewType(final int position) {
        return getHeaderViewTypes()[getHeaderItemRelativePosition(position)];
    }

    @Override
    protected boolean isHeaderItemViewType(final int viewType) {
        return super.isHeaderItemViewType(viewType) ||
               ArrayUtils.contains(getHeaderViewTypes(), viewType);
    }

    @Override
    protected void onBindHeaderItemViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {
        final val viewType = getHeaderItemViewType(position);

        if (VIEW_TYPE_PLACE == viewType) {
            onBindPlaceViewHolder((HeaderViewHolder) holder);
        } else if (VIEW_TYPE_SALE_TYPE == viewType) {
            onBindSaleTypeViewHolder((HeaderViewHolder) holder);
        } else if (VIEW_TYPE_SALE_VALUE == viewType) {
            onBindSaleValueViewHolder((HeaderViewHolder) holder);
        } else {
            super.onBindHeaderItemViewHolder(holder, viewType);
        }
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateHeaderItemViewHolder(
        final ViewGroup parent, final int viewType) {
        final ExtendedRecyclerViewHolder holder;

        if (VIEW_TYPE_PLACE == viewType) {
            holder = onCreatePlaceViewHolder(parent);
        } else if (VIEW_TYPE_SALE_TYPE == viewType) {
            holder = onCreateSaleTypeViewHolder(parent);
        } else if (VIEW_TYPE_SALE_VALUE == viewType) {
            holder = onCreateSaleValueViewHolder(parent);
        } else {
            holder = super.onCreateHeaderItemViewHolder(parent, viewType);
        }

        return holder;
    }

    public void notifyPlaceChanged() {
        if (isPlaceSelectionEnabled()) {
            notifyItemChanged(ArrayUtils.indexOf(getHeaderViewTypes(), VIEW_TYPE_PLACE));
        }
    }

    @NonNull
    protected int[] getHeaderViewTypes() {
        return isPlaceSelectionEnabled()
               ? FULL_HEADER_ITEMS_VIEW_TYPES
               : SHORT_HEADER_ITEMS_VIEW_TYPES;
    }

    @CallSuper
    protected void onBindPlaceViewHolder(@NonNull final HeaderViewHolder holder) {
        Contracts.requireNonNull(holder, "holder == null");

        holder.valueView.setText(getPlace());
    }

    @CallSuper
    protected void onBindSaleTypeViewHolder(@NonNull final HeaderViewHolder holder) {
        Contracts.requireNonNull(holder, "holder == null");

        holder.valueView.setText(getSaleTypeNameId(getSaleType()));
    }

    @CallSuper
    protected void onBindSaleValueViewHolder(@NonNull final HeaderViewHolder holder) {
        Contracts.requireNonNull(holder, "holder == null");

        final val context = holder.getContext();

        holder.valueContainerView.setHint(context.getString(getSaleTypeLabelId(getSaleType())));

        holder.valueView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(
                final TextView v, final int actionId, final KeyEvent event) {
                final boolean consumed;

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    v.clearFocus();
                    ImeUtils.hideIme(v);

                    consumed = true;
                } else {
                    consumed = false;
                }

                return consumed;
            }
        });

        holder.valueView.removeTextChangedListener(_saleValueListener);

        final val saleValue = getSaleValue();
        holder.valueView.setText(saleValue == null ? null : String.valueOf((int) saleValue));

        holder.valueView.addTextChangedListener(_saleValueListener);
    }

    @NonNull
    protected HeaderViewHolder onCreatePlaceViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.activity_sale_editor_header, parent);

        final val holder = new HeaderViewHolder(view);

        final val context = holder.getContext();

        holder.valueContainerView.setHint(context.getString(R.string.sale_editor_place_label));
        holder.valueView.setOnClickListener(_pickPlaceOnClick);

        return holder;
    }

    @NonNull
    protected HeaderViewHolder onCreateSaleTypeViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.activity_sale_editor_header, parent);

        final val holder = new HeaderViewHolder(view);

        final val context = holder.getContext();

        holder.valueContainerView.setHint(context.getString(R.string.sale_editor_sale_type_label));
        holder.valueView.setOnClickListener(_pickSaleTypeOnClick);

        DividerItemDecoration.setDecorationMode(holder.itemView,
                                                DividerItemDecoration.DecorationMode.NONE);

        return holder;
    }

    @NonNull
    protected HeaderViewHolder onCreateSaleValueViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.activity_sale_editor_sale_value_header, parent);

        return new HeaderViewHolder(view);
    }

    @NonNull
    private final ManagedNoticeEvent _pickPlaceEvent = Events.createNoticeEvent();

    @NonNull
    private final View.OnClickListener _pickPlaceOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            _pickPlaceEvent.rise();
        }
    };

    @Getter
    @Nullable
    private CharSequence _place;

    @Getter
    private boolean _placeSelectionEnabled = true;

    @Nullable
    private SaleType _saleType;

    @NonNull
    private final View.OnClickListener _pickSaleTypeOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final val context = v.getContext();

            new AlertDialog.Builder(context)
                .setSingleChoiceItems(ContextUtils.getStrings(context, SALE_TYPES_NAMES_IDS),
                                      getSaleTypePosition(getSaleType()),
                                      new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(
                                              final DialogInterface dialog, final int which) {
                                              setSaleType(getSaleTypeByPosition(which));

                                              dialog.dismiss();
                                          }
                                      })
                .show();
        }
    };

    @Getter
    @Nullable
    private Integer _saleValue;

    @NonNull
    private final TextWatcher _saleValueListener = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(
            final CharSequence s, final int start, final int before, final int count) {
            try {
                _saleValue = Integer.parseInt(s.toString());
            } catch (@NonNull final NumberFormatException ignored) {
                setSaleValue(null);
            }
        }
    };

    @NonNull
    private SaleType getSaleTypeByNameId(@StringRes final int saleTypeNameId) {
        final SaleType saleType;

        switch (saleTypeNameId) {
            case R.string.sale_editor_sale_type_absolute: {
                saleType = SaleType.ABSOLUTE;
                break;
            }
            case R.string.sale_editor_sale_type_relative: {
                saleType = SaleType.RELATIVE;
                break;
            }
            case R.string.sale_editor_sale_type_new_price: {
                saleType = SaleType.NEW_PRICE;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown sale type name id: " + saleTypeNameId);
            }
        }

        return saleType;
    }

    @NonNull
    private SaleType getSaleTypeByPosition(final int position) {
        return getSaleTypeByNameId(SALE_TYPES_NAMES_IDS[position]);
    }

    @StringRes
    private int getSaleTypeLabelId(@NonNull final SaleType saleType) {
        Contracts.requireNonNull(saleType, "saleType == null");

        final int saleTypeNameId;

        switch (saleType) {
            case ABSOLUTE: {
                saleTypeNameId = R.string.sale_editor_sale_type_label_absolute;
                break;
            }
            case RELATIVE: {
                saleTypeNameId = R.string.sale_editor_sale_type_label_relative;
                break;
            }
            case NEW_PRICE: {
                saleTypeNameId = R.string.sale_editor_sale_type_label_new_price;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown sale type: " + saleType);
            }
        }

        return saleTypeNameId;
    }

    @StringRes
    private int getSaleTypeNameId(@NonNull final SaleType saleType) {
        Contracts.requireNonNull(saleType, "saleType == null");

        final int saleTypeNameId;

        switch (saleType) {
            case ABSOLUTE: {
                saleTypeNameId = R.string.sale_editor_sale_type_absolute;
                break;
            }
            case RELATIVE: {
                saleTypeNameId = R.string.sale_editor_sale_type_relative;
                break;
            }
            case NEW_PRICE: {
                saleTypeNameId = R.string.sale_editor_sale_type_new_price;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown sale type: " + saleType);
            }
        }

        return saleTypeNameId;
    }

    private int getSaleTypePosition(@NonNull final SaleType saleType) {
        return ArrayUtils.indexOf(SALE_TYPES_NAMES_IDS, getSaleTypeNameId(saleType));
    }
}
