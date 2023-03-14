package com.genbi.genbintb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileUser extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    TextView LogOut;
    private int IMG_REQUEST = 21;
    ImageView FotoProfile;
    EditText TanggalLahir;
    DatePickerDialog.OnDateSetListener setListener;
    String[] UniversitasList = {"Universitas Sumbawa", "Universitas Mataram", "Universitas Negeri Islam Mataram", "Universitas Pendidikan Mandalika", "Universitas Hamzanwaldi", "Universitas Bumigora", "UNIQBA"};
    AutoCompleteTextView pilihUniversitas;
    ArrayAdapter<String> adapterItems;

    private Bitmap bitmap;

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user);
        if(!checkKoneksi()){
            Intent intent = new Intent(getApplicationContext(), Koneksi.class);
            startActivity(intent);
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profile:
                        return true;
                    case  R.id.data:
                        startActivity(new Intent(getApplicationContext(),DataUser.class));
                        overridePendingTransition(0,0);
                        return true;
                    case  R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainMenu.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        TextView namaText = (TextView) findViewById(R.id.TextNama);
        FotoProfile = (ImageView) findViewById(R.id.FotoProfile);
        TextView namaPengguna = (TextView) findViewById(R.id.NamaPengguna);

        sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);
        ProgressDialog progressDialog;
        TextView LogOut;
        String email = sharedPreferences.getString(ManajemenSesi.KEY_EMAIL,null);
        String username = sharedPreferences.getString(ManajemenSesi.KEY_USERNAME,null);
        String namaUser = sharedPreferences.getString(ManajemenSesi.KEY_NAMA,null);
        String NoHP = sharedPreferences.getString(ManajemenSesi.KEY_NOHP,null);
        String TglLahir = sharedPreferences.getString(ManajemenSesi.KEY_TGLLAHIR,null);
        String FotoProfileURL = sharedPreferences.getString(ManajemenSesi.KEY_FOTO_PROFILE,null);
        String JenisKelamin = sharedPreferences.getString(ManajemenSesi.KEY_JENISKELAMIN,null);
        String Universitas = sharedPreferences.getString(ManajemenSesi.KEY_UNIVERSITAS,null);
        String jurusan = sharedPreferences.getString(ManajemenSesi.KEY_JURUSAN,null);

        pilihUniversitas = findViewById(R.id.pilihUniversitas);
        pilihUniversitas.setText(Universitas);
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,UniversitasList);

        pilihUniversitas.setAdapter(adapterItems);

        pilihUniversitas.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String item = parent.getItemAtPosition(position).toString();
            }
        });

//        LoadImage loadImage = new LoadImage(FotoProfile);
//        loadImage.execute(FotoProfileURL);
//        Picasso.with(this).load(FotoProfileURL)
//                .into(FotoProfile);
        Picasso.get().load(FotoProfileURL)
                .into(FotoProfile);
        namaPengguna.setText(namaUser);




        TanggalLahir = (EditText) findViewById(R.id.TanggalLahir);
        TanggalLahir.setText(TglLahir);

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
                        ProfileUser.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        setListener,2000,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });


        EditText HP = findViewById(R.id.NoHP);
        HP.setText(NoHP);

        EditText EmailI = findViewById(R.id.Email);
        EmailI.setText(email);

        EditText JurusanI = findViewById(R.id.JurusanInput);
        JurusanI.setText(jurusan);

        TextView NamaJurusan = findViewById(R.id.NamaJurusan);
        NamaJurusan.setText(jurusan);

        TextView NamaUniversitas = findViewById(R.id.NamaUniversitas);
        NamaUniversitas.setText(Universitas);

        TextView TombolSimpan = findViewById(R.id.TombolSimpan);

        TombolSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String snamaPengguna = namaPengguna.getText().toString();
                String semail = email;
                String snohp = HP.getText().toString();
                String sjurusan = JurusanI.getText().toString();
                String stanggalLahir = TanggalLahir.getText().toString();
                String suniversitas = pilihUniversitas.getText().toString();

                System.out.println(semail+snamaPengguna+snohp+sjurusan+stanggalLahir);

                MenerapkanData(semail,snamaPengguna,snohp,sjurusan,stanggalLahir,suniversitas);
            }
        });
    }

    public void GantiFotoProfile(View view){
//        Picasso.get().load("https://genbintb.000webhostapp.com/images/profile/default.png")
//                .into(FotoProfile);
        cropGambar();
    }
    public void logout(View view){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(getApplicationContext(),Login.class);
        startActivity(intent);
        finish();
    }

    //ProfileGambar
    private void cropGambar(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }
    private void uploadImage(){
        sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);
        String email = sharedPreferences.getString(ManajemenSesi.KEY_EMAIL,null);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        byte[] imgInByte = byteArrayOutputStream.toByteArray();

        String encodeImage = Base64.encodeToString(imgInByte, Base64.DEFAULT);
        MembuatDataServer(encodeImage,email);
//        progressDialog.show();
//        progressDialog.setMessage("Mengupload Gambar");
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                    progressDialog.cancel();
//            }
//        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                FotoProfile = (ImageView) findViewById(R.id.FotoProfile);
                FotoProfile.setImageURI(resultUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    uploadImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
//        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null){
//            Uri path = data.getData();
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
//                imageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void MembuatDataServer(final String encodeImage, final String email){
        if(checkKoneksi()){
//            progressDialog.show();
//            progressDialog.setMessage("Memeriksa akun");
            final RequestQueue requestQueue= Volley.newRequestQueue(ProfileUser.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_UPLOAD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String res = jsonObject.getString("respone");
                                String fotoprofileJSON = jsonObject.getString("profile");

                                if(res.equals("OK")){
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putString(ManajemenSesi.KEY_TUNTAS,"0");
//                                    editor.putString(ManajemenSesi.KEY_EMAIL,email);
//                                    editor.apply();
//                                    editor.commit();
                                    Toast.makeText(getApplicationContext(), "Berhasil mengganti gambar!", Toast.LENGTH_SHORT).show();
                                    sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);
                                    String FotoProfileURL = sharedPreferences.getString(ManajemenSesi.KEY_FOTO_PROFILE,null);
                                    FotoProfile = findViewById(R.id.FotoProfile);
                                    Picasso.get().invalidate(FotoProfileURL);
                                    Picasso.get().load(FotoProfileURL).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE);
//                                    Picasso.get().load(FotoProfileURL)
//                                            .into(FotoProfile);
//                                    Intent intent = new Intent(getApplicationContext(), DaftarDetail.class);
//                                    startActivity(intent);

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(ManajemenSesi.KEY_FOTO_PROFILE,fotoprofileJSON);
                                    editor.apply();

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
                    params.put("Profile_img",encodeImage);
                    params.put("email",email);
                    return params;
                }
            };

            VolleyConnection.getInstance(getApplicationContext()).addToRequestQue(stringRequest);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    progressDialog.cancel();
                }
            }, 2000);
        }else {
            Toast.makeText(getApplicationContext(), "Koneksi tidak ada!", Toast.LENGTH_SHORT).show();
        }
    }






    public void MenerapkanData(final String email, final String nama,final  String nohp,final String jurusan, final String tanggallahir,final  String universitas){
        if(checkKoneksi()){
//            Toast.makeText(getApplicationContext(),"Masuk!",Toast.LENGTH_LONG).show();
//            progressDialog.show();
//            progressDialog.setMessage("Memeriksa akun");

//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    progressDialog.cancel();
//                }
//            }, 2000);

            final RequestQueue requestQueue= Volley.newRequestQueue(ProfileUser.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_PROFILE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String res = jsonObject.getString("respone");
                                if(res.equals("OK")){
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(ManajemenSesi.KEY_NOHP,nohp);
                                    editor.putString(ManajemenSesi.KEY_JURUSAN,jurusan);
                                    editor.putString(ManajemenSesi.KEY_TGLLAHIR,tanggallahir);
                                    editor.putString(ManajemenSesi.KEY_NAMA,nama);
                                    editor.putString(ManajemenSesi.KEY_UNIVERSITAS,universitas);
                                    editor.putString(ManajemenSesi.KEY_TUNTAS,"1");
                                    editor.apply();
                                    TextView NamaUniversitas = findViewById(R.id.NamaUniversitas);
                                    NamaUniversitas.setText(universitas);
                                    TextView NamaJurusan = findViewById(R.id.NamaJurusan);
                                    NamaJurusan.setText(jurusan);
                                    Toast.makeText(getApplicationContext(),"Sukses menyimpan!",Toast.LENGTH_LONG).show();
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
                    params.put("nama",nama);
                    params.put("jurusan",jurusan);
                    params.put("NoHP",nohp);
                    params.put("TanggalLahir",tanggallahir);
                    params.put("universitas",universitas);
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

    public void ResetPassword(View view) {
        Intent intent = new Intent(getApplicationContext(),ResetPassword.class);
        startActivity(intent);
    }
}