package com.genbi.genbintb;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EmailVerification extends AppCompatActivity {
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    String kode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification);
        if(!checkKoneksi()){
            Intent intent = new Intent(getApplicationContext(), Koneksi.class);
            startActivity(intent);
        }

        sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);

        String EmailSementara = sharedPreferences.getString(ManajemenSesi.SEM_EMAIL,null);
        String NamaSementara = sharedPreferences.getString(ManajemenSesi.SEM_NAMA,null);
        String PanggilanSementara = sharedPreferences.getString(ManajemenSesi.SEM_PANGGILAN,null);
        String PasswordSementara = sharedPreferences.getString(ManajemenSesi.SEM_PW,null);

        VerifikasiEmail(EmailSementara);

        TextView kirimUlang = findViewById(R.id.kirimUlang);
        TextView verifikasi = findViewById(R.id.verifikasi);
        EditText kodeInput = findViewById(R.id.kodeInput);

        verifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String KodeInput = kodeInput.getText().toString();
                if(KodeInput.equals(kode)){
                    MembuatDataServer(EmailSementara, PasswordSementara, PanggilanSementara, NamaSementara);
                }else {
                    Toast.makeText(getApplicationContext(),"Nomor yang dimasukan salah! "+kode, Toast.LENGTH_SHORT).show();
                }
            }
        });

        kirimUlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifikasiEmail(EmailSementara);
            }
        });
    }

    public void VerifikasiEmail(final String email){
        if(checkKoneksi()){
//            progressDialog.show();
//            progressDialog.setMessage("Memeriksa akun");
            final RequestQueue requestQueue= Volley.newRequestQueue(EmailVerification.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_VERIFIKASI,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String res = jsonObject.getString("respone");
                                String KodeJSON = jsonObject.getString("kode");
                                if(res.equals("OK")){
                                    kode = KodeJSON;
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    Toast.makeText(getApplicationContext(), "Berhasil mendaftar", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), "Verifikasi telah dikirimkan ke email anda!", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(getApplicationContext(), DaftarDetail.class);
//                                    startActivity(intent);

                                }else if(res.equals("[{\"status\":\"READY\"}]")){
                                    Toast.makeText(getApplicationContext(), "Akun sudah digunakan", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getApplicationContext(), res+" jsdkajska", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Terjadi error pada sistem", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "Koneksi tidak ada!", Toast.LENGTH_SHORT).show();
        }
    }

    public void MembuatDataServer(final String email, final String password, final String firstname, final String lastname){
        if(checkKoneksi()){
//            progressDialog.show();
//            progressDialog.setMessage("Memeriksa akun");
            final RequestQueue requestQueue= Volley.newRequestQueue(EmailVerification.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_REGISTER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String res = jsonObject.getString("respone");
//                                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
                                if(res.equals("OK")){
                                    sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(ManajemenSesi.KEY_TUNTAS,"0");
                                    editor.putString(ManajemenSesi.KEY_EMAIL,email);
                                    editor.apply();
                                    editor.commit();
                                    Toast.makeText(getApplicationContext(), "Berhasil mendaftar", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), DaftarDetail.class);
                                    startActivity(intent);

                                }else if(res.equals("READY")){
                                    Toast.makeText(getApplicationContext(), "Akun sudah digunakan", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Terjadi error pada sistem", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("email",email);
                    params.put("password",password);
                    params.put("firstname",firstname);
                    params.put("lastname",lastname);
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
}