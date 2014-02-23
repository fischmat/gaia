package sep.gaia.resources.poi;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

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
import sep.gaia.renderer.layer.ResourceAdapter;
import sep.gaia.resources.Cache;
import sep.gaia.resources.DataResourceManager;
import sep.gaia.resources.Loader;
import sep.gaia.resources.LoaderEventListener;
import sep.gaia.resources.ResourceMaster;
import sep.gaia.resources.ResourceObserver;
import sep.gaia.resources.WorkerFactory;
import sep.gaia.state.AbstractStateManager.StateType;
import sep.gaia.state.GLState;
import sep.gaia.state.State;
import sep.gaia.state.StateManager;
import sep.gaia.util.AlgoUtil;
import sep.gaia.util.FloatBoundingBox;
import sep.gaia.util.FloatVector3D;

/**
 * Manages <code>PointOfInterest</code>-objects using appropriate major- and sub-categories.
 * Instances of this class observe the <code>GeoState</code>-object and request new data if
 * necessary.
 * @author Matthias Fisch (specification/implementation)
 *
 */
public class POIManager extends DataResourceManager<PointOfInterest> implements LoaderEventListener<PointOfInterest> {

	/**
	 * The label used to identify a POIManager.
	 */
	public static final String MANAGER_LABEL = "POIManager";
	
	/**
	 * The smallest zoom-level the manager is active.
	 */
	private static final int MIN_ACTIVE_LEVEL = 12;
	
	/**
	 * The scale of the query-boxes relative to the current counding-box.
	 */
	private static final float QUERY_BOX_SCALE = 2.0f;
	
	/**
	 * Collection of categories being managed by this instance.
	 */
	private Map<String, POICategory> categoriesByName = new HashMap<>();

	/**
	 * The loader this manager uses for retrieving data.
	 */
	private Loader<POIQuery, PointOfInterest> loader;
	
	private boolean clearRequired;
	
	/**
	 * The area of POIs currently loaded.
	 */
	private FloatBoundingBox loadedArea;
	
	/**
	 * The zoom-level of the last update.
	 */
	private int lastZoom;
	
	/**
	 * Initializes the manager.
	 */
	public POIManager() {
		super(MANAGER_LABEL);
		// Create a cache
		Cache<PointOfInterest> cache = new POICache();

		// Create a object for creating new workers
		WorkerFactory<POIQuery, PointOfInterest> factory = new POIWorkerFactory();

		// Create the loader itself and pass the created objects
		loader = new Loader<>(cache, factory, null); // Create the loader itself and pass the created objects
		
		loader.addListener(this);
		
		loader.start();
		
		loadFromXML();
	}

	/**
	 * Loads the description for the major- and subcategories from a xml-formatted file
	 * and creates them.
	 */
	private boolean loadFromXML() {
		// Get the application-environment:
		Environment environment = Environment.getInstance();
		// Get the XML-file containing the cached tiles:
		String xmlPath = environment.getString(EnvVariable.POI_CATEGORY_FILE);
		// Get the XML-Schema-Definition describing valid formats for the XML-file:
		String schemaPath = environment.getString(EnvVariable.POI_CATEGORY_FILE_SCHEMA);
		
		Schema schema;
		// Get a factory for creation of new XML-schemas:
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
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
		} catch(SAXException | IOException e) {
			// If validation fails or if the underlying XMLReader throws IOException:
			return false;
		}
		
		// Get all category-tags:
		NodeList categoryNodes = doc.getElementsByTagName("category");
		
		// Iterate all category-tags:
		for(int i = 0; i < categoryNodes.getLength(); i++) {
			String categoryName = null;
			POIFilter categoryFilter = new POIFilter();
			Collection<SubCategory> subCategories = new LinkedList<>();
			
			// Iterate all child-nodes:
			NodeList categoryChilds = categoryNodes.item(i).getChildNodes();
			for(int j = 0; j < categoryChilds.getLength(); j++) {
				Node currentCategoryChild = categoryChilds.item(j);
				
				if(currentCategoryChild.getNodeName().equals("name")) {
					// Adopt name:
					categoryName = currentCategoryChild.getTextContent();
					
				} else if(currentCategoryChild.getNodeName().equals("attribute")) {
					
					String key = null;
					String value = null;
					
					// Iterate the children defining key and value:
					NodeList attributeChilds = currentCategoryChild.getChildNodes();
					for(int k = 0; k < attributeChilds.getLength(); k++) {
						Node currentAttributeChild = attributeChilds.item(k);
						
						if(currentAttributeChild.getNodeName().equals("key")) {
							key = currentAttributeChild.getTextContent();
							
						} else if(currentAttributeChild.getNodeName().equals("value")) {
							value = currentAttributeChild.getTextContent();
						}
					}
					
					// Add limitation to filter:
					categoryFilter.addLimitation(key, value);
					
				} else if(currentCategoryChild.getNodeName().equals("subcategory")) {
					
					String subCatName = null;
					POIFilter subcatFilter = new POIFilter();
					
					// Iterate the subcategories childs:
					NodeList subCatChilds = currentCategoryChild.getChildNodes();
					for(int k = 0; k < subCatChilds.getLength(); k++) {
						Node currentSubCatChild = subCatChilds.item(k);
						
						String tag = currentSubCatChild.getNodeName();
						
						if(tag.equals("name")) {
							// Adpot the name:
							subCatName = currentSubCatChild.getTextContent();
							
						} else if(tag.equals("attribute")) {
							
							String key = null;
							String value = null;
							
							// Iterate the children defining key and value:
							NodeList attributeChilds = currentSubCatChild.getChildNodes();
							for(int l = 0; l < attributeChilds.getLength(); l++) {
								Node currentAttributeChild = attributeChilds.item(l);
								
								tag = currentAttributeChild.getNodeName();
								
								if(tag.equals("key")) {
									key = currentAttributeChild.getTextContent();
									
								} else if(tag.equals("value")) {
									value = currentAttributeChild.getTextContent();
								}
							}
							
							// Add the limitation to sub-categories filter:
							subcatFilter.addLimitation(key, value);
						}
					}
					
					// Add sub-category
					subCategories.add(new SubCategory(subCatName, subcatFilter, false));
				}
				
				if(j == categoryChilds.getLength() - 1) {
					// Create and add the category:
					categoriesByName.put(categoryName, new POICategory(categoryName, subCategories, true, categoryFilter));
				}
			}
		}
		
		return true;
	}

	@Override
	public void onUpdate(State state) {
		// Check if notification comes from the geo-coordinate-view of state:
		if(state != null && state instanceof GLState) {
			GLState glState = (GLState)state;
			
			
			int tileZoom = AlgoUtil.glToTileZoom(glState.getZoom());
			lastZoom = tileZoom;
			
			if(tileZoom > MIN_ACTIVE_LEVEL && isEnabled()) {
				
				// The current bounding-box:
				FloatBoundingBox bbox = glState.getBoundingBox().getMinimalNonRotated();
				
				bbox.scale(QUERY_BOX_SCALE);
				
				bbox = AlgoUtil.glToGeo(bbox);
				
				// Prevent that POIs pop-up at every minimal change of state:
				if(loadedArea == null || !loadedArea.contains(bbox) || tileZoom != lastZoom) {
					
					
					// Total reload of POIs will be done:
					clearRequired = true;
					
					// Cancel all currently active requests:
					loader.requestWorkersStop();
					loader.clearQueryQueue();
					
					// Iterate all major-categories:
					for(POICategory category : categoriesByName.values()) {
						// Data must only be loaded if the category is active:
						if(category.isActivated()) {
							/*
							 * Now iterate all sub-categories of the current category.
							 * Condition-sets will be merged.
							 */
							for(SubCategory subcategory : category.getSubcategories()) {
								if(subcategory.isActivated()) {
									// Create a query for this sub-category:
									POIQuery query = new POIQuery(bbox, subcategory.getConditions());
									query.setCategoryName(category.getName() + "_" + subcategory.getName());
									loader.request(query); // Enqueue
								}
							}
						}
					}
					
					loadedArea = new FloatBoundingBox(bbox);
				}
			} else {
				clearAll();
				loadedArea = null;
			}
		}
	}
	
	public Collection<POICategory> getCategories() {
		if(categoriesByName.isEmpty()) {
			loadFromXML();
		}
		
		return categoriesByName.values();
	}
	
	// POIEditor specific
	public void setCategories(Collection<POICategory> categories) {
		for(POICategory category : categories) {
			categoriesByName.put(category.getName(), category);
		}
	}

	/**
	 * Interrupts the loader. Note that termination of the loader-thread and its workers
	 * may delay if a request is currently performed.
	 */
	@Override
	public void requestLoaderStop() {
		if(loader != null) { // If loader exists:
			loader.interrupt(); // set the interrupt-flag to request termination.
		}
	}

	/**
	 * Returns the sub-category from <code>categoriesByName</code> with an identifier <code>id</code>.
	 * @param id The identifier to check for.
	 * @return The sub-category or <code>null</code> if nothing was found.
	 */
	private SubCategory getSubCategoryByIdentifier(String id) {
		for(POICategory category : categoriesByName.values()) {
			String categoryName = category.getName();
			
			// Iterate all sub-categories of the current category:
			for(SubCategory subCategory : category.getSubcategories()) {
				// The identifier is composed by the categories name, an '_' and the sub-categories name:
				String subCategoryId = categoryName + "_" + subCategory.getName();
				
				// Return the current sub-category if identifiers match:
				if(subCategoryId.equals(id)) {
					return subCategory;
				}
			}
		}
		return null;
	}
	
	@Override
	public void onResourcesAvailable(Collection<PointOfInterest> resources) {
		
		// Only notify if the manager is enabled:
		if(isEnabled() && !resources.isEmpty()) {
			if(clearRequired) {
				clearAll();
				clearRequired = false;
			}
			
			notifyAll(resources);
		}
	}
	
	/**
	 * Sets a sub-category managed to a certain state of activation.
	 * @param subCategory The sub-category to set.
	 * @param active Whether the sub-category should be set activated or not.
	 * @return <code>true</code> if the entry was found and set appropriate or <code>false</code>
	 * if the sub-category is not managed.
	 */
	public boolean setSubCategoryActive(SubCategory subCategory, boolean active) {
		
		boolean found = false;
		boolean enabledExisting = false;
		
		// Iterate all categories:
		for(POICategory category : categoriesByName.values()) {
			
			
			// Iterate the current categories sub-categories:
			Collection<SubCategory> subCategories = category.getSubcategories();
			for(SubCategory sub : subCategories) {
				// If category found:
				if(sub.equals(subCategory)) {
					// Adopt the wished value:
					sub.setActivated(active);
					
					// An update of the manager is required. Reset information about things loaded:
					found = true;
					loadedArea = null;
					GLState state = (GLState) StateManager.getInstance().getState(StateType.GLState);
					onUpdate(state);
				}
				
				enabledExisting |= sub.isActivated();
			}
		}
		
		if(!enabledExisting) {
			clearAll();
		}
		
		// Nothing found, return false as required:
		return found;
	}
}
