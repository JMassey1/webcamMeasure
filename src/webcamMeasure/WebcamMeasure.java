package webcamMeasure;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class WebcamMeasure {
	
	private static BufferedImage image;
	private static File picture;
	private static Desktop desktop;
	private static int counter;
	private static Writer wr;
	private static final int THRESHOLD = 50;
	private static final int DISTANCE_WIDTH = 12; //Distance in inches
	private static final int DISTANCE_TO_BACKGROUND = 12; //Distance in inches

	public static void main(String[] args) throws InterruptedException, IOException {
		try {
		Scanner in = new Scanner(new File("counter.txt"));
		if (!in.hasNext()) {
		String temp = in.nextLine();
		counter = Integer.parseInt(temp);
		System.out.println(temp);
		} else {
			counter = 0; 		
		}
		in.close();
		} catch(FileNotFoundException e) {
			Writer tempWr = new FileWriter("counter.txt");
			tempWr.write(Integer.toString(0));
			tempWr.close();
		}
		
		
		java.util.List<Webcam> tempList = Webcam.getWebcams();
		Webcam webcam = tempList.get(tempList.size()-1);
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		System.out.println(WebcamResolution.VGA.getSize());
		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setMirrored(true);
		
		
		//getting Desktop
		if (!Desktop.isDesktopSupported()) {
			System.out.println("Desktop not supported");
		} else {
			desktop = Desktop.getDesktop();
		}
		
		
		JFrame window = new JFrame("Webcam Viewer Panel");
		JPanel captureP = new JPanel();
		JButton capture = new JButton("Capture");
		JLabel response = new JLabel();
		JRadioButton save = new JRadioButton("Save Picture");
		captureP.setLayout(new BoxLayout(captureP, BoxLayout.Y_AXIS));
		response.setText("Press to Capture");
		response.setBounds(10,110,100,100);
		capture.setBounds(100,100,200,50);
//		System.out.println("2  " + counter);
		capture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				response.setText("Image Captured");
				image = webcam.getImage();
				try {
					int[] results = measureDistance(image);
					System.out.printf("PIXELS%nLength: %d%nHeight: %d%nINCHES%nLength: %f%nWidth: %f",results[0], results[1],pixToInchL(results[0]),pixToInchW(results[1]));
//					ImageIO.write(measureDistance(image), "PNG", new File("testingstuff.png"));
					JFrame resultsF = new JFrame("Results");
					JPanel text = new JPanel();
					text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
					JLabel pixelM = new JLabel();
					JLabel inchM = new JLabel();
					JLabel pixel1 = new JLabel();
					JLabel pixel2 = new JLabel();
					JLabel inch1 = new JLabel();
					JLabel inch2 = new JLabel();
					JLabel filler = new JLabel();
					pixelM.setText("PIXEL MEASUREMENTS");
					pixelM.setFont(new Font("Calibri", Font.BOLD, 20));
					inchM.setText("IMPERIAL SYSTEM MEASUREMENTS");
					inchM.setFont(new Font("Calibri", Font.BOLD, 20));
					pixel1.setText("Length: " + Integer.toString(results[0]) + " px");
					pixel2.setText("Height: " + Integer.toString(results[1]) + " px");
					inch1.setText("Length: " + Double.toString(pixToInchL(results[0])) + " in");
					inch2.setText("Height" + Double.toString(pixToInchW(results[1])) + " in");
					filler.setText("\n");
					text.add(pixelM);
					text.add(pixel1);
					text.add(pixel2);
					text.add(filler);
					text.add(inchM);
					text.add(inch1);
					text.add(inch2);
					
					resultsF.add(text);
					resultsF.setResizable(true);
//					resultsF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					resultsF.pack();
					resultsF.setVisible(true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (save.isSelected()) {
					try {
						System.out.println("4  " + counter);
						picture = new File("test" + counter + ".png");
						ImageIO.write(image, "PNG", picture);
						desktop.open(picture);
						counter++;
						response.setText("Press to Capture");
						System.out.println("5  " + counter);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					wr = new FileWriter("counter.txt");
					wr.write(Integer.toString(counter));
					wr.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		window.setLayout(new FlowLayout());
		
		captureP.add(capture);
		captureP.add(response);
		captureP.add(save);
		window.add(panel);
		window.add(captureP);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
//		System.out.println("3  " + counter);
	}
	//MAKE RETURN INT ONLY SET TO VOID SO THAT ECLIPSE WILL SHUT UP
	public static int[] measureDistance(Image img) throws IOException {
		BufferedImage grayImg = makeGrayscale(img);
//		ImageIO.write(grayImg, "PNG", new File("rendere20.png"));
		Graphics2D grayImgGraphics = grayImg.createGraphics();
		grayImgGraphics.drawImage(grayImg, 0, 0, Color.WHITE, null);
//		grayImgGraphics.drawOval(10, 10, 1000, 10);
//		RenderedImage renderedImage = (RenderedImage)grayImg;
//		ImageIO.write(renderedImage, "PNG", new File("rendered.png"));
		int pixel,count,max,height;
		max = 0;
		height = 0;
		for (int y = 0; y < grayImg.getHeight(); y++) {
			count = 0;
			for (int x = 0; x < grayImg.getWidth(); x++) {
				pixel = grayImg.getRGB(x, y);
				if (!isWhite(pixel)) {
					count++;
				}
			}
			if (count > max) {
				max = count;
				height = y;
			}
		}
		return new int[] {max,height};
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
//				System.out.println("SECOND " + makeGrayPix(pixel) + "/n");
			}
		}
		return temp;
	}
	
/*
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
*/
	//CHANGES PIXEL TO GRAYSCALE USING BIT SHIFTS AND AVERAGE PIXEL COLORS
	public static int makeGrayPix(int pix) {
		int alpha = getAlpha(pix);
		int red = getRed(pix);
		int green = getGreen(pix);
		int blue = getBlue(pix);
		int avg = (red + green + blue)/3;
		return ((alpha<<24) | (avg<<16) | (avg<<8) | avg);
	}
	
	public static double pixToInchL(int px) {
		return (((double)px)*DISTANCE_WIDTH/640);
	}
	public static double pixToInchW(int px) {
		return (((double)px)*DISTANCE_TO_BACKGROUND/480);
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
