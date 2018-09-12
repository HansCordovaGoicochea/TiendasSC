package com.scientechperu.tiendassc.Clases;

import com.orm.SugarRecord;

public class Carroprincipal extends SugarRecord{
    private String id_cart;
    private Integer id_shop;

    public Carroprincipal() {
    }

    public Carroprincipal(String id_cart, Integer id_shop) {
        this.id_cart = id_cart;
        this.id_shop = id_shop;
    }

    public String getId_cart() {
        return id_cart;
    }

    public void setId_cart(String id_cart) {
        this.id_cart = id_cart;
    }

    public Integer getId_shop() {
        return id_shop;
    }

    public void setId_shop(Integer id_shop) {
        this.id_shop = id_shop;
    }
}
