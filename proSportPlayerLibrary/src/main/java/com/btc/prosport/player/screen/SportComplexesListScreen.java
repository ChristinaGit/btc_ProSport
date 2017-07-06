package com.btc.prosport.player.screen;

import android.support.annotation.Nullable;

import com.btc.prosport.api.model.SportComplexPreview;

import java.util.List;

public interface SportComplexesListScreen extends SportComplexesViewerPageScreen {
    void displaySportComplexes(
        @Nullable List<SportComplexPreview> sportComplexes, boolean lastPage);

    void displaySportComplexesPage(
        @Nullable List<SportComplexPreview> sportComplexes, boolean lastPage);
}
