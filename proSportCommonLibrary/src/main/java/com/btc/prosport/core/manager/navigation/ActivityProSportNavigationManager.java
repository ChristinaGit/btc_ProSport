package com.btc.prosport.core.manager.navigation;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Func1;

import com.btc.common.contract.Contracts;
import com.btc.common.control.adviser.ResourceAdviser;
import com.btc.common.control.manager.navigation.ActivityNavigationManager;
import com.btc.common.control.manager.navigation.ActivityNavigationResult;
import com.btc.common.extension.activity.ObservableActivity;
import com.btc.common.utility.SettingsIntentUtils;
import com.btc.prosport.core.result.PhotoResult;
import com.btc.prosport.screen.fragment.proSportAuth.proSportAdditionalInfo
    .ProSportAdditionalInfoFragment;
import com.btc.prosport.screen.fragment.proSportAuth.proSportLogIn.ProSportLogInFragment;
import com.btc.prosport.screen.fragment.proSportAuth.proSportReLogIn.ProSportReLogInFragment;
import com.btc.prosport.screen.fragment.proSportAuth.proSportSignUp.ProSportSignUpFragment;

@Accessors(prefix = "_")
public class ActivityProSportNavigationManager extends ActivityNavigationManager
    implements ProSportNavigationManager {
    private static final int _REQUEST_CODE_GET_IMAGE;

    private static final int _REQUEST_CODE_IMAGE_CAPTURE;

    static {
        int i = 0;

        _REQUEST_CODE_GET_IMAGE = ++i;
        _REQUEST_CODE_IMAGE_CAPTURE = ++i;
    }

    public ActivityProSportNavigationManager(
        @NonNull final ResourceAdviser resourceAdviser,
        @NonNull final ObservableActivity observableActivity,
        final int containerId) {
        super(Contracts.requireNonNull(resourceAdviser, "resourceAdviser == null"),
              Contracts.requireNonNull(observableActivity, "observableActivity == null"));
        _containerId = containerId;
    }

    @Override
    public void navigateToAdditionalInfo() {
        ProSportAdditionalInfoFragment.startView(getActivity().getSupportFragmentManager(),
                                                 getContainerId());
    }

    @Override
    public boolean navigateToApplicationSettings() {
        final boolean started;

        final val activity = getActivity();

        final val settingsIntent =
            SettingsIntentUtils.getApplicationDetailsSettingsIntent(activity);

        if (settingsIntent.resolveActivity(activity.getPackageManager()) != null) {
            started = true;

            activity.startActivity(settingsIntent);
        } else {
            started = false;
        }

        return started;
    }

    @NonNull
    @Override
    public Observable<PhotoResult> navigateToImageCapture(@NonNull final Uri path) {
        val cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, path);
        return navigateToActivityForResult(cameraIntent,
                                           _REQUEST_CODE_IMAGE_CAPTURE).map(new Func1<ActivityNavigationResult, PhotoResult>() {

            @Override
            public PhotoResult call(final ActivityNavigationResult result) {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    return new PhotoResult(path);
                } else {
                    return new PhotoResult(null);
                }
            }
        });
    }

    @NonNull
    @Override
    public Observable<PhotoResult> navigateToImagePicker() {
        val activity = getActivity();
        val intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            return navigateToActivityForResult(intent,
                                               _REQUEST_CODE_GET_IMAGE).map(new Func1<ActivityNavigationResult, PhotoResult>() {
                @Override
                public PhotoResult call(final ActivityNavigationResult result) {
                    val resultIntent = result.getData();
                    if (resultIntent != null &&
                        result.getResultCode() == AppCompatActivity.RESULT_OK) {

                        return new PhotoResult(resultIntent.getData());
                    } else {
                        return new PhotoResult(null);
                    }
                }
            });
        } else {
            return Observable.error(new RuntimeException("Component not resolved"));
        }
    }

    @Override
    public void navigateToLogIn(
        @Nullable final String phoneNumber, final boolean allowRegistration) {
        ProSportLogInFragment.startView(getActivity().getSupportFragmentManager(),
                                        getContainerId(),
                                        phoneNumber,
                                        allowRegistration);
    }

    @Override
    public void navigateToReLogIn(@Nullable final String phoneNumber) {
        ProSportReLogInFragment.startView(getActivity().getSupportFragmentManager(),
                                          getContainerId(),
                                          phoneNumber);
    }

    @Override
    public void navigateToSignUp(@Nullable final String phoneNumber) {
        ProSportSignUpFragment.startView(getActivity().getSupportFragmentManager(),
                                         getContainerId(),
                                         phoneNumber);
    }

    @IdRes
    @Getter(AccessLevel.PROTECTED)
    private final int _containerId;
}
