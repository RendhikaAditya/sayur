package com.example.sayur.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sayur.R;
import com.example.sayur.activity.DetailOrder;
import com.example.sayur.model.Order_model;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ListViewHolder> {
    private List<Order_model>order;
    private Context context;

    public OrderAdapter(List<Order_model> order) {
        this.order = order;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pesanan_row, parent,false);
        OrderAdapter.ListViewHolder holder = new OrderAdapter.ListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, int position) {
        Locale localeId = new Locale("in", "ID");
        final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeId);
        final Order_model model = order.get(position);
        holder.status.setText(model.getStatus());
        holder.nama.setText(model.getNamaProd());
        holder.jumlah.setText(model.getJumlah());
        holder.harga.setText(formatRupiah.format((double)model.getHarga()));
        holder.banyak1.setText(model.getBanyakItem());
        holder.banyak2.setText(model.getBanyakItem());
        holder.total.setText(formatRupiah.format((double)model.getTotal()));
        Picasso.get().load(model.getGambar()).into(holder.foto);
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailOrder.class);
                intent.putExtra("IDORDER", model.getIdOrd());
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return order.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        TextView status, nama, jumlah, banyak1, banyak2, harga, total;
        ImageView foto;
        LinearLayout row;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            context=itemView.getContext();
            row = itemView.findViewById(R.id.row_order);
            foto = itemView.findViewById(R.id.imgOrde);
            status = itemView.findViewById(R.id.statusOrder);
            nama = itemView.findViewById(R.id.judulBrg);
            jumlah = itemView.findViewById(R.id.jumlahnya);
            banyak1 = itemView.findViewById(R.id.totalProduk1);
            banyak2 = itemView.findViewById(R.id.totalProduk2);
            harga = itemView.findViewById(R.id.hargaBrg);
            total = itemView.findViewById(R.id.hargaTotal);
        }
    }
}
