package com.bkstek.stour.util;

/**
 * Created by thold on 8/1/2017.
 */

public interface CommonDefine {


    //name of bottom tab
    public static final String HOME = "Home";
    public static final String PLACEHOLD = "PlaceHold";
    public static final String INFO = "Info";
    public static final String ACCOUNT = "Account";


    ///
    public static final String CURRENT_LOCATION = "current";
    public static final String TWO_POINT_RANDOM = "random_point";
    public static final String MULTI_DIRECTION = "multi_direction";


    /// mode of google map
    public static final String MODE_DRIVING = "driving";
    public static final String MODE_BICYCLING = "bicycle";
    public static final String MODE_WALKING = "walking";
    public static final String MODE_TRAVEL = "MODE_TRAVEL";

    ///url get data

    public static final String DOMAIN_STOUR = "https://stour.sanslab.vn/";

    //get all category
//    public static final String GET_ALL_CATEGORY = DOMAIN_STOUR + "api/categorylocation/GetAll";
    public static final String GET_ALL_CATEGORY = DOMAIN_STOUR + "mobile/categorylocation/getall";

    //get place by cate id
//    public static final String GET_PLACE_BY_CATE = DOMAIN_STOUR + "api/location/GetByCategory";
    public static final String GET_PLACE_BY_CATE = DOMAIN_STOUR + "mobile/location/getbycategory";

    //get place by cate id
//    public static final String GET_PLACE_DETAIL = DOMAIN_STOUR + "api/location/GetDetailLocation";
    public static final String GET_PLACE_DETAIL = DOMAIN_STOUR + "mobile/location/detail";

    //get banner for main screen
//    public static final String GET_BANNER = DOMAIN_STOUR + "api/banner/GetBanners";
    public static final String GET_BANNER = DOMAIN_STOUR + "mobile/banner/getall";

    //get info for history
//    public static final String GET_HIS_PLACE = DOMAIN_STOUR + "api/location/GetByCategory?categoryId=3";
    public static final String GET_HIS_PLACE = GET_PLACE_BY_CATE + "?categoryId=1";

    //get info for culture place
//    public static final String GET_CULTURE_PLACE = DOMAIN_STOUR + "api/location/GetByCategory?categoryId=4";
    public static final String GET_CULTURE_PLACE = DOMAIN_STOUR + "mobile/cultural/getall";

    public static final String GET_CULTURE_DETAIL = DOMAIN_STOUR + "mobile/cultural/detail";

    //get info for food place
//    public static final String GET_FOOD_PLACE = DOMAIN_STOUR + "api/food/GetAll";
    public static final String GET_FOOD_PLACE = DOMAIN_STOUR + "mobile/food/getall";

    //get info for food detail
//    public static final String GET_FOOD_DETAIL = DOMAIN_STOUR + "api/food/GetDetail";
    public static final String GET_FOOD_DETAIL = DOMAIN_STOUR + "mobile/food/detail";

    //get all hotel
//    public static final String GET_ALL_HOTEL = DOMAIN_STOUR + "api/hotel/GetAll";
    public static final String GET_ALL_HOTEL = DOMAIN_STOUR + "mobile/hotel/getall";

    //get hotel detail
//    public static final String GET_HOTEL_DETAIL = DOMAIN_STOUR + "api/hotel/GetHotelDetail";
    public static final String GET_HOTEL_DETAIL = DOMAIN_STOUR + "mobile/hotel/detail";

    //get all hotel
//    public static final String GET_ALL_RESTAURANT = DOMAIN_STOUR + "api/restaurant/GetAll";
    public static final String GET_ALL_RESTAURANT = DOMAIN_STOUR + "mobile/restaurant/getall";

    //get hotel detail
//    public static final String GET_RESTAURANT_DETAIL = DOMAIN_STOUR + "api/restaurant/GetRestaurantDetail";
    public static final String GET_RESTAURANT_DETAIL = DOMAIN_STOUR + "mobile/restaurant/detail";

    //get pois for map
    public static final String GET_POIS = DOMAIN_STOUR + "mobile/poi/getall";


    public static final String GEOSERVER_FORMAT =
            "http://sanslab.vn:5588/geoserver/KVBK/wms?" +
                    "service=WMS&version=1.1.1" +
                    "&request=GetMap" +
                    "&layers=KVBK:road_split" +
                    "&bbox=%f,%f,%f,%f" +
                    "&width=768&height=427" +
                    "&srs=EPSG:900913&&format=image/png&transparent=true";


    public static final String WMS_FORMAT_ROUTE_STRING =
            "http://sanslab.vn:5588/geoserver/KVBK/wms?" +
                    "LAYERS=KVBK:route&FORMAT=image/png" +
                    "&TRANSPARENT=TRUE" +
                    "&VIEWPARAMS";

    public static final String GOOGLEMAP_DIRECTION = "https://maps.googleapis.com/maps/api/directions/";

    public static final String KEY_YOUTUBE = "AIzaSyB2W7h5EkA1-4Y0jQRcqMK6vvtqsJm8NNg";


    /*
     ----------------       API Open Weather ---------------------
     */
    public static final String URL_OPEN_WEATHER = "http://api.openweathermap.org/data/2.5/weather?";
//    public static final String URL_OPEN_WEATHER = "http://api.openweathermap.org/data/2.5/forecast?";
    public static final String API_KEY_OPEN_WEATHER = "4d2a8ab0ce16ba243d26a225856a7f92";


    public static final String VIDEO_DIR = "VIDEO_DIR";
    public static final String TITLE = "TITLE";
    public static final String TAG = "TAG";
    public static final String FUNC = "FUNC";
    public static final String ROUTING = "ROUTING";
    public static final String SMART = "SMART";

    public static final String SETTING = "SETTING";
    public static final String TIME_UPDATE_LOCATION = "TIME_UPDATE_LOCATION";
    public static final String RADIUS_ACCESS = "RADIUS_ACCESS";

    public static final String SELECTION_SEARCH = "SELECTION_SEARCH";
    public static final String SELECTION_ULTILITIES = "SELECTION_ULTILITIES";
    public static final String ATM = "ATM";
    public static final String BANK = "BANK";
    public static final String MART = "MART";
    public static final String LBS = "LBS";
}
