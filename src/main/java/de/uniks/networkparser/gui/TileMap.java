package de.uniks.networkparser.gui;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.MapEntityStack;
import de.uniks.networkparser.Pos;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.XMLTokener;

public class TileMap implements SendableEntityCreatorTag {
	public static final String ENCODING = "encoding";
	public static final String TAG = "map";
	public static final String VERSION = "version";
	public static final String ORIENTATION = "orientation";
	public static final String RENDERORDER = "renderorder";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String TILEWIDTH = "tilewidth";
	public static final String TILEHEIGHT = "tileheight";

	public static final String TILESET_TILE = "tileset";
	public static final String TILESET_LAYER = "layer";

	public static final String TILESET_OBJECTGROUP = "objectgroup";

	public String version;
	public String orientation;
	public String renderorder;
	public int width;
	public int height;
	public int tilewidth;
	public int tileheight;

	private SimpleList<TileObject> images = new SimpleList<TileObject>();

	public SimpleKeyValueList<String, SimpleList<TileObject>> objects = new SimpleKeyValueList<String, SimpleList<TileObject>>();

	public int[] background;
	public SimpleList<String> backgroundNames = new SimpleList<String>();

	private String path = "";

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TileMap();
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String[] getProperties() {
		return new String[] { VERSION, ORIENTATION, RENDERORDER, WIDTH, HEIGHT, TILEWIDTH, TILEHEIGHT, TILESET_TILE,
				TILESET_LAYER, TILESET_OBJECTGROUP };
	}

	/**
	 * Return the Position of the Background Sprite
	 * 
	 * @param ebene         Ebene of background
	 * @param backgroundPos Background Positoin 0..n
	 * @return Position of Background Pos
	 */
	public Pos getSpriteBackgroundPos(int ebene, int backgroundPos) {
		int spritePos = 0;
		if (background != null) {
			spritePos = background[backgroundPos];
		}
		return this.getSpritePos(ebene, spritePos);
	}

	/**
	 * Return the Position of Sprite
	 * 
	 * @param ebene Ebene of background
	 * @param pos   The Position 00..n
	 * @return The Position
	 */
	public Pos getSpritePos(int ebene, int pos) {
		Pos result = new Pos();
		TileObject tileObject = this.images.get(ebene);
		int columns = 1;
		if (this.tileheight > 0) {
			columns = tileObject.width / this.tileheight;
		}
		result.y = pos / columns;
		result.x = pos - (result.y * columns) - 1;
		return result;
	}

	public int getBackground(int sprite) {
		if (sprite < 0 || background == null || sprite >= background.length) {
			return 0;
		}
		return background[sprite];
	}

	/**
	 * Return the Position of a Sprite
	 * 
	 * @param sprite the SpriteNumber
	 * @return the Position of Sprite
	 */
	public Pos getPos(int sprite) {
		Pos pos = new Pos();
		if (width > 0) {
			pos.y = sprite / this.width;
		}
		pos.x = sprite - (pos.y * this.width);
		return pos;
	}

	public int length() {
		if (background == null) {
			return 0;
		}
		return background.length;
	}

	public SimpleList<TileObject> getByName(String element) {
		return this.objects.get(element);
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (entity instanceof TileMap == false) {
			return null;
		}
		TileMap map = (TileMap) entity;
		if (VERSION.equalsIgnoreCase(attribute)) {
			return map.version;
		}
		if (ORIENTATION.equalsIgnoreCase(attribute)) {
			return map.orientation;
		}
		if (RENDERORDER.equalsIgnoreCase(attribute)) {
			return map.renderorder;
		}
		if (WIDTH.equalsIgnoreCase(attribute)) {
			return map.width;
		}
		if (HEIGHT.equalsIgnoreCase(attribute)) {
			return map.height;
		}
		if (TILEWIDTH.equalsIgnoreCase(attribute)) {
			return map.tilewidth;
		}
		if (TILEHEIGHT.equalsIgnoreCase(attribute)) {
			return map.tileheight;
		}
		if (TileObject.PROPERTY_SOURCE.equalsIgnoreCase(attribute)) {
			if (map.images.size() > 0) {
				return map.images.first().name;
			}
			return null;
		}
		if (TileObject.PROPERTY_WIDTH.equalsIgnoreCase(attribute)) {
			if (map.images.size() > 0) {
				return map.images.first().width;
			}
			return null;
		}
		if (TileObject.PROPERTY_HEIGHT.equalsIgnoreCase(attribute)) {
			if (map.images.size() > 0) {
				return map.images.first().height;
			}
			return null;
		}
		if (TILESET_TILE.equalsIgnoreCase(attribute)) {
			if (map.images.size() < 1) {
				return null;
			}
			XMLEntity tileset = XMLEntity.TAG("tileset");
			for (TileObject image : map.images) {
				tileset.add("firstgid", image.gid);
				if (image.name != "") {
					tileset.add("name", image.name);
				}
				tileset.add("tilewidth", map.tilewidth);
				tileset.add("tileheight", map.tileheight);
				tileset.add("tilecount", image.count);
				tileset.add("columns", image.width / map.tileheight);
				XMLEntity imageXML = tileset.createChild("image");
				imageXML.add("source", image.source);
				imageXML.add("width", image.width);
				imageXML.add("height", image.height);
			}
			return tileset;
		}
		if (TILESET_LAYER.equalsIgnoreCase(attribute)) {
			if (map.background == null) {
				return null;
			}
			XMLEntity layer = XMLEntity.TAG("layer");
			layer.withKeyValue("name", "Background");
			layer.withKeyValue("width", map.width);
			layer.withKeyValue("height", map.height);
			XMLEntity data = layer.createChild("data");
			data.withKeyValue(ENCODING, "csv");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < map.background.length; i++) {
				sb.append(map.background[i]);
				if (i < map.background.length - 1) {
					sb.append(",");
				}
				if (i % 10 == 9) {
					sb.append("\r\n");
				}
			}
			data.withValue(sb.toString());
			return layer;
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (entity instanceof TileMap == false) {
			return false;
		}
		TileMap map = (TileMap) entity;
		if (VERSION.equalsIgnoreCase(attribute)) {
			map.version = "" + value;
			return true;
		}
		if (ORIENTATION.equalsIgnoreCase(attribute)) {
			map.orientation = "" + value;
			return true;
		}
		if (RENDERORDER.equalsIgnoreCase(attribute)) {
			map.renderorder = "" + value;
			return true;
		}
		if (WIDTH.equalsIgnoreCase(attribute)) {
			map.width = Integer.valueOf("" + value);
			return true;
		}
		if (HEIGHT.equalsIgnoreCase(attribute)) {
			map.height = Integer.valueOf("" + value);
			return true;
		}
		if (TILEWIDTH.equalsIgnoreCase(attribute)) {
			map.tilewidth = Integer.valueOf("" + value);
			return true;
		}
		if (TILEHEIGHT.equalsIgnoreCase(attribute)) {
			map.tileheight = Integer.valueOf("" + value);
			return true;
		}
		if (TILESET_TILE.equalsIgnoreCase(attribute)) {
			/* Complex Child Layer */
			XMLEntity tileSet = (XMLEntity) value;
			if (tileSet.sizeChildren() == 1) {
				XMLEntity imageXML = (XMLEntity) tileSet.getChild(0);
				TileObject image = new TileObject();
				image.gid = tileSet.getInt("firstgid");
				image.count = tileSet.getInt("tilecount");
				image.width = imageXML.getInt("width");
				image.height = imageXML.getInt("height");
				image.source = imageXML.getString("source");
				image.name = imageXML.getString("name");
				map.images.add(image);
			}
			return true;
		}
		if (TILESET_LAYER.equalsIgnoreCase(attribute)) {
			/* Complex Child Layer */
			XMLEntity layer = (XMLEntity) value;
			if (layer.sizeChildren() == 1) {
				XMLEntity data = (XMLEntity) layer.getChild(0);
				if ("csv".equals(data.get(ENCODING))) {
					String text = data.getValue();
					int i = 0;
					int start = 0;
					int z = 0;
					map.background = new int[map.width * map.height];
					while (i < text.length()) {
						if (text.charAt(i) != ',') {
							i++;
							continue;
						}
						String number = text.substring(start, i);
						map.background[z++] = Integer.valueOf(number.trim());
						i++;
						start = i;
					}
					String number = text.substring(start, i);
					map.background[z] = Integer.valueOf(number.trim());
					return true;
				}
			}
		}
		if (TILESET_OBJECTGROUP.equalsIgnoreCase(attribute)) {
			XMLEntity objectGroup = (XMLEntity) value;
			String tag = objectGroup.getString("name");
			if (tag == null || tag.length() < 1) {
				tag = "element";
			}
			SimpleList<TileObject> objects = this.objects.get(tag);
			if (objects == null) {
				objects = new SimpleList<TileObject>();
				this.objects.put(tag, objects);
			}
			for (int i = 0; i < objectGroup.size(); i++) {
				BaseItem item = objectGroup.getChild(i);
				objects.add(TileObject.create((Entity) item));
			}
			return true;
		}
		return false;
	}

	public static TileMap create(String value) {
		TileMap entity = new TileMap();
		String tag = entity.getTag();
		MapEntity map = new MapEntity(null);
		map.withStack(new MapEntityStack().withStack(tag, entity, entity));
		XMLTokener tokener = new XMLTokener();
		CharacterBuffer buffer = new CharacterBuffer().with(value);
		tokener.skipHeader(buffer);
		buffer.skipTo(' ', false);

		tokener.parse(tokener, buffer, map);
		return entity;
	}

	public String getSource() {
		if (images.size() > 0) {
			return images.first().name;
		}
		return null;
	}

	public TileMap withPath(String value) {
		this.path = value;
		return this;
	}

	public String getPath() {
		return path;
	}
}
