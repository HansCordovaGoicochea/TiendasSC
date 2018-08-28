package com.scientechperu.tiendassc.Clases;

import java.io.Serializable;

public class Productos implements Serializable{
    private Integer id_product;
    private String name;
    private Integer id_image;
    private String description_short;
    private String description;
    private String price;
    private String id_supplier;
    private String reference;
    private String activo;
    private String available;
    private String precio_con_igv;

    public Productos() {
    }

    public Productos(Integer id_product, String name, Integer id_image, String description_short, String description, Double price,
                     String id_supplier, String reference, String activo, String available, Double precio_con_igv) {
        this.id_product        = id_product;
        this.name              = name;
        this.id_image          = id_image;
        this.description_short = description_short;
        this.description       = description;
        this.price             = ""+price;
        this.id_supplier       = id_supplier+"";
        this.reference         = reference+"";
        this.activo            = activo;
        this.available         = available+"";
        this.precio_con_igv    = ""+precio_con_igv;
    }

    public Integer getId_product() {
        return id_product;
    }

    public void setId_product(Integer id_product) {
        this.id_product = id_product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId_image() {
        return id_image;
    }

    public void setId_image(Integer id_image) {
        this.id_image = id_image;
    }

    public String getDescription_short() {
        return description_short;
    }

    public void setDescription_short(String description_short) {
        this.description_short = description_short;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId_supplier() {
        return id_supplier;
    }

    public void setId_supplier(String id_supplier) {
        this.id_supplier = id_supplier;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getPrecio_con_igv() {
        return precio_con_igv;
    }

    public void setPrecio_con_igv(String precio_con_igv) {
        this.precio_con_igv = precio_con_igv;
    }

}
