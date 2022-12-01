package br.com.caelum.cadastro;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import br.com.caelum.cadastro.fragment.MapaFragment;

import com.google.android.gms.maps.model.LatLng;

public class AtualizadorDeLocalizacao implements LocationListener {
	
	private MapaFragment mapa;
	private LocationManager manager;

	public AtualizadorDeLocalizacao(Context context, MapaFragment mapa) {
		this.mapa = mapa;

		manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		int distanciaMinima = 20;
		int tempoMinimo = 2000;
		
//		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tempoMinimo, distanciaMinima, this);
		manager.requestLocationUpdates(getMelhorProvider(), tempoMinimo, distanciaMinima, this);
	}

	public void cancela() {
		manager.removeUpdates(this);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		double latitude  = location.getLatitude();
		double longitude = location.getLongitude();
		
		LatLng local = new LatLng(latitude, longitude);
		
		mapa.centralizaNo(local);
	}

	@Override
	public void onProviderDisabled(String provider) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }
	
	private String getMelhorProvider() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		String provider = manager.getBestProvider(criteria, true);
		return provider;
	}

}
