package com.ruitzei.tuentrada.model;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.ruitzei.tuentrada.R;
import com.ruitzei.tuentrada.fragments.FragmentAgenda;
import com.ruitzei.tuentrada.items.ItemAgenda;

/**
 * Created by RUITZEI on 07/02/2015.
 */
public class OnAgendaOverflowItemSelectedListener implements View.OnClickListener {
    private Context mContext;
    private FragmentAgenda fragment;
    private ItemAgenda itemAgenda;

    public OnAgendaOverflowItemSelectedListener(Context context, FragmentAgenda fragment, ItemAgenda item) {
        mContext = context;
        this.fragment = fragment;
        this.itemAgenda = item;
    }

    @Override
    public void onClick(View v) {
        // This is an android.support.v7.widget.PopupMenu;
        Log.d("Listener", "Btn clicked");

        PopupMenu popupMenu = new PopupMenu(mContext, v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_list_overflow, popupMenu.getMenu());

        // Force icons to show
        /*Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[] { boolean.class };
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            // Possible exceptions are NoSuchMethodError and NoSuchFieldError
            //
            // In either case, an exception indicates something is wrong with the reflection code, or the
            // structure of the PopupMenu class or its dependencies has changed.
            //
            // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
            // but in the case that they do, we simply can't force icons to display, so log the error and
            // show the menu normally.

            Log.w("asd", "error forcing menu icons to show", e);
            popupMenu.show();
            return;
        }*/

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.agenda_overflow_buy:
                        fragment.goToBuy(itemAgenda);
                        return true;

                    case R.id.agenda_overflow_details:
                        fragment.goToDetails(itemAgenda);
                        return true;

                    case R.id.agenda_overflow_schedule:
                        fragment.addToCalendar(itemAgenda);
                        return true;

                    default:
                        return false;
                }
            }
        });


        popupMenu.show();
    }
}
