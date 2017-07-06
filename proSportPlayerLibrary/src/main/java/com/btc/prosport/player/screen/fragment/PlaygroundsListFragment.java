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
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.extension.view.recyclerView.decoration.SpacingItemDecoration;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.SwipeRefreshUtils;
import com.btc.prosport.api.ProSportContract;
import com.btc.prosport.api.model.PlaygroundPreview;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.adapter.playgroundsList.PlaygroundsAdapter;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.screen.PlaygroundsListScreen;
import com.btc.prosport.player.screen.activity.booking.BookingActivity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class PlaygroundsListFragment extends BaseSportComplexViewerPageFragment
    implements SwipeRefreshLayout.OnRefreshListener, PlaygroundsListScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(PlaygroundsListFragment.class);

    @Override
    public final void displayPlaygrounds(@Nullable final List<PlaygroundPreview> playgrounds) {
        if (_playgroundsRefreshView != null) {
            _playgroundsRefreshView.setRefreshing(false);
        }

        final val playgroundsAdapter = getPlaygroundsAdapter();
        if (playgroundsAdapter != null) {
            if (playgrounds != null) {
                playgroundsAdapter.setItems(playgrounds);
            } else {
                playgroundsAdapter.removeItems();
            }
        }

        final val loadingViewDelegate = getPlaygroundsLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(playgrounds != null && !playgrounds.isEmpty());
        }
    }

    @Override
    public final void displayPlaygroundsLoading() {
        final val loadingViewDelegate = getPlaygroundsLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public final void displayPlaygroundsLoadingError() {
        if (_playgroundsRefreshView != null) {
            _playgroundsRefreshView.setRefreshing(false);
        }

        final val loadingViewDelegate = getPlaygroundsLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            final val playgroundsAdapter = getPlaygroundsAdapter();
            if (playgroundsAdapter == null || playgroundsAdapter.getItems().isEmpty()) {
                loadingViewDelegate.showError();
            } else {
                loadingViewDelegate.showContent();
            }
        }
    }

    @NonNull
    @Override
    public final ManagedEvent<IdEventArgs> getRefreshPlaygroundsEvent() {
        return _refreshPlaygroundsEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getReservePlaygroundEvent() {
        return _reservePlaygroundEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getViewPlaygroundsEvent() {
        return _viewPlaygroundsEvent;
    }

    @CallSuper
    @Override
    public void bindViews(@NonNull final View view) {
        super.bindViews(Contracts.requireNonNull(view, "view == null"));

        _playgroundsView = (RecyclerView) view.findViewById(R.id.playgrounds_list);
        _playgroundsNoContentView = view.findViewById(R.id.no_content);
        _playgroundsLoadingErrorView = view.findViewById(R.id.loading_error);
        _playgroundsLoadingView = (ContentLoaderProgressBar) view.findViewById(R.id.loading);
        _playgroundsRefreshView =
            (SwipeRefreshLayout) view.findViewById(R.id.playgrounds_list_refresh);
    }

    @Nullable
    @Override
    public final View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view = inflater.inflate(R.layout.fragment_playgrounds_list, container, false);

        bindViews(view);

        initializePlaygroundsView();

        final val playgroundsAdapter = getPlaygroundsAdapter();
        if (playgroundsAdapter != null) {
            playgroundsAdapter.getReservePlaygroundEvent().addHandler(_reservePlaygroundHandler);
        }

        if (_playgroundsRefreshView != null) {
            SwipeRefreshUtils.setupPrimaryColors(_playgroundsRefreshView);
            _playgroundsRefreshView.setOnRefreshListener(this);
        }

        //@formatter:off
        _playgroundsLoadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_playgroundsView)
            .setLoadingView(_playgroundsLoadingView)
            .setNoContentView(_playgroundsNoContentView)
            .setErrorView(_playgroundsLoadingErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long,
                                                                   FadeVisibilityHandler.NO_ANIMATION))
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();
        //@formatter:on

        return view;
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        final val playgroundsAdapter = getPlaygroundsAdapter();
        if (playgroundsAdapter != null) {
            playgroundsAdapter.getReservePlaygroundEvent().removeHandler(_reservePlaygroundHandler);
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
    public void onRefresh() {

        final val sportComplexId = getSportComplexId();
        if (sportComplexId != null) {
            _refreshPlaygroundsEvent.rise(new IdEventArgs(sportComplexId));
        } else {
            final val loadingViewDelegate = getPlaygroundsLoadingViewDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.showContent(false);
            }
        }
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();

        if (isPageActive()) {
            final val sportComplexId = getSportComplexId();
            if (sportComplexId != null) {
                _viewPlaygroundsEvent.rise(new IdEventArgs(sportComplexId));
            } else {
                final val loadingViewDelegate = getPlaygroundsLoadingViewDelegate();
                if (loadingViewDelegate != null) {
                    loadingViewDelegate.showContent(false);
                }
            }
        }
    }

    @CallSuper
    @Override
    public void onPause() {
        super.onPause();

        final val loadingViewDelegate = getPlaygroundsLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.hideAll();
        }
    }

    @CallSuper
    @Override
    protected void onEnterPage() {
        super.onEnterPage();

        if (isPageActive()) {
            final val sportComplexId = getSportComplexId();
            if (sportComplexId != null) {
                _viewPlaygroundsEvent.rise(new IdEventArgs(sportComplexId));
            } else {
                final val loadingViewDelegate = getPlaygroundsLoadingViewDelegate();
                if (loadingViewDelegate != null) {
                    loadingViewDelegate.showContent(false);
                }
            }
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

    @Named(PresenterNames.PLAYGROUNDS_LIST)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<PlaygroundsListScreen> _presenter;

    @NonNull
    private final ManagedEvent<IdEventArgs> _refreshPlaygroundsEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _reservePlaygroundEvent = Events.createEvent();

    @NonNull
    private final EventHandler<IdEventArgs> _reservePlaygroundHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                // FIXME: 13.04.2017
                BookingActivity.start(getContext(),
                                      ProSportContract.getPlaygroundUri(eventArgs.getId()));

                _reservePlaygroundEvent.rise(eventArgs);
            }
        };

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewPlaygroundsEvent = Events.createEvent();

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private PlaygroundsAdapter _playgroundsAdapter;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LinearLayoutManager _playgroundsLayoutManager;

    @Nullable
    private View _playgroundsLoadingErrorView;

    @Nullable
    private ContentLoaderProgressBar _playgroundsLoadingView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _playgroundsLoadingViewDelegate;

    @Nullable
    private View _playgroundsNoContentView;

    @Nullable
    private SwipeRefreshLayout _playgroundsRefreshView;

    @Nullable
    private RecyclerView _playgroundsView;

    private void initializePlaygroundsView() {
        if (_playgroundsView != null) {
            final val context = getContext();
            final val resources = context.getResources();

            _playgroundsLayoutManager = new LinearLayoutManager(context);
            _playgroundsAdapter = new PlaygroundsAdapter();

            final int spacing = resources.getDimensionPixelOffset(R.dimen.grid_large_spacing);

            final val spacingDecorator = SpacingItemDecoration
                .builder()
                .setSpacing(spacing)
                .collapseBorders()
                .enableEdges()
                .build();
            _playgroundsView.addItemDecoration(spacingDecorator);

            _playgroundsView.setLayoutManager(_playgroundsLayoutManager);
            _playgroundsView.setAdapter(_playgroundsAdapter);
        }
    }
}
