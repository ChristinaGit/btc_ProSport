package com.btc.prosport.core.adapter.cell;

import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.var;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.common.utility.ThemeUtils;
import com.btc.prosport.common.R;

import org.apache.commons.lang3.Range;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Accessors(prefix = "_")
public abstract class SelectableCellsAdapter<TCell> extends CellsAdapter<TCell> {
    public final void clearSelection() {
        clearRangeSelection();

        clearRowsSelection();

        clearColumnsSelection();
    }

    @NonNull
    public final NoticeEvent getCellsSelectionChangedEvent() {
        return _cellsSelectionChangedEvent;
    }

    @NonNull
    public final NoticeEvent getCellsSelectionStateChangedEvent() {
        return _cellsSelectionStateChangedEvent;
    }

    public final boolean isColumnSelected(final int position) {
        return getSelectedColumns().contains(getColumnHeaderCellRelativePosition(position));
    }

    public final boolean isRowSelected(final int position) {
        return getSelectedRows().contains(getRowHeaderCellRelativePosition(position));
    }

    public final void notifySelectedCellRangeChanged() {
        final val selectedCellRange = getSelectedCellRange();
        if (selectedCellRange != null) {
            notifyCellRangeChanged(selectedCellRange.getMinimum(), selectedCellRange.getMaximum());
        }
    }

    @Nullable
    public TCell getFirstSelectedCell() {
        TCell cell = null;

        final val selectedCellRange = getSelectedCellRange();
        if (selectedCellRange != null) {
            final int position = selectedCellRange.getMinimum();

            cell = getCellItemByRelativePosition(position);
        }

        return cell;
    }

    @Nullable
    public TCell getLastSelectedCell() {
        TCell cell = null;

        final val selectedCellRange = getSelectedCellRange();
        if (selectedCellRange != null) {
            final int position = selectedCellRange.getMaximum();

            cell = getCellItemByRelativePosition(position);
        }

        return cell;
    }

    @Nullable
    public Long getSelectionEndDay() {
        Long reservationDayTime = null;

        final val selectedCellRange = getSelectedCellRange();
        if (selectedCellRange != null) {
            reservationDayTime = getDayByRelativePosition(selectedCellRange.getMaximum());
        }

        return reservationDayTime;
    }

    @Nullable
    public Long getSelectionStartDay() {
        Long reservationDayTime = null;

        final val selectedCellRange = getSelectedCellRange();
        if (selectedCellRange != null) {
            reservationDayTime = getDayByRelativePosition(selectedCellRange.getMinimum());
        }

        return reservationDayTime;
    }

    public boolean hasColumnsSelection() {
        return !getSelectedColumns().isEmpty();
    }

    public boolean hasRangeSelection() {
        return getSelectedCellRange() != null;
    }

    public boolean hasRowsSelection() {
        return !getSelectedRows().isEmpty();
    }

    public boolean hasSelection() {
        return hasRangeSelection() || hasRowsSelection() || hasColumnsSelection();
    }

    public boolean isCellClickable(@Nullable final TCell cell) {
        return isCellSelectable(cell);
    }

    public boolean isCellSelected(final int position) {
        return isRangeCellSelected(position) || isRowSelected(position) ||
               isColumnSelected(position);
    }

    public boolean isColumnSelectable(final int position) {
        return false;
    }

    public boolean isRangeCellSelected(final int position) {
        return _selectedCellRange != null &&
               _selectedCellRange.contains(getCellVerticalRelativePosition(position));
    }

    public boolean isRowSelectable(final int position) {
        return false;
    }

    public void performSelectColumn(final int position) {
        final val hasSelection = hasSelection();

        boolean selectionChanged;

        final val column = getColumnHeaderCellRelativePosition(position);

        final val selectedColumns = getSelectedColumns();
        selectionChanged = selectedColumns.remove(column);
        if (!selectionChanged) {
            selectionChanged = selectedColumns.add(column);
        }

        notifyColumnChanged(column);

        clearRangeSelection();
        clearRowsSelection();

        if (selectionChanged) {
            riseCellsSelectionChanged();
        }

        if (hasSelection != hasSelection()) {
            riseCellsSelectionStateChanged();
        }
    }

    public void performSelectRow(final int position) {
        final val hasSelection = hasSelection();

        boolean selectionChanged;

        final val row = getRowHeaderCellRelativePosition(position);

        final val selectedRows = getSelectedRows();
        selectionChanged = selectedRows.remove(row);
        if (!selectionChanged) {
            selectionChanged = selectedRows.add(row);
        }

        notifyRowChanged(row);

        clearRangeSelection();
        clearColumnsSelection();

        if (selectionChanged) {
            riseCellsSelectionChanged();
        }

        if (hasSelection != hasSelection()) {
            riseCellsSelectionStateChanged();
        }
    }

    public abstract boolean isCellSelectable(@Nullable final TCell cell);

    protected final void riseCellsSelectionChanged() {
        _cellsSelectionChangedEvent.rise();
    }

    protected final void riseCellsSelectionStateChanged() {
        _cellsSelectionStateChangedEvent.rise();
    }

    protected void clearColumnsSelection() {
        final val selectedColumns = getSelectedColumns();
        if (!selectedColumns.isEmpty()) {
            final val iterator = selectedColumns.iterator();
            while (iterator.hasNext()) {
                final val column = iterator.next();
                iterator.remove();

                notifyColumnChanged(column);
            }
        }
    }

    protected void clearRangeSelection() {
        if (_selectedCellRange != null) {
            final val oldMinimum = _selectedCellRange.getMinimum();
            final val oldMaximum = _selectedCellRange.getMaximum();

            _selectedCellRange = null;

            notifyCellRangeChanged(oldMinimum, oldMaximum);
        }
    }

    protected void clearRowsSelection() {
        final val selectedRows = getSelectedRows();
        if (!selectedRows.isEmpty()) {
            final val iterator = selectedRows.iterator();
            while (iterator.hasNext()) {
                final val row = iterator.next();
                iterator.remove();

                notifyRowChanged(row);
            }
        }
    }

    protected boolean isIntervalsRangeSelectable(
        final int startRelativePosition, final int endRelativePosition) {
        var isIntervalsRangeSelectable = true;

        for (var i = startRelativePosition; i <= endRelativePosition; i++) {
            final val cell = getCellItemByRelativePosition(i);

            if (!isCellSelectable(cell)) {
                isIntervalsRangeSelectable = false;
                break;
            }
        }

        return isIntervalsRangeSelectable;
    }

    @Override
    protected void onBindCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {
        super.onBindCellViewHolder(Contracts.requireNonNull(holder, "holder == null"), position);

        final val context = holder.getContext();

        final int relativePosition = getCellVerticalRelativePosition(position);
        final val interval = getCellItemByRelativePosition(relativePosition);

        holder.itemView.setEnabled(isCellClickable(interval));

        if (isRangeCellSelected(position)) {
            final val prevPosition =
                getCellAdapterPositionByVerticalRelativePosition(relativePosition - 1);
            final val nextPosition =
                getCellAdapterPositionByVerticalRelativePosition(relativePosition + 1);

            final val isFirstInterval = !isCellSelected(prevPosition);
            final val isLastInterval = !isCellSelected(nextPosition);

            final val itemColor = ThemeUtils.resolveColor(context, R.attr.colorAccent);
            final val background =
                getIntervalDrawable(context, itemColor, isFirstInterval, isLastInterval);

            holder.itemView.setBackground(background);
        } else if (isCellSelected(position)) {
            final val itemColor = ThemeUtils.resolveColor(context, R.attr.colorAccent);
            holder.itemView.setBackgroundColor(itemColor);
        } else {
            // From super
        }
    }

    @Override
    protected void onBindColumnHeaderCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder genericHolder, final int position) {
        super.onBindColumnHeaderCellViewHolder(genericHolder, position);

        final val context = genericHolder.getContext();

        final int backgroundColor;
        final boolean enabled = isRowSelectable(position);

        if (isColumnSelected(position)) {
            backgroundColor = ThemeUtils.resolveColor(context, R.attr.colorAccent);
        } else {
            backgroundColor = Color.TRANSPARENT;
        }

        genericHolder.itemView.setBackgroundColor(backgroundColor);
        genericHolder.itemView.setEnabled(enabled);
    }

    @Override
    protected void onBindRowHeaderCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder genericHolder, final int position) {
        super.onBindRowHeaderCellViewHolder(genericHolder, position);

        final val context = genericHolder.getContext();

        final int backgroundColor;
        final boolean enabled = isRowSelectable(position);

        if (isRowSelected(position)) {
            backgroundColor = ThemeUtils.resolveColor(context, R.attr.colorAccent);
        } else {
            backgroundColor = Color.TRANSPARENT;
        }

        genericHolder.itemView.setBackgroundColor(backgroundColor);
        genericHolder.itemView.setEnabled(enabled);
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

        final val context = itemView.getContext();
        final val columnAdapterPosition = getColumnHeaderCellAdapterPosition(column);
        final val columnSelected = isColumnSelected(columnAdapterPosition);

        final int backgroundColor;
        if (columnSelected) {
            backgroundColor = ThemeUtils.resolveColor(context, R.attr.colorAccent);
        } else {
            backgroundColor = Color.TRANSPARENT;
        }

        itemView.setTag(R.id.tag_view_position, columnAdapterPosition);
        itemView.setOnClickListener(_riseColumnHeaderClickOnClick);

        itemView.setEnabled(isColumnSelectable(columnAdapterPosition));
        itemView.setBackgroundColor(backgroundColor);
    }

    @CallSuper
    @Override
    protected void onCellClick(final int position) {
        super.onCellClick(position);

        final val relativePosition = getCellVerticalRelativePosition(position);
        final val cell = getCellItemByRelativePosition(relativePosition);

        if (isCellSelectable(cell)) {
            performSelectCell(position);
        }
    }

    @CallSuper
    @Override
    protected void onColumnHeaderCellClick(final int position) {
        super.onColumnHeaderCellClick(position);

        if (isColumnSelectable(position)) {
            performSelectColumn(position);
        }
    }

    @CallSuper
    @Override
    protected void onRowHeaderCellClick(final int position) {
        super.onRowHeaderCellClick(position);

        if (isRowSelectable(position)) {
            performSelectRow(position);
        }
    }

    @NonNull
    private final ManagedNoticeEvent _cellsSelectionChangedEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _cellsSelectionStateChangedEvent = Events.createNoticeEvent();

    @Getter
    @NonNull
    private final Set<Integer> _selectedColumns = new HashSet<>();

    @Getter
    @NonNull
    private final Set<Integer> _selectedRows = new HashSet<>();

    // Relative vertical positions
    @Getter
    @Nullable
    private Range<Integer> _selectedCellRange;

    @NonNull
    private final View.OnClickListener _riseColumnHeaderClickOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final val columnAdapterPosition = (int) v.getTag(R.id.tag_view_position);

            onColumnHeaderCellClick(columnAdapterPosition);
        }
    };

    private void expandRangeSelectionBackward() {
        if (_selectedCellRange != null) {
            final val oldMinimum = _selectedCellRange.getMinimum();
            final val oldMaximum = _selectedCellRange.getMaximum();

            final val newMinimum = oldMinimum - 1;
            final val newMaximum = oldMaximum;

            _selectedCellRange = Range.between(newMinimum, newMaximum);

            notifySelectedCellRangeChanged();
        }
    }

    private void expandRangeSelectionForward() {
        if (_selectedCellRange != null) {
            final val oldMinimum = _selectedCellRange.getMinimum();
            final val oldMaximum = _selectedCellRange.getMaximum();

            final val newMinimum = oldMinimum;
            final val newMaximum = oldMaximum + 1;

            _selectedCellRange = Range.between(newMinimum, newMaximum);

            notifySelectedCellRangeChanged();
        }
    }

    @Nullable
    private Integer getSingleSelectedCell() {
        final Integer selectedCell;

        if (_selectedCellRange != null) {
            final val minimum = _selectedCellRange.getMinimum();
            final val maximum = _selectedCellRange.getMaximum();

            if (Objects.equals(minimum, maximum)) {
                selectedCell = minimum;
            } else {
                selectedCell = null;
            }
        } else {
            selectedCell = null;
        }

        return selectedCell;
    }

    private boolean isRangeSelectionNextCell(final int relativePosition) {
        return _selectedCellRange != null && _selectedCellRange.isEndedBy(relativePosition - 1);
    }

    private boolean isRangeSelectionPreviousCell(final int relativePosition) {
        return _selectedCellRange != null && _selectedCellRange.isStartedBy(relativePosition + 1);
    }

    private void performSelectCell(final int position) {
        boolean selectionChanged = false;
        final int relativePosition = getCellVerticalRelativePosition(position);
        final val hasSelection = hasSelection();

        if (!hasSelection) {
            setRangeSelection(relativePosition);

            selectionChanged = true;
        } else {
            if (isCellSelected(position)) {
                final val singleSelectedCell = getSingleSelectedCell();
                if (singleSelectedCell != null) {
                    clearSelection();

                    selectionChanged = true;
                } else {
                    if (_selectedCellRange != null) {
                        final val start = _selectedCellRange.getMinimum();

                        if (start == relativePosition) {
                            clearSelection();

                            selectionChanged = true;
                        } else {
                            setRangeSelection(start, relativePosition - 1);

                            selectionChanged = true;
                        }
                    }
                }
            } else {
                final val singleSelectedCell = getSingleSelectedCell();
                if (singleSelectedCell != null) {
                    final val start = Math.min(singleSelectedCell, relativePosition);
                    final val end = Math.max(singleSelectedCell, relativePosition);

                    if (isIntervalsRangeSelectable(start, end)) {
                        setRangeSelection(start, end);
                    } else {
                        setRangeSelection(relativePosition);
                    }
                } else {
                    if (isRangeSelectionNextCell(relativePosition)) {
                        expandRangeSelectionForward();
                    } else if (isRangeSelectionPreviousCell(relativePosition)) {
                        expandRangeSelectionBackward();
                    } else {
                        setRangeSelection(relativePosition);
                    }
                }

                selectionChanged = true;
            }
        }

        clearRowsSelection();
        clearColumnsSelection();

        if (selectionChanged) {
            riseCellsSelectionChanged();
        }

        if (hasSelection != hasSelection()) {
            riseCellsSelectionStateChanged();
        }
    }

    private void setRangeSelection(final int startRelativePosition, final int endRelativePosition) {
        final val newSelection = Range.between(startRelativePosition, endRelativePosition);

        Integer oldMinimum = null;
        Integer oldMaximum = null;

        boolean isOverlapped = false;
        if (_selectedCellRange != null) {
            oldMinimum = _selectedCellRange.getMinimum();
            oldMaximum = _selectedCellRange.getMaximum();

            isOverlapped = _selectedCellRange.isOverlappedBy(newSelection);
        }

        _selectedCellRange = newSelection;

        if (isOverlapped) {
            if (oldMinimum != null && oldMaximum != null) {
                notifyCellRangeChanged(
                    Math.min(oldMinimum, startRelativePosition),
                    Math.max(oldMaximum, endRelativePosition));
            } else {
                notifyCellRangeChanged(startRelativePosition, endRelativePosition);
            }
        } else {
            if (oldMinimum != null && oldMaximum != null) {
                notifyCellRangeChanged(oldMinimum, oldMaximum);
            }

            notifyCellRangeChanged(startRelativePosition, endRelativePosition);
        }
    }

    private void setRangeSelection(final int relativePosition) {
        setRangeSelection(relativePosition, relativePosition);
    }
}
