package com.btc.prosport.player.screen.activity.sportComplexViewer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.MenuItemUtils;
import com.btc.prosport.api.ProSportContract;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.adapter.sportComplexViewerPager.SportComplexViewerPageFactory;
import com.btc.prosport.player.core.adapter.sportComplexViewerPager.SportComplexViewerPagerAdapter;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.screen.SportComplexViewerScreen;
import com.btc.prosport.player.screen.activity.BasePlayerNavigationActivity;

import org.apache.commons.lang3.ObjectUtils;
import org.parceler.Parcels;

import java.util.Calendar;
import java.util.Objects;
import java.util.SimpleTimeZone;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class SportComplexViewerActivity extends BasePlayerNavigationActivity
    implements SportComplexViewerScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SportComplexViewerActivity.class);

    private static final String _KEY_SAVED_STATE =
        ConstantBuilder.savedStateKey(SportComplexViewerActivity.class, "saved_state");

    @NonNull
    public static Intent getViewIntent(
        @NonNull final Context context, @NonNull final Uri sportComplexUri) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(sportComplexUri, "sportComplexUri == null");

        final val intent = new Intent(context, SportComplexViewerActivity.class);

        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(sportComplexUri);

        return intent;
    }

    public static void startView(
        @NonNull final Context context, @NonNull final Uri sportComplexUri) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(sportComplexUri, "sportComplexUri == null");

        context.startActivity(getViewIntent(context, sportComplexUri));
    }

    @Override
    @CallSuper
    public void bindViews() {
        super.bindViews();

        _appbarView = (AppBarLayout) findViewById(R.id.appbar);
        _toolbarView = (Toolbar) findViewById(R.id.toolbar);
        _tabsView = (TabLayout) findViewById(R.id.tabs);
        _pagerView = (ViewPager) findViewById(R.id.pager);
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            if (_state == null) {
                _state = new SportComplexViewerState();
            }

            _state.setSportComplexId(getSportComplexId());

            if (_tabsView != null) {
                _state.setActiveTab(_tabsView.getSelectedTabPosition());
            } else {
                _state.setActiveTab(0);
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

    @CallSuper
    @Override
    public void displayChangeSportComplexFavoriteStateError() {
        setSportComplexLoading(false);
    }

    @CallSuper
    @Override
    public void displaySportComplex(@Nullable final SportComplexTitle sportComplex) {
        _sportComplex = sportComplex;

        setSportComplexLoading(false);
    }

    @CallSuper
    @Override
    public void displaySportComplexLoading() {
        setSportComplexLoading(true);
    }

    @CallSuper
    @Override
    public void displaySportComplexLoadingError() {
        _sportComplex = null;

        setSportComplexLoading(false);
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getAddSportComplexToFavoriteEvent() {
        return _addSportComplexToFavoriteEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getRemoveSportComplexFromFavoriteEvent() {
        return _removeSportComplexFromFavoriteEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getViewSportComplexEvent() {
        return _viewSportComplexEvent;
    }

    @CallSuper
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.sport_complex_viewer, menu);

        final val loadingItem = menu.findItem(R.id.action_loading);
        MenuItemCompat.setActionProvider(loadingItem, new LoadingActionProvider(this));

        return true;
    }

    @CallSuper
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final boolean showMenu = super.onPrepareOptionsMenu(menu);

        final val addToFavoritesItem = menu.findItem(R.id.action_add_to_favorites);
        final val loadingItem = menu.findItem(R.id.action_loading);

        final val actionBar = getSupportActionBar();

        if (isSportComplexLoading()) {
            if (actionBar != null) {
                actionBar.setTitle(R.string.sport_complex_viewer_title_loading);
            }

            addToFavoritesItem.setVisible(false);
            loadingItem.setVisible(true);
        } else {
            final val sportComplex = getSportComplex();

            if (actionBar != null) {
                final val title = sportComplex == null ? null : sportComplex.getName();

                actionBar.setTitle(title);
            }

            final val sportComplexFavoriteState =
                sportComplex == null ? null : sportComplex.getFavoriteState();
            if (sportComplexFavoriteState == null) {
                addToFavoritesItem.setVisible(false);
            } else {
                addToFavoritesItem.setVisible(true);

                addToFavoritesItem.setChecked(sportComplexFavoriteState);
            }

            MenuItemUtils.setCheckableItemIcon(addToFavoritesItem,
                                               R.drawable.ic_material_star,
                                               R.drawable.ic_material_star_border);

            loadingItem.setVisible(false);
        }

        return showMenu;
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        boolean consumed = false;

        final int itemId = item.getItemId();
        if (R.id.action_add_to_favorites == itemId) {
            final val sportComplexId = getSportComplexId();
            if (sportComplexId != null) {
                if (item.isChecked()) {
                    _removeSportComplexFromFavoriteEvent.rise(new IdEventArgs(sportComplexId));
                } else {
                    _addSportComplexToFavoriteEvent.rise(new IdEventArgs(sportComplexId));
                }

                consumed = true;
            }
        } else {
            consumed = super.onOptionsItemSelected(item);
        }

        return consumed;
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();

        if (_pagerView != null) {
            // Must be post. Because PagerView may not be ready.
            _pagerView.post(new Runnable() {
                @Override
                public void run() {
                    if (_pagerView != null) {
                        onPageChanged(_pagerView.getCurrentItem());
                    }
                }
            });
        }
    }

    protected final void setSportComplexId(@Nullable final Long sportComplexId) {
        if (!Objects.equals(_sportComplexId, sportComplexId)) {
            _sportComplexId = sportComplexId;

            onSportComplexIdChanged();
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
            setContentView(R.layout.activity_sport_complex_viewer);
            bindViews();

            if (_toolbarView != null) {
                setSupportActionBar(_toolbarView);
            }

            final val actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
            }

            _sportComplexViewerPages = new SportComplexViewerPageFactory();

            _sportComplexViewerPagerAdapter = new SportComplexViewerPagerAdapter(this);

            _sportComplexViewerPagerAdapter.setPageFactory(_sportComplexViewerPages);

            if (_pagerView != null) {
                _pagerView.addOnPageChangeListener(_pageChangeListener);

                _pagerView.setAdapter(_sportComplexViewerPagerAdapter);
            }

            if (_tabsView != null) {
                _tabsView.setupWithViewPager(_pagerView);
                final val activeTabPosition = _state.getActiveTab();
                final val activeTab =
                    _tabsView.getTabAt(ObjectUtils.defaultIfNull(activeTabPosition,
                                                                 SportComplexViewerPageFactory
                                                                     .POSITION_PLAYGROUNDS_VIEWER));
                if (activeTab != null) {
                    activeTab.select();
                }
            }

            setSportComplexId(_state.getSportComplexId());
        } else {
            finish();
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (_pagerView != null) {
            _pagerView.removeOnPageChangeListener(_pageChangeListener);
        }

        unbindViews();
    }

    @Nullable
    @CallSuper
    protected SportComplexViewerState onHandleIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final SportComplexViewerState state;

        final val action = getIntent().getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            state = onHandleViewIntent(intent);
        } else {
            state = null;
        }

        return state;
    }

    @Nullable
    @CallSuper
    protected SportComplexViewerState onHandleSavedState(@NonNull final Bundle savedInstanceState) {
        SportComplexViewerState state = null;

        if (savedInstanceState.containsKey(_KEY_SAVED_STATE)) {
            state = Parcels.unwrap(savedInstanceState.getParcelable(_KEY_SAVED_STATE));
        }

        return state;
    }

    @Nullable
    @CallSuper
    protected SportComplexViewerState onHandleViewIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final SportComplexViewerState state;

        final val data = intent.getData();
        final int code = ProSportContract.getCode(data);

        if (code == ProSportContract.CODE_SPORT_COMPLEX) {
            state = new SportComplexViewerState();
            state.setSportComplexId(ContentUris.parseId(data));
        } else {
            // TODO: 02.02.2017 handle incorrect data. (Toast or snackbar, error view)
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
        supportInvalidateOptionsMenu();

        final val sportComplexViewerPagerAdapter = getSportComplexViewerPagerAdapter();
        if (sportComplexViewerPagerAdapter != null) {
            sportComplexViewerPagerAdapter.notifyActivePageChanged(newPosition);
        }
    }

    @CallSuper
    @Override
    protected void onPlayerChanged() {
        super.onPlayerChanged();

        supportInvalidateOptionsMenu();
    }

    @CallSuper
    protected void onSportComplexIdChanged() {
        final val sportComplexId = getSportComplexId();
        if (sportComplexId != null) {
            _viewSportComplexEvent.rise(new IdEventArgs(sportComplexId));
        }

        final val sportComplexViewerPagerAdapter = getSportComplexViewerPagerAdapter();
        if (sportComplexViewerPagerAdapter != null) {
            sportComplexViewerPagerAdapter.notifySportComplexIdChanged(sportComplexId);
        }
    }

    @CallSuper
    protected void onSportComplexLoadingStateChanged() {
        supportInvalidateOptionsMenu();
    }

    @Named(PresenterNames.SPORT_COMPLEX_VIEWER)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<SportComplexViewerScreen> _presenter;

    @NonNull
    private final ManagedEvent<IdEventArgs> _addSportComplexToFavoriteEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _removeSportComplexFromFavoriteEvent =
        Events.createEvent();

    @Getter
    @NonNull
    private final Calendar _reservationSelectedDateConverter =
        Calendar.getInstance(new SimpleTimeZone(0, "GMT"));

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewSportComplexEvent = Events.createEvent();

    @Nullable
    private AppBarLayout _appbarView;

    @Nullable
    private ViewPager _pagerView;

    @Getter
    @Nullable
    private SportComplexTitle _sportComplex;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private Long _sportComplexId;

    @Getter(AccessLevel.PROTECTED)
    private boolean _sportComplexLoading = false;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private SportComplexViewerPagerAdapter _sportComplexViewerPagerAdapter;

    @NonNull
    private final ViewPager.OnPageChangeListener _pageChangeListener =
        new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                onPageChanged(position);
            }
        };

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private SportComplexViewerPageFactory _sportComplexViewerPages;

    @Nullable
    private SportComplexViewerState _state;

    @Nullable
    private TabLayout _tabsView;

    @Nullable
    private Toolbar _toolbarView;

    private void setSportComplexLoading(final boolean sportComplexLoading) {
        if (_sportComplexLoading != sportComplexLoading) {
            _sportComplexLoading = sportComplexLoading;

            onSportComplexLoadingStateChanged();
        }
    }
}
