package com.btc.prosport.manager.screen.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.recyclerView.decoration.SpacingItemDecoration;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.api.model.Covering;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.coveringEditor.CoveringsListAdapter;
import com.btc.prosport.manager.core.eventArgs.ChangePlaygroundCoveringEventArgs;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.PlaygroundCoveringEditorScreen;
import com.btc.prosport.manager.screen.fragment.playgroundEditor.BasePlaygroundEditorPageFragment;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public class PlaygroundCoveringEditorFragment extends BasePlaygroundEditorPageFragment
    implements PlaygroundCoveringEditorScreen {

    private static final String _LOG_TAG =
        ConstantBuilder.logTag(PlaygroundCoveringEditorFragment.class);

    @Override
    public void bindViews(@NonNull final View source) {
        super.bindViews(source);

        _coveringNoContentView = source.findViewById(R.id.no_content);
        _coveringLoadingErrorView = source.findViewById(R.id.loading_error);
        _coveringLoadingView = (ProgressBar) source.findViewById(R.id.loading);
        _coveringListView = (RecyclerView) source.findViewById(R.id.coverings);
    }

    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view =
            inflater.inflate(R.layout.fragment_playground_covering_editor, container, false);

        bindViews(view);

        initCoveringsListAdapter();

        final val coveringsAdapter = getCoveringsAdapter();
        if (coveringsAdapter != null) {
            coveringsAdapter.getPickCoveringEvent().addHandler(_changeSelectedCoveringIdHandler);
        }
        _coveringLoadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_coveringListView)
            .setLoadingView(_coveringLoadingView)
            .setNoContentView(_coveringNoContentView)
            .setErrorView(_coveringLoadingErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long, 0))
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();

        _coveringLoadingViewDelegate.showContent();

        return view;
    }

    @Override
    public void commitChanges() {
        final val playgroundId = getPlaygroundId();
        final val newCoveringId = getSelectedCoveringId();
        if (playgroundId != null && newCoveringId != null) {
            final val eventArgs =
                new ChangePlaygroundCoveringEventArgs(playgroundId, newCoveringId);
            _changePlaygroundCoveringEvent.rise(eventArgs);
        }
    }

    @Override
    public int getLabelId() {
        return R.string.playground_editor_covering_title;
    }

    @Override
    public void onResume() {
        super.onResume();
        riseViewCoveringsEvent();
    }

    @Override
    public void revertPlaygroundCovering() {
        final val coveringsAdapter = getCoveringsAdapter();
        if (coveringsAdapter != null) {
            final val coveringId = getPlaygroundCoveringId();
            if (coveringId != null) {
                coveringsAdapter.setSelectedCovering(coveringId);
                setSelectedCoveringId(coveringId);
                coveringsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void displayLoading() {
        final val loadingViewDelegate = getCoveringLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public void displayLoadingError() {
        final val loadingViewDelegate = getCoveringLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showError();
        }
    }

    @Override
    public void displayPlaygroundCovering(
        @Nullable final Covering playgroundCovering, @Nullable final List<Covering> coverings) {

        final val coveringsAdapter = getCoveringsAdapter();
        if (coveringsAdapter != null) {
            final val adapterCoverings = coveringsAdapter.getCoveringItems();
            if (!adapterCoverings.isEmpty()) {
                adapterCoverings.clear();
            }
            if (playgroundCovering != null && coverings != null && !coverings.isEmpty()) {
                final long coveringId = playgroundCovering.getId();
                setPlaygroundCoveringId(coveringId);
                setSelectedCoveringId(coveringId);
                coveringsAdapter.setSelectedCovering(coveringId);
                adapterCoverings.addAll(coverings);
            }
            coveringsAdapter.notifyDataSetChanged();
        }
        final val loadingViewDelegate = getCoveringLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(coverings != null && !coverings.isEmpty());
        }
    }

    @Override
    @NonNull
    public Event<ChangePlaygroundCoveringEventArgs> getChangePlaygroundCoveringEvent() {
        return _changePlaygroundCoveringEvent;
    }

    @Override
    @NonNull
    public Event<IdEventArgs> getViewPlaygroundCoveringEvent() {
        return _viewPlaygroundCoveringEvent;
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

    @Override
    @CallSuper
    protected void onPlaygroundIdChanged() {
        super.onPlaygroundIdChanged();

        if (isResumed()) {
            riseViewCoveringsEvent();
        }
    }

    @Named(PresenterNames.COVERING_EDITOR)
    @Inject
    @Nullable
    @Getter(AccessLevel.PROTECTED)
    /*package-private*/ Presenter<PlaygroundCoveringEditorScreen> _presenter;

    @NonNull
    private final ManagedEvent<ChangePlaygroundCoveringEventArgs> _changePlaygroundCoveringEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewPlaygroundCoveringEvent = Events.createEvent();

    @Nullable
    private RecyclerView _coveringListView;

    @Nullable
    private View _coveringLoadingErrorView;

    @Nullable
    private ProgressBar _coveringLoadingView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _coveringLoadingViewDelegate;

    @Nullable
    private View _coveringNoContentView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private CoveringsListAdapter _coveringsAdapter;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LinearLayoutManager _coveringsLayoutManager;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private Long _playgroundCoveringId;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private Long _selectedCoveringId;

    @NonNull
    private final EventHandler<IdEventArgs> _changeSelectedCoveringIdHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                setSelectedCoveringId(eventArgs.getId());
                _changePlaygroundCoveringEvent.rise(new ChangePlaygroundCoveringEventArgs
                                                        (getPlaygroundId(),
                                                                                          getSelectedCoveringId()));
            }
        };

    private void initCoveringsListAdapter() {
        if (_coveringListView != null) {
            final val context = getContext();
            final val resources = context.getResources();

            _coveringsLayoutManager = new LinearLayoutManager(context);
            _coveringsAdapter = new CoveringsListAdapter();

            final int spacing = resources.getDimensionPixelOffset(R.dimen.grid_large_spacing);

            final val spacingDecorator = SpacingItemDecoration
                .builder()
                .setSpacing(spacing)
                .collapseBorders()
                .enableEdges()
                .build();
            _coveringListView.addItemDecoration(spacingDecorator);
            _coveringListView.setLayoutManager(_coveringsLayoutManager);
            _coveringListView.setAdapter(_coveringsAdapter);
        }
    }

    private void riseViewCoveringsEvent() {
        final val playgroundId = getPlaygroundId();
        if (playgroundId != null) {
            _viewPlaygroundCoveringEvent.rise(new IdEventArgs(playgroundId));
        } else {
            final val loadingViewDelegate = getCoveringLoadingViewDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.showContent(false);
            }
        }
    }
}
