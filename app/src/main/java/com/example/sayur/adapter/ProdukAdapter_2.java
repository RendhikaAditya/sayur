package com.example.sayur.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sayur.R;
import com.example.sayur.activity.DetailProduk;
import com.example.sayur.model.Produk_model;
import com.example.sayur.model.Produk_model_2;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProdukAdapter_2 extends RecyclerView.Adapter<ProdukAdapter_2.ListViewHolder> {
    private List<Produk_model_2>produk;
    private Context context;
    private int jml = 1;

    public ProdukAdapter_2(List<Produk_model_2> produk) {
        this.produk = produk;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.produk_row, parent,false);
        ListViewHolder holder = new ListViewHolder(v);
        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
        public void onBindViewHolder(@NonNull final ListViewHolder holder, int position) {
        Locale localeId = new Locale("in", "ID");
        final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeId);
        final Produk_model_2 model=produk.get(position);
        holder.nama.setText(model.getNama());
        holder.harga.setText(formatRupiah.format((double)model.getHarga()));
        holder.satuan.setText(model.getSatuan());
        Picasso.get().load(model.getImage()).into(holder.image);

        if (model.getStok()==0){
            holder.beli.setText("Habis");
            holder.habis.setVisibility(View.GONE);
            holder.prodHabis.setVisibility(View.VISIBLE);
            holder.cardProduk.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(context, "Stok Kosong", Toast.LENGTH_SHORT).show();

                }
            });
        }else {
            holder.habis.setVisibility(View.GONE);
            holder.cardProduk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailProduk.class);
                    intent.putExtra("ID", model.getId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return produk.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        private ImageView image, kurang, tambah;
        private CardView cardProduk, btnadd;
        private LinearLayout habis;
        private LinearLayout viewjumlah, prodHabis;
        private TextView nama, harga, satuan, jumlah, beli;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            context=itemView.getContext();
            prodHabis =itemView.findViewById(R.id.saaa);
            habis = itemView.findViewById(R.id.habisbtn);
            beli =itemView.findViewById(R.id.textBeli);
            kurang = itemView.findViewById(R.id.minusjumblah);
            tambah = itemView.findViewById(R.id.plusjumblah);
            jumlah = itemView.findViewById(R.id.numbrjumblah);
            viewjumlah = itemView.findViewById(R.id.viewjumlah);
            btnadd = itemView.findViewById(R.id.btnadd);
            cardProduk =itemView.findViewById(R.id.card_produk);
            image=itemView.findViewById(R.id.imageproduk);
            nama = itemView.findViewById(R.id.namaproduk);
            harga=itemView.findViewById(R.id.hargaproduk);
            satuan= itemView.findViewById(R.id.satuanproduk);
        }
    }
}
