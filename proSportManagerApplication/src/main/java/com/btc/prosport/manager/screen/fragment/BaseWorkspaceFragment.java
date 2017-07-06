package com.btc.prosport.manager.screen.fragment;

import android.support.annotation.CallSuper;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.extension.pager.ActivePage;

@Accessors(prefix = "_")
public abstract class BaseWorkspaceFragment extends BaseManagerFragment implements ActivePage {
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

    @CallSuper
    protected void onEnterPage() {
    }

    @CallSuper
    protected void onLeavePage() {
    }

    @Getter
    private boolean _pageActive;
}
