package com.reindeermobile.example;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.reindeermobile.example.providers.User.Users;
import com.reindeermobile.example.providers.UserProvider;

public class ContentProviderExampleActivity extends Activity implements OnClickListener {
	private EditText emailEditText;
	private EditText nameEditText;
	private ListView userListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		emailEditText = (EditText) findViewById(R.id.emailEditText);
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		Button saveButton = (Button) findViewById(R.id.saveButton);
		if (saveButton != null) {
			saveButton.setOnClickListener(this);
		}

		userListView = (ListView) findViewById(android.R.id.list);
		updateUI();
	}

	@Override
	public void onClick(View v) {
		ContentValues values = new ContentValues();
		values.put(Users.EMAIL, emailEditText.getText().toString().trim());
		values.put(Users.NAME, nameEditText.getText().toString().trim());
		Uri uri = getContentResolver().insert(Uri.parse("content://" + UserProvider.PROVIDER_NAME + "/" + Users.USERS_TABLE_NAME), values);
		Toast.makeText(ContentProviderExampleActivity.this, "User saved", Toast.LENGTH_LONG).show();

		updateUI();
	}

	private void updateUI() {
		Uri allUser = Uri.parse("content://" + UserProvider.PROVIDER_NAME + "/" + Users.USERS_TABLE_NAME);

		Cursor c = managedQuery(allUser, new String[] {
				Users.USER_ID, Users.EMAIL
		}, null, null, null);

		String[] from = new String[] {
				Users.EMAIL
		};
		int[] to = new int[] {
				android.R.id.text1
		};

		CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, c, from, to);
		this.userListView.setAdapter(cursorAdapter);
	}

}