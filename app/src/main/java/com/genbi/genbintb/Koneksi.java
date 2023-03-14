package com.genbi.genbintb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Koneksi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.koneksi);
    }
    @Override
    public void onBackPressed() {

    }

    public void Refresh(View view) {
        if(checkKoneksi()){
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"Pastikan jaringan tersedia!",Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkKoneksi(){
        ConnectivityManager koneksiManajer = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo koneksiInfo = koneksiManajer.getActiveNetworkInfo();
        return (koneksiInfo != null && koneksiInfo.isConnected());
    }
}