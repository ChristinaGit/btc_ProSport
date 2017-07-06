package com.btc.prosport.player.core.adapter.sportComplexesViewerPager;

import android.support.annotation.Nullable;

import com.btc.common.extension.pager.ActivePage;
import com.btc.prosport.player.core.SportComplexesFilter;
import com.btc.prosport.player.core.SportComplexesSortOrder;

public interface SportComplexesViewerPage extends ActivePage {
    @Nullable
    Long getDateSearchParams();

    void setDateSearchParams(@Nullable Long dateSearchParams);

    @Nullable
    Long getEndTimeSearchParams();

    void setEndTimeSearchParams(@Nullable Long timeSearchParams);

    @Nullable
    String getNameSearchParams();

    void setNameSearchParams(@Nullable String sportComplexNameSearchParams);

    @Nullable
    SportComplexesFilter getSportComplexesFilter();

    void setSportComplexesFilter(@Nullable SportComplexesFilter playgroundsFilter);

    @Nullable
    SportComplexesSortOrder getSportComplexesSortOrder();

    void setSportComplexesSortOrder(@Nullable SportComplexesSortOrder playgroundsFilter);

    @Nullable
    Long getStartTimeSearchParams();

    void setStartTimeSearchParams(@Nullable Long timeSearchParams);

    boolean hasContent();
}
