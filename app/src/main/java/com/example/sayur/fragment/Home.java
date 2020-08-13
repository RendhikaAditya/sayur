package com.example.sayur.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.sayur.R;
import com.example.sayur.ServerApi;
import com.example.sayur.activity.Cari;
import com.example.sayur.activity.KategoriProduk;
import com.example.sayur.activity.Keranjang;
import com.example.sayur.adapter.KategoriAdapter;
import com.example.sayur.adapter.ProdukAdapter;
import com.example.sayur.adapter.ProdukAdapter_1;
import com.example.sayur.adapter.ProdukAdapter_2;
import com.example.sayur.adapter.SliderAdapter;
import com.example.sayur.model.Kategori_model;
import com.example.sayur.model.Produk_model;
import com.example.sayur.model.Produk_model_1;
import com.example.sayur.model.Produk_model_2;
import com.example.sayur.model.Slider_model;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    public Home() {
        // Required empty public constructor
    }

    ImageView promo1, promo2, keranjang, cari;
    String peromosi1, peromosi2;
    TextView semuaTerbaru, semuaSayur, semuaBuah;

    private RecyclerView rvSlider, rvKategori, rvProduk, rvProdbuah, rvProdsusu;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Slider_model>slider;
    private List<Kategori_model>kategori;
    private List<Produk_model>produk;
    private List<Produk_model_1> produk1;
    private List<Produk_model_2>produk2;

    RelativeLayout layout_koneksi, layout_kosong;
    ShimmerFrameLayout home_shimmer;
    LinearLayout home;

    final int time = 3000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        semuaBuah = view.findViewById(R.id.semuaBuah);
        semuaBuah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), KategoriProduk.class);
                intent.putExtra("IDK", "10");
                startActivity(intent);
            }
        });

        semuaSayur = view.findViewById(R.id.semuaSayur);
        semuaSayur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), KategoriProduk.class);
                intent.putExtra("IDK", "9");
                startActivity(intent);
            }
        });

        semuaTerbaru = view.findViewById(R.id.semuaTerbaru);
        semuaTerbaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), KategoriProduk.class);
                intent.putExtra("IDK", "all");
                startActivity(intent);
            }
        });

        cari = view.findViewById(R.id.cariHome);
        cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Cari.class);
                startActivity(intent);
            }
        });

        promo1 = view.findViewById(R.id.promo1);
        promo2 = view.findViewById(R.id.promo2);

        keranjang = view.findViewById(R.id.homekeranjang);
        keranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Keranjang.class);
                startActivity(intent);
            }
        });

        home_shimmer = view.findViewById(R.id.shimmer_home);

        home = view.findViewById(R.id.layout_home);
        layout_koneksi = view.findViewById(R.id.layout_koneksi);
        layout_koneksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        layout_kosong = view.findViewById(R.id.layout_kosong);

        rvProduk = view.findViewById(R.id.rv_prodnew);
        rvProduk.setHasFixedSize(true);
        rvProduk.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        produk = new ArrayList<>();

        rvProdbuah = view.findViewById(R.id.rv_prodbuah);
        rvProdbuah.setHasFixedSize(true);
        rvProdbuah.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        produk2 = new ArrayList<>();

        rvProdsusu = view.findViewById(R.id.rv_prodsusu);
        rvProdsusu.setHasFixedSize(true);
        rvProdsusu.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        produk1 = new ArrayList<>();

        rvSlider = view.findViewById(R.id.rv_slider);
        rvSlider.setItemAnimator(null);
        rvSlider.setHasFixedSize(true);
        slider = new ArrayList<>();

        rvKategori = view.findViewById(R.id.rv_kategori);
        rvKategori.setHasFixedSize(true);
        rvKategori.setLayoutManager(new GridLayoutManager(getContext(), 4));
        kategori = new ArrayList<>();

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvSlider);


        swipeRefreshLayout = view.findViewById(R.id.sf_branda);
        AndroidNetworking.initialize(getContext());
//        swipeRefreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                getData();
//            }
//        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                slider.clear();
                kategori.clear();
                produk.clear();
                getData();
            }
        });

        getData();
        return view;
    }



    private void set_loading(){
        home.setVisibility(View.VISIBLE);
        home_shimmer.setVisibility(View.VISIBLE);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);
//        beranda_content.setVisibility(View.GONE);
        home_shimmer.startShimmer();
    }

    private void load_fail(){
        swipeRefreshLayout.setRefreshing(false);
        home.setVisibility(View.VISIBLE);
        layout_koneksi.setVisibility(View.VISIBLE);
        layout_kosong.setVisibility(View.GONE);
        home_shimmer.setVisibility(View.GONE);
//        beranda_content.setVisibility(View.GONE);
        home_shimmer.stopShimmer();
    }
    private void load_success(){
        swipeRefreshLayout.setRefreshing(false);
        home.setVisibility(View.VISIBLE);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);
        home_shimmer.setVisibility(View.GONE);
//        beranda_content.setVisibility(View.VISIBLE);
        home_shimmer.stopShimmer();
    }

    private void getData() {
        set_loading();
            AndroidNetworking.get(ServerApi.site_url+"slider.php")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            swipeRefreshLayout.setRefreshing(false);
                            Log.d("Sukses slid", "onResponse"+response);
                            try {
                                slider.clear();
                                for (int i = 0; i<response.length(); i++) {
                                    JSONObject data = response.getJSONObject(i);
                                    slider.add(new Slider_model(
                                            data.getInt("id"),
                                            data.getString("nama"),
                                            data.getString("gambar")
                                    ));
                                }
                                final SliderAdapter sliderAdapter = new SliderAdapter(slider);
                                rvSlider.setAdapter(sliderAdapter);
                                sliderAdapter.notifyDataSetChanged();

                                final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                rvSlider.setLayoutManager(layoutManager);
                                //auto slider start
                                final Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (layoutManager.findFirstVisibleItemPosition()!= -1){
                                            if (layoutManager.findFirstCompletelyVisibleItemPosition()< (sliderAdapter.getItemCount() - 1)){
                                                layoutManager.smoothScrollToPosition(rvSlider, new RecyclerView.State(), layoutManager.findLastCompletelyVisibleItemPosition() + 1 );
                                            }else if (layoutManager.findLastCompletelyVisibleItemPosition() == (sliderAdapter.getItemCount()-1)){
                                                layoutManager.smoothScrollToPosition(rvSlider, new RecyclerView.State(), 0);
                                            }
                                        }
                                    }
                                }, 0, time);
                                load_success();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            load_fail();
                            swipeRefreshLayout.setRefreshing(false);
                            Log.d("error", "onError : "+anError);
                        }
                    });

            //start mengambil data kategori
            AndroidNetworking.get(ServerApi.site_url+"kategori.php")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            swipeRefreshLayout.setRefreshing(false);
                            Log.d("sukses kat", "onResponse"+response);
                            try {
                                kategori.clear();
                                for (int i = 0; i<response.length(); i++){
                                    JSONObject kat = response.getJSONObject(i);
                                    kategori.add(new Kategori_model(
                                            kat.getString("id"),
                                            kat.getString("nama"),
                                            kat.getString("icon")
                                    ));
                                }
                                KategoriAdapter kategoriAdapter = new KategoriAdapter(kategori);
                                rvKategori.setAdapter(kategoriAdapter);
                                kategoriAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            swipeRefreshLayout.setRefreshing(false);
                            Log.d("error","onError :"+anError);
                        }
                    });
            //end mengambil data kategori
        AndroidNetworking.post(ServerApi.site_url+"produk.php")
                .addBodyParameter("idc", String.valueOf(0))
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
                            rvProduk.setAdapter(produkAdapter);
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

        AndroidNetworking.post(ServerApi.site_url+"promo.php")
                .addBodyParameter("id_promo", "1")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Sukses prom", "onResponse"+response);
                        try {
                            for (int i=0; i<response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                peromosi1 = object.getString("foto");
                                Picasso.get().load(peromosi1).into(promo1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        load_success();
                    }

                    @Override
                    public void onError(ANError anError) {
                        load_fail();
                        Log.d("error promo", "onError : "+anError);
                    }
                });

        AndroidNetworking.post(ServerApi.site_url+"produk.php")
                .addBodyParameter("idc", String.valueOf(10))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Sukses prod", "onResponse"+response);
                        try {
                            produk2.clear();
                            for (int i = 0; i<response.length();i++) {
                                JSONObject prod = response.getJSONObject(i);
                                produk2.add(new Produk_model_2(
                                        prod.getInt("id"),
                                        prod.getString("nama"),
                                        prod.getInt("harga"),
                                        prod.getString("satuan"),
                                        prod.getInt("stok"),
                                        prod.getString("gambar")
                                ));
                            }
                            ProdukAdapter_2 produkAdapter2 = new ProdukAdapter_2(produk2);
                            rvProdbuah.setAdapter(produkAdapter2);
                            produkAdapter2.notifyDataSetChanged();
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

        AndroidNetworking.post(ServerApi.site_url+"promo.php")
                .addBodyParameter("id_promo", "2")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Sukses prom", "onResponse"+response);
                        try {
                            for (int i=0; i<response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                peromosi2 = object.getString("foto");
                                Picasso.get().load(peromosi2).into(promo2);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        load_success();
                    }

                    @Override
                    public void onError(ANError anError) {
                        load_fail();
                        Log.d("error promo", "onError : "+anError);
                    }
                });

        AndroidNetworking.post(ServerApi.site_url+"produk.php")
                .addBodyParameter("idc", String.valueOf(9))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Sukses prod", "onResponse"+response);
                        try {
                            produk1.clear();
                            for (int i = 0; i<response.length();i++) {
                                JSONObject prod = response.getJSONObject(i);
                                produk1.add(new Produk_model_1(
                                        prod.getInt("id"),
                                        prod.getString("nama"),
                                        prod.getInt("harga"),
                                        prod.getString("satuan"),
                                        prod.getInt("stok"),
                                        prod.getString("gambar")
                                ));
                            }
                            ProdukAdapter_1 produkAdapter1 = new ProdukAdapter_1(produk1);
                            rvProdsusu.setAdapter(produkAdapter1);
                            produkAdapter1.notifyDataSetChanged();
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
}
