package com.scientechperu.pideloya.Clases;
import com.orm.SugarRecord;

public class CategoriaTienda extends SugarRecord {
    private Integer id_categoria_tienda;
    private String nombre;
    private String tiendas;

    public CategoriaTienda() {
    }

    public CategoriaTienda(Integer id_categoria_tienda, String nombre, String tiendas) {
        this.id_categoria_tienda = id_categoria_tienda;
        this.nombre = nombre;
        this.tiendas = tiendas;
    }

    public Integer getId_categoria_tienda() {
        return id_categoria_tienda;
    }

    public void setId_categoria_tienda(Integer id_categoria_tienda) {
        this.id_categoria_tienda = id_categoria_tienda;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTiendas() {
        return tiendas;
    }

    public void setTiendas(String tiendas) {
        this.tiendas = tiendas;
    }
}
