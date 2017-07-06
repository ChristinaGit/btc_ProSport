package com.btc.prosport.player.core.adapter.sportComplexViewerPager;

import android.support.annotation.Nullable;

import com.btc.common.extension.pager.ActivePage;

public interface SportComplexViewerPage extends ActivePage {
    @Nullable
    Long getSportComplexId();

    void setSportComplexId(@Nullable Long sportComplexId);
}
