package webcamMeasure;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

public class googleTest1 {
	
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		try {
			Map<String, String> temp = googleSearch( "basketball shoes size 9.5");
			Set<Map.Entry<String, String>> tempS = temp.entrySet();
			for (Map.Entry<String, String> entry: tempS) {
				if (entry.getKey().toLowerCase().contains("amazon")) {
					System.out.println(entry.getValue() + "");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		JFrame frame = new JFrame("website");
		JFXPanel panel = new JFXPanel();
		Platform.runLater( () -> {
			WebView webView = new WebView();
			webView.getEngine().load("http://www.google.com");
			panel.setScene(new Scene(webView));
		});
		frame.add(panel);
		frame.setResizable(true);
		frame.setSize(100,100);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	
	
	public static Map<String, String> googleSearch(String search) throws UnsupportedEncodingException, IOException {
		String google = "http://www.google.com/search?q=";
//		String search = "stackoverflow";
		String charset = "UTF-8";
		String userAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"; // Change this to your company's name and bot homepage!
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
