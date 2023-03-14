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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    EditText Email,Password;
    TextView Masuk;
    TextView newUser,ForgotPassword;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        if(!checkKoneksi()){
            Intent intent = new Intent(getApplicationContext(), Koneksi.class);
            startActivity(intent);
        }

        newUser = (TextView) findViewById(R.id.NewUser);
        sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);

        String EmailShared = sharedPreferences.getString(ManajemenSesi.KEY_EMAIL,null);
        String TuntasShared = sharedPreferences.getString(ManajemenSesi.KEY_TUNTAS,null);



//        Toast.makeText(getApplicationContext(),TuntasShared+" dksal",Toast.LENGTH_LONG).show();

        if(EmailShared != null){
            if(TuntasShared.equals("1")) {
                Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(getApplicationContext(), DaftarDetail.class);
                startActivity(intent);
                finish();
            }
        }

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Daftar.class);
                startActivity(intent);
            }
        });

        Email = (EditText) findViewById(R.id.Email);
        Password = (EditText) findViewById(R.id.Password);
        Masuk = (TextView) findViewById(R.id.masuk);
        progressDialog = new ProgressDialog(Login.this);

        Masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String semail = Email.getText().toString();
                String spassword = Password.getText().toString();
                if(!isEmailValid(semail)){
                    Email.setError("Masukan email dengan benar!");
                }else {
                    if(spassword.trim().length() < 8){
                        Password.setError("Password minimal 8 karakter!");
                    }else {
                        MembuatDataServer(semail, spassword);
                    }
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

    public void MembuatDataServer(final String email, final String password){
        if(checkKoneksi()){
            progressDialog.show();
            progressDialog.setMessage("Memeriksa akun");
            final RequestQueue requestQueue= Volley.newRequestQueue(Login.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                System.out.println(response);
                                String res = jsonObject.getString("respone");
                                String usernameJSON = jsonObject.getString("username");
                                String nohpJSON = jsonObject.getString("nohp");
                                String namaJSON = jsonObject.getString("nama");
                                String universitasJSON = jsonObject.getString("universitas");
                                String fotoprofileJSON = jsonObject.getString("profile");
                                String jeniskelaminJSON = jsonObject.getString("jeniskelamin");
                                String tanggalLahirJSON = jsonObject.getString("tanggallahir");
                                String jurusanJSON = jsonObject.getString("jurusan");
                                if(res.equals("OK")){
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(ManajemenSesi.KEY_EMAIL,email);
                                    editor.putString(ManajemenSesi.KEY_USERNAME,usernameJSON);
                                    editor.putString(ManajemenSesi.KEY_NOHP,nohpJSON);
                                    editor.putString(ManajemenSesi.KEY_NAMA,namaJSON);
                                    editor.putString(ManajemenSesi.KEY_UNIVERSITAS,universitasJSON);
                                    editor.putString(ManajemenSesi.KEY_JENISKELAMIN,jeniskelaminJSON);
                                    editor.putString(ManajemenSesi.KEY_FOTO_PROFILE,fotoprofileJSON);
                                    editor.putString(ManajemenSesi.KEY_JURUSAN,jurusanJSON);
                                    editor.putString(ManajemenSesi.KEY_TGLLAHIR,tanggalLahirJSON);
                                    editor.putString(ManajemenSesi.KEY_TUNTAS,"1");
                                    editor.apply();
                                    Intent intent = new Intent(getApplicationContext(),MainMenu.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(getApplicationContext(),"Selamat datang!",Toast.LENGTH_LONG).show();
                                }else if(res.equals("OKE")){
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(ManajemenSesi.KEY_EMAIL,email);
                                    editor.putString(ManajemenSesi.KEY_EMAIL,usernameJSON);
                                    editor.putString(ManajemenSesi.KEY_NAMA,namaJSON);
                                    editor.putString(ManajemenSesi.KEY_TUNTAS,"0");
                                    editor.apply();

                                    Intent intent = new Intent(getApplicationContext(),DaftarDetail.class);
                                    startActivity(intent);
                                }else if(res.equals("NO")){
                                    Toast.makeText(getApplicationContext(), "Akun belum terdaftar", Toast.LENGTH_SHORT).show();
                                }else if(res.equals("WRONG")){
                                    Toast.makeText(getApplicationContext(), "Email atau password salah!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "slda;"+res, Toast.LENGTH_SHORT).show();
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

    public void LupaPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), Email.class);
        startActivity(intent);
    }
}