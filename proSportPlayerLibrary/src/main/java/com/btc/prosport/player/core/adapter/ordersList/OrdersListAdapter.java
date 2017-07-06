package com.btc.prosport.player.core.adapter.ordersList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.var;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.eventArgs.UriEventArgs;
import com.btc.common.extension.view.recyclerView.adapter.PaginationRecyclerViewListAdapter;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.common.utility.UriFactoryUtils;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.OrderMetadataInterval;
import com.btc.prosport.api.model.utility.OrderState;
import com.btc.prosport.core.utility.OrderUtils;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.core.utility.ProSportFormat;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.adapter.sportComplexesList.SportComplexesAdapter;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public final class OrdersListAdapter extends PaginationRecyclerViewListAdapter<Order> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SportComplexesAdapter.class);

    @NonNull
    public final Event<UriEventArgs> getCallPlayerEvent() {
        return _callPlayerEvent;
    }

    @NonNull
    public final ManagedEvent<IdEventArgs> getNavigateToPlaygroundEvent() {
        return _navigateToPlaygroundEvent;
    }

    public final int getOrderIndex(final long orderId) {
        return IterableUtils.indexOf(getItems(), new Predicate<Order>() {
            @Override
            public boolean evaluate(final Order object) {
                return object.getId() == orderId;
            }
        });
    }

    @Override
    protected void onBindInnerItemViewHolder(
        @NonNull final ExtendedRecyclerViewHolder genericHolder, final int position) {
        Contracts.requireNonNull(genericHolder, "genericHolder == null");

        final val holder = (OrderViewHolder) genericHolder;
        final val context = holder.getContext();
        final val order = getItem(position);

        final val intervals = (List<OrderMetadataInterval>) order.getIntervals();
        if (intervals != null && !intervals.isEmpty()) {
            holder.orderIntervalsView.setVisibility(View.VISIBLE);

            final val formattedIntervals =
                ProSportFormat.getFormattedOrderIntervals(context, order, StringUtils.LF);

            holder.orderIntervalsView.setText(formattedIntervals);
        } else {
            holder.orderIntervalsView.setVisibility(View.GONE);
        }

        holder.itemView.setTag(R.id.tag_view_order_id, order.getId());
        holder.orderMoreView.setTag(R.id.tag_view_order, order);
        holder.orderMoreView.setOnClickListener(_showMoreOnClick);

        final val noDataPlaceholder = context.getString(R.string.orders_list_no_data);

        holder.orderIdView.setText(context.getString(R.string.orders_list_id_format,
                                                     order.getId()));

        final val formattedPrice = ProSportFormat.getFormattedOrderSalePrice(order);
        holder.orderPriceView.setText(formattedPrice);

        final val orderPlace = ProSportFormat.getFormattedOrderPlace(order);
        final val formattedOrderPlace =
            context.getString(R.string.orders_list_place_format, orderPlace);

        holder.orderPlaceView.setText(StringUtils.defaultString(formattedOrderPlace,
                                                                noDataPlaceholder));

        final val orderState = OrderState.byCode(order.getStateCode());
        final val orderStateName = context.getString(OrderUtils.getOrderStateName(orderState));
        final var formattedState =
            context.getString(R.string.orders_list_state_format, orderStateName);
        holder.orderStateView.setText(formattedState);

        holder.orderDateView.setText(getFormattedDate(context, order));
        // TODO: 28.04.2017 Enable order menu
        holder.orderMoreView.setVisibility(View.GONE);
        final val orderStateColor = OrderUtils.getOrderStateColor(orderState);
        holder.orderStateMarkerView.setBackgroundResource(orderStateColor);
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateInnerItemViewHolder(
        final ViewGroup parent, final int viewType) {
        final val view = inflateView(R.layout.fragment_orders_list_item, parent);

        return new OrderViewHolder(view);
    }

    @NonNull
    @Override
    protected ExtendedRecyclerViewHolder onCreateLoadingViewHolder(
        final ViewGroup parent, final int viewType) {
        final val view = inflateView(R.layout.layout_list_item_loading, parent);

        return new OrdersLoadingViewHolder(view);
    }

    @NonNull
    private final ManagedEvent<UriEventArgs> _callPlayerEvent = Events.createEvent();

    @Getter(onMethod = @__(@Override))
    @NonNull
    private final List<Order> _items = new ArrayList<>();

    @NonNull
    private final ManagedEvent<IdEventArgs> _navigateToPlaygroundEvent = Events.createEvent();

    @NonNull
    private final View.OnClickListener _showMoreOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final val context = v.getContext();

            final val order = (Order) v.getTag(R.id.tag_view_order);

            final val popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.order);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final MenuItem item) {
                    final boolean consumed;

                    final int itemId = item.getItemId();
                    if (R.id.action_navigate_to_playground == itemId) {
                        final val playground = order.getPlayground();

                        if (playground != null) {
                            _navigateToPlaygroundEvent.rise(new IdEventArgs(playground.getId()));
                        }

                        consumed = true;
                    } else if (R.id.action_phone_call == itemId) {
                        final val sportComplex = order.getSportComplex();
                        final val phoneNumbers =
                            sportComplex == null ? null : sportComplex.getPhoneNumbers();
                        final val phoneNumber = phoneNumbers == null || phoneNumbers.isEmpty()
                                                ? null
                                                : phoneNumbers.get(0);

                        if (phoneNumber != null) {
                            final val phoneUri = UriFactoryUtils.getTelephoneUri(phoneNumber);
                            _callPlayerEvent.rise(new UriEventArgs(phoneUri));
                        }

                        consumed = true;
                    } else {
                        consumed = false;
                    }

                    return consumed;
                }
            });
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