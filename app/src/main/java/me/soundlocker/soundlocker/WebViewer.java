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
        insertPasswordIntoWebsite();
    }

    protected void onResume() {
        super.onResume();
        insertPasswordIntoWebsite();
    }

    private void insertPasswordIntoWebsite() {
        Intent intent = getIntent();
        final String websiteString = intent.getStringExtra(ApplicationConstants.WEBSITE);
        final String password = intent.getStringExtra(ApplicationConstants.PASSWORD);
        insertPasswordIntoWebsiteAux(websiteString, password);
    }

    private void insertPasswordIntoWebsiteAux(String websiteString, final String password) {
        WebView webview = buildWebView();
        final Website website = StorageWrapper.getWebsite(this.getApplicationContext(), websiteString);
        loadWebsiteIntoView(webview, website);
        insertPassword(password, webview, website);
    }

    private void loadWebsiteIntoView(WebView webview, Website website) {
        webview.loadUrl(website.getLoginUrl());
        setContentView(webview);
    }

    private WebView buildWebView() {
        WebView webview = new WebView(this);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        return webview;
    }

    private void insertPassword(final String password, WebView webview, final Website website) {
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript(website.getPasswordFieldElement() + ".value = '" + password + "';", null);
            }
        });
    }
}
