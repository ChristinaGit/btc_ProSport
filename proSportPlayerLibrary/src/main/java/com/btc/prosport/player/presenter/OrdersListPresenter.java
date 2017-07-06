package com.btc.prosport.player.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.exception.AccountNotFoundException;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.ProSportDataContract;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.entity.OrderEntity;
import com.btc.prosport.api.model.entity.PageEntity;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.player.R;
import com.btc.prosport.player.screen.OrdersListScreen;

import java.util.List;

@Accessors(prefix = "_")
public final class OrdersListPresenter extends BasePresenter<OrdersListScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrdersListPresenter.class);

    public OrdersListPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");

        _rxManager = rxManager;
        _messageManager = messageManager;
        _proSportApiManager = proSportApiManager;
        _proSportAccountHelper = proSportAccountHelper;
    }

    protected final void displayOrders(@Nullable final List<Order> orders, final boolean lastPage) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayOrders(orders, lastPage);
        }
    }

    protected final void displayOrdersLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayOrdersLoading();
        }
    }

    protected final void displayOrdersLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayOrdersLoadingError();
        }
    }

    protected final void displayOrdersPage(
        @Nullable final List<Order> orders, final boolean lastPage) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayOrdersPage(orders, lastPage);
        }
    }

    protected final void displayRequireAuthorizationError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayRequireAuthorizationError();
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final OrdersListScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        resetCurrentOrdersPage();

        screen.getViewOrdersEvent().addHandler(_viewOrdersHandler);
        screen.getViewNextOrdersPageEvent().addHandler(_viewNextOrdersPageHandler);
        screen.getRefreshOrdersEvent().addHandler(_refreshOrdersHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final OrdersListScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewOrdersEvent().removeHandler(_viewOrdersHandler);
        screen.getViewNextOrdersPageEvent().removeHandler(_viewNextOrdersPageHandler);
        screen.getRefreshOrdersEvent().removeHandler(_refreshOrdersHandler);
    }

    protected void performLoadOrdersPage(final int page) {
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        // TODO: 02.03.2017 Fix double update when permissions asked
        final val rxManager = getRxManager();

        getProSportAccountHelper()
            .withRequiredAccessToken(new Func1<String, Observable<PageEntity<OrderEntity>>>() {
                @Override
                public Observable<PageEntity<OrderEntity>> call(final String token) {
                    final val orders = proSportApi.getPlayerOrders(token, page, null);
                    return rxManager.autoManage(orders);
                }
            }, true)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<PageEntity<OrderEntity>>() {
                @Override
                public void call(final PageEntity<OrderEntity> response) {
                    Contracts.requireMainThread();

                    final val orders = (List<Order>) (List<? extends Order>) response.getEntries();
                    if (page == ProSportDataContract.FIRST_PAGE_INDEX) {
                        displayOrders(orders, response.getNextPageUri() == null);
                    } else {
                        displayOrdersPage(orders, response.getNextPageUri() == null);
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to load orders. Page: " + page, error);

                    if (error instanceof AccountNotFoundException) {
                        displayRequireAuthorizationError();
                    } else {
                        getMessageManager().showInfoMessage(R.string.orders_list_load_fail);
                        displayOrdersLoadingError();
                    }
                }
            });
    }

    protected void resetCurrentOrdersPage() {
        _currentOrdersPage = ProSportDataContract.FIRST_PAGE_INDEX;
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportAccountHelper _proSportAccountHelper;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApiManager _proSportApiManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private int _currentOrdersPage;

    @NonNull
    private final NoticeEventHandler _viewNextOrdersPageHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            setCurrentOrdersPage(getCurrentOrdersPage() + 1);
            performLoadOrdersPage(getCurrentOrdersPage());
        }
    };

    @NonNull
    private final NoticeEventHandler _refreshOrdersHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            resetCurrentOrdersPage();
            performLoadOrdersPage(ProSportDataContract.FIRST_PAGE_INDEX);
        }
    };

    @NonNull
    private final NoticeEventHandler _viewOrdersHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            resetCurrentOrdersPage();

            displayOrdersLoading();

            performLoadOrdersPage(ProSportDataContract.FIRST_PAGE_INDEX);
        }
    };
}
