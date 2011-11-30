package com.reindeermobile.example.test;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import com.reindeermobile.example.providers.UserProvider;
import com.reindeermobile.example.providers.User.Users;

public class UserProviderTest extends ProviderTestCase2<UserProvider> {

	public UserProviderTest() {
		super(UserProvider.class, UserProvider.class.getName());
	}

	protected void setUp() throws Exception {
        super.setUp();
    }
	
	public void testFailQuery(){
        ContentProvider provider = getProvider();

        Uri uri = UserProvider.CONTENT_URI;

        Cursor cursor = provider.query(uri, null, null, null, null);

        assertNotNull(cursor);

        cursor = null;
        try {
            cursor = provider.query(Uri.parse("definitelywrong"), null, null, null, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
	
	public void testQuery(){
        ContentProvider provider = getProvider();

        Uri uri = UserProvider.CONTENT_URI;

        Cursor cursor = provider.query(uri, null, null, null, null);

        assertNotNull(cursor);
        
        String email = "";

        cursor = null;
        try {
        	ContentValues values = new ContentValues();
    		values.put(Users.EMAIL, "example@mail.hu");
    		values.put(Users.NAME, "example name");
    		Uri insertUri = provider.insert(Uri.parse("content://" + UserProvider.PROVIDER_NAME + "/" + Users.USERS_TABLE_NAME), values);
        	
            cursor = provider.query(Uri.parse("content://" + UserProvider.PROVIDER_NAME + "/" + Users.USERS_TABLE_NAME), null, null, null, null);
            if (cursor.moveToFirst()) {
            	email = cursor.getString(cursor.getColumnIndex(Users.EMAIL));
            }
            assertEquals("example@mail.hu", email);
        } catch (IllegalArgumentException e) {
            assertTrue(false);
        }
    }
}
