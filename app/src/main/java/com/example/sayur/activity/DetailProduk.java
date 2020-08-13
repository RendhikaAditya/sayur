package com.example.sayur.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.sayur.adapter.ProdukAdapter;
import com.example.sayur.fragment.Home;
import com.example.sayur.fragment.Order;
import com.example.sayur.model.Produk_model;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailProduk extends AppCompatActivity {
RecyclerView rv_relate;
private List<Produk_model>produk;
TextView judul1, keterangan, harga, kategori, satuandetail, jumlah;
ImageView imgproduk, back, keranjang;
String imagedetail;
    String j1;
    String satuan;
    String ket;
    int har;
    String kat;
    int stok;

    RelativeLayout layout_koneksi, layout_kosong;
    ShimmerFrameLayout detail_shimmer;
    LinearLayout detail;
    SwipeRefreshLayout swdetail;

    BottomSheetBehavior sheetBehavior;
    BottomSheetDialog sheetDialog;
    View bottom_sheet;
    CardView btnBeli;

    private PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produk);
        AndroidNetworking.initialize(this);
        prefManager = new PrefManager(this);

        keranjang = findViewById(R.id.detailkeranjang);
        keranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailProduk.this, Keranjang.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        btnBeli = findViewById(R.id.btnbeli);
        btnBeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSheet();
            }
        });

        swdetail =findViewById(R.id.sw_detail);
        swdetail.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        detail_shimmer = findViewById(R.id.shimmerdetail);

        back = findViewById(R.id.backdetailProd);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        detail = findViewById(R.id.detailLayout);
        layout_koneksi = findViewById(R.id.layout_koneksi);
        layout_koneksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        layout_kosong =findViewById(R.id.layout_kosong);

        rv_relate = findViewById(R.id.rv_relate);
        rv_relate.setHasFixedSize(true);
        rv_relate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        produk = new ArrayList<>();

        judul1 = findViewById(R.id.judulProduk);
//        judul2 = findViewById(R.id.judulProduk2);
        keterangan = findViewById(R.id.keterangan);
        harga = findViewById(R.id.hargadetail);
        kategori = findViewById(R.id.detailkategori);
        imgproduk = findViewById(R.id.imagedetail);
        satuandetail = findViewById(R.id.satuandetail);


        getData();
    }
    int jumblahbarang = 1;
    private void btnSheet() {

        View view = getLayoutInflater().inflate(R.layout.bottomsheet_detail, null);

        jumlah = view.findViewById(R.id.numbrjumblah);

        (view.findViewById(R.id.plusjumblah)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jumblahbarang<stok) {
                    jumblahbarang = jumblahbarang + 1;
                    jumlah.setText("" + jumblahbarang);
                }else{
                    jumblahbarang=jumblahbarang+0;
                    jumlah.setText(""+jumblahbarang);
                }
            }
        });
        (view.findViewById(R.id.minusjumblah)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jumblahbarang>=2) {
                    jumblahbarang = jumblahbarang - 1;
                    jumlah.setText("" + jumblahbarang);
                }else {
                    jumblahbarang = jumblahbarang - 0;
                    jumlah.setText("" + jumblahbarang);
                }
            }
        });

        jumlah.setText(""+jumblahbarang);

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        (view.findViewById(R.id.btnTansaksi)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefManager.getIdUser().isEmpty()){
                    Intent intent = new Intent(DetailProduk.this, MainActivity.class);
                    intent.putExtra("LOGIN",1);
                    Toast.makeText(getApplicationContext(), "Login untuk membeli barang", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }else {
                    sentData();
                }

            }
        });

        sheetDialog = new BottomSheetDialog(this);
        sheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        sheetDialog.show();
        sheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sheetDialog = null;
            }
        });
    }

    private void sentData() {
        Intent intent = getIntent();
        final int id = intent.getIntExtra("ID",0);
        AndroidNetworking.post(ServerApi.site_url+"keranjang.php")
                .addBodyParameter("idP", String.valueOf(id))
                .addBodyParameter("idC", prefManager.getIdUser())
                .addBodyParameter("jml", jumlah.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String code = response.getString("code");
                            if (code.equals("1")){
                                Toast.makeText(getApplicationContext(), "Item masuk keranjang", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DetailProduk.this, Keranjang.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(),response.getString("response"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

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
        swdetail.setRefreshing(false);
        detail.setVisibility(View.GONE);
        layout_koneksi.setVisibility(View.VISIBLE);
        layout_kosong.setVisibility(View.GONE);
        detail_shimmer.setVisibility(View.GONE);
//        beranda_content.setVisibility(View.GONE);
        detail_shimmer.stopShimmer();

    }

    private void load_success() {
        swdetail.setRefreshing(false);
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
        final int id = intent.getIntExtra("ID",0);

        AndroidNetworking.post(ServerApi.site_url+"detail.php")
                .addBodyParameter("id_produk", String.valueOf(id))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i=0;i<response.length();i++){
                                JSONObject detail = response.getJSONObject(i);
                                j1 = detail.getString("nama");
                                kat = detail.getString("kategori");
                                har = detail.getInt("harga");
                                ket = detail.getString("keterangan");
                                imagedetail = detail.getString("gambar");
                                satuan = detail.getString("satuan");
                                stok = detail.getInt("stok");

                                judul1.setText(j1);
//                                judul2.setText(j1);
                                satuandetail.setText(satuan);
                                kategori.setText(kat);
                                Locale localeId = new Locale("in", "ID");
                                final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeId);
                                harga.setText(formatRupiah.format((double)har));
                                keterangan.setText(ket);
                                Picasso.get().load(imagedetail).into(imgproduk);
                                load_success();
                        }}catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        load_fail();

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
                            rv_relate.setAdapter(produkAdapter);
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
    private void snackBar(String pesan, int warna) {
        Snackbar snackbar = Snackbar.make(detail, pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(this, warna));
        snackbar.show();

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
