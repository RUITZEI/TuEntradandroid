package com.ruitzei.tuentrada.model;

import com.ruitzei.tuentrada.items.ItemAgenda;

import java.util.HashMap;
import java.util.List;

/**
 * Created by RUITZEI on 07/01/2015.
 */
public interface OnDownloadCompleted {
    public void onDownloadSucceed(HashMap<String, List<ItemAgenda>> agenda);
    public void onDownloadFailed();
}
