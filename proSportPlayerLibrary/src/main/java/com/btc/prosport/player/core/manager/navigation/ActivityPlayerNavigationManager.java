package com.btc.prosport.player.core.manager.navigation;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.control.adviser.ResourceAdviser;
import com.btc.common.control.manager.navigation.ActivityNavigationManager;
import com.btc.common.extension.activity.ObservableActivity;
import com.btc.prosport.api.ProSportContract;
import com.btc.prosport.player.screen.activity.feedback.FeedbackActivity;
import com.btc.prosport.player.screen.activity.ordersViewer.OrdersViewerActivity;
import com.btc.prosport.player.screen.activity.photosViewer.PhotosViewerActivity;
import com.btc.prosport.player.screen.activity.playerSettings.PlayerSettingsActivity;
import com.btc.prosport.player.screen.activity.sportComplexViewer.SportComplexViewerActivity;
import com.btc.prosport.player.screen.activity.sportComplexesViewer.SportComplexesViewerActivity;

public class ActivityPlayerNavigationManager extends ActivityNavigationManager
    implements PlayerNavigationManager {
    public ActivityPlayerNavigationManager(
        @NonNull final ResourceAdviser resourceAdviser,
        @NonNull final ObservableActivity observableActivity) {
        super(Contracts.requireNonNull(resourceAdviser, "resourceAdviser == null"),
              Contracts.requireNonNull(observableActivity, "observableActivity == null"));
    }

    @Override
    public void navigateToFeedbackScreen() {
        getActivity().startActivity(FeedbackActivity
                                        .getIntent(getActivity()));
    }

    @Override
    public void navigateToHomeScreen() {
        getActivity().startActivity(SportComplexesViewerActivity
                                        .getIntent(getActivity())
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public void navigateToPlayerOrders() {
        getActivity().startActivity(OrdersViewerActivity
                                        .getIntent(getActivity())
                                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }

    @Override
    public void navigateToPlayerSettings() {
        PlayerSettingsActivity.start(getActivity());
    }

    @Override
    public void navigateToPlaygroundPhotos(final long playgroundId, final int position) {
        PhotosViewerActivity.startView(getActivity(),
                                       ProSportContract.getPlaygroundUri(playgroundId),
                                       position);
    }

    @Override
    public void navigateToPlaygrounds() {
        SportComplexesViewerActivity.start(getActivity());
    }

    @Override
    public void navigateToSportComplexDetails(final long sportComplexId) {
        SportComplexViewerActivity.startView(getActivity(),
                                             ProSportContract.getSportComplexUri(sportComplexId));
    }

    @Override
    public final void navigateToSportComplexPhotos(final long sportComplexId, final int position) {
        PhotosViewerActivity.startView(getActivity(),
                                       ProSportContract.getSportComplexUri(sportComplexId),
                                       position);
    }
}
