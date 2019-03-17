package webcamMeasure;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class NoWebcam {
	
	private static BufferedImage image;
	private static File picture;
	private static Desktop desktop;
	private static int counter;
	private static Writer wr;
	private static final int THRESHOLD = 200;
	private static final int DISTANCE_WIDTH = 10; //Distance in inches
	private static final int DISTANCE_TO_BACKGROUND = 10; //Distance in inches

	public static void main(String[] args) throws InterruptedException, IOException {
		Scanner in = new Scanner(new File("counter.txt"));
		if (!in.hasNext()) {
		String temp = in.nextLine();
		counter = Integer.parseInt(temp);
		System.out.println(temp);
		} else {
			counter = 0; 		
		}
		
		
//		java.util.List<Webcam> tempList = Webcam.getWebcams();
//		Webcam webcam = tempList.get(tempList.size()-1);
//		webcam.setViewSize(WebcamResolution.VGA.getSize());
//		WebcamPanel panel = new WebcamPanel(webcam);
//		panel.setFPSDisplayed(true);
//		panel.setDisplayDebugInfo(true);
//		panel.setMirrored(true);
		
		
		//getting Desktop
		if (!Desktop.isDesktopSupported()) {
			System.out.println("Desktop not supported");
		} else {
			desktop = Desktop.getDesktop();
		}
		
		
		JFrame window = new JFrame("Webcam Viewer Panel");
		JButton capture = new JButton("Capture");
		JLabel response = new JLabel();
		response.setText("Press to Capture");
		response.setBounds(10,110,100,100);
		capture.setBounds(100,100,200,50);
//		System.out.println("2  " + counter);
		capture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				response.setText("Image Captured");
				try {
					image = ImageIO.read(new File("img.png"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					System.out.println(measureDistance(image));
//					ImageIO.write(measureDistance(image), "PNG", new File("testingstuff.png"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
//				try {
//					System.out.println("4  " + counter);
//					picture = new File("test" + counter + ".png");
//					ImageIO.write(image, "PNG", picture);
////					Thread.sleep(1000);
//					desktop.open(picture);
//					counter++;
//					response.setText("Press to Capture");
//					System.out.println("5  " + counter);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}});
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					wr = new FileWriter("counter.txt");
					wr.write(Integer.toString(counter));
					wr.close();
					in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		window.setLayout(new FlowLayout());
		window.add(capture);
//		window.add(panel);
		window.add(response);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
		System.out.println("3  " + counter);

	}
	//MAKE RETURN INT ONLY SET TO VOID SO THAT ECLIPSE WILL SHUT UP
	public static int measureDistance(Image img) throws IOException {
		BufferedImage grayImg = makeGrayscale(img);
//		ImageIO.write(grayImg, "PNG", new File("rendere20.png"));
		Graphics2D grayImgGraphics = grayImg.createGraphics();
		grayImgGraphics.drawImage(grayImg, 0, 0, Color.WHITE, null);
//		grayImgGraphics.drawOval(10, 10, 1000, 10);
		RenderedImage renderedImage = (RenderedImage)grayImg;
		ImageIO.write(renderedImage, "PNG", new File("rendered.png"));
		
		int pixel,count,max,height;
		max = 0;
		for (int y = 0; y < grayImg.getHeight(); y++) {
			count = 0;
			for (int x = 0; x < grayImg.getWidth(); x++) {
				pixel = grayImg.getRGB(x, y);
				if (!isWhite(pixel)) {
					System.out.println("test");
					count++;
				}
			}
			if (count > max) {
				max = count;
			}
		}
		return max;
	}
	
	//GIVEN ORIGINAL PICTURE, TURNS IT TO GRAYSCALE
	public static BufferedImage makeGrayscale(Image img) {
		BufferedImage original = toBufferedImage(img);
		int w = original.getWidth();
		int h = original.getHeight();
		BufferedImage temp = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int pixel = original.getRGB(x, y);
//				System.out.println("FIRST: " + pixel);
				temp.setRGB(x, y, makeGrayPix(pixel));
//				System.out.println("SECOND " + makeGrayPix(pixel) + "\n");
			}
		}
		return temp;
	}
//	public static BufferedImage makeGrayscale(BufferedImage original) {
//		BufferedImage result = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
//		Graphics2D graphics = result.createGraphics();
//		graphics.drawImage(original, 0, 0, Color.WHITE, null);
//		
//		for (int i = 0; i < result.getHeight(); i++) {
//			for (int w = 0; w < result.getWidth(); w++) {
//				Color c = new Color(result.getRGB(w,i));
//				int red = (int)(c.getRed() * 0.299);
//				System.out.println("FIRST COLOR: " + red);
//				System.out.println("SECOND COLOR: " + getRed(result.getRGB(w, i)));
//				int green = (int) (c.getGreen() * 0.587);
//				int blue = (int) (c.getBlue() * 0.114);
//				Color newColor = new Color(red+green+blue,red+green+blue,red+green+blue);
//				result.setRGB(w, i, newColor.getRGB());
//			}
//		}
//		return result;
//	}
	//CHANGES PIXEL TO GRAYSCALE USING BIT SHIFTS AND AVERAGE PIXEL COLORS
	public static int makeGrayPix(int pix) {
		int alpha = getAlpha(pix);
		int red = getRed(pix);
		int green = getGreen(pix);
		int blue = getBlue(pix);
		int avg = (red + green + blue)/3;
		return ((alpha<<24) | (avg<<16) | (avg<<8) | avg);
	}
	
	public static boolean isWhite(int pixel) {
		if (getRed(pixel) > THRESHOLD && getGreen(pixel) > THRESHOLD && getBlue(pixel) > THRESHOLD) {
			return true;
		}
		return false;
	}
	
	public static int getAlpha(int pix) {
		return (pix>>24) & 0xff;
	}
	public static int getRed(int pix) {
		return (pix>>16) & 0xff;
	}
	public static int getGreen(int pix) {
		return (pix>>8) & 0xff;
	}
	public static int getBlue(int pix) {
		return pix & 0xff;
	}

	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage){
	        return (BufferedImage) img;
	    }
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();
	    return bimage;
	}
}