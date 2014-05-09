package org.newdawn.slick.tiled;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A layer of tiles on the map
 * 
 * @author kevin
 * @author liamzebedee
 */
public class Layer {
	/** The code used to decode Base64 encoding */
	private static byte[] baseCodes = new byte[256];

	/**
	 * Static initialiser for the codes created against Base64
	 */
	static {
		for (int i = 0; i < 256; i++)
			baseCodes[i] = -1;
		for (int i = 'A'; i <= 'Z'; i++)
			baseCodes[i] = (byte) (i - 'A');
		for (int i = 'a'; i <= 'z'; i++)
			baseCodes[i] = (byte) (26 + i - 'a');
		for (int i = '0'; i <= '9'; i++)
			baseCodes[i] = (byte) (52 + i - '0');
		baseCodes['+'] = 62;
		baseCodes['/'] = 63;
	}

	/** The map this layer belongs to */
	private final TiledMap map;
	/** The index of this layer */
	public int index;
	/** The name of this layer - read from the XML */
	public String name;
	/**
	 * The tile data representing this data, index 0 = tileset, index 1 = tile
	 * id
	 */
	public int[][][] data;
	/** The width of this layer */
	public int width;
	/** The height of this layer */
	public int height;
	/** The opacity of this layer (range 0 to 1) */
	public float opacity = 1;
	/** The visibility of this layer */
	public boolean visible = true;

	/** the properties of this layer */
	public Properties props;

	/** The TiledMapPlus of this layer */
	private TiledMapPlus tmap;

	/**
	 * Create a new layer based on the XML definition
	 * 
	 * @param element
	 *            The XML element describing the layer
	 * @param map
	 *            The map this layer is part of
	 * @throws SlickException
	 *             Indicates a failure to parse the XML layer
	 */
	public Layer(TiledMap map, Element element) throws SlickException {
		this.map = map;
		if (map instanceof TiledMapPlus) {
			tmap = (TiledMapPlus) map;
		}
		name = element.getAttribute("name");
		width = Integer.parseInt(element.getAttribute("width"));
		height = Integer.parseInt(element.getAttribute("height"));
		data = new int[width][height][3];
		String opacityS = element.getAttribute("opacity");
		if (!opacityS.equals("")) {
			opacity = Float.parseFloat(opacityS);
		}
		if (element.getAttribute("visible").equals("0")) {
			visible = false;
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

		Element dataNode = (Element) element.getElementsByTagName("data").item(
				0);
		String encoding = dataNode.getAttribute("encoding");
		String compression = dataNode.getAttribute("compression");

		if (encoding.equals("base64") && compression.equals("gzip")) {
			try {
				Node cdata = dataNode.getFirstChild();
				char[] enc = cdata.getNodeValue().trim().toCharArray();
				byte[] dec = decodeBase64(enc);
				GZIPInputStream is = new GZIPInputStream(
						new ByteArrayInputStream(dec));
				readData(is);
			} catch (IOException e) {
				Log.error(e);
				throw new SlickException("Unable to decode base 64 block");
			}
		} else if (encoding.equals("base64") && compression.equals("zlib")) {
			Node cdata = dataNode.getFirstChild();
			char[] enc = cdata.getNodeValue().trim().toCharArray();
			byte[] dec = decodeBase64(enc);
			InflaterInputStream is = new InflaterInputStream(
					new ByteArrayInputStream(dec));
			readData(is);
		} else {
			throw new SlickException("Unsupport tiled map type: " + encoding
					+ "," + compression + " (only gzip/zlib base64 supported)");
		}
	}

	/**
	 * For reading decompressed, decoded Layer data into this layer
	 * 
	 * @param is
	 */
	protected void readData(InputStream is) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int tileId = 0;
				try {
					tileId |= is.read();
					tileId |= is.read() << 8;
					tileId |= is.read() << 16;
					tileId |= is.read() << 24;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (tileId == 0) {
					data[x][y][0] = -1;
					data[x][y][1] = 0;
					data[x][y][2] = 0;
				} else {
					int realTileId = tileId & 0x1FFFFFFF;
					TileSet set = map.findTileSet(realTileId);
					if (set != null) {
						data[x][y][0] = set.index;
						data[x][y][1] = realTileId - set.firstGID;
					}
					data[x][y][2] = tileId;
				}
			}
		}
	}

	/**
	 * Get the gloal ID of the tile at the specified location in this layer
	 * 
	 * @param x
	 *            The x coorindate of the tile
	 * @param y
	 *            The y coorindate of the tile
	 * @return The global ID of the tile
	 */
	public int getTileID(int x, int y) {
		return data[x][y][2];
	}

	/**
	 * Set the global tile ID at a specified location
	 * 
	 * @param x
	 *            The x location to set
	 * @param y
	 *            The y location to set
	 * @param tile
	 *            The tile value to set
	 */
	public void setTileID(int x, int y, int tile) {
		if (tile == 0) {
			data[x][y][0] = -1;
			data[x][y][1] = 0;
			data[x][y][2] = 0;
		} else {
			TileSet set = map.findTileSet(tile);

			data[x][y][0] = set.index; // tileSetIndex
			data[x][y][1] = tile - set.firstGID; // localID
			data[x][y][2] = tile; // globalID
		}
	}

	/**
	 * Render a section of this layer
	 * 
	 * @param x
	 *            The x location to render at
	 * @param y
	 *            The y location to render at
	 * @param sx
	 *            The x tile location to start rendering
	 * @param sy
	 *            The y tile location to start rendering
	 * @param width
	 *            The number of tiles across to render
	 * @param ty
	 *            The line of tiles to render
	 * @param lineByLine
	 *            True if we should render line by line, i.e. giving us a chance
	 *            to render something else between lines
	 * @param mapTileWidth
	 *            the tile width specified in the map file
	 * @param mapTileHeight
	 *            the tile height specified in the map file
	 */
	public void render(int x, int y, int sx, int sy, int width, int ty,
			boolean lineByLine, int mapTileWidth, int mapTileHeight) {
		for (int tileset = 0; tileset < map.getTileSetCount(); tileset++) {
			TileSet set = null;

			for (int tx = 0; tx < width; tx++) {
				if ((sx + tx < 0) || (sy + ty < 0)) {
					continue;
				}
				if ((sx + tx >= this.width) || (sy + ty >= this.height)) {
					continue;
				}

				if (data[sx + tx][sy + ty][0] == tileset) {
					if (set == null) {
						set = map.getTileSet(tileset);
						set.tiles.startUse();
					}

					int sheetX = set.getTileX(data[sx + tx][sy + ty][1]);
					int sheetY = set.getTileY(data[sx + tx][sy + ty][1]);

					int tileOffsetY = set.tileHeight - mapTileHeight;

					// TODO CHECK
					// LSB: Rotate
					// LSB+1: Flip Y
					// LSB+2: Flip X
					byte b = (byte) ((data[sx + tx][sy + ty][2] & 0xE0000000L) >> 29);
					set.tiles.setAlpha(this.opacity); // Sets opacity/alpha value
					set.tiles.renderInUse(x + (tx * mapTileWidth), y
							+ (ty * mapTileHeight) - tileOffsetY, sheetX,
							sheetY, b);
				}
			}

			if (lineByLine) {
				if (set != null) {
					set.tiles.endUse();
					set = null;
				}
				map.renderedLine(ty, ty + sy, index);
			}

			if (set != null) {
				set.tiles.endUse();
			}
		}
	}

	/**
	 * Decode a Base64 string as encoded by TilED
	 * 
	 * @param data
	 *            The string of character to decode
	 * @return The byte array represented by character encoding
	 */
	private byte[] decodeBase64(char[] data) {
		int temp = data.length;
		for (int ix = 0; ix < data.length; ix++) {
			if ((data[ix] > 255) || baseCodes[data[ix]] < 0) {
				--temp;
			}
		}

		int len = (temp / 4) * 3;
		if ((temp % 4) == 3)
			len += 2;
		if ((temp % 4) == 2)
			len += 1;

		byte[] out = new byte[len];

		int shift = 0;
		int accum = 0;
		int index = 0;

		for (int ix = 0; ix < data.length; ix++) {
			int value = (data[ix] > 255) ? -1 : baseCodes[data[ix]];

			if (value >= 0) {
				accum <<= 6;
				shift += 6;
				accum |= value;
				if (shift >= 8) {
					shift -= 8;
					out[index++] = (byte) ((accum >> shift) & 0xff);
				}
			}
		}

		if (index != out.length) {
			throw new RuntimeException(
					"Data length appears to be wrong (wrote " + index
							+ " should be " + out.length + ")");
		}

		return out;
	}

	/**
	 * Gets all Tiles from this layer, formatted into Tile objects Can only be
	 * used if the layer was loaded using TiledMapPlus
	 * 
	 * @author liamzebedee
	 * @throws SlickException
	 */
	public ArrayList<Tile> getTiles() throws SlickException {
		if (tmap == null) {
			throw new SlickException(
					"This method can only be used with Layers loaded using TiledMapPlus");
		}
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				String tilesetName = tmap.tileSets.get(this.data[x][y][0]).name;
				Tile t = new Tile(x, y, this.name, y, tilesetName);
				tiles.add(t);
			}
		}
		return tiles;
	}

	/**
	 * Get all tiles from this layer that are part of a tileset Can only be used
	 * if the layer was loaded using TiledMapPlus
	 * 
	 * @author liamzebedee
	 * @param tilesetName
	 *            The name of the tileset that the tiles are part of
	 * @throws SlickException
	 */
	public ArrayList<Tile> getTilesOfTileset(String tilesetName)
			throws SlickException {
		if (tmap == null) {
			throw new SlickException(
					"This method can only be used with Layers loaded using TiledMapPlus");
		}
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		int tilesetID = tmap.getTilesetID(tilesetName);
		for (int x = 0; x < tmap.getWidth(); x++) {
			for (int y = 0; y < tmap.getHeight(); y++) {
				if (this.data[x][y][0] == tilesetID) {
					Tile t = new Tile(x, y, this.name, this.data[x][y][1],
							tilesetName);
					tiles.add(t);
				}
			}
		}
		return tiles;
	}

	/**
	 * Removes a tile
	 * 
	 * @author liamzebedee
	 * @param x
	 *            Tile X
	 * @param y
	 *            Tile Y
	 */
	public void removeTile(int x, int y) {
		this.data[x][y][0] = -1;
	}

	/**
	 * Sets a tile's tileSet Can only be used if the layer was loaded using
	 * TiledMapPlus
	 * 
	 * @author liamzebedee
	 * @param x
	 *            Tile X
	 * @param y
	 *            Tile Y
	 * @param tileOffset
	 *            The offset of the tile, within the tileSet to set this tile
	 *            to, ordered in rows
	 * @param tilesetName
	 *            The name of the tileset to set the tile to
	 * @throws SlickException
	 */
	public void setTile(int x, int y, int tileOffset, String tilesetName)
			throws SlickException {
		if (tmap == null) {
			throw new SlickException(
					"This method can only be used with Layers loaded using TiledMapPlus");
		}
		int tilesetID = tmap.getTilesetID(tilesetName);
		TileSet tileset = tmap.getTileSet(tilesetID);
		this.data[x][y][0] = tileset.index; // tileSetIndex
		this.data[x][y][1] = tileOffset; // localID
		this.data[x][y][2] = tileset.firstGID + tileOffset; // globalID
	}

	/**
	 * Returns true if this tile is part of that tileset Can only be used if the
	 * layer was loaded using TiledMapPlus
	 * 
	 * @author liamzebedee
	 * @param x
	 *            The x co-ordinate of the tile
	 * @param y
	 *            The y co-ordinate of the tile
	 * @param tilesetName
	 *            The name of the tileset, to check if the tile is part of
	 * @throws SlickException
	 */
	public boolean isTileOfTileset(int x, int y, String tilesetName)
			throws SlickException {
		if (tmap == null) {
			throw new SlickException(
					"This method can only be used with Layers loaded using TiledMapPlus");
		}
		int tilesetID = tmap.getTilesetID(tilesetName);
		if (this.data[x][y][0] == tilesetID) {
			return true;
		}
		return false;
	}

}