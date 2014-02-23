package sep.gaia.resources.mode3d;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.resources.DataResourceManager;
import sep.gaia.util.Logger;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * This class implements the three dimensional view of the earth by visualizing
 * a texture of the globe in OpenGL and holding the radius of the earth. There
 * are two features available: first, the rotation around the vertical and
 * horizontal axis of the earth and second, the zoom in and zoom out function.
 * 
 * @author Fabian Buske (specification), Max Witzelsperger (implementation), Matthias Fisch (implementation)
 * 
 */
public class Earth {

	/**
	 * the radius of the earth which can be modified in order to zoom
	 */
	private float radius;
	
	/**
	 * The key of the texture currently used to visualize the earth in 3d
	 */
	private String currentTextureKey;
	
	/**
	 * The names of all available textures
	 */
	private Map<String, String> texFilesByName = new HashMap<>();
	
	/**
	 * All available textures
	 */
	private Map<String, Texture> texturesByName = new HashMap<>();

	private String defaultTexture;
	
	// private float[] matrix =
	// {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};

	/**
	 * This method initializes the earth with a specific <code>radius</code>.
	 * 
	 * @param radius
	 *            the radius of the earth measured in OpenGL coordinates
	 */
	public Earth(float radius) {
		this.radius = radius;
		
		// Little hack TODO
		scheduleAvailableTextures(GLProfile.getDefault());
	}

	/**
	 * This method returns the actual radius of the earth.
	 * 
	 * @return the actual radius of the earth
	 */
	public float getRadius() {
		return this.radius;
	}

	/**
	 * Gets the current earth texture.
	 * 
	 * @return the texture
	 * @throws Thrown if no texture was set yet.
	 */
	public Texture getCurrentTexture() throws IllegalStateException {
		if(currentTextureKey != null) {
			return texturesByName.get(currentTextureKey);
			
		} else {
			throw new IllegalStateException("No texture specified yet.");
		}
	}

	/**
	 * Schedules all the available textures from a specific XML file for loading.
	 *  
	 * @param profile the current GL-profile
	 * 
	 * @return <code>true</code> if and only if loading the textures was
	 * successful
	 */
	public boolean scheduleAvailableTextures(GLProfile profile) {
		if(!loadTexDataFromXML()) {
			return false;
		}
		
		for(String key : texFilesByName.keySet()) {
			String textureFile = texFilesByName.get(key);
			
			texFilesByName.put(key, textureFile);
		}
		
		return true;
	}
	
	/**
	 * Creates all textures scheduled by <code>scheduleAvailableTextures()</code>.
	 * This method must be called from the OpenGL-thread in order to prevent
	 * unexpected behavior.
	 * @param gl The GL function-set to use.
	 */
	public void createScheduledTextures(GL2 gl) {
		for(String key : texFilesByName.keySet()) {
			String fileName = texFilesByName.get(key);
			
			Texture texture;
			try {
				texture = TextureIO.newTexture(new File(fileName), false);
			} catch (GLException e) {
				Logger.getInstance().error("Failed creating texture for 3D-mode. (" + key + ")");
				return;
				
			} catch (IOException e) {
				Logger.getInstance().error("Error reading file " + fileName);
				return;
			}
			
			texturesByName.put(key, texture);
		}
	}
	
	/**
	 * This method sets the radius of the earth.
	 * 
	 * @param radius
	 *            the radius of the earth to be set
	 */
	public void setRadius(float radius) {
		this.radius = radius;
	}

	/**
	 * Sets the texture to be depicted.
	 * 
	 * @param name the file name of the texture to be set
	 * 
	 * @throws IllegalArgumentException if the name of the given texture
	 * is not available in the internal list of textures of <code>this</code>
	 */
	public void setCurrentTexture(String name) throws IllegalArgumentException {
		
		currentTextureKey = name;
	}

	/**
	 * Gets all the file names of the available textures.
	 * 
	 * @return the names of the available textures as a list of strings
	 */
	public List<String> getAvailableTextureNames() {
		return new LinkedList<>(texFilesByName.keySet());
	}

	private boolean loadTexDataFromXML() {
		// Get the application-environment:
		Environment environment = Environment.getInstance();
		// Get the XML-file containing the styles:
		String xmlPath = environment.getString(EnvVariable.TEXTURE_3D_INDEX);
		// Get the XML-Schema-Definition describing valid formats for the
		// XML-file:
		String schemaPath = environment.getString(
				EnvVariable.TEXTURE_3D_INDEX_SCHEMA);

		Schema schema;
		// Get a factory for creation of new XML-schemas:
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			// Create the schema from the XSD-file:
			schema = schemaFactory.newSchema(new File(schemaPath));
		} catch (SAXException e) {
			return false;
		}
		// Get a validator for the read schema:
		Validator schemaValidator = schema.newValidator();

		// Get a factory for creation of document-builders and create one:
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// Return with error if the builder was not configured correctly:
			return false;
		}

		// Now get the actual document:
		Document doc;
		try {
			doc = docBuilder.parse(new File(xmlPath));
		} catch (SAXException | IOException e) {
			// If the XML could not be parsed or if the file does not exist:
			return false;
		}

		// Validate the XML:
		try {
			schemaValidator.validate(new DOMSource(doc));
		} catch (SAXException | IOException e) {
			// If validation fails or if the underlying XMLReader throws
			// IOException:
			return false;
		}

		// Get all texture-tags:
		NodeList textureNodes = doc.getElementsByTagName("texture");

		// Iterate all texture-tags:
		for (int i = 0; i < textureNodes.getLength(); i++) {
			// The variables to be read:
			String name = null; // The name of the texture
			String file = null; // The path to the texture-file

			// Get the current texture-node:
			Node currentTextureNode = textureNodes.item(i);
			
			NodeList textureChilds = currentTextureNode.getChildNodes();
			
			for(int j = 0; j < textureChilds.getLength(); j++) {
				Node currentChild = textureChilds.item(j);
				
				String tag = currentChild.getNodeName();
				String inner = currentChild.getTextContent();
				
				// Set the respective variable for each tag:
				if(tag.equals("name")) {
					name = inner;
					
				} else if(tag.equals("file")) {
					
					file = inner;
				}
			}
			
			if(defaultTexture == null) {
				defaultTexture = name;
			}
			
			// Add the read key-value-pair to the map of texture-files:
			texFilesByName.put(name, file);
		}
		
		return true;
	}

	public String getDefaultTexture() {
		return defaultTexture;
	}

}
