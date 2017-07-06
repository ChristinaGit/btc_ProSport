package com.btc.prosport.player.di.screen.module;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.prosport.di.screen.ScreenScope;
import com.btc.prosport.player.core.manager.render.SportComplexInfoRenderer;

import dagger.Module;
import dagger.Provides;

@Module
@ScreenScope
public class PlayerRendererScreenModule {
    private static final String _LOG_TAG = ConstantBuilder.logTag(PlayerRendererScreenModule.class);

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @Provides
    @ScreenScope
    @Nullable
    public final SportComplexInfoRenderer provideSportComplexInfoRenderer() {
        if (_sportComplexInfoRenderer == null) {
            Log.w(
                _LOG_TAG,
                SportComplexInfoRenderer.class.getSimpleName() +
                " is requested, but it is not added to " +
                PlayerRendererScreenModule.class.getSimpleName());
        }

        return _sportComplexInfoRenderer;
    }

    @Nullable
    private SportComplexInfoRenderer _sportComplexInfoRenderer;

    public static final class Builder {
        @NonNull
        public final Builder addSportComplexInfoRenderer(
            @Nullable final SportComplexInfoRenderer renderer) {
            Contracts.requireNonNull(renderer, "renderer == null");

            _sportComplexInfoRenderer = renderer;

            return this;
        }

        @NonNull
        public final PlayerRendererScreenModule build() {
            final val module = new PlayerRendererScreenModule();

            module._sportComplexInfoRenderer = _sportComplexInfoRenderer;

            return module;
        }

        @Nullable
        private SportComplexInfoRenderer _sportComplexInfoRenderer;
    }
}
