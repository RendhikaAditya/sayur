package com.example.sayur.model;

public class Order_model {
    private String idOrd;
    private String status;
    private String banyakItem;
    private String namaProd;
    private String jumlah;
    private String gambar;
    private int total;
    private int harga;

    public Order_model(String idOrd, String status, String banyakItem, String namaProd, String jumlah, String gambar, int total, int harga) {
        this.idOrd = idOrd;
        this.status = status;
        this.banyakItem = banyakItem;
        this.namaProd = namaProd;
        this.jumlah = jumlah;
        this.gambar = gambar;
        this.total = total;
        this.harga = harga;
    }

    public String getIdOrd() {
        return idOrd;
    }

    public void setIdOrd(String idOrd) {
        this.idOrd = idOrd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBanyakItem() {
        return banyakItem;
    }

    public void setBanyakItem(String banyakItem) {
        this.banyakItem = banyakItem;
    }

    public String getNamaProd() {
        return namaProd;
    }

    public void setNamaProd(String namaProd) {
        this.namaProd = namaProd;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }
}
