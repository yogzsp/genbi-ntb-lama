package com.genbi.genbintb;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DaftarDetail extends AppCompatActivity {
    EditText noHP,Alamat,TempatLahir,TanggalLahir,Jurusan;
    RadioGroup GroupRadio;
    RadioButton radioButton;
    TextView Konfirmasi;
    DatePickerDialog.OnDateSetListener setListener;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;

    String[] Universitas = {"Universitas Sumbawa", "Universitas Mataram", "Universitas Negeri Islam Mataram", "Universitas Pendidikan Mandalika", "Universitas Hamzanwaldi", "Universitas Bumigora", "UNIQBA"};
    AutoCompleteTextView pilihUniversitas;
    ArrayAdapter<String> adapterItems;

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar_detail);
        if(!checkKoneksi()){
            Intent intent = new Intent(getApplicationContext(), Koneksi.class);
            startActivity(intent);
        }

        pilihUniversitas = findViewById(R.id.pilihUniversitas);

        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,Universitas);

        pilihUniversitas.setAdapter(adapterItems);

        pilihUniversitas.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String item = parent.getItemAtPosition(position).toString();
            }
        });

        sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);

        String EmailShared = sharedPreferences.getString(ManajemenSesi.KEY_EMAIL,null);
        String TuntasShared = sharedPreferences.getString(ManajemenSesi.KEY_TUNTAS,null);

        noHP = (EditText) findViewById(R.id.NoHP);
        Alamat = (EditText) findViewById(R.id.Alamat);
        TempatLahir = (EditText) findViewById(R.id.TempatLahir);
        Jurusan = findViewById(R.id.Jurusan);


        TanggalLahir = (EditText) findViewById(R.id.TanggalLahir);
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date = dayOfMonth+"/"+month+"/"+year;
                TanggalLahir.setText(date);
            }
        };

        TanggalLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(DaftarDetail.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int day) {
//                        month = month+1;
//                        String date = day+"/"+month+"/"+year;
//                        TanggalLahir.setText(date);
//                    }
//                },year,month,day);
//                datePickerDialog.show();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        DaftarDetail.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        setListener,2000,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        GroupRadio = findViewById(R.id.radioGroup);

        Konfirmasi = (TextView) findViewById(R.id.Konfirmasi);
        progressDialog = new ProgressDialog(DaftarDetail.this);
        String Email = EmailShared;

        Konfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idRadio = GroupRadio.getCheckedRadioButtonId();
                radioButton = findViewById(idRadio);

                String snohp = noHP.getText().toString();
                String salamat = Alamat.getText().toString();
                String semail = Email;
                String suniversitas = pilihUniversitas.getText().toString();
                String sjurusan = Jurusan.getText().toString();
                String stempatlahir = TempatLahir.getText().toString();
                String stanggallahir = TanggalLahir.getText().toString();
                String sjeniskelamin = radioButton.getText().toString();

                int cError = 0;
                if(snohp.trim().length() < 10){
                    noHP.setError("Minimal 10 karakter");
                    cError++;
                }
                if(salamat.trim().length() < 5){
                    Alamat.setError("Minimal 5 karakter!");
                    cError++;
                }
                if(stempatlahir.trim().length() < 5){
                    TempatLahir.setError("Minimal 5 karakter");
                    cError++;
                }
                if(sjurusan.trim().length() < 3){
                    Jurusan.setError("Minimal 3 karakter");
                    cError++;
                }
                if(stanggallahir.trim().length() < 5){
                    TanggalLahir.setError("Isi data terlebih dahulu!");
                    cError++;
                }
                if(sjeniskelamin.trim().length() < 3){
                    sjeniskelamin = "Pria";
                    cError++;
                }
                if(suniversitas.trim().length() < 3){
                    pilihUniversitas.setError("Isi data terlebih dahulu!");
                    cError++;
                }
                if(cError == 0) {
                    MembuatDataServer(semail, snohp,suniversitas,sjurusan, salamat, stempatlahir,stanggallahir,sjeniskelamin);
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

    public void MembuatDataServer(final String email, final String nohp,final  String universitas,final String jurusan, final String alamat, final String tempatlahir, final String tanggallahir, final String jeniskelamin){
        if(checkKoneksi()){
            progressDialog.show();
            progressDialog.setMessage("Memeriksa akun");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                }
            }, 2000);

            final RequestQueue requestQueue= Volley.newRequestQueue(DaftarDetail.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_UPDATE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String res = jsonObject.getString("respone");
                                String username = jsonObject.getString("username");
                                String nohp = jsonObject.getString("nohp");
                                String nama = jsonObject.getString("nama");
                                String fotoprofileJSON = jsonObject.getString("profile");
                                String jeniskelamin = jsonObject.getString("jeniskelamin");
                                if(res.equals("OK")){
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(ManajemenSesi.KEY_EMAIL,email);
                                    editor.putString(ManajemenSesi.KEY_USERNAME,username);
                                    editor.putString(ManajemenSesi.KEY_NOHP,nohp);
                                    editor.putString(ManajemenSesi.KEY_JURUSAN,jurusan);
                                    editor.putString(ManajemenSesi.KEY_TGLLAHIR,tanggallahir);
                                    editor.putString(ManajemenSesi.KEY_NAMA,nama);
                                    editor.putString(ManajemenSesi.KEY_JENISKELAMIN,jeniskelamin);
                                    editor.putString(ManajemenSesi.KEY_FOTO_PROFILE,fotoprofileJSON);
                                    editor.putString(ManajemenSesi.KEY_UNIVERSITAS,universitas);
                                    editor.putString(ManajemenSesi.KEY_TUNTAS,"1");
                                    editor.apply();
                                    Toast.makeText(getApplicationContext(),"Selamat datang!",Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getApplicationContext(), MainMenu.class);
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
                    Toast.makeText(getApplicationContext(), "Terjadi error pada sistem", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("email",email);
                    params.put("Universitas",universitas);
                    params.put("jurusan",jurusan);
                    params.put("NoHP",nohp);
                    params.put("alamat",alamat);
                    params.put("TempatLahir",tempatlahir);
                    params.put("TanggalLahir",tanggallahir);
                    params.put("JenisKelamin",jeniskelamin);
                    return params;
                }
            };

            VolleyConnection.getInstance(getApplicationContext()).addToRequestQue(stringRequest);
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