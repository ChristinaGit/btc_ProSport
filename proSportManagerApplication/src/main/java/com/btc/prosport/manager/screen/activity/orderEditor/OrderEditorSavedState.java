package com.btc.prosport.manager.screen.activity.orderEditor;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.utility.WeekDay;

import org.parceler.Parcel;

import java.util.List;

@Parcel
@ToString
@Accessors(prefix = "_")
/*package-private*/ final class OrderEditorSavedState {

    @Getter
    @Setter
    @Nullable
    /*package-private*/ List<Interval> _intervals;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ OrderEditorMode _mode;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _orderId;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _orderStartDate;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _playerId;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _playgroundId;

    @Parcel
    @ToString
    @Accessors(prefix = "_")
    /*package-private*/ static final class Interval {
        @Getter
        @Setter
        /*package-private*/ long _dateEnd;

        @Getter
        @Setter
        /*package-private*/ long _dateStart;

        @Getter
        @Setter
        @Nullable
        /*package-private*/ Long _id;

        @Getter
        @Setter
        @Nullable
        /*package-private*/ List<WeekDay> _repeatWeekDays;

        @Getter
        @Setter
        /*package-private*/ boolean _repeatable;

        @Getter
        @Setter
        /*package-private*/ long _timeEnd;

        @Getter
        @Setter
        /*package-private*/ long _timeStart;
    }
}
