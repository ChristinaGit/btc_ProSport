package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.Price;
import com.btc.prosport.manager.core.eventArgs.ChangePlaygroundPricesEventArgs;

import java.util.List;

public interface PriceEditorScreen extends Screen {
    void displayPrices(@Nullable List<Price> prices);

    void displayPricesChangedResult();

    void displayPricesLoading();

    void displayPricesLoadingError();

    @NonNull
    Event<ChangePlaygroundPricesEventArgs> getChangePlaygroundPricesEvent();

    @NonNull
    Event<IdEventArgs> getViewPricesEvent();
}
