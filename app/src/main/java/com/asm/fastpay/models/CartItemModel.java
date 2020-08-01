package com.asm.fastpay.models;

import java.util.ArrayList;
import java.util.List;

public class CartItemModel {
    private int type;
    private String productID;
    private String productImgae;
    private String productTitle;
    private long freeCoupons;
    private String productPrice;
    private String cuttedPrice;
    private long productQuantity;
    private long maxQuantity;
    private long stockQuantity;
    private long offerApplied;
    private long couponsApplied;
    private boolean inStock;
    private List<String> qtyIDs;
    private boolean qtyError;
    private boolean COD;

    public boolean isCOD() {
        return COD;
    }

    public void setCOD(boolean COD) {
        this.COD = COD;
    }

    public static final int CART_ITEM = 0;
    public static final int TOTAL_AMOUNT = 1;


    public CartItemModel(boolean COD,int type, String productID, String productImgae, String productTitle, long freeCoupons, String productPrice, String cuttedPrice, long productQuantity, long offerApplied, long couponsApplied, boolean inStock, long maxQuantity,long stockQuantity) {
        this.COD = COD;
        this.type = type;
        this.productID = productID;
        this.productImgae = productImgae;
        this.productTitle = productTitle;
        this.freeCoupons = freeCoupons;
        this.productPrice = productPrice;
        this.cuttedPrice = cuttedPrice;
        this.productQuantity = productQuantity;
        this.offerApplied = offerApplied;
        this.couponsApplied = couponsApplied;
        this.inStock = inStock;
        this.maxQuantity = maxQuantity;
        this.stockQuantity = stockQuantity;
        qtyIDs = new ArrayList<>();
        qtyError = false;
    }

    public boolean isQtyError() {
        return qtyError;
    }

    public void setQtyError(boolean qtyError) {
        this.qtyError = qtyError;
    }

    public long getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(long stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public List<String> getQtyIDs() {
        return qtyIDs;
    }

    public void setQtyIDs(List<String> qtyIDs) {
        this.qtyIDs = qtyIDs;
    }

    public long getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(long maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImgae() {
        return productImgae;
    }

    public void setProductImgae(String productImgae) {
        this.productImgae = productImgae;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public long getFreeCoupons() {
        return freeCoupons;
    }

    public void setFreeCoupons(long freeCoupons) {
        this.freeCoupons = freeCoupons;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }

    public long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public long getOfferApplied() {
        return offerApplied;
    }

    public void setOfferApplied(long offerApplied) {
        this.offerApplied = offerApplied;
    }

    public long getCouponsApplied() {
        return couponsApplied;
    }

    public void setCouponsApplied(long couponsApplied) {
        this.couponsApplied = couponsApplied;
    }

    // cart total

    private int totalItems, totalItemPrice,totalAmount,savedAmount;
    private String deliveryPrice;

    public CartItemModel(int type) {
        this.type = type;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalItemPrice() {
        return totalItemPrice;
    }

    public void setTotalItemPrice(int totalItemPrice) {
        this.totalItemPrice = totalItemPrice;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(int savedAmount) {
        this.savedAmount = savedAmount;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }
}
