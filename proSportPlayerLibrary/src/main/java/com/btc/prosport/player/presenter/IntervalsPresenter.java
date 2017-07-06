package com.btc.prosport.player.presenter;

import android.net.Uri;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.dialer.DialerManager;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.extension.eventArgs.UriEventArgs;
import com.btc.common.extension.exception.AccountNotFoundException;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.common.utility.tuple.Tuple2;
import com.btc.common.utility.tuple.Tuple3;
import com.btc.common.utility.tuple.Tuples;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.prosport.api.model.entity.IntervalEntity;
import com.btc.prosport.api.model.entity.OrderEntity;
import com.btc.prosport.api.model.entity.PlaygroundTitleEntity;
import com.btc.prosport.api.model.entity.SportComplexTitleEntity;
import com.btc.prosport.api.request.CreateOrderForMeParams;
import com.btc.prosport.api.request.CreateOrderParams;
import com.btc.prosport.core.eventArgs.ViewIntervalsEventArgs;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.eventArgs.CreateOrderEventArgs;
import com.btc.prosport.player.core.utility.PresenterHelper;
import com.btc.prosport.player.screen.IntervalsScreen;

import java.util.Collections;
import java.util.List;

@Accessors(prefix = "_")
public final class IntervalsPresenter extends BasePresenter<IntervalsScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(IntervalsPresenter.class);

    public IntervalsPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final MessageManager messageManager,
        @NonNull final PresenterHelper presenterHelper) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(presenterHelper, "presenterHelper == null");

        _rxManager = rxManager;
        _proSportApiManager = proSportApiManager;
        _proSportAccountHelper = proSportAccountHelper;
        _proSportAccountManager = proSportAccountManager;
        _messageManager = messageManager;
        _presenterHelper = presenterHelper;
    }

    protected final void displayCreateOrderProgress() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayCreateOrderProgress();
        }
    }

    protected final void displayCreatedOrder(@Nullable final Order order) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayCreatedOrder(order);
        }
    }

    protected final void displayIntervals(
        @Nullable final SportComplexTitle sportComplex,
        @Nullable final PlaygroundTitle playground,
        @Nullable final List<Interval> intervals) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayIntervals(sportComplex, playground, intervals);
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

    protected final void performSportComplexPhoneCall(@NonNull final Uri phoneUri) {
        Contracts.requireNonNull(phoneUri, "phoneUri == null");

        getPresenterHelper().performPhoneCall(phoneUri);
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final IntervalsScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewIntervalsEvent().addHandler(_viewIntervalsHandler);
        screen.getRefreshIntervalsEvent().addHandler(_refreshIntervalsHandler);
        screen.getCreateOrderEvent().addHandler(_createOrderHandler);
        screen.getSportComplexPhoneCallEvent().addHandler(_sportComplexPhoneCallHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final IntervalsScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewIntervalsEvent().removeHandler(_viewIntervalsHandler);
        screen.getRefreshIntervalsEvent().removeHandler(_refreshIntervalsHandler);
        screen.getCreateOrderEvent().removeHandler(_createOrderHandler);
        screen.getSportComplexPhoneCallEvent().removeHandler(_sportComplexPhoneCallHandler);
    }

    protected void performCreateOrder(
        final long playgroundId,
        final long dateStart,
        final long timeStart,
        final long dateEnd,
        final long timeEnd) {
        final val rxManager = getRxManager();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        getProSportAccountHelper()
            .withRequiredAccessToken(new Func1<String, Observable<OrderEntity>>() {
                @Override
                public Observable<OrderEntity> call(final String token) {
                    final val interval =
                        new CreateOrderParams.Interval(ProSportApiDataUtils.formatDate(dateStart),
                                                       ProSportApiDataUtils.formatTime(timeStart),
                                                       ProSportApiDataUtils.formatDate(dateEnd),
                                                       ProSportApiDataUtils.formatTime(timeEnd));

                    final val createOrderParams = new CreateOrderForMeParams(playgroundId,
                                                                             Collections
                                                                                 .singletonList(
                                                                                 interval));

                    return rxManager.autoManage(proSportApi.createOrder(token, createOrderParams));
                }
            }, true)
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe(new Action1<OrderEntity>() {
                @Override
                public void call(final OrderEntity order) {
                    Contracts.requireMainThread();

                    displayCreatedOrder(order);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to create order.", error);

                    if (error instanceof AccountNotFoundException) {
                        getMessageManager().showErrorMessage(R.string
                                                                 .intervals_fail_create_order_without_account);
                    } else {
                        displayCreatedOrder(null);
                    }
                }
            });
    }

    protected void performLoadIntervals(final long playgroundId, final long startTime) {
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        final val rxManager = getRxManager();
        rxManager
            .autoManage(proSportApi
                            .getIntervals(playgroundId, ProSportApiDataUtils.formatDate(startTime))
                            .zipWith(proSportApi.getPlaygroundTitle(playgroundId, null),
                                     new Func2<List<IntervalEntity>, PlaygroundTitleEntity,
                                         Tuple2<List<IntervalEntity>, PlaygroundTitleEntity>>() {
                                         @Override
                                         public Tuple2<List<IntervalEntity>,
                                             PlaygroundTitleEntity> call(
                                             final List<IntervalEntity> intervalEntities,
                                             final PlaygroundTitleEntity playgroundEntity) {
                                             return Tuples.from(intervalEntities, playgroundEntity);
                                         }
                                     }))
            .flatMap(new Func1<Tuple2<List<IntervalEntity>, PlaygroundTitleEntity>,
                Observable<Tuple3<List<IntervalEntity>, PlaygroundTitleEntity,
                    SportComplexTitleEntity>>>() {
                @Override
                public Observable<Tuple3<List<IntervalEntity>, PlaygroundTitleEntity,
                    SportComplexTitleEntity>> call(
                    final Tuple2<List<IntervalEntity>, PlaygroundTitleEntity> arg) {
                    final val playground = arg.get2();
                    if (playground != null) {
                        return proSportApi
                            .getSportComplexTitle(playground.getSportComplexId(), null)
                            .map(new Func1<SportComplexTitleEntity, Tuple3<List<IntervalEntity>,
                                PlaygroundTitleEntity, SportComplexTitleEntity>>() {
                                @Override
                                public Tuple3<List<IntervalEntity>, PlaygroundTitleEntity,
                                    SportComplexTitleEntity> call(
                                    final SportComplexTitleEntity sportComplex) {
                                    return Tuples.from(arg.get1(), arg.get2(), sportComplex);
                                }
                            });
                    } else {
                        return Observable.just(Tuples.from(arg.get1(),
                                                           arg.get2(),
                                                           (SportComplexTitleEntity) null));
                    }
                }
            })
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe(new Action1<Tuple3<List<IntervalEntity>, PlaygroundTitleEntity,
                SportComplexTitleEntity>>() {
                @Override
                public void call(
                    final Tuple3<List<IntervalEntity>, PlaygroundTitleEntity,
                        SportComplexTitleEntity> arg) {
                    Contracts.requireMainThread();

                    final val intervals = (List<Interval>) (List<? extends Interval>) arg.get1();
                    final val playground = arg.get2();
                    final val sportComplex = arg.get3();

                    displayIntervals(sportComplex, playground, intervals);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG,
                          "Failed to load intervals. Playground Id: " + playgroundId,
                          error);

                    getMessageManager().showInfoMessage(R.string.intervals_load_fail);
                    displayIntervalsLoadingError();
                }
            });
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final PresenterHelper _presenterHelper;

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
    private final EventHandler<CreateOrderEventArgs> _createOrderHandler =
        new EventHandler<CreateOrderEventArgs>() {
            @Override
            public void onEvent(@NonNull final CreateOrderEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                displayCreateOrderProgress();

                performCreateOrder(eventArgs.getPlaygroundId(),
                                   eventArgs.getDateStart(),
                                   eventArgs.getTimeStart(),
                                   eventArgs.getDateEnd(),
                                   eventArgs.getTimeEnd());
            }
        };

    @NonNull
    private final EventHandler<UriEventArgs> _sportComplexPhoneCallHandler =
        new EventHandler<UriEventArgs>() {
            @Override
            public void onEvent(@NonNull final UriEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final val phoneUri = eventArgs.getUri();
                if (phoneUri != null) {
                    performSportComplexPhoneCall(phoneUri);
                }
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
}
