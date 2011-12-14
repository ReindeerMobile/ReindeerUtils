
package com.reindeermobile.example.model;

import android.net.Uri;



public interface IContentModel {
    void saveContent(String email, String name);
    Uri loadContent();
}
