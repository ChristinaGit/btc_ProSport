package com.btc.prosport.player.screen.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
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
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.eventArgs.PositionEventArgs;
import com.btc.common.extension.eventArgs.UriEventArgs;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration;
import com.btc.common.extension.view.recyclerView.decoration.SpacingItemDecoration;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.SwipeRefreshUtils;
import com.btc.common.utility.UriFactoryUtils;
import com.btc.prosport.api.model.Attribute;
import com.btc.prosport.api.model.SportComplexDetail;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.core.utility.ProSportFormat;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.adapter.sportComplexDetails.SportComplexDetailsAdapter;
import com.btc.prosport.player.core.adapter.sportComplexDetails.item.SportComplexAttributeItem;
import com.btc.prosport.player.core.adapter.sportComplexDetails.item.SportComplexContactItem;
import com.btc.prosport.player.core.adapter.sportComplexDetails.item.SportComplexDescriptionItem;
import com.btc.prosport.player.core.adapter.sportComplexDetails.item.SportComplexInventoryItem;
import com.btc.prosport.player.core.adapter.sportComplexDetails.item.SportComplexPhotosItem;
import com.btc.prosport.player.core.eventArgs.ViewPhotoEventArgs;
import com.btc.prosport.player.core.utility.SportComplexUtils;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.screen.SportComplexDetailScreen;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class SportComplexDetailFragment extends BaseSportComplexViewerPageFragment
    implements SportComplexDetailScreen, SwipeRefreshLayout.OnRefreshListener {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SportComplexDetailFragment.class);

    @Override
    public final void displaySportComplex(@Nullable final SportComplexDetail sportComplex) {
        if (_sportComplexDetailsRefreshView != null) {
            _sportComplexDetailsRefreshView.setRefreshing(false);
        }

        _sportComplex = sportComplex;

        final val sportComplexDetailsAdapter = getSportComplexDetailsAdapter();
        if (sportComplexDetailsAdapter != null) {
            sportComplexDetailsAdapter.clear();

            setupSportComplexPhotos();
            setupSportComplexDescription();
            setupSportComplexInventory();
            setupSportComplexAttributes();
            setupSportComplexAddress();
            setupSportComplexSubwayStations();
            setupSportComplexPhones();
            setupSportComplexWorkTime();

            sportComplexDetailsAdapter.notifyDataSetChanged();
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
        if (_sportComplexDetailsRefreshView != null) {
            _sportComplexDetailsRefreshView.setRefreshing(false);
        }

        final val loadingViewDelegate = getSportComplexLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showError();
        }
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getRefreshSportComplexEvent() {
        return _refreshSportComplexEvent;
    }

    @NonNull
    @Override
    public Event<UriEventArgs> getSportComplexPhoneCallEvent() {
        return _sportComplexPhoneCallEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getViewSportComplexEvent() {
        return _viewSportComplexEvent;
    }

    @NonNull
    @Override
    public final Event<ViewPhotoEventArgs> getViewSportComplexPhotosEvent() {
        return _viewSportComplexPhotosEvent;
    }

    @Override
    @CallSuper
    public void bindViews(@NonNull final View view) {
        super.bindViews(Contracts.requireNonNull(view, "view == null"));

        _sportComplexDetailsView = (RecyclerView) view.findViewById(R.id.sport_complex_details);
        _sportComplexNoContentView = (TextView) view.findViewById(R.id.no_content);
        _sportComplexLoadingErrorView = (TextView) view.findViewById(R.id.loading_error);
        _sportComplexLoadingView = (ContentLoaderProgressBar) view.findViewById(R.id.loading);
        _sportComplexDetailsRefreshView =
            (SwipeRefreshLayout) view.findViewById(R.id.sport_complex_details_refresh);
    }

    @CallSuper
    @Nullable
    @Override
    public final View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view = inflater.inflate(R.layout.fragment_sport_complex_viewer, container, false);

        bindViews(view);

        initializeSportComplexView();

        if (_sportComplexDetailsRefreshView != null) {
            SwipeRefreshUtils.setupPrimaryColors(_sportComplexDetailsRefreshView);
            _sportComplexDetailsRefreshView.setOnRefreshListener(this);
        }

        //@formatter:off
        _sportComplexLoadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_sportComplexDetailsView)
            .setLoadingView(_sportComplexLoadingView)
            .setNoContentView(_sportComplexNoContentView)
            .setErrorView(_sportComplexLoadingErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(
                R.anim.fade_in_long,
                FadeVisibilityHandler.NO_ANIMATION))
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();
        //@formatter:on

        _sportComplexLoadingViewDelegate.hideAll();

        final val adapter = getSportComplexDetailsAdapter();
        if (adapter != null) {
            adapter.getViewSportComplexPhotosEvent().addHandler(_viewSportComplexPhotosHandler);
        }

        return view;
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        final val adapter = getSportComplexDetailsAdapter();
        if (adapter != null) {
            adapter.getViewSportComplexPhotosEvent().removeHandler(_viewSportComplexPhotosHandler);
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

    @CallSuper
    @Override
    public void onRefresh() {
        final Long sportComplexId = getSportComplexId();

        if (sportComplexId != null) {
            riseRefreshSportComplexesEvent();
        } else {
            if (_sportComplexDetailsRefreshView != null) {
                _sportComplexDetailsRefreshView.setRefreshing(false);
            }
        }
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();

        if (isPageActive()) {
            final val sportComplex = getSportComplex();
            if (sportComplex == null) {
                riseViewSportComplexEvent();
            } else {
                riseRefreshSportComplexesEvent();
            }
        }
    }

    @Nullable
    protected final List<Attribute> getSportComplexAttributes() {
        final List<Attribute> attributes;

        final val sportComplex = getSportComplex();
        if (sportComplex != null) {
            attributes = (List<Attribute>) sportComplex.getAttributes();
        } else {
            attributes = null;
        }

        return attributes;
    }

    @CallSuper
    @Override
    protected void onEnterPage() {
        super.onEnterPage();

        final val sportComplex = getSportComplex();
        if (sportComplex == null) {
            riseViewSportComplexEvent();
        }
    }

    @CallSuper
    @Override
    protected void onSportComplexIdChanged() {
        super.onSportComplexIdChanged();

        if (isResumed() && isPageActive()) {
            riseViewSportComplexEvent();
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

    @Named(PresenterNames.SPORT_COMPLEX_DETAIL)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<SportComplexDetailScreen> _presenter;

    @NonNull
    private final ManagedEvent<IdEventArgs> _refreshSportComplexEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<UriEventArgs> _sportComplexPhoneCallEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewSportComplexEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<ViewPhotoEventArgs> _viewSportComplexPhotosEvent =
        Events.createEvent();

    @NonNull
    private final EventHandler<PositionEventArgs> _viewSportComplexPhotosHandler =
        new EventHandler<PositionEventArgs>() {
            @Override
            public void onEvent(@NonNull final PositionEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final val sportComplexId = getSportComplexId();
                if (sportComplexId != null) {
                    final val viewPhotoEventArgs =
                        new ViewPhotoEventArgs(sportComplexId, eventArgs.getPosition());
                    _viewSportComplexPhotosEvent.rise(viewPhotoEventArgs);
                }
            }
        };

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private SportComplexDetail _sportComplex;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private SportComplexDetailsAdapter _sportComplexDetailsAdapter;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LinearLayoutManager _sportComplexDetailsLayoutManager;

    @Nullable
    private SwipeRefreshLayout _sportComplexDetailsRefreshView;

    @Nullable
    private RecyclerView _sportComplexDetailsView;

    @Nullable
    private TextView _sportComplexLoadingErrorView;

    @Nullable
    private ContentLoaderProgressBar _sportComplexLoadingView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _sportComplexLoadingViewDelegate;

    @Nullable
    private TextView _sportComplexNoContentView;

    private void initializeSportComplexView() {
        if (_sportComplexDetailsView != null) {
            final val context = getContext();
            final val res = getResources();

            _sportComplexDetailsLayoutManager = new LinearLayoutManager(context);
            _sportComplexDetailsAdapter = new SportComplexDetailsAdapter();

            final int spacing = res.getDimensionPixelSize(R.dimen.sport_complex_details_spacing);
            final int padding = res.getDimensionPixelSize(R.dimen.sport_complex_details_padding);
            final val spacingDecoration = SpacingItemDecoration
                .builder()
                .setSpacing(spacing)
                .setPadding(padding, 0, padding, 0)
                .build();
            _sportComplexDetailsView.addItemDecoration(spacingDecoration);

            final int orientation = _sportComplexDetailsLayoutManager.getOrientation();
            final val dividerDecoration = new DividerItemDecoration(context, orientation);
            _sportComplexDetailsView.addItemDecoration(dividerDecoration);

            _sportComplexDetailsView.setLayoutManager(_sportComplexDetailsLayoutManager);
            _sportComplexDetailsView.setAdapter(_sportComplexDetailsAdapter);
        }
    }

    private void riseRefreshSportComplexesEvent() {
        final Long sportComplexId = getSportComplexId();
        if (sportComplexId != null) {
            _refreshSportComplexEvent.rise(new IdEventArgs(sportComplexId));
        } else {
            final val loadingViewDelegate = getSportComplexLoadingViewDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.showContent(false);
            }
        }
    }

    private void riseViewSportComplexEvent() {
        final val sportComplexId = getSportComplexId();
        if (sportComplexId != null) {
            _viewSportComplexEvent.rise(new IdEventArgs(sportComplexId));
        } else {
            final val loadingViewDelegate = getSportComplexLoadingViewDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.showContent(false);
            }
        }
    }

    private void setupSportComplexAddress() {
        final val sportComplex = getSportComplex();

        if (sportComplex != null) {
            final val sportComplexDetailsAdapter = getSportComplexDetailsAdapter();
            if (sportComplexDetailsAdapter != null) {
                final val address = sportComplex.getAddress();

                final CharSequence info;

                if (!TextUtils.isEmpty(address)) {
                    info = address;
                } else {
                    info = getString(R.string.sport_complexes_viewer_no_data);
                }

                final val sportComplexContactItem =
                    new SportComplexContactItem(R.drawable.ic_material_location_on, info);

                sportComplexDetailsAdapter
                    .getSportComplexContactItems()
                    .add(sportComplexContactItem);
            }
        }
    }

    private void setupSportComplexAttributes() {
        final val attributes = getSportComplexAttributes();

        if (attributes != null && !attributes.isEmpty()) {
            final val sportComplexDetailsAdapter = getSportComplexDetailsAdapter();
            if (sportComplexDetailsAdapter != null) {
                final val sportComplexAttributeItems =
                    sportComplexDetailsAdapter.getSportComplexAttributeItems();

                for (final val attribute : attributes) {
                    final val attributeItem =
                        new SportComplexAttributeItem(StringUtils.capitalize(attribute.getName()),
                                                      attribute.getIcon());

                    sportComplexAttributeItems.add(attributeItem);
                }
            }
        }
    }

    private void setupSportComplexDescription() {
        final val sportComplex = getSportComplex();

        if (sportComplex != null) {
            final val sportComplexDetailsAdapter = getSportComplexDetailsAdapter();
            if (sportComplexDetailsAdapter != null) {
                final val description = sportComplex.getDescription();

                if (!TextUtils.isEmpty(description)) {
                    final val sportComplexDescriptionItem =
                        new SportComplexDescriptionItem(description);

                    sportComplexDetailsAdapter.setSportComplexDescriptionItem(
                        sportComplexDescriptionItem);
                }
            }
        }
    }

    private void setupSportComplexInventory() {
        final val sportComplex = getSportComplex();

        if (sportComplex != null) {
            final val sportComplexDetailsAdapter = getSportComplexDetailsAdapter();
            if (sportComplexDetailsAdapter != null) {
                final val description = sportComplex.getInventoryDescription();
                final val inventoryChargeable =
                    BooleanUtils.isTrue(sportComplex.getInventoryChargeableState());

                final val inventoryItem =
                    new SportComplexInventoryItem(description, inventoryChargeable);
                sportComplexDetailsAdapter.setSportComplexInventoryItem(inventoryItem);
            }
        }
    }

    private void setupSportComplexPhones() {
        final val sportComplex = getSportComplex();

        if (sportComplex != null) {
            final val sportComplexDetailsAdapter = getSportComplexDetailsAdapter();
            if (sportComplexDetailsAdapter != null) {
                final CharSequence info;

                final val phoneNumbers = sportComplex.getPhoneNumbers();

                if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
                    final val stringBuilder = new SpannableStringBuilder();

                    final val phoneNumbersCount = phoneNumbers.size();
                    for (int i = 0; i < phoneNumbersCount; i++) {
                        final val phoneNumber = phoneNumbers.get(i);

                        final val start = stringBuilder.length();

                        stringBuilder.append(phoneNumber);

                        final val end = stringBuilder.length();

                        stringBuilder.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(final View widget) {
                                final val telephoneUri =
                                    UriFactoryUtils.getTelephoneUri(phoneNumber);
                                _sportComplexPhoneCallEvent.rise(new UriEventArgs(telephoneUri));
                            }
                        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        if (i != phoneNumbersCount - 1) {
                            stringBuilder.append(", ");
                        }
                    }

                    info = stringBuilder;
                } else {
                    info = getString(R.string.sport_complexes_viewer_no_data);
                }

                final val sportComplexContactItem =
                    new SportComplexContactItem(R.drawable.ic_material_local_phone, info);

                sportComplexDetailsAdapter
                    .getSportComplexContactItems()
                    .add(sportComplexContactItem);
            }
        }
    }

    private void setupSportComplexPhotos() {
        final val sportComplex = getSportComplex();

        if (sportComplex != null) {
            final val sportComplexDetailsAdapter = getSportComplexDetailsAdapter();

            if (sportComplexDetailsAdapter != null) {
                List<String> photoUris = null;

                final val sportComplexPhotos = sportComplex.getPhotos();
                if (sportComplexPhotos != null && !sportComplexPhotos.isEmpty()) {
                    photoUris = new ArrayList<>(sportComplexPhotos.size());
                    for (final val photo : sportComplexPhotos) {
                        final val photoUri = photo.getUri();
                        if (!TextUtils.isEmpty(photoUri)) {
                            photoUris.add(photoUri);
                        }
                    }
                }

                final val sportComplexPhotosItem = new SportComplexPhotosItem(photoUris);

                sportComplexDetailsAdapter.setSportComplexPhotosItem(sportComplexPhotosItem);
            }
        }
    }

    private void setupSportComplexSubwayStations() {
        final val sportComplex = getSportComplex();

        if (sportComplex != null) {
            final val subwayStations = sportComplex.getSubwayStations();

            final val sportComplexDetailsAdapter = getSportComplexDetailsAdapter();
            if (sportComplexDetailsAdapter != null) {
                final val formattedSubwayStations =
                    ProSportFormat.getFormattedSubwayStations(subwayStations);
                final val noData = getString(R.string.sport_complexes_viewer_no_data);
                final val info = StringUtils.defaultString(formattedSubwayStations, noData);
                final val coveringPropertyItem =
                    new SportComplexContactItem(R.drawable.ic_material_subway, info);

                sportComplexDetailsAdapter.getSportComplexContactItems().add(coveringPropertyItem);
            }
        }
    }

    private void setupSportComplexWorkTime() {
        final val sportComplex = getSportComplex();

        if (sportComplex != null) {
            final val sportComplexDetailsAdapter = getSportComplexDetailsAdapter();
            if (sportComplexDetailsAdapter != null) {
                final val workPeriod = SportComplexUtils.getTodayWorkPeriod(sportComplex);

                final CharSequence info;

                if (workPeriod != null) {
                    final val workTimeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);

                    final val startTime = ProSportApiDataUtils.parseTime(workPeriod.getStart());
                    final val endTime = ProSportApiDataUtils.parseTime(workPeriod.getEnd());
                    final val workStart = workTimeFormat.format(startTime);
                    final val workEnd = workTimeFormat.format(endTime);

                    info =
                        getString(R.string.sport_complex_info_work_time_format, workStart, workEnd);
                } else {
                    info = getString(R.string.sport_complexes_viewer_no_data);
                }

                final val workTimeItem =
                    new SportComplexContactItem(R.drawable.ic_material_schedule, info);

                sportComplexDetailsAdapter.getSportComplexContactItems().add(workTimeItem);
            }
        }
    }
}
