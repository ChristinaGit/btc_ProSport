package com.btc.prosport.screen.fragment.proSportAuth;

import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.core.manager.auth.AuthResult;
import com.btc.prosport.screen.activity.proSportVerification.BaseProSportVerificationActivity;
import com.btc.prosport.screen.fragment.BaseProSportFragment;

@Accessors(prefix = "_")
public class ProSportAuthFragment extends BaseProSportFragment {
    @NonNull
    protected Bundle createResult(@NonNull final AuthResult authResult) {
        Contracts.requireNonNull(authResult, "authResult == null");

        val userInfo = new Bundle(2);

        userInfo.putString(AccountManager.KEY_ACCOUNT_NAME, authResult.getPhoneNumber());
        userInfo.putString(AccountManager.KEY_ACCOUNT_TYPE, authResult.getAccountType());

        return userInfo;
    }

    protected void finishAuth() {
        val activity = getActivity();

        activity.setResult(Activity.RESULT_OK);
        activity.finish();
    }

    protected void setAccountAuthenticatorResult(@NonNull final AuthResult authResult) {
        Contracts.requireNonNull(authResult, "authResult == null");

        val activity = getActivity();
        if (activity instanceof BaseProSportVerificationActivity) {
            ((BaseProSportVerificationActivity) activity).setAccountAuthenticatorResult
                (createResult(
                authResult));
        }
    }

    protected void setTitle(@Nullable final String title) {
        val activity = getActivity();
        if (activity instanceof BaseProSportVerificationActivity) {
            ((BaseProSportVerificationActivity) activity).setTitle(title);
        }
    }
}
