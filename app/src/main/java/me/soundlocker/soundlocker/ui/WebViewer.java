package me.soundlocker.soundlocker.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import me.soundlocker.soundlocker.SoundLockerConstants;
import me.soundlocker.soundlocker.StorageWrapper;
import me.soundlocker.soundlocker.models.PreregisteredWebsite;

/**
 * Screen in which the preregistered website is opened up
 * and the password is inserted to its password field.
 */
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
        final String websiteString = intent.getStringExtra(SoundLockerConstants.PREREGISTERED_WEBSITE);
        final String password = intent.getStringExtra(SoundLockerConstants.PASSWORD);
        insertPasswordIntoWebsiteAux(websiteString, password);
    }

    private void insertPasswordIntoWebsiteAux(String websiteString, final String password) {
        WebView webview = buildWebView();
        final PreregisteredWebsite preregisteredWebsite = StorageWrapper.getWebsite(this.getApplicationContext(), websiteString);
        loadWebsiteIntoView(webview, preregisteredWebsite);
        insertPasswordIntoWebsite(password, webview, preregisteredWebsite);
    }

    private void loadWebsiteIntoView(WebView webview, PreregisteredWebsite preregisteredWebsite) {
        webview.loadUrl(preregisteredWebsite.getLoginUrl());
        setContentView(webview);
    }

    private WebView buildWebView() {
        WebView webview = new WebView(this);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        return webview;
    }

    private void insertPasswordIntoWebsite(final String password, WebView webview, final PreregisteredWebsite preregisteredWebsite) {
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String insertPasswordScript = preregisteredWebsite.getPasswordFieldElement() + ".value = '" + password + "';";
                view.evaluateJavascript(insertPasswordScript, null);
            }
        });
    }
}
