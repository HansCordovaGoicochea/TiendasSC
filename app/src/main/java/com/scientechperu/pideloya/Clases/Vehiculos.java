package com.scientechperu.pideloya.Clases;

import com.orm.SugarRecord;

public class Vehiculos extends SugarRecord {

    private String idcustomer;
    private String placa;
    private String modelo;
    private String marca;
    private String tipo_vehiculo;
    private String fecha_adquisicion;
    private String color;
    private String tipo_combustible;
    private String id_shop;
    private boolean estado;

    public Vehiculos() {
    }

    public Vehiculos(String idcustomer, String placa, String modelo, String marca, String tipo_vehiculo, String fecha_adquisicion, String color, String tipo_combustible, String id_shop, boolean estado) {
        this.idcustomer = idcustomer;
        this.placa = placa;
        this.modelo = modelo;
        this.marca = marca;
        this.tipo_vehiculo = tipo_vehiculo;
        this.fecha_adquisicion = fecha_adquisicion;
        this.color = color;
        this.tipo_combustible = tipo_combustible;
        this.id_shop = id_shop;
        this.estado = estado;
    }

    public String getIdcustomer() {
        return idcustomer;
    }

    public void setIdcustomer(String idcustomer) {
        this.idcustomer = idcustomer;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getTipo_vehiculo() {
        return tipo_vehiculo;
    }

    public void setTipo_vehiculo(String tipo_vehiculo) {
        this.tipo_vehiculo = tipo_vehiculo;
    }

    public String getFecha_adquisicion() {
        return fecha_adquisicion;
    }

    public void setFecha_adquisicion(String fecha_adquisicion) {
        this.fecha_adquisicion = fecha_adquisicion;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTipo_combustible() {
        return tipo_combustible;
    }

    public void setTipo_combustible(String tipo_combustible) {
        this.tipo_combustible = tipo_combustible;
    }

    public String getId_shop() {
        return id_shop;
    }

    public void setId_shop(String id_shop) {
        this.id_shop = id_shop;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
