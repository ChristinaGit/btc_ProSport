package com.btc.prosport.manager.screen.activity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.utility.tuple.Tuple2;
import com.btc.common.utility.tuple.Tuples;
import com.btc.prosport.api.model.PlaygroundReport;
import com.btc.prosport.api.model.SportComplexReport;
import com.btc.prosport.api.model.User;
import com.btc.prosport.core.utility.UserUtils;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.navigation.NavigationAdapter;
import com.btc.prosport.manager.core.adapter.navigation.NavigationItem;
import com.btc.prosport.manager.screen.ManagerNavigationScreen;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Accessors(prefix = "_")
public abstract class BaseManagerNavigationActivity extends BaseManagerAccountActivity
    implements ManagerNavigationScreen {
    private static final String _KEY_SAVED_STATE =
        ConstantBuilder.savedStateKey(BaseManagerNavigationActivity.class, "saved_state");

    private static final String _LOG_TAG =
        ConstantBuilder.logTag(BaseManagerNavigationActivity.class);

    @Override
    @NonNull
    public final NoticeEvent getViewManagerEvent() {
        return _viewManagerEvent;
    }

    @Override
    public void displayChangedPlayground(@NonNull final PlaygroundReport playground) {
        Contracts.requireNonNull(playground, "playground == null");

        final val navigationAdapter = getNavigationAdapter();
        if (navigationAdapter != null) {
            //@formatter:off
            navigationAdapter.changeNotConfirmedOrdersCount(
                playground.getId(),
                playground.getNotConfirmedOrdersCount());
            //@formatter:on
        }
    }

    @CallSuper
    @Override
    public void displaySportComplexes(@Nullable final List<SportComplexReport> sportComplexes) {
        _sportComplexes = sportComplexes;

        final val navigationAdapter = getNavigationAdapter();
        if (navigationAdapter != null) {
            navigationAdapter.setSportComplexesLoading(false);
            navigationAdapter.setSportComplexes(sportComplexes);

            if (sportComplexes != null && !sportComplexes.isEmpty()) {
                final val selectedPlayground = getPlaygroundSelection().get1();
                if (selectedPlayground == null) {
                    Long firstPlaygroundId = null;

                    for (final val sportComplex : sportComplexes) {
                        final val playgrounds = sportComplex.getPlaygrounds();
                        if (playgrounds != null && !playgrounds.isEmpty()) {
                            firstPlaygroundId = playgrounds.get(0).getId();
                            break;
                        }
                    }

                    setSelectedPlaygroundId(firstPlaygroundId);
                }
            }

            boolean allCollapsed = false;
            List<Integer> expandedGroups = null;
            if (_state != null) {
                if (_state.isAllCollapsed()) {
                    allCollapsed = true;
                } else if (sportComplexes != null) {
                    final val expandedSportComplexesIds = _state.getExpandedSportComplexesIds();
                    if (expandedSportComplexesIds != null && !expandedSportComplexesIds.isEmpty()) {
                        final val sportComplexToId = new Transformer<SportComplexReport, Long>() {
                            @Override
                            public Long transform(final SportComplexReport sportComplex) {
                                return sportComplex.getId();
                            }
                        };
                        final val sportComplexesIds = new ArrayList<Long>();
                        CollectionUtils.collect(sportComplexes,
                                                sportComplexToId,
                                                sportComplexesIds);

                        expandedGroups = new ArrayList<>();

                        for (final val expandedSportComplexId : expandedSportComplexesIds) {
                            final val groupIndex =
                                sportComplexesIds.indexOf(expandedSportComplexId);
                            if (groupIndex >= 0) {
                                expandedGroups.add(groupIndex);
                            }
                        }
                    }
                }
            }
            if (expandedGroups != null) {
                for (final val expandedGroup : expandedGroups) {
                    navigationAdapter.expandGroup(expandedGroup);
                }
            } else {
                if (!allCollapsed) {
                    navigationAdapter.expandAllGroups();
                } else {
                    navigationAdapter.collapseAllGroups();
                }
            }
        }
    }

    @CallSuper
    @Override
    public void displaySportComplexesLoading() {
        final val navigationAdapter = getNavigationAdapter();
        if (navigationAdapter != null) {
            navigationAdapter.setSportComplexes(null);
            navigationAdapter.setSportComplexesLoading(true);
        }
    }

    @Override
    public final void displaySportComplexesLoadingError() {
    }

    @NonNull
    @Override
    public final NoticeEvent getChangeManagerAvatarEvent() {
        return _changeManagerAvatarEvent;
    }

    @Override
    @NonNull
    public final ManagedNoticeEvent getCreatePlaygroundEvent() {
        return _createPlaygroundEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getEditPlaygroundPricesEvent() {
        return _editPlaygroundPricesEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewFeedbackEvent() {
        return _viewFeedbackEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewHomeScreenEvent() {
        return _viewHomeScreenEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewManagerOrdersEvent() {
        return _viewManagerOrdersEvent;
    }

    @Override
    @NonNull
    public final Event<IdEventArgs> getViewManagerPlaygroundEvent() {
        return _viewPlaygroundEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewManagerPricesEvent() {
        return _viewManagerPricesEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getViewManagerSettingsEvent() {
        return _viewManagerSettingsEvent;
    }

    @Override
    @NonNull
    public final NoticeEvent getViewManagerSportComplexesEvent() {
        return _viewManagerSportComplexesEvent;
    }

    @CallSuper
    @Override
    public void displayManager(@Nullable final User manager) {
        super.displayManager(manager);

        setupManagerAvatar();
        setupManagerInfo();

        final val loadingViewDelegate = getManagerLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(manager != null);
        }
    }

    @CallSuper
    @Override
    public void displayManagerLoading() {
        super.displayManagerLoading();

        final val loadingViewDelegate = getManagerLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @CallSuper
    @Override
    public void displayManagerLoadingError() {
        super.displayManagerLoadingError();

        final val loadingViewDelegate = getManagerLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showError();
        }
    }

    @CallSuper
    @Override
    public void displayNoAccountsError() {
        super.displayNoAccountsError();

        final val loadingViewDelegate = getManagerLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(false);
        }
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final boolean consumed;

        if (_actionBarDrawerToggle != null && _actionBarDrawerToggle.onOptionsItemSelected(item)) {
            consumed = true;
        } else {
            consumed = super.onOptionsItemSelected(item);
        }

        return consumed;
    }

    @NonNull
    protected Tuple2<SportComplexReport, PlaygroundReport> getPlaygroundSelection() {
        SportComplexReport selectedSportComplex = null;
        PlaygroundReport selectedPlayground = null;

        final val navigationAdapter = getNavigationAdapter();
        if (navigationAdapter != null) {
            final val sportComplexes = navigationAdapter.getSportComplexes();
            final val selectedPlaygroundId = navigationAdapter.getSelectedPlaygroundId();
            if (selectedPlaygroundId != null && sportComplexes != null) {
                for (final val sportComplex : sportComplexes) {
                    final val playgrounds = sportComplex.getPlaygrounds();

                    if (playgrounds != null) {
                        for (final val playground : playgrounds) {
                            if (selectedPlaygroundId == playground.getId()) {
                                selectedSportComplex = sportComplex;
                                selectedPlayground = playground;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return Tuples.from(selectedSportComplex, selectedPlayground);
    }

    protected boolean onNavigateToPlayground(final long id) {
        return false;
    }

    @CallSuper
    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (_actionBarDrawerToggle != null) {
            _actionBarDrawerToggle.syncState();
        }
        if (_managerLoadingViewDelegate != null) {
            _managerLoadingViewDelegate.hideAll();
        }
    }

    @CallSuper
    @Override
    public void setContentView(@LayoutRes final int layoutResID) {
        super.setContentView(R.layout.activity_manager_navigation);

        findNavigationViews();

        if (_navigationContainerView != null) {
            final val inflater = getLayoutInflater();
            final val view = inflater.inflate(layoutResID, _navigationContainerView, false);

            setNavigationContentView(view, null);
        }

        initializeNavigationView();
        initializeNavigationContainerView();
    }

    @CallSuper
    @Override
    public void setContentView(final View view) {
        super.setContentView(R.layout.activity_manager_navigation);

        findNavigationViews();

        setNavigationContentView(view, null);

        initializeNavigationView();
        initializeNavigationContainerView();
    }

    @CallSuper
    @Override
    public void setContentView(final View view, final ViewGroup.LayoutParams params) {
        super.setContentView(R.layout.activity_manager_navigation);

        findNavigationViews();

        setNavigationContentView(view, params);

        initializeNavigationView();
        initializeNavigationContainerView();
    }

    @CallSuper
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (_actionBarDrawerToggle != null) {
            _actionBarDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();

        _viewManagerSportComplexesEvent.rise();
        _viewManagerEvent.rise();
    }

    @CallSuper
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            _state = onHandleSavedState(savedInstanceState);
        }

        _navigationAdapter = new NavigationAdapter();
        _navigationAdapter.setSelectedPlaygroundId(getSelectedPlaygroundId());

        // FIXME: 11.05.2017 Add add_playground and edit_prices menu items
        final val headerNavigationItems = _navigationAdapter.getHeaderNavigationItems();
        headerNavigationItems.add(new NavigationItem(R.drawable.ic_material_edit,
                                                     R.string.manager_navigation_edit_prices,
                                                     _navigateEditPrices));

        final val footerNavigationItems = _navigationAdapter.getFooterNavigationItems();
        footerNavigationItems.add(new NavigationItem(R.drawable.ic_material_settings,
                                                     R.string.manager_navigation_settings,
                                                     _navigateToSettings));
        footerNavigationItems.add(new NavigationItem(R.drawable.ic_material_feedback,
                                                     R.string.manager_navigation_feedback,
                                                     _navigateToFeedback));

        final val navigationAdapter = getNavigationAdapter();
        if (navigationAdapter != null) {
            navigationAdapter.getOnPlaygroundClickEvent().addHandler(_onPlaygroundClickHandler);
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();

        final val navigationAdapter = getNavigationAdapter();
        if (navigationAdapter != null) {
            navigationAdapter.getOnPlaygroundClickEvent().addHandler(_onPlaygroundClickHandler);
        }
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            if (_state == null) {
                _state = new State();
            }

            final val expandedSportComplexesIds = new ArrayList<Long>();
            boolean allCollapsed = false;

            final val navigationAdapter = getNavigationAdapter();
            if (navigationAdapter != null) {
                if (navigationAdapter.getExpandedGroups().isEmpty()) {
                    allCollapsed = true;
                } else {
                    final val groupCount = navigationAdapter.getGroupParentCount();
                    for (final val groupParentIndex : navigationAdapter.getExpandedGroups()) {
                        if (groupParentIndex < groupCount) {
                            final val sportComplex =
                                navigationAdapter.getGroupParent(groupParentIndex);
                            expandedSportComplexesIds.add(sportComplex.getId());
                        }
                    }
                }
            }

            _state.setExpandedSportComplexesIds(expandedSportComplexesIds);
            _state.setAllCollapsed(allCollapsed);

            outState.putParcelable(_KEY_SAVED_STATE, Parcels.wrap(_state));
        }
    }

    @CallSuper
    protected void onSelectedPlaygroundIdChanged() {
        final val navigationAdapter = getNavigationAdapter();
        if (navigationAdapter != null) {
            navigationAdapter.setSelectedPlaygroundId(getSelectedPlaygroundId());
        }
    }

    protected void setSelectedPlaygroundId(@Nullable final Long selectedPlaygroundId) {
        if (!Objects.equals(_selectedPlaygroundId, selectedPlaygroundId)) {
            _selectedPlaygroundId = selectedPlaygroundId;

            onSelectedPlaygroundIdChanged();
        }
    }

    protected void setTitle(
        @Nullable final SportComplexReport sportComplex,
        @Nullable final PlaygroundReport playground) {
        final val sportComplexName = sportComplex == null ? null : sportComplex.getName();
        if (!TextUtils.isEmpty(sportComplexName)) {
            setTitle(sportComplexName);
        } else {
            setTitle(R.string.application_manager_label);
        }

        final val playgroundName = playground == null ? null : playground.getName();
        setSubtitle(playgroundName);
    }

    @NonNull
    private final ManagedNoticeEvent _changeManagerAvatarEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _createPlaygroundEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _editPlaygroundPricesEvent = Events.createEvent();

    @NonNull
    private final View.OnClickListener _onAvatarViewClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            _changeManagerAvatarEvent.rise();
        }
    };

    @NonNull
    private final ManagedNoticeEvent _viewFeedbackEvent = Events.createNoticeEvent();

    @NonNull
    private final Runnable _navigateToFeedback = new Runnable() {
        @Override
        public void run() {
            _viewFeedbackEvent.rise();
        }
    };

    @NonNull
    private final ManagedNoticeEvent _viewHomeScreenEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _viewManagerEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _viewManagerOrdersEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _viewManagerPricesEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedNoticeEvent _viewManagerSettingsEvent = Events.createNoticeEvent();

    @NonNull
    private final Runnable _navigateToSettings = new Runnable() {
        @Override
        public void run() {
            _viewManagerSettingsEvent.rise();
        }
    };

    @NonNull
    private final ManagedNoticeEvent _viewManagerSportComplexesEvent = Events.createNoticeEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewPlaygroundEvent = Events.createEvent();

    @Nullable
    private ActionBarDrawerToggle _actionBarDrawerToggle;

    private boolean _hasContentView = false;

    @Nullable
    private ImageView _managerAvatarView;

    @Nullable
    private View _managerInfoContainerView;

    @Nullable
    private View _managerLoadErrorView;

    @Nullable
    private ContentLoaderProgressBar _managerLoadingView;

    @Getter(AccessLevel.PRIVATE)
    @Nullable
    private LoadingViewDelegate _managerLoadingViewDelegate;

    @Nullable
    private TextView _managerNameView;

    @Nullable
    private TextView _managerPhoneNumberView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private NavigationAdapter _navigationAdapter;

    @Nullable
    private DrawerLayout _navigationContainerView;

    @Nullable
    private RecyclerView _navigationListView;

    @Nullable
    private NavigationView _navigationView;

    @Getter
    @Nullable
    private Long _selectedPlaygroundId;

    @NonNull
    private final Runnable _navigateEditPrices = new Runnable() {
        @Override
        public void run() {
            final val playgroundId = getSelectedPlaygroundId();
            if (playgroundId != null) {
                _editPlaygroundPricesEvent.rise(new IdEventArgs(playgroundId));
            }
        }
    };

    @NonNull
    private final EventHandler<IdEventArgs> _onPlaygroundClickHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                setSelectedPlaygroundId(eventArgs.getId());

                if (!onNavigateToPlayground(eventArgs.getId())) {
                    _viewPlaygroundEvent.rise(eventArgs);
                } else {
                    if (_navigationContainerView != null) {
                        _navigationContainerView.closeDrawers();
                    }
                }
            }
        };

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private List<SportComplexReport> _sportComplexes;

    @Nullable
    private State _state;

    private void findNavigationViews() {
        _navigationContainerView = (DrawerLayout) findViewById(R.id.navigation_container);
        _navigationView = (NavigationView) findViewById(R.id.navigation);
    }

    private void initializeNavigationContainerView() {
        if (_navigationContainerView != null) {
            _actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                                                               _navigationContainerView,
                                                               R.string.action_open_drawer,
                                                               R.string.action_close_drawer) {
                @Override
                public void onDrawerOpened(final View drawerView) {
                    super.onDrawerOpened(drawerView);
                    _viewManagerEvent.rise();
                }

                @Override
                public void onDrawerClosed(final View drawerView) {
                    super.onDrawerClosed(drawerView);

                    final val loadingViewDelegate = getManagerLoadingViewDelegate();
                    if (loadingViewDelegate != null) {
                        loadingViewDelegate.hideAll();
                    }
                }
            };
            // TODO: 18.04.2017 Fix action bar drawer toggle style
            _actionBarDrawerToggle.getDrawerArrowDrawable().setColor(Color.WHITE);
            _actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            _navigationContainerView.addDrawerListener(_actionBarDrawerToggle);
        }
    }

    private void initializeNavigationView() {
        if (_navigationView != null) {
            _managerAvatarView = (ImageView) _navigationView.findViewById(R.id.manager_avatar);
            _managerNameView = (TextView) _navigationView.findViewById(R.id.manager_name);
            _managerPhoneNumberView =
                (TextView) _navigationView.findViewById(R.id.manager_phone_number);
            _managerLoadingView =
                (ContentLoaderProgressBar) _navigationView.findViewById(R.id.manager_loading);
            _managerInfoContainerView = _navigationView.findViewById(R.id.manager_info_container);
            _managerLoadErrorView = _navigationView.findViewById(R.id.manager_load_error);

            //@formatter:off
            _managerLoadingViewDelegate = LoadingViewDelegate
                .builder()
                .setContentView(_managerInfoContainerView)
                .setLoadingView(_managerLoadingView)
                .setErrorView(_managerLoadErrorView)
                .setContentVisibilityHandler(new FadeVisibilityHandler(View.INVISIBLE,
                                                                       R.anim.fade_in_long,
                                                                       FadeVisibilityHandler.NO_ANIMATION))
                .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
                .build();
            //@formatter:on

            _managerLoadingViewDelegate.hideAll();

            _navigationListView = (RecyclerView) _navigationView.findViewById(R.id.navigation_list);

            if (_navigationListView != null) {
                final val layoutManager = new LinearLayoutManager(this);

                _navigationListView.setLayoutManager(layoutManager);
                _navigationListView.setAdapter(getNavigationAdapter());
            }
            if (_managerAvatarView != null) {
                _managerAvatarView.setOnClickListener(_onAvatarViewClick);
            }
        }
    }

    @Nullable
    @CallSuper
    private State onHandleSavedState(@NonNull final Bundle savedInstanceState) {
        State state = null;

        if (savedInstanceState.containsKey(_KEY_SAVED_STATE)) {
            state = Parcels.unwrap(savedInstanceState.getParcelable(_KEY_SAVED_STATE));
        }

        return state;
    }

    private void setNavigationContentView(
        @NonNull final View view, @Nullable final ViewGroup.LayoutParams params) {
        Contracts.requireNonNull(view, "view == null");

        if (_navigationContainerView != null) {
            if (_hasContentView) {
                _navigationContainerView.removeViewAt(0);
            }

            if (params == null) {
                _navigationContainerView.addView(view, 0);
            } else {
                _navigationContainerView.addView(view, 0, params);
            }

            _hasContentView = true;
        }
    }

    private void setupManagerAvatar() {
        final val manager = getManager();
        if (_managerAvatarView != null) {
            final val managerAvatarUri = manager == null ? null : manager.getAvatarUri();

            Glide
                .with(this)
                .load(managerAvatarUri)
                .asBitmap()
                .error(R.drawable.ic_material_account_circle)
                .fallback(R.drawable.ic_material_account_circle)
                .placeholder(R.drawable.ic_material_account_circle)
                .centerCrop()
                .into(new BitmapImageViewTarget(_managerAvatarView) {
                    @Override
                    protected void setResource(final Bitmap resource) {
                        final val avatar =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                        avatar.setCircular(true);
                        getView().setImageDrawable(avatar);
                    }
                });
        }
    }

    private void setupManagerInfo() {
        final val manager = getManager();

        if (_managerNameView != null) {
            final val userName = manager != null ? UserUtils.getDisplayUserName(manager) : null;
            if (!TextUtils.isEmpty(userName)) {
                _managerNameView.setVisibility(View.VISIBLE);
            } else {
                _managerNameView.setVisibility(View.GONE);
            }
            _managerNameView.setText(userName);
        }

        if (_managerPhoneNumberView != null) {
            _managerPhoneNumberView.setText(manager != null ? manager.getPhoneNumber() : null);
        }
    }

    @Parcel
    @Accessors(prefix = "_")
    /*package-private*/ static final class State {
        @Getter
        @Setter
        /*package-private*/ boolean _allCollapsed;

        @Getter
        @Setter
        @Nullable
        /*package-private*/ List<Long> _expandedSportComplexesIds;
    }
}
