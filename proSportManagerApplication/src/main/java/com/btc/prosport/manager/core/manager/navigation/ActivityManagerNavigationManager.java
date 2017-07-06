package com.btc.prosport.manager.core.manager.navigation;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Func1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.adviser.ResourceAdviser;
import com.btc.common.control.manager.navigation.ActivityNavigationManager;
import com.btc.common.control.manager.navigation.ActivityNavigationResult;
import com.btc.common.extension.activity.ObservableActivity;
import com.btc.prosport.api.ProSportContract;
import com.btc.prosport.manager.core.result.PlacePickerResult;
import com.btc.prosport.manager.screen.activity.feedback.FeedbackActivity;
import com.btc.prosport.manager.screen.activity.managerSettings.ManagerSettingsActivity;
import com.btc.prosport.manager.screen.activity.orderDetails.OrderDetailsActivity;
import com.btc.prosport.manager.screen.activity.orderEditor.OrderEditorIntent;
import com.btc.prosport.manager.screen.activity.playgroundEditor.PlaygroundEditorIntent;
import com.btc.prosport.manager.screen.activity.saleEditor.SaleEditorActivity;
import com.btc.prosport.manager.screen.activity.workspace.WorkspaceIntent;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLngBounds;

@Accessors(prefix = "_")
public class ActivityManagerNavigationManager extends ActivityNavigationManager
    implements ManagerNavigationManager {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(ActivityManagerNavigationManager.class);

    private static final int _REQUEST_CODE_PLACE_PICKER;

    private static final int _REQUEST_CODE_GET_IMAGE;

    private static final int _REQUEST_CODE_IMAGE_CAPTURE;

    static {
        int i = 0;

        _REQUEST_CODE_PLACE_PICKER = ++i;
        _REQUEST_CODE_GET_IMAGE = ++i;
        _REQUEST_CODE_IMAGE_CAPTURE = ++i;
    }

    public ActivityManagerNavigationManager(
        @NonNull final ResourceAdviser resourceAdviser,
        @NonNull final ObservableActivity observableActivity) {
        super(
            Contracts.requireNonNull(resourceAdviser, "resourceAdviser == null"),
            Contracts.requireNonNull(observableActivity, "observableActivity == null"));
    }

    @Override
    public void navigateToFeedback() {
        getActivity().startActivity(FeedbackActivity
                                        .getIntent(getActivity())
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public void navigateToHomeScreen() {
        final val context = getActivity();

        context.startActivity(WorkspaceIntent
                                  .builder(context)
                                  .build()
                                  .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public void navigateToManagerOrders() {
        final val context = getActivity();

        context.startActivity(WorkspaceIntent
                                  .builder(context)
                                  .build()
                                  .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public void navigateToManagerSettings() {
        getActivity().startActivity(ManagerSettingsActivity.getIntent(getActivity()));
    }

    @Override
    public void navigateToOrder(final long orderId) {
        OrderDetailsActivity.startView(getActivity(), ProSportContract.getOrderUri(orderId));
    }

    @Override
    public void navigateToOrderCreator(final long playgroundId) {
        final val context = getActivity();

        context.startActivity(OrderEditorIntent.Insert.builder(context).build(playgroundId));
    }

    @Override
    public void navigateToOrderCreator(
        final long playgroundId,
        final long dateStart,
        final long dateEnd,
        final long timeStart,
        final long timeEnd) {
        final val context = getActivity();

        context.startActivity(OrderEditorIntent.Insert
                                  .builder(context)
                                  .setOrderDateStart(dateStart)
                                  .setOrderDateEnd(dateEnd)
                                  .setOrderTimeStart(timeStart)
                                  .setOrderTimeEnd(timeEnd)
                                  .build(playgroundId));
    }

    @NonNull
    @Override
    public Observable<PlacePickerResult> navigateToPlacePicker(
        @Nullable final LatLngBounds viewPort) {
        try {
            val intentBuilder = new PlacePicker.IntentBuilder();
            if (viewPort != null) {
                intentBuilder.setLatLngBounds(viewPort);
            }
            final val intent = intentBuilder.build(getActivity());
            val resultObservable = navigateToActivityForResult(intent, _REQUEST_CODE_PLACE_PICKER);
            return resultObservable.map(new Func1<ActivityNavigationResult, PlacePickerResult>() {
                @Override
                public PlacePickerResult call(final ActivityNavigationResult navigationResult) {
                    val data = navigationResult.getData();

                    if (navigationResult.getResultCode() == AppCompatActivity.RESULT_OK &&
                        data != null) {
                        val activity = getActivity();
                        val place = PlacePicker.getPlace(activity, data);
                        val latLng = place.getLatLng();
                        return new PlacePickerResult(
                            place.getId(),
                            place.getAddress().toString(),
                            latLng.latitude,
                            latLng.longitude,
                            place.getViewport());
                    } else {
                        throw new RuntimeException("Location not picked");
                    }
                }
            });
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
            exception) {
            return Observable.error(exception);
        }
    }

    @Override
    public void navigateToPlaygroundCreator() {
        // FIXME: 16.05.2017
    }

    @Override
    public void navigateToPlaygroundPriceEditor(final long playgroundId) {
        final val context = getActivity();
        context.startActivity(PlaygroundEditorIntent.builder(context).build(playgroundId));
    }

    @Override
    public void navigateToPlaygroundSchedule(final long playgroundId) {
        final val context = getActivity();
        final val intent = WorkspaceIntent
            .builder(context)
            .setPlaygroundId(playgroundId)
            .build()
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public void navigateToSaleCreator() {
        final val context = getActivity();

        context.startActivity(new Intent(context, SaleEditorActivity.class));
    }
}
