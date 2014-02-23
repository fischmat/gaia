package sep.gaia.state.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InvalidClassException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import sep.gaia.state.Preferences;

public class PreferencesTest {
	
	@Test
	public void testStore() throws InvalidClassException {
		
		try {
			// Change the value of the maximal cache size of GAIA to 50 MB before testing
			if ((Preferences.getInstance().getFloat("cache", 0)) == "50.0") { // ... & other settings
				Preferences.getInstance().store();
				
				File file = new File("prefFile.xml");
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.parse(file);
				
				String[] strArray = new String[4];
				
				for (int counter = 0; counter < 3; counter++) {
					
					Node temp = doc.getChildNodes().item(counter);
					
					// The node has to be an element
					if (temp.getNodeType() == Node.ELEMENT_NODE) {
						if (temp.hasAttributes()) {
							NamedNodeMap map = temp.getAttributes();
							
							// Get the value of the node
							Node node = map.item(1);
							
							strArray[counter] = node.getNodeValue();
						}			 
					}
				}
				// The value of the maximal cache size of the read xml file has to be 50 MB
				assertEquals("50.0", strArray[0]);
				/*
				 * To be continued for other settings...
				 * assertEquals(,);
				 * assertEquals(,);
				 * assertEquals(,);
				 */
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	public void testLoad() {
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			// the settings list
			Document doc = builder.newDocument();
			Element settingsList = doc.createElement("setting_list");
			doc.appendChild(settingsList);
			
			// the maximal cache size
			Element cache = doc.createElement("max_cache");
			cache.setAttribute("key","cache");
			cache.setAttribute("value", "100.0");
			cache.setAttribute("type", "float");
			settingsList.appendChild(cache);
			
			// save it in a new file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult res = new StreamResult(new File("prefFile.xml"));
			transformer.transform(source, res);
			
			// load the preferences xml
			//Preferences.getInstance().load();
			
			// are the values equal?
			try {
				assertEquals(Preferences.getInstance().getFloat("cache", 0), "100.0");
			} catch (InvalidClassException e) {
				e.printStackTrace();
			}
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
}
