package com.btc.prosport.di.application;

import com.btc.prosport.core.manager.account.ProSportAuthenticatorService;
import com.btc.prosport.core.manager.firebaseMessaging.ProSportInstanceIdService;
import com.btc.prosport.core.manager.firebaseMessaging.ProSportMessagingService;
import com.btc.prosport.core.manager.firebaseMessaging.SubscribeJobService;
import com.btc.prosport.core.manager.firebaseMessaging.UnsubscribeJobService;
import com.btc.prosport.core.manager.notification.NotificationEventBroadcastReceiver;
import com.btc.prosport.di.application.module.ProSportAccountApplicationModule;
import com.btc.prosport.di.application.module.ProSportApiApplicationModule;
import com.btc.prosport.di.application.module.ProSportManagerApplicationModule;
import com.btc.prosport.di.application.module.ProSportSystemApplicationModule;
import com.btc.prosport.di.screen.ProSportScreenComponent;
import com.btc.prosport.di.screen.module.ProSportAccountScreenModule;
import com.btc.prosport.di.screen.module.ProSportAdviserScreenModule;
import com.btc.prosport.di.screen.module.ProSportAuthScreenModule;
import com.btc.prosport.di.screen.module.ProSportManagerScreenModule;
import com.btc.prosport.di.screen.module.ProSportPresenterScreenModule;
import com.btc.prosport.di.screen.module.ProSportRxScreenModule;
import com.btc.prosport.di.screen.module.ProSportSystemScreenModule;

import dagger.Component;

@Component(modules = {
    ProSportSystemApplicationModule.class,
    ProSportManagerApplicationModule.class,
    ProSportApiApplicationModule.class,
    ProSportAccountApplicationModule.class})
@ApplicationScope
public interface ProSportApplicationComponent {
    ProSportScreenComponent addProSportScreenComponent(
        ProSportSystemScreenModule proSportSystemScreenModule,
        ProSportAdviserScreenModule proSportAdviserScreenModule,
        ProSportRxScreenModule proSportRxScreenModule,
        ProSportPresenterScreenModule proSportPresenterScreenModule,
        ProSportAuthScreenModule proSportAuthScreenModule,
        ProSportAccountScreenModule proSportAccountScreenModule,
        ProSportManagerScreenModule proSportManagerScreenModule);

    void inject(ProSportAuthenticatorService target);

    void inject(ProSportInstanceIdService target);

    void inject(SubscribeJobService target);

    void inject(UnsubscribeJobService target);

    void inject(ProSportMessagingService target);

    void inject(NotificationEventBroadcastReceiver target);
}
