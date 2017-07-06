package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.viewHolder;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;

import lombok.Getter;
import lombok.experimental.Accessors;

import butterknife.BindView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoAdapter;

@Accessors(prefix = "_")
public class PhoneNumberViewHolder extends ExtendedRecyclerViewHolder {
    @BindView(R.id.phone_input_layout)
    @NonNull
    public TextInputLayout _phoneInputLayout;

    @BindView(R.id.phone_input)
    @NonNull
    public TextInputEditText _phoneNumber;

    public PhoneNumberViewHolder(
        @NonNull final View itemView,
        @NonNull final GeneralInfoAdapter.PhoneTextWatcher phoneTextWatcher) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));
        Contracts.requireNonNull(phoneTextWatcher, "phoneTextWatcher == null");

        _phoneTextWatcher = phoneTextWatcher;

        bindViews(itemView);
    }

    @Getter
    @NonNull
    private final GeneralInfoAdapter.PhoneTextWatcher _phoneTextWatcher;
}
