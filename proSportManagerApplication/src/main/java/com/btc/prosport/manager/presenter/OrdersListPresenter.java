package com.btc.prosport.manager.presenter;

import android.net.Uri;
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
import com.btc.common.control.manager.dialer.DialerManager;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.message.UserActionReaction;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.eventArgs.UriEventArgs;
import com.btc.common.extension.exception.InsufficientPermissionException;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.ProSportDataContract;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.entity.OrderEntity;
import com.btc.prosport.api.model.entity.PageEntity;
import com.btc.prosport.api.model.utility.OrderState;
import com.btc.prosport.api.request.ChangeOrderStateParams;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.OrdersSortOrder;
import com.btc.prosport.manager.core.eventArgs.ChangeOrderStateEventArgs;
import com.btc.prosport.manager.core.eventArgs.OrdersParamsEventArgs;
import com.btc.prosport.manager.core.manager.navigation.ManagerNavigationManager;
import com.btc.prosport.manager.screen.OrdersListScreen;
import com.btc.prosport.manager.screen.activity.workspace.OrdersFilterParams;

import java.util.List;

@Accessors(prefix = "_")
public final class OrdersListPresenter extends BasePresenter<OrdersListScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrdersListPresenter.class);

    public OrdersListPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager,
        @NonNull final DialerManager dialerManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");
        Contracts.requireNonNull(dialerManager, "dialerManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");

        _rxManager = rxManager;
        _proSportApiManager = proSportApiManager;
        _messageManager = messageManager;
        _managerNavigationManager = managerNavigationManager;
        _proSportNavigationManager = proSportNavigationManager;
        _dialerManager = dialerManager;
        _proSportAccountHelper = proSportAccountHelper;
    }

    protected void displayLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayOrdersLoading();
        }
    }

    protected void displayLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayOrdersLoadingError();
        }
    }

    protected void displayOrders(@Nullable final List<Order> orders, final boolean lastPage) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayOrders(orders, lastPage);
        }
    }

    protected void displayOrdersPage(
        @Nullable final List<Order> orders, final boolean lastPage) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayOrdersPage(orders, lastPage);
        }
    }

    @Override
    protected void onScreenAppear(@NonNull final OrdersListScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        resetCurrentOrdersPage();

        screen.getCallPlayerEvent().addHandler(_callActionHandler);
        screen.getViewOrdersEvent().addHandler(_viewOrdersHandler);
        screen.getRefreshOrdersEvent().addHandler(_refreshOrdersHandler);
        screen.getDetailsOrderEvent().addHandler(_detailsOrderHandler);
        screen.getViewNextOrdersPageEvent().addHandler(_viewNextOrdersPageHandler);
        screen.getChangeStateEvent().addHandler(_changeStateHandler);
    }

    @Override
    protected void onScreenDisappear(@NonNull final OrdersListScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getCallPlayerEvent().removeHandler(_callActionHandler);
        screen.getViewOrdersEvent().removeHandler(_viewOrdersHandler);
        screen.getRefreshOrdersEvent().removeHandler(_refreshOrdersHandler);
        screen.getDetailsOrderEvent().removeHandler(_detailsOrderHandler);
        screen.getViewNextOrdersPageEvent().removeHandler(_viewNextOrdersPageHandler);
        screen.getChangeStateEvent().removeHandler(_changeStateHandler);
    }

    protected void performLoadOrdersPage(
        final int pageIndex,
        @Nullable final OrdersFilterParams ordersFilterParams,
        @Nullable final OrdersSortOrder ordersSortOrder) {

        final Integer stateCode;
        final Long sportComplexId;
        final Long playgroundId;
        final String searchQuery;
        if (ordersFilterParams != null) {
            val stateByOrderFilter = getStateByOrderFilter(ordersFilterParams);
            if (stateByOrderFilter != null) {
                stateCode = stateByOrderFilter.getCode();
            } else {
                stateCode = null;
            }
            sportComplexId = ordersFilterParams.getSportComplexId();
            playgroundId = ordersFilterParams.getPlaygroundId();
            searchQuery = ordersFilterParams.getSearchQuery();
        } else {
            stateCode = null;
            sportComplexId = null;
            playgroundId = null;
            searchQuery = null;
        }

        final val ordering = getOrdersOrdering(ordersSortOrder);

        final val rxManager = getRxManager();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        rxManager
            .autoManage(getProSportAccountHelper().withAccessToken(new Func1<String,
                Observable<PageEntity<OrderEntity>>>() {
                @Override
                public Observable<PageEntity<OrderEntity>> call(final String token) {
                    return proSportApi.getManagerOrders(token,
                                                        pageIndex,
                                                        stateCode,
                                                        sportComplexId,
                                                        playgroundId,
                                                        searchQuery,
                                                        ordering);
                }
            }, false))
            .observeOn(rxManager.getUIScheduler())
            .subscribeOn(rxManager.getIOScheduler())
            .subscribe(new Action1<PageEntity<OrderEntity>>() {
                @Override
                public void call(
                    final PageEntity<OrderEntity> page) {
                    Contracts.requireMainThread();

                    if (pageIndex == ProSportDataContract.FIRST_PAGE_INDEX) {
                        displayOrders((List<Order>) (List<? extends Order>) page.getEntries(),
                                      page.getNextPageUri() == null);
                    } else {
                        displayOrdersPage((List<Order>) (List<? extends Order>) page.getEntries(),
                                          page.getNextPageUri() == null);
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    getMessageManager().showInfoMessage(R.string.loading_fail);
                    displayLoadingError();
                }
            });
    }

    protected void resetCurrentOrdersPage() {
        _currentOrdersPage = ProSportDataContract.FIRST_PAGE_INDEX;
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final DialerManager _dialerManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ManagerNavigationManager _managerNavigationManager;

    @NonNull
    private final EventHandler<IdEventArgs> _detailsOrderHandler = new EventHandler<IdEventArgs>() {
        @Override
        public void onEvent(@NonNull final IdEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");

            getManagerNavigationManager().navigateToOrder(eventArgs.getId());
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
    private final ProSportApiManager _proSportApiManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportNavigationManager _proSportNavigationManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @NonNull
    private final EventHandler<UriEventArgs> _callActionHandler = new EventHandler<UriEventArgs>() {
        @Override
        public void onEvent(@NonNull final UriEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");

            final val phoneUri = eventArgs.getUri();
            if (phoneUri != null) {
                performPlayerCall(phoneUri);
            }
        }
    };

    @NonNull
    private final EventHandler<ChangeOrderStateEventArgs> _changeStateHandler =
        new EventHandler<ChangeOrderStateEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeOrderStateEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                val newState = eventArgs.getNewState();
                if (newState != null) {
                    performChangeState(eventArgs.getOrderId(), eventArgs.getOldState(), newState);
                }
            }
        };

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private int _currentOrdersPage;

    @NonNull
    private final EventHandler<OrdersParamsEventArgs> _refreshOrdersHandler =
        new EventHandler<OrdersParamsEventArgs>() {
            @Override
            public void onEvent(@NonNull final OrdersParamsEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                resetCurrentOrdersPage();

                performLoadOrdersPage(ProSportDataContract.FIRST_PAGE_INDEX,
                                      eventArgs.getOrdersFilterParams(),
                                      eventArgs.getOrdersSortOrder());
            }
        };

    @NonNull
    private final EventHandler<OrdersParamsEventArgs> _viewOrdersHandler =
        new EventHandler<OrdersParamsEventArgs>() {
            @Override
            public void onEvent(@NonNull final OrdersParamsEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                resetCurrentOrdersPage();

                displayLoading();

                performLoadOrdersPage(ProSportDataContract.FIRST_PAGE_INDEX,
                                      eventArgs.getOrdersFilterParams(),
                                      eventArgs.getOrdersSortOrder());
            }
        };

    @NonNull
    private final EventHandler<OrdersParamsEventArgs> _viewNextOrdersPageHandler =
        new EventHandler<OrdersParamsEventArgs>() {
            @Override
            public void onEvent(@NonNull final OrdersParamsEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                setCurrentOrdersPage(getCurrentOrdersPage() + 1);

                performLoadOrdersPage(getCurrentOrdersPage(),
                                      eventArgs.getOrdersFilterParams(),
                                      eventArgs.getOrdersSortOrder());
            }
        };

    private void disableLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.disableOrdersLoading();
        }
    }

    private void displayOrderLoading(final long orderId) {
        val screen = getScreen();

        if (screen != null) {
            screen.displayOrderLoading(orderId);
        }
    }

    @NonNull
    private Observable<OrderEntity> getChangeStateObservable(
        final long orderId, @NonNull final OrderState newState) {
        Contracts.requireNonNull(newState, "newState == null");
        Contracts.requireMainThread();

        displayOrderLoading(orderId);

        final val rxManager = getRxManager();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();
        final val accountHelper = getProSportAccountHelper();

        final val changeOrderStateParams = new ChangeOrderStateParams(newState.getCode());

        return accountHelper.withRequiredAccessToken(new Func1<String, Observable<OrderEntity>>() {
                @Override
                public Observable<OrderEntity> call(final String token) {
                    return rxManager.autoManage(proSportApi.changeOrderState(orderId,
                                                                             token,
                                                                             changeOrderStateParams));
                }
        }, true)
            .observeOn(getRxManager().getUIScheduler())
            .subscribeOn(getRxManager().getIOScheduler()).doOnNext(new Action1<OrderEntity>() {
                @Override
                public void call(final OrderEntity orderEntity) {
                    Contracts.requireMainThread();

                    updateOrder(orderEntity);

                    hideOrderLoading(orderId);
                }
            });
    }

    @Nullable
    private String getOrdersOrdering(@Nullable final OrdersSortOrder ordersSortOrder) {
        final String ordering;

        if (ordersSortOrder != null) {
            switch (ordersSortOrder) {
                // FIXME: 11.05.2017 Use booking date instead of create date.
                case DATE_ASCENDING:
                    ordering =
                        ProSportApiDataUtils.makeQueryOrderParam(OrderEntity.FIELD_CREATE_DATE,
                                                                 false);
                    break;
                case DATE_DESCENDING:
                    ordering =
                        ProSportApiDataUtils.makeQueryOrderParam(OrderEntity.FIELD_CREATE_DATE,
                                                                 true);
                    break;
                case PRICE_ASCENDING:
                    ordering =
                        ProSportApiDataUtils.makeQueryOrderParam(OrderEntity.FIELD_PRICE, false);
                    break;
                case PRICE_DESCENDING:
                    ordering =
                        ProSportApiDataUtils.makeQueryOrderParam(OrderEntity.FIELD_PRICE, true);
                    break;
                default:
                    ordering = null;
            }
        } else {
            ordering = null;
        }

        return ordering;
    }

    @Nullable
    private OrderState getStateByOrderFilter(@NonNull final OrdersFilterParams ordersFilter) {
        final OrderState state;

        val ordersState = ordersFilter.getOrderStatusFilter();
        switch (ordersState) {
            case CANCELED:
                state = OrderState.CANCELED;
                break;
            case CONFIRMED:
                state = OrderState.CONFIRMED;
                break;
            case NOT_CONFIRMED:
                state = OrderState.NOT_CONFIRMED;
                break;
            case ALL:
                state = null;
                break;
            default:
                state = null;
                break;
        }

        return state;
    }

    private void hideOrderLoading(final long orderId) {
        val screen = getScreen();

        if (screen != null) {
            screen.hideOrderLoading(orderId);
        }
    }

    private void performChangeState(
        final long orderId,
        @NonNull final OrderState oldState, @NonNull final OrderState newState) {
        Contracts.requireNonNull(oldState, "oldState == null");
        Contracts.requireNonNull(newState, "newState == null");

        final val rxManager = getRxManager();

        getChangeStateObservable(orderId, newState)
            .flatMap(new Func1<OrderEntity, Observable<UserActionReaction>>() {
                @Override
                public Observable<UserActionReaction> call(final OrderEntity page) {
                    return getMessageManager().showActionMessage(R.string.order_state_changed,
                                                                 android.R.string.cancel);
                }
            })
            .flatMap(new Func1<UserActionReaction, Observable<OrderEntity>>() {
                @Override
                public Observable<OrderEntity> call(final UserActionReaction response) {
                    if (response == UserActionReaction.PERFORM) {
                        return getChangeStateObservable(orderId, oldState);
                    } else {
                        return Observable.empty();
                    }
                }
            })
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<OrderEntity>() {
                @Override
                public void call(final OrderEntity orderEntity) {
                    Contracts.requireMainThread();

                    getMessageManager().showInfoMessage(R.string.order_state_changed);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to change state", error);

                    hideOrderLoading(orderId);

                    getMessageManager().showErrorMessage(R.string.request_fail);
                }
            });
    }

    private void performPlayerCall(@NonNull final Uri phoneUri) {
        Contracts.requireNonNull(phoneUri, "phoneUri == null");

        getDialerManager()
            .performPhoneCallWithPermissions(phoneUri, true)
            .observeOn(getRxManager().getUIScheduler())
            .subscribe(new Action1<Boolean>() {
                @Override
                public void call(final Boolean success) {
                    Contracts.requireMainThread();

                    if (!success) {
                        getMessageManager().showInfoMessage(R.string
                                                                .message_error_dialer_not_found);
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    if (error instanceof InsufficientPermissionException) {
                        getMessageManager()
                            .showActionMessage(
                                R.string
                                    .message_error_fail_perform_phone_call_insufficient_permission,
                                R.string.message_manager_fix_error)
                            .subscribe(new Action1<UserActionReaction>() {
                                @Override
                                public void call(final UserActionReaction userActionReaction) {
                                    if (userActionReaction == UserActionReaction.PERFORM) {
                                        final val navigationManager =
                                            getProSportNavigationManager();
                                        final boolean settingsOpened =
                                            navigationManager.navigateToApplicationSettings();
                                        if (!settingsOpened) {
                                            getMessageManager().showErrorMessage(R.string
                                                                                     .message_error_settings_not_found_allow_permission_manual);
                                        }
                                    }
                                }
                            });
                    }

                    Log.w(_LOG_TAG, "Failed to perform playground phone call.", error);
                }
            });
    }

    private void updateOrder(@Nullable final Order order) {
        val screen = getScreen();
        if (screen != null) {
            screen.displayChangedOrder(order);
        }
    }
}
