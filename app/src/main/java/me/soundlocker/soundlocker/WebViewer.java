package me.soundlocker.soundlocker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

/**
 * Created by SamW on 11/2/2015.
 */
public class WebViewer extends Activity{
    private ArrayList<String> webEndings;
    private String password = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webEndings = new ArrayList<String>();
        populateWebEndings(webEndings);
        Intent intent = getIntent();
        String website = intent.getStringExtra("website");
        password = intent.getStringExtra("password");

        String url = makeUrl(website);
        WebView webview = new WebView(this);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);

//        webview.loadUrl(url);
        webview.loadUrl("https://accounts.google.com/ServiceLogin?service=mail&continue=https://mail.google.com/mail/#identifier");

        setContentView(webview);

        //prevents certain sites from defaulting to browser
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                System.out.println("TESTING");
                System.out.println("TESTING");
                System.out.println("TESTING");
                System.out.println("TESTING");
                System.out.println("TESTING");
                System.out.println("TESTING");
                System.out.println(password);

//                view.loadUrl("javascript:" +"var x = document.getElementById('email').value = 'blah';");
                view.evaluateJavascript("document.getElementById('Email').value = 'blah';",null);

//                view.loadUrl("javascript:" +"var x = document.getElementById('email').value = '" + "blah" + "';");
//                view.loadUrl("javascript:var y = document.getElementById('pass').value = " + password + ";");
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
