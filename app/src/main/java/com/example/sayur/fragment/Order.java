package com.example.sayur.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.sayur.PrefManager;
import com.example.sayur.R;
import com.example.sayur.ServerApi;
import com.example.sayur.adapter.OrderAdapter;
import com.example.sayur.adapter.SliderAdapter;
import com.example.sayur.model.Order_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Order extends Fragment {

    RecyclerView rv_order;
    private List<Order_model> order;
    private PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        AndroidNetworking.initialize(getContext());
        prefManager = new PrefManager(getContext());
        rv_order = view.findViewById(R.id.rb_order);
        rv_order.setHasFixedSize(true);
        rv_order.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        order = new ArrayList<>();

        getData();

        return view;
    }

    private void getData() {
        AndroidNetworking.post(ServerApi.site_url + "order.php")
                .addBodyParameter("idc", prefManager.getIdUser())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d("sukses ord", "onResponse"+response);
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject data = response.getJSONObject(i);
                                order.add(new Order_model(
                                        data.getString("idord"),
                                        data.getString("status"),
                                        data.getString("banyakitem"),
                                        data.getString("namaprod"),
                                        data.getString("jumlah"),
                                        data.getString("gambar"),
                                        data.getInt("total"),
                                        data.getInt("harga")
                                ));
                                final OrderAdapter orderAdapter = new OrderAdapter(order);
                                rv_order.setAdapter(orderAdapter);
                                orderAdapter.notifyDataSetChanged();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error ord", "onResponse"+anError);
                    }
                });
    }
}
