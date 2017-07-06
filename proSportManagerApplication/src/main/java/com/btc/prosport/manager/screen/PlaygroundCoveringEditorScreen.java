package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.Covering;
import com.btc.prosport.manager.core.eventArgs.ChangePlaygroundCoveringEventArgs;

import java.util.List;

public interface PlaygroundCoveringEditorScreen extends Screen {

    void revertPlaygroundCovering();

    void displayLoading();

    void displayLoadingError();

    void displayPlaygroundCovering(
        @Nullable Covering playgroundCovering,
        @Nullable List<Covering> coverings);

    @NonNull
    Event<ChangePlaygroundCoveringEventArgs> getChangePlaygroundCoveringEvent();

    @NonNull
    Event<IdEventArgs> getViewPlaygroundCoveringEvent();
}
