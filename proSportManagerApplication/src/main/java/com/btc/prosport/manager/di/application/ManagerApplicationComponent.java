package com.btc.prosport.manager.di.application;

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
import com.btc.prosport.manager.di.screen.ManagerScreenComponent;
import com.btc.prosport.manager.di.screen.module.ManagerManagerScreenModule;
import com.btc.prosport.manager.di.screen.module.ManagerPresenterScreenModule;

import dagger.Component;

@Component(modules = {
    ProSportSystemApplicationModule.class,
    ProSportManagerApplicationModule.class,
    ProSportApiApplicationModule.class,
    ProSportAccountApplicationModule.class})
@ApplicationScope
public interface ManagerApplicationComponent extends ProSportApplicationComponent {
    ManagerScreenComponent addManagerScreenComponent(
        ManagerPresenterScreenModule managerPresenterScreenModule,
        ManagerManagerScreenModule proSportManagerScreenModule,
        ProSportSystemScreenModule proSportSystemScreenModule,
        ProSportAdviserScreenModule proSportAdviserScreenModule,
        ProSportRxScreenModule proSportRxScreenModule,
        ProSportAuthScreenModule proSportAuthScreenModule,
        ProSportAccountScreenModule proSportAccountScreenModule);
}
