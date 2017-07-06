package com.btc.prosport.manager.core.adapter.prices;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.TextView;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.common.utility.ThemeUtils;
import com.btc.prosport.api.ProSportDataContract;
import com.btc.prosport.api.model.Price;
import com.btc.prosport.core.adapter.cell.SelectableCellsAdapter;
import com.btc.prosport.core.adapter.timeTable.viewHolder.CellViewHolder;
import com.btc.prosport.core.adapter.timeTable.viewHolder.DayViewHolder;
import com.btc.prosport.manager.R;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormatSymbols;

@Accessors(prefix = "_")
public class PricesAdapter extends SelectableCellsAdapter<Price> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(PricesAdapter.class);

    public static final int PRICE_NOT_CHANGED = -1;

    public static final int PRICE_NULL = -2;

    public final int getNewPrice(final int relativePosition) {
        return _newPrices.get(relativePosition, PRICE_NOT_CHANGED);
    }

    public final boolean isAbstractTable() {
        return getStartTime() == null;
    }

    public final boolean isPriceChanged(final int relativePosition) {
        return getNewPrice(relativePosition) != PRICE_NOT_CHANGED;
    }

    public final boolean isPriceNull(final int relativePosition) {
        return getNewPrice(relativePosition) == PRICE_NULL;
    }

    public final void removeSelectionPrices() {
        final val selection = getSelectedCellRange();
        if (selection != null) {
            removeRangePrices(selection);
        }

        final val selectedRows = getSelectedRows();
        for (final val row : selectedRows) {
            removeRowPrices(row);
        }

        final val selectedColumns = getSelectedColumns();
        for (final val row : selectedColumns) {
            removeColumnPrices(row);
        }
    }

    public final void setSelectionNewPrices(final int price) {
        final val selection = getSelectedCellRange();
        if (selection != null) {
            setRangeNewPrices(selection, price);
        }

        final val selectedRows = getSelectedRows();
        for (final val row : selectedRows) {
            setRowNewPrices(row, price);
        }

        final val selectedColumns = getSelectedColumns();
        for (final val row : selectedColumns) {
            setColumnNewPrices(row, price);
        }
    }

    @Nullable
    public Integer getActualPrice(final int relativePosition) {
        final Integer actualPrice;

        final val newPrice = getNewPrice(relativePosition);
        if (newPrice == PRICE_NOT_CHANGED) {
            final val price = getCellItemByRelativePosition(relativePosition);
            actualPrice = price == null ? null : price.getValue();
        } else if (newPrice == PRICE_NULL) {
            actualPrice = null;
        } else {
            actualPrice = newPrice;
        }

        return actualPrice;
    }

    @Override
    public boolean isColumnSelectable(final int position) {
        return true;
    }

    @Override
    public boolean isRowSelectable(final int position) {
        return true;
    }

    @Override
    public boolean isCellSelectable(@Nullable final Price price) {
        return true;
    }

    @Override
    protected void onBindCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder genericHolder, final int position) {
        super.onBindCellViewHolder(Contracts.requireNonNull(genericHolder, "genericHolder == null"),
                                   position);

        final val holder = (CellViewHolder) genericHolder;

        final val context = holder.getContext();

        final val relativePosition = getCellVerticalRelativePosition(position);
        final val price = getCellItemByRelativePosition(relativePosition);
        final val newPrice = getNewPrice(relativePosition);
        final val priceChanged = newPrice != PRICE_NOT_CHANGED;
        final val priceNull = newPrice == PRICE_NULL;

        final Integer itemColor;
        if (!isCellSelected(position)) {
            if (priceChanged) {
                final val primaryColor = ThemeUtils.resolveColor(context, R.attr.colorPrimary);
                itemColor = ColorUtils.setAlphaComponent(primaryColor, 75);
            } else {
                itemColor = Color.TRANSPARENT;
            }
        } else {
            // from super
            itemColor = null;
        }

        final String formattedPriceValue;
        if (priceChanged) {
            if (!priceNull) {
                formattedPriceValue = String.valueOf(newPrice);
            } else {
                formattedPriceValue = context.getString(R.string.price_editor_no_price);
            }
        } else if (price != null) {
            final val priceValue = price.getValue();
            final val priceValueString =
                priceValue == null ? null : String.valueOf((int) priceValue);
            formattedPriceValue = StringUtils.defaultString(priceValueString,
                                                            context.getString(R.string
                                                                                  .price_editor_no_price));
        } else {
            formattedPriceValue = context.getString(R.string.price_editor_no_price);
        }

        if (itemColor != null) {
            holder.itemView.setBackgroundColor(itemColor);
        }
        holder.valueView.setText(formattedPriceValue);
    }

    @Override
    protected void onBindColumnHeaderCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder genericHolder, final int position) {
        if (!isAbstractTable()) {
            super.onBindColumnHeaderCellViewHolder(genericHolder, position);
        } else {
            final val holder = (DayViewHolder) genericHolder;

            final val column = getColumnHeaderCellRelativePosition(position);

            final val weekdays = DateFormatSymbols.getInstance().getShortWeekdays();

            final val dayOfWeek = ProSportDataContract.PRICE_DAY_ORDER[column];

            final val formattedDayOfWeek = weekdays[dayOfWeek];

            final val context = genericHolder.getContext();

            final val columnSelected = isColumnSelected(position);

            final int backgroundColor;
            if (columnSelected) {
                backgroundColor = ThemeUtils.resolveColor(context, R.attr.colorAccent);
            } else {
                backgroundColor = Color.TRANSPARENT;
            }

            holder.itemView.setBackgroundColor(backgroundColor);
            holder.dayView.setText(null);
            holder.dayOfWeekView.setText(formattedDayOfWeek);
        }
    }

    @Override
    protected void onBindOverlayHeaderItem(
        final int column,
        @NonNull final View itemView,
        @NonNull final TextView intervalDayView,
        @NonNull final TextView intervalDayOfWeekView) {
        //@formatter:off
        super.onBindOverlayHeaderItem(column,
                                      Contracts.requireNonNull(itemView, "itemView == null"),
                                      Contracts.requireNonNull(intervalDayView, "intervalDayView == null"),
                                      Contracts.requireNonNull(intervalDayOfWeekView, "intervalDayOfWeekView == null"));
        //@formatter:on

        final val weekdays = DateFormatSymbols.getInstance().getShortWeekdays();
        final val dayOfWeek = ProSportDataContract.PRICE_DAY_ORDER[column];
        final val formattedDayOfWeek = weekdays[dayOfWeek];

        intervalDayView.setText(formattedDayOfWeek);
        intervalDayOfWeekView.setText(null);
    }

    public void removeColumnPrices(final int column) {
        setColumnNewPrices(column, PRICE_NULL);
    }

    public void removePrice(final int relativePosition) {
        setNewPrice(relativePosition, PRICE_NULL);
    }

    public void removePriceChanges() {
        _newPrices.clear();
    }

    public void removeRangePrices(@NonNull final Range<Integer> range) {
        Contracts.requireNonNull(range, "range == null");

        setRangeNewPrices(range, PRICE_NULL);
    }

    public void removeRowPrices(final int row) {
        setRowNewPrices(row, PRICE_NULL);
    }

    public void setColumnNewPrices(final int column, final int price) {
        final val columnAdapterPosition = getColumnHeaderCellAdapterPosition(column);
        final val rowLength = getRowLength();
        final val rowCount = getTableRowCount();

        for (int i = 1; i <= rowCount; i++) {
            final val relativePosition =
                getCellVerticalRelativePosition(columnAdapterPosition + i * rowLength);
            setNewPriceInternal(relativePosition, price);
        }

        notifyColumnChanged(column);
    }

    public void setNewPrice(final int relativePosition, final int price) {
        setNewPriceInternal(relativePosition, price);

        notifyCellChanged(relativePosition);
    }

    public void setRangeNewPrices(@NonNull final Range<Integer> range, final int price) {
        Contracts.requireNonNull(range, "range == null");

        final val start = (int) range.getMinimum();
        final val end = (int) range.getMaximum();

        for (int i = start; i <= end; i++) {
            setNewPriceInternal(i, price);
        }

        notifyCellRangeChanged(start, end);
    }

    public void setRowNewPrices(final int row, final int price) {
        final val rowAdapterPosition = getRowHeaderCellAdapterPosition(row);
        final val columnCount = getTableColumnCount();

        for (int i = 0; i < columnCount; i++) {
            setNewPriceInternal(getCellVerticalRelativePosition(rowAdapterPosition + 1 + i), price);
        }

        notifyRowChanged(row);
    }

    @Getter
    @NonNull
    private final SparseIntArray _newPrices = new SparseIntArray();

    private void setNewPriceInternal(final int relativePosition, final int newPrice) {
        final val price = getCellItemByRelativePosition(relativePosition);
        final val oldPrice = price == null ? null : price.getValue();

        final val oldPriceInternal = oldPrice == null ? PRICE_NULL : (int) oldPrice;

        if (newPrice != oldPriceInternal) {
            _newPrices.append(relativePosition, newPrice);
        } else {
            final val index = _newPrices.indexOfKey(relativePosition);
            if (index >= 0) {
                _newPrices.removeAt(index);
            }
        }
    }
}
