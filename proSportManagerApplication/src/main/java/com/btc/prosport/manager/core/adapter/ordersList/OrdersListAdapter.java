package com.btc.prosport.manager.core.adapter.ordersList;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.eventArgs.UriEventArgs;
import com.btc.common.extension.view.recyclerView.adapter.PaginationRecyclerViewListAdapter;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.common.utility.UriFactoryUtils;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.OrderMetadataInterval;
import com.btc.prosport.api.model.utility.OrderState;
import com.btc.prosport.core.utility.OrderUtils;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.core.utility.ProSportFormat;
import com.btc.prosport.core.utility.UserUtils;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.ordersList.viewHolder.OrderViewHolder;
import com.btc.prosport.manager.core.adapter.ordersList.viewHolder.OrdersLoadingViewHolder;
import com.btc.prosport.manager.core.eventArgs.ChangeOrderStateEventArgs;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public final class OrdersListAdapter extends PaginationRecyclerViewListAdapter<Order> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrdersListAdapter.class);

    @NonNull
    public final Event<UriEventArgs> getCallPlayerEvent() {
        return _callPlayerEvent;
    }

    @NonNull
    public final Event<ChangeOrderStateEventArgs> getCancelOrderEvent() {
        return _cancelOrderEvent;
    }

    @NonNull
    public final Event<ChangeOrderStateEventArgs> getChangeStatusEvent() {
        return _changeStatusEvent;
    }

    @NonNull
    public final Event<ChangeOrderStateEventArgs> getConfirmOrderEvent() {
        return _confirmOrderEvent;
    }

    @NonNull
    public final Event<IdEventArgs> getDetailsOrderEvent() {
        return _detailsOrderEvent;
    }

    @NonNull
    public final Event<IdEventArgs> getEditOrderEvent() {
        return _editOrderEvent;
    }

    public final int getOrderIndex(final long orderId) {
        return IterableUtils.indexOf(getItems(), new Predicate<Order>() {
            @Override
            public boolean evaluate(final Order order) {
                return order.getId() == orderId;
            }
        });
    }

    public void hideLoading(final long orderId) {
        _loadingOrdersIds.remove((Object) orderId);

        final int orderIndex = getOrderIndex(orderId);
        if (orderIndex != -1) {
            notifyItemChanged(getInnerItemAdapterPosition(orderIndex));
        }
    }

    public boolean isLoading(final long orderId) {
        return _loadingOrdersIds.contains(orderId);
    }

    @Override
    public void onBindInnerItemViewHolder(
        @NonNull final ExtendedRecyclerViewHolder genericHolder, final int position) {
        Contracts.requireNonNull(genericHolder, "genericHolder == null");

        final val holder = (OrderViewHolder) genericHolder;
        final val order = getItem(position);

        final val sportComplex = order.getSportComplex();
        final val playground = order.getPlayground();

        final val context = holder.getContext();

        if (isLoading(order.getId())) {
            holder.loading.setVisibility(View.VISIBLE);
            holder.orderView.setVisibility(View.INVISIBLE);
        } else {
            holder.loading.setVisibility(View.GONE);
            holder.orderView.setVisibility(View.VISIBLE);
        }

        final val intervals = (List<OrderMetadataInterval>) order.getIntervals();
        if (intervals != null && !intervals.isEmpty()) {
            holder.orderIntervalsView.setVisibility(View.VISIBLE);

            final val formattedIntervals =
                ProSportFormat.getFormattedOrderIntervals(context, order, StringUtils.LF);

            holder.orderIntervalsView.setText(formattedIntervals);
        } else {
            holder.orderIntervalsView.setVisibility(View.GONE);
        }

        final val noDataPlaceholder = context.getString(R.string.orders_list_no_data);

        holder.itemView.setTag(R.id.tag_view_order_id, order.getId());
        holder.itemView.setOnClickListener(_itemViewOnClick);

        final val player = order.getPlayer();

        final val orderState = OrderState.byCode(order.getStateCode());
        holder.orderStateMarkerView.setBackgroundResource(OrderUtils.getOrderStateColor
            (orderState));

        holder.orderMoreView.setTag(R.id.tag_view_order, order);
        holder.orderMoreView.setOnClickListener(_showMoreOnClick);

        holder.orderIdView.setText(context.getString(R.string.order_number, order.getId()));

        final val formattedDate = getFormattedDate(context, order);
        holder.orderDateView.setText(formattedDate);

        final String displayUserName;
        if (player != null) {
            displayUserName = UserUtils.getDisplayContactUserName(player);
            holder.userNameView.setVisibility(View.VISIBLE);
        } else {
            displayUserName = null;
            holder.userNameView.setVisibility(View.GONE);
        }
        holder.userNameView.setText(displayUserName);

        final val sportComplexName =
            StringUtils.defaultString(sportComplex == null ? null : sportComplex.getName(),
                                      noDataPlaceholder);
        holder.orderSportComplexView.setText(sportComplexName);

        final val playgroundName =
            StringUtils.defaultString(playground == null ? null : playground.getName(),
                                      noDataPlaceholder);
        holder.orderPlaygroundView.setText(playgroundName);

        final val formattedPrice = ProSportFormat.getFormattedOrderSalePrice(order);
        holder.orderPriceView.setText(formattedPrice);

        holder.itemView.invalidate();
    }

    @NonNull
    @Override
    public ExtendedRecyclerViewHolder onCreateInnerItemViewHolder(
        final ViewGroup parent, final int viewType) {

        final val view = inflateView(R.layout.fragment_orders_list_item, parent);

        return new OrderViewHolder(view);
    }

    public void showLoading(final long orderId) {
        _loadingOrdersIds.add(orderId);

        final int orderIndex = getOrderIndex(orderId);
        if (orderIndex != -1) {
            notifyItemChanged(getInnerItemAdapterPosition(getOrderIndex(orderId)));
        }
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateLoadingViewHolder(
        final ViewGroup parent, final int viewType) {
        final val view = inflateView(R.layout.fragment_orders_list_loading_item, parent);

        final val holder = new OrdersLoadingViewHolder(view);

        DividerItemDecoration.setDecorationMode(holder.itemView,
                                                DividerItemDecoration.DecorationMode.NONE);

        return holder;
    }

    @NonNull
    private final ManagedEvent<UriEventArgs> _callPlayerEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<ChangeOrderStateEventArgs> _cancelOrderEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<ChangeOrderStateEventArgs> _changeStatusEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<ChangeOrderStateEventArgs> _confirmOrderEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _detailsOrderEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _editOrderEvent = Events.createEvent();

    @NonNull
    private final View.OnClickListener _itemViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final long orderId = (long) v.getTag(R.id.tag_view_order_id);
            _detailsOrderEvent.rise(new IdEventArgs(orderId));
        }
    };

    @Getter(onMethod = @__(@Override))
    @NonNull
    private final List<Order> _items = new ArrayList<>();

    @NonNull
    private final List<Long> _loadingOrdersIds = new ArrayList<>();

    @NonNull
    private final View.OnClickListener _showMoreOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final val context = v.getContext();

            final val order = (Order) v.getTag(R.id.tag_view_order);

            final val popupMenu = new PopupMenu(context, v);

            abstract class BaseOrderMenuClickListener implements PopupMenu.OnMenuItemClickListener {
                @CallSuper
                @Override
                public boolean onMenuItemClick(final MenuItem item) {
                    final boolean consumed;

                    final int itemId = item.getItemId();
                    switch (itemId) {
                        case R.id.action_phone_call: {
                            final val player = order.getPlayer();
                            final val phoneNumber = player == null ? null : player.getPhoneNumber();
                            if (phoneNumber != null) {
                                final val phoneUri = UriFactoryUtils.getTelephoneUri(phoneNumber);
                                _callPlayerEvent.rise(new UriEventArgs(phoneUri));
                            }

                            consumed = true;
                            break;
                        }
                        case R.id.action_edit_order: {
                            _editOrderEvent.rise(new IdEventArgs(order.getId()));

                            consumed = true;
                            break;
                        }
                        default: {
                            consumed = false;
                            break;
                        }
                    }

                    return consumed;
                }
            }

            final val orderState = OrderState.byCode(order.getStateCode());
            if (orderState == OrderState.NOT_CONFIRMED) {
                popupMenu.inflate(R.menu.order_not_confirmed);

                popupMenu.setOnMenuItemClickListener(new BaseOrderMenuClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        final boolean consumed;

                        final int itemId = item.getItemId();
                        switch (itemId) {
                            case R.id.action_confirm_order: {
                                final val order = (Order) v.getTag(R.id.tag_view_order);
                                final val oldState = OrderState.byCode(order.getStateCode());
                                final val eventArgs = new ChangeOrderStateEventArgs(order.getId(),
                                                                                    oldState,
                                                                                    OrderState
                                                                                        .CONFIRMED);
                                _confirmOrderEvent.rise(eventArgs);

                                consumed = true;
                                break;
                            }
                            case R.id.action_cancel_order: {
                                final val oldState = OrderState.byCode(order.getStateCode());
                                final val newState = OrderState.CANCELED;
                                final val eventArgs = new ChangeOrderStateEventArgs(order.getId(),
                                                                                    oldState,
                                                                                    newState);
                                _cancelOrderEvent.rise(eventArgs);

                                consumed = true;
                                break;
                            }
                            default: {
                                consumed = super.onMenuItemClick(item);
                                break;
                            }
                        }

                        return consumed;
                    }
                });
            } else {
                popupMenu.inflate(R.menu.order);
                popupMenu.setOnMenuItemClickListener(new BaseOrderMenuClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        final boolean consumed;

                        final int itemId = item.getItemId();
                        switch (itemId) {
                            case R.id.action_change_order_state: {
                                final val order = (Order) v.getTag(R.id.tag_view_order);
                                final val oldState = OrderState.byCode(order.getStateCode());
                                _changeStatusEvent.rise(new ChangeOrderStateEventArgs(order.getId(),
                                                                                      oldState,
                                                                                      null));

                                consumed = true;
                                break;
                            }
                            default: {
                                consumed = super.onMenuItemClick(item);
                                break;
                            }
                        }

                        return consumed;
                    }
                });
            }
            popupMenu.show();
        }
    };

    @Nullable
    private String getFormattedDate(
        @NonNull final Context context, @Nullable final Order order) {
        Contracts.requireNonNull(context, "context == null");

        final String formattedDate;

        if (order != null) {
            final val orderDateFormat = ProSportFormat.getIntervalLongDateFormat();

            final val orderDate = order.getCreateDate();
            if (orderDate != null) {
                final val date = ProSportApiDataUtils.parseDate(orderDate);
                formattedDate = orderDateFormat.format(date);
            } else {
                formattedDate = null;
            }
        } else {
            formattedDate = null;
        }

        return formattedDate;
    }
}

