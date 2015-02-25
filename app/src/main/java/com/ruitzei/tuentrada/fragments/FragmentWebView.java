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
    private static final String APP_SECRET = "4068d1dae1fd9c553d8124a5f1ebf0ad";
    private static final String APP_ID = "353925808146091";
    private WebView web;
    private MainActivity actividadPrincipal;
    private String link;
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
        web.loadUrl(link);
        Log.d("WebView", "Abriendo Link:" + link);

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
                ClipData clip = ClipData.newPlainText("Link", link);
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
                /*
                Share the Link Via facebook
                 */
                actividadPrincipal.shareLinkOnFb(this.link
                );
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

    public void shareOnFacebook(){
        String mySharedLink = this.link;
        String myAppId = APP_ID;

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mySharedLink);

        // Include your Facebook App Id for attribution
        shareIntent.putExtra("com.facebook.platform.extra.APPLICATION_ID", myAppId);

        startActivityForResult(Intent.createChooser(shareIntent, "Share"), 10);

    }

    public void onShareClick() {
        Log.d("Web", "On Share CLick clicked");
        Resources resources = getResources();

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Extra text");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Extra Subject");
        emailIntent.setType("message/rfc822");

        PackageManager pm = getActivity().getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");


        Intent openInChooser = Intent.createChooser(emailIntent, "elegite algo");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if(packageName.contains("twitter")) {
                    intent.putExtra(Intent.EXTRA_TEXT, "Twitta");
                } else if(packageName.contains("facebook")) {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                    // will show the <meta content ="..."> text from that page with our link in Facebook.
                    String mySharedLink = this.link;
                    String myAppId = APP_ID;

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, mySharedLink);

                    // Include your Facebook App Id for attribution
                    intent.putExtra("com.facebook.platform.extra.APPLICATION_ID", myAppId);
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

    public void share(){
        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(actividadPrincipal)
                .setLink(this.link)
                .setApplicationName("TuEntrada")
                .build();
        actividadPrincipal.getUiHelper().trackPendingDialogCall(shareDialog.present());
    }


    @Override
    public void onDestroy(){
        Log.d(TAG, "Se Limpio el Cache");
        web.clearCache(true);
        super.onDestroy();
    }
}
