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
import com.btc.common.utility.listener.SimpleTextWatcher;
import com.btc.prosport.manager.R;

@Accessors(prefix = "_")
public class NameViewHolder extends ExtendedRecyclerViewHolder {
    @BindView(R.id.name_input)
    @NonNull
    public TextInputEditText _nameInput;

    @BindView(R.id.name_input_layout)
    @NonNull
    public TextInputLayout _nameInputLayout;

    public NameViewHolder(
        @NonNull final View itemView, @NonNull final SimpleTextWatcher textWatcher) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));
        Contracts.requireNonNull(textWatcher, "textWatcher == null");

        bindViews(itemView);

        _textWatcher = textWatcher;
    }

    @Getter
    @NonNull
    private final SimpleTextWatcher _textWatcher;
}
