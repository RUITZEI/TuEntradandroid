package com.ruitzei.tuentrada;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manuelpeinado.fadingactionbar.view.ObservableScrollable;
import com.manuelpeinado.fadingactionbar.view.OnScrollChangedCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.shamanland.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RUITZEI on 22/12/2014.
 */
public class FragmentTest extends Fragment implements AbsListView.OnScrollListener{
    // The height of your fully expanded header view (same than in the xml layout)
    int headerHeight = 250;
    // The height of your fully collapsed header view. Actually the Toolbar height (56dp)
    int minHeaderTranslation = 56;
    // The left margin of the Toolbar title (according to specs, 72dp)
    int toolbarTitleLeftMargin = 72;

    private ListView listView;

    private View rootView;
    // Header views
    private RelativeLayout headerContainer;
    private TextView headerTitle;
    private TextView headerSubtitle;
    private ImageView mHeaderImage;
    private DetailListAdapter adapter;
    private FloatingActionButton headerFab;

    private MainActivity actividadPrincipal;
    private ImageLoader mImageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate your header view
        View headerView = inflater.inflate(R.layout.header_view, listView, false);
        //Inflating rootview
        //rootView = inflater.inflate(R.layout.test_list, container, false);
        rootView = inflater.inflate(R.layout.test_list, container, false);
        setHasOptionsMenu(false);

        actividadPrincipal = (MainActivity)getActivity();

        listView = (ListView) rootView.findViewById(R.id.listView);

        List<String> detailValues = new ArrayList<String>();
        /*
        nombre , fecha , venue_name , seats_from , ciudad
        */
        detailValues.add(getArguments().getString("nombre"));
        detailValues.add(getArguments().getString("fecha"));
        detailValues.add(getArguments().getString("ciudad"));
        detailValues.add(getArguments().getString("venue_name"));
        detailValues.add(getArguments().getString("seats_from"));
        detailValues.add(getArguments().getString("series_name"));

        adapter = new DetailListAdapter(getActivity(), detailValues);
        listView.setAdapter(adapter);

        mHeaderImage = (ImageView) headerView.findViewById(R.id.header_image);

        // Init the headerHeight and minHeaderTranslation values
        headerHeight = 250;
        minHeaderTranslation = -headerHeight +
                56;

        // Retrieve the header views
        //headerContainer = (RelativeLayout) headerView .findViewById(R.id.header_container);
        headerContainer = (RelativeLayout) headerView .findViewById(R.id.header_container);
        headerTitle = (TextView) headerView .findViewById(R.id.header_title);
        headerFab = (FloatingActionButton) headerView.findViewById(R.id.header_fab);

        //headerSubtitle = (TextView) headerView .findViewById(R.id.header_subtitle);

        // Add the headerView to your listView
        listView.addHeaderView(headerView, null, false);

        // Set the onScrollListener
        listView.setOnScrollListener(this);

        // ...
        mImageLoader = actividadPrincipal.getImageLoader();

        String link = getArguments().getString("image");
        mImageLoader.displayImage(link, mHeaderImage);

        return rootView;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        // Do nothing
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        Integer scrollY = getScrollY(view);

        // This will collapse the header when scrolling, until its height reaches
        // the toolbar height
        headerContainer.setTranslationY(Math.max(0, scrollY + minHeaderTranslation));


        // Scroll ratio (0 <= ratio <= 1).
        // The ratio value is 0 when the header is completely expanded,
        // 1 when it is completely collapsed
        float ratio = 1 - Math.max(
                (float) (-minHeaderTranslation - scrollY) / -minHeaderTranslation, 0f);


        // Now that we have this ratio, we only have to apply translations, scales,
        // alpha, etc. to the header views

        // For instance, this will move the toolbar title & subtitle on the X axis
        // from its original position when the ListView will be completely scrolled
        // down, to the Toolbar title position when it will be scrolled up.
        headerTitle.setTranslationX(toolbarTitleLeftMargin * ratio);
        //headerSubtitle.setTranslationX(toolbarTitleLeftMargin * ratio);

        headerFab.setAlpha(1 - ratio);
    }


    // Method that allows us to get the scroll Y position of the ListView
    public int getScrollY(AbsListView view)
    {
        View c = view.getChildAt(0);

        if (c == null)
            return 0;

        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1)
            headerHeight = this.headerHeight;

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    @Override
    public void onResume(){
        super.onResume();
        actividadPrincipal.mDrawerToggle.setDrawerIndicatorEnabled(false);
        actividadPrincipal.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actividadPrincipal.getSupportActionBar().setTitle("Nombre del evento");
        actividadPrincipal.getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_web, menu);
    }
}