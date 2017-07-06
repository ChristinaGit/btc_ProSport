package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;

import com.btc.common.event.generic.Event;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.manager.core.Dimension;
import com.btc.prosport.manager.core.eventArgs.ChangePlaygroundDimensionsEventArgs;

import javax.annotation.Nonnull;

public interface PlaygroundDimensionsEditorScreen extends Screen {

    void displayDimensionsLoading();

    void displayDimensionsLoadingError();

    void displayPlaygroundDimensions(
        @Nonnull Dimension dimension);

    void revertChangedDimensions();

    @NonNull
    Event<ChangePlaygroundDimensionsEventArgs> getChangePlaygroundDimensionsEvent();

    @NonNull
    Event<IdEventArgs> getViewPlaygroundDimensionsEvent();
}
