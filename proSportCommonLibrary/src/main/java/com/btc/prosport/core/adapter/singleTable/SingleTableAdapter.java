package com.btc.prosport.core.adapter.singleTable;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.adapter.WrappedRecyclerViewAdapter;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.common.R;

@Accessors(prefix = "_")
public abstract class SingleTableAdapter
    extends WrappedRecyclerViewAdapter<ExtendedRecyclerViewHolder> {
    public static final int VIEW_TYPE_CORNER_CELL = newViewType();

    public static final int VIEW_TYPE_CELL = newViewType();

    public static final int VIEW_TYPE_COLUMN_HEADER_CELL = newViewType();

    public static final int VIEW_TYPE_ROW_HEADER_CELL = newViewType();

    public int getCellAdapterPositionByVerticalRelativePosition(final int relativePosition) {
        final int column = relativePosition / getTableRowCount();
        final int row = relativePosition % getTableRowCount();

        return getHeaderItemCount() + /*column header row*/ getRowLength() + column +
               (getTableColumnCount() + 1) * row + 1;
    }

    public int getCellHorizontalRelativePosition(final int adapterPosition) {
        final int rawPosition =
            adapterPosition - getHeaderItemCount() - /*corner cell*/ 1 - getTableColumnCount();

        return rawPosition - rawPosition / getRowLength() - 1;
    }

    public int getCellVerticalRelativePosition(final int position) {
        final int horizontalRelativePosition = getCellHorizontalRelativePosition(position);
        final int row = getRowByHorizontalRelativePosition(horizontalRelativePosition);
        final int column = getColumnByHorizontalRelativePosition(horizontalRelativePosition);

        return row + column * getTableRowCount();
    }

    public int getColumnByAdapterPosition(final int position) {
        return getColumnByHorizontalRelativePosition(getCellHorizontalRelativePosition(position));
    }

    public int getColumnByHorizontalRelativePosition(final int relativePosition) {
        return relativePosition % getTableColumnCount();
    }

    public int getColumnByVerticalRelativePosition(final int relativePosition) {
        return relativePosition / getTableRowCount();
    }

    public int getColumnHeaderCellAdapterPosition(final int column) {
        return getHeaderItemCount() + 1 + column;
    }

    // column of row headers' -1
    public int getColumnHeaderCellRelativePosition(final int position) {
        return (position - getHeaderItemCount()) % getRowLength() - /*row headers column*/ 1;
    }

    public int getColumnLength() {
        return getTableRowCount() + /*column header row*/ 1;
    }

    public int getCornerCellRelativePosition(final int position) {
        return position - getHeaderItemCount();
    }

    @Override
    public int getInnerItemCount() {
        return /*corner cell*/ 1 + getTableColumnCount() + getTableRowCount() + getTableCellCount();
    }

    @Override
    public int getInnerItemViewType(final int position) {
        final int viewType;

        final int innerItemPosition = getInnerItemRelativePosition(position);

        final int columnHeaderCellCount = getTableColumnCount();

        if (innerItemPosition == 0) {
            viewType = VIEW_TYPE_CORNER_CELL;
        } else if (1 <= innerItemPosition &&
                   innerItemPosition < columnHeaderCellCount + /*corner cell*/ 1) {
            viewType = VIEW_TYPE_COLUMN_HEADER_CELL;
        } else if (columnHeaderCellCount <= innerItemPosition) {
            final int tableRowLength = getRowLength();
            if (tableRowLength != 0 && innerItemPosition % tableRowLength == 0) {
                viewType = VIEW_TYPE_ROW_HEADER_CELL;
            } else {
                viewType = VIEW_TYPE_CELL;
            }
        } else {
            throw new IllegalArgumentException("Illegal position: " + position);
        }

        return viewType;
    }

    @Override
    protected boolean isInnerItemViewType(final int viewType) {
        return super.isInnerItemViewType(viewType) || viewType == VIEW_TYPE_CORNER_CELL ||
               viewType == VIEW_TYPE_CELL || viewType == VIEW_TYPE_COLUMN_HEADER_CELL ||
               viewType == VIEW_TYPE_ROW_HEADER_CELL;
    }

    @CallSuper
    @Override
    public void onBindInnerItemViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {
        Contracts.requireNonNull(holder, "holder == null");

        final int viewType = getInnerItemViewType(position);

        if (VIEW_TYPE_CORNER_CELL == viewType) {
            onBindCornerCellViewHolder(holder, position);
        } else if (VIEW_TYPE_CELL == viewType) {
            holder.itemView.setTag(R.id.tag_view_position, position);

            onBindCellViewHolder(holder, position);
        } else if (VIEW_TYPE_COLUMN_HEADER_CELL == viewType) {
            holder.itemView.setTag(R.id.tag_view_position, position);

            onBindColumnHeaderCellViewHolder(holder, position);
        } else if (VIEW_TYPE_ROW_HEADER_CELL == viewType) {
            holder.itemView.setTag(R.id.tag_view_position, position);

            onBindRowHeaderCellViewHolder(holder, position);
        } else {
            throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
    }

    @NonNull
    @CallSuper
    @Override
    public ExtendedRecyclerViewHolder onCreateInnerItemViewHolder(
        final ViewGroup parent, final int viewType) {
        final ExtendedRecyclerViewHolder holder;

        if (VIEW_TYPE_CORNER_CELL == viewType) {
            holder = onCreateCornerCellViewHolder(parent);

            holder.itemView.setOnClickListener(getCornerCellClickListener());
        } else if (VIEW_TYPE_CELL == viewType) {
            holder = onCreateCellViewHolder(parent);

            holder.itemView.setOnClickListener(getCellClickListener());
        } else if (VIEW_TYPE_COLUMN_HEADER_CELL == viewType) {
            holder = onCreateColumnHeaderCellViewHolder(parent);

            holder.itemView.setOnClickListener(getColumnHeaderCellClickListener());
        } else if (VIEW_TYPE_ROW_HEADER_CELL == viewType) {
            holder = onCreateRowHeaderCellViewHolder(parent);

            holder.itemView.setOnClickListener(getRowHeaderCellClickListener());
        } else {
            holder = super.onCreateInnerItemViewHolder(parent, viewType);
        }

        return holder;
    }

    public int getRowByAdapterPosition(final int position) {
        return getRowByHorizontalRelativePosition(getCellHorizontalRelativePosition(position));
    }

    public int getRowByHorizontalRelativePosition(final int cellRelativePosition) {
        return cellRelativePosition / getTableColumnCount();
    }

    public int getRowHeaderCellAdapterPosition(final int row) {
        return getHeaderItemCount() + 1 + getTableColumnCount() + (row * getRowLength());
    }

    public int getRowHeaderCellRelativePosition(final int position) {
        return (position - getHeaderItemCount() - /*corner cell*/ 1 - getTableColumnCount()) /
               getRowLength();
    }

    public int getRowLength() {
        return getTableColumnCount() + /*row header column*/ 1;
    }

    public int getTableCellCount() {
        return getTableColumnCount() * getTableRowCount();
    }

    public void notifyCellChanged(final int relativePosition) {
        notifyItemChanged(getCellAdapterPositionByVerticalRelativePosition(relativePosition));
    }

    public void notifyCellRangeChanged(
        final int relativePositionStart, final int relativePositionEnd) {
        for (int i = relativePositionStart; i <= relativePositionEnd; i++) {
            notifyCellChanged(i);
        }
    }

    public void notifyColumnChanged(final int column) {
        final val position = getColumnHeaderCellAdapterPosition(column);
        final val rowCount = getTableRowCount();
        final val rowLength = getRowLength();

        for (int i = 0; i <= rowCount; i++) {
            notifyItemChanged(position + i * rowLength);
        }
    }

    public void notifyRowChanged(final int row) {
        notifyItemRangeChanged(getRowHeaderCellAdapterPosition(row), getRowLength());
    }

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(createLayoutManager(recyclerView.getContext()));
        recyclerView.setClipChildren(false);
    }

    @CallSuper
    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        recyclerView.setLayoutManager(null);
    }

    @NonNull
    protected GridLayoutManager createLayoutManager(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        return new GridLayoutManager(context, getRowLength());
    }

    @CallSuper
    protected void onCellClick(final int position) {
    }

    @CallSuper
    protected void onColumnHeaderCellClick(final int position) {
    }

    @CallSuper
    protected void onCornerCellClick() {
    }

    @CallSuper
    protected void onRowHeaderCellClick(final int position) {
    }

    protected abstract void onBindCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position);

    protected abstract void onBindColumnHeaderCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position);

    protected abstract void onBindCornerCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position);

    protected abstract void onBindRowHeaderCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position);

    @NonNull
    protected abstract ExtendedRecyclerViewHolder onCreateCellViewHolder(final ViewGroup parent);

    @NonNull
    protected abstract ExtendedRecyclerViewHolder onCreateColumnHeaderCellViewHolder(
        final ViewGroup parent);

    @NonNull
    protected abstract ExtendedRecyclerViewHolder onCreateCornerCellViewHolder(
        final ViewGroup parent);

    @NonNull
    protected abstract ExtendedRecyclerViewHolder onCreateRowHeaderCellViewHolder(
        final ViewGroup parent);

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final View.OnClickListener _cellClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            onCellClick((int) v.getTag(R.id.tag_view_position));
        }
    };

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final View.OnClickListener _columnHeaderCellClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            onColumnHeaderCellClick((int) v.getTag(R.id.tag_view_position));
        }
    };

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final View.OnClickListener _cornerCellClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            onCornerCellClick();
        }
    };

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final View.OnClickListener _rowHeaderCellClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            onRowHeaderCellClick((int) v.getTag(R.id.tag_view_position));
        }
    };

    @Getter
    @Setter
    private int _tableColumnCount;

    @Getter
    @Setter
    private int _tableRowCount;
}
