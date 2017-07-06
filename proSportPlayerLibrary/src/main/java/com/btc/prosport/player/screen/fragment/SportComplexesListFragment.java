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
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.generic.ManagedEvent;
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
import com.btc.prosport.api.model.SportComplexPreview;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.adapter.sportComplexesList.SportComplexesAdapter;
import com.btc.prosport.player.core.eventArgs.SportComplexesSearchEventArgs;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.screen.SportComplexesListScreen;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class SportComplexesListFragment extends BaseSportComplexesViewerPageFragment
    implements SwipeRefreshLayout.OnRefreshListener, PaginateInterface, SportComplexesListScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SportComplexesListFragment.class);

    @Override
    public final void displaySportComplexes(
        @Nullable final List<SportComplexPreview> sportComplexes, final boolean lastPage) {
        setPageLoading(false);
        setAllItemsLoaded(lastPage);

        if (_sportComplexesRefreshView != null) {
            _sportComplexesRefreshView.setRefreshing(false);
        }

        final val sportComplexesAdapter = getSportComplexesAdapter();
        if (sportComplexesAdapter != null) {
            if (sportComplexes != null) {
                sportComplexesAdapter.setItems(sportComplexes);
            } else {
                sportComplexesAdapter.removeItems();
            }
        }

        final val loadingViewDelegate = getSportComplexesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(sportComplexes != null && !sportComplexes.isEmpty());
        }

        final val sportComplexesPaginate = getSportComplexesPaginate();
        if (sportComplexesPaginate != null) {
            sportComplexesPaginate.checkPageLoading();
        }
    }

    @Override
    public final void displaySportComplexesPage(
        @Nullable final List<SportComplexPreview> sportComplexes, final boolean lastPage) {
        setPageLoading(false);
        setAllItemsLoaded(lastPage);

        if (_sportComplexesRefreshView != null) {
            _sportComplexesRefreshView.setRefreshing(false);
        }

        final val sportComplexesAdapter = getSportComplexesAdapter();
        if (sportComplexesAdapter != null && sportComplexes != null) {
            sportComplexesAdapter.addItems(sportComplexes);
        }

        final val sportComplexesPaginate = getSportComplexesPaginate();
        if (sportComplexesPaginate != null) {
            sportComplexesPaginate.checkPageLoading();
        }
    }

    @Override
    public final void displaySportComplexesLoading() {
        final val loadingViewDelegate = getSportComplexesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public final void displaySportComplexesLoadingError() {
        setPageLoading(false);
        setAllItemsLoaded(true);

        if (_sportComplexesRefreshView != null) {
            _sportComplexesRefreshView.setRefreshing(false);
        }

        final val loadingViewDelegate = getSportComplexesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            final val sportComplexesAdapter = getSportComplexesAdapter();
            if (sportComplexesAdapter == null || sportComplexesAdapter.getItems().isEmpty()) {
                loadingViewDelegate.showError();
            } else {
                loadingViewDelegate.showContent();
            }
        }
    }

    @NonNull
    @Override
    public final ManagedEvent<SportComplexesSearchEventArgs> getRefreshSportComplexesEvent() {
        return _refreshSportComplexesEvent;
    }

    @NonNull
    @Override
    public final ManagedEvent<SportComplexesSearchEventArgs> getViewNextSportComplexesPageEvent() {
        return _viewNextSportComplexesPageEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getViewSportComplexDetailsEvent() {
        return _viewSportComplexDetailsEvent;
    }

    @NonNull
    @Override
    public final Event<SportComplexesSearchEventArgs> getViewSportComplexesEvent() {
        return _viewSportComplexesEvent;
    }

    @Nullable
    @Override
    public final View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view = inflater.inflate(R.layout.fragment_sport_complexes_list, container, false);

        bindViews(view);

        initializeSportComplexesView();

        final val sportComplexesAdapter = getSportComplexesAdapter();
        if (sportComplexesAdapter != null) {
            sportComplexesAdapter
                .getViewSportComplexDetailsEvent()
                .addHandler(_viewSportComplexDetailsHandler);
        }

        if (_sportComplexesView != null) {
            _sportComplexesPaginate = RecyclerPaginate
                .builder(_sportComplexesView, this)
                .setLoadingTriggerThreshold(2)
                .build();

            _sportComplexesPaginate.bind();
        }

        if (_sportComplexesRefreshView != null) {
            SwipeRefreshUtils.setupPrimaryColors(_sportComplexesRefreshView);
            _sportComplexesRefreshView.setOnRefreshListener(this);
        }

        //@formatter:off
        _sportComplexesLoadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_sportComplexesView)
            .setLoadingView(_sportComplexesLoadingView)
            .setNoContentView(_sportComplexesNoContentView)
            .setErrorView(_sportComplexesLoadingErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long,
                                                                   FadeVisibilityHandler.NO_ANIMATION))
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();
        //@formatter:on

        return view;
    }

    @CallSuper
    @Override
    protected void onEnterPage() {
        super.onEnterPage();

        _viewSportComplexesEvent.rise(getSportComplexesSearchEventArgs());
    }

    @CallSuper
    @Override
    protected void onLeavePage() {
        super.onLeavePage();

        final val loadingViewDelegate = getSportComplexesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.hideAll();
        }
    }

    @CallSuper
    @Override
    protected void onSportComplexSearchParamsChangedWithInputDelay() {
        super.onSportComplexSearchParamsChangedWithInputDelay();

        if (isResumed() && isPageActive()) {
            _viewSportComplexesEvent.rise(getSportComplexesSearchEventArgs());
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
    public void bindViews(@NonNull final View view) {
        super.bindViews(Contracts.requireNonNull(view, "view == null"));

        _sportComplexesView = (RecyclerView) view.findViewById(R.id.sport_complexes_list);
        _sportComplexesNoContentView = view.findViewById(R.id.no_content);
        _sportComplexesLoadingErrorView = view.findViewById(R.id.loading_error);
        _sportComplexesLoadingView = (ContentLoaderProgressBar) view.findViewById(R.id.loading);
        _sportComplexesRefreshView =
            (SwipeRefreshLayout) view.findViewById(R.id.sport_complexes_list_refresh);
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        final val sportComplexesPaginate = getSportComplexesPaginate();
        if (sportComplexesPaginate != null) {
            sportComplexesPaginate.unbind();
        }

        final val sportComplexesAdapter = getSportComplexesAdapter();
        if (sportComplexesAdapter != null) {
            sportComplexesAdapter
                .getViewSportComplexDetailsEvent()
                .removeHandler(_viewSportComplexDetailsHandler);
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
    public boolean hasContent() {
        final boolean hasContent;

        final val sportComplexesAdapter = getSportComplexesAdapter();
        if (sportComplexesAdapter != null) {
            hasContent = !sportComplexesAdapter.getItems().isEmpty();
        } else {
            hasContent = false;
        }

        return hasContent;
    }

    @CallSuper
    @Override
    public void onLoadNextPage() {
        setPageLoading(true);

        _viewNextSportComplexesPageEvent.rise(getSportComplexesSearchEventArgs());
    }

    @CallSuper
    @Override
    public void onRefresh() {
        _refreshSportComplexesEvent.rise(getSportComplexesSearchEventArgs());
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();

        if (isPageActive()) {
            _viewSportComplexesEvent.rise(getSportComplexesSearchEventArgs());
        }
    }

    @CallSuper
    @Override
    public void onPause() {
        super.onPause();

        final val loadingViewDelegate = getSportComplexesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.hideAll();
        }
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

    protected void onPageLoadingStateChanged() {
        final val sportComplexesAdapter = getSportComplexesAdapter();
        if (sportComplexesAdapter != null) {
            sportComplexesAdapter.setLoadingNextPage(isPageLoading());
        }
    }

    @Named(PresenterNames.SPORT_COMPLEXES_LIST)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<SportComplexesListScreen> _presenter;

    @NonNull
    private final ManagedEvent<SportComplexesSearchEventArgs> _refreshSportComplexesEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<SportComplexesSearchEventArgs> _viewNextSportComplexesPageEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewSportComplexDetailsEvent = Events.createEvent();

    @NonNull
    private final EventHandler<IdEventArgs> _viewSportComplexDetailsHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                _viewSportComplexDetailsEvent.rise(eventArgs);
            }
        };

    @NonNull
    private final ManagedEvent<SportComplexesSearchEventArgs> _viewSportComplexesEvent =
        Events.createEvent();

    @Getter(onMethod = @__(@Override))
    @Setter
    private boolean _allItemsLoaded;

    @Getter(onMethod = @__(@Override))
    private boolean _pageLoading;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private SportComplexesAdapter _sportComplexesAdapter;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LinearLayoutManager _sportComplexesLayoutManager;

    @Nullable
    private View _sportComplexesLoadingErrorView;

    @Nullable
    private ContentLoaderProgressBar _sportComplexesLoadingView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _sportComplexesLoadingViewDelegate;

    @Nullable
    private View _sportComplexesNoContentView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private Paginate _sportComplexesPaginate;

    @Nullable
    private SwipeRefreshLayout _sportComplexesRefreshView;

    @Nullable
    private RecyclerView _sportComplexesView;

    private void initializeSportComplexesView() {
        if (_sportComplexesView != null) {
            final val context = getContext();
            final val resources = context.getResources();

            _sportComplexesLayoutManager = new LinearLayoutManager(context);
            _sportComplexesAdapter = new SportComplexesAdapter();

            final int spacing = resources.getDimensionPixelOffset(R.dimen.grid_large_spacing);

            final val spacingDecorator = SpacingItemDecoration
                .builder()
                .setSpacing(spacing)
                .collapseBorders()
                .enableEdges()
                .build();
            _sportComplexesView.addItemDecoration(spacingDecorator);

            _sportComplexesView.setLayoutManager(_sportComplexesLayoutManager);
            _sportComplexesView.setAdapter(_sportComplexesAdapter);
        }
    }
}
