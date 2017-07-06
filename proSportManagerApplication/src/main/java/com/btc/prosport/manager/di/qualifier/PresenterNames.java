package com.btc.prosport.manager.di.qualifier;

import com.btc.common.contract.Contracts;

public final class PresenterNames {
    private static final String NAME_PREFIX = "presenter:";

    public static final String PLAYGROUND_GENERAL_INFO_EDITOR =
        NAME_PREFIX + "playground_general_info_editor";

    public static final String PLAYGROUND_EDITOR = NAME_PREFIX + "playground_editor";

    public static final String PLAYGROUND_SCHEDULE_EDITOR =
        NAME_PREFIX + "playground_schedule_editor";

    public static final String ORDER_LIST = NAME_PREFIX + "order_list";

    public static final String SALES_LIST = NAME_PREFIX + "sales_list";

    public static final String SCHEDULE = NAME_PREFIX + "schedule";

    public static final String WORKSPACE = NAME_PREFIX + "workspace";

    public static final String ORDER_DETAILS = NAME_PREFIX + "order_details";

    public static final String ORDER_EDITOR = NAME_PREFIX + "order_editor";

    public static final String PRICE_EDITOR = NAME_PREFIX + "price_editor";

    public static final String SALE_EDITOR = NAME_PREFIX + "sale_editor";

    public static final String ORDER = NAME_PREFIX + "order";

    public static final String FEEDBACK = NAME_PREFIX + "feedback";

    public static final String SETTINGS = NAME_PREFIX + "settings";

    public static final String DIMENSIONS_EDITOR = NAME_PREFIX + "dimensions_editor";

    public static final String COVERING_EDITOR = NAME_PREFIX + "covering_editor";

    public static final String ATTRIBUTE_EDITOR = NAME_PREFIX + "attributes";

    private PresenterNames() {
        Contracts.unreachable();
    }
}
