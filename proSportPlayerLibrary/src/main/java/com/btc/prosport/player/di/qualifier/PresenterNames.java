package com.btc.prosport.player.di.qualifier;

import com.btc.common.contract.Contracts;

public final class PresenterNames {
    private static final String NAME_PREFIX = "presenter:";

    public static final String SPORT_COMPLEX_VIEWER = NAME_PREFIX + "sport_complex_viewer";

    public static final String SPORT_COMPLEX_DETAIL = NAME_PREFIX + "sport_complex_detail";

    public static final String SPORT_COMPLEXES_VIEWER = NAME_PREFIX + "sport_complexes_viewer";

    public static final String ORDERS_VIEWER = NAME_PREFIX + "orders_viewer";

    public static final String ORDERS_LIST = NAME_PREFIX + "orders_list";

    public static final String SPORT_COMPLEXES_LIST = NAME_PREFIX + "sport_complexes_list";

    public static final String PLAYGROUNDS_LIST = NAME_PREFIX + "playgrounds_list";

    public static final String SPORT_COMPLEXES_MAP = NAME_PREFIX + "sport_complexes";

    public static final String SPORT_COMPLEX_INFO = NAME_PREFIX + "sport_complex_info";

    public static final String INTERVALS = NAME_PREFIX + "intervals";

    public static final String BOOKING = NAME_PREFIX + "booking";

    public static final String PHOTOS_VIEWER = NAME_PREFIX + "photos_viewer";

    public static final String SETTINGS = NAME_PREFIX + "settings";

    public static final String FEEDBACK = NAME_PREFIX + "feedback";

    private PresenterNames() {
        Contracts.unreachable();
    }
}
