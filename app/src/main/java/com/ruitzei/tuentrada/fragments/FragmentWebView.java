package com.ruitzei.tuentrada.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.widget.FacebookDialog;
import com.ruitzei.tuentrada.MainActivity;
import com.ruitzei.tuentrada.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RUITZEI on 25/12/2014.
 */
public class FragmentWebView extends Fragment{
    private static final String TAG = "Webview Fragment";
    private WebView web;
    private MainActivity actividadPrincipal;
    private String link;
    private String linkMobile;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);

        this.actividadPrincipal = ((MainActivity)getActivity());

        //ProgressBar will allow to show progress on the top of the screen.
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        progressBar.setMax(100);


        web = (WebView)view.findViewById(R.id.webView);

        web.getSettings().setJavaScriptEnabled(true);
        web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress){
                actividadPrincipal.setProgress(progress * 1000);

                //Updating progressBar status or hiding when load finishes
                progressBar.setProgress(progress);
                progressBar.setVisibility(View.VISIBLE);

                if (progress == 100){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        web.setWebViewClient(new WebViewClient(){
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
                Toast.makeText(actividadPrincipal, "Oh no " + description, Toast.LENGTH_SHORT).show();
            }
        });
        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(true);

        //Getting link from last Fragment.
        this.link = getArguments().getString("link");
        this.linkMobile = getArguments().getString("linkMobile");
        web.loadUrl(linkMobile);
        Log.d("WebView", "Abriendo Link:" + linkMobile);

        //Has options menu + onPrepare + onCreate = inflate diff menu for each fragment.
        setHasOptionsMenu(true);

        return view;
    }

    /**
     * Importante: Esto solo se llama si en el onCreate() de cada fragment llamo al
     * metodo SetHasOptionsMenu(true);
     * II: Parece que si defino que hacer con cada boton en el MainActivity se llama solo al
     * onOptionsItemSelected del Main.
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_web, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_copy:
                /*
                Copy link to Clipboard
                 */
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("LinkMobile", linkMobile);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), "Link Copiado!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_browser:
                /*
                Open the link on cell's Browser.
                 */
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
                break;

            case R.id.action_share:
                //Comparto el link DESKTOP en el muro.
                actividadPrincipal.shareLinkOnFb(this.link);
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        actividadPrincipal.mDrawerToggle.setDrawerIndicatorEnabled(false);
        actividadPrincipal.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actividadPrincipal.getSupportActionBar().setHomeButtonEnabled(true);
        actividadPrincipal.getSupportActionBar().setTitle("Comprar");
        actividadPrincipal.getmToolBar().setBackgroundColor(getResources().getColor(R.color.TuEntradaMain));
    }


    @Override
    public void onDestroy(){
        Log.d(TAG, "Se Limpio el Cache");
        web.clearCache(true);
        super.onDestroy();
    }
}
