package org.newdawn.slick.tiled;

import java.util.Properties;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * An object from a object-group on the map
 * 
 * @author kulpae
 * @author liamzebedee
 */
public class GroupObject {
	/** The index of this object */
	public int index;
	/** The name of this object - read from the XML */
	public String name = "";
	/** The type of this object - read from the XML */
	public String type = "";
	/** The x-coordinate of this object */
	public int x = 0;
	/** The y-coordinate of this object */
	public int y = 0;
	/** The width of this object */
	public int width = 0;
	/** The height of this object */
	public int height = 0;
	/** The image source */
	String image;
	/** the properties of this group */
	public Properties props;
	/** The gid reference to the image */
	public int gid = -1;
	/** The map this object belongs to */
	TiledMapPlus map;

	/** Types of objects */
	public enum ObjectType {
		IMAGE, RECTANGLE, POLYGON, POLYLINE
	}

	/** Object Type */
	ObjectType objectType;
	/** Points of a polygon object */
	Polygon points;

	/**
	 * Create a new group based on the XML definition
	 * 
	 * @author kulpae
	 * @author liamzebedee
	 * @param element
	 *            The XML element describing the layer
	 * @throws SlickException
	 *             Indicates a failure to parse the XML group
	 */
	public GroupObject(Element element) throws SlickException {
		if (element.getAttribute("gid") != "") {
			gid = Integer.parseInt(element.getAttribute("gid"));
			this.objectType = ObjectType.IMAGE;
		}
		if (element.getElementsByTagName("polyline").item(0) != null)
			this.objectType = ObjectType.POLYLINE;
		else if (element.getElementsByTagName("polygon").item(0) != null)
			this.objectType = ObjectType.POLYGON;
		else
			this.objectType = ObjectType.RECTANGLE;

		if (objectType == ObjectType.IMAGE) {
			if (element.getAttribute("width") != "") {
				width = Integer.parseInt(element.getAttribute("width"));
			}
			if (element.getAttribute("height") != "") {
				height = Integer.parseInt(element.getAttribute("height"));
			}
			if (element.getAttribute("name") != "") {
				name = element.getAttribute("name");
			}
			if (element.getAttribute("type") != "") {
				type = element.getAttribute("type");
			}
		}

		else if ((objectType == ObjectType.POLYGON)
				|| (objectType == ObjectType.POLYLINE)) {
			name = element.getAttribute("name");
			Element polyLine;
			if (objectType == ObjectType.POLYGON)
				polyLine = (Element) element.getElementsByTagName("polygon")
						.item(0);
			else
				polyLine = (Element) element.getElementsByTagName("polyline")
						.item(0);

			String pointsUnformatted = polyLine.getAttribute("points");
			String[] pointsFormatted = pointsUnformatted.split(" ");
			for (String pointS : pointsFormatted) {
				String[] pointArray = pointS.split(",");
				float pointX = Float.parseFloat(pointArray[0]);
				float pointY = Float.parseFloat(pointArray[1]);
				points.addPoint(pointX, pointY);
			}
		}

		else if (objectType == ObjectType.RECTANGLE) {
			objectType = ObjectType.RECTANGLE;
			width = Integer.parseInt(element.getAttribute("width"));
			height = Integer.parseInt(element.getAttribute("height"));
			name = element.getAttribute("name");
			type = element.getAttribute("type");
		}
		x = Integer.parseInt(element.getAttribute("x"));
		y = Integer.parseInt(element.getAttribute("y"));

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
	}

	/**
	 * Create a new group based on the XML definition
	 * 
	 * @author kulpae
	 * @author liamzebedee
	 * @param element
	 *            The XML element describing the layer
	 * @param map
	 *            The map this object belongs to
	 * @throws SlickException
	 *             Indicates a failure to parse the XML group
	 */
	public GroupObject(Element element, TiledMapPlus map) throws SlickException {
		this.map = map;
		if (element.getAttribute("gid") != "") {
			gid = Integer.parseInt(element.getAttribute("gid"));
			this.objectType = ObjectType.IMAGE;
		}
		if (objectType == ObjectType.IMAGE) {
			if (element.getAttribute("width") != "") {
				width = Integer.parseInt(element.getAttribute("width"));
			}
			if (element.getAttribute("height") != "") {
				height = Integer.parseInt(element.getAttribute("height"));
			}
			if (element.getAttribute("name") != "") {
				name = element.getAttribute("name");
			}
			if (element.getAttribute("type") != "") {
				type = element.getAttribute("type");
			}
		} else if (objectType == ObjectType.RECTANGLE) {
			width = Integer.parseInt(element.getAttribute("width"));
			height = Integer.parseInt(element.getAttribute("height"));
			name = element.getAttribute("name");
			type = element.getAttribute("type");
		} else if (objectType == ObjectType.POLYGON) {
			name = element.getAttribute("name");
			Element polyLine = (Element) element.getElementsByTagName(
					"polyline").item(0);
			String pointsUnformatted = polyLine.getAttribute("points");
			String[] pointsFormatted = pointsUnformatted.split(" ");
			for (String pointS : pointsFormatted) {
				String[] pointArray = pointS.split(",");
				float pointX = Float.parseFloat(pointArray[0]);
				float pointY = Float.parseFloat(pointArray[1]);
				points.addPoint(pointX, pointY);
			}
		}
		x = Integer.parseInt(element.getAttribute("x"));
		y = Integer.parseInt(element.getAttribute("y"));

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
	}

	/**
	 * Puts a property to an object
	 * 
	 * @author liamzebedee
	 * @param propertyKey
	 *            The key of the property to be put to the object
	 * @param propertyValue
	 *            The value mappped to the key of the property to be put to the
	 *            object
	 */
	public void putProperty(String propertyKey, String propertyValue) {
		this.props.put(propertyKey, propertyValue);
	}

	/**
	 * Puts a property to an object
	 * 
	 * @author liamzebedee
	 * @param propertyKey
	 *            The key of the property to be put to the object
	 * @param propertyValue
	 *            The value mappped to the key of the property to be put to the
	 *            object
	 */
	public void removeProperty(String propertyKey) {
		this.props.remove(propertyKey);
	}

	/**
	 * Gets the image of this object, if it is an image object
	 * 
	 * @author liamzebedee
	 * @throws SlickException
	 */
	public Image getImage() throws SlickException {
		if (!(objectType == ObjectType.IMAGE)) {
			throw new SlickException("Object isn't an image object");
		}
		if (map == null) {
			throw new SlickException(
					"Object doesn't belong to a map of type TiledMapPlus");
		}
		TileSet tileset = this.map.getTileSetByGID(gid);
		int tilesetTileID = (this.gid - tileset.firstGID);
		return tileset.tiles.getSubImage(tileset.getTileX(tilesetTileID),
				tileset.getTileY(tilesetTileID), tileset.tileWidth,
				tileset.tileHeight);
	}
}
