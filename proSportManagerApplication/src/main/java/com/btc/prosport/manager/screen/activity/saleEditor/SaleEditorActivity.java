package com.btc.prosport.manager.screen.activity.saleEditor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.functions.Action1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.picker.TimePickerManager;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.event.notice.ManagedNoticeEvent;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.view.recyclerView.decoration.DividerItemDecoration;
import com.btc.common.extension.view.recyclerView.decoration.SpacingItemDecoration;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.Sale;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.repeatableIntervals.item.RepeatableIntervalItem;
import com.btc.prosport.manager.core.adapter.saleEditor.SaleEditorAdapter;
import com.btc.prosport.manager.core.dialog.playgroundsPicker.PlaygroundsPickerDialogFragment;
import com.btc.prosport.manager.core.dialog.playgroundsPicker.PlaygroundsPickerResult;
import com.btc.prosport.manager.core.eventArgs.CreateSaleEventArgs;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.SaleEditorScreen;
import com.btc.prosport.manager.screen.activity.BaseManagerActivity;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class SaleEditorActivity extends BaseManagerActivity implements SaleEditorScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SaleEditorActivity.class);

    public static final String _KEY_SAVED_STATE =
        ConstantBuilder.savedStateKey(SaleEditorActivity.class, "saved_state");

    @Override
    @CallSuper
    public void bindViews() {
        super.bindViews();

        _toolbarView = (Toolbar) findViewById(R.id.toolbar);
        _saleEditorView = (RecyclerView) findViewById(R.id.sale_editor);
        _salePlacesLoadingView = (ProgressBar) findViewById(R.id.loading);
        _salePlacesErrorView = (TextView) findViewById(R.id.loading_error);
        _salePlacesNoContentView = (TextView) findViewById(R.id.no_content);
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            if (_state == null) {
                _state = new SaleEditorSavedState();
            }

            final val saleEditorAdapter = getSaleEditorAdapter();
            final val orderIntervals = saleEditorAdapter.getItems();

            final val intervals =
                new ArrayList<SaleEditorSavedState.Interval>(orderIntervals.size());
            for (final val orderInterval : orderIntervals) {
                final val interval = new SaleEditorSavedState.Interval();

                interval.setTimeStart(orderInterval.getTimeStart());
                interval.setTimeEnd(orderInterval.getTimeEnd());
                interval.setDateStart(orderInterval.getDateStart());
                interval.setDateEnd(orderInterval.getDateEnd());
                interval.setRepeatable(orderInterval.isRepeatable());
                interval.setRepeatWeekDays(orderInterval.getRepeatWeekDays());

                intervals.add(interval);
            }

            _state.setIntervals(intervals);

            _state.setPlaceSelection(_placeSelection);
            _state.setSaleValue(saleEditorAdapter.getSaleValue());
            _state.setSaleType(saleEditorAdapter.getSaleType());

            outState.putParcelable(_KEY_SAVED_STATE, Parcels.wrap(_state));
        }
    }

    @Override
    protected void onReleaseInjectedMembers() {
        super.onReleaseInjectedMembers();

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.unbindScreen();
        }
    }

    @Override
    public void displayCreatedSale(@Nullable final Sale sale) {
        dismissCommitSaleChangesProgressDialog();

        if (sale != null) {
            onBackPressed();
        }
    }

    @Override
    public void displayLoadSalePlacesError() {
        finish();
    }

    @Override
    public void displaySalePlaces(
        @Nullable final Map<SportComplexTitle, List<PlaygroundTitle>> places) {
        _playgroundsBySportComplex = places;

        final val loadingDelegate = getSalePlacesLoadingDelegate();
        if (loadingDelegate != null) {
            loadingDelegate.showContent(places != null && !places.values().isEmpty());
        }
    }

    @NonNull
    @Override
    public final Event<CreateSaleEventArgs> getCreateSaleEvent() {
        return _createSaleEvent;
    }

    @NonNull
    @Override
    public final NoticeEvent getLoadSalePlacesEvent() {
        return _loadSalePlacesEvent;
    }

    @CallSuper
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.sale_editor, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final boolean consumed;

        final val id = item.getItemId();
        if (android.R.id.home == id) {
            onBackPressed();

            consumed = true;
        } else if (R.id.action_complete_editing == id) {
            final val saleEditorAdapter = getSaleEditorAdapter();
            final val saleValue = saleEditorAdapter.getSaleValue();

            List<Long> playgroundsIds = null;
            if (_placeSelection != null) {
                playgroundsIds = new ArrayList<>();
                for (final val playgroundsIdsGroup : _placeSelection.values()) {
                    playgroundsIds.addAll(playgroundsIdsGroup);
                }
            }

            if (playgroundsIds == null || playgroundsIds.isEmpty()) {
                showUserInputErrorsDialog(UserInputErrorType.NO_PLAYGROUNDS);
            } else if (saleValue == null) {
                showUserInputErrorsDialog(UserInputErrorType.NO_SALE_VALUE);
            } else if (saleValue <= 0) {
                showUserInputErrorsDialog(UserInputErrorType.INCORRECT_SALE_VALUE);
            } else {
                final val intervals = saleEditorAdapter.getItems();
                final val saleType = saleEditorAdapter.getSaleType();
                _createSaleEvent.rise(new CreateSaleEventArgs(intervals,
                                                              playgroundsIds,
                                                              saleType,
                                                              saleValue));

                showCommitSaleChangesProgressDialog();
            }

            consumed = true;
        } else {
            consumed = super.onOptionsItemSelected(item);
        }

        return consumed;
    }

    @Nullable
    @CallSuper
    protected SaleEditorSavedState onHandleIntent(@NonNull final Intent intent) {
        Contracts.requireNonNull(intent, "intent == null");

        return new SaleEditorSavedState();
    }

    @Nullable
    @CallSuper
    protected SaleEditorSavedState onHandleSavedState(@NonNull final Bundle savedInstanceState) {
        SaleEditorSavedState state = null;

        if (savedInstanceState.containsKey(_KEY_SAVED_STATE)) {
            state = Parcels.unwrap(savedInstanceState.getParcelable(_KEY_SAVED_STATE));
        }
        return state;
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getManagerScreenComponent().inject(this);

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    @Override
    protected void onPause() {
        super.onPause();

        dismissUserInputErrorsDialog();
    }

    @CallSuper
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            _state = onHandleSavedState(savedInstanceState);
        }

        if (_state == null) {
            _state = onHandleIntent(getIntent());
        }

        if (_state != null) {
            setContentView(R.layout.activity_sale_editor);
            bindViews();

            _placeSelection = _state.getPlaceSelection();

            _salePlacesLoadingDelegate = LoadingViewDelegate
                .builder()
                .setContentView(_saleEditorView)
                .setLoadingView(_salePlacesLoadingView)
                .setNoContentView(_salePlacesNoContentView)
                .setErrorView(_salePlacesErrorView)
                .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long,
                                                                       FadeVisibilityHandler
                                                                           .NO_ANIMATION))
                .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
                .build();

            final val saleEditorAdapter = getSaleEditorAdapter();
            saleEditorAdapter.setTimePickerManager(getTimePickerManager());
            saleEditorAdapter.setSaleType(_state.getSaleType());
            saleEditorAdapter.setSaleValue(_state.getSaleValue());

            final val intervals = _state.getIntervals();
            if (intervals != null) {
                for (final val interval : intervals) {
                    final val intervalItem = RepeatableIntervalItem.createEmpty();

                    intervalItem.setDateStart(interval.getDateStart());
                    intervalItem.setDateEnd(interval.getDateEnd());
                    intervalItem.setTimeStart(interval.getTimeStart());
                    intervalItem.setTimeEnd(interval.getTimeEnd());

                    intervalItem.setRepeatable(interval.isRepeatable());
                    final val repeatWeekDays = interval.getRepeatWeekDays();
                    if (repeatWeekDays != null) {
                        intervalItem.getRepeatWeekDays().addAll(repeatWeekDays);
                    }

                    saleEditorAdapter.addItem(intervalItem);
                }
            }

            if (saleEditorAdapter.getItems().isEmpty()) {
                saleEditorAdapter.addItem(RepeatableIntervalItem.createDefault());
            }

            saleEditorAdapter.getPickPlaceEvent().addHandler(_pickSalePlaceHandler);

            if (_saleEditorView != null) {
                final val orderParamsLayoutManager = new LinearLayoutManager(this);

                final val res = getResources();
                final val spacing = res.getDimensionPixelOffset(R.dimen.grid_large_spacing);
                final val padding = res.getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                final val spacingDecorator = SpacingItemDecoration
                    .builder()
                    .setSpacing(spacing)
                    .setHorizontalPadding(padding)
                    .collapseBorders()
                    .build();
                _saleEditorView.addItemDecoration(spacingDecorator);

                final int orientation = orderParamsLayoutManager.getOrientation();
                final val dividerDecoration = new DividerItemDecoration(this, orientation);
                _saleEditorView.addItemDecoration(dividerDecoration);

                saleEditorAdapter.enableIntervalRemovalBySwipe(_saleEditorView);
                _saleEditorView.setAdapter(saleEditorAdapter);
                _saleEditorView.setLayoutManager(orderParamsLayoutManager);
            }

            if (_toolbarView != null) {
                setSupportActionBar(_toolbarView);
            }

            final val actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
            }

            final val loadingViewDelegate = getSalePlacesLoadingDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.showLoading();
            }
            _loadSalePlacesEvent.rise();
        } else {
            finish();
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();

        getSaleEditorAdapter().getPickPlaceEvent().removeHandler(_pickSalePlaceHandler);

        unbindViews();
    }

    @Named(PresenterNames.SALE_EDITOR)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<SaleEditorScreen> _presenter;

    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ TimePickerManager _timePickerManager;

    @NonNull
    private final ManagedEvent<CreateSaleEventArgs> _createSaleEvent = Events.createEvent();

    @NonNull
    private final ManagedNoticeEvent _loadSalePlacesEvent = Events.createNoticeEvent();

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final SaleEditorAdapter _saleEditorAdapter = new SaleEditorAdapter();

    @Nullable
    private Dialog _commitSaleChangesProgressDialog;

    @Nullable
    private Map<Long, Set<Long>> _placeSelection;

    @Nullable
    private Map<SportComplexTitle, List<PlaygroundTitle>> _playgroundsBySportComplex;

    @NonNull
    private final NoticeEventHandler _pickSalePlaceHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            if (_playgroundsBySportComplex != null) {
                PlaygroundsPickerDialogFragment
                    .builder()
                    .setPlaygroundsBySportComplex(_playgroundsBySportComplex)
                    .setSelection(_placeSelection)
                    .show(getSupportFragmentManager())
                    .subscribe(new Action1<PlaygroundsPickerResult>() {
                        @Override
                        public void call(final PlaygroundsPickerResult result) {
                            Contracts.requireMainThread();

                            _placeSelection = result.getSelection();

                            final val sportComplexesList = new ArrayList<String>();

                            final val selected = result.getSelectedSportComplexesIds();
                            final val selection = result.getSportComplexesWithSelectionIds();
                            for (final long sportComplexId : selection) {
                                final val sportComplex = IterableUtils.find(
                                    _playgroundsBySportComplex.keySet(),
                                    new Predicate<SportComplexTitle>() {
                                        @Override
                                        public boolean evaluate(final SportComplexTitle object) {
                                            return object.getId() == sportComplexId;
                                        }
                                    });

                                final String playgroundsList;
                                if (selected != null && selected.contains(sportComplexId)) {
                                    playgroundsList =
                                        getString(R.string.sale_editor_place_all_playgrounds);
                                } else {
                                    final val playgrounds =
                                        _playgroundsBySportComplex.get(sportComplex);
                                    final val selectedPlaygroundsIds =
                                        result.getSelectedPlaygroundsIds(sportComplexId);

                                    final val playgroundsNames = new ArrayList<String>();
                                    if (selectedPlaygroundsIds != null) {
                                        for (final val playground : playgrounds) {
                                            final val playgroundId = playground.getId();
                                            if (selectedPlaygroundsIds.contains(playgroundId)) {
                                                playgroundsNames.add(playground.getName());
                                            }
                                        }
                                    }

                                    playgroundsList = TextUtils.join(", ", playgroundsNames);
                                }

                                sportComplexesList.add(getString(R.string.sale_editor_place_format,
                                                                 sportComplex.getName(),
                                                                 playgroundsList));
                            }

                            final val place = TextUtils.join("\n\n", sportComplexesList);
                            getSaleEditorAdapter().setPlace(place);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(final Throwable error) {
                            Contracts.requireMainThread();

                            Log.w(_LOG_TAG, "Fail to pick playgrounds.", error);
                        }
                    });
            }
        }
    };

    @Nullable
    private RecyclerView _saleEditorView;

    @Nullable
    private TextView _salePlacesErrorView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _salePlacesLoadingDelegate;

    @Nullable
    private ProgressBar _salePlacesLoadingView;

    @Nullable
    private TextView _salePlacesNoContentView;

    @Nullable
    private SaleEditorSavedState _state;

    @Nullable
    private Toolbar _toolbarView;

    @Nullable
    private Dialog _userInputErrorsDialog;

    private void dismissCommitSaleChangesProgressDialog() {
        if (_commitSaleChangesProgressDialog != null) {
            _commitSaleChangesProgressDialog.dismiss();

            _commitSaleChangesProgressDialog = null;
        }
    }

    private void dismissUserInputErrorsDialog() {
        if (_userInputErrorsDialog != null) {
            _userInputErrorsDialog.dismiss();
            _userInputErrorsDialog = null;
        }
    }

    private void showCommitSaleChangesProgressDialog() {
        dismissCommitSaleChangesProgressDialog();

        final val title = getString(R.string.sale_editor_commit_changes_progress_title);
        final val message = getString(R.string.sale_editor_create_sale_progress_message);
        _commitSaleChangesProgressDialog = ProgressDialog.show(this, title, message, true, false);
    }

    private void showUserInputErrorsDialog(@NonNull final UserInputErrorType errorType) {
        Contracts.requireNonNull(errorType, "errorType == null");

        dismissUserInputErrorsDialog();

        _userInputErrorsDialog = new AlertDialog.Builder(this)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    dismissUserInputErrorsDialog();
                }
            })
            .setMessage(errorType.getMessageId())
            .show();
    }

    @Accessors(prefix = "_")
    private enum UserInputErrorType {
        NO_PLAYGROUNDS(R.string.sale_editor_error_no_playgrounds),
        NO_SALE_VALUE(R.string.sale_editor_error_no_sale_value),
        INCORRECT_SALE_VALUE(R.string.sale_editor_error_incorrect_sale_value);

        @Getter
        @StringRes
        private final int _messageId;

        UserInputErrorType(final int messageId) {
            _messageId = messageId;
        }
    }
}
