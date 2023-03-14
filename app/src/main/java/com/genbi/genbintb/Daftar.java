package com.genbi.genbintb;

import androidx.annotation.Nullable;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Daftar extends AppCompatActivity {
    EditText firstName,lastName,Email,Password,RePassword;
    TextView Daftar;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar);
        if(!checkKoneksi()){
            Intent intent = new Intent(getApplicationContext(), Koneksi.class);
            startActivity(intent);
        }

        sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);

        String EmailShared = sharedPreferences.getString(ManajemenSesi.KEY_EMAIL,null);
        String TuntasShared = sharedPreferences.getString(ManajemenSesi.KEY_TUNTAS,null);

        firstName = (EditText) findViewById(R.id.FirstName);
        lastName = (EditText) findViewById(R.id.LastName);
        Email = (EditText) findViewById(R.id.Email);
        Password = (EditText) findViewById(R.id.Password);
        RePassword = (EditText) findViewById(R.id.RePassword);
        Daftar = (TextView) findViewById(R.id.Daftar);
        progressDialog = new ProgressDialog(Daftar.this);


        Daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sfirstname = firstName.getText().toString();
                String slastname = lastName.getText().toString();
                String semail = Email.getText().toString();
                String spassword = Password.getText().toString();
                String srepassword = RePassword.getText().toString();
                if(spassword.equals(srepassword)){
                    int cError = 0;
                    if(sfirstname.trim().length() < 3){
                        firstName.setError("Minimal 3 karakter");
                        cError++;
                    }
                    if(sfirstname.contains(" ")){
                        firstName.setError("Tidak boleh menggunakan spasi!");
                        cError++;
                    }
                    if(spassword.trim().length() < 8){
                        Password.setError("Minimal 8 karakter");
                        cError++;
                    }
                    if(!isEmailValid(semail)){
                        Email.setError("Pastikan email anda benar!");
                        cError++;
                    }
                    if(cError == 0) {
                        MembuatDataServer(semail, spassword, sfirstname, slastname);
                    }
                }else {
                    RePassword.setError("Password tidak cocok");
                }
            }
        });
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void MembuatDataServer(final String email, final String password, final String firstname, final String lastname){
        if(checkKoneksi()){
            progressDialog.show();
            progressDialog.setMessage("Memeriksa akun");
            final RequestQueue requestQueue= Volley.newRequestQueue(Daftar.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_CHECKEMAIL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String res = jsonObject.getString("respone");
                                if(res.equals("OK")){
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(ManajemenSesi.SEM_EMAIL,email);
                                    editor.putString(ManajemenSesi.SEM_PANGGILAN,firstname);
                                    editor.putString(ManajemenSesi.SEM_NAMA,lastname);
                                    editor.putString(ManajemenSesi.SEM_PW,password);
                                    editor.apply();
                                    editor.commit();
//                                    Toast.makeText(getApplicationContext(), "Berhasil mendaftar", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), EmailVerification.class);
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
                    Intent intent = new Intent(getApplicationContext(), Koneksi.class);
                    startActivity(intent);
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

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                }
            }, 2000);
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