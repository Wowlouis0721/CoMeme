package com.comemepro;


public class listview_item {
    int likeId,unlikeId;
    String mainId;
    String key;

    public listview_item(String mainId,String key){
        this.mainId = mainId;
        this.key=key;
    }

    public String getkey() {
        return key;
    }
    public String getmainId() {
        return mainId;
    }

    @Override
    public String toString() {
        return "listItem{}";
    }
}