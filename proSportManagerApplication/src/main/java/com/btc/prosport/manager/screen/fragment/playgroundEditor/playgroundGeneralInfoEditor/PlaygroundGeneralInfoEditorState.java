package com.btc.prosport.manager.screen.fragment.playgroundEditor.playgroundGeneralInfoEditor;

import android.net.Uri;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.google.android.gms.maps.model.LatLngBounds;

import org.parceler.Parcel;

import java.util.List;

@Parcel
@Accessors(prefix = "_")
public class PlaygroundGeneralInfoEditorState {
    @Getter
    @Setter
    @Nullable
    /*package-private*/ String _address;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _cityId;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Double _latitude;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Double _longitude;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ List<String> _phoneNumberList;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ List<Uri> _photoUriList;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ String _placeId;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _playgroundId;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ String _playgroundName;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ List<Long> _subwayIdList;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ LatLngBounds _viewPort;
}
