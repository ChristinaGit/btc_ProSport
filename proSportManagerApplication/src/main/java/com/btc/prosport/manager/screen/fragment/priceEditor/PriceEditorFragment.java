package com.btc.prosport.manager.screen.fragment.priceEditor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.ImeUtils;
import com.btc.common.utility.NumberUtils;
import com.btc.prosport.api.ProSportDataContract;
import com.btc.prosport.api.model.Price;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.prices.PricesAdapter;
import com.btc.prosport.manager.core.eventArgs.ChangePlaygroundPricesEventArgs;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.screen.PriceEditorScreen;
import com.btc.prosport.manager.screen.fragment.playgroundEditor.BasePlaygroundEditorPageFragment;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public class PriceEditorFragment extends BasePlaygroundEditorPageFragment
    implements PriceEditorScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(PriceEditorFragment.class);

    @Override
    public final void displayPrices(@Nullable final List<Price> prices) {
        final val scheduleAdapter = getPricesAdapter();
        if (scheduleAdapter != null) {
            scheduleAdapter.setCells(prices);
            scheduleAdapter.notifyDataSetChanged();
        }

        final val loadingViewDelegate = getScheduleLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(prices != null && !prices.isEmpty());
        }
    }

    @Override
    public void displayPricesChangedResult() {
        setHasUncommittedChanges(false);

        final val pricesAdapter = getPricesAdapter();
        if (pricesAdapter != null) {
            pricesAdapter.removePriceChanges();
        }

        riseViewIntervalsEvent();
    }

    @Override
    public final void displayPricesLoading() {
        final val loadingViewDelegate = getScheduleLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public final void displayPricesLoadingError() {
        final val loadingViewDelegate = getScheduleLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showError();
        }
    }

    @NonNull
    @Override
    public final Event<ChangePlaygroundPricesEventArgs> getChangePlaygroundPricesEvent() {
        return _changePlaygroundPricesEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getViewPricesEvent() {
        return _viewPricesEvent;
    }

    @CallSuper
    @Override
    public void bindViews(@NonNull final View view) {
        super.bindViews(view);

        _pricesLoadingErrorView = view.findViewById(R.id.loading_error);
        _pricesView = (RecyclerView) view.findViewById(R.id.prices);
        _pricesLoadingView = (ContentLoaderProgressBar) view.findViewById(R.id.loading);
        _pricesNoContentView = view.findViewById(R.id.no_content);
        _pricesOverlayHeaderView = (LinearLayout) view.findViewById(R.id.prices_overlay_header);
    }

    @CallSuper
    @Nullable
    @Override
    public final View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view =
            inflater.inflate(R.layout.fragment_playground_price_editor, container, false);

        bindViews(view);

        initializeScheduleView();

        final val pricesAdapter = getPricesAdapter();
        if (pricesAdapter != null) {
            pricesAdapter
                .getCellsSelectionStateChangedEvent()
                .addHandler(_cellsSelectionStateChangedHandler);
            pricesAdapter.getCellsSelectionChangedEvent().addHandler(_cellsSelectionChangedHandler);
        }

        _scheduleLoadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_pricesView)
            .setLoadingView(_pricesLoadingView)
            .setNoContentView(_pricesNoContentView)
            .setErrorView(_pricesLoadingErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long, 0) {
                @Override
                public void changeVisibility(@NonNull final View view, final boolean visible) {
                    super.changeVisibility(view, visible);

                    final val pricesAdapter = getPricesAdapter();
                    if (pricesAdapter != null && _pricesView != null) {
                        pricesAdapter.invalidateOverlayHeaderVisibility(_pricesView);
                    }
                }
            })
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();

        _scheduleLoadingViewDelegate.showContent();

        return view;
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        final val pricesAdapter = getPricesAdapter();
        if (pricesAdapter != null) {
            pricesAdapter
                .getCellsSelectionStateChangedEvent()
                .removeHandler(_cellsSelectionStateChangedHandler);
            pricesAdapter
                .getCellsSelectionChangedEvent()
                .removeHandler(_cellsSelectionChangedHandler);

            if (_pricesView != null) {
                pricesAdapter.unbindOverlayHeaderView(_pricesView);
            }
        }

        unbindViews();
    }

    @Override
    public void commitChanges() {
        final val playgroundId = getPlaygroundId();
        final val pricesAdapter = getPricesAdapter();
        if (playgroundId != null && pricesAdapter != null) {
            final val priceChanges = new HashMap<String, Integer>();

            final val newPrices = pricesAdapter.getNewPrices();

            final val newPricesCount = newPrices.size();
            for (int i = 0; i < newPricesCount; i++) {
                final val relativePosition = newPrices.keyAt(i);
                final val newPrice = newPrices.valueAt(i);

                final val price = pricesAdapter.getCellItemByRelativePosition(relativePosition);
                final val priceId = price == null ? null : price.getId();
                if (priceId != null) {
                    if (newPrice == PricesAdapter.PRICE_NULL) {
                        priceChanges.put(priceId, null);
                    } else {
                        priceChanges.put(priceId, newPrice);
                    }
                }
            }

            final val eventArgs = new ChangePlaygroundPricesEventArgs(playgroundId, priceChanges);
            _changePlaygroundPricesEvent.rise(eventArgs);
        }
    }

    @Override
    public int getLabelId() {
        return R.string.price_editor_label;
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();

        final val pricesAdapter = getPricesAdapter();
        if (pricesAdapter != null && _pricesView != null) {
            pricesAdapter.invalidateOverlayHeader(_pricesView);
            pricesAdapter.invalidateOverlayHeaderVisibility(_pricesView);
        }

        riseViewIntervalsEvent();
    }

    @CallSuper
    @Override
    public void onPause() {
        super.onPause();

        dismissPickNewPriceDialog();
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getManagerSubscreenComponent().inject(this);

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @Override
    @CallSuper
    protected void onPlaygroundIdChanged() {
        super.onPlaygroundIdChanged();

        if (isResumed()) {
            riseViewIntervalsEvent();
        }
    }

    @Named(PresenterNames.PRICE_EDITOR)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<PriceEditorScreen> _presenter;

    @NonNull
    private final ManagedEvent<ChangePlaygroundPricesEventArgs> _changePlaygroundPricesEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewPricesEvent = Events.createEvent();

    @Nullable
    private Dialog _pickNewPriceDialog;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private PricesAdapter _pricesAdapter;

    @Nullable
    private View _pricesLoadingErrorView;

    @Nullable
    private ContentLoaderProgressBar _pricesLoadingView;

    @Nullable
    private View _pricesNoContentView;

    @Nullable
    private LinearLayout _pricesOverlayHeaderView;

    @Nullable
    private ActionMode _pricesSelectionActionMode;

    @NonNull
    private final NoticeEventHandler _cellsSelectionChangedHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            if (_pricesSelectionActionMode != null) {
                _pricesSelectionActionMode.invalidate();
            }
        }
    };

    @NonNull
    private final NoticeEventHandler _cellsSelectionStateChangedHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            final val pricesAdapter = getPricesAdapter();
            if (pricesAdapter != null) {
                if (pricesAdapter.hasSelection()) {
                    startPricesSelectionActionMode();
                } else {
                    finishPricesSelectionActionMode();
                }
            }
        }
    };

    @Nullable
    private RecyclerView _pricesView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private GridLayoutManager _scheduleLayoutManger;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _scheduleLoadingViewDelegate;

    private void dismissPickNewPriceDialog() {
        if (_pickNewPriceDialog != null) {
            _pickNewPriceDialog.dismiss();

            _pickNewPriceDialog = null;
        }
    }

    private void finishPricesSelectionActionMode() {
        if (_pricesSelectionActionMode != null) {
            _pricesSelectionActionMode.finish();
        }
    }

    private void initializeScheduleView() {
        if (_pricesView != null) {
            final long intervalLength = ProSportDataContract.INTERVAL_LENGTH;
            final int columnCount = (int) (DateUtils.WEEK_IN_MILLIS / DateUtils.DAY_IN_MILLIS);
            final int rowCount = (int) (DateUtils.DAY_IN_MILLIS / intervalLength);

            _pricesAdapter = new PricesAdapter();

            _pricesAdapter.setIntervalLength(intervalLength);
            _pricesAdapter.setTableColumnCount(columnCount);
            _pricesAdapter.setTableRowCount(rowCount);

            if (_pricesOverlayHeaderView != null) {
                _pricesAdapter.bindOverlayHeaderView(_pricesView, _pricesOverlayHeaderView);
            }
            _scheduleLayoutManger = (GridLayoutManager) _pricesView.getLayoutManager();

            _pricesView.setItemViewCacheSize(400);
            _pricesView.setAdapter(_pricesAdapter);
        }
    }

    private void riseViewIntervalsEvent() {
        final val playgroundId = getPlaygroundId();
        if (playgroundId != null) {
            _viewPricesEvent.rise(new IdEventArgs(playgroundId));
        } else {
            final val loadingViewDelegate = getScheduleLoadingViewDelegate();
            if (loadingViewDelegate != null) {
                loadingViewDelegate.showContent(false);
            }
        }
    }

    private void showPickNewPriceDialog() {
        dismissPickNewPriceDialog();

        final val dialog = new AlertDialog.Builder(getContext())
            .setView(R.layout.layout_new_price_dialog)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(
                    final DialogInterface dialogInterface, final int which) {
                    if (dialogInterface instanceof Dialog) {
                        final val dialog = (Dialog) dialogInterface;
                        final val newPriceView = (TextView) dialog.findViewById(R.id.new_price);

                        final val newPrice =
                            NumberUtils.tryParse(newPriceView.getText().toString());

                        final val pricesAdapter = getPricesAdapter();
                        if (pricesAdapter != null) {
                            if (newPrice == null) {
                                pricesAdapter.removeSelectionPrices();
                            } else {
                                pricesAdapter.setSelectionNewPrices(newPrice);
                            }

                            setHasUncommittedChanges(true);
                        }

                        ImeUtils.hideIme(newPriceView);
                    }

                    dismissPickNewPriceDialog();
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    dismissPickNewPriceDialog();
                }
            })
            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(final DialogInterface dialog) {
                    finishPricesSelectionActionMode();
                }
            })
            .setTitle(R.string.price_editor_new_price_dialog_title)
            .show();
        final val newPriceView = (EditText) dialog.findViewById(R.id.new_price);
        if (newPriceView != null) {
            newPriceView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(
                    final TextView v, final int actionId, final KeyEvent event) {
                    final boolean consumed;

                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

                        consumed = true;
                    } else {
                        consumed = false;
                    }

                    return consumed;
                }
            });

            newPriceView.post(new Runnable() {
                @Override
                public void run() {
                    final val pricesAdapter = getPricesAdapter();
                    if (pricesAdapter != null) {
                        final val selection = pricesAdapter.getSelectedCellRange();
                        if (selection != null) {
                            final val actualPrice =
                                pricesAdapter.getActualPrice(selection.getMinimum());
                            final val formattedInitialPrice =
                                actualPrice == null ? null : String.valueOf((int) actualPrice);
                            newPriceView.setText(formattedInitialPrice);
                        }
                    }

                    newPriceView.setSelection(newPriceView.getText().length());
                    newPriceView.requestFocus();
                    ImeUtils.showIme(newPriceView);
                }
            });
        }

        _pickNewPriceDialog = dialog;
    }

    private void startPricesSelectionActionMode() {
        final val activity = getSupportActivity();
        if (activity != null) {
            _pricesSelectionActionMode = activity.startSupportActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
                    mode.setTitle(R.string.price_editor_prices_selection_mode_title);

                    mode.getMenuInflater().inflate(R.menu.prices_editor_prices_selection, menu);

                    setInEditingState(true);

                    return true;
                }

                @Override
                public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
                    final boolean consumed;

                    final int id = item.getItemId();
                    if (id == R.id.action_edit_prices) {
                        showPickNewPriceDialog();

                        consumed = true;
                    } else {
                        consumed = false;
                    }

                    return consumed;
                }

                @Override
                public void onDestroyActionMode(final ActionMode mode) {
                    final val pricesAdapter = getPricesAdapter();
                    if (pricesAdapter != null) {
                        pricesAdapter.clearSelection();
                    }

                    _pricesSelectionActionMode = null;

                    setInEditingState(false);
                }
            });
        }
    }
}
