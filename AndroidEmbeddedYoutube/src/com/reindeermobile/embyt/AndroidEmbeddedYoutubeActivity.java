
package com.reindeermobile.embyt;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AndroidEmbeddedYoutubeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.main);

        // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(
        // "http://www.youtube.com/v/JY3u7bB7dZk"))));

        WebView wv = new WebView(getApplicationContext());
        // wv.getSettings().setPluginsEnabled(true);
        wv.getSettings().setPluginState(PluginState.ON_DEMAND);
        wv.getSettings().setJavaScriptEnabled(true);
        // wv.loadUrl("http://www.youtube.com/embed/JY3u7bB7dZk?autoplay=1");
        // wv.setWebViewClient(new WebViewClient() {
        // @Override
        // public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // // YouTube video link
        // if (url.startsWith("vnd.youtube:")) {
        // int n = url.indexOf("?");
        // if (n > 0)
        // {
        // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(
        // "http://www.youtube.com/v/%s",
        // url.substring("vnd.youtube:".length(), n)))));
        // }
        // return (true);
        // }
        //
        // return false;
        // }
        // });
        // wv.loadUrl("vnd.youtube:JY3u7bB7dZk");
        //
        // // wv.getSettings()
        // // .setUserAgentString(
        // //
        // "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/534.36 (KHTML, like Gecko) Chrome/13.0.766.0 Safari/534.36");
        try {
            InputStream inputStream = getAssets().open("video.html");
            BufferedReader bufferedReader = new BufferedReader(new
                    InputStreamReader(inputStream), 8 * 1024);

            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb = sb.append(line);
            }

            wv.loadData(sb.toString(), "text/html", null);
            // //
            // wv.loadUrl("http://apiblog.youtube.com/2010/07/new-way-to-embed-youtube-videos.html");
        } catch (IOException exception) {
            // // TODO Auto-generated catch block
            // exception.printStackTrace();
        }
        setContentView(wv);
    }
}
