package com.btc.prosport.player.di.application;

import com.btc.prosport.di.application.ApplicationScope;
import com.btc.prosport.di.application.ProSportApplicationComponent;
import com.btc.prosport.di.application.module.ProSportAccountApplicationModule;
import com.btc.prosport.di.application.module.ProSportApiApplicationModule;
import com.btc.prosport.di.application.module.ProSportManagerApplicationModule;
import com.btc.prosport.di.application.module.ProSportSystemApplicationModule;
import com.btc.prosport.di.screen.module.ProSportAccountScreenModule;
import com.btc.prosport.di.screen.module.ProSportAdviserScreenModule;
import com.btc.prosport.di.screen.module.ProSportAuthScreenModule;
import com.btc.prosport.di.screen.module.ProSportRxScreenModule;
import com.btc.prosport.di.screen.module.ProSportSystemScreenModule;
import com.btc.prosport.player.di.screen.PlayerScreenComponent;
import com.btc.prosport.player.di.screen.module.PlayerManagerScreenModule;
import com.btc.prosport.player.di.screen.module.PlayerPresenterScreenModule;
import com.btc.prosport.player.di.screen.module.PlayerRendererScreenModule;

import dagger.Component;

@Component(modules = {
    ProSportSystemApplicationModule.class,
    ProSportManagerApplicationModule.class,
    ProSportApiApplicationModule.class,
    ProSportAccountApplicationModule.class})
@ApplicationScope
public interface PlayerApplicationComponent extends ProSportApplicationComponent {
    PlayerScreenComponent addPlayerScreenComponent(
        PlayerRendererScreenModule playerRendererScreenModule,
        PlayerPresenterScreenModule playerPresenterScreenModule,
        PlayerManagerScreenModule playerManagerScreenModule,
        ProSportSystemScreenModule proSportSystemScreenModule,
        ProSportAdviserScreenModule proSportAdviserScreenModule,
        ProSportRxScreenModule proSportRxScreenModule,
        ProSportAuthScreenModule proSportAuthScreenModule,
        ProSportAccountScreenModule proSportAccountScreenModule);
}
