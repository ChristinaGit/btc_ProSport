package com.btc.prosport.api.model.utility;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.core.manager.account.AccountContracts;

@Accessors(prefix = "_")
public enum UserRole {
    MANGER(AccountContracts.ACCOUNT_TYPE_MANAGER, false),
    PLAYER(AccountContracts.ACCOUNT_TYPE_PLAYER, true);

    @NonNull
    public static UserRole byAccountType(@NonNull final String accountType) {
        Contracts.requireNonNull(accountType, "accountType == null");

        UserRole foundUserRole = null;

        for (final val userRole : values()) {
            if (userRole.getAccountType().equals(accountType)) {
                foundUserRole = userRole;
            }
        }

        if (foundUserRole == null) {
            throw new IllegalArgumentException("Account type " + accountType + " not found");
        }

        return foundUserRole;
    }

    @Getter
    @NonNull
    private final String _accountType;

    @Getter
    private final boolean _allowRegistration;

    UserRole(@NonNull final String accountType, final boolean allowRegistration) {
        Contracts.requireNonNull(accountType, "accountType == null");

        _accountType = accountType;
        _allowRegistration = allowRegistration;
    }
}
