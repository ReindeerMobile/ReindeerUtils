package com.reindeermobile.example.test;

import com.reindeermobile.example.providers.UserProvider;

import android.test.ProviderTestCase2;

public class UserProviderTest extends ProviderTestCase2<UserProvider> {

	public UserProviderTest() {
		super(UserProvider.class, UserProvider.class.getName());
	}

	protected void setUp() throws Exception {
        super.setUp();
    }
	
}
