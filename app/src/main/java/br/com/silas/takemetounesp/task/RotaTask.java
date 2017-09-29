package br.com.silas.takemetounesp.task;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import br.com.silas.takemetounesp.utils.RequestHttp;

public class RotaTask extends AsyncTaskLoader<List<LatLng>> {

    private LatLng origem;
    private LatLng destino;
    private List<LatLng> rota;

    public RotaTask(Context context, LatLng origem, LatLng destino) {
        super(context);
        this.origem = origem;
        this.destino = destino;
    }

    @Override
    protected void onStartLoading() {
        if (rota == null) {
            forceLoad();
        } else {
            deliverResult(rota);
        }
    }

    @Override
    public List<LatLng> loadInBackground() {
        rota = RequestHttp.carregarRota(origem, destino);
        return rota;
    }

}
