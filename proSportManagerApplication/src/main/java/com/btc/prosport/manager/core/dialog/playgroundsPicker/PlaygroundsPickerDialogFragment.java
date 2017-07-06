package com.btc.prosport.manager.core.dialog.playgroundsPicker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.Subscriber;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.playgroundPicker.PlaygroundPickerAdapter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Accessors(prefix = "_")
public class PlaygroundsPickerDialogFragment extends DialogFragment
    implements DialogInterface.OnClickListener {
    public static final String DEFAULT_TAG = PlaygroundsPickerDialogFragment.class.getName();

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static void dismissDialog(@NonNull final FragmentManager fragmentManager) {
        Contracts.requireNonNull(fragmentManager, "fragmentManager == null");

        dismissDialog(fragmentManager, DEFAULT_TAG);
    }

    public static void dismissDialog(
        @NonNull final FragmentManager fragmentManager, @NonNull final String tag) {
        Contracts.requireNonNull(fragmentManager, "fragmentManager == null");
        Contracts.requireNonNull(tag, "tag == null");

        final val oldDialogCandidate = fragmentManager.findFragmentByTag(tag);
        if (oldDialogCandidate instanceof PlaygroundsPickerDialogFragment) {
            final val oldDialog = (PlaygroundsPickerDialogFragment) oldDialogCandidate;

            if (oldDialog._pickerSubscriber != null) {
                oldDialog._pickerSubscriber.onCompleted();
            }

            oldDialog.dismiss();
        }
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE: {
                if (_pickerSubscriber != null) {
                    final val pickerAdapter = getPlaygroundPickerAdapter();
                    final val result = new PlaygroundsPickerResult(pickerAdapter.getSelection(),
                                                                   pickerAdapter
                                                                       .getSelectedSportComplexesIds());
                    _pickerSubscriber.onNext(result);
                    _pickerSubscriber.onCompleted();
                }
                break;
            }
            default: {
                if (_pickerSubscriber != null) {
                    _pickerSubscriber.onCompleted();
                }
                break;
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final val playgroundPickerAdapter = getPlaygroundPickerAdapter();

        if (_playgroundsBySportComplex != null) {
            for (final val playgroundsBySportComplex : _playgroundsBySportComplex.entrySet()) {
                playgroundPickerAdapter.addSportComplex(playgroundsBySportComplex.getKey(),
                                                        playgroundsBySportComplex.getValue());
            }
        }

        final val dialog = new AlertDialog.Builder(getContext(), getTheme())
            .setPositiveButton(R.string.playground_picker_save, this)
            .setNegativeButton(android.R.string.cancel, this)
            .create();

        final val dialogContext = dialog.getContext();

        final val inflater = LayoutInflater.from(dialogContext);
        final val view = inflater.inflate(R.layout.layout_playground_picker, null);
        final val playgroundsListView = (RecyclerView) view.findViewById(R.id.playgrounds_list);

        playgroundsListView.setLayoutManager(new LinearLayoutManager(dialogContext));
        playgroundsListView.setAdapter(playgroundPickerAdapter);

        dialog.setView(view);

        return dialog;
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final PlaygroundPickerAdapter _playgroundPickerAdapter = new PlaygroundPickerAdapter();

    @Nullable
    private Subscriber<? super PlaygroundsPickerResult> _pickerSubscriber;

    @Nullable
    private Map<SportComplexTitle, List<PlaygroundTitle>> _playgroundsBySportComplex;

    public static class Builder {
        @NonNull
        public final Builder setPlaygroundsBySportComplex(
            @Nullable
            final Map<SportComplexTitle, List<PlaygroundTitle>> playgroundsBySportComplex) {
            _playgroundsBySportComplex = playgroundsBySportComplex;

            return this;
        }

        @NonNull
        public final Builder setSelection(@Nullable final Map<Long, Set<Long>> selection) {
            _selection = selection;

            return this;
        }

        @NonNull
        public Observable<PlaygroundsPickerResult> show(
            @NonNull final FragmentManager fragmentManager, @NonNull final String tag) {
            Contracts.requireNonNull(fragmentManager, "fragmentManager == null");
            Contracts.requireNonNull(tag, "tag == null");

            return Observable.create(new Observable.OnSubscribe<PlaygroundsPickerResult>() {
                @Override
                public void call(final Subscriber<? super PlaygroundsPickerResult> subscriber) {
                    final val transaction = fragmentManager.beginTransaction();
                    final val oldDialog = fragmentManager.findFragmentByTag(tag);
                    if (oldDialog != null) {
                        transaction.remove(oldDialog);
                    }
                    transaction.addToBackStack(null);

                    final val newDialog = new PlaygroundsPickerDialogFragment();

                    newDialog._pickerSubscriber = subscriber;
                    newDialog._playgroundsBySportComplex = _playgroundsBySportComplex;

                    if (_selection != null) {
                        newDialog.getPlaygroundPickerAdapter().getSelection().putAll(_selection);
                    }

                    newDialog.show(transaction, tag);
                }
            });
        }

        @NonNull
        public Observable<PlaygroundsPickerResult> show(
            @NonNull final FragmentManager fragmentManager) {
            Contracts.requireNonNull(fragmentManager, "fragmentManager == null");

            return show(fragmentManager, DEFAULT_TAG);
        }

        @Nullable
        private Map<SportComplexTitle, List<PlaygroundTitle>> _playgroundsBySportComplex;

        @Nullable
        private Map<Long, Set<Long>> _selection;
    }
}
