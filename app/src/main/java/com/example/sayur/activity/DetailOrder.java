package com.example.sayur.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.example.sayur.PrefManager;
import com.example.sayur.R;
import com.example.sayur.ServerApi;
import com.example.sayur.adapter.CheckoutAdapter;
import com.example.sayur.adapter.OrderAdapter;
import com.example.sayur.model.Keranjang_model;
import com.example.sayur.model.Order_model;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailOrder extends AppCompatActivity {
    TextView alamat, nama, status;
    private PrefManager prefManager;
    RecyclerView rvOrdDetail;
    List<Keranjang_model>keranjang;
    ImageView back;
    TextView hargaO, ongkir, totalO;
    RelativeLayout layout_koneksi;
    ShimmerFrameLayout shimmer;
    NestedScrollView layoout_detail_ord;
    SwipeRefreshLayout swipeRefreshLayout;

    int total;
        int harga, jumlah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);
        AndroidNetworking.initialize(this);

        alamat = findViewById(R.id.AlamatCheckout);
        nama = findViewById(R.id.namaOrang);
        getAlamat();

        swipeRefreshLayout = findViewById(R.id.sw_detail_order);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                keranjang.clear();
                getAlamat();
                getProd();
                getStatus();
            }
        });

        shimmer = findViewById(R.id.shimmerDetailOrder);
        layout_koneksi = findViewById(R.id.layout_koneksi);
        layoout_detail_ord = findViewById(R.id.detOrdLayout);

        hargaO = findViewById(R.id.hargaOrder);
        ongkir = findViewById(R.id.ongkirOrder);
        totalO = findViewById(R.id.totalOrder);

        rvOrdDetail = findViewById(R.id.rv_detailord);
        rvOrdDetail.setHasFixedSize(true);
        rvOrdDetail.setLayoutManager(new LinearLayoutManager(this));
        keranjang = new ArrayList<>();
        getProd();

        status = findViewById(R.id.statusDetail);
        getStatus();

        back = findViewById(R.id.backOrd);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void set_loading(){
        swipeRefreshLayout.setRefreshing(true);
        shimmer.setVisibility(View.VISIBLE);
        layout_koneksi.setVisibility(View.GONE);
        layoout_detail_ord.setVisibility(View.GONE);
        shimmer.startShimmer();
    }

    private void load_fail(){
        swipeRefreshLayout.setRefreshing(false);
        layoout_detail_ord.setVisibility(View.GONE);
        layout_koneksi.setVisibility(View.VISIBLE);
        shimmer.setVisibility(View.GONE);
        shimmer.stopShimmer();
    }
    private void load_success(){
        swipeRefreshLayout.setRefreshing(false);
        layoout_detail_ord.setVisibility(View.VISIBLE);
        layout_koneksi.setVisibility(View.GONE);
        shimmer.setVisibility(View.GONE);
        shimmer.stopShimmer();
    }

    private void getStatus() {
        Intent intent = getIntent();
        final String id = intent.getStringExtra("IDORDER");
//        set_loading();
        AndroidNetworking.post(ServerApi.site_url + "get_status.php")
                .addBodyParameter("idc", id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d("sukses stat", "onResponse"+response);
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject data = response.getJSONObject(i);

                                String stats = data.getString("status");
                                status.setText(stats);
                                if(data.getString("status").equalsIgnoreCase("Pembayaran Tertunda")){
                                    Intent ne = new Intent(DetailOrder.this, Konfirmasi.class);
                                    ne.putExtra("KONFIRM", 1);
                                    ne.putExtra("IDO", data.getString("id"));
                                    startActivity(ne);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error stat", "onResponse"+anError);
                    }
                });
    }

    private void getProd() {
        set_loading();
        Intent intent = getIntent();
        final String id = intent.getStringExtra("IDORDER");
        AndroidNetworking.post(ServerApi.site_url+"detai_order.php")
                .addBodyParameter("ido", id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length()>=1){
//                            cartKosong.setVisibility(View.GONE);
//                            check.setVisibility(View.VISIBLE);
                            keranjang.clear();

                            try {
                                total = 0;
                                Log.d("sukses dOrd", "onResponse" + response);
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

                                    load_success();
                                    harga = item.getInt("harga");
                                    jumlah = item.getInt("jumlah");
                                    Locale localeId = new Locale("in", "ID");
                                    final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeId);
                                    total = (harga*jumlah) + total;
                                    hargaO.setText(formatRupiah.format(total));
                                    ongkir.setText(formatRupiah.format(7000));
                                    totalO.setText(formatRupiah.format(total+7000));
                                }
                                CheckoutAdapter checkoutAdapter = new CheckoutAdapter(keranjang);
                                rvOrdDetail.setAdapter(checkoutAdapter);
                                checkoutAdapter.notifyDataSetChanged();
                                load_success();
                            } catch (JSONException e) {
                                e.printStackTrace();
//                                set_error();
                            }
                        }else{
//                            cartKosong.setVisibility(View.VISIBLE);
//                            check.setVisibility(View.GONE);
                            load_fail();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        load_fail();
//                        set_error();
                        Log.d("error dOrd", "onResponse" + anError);
                    }
                });
    }

    private void getAlamat() {
//        set_loading();
        prefManager = new PrefManager(this);
        AndroidNetworking.post(ServerApi.site_url+"get_alamat.php")
                .addBodyParameter("id", prefManager.getIdUser())
                .addBodyParameter("cod", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("sukses kat", "onResponse" + response);
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                String A = object.getString("alamat");
                                String N = object.getString("nama");
                                alamat.setText(A);
                                nama.setText(N);
                                load_success();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("catch kat", "onResponse" + response);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error kat", "onResponse" + anError);
                        load_fail();
                    }
                });


    }
}
