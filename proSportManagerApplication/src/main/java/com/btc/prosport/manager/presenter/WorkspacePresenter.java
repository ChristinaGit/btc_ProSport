package com.btc.prosport.manager.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import lombok.experimental.Accessors;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.notificationEvent.ProSportMangerNotificationEventManager;
import com.btc.prosport.core.manager.photo.PhotoManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.manager.core.manager.navigation.ManagerNavigationManager;
import com.btc.prosport.manager.screen.WorkspaceScreen;

@Accessors(prefix = "_")
public final class WorkspacePresenter extends BaseManagerNavigationPresenter<WorkspaceScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(WorkspacePresenter.class);

    public WorkspacePresenter(
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final ProSportMangerNotificationEventManager notificationEventManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        //@formatter:off
        super(Contracts.requireNonNull(accountManager, "accountManager == null"),
              Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null"),
              Contracts.requireNonNull(rxManager, "rxManager == null"),
              Contracts.requireNonNull(messageManager, "messageManager == null"),
              Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null"),
              Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null"),
              Contracts.requireNonNull(notificationEventManager, "notificationEventManager == null"),
              Contracts.requireNonNull(photoManager, "photoManager == null"),
              Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null"));
        //@formatter:on
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final WorkspaceScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getCreateSaleEvent().addHandler(_createSaleHandler);
        screen.getCreateOrderEvent().addHandler(_createOrderHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final WorkspaceScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getCreateSaleEvent().removeHandler(_createSaleHandler);
        screen.getCreateOrderEvent().removeHandler(_createOrderHandler);
    }

    @NonNull
    private final EventHandler<IdEventArgs> _createOrderHandler = new EventHandler<IdEventArgs>() {
        @Override
        public void onEvent(@NonNull final IdEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");

            getManagerNavigationManager().navigateToOrderCreator(eventArgs.getId());
        }
    };

    @NonNull
    private final NoticeEventHandler _createSaleHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            getManagerNavigationManager().navigateToSaleCreator();
        }
    };
}
