package com.btc.prosport.player.core.adapter.sportComplexViewerPager;

import android.content.Context;
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

import java.util.Objects;

@Accessors(prefix = "_")
public final class SportComplexViewerPagerAdapter extends WeakFragmentStatePagerAdapter {
    public SportComplexViewerPagerAdapter(@NonNull final AppCompatActivity activity) {
        super(Contracts
                  .requireNonNull(activity, "fragmentManager == null")
                  .getSupportFragmentManager());

        _context = activity.getApplicationContext();
    }

    @Nullable
    public final SportComplexViewerPage getSportComplexViewerPage(final int position) {
        final SportComplexViewerPage page;

        final val fragment = getFragment(position);

        if (fragment instanceof SportComplexViewerPage) {
            page = (SportComplexViewerPage) fragment;
        } else {
            page = null;
        }

        return page;
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        final CharSequence pageTitle;

        if (SportComplexViewerPageFactory.POSITION_SPORT_COMPLEX_DETAILS == position) {
            pageTitle = getContext().getString(R.string.sport_complex_viewer_details_title);
        } else if (SportComplexViewerPageFactory.POSITION_PLAYGROUNDS_VIEWER == position) {
            pageTitle = getContext().getString(R.string.sport_complex_viewer_playgrounds_title);
        } else {
            throw new IllegalArgumentException("Illegal position: " + position);
        }

        return pageTitle;
    }

    public void notifySportComplexIdChanged(@Nullable final Long sportComplexId) {
        if (!Objects.equals(_sportComplexId, sportComplexId)) {
            _sportComplexId = sportComplexId;

            final int pageCount = getCount();
            for (int i = 0; i < pageCount; i++) {
                final val viewerPage = getSportComplexViewerPage(i);
                if (viewerPage != null) {
                    viewerPage.setSportComplexId(sportComplexId);
                }
            }
        }
    }

    @Override
    protected void onInstantiateFragment(@NonNull final Fragment fragment, final int position) {
        super.onInstantiateFragment(Contracts.requireNonNull(fragment, "fragment == null"),
                                    position);

        if (fragment instanceof SportComplexViewerPage) {
            final val playgroundDetailsPage = (SportComplexViewerPage) fragment;
            playgroundDetailsPage.setSportComplexId(getSportComplexId());
        }
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Context _context;

    @Getter
    @Nullable
    private Long _sportComplexId;
}
