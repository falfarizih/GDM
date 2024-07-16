package u6;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class Scale_ implements PlugInFilter {

    public static void main(String args[]) {
        IJ.open("C:\\Users\\falfa\\Documents\\Uni\\2.Semester\\GDM\\Übungen\\u6\\component.jpg");


        ImagePlus imp = IJ.getImage();

        Scale_ scalePlugin = new Scale_();
        scalePlugin.setup("", imp);
        scalePlugin.run(imp.getProcessor());
    }

    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about")) {
            showAbout();
            return DONE;
        }
        return DOES_RGB + NO_CHANGES;
        // kann RGB-Bilder und veraendert das Original nicht
    }

    public void run(ImageProcessor ip) {

        String[] dropdownmenue = {"Kopie", "Pixelwiederholung", "Bilinear"};

        GenericDialog gd = new GenericDialog("scale");
        gd.addChoice("Methode", dropdownmenue, dropdownmenue[0]);
        gd.addNumericField("Hoehe:", 500, 0);
        gd.addNumericField("Breite:", 400, 0);

        gd.showDialog();

        String method = gd.getNextChoice();
        int height_n = (int) gd.getNextNumber(); // _n fuer das neue skalierte Bild
        int width_n = (int) gd.getNextNumber();

        if (method.equals("Kopie")) {
            scaleKopie(ip, height_n, width_n);
        } else if (method.equals("Pixelwiederholung")) {
            scalePixelwiederholung(ip, height_n, width_n);
        } else if (method.equals("Bilinear")) {
            scaleBilinear(ip, height_n, width_n);
        }

    }

    private void scaleKopie(ImageProcessor ip, int height_n, int width_n) {
        int width = ip.getWidth();  // Breite bestimmen
        int height = ip.getHeight(); // Hoehe bestimmen

        //height_n = height;
        //width_n  = width;

        ImagePlus neu = NewImage.createRGBImage("Skaliertes Bild",
                width_n, height_n, 1, NewImage.FILL_BLACK);

        ImageProcessor ip_n = neu.getProcessor();


        int[] pix = (int[]) ip.getPixels();
        int[] pix_n = (int[]) ip_n.getPixels();

        // Schleife ueber das neue Bild
        for (int y_n = 0; y_n < height_n; y_n++) {
            for (int x_n = 0; x_n < width_n; x_n++) {
                int y = y_n;
                int x = x_n;

                if (y < height && x < width) {
                    int pos_n = y_n * width_n + x_n;
                    int pos = y * width + x;

                    pix_n[pos_n] = pix[pos];
                }
            }
        }


        // neues Bild anzeigen
        neu.show();
        neu.updateAndDraw();
    }

    private void scalePixelwiederholung(ImageProcessor ip, int height_n, int width_n) {
        int width = ip.getWidth();
        int height = ip.getHeight();

        ImagePlus neu = NewImage.createRGBImage("Scaled Image - Pixelwiederholung", width_n, height_n, 1, NewImage.FILL_BLACK);
        ImageProcessor ip_n = neu.getProcessor();

        int[] pix = (int[]) ip.getPixels();
        int[] pix_n = (int[]) ip_n.getPixels();

        double x_scale = (double) width / width_n;
        double y_scale = (double) height / height_n;

        for (int y_n = 0; y_n < height_n; y_n++) {
            for (int x_n = 0; x_n < width_n; x_n++) {
                int y = (int) Math.round(y_n * x_scale);
                int x = (int) Math.round(x_n * y_scale);

                y = Math.min(y, height - 1);
                x = Math.min(x, width - 1);

                int pos_n = y_n * width_n + x_n;
                int pos = y * width + x;

                pix_n[pos_n] = pix[pos];
            }
        }

        neu.show();
        neu.updateAndDraw();
    }

    private void scaleBilinear(ImageProcessor ip, int height_n, int width_n) {
        int width = ip.getWidth();
        int height = ip.getHeight();

        double x_scale = (double) width / width_n;
        double y_scale = (double) height / height_n;

        ImagePlus neu = NewImage.createRGBImage("Scaled Image - Bilinear", width_n, height_n, 1, NewImage.FILL_BLACK);
        ImageProcessor ip_n = neu.getProcessor();

        int[] pix = (int[]) ip.getPixels();
        int[] pix_n = (int[]) ip_n.getPixels();

        for (int x_n = 0; x_n < width_n; x_n++) {
            for (int y_n = 0; y_n < height_n; y_n++) {
                double x_source = x_n * x_scale;
                double y_source = y_n * y_scale;

                int x_left = (int) x_source;
                int x_right = Math.min(x_left + 1, width - 1);
                int y_top = (int) y_source;
                int y_bot = Math.min(y_top + 1, height - 1);

                double h = x_source - x_left;
                double v = y_source - y_top;

                int a = pix[x_left + y_top * width];
                int b = pix[x_right + y_top * width];
                int c = pix[x_left + y_bot * width];
                int d = pix[x_right + y_bot * width];

                int red_a = (a >> 16) & 0xFF;
                int green_a = (a >> 8) & 0xFF;
                int blue_a = a & 0xFF;

                int red_b = (b >> 16) & 0xFF;
                int green_b = (b >> 8) & 0xFF;
                int blue_b = b & 0xFF;

                int red_c = (c >> 16) & 0xFF;
                int green_c = (c >> 8) & 0xFF;
                int blue_c = c & 0xFF;

                int red_d = (d >> 16) & 0xFF;
                int green_d = (d >> 8) & 0xFF;
                int blue_d = d & 0xFF;

                double red_top = red_a * (1 - h) + red_b * h;
                double red_bottom = red_c * (1 - h) + red_d * h;

                double green_top = green_a * (1 - h) + green_b * h;
                double green_bottom = green_c * (1 - h) + green_d * h;

                double blue_top = blue_a * (1 - h) + blue_b * h;
                double blue_bottom = blue_c * (1 - h) + blue_d * h;

                int target_red = (int) (red_top * (1 - v) + red_bottom * v);
                int target_green = (int) (green_top * (1 - v) + green_bottom * v);
                int target_blue = (int) (blue_top * (1 - v) + blue_bottom * v);

                int target_color = (0xFF << 24) | (target_red << 16) | (target_green << 8) | target_blue;
                pix_n[y_n * width_n + x_n] = target_color;
            }
        }

        neu.show();
        neu.updateAndDraw();
    }

    void showAbout() {
        IJ.showMessage("");
    }
}

