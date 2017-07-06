package com.btc.prosport.manager.screen.activity.saleEditor;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.utility.SaleType;
import com.btc.prosport.api.model.utility.WeekDay;

import org.parceler.Parcel;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Parcel
@ToString
@Accessors(prefix = "_")
/*package-private*/ final class SaleEditorSavedState {

    @Getter
    @Setter
    @Nullable
    /*package-private*/ List<Interval> _intervals;

    @Getter
    @Setter
    @Nullable
     /*package-private*/ Map<Long, Set<Long>> _placeSelection;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ SaleType _saleType;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Integer _saleValue;

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
