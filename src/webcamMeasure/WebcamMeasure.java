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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
	private static String[] sports = new String[] {"Volleyball", "Tennis", "Basketball", "Football","Baseball/Softball","Soccer","Running", "No Sport"};
	private static String[] sportsFootball = new String[] {"Offensive Line", "Defensive Line", "Line Backer", "Defensive Back/ WR", "Running Back","Not Football"};
	private static String[] typesOfField = new String[] {"Grass", "Turf","Court", "Sand","Treadmill", "Track", "Concrete", "N/A"};
	
	private static boolean isMale = true, isSport, isFootball, doesField, hasUsedBefore;
	private static String sportsS, footballPos, fieldType;
	private static Object[] surveyResults; //Holds ALL THE ABOVE VARIABLES	
	
	private static int footWidth;
	private static JPanel resultsF;
	private static JFrame mainFrame, survey;
	private static BufferedImage image;
	private static UserShoe shoeSize;
	private static Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	private static int THRESHOLD = 10;
	private static double DISTANCE_WIDTH = 4; //Distance in inches from left to right
	
	
	public static void main(String[] args) throws InterruptedException, IOException {
		survey = new JFrame();
		survey.setTitle("Pre-Measure Survey");
		survey.setLayout(new FlowLayout());
		JPanel title = new JPanel();
		title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
		title.add(new JLabel("<html><h1><strong><i>Shoe Measurer</i></strong></h1><hr></html>"));
		title.add(Box.createHorizontalStrut(5));
		title.add(new JLabel("Senior Capstone", SwingConstants.CENTER));
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
				if (tempS.equals("male") && tempS.indexOf("fe") == -1) {
					isMale = true;
				} else {
					isMale = true;
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
				if (sportsS.contains("no sport")) {
					isSport = false;
				} else { isSport = true;}
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
				if (tempS.equals("not football")) {
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
				if (fieldType.contains("n/a")) {
					doesField = false;
				} else { doesField = true;}
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
					surveyResults = new Object[] {isMale, isSport, sportsS, isFootball, footballPos, doesField, fieldType};
					program(surveyResults);
				} catch (InterruptedException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton settingsStuff = new JButton("Settings");
		questions.add(settingsStuff);
		settingsStuff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				settingsSurvey();
			}
		});
		
		survey.add(questions);
		questions.setAlignmentX(SwingConstants.CENTER);
		survey.add(Box.createHorizontalStrut(20));
		survey.setVisible(true);
		survey.setSize(screen.width,screen.height);
		
	}
	
	public static void settingsSurvey() {
		JFrame sett = new JFrame();
		JPanel settings = new JPanel();
		settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));
		
		JPanel thresholdPanel = new JPanel();
		thresholdPanel.setLayout(new FlowLayout());
		thresholdPanel.add(new JLabel("THRESHOLD(0-255):   "));
		JTextField thresh = new JTextField(20);
		thresholdPanel.add(thresh);
		
		JPanel widthPanel = new JPanel();
		widthPanel.setLayout(new FlowLayout());
		widthPanel.add(new JLabel("WIDTH THAT CAM SEES(INCHES):    "));
		JTextField widthD = new JTextField(20);
		widthPanel.add(widthD);
		
		settings.add(thresholdPanel);
		settings.add(Box.createVerticalStrut(10));
		settings.add(widthPanel);
		JButton closeSettings = new JButton("SAVE AND CLOSE");
		closeSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(THRESHOLD = Integer.parseInt(thresh.getText()));
				System.out.println(DISTANCE_WIDTH = Double.parseDouble(widthD.getText()));
				sett.dispatchEvent(new WindowEvent(sett, WindowEvent.WINDOW_CLOSING));
				sett.dispose();
			}
			
		});
		settings.add(closeSettings);
		sett.add(settings);
		sett.setVisible(true);
		sett.pack();
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
		JButton capture = new JButton("Foot Length (Side View)");
		JButton capture1 = new JButton("Foot Width (Front View)");
		JLabel response = new JLabel();
		JLabel response1 = new JLabel();
		inBetweenPanel.setLayout(new BoxLayout(inBetweenPanel, BoxLayout.Y_AXIS));
		captureP.setLayout(new BoxLayout(captureP, BoxLayout.Y_AXIS));
		captureP1.setLayout(new BoxLayout(captureP1, BoxLayout.Y_AXIS));
		
		JLabel directions = new JLabel("<html><h4><p align=\"left\"><u>Click button one first, then click button two. "
				+ "Results will load once the second button is pressed, so make sure to click it last.</p></h4></u><h4><p align=\"left\"><u>If Webpage is black then closes, remeasure OR your foot is too big for standard shoe sizes</u></p></h4></html>", SwingConstants.LEFT);
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
						results = new int[] {0,0,0};
						e1.printStackTrace();
					}
					shoeSize = new UserShoe(pixToInchL(results[0]), surveyResults, DISTANCE_WIDTH);
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
					pixel2.setText("Width: " + Integer.toString(footWidth) + " px");
					inch1.setText("Length: " + Double.toString(pixToInchL(results[0])) + " in");
					inch2.setText("Width: " + Double.toString(pixToInchL(footWidth)) + " in");
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
					
					if (shoeSize.getShoeURL().equals("http://www.google.com/")) {
						JFrame error = new JFrame();
						error.add(new JLabel("<html><h1><u><i>COULD NOT FIND SHOE, TRY REMEASURING</i></u></h1></html>", SwingConstants.CENTER));
						error.setVisible(true);
						error.setSize(screen.width, screen.height);
						try {
							Thread.sleep(5000);
							error.dispatchEvent(new WindowEvent(error, WindowEvent.WINDOW_CLOSING));
							error.dispose();
						} 
						catch (InterruptedException e) {e.printStackTrace();}
					} else {
					
					JFrame newWindow = new JFrame();
					JFXPanel panel = new JFXPanel();
					Platform.runLater( () -> {
						WebView webView = new WebView();
						webView.getEngine().load(shoeSize.getShoeURL());
						panel.setScene(new Scene(webView));
					});
					newWindow.add(panel);
					newWindow.setFocusable(true);
					newWindow.requestFocus();
					newWindow.addKeyListener(new KeyListener() {

						@Override
						public void keyPressed(KeyEvent e) {
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
					newWindow.setTitle("WebPage -  Estimated Shoe Size ~~ " + shoeSize.returnShoeSize());
					newWindow.setVisible(true);
					Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
					newWindow.setSize(screen.width, screen.height);
			}
				hasUsedBefore = true;
			}
		});
		capture1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (hasUsedBefore) {
					webcamFrame.remove(resultsF);
				}
				webcamFrame.revalidate();
				webcamFrame.repaint();
				response1.setText("Image Captured");
				image = webcam.getImage();
				try {
					int[] results = measureDistance(image);
					footWidth = results[0];
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		webcamFrame.setLayout(new FlowLayout());
		
		
		captureP.add(capture);
		captureP.add(response);	
		captureP1.add(capture1);
		captureP1.add(response1);
		webcamFrame.add(panel);
		webcamFrame.add(captureP1);
		webcamFrame.add(captureP);
		inBetweenPanel.add(webcamFrame);
		JButton settingsStuff = new JButton("Settings");
		inBetweenPanel.add(settingsStuff);
		settingsStuff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				settingsSurvey();
			}
		});
		mainFrame.add(inBetweenPanel);
		mainFrame.setResizable(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(screen.width, screen.height);
		mainFrame.setVisible(true);
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
		return (((double)px)*DISTANCE_WIDTH/640.0);
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
	private static final double[][] MENS_SIZE = new double[][]{{7.0,9.6},{7.5,9.75},{8.0,9.9},{8.5,10.125},{9.0,10.25},{9.5,10.4},{10.0,10.6},{10.5,10.75},{11.0,10.9},{11.5, 11.125},{12.0,11.25},{13.0,11.6}};
	private static final double[][] WOMENS_SIZE = new double[][] {{6.0,8.75},{6.5,9.0},{7.0,9.25},{7.5,9.375},{8.0,9.5},{8.5,9.75},{9.0,9.875},{9.5,10.0},{10.0,10.2},{10.5,10.35},{11,10.5}};
	private double footL;
	private double shoeSize;
	private double DISTANCE_WIDTH;
	boolean isMale,isFootball,isSport,doesField; //True if Man, False if Woman
	String sportsS,footballPos,fieldType;
	
	
	public UserShoe(double footLength, Object[] survey, double DISTANCE_WIDTH) {
		footL = footLength;
		isMale = (boolean)survey[0];
		isSport = (boolean)survey[1];
		sportsS = (String)survey[2];
		isFootball = (boolean)survey[3];
		footballPos = (String)survey[4];
		doesField = (boolean)survey[5];
		fieldType = (String)survey[6];
		this.DISTANCE_WIDTH = DISTANCE_WIDTH;
		shoeSize = getShoeSize();
	}
	
	public double returnShoeSize() {
		return shoeSize;
	}
	
	public double pixToInchL(int px) {
		return (((double)px)*(double)DISTANCE_WIDTH/(double)640.0);
	}	
	
	public double getShoeSize() {
		if (isMale) {
			System.out.print(footL);
			for (double[] shoes: MENS_SIZE) {
				if (shoes[1] >= footL) {
					return shoes[0];
				}
			}
			if (footL > 11.6) {
				return 9999999999999999.9;
			} else {
			return MENS_SIZE[0][0];
			}
		} else {
			for (double[] shoes: WOMENS_SIZE) {
				if (shoes[1] >= footL) {
					return shoes[0];
				}
			}
			if (footL > 10.5) {
				return 9999999999999999.9;
			} else {
			return WOMENS_SIZE[WOMENS_SIZE.length - 1][0];
			}
		}
	}

	public void setShoeSize(double shoeSize) {
		this.shoeSize = shoeSize;
	}
	
	public String getShoeURL() {
		try {
			Map<String, String> searchList;
			String query = "";
			if (isSport) {query += sportsS + " ";}
			if (isFootball) {query += footballPos + " ";}
			if (doesField) {query += fieldType + " ";}
			query += "size " + shoeSize + " shoe buy";
			System.out.println(query);
			searchList = searchGoogle.googleSearch(query);
			Set<Map.Entry<String, String>> searchListSet = searchList.entrySet();
			for (Map.Entry<String, String> entry: searchListSet) { 
				if (entry.getKey().toLowerCase().contains("amazon")) {return entry.getValue();}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Set<Map.Entry<String, String>> takeTwo = searchGoogle.googleSearch("\"shoe\" amazon size buy" + shoeSize).entrySet();
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