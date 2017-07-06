package com.btc.prosport.manager.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.functions.Action1;
import rx.functions.Func2;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.common.utility.tuple.Tuple2;
import com.btc.common.utility.tuple.Tuples;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.entity.IntervalEntity;
import com.btc.prosport.api.model.entity.PlaygroundTitleEntity;
import com.btc.prosport.core.eventArgs.ViewIntervalsEventArgs;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.eventArgs.CreateSimpleOrderEventArgs;
import com.btc.prosport.manager.core.manager.navigation.ManagerNavigationManager;
import com.btc.prosport.manager.screen.ScheduleScreen;

import java.util.List;

@Accessors(prefix = "_")
public final class SchedulePresenter extends BasePresenter<ScheduleScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SchedulePresenter.class);

    public SchedulePresenter(
        @NonNull final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final MessageManager messageManager) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null");
        Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");

        _rxManager = rxManager;
        _proSportApiManager = proSportApiManager;
        _proSportAccountHelper = proSportAccountHelper;
        _proSportAccountManager = proSportAccountManager;
        _managerNavigationManager = managerNavigationManager;
        _messageManager = messageManager;
    }

    protected final void displayIntervals(
        @Nullable final PlaygroundTitle playground, @Nullable final List<Interval> intervals) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayIntervals(playground, intervals);
        }
    }

    protected final void displayIntervalsLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayIntervalsLoading();
        }
    }

    protected final void displayIntervalsLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayIntervalsLoadingError();
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final ScheduleScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewIntervalsEvent().addHandler(_viewIntervalsHandler);
        screen.getRefreshIntervalsEvent().addHandler(_refreshIntervalsHandler);
        screen.getViewOrderEvent().addHandler(_viewOrderHandler);
        screen.getCreateOrderEvent().addHandler(_createOrderHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final ScheduleScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewIntervalsEvent().removeHandler(_viewIntervalsHandler);
        screen.getRefreshIntervalsEvent().removeHandler(_refreshIntervalsHandler);
        screen.getViewOrderEvent().removeHandler(_viewOrderHandler);
        screen.getCreateOrderEvent().removeHandler(_createOrderHandler);
    }

    protected void performLoadIntervals(final long playgroundId, final long startTime) {
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        final val rxManager = getRxManager();
        rxManager
            .autoManage(proSportApi
                            .getIntervals(playgroundId, ProSportApiDataUtils.formatDate(startTime))
                            .zipWith(
                                proSportApi.getPlaygroundTitle(playgroundId, null),
                                new Func2<List<IntervalEntity>, PlaygroundTitleEntity,
                                    Tuple2<List<IntervalEntity>, PlaygroundTitleEntity>>() {
                                    @Override
                                    public Tuple2<List<IntervalEntity>, PlaygroundTitleEntity> call(
                                        final List<IntervalEntity> intervalEntities,
                                        final PlaygroundTitleEntity playgroundEntity) {
                                        return Tuples.from(intervalEntities, playgroundEntity);
                                    }
                                }))
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe(new Action1<Tuple2<List<IntervalEntity>, PlaygroundTitleEntity>>() {
                @Override
                public void call(final Tuple2<List<IntervalEntity>, PlaygroundTitleEntity> arg) {
                    Contracts.requireMainThread();

                    final val intervals = (List<Interval>) (List<? extends Interval>) arg.get1();
                    final val playground = arg.get2();

                    displayIntervals(playground, intervals);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(
                        _LOG_TAG,
                        "Failed to load intervals. Playground Id: " + playgroundId,
                        error);

                    getMessageManager().showInfoMessage(R.string
                                                            .manager_schedule_interval_load_fail);
                    displayIntervalsLoadingError();
                }
            });
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ManagerNavigationManager _managerNavigationManager;

    @NonNull
    private final EventHandler<CreateSimpleOrderEventArgs> _createOrderHandler =
        new EventHandler<CreateSimpleOrderEventArgs>() {
            @Override
            public void onEvent(@NonNull final CreateSimpleOrderEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                getManagerNavigationManager().navigateToOrderCreator(
                    eventArgs.getPlaygroundId(),
                    eventArgs.getDateStart(),
                    eventArgs.getDateEnd(),
                    eventArgs.getTimeStart(),
                    eventArgs.getTimeEnd());
            }
        };

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportAccountHelper _proSportAccountHelper;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final AccountManager<ProSportAccount> _proSportAccountManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApiManager _proSportApiManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @NonNull
    private final EventHandler<ViewIntervalsEventArgs> _refreshIntervalsHandler =
        new EventHandler<ViewIntervalsEventArgs>() {
            @Override
            public void onEvent(@NonNull final ViewIntervalsEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                performLoadIntervals(eventArgs.getId(), eventArgs.getStartTime());
            }
        };

    @NonNull
    private final EventHandler<ViewIntervalsEventArgs> _viewIntervalsHandler =
        new EventHandler<ViewIntervalsEventArgs>() {
            @Override
            public void onEvent(@NonNull final ViewIntervalsEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                displayIntervalsLoading();

                performLoadIntervals(eventArgs.getId(), eventArgs.getStartTime());
            }
        };

    @NonNull
    private final EventHandler<IdEventArgs> _viewOrderHandler = new EventHandler<IdEventArgs>() {
        @Override
        public void onEvent(@NonNull final IdEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");

            getManagerNavigationManager().navigateToOrder(eventArgs.getId());
        }
    };
}
