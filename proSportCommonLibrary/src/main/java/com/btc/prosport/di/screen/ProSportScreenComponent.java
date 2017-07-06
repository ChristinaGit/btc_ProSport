package com.btc.prosport.di.screen;

import com.btc.prosport.di.screen.module.ProSportAccountScreenModule;
import com.btc.prosport.di.screen.module.ProSportAdviserScreenModule;
import com.btc.prosport.di.screen.module.ProSportAuthScreenModule;
import com.btc.prosport.di.screen.module.ProSportManagerScreenModule;
import com.btc.prosport.di.screen.module.ProSportPresenterScreenModule;
import com.btc.prosport.di.screen.module.ProSportRxScreenModule;
import com.btc.prosport.di.screen.module.ProSportSystemScreenModule;
import com.btc.prosport.di.subscreen.ProSportSubscreenComponent;
import com.btc.prosport.di.subscreen.module.ProSportPresenterSubscreenModule;
import com.btc.prosport.di.subscreen.module.ProSportRxSubscreenModule;
import com.btc.prosport.di.subscreen.module.ProSportSystemSubscreenModule;
import com.btc.prosport.screen.activity.proSportVerification.ProSportVerificationActivity;

import dagger.Subcomponent;

@Subcomponent(modules = {
    ProSportSystemScreenModule.class,
    ProSportAdviserScreenModule.class,
    ProSportRxScreenModule.class,
    ProSportPresenterScreenModule.class,
    ProSportAuthScreenModule.class,
    ProSportAccountScreenModule.class,
    ProSportManagerScreenModule.class})
@ScreenScope
public interface ProSportScreenComponent {
    ProSportSubscreenComponent addProSportSubscreenComponent(
        ProSportSystemSubscreenModule proSportSystemSubscreenModule,
        ProSportPresenterSubscreenModule proSportPresenterSubscreenModule,
        ProSportRxSubscreenModule proSportRxSubscreenModule);

    void inject(ProSportVerificationActivity target);
}
