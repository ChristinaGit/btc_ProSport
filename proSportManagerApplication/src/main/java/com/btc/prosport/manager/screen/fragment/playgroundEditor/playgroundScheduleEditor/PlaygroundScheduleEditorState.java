package com.btc.prosport.manager.screen.fragment.playgroundEditor.playgroundScheduleEditor;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.parceler.Parcel;

import java.util.HashMap;

@Parcel
@Accessors(prefix = "_")
public class PlaygroundScheduleEditorState {
    @Getter
    @Setter
    @Nullable
    /*package-private*/ HashMap<Integer, Weekday> _scheduleDayList;

    @Parcel
    @Accessors(prefix = "_")
    public static class Weekday {
        @Getter
        @Setter
        /*package-private*/ boolean _dayOff;

        @Getter
        @Setter
        /*package-private*/ long _endTime;

        @Getter
        @Setter
        /*package-private*/ long _startTime;
    }
}
