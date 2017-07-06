package com.btc.prosport.manager.screen.activity.workspace;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.utility.IntentUtils;
import com.btc.prosport.api.ProSportContract;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.OrderStatusFilter;

public final class WorkspaceIntent {
    public static final int TAB_ID_SCHEDULE = R.id.tab_schedule;

    public static final int TAB_ID_SALES = R.id.tab_sales;

    public static final int TAB_ID_ORDERS = R.id.tab_orders;

    public static final String EXTRA_ORDERS_FILTER = "orders_filter";

    public static final String EXTRA_TAB_ID = "page_id";

    @NonNull
    public static IntentBuilder builder(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        return new IntentBuilder(context);
    }

    @Nullable
    public static Integer getTabId(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        return IntentUtils.getNullableIntExtra(intent, EXTRA_TAB_ID);
    }

    @Nullable
    public static OrderStatusFilter getOrdersStatusFilter(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        return (OrderStatusFilter) intent.getSerializableExtra(EXTRA_ORDERS_FILTER);
    }

    private WorkspaceIntent() {
        Contracts.unreachable();
    }

    public static final class IntentBuilder {
        @NonNull
        public final Intent build() {
            final val intent = new Intent(_context, WorkspaceActivity.class);

            if (_playgroundId != null) {
                intent.setData(ProSportContract.getPlaygroundUri(_playgroundId));
            } else {
                intent.setData(null);
            }

            intent.putExtra(EXTRA_ORDERS_FILTER, _orderStatusFilter);
            IntentUtils.putNullableExtra(intent, EXTRA_TAB_ID, _tabId);

            return intent;
        }

        @NonNull
        public IntentBuilder setOrderStateFilter(
            @Nullable final OrderStatusFilter orderStatusFilter) {
            _orderStatusFilter = orderStatusFilter;

            return this;
        }

        @NonNull
        public IntentBuilder setPlaygroundId(@Nullable final Long playgroundId) {
            _playgroundId = playgroundId;

            return this;
        }

        @NonNull
        public IntentBuilder setTabId(@Nullable final Integer tabId) {
            _tabId = tabId;

            return this;
        }

        @NonNull
        private final Context _context;

        @Nullable
        private OrderStatusFilter _orderStatusFilter;

        @Nullable
        private Long _playgroundId;

        @Nullable
        private Integer _tabId;

        private IntentBuilder(@NonNull final Context context) {
            Contracts.requireNonNull(context, "context == null");

            _context = context;
        }
    }
}
