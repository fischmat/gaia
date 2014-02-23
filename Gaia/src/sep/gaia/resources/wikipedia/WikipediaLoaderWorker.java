package sep.gaia.resources.wikipedia;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sep.gaia.resources.AbstractLoaderWorker;
import sep.gaia.resources.Cache;
import sep.gaia.resources.DataResource;
import sep.gaia.resources.Query;
import sep.gaia.resources.tiles2d.TileResource;

/**
 * The <code>WikipediaLoaderWorker</code> class object which runs in its own
 * thread.
 * 
 * As a child of <code>AbstractLoaderWorker</code>, an overgiven query in form
 * of a <code>Query</code> object to the Wikipedia API in a single worker
 * is started. If successful, the response is a String which is given back to
 * the <code>WikipediaData</code> dummy object after being converted by the
 * <code>WikipediaMarkupParser</code>.
 * 
 * @author Michael Mitterer
 */
public class WikipediaLoaderWorker extends AbstractLoaderWorker<Query, WikipediaData> {
	/**
	 * Constructor with the task which this worker has to do with the
	 * <code>Query</code> object.
	 * 
	 * @param subQuery The query, which this thread has to treat.
	 */
	public WikipediaLoaderWorker(Query subQuery, Cache<WikipediaData> cache) {
		super(subQuery, cache);
	}
	
	@Override
	public void run() {
		Query query = this.getSubQuery();
		if(query != null) {
			Collection<DataResource> res = query.getResources();
			List<WikipediaData> wikiResources = new LinkedList<>();
			
			String shortDescriptionMarkup = null;
			String summary = null;
			
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document document;
				BufferedReader apiInput;
				
				for (DataResource dummy : res) {
					if(dummy instanceof WikipediaData) {
						WikipediaData data = (WikipediaData) dummy;
						apiInput = new BufferedReader(new InputStreamReader(WikiMarkupParser.getUrl(data.getName()).openStream()));
						String tempLine;
						StringBuilder responseBuilder = new StringBuilder();
						while((tempLine = apiInput.readLine()) != null) {
							responseBuilder.append(tempLine);
						}
						shortDescriptionMarkup = responseBuilder.toString();
						
						document = dBuilder.parse(new ByteArrayInputStream(shortDescriptionMarkup.getBytes()));
						
						NodeList revisions = document.getElementsByTagName("rev");
						if(revisions.getLength() != 0) {
				    		Node node = revisions.item(0);
				
				            summary = WikiMarkupParser.markupToPlainText(node.getTextContent(), data.getName());
				            
				            data.setSummaryText(summary);
							
							Cache<WikipediaData> cache = getCache();
							dummy.setDummy(false);
							data.setDummy(false);
							wikiResources.add(data);
							cache.add(data);
						} else {
							wikiResources = null;
						}
					}
				}
	
			} catch (MalformedURLException e) {
				
			} catch (IOException e) {
				
			} catch (ParserConfigurationException e) {

			} catch (SAXException e) {
				
			}
			setResults(wikiResources);
		}
	}
}
