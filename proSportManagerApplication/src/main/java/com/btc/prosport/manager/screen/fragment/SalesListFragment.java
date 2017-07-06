package com.btc.prosport.manager.screen.fragment;

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

import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.pagination.Paginate;
import com.btc.common.extension.pagination.PaginateInterface;
import com.btc.common.extension.pagination.RecyclerPaginate;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.extension.view.recyclerView.decoration.SpacingItemDecoration;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.SwipeRefreshUtils;
import com.btc.prosport.api.model.Sale;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.salesList.SalesListAdapter;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.SalesListScreen;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class SalesListFragment extends BaseWorkspaceFragment
    implements SwipeRefreshLayout.OnRefreshListener, PaginateInterface, SalesListScreen {
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

        _salesErrorView = view.findViewById(R.id.loading_error);
        _salesView = (RecyclerView) view.findViewById(R.id.sales_list);
        _salesLoadingView = (ContentLoaderProgressBar) view.findViewById(R.id.loading);
        _salesNoContentView = view.findViewById(R.id.no_content);
        _salesRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.sales_list_refresh);
    }

    @Nullable
    @Override
    public final View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view = inflater.inflate(R.layout.fragment_sales_list, container, false);

        bindViews(view);

        initializeSalesView();

        if (_salesView != null) {
            _salesPaginate =
                RecyclerPaginate.builder(_salesView, this).setLoadingTriggerThreshold(2).build();

            _salesPaginate.bind();
        }

        if (_salesRefreshView != null) {
            SwipeRefreshUtils.setupPrimaryColors(_salesRefreshView);
            _salesRefreshView.setOnRefreshListener(this);
        }

        //@formatter:off
        _salesLoadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_salesView)
            .setLoadingView(_salesLoadingView)
            .setNoContentView(_salesNoContentView)
            .setErrorView(_salesErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long,
                                                                   FadeVisibilityHandler.NO_ANIMATION))
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();
        //@formatter:on

        final val salesAdapter = getSalesAdapter();
        if (salesAdapter != null) {
            salesAdapter.getDeleteSaleEvent().addHandler(_deleteSaleHandler);
        }

        return view;
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        final val salesAdapter = getSalesAdapter();
        if (salesAdapter != null) {
            salesAdapter.getDeleteSaleEvent().removeHandler(_deleteSaleHandler);
        }

        final val salesPaginate = getSalesPaginate();
        if (salesPaginate != null) {
            salesPaginate.unbind();
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
    public void displayDeletedSale(@Nullable final Long saleId) {
        if (saleId != null) {
            final val salesAdapter = getSalesAdapter();
            if (salesAdapter != null) {
                final val saleIndex = salesAdapter.getSaleIndex(saleId);
                salesAdapter.removeItem(saleIndex);
            }
        }
    }

    @Override
    public final void displaySales(@Nullable final List<Sale> sales, final boolean lastPage) {
        setPageLoading(false);
        setAllItemsLoaded(lastPage);

        if (_salesRefreshView != null) {
            _salesRefreshView.setRefreshing(false);
        }

        final val salesAdapter = getSalesAdapter();
        if (salesAdapter != null) {
            if (sales != null) {
                salesAdapter.setItems(sales);
            } else {
                salesAdapter.removeItems();
            }
        }

        final val loadingViewDelegate = getSalesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(sales != null && !sales.isEmpty());
        }

        final val salesPaginate = getSalesPaginate();
        if (salesPaginate != null) {
            salesPaginate.checkPageLoading();
        }
    }

    @Override
    public final void displaySalesLoading() {
        final val loadingViewDelegate = getSalesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public final void displaySalesLoadingError() {
        setPageLoading(false);
        setAllItemsLoaded(true);

        if (_salesRefreshView != null) {
            _salesRefreshView.setRefreshing(false);
        }

        final val loadingViewDelegate = getSalesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            final val salesAdapter = getSalesAdapter();
            if (salesAdapter == null || salesAdapter.getItems().isEmpty()) {
                loadingViewDelegate.showError();
            } else {
                loadingViewDelegate.showContent();
            }
        }
    }

    @Override
    public final void displaySalesPage(
        @Nullable final List<Sale> sales, final boolean lastPage) {
        setPageLoading(false);
        setAllItemsLoaded(lastPage);

        if (_salesRefreshView != null) {
            _salesRefreshView.setRefreshing(false);
        }

        final val salesAdapter = getSalesAdapter();
        if (salesAdapter != null && sales != null) {
            salesAdapter.addItems(sales);
        }

        final val salesPaginate = getSalesPaginate();
        if (salesPaginate != null) {
            salesPaginate.checkPageLoading();
        }
    }

    @Override
    @NonNull
    public final Event<IdEventArgs> getDeleteSaleEvent() {
        return _deleteSaleEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getRefreshSalesEvent() {
        return _refreshSalesEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewNextSalesPageEvent() {
        return _viewNextSalesPageEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewSalesEvent() {
        return _viewSalesEvent;
    }

    @CallSuper
    @Override
    public void onLoadNextPage() {
        setPageLoading(true);

        _viewNextSalesPageEvent.rise();
    }

    @CallSuper
    @Override
    public void onRefresh() {
        _refreshSalesEvent.rise();
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();

        if (isPageActive()) {
            _viewSalesEvent.rise();
        }
    }

    @CallSuper
    @Override
    public void onPause() {
        super.onPause();

        final val loadingViewDelegate = getSalesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.hideAll();
        }
    }

    @Override
    protected void onEnterPage() {
        super.onEnterPage();

        if (isResumed()) {
            _viewSalesEvent.rise();
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
    protected void onPageLoadingStateChanged() {
        final val salesAdapter = getSalesAdapter();
        if (salesAdapter != null) {
            salesAdapter.setLoadingNextPage(isPageLoading());
        }
    }

    @Named(PresenterNames.SALES_LIST)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<SalesListScreen> _presenter;

    @NonNull
    private final ManagedEvent<IdEventArgs> _deleteSaleEvent = Events.createEvent();

    @NonNull
    private final EventHandler<IdEventArgs> _deleteSaleHandler = new EventHandler<IdEventArgs>() {
        @Override
        public void onEvent(@NonNull final IdEventArgs eventArgs) {
            Contracts.requireNonNull(eventArgs, "eventArgs == null");

            _deleteSaleEvent.rise(eventArgs);
        }
    };

    @NonNull
    private final ManagedNoticeEvent _refreshSalesEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _viewNextSalesPageEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _viewSalesEvent = Events.createNoticeEvent();

    @Getter(onMethod = @__(@Override))
    @Setter
    private boolean _allItemsLoaded;

    @Getter(onMethod = @__(@Override))
    private boolean _pageLoading;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private SalesListAdapter _salesAdapter;

    @Nullable
    private View _salesErrorView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LinearLayoutManager _salesLayoutManager;

    @Nullable
    private ContentLoaderProgressBar _salesLoadingView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _salesLoadingViewDelegate;

    @Nullable
    private View _salesNoContentView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private Paginate _salesPaginate;

    @Nullable
    private SwipeRefreshLayout _salesRefreshView;

    @Nullable
    private RecyclerView _salesView;

    private void initializeSalesView() {
        if (_salesView != null) {
            final val context = getContext();
            final val resources = context.getResources();

            _salesLayoutManager = new LinearLayoutManager(context);
            _salesAdapter = new SalesListAdapter();

            final int spacing = resources.getDimensionPixelOffset(R.dimen.grid_large_spacing);

            final val spacingDecorator = SpacingItemDecoration
                .builder()
                .setSpacing(spacing)
                .collapseBorders()
                .enableEdges()
                .build();
            _salesView.addItemDecoration(spacingDecorator);

            _salesView.setLayoutManager(_salesLayoutManager);
            _salesView.setAdapter(_salesAdapter);
        }
    }
}
