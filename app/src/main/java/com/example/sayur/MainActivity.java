package com.example.sayur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.sayur.fragment.Chat;
import com.example.sayur.fragment.Home;
import com.example.sayur.fragment.Inbox;
import com.example.sayur.fragment.Order;
import com.example.sayur.fragment.Profil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);


        Intent intent = getIntent();
        int id = intent.getIntExtra("LOGIN",0);
        if (id==0) {
            loadFragment(new Home());
        }else {
            loadFragment(new Profil());
        }



        BottomNavigationView bottomNavigationView = findViewById(R.id.bn_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private boolean loadFragment(Fragment fragment) {
        if(fragment!=null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()){
            case R.id.home:
                fragment = new Home();
                break;
            case R.id.chat:
                fragment = new Chat();
                break;
            case R.id.order:
                fragment = new Order();
                break;
//            case R.id.inbox:
//                fragment = new Inbox();
//                break;
            case R.id.profil:
                fragment = new Profil();
                break;
        }

        return loadFragment(fragment);
    }
}
