
package com.reindeermobile.example.providers;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class User {
    public static final class Users implements BaseColumns {
        public static final String USERS_TABLE_NAME = "users";

        public static final Uri CONTENT_URI = Uri.parse("content://" + UserProvider.PROVIDER_NAME
                + "/users");
        // public static final String CONTENT_TYPE =
        // "vnd.android.cursor.dir/com.reindeermobile.example.users";
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/com.reindeermobile.example.users";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/com.reindeermobile.example.users";

        public static final int USERS = 1;
        public static final int USERS_ID = 2;

        public static final String USER_ID = "_id";
        public static final String EMAIL = "email";
        public static final String NAME = "name";

        private Users() {
        }
    }
}
