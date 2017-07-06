package com.btc.prosport.api.debug;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.val;

import rx.Observable;

import com.btc.common.contract.Contracts;
import com.btc.common.utility.RandomUtils;
import com.btc.prosport.api.ProSportApi;
import com.btc.prosport.api.ProSportDataContract;
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
import com.btc.prosport.api.model.entity.WorkPeriodEntity;
import com.btc.prosport.api.model.utility.IntervalState;
import com.btc.prosport.api.model.utility.WeekDay;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Callable;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public final class DebugProSportApi implements ProSportApi {
    private final static String[] ADDRESSES =
        {"Московская 2", "Тихонова 34", "Рандомная 33", "Мирная 14", "Религиозная 88"};

    private final static String[] INVENTORY_DESCRIPTIONS =
        {"Мяч, Ворота", "Сетка, Ворота", "Сетка, Раздевалка"};

    private final static String[] SPORT_COMPLEX_NAMES =
        {"Арена спорта", "Подвал футбола", "Стадион победы", "Арена \"Звезда\""};

    private final static String[] PLAYGROUND_NAMES =
        {"Глваное поле", "Подвальное поле", "Поле №2", "Поле VIP"};

    private final static String[] ATTRIBUTE_NAMES = {"Свет", "Ворота", "Душ", "Баня"};

    private final static String[] SUBWAY_STATION_NAMES =
        {"Вологодская", "Кантимировская", "Ленина", "Безымянная"};

    private final static String[] PHOTOS = {
        "http://tdanyc.com/sites/files/styles/fullscreen/public/architecture/images/limassol" +
        "-sport-arena-interior.jpg?itok=_0ZMPxfV",
        "http://www.uclan.ac.uk/uclan_sports_arena/assets/images/DSCF3153_banner.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/Papp_L%C3%A1szl%C3" +
        "%B3_Budapest_Sportar%C3%A9na.jpg/1200px-Papp_L%C3%A1szl%C3%B3_Budapest_Sportar%C3%A9na" +
        ".jpg",
        "http://www.rubbuk.com/images/sports/projects/meadowbank/meadowbank3_large.jpg",
        "http://www.leedsbeckett.ac.uk/sport/-/media/images/sport-active-lifestyles/facilities" +
        "/sportshall.jpg?mw=768"};

    private final static String[] PHONE_NUMBERS = {
        "201(279)904-05-98",
        "586(2802)279-12-93",
        "0(5888)839-43-71",
        "675(6525)059-24-34",
        "20(082)112-59-34",
        "10(758)488-30-27",
        "89(294)865-01-54",
        "15(731)663-49-47",
        "056(751)377-49-29",
        "35(08)649-09-94"};

    private static final int PAGE_SIZE = 10;

    private static final String[] COVERING_NAMES = {"Трава", "Асфальт", "Грязь", "Мокрый бетон"};

    @NonNull
    @Override
    public Observable<OrderEntity> changeOrder(
        final long orderId,
        @NonNull final String accessToken,
        @NonNull final ChangeOrderParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<OrderEntity> changeOrder(
        final long orderId,
        @NonNull final String accessToken,
        @NonNull final ChangeOrderForUnknownUserParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<OrderEntity> changeOrderState(
        final long orderId,
        @NonNull final String accessToken,
        @NonNull final ChangeOrderStateParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<List<CoveringEntity>> getCoverings(
        @NonNull final String accessToken) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<List<AttributeEntity>> getAttributes(
        @NonNull final String accessToken) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<ResponseBody> changePlaygroundDimensions(
        final long playgroundId,
        @NonNull final String accessToken,
        @NonNull final ChangePlaygroundDimensionsParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<ResponseBody> changePlaygroundCovering(
        final long playgroundId,
        @NonNull final String accessToken,
        @NonNull final ChangePlaygroundCoveringParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<ResponseBody> changePlaygroundAttributes(
        final long playgroundId,
        @NonNull final String accessToken,
        @NonNull final ChangePlaygroundAttributesParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> changePlayerCity(
        @NonNull final String accessToken, @NonNull final ChangeUserCityParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> changePlayerEmail(
        @NonNull final String accessToken, @NonNull final ChangeUserEmailParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> changePlayerFirstName(
        @NonNull final String accessToken, @NonNull final ChangeUserFirstNameParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> changePlayerLastName(
        @NonNull final String accessToken, @NonNull final ChangeUserLastNameParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> changePlayerPhoneNumber(
        @NonNull final String accessToken, @NonNull final ChangeUserPhoneNumberParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> changePlayerAvatar(
        @NonNull final String accessToken, @NonNull final MultipartBody.Part avatar) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> changeManagerCity(
        @NonNull final String accessToken, @NonNull final ChangeUserCityParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> changeManagerEmail(
        @NonNull final String accessToken, @NonNull final ChangeUserEmailParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> changeManagerFirstName(
        @NonNull final String accessToken, @NonNull final ChangeUserFirstNameParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> changeManagerLastName(
        @NonNull final String accessToken, @NonNull final ChangeUserLastNameParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> changeManagerPhoneNumber(
        @NonNull final String accessToken, @NonNull final ChangeUserPhoneNumberParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> changeManagerAvatar(
        @NonNull final String accessToken, @NonNull final MultipartBody.Part avatar) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<Response<ResponseBody>> changePrices(
        final long playgroundId,
        @NonNull final List<ChangePriceParams> params,
        final String token) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<OrderEntity> createOrder(
        @NonNull final String accessToken, @NonNull final CreateOrderParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<OrderEntity> createOrder(
        @NonNull final String accessToken, @NonNull final CreateOrderForMeParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<OrderEntity> createOrder(
        @NonNull final String accessToken, @NonNull final CreateOrderForUnknownUserParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<SaleEntity> createSale(
        @NonNull final String accessToken, @NonNull final CreateSaleParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<Response<ResponseBody>> deleteSale(
        final long saleId, @NonNull final String accessToken) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<Response<ResponseBody>> favoriteSportComplex(
        final long sportComplexId, @NonNull final String accessToken) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<List<CityEntity>> getCities() {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<CityEntity> getCity(final long cityId) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<List<IntervalEntity>> getIntervals(
        final long playgroundId, @Nullable final String startDate) {
        return Observable.fromCallable(new Callable<List<IntervalEntity>>() {
            @Override
            public List<IntervalEntity> call()
                throws Exception {
                Contracts.requireWorkerThread();

                // Testing
                Thread.sleep(1000);

                return generateIntervals(new Random(Objects.hash(playgroundId, startDate)));
            }
        });
    }

    @NonNull
    @Override
    public Observable<UserEntity> getManager(@NonNull final String accessToken) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<PageEntity<OrderEntity>> getManagerOrders(
        @NonNull final String token,
        @Nullable final Integer page,
        @Nullable final Integer state,
        @Nullable final Long sportComplexId,
        @Nullable final Long playgroundId,
        @Nullable final String searchQuery,
        @Nullable final String ordering) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<PageEntity<SaleEntity>> getManagerSales(
        @NonNull final String token, @Nullable final Integer page) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<List<SportComplexDetailEntity>> getManagerSportComplexesDetails(
        final String token) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<PageEntity<SportComplexDetailEntity>> getManagerSportComplexesDetailsPages(
        final String token) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<List<SportComplexInfoEntity>> getManagerSportComplexesInfos(
        final String token) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<PageEntity<SportComplexInfoEntity>> getManagerSportComplexesInfosPages
        (final String token) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<List<SportComplexPreviewEntity>> getManagerSportComplexesPreviews(
        final String token) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<PageEntity<SportComplexPreviewEntity>> getManagerSportComplexesPreviewsPages(
        final String token) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<List<SportComplexReportEntity>> getManagerSportComplexesReports(
        final String token) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<PageEntity<SportComplexReportEntity>> getManagerSportComplexesReportsPages(
        final String token) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<List<SportComplexTitleEntity>> getManagerSportComplexesTitles(
        final String token) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<PageEntity<SportComplexTitleEntity>> getManagerSportComplexesTitlesPages(
        final String token) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<NotificationSubscribeEntity> getNotificationsSubscribe(
        @NonNull final String registrationId) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<OrderEntity> getOrder(final long orderId, @NonNull final String accessToken) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> getPlayer(@NonNull final String accessToken) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> getPlayer(
        final long playerId, @NonNull final String accessToken) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> getPlayer(
        final String playerPhone, @NonNull final String accessToken) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<PageEntity<OrderEntity>> getPlayerOrders(
        @NonNull final String token, @Nullable final Integer page, @Nullable final Integer state) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<PageEntity<UserEntity>> getPlayers(
        @NonNull final String accessToken,
        @Nullable final Integer page,
        @Nullable final String phoneNumber) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<List<PhotoEntity>> getPlaygroundPhotos(final long playgroundId) {
        return Observable.just(generatePhotos(new Random(playgroundId)));
    }

    @NonNull
    @Override
    public Observable<PlaygroundPreviewEntity> getPlaygroundPreview(
        final long playgroundId, @Nullable final String accessToken) {
        return Observable.just(generatePlaygroundPreview());
    }

    @NonNull
    @Override
    public Observable<PlaygroundTitleEntity> getPlaygroundTitle(
        final long playgroundId, @Nullable final String accessToken) {
        return Observable.just(generatePlaygroundTitle());
    }

    @NonNull
    @Override
    public Observable<List<PlaygroundPreviewEntity>> getPlaygroundsPreviews(
        final long sportComplexId,
        @Nullable final String time,
        @Nullable final String date,
        @Nullable final String accessToken) {
        return Observable.just(generatePlaygroundPreviews());
    }

    @NonNull
    @Override
    public Observable<List<PlaygroundTitleEntity>> getPlaygroundsTitles(
        final long sportComplexId,
        @Nullable final String time,
        @Nullable final String date,
        @Nullable final String accessToken) {
        return Observable.just(generatePlaygroundTitles());
    }

    @NonNull
    @Override
    public Observable<List<PlaygroundTitleEntity>> getPlaygroundsTitles(
        final long sportComplexId, @Nullable final String accessToken) {
        return Observable.just(generatePlaygroundTitles());
    }

    @NonNull
    @Override
    public Observable<List<PriceEntity>> getPrices(
        final long playgroundId, @NonNull final String accessToken) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<SportComplexDetailEntity> getSportComplexDetail(
        final long sportComplexId, @Nullable final String accessToken) {
        return Observable.just(generateSportComplexDetail());
    }

    @NonNull
    @Override
    public Observable<SportComplexInfoEntity> getSportComplexInfo(
        final long sportComplexId, @Nullable final String accessToken) {
        return Observable.just(generateSportComplexInfo());
    }

    @NonNull
    @Override
    public Observable<SportComplexLocationEntity> getSportComplexLocation(
        final long sportComplexId, @Nullable final String accessToken) {
        return Observable.just(generateSportComplexLocation());
    }

    @NonNull
    @Override
    public Observable<List<PhotoEntity>> getSportComplexPhotos(final long sportComplexId) {
        return Observable.just(generatePhotos(new Random(sportComplexId)));
    }

    @NonNull
    @Override
    public Observable<SportComplexPreviewEntity> getSportComplexPreview(
        final long sportComplexId, @Nullable final String accessToken) {
        return Observable.just(generateSportComplexPreview());
    }

    @NonNull
    @Override
    public Observable<SportComplexTitleEntity> getSportComplexTitle(
        final long sportComplexId, @Nullable final String accessToken) {
        return Observable.just(generateSportComplexTitle());
    }

    @NonNull
    @Override
    public Observable<List<SportComplexDetailEntity>> getSportComplexesDetails(
        @Nullable final Integer page,
        @Nullable final String sportComplexName,
        @Nullable final String startTime,
        @Nullable final String endTime,
        @Nullable final String date,
        @Nullable final String location,
        @Nullable final String ordering,
        @Nullable final Boolean favorites,
        @Nullable final String accessToken) {
        return Observable.just(generateSportComplexDetails());
    }

    @NonNull
    @Override
    public Observable<PageEntity<SportComplexDetailEntity>> getSportComplexesDetailsPages(
        @Nullable final Integer page,
        @Nullable final String sportComplexName,
        @Nullable final String startTime,
        @Nullable final String endTime,
        @Nullable final String date,
        @Nullable final String location,
        @Nullable final String ordering,
        @Nullable final Boolean favorites,
        @Nullable final String accessToken) {
        return Observable.just(generatePage(generateSportComplexDetails()));
    }

    @NonNull
    @Override
    public Observable<List<SportComplexInfoEntity>> getSportComplexesInfos(
        @Nullable final Integer page,
        @Nullable final String sportComplexName,
        @Nullable final String startTime,
        @Nullable final String endTime,
        @Nullable final String date,
        @Nullable final String location,
        @Nullable final String ordering,
        @Nullable final Boolean favorites,
        @Nullable final String accessToken) {
        return Observable.just(generateSportComplexInfos());
    }

    @NonNull
    @Override
    public Observable<PageEntity<SportComplexInfoEntity>> getSportComplexesInfosPages(
        @Nullable final Integer page,
        @Nullable final String sportComplexName,
        @Nullable final String startTime,
        @Nullable final String endTime,
        @Nullable final String date,
        @Nullable final String location,
        @Nullable final String ordering,
        @Nullable final Boolean favorites,
        @Nullable final String accessToken) {
        return Observable.just(generatePage(generateSportComplexInfos()));
    }

    @NonNull
    @Override
    public Observable<List<SportComplexLocationEntity>> getSportComplexesLocations(
        @Nullable final Integer page,
        @Nullable final String sportComplexName,
        @Nullable final String startTime,
        @Nullable final String endTime,
        @Nullable final String date,
        @Nullable final String location,
        @Nullable final String ordering,
        @Nullable final Boolean favorites,
        @Nullable final String accessToken) {
        return Observable.just(generateSportComplexLocations());
    }

    @NonNull
    @Override
    public Observable<PageEntity<SportComplexLocationEntity>> getSportComplexesLocationsPages(
        @Nullable final Integer page,
        @Nullable final String sportComplexName,
        @Nullable final String startTime,
        @Nullable final String endTime,
        @Nullable final String date,
        @Nullable final String location,
        @Nullable final String ordering,
        @Nullable final Boolean favorites,
        @Nullable final String accessToken) {
        return Observable.just(generatePage(generateSportComplexLocations()));
    }

    @NonNull
    @Override
    public Observable<List<SportComplexPreviewEntity>> getSportComplexesPreviews(
        @Nullable final Integer page,
        @Nullable final String sportComplexName,
        @Nullable final String startTime,
        @Nullable final String endTime,
        @Nullable final String date,
        @Nullable final String location,
        @Nullable final String ordering,
        @Nullable final Boolean favorites,
        @Nullable final String accessToken) {
        return Observable.just(generateSportComplexPreviews());
    }

    @NonNull
    @Override
    public Observable<PageEntity<SportComplexPreviewEntity>> getSportComplexesPreviewsPages(
        @Nullable final Integer page,
        @Nullable final String sportComplexName,
        @Nullable final String startTime,
        @Nullable final String endTime,
        @Nullable final String date,
        @Nullable final String location,
        @Nullable final String ordering,
        @Nullable final Boolean favorites,
        @Nullable final String accessToken) {
        return Observable.just(generatePage(generateSportComplexPreviews()));
    }

    @NonNull
    @Override
    public Observable<List<SportComplexTitleEntity>> getSportComplexesTitles(
        @Nullable final Integer page,
        @Nullable final String sportComplexName,
        @Nullable final String startTime,
        @Nullable final String endTime,
        @Nullable final String date,
        @Nullable final String location,
        @Nullable final String ordering,
        @Nullable final Boolean favorites,
        @Nullable final String accessToken) {
        return Observable.just(generateSportComplexTitles());
    }

    @NonNull
    @Override
    public Observable<PageEntity<SportComplexTitleEntity>> getSportComplexesTitlesPages(
        @Nullable final Integer page,
        @Nullable final String sportComplexName,
        @Nullable final String startTime,
        @Nullable final String endTime,
        @Nullable final String date,
        @Nullable final String location,
        @Nullable final String ordering,
        @Nullable final Boolean favorites,
        @Nullable final String accessToken) {
        return Observable.just(generatePage(generateSportComplexTitles()));
    }

    @NonNull
    @Override
    public Observable<List<SubwayStationEntity>> getSubwayStations(
        final long cityId) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<AuthorizationResponse> logInUser(
        @NonNull final String clientId,
        @NonNull final String clientSecret,
        @NonNull final String username,
        @NonNull final String password) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<AuthorizationResponse> refreshToken(
        @NonNull final String clientId,
        @NonNull final String clientSecret,
        @NonNull final String refreshToken) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> registerPlayer(final UserRegisterParams userRegisterParams) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<NotificationSubscribeEntity> subscribeNotifications(
        @Nullable final String accessToken, @NonNull final NotificationsSubscribeParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<Response<ResponseBody>> unfavoriteSportComplex(
        final long playgroundId, @NonNull final String accessToken) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<ResponseBody> unsubscribeNotifications(@NonNull final String registrationId) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<NotificationSubscribeEntity> updateNotificationsSubscribe(
        @NonNull final String registrationId,
        @Nullable final String accessToken,
        @NonNull final ChangeActiveNotificationsSubscribeParams params) {
        return unsupportedInDebugError();
    }

    @NonNull
    @Override
    public Observable<UserEntity> updateUserAdditionalInfo(
        @NonNull final String accessToken, @NonNull final UpdateUserAdditionalInfoParams params) {
        return unsupportedInDebugError();
    }

    private long _lastId = 0;

    @Nullable
    private String generateAddress(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return RandomUtils.randomItem(random, ADDRESSES);
    }

    @NotNull
    private AttributeEntity generateAttribute(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        final val result = new AttributeEntity();

        result.setId(generateId());
        result.setName(generateAttributeName(random));
        result.setIcon("iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAQAAAD" +
                       "/5HvMAAAA6UlEQVR4AezUsQ2CYBBA4RcKCq3oaKnpdQAmYAF" +
                       "dgAV0ARbQBViACRiAAWix1I6KQhNyJnYmxhx68Bf6vh0ev9e/JXtadix" +
                       "wHz4ZF+ThTIaPwzy2nJAnLRs8nJTSIC81pMxcQo28VZMwU2sqRKVixcTFlMgo" +
                       "JTETFVEwIKMNFEQYF3LkhnzsyoEQowJyeuRrPTmBxYM7xEyn+LniwVYUP1c82J7i" +
                       "54oH22tI1Q+ejeLncm/HDg0AgEEgiO0/dTsACsEjyL1FRJMeENA5kKRkr2zuBgg" +
                       "ICAioHxAQEBAQ0H6SvPSAgICAJCX6FxsyzVkR6wIAAAAASUVORK5CYII=");

        return result;
    }

    @Nullable
    private String generateAttributeName(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return RandomUtils.randomItem(random, ATTRIBUTE_NAMES);
    }

    @NonNull
    private List<AttributeEntity> generateAttributes(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        final val count = RandomUtils.nextInt(random, 3, 7);

        final val result = new ArrayList<AttributeEntity>(count);
        for (int i = 0; i < count; i++) {
            result.add(generateAttribute(random));
        }

        return result;
    }

    @NotNull
    private CoveringEntity generateCovering(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        final val result = new CoveringEntity();

        result.setId(generateId());
        result.setName(generateCoveringName(random));

        return result;
    }

    @Nullable
    private String generateCoveringName(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return RandomUtils.randomItem(random, COVERING_NAMES);
    }

    @NotNull
    private String generateDescription(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
               "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis " +
               "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
               "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
               "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in " +
               "culpa qui officia deserunt mollit anim id est laborum.";
    }

    private long generateDistance(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return RandomUtils.nextInt(random, 400, 150000);
    }

    private long generateId() {
        return ++_lastId;
    }

    @NotNull
    private IntervalEntity generateInterval(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        final val result = new IntervalEntity();

        result.setId("fake_slug");
        result.setIndex(1);
        result.setOrderId(random.nextBoolean() ? null : (long) RandomUtils.nextInt(random, 1, 7));
        result.setSale(random.nextInt(10) > 7);
        result.setPrice(RandomUtils.nextInt(random, 100, 9000));
        result.setStateCode((result.getOrderId() == null
                             ? IntervalState.BUSY
                             : IntervalState.FREE).getCode());

        return result;
    }

    @NonNull
    private List<IntervalEntity> generateIntervals(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        final val count = ProSportDataContract.DAY_IN_INTERVALS * 7;

        final val result = new ArrayList<IntervalEntity>(count);
        for (int i = 0; i < count; i++) {
            result.add(generateInterval(random));
        }

        return result;
    }

    private boolean generateInventoryChargeableState(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return random.nextBoolean();
    }

    @Nullable
    private String generateInventoryDescription(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return RandomUtils.randomItem(random, INVENTORY_DESCRIPTIONS);
    }

    private double generateLocationLatitude(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return RandomUtils.nextInt(random, 30, 60);
    }

    private double generateLocationLongitude(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return RandomUtils.nextInt(random, 25, 40);
    }

    private int generateMinimumPrice(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return RandomUtils.nextInt(random, 400, 15000);
    }

    @NotNull
    private <T> PageEntity<T> generatePage(@NotNull final List<T> entities) {
        Contracts.requireNonNull(entities, "entities == null");

        final val result = new PageEntity<T>();

        result.setEntries(entities);
        result.setEntriesCount(entities.size());
        result.setNextPageUri("fake_next");
        result.setPreviousPageUri("fake_prev");

        return result;
    }

    @Nullable
    private String generatePhoneNumber(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return RandomUtils.randomItem(random, PHONE_NUMBERS);
    }

    @NonNull
    private List<String> generatePhoneNumbers(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        final val count = RandomUtils.nextInt(random, 1, 4);

        final val result = new ArrayList<String>(count);
        for (int i = 0; i < count; i++) {
            result.add(generatePhoneNumber(random));
        }

        return result;
    }

    @NotNull
    private PhotoEntity generatePhoto(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        final val result = new PhotoEntity();

        result.setId(generateId());
        result.setPosition((int) result.getId());
        result.setUri(generatePhotoUri(random));

        return result;
    }

    @Nullable
    private String generatePhotoUri(@NotNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return RandomUtils.randomItem(random, PHOTOS);
    }

    @NonNull
    private List<PhotoEntity> generatePhotos(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        final val count = RandomUtils.nextInt(random, 5, 10);

        final val result = new ArrayList<PhotoEntity>(count);
        for (int i = 0; i < count; i++) {
            result.add(generatePhoto(random));
        }

        return result;
    }

    @Nullable
    private String generatePlaygroundName(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return RandomUtils.randomItem(random, PLAYGROUND_NAMES);
    }

    @NotNull
    private PlaygroundPreviewEntity generatePlaygroundPreview() {
        final val result = new PlaygroundPreviewEntity();

        final val id = generateId();

        final val random = new Random(id);

        result.setId(id);
        result.setAttributes(generateAttributes(random));
        result.setCovering(generateCovering(random));
        result.setHeight(RandomUtils.nextInt(random, 5, 20));
        result.setLength(RandomUtils.nextInt(random, 5, 20));
        result.setWidth(RandomUtils.nextInt(random, 5, 20));
        result.setPartLength(result.getLength() / 2);
        result.setPartWidth(result.getWidth() / 2);
        result.setName(generatePlaygroundName(random));
        result.setPhoto(generatePhoto(random));
        result.setSportComplexId(random.nextLong());

        return result;
    }

    @NonNull
    private List<PlaygroundPreviewEntity> generatePlaygroundPreviews() {
        final val result = new ArrayList<PlaygroundPreviewEntity>(PAGE_SIZE);

        for (int i = 0; i < PAGE_SIZE; i++) {
            result.add(generatePlaygroundPreview());
        }

        return result;
    }

    @NotNull
    private PlaygroundTitleEntity generatePlaygroundTitle() {
        final val result = new PlaygroundTitleEntity();

        final val id = generateId();

        final val random = new Random(id);

        result.setId(id);
        result.setName(generatePlaygroundName(random));
        result.setSportComplexId(random.nextLong());

        return result;
    }

    @NonNull
    private List<PlaygroundTitleEntity> generatePlaygroundTitles() {
        final val result = new ArrayList<PlaygroundTitleEntity>(PAGE_SIZE);

        for (int i = 0; i < PAGE_SIZE; i++) {
            result.add(generatePlaygroundTitle());
        }

        return result;
    }

    @NotNull
    private SportComplexDetailEntity generateSportComplexDetail() {
        final val result = new SportComplexDetailEntity();

        final val id = generateId();

        final val random = new Random(id);

        result.setId(id);
        result.setAddress(generateAddress(random));
        result.setDescription(generateDescription(random));
        result.setInventoryDescription(generateInventoryDescription(random));
        result.setInventoryChargeableState(generateInventoryChargeableState(random));
        result.setAttributes(generateAttributes(random));
        result.setName(generateSportComplexName(random));
        result.setPhoneNumbers(generatePhoneNumbers(random));
        result.setPhotos(generatePhotos(random));
        result.setSubwayStations(generateSubwayStations(random));
        result.setWorkPeriods(generateWorkPeriods(random));

        return result;
    }

    @NonNull
    private List<SportComplexDetailEntity> generateSportComplexDetails() {
        final val result = new ArrayList<SportComplexDetailEntity>(PAGE_SIZE);

        for (int i = 0; i < PAGE_SIZE; i++) {
            result.add(generateSportComplexDetail());
        }

        return result;
    }

    @NotNull
    private SportComplexInfoEntity generateSportComplexInfo() {
        final val result = new SportComplexInfoEntity();

        final val id = generateId();

        final val random = new Random(id);

        result.setId(id);
        result.setAddress(generateAddress(random));
        result.setName(generateSportComplexName(random));
        result.setPhoneNumbers(generatePhoneNumbers(random));
        result.setPhotos(generatePhotos(random));
        result.setSubwayStations(generateSubwayStations(random));
        result.setWorkPeriods(generateWorkPeriods(random));

        return result;
    }

    @NonNull
    private List<SportComplexInfoEntity> generateSportComplexInfos() {
        final val result = new ArrayList<SportComplexInfoEntity>(PAGE_SIZE);

        for (int i = 0; i < PAGE_SIZE; i++) {
            result.add(generateSportComplexInfo());
        }

        return result;
    }

    @NotNull
    private SportComplexLocationEntity generateSportComplexLocation() {
        final val result = new SportComplexLocationEntity();

        final val id = generateId();

        final val random = new Random(id);

        result.setId(id);
        result.setLocationLatitude(generateLocationLatitude(random));
        result.setLocationLongitude(generateLocationLongitude(random));

        return result;
    }

    @NonNull
    private List<SportComplexLocationEntity> generateSportComplexLocations() {
        final val result = new ArrayList<SportComplexLocationEntity>(PAGE_SIZE);

        for (int i = 0; i < PAGE_SIZE; i++) {
            result.add(generateSportComplexLocation());
        }

        return result;
    }

    @Nullable
    private String generateSportComplexName(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return RandomUtils.randomItem(random, SPORT_COMPLEX_NAMES);
    }

    @NotNull
    private SportComplexPreviewEntity generateSportComplexPreview() {
        final val result = new SportComplexPreviewEntity();

        final val id = generateId();

        final val random = new Random(id);

        result.setId(id);
        result.setDistance(generateDistance(random));
        result.setMinimumPrice(generateMinimumPrice(random));
        result.setName(generateSportComplexName(random));
        result.setPhoto(generatePhoto(random));
        result.setSubwayStations(generateSubwayStations(random));

        return result;
    }

    @NonNull
    private List<SportComplexPreviewEntity> generateSportComplexPreviews() {
        final val result = new ArrayList<SportComplexPreviewEntity>(PAGE_SIZE);

        for (int i = 0; i < PAGE_SIZE; i++) {
            result.add(generateSportComplexPreview());
        }

        return result;
    }

    @NotNull
    private SportComplexTitleEntity generateSportComplexTitle() {
        final val result = new SportComplexTitleEntity();

        final val id = generateId();

        final val random = new Random(id);

        result.setId(id);
        result.setName(generateSportComplexName(random));
        result.setPhoneNumbers(generatePhoneNumbers(random));

        return result;
    }

    @NonNull
    private List<SportComplexTitleEntity> generateSportComplexTitles() {
        final val result = new ArrayList<SportComplexTitleEntity>(PAGE_SIZE);

        for (int i = 0; i < PAGE_SIZE; i++) {
            result.add(generateSportComplexTitle());
        }

        return result;
    }

    @NotNull
    private SubwayStationEntity generateSubwayStation(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        final val result = new SubwayStationEntity();

        result.setId(generateId());
        result.setName(generateSubwayStationName(random));

        return result;
    }

    @Nullable
    private String generateSubwayStationName(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        return RandomUtils.randomItem(random, SUBWAY_STATION_NAMES);
    }

    @NonNull
    private List<SubwayStationEntity> generateSubwayStations(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        final val count = RandomUtils.nextInt(random, 1, 4);

        final val result = new ArrayList<SubwayStationEntity>(count);
        for (int i = 0; i < count; i++) {
            result.add(generateSubwayStation(random));
        }

        return result;
    }

    @NotNull
    private List<WorkPeriodEntity> generateWorkPeriods(@NonNull final Random random) {
        Contracts.requireNonNull(random, "random == null");

        final val result = new ArrayList<WorkPeriodEntity>();

        for (final val weekDay : WeekDay.values()) {
            final val workPeriod = new WorkPeriodEntity();

            workPeriod.setWeekDay(weekDay.getId());
            workPeriod.setStart(String.format(
                Locale.US,
                "%1$d:%2$d",
                RandomUtils.nextInt(random, 7, 12),
                random.nextBoolean() ? 0 : 30));
            workPeriod.setEnd(String.format(
                Locale.US,
                "%1$d:%2$d",
                RandomUtils.nextInt(random, 17, 22),
                random.nextBoolean() ? 0 : 30));

            result.add(workPeriod);
        }

        return result;
    }

    @NotNull
    private <T> Observable<T> unsupportedInDebugError() {
        return Observable.error(new UnsupportedOperationException(
            "Unsupported operation in debug version"));
    }
}
