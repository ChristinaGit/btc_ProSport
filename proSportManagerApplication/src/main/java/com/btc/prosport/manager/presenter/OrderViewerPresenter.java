package com.btc.prosport.manager.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.common.utility.tuple.Tuple3;
import com.btc.common.utility.tuple.Tuples;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.entity.IntervalEntity;
import com.btc.prosport.api.model.entity.OrderEntity;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.manager.navigation.ManagerNavigationManager;
import com.btc.prosport.manager.screen.OrderViewerScreen;

import java.util.List;

@Accessors(prefix = "_")
public final class OrderViewerPresenter extends BasePresenter<OrderViewerScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrderViewerPresenter.class);

    public OrderViewerPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null");
        Contracts.requireNonNull(accountManager, "accountManager== null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");

        _rxManager = rxManager;
        _proSportApiManager = proSportApiManager;
        _messageManager = messageManager;
        _managerNavigationManager = managerNavigationManager;
        _accountManager = accountManager;
        _proSportAccountHelper = proSportAccountHelper;
    }

    @Override
    protected void onScreenAppear(
        @NonNull final OrderViewerScreen screen) {
        super.onScreenAppear(screen);

        screen.getReservationEditEvent().addHandler(_reservationEditHandler);
        screen.getViewOrderEvent().addHandler(_viewOrderHandler);
    }

    @Override
    protected void onScreenDisappear(
        @NonNull final OrderViewerScreen screen) {
        super.onScreenDisappear(screen);

        screen.getReservationEditEvent().removeHandler(_reservationEditHandler);
        screen.getViewOrderEvent().removeHandler(_viewOrderHandler);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final AccountManager<ProSportAccount> _accountManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ManagerNavigationManager _managerNavigationManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportAccountHelper _proSportAccountHelper;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApiManager _proSportApiManager;

    @NonNull
    private final EventHandler<IdEventArgs> _reservationEditHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");
                //TODO: 02.03.2017
            }
        };

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @NonNull
    private final EventHandler<IdEventArgs> _viewOrderHandler = new EventHandler<IdEventArgs>() {
        @Override
        public void onEvent(@NonNull final IdEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");

            displayLoading();

            performLoadOrder(eventArgs.getId());
        }
    };

    private void displayLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayLoading();
        }
    }

    private void displayLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayLoadingError();
        }
    }

    private void displayOrder(
        @Nullable final Order order,
        @Nullable final List<Interval> intervals,
        final long startDate) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayOrder(order, intervals, startDate);
        }
    }

    private void performLoadOrder(final long orderId) {
        final val apiManager = getProSportApiManager();
        final val proSportApi = apiManager.getProSportApi();
        final val accountHelper = getProSportAccountHelper();

        final val rxManager = getRxManager();

        accountHelper
            .withAccessToken(new Func1<String, Observable<Tuple3<OrderEntity,
                List<IntervalEntity>, Long>>>() {
                @Override
                public Observable<Tuple3<OrderEntity, List<IntervalEntity>, Long>> call(
                    final String token) {
                    final val order = proSportApi
                        .getOrder(orderId, token)
                        .flatMap(new Func1<OrderEntity, Observable<Tuple3<OrderEntity,
                            List<IntervalEntity>, Long>>>() {
                            @Override
                            public Observable<Tuple3<OrderEntity, List<IntervalEntity>, Long>> call(
                                final OrderEntity order) {
                                final long playgroundId = order.getPlayground().getId();

                                final long startDate;

                                final val intervals = order.getIntervals();
                                if (intervals != null && !intervals.isEmpty()) {
                                    final val firstInterval = intervals.get(0);
                                    final long date = ProSportApiDataUtils
                                        .parseDate(firstInterval.getDateStart())
                                        .getTime();

                                    startDate = date - DateUtils.DAY_IN_MILLIS * 3;
                                } else {
                                    startDate = System.currentTimeMillis();
                                }

                                return proSportApi
                                    .getIntervals(playgroundId,
                                                  ProSportApiDataUtils.formatDate(startDate))
                                    .map(new Func1<List<IntervalEntity>, Tuple3<OrderEntity,
                                        List<IntervalEntity>, Long>>() {
                                        @Override
                                        public Tuple3<OrderEntity, List<IntervalEntity>, Long> call(
                                            final List<IntervalEntity> intervals) {
                                            return Tuples.from(order, intervals, startDate);
                                        }
                                    });
                            }
                        });
                    return rxManager.autoManage(order);
                }
            }, false)
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe(new Action1<Tuple3<OrderEntity, List<IntervalEntity>, Long>>() {
                @Override
                public void call(
                    @NonNull final Tuple3<OrderEntity, List<IntervalEntity>, Long> arg) {
                    Contracts.requireMainThread();

                    displayOrder(
                        arg.get1(),
                        (List<Interval>) (List<? extends Interval>) arg.get2(),
                        arg.get3());
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to load order.", error);

                    getMessageManager().showInfoMessage(R.string.loading_fail);
                    displayLoadingError();
                }
            });
    }
}
