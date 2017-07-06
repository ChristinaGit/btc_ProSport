package com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.item;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.Item;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.ScheduleAdapter;

@ToString
@Accessors(prefix = "_")
public class HeaderItem implements Item {
    public HeaderItem(final int weekDay, @NonNull final String weekdayName) {
        Contracts.requireNonNull(weekdayName, "weekdayName == null");

        _day = weekDay;
        _weekdayName = weekdayName;
    }

    @Override
    public int getItemType() {
        return ScheduleAdapter.VIEW_TYPE_DAY_OFF;
    }

    @Getter
    private final int _day;

    @Getter
    private final String _weekdayName;

    @Getter
    @Setter
    private boolean _dayOff;
}
