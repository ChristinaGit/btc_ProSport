package com.btc.prosport.manager.screen.activity.workspace;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.btc.prosport.manager.core.OrdersSortOrder;

import org.parceler.Parcel;

@Parcel
@Accessors(prefix = "_")
/*package-private*/ final class WorkspaceState {
    @Getter
    @Setter
    @Nullable
    /*package-private*/ Integer _activeTabId;

    @Getter
    @Setter
    /*package-private*/ boolean _datePickerExpanded = false;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ OrdersFilterParams _ordersFilterParams;

    @Getter
    @Setter
    /*package-private*/ boolean _ordersSearchEnabled;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ OrdersSortOrder _ordersSortOrder;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _selectedPlaygroundId;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _startDisplayedDate;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ OrdersFilterParams _statusOrdersFilterParams;
}
