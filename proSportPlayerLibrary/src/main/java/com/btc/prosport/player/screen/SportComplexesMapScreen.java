package com.btc.prosport.player.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.prosport.api.model.SportComplexLocation;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.player.core.eventArgs.SportComplexesSearchEventArgs;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

public interface SportComplexesMapScreen extends SportComplexesViewerPageScreen {
    void displayInsufficientMapPermissionsError();

    void displaySportComplexes(
        @NonNull GoogleMap googleMap,
        @Nullable List<SportComplexLocation> sportComplexes,
        boolean lastPage);

    void displaySportComplexesPage(
        @NonNull GoogleMap googleMap,
        @Nullable List<SportComplexLocation> sportComplexes,
        boolean lastPage);

    @NonNull
    Event<SportComplexesSearchEventArgs> getRetryViewSportComplexesEvent();

    @NonNull
    Event<IdEventArgs> getViewSportComplexInfoEvent();
}
