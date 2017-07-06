package com.btc.prosport.manager.screen.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.User;
import com.btc.prosport.manager.screen.ManagerAccountScreen;

@Accessors(prefix = "_")
public abstract class BaseManagerAccountActivity extends BaseManagerActivity
    implements ManagerAccountScreen {
    @CallSuper
    @Override
    public void displayManager(@Nullable final User manager) {
        setManager(manager);
    }

    @CallSuper
    @Override
    public void displayManagerLoading() {
    }

    @CallSuper
    @Override
    public void displayManagerLoadingError() {
        finishAffinity();
    }

    @CallSuper
    @Override
    public void displayNoAccountsError() {
    }

    @CallSuper
    protected void onManagerChanged() {
    }

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private User _manager;

    private void setManager(@Nullable final User manager) {
        if (_manager != manager) {
            _manager = manager;

            onManagerChanged();
        }
    }
}
