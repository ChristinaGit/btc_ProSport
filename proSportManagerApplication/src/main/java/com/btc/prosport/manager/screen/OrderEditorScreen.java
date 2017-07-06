package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.User;
import com.btc.prosport.manager.core.eventArgs.ChangeOrderEventArgs;
import com.btc.prosport.manager.core.eventArgs.ChangeOrderForUnknownPlayerEventArgs;
import com.btc.prosport.manager.core.eventArgs.CreateOrderEventArgs;
import com.btc.prosport.manager.core.eventArgs.CreateOrderForUnknownPlayerEventArgs;
import com.btc.prosport.manager.core.eventArgs.UserSearchEventArgs;

public interface OrderEditorScreen extends Screen {
    void displayChangedOrder(@Nullable Order order);

    void displayOrder(@Nullable Order order);

    void displayPlayerSearchError();

    void displayPlayerSearchResult(@Nullable User player);

    @NonNull
    Event<ChangeOrderEventArgs> getChangeOrderEvent();

    @NonNull
    Event<ChangeOrderForUnknownPlayerEventArgs> getChangeOrderForUnknownPlayerEvent();

    @NonNull
    Event<CreateOrderEventArgs> getCreateOrderEvent();

    @NonNull
    Event<CreateOrderForUnknownPlayerEventArgs> getCreateOrderForUnknownPlayerEvent();

    @NonNull
    Event<UserSearchEventArgs> getSearchPlayerEvent();

    @NonNull
    Event<IdEventArgs> getViewOrderEvent();

    @NonNull
    Event<IdEventArgs> getViewPlayerEvent();
}
