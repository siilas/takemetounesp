package br.com.silas.takemetounesp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private static final LatLng UNESP = new LatLng(-22.331382, -49.160657);

    private GoogleMap mapa;
    private LatLng location;
    private LoaderManager loader;
    private ArrayList<LatLng> rota;
    private GoogleApiClient client;
    private ProgressDialog progress;
    private LoaderManager.LoaderCallbacks<List<LatLng>> callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progress = ProgressDialog.show(this, getString(R.string.loading_title),
                getString(R.string.loading_message));
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

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        client.connect();
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

        mapa.animateCamera(CameraUpdateFactory.newLatLngBounds(area, 180));

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(rota)
                .width(3)
                .color(Color.RED)
                .visible(true);

        mapa.addPolyline(polylineOptions);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mapa.setMyLocationEnabled(true);
            LocationRequest request = new LocationRequest();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
        } catch (SecurityException e) {
            mostrarMensagemErro();
        } catch (Exception e) {
            mostrarMensagemErro();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            this.location = new LatLng(location.getLatitude(), location.getLongitude());
            loader.initLoader(1, null, callback);
        } catch (Exception e) {
            mostrarMensagemErro();
        }
        progress.hide();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

}
