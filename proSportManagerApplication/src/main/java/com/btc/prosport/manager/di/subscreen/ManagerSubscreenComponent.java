package com.btc.prosport.manager.di.subscreen;

import com.btc.prosport.di.subscreen.ProSportSubscreenComponent;
import com.btc.prosport.di.subscreen.SubscreenScope;
import com.btc.prosport.di.subscreen.module.ProSportRxSubscreenModule;
import com.btc.prosport.di.subscreen.module.ProSportSystemSubscreenModule;
import com.btc.prosport.manager.di.subscreen.module.ManagerPresenterSubscreenModule;
import com.btc.prosport.manager.screen.fragment.OrderViewerFragment;
import com.btc.prosport.manager.screen.fragment.PlaygroundAttributesEditorFragment;
import com.btc.prosport.manager.screen.fragment.PlaygroundCoveringEditorFragment;
import com.btc.prosport.manager.screen.fragment.PlaygroundDimensionsEditorFragment;
import com.btc.prosport.manager.screen.fragment.SalesListFragment;
import com.btc.prosport.manager.screen.fragment.ScheduleFragment;
import com.btc.prosport.manager.screen.fragment.playgroundEditor.playgroundGeneralInfoEditor
    .PlaygroundGeneralInfoEditorFragment;
import com.btc.prosport.manager.screen.fragment.playgroundEditor.playgroundScheduleEditor
    .PlaygroundScheduleEditorFragment;
import com.btc.prosport.manager.screen.fragment.playgroundOrdersList.OrdersListFragment;
import com.btc.prosport.manager.screen.fragment.priceEditor.PriceEditorFragment;

import dagger.Subcomponent;

@Subcomponent(modules = {
    ManagerPresenterSubscreenModule.class,
    ProSportSystemSubscreenModule.class,
    ProSportRxSubscreenModule.class})
@SubscreenScope
public interface ManagerSubscreenComponent extends ProSportSubscreenComponent {
    void inject(OrdersListFragment target);

    void inject(SalesListFragment target);

    void inject(PriceEditorFragment target);

    void inject(OrderViewerFragment target);

    void inject(PlaygroundGeneralInfoEditorFragment target);

    void inject(ScheduleFragment target);

    void inject(PlaygroundScheduleEditorFragment target);

    void inject(PlaygroundDimensionsEditorFragment target);

    void inject(PlaygroundCoveringEditorFragment target);

    void inject(PlaygroundAttributesEditorFragment target);
}
