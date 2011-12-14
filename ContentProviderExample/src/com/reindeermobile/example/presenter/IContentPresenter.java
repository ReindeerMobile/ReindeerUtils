package com.reindeermobile.example.presenter;

import android.net.Uri;


public interface IContentPresenter {
    void saveUser(String email, String name);
    Uri loadUsers();
}
