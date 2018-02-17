package de.uniks.networkparser.gui;

import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.Pos;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.MapEntityStack;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.XMLTokener;

public class TileMap implements SendableEntityCreatorTag {
	public static final String ENCODING="encoding";
	public static final String TAG="map";
	public static final String VERSION="version";
	public static final String ORIENTATION="orientation";
	public static final String RENDERORDER="renderorder";
	public static final String WIDTH="width";
	public static final String HEIGHT="height";
	public static final String TILEWIDTH="tilewidth";
	public static final String TILEHEIGHT="tileheight";
	
	public static final String TILESET_FIRSTGID=".tileset.firstgid";
	public static final String TILESET_TILEWIDTH =".tileset."+TILEWIDTH;
	public static final String TILESET_TILEHEIGHT =".tileset."+TILEHEIGHT;
	public static final String TILESET_COUNT =".tileset.tilecount";
	public static final String TILESET_COLUMNS =".tileset.columns";
	public static final String TILESET_SOURCE=".tileset.image.source";
	public static final String TILESET_WIDTH=".tileset.image.width";
	public static final String TILESET_HEIGHT=".tileset.image.height";
	public static final String TILESET_LAYER="layer";
	public static final String TILESET_OBJECTGROUP="objectgroup";
	
	public String version;
	public String orientation;
	public String renderorder;
	public int width;
	public int height;
	public int tilewidth;
	public int tileheight;
	public SimpleKeyValueList<String, SimpleList<TileObject>> objects=new SimpleKeyValueList<String, SimpleList<TileObject>>();
	
	public int tileFirstGrid = 1;
	public int count;
	public int columns;
	public String source;
	public int imagewidth;
	public int imageheight;
	public int[] background;
	
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
		return new String[]{
				VERSION, ORIENTATION, RENDERORDER, WIDTH, HEIGHT, TILEWIDTH, TILEHEIGHT,
				TILESET_FIRSTGID, TILESET_TILEWIDTH, TILESET_TILEHEIGHT, TILESET_COUNT, TILESET_COLUMNS, TILESET_SOURCE, TILESET_WIDTH, TILESET_HEIGHT,
				TILESET_LAYER,
				TILESET_OBJECTGROUP
				};
	}
	
	/**
	 * Return the Position of the Background Sprite
	 * @param backgroundPos Background Positoin 0..n
	 * @return Position of Background Pos
	 */
	public Pos getSpriteBackgroundPos(int backgroundPos) {
		int spritePos = 0;
		if(background != null) {
			spritePos = background[backgroundPos];
		}
		return this.getSpritePos(spritePos);
	}
	
	public int getBackground(int sprite) {
		if(sprite<0 || sprite>=background.length) {
			return 0;
		}
		return background[sprite];
	}
	
	/** Return the Position of Sprite
	 * @param pos The Position 00..n
	 * @return The Position
	 */
	public Pos getSpritePos(int pos) {
		Pos result = new Pos();
		result.y = pos/columns;
		result.x = pos-(result.y*columns) - 1;
		return result;
	}
	
	/**
	 * Return the Position of a Sprite
	 * @param sprite the SpriteNumber
	 * @return the Position of Sprite
	 */
	public Pos getPos(int sprite) {
		Pos pos = new Pos();
		pos.y = sprite/this.width;
		pos.x = sprite-(pos.y*this.width);
		return pos;
	}
	
	public int length() {
		if(background == null) {
			return 0;
		}
		return background.length;
	}
	
	public SimpleList<TileObject> getByName(String element) {
		return this.objects.get(element);
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(entity instanceof TileMap == false) {
			return null;
		}
		TileMap map = (TileMap) entity;
		if(VERSION.equalsIgnoreCase(attribute)) {
			return map.version;
		}
		if(ORIENTATION.equalsIgnoreCase(attribute)) {
			return map.orientation;
		}
		if(RENDERORDER.equalsIgnoreCase(attribute)) {
			return map.renderorder;
		}
		if(WIDTH.equalsIgnoreCase(attribute)) {
			return map.width;
		}
		if(HEIGHT.equalsIgnoreCase(attribute)) {
			return map.height;
		}
		if(TILEWIDTH.equalsIgnoreCase(attribute)) {
			return map.tilewidth;
		}
		if(TILEHEIGHT.equalsIgnoreCase(attribute)) {
			return map.tileheight;
		}
		if(TILESET_FIRSTGID.equalsIgnoreCase(attribute)) {
			return map.tileFirstGrid;
		}
		if(TILESET_TILEWIDTH.equalsIgnoreCase(attribute)) {
			return map.tilewidth;
		}
		if(TILESET_TILEHEIGHT.equalsIgnoreCase(attribute)) {
			return map.tileheight;
		}
		if(TILESET_COUNT.equalsIgnoreCase(attribute)) {
			return map.count;
		}
		if(TILESET_COLUMNS.equalsIgnoreCase(attribute)) {
			return map.columns;
		}
		if(TILESET_SOURCE.equalsIgnoreCase(attribute)) {
			return map.source;
		}
		if(TILESET_WIDTH.equalsIgnoreCase(attribute)) {
			return map.imagewidth;
		}
		if(TILESET_HEIGHT.equalsIgnoreCase(attribute)) {
			return map.imageheight;
		}
		if(TILESET_LAYER.equalsIgnoreCase(attribute)) {
			if(map.background == null) {
				return null;
			}
			XMLEntity layer=XMLEntity.TAG("layer");
			layer.withKeyValue("name", "Background");
			layer.withKeyValue("width", map.width);
			layer.withKeyValue("height", map.height);
			XMLEntity data=XMLEntity.TAG("data");
			data.withKeyValue(ENCODING, "csv");
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<map.background.length;i++) {
				sb.append(map.background[i]);
				if(i<map.background.length - 1) {
					sb.append(",");
				}
				if(i%10==9) {
					sb.append("\r\n");
				}
			}
			data.withValue(sb.toString());
			layer.with(data);
			return layer;
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(entity instanceof TileMap == false) {
			return false;
		}
		TileMap map = (TileMap) entity;
		if(VERSION.equalsIgnoreCase(attribute)) {
			map.version = ""+value;
			return true;
		}
		if(ORIENTATION.equalsIgnoreCase(attribute)) {
			map.orientation = ""+value;
			return true;
		}
		if(RENDERORDER.equalsIgnoreCase(attribute)) {
			map.renderorder = ""+value;
			return true;
		}
		if(WIDTH.equalsIgnoreCase(attribute)) {
			map.width = Integer.valueOf(""+value);
			return true;
		}
		if(HEIGHT.equalsIgnoreCase(attribute)) {
			map.height = Integer.valueOf(""+value);
			return true;
		}
		if(TILEWIDTH.equalsIgnoreCase(attribute)) {
			map.tilewidth = Integer.valueOf(""+value);
			return true;
		}
		if(TILEHEIGHT.equalsIgnoreCase(attribute)) {
			map.tileheight = Integer.valueOf(""+value);
			return true;
		}
		if(TILESET_FIRSTGID.equalsIgnoreCase(attribute)) {
			map.tileFirstGrid  = Integer.valueOf(""+value);
			return true;
		}
//		if(TILESET_TILEWIDTH.equalsIgnoreCase(attribute)) {
//		if(TILESET_TILEHEIGHT.equalsIgnoreCase(attribute)) {
		if(TILESET_COUNT.equalsIgnoreCase(attribute)) {
			map.count = Integer.valueOf(""+value);
			return true;
		}
		if(TILESET_COLUMNS.equalsIgnoreCase(attribute)) {
			map.columns = Integer.valueOf(""+value);
			return true;
		}
		if(TILESET_SOURCE.equalsIgnoreCase(attribute)) {
			map.source = ""+value;
			return true;
		}
		if(TILESET_WIDTH.equalsIgnoreCase(attribute)) {
			map.imagewidth = Integer.valueOf(""+value);
			return true;
		}
		if(TILESET_HEIGHT.equalsIgnoreCase(attribute)) {
			map.imageheight = Integer.valueOf(""+value);
			return true;
		}
		if(TILESET_LAYER.equalsIgnoreCase(attribute) ) {
			// Complex Child Layer
			XMLEntity layer = (XMLEntity) value;
			if(layer.sizeChildren()==1) {
				XMLEntity data = (XMLEntity) layer.getChild(0);
				if("csv".equals(data.get(ENCODING))) {
					String text = data.getValue();
					int i=0;
					int start=0;
					int z=0;
					map.background = new int[map.width*map.height];
					while(i<text.length() ) {
						if(text.charAt(i) != ',') {
							i++;
							continue;
						}
						String number = text.substring(start, i);
						map.background[z++] = Integer.valueOf(number.trim());
						i++;
						start=i;
					}
					String number = text.substring(start, i);
					map.background[z] = Integer.valueOf(number.trim());
					return true;
				}
			}
		}
		if(TILESET_OBJECTGROUP.equalsIgnoreCase(attribute) ) {
			XMLEntity objectGroup = (XMLEntity) value;
			String tag = objectGroup.getString("name");
			if(tag == null || tag.length()<1) {
				tag = "element";
			}
			SimpleList<TileObject> objects = this.objects.get(tag);
			if(objects == null) {
				objects = new SimpleList<TileObject>();
				this.objects.put(tag, objects);
			}
			for(int i=0;i<objectGroup.size();i++) {
				BaseItem item = objectGroup.getChild(i);
				objects.add(TileObject.create((Entity)item));
			}
			return true;
		}
		return false;
	}

	public static TileMap create(String value) {
		TileMap entity = new TileMap();
		String tag = entity.getTag();
//		MapEntity map = new MapEntity(new SimpleGrammar(), tag, entity, entity);
		MapEntity map = new MapEntity(null);
		map.withStack(new MapEntityStack().withStack(tag, entity, entity));
		XMLTokener tokener = new XMLTokener();
		tokener.withBuffer(value);
		tokener.skipHeader();
		tokener.skipTo(' ', false);
		tokener.parse(tokener, map);
		return entity;
	}
	
	@Override
	public String toString() {
		
		return super.toString();
	}
}
