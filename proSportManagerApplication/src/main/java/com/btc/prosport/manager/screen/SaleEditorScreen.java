package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.Sale;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.prosport.manager.core.eventArgs.CreateSaleEventArgs;

import java.util.List;
import java.util.Map;

public interface SaleEditorScreen extends Screen {
    void displayCreatedSale(@Nullable final Sale sale);

    void displayLoadSalePlacesError();

    void displaySalePlaces(@Nullable Map<SportComplexTitle, List<PlaygroundTitle>> places);

    @NonNull
    Event<CreateSaleEventArgs> getCreateSaleEvent();

    @NonNull
    NoticeEvent getLoadSalePlacesEvent();
}
