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

    ///url get data

    public static final String DOMAIN_STOUR = "http://stour.sanslab.vn/";

    //get all category
    public static final String GET_ALL_CATEGORY = DOMAIN_STOUR + "api/categorylocation/GetAll";

    //get place by cate id
    public static final String GET_PLACE_BY_CATE = DOMAIN_STOUR + "api/location/GetByCategory";

    //get place by cate id
    public static final String GET_PLACE_DETAIL = DOMAIN_STOUR + "api/location/GetDetailLocation";

    //get banner for main screen
    public static final String GET_BANNER = DOMAIN_STOUR + "api/banner/GetBanners";

    //get info for history
    public static final String GET_HIS_PLACE = DOMAIN_STOUR + "api/location/GetByCategory?categoryId=3";

    //get info for culture place
    public static final String GET_CULTURE_PLACE = DOMAIN_STOUR + "api/location/GetByCategory?categoryId=4";

    //get info for food place
    public static final String GET_FOOD_PLACE = DOMAIN_STOUR + "api/food/GetAll";

    //get info for food detail
    public static final String GET_FOOD_DETAIL = DOMAIN_STOUR + "api/food/GetDetail";

    //get all hotel
    public static final String GET_ALL_HOTEL = DOMAIN_STOUR + "api/hotel/GetAll";

    //get hotel detail
    public static final String GET_HOTEL_DETAIL = DOMAIN_STOUR + "api/hotel/GetHotelDetail";

    //get all hotel
    public static final String GET_ALL_RESTAURANT = DOMAIN_STOUR + "api/restaurant/GetAll";

    //get hotel detail
    public static final String GET_RESTAURANT_DETAIL = DOMAIN_STOUR + "api/restaurant/GetRestaurantDetail";

    //get pois for map
    public static final String GET_POIS = DOMAIN_STOUR + "api/poi/GetPOIs";


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


}
