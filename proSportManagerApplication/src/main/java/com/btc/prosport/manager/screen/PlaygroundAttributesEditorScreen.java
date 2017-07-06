package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;

import com.btc.common.event.generic.Event;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.Attribute;
import com.btc.prosport.manager.core.eventArgs.ChangePlaygroundAttributesEventArgs;

import java.util.List;

public interface PlaygroundAttributesEditorScreen extends Screen {

    void displayLoading();

    void displayLoadingError();

    void displayPlaygroundAttributes(
        @NonNull List<Attribute> playgroundAttributes, @NonNull List<Attribute> allAttributes);

    @NonNull
    Event<ChangePlaygroundAttributesEventArgs> getChangePlaygroundAttributesEvent();

    @NonNull
    Event<IdEventArgs> getViewPlaygroundAttributesEvent();

    void revertChangedAttributes();
}
