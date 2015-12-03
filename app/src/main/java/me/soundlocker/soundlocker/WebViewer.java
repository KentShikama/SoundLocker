package me.soundlocker.soundlocker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewer extends Activity {
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
        final String websiteString = intent.getStringExtra(ApplicationConstants.WEBSITE);
        final String password = intent.getStringExtra(ApplicationConstants.PASSWORD);

        WebView webview = new WebView(this);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        final Website website = StorageWrapper.getWebsite(this.getApplicationContext(), websiteString);
        webview.loadUrl(website.getLoginUrl());
        setContentView(webview);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript(website.getPasswordFieldElement() + ".value = '" + password + "';", null);
            }
        });
    }
}
