package com.ruitzei.tuentrada.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.manuelpeinado.fadingactionbar.view.ObservableScrollable;
import com.manuelpeinado.fadingactionbar.view.OnScrollChangedCallback;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.ruitzei.tuentrada.MainActivity;
import com.ruitzei.tuentrada.R;
import com.shamanland.fab.FloatingActionButton;

/**
 * Created by RUITZEI on 21/12/2014.
 */
public class FragmentDetails extends Fragment implements OnScrollChangedCallback{
    private Toolbar mToolbar;
    private Drawable mActionBarBackgroundDrawable;
    private ImageView mHeader;
    private View rootView;
    private int mLastDampedScroll;
    private int mInitialStatusBarColor;
    private int mFinalStatusBarColor;
    private SystemBarTintManager mStatusBarManager;
    private MainActivity actividadPrincipal;
    private FloatingActionButton fab;

            /*
        image , nombre , fecha , venue_name , seats_from , ciudad
        */
    private TextView detailCity;
    private TextView detaillName;
    private TextView detailDate;
    private TextView detailVenue;
    private TextView detailMin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        actividadPrincipal = ((MainActivity)getActivity());

        mToolbar = ((MainActivity)getActivity()).getmToolBar();

        this.detailCity = (TextView) rootView.findViewById(R.id.detail_ciudad);
        this.detailDate = (TextView) rootView.findViewById(R.id.detail_fecha);
        this.detaillName = (TextView) rootView.findViewById(R.id.detail_nombre);
        this.detailMin = (TextView) rootView.findViewById(R.id.detail_minimo);
        this.detailVenue = (TextView) rootView.findViewById(R.id.detail_lugar);

        mActionBarBackgroundDrawable = mToolbar.getBackground();
        actividadPrincipal.setSupportActionBar(mToolbar);

        mStatusBarManager = new SystemBarTintManager(getActivity());
        mStatusBarManager.setStatusBarTintEnabled(true);
        mInitialStatusBarColor = Color.BLACK;
        mFinalStatusBarColor = getResources().getColor(R.color.primary_dark_material_dark);

        mHeader = (ImageView) rootView.findViewById(R.id.image_header);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("link", getArguments().getString("link"));
                Fragment fragment = new FragmentWebView();
                fragment.setArguments(args);
                FragmentManager fm = actividadPrincipal.getSupportFragmentManager();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.container, fragment)
                        .addToBackStack("Fragback")
                        .commit();
            }
        });

        ObservableScrollable scrollView = (ObservableScrollable) rootView.findViewById(R.id.scrollview);
        scrollView.setOnScrollChangedCallback(this);

        onScroll(-1, 0);

        String link = getArguments().getString("image");
        //actividadPrincipal.getImageLoader().displayImage(link, mHeader);
        //mHeader.setImageBitmap(blurBitmap(BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.ic_launcher)));
        actividadPrincipal.getImageLoader().loadImage(link, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage){
                mHeader.setImageBitmap(loadedImage);
            }
        });

        link = getArguments().getString("nombre");
        this.detaillName.setText(link);

        link = getArguments().getString("fecha");
        this.detailDate.setText(link);

        link = getArguments().getString("venue_name");
        this.detailVenue.setText(link);

        link = getArguments().getString("seats_from");
        this.detailMin.setText("Desde $" + link);

        link = getArguments().getString("ciudad");
        this.detailCity.setText(link);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onScroll(int l, int scrollPosition) {
        int headerHeight = mHeader.getHeight() - mToolbar.getHeight();
        float ratio = 0;
        if (scrollPosition > 0 && headerHeight > 0)
            ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;

        updateActionBarTransparency(ratio);
        updateStatusBarColor(ratio);
        updateParallaxEffect(scrollPosition);
    }

    private void updateActionBarTransparency(float scrollRatio) {
        int newAlpha = (int) (scrollRatio * 255 + 55);
        mActionBarBackgroundDrawable.setAlpha(newAlpha);
        mToolbar.setBackground(mActionBarBackgroundDrawable);
    }

    private void updateStatusBarColor(float scrollRatio) {
        int r = interpolate(Color.red(mInitialStatusBarColor), Color.red(mFinalStatusBarColor), 1 - scrollRatio);
        int g = interpolate(Color.green(mInitialStatusBarColor), Color.green(mFinalStatusBarColor), 1 - scrollRatio);
        int b = interpolate(Color.blue(mInitialStatusBarColor), Color.blue(mFinalStatusBarColor), 1 - scrollRatio);
        mStatusBarManager.setTintColor(Color.rgb(r, g, b));
    }

    private void updateParallaxEffect(int scrollPosition) {
        float damping = 0.5f;
        int dampedScroll = (int) (scrollPosition * damping);
        int offset = mLastDampedScroll - dampedScroll;
        mHeader.offsetTopAndBottom(-offset);

        mLastDampedScroll = dampedScroll;
    }

    private int interpolate(int from, int to, float param) {
        return (int) (from * param + to * (1 - param));
    }

    @Override
    public void onResume(){
        super.onResume();
        actividadPrincipal.mDrawerToggle.setDrawerIndicatorEnabled(false);
        actividadPrincipal.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actividadPrincipal.getSupportActionBar().setTitle("  TuEntrada");
        actividadPrincipal.getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        //inflater.inflate(R.menu.menu_detail, menu);
    }

    public Bitmap blurBitmap(Bitmap bitmap){

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(getActivity().getApplicationContext());

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        blurScript.setRadius(25.f);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
        bitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;
    }
}