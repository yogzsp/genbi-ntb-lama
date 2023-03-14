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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email extends AppCompatActivity {
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email);
        if(!checkKoneksi()){
            Intent intent = new Intent(getApplicationContext(), Koneksi.class);
            startActivity(intent);
        }
    }

    public void MembuatDataServer(final String email){
        if(checkKoneksi()){
//            progressDialog.show();
//            progressDialog.setMessage("Memeriksa akun");
            final RequestQueue requestQueue= Volley.newRequestQueue(Email.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_CHECKEMAIL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String res = jsonObject.getString("respone");
                                if(res.equals("OK")){
                                    Toast.makeText(getApplicationContext(), "Akun belum di daftarkan", Toast.LENGTH_SHORT).show();

                                }else if(res.equals("[{\"status\":\"READY\"}]")){
                                    sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(ManajemenSesi.SEM_EMAIL,email);
                                    editor.apply();
//                                    Toast.makeText(getApplicationContext(), "Berhasil mendaftar"+email, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), EmailVerify.class);
                                    startActivity(intent);
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

    public void VerifikasiEmail(View view) {
        EditText email = findViewById(R.id.Email);

        String semail = email.getText().toString();
        if(!isEmailValid(semail)){
            email.setError("Masukan email dengan benar!");
        }else {
            MembuatDataServer(semail);
        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}