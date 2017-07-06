package com.btc.prosport.player.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.eventArgs.UriEventArgs;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.SportComplexDetail;
import com.btc.prosport.player.core.eventArgs.ViewPhotoEventArgs;

public interface SportComplexDetailScreen extends Screen {
    void displaySportComplex(@Nullable SportComplexDetail sportComplex);

    void displaySportComplexLoading();

    void displaySportComplexLoadingError();

    @NonNull
    Event<IdEventArgs> getRefreshSportComplexEvent();

    @NonNull
    Event<UriEventArgs> getSportComplexPhoneCallEvent();

    @NonNull
    Event<IdEventArgs> getViewSportComplexEvent();

    @NonNull
    Event<ViewPhotoEventArgs> getViewSportComplexPhotosEvent();
}
