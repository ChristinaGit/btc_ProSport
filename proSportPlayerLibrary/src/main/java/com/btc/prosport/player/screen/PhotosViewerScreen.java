package com.btc.prosport.player.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.Photo;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.common.extension.eventArgs.IdEventArgs;

import java.util.List;

public interface PhotosViewerScreen extends Screen {
    void displayPhotosLoading();

    void displayPhotosLoadingError();

    void displayPlaygroundPhotos(
        @Nullable PlaygroundTitle playground, @Nullable List<Photo> photos);

    void displaySportComplexPhotos(
        @Nullable SportComplexTitle sportComplex, @Nullable List<Photo> photos);

    @NonNull
    Event<IdEventArgs> getViewPlaygroundPhotosEvent();

    @NonNull
    Event<IdEventArgs> getViewSportComplexPhotosEvent();
}
