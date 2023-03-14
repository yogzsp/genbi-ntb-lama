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

public class ResetPassword extends AppCompatActivity {
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
    }

    public void VerifikasiPassword(View view) {
        EditText password = findViewById(R.id.PasswordLama);
        EditText passwordBaru = findViewById(R.id.PasswordBaru);
        EditText repassword = findViewById(R.id.RePassword);

        String spassword = password.getText().toString();
        String spasswordbaru = passwordBaru.getText().toString();
        String srepassword = repassword.getText().toString();

        sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);

        String EmailShared = sharedPreferences.getString(ManajemenSesi.KEY_EMAIL,null);

        int Kesalahan = 0;

        if(spasswordbaru.equals(srepassword)) {
            if (spasswordbaru.length() < 8) {
                repassword.setError("Password minimal 8 karakter!");
                Kesalahan++;
            }
            if (srepassword.length() < 8) {
                repassword.setError("Password minimal 8 karakter!");
                Kesalahan++;
            }
            if (spassword.length() < 8) {
                repassword.setError("Password minimal 8 karakter!");
                Kesalahan++;
            }

            if (Kesalahan == 0) {
                MembuatDataServer(EmailShared, spassword, spasswordbaru);
            }
        }else {
            repassword.setError("Password yang dimasukan ulang salah!");
        }
    }

    public void MembuatDataServer(final String email, final String password, final String passwordbaru){
        if(checkKoneksi()){
//            progressDialog.show();
//            progressDialog.setMessage("Memeriksa akun");
            final RequestQueue requestQueue= Volley.newRequestQueue(ResetPassword.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_RESETPASSWORD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                System.out.println(response);
                                String res = jsonObject.getString("respone");
                                if(res.equals("OK")){
                                    Toast.makeText(getApplicationContext(),"Berhasil mengganti password!",Toast.LENGTH_LONG).show();
                                    finish();
                                }else if(res.equals("NO")){
                                    Toast.makeText(getApplicationContext(), "Akun belum terdaftar", Toast.LENGTH_SHORT).show();
                                }else if(res.equals("WRONG")){
                                    Toast.makeText(getApplicationContext(), "password salah!", Toast.LENGTH_SHORT).show();
                                }else{
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
                    params.put("passwordbaru",passwordbaru);
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