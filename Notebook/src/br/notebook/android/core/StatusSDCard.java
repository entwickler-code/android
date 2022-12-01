package br.notebook.android.core;

/* Classe responsável por mostrar o Status do SDCard */

import android.os.Environment;

public class StatusSDCard {
	
	public static final boolean LEITURA = true;
	public static final boolean ESCRITA = false;
	public static final boolean AMBOS = true;
	
	public static boolean verificaStatusSDCard(String status, boolean tipo){
		if (Environment.MEDIA_MOUNTED.equals(status)) {
		    return true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(status)) {
			return tipo == LEITURA ? true : false;
		} else {
		    return false;
		}		
	}
}