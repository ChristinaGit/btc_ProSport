package com.btc.prosport.player.core.utility;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.model.SportComplexDetail;
import com.btc.prosport.api.model.SportComplexInfo;
import com.btc.prosport.api.model.SportComplexLocation;
import com.btc.prosport.api.model.WorkPeriod;
import com.btc.prosport.api.model.utility.WeekDay;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.player.core.PlaygroundPlaceInfo;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public final class SportComplexUtils {
    @Nullable
    private static WorkPeriod getTodayWorkPeriod(
        @Nullable final List<? extends WorkPeriod> workPeriods, @NonNull final Date localTime) {
        Contracts.requireNonNull(localTime, "localTime == null");

        WorkPeriod todayWorkPeriod = null;

        final val localCalendar = Calendar.getInstance();
        localCalendar.setTime(localTime);
        final int dayOfWeek = localCalendar.get(Calendar.DAY_OF_WEEK);

        if (workPeriods != null) {
            for (final val workPeriod : workPeriods) {
                if (Objects.equals(workPeriod.getWeekDay(),
                                   WeekDay.byCode(dayOfWeek).getId())) {
                    todayWorkPeriod = workPeriod;
                    break;
                }
            }
        }

        return todayWorkPeriod;
    }

    @Nullable
    public static WorkPeriod getTodayWorkPeriod(@NonNull final SportComplexDetail sportComplex) {
        Contracts.requireNonNull(sportComplex, "sportComplex == null");

        final WorkPeriod workPeriod;

        final val localTime = ProSportApiDataUtils.parseDateTime(sportComplex.getLocalTime());
        if (localTime != null) {
            workPeriod = getTodayWorkPeriod(sportComplex.getWorkPeriods(), localTime);
        } else {
            workPeriod = null;
        }

        return workPeriod;
    }

    @Nullable
    public static WorkPeriod getTodayWorkPeriod(@NonNull final SportComplexInfo sportComplex) {
        Contracts.requireNonNull(sportComplex, "sportComplex == null");

        final WorkPeriod workPeriod;

        final val localTime = ProSportApiDataUtils.parseDateTime(sportComplex.getLocalTime());
        if (localTime != null) {
            workPeriod = getTodayWorkPeriod(sportComplex.getWorkPeriods(), localTime);
        } else {
            workPeriod = null;
        }

        return workPeriod;
    }

    @NonNull
    public static PlaygroundPlaceInfo getPlaygroundPlaceInfo(@NonNull final Place place) {
        Contracts.requireNonNull(place, "place == null");

        final val playgroundPlaceInfo = new PlaygroundPlaceInfo();

        playgroundPlaceInfo.setAddress(place.getAddress());
        playgroundPlaceInfo.setAttributions(place.getAttributions());
        playgroundPlaceInfo.setCoordinates(place.getLatLng());
        playgroundPlaceInfo.setViewport(place.getViewport());
        playgroundPlaceInfo.setLocale(place.getLocale());
        playgroundPlaceInfo.setName(place.getName());
        playgroundPlaceInfo.setPhoneNumber(place.getPhoneNumber());
        playgroundPlaceInfo.setPlaceId(place.getId());
        playgroundPlaceInfo.setPriceLevel(place.getPriceLevel());
        playgroundPlaceInfo.setRating(place.getRating());
        playgroundPlaceInfo.setWebsiteUr(place.getWebsiteUri());

        return playgroundPlaceInfo;
    }

    @Nullable
    public static LatLng getCoordinates(@NonNull final SportComplexLocation sportComplex) {
        Contracts.requireNonNull(sportComplex, "sportComplex == null");

        final LatLng coordinates;

        final Double latitude = sportComplex.getLocationLatitude();
        final Double longitude = sportComplex.getLocationLongitude();

        if (latitude != null && longitude != null) {
            coordinates = new LatLng(latitude, longitude);
        } else {
            coordinates = null;
        }

        return coordinates;
    }

    private SportComplexUtils() {
        Contracts.unreachable();
    }
}
