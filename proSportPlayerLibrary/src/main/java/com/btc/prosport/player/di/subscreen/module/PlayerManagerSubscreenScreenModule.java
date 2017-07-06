package com.btc.prosport.player.di.subscreen.module;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.permission.PermissionManager;
import com.btc.common.extension.fragment.ObservableFragment;
import com.btc.prosport.di.subscreen.SubscreenScope;
import com.btc.prosport.player.core.manager.googleMap.FragmentGoogleMapManager;
import com.btc.prosport.player.core.manager.googleMap.GoogleMapManager;
import com.btc.prosport.player.core.manager.plugin.GoogleMapPlugin;

import dagger.Module;
import dagger.Provides;

@Module
@SubscreenScope
public class PlayerManagerSubscreenScreenModule {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(PlayerManagerSubscreenScreenModule.class);

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @Provides
    @SubscreenScope
    @Nullable
    public final GoogleMapManager provideGoogleMapManager(
        @NonNull final ObservableFragment observableFragment,
        @NonNull final PermissionManager permissionManager) {
        Contracts.requireNonNull(observableFragment, "observableFragment == null");
        Contracts.requireNonNull(permissionManager, "permissionManager == null");

        final GoogleMapManager result;

        if (_googleMapPlugin != null) {
            result = new FragmentGoogleMapManager(_googleMapPlugin.getMapViewId(),
                                                  observableFragment,
                                                  permissionManager);
        } else {
            Log.w(_LOG_TAG,
                  GoogleMapManager.class.getSimpleName() + " is requested, but " +
                  GoogleMapPlugin.class.getSimpleName() + " is not added.");

            result = null;
        }

        return result;
    }

    @Nullable
    private GoogleMapPlugin _googleMapPlugin;

    public static final class Builder {
        @NonNull
        public final Builder addGoogleMapPlugin(@NonNull final GoogleMapPlugin googleMapPlugin) {
            Contracts.requireNonNull(googleMapPlugin, "googleMapPlugin == null");

            _googleMapPlugin = googleMapPlugin;

            return this;
        }

        @NonNull
        public final PlayerManagerSubscreenScreenModule build() {
            final val module = new PlayerManagerSubscreenScreenModule();

            module._googleMapPlugin = _googleMapPlugin;

            return module;
        }

        @Nullable
        private GoogleMapPlugin _googleMapPlugin;
    }
}
