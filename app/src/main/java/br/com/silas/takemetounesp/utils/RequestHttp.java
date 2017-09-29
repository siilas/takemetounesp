package br.com.silas.takemetounesp.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RequestHttp {

    public static List<LatLng> carregarRota(LatLng origem, LatLng destino) {
        try {
            String urlRota = String.format(Locale.US,
                    "http://maps.googleapis.com/maps/api/directions/json?" +
                            "origin=%f,%f&destination=%f,%f&" +
                            "sensor=true&mode=driving",
                    origem.latitude, origem.longitude,
                    destino.latitude, destino.longitude);
            URL url = new URL(urlRota);
            String result = bytesParaString(url.openConnection().getInputStream());

            JSONObject json = new JSONObject(result);
            JSONObject jsonRoute = json.getJSONArray("routes").getJSONObject(0);
            JSONObject leg = jsonRoute.getJSONArray("legs").getJSONObject(0);

            JSONArray steps = leg.getJSONArray("steps");

            final int numSteps = steps.length();

            JSONObject step;
            List<LatLng> posicoes = new ArrayList<>();
            for (int i = 0; i < numSteps; i++) {
                step = steps.getJSONObject(i);
                String pontos = step.getJSONObject("polyline").getString("points");
                posicoes.addAll(PolyUtil.decode(pontos));
            }
            return posicoes;
        } catch (Exception e) {
            return null;
        }
    }

    private static String bytesParaString(InputStream is) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bufferzao = new ByteArrayOutputStream();
        int bytesLidos;
        while ((bytesLidos = is.read(buffer)) != -1) {
            bufferzao.write(buffer, 0, bytesLidos);
        }
        return new String(bufferzao.toByteArray(), "UTF-8");
    }

}
