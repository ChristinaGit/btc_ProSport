package com.btc.prosport.screen.activity.proSportVerification;

import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.prosport.common.R;
import com.btc.prosport.screen.activity.BaseProSportActivity;

@Accessors(prefix = "_")
public class BaseProSportVerificationActivity extends BaseProSportActivity {
    public final void setTitle(@Nullable final String title) {
        val supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(true);
            supportActionBar.setTitle(title);
        }
    }

    @CallSuper
    @Override
    public void finish() {
        if (_accountAuthenticatorResponse != null) {
            val accountAuthenticatorResult = getAccountAuthenticatorResult();
            if (accountAuthenticatorResult != null) {
                _accountAuthenticatorResponse.onResult(accountAuthenticatorResult);
            } else {
                _accountAuthenticatorResponse.onError(
                    AccountManager.ERROR_CODE_CANCELED,
                    getString(R.string.message_error_operation_canceled));
            }
            _accountAuthenticatorResponse = null;
        }

        super.finish();
    }

    @CallSuper
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _accountAuthenticatorResponse =
            getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (_accountAuthenticatorResponse != null) {
            _accountAuthenticatorResponse.onRequestContinued();
        }
    }

    @Nullable
    private AccountAuthenticatorResponse _accountAuthenticatorResponse;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PUBLIC)
    @Nullable
    private Bundle _accountAuthenticatorResult;
}
