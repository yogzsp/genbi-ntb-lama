package com.genbi.genbintb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DataRegistrasi extends AppCompatActivity {

    String Mode;
    EditText NamaAyah, NamaIbu, JumlahSaudara,NamaLk,nim, prodi,universitas,alamat,Payah,Pibu;
    TextView Konfirmasi;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;

    String[] Agama = {"Islam", "Kristen", "Hindu", "Budha", "Katolik", "Khonghucu", "Lainnya"};
    String[] Semester = {"3", "4", "5", "6", "7", "8", "Lainnya"};
    AutoCompleteTextView pilihAgama,pilihSemester;
    ArrayAdapter<String> adapterItems,AdapterSemester;

    private int IMG_REQUEST = 21;
    private int REQ_PDF = 21;
    private String encodedPDF;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_registrasi);
        if(!checkKoneksi()){
            Intent intent = new Intent(getApplicationContext(), Koneksi.class);
            startActivity(intent);
        }


        Konfirmasi = (TextView) findViewById(R.id.TombolSimpan);
        NamaAyah = findViewById(R.id.NamaAyah);
        NamaIbu = findViewById(R.id.NamaIbu);
        NamaLk= findViewById(R.id.NamaLengkap);
        nim= findViewById(R.id.Nim);
        prodi = findViewById(R.id.Prodi);
        universitas = findViewById(R.id.Universitas);
        alamat= findViewById(R.id.Alamat);
        Payah= findViewById(R.id.PekerjaanAyah);
        Pibu= findViewById(R.id.PekerjaanIbu);
        JumlahSaudara = findViewById(R.id.JumlahSaudara);
        progressDialog = new ProgressDialog(DataRegistrasi.this);
        sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);
        String email = sharedPreferences.getString(ManajemenSesi.KEY_EMAIL,null);
        String Universitas = sharedPreferences.getString(ManajemenSesi.KEY_UNIVERSITAS,null);
        String Nama = sharedPreferences.getString(ManajemenSesi.KEY_NAMA,null);
        universitas.setText(Universitas);
        NamaLk.setText(Nama);

        MemeriksaData(email);


        pilihAgama = findViewById(R.id.pilihAgama);
        pilihSemester = findViewById(R.id.Semester);
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,Agama);
        AdapterSemester = new ArrayAdapter<String>(this,R.layout.list_item,Semester);

        pilihAgama.setAdapter(adapterItems);
        pilihSemester.setAdapter(AdapterSemester);

        pilihAgama.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String item = parent.getItemAtPosition(position).toString();
            }
        });

        pilihSemester.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String item = parent.getItemAtPosition(position).toString();
            }
        });

        Konfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String snamaayah = NamaAyah.getText().toString();
                String snamaibu = NamaIbu.getText().toString();
                String sjumlahsaudara = JumlahSaudara.getText().toString();
                String sagama = pilihAgama.getText().toString();
                String snamaleng = NamaLk.getText().toString();
                String snim = nim.getText().toString();
                String sprodi = prodi.getText().toString();
                String ssemester = pilihSemester.getText().toString();
                String salamat = alamat.getText().toString();
                String spayah = Payah.getText().toString();
                String spibu = Pibu.getText().toString();
                String semail = email;

                int cError = 0;
                if(snamaayah.trim().length() < 3){
                    NamaAyah.setError("Minimal 3 karakter");
                    cError++;
                }
                if(snamaibu.trim().length() < 3){
                    NamaIbu.setError("Minimal 3 karakter!");
                    cError++;
                }
                if(sjumlahsaudara.trim().length() < 1){
                    JumlahSaudara.setError("Isi data terlebih dahulu!");
                    cError++;
                }
                if(sagama.trim().length() < 3){
                    pilihAgama.setError("Isi data terlebih dahulu!");
                    cError++;
                }
                if(cError == 0) {
//                    final String NamaLengkap, final String nim, final String prodi, final String semester, final String alamat, final String payah, final String pibu


                    MenyimpanData(semail, snamaayah,snamaibu, sagama, sjumlahsaudara, snamaleng, snim, sprodi, ssemester, salamat, spayah,spibu);
                }
            }
        });


    }

    //ProsesTekanTombolUpload

    public void UploadFotoDiri(View view){
        Mode = "FotoDiri";
//        TextView textView = findViewById(R.id.InputFoto);
//        textView.setText("Sedang Upload!");
//        textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
        cropGambar();
    }
    public void UploadFotoKTP(View view){
        Mode = "FotoKTP";
//        TextView textView = findViewById(R.id.inputKtp);
//        textView.setText("Sedang Upload!");
//        textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
        cropGambar();
    }
    public void UploadFotoKK(View view){
        Mode = "FotoKK";
//        TextView textView = findViewById(R.id.inputKK);
//        textView.setText("Sedang Upload!");
//        textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
        cropGambar();
    }
    public void UploadFotoSlipGaji(View view){
        Mode = "FotoSlipGaji";
//        TextView textView = findViewById(R.id.inputSGaji);
//        textView.setText("Sedang Upload!");
//        textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
        cropGambar();
    }
    public void UploadPDFTNilai(View view){
        Mode = "PDFTNilai";
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent = Intent.createChooser(intent, "Pilih File");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQ_PDF);
//        TextView textView = findViewById(R.id.inputTNilai);
//        textView.setText("Sedang Upload!");
//        textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
    }
    public void UploadPDFKHS(View view){
        Mode = "PDFKHS";
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent = Intent.createChooser(intent, "Pilih File");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQ_PDF);
//        TextView textView = findViewById(R.id.inputKHS);
//        textView.setText("Sedang Upload!");
//        textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
    }
    public void UploadPDFSertifikat(View view){
        Mode = "PDFSertifikat";
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent = Intent.createChooser(intent, "Pilih File");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQ_PDF);
//        TextView textView = findViewById(R.id.inputSertifikat);
//        textView.setText("Sedang Upload!");
//        textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
    }
    public void UploadPDFMotivationLatter(View view){
        Mode = "PDFMotivationL";
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent = Intent.createChooser(intent, "Pilih File");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQ_PDF);
//        TextView textView = findViewById(R.id.inputMLatter);
//        textView.setText("Sedang Upload!");
//        textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
    }
    public void UploadSBeasiswa(View view){
        Mode = "PDFBeasiswa";
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent = Intent.createChooser(intent, "Pilih File");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQ_PDF);
//        TextView textView = findViewById(R.id.inputSBeasiswa);
//        textView.setText("Sedang Upload!");
//        textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
    }




    //ProfileGambar
    private void cropGambar(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }


    private void uploadImage(){
        sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);
        String email = sharedPreferences.getString(ManajemenSesi.KEY_EMAIL,null);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        byte[] imgInByte = byteArrayOutputStream.toByteArray();

        String encodeImage = Base64.encodeToString(imgInByte, Base64.DEFAULT);
        MembuatDataServer(encodeImage,email,Mode);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
//                FotoProfile = (ImageView) findViewById(R.id.FotoProfile);
//                FotoProfile.setImageURI(resultUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    uploadImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }else if(requestCode == REQ_PDF && resultCode == RESULT_OK && data != null){
            Uri path = data.getData();
            try {
                InputStream inputStream = DataRegistrasi.this.getContentResolver().openInputStream(path);
                byte[] PdfInBytes = new byte[inputStream.available()];
                inputStream.read(PdfInBytes);
                encodedPDF = Base64.encodeToString(PdfInBytes, Base64.DEFAULT);
                sharedPreferences = getSharedPreferences(ManajemenSesi.SHARED_PREF_NAME,MODE_PRIVATE);
                String email = sharedPreferences.getString(ManajemenSesi.KEY_EMAIL,null);
                MembuatDataServer(encodedPDF,email,Mode);
            }catch (IOException e){
                e.printStackTrace();
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//    }

    public void MembuatDataServer(final String encodeImage, final String email,final String Mode){
        if(checkKoneksi()){
            if(DataRegistrasi.this.Mode == "FotoDiri"){
                TextView textView = findViewById(R.id.InputFoto);
                textView.setText("Menunggu!");
                textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
            }else if(DataRegistrasi.this.Mode == "FotoKTP"){
                TextView textView = findViewById(R.id.inputKtp);
                textView.setText("Menunggu!");
                textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
            }else if(DataRegistrasi.this.Mode == "FotoKK"){
                TextView textView = findViewById(R.id.inputKK);
                textView.setText("Menunggu!");
                textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
            }else if(DataRegistrasi.this.Mode == "FotoSlipGaji"){
                TextView textView = findViewById(R.id.inputSGaji);
                textView.setText("Menunggu!");
                textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
            }else if(DataRegistrasi.this.Mode == "PDFTNilai"){
                TextView textView = findViewById(R.id.inputTNilai);
                textView.setText("Menunggu!");
                textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
            }else if(DataRegistrasi.this.Mode == "PDFKHS"){
                TextView textView = findViewById(R.id.inputKHS);
                textView.setText("Menunggu!");
                textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
            }else if(DataRegistrasi.this.Mode == "PDFSertifikat"){
                TextView textView = findViewById(R.id.inputSertifikat);
                textView.setText("Menunggu!");
                textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
            }else if(DataRegistrasi.this.Mode == "PDFMotivationL"){
                TextView textView = findViewById(R.id.inputMLatter);
                textView.setText("Menunggu!");
                textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
            }else if(DataRegistrasi.this.Mode == "PDFBeasiswa"){
                TextView textView = findViewById(R.id.inputSBeasiswa);
                textView.setText("Menunggu!");
                textView.setBackgroundResource(R.drawable.radius_kuninggenbi);
            }
//            progressDialog.show();
//            progressDialog.setMessage("Memeriksa akun");
            final RequestQueue requestQueue= Volley.newRequestQueue(DataRegistrasi.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_UPLOAD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String res = jsonObject.getString("respone");

                                if(res.equals("OK")){
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putString(ManajemenSesi.KEY_TUNTAS,"0");
//                                    editor.putString(ManajemenSesi.KEY_EMAIL,email);
//                                    editor.apply();
//                                    editor.commit();
                                    if(DataRegistrasi.this.Mode == "FotoDiri"){
                                        TextView textView = findViewById(R.id.InputFoto);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }else if(DataRegistrasi.this.Mode == "FotoKTP"){
                                        TextView textView = findViewById(R.id.inputKtp);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }else if(DataRegistrasi.this.Mode == "FotoKK"){
                                        TextView textView = findViewById(R.id.inputKK);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }else if(DataRegistrasi.this.Mode == "FotoSlipGaji"){
                                        TextView textView = findViewById(R.id.inputSGaji);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }else if(DataRegistrasi.this.Mode == "PDFTNilai"){
                                        TextView textView = findViewById(R.id.inputTNilai);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }else if(DataRegistrasi.this.Mode == "PDFKHS"){
                                        TextView textView = findViewById(R.id.inputKHS);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }else if(DataRegistrasi.this.Mode == "PDFSertifikat"){
                                        TextView textView = findViewById(R.id.inputSertifikat);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }else if(DataRegistrasi.this.Mode == "PDFMotivationL"){
                                        TextView textView = findViewById(R.id.inputMLatter);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }else if(DataRegistrasi.this.Mode == "PDFBeasiswa"){
                                        TextView textView = findViewById(R.id.inputSBeasiswa);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }
                                    Toast.makeText(getApplicationContext(),"Berhasil mengupload data!",Toast.LENGTH_SHORT).show();

                                }else {
                                    if(DataRegistrasi.this.Mode == "FotoDiri"){
                                        TextView textView = findViewById(R.id.InputFoto);
                                        textView.setText("Gagal Upload");
                                        textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                    }else if(DataRegistrasi.this.Mode == "FotoKTP"){
                                        TextView textView = findViewById(R.id.inputKtp);
                                        textView.setText("Gagal Upload");
                                        textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                    }else if(DataRegistrasi.this.Mode == "FotoKK"){
                                        TextView textView = findViewById(R.id.inputKK);
                                        textView.setText("Gagal Upload");
                                        textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                    }else if(DataRegistrasi.this.Mode == "FotoSlipGaji"){
                                        TextView textView = findViewById(R.id.inputSGaji);
                                        textView.setText("Gagal Upload");
                                        textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                    }else if(DataRegistrasi.this.Mode == "PDFTNilai"){
                                        TextView textView = findViewById(R.id.inputTNilai);
                                        textView.setText("Gagal Upload");
                                        textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                    }else if(DataRegistrasi.this.Mode == "PDFKHS"){
                                        TextView textView = findViewById(R.id.inputKHS);
                                        textView.setText("Gagal Upload");
                                        textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                    }else if(DataRegistrasi.this.Mode == "PDFSertifikat"){
                                        TextView textView = findViewById(R.id.inputSertifikat);
                                        textView.setText("Gagal Upload");
                                        textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                    }else if(DataRegistrasi.this.Mode == "PDFMotivationL"){
                                        TextView textView = findViewById(R.id.inputMLatter);
                                        textView.setText("Gagal Upload");
                                        textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                    }else if(DataRegistrasi.this.Mode == "PDFBeasiswa"){
                                        TextView textView = findViewById(R.id.inputSBeasiswa);
                                        textView.setText("Gagal Upload");
                                        textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                    }
                                    Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                if(DataRegistrasi.this.Mode == "FotoDiri"){
                                    TextView textView = findViewById(R.id.InputFoto);
                                    textView.setText("Gagal Upload");
                                    textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                }else if(DataRegistrasi.this.Mode == "FotoKTP"){
                                    TextView textView = findViewById(R.id.inputKtp);
                                    textView.setText("Gagal Upload");
                                    textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                }else if(DataRegistrasi.this.Mode == "FotoKK"){
                                    TextView textView = findViewById(R.id.inputKK);
                                    textView.setText("Gagal Upload");
                                    textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                }else if(DataRegistrasi.this.Mode == "FotoSlipGaji"){
                                    TextView textView = findViewById(R.id.inputSGaji);
                                    textView.setText("Gagal Upload");
                                    textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                }else if(DataRegistrasi.this.Mode == "PDFTNilai"){
                                    TextView textView = findViewById(R.id.inputTNilai);
                                    textView.setText("Gagal Upload");
                                    textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                }else if(DataRegistrasi.this.Mode == "PDFKHS"){
                                    TextView textView = findViewById(R.id.inputKHS);
                                    textView.setText("Gagal Upload");
                                    textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                }else if(DataRegistrasi.this.Mode == "PDFSertifikat"){
                                    TextView textView = findViewById(R.id.inputSertifikat);
                                    textView.setText("Gagal Upload");
                                    textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                }else if(DataRegistrasi.this.Mode == "PDFMotivationL"){
                                    TextView textView = findViewById(R.id.inputMLatter);
                                    textView.setText("Gagal Upload");
                                    textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                }else if(DataRegistrasi.this.Mode == "PDFBeasiswa"){
                                    TextView textView = findViewById(R.id.inputSBeasiswa);
                                    textView.setText("Gagal Upload");
                                    textView.setBackgroundResource(R.drawable.radius_merahgenbi);
                                }
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
                    params.put("foto",encodeImage);
                    params.put("email",email);
                    params.put("PostMode",Mode);
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
            Intent intent = new Intent(getApplicationContext(), Koneksi.class);
            startActivity(intent);
        }
    }

    public void MenyimpanData(final String email, final String NamaAyah, final String NamaIbu,final String Agama, final String JSaudara, final String NamaLengkap, final String nim, final String prodi, final String semester, final String alamat, final String payah, final String pibu){
        if(checkKoneksi()){
//            progressDialog.show();
//            progressDialog.setMessage("Memeriksa akun");
            final RequestQueue requestQueue= Volley.newRequestQueue(DataRegistrasi.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_REGISTRASI,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String res = jsonObject.getString("respone");

                                if(res.equals("OK")){
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putString(ManajemenSesi.KEY_TUNTAS,"0");
//                                    editor.putString(ManajemenSesi.KEY_EMAIL,email);
//                                    editor.apply();
//                                    editor.commit();
                                    Toast.makeText(getApplicationContext(), "Berhasil menyimpan data!", Toast.LENGTH_SHORT).show();

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
                    params.put("namaayah",NamaAyah);
                    params.put("email",email);
                    params.put("namaibu",NamaIbu);
                    params.put("agama",Agama);
                    params.put("jumlahsaudara",JSaudara);
                    params.put("namalengkap",NamaLengkap);
                    params.put("nim",nim);
                    params.put("prodi",prodi);
                    params.put("semester",semester);
                    params.put("alamat",alamat);
                    params.put("payah",payah);
                    params.put("pibu",pibu);
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
            Intent intent = new Intent(getApplicationContext(), Koneksi.class);
            startActivity(intent);
        }
    }

    public void MemeriksaData(final String email){
        if(checkKoneksi()){
            progressDialog.show();
            progressDialog.setMessage("Sedang memuat data...");
            final RequestQueue requestQueue= Volley.newRequestQueue(DataRegistrasi.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBContact.SERVER_PERIKSA,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String res = jsonObject.getString("respone");

                                if(res.equals("OK")){
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putString(ManajemenSesi.KEY_TUNTAS,"0");
//                                    editor.putString(ManajemenSesi.KEY_EMAIL,email);
//                                    editor.apply();
//                                    editor.commit();
                                    if(jsonObject.getString("FotoDiri").length() > 3){
                                        TextView textView = findViewById(R.id.InputFoto);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }
                                    if(jsonObject.getString("FotoKTP").length() > 3){
                                        TextView textView = findViewById(R.id.inputKtp);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }
                                    if(jsonObject.getString("FotoKK").length() > 3){
                                        TextView textView = findViewById(R.id.inputKK);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }
                                    if(jsonObject.getString("FotoTNilai").length() > 3){
                                        TextView textView = findViewById(R.id.inputTNilai);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }
                                    if(jsonObject.getString("FotoKHS").length() > 3){
                                        TextView textView = findViewById(R.id.inputKHS);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }
                                    if(jsonObject.getString("FotoBeasiswa").length() > 3){
                                        TextView textView = findViewById(R.id.inputSBeasiswa);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }
                                    if(jsonObject.getString("FotoSGaji").length() > 3){
                                        TextView textView = findViewById(R.id.inputSGaji);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }
                                    if(jsonObject.getString("FotoSertifikat").length() > 3){
                                        TextView textView = findViewById(R.id.inputSertifikat);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }
                                    if(jsonObject.getString("FotoMotivation").length() > 3){
                                        TextView textView = findViewById(R.id.inputMLatter);
                                        textView.setText("Sudah Dipilih");
                                        textView.setBackgroundResource(R.drawable.radius_hijaugenbi);
                                    }
                                    if(jsonObject.getString("NamaAyah").length() >= 3){
                                        EditText editText = findViewById(R.id.NamaAyah);
                                        editText.setText(jsonObject.getString("NamaAyah"));
                                    }
                                    if(jsonObject.getString("NamaIbu").length() >= 3){
                                        EditText editText = findViewById(R.id.NamaIbu);
                                        editText.setText(jsonObject.getString("NamaIbu"));
                                    }
                                    if(Integer.parseInt(jsonObject.getString("JumlahSaudara")) > 0 ){
                                        EditText editText = findViewById(R.id.JumlahSaudara);
                                        editText.setText(jsonObject.getString("JumlahSaudara"));
                                    }
                                    if(jsonObject.getString("Agama").length() >= 3){
                                            pilihAgama = findViewById(R.id.pilihAgama);
                                            pilihAgama.setText(jsonObject.getString("Agama"));
                                            adapterItems = new ArrayAdapter(DataRegistrasi.this,R.layout.list_item,Agama);

                                            pilihAgama.setAdapter(adapterItems);

                                            pilihAgama.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                                                    String item = parent.getItemAtPosition(position).toString();
                                                }
                                            });
                                    }
                                    if(Integer.parseInt(jsonObject.getString("semester")) > 0 ){
                                        pilihSemester = findViewById(R.id.Semester);
                                        pilihSemester.setText(jsonObject.getString("semester"));
                                        AdapterSemester = new ArrayAdapter<String>(DataRegistrasi.this,R.layout.list_item,Semester);

                                        pilihSemester.setAdapter(AdapterSemester);

                                        pilihSemester.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                                                String item = parent.getItemAtPosition(position).toString();
                                            }
                                        });
                                    }
                                    if(jsonObject.getString("nim").length() >= 3){
                                        EditText editText = findViewById(R.id.Nim);
                                        editText.setText(jsonObject.getString("nim"));
                                    }
                                    if(jsonObject.getString("prodi").length() >= 3){
                                        EditText editText = findViewById(R.id.Prodi);
                                        editText.setText(jsonObject.getString("prodi"));
                                    }
                                    if(jsonObject.getString("alamat").length() >= 3){
                                        EditText editText = findViewById(R.id.Alamat);
                                        editText.setText(jsonObject.getString("alamat"));
                                    }
                                    if(jsonObject.getString("PekerjaanAyah").length() >= 3){
                                        EditText editText = findViewById(R.id.PekerjaanAyah);
                                        editText.setText(jsonObject.getString("PekerjaanAyah"));
                                    }
                                    if(jsonObject.getString("PekerjaanIbu").length() >= 3){
                                        EditText editText = findViewById(R.id.PekerjaanIbu);
                                        editText.setText(jsonObject.getString("PekerjaanIbu"));
                                    }
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

    public void Kembali(View view) {
        finish();
    }
}