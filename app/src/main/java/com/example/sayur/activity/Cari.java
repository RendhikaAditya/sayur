package com.example.sayur.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sayur.R;

public class Cari extends AppCompatActivity {
    EditText cari;
    ImageView btnCari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari);

        cari = findViewById(R.id.cariProd);
        cari.requestFocus();

        cari.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (cari.getText().toString().isEmpty()) {
                    cari.setError("Apa yang kamu cari?");
                    return false;
                }else {
//                    cari.setError(cari.getText().toString());?
                    Intent intent = new Intent(getApplicationContext(), KategoriProduk.class);
                    intent.putExtra("IDK", "cari");
                    intent.putExtra("CARI", cari.getText().toString());
                    startActivity(intent);
                    return true;
                }

            }
        });
        btnCari = findViewById(R.id.btnCari);
        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cari.getText().toString().isEmpty()) {
                    cari.setError("Apa yang kamu cari?");
                }else {
//                    cari.setError(cari.getText().toString());?
                    Intent intent = new Intent(getApplicationContext(), KategoriProduk.class);
                    intent.putExtra("IDK", "cari");
                    intent.putExtra("CARI", cari.getText().toString());
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
