package br.com.caelum.cadastro.fragment;

import java.util.List;

import br.com.caelum.cadastro.dao.AlunoDAO;
import br.com.caelum.cadastro.modelo.Aluno;
import br.com.caelum.cadastro.util.Localizador;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaFragment extends SupportMapFragment {

	@Override
	public void onResume() {
		super.onResume();
		
		Localizador coderUtil = new Localizador(getActivity());
        LatLng local = coderUtil.getCoordenada("Rua Ouvidor, 50, Centro, Rio de Janeiro");
        this.centralizaNo(local);
        
		AlunoDAO dao = new AlunoDAO(getActivity());
		List<Aluno> alunos = dao.getLista();
		dao.close();

		for (Aluno aluno : alunos) {
			Localizador localizador = new Localizador(getActivity());
			LatLng coordenada = localizador.getCoordenada(aluno.getEndereco());
			if (coordenada != null) {
				MarkerOptions marcador = new MarkerOptions().position(coordenada)
															.title(aluno.getNome())
															.snippet(aluno.getEndereco());
				getMap().addMarker(marcador);
			}
		}		
	}

	public void centralizaNo(LatLng local) {
        GoogleMap mapa = this.getMap();
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 17));
    }
	
}
