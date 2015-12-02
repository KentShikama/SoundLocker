package me.soundlocker.soundlocker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewer extends Activity {
    private static final String PASSWORD = "password";
    private static final String WEBSITE = "website";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInitialValues();
    }

    protected void onResume() {
        super.onResume();
        setInitialValues();
    }

    private void setInitialValues() {
        Intent intent = getIntent();
        final String website = intent.getStringExtra(WEBSITE);
        final String password = intent.getStringExtra(PASSWORD);

        WebView webview = new WebView(this);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.loadUrl("https://www.facebook.com");
        setContentView(webview);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript("document.getElementsByName('pass')[0].value = '" + password + "';", null);
            }
        });
    }
}
