package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.Sale;

import java.util.List;

public interface SalesListScreen extends Screen {
    void displayDeletedSale(@Nullable Long saleId);

    void displaySales(@Nullable List<Sale> sales, boolean lastPage);

    void displaySalesLoading();

    void displaySalesLoadingError();

    void displaySalesPage(@Nullable List<Sale> sales, boolean lastPage);

    @NonNull
    Event<IdEventArgs> getDeleteSaleEvent();

    @NonNull
    NoticeEvent getRefreshSalesEvent();

    @NonNull
    NoticeEvent getViewNextSalesPageEvent();

    @NonNull
    NoticeEvent getViewSalesEvent();
}
