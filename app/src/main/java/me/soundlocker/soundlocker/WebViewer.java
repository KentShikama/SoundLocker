package me.soundlocker.soundlocker;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by SamW on 11/2/2015.
 */
public class WebViewer extends Activity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webview = new WebView(this);
        webview.loadUrl("http://facebook.com/");

        setContentView(webview);
    }

    //doesn't work yet 
    public void onPageFinished(WebView view, String url) {
//        view.loadUrl("javascript:document.getElementsByName('school')[0].value = 'schoolname'");
//        view.loadUrl("javascript:document.getElementsByName('j_username')[0].value = 'username'");
        view.loadUrl("javascript:document.getElementsByName('j_password')[0].value = 'XXX'");

//        view.loadUrl("javascript:document.forms['login'].submit()");
    }

}
