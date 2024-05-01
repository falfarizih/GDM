package u1;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

//erste Uebung (elementare Bilderzeugung)

public class GLDM_U1_S0592099 implements PlugIn {
	
	final static String[] choices = {
		"Schwarzes Bild",
		"Gelbes Bild", 
		"Belgische Fahne",
		"Schwarz/Weiss Verlauf",
			"Schwarz/Weiss diagonale Verlauf" ,
		"Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf",
		"USA Fahne",
		"Tschechischen Fahne"
	};
	
	private String choice;
	
	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen 
		ij.exitWhenQuitting(true);
		
		GLDM_U1_S0592099 imageGeneration = new GLDM_U1_S0592099();
		imageGeneration.run("");
	}
	
	public void run(String arg) {
		
		int width  = 566;  // Breite
		int height = 400;  // Hoehe
		
		// RGB-Bild erzeugen
		ImagePlus imagePlus = NewImage.createRGBImage("GLDM_U1", width, height, 1, NewImage.FILL_BLACK);
		ImageProcessor ip = imagePlus.getProcessor();
		
		// Arrays fuer den Zugriff auf die Pixelwerte
		int[] pixels = (int[])ip.getPixels();
		
		dialog();
		
		////////////////////////////////////////////////////////////////
		// Hier bitte Ihre Aenderungen / Erweiterungen
		
		if ( choice.equals("Schwarzes Bild") ) {
			generateBlackImage(width, height, pixels);
		}

		if (choice.equals("Gelbes Bild")){
			generateYellowImage(width, height, pixels);
		}

		if (choice.equals("Belgische Fahne")){
			generateBelgianFlag(width, height, pixels);
		}

		if(choice.equals("Schwarz/Weiss Verlauf")){
			generateWhiteToBlack(width, height, pixels);
		}

		if(choice.equals("Schwarz/Weiss diagonale Verlauf")){
			generateWhiteToBlackDiagonal(width, height, pixels);
		}

		if(choice.equals("Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf")){
			generateRedToBlueDiagonal(width, height, pixels);
		}

		if(choice.equals("Tschechischen Fahne")){
			generateCzechFlag(width, height, pixels);
		}

		if(choice.equals("USA Fahne")){
			generateAmericanFlag(width, height, pixels);
		}


		////////////////////////////////////////////////////////////////////
		
		// neues Bild anzeigen
		imagePlus.show();
		imagePlus.updateAndDraw();
	}

	private void generateBlackImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				
				int r = 0;
				int g = 0;
				int b = 0;
				
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateYellowImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				int r = 255;
				int g = 255;
				int b = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateBelgianFlag(int width, int height, int[] pixels) {

		int b=0;
		int g=0;
		int r=0;
		int border1 = width/3;
		int border2 = width*2/3;

		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				if(x<=border1){
					r = 35;
					g = 31;
					b = 32;
				}
				if(border1<x && x<=border2){
					r = 255;
					g = 211;
					b = 0;
				}
				if(border2<x && x<=width){
					r = 220;
					g = 0;
					b = 46;
				}

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateWhiteToBlack(int width, int height, int[] pixels) {
		int r = 0;
		int g = 0;
		int b = 0;
		float max = 255;
		//float step = (float)(255 / width);
		float step = (max) / width-1;
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				r = (int)(x*step);
				g = (int)(x*step);
				b = (int)(x*step);

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateWhiteToBlackDiagonal(int width, int height, int[] pixels) {
		int r=0;
		int g=0;
		int b=0;
		int max = 255;
		float factorXAchse= (float)(max) / (width)-1;
		float factorYAchse= (float)(max) / (height)-1;

		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				r = (int)((x+y)*factorXAchse*factorYAchse);
				g = (int)((x+y)*factorXAchse*factorYAchse);
				b = (int)((x+y)*factorXAchse*factorYAchse);

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateRedToBlueDiagonal(int width, int height, int[] pixels) {
		int r=0;
		int g=0;
		int b=0;
		int max = 255;
		float factorXAchse= (float)(max) / (width);
		float factorYAchse= (float)(max) / (height);

		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				r = (int)((x)*factorXAchse);
				b = (int)((y)*factorYAchse);

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateCzechFlag(int width, int height, int[] pixels) {

		int r = 0;
		int g = 0;
		int b =0;
		int middle = height/2;
		int count = 0;

		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				if(y<=middle){
					r = 255;
					g = 255;
					b = 255;
				}

				if(y>middle) {
					r = 215;
					g = 20;
					b = 26;
				}

				if(x<=count){
					r = 17;
					g = 69;
					b = 126;
				}



				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;


			}
			if(y<=middle) {
				count++;
			}else{
				count--;
			}
		}

	}

	private void generateAmericanFlag(int width, int height, int[] pixels) {

		int r = 0;
		int g = 0;
		int b =0;
		float stripe = (float)(height)/(float)6.5;
		int border1 = width/3;
		int border2 = height/2;

		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				if(y % stripe <= stripe/2){
					r = 179;
					g = 25;
					b = 66;
				} else {
					r = 255;
					g = 255;
					b = 255;
				}

				if(x<border1 && y<border2){
					r = 10;
					g = 49;
					b = 97;
				}



				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;


			}
		}

	}

	private void dialog() {
		// Dialog fuer Auswahl der Bilderzeugung
		GenericDialog gd = new GenericDialog("Bildart");
		
		gd.addChoice("Bildtyp", choices, choices[0]);
		
		
		gd.showDialog();	// generiere Eingabefenster
		
		choice = gd.getNextChoice(); // Auswahl uebernehmen
		
		if (gd.wasCanceled())
			System.exit(0);
	}
}

