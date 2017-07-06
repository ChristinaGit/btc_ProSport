package com.btc.prosport.manager.core.adapter.playgroundScheduleEditor;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.item.HeaderItem;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.item.TimeItem;

@Accessors(prefix = "_")
public class WeekdayItem {
    @Getter
    @Setter
    HeaderItem _headerItem;

    @Getter
    @Setter
    TimeItem _timeItem;
}
