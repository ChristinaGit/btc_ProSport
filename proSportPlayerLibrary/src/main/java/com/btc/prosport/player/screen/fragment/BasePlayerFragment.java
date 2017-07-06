package com.btc.prosport.player.screen.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.di.subscreen.ProSportSubscreenComponent;
import com.btc.prosport.di.subscreen.module.ProSportRxSubscreenModule;
import com.btc.prosport.di.subscreen.module.ProSportSystemSubscreenModule;
import com.btc.prosport.player.di.PlayerScreenComponentProvider;
import com.btc.prosport.player.di.PlayerSubscreenComponentProvider;
import com.btc.prosport.player.di.screen.PlayerScreenComponent;
import com.btc.prosport.player.di.subscreen.PlayerSubscreenComponent;
import com.btc.prosport.player.di.subscreen.module.PlayerManagerSubscreenScreenModule;
import com.btc.prosport.player.di.subscreen.module.PlayerPresenterSubscreenModule;
import com.btc.prosport.screen.fragment.BaseProSportFragment;

@Accessors(prefix = "_")
public abstract class BasePlayerFragment extends BaseProSportFragment
    implements PlayerSubscreenComponentProvider {

    @NonNull
    public final PlayerScreenComponent getPlayerScreenComponent() {
        final val activity = getActivity();
        if (activity instanceof PlayerScreenComponentProvider) {
            final val componentProvider = (PlayerScreenComponentProvider) activity;
            return componentProvider.getPlayerScreenComponent();
        } else {
            throw new IllegalStateException(
                "The activity must implement " + PlayerSubscreenComponentProvider.class.getName());
        }
    }

    @Override
    @NonNull
    public final PlayerSubscreenComponent getPlayerSubscreenComponent() {
        if (_playerSubscreenComponent == null) {
            throw new IllegalStateException("The fragment has not yet been created.");
        }

        return _playerSubscreenComponent;
    }

    @CallSuper
    protected void onConfigureManagerModule(
        @NonNull final PlayerManagerSubscreenScreenModule.Builder builder) {
        Contracts.requireNonNull(builder, "builder == null");
    }

    @NonNull
    protected PlayerSubscreenComponent onCreatePlayerSubscreenComponent() {
        final val managerSubscreenModuleBuilder = PlayerManagerSubscreenScreenModule.builder();
        onConfigureManagerModule(managerSubscreenModuleBuilder);

        return getPlayerScreenComponent().addPlayerSubscreenComponent(
            managerSubscreenModuleBuilder.build(),
            new PlayerPresenterSubscreenModule(),
            new ProSportSystemSubscreenModule(this),
            new ProSportRxSubscreenModule(this));
    }

    @NonNull
    @Override
    protected ProSportSubscreenComponent onCreateProSportSubscreenComponent() {
        return _playerSubscreenComponent = onCreatePlayerSubscreenComponent();
    }

    @Nullable
    private PlayerSubscreenComponent _playerSubscreenComponent;
}
