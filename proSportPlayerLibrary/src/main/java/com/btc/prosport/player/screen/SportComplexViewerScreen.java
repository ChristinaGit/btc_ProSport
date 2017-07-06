package com.btc.prosport.player.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.common.extension.eventArgs.IdEventArgs;

public interface SportComplexViewerScreen extends PlayerNavigationScreen {
    void displayChangeSportComplexFavoriteStateError();

    void displaySportComplex(@Nullable SportComplexTitle sportComplex);

    void displaySportComplexLoading();

    void displaySportComplexLoadingError();

    @NonNull
    Event<IdEventArgs> getAddSportComplexToFavoriteEvent();

    @NonNull
    Event<IdEventArgs> getRemoveSportComplexFromFavoriteEvent();

    @NonNull
    Event<IdEventArgs> getViewSportComplexEvent();
}
