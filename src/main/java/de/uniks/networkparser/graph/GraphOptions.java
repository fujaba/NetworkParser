package de.uniks.networkparser.graph;

import java.util.ArrayList;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;

public class GraphOptions {
	/* Options */
	public enum TYP {
		HTML, CANVAS, SVG, PDF
	};

	public enum RANK {
		LR, TB
	};

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

	public Boolean getRaster() {
		return raster;
	}

	public GraphOptions withRaster(Boolean value) {
		this.raster = value;
		return this;
	}

	public String getCanvasid() {
		return canvasid;
	}

	public GraphOptions withCanvasid(String value) {
		this.canvasid = value;
		return this;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public GraphOptions withFontSize(Integer value) {
		this.fontSize = value;
		return this;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public GraphOptions withFontFamily(String value) {
		this.fontFamily = value;
		return this;
	}

	public String getRank() {
		return rank;
	}

	public GraphOptions withRank(String value) {
		this.rank = value;
		return this;
	}

	public Integer getNodeSep() {
		return nodeSep;
	}

	public GraphOptions withNodeSep(Integer value) {
		this.nodeSep = value;
		return this;
	}

	public Boolean getInfobox() {
		return infobox;
	}

	public GraphOptions withInfobox(Boolean value) {
		this.infobox = value;
		return this;
	}

	public Boolean getCardinalityInfo() {
		return cardinalityInfo;
	}

	public GraphOptions withCardinalityInfo(Boolean value) {
		this.cardinalityInfo = value;
		return this;
	}

	public Boolean getPropertyInfo() {
		return propertyInfo;
	}

	public GraphOptions withPropertyInfo(Boolean value) {
		this.propertyInfo = value;
		return this;
	}

	public ArrayList<TYP> getButtons() {
		return buttons;
	}

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

	public TYP getDisplay() {
		return display;
	}

	public GraphOptions withDisplay(TYP value) {
		this.display = value;
		return this;
	}

	public Boolean getRotateText() {
		return rotateText;
	}

	public GraphOptions withRotateText(Boolean value) {
		this.rotateText = value;
		return this;
	}

	public LINETYP getLineTyp() {
		return lineTyp;
	}

	public GraphOptions withLineTyp(LINETYP value) {
		this.lineTyp = value;
		return this;
	}

	public Boolean getClearCanvas() {
		return clearCanvas;
	}

	public GraphOptions withClearCanvas(Boolean clearCanvas) {
		this.clearCanvas = clearCanvas;
		return this;
	}
}
