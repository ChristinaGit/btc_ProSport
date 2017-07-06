package com.btc.prosport.api;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.UriScheme;
import com.btc.common.contract.Contracts;
import com.btc.common.utility.UriUtils;

@Accessors(prefix = "_")
public final class ProSportContract {
    public static final String STYLE_SPORT_COMPLEX_PREVIEW = "preview";

    public static final String STYLE_SPORT_COMPLEX_INFO = "info";

    public static final String STYLE_SPORT_COMPLEX_DETAIL = "detail";

    public static final String STYLE_SPORT_COMPLEX_REPORT = "report";

    public static final String STYLE_SPORT_COMPLEX_TITLE = "title";

    public static final String STYLE_SPORT_COMPLEX_LOCATION = "location";

    public static final String STYLE_SPORT_COMPLEX_DEFAULT = STYLE_SPORT_COMPLEX_DETAIL;

    public static final String STYLE_PLAYGROUND_TITLE = "title";

    public static final String STYLE_PLAYGROUND_PREVIEW = "preview";

    public static final String STYLE_PLAYGROUND_DEFAULT = STYLE_PLAYGROUND_PREVIEW;

    public static final String HOST = "*removed_for_preview*";

    public static final int PORT = 80;

    public static final String PATH_SEGMENT_API_V1 = "apis/v1";

    public static final String PATH_SEGMENT_PLAYERS = "players";

    public static final String PATH_SEGMENT_PLAYGROUNDS = "playgrounds";

    public static final String PATH_SEGMENT_PRICES = "prices";

    public static final String PATH_SEGMENT_SPORT_COMPLEXES = "sport_complexes";

    public static final String PATH_SEGMENT_MANAGERS = "managers";

    public static final String PATH_SEGMENT_SALES = "sales";

    public static final String PATH_SEGMENT_CITIES = "cities";

    public static final String PATH_SEGMENT_DEVICES = "devices";

    public static final String PATH_SEGMENT_COVERINGS = "coverings";

    public static final String PATH_SEGMENT_ATTRIBUTES = "attributes";

    public static final String PATH_SEGMENT_SUBWAY_STATIONS = "subway_stations";

    public static final String PATH_SEGMENT_ORDERS = "orders";

    public static final String PATH_SEGMENT_INTERVAL = "intervals";

    public static final String PATH_SEGMENT_OAUTH_TOKEN = "/o/token/";

    public static final int CODE_SPORT_COMPLEX_ALL;

    public static final int CODE_SPORT_COMPLEX;

    public static final int CODE_PLAYGROUND_ALL;

    public static final int CODE_PLAYGROUND;

    public static final int CODE_CITY_ALL;

    public static final int CODE_ORDER_ALL;

    public static final int CODE_ORDER;

    public static final String SCHEME = UriScheme.HTTP.getSchemeName();

    @NonNull
    private static final UriMatcher _matcher;

    static {
        int codeIndexer = UriMatcher.NO_MATCH;

        CODE_SPORT_COMPLEX_ALL = ++codeIndexer;
        CODE_SPORT_COMPLEX = ++codeIndexer;
        CODE_PLAYGROUND_ALL = ++codeIndexer;
        CODE_PLAYGROUND = ++codeIndexer;
        CODE_CITY_ALL = ++codeIndexer;
        CODE_ORDER_ALL = ++codeIndexer;
        CODE_ORDER = ++codeIndexer;

        _matcher = new UriMatcher(UriMatcher.NO_MATCH);

        _matcher.addURI(HOST,
                        UriUtils.combinePath(PATH_SEGMENT_API_V1, PATH_SEGMENT_SPORT_COMPLEXES),
                        CODE_SPORT_COMPLEX_ALL);
        _matcher.addURI(HOST,
                        UriUtils.combinePath(PATH_SEGMENT_API_V1,
                                             PATH_SEGMENT_SPORT_COMPLEXES,
                                             UriUtils.NUMBER_PLACEHOLDER),
                        CODE_SPORT_COMPLEX);

        _matcher.addURI(HOST,
                        UriUtils.combinePath(PATH_SEGMENT_API_V1, PATH_SEGMENT_PLAYGROUNDS),
                        CODE_PLAYGROUND_ALL);
        _matcher.addURI(HOST,
                        UriUtils.combinePath(PATH_SEGMENT_API_V1,
                                             PATH_SEGMENT_PLAYGROUNDS,
                                             UriUtils.NUMBER_PLACEHOLDER),
                        CODE_PLAYGROUND);

        _matcher.addURI(HOST, PATH_SEGMENT_CITIES, CODE_CITY_ALL);

        _matcher.addURI(HOST,
                        UriUtils.combinePath(PATH_SEGMENT_API_V1, PATH_SEGMENT_ORDERS),
                        CODE_ORDER_ALL);
        _matcher.addURI(HOST,
                        UriUtils.combinePath(PATH_SEGMENT_API_V1,
                                             PATH_SEGMENT_ORDERS,
                                             UriUtils.NUMBER_PLACEHOLDER),
                        CODE_ORDER);
    }

    public static int getCode(@NonNull final Uri uri) {
        Contracts.requireNonNull(uri, "uri == null");

        return _matcher.match(uri);
    }

    @NonNull
    public static Uri getBaseUri() {
        return new Uri.Builder().scheme(SCHEME).authority(HOST).build();
    }

    @NonNull
    public static Uri getSportComplexUri(final long sportComplexId) {
        return getSportComplexUri(String.valueOf(sportComplexId));
    }

    @NonNull
    public static Uri getSportComplexUri(@Nullable final String sportComplexId) {
        final val builder = new Uri.Builder()
            .scheme(SCHEME)
            .encodedAuthority(HOST)
            .appendEncodedPath(PATH_SEGMENT_API_V1)
            .appendEncodedPath(PATH_SEGMENT_SPORT_COMPLEXES);

        if (sportComplexId != null) {
            builder.appendPath(sportComplexId);
        }

        return builder.build();
    }

    @NonNull
    public static Uri getPlaygroundUri(final long playgroundId) {
        return getPlaygroundUri(String.valueOf(playgroundId));
    }

    @NonNull
    public static Uri getPlaygroundUri(@Nullable final String playgroundId) {
        final val builder = new Uri.Builder()
            .scheme(SCHEME)
            .encodedAuthority(HOST)
            .appendEncodedPath(PATH_SEGMENT_API_V1)
            .appendEncodedPath(PATH_SEGMENT_PLAYGROUNDS);

        if (playgroundId != null) {
            builder.appendPath(playgroundId);
        }

        return builder.build();
    }

    @NonNull
    public static Uri getOrderUri(final long orderId) {
        return getOrderUri(String.valueOf(orderId));
    }

    @NonNull
    public static Uri getOrderUri(@Nullable final String orderId) {
        final val builder = new Uri.Builder()
            .scheme(SCHEME)
            .encodedAuthority(HOST)
            .appendEncodedPath(PATH_SEGMENT_API_V1)
            .appendEncodedPath(PATH_SEGMENT_ORDERS);

        if (orderId != null) {
            builder.appendPath(orderId);
        }

        return builder.build();
    }

    @NonNull
    public static Uri getPlaygroundUri() {
        return getPlaygroundUri(null);
    }

    private ProSportContract() {
        Contracts.unreachable();
    }
}
