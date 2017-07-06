package com.btc.prosport.core.utility;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.utility.OrderState;
import com.btc.prosport.common.R;

public final class OrderUtils {
    public static boolean isWithSale(@NonNull final Order order) {
        Contracts.requireNonNull(order, "order == null");

        return order.getOriginalPrice() > order.getPrice();
    }

    @ColorRes
    public static int getOrderStateColor(@NonNull final OrderState orderState) {
        Contracts.requireNonNull(orderState, "orderState == null");

        switch (orderState) {
            case NOT_CONFIRMED:
                return R.color.colorOrderStateNotConfirmed;
            case CONFIRMED:
                return R.color.colorOrderStateConfirmed;
            case CANCELED:
                return R.color.colorOrderStateCanceled;
            default:
                throw new IllegalArgumentException("Unknown order state: " + orderState);
        }
    }

    @StringRes
    public static int getOrderStateName(@NonNull final OrderState orderState) {
        Contracts.requireNonNull(orderState, "orderState == null");

        switch (orderState) {
            case NOT_CONFIRMED:
                return R.string.caption_order_state_not_confirmed;
            case CONFIRMED:
                return R.string.caption_order_state_confirmed;
            case CANCELED:
                return R.string.caption_order_state_canceled;
            default:
                throw new IllegalArgumentException("Unknown order state: " + orderState);
        }
    }

    private OrderUtils() {
        Contracts.unreachable();
    }
}
