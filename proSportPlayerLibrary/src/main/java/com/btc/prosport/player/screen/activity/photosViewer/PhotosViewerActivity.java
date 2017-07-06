package com.btc.prosport.player.screen.activity.photosViewer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
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
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.api.ProSportContract;
import com.btc.prosport.api.model.Photo;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.adapter.photosPager.PhotosPagerAdapter;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.screen.PhotosViewerScreen;
import com.btc.prosport.player.screen.activity.BasePlayerActivity;

import org.parceler.Parcels;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class PhotosViewerActivity extends BasePlayerActivity implements PhotosViewerScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(PhotosViewerActivity.class);

    private static final String EXTRA_POSITION = "position";

    private static final String _KEY_SAVED_STATE =
        ConstantBuilder.savedStateKey(PhotosViewerActivity.class, "saved_state");

    @NonNull
    public static Intent getViewIntent(
        @NonNull final Context context, @NonNull final Uri uri, final int position) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(uri, "uri == null");

        final val intent = new Intent(context, PhotosViewerActivity.class);

        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.putExtra(EXTRA_POSITION, position);

        return intent;
    }

    public static void startView(
        @NonNull final Context context, @NonNull final Uri uri, final int position) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(uri, "uri == null");

        context.startActivity(getViewIntent(context, uri, position));
    }

    @CallSuper
    @Override
    public void bindViews() {
        super.bindViews();

        _photosView = (ViewPager) findViewById(R.id.pager);
        _toolbarView = (Toolbar) findViewById(R.id.toolbar);
        _loadingView = (ProgressBar) findViewById(R.id.progress_bar);
        _errorView = (TextView) findViewById(R.id.error_view);
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            if (_state == null) {
                _state = new PhotosViewerState();
            }

            _state.setPlaygroundId(getPlaygroundId());
            if (_photosView != null) {
                _state.setPosition(_photosView.getCurrentItem());
            }
            outState.putParcelable(_KEY_SAVED_STATE, Parcels.wrap(_state));
        }
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
    public void displayPhotosLoading() {
        final val loadingViewDelegate = getPhotosLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public final void displayPhotosLoadingError() {
        final val loadingViewDelegate = getPhotosLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showError();
        }
    }

    @Override
    public final void displayPlaygroundPhotos(
        @Nullable final PlaygroundTitle playground, @Nullable final List<Photo> photos) {
        setupPhotos(photos);

        final val loadingViewDelegate = getPhotosLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(photos != null && !photos.isEmpty());
        }
    }

    @Override
    public final void displaySportComplexPhotos(
        @Nullable final SportComplexTitle sportComplex, @Nullable final List<Photo> photos) {
        setupPhotos(photos);

        final val loadingViewDelegate = getPhotosLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(photos != null && !photos.isEmpty());
        }
    }

    @NonNull
    @Override
    public Event<IdEventArgs> getViewPlaygroundPhotosEvent() {
        return _viewPlaygroundPhotosEvent;
    }

    @NonNull
    @Override
    public Event<IdEventArgs> getViewSportComplexPhotosEvent() {
        return _viewSportComplexPhotosEvent;
    }

    protected final void setPlaygroundId(@Nullable final Long playgroundId) {
        if (!Objects.equals(_playgroundId, playgroundId)) {
            _playgroundId = playgroundId;

            onPlaygroundIdChanged();
        }
    }

    protected final void setSportComplexId(@Nullable final Long sportComplexId) {
        if (!Objects.equals(_sportComplexId, sportComplexId)) {
            _sportComplexId = sportComplexId;

            onSportComplexIdChanged();
        }
    }

    protected final void setupPhotos(@Nullable final List<Photo> photos) {
        final val adapter = getPhotosPagerAdapter();
        if (adapter != null) {
            final val adapterPhotosUris = adapter.getPhotosUris();
            adapterPhotosUris.clear();
            if (photos != null && !photos.isEmpty()) {
                for (final val photo : photos) {
                    adapterPhotosUris.add(photo.getUri());
                }

                adapter.notifyDataSetChanged();

                if (_photosView != null) {
                    if (_state != null) {
                        _photosView.setCurrentItem(_state.getPosition());
                    }
                    onPageChanged(_photosView.getCurrentItem());
                }
            }
        }
    }

    @Nullable
    @CallSuper
    protected PhotosViewerState onHandleIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final PhotosViewerState state;

        final val action = getIntent().getAction();
        switch (action) {
            case Intent.ACTION_VIEW: {
                state = onHandleViewIntent(intent);
                break;
            }
            default: {
                state = null;
                break;
            }
        }

        return state;
    }

    @Nullable
    @CallSuper
    protected PhotosViewerState onHandleSavedState(
        @NonNull final Bundle savedInstanceState) {
        PhotosViewerState state = null;

        if (savedInstanceState.containsKey(_KEY_SAVED_STATE)) {
            state = Parcels.unwrap(savedInstanceState.getParcelable(_KEY_SAVED_STATE));
        }

        return state;
    }

    @Nullable
    @CallSuper
    protected PhotosViewerState onHandleViewIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final PhotosViewerState state;

        final val data = intent.getData();
        final int code = ProSportContract.getCode(data);

        if (code == ProSportContract.CODE_PLAYGROUND) {
            state = new PhotosViewerState();
            state.setPlaygroundId(ContentUris.parseId(data));
            state.setPosition(intent.getIntExtra(EXTRA_POSITION, 0));
        } else if (code == ProSportContract.CODE_SPORT_COMPLEX) {
            state = new PhotosViewerState();
            state.setSportComplexId(ContentUris.parseId(data));
            state.setPosition(intent.getIntExtra(EXTRA_POSITION, 0));
        } else {
            // TODO: handle incorrect data. (Toast or snackbar, error view)
            state = null;
        }

        return state;
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getPlayerScreenComponent().inject(this);

        final val presenter = getPresenter();

        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    protected void onPageChanged(final int newPosition) {
        final val actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (_photosPagerAdapter != null && _photosPagerAdapter.getCount() > 0) {
                actionBar.setTitle(getString(R.string.playground_photos_viewer_title,
                                             newPosition + 1,
                                             _photosPagerAdapter.getCount()));
            }
        }
    }

    @CallSuper
    protected void onPlaygroundIdChanged() {
        final Long playgroundId = getPlaygroundId();
        if (playgroundId != null) {
            _viewPlaygroundPhotosEvent.rise(new IdEventArgs(playgroundId));
        } else {
            final val loadingViewDelegate = getPhotosLoadingViewDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.showContent(false);
            }
        }
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();

        if (_photosView != null) {
            _photosView.post(new Runnable() {
                @Override
                public void run() {
                    if (_photosView != null) {
                        onPageChanged(_photosView.getCurrentItem());
                    }
                }
            });
        }

        final val playgroundId = getPlaygroundId();
        final val sportComplexId = getSportComplexId();
        if (playgroundId != null) {
            _viewPlaygroundPhotosEvent.rise(new IdEventArgs(playgroundId));
        } else if (sportComplexId != null) {
            _viewSportComplexPhotosEvent.rise(new IdEventArgs(sportComplexId));
        }
    }

    @CallSuper
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            _state = onHandleSavedState(savedInstanceState);
        }

        if (_state == null) {
            _state = onHandleIntent(getIntent());
        }

        if (_state != null) {
            setContentView(R.layout.activity_photos_viewer);
            bindViews();

            if (_toolbarView != null) {
                setSupportActionBar(_toolbarView);
                final val actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setTitle(null);
                }
                _toolbarView.setNavigationOnClickListener(_navigationOnClickListener);
            }

            _photosPagerAdapter = new PhotosPagerAdapter();

            if (_photosView != null) {
                _photosView.addOnPageChangeListener(_pageChangeListener);

                _photosView.setAdapter(_photosPagerAdapter);
                _photosView.setCurrentItem(_state.getPosition());
            }

            setPlaygroundId(_state.getPlaygroundId());
            setSportComplexId(_state.getSportComplexId());

            //@formatter:off
            _photosLoadingViewDelegate = LoadingViewDelegate
                .builder()
                .setContentView(_photosView)
                .setLoadingView(_loadingView)
                .setErrorView(_errorView)
                .setContentVisibilityHandler(new FadeVisibilityHandler(
                    R.anim.fade_in_long,
                    FadeVisibilityHandler.NO_ANIMATION))
                .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
                .build();
            //@formatter:on
        } else {
            finish();
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (_photosView != null) {
            _photosView.removeOnPageChangeListener(_pageChangeListener);
        }

        unbindViews();
    }

    @CallSuper
    protected void onSportComplexIdChanged() {
        final val sportComplexId = getSportComplexId();
        if (sportComplexId != null) {
            _viewSportComplexPhotosEvent.rise(new IdEventArgs(sportComplexId));
        } else {
            final val loadingViewDelegate = getPhotosLoadingViewDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.showContent(false);
            }
        }
    }

    @Named(PresenterNames.PHOTOS_VIEWER)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<PhotosViewerScreen> _presenter;

    @NonNull
    private final View.OnClickListener _navigationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewPlaygroundPhotosEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewSportComplexPhotosEvent = Events.createEvent();

    @Nullable
    private TextView _errorView;

    @Nullable
    private ProgressBar _loadingView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _photosLoadingViewDelegate;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private PhotosPagerAdapter _photosPagerAdapter;

    @NonNull
    private final ViewPager.OnPageChangeListener _pageChangeListener =
        new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                onPageChanged(position);
            }
        };

    @Nullable
    private ViewPager _photosView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private Long _playgroundId;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private Long _sportComplexId;

    @Nullable
    private PhotosViewerState _state;

    @Nullable
    private Toolbar _toolbarView;
}
