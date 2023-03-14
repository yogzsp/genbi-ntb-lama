package com.genbi.genbintb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainMenu extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    TextView LogOut;
    private int IMG_REQUEST = 21;
    ImageView FotoProfile;

    private Bitmap bitmap;

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        if(!checkKoneksi()){
            Intent intent = new Intent(getApplicationContext(), Koneksi.class);
            startActivity(intent);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        return true;
                    case  R.id.data:
                        startActivity(new Intent(getApplicationContext(),DataUser.class));
                        overridePendingTransition(0,0);
                        return true;
                    case  R.id.profile:
                        startActivity(new Intent(getApplicationContext(),ProfileUser.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

//        NavController navController = Navigation.findNavController(this,  R.id.fragmentNav);

//        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        TextView namaText = (TextView) findViewById(R.id.TextNama);
        TextView namaPengguna = (TextView) findViewById(R.id.NamaPengguna);
        TextView statusPengguna = (TextView) findViewById(R.id.StatusPengguna);
        FotoProfile = (ImageView) findViewById(R.id.FotoProfile);

        sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);
        ProgressDialog progressDialog;
        TextView LogOut;
        String email = sharedPreferences.getString(ManajemenSesi.KEY_EMAIL,null);
        String username = sharedPreferences.getString(ManajemenSesi.KEY_USERNAME,null);
        String namaUser = sharedPreferences.getString(ManajemenSesi.KEY_NAMA,null);
        String universitasUser = sharedPreferences.getString(ManajemenSesi.KEY_UNIVERSITAS,null);
        String NoHP = sharedPreferences.getString(ManajemenSesi.KEY_NOHP,null);
        String FotoProfileURL = sharedPreferences.getString(ManajemenSesi.KEY_FOTO_PROFILE,null);
        String JenisKelamin = sharedPreferences.getString(ManajemenSesi.KEY_JENISKELAMIN,null);
        CheckEmail(email);

//        LoadImage loadImage = new LoadImage(FotoProfile);
//        loadImage.execute(FotoProfileURL);
//        Picasso.with(this).load(FotoProfileURL)
//                .into(FotoProfile);
        Picasso.get().load(FotoProfileURL)
                .into(FotoProfile);

        namaText.setText("Hi, "+username);
        namaPengguna.setText(namaUser);
        statusPengguna.setText("Mahasiswa "+universitasUser);


//        CheckAkun(email);


    }

//    private class LoadImage extends AsyncTask<String,Void,Bitmap> {
//        ImageView imageView;
//        public LoadImage(ImageView FotoProfile){
//            this.imageView = FotoProfile;
//        }
//
//
//        @Override
//        protected Bitmap doInBackground(String... strings) {
//            String UrlLink = strings[0];
//            Bitmap bitmap = null;
//            try {
//                InputStream inputStream = new java.net.URL(UrlLink).openStream();
//                bitmap = BitmapFactory.decodeStream(inputStream);
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//            return bitmap;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            FotoProfile.setImageBitmap(bitmap);
//        }
//    }


    public void CheckEmail(final String email){
        if(checkKoneksi()){
//            progressDialog.show();
//            progressDialog.setMessage("Memeriksa akun");
            final RequestQueue requestQueue= Volley.newRequestQueue(MainMenu.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_CHECKEMAIL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String res = jsonObject.getString("respone");
                                if(res.equals("OK")){
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.commit();
                                    Toast.makeText(getApplicationContext(), "Oppss.. akun anda sudah dihapus oleh admin. daftarkan kembali dengan format yang benar!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),Login.class);
                                    startActivity(intent);
                                    finish();
                                }else if(res.equals("READY")){
//                                    Toast.makeText(getApplicationContext(), "Akun sudah digunakan", Toast.LENGTH_SHORT).show();
                                }else {
//                                    Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Intent intent = new Intent(getApplicationContext(), Koneksi.class);
                    startActivity(intent);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("email",email);
                    return params;
                }
            };

            VolleyConnection.getInstance(getApplicationContext()).addToRequestQue(stringRequest);

//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    progressDialog.cancel();
//                }
//            }, 2000);
        }else {
            Intent intent = new Intent(getApplicationContext(), Koneksi.class);
            startActivity(intent);
        }
    }

    public boolean checkKoneksi(){
        ConnectivityManager koneksiManajer = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo koneksiInfo = koneksiManajer.getActiveNetworkInfo();
        return (koneksiInfo != null && koneksiInfo.isConnected());
    }

//    public void CheckAkun(final String email){
//        if(checkKoneksi()){
////            progressDialog.show();
////            progressDialog.setMessage("Memeriksa akun");
//            final RequestQueue requestQueue= Volley.newRequestQueue(MainMenu.this);
//            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_CHECKEMAIL,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                String res = jsonObject.getString("respone");
//                                if(res.equals("OK")){
//                                    Toast.makeText(getApplicationContext(), "Anda sudah logout, kemungkinan akun dihapus!", Toast.LENGTH_SHORT).show();
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.clear();
//                                    editor.commit();
//                                    Intent intent = new Intent(getApplicationContext(),Login.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Intent intent = new Intent(getApplicationContext(), Koneksi.class);
//                    startActivity(intent);
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String,String> params = new HashMap<>();
//                    params.put("email",email);
//                    return params;
//                }
//            };
//
//            VolleyConnection.getInstance(getApplicationContext()).addToRequestQue(stringRequest);
//
////            new Handler().postDelayed(new Runnable() {
////                @Override
////                public void run() {
////                    progressDialog.cancel();
////                }
////            }, 2000);
//        }else {
//            Intent intent = new Intent(getApplicationContext(), Koneksi.class);
//            startActivity(intent);
//        }
//    }
}