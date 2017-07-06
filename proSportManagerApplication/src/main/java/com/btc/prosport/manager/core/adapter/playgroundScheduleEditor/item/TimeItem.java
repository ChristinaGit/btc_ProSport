package com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.item;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.Item;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.ScheduleAdapter;

@ToString
@Accessors(prefix = "_")
public class TimeItem implements Item {
    @Override
    public int getItemType() {
        return ScheduleAdapter.VIEW_TYPE_TIME;
    }

    @Getter
    @Setter
    private long _endTime;

    @Getter
    @Setter
    private long _startTime;

    public enum Type {
        START_TIME, END_TIME
    }
}
