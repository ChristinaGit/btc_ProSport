package com.btc.prosport.player.core.adapter.photos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.delegate.loading.SimpleVisibilityHandler;
import com.btc.common.extension.delegate.loading.VisibilityHandler;
import com.btc.common.utility.ThemeUtils;
import com.btc.prosport.player.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Accessors(prefix = "_")
public final class PhotosAdapter extends PagerAdapter {
    private static final String _LOG_TAG = ConstantBuilder.logTag(PhotosAdapter.class);

    @Override
    public int getCount() {
        return getPhotosUris().size();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final val context = container.getContext();
        final val inflater = LayoutInflater.from(context);
        final val view = inflater.inflate(R.layout.layout_photo_item, container, false);

        final val uri = getPhotosUris().get(position);

        container.addView(view);

        final val photoView = (ImageView) view.findViewById(R.id.photo);
        final val photoLoadingView = view.findViewById(R.id.photo_loading);
        final val photoLoadingErrorView = (TextView) view.findViewById(R.id.photo_loading_error);

        initializeErrorView(photoLoadingErrorView);

        final val loadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(photoView)
            .setLoadingView(photoLoadingView)
            .setErrorView(photoLoadingErrorView)
            .setContentVisibilityHandler(_contentVisibilityHandler)
            .setLoadingVisibilityHandler(_progressVisibilityHandler)
            .build();

        loadingViewDelegate.showLoading();

        view.setTag(R.id.tag_view_photo_position, position);
        view.setTag(R.id.tag_view_photo_uri, uri);
        view.setOnClickListener(_risePhotoClickOnClick);

        Glide
            .with(context)
            .load(uri)
            .animate(R.anim.fade_in_long)
            .centerCrop()
            .listener(new PhotoRequestListener(loadingViewDelegate))
            .into(photoView);

        return view;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        if (object instanceof View) {
            container.removeView((View) object);
        }
    }

    @Override
    public final boolean isViewFromObject(final View view, final Object object) {
        boolean isViewFromObject = false;

        if (object instanceof View) {
            final val objectUri = (String) ((View) object).getTag(R.id.tag_view_photo_uri);
            final val viewUri = (String) view.getTag(R.id.tag_view_photo_uri);

            isViewFromObject = Objects.equals(objectUri, viewUri);
        }

        return isViewFromObject;
    }

    @Override
    public final int getItemPosition(final Object object) {
        int itemPosition = super.getItemPosition(object);

        if (object instanceof View) {
            final int position = (int) ((View) object).getTag(R.id.tag_view_photo_position);
            if (position < getCount()) {
                itemPosition = position;
            } else {
                itemPosition = POSITION_NONE;
            }
        }

        return itemPosition;
    }

    @NonNull
    private final VisibilityHandler _contentVisibilityHandler =
        new SimpleVisibilityHandler(View.INVISIBLE);

    @Getter
    @NonNull
    private final List<String> _photosUris = new ArrayList<>();

    @NonNull
    private final VisibilityHandler _progressVisibilityHandler = new ProgressVisibilityHandler();

    @Setter
    @Nullable
    private OnPhotoClickListener _onPhotoClickListener;

    @NonNull
    private final View.OnClickListener _risePhotoClickOnClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final int position = (int) v.getTag(R.id.tag_view_photo_position);
            if (_onPhotoClickListener != null) {
                _onPhotoClickListener.onPhotoClick(position);
            }
        }
    };

    private void initializeErrorView(@NonNull final TextView errorView) {
        Contracts.requireNonNull(errorView, "errorView == null");

        final val context = errorView.getContext();
        final val errorIcon = ContextCompat.getDrawable(context, R.drawable.ic_material_error);
        final val tintList =
            ThemeUtils.resolveColorStateList(context, android.R.attr.textColorSecondary);
        if (tintList != null) {
            DrawableCompat.setTint(errorIcon, tintList.getDefaultColor());
        }
        errorView.setCompoundDrawablesRelativeWithIntrinsicBounds(errorIcon, null, null, null);
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(int position);
    }

    private static final class PhotoRequestListener
        implements RequestListener<String, GlideDrawable> {
        public PhotoRequestListener(
            @NonNull final LoadingViewDelegate loadingViewDelegate) {
            Contracts.requireNonNull(loadingViewDelegate, "loadingViewDelegate == null");

            _loadingViewDelegate = loadingViewDelegate;
        }

        @Override
        public final boolean onException(
            final Exception e,
            final String model,
            final Target<GlideDrawable> target,
            final boolean isFirstResource) {
            _loadingViewDelegate.showError();

            return false;
        }

        @Override
        public final boolean onResourceReady(
            final GlideDrawable resource,
            final String model,
            final Target<GlideDrawable> target,
            final boolean isFromMemoryCache,
            final boolean isFirstResource) {
            _loadingViewDelegate.showContent();

            return false;
        }

        @NonNull
        private final LoadingViewDelegate _loadingViewDelegate;
    }
}
