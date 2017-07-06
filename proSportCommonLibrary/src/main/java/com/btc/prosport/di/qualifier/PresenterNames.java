package com.btc.prosport.di.qualifier;

import com.btc.common.contract.Contracts;

public final class PresenterNames {
    private static final String NAME_PREFIX = "presenter:";

    public static final String AUTH = NAME_PREFIX + "auth";

    public static final String LOG_IN = NAME_PREFIX + "logIn";

    public static final String RE_LOG_IN = NAME_PREFIX + "reLogIn";

    public static final String SIGN_UP = NAME_PREFIX + "signUp";

    public static final String ADDITIONAL_INFO = NAME_PREFIX + "additionalInfo";

    public static final String FEEDBACK = NAME_PREFIX + "feedback";

    private PresenterNames() {
        Contracts.unreachable();
    }
}
