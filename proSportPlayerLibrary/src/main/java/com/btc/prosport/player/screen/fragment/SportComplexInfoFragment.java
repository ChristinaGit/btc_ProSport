package com.btc.prosport.player.screen.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.extension.view.PageIndicatorView;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.api.model.SportComplexInfo;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.core.utility.ProSportFormat;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.adapter.photos.PhotosAdapter;
import com.btc.prosport.player.core.utility.SportComplexUtils;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.screen.SportComplexInfoScreen;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class SportComplexInfoFragment extends BasePlayerFragment
    implements SportComplexInfoScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SportComplexInfoFragment.class);

    public final void setSportComplexId(@Nullable final Long sportComplexId) {
        if (!Objects.equals(_sportComplexId, sportComplexId)) {
            _sportComplexId = sportComplexId;

            onSportComplexIdChanged();
        }
    }

    @Override
    @CallSuper
    public void bindViews(@NonNull final View source) {
        super.bindViews(Contracts.requireNonNull(source, "source == null"));

        _sportComplexInfoContainerView = source.findViewById(R.id.sport_complex_info_container);
        _sportComplexLoadingView =
            (ContentLoaderProgressBar) source.findViewById(R.id.sport_complex_loading);
        _sportComplexNoContentView = source.findViewById(R.id.sport_complex_no_content);
        _sportComplexLadingErrorView = source.findViewById(R.id.sport_complex_loading_error);
        _sportComplexNameView = (TextView) source.findViewById(R.id.sport_complex_name);
        _sportComplexAddressView = (TextView) source.findViewById(R.id.sport_complex_address);
        _sportComplexSubwayStationsView =
            (TextView) source.findViewById(R.id.sport_complex_subway_stations);
        _sportComplexPhoneView = (TextView) source.findViewById(R.id.sport_complex_phone);
        _sportComplexWorkTimeView = (TextView) source.findViewById(R.id.sport_complex_work_time);
        _sportComplexPhotosView = (ViewPager) source.findViewById(R.id.sport_complex_photos);
        _sportComplexPhotosIndicatorView =
            (PageIndicatorView) source.findViewById(R.id.sport_complex_photos_indicator);
    }

    @Nullable
    @Override
    public final View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view = inflater.inflate(R.layout.fragment_sport_complex_info, container, false);

        bindViews(view);

        //@formatter:off
        _sportComplexLoadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_sportComplexInfoContainerView)
            .setLoadingView(_sportComplexLoadingView)
            .setNoContentView(_sportComplexNoContentView)
            .setErrorView(_sportComplexLadingErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long,
                                                                   FadeVisibilityHandler.NO_ANIMATION))
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();
        //@formatter:on

        _sportComplexLoadingViewDelegate.hideAll();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final val sportComplexId = getSportComplexId();
                if (sportComplexId != null) {
                    _viewSportComplexDetailsEvent.rise(new IdEventArgs(sportComplexId));
                }
            }
        });

        _photosAdapter = new PhotosAdapter();
        if (_sportComplexPhotosView != null) {
            _sportComplexPhotosView.setAdapter(_photosAdapter);

            _sportComplexPhotosView.addOnPageChangeListener(_photosPageChangedListener);
        }

        return view;
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (_sportComplexPhotosView != null) {
            _sportComplexPhotosView.removeOnPageChangeListener(_photosPageChangedListener);
        }

        unbindViews();
    }

    @CallSuper
    @Override
    protected void onReleaseInjectedMembers() {
        super.onReleaseInjectedMembers();

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.unbindScreen();
        }
    }

    @Override
    public void displaySportComplex(@Nullable final SportComplexInfo sportComplex) {
        if (sportComplex != null) {
            if (_sportComplexAddressView != null) {
                final val address = sportComplex.getAddress();
                if (!TextUtils.isEmpty(address)) {
                    _sportComplexAddressView.setText(address);
                } else {
                    _sportComplexAddressView.setText(R.string.sport_complex_info_no_data);
                }
            }

            if (_sportComplexSubwayStationsView != null) {
                final val subwayStations =
                    ProSportFormat.getFormattedSubwayStations(sportComplex.getSubwayStations());
                if (!TextUtils.isEmpty(subwayStations)) {
                    _sportComplexSubwayStationsView.setText(subwayStations);
                } else {
                    _sportComplexSubwayStationsView.setText(R.string.sport_complex_info_no_data);
                }
            }

            if (_sportComplexPhoneView != null) {
                final val phoneNumbers = sportComplex.getPhoneNumbers();
                if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
                    _sportComplexPhoneView.setText(phoneNumbers.get(0));
                } else {
                    _sportComplexPhoneView.setText(R.string.sport_complex_info_no_data);
                }
            }

            if (_sportComplexWorkTimeView != null) {
                final val workPeriod = SportComplexUtils.getTodayWorkPeriod(sportComplex);
                if (workPeriod != null) {
                    final val workTimeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
                    workTimeFormat.getTimeZone().setRawOffset(0);

                    final val startTime = ProSportApiDataUtils.parseTime(workPeriod.getStart());
                    final val endTime = ProSportApiDataUtils.parseTime(workPeriod.getEnd());
                    final val workStart = workTimeFormat.format(startTime);
                    final val workEnd = workTimeFormat.format(endTime);

                    final val workTime =
                        getString(R.string.sport_complex_info_work_time_format, workStart, workEnd);
                    _sportComplexWorkTimeView.setText(workTime);
                } else {
                    _sportComplexWorkTimeView.setText(R.string.sport_complex_info_no_data);
                }
            }

            if (_sportComplexNameView != null) {
                final val name = sportComplex.getName();
                if (!TextUtils.isEmpty(name)) {
                    _sportComplexNameView.setText(name);
                } else {
                    _sportComplexNameView.setText(R.string.sport_complex_info_no_data);
                }
            }

            final val photosAdapter = getPhotosAdapter();
            if (photosAdapter != null) {
                final val photos = sportComplex.getPhotos();

                final val adapterPhotosUris = photosAdapter.getPhotosUris();
                adapterPhotosUris.clear();
                photosAdapter.notifyDataSetChanged();

                if (photos != null && !photos.isEmpty()) {
                    for (final val photo : photos) {
                        final val photoUri = photo.getUri();
                        if (!TextUtils.isEmpty(photoUri)) {
                            adapterPhotosUris.add(photoUri);
                        }
                    }

                    if (_sportComplexPhotosIndicatorView != null) {
                        _sportComplexPhotosIndicatorView.setVisibility(View.VISIBLE);
                    }
                    if (_sportComplexPhotosView != null) {
                        _sportComplexPhotosView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (_sportComplexPhotosIndicatorView != null) {
                        _sportComplexPhotosIndicatorView.setVisibility(View.GONE);
                    }
                    if (_sportComplexPhotosView != null) {
                        _sportComplexPhotosView.setVisibility(View.GONE);
                    }
                }

                photosAdapter.notifyDataSetChanged();

                if (_sportComplexPhotosIndicatorView != null) {
                    _sportComplexPhotosIndicatorView.setIndicatorCount(photosAdapter.getCount());
                    _sportComplexPhotosIndicatorView.setSelectedItem(_sportComplexPhotosView
                                                                         .getCurrentItem());
                }
            } else {
                if (_sportComplexPhotosView != null) {
                    _sportComplexPhotosView.setVisibility(View.INVISIBLE);
                }

                if (_sportComplexPhotosIndicatorView != null) {
                    _sportComplexPhotosIndicatorView.setIndicatorCount(0);
                    _sportComplexPhotosIndicatorView.setVisibility(View.INVISIBLE);
                }
            }
        }

        final val loadingViewDelegate = getSportComplexLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(sportComplex != null);
        }
    }

    @Override
    public final void displaySportComplexLoading() {
        final val loadingViewDelegate = getSportComplexLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public final void displaySportComplexLoadingError() {
        final val loadingViewDelegate = getSportComplexLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showError();
        }
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getViewSportComplexDetailsEvent() {
        return _viewSportComplexDetailsEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getViewSportComplexEvent() {
        return _viewSportComplexEvent;
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();

        final val sportComplexId = getSportComplexId();
        if (sportComplexId != null) {
            _viewSportComplexEvent.rise(new IdEventArgs(sportComplexId));
        } else {
            final val loadingViewDelegate = getSportComplexLoadingViewDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.hideAll();
            }
        }
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getPlayerSubscreenComponent().inject(this);

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    protected void onSportComplexIdChanged() {
        final val sportComplexId = getSportComplexId();
        if (sportComplexId != null) {
            _viewSportComplexEvent.rise(new IdEventArgs(sportComplexId));
        } else {
            final val loadingViewDelegate = getSportComplexLoadingViewDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.hideAll();
            }
        }
    }

    @Named(PresenterNames.SPORT_COMPLEX_INFO)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<SportComplexInfoScreen> _presenter;

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewSportComplexDetailsEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewSportComplexEvent = Events.createEvent();

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private PhotosAdapter _photosAdapter;

    @Nullable
    private TextView _sportComplexAddressView;

    @Getter
    @Nullable
    private Long _sportComplexId = null;

    @Nullable
    private View _sportComplexInfoContainerView;

    @Nullable
    private View _sportComplexLadingErrorView;

    @Nullable
    private ContentLoaderProgressBar _sportComplexLoadingView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _sportComplexLoadingViewDelegate;

    @Nullable
    private TextView _sportComplexNameView;

    @Nullable
    private View _sportComplexNoContentView;

    @Nullable
    private TextView _sportComplexPhoneView;

    @Nullable
    private PageIndicatorView _sportComplexPhotosIndicatorView;

    @NonNull
    private final ViewPager.OnPageChangeListener _photosPageChangedListener =
        new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                if (_sportComplexPhotosIndicatorView != null) {
                    _sportComplexPhotosIndicatorView.setSelectedItem(position);
                }
            }
        };

    @Nullable
    private ViewPager _sportComplexPhotosView;

    @Nullable
    private TextView _sportComplexSubwayStationsView;

    @Nullable
    private TextView _sportComplexWorkTimeView;
}
