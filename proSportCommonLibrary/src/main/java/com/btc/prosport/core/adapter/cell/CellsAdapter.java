package com.btc.prosport.core.adapter.cell;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.ViewHolderBinder;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.common.R;
import com.btc.prosport.core.adapter.timeTable.TimeTableAdapter;
import com.btc.prosport.core.adapter.timeTable.viewHolder.CellViewHolder;

import java.util.List;

@Accessors(prefix = "_")
public abstract class CellsAdapter<TCell> extends TimeTableAdapter {
    private static final String _LOG_TAG = ConstantBuilder.logTag(CellsAdapter.class);

    @Nullable
    public final TCell getCellItemByAdapterPosition(final int adapterPosition) {
        return getCellItemByRelativePosition(getCellVerticalRelativePosition(adapterPosition));
    }

    @Nullable
    public final TCell getCellItemByRelativePosition(final int relativePosition) {
        final TCell cell;

        final val cells = getCells();
        if (cells != null && !cells.isEmpty() && 0 <= relativePosition &&
            relativePosition < cells.size()) {
            cell = cells.get(relativePosition);
        } else {
            cell = null;
        }

        return cell;
    }

    @NonNull
    public final Drawable getIntervalDrawable(
        @NonNull final Context context,
        @ColorInt final int foregroundColor,
        final boolean isFirstInterval,
        final boolean isLastInterval) {
        return getIntervalDrawable(
            Contracts.requireNonNull(context, "context == null"),
            foregroundColor,
            Color.TRANSPARENT,
            isFirstInterval,
            isLastInterval);
    }

    @NonNull
    public final Drawable getIntervalDrawable(
        @NonNull final Context context,
        @ColorInt final int foregroundColor,
        @ColorInt final int backgroundColor,
        final boolean isFirstInterval,
        final boolean isLastInterval) {
        Contracts.requireNonNull(context, "context == null");

        final Drawable background;

        if (isFirstInterval && isLastInterval) {
            background = ContextCompat.getDrawable(context, R.drawable.bg_order_interval_single);
        } else if (isFirstInterval) {
            background = ContextCompat.getDrawable(context, R.drawable.bg_order_interval_first);
        } else if (isLastInterval) {
            background = ContextCompat.getDrawable(context, R.drawable.bg_order_interval_last);
        } else {
            background = ContextCompat.getDrawable(context, R.drawable.bg_order_interval);
        }

        setIntervalDrawableForeground(background, foregroundColor);
        setIntervalDrawableBackground(background, backgroundColor);

        return background;
    }

    public final void setCellViewHolderBinder(
        @Nullable final ViewHolderBinder<CellViewHolder> cellViewHolderBinder) {
        _cellViewHolderBinder = cellViewHolderBinder;

        _logMessageShown = false;
    }

    public final void setIntervalDrawableBackground(
        @NonNull final Drawable background, @ColorInt final int color) {
        Contracts.requireNonNull(background, "background == null");

        if (background instanceof LayerDrawable) {
            final val layerBackground = (LayerDrawable) background;
            final val backgroundLayer = layerBackground.findDrawableByLayerId(R.id.background);

            if (backgroundLayer != null) {
                DrawableCompat.setTint(backgroundLayer, color);
            }
        }
    }

    public final void setIntervalDrawableForeground(
        @NonNull final Drawable background, @ColorInt final int color) {
        Contracts.requireNonNull(background, "background == null");

        if (background instanceof LayerDrawable) {
            final val layerBackground = (LayerDrawable) background;
            final val foregroundLayer = layerBackground.findDrawableByLayerId(R.id.foreground);

            if (foregroundLayer != null) {
                DrawableCompat.setTint(foregroundLayer, color);
            }
        }
    }

    @CallSuper
    @Override
    protected void onBindCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {
        final val intervalHolder = (CellViewHolder) holder;

        final val cellViewBinder = getCellViewHolderBinder();
        if (cellViewBinder != null) {
            cellViewBinder.bindViewHolder(intervalHolder, position);
        } else {
            if (!_logMessageShown) {
                Log.i(
                    _LOG_TAG,
                    "Cell view binder is not set. Skipped default behavior of bind cell view " +
                    "holder");
                _logMessageShown = true;
            }
        }
    }

    @Getter
    @Nullable
    private ViewHolderBinder<CellViewHolder> _cellViewHolderBinder;

    @Getter
    @Setter
    @Nullable
    private List<TCell> _cells;

    private boolean _logMessageShown = false;
}
