package com.btc.prosport.core.utility;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.model.User;

import org.apache.commons.lang3.StringUtils;

public final class UserUtils {
    @Nullable
    public static String getDisplayContactUserName(@NonNull final User user) {
        Contracts.requireNonNull(user, "user == null");

        final String displayUserName;

        final val firstName = user.getFirstName();
        final val lastName = user.getLastName();

        final val hasFirstName = !TextUtils.isEmpty(firstName);
        final val hasLastName = !TextUtils.isEmpty(lastName);

        if (hasFirstName && hasLastName) {
            displayUserName =
                String.format("%1$s %2$s (%3$s)", firstName, lastName, user.getPhoneNumber());
        } else if (!hasFirstName && !hasLastName) {
            displayUserName = user.getPhoneNumber();
        } else {
            displayUserName = String.format(
                "%1$s (%2$s)",
                StringUtils.defaultString(firstName, lastName),
                user.getPhoneNumber());
        }

        return displayUserName;
    }

    @Nullable
    public static String getDisplayUserNameOrPhone(@NonNull final User user) {
        Contracts.requireNonNull(user, "user == null");

        final String displayUserName;

        final val firstName = user.getFirstName();
        final val lastName = user.getLastName();

        final val hasFirstName = !TextUtils.isEmpty(firstName);
        final val hasLastName = !TextUtils.isEmpty(lastName);

        if (hasFirstName && hasLastName) {
            displayUserName = String.format("%1$s %2$s", firstName, lastName);
        } else if (!hasFirstName && !hasLastName) {
            displayUserName = user.getPhoneNumber();
        } else {
            displayUserName = StringUtils.defaultString(firstName, lastName);
        }

        return displayUserName;
    }

    @Nullable
    public static String getDisplayUserName(@NonNull final User user) {
        Contracts.requireNonNull(user, "user == null");

        final String displayUserName;

        final val firstName = user.getFirstName();
        final val lastName = user.getLastName();

        final val hasFirstName = !TextUtils.isEmpty(firstName);
        final val hasLastName = !TextUtils.isEmpty(lastName);

        if (hasFirstName && hasLastName) {
            displayUserName = String.format("%1$s %2$s", firstName, lastName);
        } else {
            displayUserName = StringUtils.defaultString(firstName, lastName);
        }

        return displayUserName;
    }

    private UserUtils() {
        Contracts.unreachable();
    }
}
