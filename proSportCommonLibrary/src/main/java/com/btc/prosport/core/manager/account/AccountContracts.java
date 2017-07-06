package com.btc.prosport.core.manager.account;

import com.btc.common.contract.Contracts;

public final class AccountContracts {
    public static final String ACCOUNT_TYPE_MANAGER = "com.btc.prosport.MANAGER_ACCOUNT";

    public static final String ACCOUNT_TYPE_PLAYER = "com.btc.prosport.PLAYER_ACCOUNT";

    public static final String TOKEN_TYPE_FULL_ACCESS = "fullAccess";

    public static final String KEY_REFRESH_TOKEN = "refreshToken";

    private AccountContracts() {
        Contracts.unreachable();
    }
}
