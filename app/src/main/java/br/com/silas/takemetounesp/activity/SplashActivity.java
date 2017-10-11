package br.com.silas.takemetounesp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import br.com.silas.takemetounesp.R;

public class SplashActivity extends AppCompatActivity {

    private static final String[] PERMISSOES = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                verificarPermissoes();
                verificarGPS();
            }
        }, 2000);
    }

    private void verificarPermissoes() {
        for (String permissao : PERMISSOES) {
            if (ContextCompat.checkSelfPermission(this, permissao)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] { permissao }, 1);
            }
        }
    }

    private void verificarGPS() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!enabled) {
            mostrarMensagemErro();
        } else {
            mostrarMapa();
        }
    }

    private void mostrarMapa() {
        Intent intent = new Intent(SplashActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    private void mostrarMensagemErro() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.error_message);
        builder.setCancelable(false);

        builder.setPositiveButton(
                R.string.error_message_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        verificarGPS();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
