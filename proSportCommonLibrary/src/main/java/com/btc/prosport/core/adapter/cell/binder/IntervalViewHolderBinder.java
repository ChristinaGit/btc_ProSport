package com.btc.prosport.core.adapter.cell.binder;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.view.View;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.ViewHolderBinder;
import com.btc.common.utility.TextViewUtils;
import com.btc.common.utility.ThemeUtils;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.utility.IntervalState;
import com.btc.prosport.common.R;
import com.btc.prosport.core.adapter.cell.CellsAdapter;
import com.btc.prosport.core.adapter.intervalBooking.IntervalViewHolder;
import com.btc.prosport.core.adapter.timeTable.viewHolder.CellViewHolder;

import java.util.Objects;

/**
 * @deprecated Bad idea. Try do not use it.
 */
@Deprecated
@Accessors(prefix = "_")
public class IntervalViewHolderBinder<TAdapter extends CellsAdapter<Interval>>
    implements ViewHolderBinder<CellViewHolder> {
    public IntervalViewHolderBinder(@NonNull final TAdapter adapter) {
        Contracts.requireNonNull(adapter, "adapter == null");

        _adapter = adapter;
    }

    @CallSuper
    @Override
    public void bindViewHolder(
        @NonNull final CellViewHolder holder, final int position) {
        Contracts.requireNonNull(holder, "holder == null");

        final val adapter = getAdapter();

        final int relativePosition = adapter.getCellVerticalRelativePosition(position);
        final val interval = adapter.getCellItemByRelativePosition(relativePosition);

        onBindViewHolder(holder, position, relativePosition, interval);
    }

    @CallSuper
    protected void onBindViewHolder(
        @NonNull final CellViewHolder holder,
        final int position,
        final int relativePosition,
        @Nullable final Interval interval) {
        Contracts.requireNonNull(holder, "holder == null");

        final val adapter = getAdapter();

        final val context = holder.getContext();

        final Drawable itemBackground;
        final String itemText;
        final boolean strikeThrough;
        final boolean saleMarkerVisible;

        if (interval != null) {
            final val stateCode = interval.getStateCode();

            final val intervalState = stateCode == null ? null : IntervalState.byCode(stateCode);

            if (intervalState == IntervalState.FREE) {
                strikeThrough = false;

                final val price = interval.getPrice();
                if (price != null) {
                    itemText = context.getString(R.string.intervals_price_format, price);
                } else {
                    itemText = context.getString(R.string.intervals_price_no_data);
                }

                itemBackground = null;

                saleMarkerVisible = interval.isSale();
            } else if (intervalState == IntervalState.BUSY) {
                final int primaryColor = ThemeUtils.resolveColor(context, R.attr.colorPrimary);

                final val prevInterval =
                    adapter.getCellItemByRelativePosition(relativePosition - 1);
                final val nextInterval =
                    adapter.getCellItemByRelativePosition(relativePosition + 1);

                final val orderId = interval.getOrderId();
                final val prevOrderId = prevInterval == null ? null : prevInterval.getOrderId();
                final val nextOrderId = nextInterval == null ? null : nextInterval.getOrderId();

                final val isFirstInterval = !Objects.equals(prevOrderId, orderId);
                final val isLastInterval = !Objects.equals(nextOrderId, orderId);

                final val lightPrimaryColor = ColorUtils.setAlphaComponent(primaryColor, 45);

                itemBackground = adapter.getIntervalDrawable(context,
                                                             lightPrimaryColor,
                                                             isFirstInterval,
                                                             isLastInterval);

                final val price = interval.getPrice();
                if (price != null) {
                    strikeThrough = true;

                    itemText = context.getString(R.string.intervals_price_format, price);
                } else {
                    strikeThrough = false;

                    itemText = context.getString(R.string.intervals_price_no_data);
                }

                saleMarkerVisible = interval.isSale();
            } else {
                strikeThrough = false;

                itemText = context.getString(R.string.intervals_price_no_data);
                final val color = ContextCompat.getColor(context, R.color.disabledIntervalColor);
                itemBackground = new ColorDrawable(color);

                saleMarkerVisible = false;
            }
        } else {
            strikeThrough = false;

            itemText = null;
            final val color = ContextCompat.getColor(context, R.color.disabledIntervalColor);
            itemBackground = new ColorDrawable(color);

            saleMarkerVisible = false;
        }

        // Sorry. I failed. Again.
        if (holder instanceof IntervalViewHolder) {
            ((IntervalViewHolder) holder).priceMarkerView.setVisibility(saleMarkerVisible
                                                                        ? View.VISIBLE
                                                                        : View.GONE);
        }
        holder.itemView.setBackground(itemBackground);
        holder.valueView.setText(itemText);
        TextViewUtils.setStrikeThroughEnabled(holder.valueView, strikeThrough);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final TAdapter _adapter;
}
