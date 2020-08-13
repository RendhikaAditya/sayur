package com.example.sayur.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.sayur.fragment.Profil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlamatAdd extends AppCompatActivity {
EditText nama, pos, alamat;
Button btnSimpanAlamat;
String edNama, edPos, edAlamat;
ImageView back;
    private PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alamat_add);
        AndroidNetworking.initialize(this);
        prefManager = new PrefManager(this);

        back = findViewById(R.id.backAlamat);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nama = findViewById(R.id.namaPenerima);
        pos = findViewById(R.id.kodePos);
        alamat = findViewById(R.id.alamatEdit);
        getData();

//        nama.setText(prefManager.getIdUser());

        btnSimpanAlamat = findViewById(R.id.btnSimpanAlamat);
        btnSimpanAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Perhatian")
                .setMessage("Apakah anda yakin meningkalkan halaman ini?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(AlamatAdd.this, Keranjang.class);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();

    }

    private void getData() {

        int cod = 2;
        AndroidNetworking.post(ServerApi.site_url+"get_alamat.php")
                .addBodyParameter("id", prefManager.getIdUser())
                .addBodyParameter("cod", String.valueOf(cod))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i=0;i<response.length();i++){
                            try {
                                Log.d("Sukses alamat","sukses : "+response);
                                JSONObject object = response.getJSONObject(i);
                                edNama = object.getString("nama");
                                edPos = object.getString("pos");
                                edAlamat = object.getString("alamat");

                                nama.setText(edNama);
                                pos.setText(edPos);
                                alamat.setText(edAlamat);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("Error alamat", "eror : "+anError);
                    }
                });
    }



    private void setData() {
        final String H;
        prefManager = new PrefManager(this);
        final Intent intent = getIntent();
        H = intent.getStringExtra("B");
        String ICUS = null;
        ICUS = intent.getStringExtra("IDCUS");
        if(ICUS==null) {
            AndroidNetworking.post(ServerApi.site_url + "set_alamat.php")
                    .addBodyParameter("id", prefManager.getIdUser())
                    .addBodyParameter("nama", nama.getText().toString())
                    .addBodyParameter("pos", pos.getText().toString())
                    .addBodyParameter("alamat", alamat.getText().toString())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String code = null;
                            try {
                                code = response.getString("cod");
                                if (code.equals(1)) {
                                    Toast.makeText(getApplicationContext(), response.getString("response"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), response.getString("response"), Toast.LENGTH_SHORT).show();
                                    if (H.isEmpty()) {
                                        Intent intent = new Intent(AlamatAdd.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(AlamatAdd.this, Checkout.class);
                                        startActivity(intent);
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("error add", "error :"+anError);
                        }
                    });
        }else {
            AndroidNetworking.post(ServerApi.site_url + "set_alamat.php")
                    .addBodyParameter("id", ICUS)
                    .addBodyParameter("nama", nama.getText().toString())
                    .addBodyParameter("pos", pos.getText().toString())
                    .addBodyParameter("alamat", alamat.getText().toString())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String code = null;
                            try {
                                code = response.getString("cod");
                                if (code.equalsIgnoreCase("1")) {
//                                    Toast.makeText(getApplicationContext(), "REgister Sukses", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Register gagal", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("Error ini", "error :"+anError);
                        }
                    });
        }
    }
}
