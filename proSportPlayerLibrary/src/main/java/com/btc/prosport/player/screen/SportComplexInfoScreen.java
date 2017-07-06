package com.btc.prosport.player.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.SportComplexInfo;
import com.btc.common.extension.eventArgs.IdEventArgs;

public interface SportComplexInfoScreen extends Screen {
    void displaySportComplex(@Nullable final SportComplexInfo sportComplex);

    void displaySportComplexLoading();

    void displaySportComplexLoadingError();

    @NonNull
    Event<IdEventArgs> getViewSportComplexDetailsEvent();

    @NonNull
    Event<IdEventArgs> getViewSportComplexEvent();
}
