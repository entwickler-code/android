package br.notebook.android;

/* Classe Splash que mostra a tela de inicialização */

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

public class Splash extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/notebook/externos/");;
		File exportados = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/notebook/exportados/");;
		if(!path.exists()){
			path.mkdirs();
		}
		if(!exportados.exists()){
			exportados.mkdirs();
		}
		setContentView(R.layout.splash);
		
        Thread timer = new Thread(){  // Thread que inicia o Splash
        	public void run(){
        		try{
            		sleep(2000);
            	}catch(InterruptedException e){
	           		e.printStackTrace();
            	}finally{
           			startActivity(new Intent("MenuPrincipal"));
            	}
        	}
        };
        
        timer.start();
	}
	
    @Override
    protected void onPause() {
    	super.onPause();
    	finish();
    }

}
