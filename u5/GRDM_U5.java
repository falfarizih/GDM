package u5;

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
public class GRDM_U5 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = {"Original", "Filter 1", "Weichzeichnen", "Hochpassfilter", "verstärkten Kanten"};


	public static void main(String args[]) {

		IJ.open("C:\\Users\\falfa\\Documents\\Uni\\2.Semester\\GDM\\Übungen\\u5\\sail.jpg");
		//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

		GRDM_U5 pw = new GRDM_U5();
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
			int[] pixels = (int[])ip.getPixels();

			if (method.equals("Original")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						
						pixels[pos] = origPixels[pos];
					}
				}
			}
			
			if (method.equals("Filter 1")) {


				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;

						int rn = r/2;
						int gn = g/2;
						int bn = b/2;

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("Weichzeichnen")) {

				int kernelWidth = 3;
				float[] kernel = {
						1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
						1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
						1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f
				};

				for (int y=1; y<height-1; y++) {
					for (int x=1; x<width-1; x++) {

						float r = 0;
						float g = 0;
						float b = 0;

						for (int ky=-1; ky<=1; ky++) {
							for (int kx=-1; kx<=1; kx++) {
								int pos = (y+ky)*width + (x+kx);
								int kernelPos = (ky + 1) * kernelWidth + (kx + 1);
								int argb = origPixels[pos]; // Lesen der Pixel innerhalb des Kernel

								int R = (argb >> 16) & 0xff;
								int G = (argb >>  8) & 0xff;
								int B =  argb        & 0xff;


								r += R * kernel[kernelPos];
								g += G * kernel[kernelPos];
								b += B * kernel[kernelPos];


							}
						}
						int rn = Math.min(Math.max((int)(r), 0), 255);
						int gn = Math.min(Math.max((int)(g), 0), 255);
						int bn = Math.min(Math.max((int)(b), 0), 255);


						int pos = y*width + x;
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
					}
				}





			}

			if (method.equals("Hochpassfilter")) {

				int kernelWidth = 3;
				float[] kernel = {
						-1.0f / 9.0f, -1.0f / 9.0f, -1.0f / 9.0f,
						-1.0f / 9.0f, 8.0f / 9.0f, -1.0f / 9.0f,
						-1.0f / 9.0f, -1.0f / 9.0f, -1.0f / 9.0f
				};

				for (int y=1; y<height-1; y++) {
					for (int x=1; x<width-1; x++) {

						float r = 0;
						float g = 0;
						float b = 0;

						for (int ky=-1; ky<=1; ky++) {
							for (int kx=-1; kx<=1; kx++) {
								int pos = (y+ky)*width + (x+kx);
								int kernelPos = (ky + 1) * kernelWidth + (kx + 1);
								int argb = origPixels[pos]; // Lesen der Pixel innerhalb des Kernel

								int R = (argb >> 16) & 0xff;
								int G = (argb >>  8) & 0xff;
								int B =  argb        & 0xff;


								r += (R * kernel[kernelPos]);
								g += (G * kernel[kernelPos]);
								b += (B * kernel[kernelPos]);


							}
						}
						int rn = Math.min(Math.max((int)(r + 128), 0), 255);
						int gn = Math.min(Math.max((int)(g + 128), 0), 255);
						int bn = Math.min(Math.max((int)(b + 128), 0), 255);


						int pos = y*width + x;
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
					}
				}
			}

			if (method.equals("verstärkten Kanten")) {

				int kernelWidth = 3;
				float[] kernel = {
						-1.0f / 9.0f, -1.0f / 9.0f, -1.0f / 9.0f,
						-1.0f / 9.0f, 17.0f / 9.0f, -1.0f / 9.0f,
						-1.0f / 9.0f, -1.0f / 9.0f, -1.0f / 9.0f
				};

				for (int y=1; y<height-1; y++) {
					for (int x=1; x<width-1; x++) {

						float r = 0;
						float g = 0;
						float b = 0;

						for (int ky=-1; ky<=1; ky++) {
							for (int kx=-1; kx<=1; kx++) {
								int pos = (y+ky)*width + (x+kx);
								int kernelPos = (ky + 1) * kernelWidth + (kx + 1);
								int argb = origPixels[pos]; // Lesen der Pixel innerhalb des Kernel

								int R = (argb >> 16) & 0xff;
								int G = (argb >>  8) & 0xff;
								int B =  argb        & 0xff;


								r += R * kernel[kernelPos];
								g += G * kernel[kernelPos];
								b += B * kernel[kernelPos];


							}
						}
						int rn = Math.min(Math.max((int)(r), 0), 255);
						int gn = Math.min(Math.max((int)(g), 0), 255);
						int bn = Math.min(Math.max((int)(b), 0), 255);


						int pos = y*width + x;
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
					}
				}
			}
			
		}


	} // CustomWindow inner class
} 
