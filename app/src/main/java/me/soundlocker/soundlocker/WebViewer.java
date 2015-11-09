package me.soundlocker.soundlocker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import java.util.ArrayList;

/**
 * Created by SamW on 11/2/2015.
 */
public class WebViewer extends Activity{
    private ArrayList<String> webEndings;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webEndings = new ArrayList<String>();
        populateWebEndings(webEndings);
        Intent intent = getIntent();
        String website = intent.getStringExtra("website");

        String url = makeUrl(website);
        WebView webview = new WebView(this);
        webview.loadUrl(url);

        setContentView(webview);
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
    //doesn't work yet 
    public void onPageFinished(WebView view, String url) {
//        view.loadUrl("javascript:document.getElementsByName('school')[0].value = 'schoolname'");
//        view.loadUrl("javascript:document.getElementsByName('j_username')[0].value = 'username'");
        view.loadUrl("javascript:document.getElementsByName('j_password')[0].value = 'XXX'");

//        view.loadUrl("javascript:document.forms['login'].submit()");
    }

}
