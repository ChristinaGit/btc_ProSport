package com.btc.prosport.player.core.adapter.sportComplexesViewerPager;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.pager.WeakFragmentStatePagerAdapter;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.SportComplexesFilter;
import com.btc.prosport.player.core.SportComplexesSortOrder;

@Accessors(prefix = "_")
public final class SportComplexesViewerPagerAdapter extends WeakFragmentStatePagerAdapter {
    public SportComplexesViewerPagerAdapter(@NonNull final AppCompatActivity activity) {
        super(Contracts
                  .requireNonNull(activity, "fragmentManager == null")
                  .getSupportFragmentManager());

        _context = activity.getApplicationContext();
    }

    @Nullable
    public final SportComplexesViewerPage getSportComplexesViewerPage(final int position) {
        final SportComplexesViewerPage page;

        final val fragment = getFragment(position);

        if (fragment instanceof SportComplexesViewerPage) {
            page = (SportComplexesViewerPage) fragment;
        } else {
            page = null;
        }

        return page;
    }

    public final void notifyDateSearchParamsChanged(
        @Nullable final Long dateSearchParams) {
        _dateSearchParams = dateSearchParams;

        final int pageCount = getCount();
        for (int i = 0; i < pageCount; i++) {
            final val viewerPage = getSportComplexesViewerPage(i);
            if (viewerPage != null) {
                viewerPage.setDateSearchParams(dateSearchParams);
            }
        }
    }

    public final void notifyEndTimeSearchParamsChanged(
        @Nullable final Long timeSearchParams) {
        _endTimeSearchParams = timeSearchParams;

        final int pageCount = getCount();
        for (int i = 0; i < pageCount; i++) {
            final val viewerPage = getSportComplexesViewerPage(i);
            if (viewerPage != null) {
                viewerPage.setEndTimeSearchParams(timeSearchParams);
            }
        }
    }

    public final void notifyNameSearchParamsChanged(@Nullable final String nameSearchParams) {
        _nameSearchParams = nameSearchParams;

        final int pageCount = getCount();
        for (int i = 0; i < pageCount; i++) {
            final val viewerPage = getSportComplexesViewerPage(i);
            if (viewerPage != null) {
                viewerPage.setNameSearchParams(nameSearchParams);
            }
        }
    }

    public final void notifySportComplexesFilterChanged(
        @Nullable final SportComplexesFilter sportComplexesFilter) {
        _sportComplexesFilter = sportComplexesFilter;

        final int pageCount = getCount();
        for (int i = 0; i < pageCount; i++) {
            final val viewerPage = getSportComplexesViewerPage(i);
            if (viewerPage != null) {
                viewerPage.setSportComplexesFilter(sportComplexesFilter);
            }
        }
    }

    public final void notifySportComplexesSortOrderChanged(
        @Nullable final SportComplexesSortOrder sportComplexesSortOrder) {
        _sportComplexesSortOrder = sportComplexesSortOrder;

        final int pageCount = getCount();
        for (int i = 0; i < pageCount; i++) {
            final val viewerPage = getSportComplexesViewerPage(i);
            if (viewerPage != null) {
                viewerPage.setSportComplexesSortOrder(sportComplexesSortOrder);
            }
        }
    }

    public final void notifyStartTimeSearchParamsChanged(
        @Nullable final Long timeSearchParams) {
        _startTimeSearchParams = timeSearchParams;

        final int pageCount = getCount();
        for (int i = 0; i < pageCount; i++) {
            final val viewerPage = getSportComplexesViewerPage(i);
            if (viewerPage != null) {
                viewerPage.setStartTimeSearchParams(timeSearchParams);
            }
        }
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        final CharSequence pageTitle;

        if (SportComplexesViewerPageFactory.POSITION_SPORT_COMPLEXES_LIST == position) {
            pageTitle = getContext().getString(R.string.sport_complexes_viewer_list_title);
        } else if (SportComplexesViewerPageFactory.POSITION_SPORT_COMPLEXES_MAP == position) {
            pageTitle = getContext().getString(R.string.sport_complexes_viewer_map_title);
        } else {
            throw new IllegalArgumentException("Illegal position: " + position);
        }

        return pageTitle;
    }

    @CallSuper
    @Override
    protected void onInstantiateFragment(
        @NonNull final Fragment fragment, final int position) {
        super.onInstantiateFragment(Contracts.requireNonNull(fragment, "fragment == null"),
                                    position);

        if (fragment instanceof SportComplexesViewerPage) {
            final val viewerPage = (SportComplexesViewerPage) fragment;

            viewerPage.setDateSearchParams(_dateSearchParams);
            viewerPage.setStartTimeSearchParams(_startTimeSearchParams);
            viewerPage.setEndTimeSearchParams(_endTimeSearchParams);
            viewerPage.setNameSearchParams(_nameSearchParams);
            viewerPage.setSportComplexesFilter(_sportComplexesFilter);
            viewerPage.setSportComplexesSortOrder(_sportComplexesSortOrder);
        }
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Context _context;

    @Nullable
    private Long _dateSearchParams;

    @Nullable
    private Long _endTimeSearchParams;

    @Nullable
    private String _nameSearchParams;

    @Nullable
    private SportComplexesFilter _sportComplexesFilter;

    @Nullable
    private SportComplexesSortOrder _sportComplexesSortOrder;

    @Nullable
    private Long _startTimeSearchParams;
}
