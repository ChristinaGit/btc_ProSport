package com.btc.prosport.manager.screen.activity.orderEditor;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.utility.IntentUtils;
import com.btc.prosport.api.ProSportContract;

public final class OrderEditorIntent {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrderEditorIntent.class);

    private OrderEditorIntent() {
        Contracts.unreachable();
    }

    public static final class Edit {
        @NonNull
        public static Intent build(@NonNull final Context context, final long orderId) {
            Contracts.requireNonNull(context, "context == null");

            final val intent = new Intent(context, OrderEditorActivity.class);
            intent.setAction(Intent.ACTION_EDIT);
            intent.setData(ProSportContract.getOrderUri(orderId));

            return intent;
        }

        @Nullable
        public static Long getOrderId(@NonNull final Intent intent) {
            Contracts.requireNonNull(intent, "intent == null");

            Long orderId = null;

            final val data = intent.getData();
            if (data != null) {
                final int code = ProSportContract.getCode(data);

                if (code == ProSportContract.CODE_ORDER) {
                    orderId = ContentUris.parseId(data);
                }
            }

            return orderId;
        }

        private Edit() {
            Contracts.unreachable();
        }
    }

    public static final class Insert {
        public static final String EXTRA_PLAYER_ID = "player_id";

        public static final String EXTRA_ORDER_DATE_START = "order_date_start";

        public static final String EXTRA_ORDER_DATE_END = "order_date_end";

        public static final String EXTRA_ORDER_TIME_START = "order_time_start";

        public static final String EXTRA_ORDER_TIME_END = "order_time_end";

        public static final String EXTRA_PLAYGROUND_ID = "playground_id";

        @NonNull
        public static IntentBuilder builder(@NonNull final Context context) {
            Contracts.requireNonNull(context, "context == null");

            return new IntentBuilder(context);
        }

        @Nullable
        public static Long getPlaygroundId(@NonNull final Intent intent) {
            Contracts.requireNonNull(intent, "intent == null");

            return IntentUtils.getNullableLongExtra(intent, EXTRA_PLAYGROUND_ID);
        }

        @Nullable
        public static Long getPlayerId(@NonNull final Intent intent) {
            Contracts.requireNonNull(intent, "intent == null");

            return IntentUtils.getNullableLongExtra(intent, EXTRA_PLAYER_ID);
        }

        @Nullable
        public static Long getOrderDateStart(@NonNull final Intent intent) {
            Contracts.requireNonNull(intent, "intent == null");

            return IntentUtils.getNullableLongExtra(intent, EXTRA_ORDER_DATE_START);
        }

        @Nullable
        public static Long getOrderDateEnd(@NonNull final Intent intent) {
            Contracts.requireNonNull(intent, "intent == null");

            return IntentUtils.getNullableLongExtra(intent, EXTRA_ORDER_DATE_END);
        }

        @Nullable
        public static Long getOrderTimeStart(@NonNull final Intent intent) {
            Contracts.requireNonNull(intent, "intent == null");

            return IntentUtils.getNullableLongExtra(intent, EXTRA_ORDER_TIME_START);
        }

        @Nullable
        public static Long getOrderTimeEnd(@NonNull final Intent intent) {
            Contracts.requireNonNull(intent, "intent == null");

            return IntentUtils.getNullableLongExtra(intent, EXTRA_ORDER_TIME_END);
        }

        private Insert() {
            Contracts.unreachable();
        }

        public static final class IntentBuilder {
            @NonNull
            public final Intent build(final long playgroundId) {
                final val intent = new Intent(_context, OrderEditorActivity.class);
                intent.setAction(Intent.ACTION_INSERT);

                IntentUtils.putNullableExtra(intent, EXTRA_PLAYGROUND_ID, playgroundId);
                IntentUtils.putNullableExtra(intent, EXTRA_PLAYER_ID, _playerId);
                IntentUtils.putNullableExtra(intent, EXTRA_ORDER_DATE_START, _orderDateStart);
                IntentUtils.putNullableExtra(intent, EXTRA_ORDER_DATE_END, _orderDateEnd);
                IntentUtils.putNullableExtra(intent, EXTRA_ORDER_TIME_START, _orderTimeStart);
                IntentUtils.putNullableExtra(intent, EXTRA_ORDER_TIME_END, _orderTimeEnd);

                return intent;
            }

            @NonNull
            public IntentBuilder setOrderDateEnd(@Nullable final Long orderDateEnd) {
                _orderDateEnd = orderDateEnd;

                return this;
            }

            @NonNull
            public IntentBuilder setOrderDateStart(@Nullable final Long orderDateStart) {
                _orderDateStart = orderDateStart;

                return this;
            }

            @NonNull
            public IntentBuilder setOrderTimeEnd(@Nullable final Long orderTimeEnd) {
                _orderTimeEnd = orderTimeEnd;

                return this;
            }

            @NonNull
            public IntentBuilder setOrderTimeStart(@Nullable final Long orderTimeStart) {
                _orderTimeStart = orderTimeStart;

                return this;
            }

            @NonNull
            public IntentBuilder setPlayerId(@Nullable final Long playerId) {
                _playerId = playerId;

                return this;
            }

            @NonNull
            private final Context _context;

            @Nullable
            private Long _orderDateEnd;

            @Nullable
            private Long _orderDateStart;

            @Nullable
            private Long _orderTimeEnd;

            @Nullable
            private Long _orderTimeStart;

            @Nullable
            private Long _playerId;

            private IntentBuilder(@NonNull final Context context) {
                Contracts.requireNonNull(context, "context == null");

                _context = context;
            }
        }
    }
}
