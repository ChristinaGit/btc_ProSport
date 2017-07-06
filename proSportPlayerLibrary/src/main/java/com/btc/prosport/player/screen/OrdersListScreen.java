package com.btc.prosport.player.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.Order;

import java.util.List;

public interface OrdersListScreen extends Screen {
    void displayOrders(@Nullable List<Order> orders, boolean lastPage);

    void displayOrdersLoading();

    void displayOrdersLoadingError();

    void displayOrdersPage(@Nullable List<Order> orders, boolean lastPage);

    void displayRequireAuthorizationError();

    @NonNull
    NoticeEvent getRefreshOrdersEvent();

    @NonNull
    NoticeEvent getViewNextOrdersPageEvent();

    @NonNull
    NoticeEvent getViewOrdersEvent();
}
