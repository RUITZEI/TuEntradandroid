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

        String conciertos = "drawable://" + R.drawable.ic_conciertos;
        String deportes = "drawable://" + R.drawable.ic_deportes;
        String familia = "drawable://" + R.drawable.ic_familia;
        String teatro = "drawable://" + R.drawable.ic_teatro;
        String exposiciones = "drawable://" + R.drawable.ic_exposiciones;
        String facebook = "drawable://" + R.drawable.ic_launcher;

        darwerList.add(new ItemDrawer("Conciertos", conciertos));
        darwerList.add(new ItemDrawer("Deportes", deportes));
        darwerList.add(new ItemDrawer("Familia", familia));
        darwerList.add(new ItemDrawer("Teatro", teatro));
        darwerList.add(new ItemDrawer("Exposiciones", exposiciones));
        darwerList.add(new ItemDrawer("Compartir FB", facebook));


        return darwerList;
    }

    public static List<ItemDrawer> getHeaderList(){
        List<ItemDrawer> darwerList = new ArrayList<ItemDrawer>();

        String principal = "drawable://" + R.drawable.ic_principal;
        String destacados = "drawable://" + R.drawable.ic_destacado;

        darwerList.add(new ItemDrawer("Principal", principal));
        darwerList.add(new ItemDrawer("Destacados", destacados));

        return darwerList;
    }

}

