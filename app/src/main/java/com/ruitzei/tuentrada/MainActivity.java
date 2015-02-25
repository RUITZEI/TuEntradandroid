package com.ruitzei.tuentrada;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.facebook.AppEventsLogger;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.ruitzei.tuentrada.adapters.DrawerAdapter;
import com.ruitzei.tuentrada.fragments.FragmentAgenda;
import com.ruitzei.tuentrada.items.ItemAgenda;
import com.ruitzei.tuentrada.items.ItemDrawer;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    public ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private ListView mDrawerListCategories;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolBar;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mEventPhotoOptions;

    private List<ItemAgenda> agenda;

    //Facebook
    UiLifecycleHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        /*
         * This is for Facebook
         */
        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);


        mToolBar = (Toolbar) findViewById(R.id.toolbar);

        if (mToolBar != null){
            setSupportActionBar(mToolBar);
        }


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerListCategories = (ListView) findViewById(R.id.drawer_list_categories);

        if( Build.VERSION.SDK_INT >= 21 ) {
            //getActionBar().setIcon(R.drawable.ic_launcher);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor( getResources().getColor(R.color.darkerGray) );
        } else {
            //getActionBar().setIcon(R.drawable.ic_launcher);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(10);

        /*
        Image Loader Stuff. Cache enabled to improve imageloading.
         */
        mImageLoader = ImageLoader.getInstance();

        if (! mImageLoader.isInited()) {
            File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                    .threadPriority(Thread.MIN_PRIORITY)
                    .memoryCacheSizePercentage(10)
                    .diskCache(new UnlimitedDiscCache(cacheDir))
                    .build();

            mEventPhotoOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();

            mImageLoader.init(config);
        }

        mDrawerList.setAdapter(new DrawerAdapter(this, ItemDrawer.getHeaderList(), getImageLoader()));
        setListViewHeightBasedOnChildren(mDrawerList);
        mDrawerListCategories.setAdapter(new DrawerAdapter(this, ItemDrawer.getTestingList(), getImageLoader()));
        setListViewHeightBasedOnChildren(mDrawerListCategories);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close){
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActivity().getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActivity().getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_DRAGGING) {
                }
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Header Drawer click: ", "item clicked = " + position);
                if (position == 2){

                }
            }
        });

        mDrawerListCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Categories Drawer", "item clicked = " + position);
                if (position == 2){

                }
            }
        });

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            FragmentAgenda fragment = new FragmentAgenda();
            transaction.replace(R.id.container, fragment);
            //transaction.setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_right);
            transaction.commit();
        }
    }

    @Override protected void onPostCreate(Bundle savedInstance){
        super.onPostCreate(savedInstance);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle your other action bar items...
        if (id == android.R.id.home){
            Log.d("Back button", "Back button pressed, go back");
            //hideSoftKeyboard();
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight=0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        LinearLayout.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount()));

        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    public ImageLoader getImageLoader(){
        return this.mImageLoader;
    }

    public List<ItemAgenda> getAgenda(){
        return this.agenda;
    }

    public boolean existeAgenda(){
        return (this.agenda != null);
    }

    public void setAgenda(List<ItemAgenda> agenda){
        this.agenda = agenda;
    }

    public Toolbar getmToolBar(){
        return this.mToolBar;
    }

    public DisplayImageOptions getEventPhotoOptions() {
        return mEventPhotoOptions;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Resumes the uiHelper
        uiHelper.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        //Pausing uiHelper
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }

    public UiLifecycleHelper getUiHelper(){
        return this.uiHelper;
    }

    public void shareLinkOnFb(String link){
        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                .setLink(link)
                .setApplicationName("TuEntrada")
                .build();
        uiHelper.trackPendingDialogCall(shareDialog.present());
    }
}
