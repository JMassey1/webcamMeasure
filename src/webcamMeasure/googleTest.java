package webcamMeasure;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.frostwire.gui.webbrowser.BrowserFactory;
import com.frostwire.gui.webbrowser.WebBrowser;

public class googleTest {
	
	
	public static void main(String[] args) {
		try {
			Map<String, String> temp = googleSearch( "basketball shoes size 9.5");
			Set<Map.Entry<String, String>> tempS = temp.entrySet();
			for (Map.Entry<String, String> entry: tempS) {
				if (entry.getKey().toLowerCase().contains("amazon")) {
					System.out.println(entry.getValue() + "");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		WebBrowser browser = BrowserFactory.instance().createBrowser();
		browser.setListener(this);
		
		
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
		    String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
		    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

		    if (!url.startsWith("http")) {
		        continue; // Ads/news/etc.
		    }
		    results.put(title,  url);
		}
		return results;
	}
}
