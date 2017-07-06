package com.btc.prosport.di.subscreen.module;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.api.ProSportApi;
import com.btc.prosport.api.model.utility.UserRole;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.auth.AuthManager;
import com.btc.prosport.core.manager.firebaseMessaging.FirebaseMessagingManager;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.di.qualifier.PresenterNames;
import com.btc.prosport.di.qualifier.ScopeNames;
import com.btc.prosport.di.subscreen.SubscreenScope;
import com.btc.prosport.presenter.FeedbackViewerPresenter;
import com.btc.prosport.presenter.ProSportAdditionalInfoPresenter;
import com.btc.prosport.presenter.ProSportLogInPresenter;
import com.btc.prosport.presenter.ProSportReLogInPresenter;
import com.btc.prosport.presenter.ProSportSignUpPresenter;
import com.btc.prosport.screen.FeedbackViewerScreen;
import com.btc.prosport.screen.ProSportAdditionalInfoScreen;
import com.btc.prosport.screen.ProSportLogInScreen;
import com.btc.prosport.screen.ProSportReLogInScreen;
import com.btc.prosport.screen.ProSportSignUpScreen;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
@SubscreenScope
public class ProSportPresenterSubscreenModule {
    @Named(PresenterNames.ADDITIONAL_INFO)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<ProSportAdditionalInfoScreen> provideAdditionalInfoPresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final ProSportApi proSportApi,
        @NonNull final UserRole userRole) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApi, "proSportApi == null");
        Contracts.requireNonNull(userRole, "userRole == null");

        return new ProSportAdditionalInfoPresenter(rxManager,
                                                   messageManager,
                                                   proSportAccountHelper,
                                                   proSportApi,
                                                   userRole);
    }

    @Named(PresenterNames.FEEDBACK)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<FeedbackViewerScreen> provideFeedbackFragmentPresenter(
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportNavigationManager navigationManager) {
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(accountManager, "accountManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(navigationManager, "navigationManager == null");

        return new FeedbackViewerPresenter(proSportAccountHelper,
                                           accountManager,
                                           rxManager,
                                           proSportApiManager,
                                           messageManager,
                                           navigationManager);
    }

    @Named(PresenterNames.LOG_IN)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<ProSportLogInScreen> provideLogInPresenter(
        @NonNull final MessageManager messageManager,
        @NonNull final AuthManager authManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager,
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final FirebaseMessagingManager firebaseMessagingManager) {
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(authManager, "authManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");

        return new ProSportLogInPresenter(rxManager,
                                          messageManager,
                                          authManager,
                                          proSportNavigationManager,
                                          firebaseMessagingManager);
    }

    @Named(PresenterNames.RE_LOG_IN)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<ProSportReLogInScreen> provideReLogInPresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final AuthManager authManager,
        @NonNull final FirebaseMessagingManager firebaseMessagingManager) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(authManager, "authManager == null");

        return new ProSportReLogInPresenter(rxManager,
                                            messageManager,
                                            authManager,
                                            firebaseMessagingManager);
    }

    @Named(PresenterNames.SIGN_UP)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<ProSportSignUpScreen> provideSignUpPresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final AuthManager authManager,
        @NonNull final FirebaseMessagingManager firebaseMessagingManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(authManager, "authManager == null");
        Contracts.requireNonNull(firebaseMessagingManager, "firebaseMessagingManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        return new ProSportSignUpPresenter(rxManager,
                                           messageManager,
                                           authManager,
                                           firebaseMessagingManager,
                                           proSportNavigationManager);
    }
}
