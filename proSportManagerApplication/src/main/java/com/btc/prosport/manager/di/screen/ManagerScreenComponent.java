package com.btc.prosport.manager.di.screen;

import com.btc.prosport.di.screen.ProSportScreenComponent;
import com.btc.prosport.di.screen.ScreenScope;
import com.btc.prosport.di.screen.module.ProSportAccountScreenModule;
import com.btc.prosport.di.screen.module.ProSportAdviserScreenModule;
import com.btc.prosport.di.screen.module.ProSportAuthScreenModule;
import com.btc.prosport.di.screen.module.ProSportRxScreenModule;
import com.btc.prosport.di.screen.module.ProSportSystemScreenModule;
import com.btc.prosport.di.subscreen.module.ProSportRxSubscreenModule;
import com.btc.prosport.di.subscreen.module.ProSportSystemSubscreenModule;
import com.btc.prosport.manager.di.screen.module.ManagerManagerScreenModule;
import com.btc.prosport.manager.di.screen.module.ManagerPresenterScreenModule;
import com.btc.prosport.manager.di.subscreen.ManagerSubscreenComponent;
import com.btc.prosport.manager.di.subscreen.module.ManagerPresenterSubscreenModule;
import com.btc.prosport.manager.screen.activity.orderEditor.OrderEditorActivity;
import com.btc.prosport.manager.screen.activity.feedback.FeedbackActivity;
import com.btc.prosport.manager.screen.activity.managerSettings.ManagerSettingsActivity;
import com.btc.prosport.manager.screen.activity.orderDetails.OrderDetailsActivity;
import com.btc.prosport.manager.screen.activity.playgroundEditor.PlaygroundEditorActivity;
import com.btc.prosport.manager.screen.activity.saleEditor.SaleEditorActivity;
import com.btc.prosport.manager.screen.activity.workspace.WorkspaceActivity;

import dagger.Subcomponent;

@Subcomponent(modules = {
    ManagerPresenterScreenModule.class,
    ManagerManagerScreenModule.class,
    ProSportSystemScreenModule.class,
    ProSportAdviserScreenModule.class,
    ProSportRxScreenModule.class,
    ProSportAuthScreenModule.class,
    ProSportAccountScreenModule.class})
@ScreenScope
public interface ManagerScreenComponent extends ProSportScreenComponent {
    ManagerSubscreenComponent addProSportSubscreenComponent(
        ManagerPresenterSubscreenModule managerPresenterSubscreenModule,
        ProSportSystemSubscreenModule proSportSystemSubscreenModule,
        ProSportRxSubscreenModule proSportRxSubscreenModule);

    void inject(WorkspaceActivity target);

    void inject(SaleEditorActivity target);

    void inject(OrderEditorActivity target);

    void inject(OrderDetailsActivity target);

    void inject(PlaygroundEditorActivity target);

    void inject(FeedbackActivity target);

    void inject(ManagerSettingsActivity target);
}
