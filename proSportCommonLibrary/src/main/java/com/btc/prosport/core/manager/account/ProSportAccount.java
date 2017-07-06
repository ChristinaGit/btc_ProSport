package com.btc.prosport.core.manager.account;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;
import org.parceler.ParcelProperty;

@ToString()
@Parcel
@Accessors(prefix = "_")
public class ProSportAccount {
    @ParcelConstructor
    public ProSportAccount(@ParcelProperty("phoneNumber") @NonNull final String phoneNumber) {
        Contracts.requireNonNull(phoneNumber, "phoneNumber == null");

        _phoneNumber = phoneNumber;
    }

    @Getter
    @ParcelProperty("phoneNumber")
    @NonNull
    /*package-private*/ final String _phoneNumber;
}
