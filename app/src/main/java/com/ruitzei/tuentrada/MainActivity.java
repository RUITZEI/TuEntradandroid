package com.ruitzei.tuentrada;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
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
import com.ruitzei.tuentrada.model.Categorias;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends ActionBarActivity{
    private static final String TAG = "Actividad Principal";

    public ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private ListView mDrawerListCategories;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolBar;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mEventPhotoOptions;
    private int ultimaPosicionSeleccionada = -1;

    private List<ItemAgenda> agenda;

    //Deberia ser el link para descargar del playstore.
    private static final String LINK_TUENTRADA = "http://tuentrada.com";

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
            //getWindow().setStatusBarColor( getResources().getColor(R.color.darkYellow) );
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

        //Navigation drawer. Es medio engorroso pero no se podia solucionar de otra manera.
        mDrawerList.setAdapter(new DrawerAdapter(this, ItemDrawer.getHeaderList(), getImageLoader()));
        setListViewHeightBasedOnChildren(mDrawerList);
        mDrawerListCategories.setAdapter(new DrawerAdapter(this, ItemDrawer.getTestingList(), getImageLoader()));
        setListViewHeightBasedOnChildren(mDrawerListCategories);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close){
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                Log.d("TOP CLOSE> ", Integer.toString(mDrawerList.getSelectedItemPosition()));
                Log.d("BOTTOM CLOSE> ", Integer.toString(mDrawerListCategories.getSelectedItemPosition()));
                //getActivity().getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActivity().getActionBar().setTitle(mDrawerTitle);
                Log.d("Top List position> ", Integer.toString(mDrawerList.getSelectedItemPosition()));
                Log.d("Bottom List position> ", Integer.toString(mDrawerListCategories.getSelectedItemPosition()));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    //La primera vez que abro el nav. drawer, muestro el primer item seleccionado.
                    if (ultimaPosicionSeleccionada < 0){
                        selectFirstItem();
                    }
                }
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);



        mDrawerListCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Tengo que estar seguro de que el fragment esta ahi visible...
                final FragmentAgenda fragment = (FragmentAgenda)getSupportFragmentManager().findFragmentByTag("FRAGMENT_AGENDA");
                if (fragment.isVisible() && existeAgenda()){
                    Log.d("Categories Drawer", "item clicked = " + position);
                    ultimaPosicionSeleccionada = position;
                    //mDrawerList.getItem
                    Log.d("List count: " , Integer.toString(mDrawerListCategories.getAdapter().getCount()));

                    //5 = Compartir en facebook.
                    //6 = Compartir en Twitter
                    switch (position){
                        case 0:
                            actualizarVistaAgendaConDatos(Categorias.PRINCIPAL, fragment);
                            break;
                        case 1:
                            mostrarDestacados(fragment);
                            break;
                        case 2:
                            //Aca pongo un return porque de ahora en mas, en la segunda posicion tengo el header.
                            return;
                        case 3:
                            actualizarVistaAgendaConDatos(Categorias.CONCIERTOS, fragment);
                            break;
                        case 4:
                            actualizarVistaAgendaConDatos(Categorias.DEPORTES, fragment);
                            break;
                        case 5:
                            actualizarVistaAgendaConDatos(Categorias.FAMILIA, fragment);
                            break;
                        case 6:
                            actualizarVistaAgendaConDatos(Categorias.TEATRO, fragment);
                            break;
                        case 7:
                            actualizarVistaAgendaConDatos(Categorias.EXPOSICIONES, fragment);
                            break;
                        case 8:
                            shareLinkOnFb(LINK_TUENTRADA);
                            break;
                        case 9:
                            shareLinkOnTwitter(LINK_TUENTRADA);
                            break;
                        default:
                            Log.e("fragment Agenda:" , "coso incorrecto");
                    }
                    deselectAllDrawerItems();
                    view.setSelected(true);
                    view.setBackgroundResource(R.color.lightGray);
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }

            }

        });

        //Me muestra todos los mails registrados en el telefono.
        saludarUsuario();

        //Dejo seleccionado el primer item del navigation drawer.

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            FragmentAgenda fragment = new FragmentAgenda();
            transaction.replace(R.id.container, fragment, "FRAGMENT_AGENDA");
            //transaction.setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_right);
            transaction.commit();
        }
    }




    // Workaround beacuse listview's setSelected doesn't seem to be working at all...
    public void deselectAllDrawerItems() {
        //Primera lista (novedades + todas)
        for (int i = 0; i < mDrawerListCategories.getChildCount(); i++) {
            mDrawerListCategories.getChildAt(i).setBackgroundResource(R.drawable.chat_list_selector);
        }
    }

    public void selectFirstItem(){
        ultimaPosicionSeleccionada = 0;
        mDrawerListCategories.getChildAt(0).setBackgroundResource(R.color.lightGray);
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

    public static void setListViewHeightBasedOnChildren(ListView listView){
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

    /*
    Primero vacio la lista para que se vea el fondo blanco, pongo a girar el spinner.
    Luego de 0.5secs desaparece el spinner y se muestran los nuevos datos.
     */
    public void actualizarVistaAgendaConDatos(final String clave, final FragmentAgenda fragment){
        agenda = new ArrayList<ItemAgenda>();
        fragment.mostrarLista();
        fragment.hideErrorLayout();
        fragment.mostrarSpinner();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragment.cambiarAgenda(clave);
                        fragment.mostrarLista();
                        fragment.ocultarSpinner();
                    }
                });
            }
        }, 500);
    }

    public void mostrarDestacados(final FragmentAgenda fragment){
        agenda = new ArrayList<ItemAgenda>();
        fragment.mostrarLista();
        fragment.hideErrorLayout();
        fragment.mostrarSpinner();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragment.cambiarAgenda(Categorias.CONCIERTOS);
                        fragment.mostrarDestacados();
                        fragment.ocultarSpinner();
                    }
                });
            }
        }, 500);
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


    /*
    Abre el dialog comun de facebook para compartir un link en el muro.
     */
    public void shareLinkOnFb(String link){
        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                .setLink(link)
                .setApplicationName("TuEntrada")
                .build();
        uiHelper.trackPendingDialogCall(shareDialog.present());
    }

    public void shareLinkOnTwitter(String link){
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("Quien me acompaña? " + link);
        builder.show();
    }

    public void saludarUsuario(){
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                Log.d(TAG, "Posible mail: " + possibleEmail);
            }
        }
    }

    public int getUltimaPosicionSeleccionada() {
        return ultimaPosicionSeleccionada;
    }

    public void setUltimaPosicionSeleccionada(int ultimaPosicionSeleccionada) {
        this.ultimaPosicionSeleccionada = ultimaPosicionSeleccionada;
    }
}
