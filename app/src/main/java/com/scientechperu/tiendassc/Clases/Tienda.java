package com.scientechperu.tiendassc.Clases;

import com.orm.SugarRecord;

public class Tienda extends SugarRecord {

    private Integer id_store;
    private Integer id_shop;
    private String nombre;
    private String razon_social;
    private String ruc;
    private String virtual_uri;
    private String domain;
    private String latitud;
    private String longitud;
    private Integer id_imagen;
    private String direccion;
    private String horarios;
    private Integer id_caja;
    private String telefono;
    private String categoria;
    private String celular_whatsapp;



    public Tienda() {
    }

    public Tienda(Integer id_store, Integer id_shop, String nombre, String razon_social, String ruc, String virtual_uri, String domain,
                  String latitud, String longitud, Integer id_imagen, String direccion, String horarios, Integer id_caja,
                  String telefono, String categoria, String celular_whatsapp) {
        this.id_store = id_store;
        this.id_shop = id_shop;
        this.nombre = nombre;
        this.razon_social = razon_social;
        this.ruc = ruc;
        this.virtual_uri = virtual_uri;
        this.domain = domain;
        this.latitud = latitud;
        this.longitud = longitud;
        this.id_imagen = id_imagen;
        this.direccion = direccion;
        this.horarios = horarios;
        this.id_caja = id_caja;
        this.telefono = telefono;
        this.categoria = categoria;
        this.celular_whatsapp = celular_whatsapp;
    }

    public Integer getId_store() {
        return id_store;
    }

    public void setId_store(Integer id_store) {
        this.id_store = id_store;
    }

    public Integer getId_shop() {
        return id_shop;
    }

    public void setId_shop(Integer id_shop) {
        this.id_shop = id_shop;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getVirtual_uri() {
        return virtual_uri;
    }

    public void setVirtual_uri(String virtual_uri) {
        this.virtual_uri = virtual_uri;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public Integer getId_imagen() {
        return id_imagen;
    }

    public void setId_imagen(Integer id_imagen) {
        this.id_imagen = id_imagen;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getHorarios() {
        return horarios;
    }

    public void setHorarios(String horarios) {
        this.horarios = horarios;
    }

    public Integer getId_caja() {
        return id_caja;
    }

    public void setId_caja(Integer id_caja) {
        this.id_caja = id_caja;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }


    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCelular_whatsapp() {
        return celular_whatsapp;
    }

    public void setCelular_whatsapp(String celular_whatsapp) {
        this.celular_whatsapp = celular_whatsapp;
    }
}
