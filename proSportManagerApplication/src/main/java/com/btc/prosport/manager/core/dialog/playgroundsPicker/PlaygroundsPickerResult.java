package com.btc.prosport.manager.core.dialog.playgroundsPicker;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Set;

@ToString()
@Accessors(prefix = "_")
public final class PlaygroundsPickerResult {
    @Nullable
    public final Set<Long> getSelectedPlaygroundsIds(final long sportComplexId) {
        return _selection == null ? null : _selection.get(sportComplexId);
    }

    @Nullable
    public final Set<Long> getSportComplexesWithSelectionIds() {
        return _selection == null ? null : _selection.keySet();
    }

    /*package-private*/ PlaygroundsPickerResult(
        @Nullable final Map<Long, Set<Long>> selection,
        @Nullable final Set<Long> selectedSportComplexesIds) {
        _selection = selection;
        _selectedSportComplexesIds = selectedSportComplexesIds;
    }

    @Getter
    @Nullable
    private final Set<Long> _selectedSportComplexesIds;

    @Getter
    @Nullable
    private final Map<Long, Set<Long>> _selection;
}
