package com.btc.prosport.player.screen.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.pagination.Paginate;
import com.btc.common.extension.pagination.PaginateInterface;
import com.btc.common.extension.pagination.RecyclerPaginate;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.extension.view.recyclerView.decoration.SpacingItemDecoration;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.SwipeRefreshUtils;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.adapter.ordersList.OrdersListAdapter;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.screen.OrdersListScreen;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class OrdersListFragment extends BasePlayerFragment
    implements SwipeRefreshLayout.OnRefreshListener, PaginateInterface, OrdersListScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrdersListFragment.class);

    @Override
    public final void displayOrders(
        @Nullable final List<Order> orders, final boolean lastPage) {
        setPageLoading(false);
        setAllItemsLoaded(lastPage);

        if (_ordersRefreshView != null) {
            _ordersRefreshView.setRefreshing(false);
        }

        final val ordersListAdapter = getOrdersAdapter();
        if (ordersListAdapter != null) {
            if (orders != null) {
                ordersListAdapter.setItems(orders);
            } else {
                ordersListAdapter.removeItems();
            }
        }

        final val loadingViewDelegate = getOrdersLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(orders != null && !orders.isEmpty());
        }

        final val ordersPaginate = getOrdersPaginate();
        if (ordersPaginate != null) {
            ordersPaginate.checkPageLoading();
        }
    }

    @Override
    public final void displayOrdersLoading() {
        final val loadingViewDelegate = getOrdersLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public final void displayOrdersLoadingError() {
        setPageLoading(false);
        setAllItemsLoaded(true);

        if (_ordersRefreshView != null) {
            _ordersRefreshView.setRefreshing(false);
        }

        final val loadingViewDelegate = getOrdersLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            final val ordersListAdapter = getOrdersAdapter();
            if (ordersListAdapter == null || ordersListAdapter.getItems().isEmpty()) {
                loadingViewDelegate.showError();
            } else {
                loadingViewDelegate.showContent();
            }
        }
    }

    @Override
    public final void displayOrdersPage(
        @Nullable final List<Order> orders, final boolean lastPage) {
        setPageLoading(false);
        setAllItemsLoaded(lastPage);

        if (_ordersRefreshView != null) {
            _ordersRefreshView.setRefreshing(false);
        }

        final val ordersListAdapter = getOrdersAdapter();
        if (ordersListAdapter != null && orders != null) {
            ordersListAdapter.addItems(orders);
        }

        final val ordersPaginate = getOrdersPaginate();
        if (ordersPaginate != null) {
            ordersPaginate.checkPageLoading();
        }
    }

    @Override
    public final void displayRequireAuthorizationError() {
        setPageLoading(false);
        setAllItemsLoaded(true);

        if (_ordersRefreshView != null) {
            _ordersRefreshView.setRefreshing(false);
        }

        final val loadingViewDelegate = getOrdersLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.hideAll();
        }

        if (_ordersRequireAuthorisationErrorView != null) {
            _ordersRequireAuthorisationErrorView.setVisibility(View.VISIBLE);
        }

        _canTryLoadOrders = false;
    }

    @NonNull
    @Override
    public final NoticeEvent getRefreshOrdersEvent() {
        return _refreshOrdersEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewNextOrdersPageEvent() {
        return _viewNextOrdersPageEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewOrdersEvent() {
        return _viewOrdersEvent;
    }

    public final void setPageLoading(final boolean pageLoading) {
        if (_pageLoading != pageLoading) {
            _pageLoading = pageLoading;

            onPageLoadingStateChanged();
        }
    }

    @CallSuper
    @Override
    public void bindViews(@NonNull final View view) {
        super.bindViews(Contracts.requireNonNull(view, "view == null"));

        _ordersErrorView = view.findViewById(R.id.loading_error);
        _ordersView = (RecyclerView) view.findViewById(R.id.orders_list);
        _ordersLoadingView = (ContentLoaderProgressBar) view.findViewById(R.id.loading);
        _ordersNoContentView = view.findViewById(R.id.no_content);
        _ordersRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.orders_list_refresh);
        _ordersRequireAuthorisationErrorView = view.findViewById(R.id.orders_require_authorisation);
    }

    @Nullable
    @Override
    public final View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view = inflater.inflate(R.layout.fragment_orders_list, container, false);

        bindViews(view);

        initializeOrdersView();

        if (_ordersView != null) {
            _ordersPaginate =
                RecyclerPaginate.builder(_ordersView, this).setLoadingTriggerThreshold(2).build();

            _ordersPaginate.bind();
        }

        if (_ordersRefreshView != null) {
            SwipeRefreshUtils.setupPrimaryColors(_ordersRefreshView);
            _ordersRefreshView.setOnRefreshListener(this);
        }

        _ordersLoadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_ordersView)
            .setLoadingView(_ordersLoadingView)
            .setNoContentView(_ordersNoContentView)
            .setErrorView(_ordersErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(
                R.anim.fade_in_long,
                FadeVisibilityHandler.NO_ANIMATION))
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .setVisibilityChangedListener(new LoadingViewDelegate.VisibilityChangedListener() {
                @Override
                public void onVisibilityChanged() {
                    final val loadingViewDelegate = getOrdersLoadingViewDelegate();
                    if (loadingViewDelegate != null) {
                        if (loadingViewDelegate.isVisible()) {
                            if (_ordersRequireAuthorisationErrorView != null) {
                                _ordersRequireAuthorisationErrorView.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            })
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

        _canTryLoadOrders = true;
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();

        if (_canTryLoadOrders) {
            _viewOrdersEvent.rise();
        }
    }

    @CallSuper
    @Override
    public void onPause() {
        super.onPause();

        final val loadingViewDelegate = getOrdersLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.hideAll();
        }
    }

    @CallSuper
    @Override
    public void onLoadNextPage() {
        setPageLoading(true);

        _viewNextOrdersPageEvent.rise();
    }

    @CallSuper
    @Override
    public void onRefresh() {
        _refreshOrdersEvent.rise();
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getPlayerSubscreenComponent().inject(this);

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    protected void onPageLoadingStateChanged() {
        final val ordersListAdapter = getOrdersAdapter();
        if (ordersListAdapter != null) {
            ordersListAdapter.setLoadingNextPage(isPageLoading());
        }
    }

    @Named(PresenterNames.ORDERS_LIST)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<OrdersListScreen> _presenter;

    @NonNull
    private final ManagedNoticeEvent _refreshOrdersEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _viewNextOrdersPageEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _viewOrdersEvent = Events.createNoticeEvent();

    @Getter(onMethod = @__(@Override))
    @Setter
    private boolean _allItemsLoaded;

    private boolean _canTryLoadOrders = true;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private OrdersListAdapter _ordersAdapter;

    @Nullable
    private View _ordersErrorView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LinearLayoutManager _ordersLayoutManager;

    @Nullable
    private ContentLoaderProgressBar _ordersLoadingView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _ordersLoadingViewDelegate;

    @Nullable
    private View _ordersNoContentView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private Paginate _ordersPaginate;

    @Nullable
    private SwipeRefreshLayout _ordersRefreshView;

    @Nullable
    private View _ordersRequireAuthorisationErrorView;

    @Nullable
    private RecyclerView _ordersView;

    @Getter(onMethod = @__(@Override))
    private boolean _pageLoading;

    private void initializeOrdersView() {
        if (_ordersView != null) {
            final val context = getContext();
            final val resources = context.getResources();

            _ordersLayoutManager = new LinearLayoutManager(context);
            _ordersAdapter = new OrdersListAdapter();

            final int spacing = resources.getDimensionPixelOffset(R.dimen.grid_large_spacing);

            final val spacingDecorator = SpacingItemDecoration
                .builder()
                .setSpacing(spacing)
                .collapseBorders()
                .enableEdges()
                .build();
            _ordersView.addItemDecoration(spacingDecorator);

            _ordersView.setLayoutManager(_ordersLayoutManager);
            _ordersView.setAdapter(_ordersAdapter);
        }
    }
}
