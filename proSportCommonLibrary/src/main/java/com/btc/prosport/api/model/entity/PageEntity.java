package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.Page;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class PageEntity<TEntity> implements Page<TEntity> {
    public static final String FIELD_ENTRIES = "results";

    public static final String FIELD_ENTRIES_COUNT = "count";

    public static final String FIELD_NEXT_PAGE_URI = "next";

    public static final String FIELD_PREVIOUS_PAGE_URI = "previous";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ENTRIES)
    @Nullable
    private List<TEntity> _entries;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ENTRIES_COUNT)
    private int _entriesCount;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_NEXT_PAGE_URI)
    @Nullable
    private String _nextPageUri;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PREVIOUS_PAGE_URI)
    @Nullable
    private String _previousPageUri;
}
