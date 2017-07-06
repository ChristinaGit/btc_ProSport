package com.btc.prosport.player.screen.activity.sportComplexesViewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.AnimationViewUtils;
import com.btc.common.utility.ImeUtils;
import com.btc.common.utility.listener.SimpleTextWatcher;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.SportComplexesFilter;
import com.btc.prosport.player.core.SportComplexesSortOrder;
import com.btc.prosport.player.core.adapter.sportComplexesFilterAdapter.SportComplexesFilterAdapter;
import com.btc.prosport.player.core.adapter.sportComplexesViewerPager
    .SportComplexesViewerPageFactory;
import com.btc.prosport.player.core.adapter.sportComplexesViewerPager
    .SportComplexesViewerPagerAdapter;
import com.btc.prosport.player.core.eventArgs.SelectSearchTimeEventArgs;
import com.btc.prosport.player.core.manager.render.SportComplexInfoRenderer;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.di.screen.module.PlayerRendererScreenModule;
import com.btc.prosport.player.screen.SportComplexesViewerScreen;
import com.btc.prosport.player.screen.activity.BasePlayerNavigationActivity;
import com.btc.prosport.player.screen.fragment.SportComplexInfoFragment;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.SimpleTimeZone;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class SportComplexesViewerActivity extends BasePlayerNavigationActivity
    implements SportComplexesViewerScreen, SportComplexInfoRenderer {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(SportComplexesViewerActivity.class);

    private static final String _KEY_SAVED_STATE =
        ConstantBuilder.savedStateKey(SportComplexesViewerActivity.class, "saved_state");

    @NonNull
    public static Intent getIntent(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        final val intent = new Intent(context, SportComplexesViewerActivity.class);

        intent.setAction(Intent.ACTION_MAIN);

        return intent;
    }

    public static void start(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        context.startActivity(getIntent(context));
    }

    @Override
    public final void renderSportComplexInfo(final long sportComplexId) {
        final val sportComplexInfoFragment = getSportComplexInfoFragment();
        if (sportComplexInfoFragment != null) {
            sportComplexInfoFragment.setSportComplexId(sportComplexId);

            final val sportComplexInfoBehavior = getSportComplexInfoBehavior();
            if (sportComplexInfoBehavior != null) {
                sportComplexInfoBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    @Override
    @CallSuper
    public void bindViews() {
        super.bindViews();

        _pagerView = (ViewPager) findViewById(R.id.pager);
        _toolbarView = (Toolbar) findViewById(R.id.toolbar);
        _tabsView = (TabLayout) findViewById(R.id.tabs);
        _appbarView = (AppBarLayout) findViewById(R.id.appbar);

        _sportComplexesFilterView = (Spinner) findViewById(R.id.sport_complexes_filter);

        _searchContainerView = findViewById(R.id.sport_complexes_search_container);
        _searchStartTimeView = (TextView) findViewById(R.id.sport_complexes_search_start_time);
        _searchEndTimeView = (TextView) findViewById(R.id.sport_complexes_search_end_time);
        _searchDateView = (TextView) findViewById(R.id.sport_complexes_search_date);
        _searchNameView = (EditText) findViewById(R.id.sport_complexes_search_name);

        _clearSearchStartTimeView = findViewById(R.id.sport_complexes_clear_search_start_time);
        _clearSearchEndTimeView = findViewById(R.id.sport_complexes_clear_search_end_time);
        _clearSearchDateView = findViewById(R.id.sport_complexes_clear_search_date);
        _clearSearchNameView = findViewById(R.id.sport_complexes_clear_search_name);

        _sportComplexInfoContainerView = findViewById(R.id.sport_complex_info_container);
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            if (_state == null) {
                _state = new SportComplexesViewerState();
            }

            _state.setStartTimeSearchParam(getStartTimeSearchParam());
            _state.setEndTimeSearchParam(getEndTimeSearchParam());
            _state.setDateSearchParam(getDateSearchParam());
            _state.setNameSearchParam(getNameSearchParam());
            _state.setSearchExpanded(isSearchExpanded());

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

    @Override
    public void displaySelectedSportComplexesSearchDateParam(
        final int year, final int month, final int dayOfMonth) {
        _searchDateConverter.clear();
        _searchDateConverter.set(year, month, dayOfMonth);

        setDateSearchParam(_searchDateConverter.getTimeInMillis());
    }

    @Override
    public void displaySelectedSportComplexesEndTimeSearchParam(
        final int hourOfDay, final int minute) {
        _searchDateConverter.clear();

        _searchDateConverter.set(Calendar.HOUR_OF_DAY, hourOfDay);
        _searchDateConverter.set(Calendar.MINUTE, minute);

        setEndTimeSearchParam(_searchDateConverter.getTimeInMillis());
    }

    @Override
    public void displaySelectedSportComplexesStartTimeSearchParam(
        final int hourOfDay, final int minute) {
        _searchDateConverter.clear();

        _searchDateConverter.set(Calendar.HOUR_OF_DAY, hourOfDay);
        _searchDateConverter.set(Calendar.MINUTE, minute);

        setStartTimeSearchParam(_searchDateConverter.getTimeInMillis());
    }

    @Override
    @NonNull
    public final NoticeEvent getSelectSportComplexesSearchDateEvent() {
        return _selectSportComplexesSearchDateEvent;
    }

    @Override
    @NonNull
    public final Event<SelectSearchTimeEventArgs> getSelectSportComplexesSearchTimeEvent() {
        return _selectSportComplexesSearchTimeEvent;
    }

    @Override
    @NonNull
    public final Event<IdEventArgs> getViewSportComplexDetailsEvent() {
        return _viewSportComplexDetailsEvent;
    }

    @CallSuper
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.sport_complexes_viewer, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final boolean showMenu = super.onPrepareOptionsMenu(menu);

        final boolean sortVisible;

        if (_pagerView != null && _pagerView.getCurrentItem() ==
                                  SportComplexesViewerPageFactory.POSITION_SPORT_COMPLEXES_MAP) {
            sortVisible = false;
        } else {
            sortVisible = true;
        }

        menu.findItem(R.id.action_sort).setVisible(sortVisible);

        final val sportComplexesSortOrder = getSportComplexesSortOrder();
        switch (sportComplexesSortOrder) {
            case PRICE_ASCENDING:
                menu.findItem(R.id.action_sort_price_ascending).setChecked(true);
                break;
            case PRICE_DESCENDING:
                menu.findItem(R.id.action_sort_price_descending).setChecked(true);
                break;
            case NAME:
                menu.findItem(R.id.action_sort_name).setChecked(true);
                break;
            case DISTANCE:
                menu.findItem(R.id.action_sort_distance).setChecked(true);
                break;
        }

        return showMenu;
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        boolean consumed = false;

        final int itemId = item.getItemId();
        if (R.id.action_search == itemId) {
            setSearchExpanded(!isSearchExpanded(), true);

            consumed = true;
        } else if (R.id.action_sort_price_ascending == itemId) {
            setSportComplexesSortOrder(SportComplexesSortOrder.PRICE_ASCENDING);
        } else if (R.id.action_sort_price_descending == itemId) {
            setSportComplexesSortOrder(SportComplexesSortOrder.PRICE_DESCENDING);
        } else if (R.id.action_sort_distance == itemId) {
            setSportComplexesSortOrder(SportComplexesSortOrder.DISTANCE);
        } else if (R.id.action_sort_name == itemId) {
            setSportComplexesSortOrder(SportComplexesSortOrder.NAME);
        } else {
            consumed = super.onOptionsItemSelected(item);
        }

        return consumed;
    }

    @Override
    protected boolean onNavigateToHomeScreen() {
        return true;
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

    @Nullable
    protected final SportComplexInfoFragment getSportComplexInfoFragment() {
        final SportComplexInfoFragment sportComplexInfoFragment;

        final val fragment =
            getSupportFragmentManager().findFragmentById(R.id.sport_complexes_info_fragment);
        if (fragment instanceof SportComplexInfoFragment) {
            sportComplexInfoFragment = (SportComplexInfoFragment) fragment;
        } else {
            sportComplexInfoFragment = null;
        }

        return sportComplexInfoFragment;
    }

    @Nullable
    protected final SportComplexesFilter getSportComplexesFilter() {
        SportComplexesFilter sportComplexesFilter = null;

        final int position;

        final val sportComplexesFilterAdapter = getSportComplexesFilterAdapter();
        if (_sportComplexesFilterView != null && sportComplexesFilterAdapter != null) {
            position = _sportComplexesFilterView.getSelectedItemPosition();
            sportComplexesFilter = sportComplexesFilterAdapter.getItem(position);
        }

        return sportComplexesFilter;
    }

    protected void setSportComplexesFilter(
        @Nullable final SportComplexesFilter sportComplexesFilter) {
        int position = -1;

        final val sportComplexesFilterAdapter = getSportComplexesFilterAdapter();
        if (sportComplexesFilter != null) {
            if (sportComplexesFilterAdapter != null) {
                position = sportComplexesFilterAdapter.getPosition(sportComplexesFilter);
            }
        }

        if (position > 0) {
            if (_sportComplexesFilterView != null) {
                _sportComplexesFilterView.setSelection(position);
            }
        }
    }

    @NonNull
    protected final SportComplexesSortOrder getSportComplexesSortOrder() {
        final SportComplexesSortOrder sportComplexesSortOrder;

        if (_sportComplexesSortOrder != null) {
            sportComplexesSortOrder = _sportComplexesSortOrder;
        } else {
            sportComplexesSortOrder = SportComplexesSortOrder.NAME;
        }

        return sportComplexesSortOrder;
    }

    protected final void setSportComplexesSortOrder(
        @Nullable final SportComplexesSortOrder sportComplexesSortOrder) {
        if (_sportComplexesSortOrder != sportComplexesSortOrder) {
            _sportComplexesSortOrder = sportComplexesSortOrder;

            onSportComplexesSortOrderChanged();
        }
    }

    protected final boolean isSearchExpanded() {
        return BooleanUtils.isTrue(_searchExpanded);
    }

    protected final void setDateSearchParam(@Nullable final Long dateSearchParam) {
        if (!Objects.equals(dateSearchParam, _dateSearchParam)) {
            _dateSearchParam = dateSearchParam;

            onDateSearchParamChanged();
        }
    }

    protected final void setEndTimeSearchParam(@Nullable final Long timeSearchParam) {
        if (!Objects.equals(timeSearchParam, _endTimeSearchParam)) {
            _endTimeSearchParam = timeSearchParam;

            onEndTimeSearchParamsChanged();
        }
    }

    protected final void setStartTimeSearchParam(@Nullable final Long timeSearchParam) {
        if (!Objects.equals(timeSearchParam, _startTimeSearchParam)) {
            _startTimeSearchParam = timeSearchParam;

            onStartTimeSearchParamsChanged();
        }
    }

    @Nullable
    protected String getFormattedSearchDate(@Nullable final Long time) {
        final String formattedSearchTime;

        if (_searchDateFormat == null) {
            final val pattern =
                DateFormat.getBestDateTimePattern(Locale.getDefault(), "dd MMMM yyyy");
            _searchDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            _searchDateFormat.getTimeZone().setRawOffset(0);
        }

        if (time != null) {
            formattedSearchTime = _searchDateFormat.format(new Date(time));
        } else {
            formattedSearchTime = null;
        }

        return formattedSearchTime;
    }

    @Nullable
    protected String getFormattedSearchTime(@Nullable final Long time) {
        final String formattedSearchTime;

        if (_searchTimeFormat == null) {
            _searchTimeFormat = DateFormat.getTimeFormat(this);
            _searchTimeFormat.getTimeZone().setRawOffset(0);
        }

        if (time != null) {
            formattedSearchTime = _searchTimeFormat.format(new Date(time));
        } else {
            formattedSearchTime = null;
        }

        return formattedSearchTime;
    }

    @Nullable
    protected String getNameSearchParam() {
        final String result;

        if (_searchNameView != null) {
            final val searchName = _searchNameView.getText().toString();
            result = TextUtils.isEmpty(searchName) ? null : searchName;
        } else {
            result = null;
        }

        return result;
    }

    protected void setNameSearchParam(@Nullable final String param) {
        if (_searchNameView != null) {
            _searchNameView.setText(param);
        }
    }

    @CallSuper
    protected void invalidateSearchBar() {
        final val startTimeSearchParam = getFormattedSearchTime(getStartTimeSearchParam());

        if (startTimeSearchParam != null) {
            if (_searchStartTimeView != null) {
                _searchStartTimeView.setText(startTimeSearchParam);
            }
            if (_clearSearchStartTimeView != null) {
                _clearSearchStartTimeView.setVisibility(View.VISIBLE);
            }
        } else {
            if (_searchStartTimeView != null) {
                _searchStartTimeView.setText(null);
            }
            if (_clearSearchStartTimeView != null) {
                _clearSearchStartTimeView.setVisibility(View.INVISIBLE);
            }
        }

        final val endTimeSearchParam = getFormattedSearchTime(getEndTimeSearchParam());

        if (endTimeSearchParam != null) {
            if (_searchEndTimeView != null) {
                _searchEndTimeView.setText(endTimeSearchParam);
            }
            if (_clearSearchEndTimeView != null) {
                _clearSearchEndTimeView.setVisibility(View.VISIBLE);
            }
        } else {
            if (_searchEndTimeView != null) {
                _searchEndTimeView.setText(null);
            }
            if (_clearSearchEndTimeView != null) {
                _clearSearchEndTimeView.setVisibility(View.INVISIBLE);
            }
        }

        final val dateSearchParams = getFormattedSearchDate(getDateSearchParam());

        if (dateSearchParams != null) {
            if (_searchDateView != null) {
                _searchDateView.setText(dateSearchParams);
            }
            if (_clearSearchDateView != null) {
                _clearSearchDateView.setVisibility(View.VISIBLE);
            }
        } else {
            if (_searchDateView != null) {
                if (startTimeSearchParam != null) {
                    _searchDateView.setText(R.string.sport_complexes_viewer_search_day_today);
                } else {
                    _searchDateView.setText(null);
                }
            }
            if (_clearSearchDateView != null) {
                _clearSearchDateView.setVisibility(View.INVISIBLE);
            }
        }

        final val nameSearchParams = getNameSearchParam();

        if (!TextUtils.isEmpty(nameSearchParams)) {
            if (_clearSearchNameView != null) {
                _clearSearchNameView.setVisibility(View.VISIBLE);
            }
        } else {
            if (_clearSearchNameView != null) {
                _clearSearchNameView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @CallSuper
    protected void onClearSearchDateClick() {
        setDateSearchParam(null);
    }

    @CallSuper
    protected void onClearSearchEndTimeClick() {
        setEndTimeSearchParam(null);
    }

    @CallSuper
    protected void onClearSearchNameClick() {
        setNameSearchParam(null);
    }

    @CallSuper
    protected void onClearSearchStartTimeClick() {
        setStartTimeSearchParam(null);
    }

    @CallSuper
    @Override
    protected void onConfigureRendererModule(
        @NonNull final PlayerRendererScreenModule.Builder builder) {
        super.onConfigureRendererModule(Contracts.requireNonNull(builder, "builder == null"));

        builder.addSportComplexInfoRenderer(this);
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
            setContentView(R.layout.activity_sport_complexes_viewer);
            bindViews();

            if (_sportComplexInfoContainerView != null) {
                _sportComplexInfoBehavior =
                    BottomSheetBehavior.from(_sportComplexInfoContainerView);
            }

            if (_sportComplexInfoBehavior != null) {
                _sportComplexInfoBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }

            if (_toolbarView != null) {
                setSupportActionBar(_toolbarView);
            }

            final val actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
            }

            if (_appbarView != null) {
                _appbarView.addOnOffsetChangedListener(_removeSearchViewOnToolbarCollapsed);
            }

            _sportComplexesFilterAdapter = new SportComplexesFilterAdapter(this);

            if (_sportComplexesFilterView != null) {
                _sportComplexesFilterView.setAdapter(getSportComplexesFilterAdapter());
                _sportComplexesFilterView.setOnItemSelectedListener(
                    _onSelectSportComplexFilterListener);
            }

            _sportComplexesViewerPages = new SportComplexesViewerPageFactory();

            _sportComplexesViewerPagerAdapter = new SportComplexesViewerPagerAdapter(this);

            _sportComplexesViewerPagerAdapter.setPageFactory(_sportComplexesViewerPages);

            if (_pagerView != null) {
                _pagerView.setAdapter(_sportComplexesViewerPagerAdapter);

                _pagerView.addOnPageChangeListener(_pageChangeListener);
            }

            if (_tabsView != null) {
                _tabsView.setupWithViewPager(_pagerView);
                final val activeTabPosition = _state.getActiveTab();
                final val activeTab =
                    _tabsView.getTabAt(ObjectUtils.defaultIfNull(activeTabPosition,
                                                                 SportComplexesViewerPageFactory
                                                                     .POSITION_SPORT_COMPLEXES_LIST));
                if (activeTab != null) {
                    activeTab.select();
                }
            }

            if (_searchDateView != null) {
                _searchDateView.setOnClickListener(_controlsClickListener);
            }
            if (_searchStartTimeView != null) {
                _searchStartTimeView.setOnClickListener(_controlsClickListener);
            }
            if (_searchEndTimeView != null) {
                _searchEndTimeView.setOnClickListener(_controlsClickListener);
            }
            if (_searchNameView != null) {
                _searchNameView.setOnClickListener(_controlsClickListener);
                _searchNameView.setOnFocusChangeListener(_hideImeOnLostFocus);
                _searchNameView.addTextChangedListener(_searchNameListener);
                _searchNameView.setOnEditorActionListener(_performSearchOnEditorAction);
            }

            if (_clearSearchStartTimeView != null) {
                _clearSearchStartTimeView.setOnClickListener(_controlsClickListener);
            }
            if (_clearSearchEndTimeView != null) {
                _clearSearchEndTimeView.setOnClickListener(_controlsClickListener);
            }
            if (_clearSearchDateView != null) {
                _clearSearchDateView.setOnClickListener(_controlsClickListener);
            }
            if (_clearSearchNameView != null) {
                _clearSearchNameView.setOnClickListener(_controlsClickListener);
            }

            setStartTimeSearchParam(_state.getStartTimeSearchParam());
            setEndTimeSearchParam(_state.getEndTimeSearchParam());
            setDateSearchParam(_state.getDateSearchParam());
            setNameSearchParam(_state.getNameSearchParam());
            // TODO: 17.04.2017 Fix behaviour after rotation
            setSearchExpanded(_state.isSearchExpanded(), false);

            invalidateSearchBar();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (_searchNameView != null) {
            _searchNameView.removeTextChangedListener(_searchNameListener);
        }

        if (_pagerView != null) {
            _pagerView.removeOnPageChangeListener(_pageChangeListener);
        }

        if (_appbarView != null) {
            _appbarView.removeOnOffsetChangedListener(_removeSearchViewOnToolbarCollapsed);
        }

        unbindViews();
    }

    @CallSuper
    protected void onDateSearchParamChanged() {
        invalidateSearchBar();

        updatePagesSearchParams();
    }

    @CallSuper
    protected void onEndTimeSearchParamsChanged() {
        invalidateSearchBar();

        updatePagesSearchParams();
    }

    @Nullable
    @CallSuper
    protected SportComplexesViewerState onHandleIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        final SportComplexesViewerState state;

        final val action = getIntent().getAction();
        if (Intent.ACTION_MAIN.equals(action)) {
            state = onHandleMainIntent(intent);
        } else {
            state = null;
        }

        return state;
    }

    @Nullable
    @CallSuper
    protected SportComplexesViewerState onHandleMainIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        return new SportComplexesViewerState();
    }

    @Nullable
    @CallSuper
    protected SportComplexesViewerState onHandleSavedState(
        @NonNull final Bundle savedInstanceState) {
        SportComplexesViewerState state = null;

        if (savedInstanceState.containsKey(_KEY_SAVED_STATE)) {
            state = Parcels.unwrap(savedInstanceState.getParcelable(_KEY_SAVED_STATE));
        }

        return state;
    }

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
    protected void onNameSearchParamChanged() {
        invalidateSearchBar();

        updatePagesSearchParams();
    }

    @CallSuper
    protected void onPageChanged(final int newPosition) {
        if (newPosition == SportComplexesViewerPageFactory.POSITION_SPORT_COMPLEXES_MAP) {
            setSearchExpanded(false, false);

            if (_appbarView != null) {
                _appbarView.setExpanded(false, true);
            }
        }

        final val sportComplexInfoBehavior = getSportComplexInfoBehavior();

        if (sportComplexInfoBehavior != null) {
            sportComplexInfoBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        supportInvalidateOptionsMenu();

        final val pagerAdapter = getSportComplexesViewerPagerAdapter();

        if (pagerAdapter != null) {
            pagerAdapter.notifyActivePageChanged(newPosition);
        }
    }

    @CallSuper
    @Override
    protected void onPlayerChanged() {
        super.onPlayerChanged();

        final val sportComplexesFilterAdapter = getSportComplexesFilterAdapter();
        if (sportComplexesFilterAdapter != null) {
            sportComplexesFilterAdapter.setAllowUserSpecificFilters(getPlayer() != null);
        }
    }

    @CallSuper
    protected void onSearchDateClick() {
        _selectSportComplexesSearchDateEvent.rise();
    }

    @CallSuper
    protected void onSearchEndTimeClick() {
        _selectSportComplexesSearchTimeEvent.rise(new SelectSearchTimeEventArgs(
            SelectSearchTimeEventArgs.Mode.END));
    }

    @CallSuper
    protected void onSearchExpandedStateChanged(final boolean animate) {
        final boolean searchExpanded = isSearchExpanded();

        if (searchExpanded) {
            if (_appbarView != null) {
                _appbarView.removeCallbacks(_removeSearchContainerView);

                if (_searchContainerView != null &&
                    _appbarView.indexOfChild(_searchContainerView) < 0) {
                    final int toolbarIndex = _appbarView.indexOfChild(_toolbarView);
                    _appbarView.addView(_searchContainerView, toolbarIndex + 1);

                    if (animate) {
                        AnimationViewUtils.animateSetVisibility(_searchContainerView,
                                                                View.VISIBLE,
                                                                R.anim.fade_in_short);
                    } else {
                        _searchContainerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            if (_searchContainerView != null) {
                _searchContainerView.setVisibility(View.INVISIBLE);
            }

            if (_appbarView != null) {
                if (animate) {
                    _appbarView.postDelayed(_removeSearchContainerView, /*TODO: DELAY HACK!*/ 15);
                } else {
                    _removeSearchContainerView.run();
                }
            }
        }
    }

    @CallSuper
    protected void onSearchNameClick() {
        if (_searchNameView != null) {
            enableSearchName();

            ImeUtils.showIme(_searchNameView);
        }
    }

    @CallSuper
    protected void onSearchStartTimeClick() {
        _selectSportComplexesSearchTimeEvent.rise(new SelectSearchTimeEventArgs(
            SelectSearchTimeEventArgs.Mode.START));
    }

    @CallSuper
    protected void onSportComplexesFilterTypeChanged() {
        disableSearchName();

        updatePagesSearchParams();
    }

    @CallSuper
    protected void onSportComplexesSortOrderChanged() {
        supportInvalidateOptionsMenu();

        updatePagesSearchParams();
    }

    @CallSuper
    protected void onStartTimeSearchParamsChanged() {
        invalidateSearchBar();

        updatePagesSearchParams();
    }

    protected void setSearchExpanded(final boolean searchExpanded, final boolean animate) {
        if (!Objects.equals(_searchExpanded, searchExpanded)) {
            _searchExpanded = searchExpanded;

            onSearchExpandedStateChanged(animate);
        }
    }

    @CallSuper
    protected void updatePagesSearchParams() {
        final val pagerAdapter = getSportComplexesViewerPagerAdapter();
        if (pagerAdapter != null) {
            pagerAdapter.notifyDateSearchParamsChanged(getDateSearchParam());
            pagerAdapter.notifyStartTimeSearchParamsChanged(getStartTimeSearchParam());
            pagerAdapter.notifyEndTimeSearchParamsChanged(getEndTimeSearchParam());
            pagerAdapter.notifyNameSearchParamsChanged(getNameSearchParam());
            pagerAdapter.notifySportComplexesFilterChanged(getSportComplexesFilter());
            pagerAdapter.notifySportComplexesSortOrderChanged(getSportComplexesSortOrder());
        }
    }

    @Named(PresenterNames.SPORT_COMPLEXES_VIEWER)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<SportComplexesViewerScreen> _presenter;

    @NonNull
    private final View.OnFocusChangeListener _hideImeOnLostFocus =
        new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (!hasFocus) {
                    ImeUtils.hideIme(v);
                }
            }
        };

    @NonNull
    private final Calendar _searchDateConverter =
        Calendar.getInstance(new SimpleTimeZone(0, "UTC"));

    @NonNull
    private final ManagedNoticeEvent _selectSportComplexesSearchDateEvent =
        Events.createNoticeEvent();

    @NonNull
    private final ManagedEvent<SelectSearchTimeEventArgs> _selectSportComplexesSearchTimeEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewSportComplexDetailsEvent = Events.createEvent();

    @Nullable
    private AppBarLayout _appbarView;

    @Nullable
    private View _clearSearchDateView;

    @Nullable
    private View _clearSearchEndTimeView;

    @Nullable
    private View _clearSearchNameView;

    @Nullable
    private View _clearSearchStartTimeView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private Long _dateSearchParam;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private Long _endTimeSearchParam;

    @Nullable
    private ViewPager _pagerView;

    @Nullable
    private View _searchContainerView;

    @NonNull
    private final Runnable _removeSearchContainerView = new Runnable() {
        @Override
        public void run() {
            if (_appbarView != null) {
                _appbarView.removeView(_searchContainerView);
            }
        }
    };

    @Nullable
    private java.text.DateFormat _searchDateFormat;

    @Nullable
    private TextView _searchDateView;

    @Nullable
    private TextView _searchEndTimeView;

    @Nullable
    private Boolean _searchExpanded;

    @Nullable
    private EditText _searchNameView;

    @Nullable
    private TextView _searchStartTimeView;

    @Nullable
    private java.text.DateFormat _searchTimeFormat;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private BottomSheetBehavior<View> _sportComplexInfoBehavior;

    @Nullable
    private View _sportComplexInfoContainerView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private SportComplexesFilterAdapter _sportComplexesFilterAdapter;

    @Nullable
    private Spinner _sportComplexesFilterView;

    @Nullable
    private SportComplexesSortOrder _sportComplexesSortOrder;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private SportComplexesViewerPagerAdapter _sportComplexesViewerPagerAdapter;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private SportComplexesViewerPageFactory _sportComplexesViewerPages;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private Long _startTimeSearchParam;

    @NonNull
    private final View.OnClickListener _controlsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            disableSearchName();

            final int id = v.getId();
            if (R.id.sport_complexes_search_start_time == id) {
                onSearchStartTimeClick();
            } else if (R.id.sport_complexes_search_end_time == id) {
                onSearchEndTimeClick();
            } else if (R.id.sport_complexes_search_date == id) {
                onSearchDateClick();
            } else if (R.id.sport_complexes_search_name == id) {
                onSearchNameClick();
            } else if (R.id.sport_complexes_clear_search_start_time == id) {
                onClearSearchStartTimeClick();
            } else if (R.id.sport_complexes_clear_search_end_time == id) {
                onClearSearchEndTimeClick();
            } else if (R.id.sport_complexes_clear_search_date == id) {
                onClearSearchDateClick();
            } else if (R.id.sport_complexes_clear_search_name == id) {
                onClearSearchNameClick();
            }
        }
    };

    @NonNull
    private final AdapterView.OnItemSelectedListener _onSelectSportComplexFilterListener =
        new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(
                final AdapterView<?> parent, final View view, final int position, final long id) {
                onSportComplexesFilterTypeChanged();
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                onSportComplexesFilterTypeChanged();
            }
        };

    @NonNull
    private final TextView.OnEditorActionListener _performSearchOnEditorAction =
        new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(
                final TextView v, final int actionId, final KeyEvent event) {
                final boolean consumed;
                if (EditorInfo.IME_ACTION_SEARCH == actionId) {
                    updatePagesSearchParams();

                    if (_appbarView != null) {
                        _appbarView.setExpanded(false);
                    }

                    disableSearchName();
                    ImeUtils.hideIme(v);

                    consumed = true;
                } else {
                    consumed = false;
                }

                return consumed;
            }
        };

    @NonNull
    private final TextWatcher _searchNameListener = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(
            final CharSequence s, final int start, final int before, final int count) {
            super.onTextChanged(s, start, before, count);

            onNameSearchParamChanged();
        }
    };

    @Nullable
    private SportComplexesViewerState _state;

    @Nullable
    private TabLayout _tabsView;

    @Nullable
    private Toolbar _toolbarView;

    @NonNull
    private final AppBarLayout.OnOffsetChangedListener _removeSearchViewOnToolbarCollapsed =
        new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(
                final AppBarLayout appBarLayout, final int verticalOffset) {
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    setSearchExpanded(false, true);
                }
            }
        };

    @NonNull
    private final ViewPager.OnPageChangeListener _pageChangeListener =
        new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                onPageChanged(position);
            }
        };

    private void disableSearchName() {
        if (_searchNameView != null) {
            _searchNameView.clearFocus();
            _searchNameView.setFocusable(false);
        }
    }

    private void enableSearchName() {
        if (_searchNameView != null) {
            _searchNameView.setFocusable(true);
            _searchNameView.setFocusableInTouchMode(true);
            _searchNameView.requestFocus();
        }
    }
}
