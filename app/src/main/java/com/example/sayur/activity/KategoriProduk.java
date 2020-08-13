package com.example.sayur.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.sayur.R;
import com.example.sayur.ServerApi;
import com.example.sayur.adapter.ProdukAdapter;
import com.example.sayur.model.Produk_model;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KategoriProduk extends AppCompatActivity {

    TextView nama;
    RecyclerView rv_katproduk;
    String kat;
    ImageView back;
    private List<Produk_model>produk;
    private SwipeRefreshLayout swipeRefreshLayout;

    RelativeLayout layout_koneksi, layout_kosong;
    ShimmerFrameLayout detail_shimmer;
    NestedScrollView detail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori_produk);
        AndroidNetworking.initialize(this);

        back = findViewById(R.id.backProdKat);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        detail_shimmer = findViewById(R.id.shimmerdetail);

        detail = findViewById(R.id.layout_produk);
        layout_koneksi = findViewById(R.id.layout_koneksi);
        layout_koneksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        layout_kosong =findViewById(R.id.layout_kosong);
        layout_kosong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                produk.clear();
                getData();
            }
        });

        rv_katproduk = findViewById(R.id.rv_prodKategori);
        rv_katproduk.setHasFixedSize(true);
        rv_katproduk.setLayoutManager(new GridLayoutManager(this,2));
        produk = new ArrayList<>();
        nama = findViewById(R.id.namakategori);
        getData();
        swipeRefreshLayout = findViewById(R.id.sw_katproduk);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                produk.clear();
                getData();
            }
        });

    }

    private void set_loading(){
        detail.setVisibility(View.VISIBLE);
        detail_shimmer.setVisibility(View.VISIBLE);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);
//        beranda_content.setVisibility(View.GONE);
        detail_shimmer.startShimmer();
    }

    private void load_fail() {
        swipeRefreshLayout.setRefreshing(false);
        detail.setVisibility(View.GONE);
        layout_koneksi.setVisibility(View.VISIBLE);
        layout_kosong.setVisibility(View.GONE);
        detail_shimmer.setVisibility(View.GONE);
//        beranda_content.setVisibility(View.GONE);
        detail_shimmer.stopShimmer();

    }

    private void load_kosong(){
        swipeRefreshLayout.setRefreshing(false);
        detail.setVisibility(View.GONE);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.VISIBLE);
        detail_shimmer.setVisibility(View.GONE);
//        beranda_content.setVisibility(View.GONE);
        detail_shimmer.stopShimmer();

    }

    private void load_success() {
        swipeRefreshLayout.setRefreshing(false);
        detail.setVisibility(View.VISIBLE);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);
        detail_shimmer.setVisibility(View.GONE);
//        beranda_content.setVisibility(View.GONE);
        detail_shimmer.stopShimmer();
    }



    private void getData() {
        set_loading();
        Intent intent = getIntent();
        String id = intent.getStringExtra("IDK");
        if (id.equalsIgnoreCase("cari")){
            Intent intent1 = getIntent();
            String idc= intent1.getStringExtra("CARI");
            AndroidNetworking.post(ServerApi.site_url+"produk_kategori.php")
                    .addBodyParameter("id_kat", "cari")
                    .addBodyParameter("cari", idc)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                if (response.length()>=1){
                                    for (int i=0; i<response.length(); i++){

                                        JSONObject prod = response.getJSONObject(i);
                                        kat = prod.getString("kategori");
                                        nama.setText(kat);
                                        produk.add(new Produk_model(
                                                prod.getInt("id"),
                                                prod.getString("nama"),
                                                prod.getInt("harga"),
                                                prod.getString("satuan"),
                                                prod.getInt("stok"),
                                                prod.getString("gambar")
                                        ));
                                        ProdukAdapter produkAdapter = new ProdukAdapter(produk);
                                        rv_katproduk.setAdapter(produkAdapter);
                                        produkAdapter.notifyDataSetChanged();

                                        load_success();
                                    }
                                }else {
                                    load_kosong();
                                }} catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("eroor cari", "error"+anError);
                            load_fail();
                        }
                    });

        }else if(id.equalsIgnoreCase("all")){

            AndroidNetworking.post(ServerApi.site_url+"produk_kategori.php")
                    .addBodyParameter("id_kat", id)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                if (response.getJSONObject(0).getString("message").equalsIgnoreCase("ada")){
                                    for (int i=0; i<response.length(); i++){
                                        JSONObject prod = response.getJSONObject(i);
                                        kat = prod.getString("kategori");
                                        nama.setText(kat);
                                        produk.add(new Produk_model(
                                                prod.getInt("id"),
                                                prod.getString("nama"),
                                                prod.getInt("harga"),
                                                prod.getString("satuan"),
                                                prod.getInt("stok"),
                                                prod.getString("gambar")
                                        ));
                                        ProdukAdapter produkAdapter = new ProdukAdapter(produk);
                                        rv_katproduk.setAdapter(produkAdapter);
                                        produkAdapter.notifyDataSetChanged();
                                        load_success();
                                        Log.d("eroor ada", "error"+response);
                                    }
                                }else {
                                    load_kosong();
                                }} catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("eroor bukan", "error"+anError);
                            load_fail();
                        }
                    });

        }else{

            AndroidNetworking.post(ServerApi.site_url+"produk_kategori.php")
                    .addBodyParameter("id_kat", id)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                if(response.getJSONObject(0).getString("message").equalsIgnoreCase("ada")){

                                    for (int i=0; i<response.length(); i++){
                                        JSONObject prod = response.getJSONObject(i);
                                        kat = prod.getString("kategori");
                                        nama.setText(kat);
                                        produk.add(new Produk_model(
                                                prod.getInt("id"),
                                                prod.getString("nama"),
                                                prod.getInt("harga"),
                                                prod.getString("satuan"),
                                                prod.getInt("stok"),
                                                prod.getString("gambar")
                                        ));
                                        ProdukAdapter produkAdapter = new ProdukAdapter(produk);
                                        rv_katproduk.setAdapter(produkAdapter);
                                        produkAdapter.notifyDataSetChanged();

                                        load_success();

                                    }}
                                else {
                                        load_kosong();
                                    }

                                } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("eroor bukan", "error"+anError);
                            load_fail();
                        }
                    });

        }

    }
}
