package com.example.sayur.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.sayur.MainActivity;
import com.example.sayur.PrefManager;
import com.example.sayur.R;
import com.example.sayur.ServerApi;
import com.example.sayur.activity.AlamatAdd;
import com.example.sayur.activity.UploadFoto;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Profil extends Fragment {
    EditText username, password, namaReg, passReg, emaiReg, telpReg;
    Button login, regist, registerbtn;

    LinearLayout registlayout, loginlayout, profilelayout, pindah;
    TextView daftarakun, loginakun, namauser, nomoruser, emailuser, alamatuser, title;
    ImageView fotouser;
    SwipeRefreshLayout swProfile;

    RelativeLayout layout_koneksi;
    ShimmerFrameLayout home_shimmer;

    CardView logout, edit;

    String nama, email, nomr, alamat, foto;

    private PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profil, container, false);
        AndroidNetworking.initialize(getContext());
        prefManager = new PrefManager(getContext());
        swProfile = view.findViewById(R.id.sw_profile);

        home_shimmer = view.findViewById(R.id.shimmer_home);
        layout_koneksi = view.findViewById(R.id.layout_koneksi);
        layout_koneksi = view.findViewById(R.id.layout_koneksi);

        emaiReg = view.findViewById(R.id.emailuserReg);
        passReg = view.findViewById(R.id.passworduserReg);
        namaReg = view.findViewById(R.id.namauserReg);
        telpReg = view.findViewById(R.id.notelponReg);

        registerbtn = view.findViewById(R.id.btnRegist);
        fotouser = view.findViewById(R.id.fotouser);
        namauser = view.findViewById(R.id.usernameuser);
        nomoruser = view.findViewById(R.id.nomruser);
        emailuser = view.findViewById(R.id.gmailuser);
        alamatuser = view.findViewById(R.id.alamatuser);

        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);

        pindah =view.findViewById(R.id.pindahLogin);
        title = view.findViewById(R.id.title);
        edit = view.findViewById(R.id.btnEditProf);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registlayout.setVisibility(View.VISIBLE);
                loginlayout.setVisibility(View.GONE);
                profilelayout.setVisibility(View.GONE);
                title.setText("Edit Profile");
                pindah.setVisibility(View.GONE);
                editProfil();
            }
        });

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Perhatian")
                        .setMessage("Apakah anda yakin akan keluar?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                logout();
                            }
                        })
                        .setNegativeButton("Tidak", null)
                        .show();

            }
        });

        profilelayout = view.findViewById(R.id.layout_profile);
        registlayout = view.findViewById(R.id.layout_register);
        loginlayout = view.findViewById(R.id.layout_login);

        if (prefManager.getLoginStatus()) {
            profilelayout.setVisibility(View.VISIBLE);
            registlayout.setVisibility(View.GONE);
            loginlayout.setVisibility(View.GONE);
            getData();
        } else {
            profilelayout.setVisibility(View.GONE);
            registlayout.setVisibility(View.GONE);
            loginlayout.setVisibility(View.VISIBLE);
        }

        regist = view.findViewById(R.id.btnRegist);
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                if (prefManager.getIdUser().isEmpty()){
//                    regist.setText("|kosong|");
//                }else {
//                    regist.setText("|"+prefManager.getIdUser()+"|");
//                }

                register();

            }
        });

        login = view.findViewById(R.id.btnlogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().isEmpty()) {
                    username.setError("Email harus diisi");
                } else if (password.getText().toString().isEmpty()) {
                    password.setError("Kata sandi harus diisi");
                } else {
                    login(username.getText().toString(), password.getText().toString());
                }

            }
        });
        daftarakun = view.findViewById(R.id.daftarakun);
        daftarakun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registlayout.setVisibility(View.VISIBLE);
                loginlayout.setVisibility(View.GONE);
                profilelayout.setVisibility(View.GONE);
            }
        });
        loginakun = view.findViewById(R.id.masukakun);
        loginakun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginlayout.setVisibility(View.VISIBLE);
                registlayout.setVisibility(View.GONE);
                profilelayout.setVisibility(View.GONE);
            }
        });
        return view;
    }

    private void editProfil() {
        set_loading();
        swProfile.setRefreshing(true);
        registerbtn.setText("EDIT");
        AndroidNetworking.post(ServerApi.site_url + "profil.php")
                .addBodyParameter("id_user", prefManager.getIdUser())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject profil = response.getJSONObject(i);
                                nama = profil.getString("nama");
                                nomr = profil.getString("no");
                                email = profil.getString("mail");
                                alamat = profil.getString("alamat");
                                foto = profil.getString("foto");
                                String pass = profil.getString("pass");

                                namaReg.setText(nama);
                                telpReg.setText(nomr);
                                emaiReg.setText(email);
                                passReg.setText(pass);
//                                alamatuser.setText(alamat);
//                                Picasso.get().load(foto).into(fotouser);
                                swProfile.setRefreshing(false);
                                home_shimmer.stopShimmer();
                                home_shimmer.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            setError();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("Error edit", "eror :" + anError);
                        setError();
                    }
                });
    }

    private void register() {
        prefManager = new PrefManager(getContext());
        if(prefManager.getIdUser().isEmpty()) {
            AndroidNetworking.post(ServerApi.site_url + "register.php")
                    .addBodyParameter("email", emaiReg.getText().toString())
                    .addBodyParameter("password", passReg.getText().toString())
                    .addBodyParameter("nama", namaReg.getText().toString())
                    .addBodyParameter("notelp", telpReg.getText().toString())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("success") == String.valueOf(1)) {
                                    Toast.makeText(getContext(), response.getString("message") + " Hahah", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getContext(), AlamatAdd.class);
//                                    intent.putExtra("IDC", emaiReg.getText().toString());
                                    intent.putExtra("IDCUS", response.getString("idc"));
                                    startActivity(intent);
                                    response.getString("idc");
                                    registlayout.setVisibility(View.GONE);
                                    loginlayout.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("error regis", "error : " + anError);
                        }
                    });
        }else {
            AndroidNetworking.post(ServerApi.site_url + "edRegister.php")
                    .addBodyParameter("idc", prefManager.getIdUser())
                    .addBodyParameter("email", emaiReg.getText().toString())
                    .addBodyParameter("password", passReg.getText().toString())
                    .addBodyParameter("nama", namaReg.getText().toString())
                    .addBodyParameter("notelp", telpReg.getText().toString())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                    if (response.getString("success") == String.valueOf(1)) {
                                    Toast.makeText(getContext(), response.getString("message") + " Hahah", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getContext(), AlamatAdd.class);
//                                    intent.putExtra("IDC", emaiReg.getText().toString());
                                    intent.putExtra("IDCUS", prefManager.getIdUser());
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getContext(), response.getString("message")+"hilih", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("error edit   ", "error : " + anError);
                        }
                    });
        }
    }

    private void logout() {
        swProfile.setRefreshing(true);
        prefManager.setIdUser("");
        prefManager.setLoginStatus(false);
        loginlayout.setVisibility(View.VISIBLE);
        registlayout.setVisibility(View.GONE);
        profilelayout.setVisibility(View.GONE);
        swProfile.setRefreshing(false);
    }

    private void set_loading() {
        home_shimmer.setVisibility(View.VISIBLE);
        layout_koneksi.setVisibility(View.GONE);
//        profilelayout.setVisibility(View.GONE);
//        registlayout.setVisibility(View.GONE);
//        loginlayout.setVisibility(View.GONE);
        home_shimmer.startShimmer();
    }

    private void setError() {
        home_shimmer.setVisibility(View.GONE);
        layout_koneksi.setVisibility(View.VISIBLE);
//        profilelayout.setVisibility(View.GONE);
//        registlayout.setVisibility(View.GONE);
//        loginlayout.setVisibility(View.GONE);
        home_shimmer.stopShimmer();
    }

    private void login(String mail, String pass) {
        set_loading();
        AndroidNetworking.post(ServerApi.site_url + "login.php")
                .addBodyParameter("email", mail)
                .addBodyParameter("password", pass)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String code = response.getString("code");
                            if (code.equals("2")) {
                                snackBar(response.getString("response"), R.color.error);
                                home_shimmer.stopShimmer();
                                home_shimmer.setVisibility(View.GONE);
                                profilelayout.setVisibility(View.GONE);
                                registlayout.setVisibility(View.GONE);
                                loginlayout.setVisibility(View.VISIBLE);
                                swProfile.setRefreshing(false);
                            } else if (code.equals("1")) {
                                String id = response.getString("id_user");
                                prefManager.setIdUser(id);
                                prefManager.setLoginStatus(true);
                                getData();
                                Intent intentp = new Intent(getContext(), MainActivity.class);
                                startActivity(intentp);
                                home_shimmer.stopShimmer();
                                home_shimmer.setVisibility(View.GONE);
                                profilelayout.setVisibility(View.VISIBLE);
                                registlayout.setVisibility(View.GONE);
                                loginlayout.setVisibility(View.GONE);
                                swProfile.setRefreshing(false);
                            } else {
                                snackBar(response.getString("response"), R.color.error);
                                home_shimmer.stopShimmer();
                                home_shimmer.setVisibility(View.GONE);
                                profilelayout.setVisibility(View.GONE);
                                registlayout.setVisibility(View.GONE);
                                loginlayout.setVisibility(View.VISIBLE);
                                swProfile.setRefreshing(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        setError();
                    }
                });
    }

    private void getData() {
        set_loading();
        swProfile.setRefreshing(true);
        AndroidNetworking.post(ServerApi.site_url + "profil.php")
                .addBodyParameter("id_user", prefManager.getIdUser())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject profil = response.getJSONObject(i);
                                nama = profil.getString("nama");
                                nomr = profil.getString("no");
                                email = profil.getString("mail");
                                alamat = profil.getString("alamat");
                                foto = profil.getString("foto");

                                namauser.setText(nama);
                                nomoruser.setText(nomr);
                                emailuser.setText(email);
                                alamatuser.setText(alamat);
                                Picasso.get().load(foto).into(fotouser);
                                swProfile.setRefreshing(false);
                                home_shimmer.stopShimmer();
                                home_shimmer.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        setError();
                    }
                });
    }

    private void snackBar(String pesan, int warna) {
        Snackbar snackbar = Snackbar.make(loginlayout, pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getContext(), warna));
        snackbar.show();

    }


}
