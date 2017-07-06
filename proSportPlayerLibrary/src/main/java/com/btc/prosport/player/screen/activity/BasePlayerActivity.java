package com.btc.prosport.player.screen.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.di.screen.ProSportScreenComponent;
import com.btc.prosport.di.screen.module.ProSportAccountScreenModule;
import com.btc.prosport.di.screen.module.ProSportAdviserScreenModule;
import com.btc.prosport.di.screen.module.ProSportAuthScreenModule;
import com.btc.prosport.di.screen.module.ProSportRxScreenModule;
import com.btc.prosport.di.screen.module.ProSportSystemScreenModule;
import com.btc.prosport.player.di.PlayerApplicationComponentProvider;
import com.btc.prosport.player.di.PlayerScreenComponentProvider;
import com.btc.prosport.player.di.application.PlayerApplicationComponent;
import com.btc.prosport.player.di.screen.PlayerScreenComponent;
import com.btc.prosport.player.di.screen.module.PlayerManagerScreenModule;
import com.btc.prosport.player.di.screen.module.PlayerPresenterScreenModule;
import com.btc.prosport.player.di.screen.module.PlayerRendererScreenModule;
import com.btc.prosport.screen.activity.BaseProSportActivity;

@Accessors(prefix = "_")
public abstract class BasePlayerActivity extends BaseProSportActivity
    implements PlayerScreenComponentProvider {

    @NonNull
    public final PlayerApplicationComponent getPlayerApplicationComponent() {
        final val application = getApplication();
        if (application instanceof PlayerApplicationComponentProvider) {
            final val componentProvider = (PlayerApplicationComponentProvider) application;
            return componentProvider.getPlayerApplicationComponent();
        } else {
            throw new IllegalStateException("The application must implement " +
                                            PlayerApplicationComponentProvider.class.getName());
        }
    }

    @Override
    @NonNull
    public final PlayerScreenComponent getPlayerScreenComponent() {
        if (_playerScreenComponent == null) {
            throw new IllegalStateException("The activity has not yet been created.");
        }

        return _playerScreenComponent;
    }

    @CallSuper
    protected void onConfigureRendererModule(
        @NonNull final PlayerRendererScreenModule.Builder builder) {
        Contracts.requireNonNull(builder, "builder == null");
    }

    @NonNull
    protected PlayerScreenComponent onCreatePlayerScreenComponent() {
        final val rendererScreenModuleBuilder = PlayerRendererScreenModule.builder();
        onConfigureRendererModule(rendererScreenModuleBuilder);

        return getPlayerApplicationComponent().addPlayerScreenComponent(
            rendererScreenModuleBuilder.build(),
            new PlayerPresenterScreenModule(),
            new PlayerManagerScreenModule(),
            new ProSportSystemScreenModule(this),
            new ProSportAdviserScreenModule(this),
            new ProSportRxScreenModule(this),
            new ProSportAuthScreenModule(),
            new ProSportAccountScreenModule());
    }

    @NonNull
    @Override
    protected ProSportScreenComponent onCreateProSportScreenComponent() {
        return _playerScreenComponent = onCreatePlayerScreenComponent();
    }

    @Nullable
    private PlayerScreenComponent _playerScreenComponent;
}
