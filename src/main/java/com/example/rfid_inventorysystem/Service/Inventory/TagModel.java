package com.example.rfid_inventorysystem.Service.Inventory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TagModel {
    String EPC, productName, suppName, responsible;
    Integer id, price;
    boolean permission;
    String date;

    public TagModel(String EPC, String productName, String suppName, String responsible, Integer id, Integer price, boolean permission, String date) {
        this.EPC = EPC;
        this.productName = productName;
        this.suppName = suppName;
        this.responsible = responsible;
        this.id = id;
        this.price = price;
        this.permission = permission;
        this.date = date;
    }
    public TagModel(){}

    public TagModel(Integer id, String epc, String productName, String suppName, String responsible, Integer price) {
        this.id = id;
        this.EPC = epc;
        this.productName = productName;
        this.suppName = suppName;
        this.responsible = responsible;
        this.price = price;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        this.date = dtf.format(LocalDateTime.now());
    }

    public String getEPC() {
        return EPC;
    }

    public void setEPC(String EPC) {
        this.EPC = EPC;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSuppName() {
        return suppName;
    }

    public void setSuppName(String suppName) {
        this.suppName = suppName;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public boolean isPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
