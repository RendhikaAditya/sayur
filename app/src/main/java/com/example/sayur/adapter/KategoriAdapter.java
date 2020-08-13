package com.example.sayur.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sayur.R;
import com.example.sayur.activity.DetailProduk;
import com.example.sayur.activity.KategoriProduk;
import com.example.sayur.model.Kategori_model;
import com.squareup.picasso.Picasso;

import java.util.List;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.ListViewHolder> {
    private List<Kategori_model> kategori;
    private Context context;

    public KategoriAdapter(List<Kategori_model> kategori) {
        this.kategori = kategori;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.kategori_row, parent, false);
        ListViewHolder holder = new ListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        final Kategori_model model = kategori.get(position);
        if(model.getNama()==null){
            holder.textView.setText(" ");
        }
        holder.textView.setText(model.getNama());
        Picasso.get().load(model.getIcon()).into(holder.icon);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), KategoriProduk.class);
                intent.putExtra("IDK", model.getId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kategori.size();
    }


    public class ListViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private CardView cardView;
        private TextView textView;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            cardView = itemView.findViewById(R.id.card_kategori);
            icon = itemView.findViewById(R.id.imgKategori);
            textView = itemView.findViewById(R.id.textkategori);
        }
    }
}
