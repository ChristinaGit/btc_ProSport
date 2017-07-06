package com.btc.prosport.manager.core.manager.navigation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;

import com.btc.prosport.manager.core.result.PlacePickerResult;
import com.google.android.gms.maps.model.LatLngBounds;

public interface ManagerNavigationManager {
    void navigateToFeedback();

    void navigateToHomeScreen();

    void navigateToManagerOrders();

    void navigateToManagerSettings();

    void navigateToOrder(final long orderId);

    void navigateToOrderCreator(final long playgroundId);

    void navigateToOrderCreator(
        final long playgroundId, long dateStart, long dateEnd, long timeStart, long timeEnd);

    @NonNull
    Observable<PlacePickerResult> navigateToPlacePicker(
        @Nullable LatLngBounds viewPort);

    void navigateToPlaygroundCreator();

    void navigateToPlaygroundPriceEditor(final long playgroundId);

    void navigateToPlaygroundSchedule(final long playgroundId);

    void navigateToSaleCreator();
}
