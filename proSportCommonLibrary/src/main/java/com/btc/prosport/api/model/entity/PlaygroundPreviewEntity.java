package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.PlaygroundPreview;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class PlaygroundPreviewEntity implements PlaygroundPreview {
    public static final String FIELD_ID = "id";

    public static final String FIELD_SPORT_COMPLEX_ID = "sport_complex";

    public static final String FIELD_NAME = "name";

    public static final String FIELD_COVERING = "covering";

    public static final String FIELD_HEIGHT = "height";

    public static final String FIELD_LENGTH = "length";

    public static final String FIELD_PART_LENGTH = "part_length";

    public static final String FIELD_PART_RESERVATION_SUPPORTED_STATE = "part";

    public static final String FIELD_PART_WIDTH = "part_width";

    public static final String FIELD_PHOTO = "photo";

    public static final String FIELD_TYPE = "type";

    public static final String FIELD_WIDTH = "width";

    public static final String FIELD_ATTRIBUTES = "attribute";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ATTRIBUTES)
    @Nullable
    private List<AttributeEntity> _attributes;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_COVERING)
    @Nullable
    private CoveringEntity _covering;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_HEIGHT)
    @Nullable
    private Integer _height;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ID)
    private long _id;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_LENGTH)
    @Nullable
    private Integer _length;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_NAME)
    @Nullable
    private String _name;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PART_LENGTH)
    @Nullable
    private Integer _partLength;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PART_RESERVATION_SUPPORTED_STATE)
    @Nullable
    private Boolean _partReservationSupportedState;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PART_WIDTH)
    @Nullable
    private Integer _partWidth;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PHOTO)
    @Nullable
    private PhotoEntity _photo;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_SPORT_COMPLEX_ID)
    private long _sportComplexId;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_TYPE)
    @Nullable
    private PlaygroundTypeEntity _type;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_WIDTH)
    @Nullable
    private Integer _width;
}
