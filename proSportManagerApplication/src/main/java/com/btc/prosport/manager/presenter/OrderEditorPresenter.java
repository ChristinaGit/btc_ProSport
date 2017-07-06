package com.btc.prosport.manager.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.exception.AccountNotFoundException;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.common.utility.UriFactoryUtils;
import com.btc.prosport.api.ProSportApi;
import com.btc.prosport.api.exception.ProSportApiException;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.User;
import com.btc.prosport.api.model.entity.OrderEntity;
import com.btc.prosport.api.model.entity.UserEntity;
import com.btc.prosport.api.request.BaseChangeOrderParams;
import com.btc.prosport.api.request.BaseCreateOrderParams;
import com.btc.prosport.api.request.ChangeOrderForUnknownUserParams;
import com.btc.prosport.api.request.ChangeOrderParams;
import com.btc.prosport.api.request.CreateOrderForUnknownUserParams;
import com.btc.prosport.api.request.CreateOrderParams;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.repeatableIntervals.item.RepeatableIntervalItem;
import com.btc.prosport.manager.core.eventArgs.ChangeOrderEventArgs;
import com.btc.prosport.manager.core.eventArgs.ChangeOrderForUnknownPlayerEventArgs;
import com.btc.prosport.manager.core.eventArgs.CreateOrderEventArgs;
import com.btc.prosport.manager.core.eventArgs.CreateOrderForUnknownPlayerEventArgs;
import com.btc.prosport.manager.core.eventArgs.UserSearchEventArgs;
import com.btc.prosport.manager.screen.OrderEditorScreen;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public final class OrderEditorPresenter extends BasePresenter<OrderEditorScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrderEditorPresenter.class);

    public OrderEditorPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApi proSportApi) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApi, "proSportApi == null");

        _rxManager = rxManager;
        _proSportAccountHelper = proSportAccountHelper;
        _messageManager = messageManager;
        _proSportApi = proSportApi;
    }

    public void displayCreatedOrder(@Nullable final Order order) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayChangedOrder(order);
        }
    }

    protected final void displayEditedOrder(@Nullable final Order order) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayOrder(order);
        }
    }

    protected final void displayPlayerSearchError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPlayerSearchError();
        }
    }

    protected final void displayPlayerSearchResult(@Nullable final User player) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPlayerSearchResult(player);
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final OrderEditorScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getSearchPlayerEvent().addHandler(_playerSearchHandler);
        screen.getCreateOrderEvent().addHandler(_createOrderHandler);
        screen.getCreateOrderForUnknownPlayerEvent().addHandler(_crateOrderForUnknownPlayerHandler);
        screen.getChangeOrderEvent().addHandler(_changeOrderHandlerHandler);
        screen.getChangeOrderForUnknownPlayerEvent().addHandler(_changeOrderForUnknownPlayerEvent);
    }

    @Override
    protected void onScreenCreate(@NonNull final OrderEditorScreen screen) {
        super.onScreenCreate(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewPlayerEvent().addHandler(_viewPlayerHandler);
        screen.getViewOrderEvent().addHandler(_viewOrderHandler);
    }

    @Override
    protected void onScreenDestroy(@NonNull final OrderEditorScreen screen) {
        super.onScreenDestroy(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewOrderEvent().removeHandler(_viewOrderHandler);
        screen.getViewPlayerEvent().removeHandler(_viewPlayerHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final OrderEditorScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getSearchPlayerEvent().removeHandler(_playerSearchHandler);
        screen.getCreateOrderEvent().removeHandler(_createOrderHandler);
        screen
            .getCreateOrderForUnknownPlayerEvent()
            .removeHandler(_crateOrderForUnknownPlayerHandler);
        screen.getChangeOrderEvent().removeHandler(_changeOrderHandlerHandler);
    }

    protected void performLoadPlayer(final long playerId) {
        final val rxManager = getRxManager();
        final val accountHelper = getProSportAccountHelper();
        final val proSportApi = getProSportApi();
        final val messageManager = getMessageManager();

        final val searchMessage = messageManager.getMessage(R.string.order_editor_player_search);
        messageManager.showProgressMessage(searchMessage, null);

        accountHelper
            .withRequiredAccessToken(new Func1<String, Observable<UserEntity>>() {
                @Override
                public Observable<UserEntity> call(final String token) {
                    return rxManager.autoManage(proSportApi.getPlayer(playerId, token));
                }
            }, true)
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity user) {
                    Contracts.requireMainThread();

                    displayPlayerSearchResult(user);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Fail to perform player search.", error);

                    messageManager.dismissProgressMessage();

                    displayPlayerSearchError();
                }
            }, new Action0() {
                @Override
                public void call() {
                    Contracts.requireMainThread();

                    messageManager.dismissProgressMessage();
                }
            });
    }

    protected void performSearchPlayer(@NonNull final String phoneNumber) {
        Contracts.requireNonNull(phoneNumber, "phoneNumber == null");

        final val rxManager = getRxManager();
        final val accountHelper = getProSportAccountHelper();
        final val proSportApi = getProSportApi();
        final val messageManager = getMessageManager();

        final val searchMessage = messageManager.getMessage(R.string.order_editor_player_search);
        messageManager.showProgressMessage(searchMessage, null);

        accountHelper
            .withRequiredAccessToken(new Func1<String, Observable<UserEntity>>() {
                @Override
                public Observable<UserEntity> call(final String token) {
                    final val phoneNumberUri =
                        UriFactoryUtils.getTelephoneUri(phoneNumber).toString();
                    return rxManager.autoManage(proSportApi.getPlayer(phoneNumberUri, token));
                }
            }, true)
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity user) {
                    Contracts.requireMainThread();

                    displayPlayerSearchResult(user);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Fail to perform player search.", error);

                    messageManager.dismissProgressMessage();

                    displayPlayerSearchError();
                }
            }, new Action0() {
                @Override
                public void call() {
                    Contracts.requireMainThread();

                    messageManager.dismissProgressMessage();
                }
            });
    }

    @NonNull
    private final Transformer<RepeatableIntervalItem, BaseChangeOrderParams.Interval>
        _changeOrderIntervalsTransformer =
        new Transformer<RepeatableIntervalItem, BaseChangeOrderParams.Interval>() {
            @Override
            public BaseChangeOrderParams.Interval transform(
                final RepeatableIntervalItem input) {
                final val dateStart = input.getDateStart();
                final val dateEnd = input.getDateEnd();
                final val timeStart = input.getTimeStart();
                final val timeEnd = input.getTimeEnd();
                final val repeatWeekDays = input.getRepeatWeekDays();

                //@formatter:off
                return new BaseChangeOrderParams.Interval(
                    input.getId(),
                    ProSportApiDataUtils.formatDate(dateStart),
                    ProSportApiDataUtils.formatTime(timeStart),
                    ProSportApiDataUtils.formatDate(dateEnd),
                    ProSportApiDataUtils.formatTime(timeEnd),
                    ProSportApiDataUtils.makeRepeatingIntervalWeekDays(repeatWeekDays));
                //@formatter:on
            }
        };

    @NonNull
    private final Transformer<RepeatableIntervalItem, BaseCreateOrderParams.Interval>
        _createOrderIntervalsTransformer =
        new Transformer<RepeatableIntervalItem, BaseCreateOrderParams.Interval>() {
            @Override
            public BaseCreateOrderParams.Interval transform(
                final RepeatableIntervalItem input) {
                final val dateStart = input.getDateStart();
                final val dateEnd = input.getDateEnd();
                final val timeStart = input.getTimeStart();
                final val timeEnd = input.getTimeEnd();
                final val repeatWeekDays = input.getRepeatWeekDays();

                //@formatter:off
                return new BaseCreateOrderParams.Interval(
                    ProSportApiDataUtils.formatDate(dateStart),
                    ProSportApiDataUtils.formatTime(timeStart),
                    ProSportApiDataUtils.formatDate(dateEnd),
                    ProSportApiDataUtils.formatTime(timeEnd),
                    ProSportApiDataUtils.makeRepeatingIntervalWeekDays(repeatWeekDays));
                //@formatter:on
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
    private final ProSportApi _proSportApi;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @NonNull
    private final EventHandler<ChangeOrderForUnknownPlayerEventArgs>
        _changeOrderForUnknownPlayerEvent =
        new EventHandler<ChangeOrderForUnknownPlayerEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeOrderForUnknownPlayerEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final val orderId = eventArgs.getId();
                final val playerPhoneNumber = eventArgs.getPlayerPhoneNumber();
                final val playerFirstName = eventArgs.getPlayerFirstName();
                final val playerLastName = eventArgs.getPlayerLastName();
                final val playgroundId = eventArgs.getPlaygroundId();
                final val orderIntervals = eventArgs.getOrderIntervals();

                if (orderIntervals != null) {
                    performChangeOrder(orderId,
                                       playgroundId,
                                       playerPhoneNumber,
                                       playerFirstName,
                                       playerLastName,
                                       orderIntervals);
                }
            }
        };

    @NonNull
    private final EventHandler<ChangeOrderEventArgs> _changeOrderHandlerHandler =
        new EventHandler<ChangeOrderEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeOrderEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final val orderId = eventArgs.getId();
                final val playerId = eventArgs.getPlayerId();
                final val playgroundId = eventArgs.getPlaygroundId();
                final val orderIntervals = eventArgs.getOrderIntervals();

                if (orderIntervals != null) {
                    performChangeOrder(orderId, playerId, playgroundId, orderIntervals);
                }
            }
        };

    @NonNull
    private final EventHandler<CreateOrderForUnknownPlayerEventArgs>
        _crateOrderForUnknownPlayerHandler =
        new EventHandler<CreateOrderForUnknownPlayerEventArgs>() {

            @Override
            public void onEvent(
                @NonNull final CreateOrderForUnknownPlayerEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final val playgroundId = eventArgs.getPlaygroundId();
                final val playerPhoneNumber = eventArgs.getPlayerPhoneNumber();
                final val playerFirstName = eventArgs.getPlayerFirstName();
                final val playerLastName = eventArgs.getPlayerLastName();
                final val orderIntervals = eventArgs.getOrderIntervals();

                if (orderIntervals != null) {
                    performCreateOrder(playgroundId,
                                       playerPhoneNumber,
                                       playerFirstName,
                                       playerLastName,
                                       orderIntervals);
                }
            }
        };

    @NonNull
    private final EventHandler<CreateOrderEventArgs> _createOrderHandler =
        new EventHandler<CreateOrderEventArgs>() {
            @Override
            public void onEvent(@NonNull final CreateOrderEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final val playerId = eventArgs.getPlayerId();
                final val playgroundId = eventArgs.getPlaygroundId();
                final val orderIntervals = eventArgs.getOrderIntervals();

                if (orderIntervals != null) {
                    performCreateOrder(playerId, playgroundId, orderIntervals);
                }
            }
        };

    @NonNull
    private final EventHandler<UserSearchEventArgs> _playerSearchHandler =
        new EventHandler<UserSearchEventArgs>() {
            @Override
            public void onEvent(@NonNull final UserSearchEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final val phoneNumber = eventArgs.getPhoneNumber();
                if (!TextUtils.isEmpty(phoneNumber)) {
                    performSearchPlayer(phoneNumber);
                }
            }
        };

    @NonNull
    private final EventHandler<IdEventArgs> _viewOrderHandler = new EventHandler<IdEventArgs>() {
        @Override
        public void onEvent(@NonNull final IdEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");

            final val rxManager = getRxManager();
            final val proSportApi = getProSportApi();
            final val messageManager = getMessageManager();

            getProSportAccountHelper()
                .withAccessToken(new Func1<String, Observable<OrderEntity>>() {
                    @Override
                    public Observable<OrderEntity> call(final String token) {
                        return rxManager.autoManage(proSportApi.getOrder(eventArgs.getId(), token));
                    }
                }, true)
                .subscribeOn(rxManager.getIOScheduler())
                .observeOn(rxManager.getUIScheduler())
                .subscribe(new Action1<OrderEntity>() {
                    @Override
                    public void call(final OrderEntity order) {
                        Contracts.requireMainThread();

                        displayEditedOrder(order);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable error) {
                        Contracts.requireMainThread();

                        Log.w(_LOG_TAG, "Failed to load order.", error);

                        messageManager.showErrorMessage(R.string.order_editor_fail_load_order);

                        displayEditedOrder(null);
                    }
                });
        }
    };

    @NonNull
    private final EventHandler<IdEventArgs> _viewPlayerHandler = new EventHandler<IdEventArgs>() {
        @Override
        public void onEvent(@NonNull final IdEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");

            performLoadPlayer(eventArgs.getId());
        }
    };

    @NonNull
    private Action1<Throwable> getFailedToChangeOrderAction(final boolean create) {
        return new Action1<Throwable>() {
            @Override
            public void call(final Throwable error) {
                Contracts.requireMainThread();

                getMessageManager().dismissProgressMessage();

                if (error instanceof AccountNotFoundException) {
                    final val messageId = create
                                          ? R.string.order_editor_fail_create_order_without_account
                                          : R.string.order_editor_fail_change_order_without_account;
                    getMessageManager().showErrorMessage(messageId);
                } else if (error instanceof ProSportApiException) {
                    final val apiException = (ProSportApiException) error;
                    final val errorResponse = apiException.getErrorResponse();
                    if (errorResponse != null) {
                        final val allErrors = errorResponse.getAllErrors();

                        final val errorMessage =
                            TextUtils.join(StringUtils.LF + StringUtils.LF, allErrors);
                        getMessageManager().showModalMessage(errorMessage);
                    }
                } else {
                    final val messageId = create
                                          ? R.string.order_editor_fail_create_order
                                          : R.string.order_editor_fail_change_order;
                    getMessageManager().showErrorMessage(messageId);
                }

                displayCreatedOrder(null);
            }
        };
    }

    private void performChangeOrder(
        final long orderId,
        final long playerId,
        final long playgroundId,
        @NonNull final List<RepeatableIntervalItem> orderIntervals) {
        Contracts.requireNonNull(orderIntervals, "orderIntervals == null");

        final val rxManager = getRxManager();
        final val proSportApi = getProSportApi();
        final val messageManager = getMessageManager();

        messageManager.showProgressMessage(R.string.order_editor_commit_changes_progress_title,
                                           R.string.order_editor_change_order_progress_message);

        getProSportAccountHelper()
            .withRequiredAccessToken(new Func1<String, Observable<OrderEntity>>() {
                @Override
                public Observable<OrderEntity> call(final String token) {
                    final val intervals =
                        new ArrayList<BaseChangeOrderParams.Interval>(orderIntervals.size());
                    CollectionUtils.collect(orderIntervals,
                                            _changeOrderIntervalsTransformer,
                                            intervals);

                    final val createOrderParams =
                        new ChangeOrderParams(playerId, playgroundId, intervals);

                    return rxManager.autoManage(proSportApi.changeOrder(orderId,
                                                                        token,
                                                                        createOrderParams));
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
            }, getFailedToChangeOrderAction(false), new Action0() {
                @Override
                public void call() {
                    Contracts.requireMainThread();

                    messageManager.dismissProgressMessage();
                }
            });
    }

    private void performChangeOrder(
        final long orderId,
        final long playgroundId,
        @NonNull final String playerPhoneNumber,
        @Nullable final String playerFirstName,
        @Nullable final String playerLastName,
        @NonNull final List<RepeatableIntervalItem> orderIntervals) {
        Contracts.requireNonNull(playerPhoneNumber, "playerPhoneNumber == null");
        Contracts.requireNonNull(orderIntervals, "orderIntervals == null");

        final val rxManager = getRxManager();
        final val proSportApi = getProSportApi();
        final val messageManager = getMessageManager();

        messageManager.showProgressMessage(R.string.order_editor_commit_changes_progress_title,
                                           R.string.order_editor_change_order_progress_message);

        getProSportAccountHelper()
            .withRequiredAccessToken(new Func1<String, Observable<OrderEntity>>() {
                @Override
                public Observable<OrderEntity> call(final String token) {
                    final val intervals =
                        new ArrayList<BaseChangeOrderParams.Interval>(orderIntervals.size());
                    CollectionUtils.collect(orderIntervals,
                                            _changeOrderIntervalsTransformer,
                                            intervals);

                    final val params = new ChangeOrderForUnknownUserParams(playerPhoneNumber,
                                                                           playerFirstName,
                                                                           playerLastName,
                                                                           playgroundId,
                                                                           intervals);

                    return rxManager.autoManage(proSportApi.changeOrder(orderId, token, params));
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
            }, getFailedToChangeOrderAction(false), new Action0() {
                @Override
                public void call() {
                    Contracts.requireMainThread();

                    messageManager.dismissProgressMessage();
                }
            });
    }

    private void performCreateOrder(
        final long playgroundId,
        @NonNull final String playerPhoneNumber,
        @Nullable final String playerFirstName,
        @Nullable final String playerLastName,
        @NonNull final List<RepeatableIntervalItem> orderIntervals) {
        Contracts.requireNonNull(playerPhoneNumber, "playerPhoneNumber == null");
        Contracts.requireNonNull(orderIntervals, "orderIntervals == null");

        final val rxManager = getRxManager();
        final val proSportApi = getProSportApi();
        final val messageManager = getMessageManager();

        messageManager.showProgressMessage(R.string.order_editor_commit_changes_progress_title,
                                           R.string.order_editor_create_order_progress_message);

        getProSportAccountHelper()
            .withRequiredAccessToken(new Func1<String, Observable<OrderEntity>>() {
                @Override
                public Observable<OrderEntity> call(final String token) {
                    final val intervals =
                        new ArrayList<BaseCreateOrderParams.Interval>(orderIntervals.size());
                    CollectionUtils.collect(orderIntervals,
                                            _createOrderIntervalsTransformer,
                                            intervals);

                    final val createOrderParams = new CreateOrderForUnknownUserParams(
                        playerPhoneNumber,
                        playerFirstName,
                        playerLastName,
                        playgroundId,
                        intervals);

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
            }, getFailedToChangeOrderAction(true), new Action0() {
                @Override
                public void call() {
                    Contracts.requireMainThread();

                    messageManager.dismissProgressMessage();
                }
            });
    }

    private void performCreateOrder(
        final long playerId,
        final long playgroundId,
        @NonNull final List<RepeatableIntervalItem> orderIntervals) {
        Contracts.requireNonNull(orderIntervals, "orderIntervals == null");

        final val rxManager = getRxManager();
        final val proSportApi = getProSportApi();
        final val messageManager = getMessageManager();

        messageManager.showProgressMessage(R.string.order_editor_commit_changes_progress_title,
                                           R.string.order_editor_create_order_progress_message);

        getProSportAccountHelper()
            .withRequiredAccessToken(new Func1<String, Observable<OrderEntity>>() {
                @Override
                public Observable<OrderEntity> call(final String token) {
                    final val intervals =
                        new ArrayList<BaseCreateOrderParams.Interval>(orderIntervals.size());
                    CollectionUtils.collect(orderIntervals,
                                            _createOrderIntervalsTransformer,
                                            intervals);

                    final val createOrderParams =
                        new CreateOrderParams(playerId, playgroundId, intervals);

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
            }, getFailedToChangeOrderAction(true), new Action0() {
                @Override
                public void call() {
                    Contracts.requireMainThread();

                    messageManager.dismissProgressMessage();
                }
            });
    }
}
