package com.ruitzei.tuentrada.items;

import com.ruitzei.tuentrada.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RUITZEI on 13/12/2014.
 */
public class ItemDrawer {

    private String name;
    private String imageLink;

    public ItemDrawer(String name, String imageLink){
        this.name = name;
        this.imageLink = imageLink;
    }

    public String getName(){
        return this.name;
    }

    public String getImageLink(){
        return this.imageLink;
    }

    public static List<ItemDrawer> getTestingList(){

        List<ItemDrawer> darwerList = new ArrayList<ItemDrawer>();

        String aux1 = "drawable://" + R.drawable.ic_launcher;
        String aux2 = "drawable://" + R.drawable.ic_launcher;
        String aux3 = "drawable://" + R.drawable.ic_launcher;

        darwerList.add(new ItemDrawer("Conciertos", aux1));
        darwerList.add(new ItemDrawer("Deportes", aux2));
        darwerList.add(new ItemDrawer("Familia", aux3));
        darwerList.add(new ItemDrawer("Teatro", aux3));
        darwerList.add(new ItemDrawer("Exposiciones", aux3));


        return darwerList;
    }

    public static List<ItemDrawer> getHeaderList(){
        List<ItemDrawer> darwerList = new ArrayList<ItemDrawer>();

        String aux1 = "drawable://" + R.drawable.ic_launcher;
        String aux2 = "drawable://" + R.drawable.ic_launcher;
        String aux3 = "drawable://" + R.drawable.ic_launcher;

        darwerList.add(new ItemDrawer("Principal", aux1));
        darwerList.add(new ItemDrawer("Destacados", aux1));

        return darwerList;
    }

}

