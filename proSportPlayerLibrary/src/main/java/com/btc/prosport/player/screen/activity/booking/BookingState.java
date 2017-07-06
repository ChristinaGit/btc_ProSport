package com.btc.prosport.player.screen.activity.booking;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.parceler.Parcel;

@Parcel
@Accessors(prefix = "_")
/*package-private*/ final class BookingState {
    @Getter
    @Setter
    /*package-private*/ boolean _datePickerExpanded = false;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _playgroundId;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _startReservationDate;
}
