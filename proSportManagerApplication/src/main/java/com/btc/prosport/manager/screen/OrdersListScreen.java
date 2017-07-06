package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.eventArgs.UriEventArgs;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.manager.core.eventArgs.ChangeOrderStateEventArgs;
import com.btc.prosport.manager.core.eventArgs.OrdersParamsEventArgs;

import java.util.List;

public interface OrdersListScreen extends Screen {
    void disableOrdersLoading();

    void displayChangedOrder(@Nullable Order order);

    void displayOrderLoading(long orderId);

    void displayOrders(@Nullable List<Order> orders, boolean lastPage);

    void displayOrdersLoading();

    void displayOrdersLoadingError();

    void displayOrdersPage(@Nullable List<Order> orders, boolean lastPage);

    @NonNull
    Event<UriEventArgs> getCallPlayerEvent();

    @NonNull
    Event<ChangeOrderStateEventArgs> getChangeStateEvent();

    @NonNull
    Event<IdEventArgs> getDetailsOrderEvent();

    @NonNull
    Event<OrdersParamsEventArgs> getRefreshOrdersEvent();

    @NonNull
    Event<OrdersParamsEventArgs> getViewNextOrdersPageEvent();

    @NonNull
    Event<OrdersParamsEventArgs> getViewOrdersEvent();

    void hideOrderLoading(long orderId);
}
