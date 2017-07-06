package com.btc.prosport.screen.activity.proSportVerification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.common.R;
import com.btc.prosport.di.qualifier.PresenterNames;
import com.btc.prosport.screen.ProSportVerificationScreen;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class ProSportVerificationActivity extends BaseProSportVerificationActivity
    implements ProSportVerificationScreen {
    private static final String _KEY_PHONE_NUMBER = "phoneNumber";

    private static final String _KEY_AUTH_STATE = "authState";

    private static final String _KEY_ALLOW_REGISTRATION = "allowRegistration";

    @NonNull
    public static Intent getViewIntent(
        @NonNull final Context context,
        @Nullable final ProSportVerificationState proSportVerificationState,
        @Nullable final String phoneNumber,
        final boolean allowRegistration) {
        Contracts.requireNonNull(context, "context == null");

        val intent = new Intent(context, ProSportVerificationActivity.class);
        if (proSportVerificationState != null) {
            intent.putExtra(_KEY_AUTH_STATE, proSportVerificationState);
        }
        if (phoneNumber != null) {
            intent.putExtra(_KEY_PHONE_NUMBER, phoneNumber);
        }
        intent.putExtra(_KEY_ALLOW_REGISTRATION, allowRegistration);
        return intent;
    }

    @NonNull
    @Override
    public Event<ProSportLoginEventArgs> getDisplayLogInScreenEvent() {
        return _displayLogInScreenEvent;
    }

    @NonNull
    @Override
    public Event<ProSportVerificationEventArgs> getDisplayReLogInScreenEvent() {
        return _displayReLogInScreenEvent;
    }

    @NonNull
    @Override
    public Event<ProSportVerificationEventArgs> getDisplaySignUpScreenEvent() {
        return _displaySignUpScreenEvent;
    }

    @Override
    public void onBackPressed() {
        if (getAccountAuthenticatorResult() != null) {
            setResult(RESULT_OK);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();

                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @CallSuper
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        val actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        onHandleIntent(getIntent());

        val fragments = getSupportFragmentManager().getFragments();
        if (fragments == null || fragments.isEmpty()) {
            switch (_authState) {
                case LOG_IN:
                    _displayLogInScreenEvent.rise(new ProSportLoginEventArgs(_phoneNumber,
                                                                             _allowRegistration));
                    break;
                case RE_LOG_IN:
                    _displayReLogInScreenEvent.rise(new ProSportVerificationEventArgs
                                                        (_phoneNumber));
                    break;
                case SIGN_UP:
                    _displaySignUpScreenEvent.rise(new ProSportVerificationEventArgs(_phoneNumber));
                    break;
            }
        }
    }

    @CallSuper
    protected void onHandleIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        if (intent.hasExtra(_KEY_PHONE_NUMBER)) {
            _phoneNumber = intent.getStringExtra(_KEY_PHONE_NUMBER);
        }

        if (intent.hasExtra(_KEY_ALLOW_REGISTRATION)) {
            _allowRegistration = intent.getBooleanExtra(_KEY_ALLOW_REGISTRATION, false);
        }

        if (intent.hasExtra(_KEY_AUTH_STATE)) {
            val authState =
                (ProSportVerificationState) intent.getSerializableExtra(_KEY_AUTH_STATE);
            if (authState != null) {
                _authState = authState;
            }
        }
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getProSportScreenComponent().inject(this);
        val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    @Override
    protected void onReleaseInjectedMembers() {
        super.onReleaseInjectedMembers();

        val presenter = getPresenter();
        if (presenter != null) {
            presenter.unbindScreen();
        }
    }

    @Named(PresenterNames.AUTH)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<ProSportVerificationScreen> _presenter;

    @NonNull
    private final ManagedEvent<ProSportLoginEventArgs> _displayLogInScreenEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<ProSportVerificationEventArgs> _displayReLogInScreenEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<ProSportVerificationEventArgs> _displaySignUpScreenEvent =
        Events.createEvent();

    private boolean _allowRegistration;

    @NonNull
    private ProSportVerificationState _authState = ProSportVerificationState.LOG_IN;

    @Nullable
    private String _phoneNumber;
}
