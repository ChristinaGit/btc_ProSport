package com.btc.prosport.player.core.manager.navigation;

public interface PlayerNavigationManager {
    void navigateToFeedbackScreen();

    void navigateToHomeScreen();

    void navigateToPlayerOrders();

    void navigateToPlayerSettings();

    void navigateToPlaygroundPhotos(long playgroundId, int position);

    void navigateToPlaygrounds();

    void navigateToSportComplexDetails(long playgroundId);

    void navigateToSportComplexPhotos(long playgroundId, int position);
}
