package com.reindeermobile.reindeerutils.mvp;


public class MessageConstans {
	public static final int M_LOCATION_UPDATE = 4;
	public static final int M_REFRESH_PLACES = 5;
	public static final int V_GET_PLACES = 7;

	/**
	 * A {@link GeoModel} kapja meg, és válaszként küldi a közeli helyek
	 * listáját.
	 */
	public static final int M_UPDATE_PLACES = 8;

	/**
	 * A {@link GeoModel} küldi el a kijelölt pont adatait tartalmazó
	 * {@link Place} példányt.
	 */
	public static final int M_SEND_SELECTED_PLACE = 9;

	// public static final int V_GET_MAP_STATUS = 10;

	public static final int V_REQUEST_MAP_STATUS = 12;

	public static final int M_REFRESH_BALLON_ICON = 13;
	public static final int V_ENABLE_UPDATE_LOCATION = 15;
	public static final int M_SEND_MY_GEOPOINT = 16;

	public static final int M_REFRESH_FAVOURITES = 18;
	public static final int M_SAVE_PLACE = 20;
	public static final int M_REMOVE_PLACE = 22;
	public static final int M_RESPONSE_FAVOURITE_ITEM = 24;
	public static final int M_STREET_LIST = 28;
	public static final int M_PROVIDER_DISABLED = 30;
	public static final int M_PROVIDER_ENABLED = 31;

	/**
	 * Válasz erre: {@link DbModel#V_LIST_CITIES}<br>
	 * Paraméterként egy {@link ListWrapper}-be csomagolt {@link String} lista.
	 */
	public static final int M_LIST_CITIES = 33;

	/**
	 * Ezzel lehet lekérdezni, hogy az adatbázis készen áll-e. Induláskor az
	 * első activity kéri ki.
	 */
	// TODO Melyik??
	public static final int V_GET_DATABASE_STATUS = 34;

	/**
	 * A {@link MessageConstans#V_GET_DATABASE_STATUS}-ra küldött válasz abban
	 * az esetben, ha nincs felhasználó az adatbázisban.
	 */
	public static final int M_DATABASE_OK_WITHOUT_USER = 35;

	/**
	 * A {@link MessageConstans#V_GET_DATABASE_STATUS}-ra küldött válasz abban
	 * az esetben, ha nincs alapértelmezett felhasználó.
	 */
	public static final int M_DATABASE_OK_USER_LIST = 36;

	/**
	 * A {@link MessageConstans#V_GET_DATABASE_STATUS}-ra küldött válasz abban
	 * az esetben ha be van állítva alapértelmezett felhasználó.
	 */
	public static final int M_DATABASE_OK_DEF_USER = 37;

	/**
	 * A {@link DbModel} válasta a {@link DbModel#V_SAVE_USER} kérésre.
	 */
	public static final int M_SAVE_USER = 39;

	public static final int M_SEND_USER_LIST = 41;

	public static final int M_SEND_DEFAULT_USER = 44;

}
