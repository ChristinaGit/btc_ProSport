package com.btc.prosport.core.eventArgs;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.event.eventArgs.EventArgs;
import com.btc.prosport.core.manager.account.ProSportAccount;

@Accessors(prefix = "_")
public class LogoutEventArgs extends EventArgs {
    public LogoutEventArgs(@NonNull final ProSportAccount account) {
        Contracts.requireNonNull(account, "account == null");

        _account = account;
    }

    @Getter
    @NonNull
    private final ProSportAccount _account;
}
