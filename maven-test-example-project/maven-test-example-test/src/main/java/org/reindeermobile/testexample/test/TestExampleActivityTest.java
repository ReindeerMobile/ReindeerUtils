package org.reindeermobile.testexample.test;

import hu.reindeermobile.testexample.HelloAndroidActivity;
import junit.framework.Assert;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

public class TestExampleActivityTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

	private Activity activity;
	
	public TestExampleActivityTest() {
		super(HelloAndroidActivity.class);
	}
	
	@Override
	public void setUp() throws Exception {
		activity = getActivity();
	}
	
	public void testPreConditions() throws Exception {
		Assert.assertNotNull(activity);
	}
	
	
}
