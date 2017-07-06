package com.btc.prosport.api;

import android.text.format.DateUtils;

import com.btc.common.contract.Contracts;

import java.util.Calendar;

public final class ProSportDataContract {
    public static final long INTERVAL_LENGTH = DateUtils.HOUR_IN_MILLIS / 2;

    public static final int DAY_IN_INTERVALS = (int) (DateUtils.DAY_IN_MILLIS / INTERVAL_LENGTH);

    public static final int FIRST_PAGE_INDEX = 1;

    public static final int FIRST_INTERVAL_INDEX = 1;

    public static final int LAST_INTERVAL_INDEX = FIRST_INTERVAL_INDEX + DAY_IN_INTERVALS - 1;

    public static final int[] PRICE_DAY_ORDER = {
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY,
        Calendar.SUNDAY};

    private ProSportDataContract() {
        Contracts.unreachable();
    }
}
