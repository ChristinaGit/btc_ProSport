package com.btc.prosport.player.screen.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.prosport.player.core.adapter.sportComplexViewerPager.SportComplexViewerPage;

import java.util.Objects;

@Accessors(prefix = "_")
public abstract class BaseSportComplexViewerPageFragment extends BasePlayerFragment
    implements SportComplexViewerPage {

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

    @Override
    public void setSportComplexId(@Nullable final Long sportComplexId) {
        if (!Objects.equals(_sportComplexId, sportComplexId)) {
            _sportComplexId = sportComplexId;

            onSportComplexIdChanged();
        }
    }

    @CallSuper
    protected void onEnterPage() {
    }

    @CallSuper
    protected void onLeavePage() {
    }

    protected void onSportComplexIdChanged() {
    }

    @Getter(onMethod = @__(@Override))
    private boolean _pageActive = false;

    @Getter(onMethod = @__(@Override))
    @Nullable
    private Long _sportComplexId;
}
