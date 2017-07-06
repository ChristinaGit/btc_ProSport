package com.btc.prosport.player.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.PlaygroundPreview;
import com.btc.common.extension.eventArgs.IdEventArgs;

import java.util.List;

public interface PlaygroundsListScreen extends Screen {
    void displayPlaygrounds(@Nullable List<PlaygroundPreview> playgrounds);

    void displayPlaygroundsLoading();

    void displayPlaygroundsLoadingError();

    @NonNull
    Event<IdEventArgs> getRefreshPlaygroundsEvent();

    @NonNull
    Event<IdEventArgs> getReservePlaygroundEvent();

    @NonNull
    Event<IdEventArgs> getViewPlaygroundsEvent();
}
