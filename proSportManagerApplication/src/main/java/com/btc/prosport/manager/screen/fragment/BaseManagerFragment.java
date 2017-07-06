package com.btc.prosport.manager.screen.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.prosport.di.subscreen.ProSportSubscreenComponent;
import com.btc.prosport.di.subscreen.module.ProSportRxSubscreenModule;
import com.btc.prosport.di.subscreen.module.ProSportSystemSubscreenModule;
import com.btc.prosport.manager.di.ManagerScreenComponentProvider;
import com.btc.prosport.manager.di.ManagerSubscreenComponentProvider;
import com.btc.prosport.manager.di.screen.ManagerScreenComponent;
import com.btc.prosport.manager.di.subscreen.ManagerSubscreenComponent;
import com.btc.prosport.manager.di.subscreen.module.ManagerPresenterSubscreenModule;
import com.btc.prosport.screen.fragment.BaseProSportFragment;

@Accessors(prefix = "_")
public abstract class BaseManagerFragment extends BaseProSportFragment
    implements ManagerSubscreenComponentProvider {

    @NonNull
    public final ManagerScreenComponent getManagerScreenComponent() {
        final val activity = getActivity();
        if (activity instanceof ManagerScreenComponentProvider) {
            final val componentProvider = (ManagerScreenComponentProvider) activity;
            return componentProvider.getManagerScreenComponent();
        } else {
            throw new IllegalStateException(
                "The activity must implement " + ManagerSubscreenComponentProvider.class.getName());
        }
    }

    @Override
    @NonNull
    public final ManagerSubscreenComponent getManagerSubscreenComponent() {
        if (_managerSubscreenComponent == null) {
            throw new IllegalStateException("The fragment has not yet been created.");
        }

        return _managerSubscreenComponent;
    }

    @NonNull
    protected ManagerSubscreenComponent onCreateManagerSubscreenComponent() {
        return getManagerScreenComponent().addProSportSubscreenComponent(
            new ManagerPresenterSubscreenModule(),
            new ProSportSystemSubscreenModule(this),
            new ProSportRxSubscreenModule(this));
    }

    @NonNull
    @Override
    protected ProSportSubscreenComponent onCreateProSportSubscreenComponent() {
        return _managerSubscreenComponent = onCreateManagerSubscreenComponent();
    }

    @Nullable
    private ManagerSubscreenComponent _managerSubscreenComponent;
}
