package com.btc.prosport.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.screen.ProSportVerificationScreen;
import com.btc.prosport.screen.activity.proSportVerification.ProSportLoginEventArgs;
import com.btc.prosport.screen.activity.proSportVerification.ProSportVerificationEventArgs;

@Accessors(prefix = "_")
public class ProSportVerificationPresenter extends BasePresenter<ProSportVerificationScreen> {
    public ProSportVerificationPresenter(
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        _proSportNavigationManager = proSportNavigationManager;
    }

    @CallSuper
    @Override
    protected void onScreenCreate(@NonNull final ProSportVerificationScreen screen) {
        super.onScreenCreate(Contracts.requireNonNull(screen, "screen == null"));

        screen.getDisplayReLogInScreenEvent().addHandler(_displayReLogInEventHandler);
        screen.getDisplayLogInScreenEvent().addHandler(_displayLogInEventHandler);
        screen.getDisplaySignUpScreenEvent().addHandler(_displaySignUpEventHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDestroy(@NonNull final ProSportVerificationScreen screen) {
        super.onScreenDestroy(Contracts.requireNonNull(screen, "screen == null"));

        screen.getDisplayReLogInScreenEvent().addHandler(_displayReLogInEventHandler);
        screen.getDisplayLogInScreenEvent().removeHandler(_displayLogInEventHandler);
        screen.getDisplaySignUpScreenEvent().removeHandler(_displaySignUpEventHandler);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportNavigationManager _proSportNavigationManager;

    @NonNull
    private final EventHandler<ProSportLoginEventArgs> _displayLogInEventHandler =
        new EventHandler<ProSportLoginEventArgs>() {
            @Override
            public void onEvent(
                @NonNull final ProSportLoginEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                getProSportNavigationManager().navigateToLogIn(eventArgs.getPhoneNumber(),
                                                               eventArgs.isAllowRegistration());
            }
        };

    @NonNull
    private final EventHandler<ProSportVerificationEventArgs> _displayReLogInEventHandler =
        new EventHandler<ProSportVerificationEventArgs>() {
            @Override
            public void onEvent(
                @NonNull final ProSportVerificationEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                getProSportNavigationManager().navigateToReLogIn(eventArgs.getPhoneNumber());
            }
        };

    @NonNull
    private final EventHandler<ProSportVerificationEventArgs> _displaySignUpEventHandler =
        new EventHandler<ProSportVerificationEventArgs>() {
            @Override
            public void onEvent(
                @NonNull final ProSportVerificationEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                getProSportNavigationManager().navigateToSignUp(eventArgs.getPhoneNumber());
            }
        };
}
