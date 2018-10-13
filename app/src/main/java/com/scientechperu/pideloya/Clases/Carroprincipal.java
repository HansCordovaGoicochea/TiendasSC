package com.scientechperu.pideloya.Clases;

import com.orm.SugarRecord;

public class Carroprincipal extends SugarRecord{
    private String codigounico;
    private String id_cart;
    private Integer id_shop;
    private Integer id_cliente;
    private String nro_mesa;
    private Integer id_caja;

    public Carroprincipal() {
    }

    public Carroprincipal(String codigounico, String id_cart, Integer id_shop, Integer id_cliente, String nro_mesa, Integer id_caja) {
        this.codigounico = codigounico;
        this.id_cart = id_cart;
        this.id_shop = id_shop;
        this.id_cliente = id_cliente;
        this.nro_mesa = nro_mesa;
        this.id_caja = id_caja;
    }

    public String getCodigounico() {
        return codigounico;
    }

    public void setCodigounico(String codigounico) {
        this.codigounico = codigounico;
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

    public Integer getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Integer id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getNro_mesa() {
        return nro_mesa;
    }

    public void setNro_mesa(String nro_mesa) {
        this.nro_mesa = nro_mesa;
    }

    public Integer getId_caja() {
        return id_caja;
    }

    public void setId_caja(Integer id_caja) {
        this.id_caja = id_caja;
    }
}
