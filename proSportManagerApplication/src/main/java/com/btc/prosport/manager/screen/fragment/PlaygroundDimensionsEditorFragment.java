package com.btc.prosport.manager.screen.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
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
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.Dimension;
import com.btc.prosport.manager.core.eventArgs.ChangePlaygroundDimensionsEventArgs;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.PlaygroundDimensionsEditorScreen;
import com.btc.prosport.manager.screen.fragment.playgroundEditor.BasePlaygroundEditorPageFragment;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public class PlaygroundDimensionsEditorFragment extends BasePlaygroundEditorPageFragment
    implements PlaygroundDimensionsEditorScreen {

    public static final int DEFAULT_MIN_SIZE = 0;

    private static final String _LOG_TAG =
        ConstantBuilder.logTag(PlaygroundDimensionsEditorFragment.class);

    @Override
    public void bindViews(@NonNull final View source) {
        super.bindViews(source);

        _playgroundHeightView = (TextInputEditText) source.findViewById(R.id.playground_height);
        _playgroundLengthView = (TextInputEditText) source.findViewById(R.id.playground_length);
        _playgroundWidthView = (TextInputEditText) source.findViewById(R.id.playground_width);
        _dimensionsNoContentView = source.findViewById(R.id.no_content);
        _dimensionsLoadingErrorView = source.findViewById(R.id.loading_error);
        _dimensionsLoadingView = (ProgressBar) source.findViewById(R.id.loading);
        _dimensionsView = source.findViewById(R.id.dimensions);
    }

    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view =
            inflater.inflate(R.layout.fragment_playground_dimensions_editor, container, false);

        bindViews(view);

        _dimensionsLoadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_dimensionsView)
            .setLoadingView(_dimensionsLoadingView)
            .setNoContentView(_dimensionsNoContentView)
            .setErrorView(_dimensionsLoadingErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long, 0))
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();

        _dimensionsLoadingViewDelegate.showContent();

        return view;
    }

    @Override
    public void commitChanges() {
        final val playgroundId = getPlaygroundId();
        if (playgroundId != null) {

            Integer width = null;
            Integer height = null;
            Integer length = null;

            if (_playgroundWidthView != null) {
                final String text = _playgroundWidthView.getText().toString();
                width = text.isEmpty() ? DEFAULT_MIN_SIZE : Integer.valueOf(text);
            }
            if (_playgroundHeightView != null) {
                final String text = _playgroundHeightView.getText().toString();
                height = text.isEmpty() ? DEFAULT_MIN_SIZE : Integer.valueOf(text);
            }
            if (_playgroundLengthView != null) {
                final String text = _playgroundLengthView.getText().toString();
                length = text.isEmpty() ? DEFAULT_MIN_SIZE : Integer.valueOf(text);
            }

            final val eventArgs =
                new ChangePlaygroundDimensionsEventArgs(playgroundId, width, height, length);
            _changePlaygroundDimensionsEvent.rise(eventArgs);
        }
    }

    @Override
    public int getLabelId() {
        return R.string.playground_editor_dimensions_title;
    }

    @Override
    public void displayDimensionsLoading() {
        final val loadingViewDelegate = getDimensionsLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public void displayDimensionsLoadingError() {
        final val loadingViewDelegate = getDimensionsLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showError();
        }
    }

    @Override
    public void displayPlaygroundDimensions(
        @Nonnull final Dimension dimension) {

        setDimensions(dimension);

        final val width = dimension.getWidth();
        final val height = dimension.getHeight();
        final val length = dimension.getLength();

        if (_playgroundWidthView != null) {
            if (width != null && width != DEFAULT_MIN_SIZE) {
                _playgroundWidthView.setText(String.valueOf(width));
            }
        }
        if (_playgroundHeightView != null) {
            if (height != null && height != DEFAULT_MIN_SIZE) {
                _playgroundHeightView.setText(String.valueOf(height));
            }
        }
        if (_playgroundLengthView != null) {
            if (length != null && length != DEFAULT_MIN_SIZE) {
                _playgroundLengthView.setText(String.valueOf(length));
            }
        }

        final val loadingViewDelegate = getDimensionsLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent();
        }
    }

    @Override
    public void revertChangedDimensions() {

        final val dimension = getDimensions();
        if (dimension != null) {
            displayPlaygroundDimensions(dimension);
        }
    }

    @Override
    @NonNull
    public Event<ChangePlaygroundDimensionsEventArgs> getChangePlaygroundDimensionsEvent() {
        return _changePlaygroundDimensionsEvent;
    }

    @Override
    @NonNull
    public Event<IdEventArgs> getViewPlaygroundDimensionsEvent() {
        return _viewPlaygroundDimensionsEvent;
    }

    @Override
    public void onResume() {
        super.onResume();

        riseViewDimensionsEvent();
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
            riseViewDimensionsEvent();
        }
    }

    @Named(PresenterNames.DIMENSIONS_EDITOR)
    @Inject
    @Nullable
    @Getter(AccessLevel.PROTECTED)
    /*package-private*/ Presenter<PlaygroundDimensionsEditorScreen> _presenter;

    @NonNull
    private final ManagedEvent<ChangePlaygroundDimensionsEventArgs>
        _changePlaygroundDimensionsEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewPlaygroundDimensionsEvent = Events.createEvent();

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private Dimension _dimensions;

    @Nullable
    private View _dimensionsLoadingErrorView;

    @Nullable
    private ProgressBar _dimensionsLoadingView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _dimensionsLoadingViewDelegate;

    @Nullable
    private View _dimensionsNoContentView;

    @Nullable
    private View _dimensionsView;

    @Nullable
    private TextInputEditText _playgroundHeightView;

    @Nullable
    private TextInputEditText _playgroundLengthView;

    @Nullable
    private TextInputEditText _playgroundWidthView;

    private void riseViewDimensionsEvent() {
        final val playgroundId = getPlaygroundId();
        if (playgroundId != null) {
            _viewPlaygroundDimensionsEvent.rise(new IdEventArgs(playgroundId));
        } else {
            final val loadingViewDelegate = getDimensionsLoadingViewDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.showContent(false);
            }
        }
    }
}
