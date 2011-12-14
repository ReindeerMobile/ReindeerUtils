
package com.reindeermobile.example.model;

import com.reindeermobile.example.providers.User.Users;
import com.reindeermobile.example.providers.UserProvider;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;



public class ContentModel implements IContentModel {
    
    private Context context;

    public ContentModel(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void saveContent(String email, String name) {
        ContentValues values = new ContentValues();
        values.put(Users.EMAIL, email);
        values.put(Users.NAME, name);
        Uri uri = context.getContentResolver().insert(Uri.parse("content://" + UserProvider.PROVIDER_NAME + "/" + Users.USERS_TABLE_NAME), values);
    }

    @Override
    public Uri loadContent() {
        return  Uri.parse("content://" + UserProvider.PROVIDER_NAME + "/" + Users.USERS_TABLE_NAME);
    }

}
