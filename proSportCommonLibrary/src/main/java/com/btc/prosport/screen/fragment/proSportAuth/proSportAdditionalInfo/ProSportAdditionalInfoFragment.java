package com.btc.prosport.screen.fragment.proSportAuth.proSportAdditionalInfo;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.common.utility.ImeUtils;
import com.btc.common.utility.listener.SimpleTextWatcher;
import com.btc.prosport.common.R;
import com.btc.prosport.di.qualifier.PresenterNames;
import com.btc.prosport.screen.ProSportAdditionalInfoScreen;
import com.btc.prosport.screen.fragment.proSportAuth.ProSportAuthFragment;

import javax.inject.Inject;
import javax.inject.Named;

// TODO: 16.02.2017 Need to fix showing keyboard after request focus.
@Accessors(prefix = "_")
public class ProSportAdditionalInfoFragment extends ProSportAuthFragment
    implements ProSportAdditionalInfoScreen {

    public static void startView(
        @NonNull final FragmentManager fragmentManager, final int containerId) {
        Contracts.requireNonNull(fragmentManager, "fragmentManager == null");

        val fragment = new ProSportAdditionalInfoFragment();

        val fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentById(containerId) != null) {
            fragmentTransaction.setCustomAnimations(R.anim.enter,
                                                    R.anim.exit,
                                                    R.anim.pop_enter,
                                                    R.anim.pop_exit);
        }
        fragmentTransaction.replace(containerId, fragment).commit();
    }

    @CallSuper
    @Override
    public void bindViews(@NonNull final View source) {
        super.bindViews(source);

        _contentLayout =
            (ConstraintLayout) source.findViewById(R.id.prosport_additional_info_content);
        _progressBar = (ContentLoaderProgressBar) source.findViewById(R.id.progress_bar);

        _firstName = (TextInputEditText) source.findViewById(R.id.prosport_first_name_input);
        _lastName = (TextInputEditText) source.findViewById(R.id.prosport_last_name_input);
        _email = (TextInputEditText) source.findViewById(R.id.prosport_email_input);

        _firstNameLayout =
            (TextInputLayout) source.findViewById(R.id.prosport_first_name_input_layout);
        _lastNameLayout =
            (TextInputLayout) source.findViewById(R.id.prosport_last_name_input_layout);
        _emailLayout = (TextInputLayout) source.findViewById(R.id.prosport_email_input_layout);

        _sendData = (Button) source.findViewById(R.id.send_additional_info_button);

        if (_sendData != null) {
            _sendData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    performSendAdditionalInfo();
                }
            });
        }

        if (_email != null) {
            _email.setOnEditorActionListener(_emailActionListener);
            _email.addTextChangedListener(_emailTextWatcher);
        }

        if (_lastName != null) {
            _lastName.addTextChangedListener(_lastNameTextWatcher);
        }

        if (_firstName != null) {
            _firstName.addTextChangedListener(_firstNameTextWatcher);
        }

        _loadingDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_contentLayout)
            .setLoadingView(_progressBar)
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .build();

        _loadingDelegate.showContent();
    }

    @Override
    public void unbindViews() {
        super.unbindViews();

        if (_firstName != null) {
            _firstName.removeTextChangedListener(_firstNameTextWatcher);
        }
        if (_lastName != null) {
            _lastName.removeTextChangedListener(_lastNameTextWatcher);
        }
        if (_email != null) {
            _email.removeTextChangedListener(_emailTextWatcher);
        }
    }

    @CallSuper
    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(Contracts.requireNonNull(inflater, "inflater == null"),
                           container,
                           savedInstanceState);

        val inflate = inflater.inflate(R.layout.fragment_additional_info, container, false);

        bindViews(inflate);

        return inflate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbindViews();
    }

    @CallSuper
    @Override
    protected void onReleaseInjectedMembers() {
        super.onReleaseInjectedMembers();

        val presenter = getPresenter();
        if (presenter != null) {
            presenter.unbindScreen();
        }
    }

    @Override
    public void displayContent() {
        val loadingDelegate = getLoadingDelegate();
        if (loadingDelegate != null) {
            loadingDelegate.showContent();
        }
    }

    @Override
    public void displayEmailAddressError(@Nullable final String errorMessage) {
        if (_emailLayout != null) {
            _emailLayout.setError(errorMessage);
        }
    }

    @Override
    public void displayFirstNameError(@Nullable final String errorMessage) {
        if (_firstNameLayout != null) {
            _firstNameLayout.setError(errorMessage);
        }
    }

    @Override
    public void displayLastNameError(@Nullable final String errorMessage) {
        if (_lastNameLayout != null) {
            _lastNameLayout.setError(errorMessage);
        }
    }

    @Override
    public void displayLoading() {
        val loadingDelegate = getLoadingDelegate();
        if (loadingDelegate != null) {
            loadingDelegate.showLoading();
        }
    }

    @CallSuper
    @Override
    public void displaySuccessfulSendData() {
        finishAuth();
    }

    @NonNull
    @Override
    public Event<AdditionalInfoArgs> getSendAdditionInfoEvent() {
        return _sendAdditionalInfoEvent;
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getProSportSubscreenComponent().inject(this);
        val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    protected void performSendAdditionalInfo() {
        if (_firstName != null && _lastName != null && _email != null) {
            ImeUtils.hideIme(_firstName);
            _sendAdditionalInfoEvent.rise(new AdditionalInfoArgs(_firstName.getText().toString(),
                                                                 _lastName.getText().toString(),
                                                                 _email.getText().toString()));
        }
    }

    @Named(PresenterNames.ADDITIONAL_INFO)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<ProSportAdditionalInfoScreen> _presenter;

    private final ManagedEvent<AdditionalInfoArgs> _sendAdditionalInfoEvent = Events.createEvent();

    @Nullable
    private ConstraintLayout _contentLayout;

    @Nullable
    private TextInputEditText _email;

    @Nullable
    private TextInputLayout _emailLayout;

    @NonNull
    private final SimpleTextWatcher _emailTextWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(
            final CharSequence s, final int start, final int before, final int count) {
            super.onTextChanged(s, start, before, count);

            if (_emailLayout != null) {
                _emailLayout.setError(null);
            }
        }
    };

    @Nullable
    private TextInputEditText _firstName;

    @Nullable
    private TextInputLayout _firstNameLayout;

    @NonNull
    private final SimpleTextWatcher _firstNameTextWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(
            final CharSequence s, final int start, final int before, final int count) {
            super.onTextChanged(s, start, before, count);

            if (_firstNameLayout != null) {
                _firstNameLayout.setError(null);
            }
        }
    };

    @Nullable
    private TextInputEditText _lastName;

    @NonNull
    private final TextView.OnEditorActionListener _emailActionListener =
        new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(
                final TextView textView, final int id, final KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    performSendAdditionalInfo();
                    return true;
                }
                return false;
            }
        };

    @Nullable
    private TextInputLayout _lastNameLayout;

    @NonNull
    private final SimpleTextWatcher _lastNameTextWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(
            final CharSequence s, final int start, final int before, final int count) {
            super.onTextChanged(s, start, before, count);

            if (_lastNameLayout != null) {
                _lastNameLayout.setError(null);
            }
        }
    };

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _loadingDelegate;

    @Nullable
    private ContentLoaderProgressBar _progressBar;

    @Nullable
    private Button _sendData;
}
