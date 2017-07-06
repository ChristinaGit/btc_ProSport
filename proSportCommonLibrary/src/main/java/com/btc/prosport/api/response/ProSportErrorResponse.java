package com.btc.prosport.api.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ToString
@Accessors(prefix = "_")
public class ProSportErrorResponse {
    public static final String FIELD_DETAIL = "detail";

    public static final String FIELD_NON_FIELD_ERRORS = "non_field_errors";

    public ProSportErrorResponse(@Nullable final Map<String, Object> errorByField) {
        _errorByField = errorByField;
    }

    @Nullable
    public final List<String> getAllErrors() {
        List<String> allErrors = null;

        for (final val errorCandidate : getErrorByField().values()) {
            if (allErrors == null) {
                allErrors = new ArrayList<>();
            }

            if (errorCandidate instanceof String) {
                allErrors.add((String) errorCandidate);
            } else if (errorCandidate instanceof Collection) {
                final val errors = new ArrayList<String>();
                CollectionUtils.select((Collection) errorCandidate, new Predicate<Object>() {
                    @Override
                    public boolean evaluate(final Object object) {
                        return object instanceof String;
                    }
                }, errors);
                allErrors.addAll(errors);
            }
        }

        return allErrors;
    }

    @Nullable
    public final String getDetail() {
        return (String) getFiledError(FIELD_DETAIL);
    }

    @Nullable
    public final List<String> getNonFieldErrors() {
        return (List<String>) getFiledError(FIELD_NON_FIELD_ERRORS);
    }

    @Nullable
    public Object getFiledError(@NonNull final String field) {
        Contracts.requireNonNull(field, "field == null");

        return getErrorByField().get(field);
    }

    @Nullable
    private final Map<String, Object> _errorByField;

    @NonNull
    private Map<String, Object> getErrorByField() {
        return _errorByField == null ? Collections.<String, Object>emptyMap() : _errorByField;
    }
}
