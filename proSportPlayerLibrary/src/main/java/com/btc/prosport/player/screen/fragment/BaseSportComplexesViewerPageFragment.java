package com.btc.prosport.player.screen.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.prosport.player.R;
import com.btc.prosport.player.core.SportComplexesFilter;
import com.btc.prosport.player.core.SportComplexesSortOrder;
import com.btc.prosport.player.core.adapter.sportComplexesViewerPager.SportComplexesViewerPage;
import com.btc.prosport.player.core.eventArgs.SportComplexesSearchEventArgs;

import java.util.Objects;

@Accessors(prefix = "_")
public abstract class BaseSportComplexesViewerPageFragment extends BasePlayerFragment
    implements SportComplexesViewerPage {

    @Override
    public final void setDateSearchParams(@Nullable final Long dateSearchParams) {
        if (!Objects.equals(_dateSearchParams, dateSearchParams)) {
            _dateSearchParams = dateSearchParams;

            onDateSearchParamsChanged();
        }
    }

    @Override
    public final void setEndTimeSearchParams(@Nullable final Long timeSearchParams) {
        if (!Objects.equals(_endTimeSearchParams, timeSearchParams)) {
            _endTimeSearchParams = timeSearchParams;

            onEndTimeSearchParamsChanged();
        }
    }

    @Override
    public final void setNameSearchParams(@Nullable final String nameSearchParams) {
        if (!Objects.equals(_nameSearchParams, nameSearchParams)) {
            _nameSearchParams = nameSearchParams;

            onNameSearchParamsChanged();
        }
    }

    @Override
    public final void setSportComplexesFilter(
        @Nullable final SportComplexesFilter sportComplexesFilter) {
        if (!Objects.equals(_sportComplexesFilter, sportComplexesFilter)) {
            _sportComplexesFilter = sportComplexesFilter;

            onSportComplexesFilterChanged();
        }
    }

    @Override
    public final void setSportComplexesSortOrder(
        @Nullable final SportComplexesSortOrder sportComplexesSortOrder) {
        if (!Objects.equals(_sportComplexesSortOrder, sportComplexesSortOrder)) {
            _sportComplexesSortOrder = sportComplexesSortOrder;

            onSportComplexesSortOrderChanged();
        }
    }

    @Override
    public final void setStartTimeSearchParams(@Nullable final Long timeSearchParams) {
        if (!Objects.equals(_startTimeSearchParams, timeSearchParams)) {
            _startTimeSearchParams = timeSearchParams;

            onStartTimeSearchParamsChanged();
        }
    }

    @Override
    public final void setPageActive(final boolean pageActive) {
        if (_pageActive != pageActive) {
            _pageActive = pageActive;

            if (isPageActive()) {
                onEnterPage();
            } else {
                onLeavePage();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        final val view = super.onCreateView(inflater, container, savedInstanceState);

        _inputDelayUserSearch = getResources().getInteger(R.integer.input_delay_user_search);

        return view;
    }

    @CallSuper
    @Override
    public void onStop() {
        super.onStop();

        cancelSportComplexesSearchParamsChangedWithInputDelay();
    }

    @NonNull
    protected final SportComplexesSearchEventArgs getSportComplexesSearchEventArgs() {
        return new SportComplexesSearchEventArgs(getDateSearchParams(),
                                                 getStartTimeSearchParams(),
                                                 getEndTimeSearchParams(),
                                                 getNameSearchParams(),
                                                 getSportComplexesSortOrder(),
                                                 getSportComplexesFilter());
    }

    @CallSuper
    protected void onDateSearchParamsChanged() {
        if (isResumed()) {
            riseSportComplexesSearchParamsChangedWithInputDelay();
        }
    }

    @CallSuper
    protected void onEndTimeSearchParamsChanged() {
        if (isResumed()) {
            riseSportComplexesSearchParamsChangedWithInputDelay();
        }
    }

    @CallSuper
    protected void onEnterPage() {
    }

    @CallSuper
    protected void onLeavePage() {
    }

    @CallSuper
    protected void onNameSearchParamsChanged() {
        if (isResumed()) {
            riseSportComplexesSearchParamsChangedWithInputDelay();
        }
    }

    @CallSuper
    protected void onSportComplexSearchParamsChangedWithInputDelay() {
    }

    @CallSuper
    protected void onSportComplexesFilterChanged() {
        if (isResumed()) {
            riseSportComplexesSearchParamsChangedWithInputDelay();
        }
    }

    @CallSuper
    protected void onSportComplexesSortOrderChanged() {
        if (isResumed()) {
            riseSportComplexesSearchParamsChangedWithInputDelay();
        }
    }

    @CallSuper
    protected void onStartTimeSearchParamsChanged() {
        if (isResumed()) {
            riseSportComplexesSearchParamsChangedWithInputDelay();
        }
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Handler _handler = new Handler();

    // TODO: 04.05.2017 Replace on notify* pattern. Handle user input delay in activity.
    @NonNull
    private final Runnable _onSportComplexesSearchParamsChangedWithInputDelay = new Runnable() {
        @Override
        public void run() {
            onSportComplexSearchParamsChangedWithInputDelay();
        }
    };

    @Getter(onMethod = @__(@Override))
    @Nullable
    private Long _dateSearchParams;

    @Getter(onMethod = @__(@Override))
    @Nullable
    private Long _endTimeSearchParams;

    private int _inputDelayUserSearch;

    @Getter(onMethod = @__(@Override))
    @Nullable
    private String _nameSearchParams;

    @Getter(onMethod = @__(@Override))
    private boolean _pageActive = false;

    @Getter(onMethod = @__(@Override))
    @Nullable
    private SportComplexesFilter _sportComplexesFilter;

    @Getter(onMethod = @__(@Override))
    @Nullable
    private SportComplexesSortOrder _sportComplexesSortOrder;

    @Getter(onMethod = @__(@Override))
    @Nullable
    private Long _startTimeSearchParams;

    private void cancelSportComplexesSearchParamsChangedWithInputDelay() {
        getHandler().removeCallbacks(_onSportComplexesSearchParamsChangedWithInputDelay);
    }

    private void riseSportComplexesSearchParamsChangedWithInputDelay() {
        cancelSportComplexesSearchParamsChangedWithInputDelay();
        getHandler().postDelayed(_onSportComplexesSearchParamsChangedWithInputDelay,
                                 _inputDelayUserSearch);
    }
}
