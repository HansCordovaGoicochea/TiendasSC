package com.scientechperu.pideloya.Clases;

import com.orm.SugarRecord;

public class Carro extends SugarRecord {

    private String id_cart;
    private String nombre_producto;
    private Double precio_producto;
    private String id_producto; //necesarios para el post
    private Integer cantidad; //necesarios para el post
    private Integer id_image;
    private Double importe;
    private Integer id_shop;
    private Integer id_caja;

    public Carro() {
    }

    public Carro(String id_cart, String nombre_producto, Double precio_producto, String id_producto, Integer cantidad, Integer id_image, Double importe, Integer id_shop, Integer id_caja) {
        this.id_cart = id_cart;
        this.nombre_producto = nombre_producto;
        this.precio_producto = precio_producto;
        this.id_producto = id_producto;
        this.cantidad = cantidad;
        this.id_image = id_image;
        this.importe = importe;
        this.id_shop = id_shop;
        this.id_caja = id_caja;
    }

    public String getId_cart() {
        return id_cart;
    }

    public void setId_cart(String id_cart) {
        this.id_cart = id_cart;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public Double getPrecio_producto() {
        return precio_producto;
    }

    public void setPrecio_producto(Double precio_producto) {
        this.precio_producto = precio_producto;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getId_image() {
        return id_image;
    }

    public void setId_image(Integer id_image) {
        this.id_image = id_image;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public Integer getId_shop() {
        return id_shop;
    }

    public void setId_shop(Integer id_shop) {
        this.id_shop = id_shop;
    }

    public Integer getId_caja() {
        return id_caja;
    }

    public void setId_caja(Integer id_caja) {
        this.id_caja = id_caja;
    }
}
