package com.example.sayur.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.sayur.MainActivity;
import com.example.sayur.R;
import com.example.sayur.ServerApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class Konfirmasi extends AppCompatActivity {
TextView satu, dua, harga, noRek, naRek, btnPick;
ImageView shImage;
LinearLayout tfOnly;
Button konfirm;
String no, na;
int totalh;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";

    String tag_json_obj = "json_obj_req";

    Bitmap bitmap, decoded;

    int success;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60; // range 1 - 100

    private static final String TAG = Konfirmasi.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi);
        AndroidNetworking.initialize(this);

        harga =findViewById(R.id.totalHarga);
        getHarga();
        noRek = findViewById(R.id.noRekening);
        naRek = findViewById(R.id.namaRekening);
        getRek();
        konfirm = findViewById(R.id.btnSelesai);


        shImage =findViewById(R.id.imgShow);
        btnPick =findViewById(R.id.btnPick);
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        
        tfOnly =findViewById(R.id.onlyTransfer);
        satu = findViewById(R.id.satu);
        dua = findViewById(R.id.dua);
        Intent intent = getIntent();
        int konfirmasi = intent.getIntExtra("KONFIRM",0);
        if (konfirmasi==2){
            tfOnly.setVisibility(View.GONE);
            konfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSiap();
                }
            });
            satu.setText("Pembayaran akan di lakukan ketika barang sampai");
            dua.setText("Siapkan uang sesuai nominal diatas agar tidak mempersulit kurir dalam peroses pengantaran barang");
        }else {
            tfOnly.setVisibility(View.VISIBLE);
            konfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelesai();
                }
            });
            satu.setText("Transfer Sesuai Nominal Diatas");
            dua.setText("Pembayaran akan di cek dalam 1 jam, jika tidak ada pembayaran jika tidak maka barang akan di cencel. Dan Barang akan di proses setelah bukti transfer di upload");
        }
    }




    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);

        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        shImage.setImageBitmap(decoded);
    }

    // fungsi resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void getRek() {
        AndroidNetworking.get(ServerApi.site_url+"get_rek.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("Sukses confirm", "onResponse"+response);
                            na = response.getString("nama");
                            no = response.getString("nomr");
                            noRek.setText(no);
                            naRek.setText(na);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error confrm", "onResponse"+anError);
                    }
                });
    }

    private void getHarga() {
        Intent intent = getIntent();
        final String ido = intent.getStringExtra("IDO");
        AndroidNetworking.post(ServerApi.site_url+"get_total.php")
                .addBodyParameter("id", ido)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            totalh = response.getInt("total");
                            Locale localeId = new Locale("in", "ID");
                            final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeId);
                            harga.setText(formatRupiah.format(totalh));
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
    public void onBackPressed() {

    }

    private void setSiap() {
        Intent intent = getIntent();
        String ido = intent.getStringExtra("IDO");
        AndroidNetworking.post(ServerApi.site_url+"konfirmasi.php")
                .addBodyParameter("ido", ido)
                .addBodyParameter("img", ido)
                .addBodyParameter("harga", String.valueOf(totalh))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("Sukses upload", "onResponse"+response);
                            String code = response.getString("success");
                            if (code.equalsIgnoreCase("1")) {
                                Intent i = new Intent(Konfirmasi.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error upload", "onResponse"+anError);
                    }
                });
    }

    private void setSelesai() {
        Intent intent = getIntent();
        String ido = intent.getStringExtra("IDO");
        if (shImage.getDrawable()==null){
            new AlertDialog.Builder(this)
                .setMessage("Masukan Bukti transfer !")
                .setCancelable(false)
                .setNegativeButton("Ok", null)
                .show();
//            Toast.makeText(getApplicationContext(), "masukan foto transfer", Toast.LENGTH_SHORT).show();
        }else {
            AndroidNetworking.post(ServerApi.site_url + "konfirmasi.php")
                    .addBodyParameter("ido", ido)
                    .addBodyParameter("img", getStringImage(decoded))
                    .addBodyParameter("harga", String.valueOf(totalh))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("Sukses upload", "onResponse" + response);
                                String code = response.getString("success");
                                if (code.equalsIgnoreCase("1")) {
                                    Intent i = new Intent(Konfirmasi.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("error upload", "onResponse" + anError);
                        }
                    });
        }
    }
}
