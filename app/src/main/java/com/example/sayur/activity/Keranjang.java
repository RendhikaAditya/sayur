package com.example.sayur.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.sayur.InterfaceCount.OnUpdateHarga;
import com.example.sayur.MainActivity;
import com.example.sayur.PrefManager;
import com.example.sayur.R;
import com.example.sayur.ServerApi;
import com.example.sayur.adapter.KeranjangAdapter;
import com.example.sayur.adapter.ProdukAdapter;
import com.example.sayur.model.Keranjang_model;
import com.example.sayur.model.Produk_model;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Keranjang extends AppCompatActivity implements OnUpdateHarga {

    Button belanja;
    RecyclerView rv_produk, rv_keranjang ;
    private List<Produk_model> produk;
    private List<Keranjang_model>keranjang;

    TextView TotalView;
    String hasil;
    int total, harga, jumlah;
    private PrefManager prefManager;
    ImageView back;

    RelativeLayout layoutkoneksi;
    LinearLayout layoutkeranjang, cartKosong, check, btnCheckout;
    ShimmerFrameLayout keranjangshimmer;
    SwipeRefreshLayout swKeranjang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);
        AndroidNetworking.initialize(this);
        prefManager = new PrefManager(this);
        total = 0;

        back = findViewById(R.id.backKeranjang);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        check = findViewById(R.id.checkoutlayout);
        check.setVisibility(View.GONE);
        cartKosong = findViewById(R.id.layout_cart_kosong);
        layoutkeranjang = findViewById(R.id.layout_keranjang);
        layoutkoneksi = findViewById(R.id.layout_koneksi);
        keranjangshimmer = findViewById(R.id.shimmer_keranjang);
        TotalView = findViewById(R.id.totalKeranjang);
        swKeranjang = findViewById(R.id.sw_keranjang);
        swKeranjang.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                produk.clear();
                keranjang.clear();
                getProd();
            }
        });

        btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Keranjang.this, Checkout.class);
                startActivity(intent);
            }
        });

//        totalview();

        belanja = findViewById(R.id.btnbelanja);
        belanja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Keranjang.this, MainActivity.class);
                startActivity(intent);
            }
        });

        rv_keranjang = findViewById(R.id.rv_keranjangitem);
        rv_keranjang.setHasFixedSize(true);
        rv_keranjang.setLayoutManager(new LinearLayoutManager(this));
        keranjang = new ArrayList<>();

        rv_produk = findViewById(R.id.rv_prodKeranjang);
        rv_produk.setHasFixedSize(true);
        rv_produk.setLayoutManager(new GridLayoutManager(this,2));
        produk = new ArrayList<>();
        getProd();
    }

    private void set_loading(){
        layoutkeranjang.setVisibility(View.GONE);
        keranjangshimmer.setVisibility(View.VISIBLE);
        layoutkoneksi.setVisibility(View.GONE);
        keranjangshimmer.startShimmer();
    }

    private void load_fail(){
        swKeranjang.setRefreshing(false);
        layoutkeranjang.setVisibility(View.GONE);
        keranjangshimmer.setVisibility(View.GONE);
        layoutkoneksi.setVisibility(View.VISIBLE);
        keranjangshimmer.stopShimmer();
    }
    private void load_success(){
        swKeranjang.setRefreshing(false);
        layoutkeranjang.setVisibility(View.VISIBLE);
        keranjangshimmer.setVisibility(View.GONE);
        layoutkoneksi.setVisibility(View.GONE);
        keranjangshimmer.stopShimmer();
    }
    private void getProd() {
        set_loading();
        AndroidNetworking.post(ServerApi.site_url+"get_keranjang.php")
                .addBodyParameter("idc", prefManager.getIdUser())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length()>=1){
                            cartKosong.setVisibility(View.GONE);
                            check.setVisibility(View.VISIBLE);
                            try {
                                total = 0;
                                for (int i=0; i<response.length(); i++){
                                    JSONObject item = response.getJSONObject(i);
                                    keranjang.add(new Keranjang_model(
                                            item.getString("id"),
                                            item.getString("nama"),
                                            item.getString("satuan"),
                                            item.getInt("harga"),
                                            item.getInt("jumlah"),
                                            item.getInt("total"),
                                            item.getInt("stok"),
                                            item.getString("gambar")
                                    ));
                                    harga = item.getInt("harga");
                                    jumlah = item.getInt("jumlah");
                                    Locale localeId = new Locale("in", "ID");
                                    final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeId);
                                    total = (harga*jumlah) + total;
                                    TotalView.setText(formatRupiah.format(total));
                                }
                                setAdapter();
                                load_success();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            cartKosong.setVisibility(View.VISIBLE);
                            check.setVisibility(View.GONE);
                            layoutkoneksi.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        load_fail();
                        Log.d("error Kranjang ", "error :"+anError);
                    }
                });

        AndroidNetworking.get(ServerApi.site_url+"produk.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Sukses prod", "onResponse"+response);
                        try {
                            produk.clear();
                            for (int i = 0; i<response.length();i++) {
                                JSONObject prod = response.getJSONObject(i);
                                produk.add(new Produk_model(
                                        prod.getInt("id"),
                                        prod.getString("nama"),
                                        prod.getInt("harga"),
                                        prod.getString("satuan"),
                                        prod.getInt("stok"),
                                        prod.getString("gambar")
                                ));
                            }
                            ProdukAdapter produkAdapter = new ProdukAdapter(produk);
                            rv_produk.setAdapter(produkAdapter);
                            produkAdapter.notifyDataSetChanged();
                            load_success();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        load_fail();
                        Log.d("error prod", "onError : "+anError);
                    }
                });


    }

    private void setAdapter() {
        KeranjangAdapter keranjangAdapter = new KeranjangAdapter(keranjang, this);
        rv_keranjang.setAdapter(keranjangAdapter);
        keranjangAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUpdateHarga() {
//        produk.clear();
//        keranjang.clear();
//        getProd();
        AndroidNetworking.post(ServerApi.site_url+"get_keranjang.php")
                .addBodyParameter("idc", prefManager.getIdUser())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length()>=1){
                            cartKosong.setVisibility(View.GONE);
                            check.setVisibility(View.VISIBLE);
                            try {
                                total = 0;
                                for (int i=0; i<response.length(); i++){
                                    JSONObject item = response.getJSONObject(i);
                                    harga = item.getInt("harga");
                                    jumlah = item.getInt("jumlah");
                                    Locale localeId = new Locale("in", "ID");
                                    final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeId);
                                    total = (harga*jumlah) + total;
                                    TotalView.setText(formatRupiah.format(total));
                                }
                                load_success();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            cartKosong.setVisibility(View.VISIBLE);
                            check.setVisibility(View.GONE);
                            layoutkoneksi.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        load_fail();
                    }
                });
    }

//    private void totalview() {
//        AndroidNetworking.post(ServerApi.site_url+"keranjang_total.php")
//                .addBodyParameter("id", prefManager.getIdUser())
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            hasil = response.getString("total");
////                            TotalView.setText(hasil);
////                            Locale localeId = new Locale("in", "ID");
////                            final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeId);
//
//                            load_success();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//
//                    }
//                });
//    }
}
