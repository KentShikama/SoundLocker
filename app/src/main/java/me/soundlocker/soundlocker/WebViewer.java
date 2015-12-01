package me.soundlocker.soundlocker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

public class WebViewer extends Activity{
    private static final String PASSWORD = "password";
    private static final String WEBSITE = "website";
    private ArrayList<String> webEndings;
    private String password = "";

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
        String website = intent.getStringExtra(WEBSITE);
        password = intent.getStringExtra(PASSWORD);
        webEndings = new ArrayList<String>();
        populateWebEndings(webEndings);

        String url = makeUrl(website);
        WebView webview = new WebView(this);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);

        webview.loadUrl("http://www.facebook.com");

        setContentView(webview);

        //prevents certain sites from defaulting to browser
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript("document.getElementsByName('pass')[0].value = '"+password+"';",null);
            }
        });

    }

    private void populateWebEndings(ArrayList<String> aL) {
        aL.add(".org");
        aL.add(".com");
        aL.add(".net");
        aL.add(".gov");
        aL.add(".edu");
    }

    private String makeUrl (String siteName){
        if (!siteName.contains("http://")){
            siteName = "http://" + siteName;
        }



        for(int i=0; i<webEndings.size(); i++){
            if(siteName.contains(webEndings.get(i))){
                return siteName;
            }
        }
        String appendedWebSite = siteName+".com";
        return appendedWebSite;

    }

}
