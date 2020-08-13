package com.example.sayur.model;

public class Kategori_model {
    private String id;
    private String nama;
    private String icon;

    public String  getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Kategori_model(String  id, String nama, String icon) {
        this.id = id;
        this.nama = nama;
        this.icon = icon;
    }
}
