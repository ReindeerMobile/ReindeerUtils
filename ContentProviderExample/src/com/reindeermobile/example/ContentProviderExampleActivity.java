package com.reindeermobile.example;

import com.reindeermobile.example.activity.IContentView;
import com.reindeermobile.example.presenter.ContentPresenter;
import com.reindeermobile.example.providers.User.Users;

import android.app.Activity;
import android.content.Context;
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


public class ContentProviderExampleActivity extends Activity implements IContentView, OnClickListener {
	private EditText emailEditText;
	private EditText nameEditText;
	private ListView userListView;
	
	private ContentPresenter contentPresenter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.contentPresenter = new ContentPresenter(this);
		setContentView(R.layout.main);

		emailEditText = (EditText) findViewById(R.id.emailEditText);
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		Button saveButton = (Button) findViewById(R.id.saveButton);
		if (saveButton != null) {
			saveButton.setOnClickListener(this);
		}

		userListView = (ListView) findViewById(android.R.id.list);
	}

	@Override
	public void onClick(View v) {
	    contentPresenter.saveUser(emailEditText.getText().toString().trim(), nameEditText.getText().toString().trim());
		Toast.makeText(ContentProviderExampleActivity.this, "User saved", Toast.LENGTH_LONG).show();
	}


    @Override
    public void updateContentList() {
        Uri allUser = contentPresenter.loadUsers();
        
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

    @Override
    public Context getContext() {
        return this;
    }

}