
package com.masterpradipg.pradip.sunshine.app.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.masterpradipg.pradip.sunshine.app.data.WeatherContract.LocationEntry;
import com.masterpradipg.pradip.sunshine.app.data.WeatherContract.WeatherEntry;
import com.masterpradipg.pradip.sunshine.app.data.WeatherDbHelper;

import java.util.Map;
import java.util.Set;


/**
 * Created by Pradip on 5/24/2015.
 */


public class TestDb extends AndroidTestCase {          //video 29l4 4.08

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    static public String TEST_CITY_NAME = "North Pole";

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


    public void testInsertReadDb() {


        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = getLocationContentValues();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);


        // A cursor is your primary interface to the query results.             custom projection
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        if (cursor.moveToFirst()) {
            validateCursor(values, cursor);

ContentValues weatherValues = getWeatherContentValues(locationRowId);
            long weatherRowId;
             weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
            assertTrue(weatherRowId != -1);

            // A cursor is your primary interface to the query results.
            Cursor weatherCursor = db.query(
                    WeatherEntry.TABLE_NAME,  // Table to Query
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null, // columns to group by
                    null, // columns to filter by row groups
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


}




