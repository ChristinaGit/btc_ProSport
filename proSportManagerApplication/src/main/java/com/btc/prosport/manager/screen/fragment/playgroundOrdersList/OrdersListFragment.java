package com.btc.prosport.manager.screen.fragment.playgroundOrdersList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.eventArgs.UriEventArgs;
import com.btc.common.extension.pagination.Paginate;
import com.btc.common.extension.pagination.PaginateInterface;
import com.btc.common.extension.pagination.RecyclerPaginate;
import com.btc.common.extension.view.recyclerView.decoration.SpacingItemDecoration;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.SwipeRefreshUtils;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.utility.OrderState;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.OrderStatusFilter;
import com.btc.prosport.manager.core.OrdersSortOrder;
import com.btc.prosport.manager.core.adapter.orderStatusAdapter.OrderStatesAdapter;
import com.btc.prosport.manager.core.adapter.ordersList.OrdersListAdapter;
import com.btc.prosport.manager.core.eventArgs.ChangeOrderStateEventArgs;
import com.btc.prosport.manager.core.eventArgs.OrdersParamsEventArgs;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.OrdersListScreen;
import com.btc.prosport.manager.screen.activity.orderEditor.OrderEditorIntent;
import com.btc.prosport.manager.screen.activity.workspace.OrdersFilterParams;
import com.btc.prosport.manager.screen.fragment.BaseWorkspaceFragment;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public class OrdersListFragment extends BaseWorkspaceFragment
    implements OrdersListScreen, PaginateInterface, SwipeRefreshLayout.OnRefreshListener {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrdersListFragment.class);

    @Override
    public final void disableOrdersLoading() {
        final val loadingViewDelegate = getOrdersListLoadingDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent();
        }
    }

    @Override
    public void displayChangedOrder(@Nullable final Order order) {
        val ordersListAdapter = getOrdersListAdapter();
        if (ordersListAdapter != null && order != null) {
            final int orderIndex = ordersListAdapter.getOrderIndex(order.getId());
            if (orderIndex != -1) {
                val ordersFilterParams = getOrdersFilterParams();
                if (ordersFilterParams == null ||
                    ordersFilterParams.getOrderStatusFilter() == OrderStatusFilter.ALL) {
                    ordersListAdapter.setItem(orderIndex, order);
                } else {
                    ordersListAdapter.removeItem(orderIndex);
                }
            } else {
                riseViewOrdersEvent();
            }
        }
    }

    @Override
    public void displayOrderLoading(final long orderId) {
        val ordersListAdapter = getOrdersListAdapter();

        if (ordersListAdapter != null) {
            ordersListAdapter.showLoading(orderId);
        }
    }

    @Override
    public void displayOrders(@Nullable final List<Order> orders, final boolean lastPage) {
        setPageLoading(false);
        setAllItemsLoaded(lastPage);

        if (_ordersListRefreshView != null) {
            _ordersListRefreshView.setRefreshing(false);
        }

        final val ordersListAdapter = getOrdersListAdapter();
        if (ordersListAdapter != null) {
            if (orders != null) {
                ordersListAdapter.setItems(orders);
            } else {
                ordersListAdapter.removeItems();
            }
        }

        final val loadingViewDelegate = getOrdersListLoadingDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(orders != null && !orders.isEmpty());
        }

        final val ordersPaginate = getOrdersPaginate();
        if (ordersPaginate != null) {
            ordersPaginate.checkPageLoading();
        }
    }

    @Override
    public void displayOrdersLoading() {
        final val loadingViewDelegate = getOrdersListLoadingDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public void displayOrdersLoadingError() {
        setPageLoading(false);
        setAllItemsLoaded(true);

        if (_ordersListRefreshView != null) {
            _ordersListRefreshView.setRefreshing(false);
        }

        final val loadingViewDelegate = getOrdersListLoadingDelegate();
        if (loadingViewDelegate != null) {
            final val ordersListAdapter = getOrdersListAdapter();
            if (ordersListAdapter == null || ordersListAdapter.getItems().isEmpty()) {
                loadingViewDelegate.showError();
            } else {
                loadingViewDelegate.showContent();
            }
        }
    }

    @Override
    public void displayOrdersPage(@Nullable final List<Order> orders, final boolean lastPage) {
        setPageLoading(false);
        setAllItemsLoaded(lastPage);

        final val ordersListAdapter = getOrdersListAdapter();
        if (ordersListAdapter != null && orders != null) {
            ordersListAdapter.addItems(orders);
        }

        final val ordersPaginate = getOrdersPaginate();
        if (ordersPaginate != null) {
            ordersPaginate.checkPageLoading();
        }
    }

    @NonNull
    @Override
    public final Event<UriEventArgs> getCallPlayerEvent() {
        return _callPlayerEvent;
    }

    @NonNull
    @Override
    public final Event<ChangeOrderStateEventArgs> getChangeStateEvent() {
        return _changeStatusEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getDetailsOrderEvent() {
        return _detailsOrderEvent;
    }

    @NonNull
    @Override
    public final Event<OrdersParamsEventArgs> getRefreshOrdersEvent() {
        return _refreshOrdersEvent;
    }

    @NonNull
    @Override
    public final Event<OrdersParamsEventArgs> getViewNextOrdersPageEvent() {
        return _viewNextOrdersPageEvent;
    }

    @NonNull
    @Override
    public final Event<OrdersParamsEventArgs> getViewOrdersEvent() {
        return _viewOrdersEvent;
    }

    @Override
    public void hideOrderLoading(final long orderId) {
        val ordersListAdapter = getOrdersListAdapter();

        if (ordersListAdapter != null) {
            ordersListAdapter.hideLoading(orderId);
        }
    }

    public final void setPageLoading(final boolean pageLoading) {
        if (_pageLoading != pageLoading) {
            _pageLoading = pageLoading;

            onPageLoadingStateChanged();
        }
    }

    @CallSuper
    @Override
    public void bindViews(@NonNull final View source) {
        super.bindViews(Contracts.requireNonNull(source, "source == null"));

        _ordersListView = (RecyclerView) source.findViewById(R.id.orders_list);
        _ordersListRefreshView = (SwipeRefreshLayout) source.findViewById(R.id.orders_list_refresh);
        _ordersLoadingView = (ProgressBar) source.findViewById(R.id.loading);
        _ordersErrorView = (TextView) source.findViewById(R.id.loading_error);
        _ordersNoContentView = (TextView) source.findViewById(R.id.no_content);
    }

    @CallSuper
    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view = inflater.inflate(R.layout.fragment_orders_list, container, false);

        bindViews(view);

        initializeOrdersListView();

        final val ordersListAdapter = getOrdersListAdapter();
        if (ordersListAdapter != null) {
            ordersListAdapter.getCallPlayerEvent().addHandler(_callActionHandler);
            ordersListAdapter.getCancelOrderEvent().addHandler(_cancelActionHandler);
            ordersListAdapter.getConfirmOrderEvent().addHandler(_confirmActionHandler);
            ordersListAdapter.getDetailsOrderEvent().addHandler(_detailsOrderHandler);
            ordersListAdapter.getChangeStatusEvent().addHandler(_statusChangeHandler);
            ordersListAdapter.getEditOrderEvent().addHandler(new EventHandler<IdEventArgs>() {
                @Override
                public void onEvent(@NonNull final IdEventArgs eventArgs) {
                    Contracts.requireNonNull(eventArgs, "eventArgs == null");

                    startActivity(OrderEditorIntent.Edit.build(getContext(), eventArgs.getId()));
                }
            });
        }

        if (_ordersListRefreshView != null) {
            SwipeRefreshUtils.setupPrimaryColors(_ordersListRefreshView);
            _ordersListRefreshView.setOnRefreshListener(this);
        }

        if (_ordersListView != null) {
            _ordersPaginate = RecyclerPaginate
                .builder(_ordersListView, this)
                .setLoadingTriggerThreshold(2)
                .build();

            _ordersPaginate.bind();
        }

        _ordersListLoadingDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_ordersListView)
            .setLoadingView(_ordersLoadingView)
            .setNoContentView(_ordersNoContentView)
            .setErrorView(_ordersErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long,
                                                                   FadeVisibilityHandler
                                                                       .NO_ANIMATION))
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();

        return view;
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        final val ordersPaginate = getOrdersPaginate();
        if (ordersPaginate != null) {
            ordersPaginate.unbind();
        }

        final val ordersListAdapter = getOrdersListAdapter();
        if (ordersListAdapter != null) {
            ordersListAdapter.getCallPlayerEvent().removeHandler(_callActionHandler);
            ordersListAdapter.getCancelOrderEvent().removeHandler(_cancelActionHandler);
            ordersListAdapter.getConfirmOrderEvent().removeHandler(_confirmActionHandler);
            ordersListAdapter.getDetailsOrderEvent().removeHandler(_detailsOrderHandler);
            ordersListAdapter.getChangeStatusEvent().removeHandler(_statusChangeHandler);
        }

        unbindViews();
    }

    @CallSuper
    @Override
    protected void onReleaseInjectedMembers() {
        super.onReleaseInjectedMembers();

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.unbindScreen();
        }
    }

    @CallSuper
    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();

        if (isPageActive()) {
            riseViewOrdersEvent();
        }
    }

    @CallSuper
    @Override
    public void onLoadNextPage() {
        setPageLoading(true);

        _viewNextOrdersPageEvent.rise(new OrdersParamsEventArgs(getOrdersFilterParams(),
                                                                getOrdersSortOrder()));
    }

    @CallSuper
    @Override
    public void onRefresh() {
        _refreshOrdersEvent.rise(new OrdersParamsEventArgs(getOrdersFilterParams(),
                                                           getOrdersSortOrder()));
    }

    public void setOrdersFilterParams(@Nullable final OrdersFilterParams ordersFilterParams) {
        if (!Objects.equals(ordersFilterParams, _ordersFilterParams)) {
            _ordersFilterParams = ordersFilterParams;

            onOrderFilterParamsChanged();
        }
    }

    public void setSortOrder(@Nullable final OrdersSortOrder ordersSortOrder) {
        if (ordersSortOrder != _ordersSortOrder) {
            _ordersSortOrder = ordersSortOrder;

            onOrderSortChanged();
        }
    }

    @CallSuper
    @Override
    protected void onEnterPage() {
        super.onEnterPage();

        if (isResumed()) {
            riseViewOrdersEvent();
        }
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getManagerSubscreenComponent().inject(this);

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    protected void onOrderFilterParamsChanged() {
        if (isResumed() && isPageActive()) {
            riseViewOrdersEvent();
        }
    }

    @CallSuper
    protected void onOrderSortChanged() {
        if (isResumed() && isPageActive()) {
            riseViewOrdersEvent();
        }
    }

    @Named(PresenterNames.ORDER_LIST)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<OrdersListScreen> _presenter;

    @NonNull
    private final ManagedEvent<UriEventArgs> _callPlayerEvent = Events.createEvent();

    @NonNull
    private final EventHandler<UriEventArgs> _callActionHandler = new EventHandler<UriEventArgs>() {
        @Override
        public void onEvent(@NonNull final UriEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");

            _callPlayerEvent.rise(eventArgs);
        }
    };

    @NonNull
    private final ManagedEvent<ChangeOrderStateEventArgs> _changeStatusEvent = Events.createEvent();

    @NonNull
    private final EventHandler<ChangeOrderStateEventArgs> _cancelActionHandler =
        new EventHandler<ChangeOrderStateEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeOrderStateEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");
                _changeStatusEvent.rise(new ChangeOrderStateEventArgs(eventArgs.getOrderId(),
                                                                      eventArgs.getOldState(),
                                                                      eventArgs.getNewState()));
            }
        };

    @NonNull
    private final EventHandler<ChangeOrderStateEventArgs> _confirmActionHandler =
        new EventHandler<ChangeOrderStateEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeOrderStateEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");
                _changeStatusEvent.rise(new ChangeOrderStateEventArgs(eventArgs.getOrderId(),
                                                                      eventArgs.getOldState(),
                                                                      eventArgs.getNewState()));
            }
        };

    @NonNull
    private final ManagedEvent<IdEventArgs> _detailsOrderEvent = Events.createEvent();

    @NonNull
    private final EventHandler<IdEventArgs> _detailsOrderHandler = new EventHandler<IdEventArgs>() {
        @Override
        public void onEvent(@NonNull final IdEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");
            _detailsOrderEvent.rise(eventArgs);
        }
    };

    @NonNull
    private final ManagedEvent<OrdersParamsEventArgs> _refreshOrdersEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<OrdersParamsEventArgs> _viewNextOrdersPageEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<OrdersParamsEventArgs> _viewOrdersEvent = Events.createEvent();

    @Getter(onMethod = @__(@Override))
    @Setter
    private boolean _allItemsLoaded;

    @Nullable
    private OrderStatesAdapter _orderStatesAdapter;

    @NonNull
    private final EventHandler<ChangeOrderStateEventArgs> _statusChangeHandler =
        new EventHandler<ChangeOrderStateEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeOrderStateEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                showDialog(eventArgs.getOrderId(), eventArgs.getOldState());
            }
        };

    @Nullable
    private TextView _ordersErrorView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private OrdersFilterParams _ordersFilterParams;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private OrdersListAdapter _ordersListAdapter;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _ordersListLoadingDelegate;

    @Nullable
    private SwipeRefreshLayout _ordersListRefreshView;

    @Nullable
    private RecyclerView _ordersListView;

    @Nullable
    private ProgressBar _ordersLoadingView;

    @Nullable
    private TextView _ordersNoContentView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private Paginate _ordersPaginate;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private OrdersSortOrder _ordersSortOrder;

    @Getter(onMethod = @__(@Override))
    private boolean _pageLoading;

    private void initializeOrdersListView() {
        if (_ordersListView != null) {
            final val context = getContext();
            final val layoutManager = new LinearLayoutManager(context);

            final val spacingDecorator = SpacingItemDecoration
                .builder()
                .setSpacing(getResources().getDimensionPixelOffset(R.dimen.grid_large_spacing))
                .collapseBorders()
                .enableEdges()
                .build();
            _ordersListView.addItemDecoration(spacingDecorator);
            _ordersListAdapter = new OrdersListAdapter();
            _ordersListView.setLayoutManager(layoutManager);
            _ordersListView.setAdapter(_ordersListAdapter);

            _ordersListView.setHasFixedSize(false);
        }
    }

    private void onPageLoadingStateChanged() {
        final val ordersListAdapter = getOrdersListAdapter();
        if (ordersListAdapter != null) {
            ordersListAdapter.setLoadingNextPage(isPageLoading());
        }
    }

    private void riseViewOrdersEvent() {
        _viewOrdersEvent.rise(new OrdersParamsEventArgs(getOrdersFilterParams(),
                                                        getOrdersSortOrder()));
    }

    private void showDialog(final long orderId, @NonNull final OrderState oldState) {
        Contracts.requireNonNull(oldState, "oldState == null");

        if (_orderStatesAdapter == null) {
            _orderStatesAdapter = new OrderStatesAdapter(getContext());
        }
        final val dialog = new AlertDialog.Builder(getContext()).setSingleChoiceItems(
            _orderStatesAdapter,
            _orderStatesAdapter.getPosition(oldState),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    dialog.dismiss();
                    final val newState = _orderStatesAdapter.getItem(which);
                    if (newState != null) {
                        _changeStatusEvent.rise(new ChangeOrderStateEventArgs(orderId,
                                                                              oldState,
                                                                              newState));
                    }
                }
            }).show();
    }
}
