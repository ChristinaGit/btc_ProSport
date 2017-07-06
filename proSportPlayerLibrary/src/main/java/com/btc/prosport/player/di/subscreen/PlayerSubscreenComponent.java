package com.btc.prosport.player.di.subscreen;

import com.btc.prosport.di.subscreen.ProSportSubscreenComponent;
import com.btc.prosport.di.subscreen.SubscreenScope;
import com.btc.prosport.di.subscreen.module.ProSportRxSubscreenModule;
import com.btc.prosport.di.subscreen.module.ProSportSystemSubscreenModule;
import com.btc.prosport.player.di.subscreen.module.PlayerManagerSubscreenScreenModule;
import com.btc.prosport.player.di.subscreen.module.PlayerPresenterSubscreenModule;
import com.btc.prosport.player.screen.fragment.IntervalsFragment;
import com.btc.prosport.player.screen.fragment.OrdersListFragment;
import com.btc.prosport.player.screen.fragment.PlaygroundsListFragment;
import com.btc.prosport.player.screen.fragment.SportComplexDetailFragment;
import com.btc.prosport.player.screen.fragment.SportComplexInfoFragment;
import com.btc.prosport.player.screen.fragment.SportComplexesListFragment;
import com.btc.prosport.player.screen.fragment.SportComplexesMapFragment;

import dagger.Subcomponent;

@Subcomponent(modules = {
    PlayerManagerSubscreenScreenModule.class,
    PlayerPresenterSubscreenModule.class,
    ProSportSystemSubscreenModule.class,
    ProSportRxSubscreenModule.class})
@SubscreenScope
public interface PlayerSubscreenComponent extends ProSportSubscreenComponent {
    void inject(PlaygroundsListFragment target);

    void inject(SportComplexDetailFragment target);

    void inject(SportComplexesListFragment target);

    void inject(OrdersListFragment target);

    void inject(SportComplexInfoFragment target);

    void inject(SportComplexesMapFragment target);

    void inject(IntervalsFragment target);
}
