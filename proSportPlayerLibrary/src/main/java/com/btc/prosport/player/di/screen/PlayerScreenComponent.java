package com.btc.prosport.player.di.screen;

import com.btc.prosport.di.screen.ProSportScreenComponent;
import com.btc.prosport.di.screen.ScreenScope;
import com.btc.prosport.di.screen.module.ProSportAccountScreenModule;
import com.btc.prosport.di.screen.module.ProSportAdviserScreenModule;
import com.btc.prosport.di.screen.module.ProSportAuthScreenModule;
import com.btc.prosport.di.screen.module.ProSportRxScreenModule;
import com.btc.prosport.di.screen.module.ProSportSystemScreenModule;
import com.btc.prosport.di.subscreen.module.ProSportRxSubscreenModule;
import com.btc.prosport.di.subscreen.module.ProSportSystemSubscreenModule;
import com.btc.prosport.player.di.screen.module.PlayerManagerScreenModule;
import com.btc.prosport.player.di.screen.module.PlayerPresenterScreenModule;
import com.btc.prosport.player.di.screen.module.PlayerRendererScreenModule;
import com.btc.prosport.player.di.subscreen.PlayerSubscreenComponent;
import com.btc.prosport.player.di.subscreen.module.PlayerManagerSubscreenScreenModule;
import com.btc.prosport.player.di.subscreen.module.PlayerPresenterSubscreenModule;
import com.btc.prosport.player.screen.activity.booking.BookingActivity;
import com.btc.prosport.player.screen.activity.feedback.FeedbackActivity;
import com.btc.prosport.player.screen.activity.ordersViewer.OrdersViewerActivity;
import com.btc.prosport.player.screen.activity.photosViewer.PhotosViewerActivity;
import com.btc.prosport.player.screen.activity.playerSettings.PlayerSettingsActivity;
import com.btc.prosport.player.screen.activity.sportComplexViewer.SportComplexViewerActivity;
import com.btc.prosport.player.screen.activity.sportComplexesViewer.SportComplexesViewerActivity;

import dagger.Subcomponent;

@Subcomponent(modules = {
    PlayerRendererScreenModule.class,
    PlayerPresenterScreenModule.class,
    PlayerManagerScreenModule.class,
    ProSportSystemScreenModule.class,
    ProSportAdviserScreenModule.class,
    ProSportRxScreenModule.class,
    ProSportAuthScreenModule.class,
    ProSportAccountScreenModule.class})
@ScreenScope
public interface PlayerScreenComponent extends ProSportScreenComponent {
    PlayerSubscreenComponent addPlayerSubscreenComponent(
        PlayerManagerSubscreenScreenModule playerManagerSubscreenScreenModule,
        PlayerPresenterSubscreenModule playerPresenterSubscreenModule,
        ProSportSystemSubscreenModule proSportSystemSubscreenModule,
        ProSportRxSubscreenModule proSportRxSubscreenModule);

    void inject(SportComplexViewerActivity activity);

    void inject(BookingActivity activity);

    void inject(OrdersViewerActivity activity);

    void inject(SportComplexesViewerActivity activity);

    void inject(PhotosViewerActivity photosViewerActivity);

    void inject(PlayerSettingsActivity activity);

    void inject(FeedbackActivity activity);
}
