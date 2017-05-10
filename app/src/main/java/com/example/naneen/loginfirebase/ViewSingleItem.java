package com.example.naneen.loginfirebase;

/**
 * Created by naneen on 5/4/2017 AD.
 */

public class ViewSingleItem {
    private String Image_URL, Image_Title;

    public ViewSingleItem(String image_URL, String image_Title) {
        // the same name with DB
        Image_URL = image_URL;
        Image_Title = image_Title;
    }

    public ViewSingleItem() {
        // constructor
    }

    public String getImage_URL() {
        return Image_URL;
    }

    public void setImage_URL(String image_URL) {
        Image_URL = image_URL;
    }

    public String getImage_Title() {
        return Image_Title;
    }

    public void setImage_Title(String image_Title) {
        Image_Title = image_Title;
    }
}
