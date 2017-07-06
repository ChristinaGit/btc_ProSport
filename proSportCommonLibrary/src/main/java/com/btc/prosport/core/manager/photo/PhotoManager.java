package com.btc.prosport.core.manager.photo;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;

import com.btc.common.control.manager.permission.CheckPermissionsResult;
import com.btc.prosport.core.result.PhotoResult;

import java.util.List;

public interface PhotoManager {
    @NonNull
    CheckPermissionsResult checkExternalStoragePermissions();

    @NonNull
    List<String> getExternalStoragePermissions();

    @NonNull
    Observable<PhotoResult> getPhoto();

    @Nullable
    String getPhotoFromGalleryAbsolutePath(@NonNull final Uri uri);

    @NonNull
    Observable<PhotoResult> getPhotoWithPermissions(boolean allowAskPermissions);
}
