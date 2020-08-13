package com.example.sayur.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.sayur.InterfaceCount.OnUpdateHarga;
import com.example.sayur.R;
import com.example.sayur.ServerApi;
import com.example.sayur.activity.Keranjang;
import com.example.sayur.model.Keranjang_model;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class KeranjangAdapter extends RecyclerView.Adapter<KeranjangAdapter.ListViewHolder> {
    private List<Keranjang_model> keranjang;
    private Context context;
    OnUpdateHarga onUpdateHarga;

    public KeranjangAdapter(List<Keranjang_model> keranjang, OnUpdateHarga onUpdateHarga) {
        this.keranjang = keranjang;
        this.onUpdateHarga = onUpdateHarga;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.keranjang_row, parent, false);
        KeranjangAdapter.ListViewHolder holder = new KeranjangAdapter.ListViewHolder(v);
        return holder;
    }

    int angka;
    int hasil =0;

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, final int position) {
        Locale localeId = new Locale("in", "ID");
        final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeId);
        final Keranjang_model model = keranjang.get(position);
        holder.nama.setText(model.getNama());
        holder.harga.setText(formatRupiah.format((double) model.getHarga()));
        holder.satuan.setText(model.getSatuan());
        angka = model.getJumlah();
        holder.jumlah.setText(String.valueOf(angka));
        Picasso.get().load(model.getFoto()).into(holder.foto);
        holder.tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tesangka = Integer.valueOf(holder.jumlah.getText().toString());
                if (tesangka<model.getStok()){
                    tesangka++;
                    Tambah(model.getIdk(), model.getJumlah());
                }else {
                    tesangka=tesangka+0;
                    Toast.makeText(context, "Stok Hanya tersisa "+model.getStok(), Toast.LENGTH_SHORT).show();
                }

                holder.jumlah.setText(String.valueOf(tesangka));

            }

        });
        holder.kurang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Kangka = Integer.valueOf(holder.jumlah.getText().toString());
                if (Kangka>1) {
                    Kangka--;
                    holder.jumlah.setText(String.valueOf(Kangka));
                    Kurang(model.getIdk(), model.getJumlah());

                }else if (Kangka==1) {

                        new AlertDialog.Builder(context)
                                .setMessage("Apakah anda yakin untuk menghapus ?")
                                .setCancelable(false)
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(context, Keranjang.class);
                                        context.startActivity(intent);
                                        Hapus(model.getIdk());
                                    }
                                })
                                .setNegativeButton("Tidak", null)
                                .show();


                }

            }
        });
        holder.hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Apakah anda yakin untuk menghapus ?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(context, Keranjang.class);
                                context.startActivity(intent);
                                Hapus(model.getIdk());
                            }
                        })
                        .setNegativeButton("Tidak", null)
                        .show();
            }
        });


    }

    private void Hapus(String idk) {
        AndroidNetworking.post(ServerApi.site_url+"keranjang_update.php")
                .addBodyParameter("id", idk)
                .addBodyParameter("code", String.valueOf(3))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String code = response.getString("cod");
                            if (code.equals(1)) {
                                Toast.makeText(context, response.getString("response"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, response.getString("response"), Toast.LENGTH_SHORT).show();
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

    private void Kurang(String idk, int jumlah) {
        AndroidNetworking.post(ServerApi.site_url+"keranjang_update.php")
                .addBodyParameter("id", idk)
                .addBodyParameter("jml", String.valueOf(jumlah))
                .addBodyParameter("code", String.valueOf(2))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String code = response.getString("cod");
                            if (code.equalsIgnoreCase("1")) {
//                                Toast.makeText(context, response.getString("response"), Toast.LENGTH_SHORT).show();
                                onUpdateHarga.onUpdateHarga();
                            } else {
                                Toast.makeText(context, response.getString("response"), Toast.LENGTH_SHORT).show();
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

    private void Tambah(String idk, final int jumlah) {
        AndroidNetworking.post(ServerApi.site_url+"keranjang_update.php")
                .addBodyParameter("id", idk)
                .addBodyParameter("jml", String.valueOf(jumlah))
                .addBodyParameter("code", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String code = response.getString("cod");
                            if (code.equalsIgnoreCase("1")) {
                                onUpdateHarga.onUpdateHarga();
//                                Toast.makeText(context, response.getString("response"), Toast.LENGTH_SHORT).show();
                            } else {
//                                onUpdateHarga.onUpdateHarga();
                                Toast.makeText(context, response.getString("response"), Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return keranjang.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        private ImageView foto, tambah, kurang, hapus;
        private TextView nama, harga, jumlah, satuan, total;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            total = itemView.findViewById(R.id.totalKeranjang);
            foto = itemView.findViewById(R.id.imagekeranjang);
            nama = itemView.findViewById(R.id.judulKeranjang);
            harga = itemView.findViewById(R.id.hargaKeranjang);
            satuan = itemView.findViewById(R.id.satuankeranjang);
            tambah = itemView.findViewById(R.id.tambahkeranjang);
            kurang = itemView.findViewById(R.id.minusjumblah);
            jumlah = itemView.findViewById(R.id.itemjumlah);
            hapus = itemView.findViewById(R.id.hapuskeranjang);
        }
    }
}
