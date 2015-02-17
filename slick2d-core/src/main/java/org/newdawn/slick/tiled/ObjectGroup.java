package org.newdawn.slick.tiled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A group of objects on the map (objects layer)
 * 
 * @author liamzebedee
 */
public class ObjectGroup {
	/** The index of this group */
	public int index;
	/** The name of this group - read from the XML */
	public String name;
	/** The Objects of this group */
	public ArrayList<GroupObject> objects;
	/** The width of this layer */
	public int width;
	/** The height of this layer */
	public int height;
	/** The mapping between object names and offsets */
	private HashMap<String, Integer> nameToObjectMap = new HashMap<String, Integer>();
	/** the properties of this group */
	public Properties props;
	/** The TiledMap of which this ObjectGroup belongs to */
	TiledMap map;
	/** The opacity of this layer (range 0 to 1) */
	public float opacity = 1;
	/** The visibility of this layer */
	public boolean visible = true;
	/** The color of this layer. NOTE: Slick does not render objects on default */
	public Color color = new Color(Color.white);

	/**
	 * Create a new group based on the XML definition
	 * 
	 * @author kulpae
	 * @author liamzebedee
	 * @param element
	 *            The XML element describing the layer
	 * @param map
	 *            The map to which the ObjectGroup belongs
	 * @throws SlickException
	 *             Indicates a failure to parse the XML group
	 */
	public ObjectGroup(Element element, TiledMap map) throws SlickException {
		this.map = map;
		TiledMapPlus tmap = null;
		if (map instanceof TiledMapPlus) {
			tmap = (TiledMapPlus) map;
		}
		name = element.getAttribute("name");
		String widthS = element.getAttribute("width");
		if (widthS != null && widthS.length()!=0) {
			width = Integer.parseInt(widthS);
		}
		String heightS = element.getAttribute("height");
		if (heightS != null && heightS.length()!=0) {
			height = Integer.parseInt(heightS);
		}
		if (width==0||height==0)
			Log.warn("ObjectGroup "+name+" has zero size (width or height equal to 0)");

		objects = new ArrayList<GroupObject>();
		String opacityS = element.getAttribute("opacity");
		if (opacityS!=null && opacityS.length()!=0) {
			opacity = Float.parseFloat(opacityS);
		}
		if ("0".equals(element.getAttribute("visible"))) {
			visible = false;
		}
		//will default to Color.white if attribute is not found / can't be parsed
		String colorS = element.getAttribute("color");
		if (colorS != null && colorS.length()!=0) {
			try {
				color = Color.decode(colorS);
			} catch (NumberFormatException e) {
				Log.warn("color attribute in element "+name+" could not be parsed; reverting to white");
			}
		}

		// now read the layer properties
		Element propsElement = (Element) element.getElementsByTagName(
				"properties").item(0);
		if (propsElement != null) {
			NodeList properties = propsElement.getElementsByTagName("property");
			if (properties != null) {
				props = new Properties();
				for (int p = 0; p < properties.getLength(); p++) {
					Element propElement = (Element) properties.item(p);
					String name = propElement.getAttribute("name");
					String value = propElement.getAttribute("value");
					props.setProperty(name, value);
				}
			}
		}

		NodeList objectNodes = element.getElementsByTagName("object");
		for (int i = 0; i < objectNodes.getLength(); i++) {
			Element objElement = (Element) objectNodes.item(i);
			GroupObject object = null;
			if (tmap != null) {
				object = new GroupObject(objElement, tmap);
			} else {
				object = new GroupObject(objElement);
			}
			object.index = i;
			objects.add(object);
		}
	}

	/**
	 * Gets an object by its name
	 * 
	 * @author liamzebedee
	 * @param objectName
	 *            The name of the object
	 */
	public GroupObject getObject(String objectName) {
		GroupObject g = this.objects.get(this.nameToObjectMap.get(objectName));
		return g;
	}

	/**
	 * Gets all objects of a specific type on a layer
	 * 
	 * @author liamzebedee
	 * @param type
	 *            The name of the type
	 */
	public ArrayList<GroupObject> getObjectsOfType(String type) {
		ArrayList<GroupObject> foundObjects = new ArrayList<GroupObject>();
		for (GroupObject object : this.objects) {
			if (object.type.equals(type)) {
				foundObjects.add(object);
			}
		}
		return foundObjects;
	}

	/**
	 * Removes an object
	 * 
	 * @author liamzebedee
	 * @param objectName
	 *            The name of the object
	 */
	public void removeObject(String objectName) {
		int objectOffset = this.nameToObjectMap.get(objectName);
		GroupObject object = this.objects.remove(objectOffset);
	}

	/**
	 * Sets the mapping from object names to their offsets
	 * 
	 * @author liamzebedee
	 * @param map
	 *            The name of the map
	 */
	public void setObjectNameMapping(HashMap<String, Integer> map) {
		this.nameToObjectMap = map;
	}

	/**
	 * Adds an object to the object group
	 * 
	 * @author liamzebedee
	 * @param object
	 *            The object to be added
	 */
	public void addObject(GroupObject object) {
		this.objects.add(object);
		this.nameToObjectMap.put(object.name, this.objects.size());
	}

	/**
	 * Gets all the objects from this group
	 * 
	 * @author liamzebedee
	 */
	public ArrayList<GroupObject> getObjects() {
		return this.objects;
	}
}
