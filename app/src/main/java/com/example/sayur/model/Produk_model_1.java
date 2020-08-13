package com.example.sayur.model;

public class Produk_model_1 {
    private int id;
    private String nama;
    private int harga;
    private String satuan;
    private int stok;
    private String image;

    public Produk_model_1(int id, String nama, int harga, String satuan, int stok, String image) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.satuan = satuan;
        this.stok = stok;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
