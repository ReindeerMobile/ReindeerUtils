
package com.reindeermobile.example.presenter;

import com.reindeermobile.example.activity.IContentView;
import com.reindeermobile.example.model.ContentModel;
import com.reindeermobile.example.model.IContentModel;

import android.net.Uri;


public class ContentPresenter implements IContentPresenter {
    private IContentView contentView;
    private IContentModel contentModel;

    public ContentPresenter(IContentView contentView) {
        super();
        this.setContentView(contentView);
        this.setContentModel(new ContentModel(contentView.getContext()));
    }

    public IContentView getContentView() {
        return contentView;
    }

    public void setContentView(IContentView contentView) {
        this.contentView = contentView;
    }

    public IContentModel getContentModel() {
        return contentModel;
    }

    public void setContentModel(IContentModel contentModel) {
        this.contentModel = contentModel;
    }

    @Override
    public void saveUser(String email, String name) {
        contentModel.saveContent(email, name);
        contentView.updateContentList();
    }

    @Override
    public Uri loadUsers() {
        return contentModel.loadContent();
    }

}
