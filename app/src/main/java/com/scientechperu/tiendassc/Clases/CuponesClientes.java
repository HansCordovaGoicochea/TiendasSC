package com.scientechperu.tiendassc.Clases;


public class CuponesClientes{

    private String id_cupon_cliente;
    private String id_shop;
    private String id_cliente;
    private String id_producto;
    private String acumulado;
    private String terminos_condiciones;
    private String nombre_producto;
    private String id_imagen_default;
    private String date_add;
    private String date_upd;
    private String id_caja;

    public CuponesClientes() {
    }

    public CuponesClientes(String id_cupon_cliente, String id_shop, String id_cliente, String id_producto, String acumulado, String terminos_condiciones, String nombre_producto, String id_imagen_default, String date_add, String date_upd, String id_caja) {
        this.id_cupon_cliente = id_cupon_cliente;
        this.id_shop = id_shop;
        this.id_cliente = id_cliente;
        this.id_producto = id_producto;
        this.acumulado = acumulado;
        this.terminos_condiciones = terminos_condiciones;
        this.nombre_producto = nombre_producto;
        this.id_imagen_default = id_imagen_default;
        this.date_add = date_add;
        this.date_upd = date_upd;
        this.id_caja = id_caja;
    }


    public String getId_cupon_cliente() {
        return id_cupon_cliente;
    }

    public void setId_cupon_cliente(String id_cupon_cliente) {
        this.id_cupon_cliente = id_cupon_cliente;
    }

    public String getId_shop() {
        return id_shop;
    }

    public void setId_shop(String id_shop) {
        this.id_shop = id_shop;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public String getAcumulado() {
        return acumulado;
    }

    public void setAcumulado(String acumulado) {
        this.acumulado = acumulado;
    }

    public String getTerminos_condiciones() {
        return terminos_condiciones;
    }

    public void setTerminos_condiciones(String terminos_condiciones) {
        this.terminos_condiciones = terminos_condiciones;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public String getId_imagen_default() {
        return id_imagen_default;
    }

    public void setId_imagen_default(String id_imagen_default) {
        this.id_imagen_default = id_imagen_default;
    }

    public String getDate_add() {
        return date_add;
    }

    public void setDate_add(String date_add) {
        this.date_add = date_add;
    }

    public String getDate_upd() {
        return date_upd;
    }

    public void setDate_upd(String date_upd) {
        this.date_upd = date_upd;
    }

    public String getId_caja() {
        return id_caja;
    }

    public void setId_caja(String id_caja) {
        this.id_caja = id_caja;
    }
}
