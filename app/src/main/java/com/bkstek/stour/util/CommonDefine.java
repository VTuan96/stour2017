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

}
