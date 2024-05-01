package u2;

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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
*/
public class GRDM_U2 implements PlugIn {

    ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;
	
	
    public static void main(String args[]) {
		//new ImageJ();
    	//IJ.open("/users/barthel/applications/ImageJ/_images/orchid.jpg");
    	IJ.open("C:\\Users\\falfa\\Documents\\Uni\\2.Semester\\GDM\\Übungen\\u2\\orchid.jpg");
		
		GRDM_U2 pw = new GRDM_U2();
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
    
    
    class CustomWindow extends ImageWindow implements ChangeListener {
         
        private JSlider jSliderBrightness;
		private JSlider jSlider2;
		private JSlider jSliderKontrast;
		private double brightness;

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }
    
        void addPanel() {
        	//JPanel panel = new JPanel();
        	Panel panel = new Panel();

            panel.setLayout(new GridLayout(4, 1));
            jSliderBrightness = makeTitledSilder("Helligkeit", 0, 360, 180);
            jSlider2 = makeTitledSilder("Slider2-Wert", 0, 50, 0);
			jSliderKontrast = makeTitledSilder("Kontrast", 0, 20, 10);
            panel.add(jSliderBrightness);
            panel.add(jSlider2);
			panel.add(jSliderKontrast);
            
            add(panel);
            
            pack();
         }
      
        private JSlider makeTitledSilder(String string, int minVal, int maxVal, int val) {
		
        	JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val );
        	Dimension preferredSize = new Dimension(width, 50);
        	slider.setPreferredSize(preferredSize);
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(), 
					string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
			slider.setMajorTickSpacing((maxVal - minVal)/10 );
			slider.setPaintTicks(true);
			slider.addChangeListener(this);
			
			return slider;
		}
        
        private void setSliderTitle(JSlider slider, String str) {
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
				str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
		}

		public void stateChanged( ChangeEvent e ){
			JSlider slider = (JSlider)e.getSource();

			if (slider == jSliderBrightness) {
				brightness = slider.getValue()-180;
				String str = "Helligkeit " + brightness; 
				setSliderTitle(jSliderBrightness, str); 
			}
			
			if (slider == jSlider2) {
				int value = slider.getValue();
				String str = "Slider2-Wert " + value; 
				setSliderTitle(jSlider2, str); 
			}

			if (slider == jSliderKontrast) {
				int value = slider.getValue()-10;
				String str = "Kontrast " + value;
				setSliderTitle(jSliderKontrast, str);
			}
			
			changePixelValues(imp.getProcessor());
			
			imp.updateAndDraw();
		}

		
		private void changePixelValues(ImageProcessor ip) {
			
			// Array fuer den Zugriff auf die Pixelwerte
			int[] pixels = (int[])ip.getPixels();
			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
					
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;

					//Umwandlung von RGB nach YUV
					double Y = 0.299 * r + 0.587 * g + 0.114 * b;
					double U = (b - Y) * 0.493;
					double V = (r - Y) * 0.877;


					//TODO Kontrast (0 bis 10.0)
					Y =


					//TODO Farbsättigung (0 bis 5.0)

					//TODO Farbdrehung um den Winkel 0 bis 360





					//Umwandlung nach YUV nach RGB
					r = (int)(Y + V/0.877);
					g = (int)(1/0.587 * Y - 0.299/0.587*r - 0.114/0.587 * b);
					b = (int)(Y + U/0.493);



					// anstelle dieser drei Zeilen später hier die Farbtransformation durchführen,
					// die Y Cb Cr -Werte verändern und dann wieder zurücktransformieren
					int rn = (int) (r + brightness);
					int gn = (int) (g + brightness);
					int bn = (int) (b + brightness);

					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
					rn = Math.min(Math.max(rn, 0), 255);
					gn = Math.min(Math.max(gn, 0), 255);
					bn = Math.min(Math.max(bn, 0), 255);
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;


				}
			}
		}
		
    } // CustomWindow inner class
} 
