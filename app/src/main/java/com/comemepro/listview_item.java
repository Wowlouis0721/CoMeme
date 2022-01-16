package com.comemepro;


public class listview_item {
    int likeId,unlikeId;
    String mainId;

    public listview_item(String mainId) {
        this.mainId = mainId;
    }
    public String getmainId() {
        return mainId;
    }

    @Override
    public String toString() {
        return "listItem{}";
    }
}