package com.example.sayur.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.sayur.MainActivity;
import com.example.sayur.PrefManager;
import com.example.sayur.R;
import com.example.sayur.ServerApi;
import com.example.sayur.adapter.CheckoutAdapter;
import com.example.sayur.adapter.KeranjangAdapter;
import com.example.sayur.model.Keranjang_model;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;

public class Checkout extends AppCompatActivity {
TextView alamat, nama, total, brang, ongkir, bayar, title;
LinearLayout layoutAlamat, belanjaLagi, pesan;
RadioGroup rgCaraBayar;
    RadioButton radioButton;
private PrefManager prefManager;
ImageView back;
int harga, jumlah;
RecyclerView rv_checkout;
String a = "a";
    int totall = 0;
    int Byar = 0;
    SwipeRefreshLayout swipeRefreshLayout;
RelativeLayout koneksi;
RelativeLayout layoutCheckout;
ShimmerFrameLayout shimmerCheck;

    private List<Keranjang_model> keranjang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        AndroidNetworking.initialize(this);

        back =findViewById(R.id.backCheck);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ongkir = findViewById(R.id.ongkosKirim);
        bayar =findViewById(R.id.totalBayar);

        shimmerCheck = findViewById(R.id.shimmer_checkout);
        layoutCheckout = findViewById(R.id.layout_checkout);
        koneksi = findViewById(R.id.layout_koneksi);
        swipeRefreshLayout = findViewById(R.id.sw_checkout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAlamat();
            }
        });



        rgCaraBayar = findViewById(R.id.RdgChek);

        pesan = findViewById(R.id.buatPesanan);
        pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    simpanPesanan();

            }
        });

        brang = findViewById(R.id.brgChek);
        total = findViewById(R.id.totalCheck);
        belanjaLagi = findViewById(R.id.belanjalagi);
        belanjaLagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Checkout.this, MainActivity.class);
                startActivity(intent);
            }
        });
        rv_checkout = findViewById(R.id.rv_checkout);
        rv_checkout.setHasFixedSize(true);
        rv_checkout.setLayoutManager(new LinearLayoutManager(this));
        keranjang = new ArrayList<>();

        alamat = findViewById(R.id.AlamatCheckout);
        nama = findViewById(R.id.namaOrang);
        getAlamat();
        layoutAlamat = findViewById(R.id.alamatLayout);
        layoutAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Checkout.this, AlamatAdd.class);
                intent.putExtra("B", "H");
                startActivity(intent);
            }
        });
    }

    private void simpanPesanan() {
        int selectedID = rgCaraBayar.getCheckedRadioButtonId();
        radioButton = findViewById(selectedID);
        prefManager = new PrefManager(this);
        if (rgCaraBayar.getCheckedRadioButtonId()==-1){
            Toast.makeText(getApplicationContext(), "Pilih Metode Pembayaran", Toast.LENGTH_SHORT).show();
        }else {
            AndroidNetworking.post(ServerApi.site_url + "pesan.php")
                    .addBodyParameter("id", prefManager.getIdUser())
                    .addBodyParameter("total", String.valueOf(Byar))
                    .addBodyParameter("metode", radioButton.getText().toString())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("Sukses check", "onResponse"+response);
                                String code = response.getString("cod");
                                String ido = response.getString("ido");
                                if (code.equalsIgnoreCase("1")) {
                                    Intent intent = new Intent(Checkout.this, Konfirmasi.class);
                                    if (radioButton.getText().toString().equalsIgnoreCase("COD")){
                                        intent.putExtra("KONFIRM", 2);
                                    }else {
                                        intent.putExtra("KONFIRM", 1);
                                    }
                                    intent.putExtra("IDO", ido);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), response.getString("response"), Toast.LENGTH_SHORT).show();
                                    shDialog();
                                } else {
                                    Toast.makeText(getApplicationContext(), response.getString("response"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("Error check", "onResponse"+anError);
                        }
                    });
        }
    }

    private void shDialog() {
//        new AlertDialog.Builder(this)
//                .setTitle("Pesanan Berhasil")
//                .setMessage("Lanjut belanja ?")
//                .setCancelable(false)
//                .setPositiveButton("Belanja", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Intent intent = new Intent(Checkout.this, MainActivity.class);
//                        startActivity(intent);
//                    }
//                })
//                .show();


    }

    private void set_error() {
        shimmerCheck.stopShimmer();
        shimmerCheck.setVisibility(View.GONE);
        layoutCheckout.setVisibility(View.GONE);
        koneksi.setVisibility(View.VISIBLE);
//        swipeRefreshLayout.setRefreshing(false);
    }

    private void set_sukses() {
        shimmerCheck.stopShimmer();
        shimmerCheck.setVisibility(View.GONE);
        layoutCheckout.setVisibility(View.VISIBLE);
        koneksi.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void set_loading() {
        shimmerCheck.startShimmer();
        shimmerCheck.setVisibility(View.VISIBLE);
        layoutCheckout.setVisibility(View.GONE);
        koneksi.setVisibility(View.GONE);
//        swipeRefreshLayout.setRefreshing(true);
    }


    private void getAlamat() {
        set_loading();
        totall=0;
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
                        if (response.length()>0) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    String A = object.getString("alamat");
                                    String N = object.getString("nama");
                                    alamat.setText(A);
                                    nama.setText(N);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("catch kat", "onResponse" + response);
                                }
                            }
                        }else {
                            Intent intent = new Intent(Checkout.this, AlamatAdd.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error kat", "onResponse" + anError);
                        Intent intent = new Intent(Checkout.this, AlamatAdd.class);
                        startActivity(intent);
                    }
                });

        AndroidNetworking.post(ServerApi.site_url+"get_keranjang.php")
                .addBodyParameter("idc", prefManager.getIdUser())
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
//                                total = 0;
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
                                    set_sukses();
//                                    harga = item.getInt("harga");
//                                    jumlah = item.getInt("jumlah");
                                    Locale localeId = new Locale("in", "ID");
                                    final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeId);
//                                    total = (harga*jumlah) + total;
//                                    TotalView.setText(formatRupiah.format(total));
                                }
                                CheckoutAdapter checkoutAdapter = new CheckoutAdapter(keranjang);
                                rv_checkout.setAdapter(checkoutAdapter);
                                checkoutAdapter.notifyDataSetChanged();
//                                load_success();
                            } catch (JSONException e) {
                                e.printStackTrace();
//                                load_fail();
                            }
                        }else{
//                            cartKosong.setVisibility(View.VISIBLE);
//                            check.setVisibility(View.GONE);
                            set_error();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
//                        load_fail();
                        set_error();
                    }
                });

        AndroidNetworking.post(ServerApi.site_url+"get_keranjang.php")
                .addBodyParameter("idc", prefManager.getIdUser())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length()>=1){
//                            cartKosong.setVisibility(View.GONE);
//                            check.setVisibility(View.VISIBLE);
                            try {

                                int brg=0;
                                Byar=0;
                                for (int i=0; i<response.length(); i++){
                                    JSONObject item = response.getJSONObject(i);
                                    harga = item.getInt("harga");
                                    jumlah = item.getInt("jumlah");
                                    Locale localeId = new Locale("in", "ID");
                                    final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeId);
                                    brg = jumlah+brg;
                                    totall = (harga*jumlah) + totall;
                                    Byar=totall+7000;
                                    ongkir.setText(formatRupiah.format(7000));
                                    bayar.setText(formatRupiah.format(Byar));
                                    total.setText(formatRupiah.format(totall));
                                    brang.setText(String.valueOf(brg));
                                    set_sukses();
                                }
//                                load_success();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
//                            cartKosong.setVisibility(View.VISIBLE);
//                            check.setVisibility(View.GONE);
                            set_error();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
//                        load_fail();
                        set_error();
                    }
                });
    }
}
