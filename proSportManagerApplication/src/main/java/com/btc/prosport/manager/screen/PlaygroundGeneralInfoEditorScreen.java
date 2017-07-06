package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;

import com.btc.common.event.generic.Event;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.City;
import com.btc.prosport.api.model.SubwayStation;
import com.btc.prosport.core.result.PhotoResult;
import com.btc.prosport.manager.core.eventArgs.PlacePickerEventArgs;
import com.btc.prosport.manager.core.eventArgs.UpdateSubwaysEventArgs;
import com.btc.prosport.manager.core.result.PlacePickerResult;

import java.util.List;

public interface PlaygroundGeneralInfoEditorScreen extends Screen {
    void displayCity(@NonNull final List<City> cities);

    void displayPhoto(@NonNull final PhotoResult photoResult);

    void displayPickedLocation(@NonNull final PlacePickerResult placePickerResult);

    void displaySubways(@NonNull final List<SubwayStation> locations);

    NoticeEvent getCitiesUpdateEvent();

    @NonNull
    Event<PlacePickerEventArgs> getPickLocationEvent();

    @NonNull
    NoticeEvent getPickPhotoEvent();

    @NonNull
    Event<UpdateSubwaysEventArgs> getSubwaysUpdateEvent();
}
