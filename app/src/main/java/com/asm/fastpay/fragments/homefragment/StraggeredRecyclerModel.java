package com.asm.fastpay.fragments.homefragment;

public class StraggeredRecyclerModel {

    private String productID;
    private String productImage;
    private String productTitle;
    private String productSub;
    private String productPrice;

    public StraggeredRecyclerModel(String productID, String productImage, String productTitle, String productSub, String productPrice) {
        this.productID = productID;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productSub = productSub;
        this.productPrice = productPrice;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductSub() {
        return productSub;
    }

    public void setProductSub(String productSub) {
        this.productSub = productSub;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
