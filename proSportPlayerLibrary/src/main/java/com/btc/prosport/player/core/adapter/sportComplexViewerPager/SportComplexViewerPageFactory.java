package com.btc.prosport.player.core.adapter.sportComplexViewerPager;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import lombok.experimental.Accessors;

import com.btc.common.extension.pager.PageFactory;
import com.btc.prosport.player.screen.fragment.PlaygroundsListFragment;
import com.btc.prosport.player.screen.fragment.SportComplexDetailFragment;

@Accessors(prefix = "_")
public final class SportComplexViewerPageFactory implements PageFactory {
    public static final int POSITION_SPORT_COMPLEX_DETAILS;

    public static final int POSITION_PLAYGROUNDS_VIEWER;

    protected static final int POSITION_LAST;

    static {
        int positionIndexer = 0;

        POSITION_SPORT_COMPLEX_DETAILS = positionIndexer++;
        POSITION_PLAYGROUNDS_VIEWER = positionIndexer++;

        POSITION_LAST = positionIndexer;
    }

    @NonNull
    @Override
    public Fragment createPageFragment(final int position) {
        final Fragment pageFragment;

        if (POSITION_SPORT_COMPLEX_DETAILS == position) {
            pageFragment = new SportComplexDetailFragment();
        } else if (POSITION_PLAYGROUNDS_VIEWER == position) {
            pageFragment = new PlaygroundsListFragment();
        } else {
            throw new IllegalArgumentException("Illegal position: " + position);
        }

        return pageFragment;
    }

    @Override
    public int getPageCount() {
        return POSITION_LAST;
    }
}
