package com.asm.fastpay.models;

import java.util.List;

public class HomePageModel {

    public static final int BANNER_SLIDER = 0;
    public static final int HORIZONTAL_PRODUCT_VIEW = 1;
    public static final int GRID_PRODUCT_VIEW = 2;

    private int type;
    private String backgroudColor;


    // Banner Slider
    private List<SliderModel> sliderModelList;
    private String title;
    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;
    private List<WishListModel> viewAllProductList;

    public HomePageModel(int type, List<SliderModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;
    }

    public HomePageModel(int type, String title, String backgroudColor, List<HorizontalProductScrollModel> horizontalProductScrollModelList, List<WishListModel> viewAllProductList) {
        this.type = type;
        this.title = title;
        this.backgroudColor = backgroudColor;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.viewAllProductList = viewAllProductList;
    }

    public HomePageModel(int type, String title, String backgroudColor, List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.type = type;
        this.title = title;
        this.backgroudColor = backgroudColor;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    public List<WishListModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<WishListModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }

    public String getBackgroudColor() {
        return backgroudColor;
    }

    public void setBackgroudColor(String backgroudColor) {
        this.backgroudColor = backgroudColor;
    }

    public int getType() {
        return type;
    }

    // Banner Slider

    // horizontal product layout

    public void setType(int type) {
        this.type = type;
    }

    public List<SliderModel> getSliderModelList() {
        return sliderModelList;
    }

    public void setSliderModelList(List<SliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HorizontalProductScrollModel> getHorizontalProductScrollModelList() {
        return horizontalProductScrollModelList;
    }

    public void setHorizontalProductScrollModelList(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    // horizontal product layout
}
