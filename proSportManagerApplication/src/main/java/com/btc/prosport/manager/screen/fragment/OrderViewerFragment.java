package com.btc.prosport.manager.screen.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.SwipeRefreshUtils;
import com.btc.prosport.api.ProSportDataContract;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.core.adapter.cell.binder.IntervalViewHolderBinder;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.orderDetails.OrderDetailsAdapter;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.OrderViewerScreen;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class OrderViewerFragment extends BaseWorkspaceFragment
    implements OrderViewerScreen, SwipeRefreshLayout.OnRefreshListener {
    private static final String _LOG_TAG = ConstantBuilder.logTag(OrderViewerFragment.class);

    @CallSuper
    @Override
    public void bindViews(@NonNull final View view) {
        super.bindViews(Contracts.requireNonNull(view, "view == null"));

        _orderLoadingView = (ContentLoaderProgressBar) view.findViewById(R.id.loading);
        _orderLoadingErrorView = (TextView) view.findViewById(R.id.loading_error);
        _orderNoContentView = (TextView) view.findViewById(R.id.no_content);

        _orderDetailsView = (RecyclerView) view.findViewById(R.id.order_details);
        _swipeRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.order_details_refresh);

        _intervalsOverlayHeaderView =
            (LinearLayout) view.findViewById(R.id.intervals_overlay_header);
    }

    @CallSuper
    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view = inflater.inflate(R.layout.fragment_order_viewer, container, false);

        bindViews(view);

        initializeOrderDetailsView();

        if (_swipeRefreshView != null) {
            SwipeRefreshUtils.setupPrimaryColors(_swipeRefreshView);

            _swipeRefreshView.setOnRefreshListener(this);
        }

        _loadingOrderViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_orderDetailsView)
            .setLoadingView(_orderLoadingView)
            .setErrorView(_orderLoadingErrorView)
            .setNoContentView(_orderNoContentView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long, 0) {
                @Override
                public void changeVisibility(@NonNull final View view, final boolean visible) {
                    super.changeVisibility(view, visible);

                    final val orderDetailsAdapter = getOrderDetailsAdapter();
                    if (orderDetailsAdapter != null && _orderDetailsView != null) {
                        orderDetailsAdapter.invalidateOverlayHeaderVisibility(_orderDetailsView);
                    }
                }
            })
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();

        return view;
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        final val orderDetailsAdapter = getOrderDetailsAdapter();
        if (orderDetailsAdapter != null && _orderDetailsView != null) {
            orderDetailsAdapter.unbindOverlayHeaderView(_orderDetailsView);
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

    @Override
    public void displayLoading() {
        final val loadingOrderViewDelegate = getLoadingOrderViewDelegate();
        if (loadingOrderViewDelegate != null) {
            loadingOrderViewDelegate.showLoading();
        }
    }

    @Override
    public void displayLoadingError() {
        final val loadingOrderViewDelegate = getLoadingOrderViewDelegate();
        if (loadingOrderViewDelegate != null) {
            loadingOrderViewDelegate.showError();
        }
    }

    @Override
    public void displayOrder(
        @Nullable final Order order,
        @Nullable final List<Interval> intervals,
        final long startDate) {
        final val orderDetailsAdapter = getOrderDetailsAdapter();
        if (orderDetailsAdapter != null) {
            orderDetailsAdapter.setOrder(order);
            orderDetailsAdapter.setCells(intervals);
            orderDetailsAdapter.setStartTime(startDate);
            orderDetailsAdapter.notifyDataSetChanged();
        }

        final val loadingOrderViewDelegate = getLoadingOrderViewDelegate();
        if (loadingOrderViewDelegate != null) {
            loadingOrderViewDelegate.showContent(order != null);
        }
    }

    @NonNull
    @Override
    public Event<IdEventArgs> getReservationEditEvent() {
        return _reservationEditEvent;
    }

    @NonNull
    @Override
    public Event<IdEventArgs> getViewOrderEvent() {
        return _viewOrderEvent;
    }

    @CallSuper
    @Override
    public void onRefresh() {
        // FIXME: 05.04.2017
        if (_swipeRefreshView != null) {
            _swipeRefreshView.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        final Long orderId = getOrderId();
        if (orderId != null) {
            _viewOrderEvent.rise(new IdEventArgs(orderId));
        } else {
            final val loadingViewDelegate = getLoadingOrderViewDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.hideAll();
            }
        }

        final val orderDetailsAdapter = getOrderDetailsAdapter();
        if (orderDetailsAdapter != null && _orderDetailsView != null) {
            orderDetailsAdapter.invalidateOverlayHeader(_orderDetailsView);
            orderDetailsAdapter.invalidateOverlayHeaderVisibility(_orderDetailsView);
        }
    }

    public void setOrderId(@Nullable final Long orderId) {
        if (!Objects.equals(orderId, _orderId)) {
            _orderId = orderId;
            onOrderIdChanged();
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

    @Named(PresenterNames.ORDER)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<OrderViewerScreen> _presenter;

    @NonNull
    private final ManagedEvent<IdEventArgs> _reservationEditEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewOrderEvent = Events.createEvent();

    @Nullable
    private LinearLayout _intervalsOverlayHeaderView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _loadingOrderViewDelegate;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private OrderDetailsAdapter _orderDetailsAdapter;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private GridLayoutManager _orderDetailsLayoutManger;

    @Nullable
    private RecyclerView _orderDetailsView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private Long _orderId;

    @Nullable
    private TextView _orderLoadingErrorView;

    @Nullable
    private ContentLoaderProgressBar _orderLoadingView;

    @Nullable
    private TextView _orderNoContentView;

    @Nullable
    private SwipeRefreshLayout _swipeRefreshView;

    private void initializeOrderDetailsView() {
        if (_orderDetailsView != null) {
            final val context = getContext();

            final long intervalLength = ProSportDataContract.INTERVAL_LENGTH;
            final int columnCount = (int) (DateUtils.WEEK_IN_MILLIS / DateUtils.DAY_IN_MILLIS);
            final int rowCount = (int) (DateUtils.DAY_IN_MILLIS / intervalLength);

            _orderDetailsAdapter = new OrderDetailsAdapter(context);

            _orderDetailsAdapter.setCellViewHolderBinder(new IntervalViewHolderBinder<>(
                _orderDetailsAdapter));
            _orderDetailsAdapter.setIntervalLength(intervalLength);
            _orderDetailsAdapter.setTableColumnCount(columnCount);
            _orderDetailsAdapter.setTableRowCount(rowCount);
            _orderDetailsAdapter.setStartTime(System.currentTimeMillis());

            if (_intervalsOverlayHeaderView != null) {
                _orderDetailsAdapter.bindOverlayHeaderView(_orderDetailsView,
                                                           _intervalsOverlayHeaderView);
            }
            _orderDetailsLayoutManger = (GridLayoutManager) _orderDetailsView.getLayoutManager();

            _orderDetailsView.setItemViewCacheSize(400);
            _orderDetailsView.setAdapter(_orderDetailsAdapter);
        }
    }

    private void onOrderIdChanged() {
        final Long orderId = getOrderId();
        if (orderId != null) {
            _viewOrderEvent.rise(new IdEventArgs(orderId));
        }
    }
}
