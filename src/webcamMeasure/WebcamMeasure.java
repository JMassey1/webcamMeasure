package webcamMeasure;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

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

public class WebcamMeasure {
	
	//VARIABLES FOR SURVEY
	private static String[] gender = new String[] {"Male", "Female", "N/A"};
	private static String[] sports = new String[] {"Volleyball", "Tennis", "Basketball", "Football","Baseball/Softball","Soccer","Running"};
	private static String[] sportsFootball = new String[] {"Offensive Line", "Defensive Line", "Line Backer", "Defensive Back/ WR", "Running Back","Not Football"};
	private static String[] typesOfField = new String[] {"Grass", "Turf","Court", "Sand","Treadmill", "Track", "Concrete"};
	
	private static boolean isMale, isFootball;
	private static String sportsS, footballPos, fieldType;
	
	
	
	private static Object[] surveyResults; //Holds ALL THE ABOVE VARIABLES
	
	private static JPanel resultsF;
	private static JFrame mainFrame;
	private static BufferedImage image;
	private static File picture;
	private static UserShoe shoeSize;
	private static Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	private static boolean hasUsedBefore, willCloseSoon = false;
	private static final int THRESHOLD = 50;
	private static final double DISTANCE_WIDTH = 12.5; //Distance in inches from left to right
	//private static final double DISTANCE_TO_MEASURE = 9.25; //Distance in inches from camera to surface
	
	public static void main(String[] args) throws InterruptedException, IOException {
		JFrame survey = new JFrame();
		survey.setTitle("Pre-Measure Survey");
		survey.setLayout(new FlowLayout());
		JPanel title = new JPanel();
		title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
		title.add(new JLabel("<html><h1><strong><i>Shoe Measurer</i></strong></h1><hr></html>"));
		title.add(Box.createHorizontalStrut(5));
		title.add(new JLabel("Jordan Masesy", SwingConstants.CENTER));
		survey.add(title);
		survey.add(Box.createHorizontalStrut(20));

		JPanel questions = new JPanel();
		questions.setLayout(new BoxLayout(questions, BoxLayout.Y_AXIS));
		
		//Gender Label and Question
		questions.add(new JLabel("Gender?", SwingConstants.CENTER));
		JComboBox<String> genderSurvey = new JComboBox<String>(gender);
		genderSurvey.setAlignmentX(SwingConstants.CENTER);
		questions.add(genderSurvey);
		genderSurvey.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> temp = (JComboBox<String>)e.getSource();
				String tempS = ((String)temp.getSelectedItem()).toLowerCase();
				if (tempS.equals("male") && !tempS.contains("fe")) {
//					genderS = tempS;
					isMale = false;
				}
					
				
			}
		});
		questions.add(Box.createVerticalStrut(20));
		
		//Sports Label and Question
		questions.add(new JLabel("Sport?", SwingConstants.CENTER));
		JComboBox<String> sportsSurvey = new JComboBox<String>(sports);
		sportsSurvey.setAlignmentX(SwingConstants.CENTER);
		questions.add(sportsSurvey);
		sportsSurvey.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> temp = (JComboBox<String>)e.getSource();
				sportsS = ((String)temp.getSelectedItem()).toLowerCase();
			}
			
		});
		questions.add(Box.createVerticalStrut(20));
		
		//Position (If applicable) Label and Question
		questions.add(new JLabel("Position? (Only if football, otherwise put \"Not Football\")", SwingConstants.CENTER));
		JComboBox<String> positionSurvey = new JComboBox<String>(sportsFootball);
		positionSurvey.setAlignmentX(SwingConstants.CENTER);
		questions.add(positionSurvey);
		positionSurvey.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> temp = (JComboBox<String>)e.getSource();
				String tempS = ((String)temp.getSelectedItem()).toLowerCase();
				if (tempS.equals("Not Football")) {
					isFootball = false;
				} else {
					isFootball = true;
				}
				footballPos = tempS;
			}
			
		});
		questions.add(Box.createVerticalStrut(20));
		
		//Type of Field (If applicable) Label and Question
		questions.add(new JLabel("Type of Running Environment? (If none appliccable, select N/A)", SwingConstants.CENTER));
		JComboBox<String> fieldSurvey = new JComboBox<String>(typesOfField);
		fieldSurvey.setAlignmentX(SwingConstants.CENTER);
		questions.add(fieldSurvey);
		fieldSurvey.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> temp = (JComboBox<String>)e.getSource();
				fieldType = ((String)temp.getSelectedItem()).toLowerCase();
			}
		});
		questions.add(Box.createVerticalStrut(50));
		
		JButton submit = new JButton("Submit Answers");
		questions.add(new JLabel("ONLY PRESS WHEN DONE WITH QUESTIONS",  SwingConstants.CENTER));
		questions.add(submit);
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				survey.dispatchEvent(new WindowEvent(survey, WindowEvent.WINDOW_CLOSING));
				survey.dispose();
				try {
					surveyResults = new Object[] {isMale, sportsS, isFootball, footballPos, fieldType};
					program(surveyResults);
				} catch (InterruptedException | IOException e1) {
					e1.printStackTrace();
				}
				System.out.println("DONE");
			}
		});
		
		survey.add(questions);
		questions.setAlignmentX(SwingConstants.CENTER);
		survey.add(Box.createHorizontalStrut(20));
		survey.setVisible(true);
		survey.setSize(screen.width,screen.height);
//		survey.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}
	
	
	public static void program(Object[] surveyResults) throws InterruptedException, IOException {
		hasUsedBefore = false;
		java.util.List<Webcam> tempList = Webcam.getWebcams();
		Webcam webcam = tempList.get(tempList.size()-1);
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		System.out.println(WebcamResolution.VGA.getSize());
		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setMirrored(true);
		
		mainFrame = new JFrame("Shoe Measurer");
		JPanel inBetweenPanel = new JPanel();
		JPanel webcamFrame = new JPanel();
		JPanel captureP = new JPanel();
		JPanel captureP1 = new JPanel();
		JButton capture = new JButton("Side View");
		JButton capture1 = new JButton("Front View");
		JLabel response = new JLabel();
		JLabel response1 = new JLabel();
		JRadioButton save = new JRadioButton("Save Picture");
		JRadioButton save1 = new JRadioButton("Save Picture");
		inBetweenPanel.setLayout(new BoxLayout(inBetweenPanel, BoxLayout.Y_AXIS));
		captureP.setLayout(new BoxLayout(captureP, BoxLayout.Y_AXIS));
		captureP1.setLayout(new BoxLayout(captureP1, BoxLayout.Y_AXIS));
		
		JLabel directions = new JLabel("<html><h4><p align=\"left\"><u>Click button one first, then click button two. "
				+ "Results will load once the second button is pressed, so make sure to click it last.</p></h4></u></html>", SwingConstants.LEFT);
		JLabel directions2 = new JLabel("<html><h4><p align=\"left\">Once results are loaded, press ESC to close out and start again</h4></html>", SwingConstants.LEFT);
		Font tempFont = directions.getFont();
		directions.setFont(new Font(tempFont.getName(), Font.PLAIN, 10));
		directions2.setFont(new Font(tempFont.getName(), Font.PLAIN, 10));
		inBetweenPanel.add(directions);
		inBetweenPanel.add(Box.createVerticalStrut(5));
		inBetweenPanel.add(directions2);
		
		
		
		response.setText("Press to Capture");
		response1.setText("Press to Capture");
		response.setBounds(10,110,100,100);
		response1.setBounds(10,110,100,100);
		
		capture.setBounds(100,100,210,50);
		capture1.setBounds(100,100,200,50);
		//SIDE VIEW
		capture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (hasUsedBefore) {
				webcamFrame.remove(resultsF);
				}
				webcamFrame.revalidate();
				response.setText("Image Captured");
				image = webcam.getImage();
					int[] results;
					try {
						results = measureDistance(image);
					} catch (IOException e1) {
						e1.printStackTrace();
						results = new int[] {0,0,0};
					}
					shoeSize = new UserShoe(results, surveyResults);
					System.out.printf("PIXELS%nLength: %d%nHeight: %d%nINCHES%nLength: %f%nWidth: %f",results[0], results[1],pixToInchL(results[0]),pixToInchW(results[1],results[2]));
					resultsF = new JPanel();
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
					inch2.setText("Height: " + Double.toString(pixToInchW(results[1],results[2])) + " in");
					filler.setText("\n");
					text.add(pixelM);
					text.add(pixel1);
					text.add(pixel2);
					text.add(filler);
					text.add(inchM);
					text.add(inch1);
					text.add(inch2);
					resultsF.add(text);
					webcamFrame.add(resultsF);
					webcamFrame.revalidate();
//					resultsF.setVisible(true);
					
					JFrame newWindow = new JFrame();
					JFXPanel panel = new JFXPanel();
					Platform.runLater( () -> {
						WebView webView = new WebView();
						webView.getEngine().load(shoeSize.getShoeURL());
						panel.setScene(new Scene(webView));
					});
					System.out.println(shoeSize.getShoeURL());
					newWindow.add(panel);
					newWindow.setFocusable(true);
					newWindow.requestFocus();
					newWindow.addKeyListener(new KeyListener() {

						@Override
						public void keyPressed(KeyEvent e) {
							System.out.println("HELP ME DEAR GOD");
							if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getModifiers() == 0)
								newWindow.dispatchEvent(new WindowEvent(newWindow, WindowEvent.WINDOW_CLOSING));
						}

						@Override
						public void keyTyped(KeyEvent e) {
							
						}

						@Override
						public void keyReleased(KeyEvent e) {
							
						}
						
					});
					System.out.println(newWindow.getKeyListeners());
					newWindow.setTitle("WebPage");
					newWindow.setVisible(true);
					Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
					newWindow.setSize(screen.width, screen.height);
				if (save.isSelected()) {
					try {
						String fileName;
						File tempFile;
						boolean isThere;
						Random r = new Random();
						do {
							fileName = Integer.toString(r.nextInt(10000000));
							tempFile = File.createTempFile(fileName, ".png");
							isThere = tempFile.exists();
							System.out.println(fileName);
						} while (!isThere);
						picture = new File(fileName + ".png");
						ImageIO.write(image, "PNG", picture);
						response.setText("Press to Capture");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				hasUsedBefore = true;
			}
		});
		//FRONT VIEW
		capture1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (hasUsedBefore) {
					webcamFrame.remove(resultsF);
				}
				webcamFrame.revalidate();
				response1.setText("Image Captured");
				image = webcam.getImage();
				try {
					int[] results = measureDistance(image);
					System.out.printf("PIXELS%nLength: %d%nHeight: %d%nINCHES%nLength: %f%nWidth: %f",results[0], results[1],pixToInchL(results[0]),pixToInchW(results[1],results[2]));
					JPanel resultsF = new JPanel();
					JPanel text = new JPanel();
					text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
					JLabel pixelM = new JLabel("PIXEL MEASUREMENTS");
					JLabel inchM = new JLabel("IMPERIAL SYSTEM MEASUREMENTS");
					JLabel pixel1 = new JLabel("Length: " + Integer.toString(results[0]) + " px");
					JLabel pixel2 = new JLabel("Height: " + Integer.toString(results[1]) + " px");
					JLabel inch1 = new JLabel("Length: " + Double.toString(pixToInchL(results[0])) + " in");
					JLabel inch2 = new JLabel("Height: " + Double.toString(pixToInchW(results[1],results[2])) + " in");
					JLabel filler = new JLabel("\n");
					pixelM.setFont(new Font("Calibri", Font.BOLD, 20));
					inchM.setFont(new Font("Calibri", Font.BOLD, 20));
					text.add(pixelM);
					text.add(pixel1);
					text.add(pixel2);
					text.add(filler);
					text.add(inchM);
					text.add(inch1);
					text.add(inch2);
					
					resultsF.add(text);
					webcamFrame.add(resultsF);
					webcamFrame.revalidate();
					
					JFrame newWindow = new JFrame();
					JFXPanel panel = new JFXPanel();
					Platform.runLater( () -> {
						WebView webView = new WebView();
						webView.getEngine().load(shoeSize.getShoeURL());
						panel.setScene(new Scene(webView));
					});
					System.out.println(shoeSize.getShoeURL());
					newWindow.add(panel);
					newWindow.setFocusable(true);
					newWindow.requestFocus();
					newWindow.addKeyListener(new KeyListener() {

						@Override
						public void keyTyped(KeyEvent e) {
							if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getModifiers() == 0)
								newWindow.dispatchEvent(new WindowEvent(newWindow, WindowEvent.WINDOW_CLOSING));
						}

						@Override
						public void keyPressed(KeyEvent e) {
							
						}

						@Override
						public void keyReleased(KeyEvent e) {
							
						}
						
					});
					System.out.println(newWindow.getKeyListeners());
					newWindow.setTitle("WebPage");
					newWindow.setVisible(true);
					Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
					newWindow.setSize(screen.width, screen.height);
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (save1.isSelected()) {
					try {
						String fileName;
						File tempFile;
						boolean isThere;
						Random r = new Random();
						do {
//							fileName = Integer.toString(0 + (int)(Math.random() + 1) * 100000);
							fileName = "a" + Integer.toString(r.nextInt(10000000));
							tempFile = File.createTempFile(fileName, ".png");
							isThere = tempFile.exists();
						} while (!isThere);
						picture = new File(fileName + ".png");
						ImageIO.write(image, "PNG", picture);
						response.setText("Press to Capture");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				hasUsedBefore = true;
			}
		});
		webcamFrame.setLayout(new FlowLayout());
		
		
		captureP.add(capture);
		captureP.add(response);
		captureP.add(save);
		
		captureP1.add(capture1);
		captureP1.add(response1);
		captureP1.add(save1);
		webcamFrame.add(panel);
		webcamFrame.add(captureP1);
		webcamFrame.add(captureP);
		inBetweenPanel.add(webcamFrame);
		mainFrame.add(inBetweenPanel);
		mainFrame.setResizable(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		webcamFrame.pack();
		mainFrame.setSize(screen.width, screen.height);
		mainFrame.setVisible(true);
//		System.out.println("3  " + counter);
	}
	public static int[] measureDistance(Image img) throws IOException {
		BufferedImage grayImg = makeGrayscale(img);
		Graphics2D grayImgGraphics = grayImg.createGraphics();
		grayImgGraphics.drawImage(grayImg, 0, 0, Color.WHITE, null);
		int pixel,count,max,height,heightCount = 1;
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
		return new int[] {max,height, heightCount};
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
				temp.setRGB(x, y, makeGrayPix(pixel));
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
	
	
	public static double pixToInchL(int px) {
		return (((double)px)*DISTANCE_WIDTH/640);
	}
	public static double pixToInchW(int px, int hCount) {
		return (((double)px)*hCount/480);
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

class UserShoe {
	//Size then Inches
	private static final double[][] MENS_SIZE = new double[][]{{7.0,9.6},{7.5,9.75},{8.0,9.9},{8.5,10.125},{9.0,10.25},{9.5,10.4},{10.0,10.6},{10.5,10.75},{11.0,10.9},{11.5, 11.125},{12.0,11.25},{13.0,11.6}};
	private static final double[][] WOMENS_SIZE = new double[][] {{6.0,8.75},{6.5,9.0},{7.0,9.25},{7.5,9.375},{8.0,9.5},{8.5,9.75},{9.0,9.875},{9.5,10.0},{10.0,10.2},{10.5,10.35},{11,10.5}};
	private double footL;
	private double shoeSize;
	boolean isMale,isFootball; //True if Man, False if Woman
	String sportsS,footballPos,fieldType;
	
	
	public UserShoe(int[] foot, Object[] survey) {
		footL = (double)foot[0];
		isMale = (boolean)survey[0];
		sportsS = (String)survey[1];
		isFootball = (boolean)survey[2];
		footballPos = (String)survey[3];
		fieldType = (String)survey[4];
		shoeSize = getShoeSize();
	}
	
	public double getShoeSize() {
		if (this.isMale) {
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
	}

	public void setShoeSize(double shoeSize) {
		this.shoeSize = shoeSize;
	}
	
	public String getShoeURL() {
		try {
			Map<String, String> searchList;
			if (isFootball) { //If in football
				searchList = searchGoogle.googleSearch(sportsS + " " + footballPos + " " + fieldType + 
						" size " + shoeSize + " \"shoe\"");
			} else { //If not in football
				searchList = searchGoogle.googleSearch(sportsS + " " + fieldType + " size " + 
			shoeSize + " \"shoe\"");
			}
			Set<Map.Entry<String, String>> searchListSet = searchList.entrySet();
			for (Map.Entry<String, String> entry: searchListSet) { 
				if (entry.getKey().toLowerCase().contains("amazon")) {
					return entry.getValue();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Set<Map.Entry<String, String>> takeTwo = searchGoogle.googleSearch(sportsS + " \"shoe\" size " + shoeSize).entrySet();
			for (Map.Entry<String, String> entry: takeTwo) {
				if (entry.getKey().toLowerCase().contains("amazon")) {
					return entry.getValue();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "http://www.google.com/";
		
	}
}

class searchGoogle {
	
	public static Map<String, String> googleSearch(String search) throws UnsupportedEncodingException, IOException {
		String google = "http://www.google.com/search?q=";
		String charset = "UTF-8";
		String userAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
		Map<String, String> results = new HashMap<String, String>();
		
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