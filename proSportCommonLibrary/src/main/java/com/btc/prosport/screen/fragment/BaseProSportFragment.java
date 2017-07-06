package com.btc.prosport.screen.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.extension.fragment.ScreenFragment;
import com.btc.prosport.di.ProSportScreenComponentProvider;
import com.btc.prosport.di.ProSportSubscreenComponentProvider;
import com.btc.prosport.di.screen.ProSportScreenComponent;
import com.btc.prosport.di.subscreen.ProSportSubscreenComponent;
import com.btc.prosport.di.subscreen.module.ProSportPresenterSubscreenModule;
import com.btc.prosport.di.subscreen.module.ProSportRxSubscreenModule;
import com.btc.prosport.di.subscreen.module.ProSportSystemSubscreenModule;

@Accessors(prefix = "_")
public abstract class BaseProSportFragment extends ScreenFragment
    implements ProSportSubscreenComponentProvider {

    @NonNull
    public final ProSportScreenComponent getProSportScreenComponent() {
        final val activity = getActivity();
        if (activity instanceof ProSportScreenComponentProvider) {
            final val componentProvider = (ProSportScreenComponentProvider) activity;
            return componentProvider.getProSportScreenComponent();
        } else {
            throw new IllegalStateException(
                "The activity must implement " + ProSportScreenComponentProvider.class.getName());
        }
    }

    @Override
    @NonNull
    public final ProSportSubscreenComponent getProSportSubscreenComponent() {
        if (_proSportSubscreenComponent == null) {
            throw new IllegalStateException("The fragment has not yet been created.");
        }

        return _proSportSubscreenComponent;
    }

    @NonNull
    protected ProSportSubscreenComponent onCreateProSportSubscreenComponent() {
        //@formatter:off
        return getProSportScreenComponent().addProSportSubscreenComponent(
                new ProSportSystemSubscreenModule(this),
                new ProSportPresenterSubscreenModule(),
                new ProSportRxSubscreenModule(this)
            );
        //@formatter:on
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        _proSportSubscreenComponent = onCreateProSportSubscreenComponent();
    }

    @Nullable
    private ProSportSubscreenComponent _proSportSubscreenComponent;
}