
package com.masterpradipg.pradip.sunshine.app.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.masterpradipg.pradip.sunshine.app.data.WeatherContract.LocationEntry;
import com.masterpradipg.pradip.sunshine.app.data.WeatherContract.WeatherEntry;

import java.util.Map;
import java.util.Set;


/**
 * Created by Pradip on 5/24/2015.
 */


public class TestProvider extends AndroidTestCase {          //video 29l4 4.08

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    // brings our database to an empty state
    public void testDeleteAllRecords() {
        mContext.getContentResolver().delete(
                WeatherEntry.CONTENT_URI,         //1st
                null,
                null
        );
        mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,                //2nd
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals( cursor.getCount(), 0);
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals( cursor.getCount(), 0);
        cursor.close();
    }

    static public String TEST_CITY_NAME = "North Pole";
    static public String TEST_LOCATION = "99705";
    static public String TEST_DATE = "20141205";


    static public ContentValues getLocationContentValues() {
        ContentValues values = new ContentValues();
        String testLocationSetting = "99705";
        double testLatitude = 64.7488;
        double testLongitude = -147.353;

        values.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);
        return values;
    }

    static public ContentValues getWeatherContentValues (long locationRowId){
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);
        return weatherValues;

    }


    static public void validateCursor(ContentValues expectedValues, Cursor valueCursor ) {

      //  assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(-1 == idx);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }

    }

    public void testGetType() {
        // content://com.masterpradipg.pradip.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.masterpradipg.pradip.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        // content://com.masterpradipg.pradip.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocation(testLocation));
        // vnd.android.cursor.dir/com.masterpradipg.pradip.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140612";
        // content://com.masterpradipg.pradip.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        // vnd.android.cursor.item/com.masterpradipg.pradip.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.masterpradipg.pradip.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        // content://com.masterpradipg.pradip.sunshine.app/location/1
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.masterpradipg.pradip.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }







    public void testInsertReadProvider () {
        // Create a new map of values, where column names are the keys
        ContentValues values = getLocationContentValues();

        //long locationRowId;
      Uri   locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);
        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);


        // A cursor is your primary interface to the query results.             custom projection
        Cursor cursor = mContext.getContentResolver().query(LocationEntry.buildLocationUri(locationRowId),  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

        if (cursor.moveToFirst()) {
            validateCursor(values, cursor);
        }
        cursor = mContext.getContentResolver().query(LocationEntry.buildLocationUri(locationRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        if (cursor.moveToFirst()) {
            validateCursor(values, cursor);

            ContentValues weatherValues = getWeatherContentValues(locationRowId);
          //  long weatherRowId;
            Uri insertUri = mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);
           // long weatherRowId = ContentUris.parseId(insertUri);
            //assertTrue(weatherRowId != -1);


            Cursor weatherCursor = mContext.getContentResolver().query(WeatherEntry.CONTENT_URI,
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // sort order
            );
         //      weatherCursor.close();

            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherCursor);

            }else {
                fail("No weather data returned!");
            }
            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().query(
                    WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE),
                    // A cursor is your primary interface to the query results.
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // sort order
            );
            if (weatherCursor.moveToFirst()) {
               validateCursor(weatherValues, weatherCursor);

            }else{
                fail("No weather data returned!");
            }

        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }
    }



    public void testUpdateLocation() {
        testDeleteAllRecords();
        // Create a new map of values, where column names are the keys
        ContentValues values = getLocationContentValues();

        Uri locationUri = mContext.getContentResolver().
                insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues values2 = new ContentValues(values);
        values2.put(LocationEntry._ID, locationRowId);
        values2.put(LocationEntry.COLUMN_CITY_NAME, "Santa's Village");

        int count = mContext.getContentResolver().update(LocationEntry.CONTENT_URI, values2,
                LocationEntry._ID + "= ?",new String[] { Long.toString(locationRowId)});

        assertEquals(count, 1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
         if (cursor.moveToFirst()){
        validateCursor(values2, cursor);
    }
        cursor.close();
    }

}




