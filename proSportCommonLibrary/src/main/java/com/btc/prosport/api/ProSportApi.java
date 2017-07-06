package com.btc.prosport.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;

import com.btc.prosport.api.model.entity.AttributeEntity;
import com.btc.prosport.api.model.entity.CityEntity;
import com.btc.prosport.api.model.entity.CoveringEntity;
import com.btc.prosport.api.model.entity.IntervalEntity;
import com.btc.prosport.api.model.entity.NotificationSubscribeEntity;
import com.btc.prosport.api.model.entity.OrderEntity;
import com.btc.prosport.api.model.entity.PageEntity;
import com.btc.prosport.api.model.entity.PhotoEntity;
import com.btc.prosport.api.model.entity.PlaygroundPreviewEntity;
import com.btc.prosport.api.model.entity.PlaygroundTitleEntity;
import com.btc.prosport.api.model.entity.PriceEntity;
import com.btc.prosport.api.model.entity.SaleEntity;
import com.btc.prosport.api.model.entity.SportComplexDetailEntity;
import com.btc.prosport.api.model.entity.SportComplexInfoEntity;
import com.btc.prosport.api.model.entity.SportComplexLocationEntity;
import com.btc.prosport.api.model.entity.SportComplexPreviewEntity;
import com.btc.prosport.api.model.entity.SportComplexReportEntity;
import com.btc.prosport.api.model.entity.SportComplexTitleEntity;
import com.btc.prosport.api.model.entity.SubwayStationEntity;
import com.btc.prosport.api.model.entity.UserEntity;
import com.btc.prosport.api.request.ChangeActiveNotificationsSubscribeParams;
import com.btc.prosport.api.request.ChangeOrderForUnknownUserParams;
import com.btc.prosport.api.request.ChangeOrderParams;
import com.btc.prosport.api.request.ChangeOrderStateParams;
import com.btc.prosport.api.request.ChangePlaygroundAttributesParams;
import com.btc.prosport.api.request.ChangePlaygroundCoveringParams;
import com.btc.prosport.api.request.ChangePlaygroundDimensionsParams;
import com.btc.prosport.api.request.ChangePriceParams;
import com.btc.prosport.api.request.ChangeUserCityParams;
import com.btc.prosport.api.request.ChangeUserEmailParams;
import com.btc.prosport.api.request.ChangeUserFirstNameParams;
import com.btc.prosport.api.request.ChangeUserLastNameParams;
import com.btc.prosport.api.request.ChangeUserPhoneNumberParams;
import com.btc.prosport.api.request.CreateOrderForMeParams;
import com.btc.prosport.api.request.CreateOrderForUnknownUserParams;
import com.btc.prosport.api.request.CreateOrderParams;
import com.btc.prosport.api.request.CreateSaleParams;
import com.btc.prosport.api.request.NotificationsSubscribeParams;
import com.btc.prosport.api.request.UpdateUserAdditionalInfoParams;
import com.btc.prosport.api.request.UserRegisterParams;
import com.btc.prosport.api.response.AuthorizationResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProSportApi {
    @PUT(ProSportContract.PATH_SEGMENT_ORDERS + "/{order_id}/")
    @NonNull
    Observable<OrderEntity> changeOrder(
        @Path("order_id") long orderId,
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangeOrderParams params);

    @PUT(ProSportContract.PATH_SEGMENT_ORDERS + "/{order_id}/")
    @NonNull
    Observable<OrderEntity> changeOrder(
        @Path("order_id") long orderId,
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangeOrderForUnknownUserParams params);

    @PATCH(ProSportContract.PATH_SEGMENT_ORDERS + "/{order_id}/")
    @NonNull
    Observable<OrderEntity> changeOrderState(
        @Path("order_id") long orderId,
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangeOrderStateParams params);

    @GET(ProSportContract.PATH_SEGMENT_PLAYGROUNDS + "/" + ProSportContract.PATH_SEGMENT_COVERINGS +
         "/")
    @NonNull
    Observable<List<CoveringEntity>> getCoverings(
        @Query("access_token") @NonNull String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/" + ProSportContract.PATH_SEGMENT_ATTRIBUTES +
         "/")
    @NonNull
    Observable<List<AttributeEntity>> getAttributes(
        @Query("access_token") @NonNull String accessToken);

    @PATCH(ProSportContract.PATH_SEGMENT_PLAYGROUNDS + "/{playground_id}/")
    @NonNull
    Observable<ResponseBody> changePlaygroundDimensions(
        @Path("playground_id") long playgroundId,
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangePlaygroundDimensionsParams params);


    @PATCH(ProSportContract.PATH_SEGMENT_PLAYGROUNDS + "/{playground_id}/")
    @NonNull
    Observable<ResponseBody> changePlaygroundCovering(
        @Path("playground_id") long playgroundId,
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangePlaygroundCoveringParams params);

    @PATCH(ProSportContract.PATH_SEGMENT_PLAYGROUNDS + "/{playground_id}/")
    @NonNull
    Observable<ResponseBody> changePlaygroundAttributes(
        @Path("playground_id") long playgroundId,
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangePlaygroundAttributesParams params);

    @PATCH(ProSportContract.PATH_SEGMENT_PLAYERS + "/me/")
    @NonNull
    Observable<UserEntity> changePlayerCity(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangeUserCityParams params);

    @PATCH(ProSportContract.PATH_SEGMENT_PLAYERS + "/me/")
    @NonNull
    Observable<UserEntity> changePlayerEmail(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangeUserEmailParams params);

    @PATCH(ProSportContract.PATH_SEGMENT_PLAYERS + "/me/")
    @NonNull
    Observable<UserEntity> changePlayerFirstName(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangeUserFirstNameParams params);

    @PATCH(ProSportContract.PATH_SEGMENT_PLAYERS + "/me/")
    @NonNull
    Observable<UserEntity> changePlayerLastName(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangeUserLastNameParams params);

    @PATCH(ProSportContract.PATH_SEGMENT_PLAYERS + "/me/")
    @NonNull
    Observable<UserEntity> changePlayerPhoneNumber(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangeUserPhoneNumberParams params);

    @Multipart
    @PATCH(ProSportContract.PATH_SEGMENT_PLAYERS + "/me/")
    @NonNull
    Observable<UserEntity> changePlayerAvatar(
        @Query("access_token") @NonNull String accessToken,
        @Part @NonNull MultipartBody.Part avatar);

    @PATCH(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/")
    @NonNull
    Observable<UserEntity> changeManagerCity(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangeUserCityParams params);

    @PATCH(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/")
    @NonNull
    Observable<UserEntity> changeManagerEmail(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangeUserEmailParams params);

    @PATCH(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/")
    @NonNull
    Observable<UserEntity> changeManagerFirstName(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangeUserFirstNameParams params);

    @PATCH(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/")
    @NonNull
    Observable<UserEntity> changeManagerLastName(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangeUserLastNameParams params);

    @PATCH(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/")
    @NonNull
    Observable<UserEntity> changeManagerPhoneNumber(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull ChangeUserPhoneNumberParams params);

    @Multipart
    @PATCH(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/")
    @NonNull
    Observable<UserEntity> changeManagerAvatar(
        @Query("access_token") @NonNull String accessToken,
        @Part @NonNull MultipartBody.Part avatar);

    @PUT(ProSportContract.PATH_SEGMENT_PLAYGROUNDS + "/{playground_id}/" +
         ProSportContract.PATH_SEGMENT_PRICES + "/")
    @NonNull
    Observable<Response<ResponseBody>> changePrices(
        @Path("playground_id") long playgroundId,
        @Body @NonNull List<ChangePriceParams> params,
        @Query("access_token") String token);

    @POST(ProSportContract.PATH_SEGMENT_ORDERS + "/")
    @NonNull
    Observable<OrderEntity> createOrder(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull CreateOrderParams params);

    @POST(ProSportContract.PATH_SEGMENT_ORDERS + "/")
    @NonNull
    Observable<OrderEntity> createOrder(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull CreateOrderForMeParams params);

    @POST(ProSportContract.PATH_SEGMENT_ORDERS + "/")
    @NonNull
    Observable<OrderEntity> createOrder(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull CreateOrderForUnknownUserParams params);

    @POST(ProSportContract.PATH_SEGMENT_SALES + "/")
    @NonNull
    Observable<SaleEntity> createSale(
        @Query("access_token") @NonNull String accessToken, @Body @NonNull CreateSaleParams params);

    @DELETE(ProSportContract.PATH_SEGMENT_SALES + "/{sale_id}/")
    @NonNull
    Observable<Response<ResponseBody>> deleteSale(
        @Path("sale_id") long saleId, @Query("access_token") @NonNull String accessToken);

    @POST(ProSportContract.PATH_SEGMENT_PLAYERS + "/me/" +
          ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/{sport_complex_id}/favorite/")
    @NonNull
    Observable<Response<ResponseBody>> favoriteSportComplex(
        @Path("sport_complex_id") long sportComplexId,
        @NonNull @Query("access_token") String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_CITIES)
    @NonNull
    Observable<List<CityEntity>> getCities();

    @GET(ProSportContract.PATH_SEGMENT_CITIES + "/{city_id}")
    @NonNull
    Observable<CityEntity> getCity(@Path("city_id") long cityId);

    @GET(ProSportContract.PATH_SEGMENT_PLAYGROUNDS + "/{playground_id}/" +
         ProSportContract.PATH_SEGMENT_INTERVAL)
    @NonNull
    Observable<List<IntervalEntity>> getIntervals(
        @Path("playground_id") long playgroundId, @Query("date") @Nullable String startDate);

    @GET(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/")
    @NonNull
    Observable<UserEntity> getManager(
        @Query("access_token") @NonNull String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/" + ProSportContract.PATH_SEGMENT_ORDERS)
    @NonNull
    Observable<PageEntity<OrderEntity>> getManagerOrders(
        @Query("access_token") @NonNull String token,
        @Query("page") @Nullable Integer page,
        @Query("state") @Nullable Integer state,
        @Query("sport_complex") @Nullable Long sportComplexId,
        @Query("playground") @Nullable Long playgroundId,
        @Query("q") @Nullable String searchQuery,
        @Query("ordering") @Nullable String ordering);

    @GET(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/" + ProSportContract.PATH_SEGMENT_SALES)
    @NonNull
    Observable<PageEntity<SaleEntity>> getManagerSales(
        @Query("access_token") @NonNull String token, @Query("page") @Nullable Integer page);

    @GET(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/" +
         ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_DETAIL)
    @NonNull
    Observable<List<SportComplexDetailEntity>> getManagerSportComplexesDetails(
        @Query("access_token") String token);

    @GET(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/" +
         ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?pagination=true&style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_DETAIL)
    @NonNull
    Observable<PageEntity<SportComplexDetailEntity>> getManagerSportComplexesDetailsPages(
        @Query("access_token") String token);

    @GET(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/" +
         ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_INFO)
    @NonNull
    Observable<List<SportComplexInfoEntity>> getManagerSportComplexesInfos(
        @Query("access_token") String token);

    @GET(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/" +
         ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?pagination=true&style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_INFO)
    @NonNull
    Observable<PageEntity<SportComplexInfoEntity>> getManagerSportComplexesInfosPages(
        @Query("access_token") String token);

    @GET(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/" +
         ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_PREVIEW)
    @NonNull
    Observable<List<SportComplexPreviewEntity>> getManagerSportComplexesPreviews(
        @Query("access_token") String token);

    @GET(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/" +
         ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?pagination=true&style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_PREVIEW)
    @NonNull
    Observable<PageEntity<SportComplexPreviewEntity>> getManagerSportComplexesPreviewsPages(
        @Query("access_token") String token);

    @GET(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/" +
         ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_REPORT)
    @NonNull
    Observable<List<SportComplexReportEntity>> getManagerSportComplexesReports(
        @Query("access_token") String token);

    @GET(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/" +
         ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?pagination=true&style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_REPORT)
    @NonNull
    Observable<PageEntity<SportComplexReportEntity>> getManagerSportComplexesReportsPages(
        @Query("access_token") String token);

    @GET(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/" +
         ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_TITLE)
    @NonNull
    Observable<List<SportComplexTitleEntity>> getManagerSportComplexesTitles(
        @Query("access_token") String token);

    @GET(ProSportContract.PATH_SEGMENT_MANAGERS + "/me/" +
         ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?pagination=true&style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_TITLE)
    @NonNull
    Observable<PageEntity<SportComplexTitleEntity>> getManagerSportComplexesTitlesPages(
        @Query("access_token") String token);

    @GET(ProSportContract.PATH_SEGMENT_DEVICES + "/{registration_id}/")
    @NonNull
    Observable<NotificationSubscribeEntity> getNotificationsSubscribe(
        @Path("registration_id") @NonNull String registrationId);

    @GET(ProSportContract.PATH_SEGMENT_ORDERS + "/{order_id}/")
    @NonNull
    Observable<OrderEntity> getOrder(
        @Path("order_id") long orderId, @Query("access_token") @NonNull String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_PLAYERS + "/me/")
    @NonNull
    Observable<UserEntity> getPlayer(
        @Query("access_token") @NonNull String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_PLAYERS + "/{player_id}/")
    @NonNull
    Observable<UserEntity> getPlayer(
        @Path("player_id") final long playerId, @Query("access_token") @NonNull String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_PLAYERS + "/{player_phone}/")
    @NonNull
    Observable<UserEntity> getPlayer(
        @Path("player_phone") final String playerPhone,
        @Query("access_token") @NonNull String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_PLAYERS + "/me/" + ProSportContract.PATH_SEGMENT_ORDERS)
    @NonNull
    Observable<PageEntity<OrderEntity>> getPlayerOrders(
        @Query("access_token") @NonNull String token,
        @Query("page") @Nullable Integer page,
        @Query("state") @Nullable Integer state);

    @GET(ProSportContract.PATH_SEGMENT_PLAYERS + "/")
    @NonNull
    Observable<PageEntity<UserEntity>> getPlayers(
        @Query("access_token") @NonNull String accessToken,
        @Query("page") @Nullable Integer page,
        @Query("filter_phone") @Nullable String phoneNumber);

    @GET(ProSportContract.PATH_SEGMENT_PLAYGROUNDS + "/{playground_id}/photos")
    @NonNull
    Observable<List<PhotoEntity>> getPlaygroundPhotos(
        @Path("playground_id") long playgroundId);

    @GET(ProSportContract.PATH_SEGMENT_PLAYGROUNDS + "/{playground_id}/?style=" +
         ProSportContract.STYLE_PLAYGROUND_PREVIEW)
    @NonNull
    Observable<PlaygroundPreviewEntity> getPlaygroundPreview(
        @Path("playground_id") long playgroundId,
        @Nullable @Query("access_token") String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_PLAYGROUNDS + "/{playground_id}/?style=" +
         ProSportContract.STYLE_PLAYGROUND_TITLE)
    @NonNull
    Observable<PlaygroundTitleEntity> getPlaygroundTitle(
        @Path("playground_id") long playgroundId,
        @Nullable @Query("access_token") String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/{sport_complex_id}/" +
         ProSportContract.PATH_SEGMENT_PLAYGROUNDS + "/?style=" +
         ProSportContract.STYLE_PLAYGROUND_PREVIEW)
    @NonNull
    Observable<List<PlaygroundPreviewEntity>> getPlaygroundsPreviews(
        @Path("sport_complex_id") long sportComplexId,
        @Query("filter_time") @Nullable String time,
        @Query("filter_date") @Nullable String date,
        @Nullable @Query("access_token") String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/{sport_complex_id}/" +
         ProSportContract.PATH_SEGMENT_PLAYGROUNDS + "/?style=" +
         ProSportContract.STYLE_PLAYGROUND_TITLE)
    @NonNull
    Observable<List<PlaygroundTitleEntity>> getPlaygroundsTitles(
        @Path("sport_complex_id") long sportComplexId,
        @Query("filter_time") @Nullable String time,
        @Query("filter_date") @Nullable String date,
        @Nullable @Query("access_token") String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/{sport_complex_id}/" +
         ProSportContract.PATH_SEGMENT_PLAYGROUNDS + "/?style=" +
         ProSportContract.STYLE_PLAYGROUND_TITLE)
    @NonNull
    Observable<List<PlaygroundTitleEntity>> getPlaygroundsTitles(
        @Path("sport_complex_id") long sportComplexId,
        @Nullable @Query("access_token") String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_PLAYGROUNDS + "/{playgroundId}/" +
         ProSportContract.PATH_SEGMENT_PRICES)
    @NonNull
    Observable<List<PriceEntity>> getPrices(
        @Path("playgroundId") long playgroundId,
        @NonNull @Query("access_token") String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/{sport_complex_id}/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_DETAIL)
    @NonNull
    Observable<SportComplexDetailEntity> getSportComplexDetail(
        @Path("sport_complex_id") long sportComplexId,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/{sport_complex_id}/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_INFO)
    @NonNull
    Observable<SportComplexInfoEntity> getSportComplexInfo(
        @Path("sport_complex_id") long sportComplexId,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/{sport_complex_id}/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_LOCATION)
    @NonNull
    Observable<SportComplexLocationEntity> getSportComplexLocation(
        @Path("sport_complex_id") long sportComplexId,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/{sport_complex_id}/photos")
    @NonNull
    Observable<List<PhotoEntity>> getSportComplexPhotos(
        @Path("sport_complex_id") long sportComplexId);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/{sport_complex_id}/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_PREVIEW)
    @NonNull
    Observable<SportComplexPreviewEntity> getSportComplexPreview(
        @Path("sport_complex_id") long sportComplexId,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/{sport_complex_id}/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_TITLE)
    @NonNull
    Observable<SportComplexTitleEntity> getSportComplexTitle(
        @Path("sport_complex_id") long sportComplexId,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_DETAIL)
    @NonNull
    Observable<List<SportComplexDetailEntity>> getSportComplexesDetails(
        @Query("page") @Nullable Integer page,
        @Query("filter_name") @Nullable String sportComplexName,
        @Query("filter_time_start") @Nullable String startTime,
        @Query("filter_time_end") @Nullable String endTime,
        @Query("filter_date") @Nullable String date,
        @Query("location") @Nullable String location,
        @Query("ordering") @Nullable String ordering,
        @Query("favorites") @Nullable Boolean favorites,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?pagination=true&style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_DETAIL)
    @NonNull
    Observable<PageEntity<SportComplexDetailEntity>> getSportComplexesDetailsPages(
        @Query("page") @Nullable Integer page,
        @Query("filter_name") @Nullable String sportComplexName,
        @Query("filter_time_start") @Nullable String startTime,
        @Query("filter_time_end") @Nullable String endTime,
        @Query("filter_date") @Nullable String date,
        @Query("location") @Nullable String location,
        @Query("ordering") @Nullable String ordering,
        @Query("favorites") @Nullable Boolean favorites,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_INFO)
    @NonNull
    Observable<List<SportComplexInfoEntity>> getSportComplexesInfos(
        @Query("page") @Nullable Integer page,
        @Query("filter_name") @Nullable String sportComplexName,
        @Query("filter_time_start") @Nullable String startTime,
        @Query("filter_time_end") @Nullable String endTime,
        @Query("filter_date") @Nullable String date,
        @Query("location") @Nullable String location,
        @Query("ordering") @Nullable String ordering,
        @Query("favorites") @Nullable Boolean favorites,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?pagination=true&style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_INFO)
    @NonNull
    Observable<PageEntity<SportComplexInfoEntity>> getSportComplexesInfosPages(
        @Query("page") @Nullable Integer page,
        @Query("filter_name") @Nullable String sportComplexName,
        @Query("filter_time_start") @Nullable String startTime,
        @Query("filter_time_end") @Nullable String endTime,
        @Query("filter_date") @Nullable String date,
        @Query("location") @Nullable String location,
        @Query("ordering") @Nullable String ordering,
        @Query("favorites") @Nullable Boolean favorites,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_LOCATION)
    @NonNull
    Observable<List<SportComplexLocationEntity>> getSportComplexesLocations(
        @Query("page") @Nullable Integer page,
        @Query("filter_name") @Nullable String sportComplexName,
        @Query("filter_time_start") @Nullable String startTime,
        @Query("filter_time_end") @Nullable String endTime,
        @Query("filter_date") @Nullable String date,
        @Query("location") @Nullable String location,
        @Query("ordering") @Nullable String ordering,
        @Query("favorites") @Nullable Boolean favorites,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?pagination=true&style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_LOCATION)
    @NonNull
    Observable<PageEntity<SportComplexLocationEntity>> getSportComplexesLocationsPages(
        @Query("page") @Nullable Integer page,
        @Query("filter_name") @Nullable String sportComplexName,
        @Query("filter_time_start") @Nullable String startTime,
        @Query("filter_time_end") @Nullable String endTime,
        @Query("filter_date") @Nullable String date,
        @Query("location") @Nullable String location,
        @Query("ordering") @Nullable String ordering,
        @Query("favorites") @Nullable Boolean favorites,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_PREVIEW)
    @NonNull
    Observable<List<SportComplexPreviewEntity>> getSportComplexesPreviews(
        @Query("page") @Nullable Integer page,
        @Query("filter_name") @Nullable String sportComplexName,
        @Query("filter_time_start") @Nullable String startTime,
        @Query("filter_time_end") @Nullable String endTime,
        @Query("filter_date") @Nullable String date,
        @Query("location") @Nullable String location,
        @Query("ordering") @Nullable String ordering,
        @Query("favorites") @Nullable Boolean favorites,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?pagination=true&style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_PREVIEW)
    @NonNull
    Observable<PageEntity<SportComplexPreviewEntity>> getSportComplexesPreviewsPages(
        @Query("page") @Nullable Integer page,
        @Query("filter_name") @Nullable String sportComplexName,
        @Query("filter_time_start") @Nullable String startTime,
        @Query("filter_time_end") @Nullable String endTime,
        @Query("filter_date") @Nullable String date,
        @Query("location") @Nullable String location,
        @Query("ordering") @Nullable String ordering,
        @Query("favorites") @Nullable Boolean favorites,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_TITLE)
    @NonNull
    Observable<List<SportComplexTitleEntity>> getSportComplexesTitles(
        @Query("page") @Nullable Integer page,
        @Query("filter_name") @Nullable String sportComplexName,
        @Query("filter_time_start") @Nullable String startTime,
        @Query("filter_time_end") @Nullable String endTime,
        @Query("filter_date") @Nullable String date,
        @Query("location") @Nullable String location,
        @Query("ordering") @Nullable String ordering,
        @Query("favorites") @Nullable Boolean favorites,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/?pagination=true&style=" +
         ProSportContract.STYLE_SPORT_COMPLEX_TITLE)
    @NonNull
    Observable<PageEntity<SportComplexTitleEntity>> getSportComplexesTitlesPages(
        @Query("page") @Nullable Integer page,
        @Query("filter_name") @Nullable String sportComplexName,
        @Query("filter_time_start") @Nullable String startTime,
        @Query("filter_time_end") @Nullable String endTime,
        @Query("filter_date") @Nullable String date,
        @Query("location") @Nullable String location,
        @Query("ordering") @Nullable String ordering,
        @Query("favorites") @Nullable Boolean favorites,
        @Query("access_token") @Nullable String accessToken);

    @GET(ProSportContract.PATH_SEGMENT_CITIES + "/{city_id}/" +
         ProSportContract.PATH_SEGMENT_SUBWAY_STATIONS)
    @NonNull
    Observable<List<SubwayStationEntity>> getSubwayStations(@Path("city_id") long cityId);

    @FormUrlEncoded
    @POST(ProSportContract.PATH_SEGMENT_OAUTH_TOKEN + "?grant_type=password")
    @NonNull
    Observable<AuthorizationResponse> logInUser(
        @Field("client_id") @NonNull String clientId,
        @Field("client_secret") @NonNull String clientSecret,
        @Field("username") @NonNull String username,
        @Field("password") @NonNull String password);

    @FormUrlEncoded
    @POST(ProSportContract.PATH_SEGMENT_OAUTH_TOKEN + "?grant_type=refresh_token")
    @NonNull
    Observable<AuthorizationResponse> refreshToken(
        @Field("client_id") @NonNull String clientId,
        @Field("client_secret") @NonNull String clientSecret,
        @Field("refresh_token") @NonNull String refreshToken);

    @POST(ProSportContract.PATH_SEGMENT_PLAYERS + "/")
    @NonNull
    Observable<UserEntity> registerPlayer(@Body UserRegisterParams userRegisterParams);

    @POST(ProSportContract.PATH_SEGMENT_DEVICES + "/")
    @NonNull
    Observable<NotificationSubscribeEntity> subscribeNotifications(
        @Query("access_token") @Nullable String accessToken,
        @Body @NonNull final NotificationsSubscribeParams params);

    @POST(ProSportContract.PATH_SEGMENT_PLAYERS + "/me/" +
          ProSportContract.PATH_SEGMENT_SPORT_COMPLEXES + "/{sport_complex_id}/unfavorite/")
    @NonNull
    Observable<Response<ResponseBody>> unfavoriteSportComplex(
        @Path("sport_complex_id") long playgroundId,
        @NonNull @Query("access_token") String accessToken);

    @DELETE(ProSportContract.PATH_SEGMENT_DEVICES + "/{registration_id}/")
    @NonNull
    Observable<ResponseBody> unsubscribeNotifications(
        @Path("registration_id") @NonNull String registrationId);

    @PATCH(ProSportContract.PATH_SEGMENT_DEVICES + "/{registration_id}/")
    @NonNull
    Observable<NotificationSubscribeEntity> updateNotificationsSubscribe(
        @Path("registration_id") @NonNull String registrationId,
        @Query("access_token") @Nullable String accessToken,
        @Body @NonNull final ChangeActiveNotificationsSubscribeParams params);

    @PATCH(ProSportContract.PATH_SEGMENT_PLAYERS + "/me/")
    @NonNull
    Observable<UserEntity> updateUserAdditionalInfo(
        @Query("access_token") @NonNull String accessToken,
        @Body @NonNull final UpdateUserAdditionalInfoParams params);
}
