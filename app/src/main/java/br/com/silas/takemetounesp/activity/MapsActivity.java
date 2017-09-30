package br.com.silas.takemetounesp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import br.com.silas.takemetounesp.R;
import br.com.silas.takemetounesp.task.RotaTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final LatLng UNESP = new LatLng(-22.331382, -49.160657);

    private GoogleMap mapa;
    private LatLng location;
    private LoaderManager loader;
    private ArrayList<LatLng> rota;
    private LoaderManager.LoaderCallbacks<List<LatLng>> callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        loader = getSupportLoaderManager();
        callback = new LoaderManager.LoaderCallbacks<List<LatLng>>() {

            @Override
            public Loader<List<LatLng>> onCreateLoader(int i, Bundle bundle) {
                return new RotaTask(MapsActivity.this, location, UNESP);
            }

            @Override
            public void onLoadFinished(final Loader<List<LatLng>> listLoader,
                                               final List<LatLng> latLngs) {

                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        if (latLngs != null && !latLngs.isEmpty()) {
                            rota = new ArrayList<>(latLngs);
                            criarMapa();
                        }
                    }

                });

            }

            @Override
            public void onLoaderReset(Loader<List<LatLng>> listLoader) {
            }

        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        location = getCurrentLocation();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (location != null) {
                    loader.initLoader(1, null, callback);
                }
            }

        }, 2000);
    }

    private void criarMapa() {
        mapa.clear();

        mapa.addMarker(new MarkerOptions().position(UNESP)
                .title(getString(R.string.destination_label)))
                .showInfoWindow();

        LatLngBounds area = LatLngBounds.builder()
                .include(location)
                .include(UNESP)
                .build();

        mapa.animateCamera(CameraUpdateFactory.newLatLngBounds(area, 99));

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(rota)
                .width(5)
                .color(Color.RED)
                .visible(true);

        mapa.addPolyline(polylineOptions);
    }

    private LatLng getCurrentLocation() {
        try {
            mapa.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (location != null) {
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (SecurityException e) {
            mostrarMensagemErro();
        } catch (Exception e) {
            mostrarMensagemErro();
        }
        return null;
    }

    private void mostrarMensagemErro() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.error_location);
        builder.setCancelable(false);

        builder.setPositiveButton(
                R.string.error_location_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
