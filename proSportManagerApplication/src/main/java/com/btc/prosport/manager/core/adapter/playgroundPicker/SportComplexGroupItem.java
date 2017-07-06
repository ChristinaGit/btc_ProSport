package com.btc.prosport.manager.core.adapter.playgroundPicker;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.SportComplexTitle;

import java.util.List;

@Accessors(prefix = "_")
public final class SportComplexGroupItem {
    public SportComplexGroupItem(
        @NonNull final SportComplexTitle sportComplex,
        @NonNull final List<PlaygroundTitle> playgrounds) {
        Contracts.requireNonNull(sportComplex, "sportComplex == null");
        Contracts.requireNonNull(playgrounds, "playgrounds == null");

        _sportComplex = sportComplex;
        _playgrounds = playgrounds;
    }

    @Getter
    @NonNull
    private final List<PlaygroundTitle> _playgrounds;

    @Getter
    @NonNull
    private final SportComplexTitle _sportComplex;
}
