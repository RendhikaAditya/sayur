package com.example.sayur.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
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

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ListViewHolder> {
    private List<Keranjang_model> keranjang;
    private Context context;

    public CheckoutAdapter(List<Keranjang_model> keranjang) {
        this.keranjang = keranjang;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.keranjang_row, parent, false);
        CheckoutAdapter.ListViewHolder holder = new CheckoutAdapter.ListViewHolder(v);
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
        holder.chkOnly.setVisibility(View.VISIBLE);
        holder.jmlChk.setText(String.valueOf(angka));
        holder.foto.getLayoutParams().width=160;
        holder.foto.getLayoutParams().height=160;
        Picasso.get().load(model.getFoto()).into(holder.foto);
        holder.viewjumlah.setVisibility(View.GONE);
        holder.hapus.setVisibility(View.GONE);
        holder.jumlah.setVisibility(View.GONE);
        holder.kurang.setVisibility(View.GONE);
        holder.tambah.setVisibility(View.GONE);

    }


    @Override
    public int getItemCount() {
        return keranjang.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        private ImageView foto, tambah, kurang, hapus;
        private TextView nama, harga, jumlah, satuan, total, viewjumlah, jmlChk;
        private LinearLayout chkOnly;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            jmlChk = itemView.findViewById(R.id.jmlChk);
            chkOnly = itemView.findViewById(R.id.onlyChk);
            viewjumlah = itemView.findViewById(R.id.texttampiljumblah);
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
