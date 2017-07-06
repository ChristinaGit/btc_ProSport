package com.btc.prosport.player.screen;

import android.support.annotation.NonNull;

import com.btc.common.event.generic.Event;
import com.btc.common.mvp.screen.Screen;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.player.core.eventArgs.SportComplexesSearchEventArgs;

public interface SportComplexesViewerPageScreen extends Screen {
    void displaySportComplexesLoading();

    void displaySportComplexesLoadingError();

    @NonNull
    Event<SportComplexesSearchEventArgs> getRefreshSportComplexesEvent();

    @NonNull
    Event<SportComplexesSearchEventArgs> getViewNextSportComplexesPageEvent();

    @NonNull
    Event<IdEventArgs> getViewSportComplexDetailsEvent();

    @NonNull
    Event<SportComplexesSearchEventArgs> getViewSportComplexesEvent();
}
