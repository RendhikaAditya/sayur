package com.example.sayur.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sayur.R;
import com.example.sayur.model.Slider_model;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ListViewHolder> {
    private List<Slider_model>promo;
    private Context context;

    public SliderAdapter(List<Slider_model> promo){
        this.promo=promo;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_row,parent,false);
        ListViewHolder holder = new ListViewHolder(v);
        return holder;
    }

    @Override
    public int getItemCount() {
        return promo.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        final Slider_model model = promo.get(position);
        holder.nama.setText(model.getNama());
        Picasso.get().load(model.getGambar())
                .into(holder.promoimg);
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView nama;
        private ImageView promoimg;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            nama = itemView.findViewById(R.id.textPromo);
            context=itemView.getContext();
            promoimg=itemView.findViewById(R.id.img_promo);

        }
    }
}
