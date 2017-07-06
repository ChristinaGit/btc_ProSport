package com.btc.prosport.player.soccer;

import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import lombok.val;

import com.btc.prosport.api.model.utility.UserRole;
import com.btc.prosport.core.manager.proSportServiceHelper.UserSettingsHelper;
import com.btc.prosport.di.application.module.ProSportAccountApplicationModule;
import com.btc.prosport.di.application.module.ProSportApiApplicationModule;
import com.btc.prosport.di.application.module.ProSportManagerApplicationModule;
import com.btc.prosport.di.application.module.ProSportSystemApplicationModule;
import com.btc.prosport.player.BasePlayerApplication;
import com.btc.prosport.player.core.SportTypeContext;
import com.btc.prosport.player.core.manager.notification.PlayerProSportNotificationManager;
import com.btc.prosport.player.di.application.DaggerPlayerApplicationComponent;
import com.btc.prosport.player.di.application.PlayerApplicationComponent;

public final class PlayerSoccerApplication extends BasePlayerApplication {
    @NonNull
    protected SportTypeContext getSportTypeContext() {
        return new SportTypeContext() {
            @Override
            public int getNotificationLargeIconId() {
                return R.mipmap.ic_launcher;
            }

            @Override
            public int getNotificationSmallIconId() {
                return R.drawable.ic_statusbar;
            }
        };
    }

    @Override
    @NonNull
    protected PlayerApplicationComponent onCreatePlayerApplicationComponent() {
        val userSettingsHelper =
            new UserSettingsHelper(PreferenceManager.getDefaultSharedPreferences(this), this);

        //@formatter:off
        return DaggerPlayerApplicationComponent
            .builder()
            .proSportSystemApplicationModule(new ProSportSystemApplicationModule(this, R.xml.tracker_config_player_soccer, !BuildConfig.DEBUG))
            .proSportManagerApplicationModule(new ProSportManagerApplicationModule(new PlayerProSportNotificationManager(this, userSettingsHelper, getSportTypeContext()),  userSettingsHelper))
            .proSportApiApplicationModule(new ProSportApiApplicationModule(UserRole.PLAYER, true))
            .proSportAccountApplicationModule(new ProSportAccountApplicationModule())
            .build();
        //@formatter:on
    }
}
