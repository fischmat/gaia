package sep.gaia.resources.wikipedia;


import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Stack;

import sep.gaia.util.Logger;

/** 
 * Static class which holds the functionality to identify the short description
 * and to convert it from the whole wikipedia article in wikipedia markup
 * language.
 * @author Michael Mitterer
 */
public class WikiMarkupParser {
	/** 
	 * Conversion of a String in wikipedia markup language to the short
	 * description String in plain text
	 * 
	 * @param markup The wikipedia markup language String
	 * @return The resulting short description String
	 */
	public static String markupToPlainText(String markup, String title) {
		WikiModel wikiModel = new WikiModel("http://de.wikipedia.org/wiki/${image}", "http://de.wikipedia.org/wiki/${title}");
		
		String encodedTitle;
		try {
			encodedTitle = URLEncoder.encode(title, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Logger.getInstance().error("UTF-8 encoding not supported.");
			return null;
		}
		
		markup = markup.replaceAll("\\{[^\\(]*\\}", "");
		
        String html = wikiModel.render(markup).replaceAll("<ref>"," <ref>").replaceAll("&nbsp;", " ");
        
        html += "<br/><a href=\"http://de.wikipedia.org/wiki/" + encodedTitle + "\">Gesamten Artikel lesen</a>";
        
        return html;
	}
	
	/**
	 * Conversion of a String in wikipedia markup language to an API-URL Object
	 * that is needed for an API database search
	 * 
	 * @throws MalformedURLException 
	 * @param title The wikipedia markup language String
	 * @return The resulting API-URL Object
	 */
	public static URL getUrl(String title) throws MalformedURLException {
		String encodedTitle;
		try {
			encodedTitle = URLEncoder.encode(title, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Logger.getInstance().error("UTF-8 encoding not supported.");
			return null;
		}
		URL url = new URL("http://de.wikipedia.org/w/api.php?format=xml&rvsection=0&prop=text&action=query&titles=" + encodedTitle + "&prop=revisions&rvprop=content");
		return url;
	}
}