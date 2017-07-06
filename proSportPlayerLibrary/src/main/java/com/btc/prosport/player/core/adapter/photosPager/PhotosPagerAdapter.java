package com.btc.prosport.player.core.adapter.photosPager;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.delegate.loading.SimpleVisibilityHandler;
import com.btc.common.extension.delegate.loading.VisibilityHandler;
import com.btc.common.utility.ThemeUtils;
import com.btc.prosport.player.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public final class PhotosPagerAdapter extends PagerAdapter {
    @Override
    public int getCount() {
        return getPhotosUris().size();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final val context = container.getContext();
        final val inflater = LayoutInflater.from(context);
        final val view = inflater.inflate(R.layout.layout_photo_page_item, container, false);

        final val uri = getPhotosUris().get(position);
        container.addView(view);

        final val photoView = (SubsamplingScaleImageView) view.findViewById(R.id.photo);
        final val photoLoadingView = view.findViewById(R.id.photo_loading);
        final val photoErrorView = (TextView) view.findViewById(R.id.photo_error);

        initializeErrorView(photoErrorView);

        final val loadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(photoView)
            .setLoadingView(photoLoadingView)
            .setErrorView(photoErrorView)
            .setContentVisibilityHandler(_contentVisibilityHandler)
            .setLoadingVisibilityHandler(_progressVisibilityHandler)
            .build();

        loadingViewDelegate.showLoading();

        Glide
            .with(context)
            .load(uri)
            .asBitmap()
            .listener(new PhotoRequestListener(loadingViewDelegate))
            .into(new ViewTarget<SubsamplingScaleImageView, Bitmap>(photoView) {
                @Override
                public void onResourceReady(
                    final Bitmap resource, final GlideAnimation<? super Bitmap> glideAnimation) {
                    getView().setImage(ImageSource.bitmap(resource));
                }
            });

        return view;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        if (object instanceof View) {
            container.removeView((View) object);
        }
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view == object;
    }

    @NonNull
    private final VisibilityHandler _contentVisibilityHandler =
        new SimpleVisibilityHandler(View.INVISIBLE);

    @Getter
    @NonNull
    private final List<String> _photosUris = new ArrayList<>();

    @NonNull
    private final VisibilityHandler _progressVisibilityHandler = new ProgressVisibilityHandler();

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

    private static final class PhotoRequestListener implements RequestListener<String, Bitmap> {
        public PhotoRequestListener(
            @NonNull final LoadingViewDelegate loadingViewDelegate) {
            Contracts.requireNonNull(loadingViewDelegate, "loadingViewDelegate == null");

            _loadingViewDelegate = loadingViewDelegate;
        }

        @Override
        public boolean onException(
            final Exception e,
            final String model,
            final Target<Bitmap> target,
            final boolean isFirstResource) {
            _loadingViewDelegate.showError();

            return false;
        }

        @Override
        public boolean onResourceReady(
            final Bitmap resource,
            final String model,
            final Target<Bitmap> target,
            final boolean isFromMemoryCache,
            final boolean isFirstResource) {
            _loadingViewDelegate.showContent();

            return false;
        }

        @NonNull
        private final LoadingViewDelegate _loadingViewDelegate;
    }
}
