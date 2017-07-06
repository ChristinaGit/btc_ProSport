package com.btc.prosport.player.core.adapter.sportComplexesViewerPager;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import lombok.experimental.Accessors;

import com.btc.common.extension.pager.PageFactory;
import com.btc.prosport.player.screen.fragment.SportComplexesListFragment;
import com.btc.prosport.player.screen.fragment.SportComplexesMapFragment;

@Accessors(prefix = "_")
public final class SportComplexesViewerPageFactory implements PageFactory {
    public static final int POSITION_SPORT_COMPLEXES_LIST;

    public static final int POSITION_SPORT_COMPLEXES_MAP;

    protected static final int POSITION_LAST;

    static {
        int positionIndexer = 0;

        POSITION_SPORT_COMPLEXES_LIST = positionIndexer++;
        POSITION_SPORT_COMPLEXES_MAP = positionIndexer++;

        POSITION_LAST = positionIndexer;
    }

    @NonNull
    @Override
    public Fragment createPageFragment(final int position) {
        final Fragment pageFragment;

        if (POSITION_SPORT_COMPLEXES_LIST == position) {
            pageFragment = new SportComplexesListFragment();
        } else if (POSITION_SPORT_COMPLEXES_MAP == position) {
            pageFragment = new SportComplexesMapFragment();
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
