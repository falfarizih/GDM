package u3;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
 */
public class GRDM_U3 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = {
			"Original",
			"Rot-Kanal",
			"Negativ",
			"Graustufen",
			"Binärbild (Schwarz / Weiß)",
			"5 Graustufen ohne Weiß und Schwarz",
			"27 Graustufen mit Weiß und Schwarz",
			"vertikaler Fehlerdiffusion",
			"Sepia",
			"9 Hauptfarben",
	};


	public static void main(String args[]) {

		IJ.open("C:\\Users\\falfa\\Documents\\Uni\\2.Semester\\GDM\\Übungen\\u3\\Bear.jpg");

		GRDM_U3 pw = new GRDM_U3();
		pw.imp = IJ.getImage();
		pw.run("");
	}

	public void run(String arg) {
		if (imp==null) 
			imp = WindowManager.getCurrentImage();
		if (imp==null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);

		storePixelValues(imp.getProcessor());

		new CustomWindow(imp, cc);
	}


	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int []) ip.getPixels()).clone();
	}


	class CustomCanvas extends ImageCanvas {

		CustomCanvas(ImagePlus imp) {
			super(imp);
		}

	} // CustomCanvas inner class


	class CustomWindow extends ImageWindow implements ItemListener {

		private String method;

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			//JPanel panel = new JPanel();
			Panel panel = new Panel();

			JComboBox cb = new JComboBox(items);
			panel.add(cb);
			cb.addItemListener(this);

			add(panel);
			pack();
		}

		public void itemStateChanged(ItemEvent evt) {

			// Get the affected item
			Object item = evt.getItem();

			if (evt.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Selected: " + item.toString());
				method = item.toString();
				changePixelValues(imp.getProcessor());
				imp.updateAndDraw();
			}

		}


		private void changePixelValues(ImageProcessor ip) {

			// Array zum Zurückschreiben der Pixelwerte
			int[] pixels = (int[]) ip.getPixels();

			if (method.equals("Original")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;

						pixels[pos] = origPixels[pos];
					}
				}
			}

			if (method.equals("Rot-Kanal")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						//int g = (argb >>  8) & 0xff;
						//int b =  argb        & 0xff;

						int rn = r;
						int gn = 0;
						int bn = 0;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
						rn = Math.min(Math.max(r, 0), 255);

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("Negativ")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						int rn = 255 - r;
						int gn = 255 - g;
						int bn = 255 - b;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
						rn = Math.min(Math.max(rn, 0), 255);
						gn = Math.min(Math.max(gn, 0), 255);
						bn = Math.min(Math.max(bn, 0), 255);

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("Graustufen")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						//Umwandlung von RGB nach YUV
						double Y = 0.299 * r + 0.587 * g + 0.114 * b;
						double U = (b - Y) * 0.493;
						double V = (r - Y) * 0.877;


						U = 0;
						V = 0;


						//Umwandlung nach YUV nach RGB, it uses the new b value, so b should be initialized first before g
						r = (int) (Y + V / 0.877);
						b = (int) (Y + U / 0.493);
						g = (int) (1 / 0.587 * Y - 0.299 / 0.587 * r - 0.114 / 0.587 * b);


						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
						int rn = Math.min(Math.max(r, 0), 255);
						int gn = Math.min(Math.max(g, 0), 255);
						int bn = Math.min(Math.max(b, 0), 255);


						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("Binärbild (Schwarz / Weiß)")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						//Umwandlung von RGB nach YUV
						double Y = 0.299 * r + 0.587 * g + 0.114 * b;
						double U = (b - Y) * 0.493;
						double V = (r - Y) * 0.877;

						U = 0;
						V = 0;

						if (Y < 128) {
							Y = 0;
						} else {
							Y = 255;
						}

						//Umwandlung nach YUV nach RGB, it uses the new b value, so b should be initialized first before g
						r = (int) (Y + V / 0.877);
						b = (int) (Y + U / 0.493);
						g = (int) (1 / 0.587 * Y - 0.299 / 0.587 * r - 0.114 / 0.587 * b);


						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
						int rn = Math.min(Math.max(r, 0), 255);
						int gn = Math.min(Math.max(g, 0), 255);
						int bn = Math.min(Math.max(b, 0), 255);

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("5 Graustufen ohne Weiß und Schwarz")) {
				int border = 255 / 5;

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						//Umwandlung von RGB nach YUV
						double Y = 0.299 * r + 0.587 * g + 0.114 * b;
						double U = (b - Y) * 0.493;
						double V = (r - Y) * 0.877;

						U = 0;
						V = 0;

						int nb = (int) (Y / border);
						Y = (nb * border) + (border / 2);


						//Umwandlung nach YUV nach RGB, it uses the new b value, so b should be initialized first before g
						r = (int) (Y + V / 0.877);
						b = (int) (Y + U / 0.493);
						g = (int) (1 / 0.587 * Y - 0.299 / 0.587 * r - 0.114 / 0.587 * b);


						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
						int rn = Math.min(Math.max(r, 0), 255);
						int gn = Math.min(Math.max(g, 0), 255);
						int bn = Math.min(Math.max(b, 0), 255);


						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("27 Graustufen mit Weiß und Schwarz")) {
				int border = 255 / 26;

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						//Umwandlung von RGB nach YUV
						double Y = 0.299 * r + 0.587 * g + 0.114 * b;
						double U = (b - Y) * 0.493;
						double V = (r - Y) * 0.877;

						U = 0;
						V = 0;

						int nb = (int) (Y / border);
						Y = (nb * border) + (border / 2);


						//Umwandlung nach YUV nach RGB, it uses the new b value, so b should be initialized first before g
						r = (int) (Y + V / 0.877);
						b = (int) (Y + U / 0.493);
						g = (int) (1 / 0.587 * Y - 0.299 / 0.587 * r - 0.114 / 0.587 * b);


						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
						int rn = Math.min(Math.max(r, 0), 255);
						int gn = Math.min(Math.max(g, 0), 255);
						int bn = Math.min(Math.max(b, 0), 255);


						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("vertikaler Fehlerdiffusion")) {
				int border = Math.round(255 / 2);
				int error = 0;

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos];
						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						int rNew = 255;
						int gNew = 255;
						int bNew = 255;

						if ((r + g + b + error) < border) {
							rNew = 0;
							gNew = 0;
							bNew = 0;
						}

						error = ((r + g + b + error) - (rNew + gNew + bNew));
						pixels[pos] = (0xFF << 24) | (rNew << 16) | (gNew << 8) | bNew;
					}


				}

			}

			if (method.equals("Sepia")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						int tr = (int)(0.393*r + 0.769*g + 0.189*b);
						int tg = (int)(0.349*r + 0.686*g + 0.168*b);
						int tb = (int)(0.272*r + 0.534*g + 0.131*b);

						if(tr > 255){
							r = 255;
						} else {
							r = tr;
						}

						if(tg > 255){
							g = 255;
						} else {
							g = tg;
						}

						if(tb > 255){
							b = 255;
						} else {
							b = tb;
						}



						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
						int rn = Math.min(Math.max(r, 0), 255);
						int gn = Math.min(Math.max(g, 0), 255);
						int bn = Math.min(Math.max(b, 0), 255);


						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("9 Hauptfarben")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						// Calculate the distance to pure red, green, and blue
						double distanceToRed = Math.sqrt(Math.pow(r - 54, 2) + Math.pow(g - 108, 2) + Math.pow(b - 146, 2));
						double distanceToGreen = Math.sqrt(Math.pow(r - 197, 2) + Math.pow(g - 197, 2) + Math.pow(b - 205, 2));
						double distanceToBlue = Math.sqrt(Math.pow(r - 13, 2) + Math.pow(g - 22, 2) + Math.pow(b - 19, 2));

						double distanceToYellow = Math.sqrt(Math.pow(r - 124, 2) + Math.pow(g - 96, 2) + Math.pow(b - 75, 2));
						double distanceToOrange = Math.sqrt(Math.pow(r - 181, 2) + Math.pow(g - 91, 2) + Math.pow(b - 90, 2));
						double distanceToPurple = Math.sqrt(Math.pow(r - 96, 2) + Math.pow(g - 97, 2) + Math.pow(b - 92, 2));

						double distanceToCyan = Math.sqrt(Math.pow(r - 207, 2) + Math.pow(g - 191, 2) + Math.pow(b - 175, 2));
						double distanceToMagenta = Math.sqrt(Math.pow(r - 57, 2) + Math.pow(g - 50, 2) + Math.pow(b - 44, 2));
						double distanceToBrown = Math.sqrt(Math.pow(r - 59, 2) + Math.pow(g - 63, 2) + Math.pow(b - 62, 2));
						// Find the smallest distance

						double minDistance = distanceToRed;
						int rn = 54;
						int gn = 108;
						int bn = 146;


						// Assign the pixel to the nearest pure color
						if (distanceToGreen < minDistance) {
							minDistance = distanceToGreen;
							rn = 197;
							gn = 197;
							bn = 205;
						}
						if (distanceToBlue < minDistance) {
							minDistance = distanceToBlue;
							rn = 13;
							gn = 22;
							bn = 19;
						}
						if (distanceToYellow < minDistance) {
							minDistance = distanceToYellow;
							rn = 124;
							gn = 96;
							bn = 75;
						}
						if (distanceToOrange < minDistance) {
							minDistance = distanceToOrange;
							rn = 181;
							gn = 91;
							bn = 90;
						}
						if (distanceToPurple < minDistance) {
							minDistance = distanceToPurple;
							rn = 96;
							gn = 97;
							bn = 92;
						}
						if (distanceToCyan < minDistance) {
							minDistance = distanceToCyan;
							rn = 207;
							gn = 191;
							bn = 175;
						}
						if (distanceToMagenta < minDistance) {
							minDistance = distanceToMagenta;
							rn = 57;
							gn = 50;
							bn = 44;
						}
						if (distanceToBrown < minDistance) {
							minDistance = distanceToBrown;
							rn = 59;
							gn = 63;
							bn = 62;
						}

						// Set the new RGB values
						rn = Math.min(Math.max(rn, 0), 255);
						gn = Math.min(Math.max(gn, 0), 255);
						bn = Math.min(Math.max(bn, 0), 255);

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}
		} // CustomWindow inner class
	}
}
