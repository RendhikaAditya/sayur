package com.example.sayur.model;

public class Keranjang_model {
    private String idk;
    private String nama;
    private String satuan;
    private int harga;
    private int jumlah;
    private int total;
    private int stok;
    private String foto;

    public Keranjang_model(String idk, String nama, String satuan, int harga, int jumlah, int total, int stok, String foto) {
        this.idk = idk;
        this.nama = nama;
        this.satuan = satuan;
        this.harga = harga;
        this.jumlah = jumlah;
        this.total = total;
        this.stok = stok;
        this.foto = foto;
    }

    public String getIdk() {
        return idk;
    }

    public void setIdk(String idk) {
        this.idk = idk;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
