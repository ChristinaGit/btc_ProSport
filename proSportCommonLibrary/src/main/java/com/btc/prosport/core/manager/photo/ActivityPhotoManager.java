package com.btc.prosport.core.manager.photo;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.permission.CheckPermissionsResult;
import com.btc.common.control.manager.permission.PermissionManager;
import com.btc.common.control.manager.permission.RequestPermissionsResult;
import com.btc.common.extension.activity.ObservableActivity;
import com.btc.common.extension.exception.InsufficientPermissionException;
import com.btc.prosport.common.R;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.result.PhotoResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public class ActivityPhotoManager implements PhotoManager {
    private static final String _PREFIX = "ProSport";

    private static final String _SUFFIX = ".jpg";

    public ActivityPhotoManager(
        @NonNull final PermissionManager permissionManager,
        @NonNull final ObservableActivity observableActivity,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(permissionManager, "permissionManager == null");
        Contracts.requireNonNull(observableActivity, "observableActivity == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        _permissionManager = permissionManager;
        _observableActivity = observableActivity;
        _proSportNavigationManager = proSportNavigationManager;
    }

    @NonNull
    @Override
    public CheckPermissionsResult checkExternalStoragePermissions() {
        return getPermissionManager().checkPermissions(getExternalStoragePermissions());
    }

    @NonNull
    @Override
    public final List<String> getExternalStoragePermissions() {
        val permissions = new ArrayList<String>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissions;
    }

    @NonNull
    @Override
    public Observable<PhotoResult> getPhoto() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(final Subscriber<? super Integer> subscriber) {
                new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.image_picker_title)
                    .setItems(R.array.image_picker_items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(which);
                                subscriber.onCompleted();
                            }
                        }
                    })
                    .create()
                    .show();
            }
        }).flatMap(new Func1<Integer, Observable<PhotoResult>>() {
            @Override
            public Observable<PhotoResult> call(final Integer integer) {
                val proSportNavigationManager = getProSportNavigationManager();
                switch (integer) {
                    case 0:
                        return proSportNavigationManager.navigateToImagePicker();
                    case 1:
                        final val values = new ContentValues(1);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                        final val insertUri = getActivity()
                            .getContentResolver()
                            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                        if (insertUri != null) {
                            return proSportNavigationManager.navigateToImageCapture(insertUri);
                        } else {
                            return Observable.error(new RuntimeException(
                                "Failed to create content uri"));
                        }
                    default:
                        return Observable.error(new RuntimeException("Item not exists"));
                }
            }
        });
    }

    @Nullable
    @Override
    public String getPhotoFromGalleryAbsolutePath(@NonNull final Uri uri) {
        Contracts.requireNonNull(uri, "uri == null");

        final String photoPath;

        final String[] projection = {MediaStore.Images.Media.DATA};
        final val contentResolver = getActivity().getContentResolver();
        try (final val cursor = contentResolver.query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final val columnIndex = cursor.getColumnIndex(projection[0]);
                photoPath = cursor.getString(columnIndex);
            } else {
                photoPath = null;
            }
        }

        return photoPath;
    }

    @NonNull
    @Override
    public Observable<PhotoResult> getPhotoWithPermissions(final boolean allowAskPermissions) {
        if (allowAskPermissions) {
            return requestMakePhotoPermissions().flatMap(new Func1<RequestPermissionsResult,
                Observable<PhotoResult>>() {
                @Override
                public Observable<PhotoResult> call(final RequestPermissionsResult result) {
                    if (result.isAllGranted()) {
                        return getPhoto();
                    } else {
                        final val error =
                            new InsufficientPermissionException(result.getDeniedPermissions());
                        return Observable.error(error);
                    }
                }
            });
        } else {
            final val checkResult = checkExternalStoragePermissions();
            if (checkResult.isAllGranted()) {
                return getPhoto();
            } else {
                final val error =
                    new InsufficientPermissionException(checkResult.getDeniedPermissions());
                return Observable.error(error);
            }
        }
    }

    @NonNull
    protected final AppCompatActivity getActivity() {
        return getObservableActivity().asActivity();
    }

    @NonNull
    protected final Observable<RequestPermissionsResult> requestMakePhotoPermissions() {
        return getPermissionManager()
            .createPermissionsRequest()
            .addPermissions(getExternalStoragePermissions())
            .show();
    }

    @Getter
    @NonNull
    private final ObservableActivity _observableActivity;

    @Getter
    @NonNull
    private final PermissionManager _permissionManager;

    @Getter
    @NonNull
    private final ProSportNavigationManager _proSportNavigationManager;

    @NonNull
    private File createImageFile(@NonNull final Context context)
        throws IOException {
        Contracts.requireNonNull(context, "context == null");

        final File storageDir;
        if (Environment.isExternalStorageEmulated()) {
            storageDir = context.getExternalCacheDir();
        } else {
            storageDir = context.getCacheDir();
        }

        return File.createTempFile(_PREFIX, _SUFFIX, storageDir);
    }
}
