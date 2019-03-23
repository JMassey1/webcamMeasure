package webcamMeasure;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

public class noCam {
	
	private static BufferedImage image;
	private static File picture;
	private static Desktop desktop;
	private static int counter;
	private static Writer wr;
	private static boolean isMale = true;
	private static Shoe shoeSize;
	private static final int THRESHOLD = 50;
	private static final double DISTANCE_WIDTH = 12; //Distance in inches
	private static final double DISTANCE_TO_MEASURE = 12; //Distance in inches
	private static final int PIX_WIDTH_CUSTOM = 100;
	private static final int PIX_HEIGHT_CUSTOM = 100;
	
	
	public static void main(String[] args) throws InterruptedException, IOException {
		
		java.util.List<Webcam> tempList = Webcam.getWebcams();
		Webcam webcam = tempList.get(tempList.size()-1);
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		System.out.println(WebcamResolution.VGA.getSize());
		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setMirrored(true);
		

		JFrame webcamFrame = new JFrame("Webcam Viewer Panel");
		JPanel captureP = new JPanel();
//		JPanel captureP1 = new JPanel();
		JButton capture = new JButton("Side View");
		JButton capture1 = new JButton("Front View");
		JLabel response = new JLabel();
		JLabel response1 = new JLabel();
		JRadioButton save = new JRadioButton("Save Picture");
//		JRadioButton male = new JRadioButton("Male?");
		JToggleButton gender = new JToggleButton("Male", true);
		captureP.setLayout(new BoxLayout(captureP, BoxLayout.Y_AXIS));
//		captureP1.setLayout(new BoxLayout(captureP1, BoxLayout.Y_AXIS));
		
		response.setText("Press to Capture");
		response1.setText("Press to Capture");
		response.setBounds(10,110,100,100);
		response1.setBounds(10,110,100,100);
		
		capture.setBounds(100,100,210,50);
		capture1.setBounds(100,100,200,50);
		gender.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					isMale = true;
					gender.setText("Men");
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					isMale = false;
					gender.setText("Women");
				}
			}
		
		});
		capture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				response.setText("Image Captured");
				try {
					image = ImageIO.read(new File("testimage.png"));
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					image = webcam.getImage();
					System.out.println("CAMERA PICTURE CAMERA PICTURE CAMERA PICTURE");
				}
				try {
					int[] results = measureDistance(image);
					shoeSize = new Shoe(results, true);
					int heightFromGround = image.getHeight() - results[1];
//					double shoesize = shoeSize.getShoeSize();
					System.out.printf("PIXELS%nLength: %d%nHeight: %d  (From Ground Up: %d)%nHeight Count: %d%nINCHES%nLength: %f%nWidth: %f%nShoe Size: %f",
							results[0], results[1], heightFromGround, results[2], pixToInchL(results[0]),pixToInchW(results[2]), shoeSize.getShoeSize());
					JFrame resultsF = new JFrame("Side View");
					JPanel text = new JPanel();
					text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
					JLabel pixelM = new JLabel();
					JLabel inchM = new JLabel();
					JLabel pixel1 = new JLabel();
					JLabel pixel2 = new JLabel();
					JLabel pixel2b = new JLabel();
					JLabel height = new JLabel();
					JLabel inch1 = new JLabel();
					JLabel inch2 = new JLabel();
					JLabel filler = new JLabel();
					pixelM.setText("PIXEL MEASUREMENTS");
					pixelM.setFont(new Font("Calibri", Font.BOLD, 20));
					inchM.setText("IMPERIAL SYSTEM MEASUREMENTS");
					inchM.setFont(new Font("Calibri", Font.BOLD, 20));
					pixel1.setText("Length: " + Integer.toString(results[0]) + " px");
					pixel2.setText("Height: " + Integer.toString(results[1]) + " px");
					pixel2b.setText("Height From Ground: " + Integer.toString(heightFromGround) + "px");
					height.setText("Height count: " + Integer.toString(results[2]) + "px");
					inch1.setText("Length: " + Double.toString(pixToInchL(results[0])) + " in");
					inch2.setText("Height: " + Double.toString(pixToInchW(results[2])) + " in");
					filler.setText("\n");
					text.add(pixelM);
					text.add(pixel1);
					text.add(pixel2);
					text.add(pixel2b);
					text.add(height);
					text.add(filler);
					text.add(inchM);
					text.add(inch1);
					text.add(inch2);
					
					resultsF.add(text);
					resultsF.setResizable(true);
//					resultsF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					resultsF.pack();
					resultsF.setVisible(true);
					
					
					JFrame newWindow = new JFrame();
					JFXPanel panel = new JFXPanel();
					Platform.runLater( () -> {
						WebView webView = new WebView();
						webView.getEngine().load(shoeSize.getShoeURL());
						panel.setScene(new Scene(webView));
					});
					newWindow.add(panel);
					newWindow.setResizable(true);
					newWindow.setVisible(true);
					System.out.printf("%n%n%n%n%s%n%n%n%n", shoeSize.getShoeURL());
					newWindow.add(new JLabel(shoeSize.getShoeURL() + "////" + Double.toString(shoeSize.getShoeSize())));
//					newWindow.add(new JLabel(Double.toString(shoeSize.getShoeSize())));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (save.isSelected()) {
					try {
						System.out.println("4  " + counter);
						picture = new File("side" + counter + ".png");
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
		webcamFrame.addWindowListener(new WindowAdapter() {
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
		webcamFrame.setLayout(new FlowLayout());
		
		captureP.add(capture);
		captureP.add(response);
		captureP.add(save);
		
		webcamFrame.add(panel);
		webcamFrame.add(captureP);
		webcamFrame.add(gender);;
		webcamFrame.setResizable(true);
		webcamFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		webcamFrame.pack();
		webcamFrame.setVisible(true);
//		System.out.println("3  " + counter);
	}
	public static int[] measureDistance(Image img) throws IOException {
		BufferedImage grayImg = makeGrayscale(img);
//		ImageIO.write(grayImg, "PNG", new File("rendere20.png"));
		Graphics2D grayImgGraphics = grayImg.createGraphics();
		grayImgGraphics.drawImage(grayImg, 0, 0, Color.WHITE, null);
//		grayImgGraphics.drawOval(10, 10, 1000, 10);
//		RenderedImage renderedImage = (RenderedImage)grayImg;
//		ImageIO.write(renderedImage, "PNG", new File("rendered.png"));
		int pixel,count,max,height, heightCount = 1;
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
				heightCount = 1;
			} else if (count == max) {
				heightCount++;
			}
		}
		return new int[] {max,height,heightCount};
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
	//CHANGES PIXEL TO GRAYSCALE USING BIT SHIFTS AND AVERAGE PIXEL COLORS
	public static int makeGrayPix(int pix) {
		int alpha = getAlpha(pix);
		int red = getRed(pix);
		int green = getGreen(pix);
		int blue = getBlue(pix);
		int avg = (red + green + blue)/3;
		return ((alpha<<24) | (avg<<16) | (avg<<8) | avg);
	}
	
	public static double getHeight() {
		double diagonal = DISTANCE_TO_MEASURE * Math.tan(Math.toRadians(68.5/2));
		return 2*(Math.sqrt(Math.pow(diagonal, 2) - Math.pow((double)DISTANCE_WIDTH/2, 2)));
	}
	
	public static double pixToInchL(int px) {
		return (((double)px)*DISTANCE_WIDTH/PIX_WIDTH_CUSTOM);
	}
	public static double pixToInchW(int px) {
		return (((double)px)*DISTANCE_TO_MEASURE/PIX_HEIGHT_CUSTOM);
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

class Shoe {
	//Size then Inches
	private static final double[][] MENS_SIZE = new double[][]{{7.0,9.6},{7.5,9.75},{8.0,9.9},{8.5,10.125},{9.0,10.25},{9.5,10.4},{10.0,10.6},{10.5,10.75},{11.0,10.9},{11.5, 11.125},{12.0,11.25},{13.0,11.6}};
	private static final double[][] WOMENS_SIZE = new double[][] {{6.0,8.75},{6.5,9.0},{7.0,9.25},{7.5,9.375},{8.0,9.5},{8.5,9.75},{9.0,9.875},{9.5,10.0},{10.0,10.2},{10.5,10.35},{11,10.5}};
	private double footL;
	private double footW;
	private double footH;
	private double shoeSize;
	boolean manOrWoman; //True if Man, False if Woman
	
	public Shoe(int footL, int footW, int footH, boolean manOrWoman) {
		this.footL = (double)footL;
		this.footW = (double)footW;
		this.footH = (double)footH;
		this.manOrWoman = manOrWoman;
		shoeSize = getShoeSize();
	}
	
	public Shoe(int[] foot, boolean manOrWoman) {
		footL = (double)foot[0];
		footW = (double)foot[1];
		footH = (double)foot[2];
		this.manOrWoman = manOrWoman;
		shoeSize = getShoeSize();
	}
	
	public double getShoeSize() {
		if (this.manOrWoman) {
			for (double[] shoes: MENS_SIZE) {
				if (shoes[1] >= footL) {
					return shoes[0];
				}
			}
			double[] temp = MENS_SIZE[MENS_SIZE.length - 1];
			return temp[temp.length - 1];
		} else {
			for (double[] shoes: WOMENS_SIZE) {
				if (shoes[1] >= footL) {
					return shoes[0];
				}
			}
			double[] temp = WOMENS_SIZE[WOMENS_SIZE.length - 1];
			return temp[temp.length - 1];
		}
//		return 0.0;
	}

	public void setShoeSize(double shoeSize) {
		this.shoeSize = shoeSize;
	}
	
	public String getShoeURL() {
//		ArrayList<String> results = new ArrayList<String>();
//		String temp1 = "";
		try {
			Map<String, String> temp = googleTest.googleSearch("basketball" + " shoe " + shoeSize);
			Set<Map.Entry<String, String>> tempS = temp.entrySet();
			for (Map.Entry<String, String> entry: tempS) {
				if (entry.getKey().toLowerCase().contains("amazon")) {
//					results.add(entry.getValue());
					return entry.getValue();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "http://www.google.com/";
		
	}
}

class googleTest {
	
	
	public void getPanel() throws MalformedURLException, IOException {
		
		JFXPanel panel = new JFXPanel();
		Platform.runLater( () -> {
			WebView webView = new WebView();
			webView.getEngine().load("http://www.google.com");
			panel.setScene(new Scene(webView));
		});
	}

	
	
	public static Map<String, String> googleSearch(String search) throws UnsupportedEncodingException, IOException {
		String google = "http://www.google.com/search?q=";
//		String search = "stackoverflow";
		String charset = "UTF-8";
		String userAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"; // Change this to your company's name and bot homepage!
		Map<String, String> results = new HashMap<String, String>();
		ArrayList<String> returnResult = new ArrayList<String>();
		
		Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)).userAgent(userAgent).get().select(".g>.r>a");

		for (Element link : links) {
		    String title = link.text();
		    String url = link.absUrl("href");
		    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

		    if (!url.startsWith("http")) {
		        continue;
		    }
		    results.put(title,  url);
		}
		return results;
	}
}

