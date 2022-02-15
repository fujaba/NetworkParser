package de.uniks.networkparser.graph;

import java.util.ArrayList;

import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;

/**
 * The Class GraphOptions.
 *
 * @author Stefan
 */
public class GraphOptions {
	
	/**
	 * The Enum TYP.
	 *
	 * @author Stefan
	 */
	/* Options */
	public enum TYP {
		HTML, CANVAS, SVG, PDF
	};

	/**
	 * The Enum RANK.
	 *
	 * @author Stefan
	 */
	public enum RANK {
		LR, TB
	};

	/**
	 * The Enum LINETYP.
	 *
	 * @author Stefan
	 */
	public enum LINETYP {
		CENTER, SQUARE
	};

	private TYP display;
	private LINETYP lineTyp;
	private Boolean raster;
	private String canvasid;
	private Boolean clearCanvas;
	private Integer fontSize;
	private String fontFamily;
	private String rank;
	private Integer nodeSep;
	private Boolean infobox;
	private Boolean cardinalityInfo;
	private Boolean propertyInfo;
	private Boolean rotateText;
	private ArrayList<TYP> buttons;

	/**
	 * Gets the json.
	 *
	 * @return the json
	 */
	public JsonObject getJson() {
		JsonObject result = new JsonObject();

		result.withKeyValue("display", display);
		result.withKeyValue("raster", raster);
		result.withKeyValue("canvasid", canvasid);
		result.withKeyValue("fontSize", fontSize);
		result.withKeyValue("fontFamily", fontFamily);
		result.withKeyValue("rank", rank);
		result.withKeyValue("nodeSep", nodeSep);
		result.withKeyValue("infobox", infobox);
		result.withKeyValue("cardinalityInfo", cardinalityInfo);
		result.withKeyValue("propertyinfo", propertyInfo);
		result.withKeyValue("rotatetext", rotateText);
		result.withKeyValue("linetyp", lineTyp);
		if (buttons != null) {
			result.withKeyValue("buttons", new JsonArray().with(buttons));
		}
		return result;
	}

	/**
	 * Gets the raster.
	 *
	 * @return the raster
	 */
	public Boolean getRaster() {
		return raster;
	}

	/**
	 * With raster.
	 *
	 * @param value the value
	 * @return the graph options
	 */
	public GraphOptions withRaster(Boolean value) {
		this.raster = value;
		return this;
	}

	/**
	 * Gets the canvasid.
	 *
	 * @return the canvasid
	 */
	public String getCanvasid() {
		return canvasid;
	}

	/**
	 * With canvasid.
	 *
	 * @param value the value
	 * @return the graph options
	 */
	public GraphOptions withCanvasid(String value) {
		this.canvasid = value;
		return this;
	}

	/**
	 * Gets the font size.
	 *
	 * @return the font size
	 */
	public Integer getFontSize() {
		return fontSize;
	}

	/**
	 * With font size.
	 *
	 * @param value the value
	 * @return the graph options
	 */
	public GraphOptions withFontSize(Integer value) {
		this.fontSize = value;
		return this;
	}

	/**
	 * Gets the font family.
	 *
	 * @return the font family
	 */
	public String getFontFamily() {
		return fontFamily;
	}

	/**
	 * With font family.
	 *
	 * @param value the value
	 * @return the graph options
	 */
	public GraphOptions withFontFamily(String value) {
		this.fontFamily = value;
		return this;
	}

	/**
	 * Gets the rank.
	 *
	 * @return the rank
	 */
	public String getRank() {
		return rank;
	}

	/**
	 * With rank.
	 *
	 * @param value the value
	 * @return the graph options
	 */
	public GraphOptions withRank(String value) {
		this.rank = value;
		return this;
	}

	/**
	 * Gets the node sep.
	 *
	 * @return the node sep
	 */
	public Integer getNodeSep() {
		return nodeSep;
	}

	/**
	 * With node sep.
	 *
	 * @param value the value
	 * @return the graph options
	 */
	public GraphOptions withNodeSep(Integer value) {
		this.nodeSep = value;
		return this;
	}

	/**
	 * Gets the infobox.
	 *
	 * @return the infobox
	 */
	public Boolean getInfobox() {
		return infobox;
	}

	/**
	 * With infobox.
	 *
	 * @param value the value
	 * @return the graph options
	 */
	public GraphOptions withInfobox(Boolean value) {
		this.infobox = value;
		return this;
	}

	/**
	 * Gets the cardinality info.
	 *
	 * @return the cardinality info
	 */
	public Boolean getCardinalityInfo() {
		return cardinalityInfo;
	}

	/**
	 * With cardinality info.
	 *
	 * @param value the value
	 * @return the graph options
	 */
	public GraphOptions withCardinalityInfo(Boolean value) {
		this.cardinalityInfo = value;
		return this;
	}

	/**
	 * Gets the property info.
	 *
	 * @return the property info
	 */
	public Boolean getPropertyInfo() {
		return propertyInfo;
	}

	/**
	 * With property info.
	 *
	 * @param value the value
	 * @return the graph options
	 */
	public GraphOptions withPropertyInfo(Boolean value) {
		this.propertyInfo = value;
		return this;
	}

	/**
	 * Gets the buttons.
	 *
	 * @return the buttons
	 */
	public ArrayList<TYP> getButtons() {
		return buttons;
	}

	/**
	 * With button.
	 *
	 * @param values the values
	 * @return the graph options
	 */
	public GraphOptions withButton(TYP... values) {
		if (values == null) {
			return this;
		}
		if (this.buttons == null) {
			this.buttons = new ArrayList<GraphOptions.TYP>();
		}
		for (TYP item : values) {
			this.buttons.add(item);
		}
		return this;
	}

	/**
	 * Gets the display.
	 *
	 * @return the display
	 */
	public TYP getDisplay() {
		return display;
	}

	/**
	 * With display.
	 *
	 * @param value the value
	 * @return the graph options
	 */
	public GraphOptions withDisplay(TYP value) {
		this.display = value;
		return this;
	}

	/**
	 * Gets the rotate text.
	 *
	 * @return the rotate text
	 */
	public Boolean getRotateText() {
		return rotateText;
	}

	/**
	 * With rotate text.
	 *
	 * @param value the value
	 * @return the graph options
	 */
	public GraphOptions withRotateText(Boolean value) {
		this.rotateText = value;
		return this;
	}

	/**
	 * Gets the line typ.
	 *
	 * @return the line typ
	 */
	public LINETYP getLineTyp() {
		return lineTyp;
	}

	/**
	 * With line typ.
	 *
	 * @param value the value
	 * @return the graph options
	 */
	public GraphOptions withLineTyp(LINETYP value) {
		this.lineTyp = value;
		return this;
	}

	/**
	 * Gets the clear canvas.
	 *
	 * @return the clear canvas
	 */
	public Boolean getClearCanvas() {
		return clearCanvas;
	}

	/**
	 * With clear canvas.
	 *
	 * @param clearCanvas the clear canvas
	 * @return the graph options
	 */
	public GraphOptions withClearCanvas(Boolean clearCanvas) {
		this.clearCanvas = clearCanvas;
		return this;
	}
}
