/*
 NetworkParser
 Copyright (c) 2011 - 2014, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
// VERSION: 2015.11.09 20:21
/*jslint forin:true, newcap:true, node: true, continue: true */
/*global document: false, window: false, navigator: false, unescape: false, java:false, Image: false, Blob: false, FileReader:false */
/*global jsPDF: false, svgConverter: false, dagre: false, SVGPathSeg: false*/
/**
 * Creates new Graph document object instance.
 *
 * @class
 * @returns {Graph}
 * @name Graph
 */
var Diagram = (function (global) {
	'use strict';
	var typ, Pos, Loader, Info, Line, Path, CSS, DragAndDrop, DagreLayout, Edge, Generalisation, Implements, Unidirectional, Aggregation, Composition, svgUtil, Drawer, CreateNode, InputNode, EditNode, CreateEdge, Selector, MoveNode, LocalEditor, ChoiceBox;
	/**
	* @constructor
	* @private
	*/
	typ = {};

	//				######################################################### util #########################################################
	typ.util = {};
	/**
	* copy One Json into another
	* @function
	* @param ref reference Json
	* @param src source Json
	* @param full all attributes include privet $
	* @param replace set the original reference or copy it
	* @returns ref
	* @name copy
	*/
	typ.util.copy = function (ref, src, full, replace) {
		if (src) {
			var i;
			for (i in src) {
				if (!src.hasOwnProperty(i) || typeof (src[i]) === "function") {
					continue;
				}
				if (i.charAt(0) === "$") {
					if (full) {ref[i] = src[i]; }
					continue;
				}
				if (typeof (src[i]) === "object") {
					if (replace) {
						ref[i] = src[i];
						continue;
					}
					if (!ref[i]) {
						if (src[i] instanceof Array) {
							ref[i] = [];
						} else {
							ref[i] = {};
						}
					}
					typ.util.copy(ref[i], src[i], full);
				} else {
					if (src[i] === "") {
						continue;
					}
					ref[i] = src[i];
				}
			}
			if (src.width) {ref.$startWidth = src.width; }
			if (src.height) {ref.$startHeight = src.height; }
		}
		return ref;
	};
	/**
	* copy Minimize Json
	* @function
	* @param target the target Json
	* @param src source Json
	* @param ref reference Json
	* @returns {target}
	*/
	typ.util.minJson = function (target, src, ref) {
		var i, temp, value;
		for (i in src) {
			if (!src.hasOwnProperty(i) || typeof (src[i]) === "function") {
				continue;
			}
			if (src[i] === null || src[i] === "" || src[i] === 0 || src[i] === false || i.charAt(0) === "$") {
				continue;
			}
			value = src[i];
			if (value instanceof typ.Options || ref !== null) {
				if (typeof (value) === "object") {
					temp = (value instanceof Array) ? [] : {};
					if (ref) {
						value = this.minJson(temp, value, ref[i]);
					} else {
						value = this.minJson(temp, value, new typ.Options());
					}
				}
				if (ref && value === ref[i]) {
					continue;
				}
			}
			if (typeof (value) === "object") {
				if (value instanceof Array && value.length < 1) {
					continue;
				}
				if (value instanceof Array) {
					target[i] = this.minJson([], value);
				} else {
					temp = this.minJson({}, value);
					if (JSON.stringify(temp, null, "") === "{}") {
						continue;
					}
					target[i] = temp;
				}
			} else {
				target[i] = value;
			}
		}
		return target;
	};
	//TODO DOCU
	typ.util.bind = function (el, eventName, eventHandler) {
		if (el.addEventListener) {
			el.addEventListener(eventName, eventHandler, false);
		} else if (el.attachEvent) {
			el.attachEvent('on' + eventName, eventHandler);
		}
	};
	typ.util.createCell = function (parent, tag, node, innerHTML, typ) {
		var tr = this.create({"tag": 'tr'}), cell;
		cell = this.create({"tag": tag, $font: true, value: innerHTML});
		node.getRoot().createdElement(cell, typ, node);
		tr.appendChild(cell);
		parent.appendChild(tr);
		return cell;
	};
	typ.util.isSVG = function (tag) {
		var i, list = ["svg", "path", "polygon", "polyline", "line", "rect", "filter", "feGaussianBlur", "feOffset", "feBlend", "linearGradient", "stop", "text", "symbol", "textPath", "defs", "fegaussianblur", "feoffset", "feblend", "circle", "ellipse", "g"];
		for (i = 0; i < list.length; i += 1) {
			if (list[i] === tag) {
				return true;
			}
		}
		return false;
	};
	typ.util.create = function (node, parent) {
		var style, item, xmlns, key, tag, k;
		if (document.createElementNS && (typ.util.isSVG(node.tag) || node.xmlns || (node.model && node.model.getRoot().getTyp() === "svg") || parent === "svg")) {
			if (node.xmlns) {
				xmlns = node.xmlns;
			} else {
				xmlns = "http://www.w3.org/2000/svg";
			}
			if (node.tag === "img" && xmlns) {
				item = document.createElementNS(xmlns, "image");
				item.setAttribute('xmlns:xlink', "http://www.w3.org/1999/xlink");
				item.setAttributeNS("http://www.w3.org/1999/xlink", 'href', node.src);
			} else {
				item = document.createElementNS(xmlns, node.tag);
			}
		} else {
			item = document.createElement(node.tag);
		}
		tag = node.tag.toLowerCase();
		for (key in node) {
			if (!node.hasOwnProperty(key)) {
				continue;
			}
			k = key.toLowerCase();
			if (node[key] === null) {
				continue;
			}
			if (k === 'tag' || k.charAt(0) === '$' || k === 'model') {
				continue;
			}
			if (k.charAt(0) === '#') {
				item[k.substring(1)] = node[key];
				continue;
			}
			if (k === 'rotate') {
				item.setAttribute("transform", "rotate(" + node[key] + "," + node.model.x + "," + node.model.y + ")");
				continue;
			}
			if (k === 'value') {
				if (!node[key]) {
					continue;
				}
				if (tag !== "input") {
					if (tag === "text") {// SVG
						item.appendChild(document.createTextNode(node[key]));
					} else {
						item.innerHTML = node[key];
					}
				} else {
					item[key] = node[key];
				}
				continue;
			}
			if (k.indexOf("on") === 0) {
				this.bind(item, k.substring(2), node[key]);
				continue;
			}
			if (k.indexOf("-") >= 0) {
				item.style[key] = node[key];
			} else {
				if (k === "style" && typeof (node[key]) === "object") {
					for (style in node[key]) {
						if (!node[key].hasOwnProperty(style)) {
							continue;
						}
						if (node[key][style]) {
							if ("transform" === style) {
								item.style.transform = node[key][style];
								item.style.msTransform = item.style.MozTransform = item.style.WebkitTransform = item.style.OTransform = node[key][style];
							} else {
								item.style[style] = node[key][style];
							}
						}
					}
				} else {
					item.setAttribute(key, node[key]);
				}
			}
		}
		if (node.$font && parent) {
			if (parent.model && parent.model.options && parent.model.options.font) {
				for (key in parent.model.options.font) {
					if (!parent.model.options.font.hasOwnProperty(key)) {
						continue;
					}
					if (parent.model.options.font[key]) {
						if (item.style) {
							item.style[key] = parent.model.options.font[key];
						} else {
							item.setAttribute(key, parent.model.options.font[key]);
						}
					}
				}
			}
		}
		if (node.$parent) {
			node.$parent.appendChild(item);
		}
		if (node.model) {
			item.model = node.model;
		}
		return item;
	};
	typ.util.getModelNode = function (element) {
		if (!element.model) {
			if (element.parentElement) {
				return this.getModelNode(element.parentElement);
			}
			return null;
		}
		return element;
	};
	typ.util.getValue = function (value) {return parseInt(("0" + value).replace("px", ""), 10); };
	typ.util.isIE = function () {return document.all && !window.opera; };
	typ.util.isFireFox = function () {return navigator.userAgent.toLowerCase().indexOf('firefox') > -1; };
	typ.util.isOpera = function () {return navigator.userAgent.indexOf("Opera") > -1; };
	typ.util.getEventX = function (event) {return (this.isIE) ? window.event.clientX : event.pageX; };
	typ.util.getEventY = function (event) {return (this.isIE) ? window.event.clientY : event.pageY; };
	typ.util.selectText = function (text) {
		var selection, range;
		if (this.isIE()) {
			range = document.body.createTextRange();
			range.moveToElementText(text);
			range.select();
		} else if (this.isFireFox() || this.isOpera()) {
			selection = window.getSelection();
			range = document.createRange();
			range.selectNodeContents(text);
			selection.removeAllRanges();
			selection.addRange(range);
		}
	};
	typ.util.sizeOf = function (item, model, node) {
		var board, rect;
		if (!item) {return; }
		board = model.getRoot().board;
		if (board.tagName === "svg") {
			if (typeof item === 'string') {
				item = typ.util.create({tag: "text", $font: true, value: item});
				item.setAttribute("width", "5px");
			}
		} else if (typeof item === 'string') {
			item = document.createTextNode(item);
		}
		board.appendChild(item);
		rect = item.getBoundingClientRect();
		board.removeChild(item);
		if (node) {
			if (!node.$startWidth) {
				node.width = Math.round(rect.width);
			}
			if (!node.$startHeight) {
				node.height = Math.round(rect.height);
			}
		}
		return rect;
	};
	typ.util.hasClass = function (ele, cls) {return ele.className.indexOf(cls) > 0; };
	typ.util.addClass = function (ele, cls) {if (!this.hasClass(ele, cls)) {
		ele.className = ele.className + " " + cls;
	} };
	typ.util.removeClass = function (ele, cls) {
		if (this.hasClass(ele, cls)) {
			var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
			ele.className = ele.className.replace(reg, ' ');
		}
	};
	typ.util.MinMax = function (node, min, max) {
		max.x = Math.max(max.x, node.x + Number(node.width) + 10);
		max.y = Math.max(max.y, node.y + Number(node.height) + 10);
		min.x = Math.max(min.x, node.x);
		min.y = Math.max(min.y, node.y);
	};
	typ.util.Range = function (min, max, x, y) {
		max.x = Math.max(max.x, x);
		max.y = Math.max(max.y, y);
		min.x = Math.min(min.x, x);
		min.y = Math.min(min.y, y);
	};
	typ.util.serializeXmlNode = function (xmlNode) {
		if (window.XMLSerializer !== undefined) {
			return (new window.XMLSerializer()).serializeToString(xmlNode);
		}
		if (xmlNode.xml) {
			return xmlNode.xml;
		}
		return xmlNode.outerHTML;
	};
	typ.util.utf8$to$b64 = function (str) {
		return window.btoa(decodeURIComponent(encodeURIComponent(str)));
	};
	typ.util.createImage = function (node, model) {
		var n, img;
		node.model = node;
		if (svgUtil.isSymbol(node)) {
			return svgUtil.draw(null, node);
		}
		n = {tag: "img", model: node, src: node.src};
		if (node.width || node.height) {
			n.width = node.width;
			n.height = node.height;
		} else {
			n.xmlns = "http://www.w3.org/1999/xhtml";
		}
		img = typ.util.create(n, model);
		if (!node.width && !node.height) {
			model.appendImage(img);
			return null;
		}
		return img;
	};
	typ.util.setSize = function (item, x, y) {
		x = typ.util.getValue(x);
		y = typ.util.getValue(y);
		item.setAttribute("width", x);
		item.setAttribute("height", y);
		item.style.width = Math.ceil(x);
		item.style.height = Math.ceil(y);
	};
	typ.util.setPos = function (item, x, y) {
		if (item.x && item.x.baseVal) {
			item.style.left = x + "px";
			item.style.top = y + "px";
		} else {
			item.x = x;
			item.y = y;
		}
	};
	typ.util.getNumber = function (str) {
		return parseInt((str || "0").replace("px", ""), 10);
	};
	typ.util.getStyle = function (styleProp) {
		var i, style, diff, current, ref, el = document.createElement("div"), css = new CSS(styleProp);
		document.body.appendChild(el);
		ref = new CSS(styleProp, el).css;
		style = window.getComputedStyle(el, null);
		el.className = styleProp;
		current = new CSS(styleProp, el).css;
		diff = typ.util.getNumber(style.getPropertyValue("border-width"));
		for (i in current) {
			if (!current.hasOwnProperty(i)) {
				continue;
			}
			if (i === "width" || i === "height") {
				if (typ.util.getNumber(current[i]) !== 0 && typ.util.getNumber(current[i]) + diff * 2 !== typ.util.getNumber(ref[i])) {
					css.add(i, current[i]);
				}
			} else if (current[i] !== ref[i]) {
				css.add(i, current[i]);
			}
		}
		document.body.removeChild(el);
		return css;
	};
	//				######################################################### CSS #########################################################
	CSS = function (name, item) {
		var i, value, border, prop, el;
		this.name = name;
		this.css = {};
		if (!item) {
			return;
		}

		el = window.getComputedStyle(item, null);
		border = el.getPropertyValue("border");
		for (i in el) {
			prop = i;
			value = el.getPropertyValue(prop);
			if (value && value !== "") {
				// optimize CSS	
				if (border) {
					if (prop === "border-bottom" || prop === "border-right" || prop === "border-top" || prop === "border-left") {
						if (value !== border) {
							this.css[prop] = value;
						}
					} else if (prop === "border-color" || prop === "border-bottom-color" || prop === "border-right-color" || prop === "border-top-color" || prop === "border-left-color") {
						if (border.substring(border.length - value.length) !== value) {
							this.css[prop] = value;
						}
					} else if (prop === "border-width") {
						if (border.substring(0, value.length) !== value) {
							this.css[prop] = value;
						}
					} else {
						this.css[prop] = value;
					}
				} else {
					this.css[prop] = value;
				}
			}
		}
	};
	CSS.prototype.add = function (key, value) {
		this.css[key] = value;
	};
	CSS.prototype.get = function (key) {
		var i;
		for (i in this.css) {
			if(i === key) {
				return this.css[key];
			}
		}
		return null;
	};
	CSS.prototype.getNumber = function (key) {
		return parseInt((this.get(key) || "0").replace("px", ""), 10);
	};
	CSS.prototype.getString = function (styleName) {
		var str, style;
		str = "{";
		for (style in this.css) {
			if (!this.css.hasOwnProperty(style)) {
				continue;
			}
			str = str + style + ":" + this.css[style] + ";";
		}
		str = str + "}";
		return str;
	};
	CSS.prototype.getSVGString = function (board) {
		var str, pos, style, defs, value, filter, z;
		str = "{";
		for (style in this.css) {
			if (!this.css.hasOwnProperty(style)) {
				continue;
			}
			if (style === "border") {
				pos = this.css[style].indexOf(" ");
				str = str + "stroke-width: " + this.css[style].substring(0, pos) + ";";
				pos = this.css[style].indexOf(" ", pos + 1);
				str = str + "stroke:" + this.css[style].substring(pos) + ";";
			} else if (style === "background-color") {
				str = str + "fill: " + this.css[style] + ";";
			} else if (style === "background") {
				value = typ.util.getSubstring(this.css[style], "linear-gradient", "(", ")", ",");
				if (value.length > 0) {
					defs = svgUtil.getDefs(board);
					if (value[0] === "45deg") {
						pos = 1;
						filter = typ.util.create({tag: "linearGradient", "id": this.name, x1: "0%", x2: "100%", y1: "100%", y2: "0%"});
					} else {
						filter = typ.util.create({tag: "linearGradient", "id": this.name, x1: "0%", x2: "0%", y1: "100%", y2: "0%"});
						pos = 0;
					}
					defs.appendChild(filter);
					while (pos < value.length) {
						value[pos] = value[pos].trim();
						z = value[pos].lastIndexOf(" ");
						filter.appendChild(typ.util.create({tag: "stop", "offset": value[pos].substring(z + 1), style: {"stop-color": value[pos].substring(0, z)}}));
						pos += 1;
					}
					str = str + "fill: url(#" + this.name + ");";
					continue;
				}
				str = str + style + ": " + this.css[style] + ";";
					//box-shadow: inset 0 3px 4px #888;
//				<defs>
//					<filter id="drop-shadow">
//						<feGaussianBlur in="SourceAlpha" result="blur-out" stdDeviation="2"></feGaussianBlur>
//						<feOffset in="blur-out" dx="2" dy="2"></feOffset>
//						<feBlend in="SourceGraphic" mode="normal"></feBlend>
//					</filter>
//				</defs>
			} else {
				str = str + style + ": " + this.css[style] + ";";
			}
		}
		str = str + "}";
		return str;
	};
	typ.util.getSubstring = function (str, search, startChar, endChar, splitter) {
		var pos, end, count = 0, array = [];
		pos = str.indexOf(search);
		if (pos > 0) {
			end = str.indexOf(startChar, pos);
			pos = end + 1;
			if (end > 0) {
				while (end < str.length) {
					if (str.charAt(end) === startChar) {
						count += 1;
					}
					if (str.charAt(end) === endChar) {
						count -= 1;
						if (count === 0) {
							if (splitter && pos !== end) {
								array.push(str.substring(pos, end).trim());
							}
							break;
						}
					}
					if (str.charAt(end) === splitter && count === 1) {
						array.push(str.substring(pos, end).trim());
						pos = end + 1;
					}

					end += 1;
				}
				if (splitter) {
					return array;
				}
				return str.substring(pos, end);
			}
			return str.substring(pos);
		}
		return "";
	};
	//				######################################################### Pos #########################################################
	/**
	* Creates new Pos document object instance. Position with X,Y and ID
	* @class
	* @returns {Pos}
	* @name Pos
	*/
	Pos = function (x, y, id) {this.x = Math.round(x || 0); this.y = Math.round(y || 0); if (id) {this.$id = id; } };
	//				######################################################### Line #########################################################
	Line = function (source, target, line, style) {this.source = source; this.target = target; this.line = line; this.style = style; };
	Line.Format = {SOLID: "SOLID", DOTTED: "DOTTED"};
	//				######################################################### Path #########################################################
	Path = function (path, fill, close, angle) {this.path = path; this.fill = fill; this.close = close; this.angle = angle; };
	//				######################################################### Loader #########################################################
	Loader = function (graph) {this.images = []; this.graph = graph; this.abort = false; };
	Loader.prototype.execute = function () {
		if (this.images.length === 0) {
			this.graph.layout(this.width, this.height);
		} else {
			var img = this.images[0];
			this.graph.root.appendChild(img);
		}
	};
	Loader.prototype.onLoad = function (e) {
		var idx, img = e.target;
		idx = this.images.indexOf(img);
		img.model.width = img.width;
		img.model.height = img.height;
		this.graph.root.removeChild(img);
		if (idx !== -1) {
			this.images.splice(idx, 1);
		}
		this.execute();
	};
	Loader.prototype.add = function (img) {
		var that = this, func = function (e) {that.onLoad(e); };
		this.graph.bind(img, "load", func);
		this.images.push(img);
		this.execute();
	};
	//				######################################################### Options #########################################################
	typ.Options = function () {
		this.raster = false;
		this.addBorder = true;
		this.display = "svg";
		this.font = {"font-size": "10px", "font-family": "Verdana"};
		this.layout = {name: "Dagre", rankDir: "TB", nodesep: 10};	// Dagre TB, LR
		this.CardinalityInfo = true;
		this.propertyinfo = true;
		this.rotatetext = true;
		this.linetyp = "center";
		this.buttons = ["HTML", "SVG"];	// ["HTML", "SVG", "PNG", "PDF"]
	};
	//				######################################################### GraphNode #########################################################
	typ.GraphNode = function (id) {
		this.typ = "node";
		this.id = id;
		this.$parent = null;
		this.x = this.y = this.width = this.height = 0;
		this.$isdraggable = true;
	};
	typ.GraphNode.prototype.getX = function () {if (this.$parent) {return this.$parent.getX() + this.x; } return this.x; };
	typ.GraphNode.prototype.getY = function () {if (this.$parent) {return this.$parent.getY() + this.y; } return this.y; };
	typ.GraphNode.prototype.getEdges = function () {return this.$edges; };
	typ.GraphNode.prototype.getRoot = function () {if (this.$parent) {return this.$parent.getRoot(); } return this; };
	typ.GraphNode.prototype.clear = function () {this.$RIGHT = this.$LEFT = this.$UP = this.$DOWN = 0; };
	typ.GraphNode.prototype.removeFromBoard = function (board) {if (this.$gui) {board.removeChild(this.$gui); this.$gui = null; } };
	typ.GraphNode.prototype.set = function (id, value) {if (value) {this[id] = value; } };
	typ.GraphNode.prototype.addEdge = function (edge) {
		if (!this.$edges) {
			this.$edges = [];
		}
		this.$edges.push(edge);
	};
	typ.GraphNode.prototype.isClosed = function () {
		if (this.status === "close") {
			return true;
		}
		if (this.$parent) {return this.$parent.isClosed(); }
		return false;
	};
	typ.GraphNode.prototype.getShowed = function () {
		if (this.status === "close") {
			if (!this.$parent.isClosed()) {
				return this;
			}
		}
		if (this.isClosed()) {
			return this.$parent.getShowed();
		}
		return this;
	};
	typ.GraphNode.prototype.draw = function (typ) {
		if (typ) {
			if (typ.toLowerCase() === "html") {
				return this.drawHTML();
			}
		}
		return this.drawSVG();
	};
	typ.GraphNode.prototype.drawSVG = function () {
		var item;
		if (this.content) {
			this.content.width = this.content.width || 0;
			this.content.height = this.content.height || 0;
			if (this.content.plain) {
				return typ.util.create({tag: "text", $font: true, "text-anchor": "left", "x": (this.x + 10), value: this.content.plain}, this);
			}
			if (this.content.src) {
				item = svgUtil.createImage(this.content);
				if (!item) {return null; }
				return item;
			}
			item = typ.util.create({tag: "g", model: this}, this);
			if (this.content.svg) {
				item.setAttribute('transform', "translate(" + this.x + " " + this.y + ")");
				item.innerHTML = this.content.$svg;
				return item;
			}
			if (this.content.html) {
				item.setAttribute('transform', "translate(" + this.x + " " + this.y + ")");
				item.innerHTML = this.content.$svg;
				return item;
			}
		}
		item = typ.util.create({tag: "circle", "class": "Node", cx: this.x + 10, cy: this.y + 10, r: "10", model: this, width: this.width, height: this.height}, this);
		return item;
	};
	typ.GraphNode.prototype.drawHTML = function (drawer) {
		var item = typ.util.create({tag: "div", model: this}, drawer);
		typ.util.setPos(item, this.x, this.y);
		if (this.content) {
			this.content.width = this.content.width || 0;
			this.content.height = this.content.height || 0;
			if (this.content.src) {
				item = drawer.createImage(this.content);
				if (!item) {return null; }
				item.appendChild(item);
			}
			if (this.content.html) {
				item.innerHTML = this.content.html;
			}
			return item;
		}
		return typ.util.create({tag: "div", "class": "Node", model: this}, drawer);
	};
	//				######################################################### Clazz #########################################################
	typ.Clazz = function () {};
	typ.Clazz.prototype = new typ.GraphNode();
	typ.Clazz.prototype.drawSVG = function (draw) {
		var width, height, id, textWidth, x, y, z, item, rect, g, board, styleHeader, headerHeight;
		board = this.getRoot().board;
		styleHeader = typ.util.getStyle("ClazzHeader");
		headerHeight = styleHeader.getNumber("height");
		width = 0;
		height = 10 + headerHeight;
		
		if (this.typ === "Object" || this.getRoot().model.typ.toLowerCase() === "objectdiagram") {
			id = this.id.charAt(0).toLowerCase() + this.id.slice(1);
			item = "Object";
		} else {
			id = this.id;
			item = "Clazz";
			if (this.counter) {
				id += " (" + this.counter + ")";
			}
		}
		g = typ.util.create({tag: "g", model: this});
		textWidth = typ.util.sizeOf(id, this).width;
		width = Math.max(width, textWidth);
		if (this.attributes && this.attributes.length > 0) {
			height = height + this.attributes.length * 25;
			for (z = 0; z < this.attributes.length; z += 1) {
				width = Math.max(width, typ.util.sizeOf(this.attributes[z], this).width);
			}
		} else {
			height += 20;
		}
		if (this.methods && this.methods.length > 0) {
			height = height + this.methods.length * 25;
			for (z = 0; z < this.methods.length; z += 1) {
				width = Math.max(width, typ.util.sizeOf(this.methods[z], this).width);
			}
		}
		width += 20;

		y = this.getY();
		x = this.getX();

		rect = {tag: "rect", "width": width, "height": height, "x": x, "y": y, "class": item+" draggable", "fill": "none"};
		g.appendChild(typ.util.create(rect));
		g.appendChild(typ.util.create({tag: "rect", rx: 0, "x": x, "y": y, height: headerHeight, "width": width, "class": "ClazzHeader"}));

		item = typ.util.create({tag: "text", $font: true, "class": "InfoText", "text-anchor": "right", "x": x + width / 2 - textWidth / 2, "y": y + (headerHeight/2), "width": textWidth});

		if (this.typ === "Object" || this.getRoot().model.typ.toLowerCase() === "objectdiagram") {
			item.setAttribute("text-decoration", "underline");
		}
		item.appendChild(document.createTextNode(id));

		g.appendChild(item);
		g.appendChild(typ.util.create({tag: "line", x1: x, y1: y + headerHeight, x2: x + width, y2: y + headerHeight, stroke: "#000"}));
		y += headerHeight + 20;

		if (this.attributes) {
			for (z = 0; z < this.attributes.length; z += 1) {
				g.appendChild(typ.util.create({tag: "text", $font: true, "text-anchor": "left", "width": width, "x": (x + 10), "y": y, value: this.attributes[z]}));
				y += 20;
			}
			if (this.attributes.length > 0) {
				y -= 10;
			}
		}
		if (this.methods && this.methods.length > 0) {
			g.appendChild(typ.util.create({tag: "line", x1: x, y1: y, x2: x + width, y2: y, stroke: "#000"}));
			y += 20;
			for (z = 0; z < this.methods.length; z += 1) {
				g.appendChild(typ.util.create({tag: "text", $font: true, "text-anchor": "left", "width": width, "x": x + 10, "y": y, value: this.methods[z]}));
				y += 20;
			}
		}
		return g;
	};
	typ.Clazz.prototype.drawHTML = function () {
		var first, z, cell, item, model, htmlElement = typ.util.create({tag: "div", model: this});
		model = this.getRoot().model;
		htmlElement.className = "classElement";
		typ.util.setPos(htmlElement, this.x, this.y);
		htmlElement.style.zIndex = 5000;

		model.createdElement(htmlElement, "class", this);
		item = typ.util.create({tag: 'table', border: "0", style: {width: "100%", height: "100%"}});
		htmlElement.appendChild(item);
		if (this.head && this.head.$src) {
			cell = this.createCell(item, "td", this);
			cell.style.textAlign = "center";
			if (!this.head.$img) {
				this.head.$img = {};
				this.head.$img.src = this.head.$src;
				this.head.$img.width = this.head.$width;
				this.head.$img.height = this.head.$height;
			}
			z = this.createImage(this.head.$img);
			if (z) {
				cell.appendChild(z);
			}
		}
		if (this.headinfo) {
			this.createCell(item, "td", this, this.headinfo).className = "head";
		}

		if (model.typ.toLowerCase() === "objectdiagram") {
			z = this.id.charAt(0).toLowerCase() + this.id.slice(1);
		} else {
			z = this.id;
		}
		if (this.href) {
			z = "<a href=\"" + this.href + "\">" + z + "</a>";
		}
		cell = typ.util.createCell(item, "th", this, z, "id");
		if (model.typ.toLowerCase() === "objectdiagram") {
			cell.style.textDecorationLine = "underline";
		}
		cell = null;
		if (this.attributes) {
			first = true;
			for (z = 0; z < this.attributes.length; z += 1) {
				cell = typ.util.createCell(item, "td", this, this.attributes[z], "attribute");
				if (!first) {
					cell.className = 'attributes';
				} else {
					cell.className = 'attributes first';
					first = false;
				}
			}
		}
		if (this.methods) {
			first = true;
			for (z = 0; z < this.methods.length; z += 1) {
				cell = typ.util.createCell(item, "td", this, this.methods[z], "method");
				if (!first) {
					cell.className = 'methods';
				} else {
					cell.className = 'methods first';
					first = false;
				}
			}
		}
		if (!cell) {
			cell = typ.util.createCell(item, "td", this, "&nbsp;");
			cell.className = 'first';
			this.getRoot().createdElement(cell, "empty", this);
		}
		htmlElement.appendChild(item);
		htmlElement.node = this;
		this.$gui = htmlElement;
		return htmlElement;
	};
	//				######################################################### Symbol #########################################################
	typ.Symbol = function () {this.lib = new typ.SymbolLibary(); };
	typ.Symbol.prototype = new typ.GraphNode();
	typ.Symbol.prototype.drawSVG = function () {
		return this.lib.draw(this, "svg");
	};
	typ.Symbol.prototype.drawHTML = function () {
		return this.lib.draw(this);
	};
	//				######################################################### Pattern #########################################################
	typ.Pattern = function () {};
	typ.Pattern.prototype = new typ.GraphNode();
	typ.Pattern.prototype.drawSVG = function (drawer) {
		var width, height, id, textWidth, x, y, rect, item, g = typ.util.create({tag: "g", model: this}, drawer);
		width = 0;
		height = 40;
		id = this.id;
		if (this.counter) {
			id += " (" + this.counter + ")";
		}
		textWidth = typ.util.sizeOf(id, this).width;
		width = Math.max(width, textWidth);
		height += 20;
		width += 20;

		y = this.getY();
		x = this.getX();

		rect = {tag: "rect", "width": width, "height": height, "x": x, "y": y, "fill": "#fff", "class": "draggable"};
		rect.fill = "lightblue";

		rect.stroke = svgUtil.getColor(this.style);
		g.appendChild(typ.util.create(rect, drawer));
		item = typ.util.create({tag: "text", $font: true, "text-anchor": "right", "x": x + width / 2 - textWidth / 2, "y": y + 20, "width": textWidth}, drawer);
		item.appendChild(document.createTextNode(id));
		g.appendChild(item);
		g.appendChild(typ.util.create({tag: "line", x1: x, y1: y + 30, x2: x + width, y2: y + 30, stroke: rect.stroke}, drawer));
		y += 50;
		return g;
	};
	typ.Pattern.prototype.drawHTML = function (drawer) {
		var cell, item = typ.util.create({tag: "div", model: this}, drawer);
		item.className = "patternElement";
		typ.util.setPos(item, this.x, this.y);
		this.getRoot().createdElement(item, "class", this);
		item.appendChild(typ.util.create({tag: 'table', border: "0", style: {width: "100%", height: "100%"}}, drawer));
		if (this.href) {
			cell = typ.util.createCell(item, "th", this, "<a href=\"" + this.href + "\">" + this.id + "</a>", "id");
		} else {
			cell = typ.util.createCell(item, "th", this, this.id, "id");
		}
		cell = typ.util.createCell(item, "td", this, "&nbsp;");
		cell.className = 'first';
		this.getRoot().createdElement(cell, "empty", this);
		item.node = this;
		this.$gui = item;
		return item;
	};
	//				######################################################### GraphModel #########################################################
	typ.GraphModel = function (json, options, parent) {
		this.typ = "classdiagram";
		this.$isdraggable = true;
		this.$parent = parent;
		json = json || {};
		this.left = json.left || 0;
		this.top = json.top || 0;
		this.x = this.y = this.width = this.height = 0;
		if (json.minid) {
			this.minid = json.minid;
		}
		this.$nodeCount = 0;
		this.nodes = {};
		this.edges = [];
		json = json || {};
		this.typ = json.typ || "classdiagram";
		this.set("id", json.id);
		this.options = typ.util.copy(typ.util.copy(new typ.Options(), json.options), options, true, true);
		this["package"] = "";
		this.set("info", json.info);
		this.set("style", json.style);
		var i;
		if (json.nodes) {
			for (i = 0; i < json.nodes.length; i += 1) {
				this.addNode(json.nodes[i]);
			}
		}
		if (json.edges) {
			for (i = 0; i < json.edges.length; i += 1) {
				this.addEdgeModel(json.edges[i]);
			}
		}
	};
	typ.GraphModel.prototype = new typ.GraphNode();
	typ.GraphModel.prototype.clear = function () {
		var i;
		typ.GraphNode.prototype.clear.call(this);
		for (i in this.nodes) {
			if (!this.nodes.hasOwnProperty(i)) {
				continue;
			}
			this.nodes[i].clear();
		}
	};
	typ.GraphModel.prototype.addEdgeModel = function (e) {
		var edge, list, typ = e.typ || "edge";
		typ = typ.charAt(0).toUpperCase() + typ.substring(1).toLowerCase();
		list = {"Edge": Edge, "Generalisation": Generalisation, "Implements": Implements, "Unidirectional": Unidirectional, "Aggregation": Aggregation, "Composition": Composition};
		if (list[typ]) {
			edge = new list[typ]();
		} else {
			edge = new Edge();
		}
		edge.$parent = this;
		edge.source = new Info(e.source, this, edge);
		edge.target = new Info(e.target, this, edge);
		edge.$sNode = this.getNode(edge.source.id, true);
		edge.$sNode.addEdge(edge);
		if (e.info) {
			if (typeof (e.info) === "string") {
				edge.info = {id: e.info};
			} else {
				edge.info = {id: e.info.id, property: e.info.property, cardinality: e.info.cardinality};
			}
		}
		edge.$parent = this;
		edge.set("style", e.style);
		edge.set("counter", e.counter);
		edge.$tNode = this.getNode(edge.target.id, true);
		edge.$tNode.addEdge(edge);
		this.edges.push(edge);
		return edge;
	};
	typ.GraphModel.prototype.addEdge = function (source, target) {
		var edge = new Edge();
		edge.source = this.addNode(source);
		edge.target = this.addNode(target);
		return this.addEdgeModel(edge);
	};
	typ.GraphModel.prototype.addNode = function (node) {
		/* testing if node is already existing in the graph */

		if (typeof (node) === "string") {
			node = {id: node, typ: "node"};
		}
		node.typ = node.typ || "node";
		node.typ = node.typ.charAt(0).toUpperCase() + node.typ.substring(1).toLowerCase();

		if (!(node.id)) {
			node.id = node.typ + "$" + (this.$nodeCount + 1);
		}
		if (this.nodes[node.id] !== undefined) {
			return this.nodes[node.id];
		}
		if (node.typ.indexOf("diagram", node.typ.length - 7) !== -1 || node.typ === "GraphModel") {
			node = new typ.GraphModel(node, new typ.Options());
		} else if (new typ.SymbolLibary().isSymbol(node)) {
			node = typ.util.copy(new typ.Symbol(), node);
		} else if (node.typ === "Clazz" || node.typ === "Object") {
			node = typ.util.copy(new typ.Clazz(), node);
		} else if (node.typ === "Pattern") {
			node = typ.util.copy(new typ.Pattern(), node);
		} else {
			node = typ.util.copy(new typ.GraphNode(), node);
		}
		this.nodes[node.id] = node;
		node.$parent = this;
		this.$nodeCount += 1;
		return this.nodes[node.id];
	};
	typ.GraphModel.prototype.removeEdge = function (idSource, idTarget) {
		var z, e;
		for (z = 0; z < this.edges.length; z += 1) {
			e = this.edges[z];
			if (e.$sNode.id === idSource && e.$tNode.id === idTarget) {
				this.edges.splice(z, 1);
				z -= 1;
			} else if (e.$tNode.id === idSource && e.$sNode.id === idTarget) {
				this.edges.splice(z, 1);
				z -= 1;
			}
		}
	};
	typ.GraphModel.prototype.removeNode = function (id) {
		delete (this.nodes[id]);
		var i;
		for (i = 0; i < this.edges.length; i += 1) {
			if (this.edges[i].$sNode.id === id || this.edges[i].$tNode.id === id) {
				this.edges.splice(i, 1);
				i -= 1;
			}
		}
	};
	typ.GraphModel.prototype.getNode = function (id, isSub, deep) {
		var n, i, r;
		deep = deep || 0;
		if (this.nodes[id]) {
			return this.nodes[id];
		}
		if (!isSub) {
			return this.addNode(id);
		}
		for (i in this.nodes) {
			if (!this.nodes.hasOwnProperty(i)) {
				continue;
			}
			n = this.nodes[i];
			if (n instanceof typ.GraphModel) {
				r = n.getNode(id, isSub, deep + 1);
				if (r) {
					return r;
				}
			}
		}
		if (deep === 0) {
			return this.addNode(id);
		}
		return null;
	};
	typ.GraphModel.prototype.toJson = function () {return this.copy({}, this); };
	typ.GraphModel.prototype.createdElement = function (element, typ) {this.$parent.createdElement(element, typ); };
	typ.GraphModel.prototype.removeFromBoard = function (board) {
		if (this.$gui) {
			board.removeChild(this.$gui);
			this.$gui = null;
		}
	};
	typ.GraphModel.prototype.resize = function (mode) {};
	typ.GraphModel.prototype.getEdges = function () {return this.edges; };
	typ.GraphModel.prototype.calcLines = function (drawer) {
		var i, sourcePos, e, ownAssoc = [];
		for (i in this.nodes) {
			if (!this.nodes.hasOwnProperty(i) || typeof (this.nodes[i]) === "function") {
				continue;
			}
			this.nodes[i].clear();
		}
		for (i = 0; i < this.edges.length; i += 1) {
			e = this.edges[i];
			if (!e.calc(this.$gui, drawer)) {
				ownAssoc.push(e);
			}
		}
		for (i = 0; i < ownAssoc.length; i += 1) {
			ownAssoc[i].calcOwnEdge();
			sourcePos = ownAssoc[i].getCenterPosition(ownAssoc[i].$sNode, ownAssoc[i].$start);
			ownAssoc[i].calcInfoPos(sourcePos, ownAssoc[i].$sNode, ownAssoc[i].source);

			sourcePos = ownAssoc[i].getCenterPosition(ownAssoc[i].$tNode, ownAssoc[i].$end);
			ownAssoc[i].calcInfoPos(sourcePos, ownAssoc[i].$tNode, ownAssoc[i].target);
		}
	};
	typ.GraphModel.prototype.validateModel = function () {
		var e, z, n, id, node, list;
		if (this.typ === "classdiagram") {
			list = this.edges;
			for (e = 0; e < list.length; e += 1) {
				node = list[e].$sNode;
				z = node.id.indexOf(":");
				if (z > 0) {
					id = node.id.substring(z + 1);
					n = this.getNode(id, true, 1);
					delete (this.nodes[node.id]);
					this.edges[e].source.id = id;
					if (n) {
						this.edges[e].$sNode = n;
					} else {
						node.id = id;
						this.nodes[node.id] = node;
					}
				}
				node = list[e].$tNode;
				z = node.id.indexOf(":");
				if (z > 0) {
					id = node.id.substring(z + 1);
					n = this.getNode(id, true, 1);
					delete (this.nodes[node.id]);
					this.edges[e].target.id = id;
					if (n) {
						this.edges[e].$tNode = n;
					} else {
						node.id = id;
						this.nodes[node.id] = node;
					}
				}
				if (!list[e].source.cardinality) {
					list[e].source.cardinality = "one";
				}
				if (!list[e].target.cardinality) {
					list[e].target.cardinality = "one";
				}
				// Refactoring Edges for same property and typ set cardinality
				for (z = e + 1; z < list.length; z += 1) {
					id = typeof (java);
					if (!(id === typeof list[z])) {
						continue;
					}
					if (this.validateEdge(list[e], list[z])) {
						list[e].target.cardinality = "many";
						list.splice(z, 1);
						z -= 1;
					} else if (this.validateEdge(list[z], list[e])) {
						list[e].source.cardinality = "many";
						list.splice(z, 1);
						z -= 1;
					}
				}
			}
		}
	};
	typ.GraphModel.prototype.validateEdge = function (sEdge, tEdge) {
		return (sEdge.source.id === tEdge.source.id && sEdge.target.id === tEdge.target.id) && (sEdge.source.property === tEdge.source.property && sEdge.target.property === tEdge.target.property);
	};
	typ.GraphModel.prototype.getBoard = function (type) {
		if (type === "svg") {
			return typ.util.create({tag: "svg"});
		}
		return typ.util.create({tag: "div", model: this});
	};
	typ.GraphModel.prototype.drawHeader = function () {
		var temp, list, that = this, child, item, func, i, board, type, isInTool, removeToolItems;
		board = this.$gui;
		type = this.getRoot().getTyp().toUpperCase();
		list = ["HTML", "SVG", "PNG"];

		isInTool = function (board, x, y, ox, oy) {
			var i, g, gx, gy, gw, gh;
			// Mode x,y
			x -= ox;
			y -= oy;
			for (i = 0; i < board.toolitems.length; i += 1) {
				g = board.toolitems[i];
				if (!g.offsetWidth && g.tool) {
					gx = g.tool.x;
					gy = g.tool.y;
					gw = g.tool.width + gx;
					gh = g.tool.height + gy;
				} else {
					gx = g.offsetLeft;
					gy = g.offsetTop;
					gw = g.offsetWidth + gx;
					gh = g.offsetHeight + gy;
				}
				if (x >= gx && x <= gw && y >= gy && y <= gh) {
					return true;
				}
			}
			return false;
		};
		removeToolItems = function () {
			for (i = 0; i < board.toolitems.length; i += 1) {
				board.toolitems[i].close();
				board.removeChild(board.toolitems[i]);
			}
		};
		temp = typeof (svgConverter);
		if (temp !== "undefined") {
			list.push("EPS");
			temp = typeof (jsPDF);
			list.push(temp !== "undefined" ? "PDF" : "");
		}
		if (type === "HTML") {
			item = this.getBoard("svg");
			board.appendChild(item);
			board = item;
		}
		board.toolitems = [];
		board.rasterElements = [];
		board.saveShow = false;
		board.onmouseover = function () {
			var i;
			for (i = 0; i < board.toolitems.length; i += 1) {
				board.appendChild(board.toolitems[i]);
			}
		};
		board.onmouseout = function (event) {
			var left = board.offsetLeft, top = board.offsetTop, x = Math.floor(event.pageX), y = Math.floor(event.pageY);
			if (!left) {
				if (board.parentNode) {
					left = board.parentNode.offsetLeft;
				} else {
					left = 0;
				}
			}
			if (!top) {
				if (board.parentNode) {
					top = board.parentNode.offsetTop;
				} else {
					top = 0;
				}
			}
			if (!isInTool(board, x, y, left, top)) {
				removeToolItems(board);
			}
		};

		item = this.options.buttons;
		func = function (e) {
			var t = e.currentTarget.typ;
			that.$parent.initBoard(t);
			that.$parent.layout();
		};
		for (i = 0; i < item.length; i += 1) {
			if (item[i] !== type) {
				child = svgUtil.draw({typ: "Button", value: item[i], y: 8, x: 2, height: 28, width: 60}, this);
				child.style.verticalAlign = "top";
				typ.util.bind(child, "mousedown", func);
				child.typ = item[i];
				board.toolitems.push(child);
			}
		}
		if (type === "HTML") {
			if (this.id) {
				func = function (e) {
					var t = e.currentTarget.value;
					if (t === "Save") {
						that.$parent.SavePosition();
					} else if (t === "Load") {
						that.$parent.LoadPosition();
					}
				};
				item = {typ: "Dropdown", x: 2, y: 8, width: 120, elements: ["Save", "Load"], activText: "Localstorage", action: func};
				board.toolitems.push(svgUtil.draw(item, this));
			}
		}
		child = {typ: "Dropdown", x: 66, y: 8, minheight: 28, maxheight: 28, width: 80, elements: list, activText: "Save", action: function (e) {removeToolItems(); that.$parent.SaveAs(e.currentTarget.value); }};
		board.toolitems.push(svgUtil.draw(child, this));
		this.options.minWidth = child.x + child.width;
		child = board.toolitems[board.toolitems.length - 1].choicebox;
		child = child.children[child.children.length - 1];
		this.options.minHeight = child.height.baseVal.value + child.y.baseVal.value + 10;
		svgUtil.addStyle(board, "SVGBtn");
	};
	typ.GraphModel.prototype.drawSVG = function () {
		var g = typ.util.create({tag: "g", model: this}), that = this, width, height, item, root;
		root = this.getRoot();
		if (this.status === "close") {
			width = svgUtil.getWidth(this.model, this.minid || this.id) + 30;
			height = 40;
			svgUtil.addChild(g, {tag: "text", $font: true, "text-anchor": "left", "x": (this.x + 2), "y": this.y + 12, value: this.minid || this.id });
		} else {
			this.left = this.top = 30;
			this.$gui = g;

			width = typ.util.getValue(this.$gui.style.width);
			height = typ.util.getValue(this.$gui.style.height);
			if (this.style && this.style.toLowerCase() === "nac") {
				svgUtil.addChildNode(g, svgUtil.createGroup(this, this.lib.drawStop(this)));
			}
		}
		svgUtil.addChild(g, {tag: "rect", "width": width, "height": height, "fill": "none", "strokeWidth": "1px", "stroke": svgUtil.getColor(this.style, "#CCC"), "x": this.getX(), "y": this.getY(), "class": "draggable"});
		if (width > 0 && width !== this.width) {this.width = width; }
		if (this.status === "close") {
			// Open Button
			item = svgUtil.createGroup(this, svgUtil.drawMax({x: (this.x + width - 20), y: this.y}));
			this.height = height;
		} else {
			item = svgUtil.createGroup(this, svgUtil.drawMin({x: (this.x + width - 20), y: this.y}));
		}
		item.setAttribute("class", "hand");

		typ.util.bind(item, "mousedown", function (e) {
			var name;
			if (that.status === "close") {
				that.status = "open";
				g.model.redrawNode(that);
			} else {
				that.status = "close";
				// try to cleanup
				for (name in that.nodes) {
					if (that.nodes.hasOwnProperty(name)) {
						that.nodes[name].$gui = null;
					}
				}
				that.model.redrawNode(that);
			}
			if (e.stopPropagation) {e.stopPropagation(); }
			if (e.cancelBubble !== null) {e.cancelBubble = true; }
		});
		g.appendChild(item);
		return g;
	};
	typ.GraphModel.prototype.drawHTML = function (drawer, draw) {
		var item = typ.util.create({tag: "div", model: this}, this);
		drawer.setPos(item, this.x, this.y);
		if (this.typ === "classdiagram") {
			item.className = "classdiagram";
		} else if (this.typ === "objectdiagram") {
			item.className = "objectdiagram";
		} else if (this.model.typ.toLowerCase() === "objectdiagram") {
			item.className = "objectElement";
		} else {
			item.className = "classElement";
		}
		this.left = this.top = 30;
		this.$gui = item;
		if (draw) {
			this.model.draw(this);
			item.style.borderColor = "red";
			if (this.style && this.style.toLowerCase() === "nac") {
				item.appendChild(this.symbolLib.draw(null, {typ: "stop", x: 0, y: 0}));
			}
		} else {
			this.model.layout(0, 0, this);
		}
		drawer.setSize(item, this.$gui.style.width, this.$gui.style.height);
		return item;
	};
	//				######################################################### Graph #########################################################	
	typ.Graph = function (json, options) {
		this.x = this.y = this.width = this.height = 0;
		json = json || {};
		json.top = json.top || 50;
		json.left = json.left || 10;
		this.model = new typ.GraphModel(json, options, this);
		this.layouts = [{name: "dagre", value: new DagreLayout()}];
		this.initLayouts();
		this.loader = new Loader(this);
		this.lib = new typ.SymbolLibary();
		this.init = false;
	};
	typ.Graph.prototype = new typ.GraphNode();
	typ.Graph.prototype.createdElement = function () {};
	typ.Graph.prototype.initOption = function () {
		if (this.init) {
			return;
		}
		this.init = true;
		var i, layout = this.layouts[0];
		for (i = 0; i < this.layouts.length; i += 1) {
			if (this.layouts[i].name === this.model.options.layout.name.toLowerCase()) {
				layout = this.layouts[i];
				break;
			}
		}
		this.layouter = layout.value;
		if (this.model.options.canvasid) {
			this.root = document.getElementById(this.model.options.canvasid);
		}
		if (this.root) {
			if (this.model.options.clearCanvas) {
				for (i = this.root.children.length - 1; i >= 0; i -= 1) {
					this.root.removeChild(this.root.children[i]);
				}
			}
		} else {
			this.root = document.createElement("div");
			this.root.setAttribute("class", "Board");
			if (this.model.options.canvasid) {
				this.root.id = this.model.options.canvasid;
			}
			document.body.appendChild(this.root);
		}
		this.initBoard();
	};
	typ.Graph.prototype.initBoard = function (newTyp) {
		if (!newTyp) {
			newTyp = this.getTyp();
		} else {
			this.model.options.display = newTyp;
			newTyp = newTyp.toLowerCase();
		}
		this.clearBoard();
		this.board = this.model.$gui = this.model.getBoard(newTyp);
		this.model.drawHeader();
		this.DragAndDrop = new DragAndDrop(this);
	};
	typ.Graph.prototype.getTyp = function () {return this.model.options.display.toLowerCase(); };
	typ.Graph.prototype.addOption = function (typ, value) {
		this.model.options[typ] = value;
		this.init = false;
	};
	typ.Graph.prototype.initLayouts = function () {};
	typ.Graph.prototype.initInfo = function (edge, info) {
		if (!this.model.options.CardinalityInfo && !this.model.options.propertyinfo) {
			return null;
		}
		var infoTxt = info.getInfo();
		if (infoTxt.length > 0) {
			typ.util.sizeOf(infoTxt, this, info);
		}
		return infoTxt;
	};
	typ.Graph.prototype.clearBoard = function (onlyElements) {
		var i, n;
		if (this.board) {
			this.clearLines(this.model);
			for (i in this.model.nodes) {
				if (!this.model.nodes.hasOwnProperty(i)) {
					continue;
				}
				n = this.model.nodes[i];
				if (this.board.children.length > 0) {
					n.removeFromBoard(this.board);
				}
				n.$RIGHT = n.$LEFT = n.$UP = n.$DOWN = 0;
			}
			if (!onlyElements) {
				this.root.removeChild(this.board);
			}
		}
		if (!onlyElements && this.drawer) {
			this.drawer.clearBoard();
		}
	};
	typ.Graph.prototype.addNode = function (node) {return this.model.addNode(node); };
	typ.Graph.prototype.addEdge = function (source, target) {return this.model.addEdge(source, target); };
	typ.Graph.prototype.removeNode = function (id) {return this.model.removeNode(id); };
	typ.Graph.prototype.calcLines = function (model) {
		model = model || this.model;
		model.calcLines(this.drawer);
	};
	typ.Graph.prototype.drawLines = function (model) {
		this.clearLines(model);
		var i, e, startShow, endShow, items = [], id;
		for (i = 0; i < model.edges.length; i += 1) {
			e = model.edges[i];
			startShow = !e.$sNode.isClosed();
			endShow = !e.$tNode.isClosed();
			if (startShow && endShow) {
				e.draw();
			} else if ((startShow && !endShow) || (!startShow && endShow)) {
				id = e.$sNode.getShowed().id + "-" + e.$tNode.getShowed().id;
				if (items.indexOf(id) < 0) {
					items.push(id);
					e.draw();
				}
			}
		}
	};
	typ.Graph.prototype.clearLines = function (model) {
		var i;
		for (i = 0; i < model.edges.length; i += 1) {
			model.edges[i].removeFromBoard(this.board);
		}
	};
	typ.Graph.prototype.resize = function (model) {
		if(!model || !model.minSize) {
			return;
		}
		var nodes, n, max, i, min = new Pos();
		max = new Pos(model.minSize.x, model.minSize.y);
		nodes = model.nodes;
		for (i in nodes) {
			if (!nodes.hasOwnProperty(i) || typeof (nodes[i]) === "function") {
				continue;
			}
			n = nodes[i];
			this.moveToRaster(n);
			typ.util.MinMax(n, min, max);
		}
		this.calcLines(model);
		for (i = 0; i < model.edges.length; i += 1) {
			n = model.edges[i];
			typ.util.MinMax(n.source, min, max);
			typ.util.MinMax(n.target, min, max);
		}
		model.height = max.y;
		model.width = max.x;
		typ.util.setSize(model.$gui, max.x, max.y);
		if (model.options.raster) {
			this.drawRaster();
		}
		this.drawLines(model);
		return max;
	};
	typ.Graph.prototype.drawRaster = function () {
		var width, height, line, i;
		while (this.board.rasterElements.length > 0) {
			this.board.removeChild(this.board.rasterElements.pop());
		}
		width = this.board.style.width.replace("px", "");
		height = this.board.style.height.replace("px", "");
		for (i = 10; i < width; i += 10) {
			line = this.drawer.getLine(i, 0, i, height, null, "#ccc");
			line.setAttribute("className", "lineRaster");
			this.board.rasterElements.push(line);
			this.board.appendChild(line);
		}
		for (i = 10; i < height; i += 10) {
			line = this.drawer.getLine(0, i, width, i, null, "#ccc");
			line.setAttribute("className", "lineRaster");
			this.board.rasterElements.push(line);
			this.board.appendChild(line);
		}
	};
	typ.Graph.prototype.draw = function (model, width, height) {
		var i, n, nodes = model.nodes, typ;
		typ = this.getTyp();
		if (model.options.addBorder) {
			for (i in nodes) {
				if (!nodes.hasOwnProperty(i) || typeof (nodes[i]) === "function") {
					continue;
				}
				n = nodes[i];
				if (model.left > 0 || model.top > 0) {
					n.x += model.left;
					n.y += model.top;
				}
			}
			model.options.addBorder = false;
		}
		model.minSize = new Pos(width || model.options.minWidth || 0, height || model.options.minHeight || 0);
		if (this.loader.abort && this.loader.images.length > 0) {
			return;
		}
		this.resize(model);
		for (i in nodes) {
			if (!nodes.hasOwnProperty(i)) {
				continue;
			}
			if (typeof (nodes[i]) === "function") {
				continue;
			}
			n = nodes[i];
			n.$gui = n.draw(typ, true);
			if (typ === "svg") {
				//svgUtil.addStyle(board, "ClazzHeader");
				svgUtil.addStyles(this.board, n.$gui);
			}
			this.DragAndDrop.add(n.$gui);
			model.$gui.appendChild(n.$gui);
		}
	};
	typ.Graph.prototype.moveToRaster = function (node) {
		if (this.model.options.raster) {
			node.x = parseInt(node.x / 10, 10) * 10;
			node.y = parseInt(node.y / 10, 10) * 10;
			if (node.$gui) {
				this.drawer.setPos(node.$gui, node.x, node.y);
			}
		}
	};
	typ.Graph.prototype.initGraph = function (model) {
		var i, n, isDiag, html, e;
		model.validateModel();
		for (i in model.nodes) {
			if (!model.nodes.hasOwnProperty(i)) {
				continue;
			}
			if (typeof (model.nodes[i]) === "function") {
				continue;
			}
			n = model.nodes[i];
			isDiag = n.typ.indexOf("diagram", n.typ.length - 7) !== -1;
			if (isDiag) {
				this.initGraph(n);
			}
			html = n.draw(model.options.display);
			if (html) {
				typ.util.sizeOf(html, this, n);
				if (isDiag) {
					n.$center = new Pos(n.x + (n.width / 2), n.y + (n.height / 2));
				}
			}
		}
		for (i = 0; i < model.edges.length; i += 1) {
			e = model.edges[i];
			this.initInfo(e, e.source);
			this.initInfo(e, e.target);
		}
	};
	typ.Graph.prototype.layout = function (minwidth, minHeight, model) {
		this.initOption();
		if (!model) {
			model = this.model;
		}
		this.initGraph(model);
		if (this.loader.images.length < 1) {
			this.layouter.layout(this, model, minwidth || 0, minHeight || 0);
		} else {
			this.loader.width = minwidth;
			this.loader.height = minHeight;
		}
	};
	typ.Graph.prototype.ExportPDF = function () {
		var converter, pdf = new jsPDF('l', 'px', [this.model.width, this.model.height]);
		converter = new svgConverter(this.board, pdf, {removeInvalid: false});
		pdf.save('Download.pdf');
	};
	typ.Graph.prototype.ExportEPS = function () {
		var converter, doc = new svgConverter.jsEPS({inverting: true});
		converter = new svgConverter(this.board, doc, {removeInvalid: false});
		doc.save();
	};
	typ.Graph.prototype.ExportPNG = function () {
		var canvas, context, a, image = new Image();
		image.src = 'data:image/svg+xml;base64,' + this.utf8$to$b64(this.serializeXmlNode(this.board));
		image.onload = function () {
			canvas = document.createElement('canvas');
			canvas.width = image.width;
			canvas.height = image.height;
			context = canvas.getContext('2d');
			context.drawImage(image, 0, 0);
			a = document.createElement('a');
			a.download = "download.png";
			a.href = canvas.toDataURL('image/png');
			a.click();
		};
	};
	typ.Graph.prototype.ExportHTML = function () {
		var data, body, script, json = this.model.toJson();
		body = document.create("body");
		script = document.create("script");
		body.appendChild(script);
		script.innerHTML = "new Graph(" + JSON.stringify(json, null, "\t") + ").layout();";
		data = "<html><head>" + document.head.innerHTML.trim() + "</head>" + body.toString() + "</html>";
		this.Save("text/json", data, "download.html");
	};
	typ.Graph.prototype.SaveAs = function (typ) {
		typ = typ.toLowerCase();
		if (typ === "svg") {
			this.Save("image/svg+xml", this.serializeXmlNode(this.board), "download.svg");
		} else if (typ === "html") {
			this.ExportHTML();
		} else if (typ === "png") {
			this.ExportPNG();
		} else if (typ === "pdf") {
			this.ExportPDF();
		} else if (typ === "eps") {
			this.ExportEPS();
		}
	};
	typ.Graph.prototype.SavePosition = function () {
		var data = [], node, id;
		for (id in this.model.nodes) {
			if (!this.model.nodes.hasOwnProperty(id)) {
				continue;
			}
			node = this.model.nodes[id];
			data.push({id: node.id, x: node.x, y: node.y});
		}
		if (window.localStorage && this.model.id) {
			window.localStorage.setItem(this.model.id, JSON.stringify(data));
		}
	};
	typ.Graph.prototype.LoadPosition = function () {
		if (this.model.id && window.localStorage) {
			var node, id, data = window.localStorage.getItem(this.model.id);
			if (data) {
				data = JSON.parse(data);
				for (id in data) {
					if (!data.hasOwnProperty(id)) {
						continue;
					}
					node = data[id];
					if (this.model.nodes[node.id]) {
						this.model.nodes[node.id].x = node.x;
						this.model.nodes[node.id].y = node.y;
					}
				}
				this.clearBoard(true);
				this.draw(this.model);
			}
		}
	};
	typ.Graph.prototype.Save = function (typ, data, name) {
		var a = document.createElement("a");
		a.href = window.URL.createObjectURL(new Blob([data], {type: typ}));
		a.download = name;
		a.click();
	};
	//				######################################################### DRAG AND DROP #########################################################
	DragAndDrop = function (parent) {
		this.parent = parent;
		this.objDrag = null;
		this.mouse = new Pos();
		this.offset = new Pos();
		this.startObj = new Pos();
		var that = this;
		parent.root.appendChild(parent.board);
		typ.util.bind(parent.board, "mousemove", function (e) {that.doDrag(e); });
		typ.util.bind(parent.board, "mouseup", function (e) {that.stopDrag(e); });
		typ.util.bind(parent.board, "mouseout", function (e) {that.stopDrag(e); });
	};
	DragAndDrop.prototype.add = function (element) {
		var that = this;
		typ.util.bind(element, "mousedown", function (e) {that.startDrag(e); });
	};
	DragAndDrop.prototype.setSelectable = function (node, value) {
		if (node.nodeType === 1) {
			if (value) {
				node.setAttribute("unselectable", value);
			} else {
				node.removeAttribute("unselectable");
			}
		}
		var child = node.firstChild;
		while (child) {
			this.setSelectable(child, value);
			child = child.nextSibling;
		}
	};
	DragAndDrop.prototype.getDragNode = function (node) {
		if (node.model) {
			if (!node.model.$isdraggable) {
				return null;
			}
			return node;
		}
		if (node.parentElement.model) {
			if (!node.parentElement.model.$isdraggable) {
				return null;
			}
			return node.parentElement;
		}
		return null;
	};
	DragAndDrop.prototype.startDrag = function (event) {
		var graph, i, n = this.getDragNode(event.currentTarget);
		if (!n) {
			return;
		}
		if (this.objDrag) {
			return;
		}
		this.objDrag = n;
		graph = this.objDrag.parentElement;
		if (graph) {
			for (i = 0; i < graph.children.length; i += 1) {
				this.setSelectable(graph.children[i], "on");
			}
		}
		this.offset.x = this.isIE ? window.event.clientX : event.pageX;
		this.offset.y = this.isIE ? window.event.clientY : event.pageY;
		this.startObj.x = this.objDrag.model.x;
		this.startObj.y = this.objDrag.model.y;
	};
	DragAndDrop.prototype.doDrag = function (event) {
		var x, y;
		this.mouse.x = this.isIE ? window.event.clientX : event.pageX;
		this.mouse.y = this.isIE ? window.event.clientY : event.pageY;

		if (this.objDrag !== null) {
			x = (this.mouse.x - this.offset.x) + this.startObj.x;
			y = (this.mouse.y - this.offset.y) + this.startObj.y;

			if (this.parent.model.options.display === "svg") {
				x = x - this.startObj.x;
				y = y - this.startObj.y;
				this.objDrag.setAttribute('transform', "translate(" + x + " " + y + ")");
			} else {
				this.drawer.setPos(this.objDrag, x, y);
				if (this.objDrag.model) {
					this.objDrag.model.x = x;
					this.objDrag.model.y = y;
					this.objDrag.model.$parent.resize(this.model);
				}
			}
		}
	};
	DragAndDrop.prototype.stopDrag = function (event) {
		var x, y, z, item, entry, parent, pos;
		if (!this.objDrag) {
			return;
		}
		if (!(event.type === "mouseup" || event.type === "mouseout") && !event.currentTarget.$isdraggable) {
			return;
		}
		if (event.type === "mouseout") {
			x = this.isIE ? window.event.clientX : event.pageX;
			y = this.isIE ? window.event.clientY : event.pageY;
			if (x < this.parent.board.offsetWidth && y <this.parent.board.offsetHeight) {
				return;
			}
		}
		item = this.objDrag;
		this.objDrag = null;
		entry = item.parentElement;
		if (entry) {
			for (z = 0; z < entry.children.length; z += 1) {
				this.setSelectable(entry.children[z], null);
			}
		}
		parent = item.parentElement;
		if (item.model) {
			if (item.model.$parent.options.display === "svg") {
				if (item.getAttributeNS("", "transform")) {
					z = item.getAttributeNS("", "transform");
					if (z.substring(0, 6) !== "rotate") {
						pos = z.slice(10, -1).split(' ');
						item.model.x = item.model.x + Number(pos[0]);
						item.model.y = item.model.y + Number(pos[1]);
					}
				}
				item.model.$center = new Pos(item.model.x + (item.model.width / 2), item.model.y + (item.model.height / 2));
				parent.removeChild(item);
				if (item.model.board) {
					item.model.board = null;
				}
			} else {
				this.board.removeChild(item);
			}

			if (item.model.typ === "Info") {
				item.model.custom = true;
				item.model.$edge.removeElement(item);
				entry = item.model.$edge.getInfo(item.model);
				item.model.$edge.drawText(this.board, this.drawer, entry, item.model);
			} else {
				item.model.$gui = item.model.draw();
				if (item.model.$gui) {
					parent.appendChild(item.model.$gui);
				}
				entry = item.model.getEdges();
				for (z = 0; z < entry.length; z += 1) {
					entry[z].source.custom = false;
					entry[z].target.custom = false;
				}
			}
			parent = item.model.$parent;
			entry = parent;
			while (entry) {
				this.parent.resize(entry);
				entry = entry.$parent;
			}
			if (parent.$parent) {
				this.redrawNode(parent, true);
				this.parent.resize(this.model);
			} else {
				this.parent.resize(parent);
			}
		}
	};
	DragAndDrop.prototype.redrawNode = function (node, draw) {
		var infoTxt, parent = node.$gui.parentElement;
		parent.removeChild(node.$gui);
		if (node.board) {
			node.board = null;
		}
		if (node.typ === "Info") {
			infoTxt = node.edge.getInfo(node.node);
			node.edge.drawText(this.board, this.drawer, infoTxt, node.node);
		} else {
			node.$gui = node.draw();
			if (node.$gui) {
				parent.appendChild(node.$gui);
			}
		}
		node.$center = new Pos(node.x + (node.width / 2), node.y + (node.height / 2));
	};
	//				######################################################### GraphLayout-Dagre #########################################################
	DagreLayout = function () {};
	DagreLayout.prototype.layout = function (graph, node, width, height) {
		var layoutNode, i, n, nodes, g, graphOptions = typ.util.copy({directed: false}, node.options.layout);
		g = new dagre.graphlib.Graph(graphOptions);
		g.setGraph(graphOptions);
		g.setDefaultEdgeLabel(function () { return {}; });
		nodes = node.nodes;
		for (i in nodes) {
			if (!nodes.hasOwnProperty(i) || typeof (nodes[i]) === "function") {
				continue;
			}
			n = nodes[i];
			g.setNode(n.id, {label: n.id, width: n.width, height: n.height, x: n.x, y: n.y});
		}
		for (i = 0; i < node.edges.length; i += 1) {
			n = node.edges[i];
			g.setEdge(this.getNodeId(n.$sNode), this.getNodeId(n.$tNode));
		}
		dagre.layout(g);
		// Set the layouting back
		for (i in nodes) {
			if (!nodes.hasOwnProperty(i) || typeof (nodes[i]) === "function") {
				continue;
			}
			n = nodes[i];
			layoutNode = g.node(n.id);
			if (n.x < 1 && n.y < 1) {
				n.x = Math.round(layoutNode.x - (n.width / 2));
				n.y = Math.round(layoutNode.y - (n.height / 2));
			}
		}
		graph.draw(node, width, height);
	};
	DagreLayout.prototype.getNodeId = function (node) {
		if (node.$parent) {
			return this.getNodeId(node.$parent, node) || node.id;
		}
		return node.id;
	};
	//				######################################################### Info #########################################################
	Info = function (info, parent, edge) {
		this.typ = "Info";
		if (typeof (info) === "string") {
			this.id = info;
		} else {
			if (info.property) {this.property = info.property; }
			if (info.cardinality) {this.cardinality = info.cardinality; }
			this.id = info.id;
		}
		this.x = this.y = this.width = this.height = 0;
		this.$center = new Pos();
		this.custom = false;
		this.$parent = parent;
		this.$edge = edge;
		this.$isdraggable = true;
	};
	Info.prototype.getRoot = function () {if (this.$parent) {return this.$parent.getRoot(); } return this; };
	Info.prototype.getX = function () {return this.x; };
	Info.prototype.getY = function () {return this.y; };
	Info.prototype.drawSVG = function (text) {
		if (!text) {
			text = this.getInfo();
		}
		if (text.length < 1) {
			return null;
		}
		var child, group, i, items = text.split("\n");
		if (items.length > 1) {
			group = typ.util.create({tag: "g", "class": "draggable", rotate: this.$angle, model: this});
			for (i = 0; i < items.length; i += 1) {
				child = typ.util.create({tag: "text", $font: true, "text-anchor": "left", "x": this.x, "y": this.y + (this.height * i)});
				child.appendChild(document.createTextNode(items[i]));
				group.appendChild(child);
			}
			this.model.createdElement(group, "info", this);
			return group;
		}
		group = typ.util.create({tag: "text", "#$font": true, "text-anchor": "left", "x": this.x, "y": this.y, value: text, "id": this.id, "class": "draggable InfoText", rotate: this.$angle, model: this});
		this.getRoot().createdElement(group, "info", this);
		return group;
	};
	Info.prototype.drawHTML = function (text) {
		var info = typ.util.create({tag: "div", $font: true, model: this, "class": "EdgeInfo", value: text});
		if (this.$angle !== 0) {
			info.style.transform = "rotate(" + this.$angle + "deg)";
			info.style.msTransform = info.style.MozTransform = info.style.WebkitTransform = info.style.OTransform = "rotate(" + this.$angle + "deg)";
		}
		this.setPos(info, this.x, this.y);
		this.getRoot().createdElement(info, "info", this);
		return info;
	};
	Info.prototype.getInfo = function () {
		var isProperty, isCardinality, infoTxt = "", graph =  this.$parent;
		isCardinality = graph.typ === "classdiagram" && graph.options.CardinalityInfo;
		isProperty = graph.options.propertyinfo;

		if (isProperty && this.property) {
			infoTxt = this.property;
		}
		if (isCardinality && this.cardinality) {
			if (infoTxt.length > 0) {
				infoTxt += "\n";
			}
			if (this.cardinality.toLowerCase() === "one") {
				infoTxt += "0..1";
			} else if (this.cardinality.toLowerCase() === "many") {
				infoTxt += "0..*";
			}
		}
		if (this.edge && this.edge.counter > 0) {
			infoTxt += " (" + this.edge.counter + ")";
		}
		return infoTxt;
	};

	//				######################################################### LINES #########################################################
	//				######################################################### Edge #########################################################
	Edge = function () {
		this.$path = [];
		this.$sNode = null;
		this.$tNode = null;
		this.$m = 0;
		this.$n = 0;
		this.$lineStyle = Line.Format.SOLID;
		this.typ = "EDGE";
		this.counter = 0;
	};
	Edge.Layout = { DIAG : 1, RECT : 0 };
	Edge.Position = {UP: "UP", LEFT: "LEFT", RIGHT: "RIGHT", DOWN: "DOWN"};
	Edge.prototype.getRoot = function () {if (this.$parent) {return this.$parent.getRoot(); } return this; };
	Edge.prototype.set = function (id, value) {if (value) {this[id] = value; } };
	Edge.prototype.removeFromBoard = function (board) {
		if (this.$gui) {
			board.removeChild(this.$gui);
			this.$gui = null;
		}
		if (this.$labels) {
			while(this.$labels.length > 0) {
				board.removeChild(this.$labels.pop());	
			}
		}
	};
	// TODO
	// many Edges SOME DOWN AND SOME RIGHT OR LEFT
	// INFOTEXT DONT SHOW IF NO PLACE
	// INFOTEXT CALCULATE POSITION
	Edge.prototype.calc = function () {
		var result, options, linetyp, source, target, sourcePos, targetPos, divisor, startNode, endNode;
		startNode = this.$sNode.getShowed();
		endNode = this.$tNode.getShowed();
		startNode.$center = new Pos(startNode.getX() + (startNode.width / 2), startNode.getY() + (startNode.height / 2));
		endNode.$center = new Pos(endNode.getX() + (endNode.width / 2), endNode.getY() + (endNode.height / 2));
		divisor = (endNode.$center.x - startNode.$center.x);
		this.$path = [];
		source = this.getTarget(startNode, startNode);
		target = this.getTarget(endNode, endNode);
		if (divisor === 0) {
			if (startNode === endNode) {
				/* OwnAssoc */
				return false;
			}
			// Must be UP_DOWN or DOWN_UP
			if (startNode.$center.y < endNode.$center.y) {
				// UP_DOWN
				sourcePos = this.getCenterPosition(source, Edge.Position.DOWN);
				targetPos = this.getCenterPosition(target, Edge.Position.UP);
			} else {
				sourcePos = this.getCenterPosition(source, Edge.Position.UP);
				targetPos = this.getCenterPosition(target, Edge.Position.DOWN);
			}
		} else {
			// add switch from option or model
			linetyp = this.linetyp;
			if (!linetyp) {
				options = this.$parent.options;
				if (options) {
					linetyp = options.linetyp;
				}
			}
			result = false;
			if (linetyp === "square") {
				result = this.calcSquareLine();
			}
			if (!result) {
				this.$m = (target.$center.y - source.$center.y) / divisor;
				this.$n = source.$center.y - (source.$center.x * this.$m);
				sourcePos = this.getPosition(this.$m, this.$n, source, target.$center);
				targetPos = this.getPosition(this.$m, this.$n, target, sourcePos);
			}
		}
		if (sourcePos && targetPos) {
			this.calcInfoPos(sourcePos, source, this.source);
			this.calcInfoPos(targetPos, target, this.target);
			source["$" + sourcePos.$id] += 1;
			target["$" + targetPos.$id] += 1;
			this.$path.push(new Line(sourcePos, targetPos, this.$lineStyle, this.style));
			if (this.info) {
				this.info.x = (sourcePos.x + targetPos.x) / 2;
				this.info.y = (sourcePos.y + targetPos.y) / 2;
			}
		}
		return true;
	};
	Edge.prototype.addLine = function (x1, y1, x2, y2) {
		var start, end;
		if (!x2 && !y2 && this.$path.length > 0) {
			start = this.$path[this.$path.length - 1].target;
			end = new Pos(x1, y1);
		} else {
			start = new Pos(x1, y1);
			end = new Pos(x2, y2);
		}
		this.$path.push(new Line(start, end, this.$lineStyle, this.style));
	};
	Edge.prototype.addLineTo = function (x1, y1, x2, y2) {
		var start, end;
		if (!x2 && !y2 && this.$path.length > 0) {
			start = this.$path[this.$path.length - 1].target;
			end = new Pos(start.x + x1, start.y + y1);
		} else {
			start = new Pos(x1, y1);
			end = new Pos(start.x + x2, start.y + y2);
		}
		this.$path.push(new Line(start, end, this.$lineStyle, this.style));
	};
	Edge.prototype.calcSquareLine = function () {
		//	1. Case		/------\
		//				|...T...|
		//				\-------/
		//			|---------|
		//			|
		//		/-------\
		//		|...S...|
		//		\-------/
		if (this.$sNode.y - 40 > this.$tNode.y + this.$tNode.height) { // oberseite von source and unterseite von target
			this.addLineTo(this.$sNode.x + this.$sNode.width / 2, this.$sNode.y, 0, -20);
			this.addLine(this.$tNode.x + this.$tNode.width / 2, this.$tNode.y + this.$tNode.height + 20);
			this.addLineTo(0, -20);
			return true;
		}
		if (this.$tNode.y - 40 > this.$sNode.y + this.$sNode.height) { // oberseite von source and unterseite von target
			// fall 1 nur andersherum
			this.addLineTo(this.$sNode.x + this.$sNode.width / 2, this.$sNode.y + this.$sNode.height, 0, +20);
			this.addLine(this.$tNode.x + this.$tNode.width / 2, this.$tNode.y - 20);
			this.addLineTo(0, 20);
			return true;
		}
		//3. fall ,falls s (source) komplett unter t (target) ist
		// beide oberseiten
		//	3. Case
		//			 |--------
		//			/---\	 |
		//			| T |	/---\
		//			\---/	| S |
		//					-----
		// or
		//			-------|
		//			|	 /---\
		//		/----\	 | T |
		//		| S	 |	 \---/
		//		------
		//
		this.addLineTo(this.$sNode.x + this.$sNode.width / 2, this.$sNode.y, 0, -20);
		this.addLine(this.$tNode.x + this.$tNode.width / 2, this.$tNode.y - 20);
		this.addLineTo(0, 20);
		return true;
	};
	Edge.prototype.calcOffset = function () {
		var i, z, min = new Pos(999999999, 999999999), max = new Pos(0, 0), item, svg, value, x, y;
		for (i = 0; i < this.$path.length; i += 1) {
			item = this.$path[i];
			if (item instanceof Line) {
				typ.util.Range(min, max, item.source.x, item.source.y);
				typ.util.Range(min, max, item.target.x, item.target.y);
			} else if (item instanceof Path) {
				value = document.createElement('div');
				svg = typ.util.create({tag: "svg"});
				svg.appendChild(this.getPath(item.path, item.fill, item.close, item.angle));
				value = svg.childNodes[0];
				x = y = 0;
				for (z = 0; z < value.pathSegList.length; z += 1) {
					item = value.pathSegList[z];
					switch (item.pathSegType) {
					case SVGPathSeg.PATHSEG_MOVETO_ABS:
					case SVGPathSeg.PATHSEG_LINETO_ABS:
					case SVGPathSeg.PATHSEG_ARC_ABS:
					case SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS:
						x = item.x;
						y = item.y;
						break;
					case SVGPathSeg.PATHSEG_MOVETO_REL:
					case SVGPathSeg.PATHSEG_LINETO_REL:
					case SVGPathSeg.PATHSEG_CURVETO_CUBIC_REL:
					case SVGPathSeg.PATHSEG_ARC_REL:
						x = x + item.x;
						y = y + item.y;
						break;
					}
					typ.util.Range(min, max, x, y);
				}
			}
		}
		return {x: min.x, y: min.y, width: max.x - min.x, height: max.y - min.y};
	};
	Edge.prototype.draw = function () {
		var i, style, angle, item, infoTxt, offset;
		offset = this.calcOffset();
		if (this.getRoot().getTyp() === "svg") {
			this.board = this.$gui = typ.util.create({tag: "g"});
			this.getRoot().board.appendChild(this.$gui);
		} else {
			this.$gui = typ.util.create({tag: "svg", style: {position: "absolute"}});
			this.board = typ.util.create({tag: "g", transform: "translate(-" + offset.x + ", -" + offset.y + ")"});
			this.$gui.appendChild(this.board);
			this.getRoot().board.appendChild(this.$gui);
		}
		typ.util.setPos(this.$gui, offset.x, offset.y);
		typ.util.setSize(this.$gui, offset.x, offset.y);
		for (i = 0; i < this.$path.length; i += 1) {
			item = this.$path[i];
			if (item instanceof Line) {
				style = item.style || this.style;
				this.board.appendChild(this.getLine(item.source.x, item.source.y, item.target.x, item.target.y, item.line || this.$lineStyle, style));
			} else if (item instanceof Path) {
				this.board.appendChild(this.getPath(item.path, item.fill, item.close, item.angle));
			}
		}
		this.drawSourceText(style);
		if (this.info) {
			infoTxt = this.getInfo(this.info);
			angle = this.getText(infoTxt, this.info, style);
			this.addElement(this.$gui, new typ.SymbolLibary().create({typ: "Arrow", x: this.info.x, y: this.info.y, rotate: angle}));
		}
		this.drawTargetText(style);
	};
	Edge.prototype.getLine = function (x1, y1, x2, y2, lineStyle, style) {
		var line = typ.util.create({tag: "line", 'x1': x1, 'y1': y1, 'x2': x2, 'y2': y2, "stroke": svgUtil.getColor(style)});
		if (lineStyle && lineStyle.toLowerCase() === "dotted") {
			line.setAttribute("stroke-miterlimit", "4");
			line.setAttribute("stroke-dasharray", "1,1");
		}
		return line;
	};
	Edge.prototype.getPath = function (path, fill, close, angle) {
		var i, d = "M" + path[0].x + " " + path[0].y;
		for (i = 1; i < path.length; i += 1) {
			d = d + "L " + path[i].x + " " + path[i].y;
		}
		if (close) {
			d = d + " Z";
		}
		return typ.util.create({tag: "path", "d": d, "fill": fill, stroke: "#000", "stroke-width": "1px"});
	};
	Edge.prototype.drawText = function (info, style) {
		if (this.$path.length < 1) {
			return;
		}
		var options, angle, p, item;
		p = this.$path[this.$path.length - 1];
		options = this.getRoot().model.options;
		if (options.rotatetext) {
			info.$angle = Math.atan((p.source.y - p.target.y) / (p.source.x - p.target.x)) * 60;
		}
		if (this.getRoot().getTyp() === "svg") {
			item = info.drawSVG();
		} else {
			item = info.drawHTML();
		}
		if (!this.$labels) {
			this.$labels = [];
		}
		if (item) {
			this.$labels.push(item);
			this.getRoot().board.appendChild(item);
		}
		return angle;
	};
	Edge.prototype.drawSourceText = function (style) {
		this.drawText(this.source, style);
	};
	Edge.prototype.drawTargetText = function (style) {
		this.drawText(this.target, style);
	};
	Edge.prototype.endPos = function () {return this.$path[this.$path.length - 1]; };
	Edge.prototype.edgePosition = function () {
		var pos = 0, i;
		for (i = 0; i < this.$sNode.$edges.length; i += 1) {
			if (this.$sNode.$edges[i] === this) {
				return pos;
			}
			if (this.$sNode.$edges[i].$tNode === this.$tNode) {
				pos += 1;
			}
		}
		return pos;
	};
	Edge.prototype.getTarget = function (node, startNode) {
		if (node instanceof typ.GraphModel) {
			if (node.status === "close") {
				return node;
			}
			return startNode;
		}
		return this.getTarget(node.$parent, startNode);
	};
	Edge.prototype.getCenterPosition = function (node, pos) {
		var offset = node["$" + pos];
		if (pos === Edge.Position.DOWN) {
			return new Pos(Math.min(node.$center.x + offset, node.x + node.width), (node.y + node.height), Edge.Position.DOWN);
		}
		if (pos === Edge.Position.UP) {
			return new Pos(Math.min(node.$center.x + offset, node.x + node.width), node.y, Edge.Position.UP);
		}
		if (pos === Edge.Position.LEFT) {
			return new Pos(node.x, Math.min(node.$center.y + offset, node.y + node.height), Edge.Position.LEFT);
		}
		if (pos === Edge.Position.RIGHT) {
			return new Pos(node.x + node.width, Math.min(node.$center.y + offset, node.y + node.height), Edge.Position.RIGHT);
		}
	};
	Edge.prototype.calcOwnEdge = function () {
		//this.source
		var sPos, tPos, offset = 20;
		this.$start = this.getFree(this.$sNode);
		if (this.$start.length > 0) {
			this.$end = this.getFreeOwn(this.$sNode, this.$start);
		} else {
			this.$start = Edge.Position.RIGHT;
			this.$end = Edge.Position.DOWN;
		}

		sPos = this.getCenterPosition(this.$sNode, this.$start);
		if (this.$start === Edge.Position.UP) {
			tPos = new Pos(sPos.x, sPos.y - offset);
		} else if (this.$start === Edge.Position.DOWN) {
			tPos = new Pos(sPos.x, sPos.y + offset);
		} else if (this.$start === Edge.Position.RIGHT) {
			tPos = new Pos(sPos.x + offset, sPos.y);
		} else if (this.$start === Edge.Position.LEFT) {
			tPos = new Pos(sPos.x - offset, sPos.y);
		}
		this.$path.push(new Line(sPos, tPos, this.$lineStyle));
		if (this.$end === Edge.Position.LEFT || this.$end === Edge.Position.RIGHT) {
			if (this.$start === Edge.Position.LEFT) {
				sPos = tPos;
				tPos = new Pos(sPos.x, this.$sNode.y - offset);
				this.$path.push(new Line(sPos, tPos, this.$lineStyle));
			} else if (this.$start === Edge.Position.RIGHT) {
				sPos = tPos;
				tPos = new Pos(sPos.x, this.$sNode.y + offset);
				this.$path.push(new Line(sPos, tPos, this.$lineStyle));
			}
			sPos = tPos;
			if (this.$end === Edge.Position.LEFT) {
				tPos = new Pos(this.$sNode.x - offset, sPos.y);
			} else {
				tPos = new Pos(this.$sNode.x + this.$sNode.width + offset, sPos.y);
			}
			this.$path.push(new Line(sPos, tPos, this.$lineStyle));
			sPos = tPos;
			tPos = new Pos(sPos.x, this.$sNode.$center.y);
			this.$path.push(new Line(sPos, tPos, this.$lineStyle));
			if (this.info) {
				this.info.x = (sPos.x + tPos.x) / 2;
				this.info.y = sPos.y;
			}
		} else if (this.$end === Edge.Position.UP || this.$end === Edge.Position.DOWN) {
			if (this.$start === Edge.Position.UP) {
				sPos = tPos;
				tPos = new Pos(this.$sNode.x + this.$sNode.width + offset, sPos.y);
				this.$path.push(new Line(sPos, tPos, this.$lineStyle));
			} else if (this.$start === Edge.Position.DOWN) {
				sPos = tPos;
				tPos = new Pos(this.$sNode.x - offset, sPos.y);
				this.$path.push(new Line(sPos, tPos, this.$lineStyle));
			}
			sPos = tPos;
			if (this.$end === Edge.Position.UP) {
				tPos = new Pos(sPos.x, this.$sNode.y - offset);
			} else {
				tPos = new Pos(sPos.x, this.$sNode.y + this.$sNode.height + offset);
			}
			this.$path.push(new Line(sPos, tPos, this.$lineStyle));
			sPos = tPos;
			tPos = new Pos(this.$sNode.$center.x, sPos.y);
			this.$path.push(new Line(sPos, tPos, this.$lineStyle));
			if (this.info) {
				this.info.x = sPos.x;
				this.info.y = (sPos.y + tPos.y) / 2;
			}
		}
		sPos = tPos;
		this.$path.push(new Line(sPos, this.getCenterPosition(this.$sNode, this.$end), this.$lineStyle));
	};
	Edge.prototype.getFree = function (node) {
		var i;
		for (i in Edge.Position) {
			if (!Edge.Position.hasOwnProperty(i)) {
				continue;
			}
			if (!node.hasOwnProperty("$" + i)) {
				continue;
			}
			if (node["$" + i] === 0) {
				node["$" + i] = 1;
				return i;
			}
		}
		return "";
	};
	Edge.prototype.getFreeOwn = function (node, start) {
		var id = 0, i, list = [Edge.Position.UP, Edge.Position.RIGHT, Edge.Position.DOWN, Edge.Position.LEFT, Edge.Position.UP, Edge.Position.RIGHT, Edge.Position.DOWN];
		for (i = 0; i < list.length; i += 1) {
			if (list[i] === start) {
				id = i;
				break;
			}
		}
		if (node["$" + list[id + 1]] === 0 || node["$" + list[id + 1]] < node["$" + list[id + 3]]) {
			node["$" + list[id + 1]] += 1;
			return list[id + 1];
		}
		node["$" + list[id + 3]] += 1;
		return list[id + 3];
	};
	Edge.prototype.calcInfoPos = function (linePos, item, info) {
		// Manuell move the InfoTag
		var newY, newX, spaceA = 20, spaceB = 0, step = 15;
		if (item.$parent.options && !item.$parent.options.rotatetext) {
			spaceA = 20;
			spaceB = 10;
		}
		if (info.custom) {
			return;
		}
		newY = linePos.y;
		newX = linePos.x;
		if (linePos.$id === Edge.Position.UP) {
			newY = newY - info.height - spaceA;
			if (this.$m !== 0) {
				newX = (newY - this.$n) / this.$m + spaceB + (item.$UP * step);
			}
		} else if (linePos.$id === Edge.Position.DOWN) {
			newY = newY + spaceA;
			if (this.$m !== 0) {
				newX = (newY - this.$n) / this.$m + spaceB + (item.$DOWN * step);
			}
		} else if (linePos.$id === Edge.Position.LEFT) {
			newX = newX - info.width - (item.$LEFT * step) - spaceA;
			if (this.$m !== 0) {
				newY = (this.$m * newX) + this.$n;
			}
		} else if (linePos.$id === Edge.Position.RIGHT) {
			newX += (item.$RIGHT * step) + spaceA;
			if (this.$m !== 0) {
				newY = (this.$m * newX) + this.$n;
			}
		}
		info.x = Math.round(newX);
		info.y = Math.round(newY);
	};
	Edge.prototype.getUDPosition = function (m, n, e, pos, step) {
		var x, y = e.getY();
		if (pos === Edge.Position.DOWN) {
			y += e.height;
		}
		x = (y - n) / m;
		if (step) {
			x += e["$" + pos] * step;
			if (x < e.getX()) {
				x = e.getX();
			} else if (x > (e.getX() + e.width)) {
				x = e.getX() + e.width;
			}
		}
		return new Pos(x, y, pos);
	};
	Edge.prototype.getLRPosition = function (m, n, e, pos, step) {
		var y, x = e.getX();
		if (pos === Edge.Position.RIGHT) {
			x += e.width;
		}
		y = m * x + n;
		if (step) {
			y += e["$" + pos] * step;
			if (y < e.getY()) {
				y = e.getY();
			} else if (y > (e.getY() + e.height)) {
				y = e.getY() + e.height;
			}
		}
		return new Pos(x, y, pos);
	};
	Edge.prototype.getPosition = function (m, n, entity, refCenter) {
		var t, pos = [], list, distance = [], min = 999999999, position, i, step = 15;
		list = [Edge.Position.LEFT, Edge.Position.RIGHT];
		for (i = 0; i < 2; i += 1) {
			t = this.getLRPosition(m, n, entity, list[i]);
			if (t.y >= entity.getY() && t.y <= (entity.getY() + entity.height)) {
				t.y += (entity["$" + list[i]] * step);
				if (t.y > (entity.getY() + entity.height)) {
					// Alternative
					t = this.getUDPosition(m, n, entity, Edge.Position.DOWN, step);
				}
				pos.push(t);
				distance.push(Math.sqrt((refCenter.x - t.x) * (refCenter.x - t.x) + (refCenter.y - t.y) * (refCenter.y - t.y)));
			}
		}
		list = [Edge.Position.UP, Edge.Position.DOWN];
		for (i = 0; i < 2; i += 1) {
			t = this.getUDPosition(m, n, entity, list[i]);
			if (t.x >= entity.getX() && t.x <= (entity.getX() + entity.width)) {
				t.x += (entity["$" + list[i]] * step);
				if (t.x > (entity.getX() + entity.width)) {
					// Alternative
					t = this.getLRPosition(m, n, entity, Edge.Position.RIGHT, step);
				}
				pos.push(t);
				distance.push(Math.sqrt((refCenter.x - t.x) * (refCenter.x - t.x) + (refCenter.y - t.y) * (refCenter.y - t.y)));
			}
		}
		for (i = 0; i < pos.length; i += 1) {
			if (distance[i] < min) {
				min = distance[i];
				position = pos[i];
			}
		}
		return position;
	};
	Edge.prototype.calcMoveLine = function (size, angle, move) {
		var lineangle, angle1, angle2, hCenter, startArrow, h;
		if (this.$path.length < 1) {
			return;
		}
		startArrow = this.endPos().source;
		this.$end = this.endPos().target;
		// calculate the angle of the line
		lineangle = Math.atan2(this.$end.y - startArrow.y, this.$end.x - startArrow.x);
		// h is the line length of a side of the arrow head
		h = Math.abs(size / Math.cos(angle));
		angle1 = lineangle + Math.PI + angle;
		hCenter = Math.abs((size / 2) / Math.cos(angle));

		this.$top = new Pos(this.$end.x + Math.cos(angle1) * h, this.$end.y + Math.sin(angle1) * h);
		this.$topCenter = new Pos(this.$end.x + Math.cos(angle1) * hCenter, this.$end.y + Math.sin(angle1) * hCenter);
		angle2 = lineangle + Math.PI - angle;
		this.$bot = new Pos(this.$end.x + Math.cos(angle2) * h, this.$end.y + Math.sin(angle2) * h);
		this.$botCenter = new Pos(this.$end.x + Math.cos(angle2) * hCenter, this.$end.y + Math.sin(angle2) * hCenter);
		if (move) {
			this.endPos().target = new Pos((this.$top.x + this.$bot.x) / 2, (this.$top.y + this.$bot.y) / 2);
		}
	};

	//				######################################################### Generalisation #########################################################
	Generalisation = function () { this.typ = "Generalisation"; };
	Generalisation.prototype = new Edge();
	Generalisation.prototype.calc = function (board, drawer) {
		if (!Edge.prototype.calc.call(this, board, drawer)) {
			return false;
		}
		this.calcMoveLine(16, 50, true);
		this.$path.push(new Line(new Pos(this.$top.x, this.$top.y), new Pos(this.$end.x, this.$end.y)));
		this.$path.push(new Line(new Pos(this.$bot.x, this.$bot.y), new Pos(this.$end.x, this.$end.y)));
		this.$path.push(new Line(new Pos(this.$top.x, this.$top.y), new Pos(this.$bot.x, this.$bot.y)));
		return true;
	};
	Generalisation.prototype.drawSourceText = function (style) {};
	Generalisation.prototype.drawTargetText = function (style) {};
	//				######################################################### Implements #########################################################
	Implements = function () { Edge.call(this); this.typ = "Implements"; this.$lineStyle = Line.Format.DOTTED; };
	Implements.prototype = new Generalisation();

	//				######################################################### Unidirectional #########################################################
	Unidirectional = function () { Edge.call(this); this.typ = "Unidirectional"; };
	Unidirectional.prototype = new Generalisation();
	Unidirectional.prototype.calc = function (board, drawer) {
		if (!Edge.prototype.calc.call(this, board, drawer)) {
			return false;
		}
		this.calcMoveLine(16, 50, false);
		this.$path.push(new Line(new Pos(this.$top.x, this.$top.y), new Pos(this.$end.x, this.$end.y)));
		this.$path.push(new Line(new Pos(this.$bot.x, this.$bot.y), new Pos(this.$end.x, this.$end.y)));
		return true;
	};
	//				######################################################### Aggregation #########################################################
	Aggregation = function () { Edge.call(this); this.typ = "Aggregation"; };
	Aggregation.prototype = new Generalisation();
	Aggregation.prototype.calc = function (board, drawer) {
		if (!Edge.prototype.calc.call(this, board, drawer)) {
			return false;
		}
		this.calcMoveLine(16, 49.8, true);
		this.$path.push(new Path([this.endPos().target, this.$topCenter, this.$end, this.$botCenter], "none", true));
		return true;
	};
	//				######################################################### Composition #########################################################
	Composition = function () { Edge.call(this); this.typ = "Composition"; };
	Composition.prototype = new Aggregation();
	Composition.prototype.draw = function () {
		Edge.prototype.draw.call(this);
		var lineangle, start = this.$path[0].source;
		lineangle = Math.atan2(this.$end.y - start.y, this.$end.x - start.x);
		this.$path.push(new Path([this.endPos().target, this.$topCenter, this.$end, this.$botCenter], "#000", true, lineangle));
	};
	//				###################################################### SymbolLibary ####################################################################################
// Example Items
// {tag: "path", d: ""}
// {tag: "rect", width:46, height:34}
// {tag: "ellipse", width:23, height:4}
// {tag: "line", x1:650, y1:-286, x2:650, y2:-252}
// {tag: "circle", r:5, x:12, y:0}
// {tag: "image", height: 30, width: 50, content$src: hallo}
// {tag: "text", "text-anchor": "left", x: "10"}
	typ.SymbolLibary = function () {};
	typ.SymbolLibary.prototype.upFirstChar = function (txt) {return txt.charAt(0).toUpperCase() + txt.slice(1).toLowerCase(); };
	typ.SymbolLibary.prototype.create = function (node) {
		if (this.isSymbol(node)) {
			return this.draw(node);
		}
		return null;
	};
	typ.SymbolLibary.prototype.isSymbol = function (node) {
		var fn = this[this.getName(node)];
		return typeof fn === "function";
	};
	typ.SymbolLibary.prototype.getName = function (node) {
		if (node.typ) {
			return "draw" + this.upFirstChar(node.typ);
		}
		if (node.src) {
			return "draw" + this.upFirstChar(node.src);
		}
		return "drawNode";
	};
	typ.SymbolLibary.prototype.draw = function (node, parent) {
		var group, board, item, fn = this[this.getName(node)];
		if (typeof fn === "function") {
			group = fn.apply(this, [node]);
			if (!parent) {
				board = typ.util.create({tag: "svg", style: {left: group.x, top: group.y, position: "absolute"}});
				node.width = node.width + 2;
				this.createGroup(node, group, board);
				return board;
			}
			return this.createGroup(node, group);
		}
	};
	typ.SymbolLibary.prototype.createGroup = function (node, group, g) {
		var func, y, yr, z, box, item, transform, i, offsetX = 0, offsetY = 0;
		if (!g) {
			g = typ.util.create({tag: "g"});
			transform = "translate(" + group.x + " " + group.y + ")";
			if (group.scale) { transform += " scale(" + group.scale + ")"; }
			if (group.rotate) { transform += " rotate(" + group.rotate + ")"; }
			g.setAttribute('transform', transform);
		}
		for (i = 0; i < group.items.length; i += 1) {
			g.appendChild(typ.util.create(group.items[i]));
		}
		if (!node.height) {
			node.height = group.height;
		}
		if (!node.minheight) {
			node.minheight = node.height;
		}
		if (!node.maxheight) {
			node.maxheight = node.height;
		}

		if (node.elements) {
			for (i = 0; i < node.elements.length; i += 1) {
				if (!node.elements[i] && node.elements[i].length < 1) {
					node.elements.splice(i, 1);
					i -= 1;
				}
			}
			box = typ.util.create({tag: "g"});
			z = node.elements.length * 25 + 6;
			box.appendChild(typ.util.create({tag: "rect", rx: 0, x: offsetX, y: (offsetY + 28), width: 60, height: z, stroke: "#000", fill: "#fff", opacity: "0.7"}));
			node.maxheight = z + node.minheight;

			g.elements = node.elements;
			g.activ = typ.util.create({tag: "text", $font: true, "text-anchor": "left", "width": 60, "x": (10 + offsetX), "y": 20, value: node.activText});
			g.appendChild(g.activ);
			y = offsetY + 46;
			yr = offsetY + 28;

			func = function (event) {
				g.activ.textContent = event.currentTarget.value;
			};
			for (z = 0; z < node.elements.length; z += 1) {
				box.appendChild(typ.util.create({tag: "text", $font: true, "text-anchor": "left", "width": 60, "x": 10, "y": y, value: node.elements[z]}));
				item = box.appendChild(typ.util.create({tag: "rect", rx: 0, x: offsetX, y: yr, width: 60, height: 24, stroke: "none", "class": "SVGChoice"}));
				item.value = node.elements[z];
				if (node.action) {
					item.onclick = node.action;
				} else {
					item.onclick = func;
				}
				y += 26;
				yr += 26;
			}
			g.choicebox = box;
		}
		g.tool = node;
		g.onclick = function () {
			if (g.status === "close") {
				g.open();
			} else {
				g.close();
			}
		};
		g.close = function () {
			if (g.status === "open" && g.choicebox) {
				this.removeChild(g.choicebox);
			}
			g.status = "close";
			g.tool.height = g.tool.minheight;
			//typ.util.setSize(g, g.tool.width + g.tool.x, g.tool.height + g.tool.y);
			typ.util.setSize(g, g.tool.width, g.tool.height);
		};
		g.open = function () {
			if (this.tagName === "svg") {
				return;
			}
			if (g.status === "close" && g.choicebox) {
				this.appendChild(g.choicebox);
			}
			g.status = "open";
			g.tool.height = g.tool.maxheight;
			typ.util.setSize(g, g.tool.width, g.tool.height);
			//typ.util.setSize(g, g.tool.width + g.tool.x + 10, g.tool.height + g.tool.y + 10);
		};
		g.close();

		return g;
	};
	typ.SymbolLibary.prototype.getColor = function (style, defaultColor) {
		if (style) {
			if (style.toLowerCase() === "create") {
				return "#008000";
			}
			if (style.toLowerCase() === "nac") {
				return "#FE3E3E";
			}
			if (style.indexOf("#") === 0) {
				return style;
			}
		}
		if (defaultColor) {
			return defaultColor;
		}
		return "#000";
	};
	typ.SymbolLibary.prototype.addChild = function (parent, json) {
		var item;
		if (json.offsetLeft) {
			item = json;
		} else {
			item = typ.util.create(json);
		}
		item.setAttribute("class", "draggable");
		parent.appendChild(item);
	};
	typ.SymbolLibary.prototype.getDefs = function (board) {
		var defs;
		if (board.getElementsByTagName("defs").length < 1) {
			defs = typ.util.create({tag: "defs"});
			board.insertBefore(defs, board.children[0]);
		} else {
			defs = board.getElementsByTagName("defs")[0];
		}
		return defs;
	};
	typ.SymbolLibary.prototype.addStyle = function (board, styleName) {
		var defs, style, css;
		if (styleName.baseVal || styleName.baseVal === "") {
			styleName = styleName.baseVal;
		}
		if (!styleName) {
			return;
		}
		defs = this.getDefs(board);
		if (defs.getElementsByTagName("style").length > 0) {
			style = defs.getElementsByTagName("style")[0];
		} else {
			style = typ.util.create({tag: "style"});
			style.item = {};
			defs.appendChild(style);
		}
		if (!style.item[styleName]) {
			css = typ.util.getStyle(styleName, board);
			style.item[styleName] = css;
			style.innerHTML = style.innerHTML + "\n." + styleName + css.getSVGString(board);
		}
	};
	typ.SymbolLibary.prototype.addStyles = function(board, item) {
		var items, i, className = item.className;
		
		if(className){
			if(className.baseVal || className.baseVal === "") {
				className = className.baseVal;	
			}
		}
		if(className) {
			items = className.split(" ");
			for(i = 0;i<items.length; i += 1) {
				this.addStyle(board, items[i].trim());
			}
		}
		for( i = 0; i < item.childNodes.length; i += 1) {
			this.addStyles(board, item.childNodes[i]);
		}
	};

	typ.SymbolLibary.prototype.drawSmily = function (node) {
		return {
			x: node.x || 0,
			y: node.y || 0,
			width: 50,
			height: 52,
			items: [
				{tag: "path", stroke: "black", fill: "none", d: "m49.01774,25.64542a24.5001,24.5 0 1 1 -49.0001,0a24.5001,24.5 0 1 1 49.0001,0z"},
				{tag: "path", d: "m8,31.5c16,20 32,0.3 32,0.3"},
				{tag: "path", d: "m19.15,20.32a1.74,2.52 0 1 1 -3.49,0a1.74,2.52 0 1 1 3.49,0z"},
				{tag: "path", d: "m33,20.32a1.74,2.52 0 1 1 -3.48,0a1.74,2.52 0 1 1 3.48,0z"},
				{tag: "path", d: "m5.57,31.65c3.39,0.91 4.03,-2.20 4.03,-2.20"},
				{tag: "path", d: "m43,32c-3,0.91 -4,-2.20 -4.04,-2.20"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawDatabase = function (node) {
		return {
			x: node.x || 0,
			y: node.y || 0,
			width: 25,
			height: 40,
			items: [
				{tag: "path", stroke: "black", fill: "none", d: "m0,6.26c0,-6.26 25.03,-6.26 25.03,0l0,25.82c0,6.26 -25.03,6.26 -25.03,0l0,-25.82z"},
				{tag: "path", stroke: "black", fill: "none", d: "m0,6.26c0,4.69 25.03,4.69 25.03,0m-25.03,2.35c0,4.69 25.03,4.69 25.03,0m-25.03,2.35c0,4.69 25.03,4.69 25.03,0"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawLetter = function (node) {
		return {
			x: node.x || 0,
			y: node.y || 0,
			width: 25,
			height: 17,
			items: [
				{tag: "path", stroke: "black", fill: "none", d: "m1,1l22,0l0,14l-22,0l0,-14z"},
				{tag: "path", stroke: "black", fill: "none", d: "m1.06,1.14l10.94,6.81l10.91,-6.91"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawMobilphone = function (node) {
		return {
			x: node.x || 0,
			y: node.y || 0,
			width: 25,
			height: 50,
			items: [
				{tag: "path", d: "m 4.2 0.5 15.61 0c 2 0 3.7 1.65 3.7 3.7l 0 41.6c 0 2-1.65 3.7-3.7 3.7l-15.6 0c-2 0-3.7-1.6-3.7-3.7l 0-41.6c 0-2 1.6-3.7 3.7-3.7z", fill: "none", stroke: "black"},
				{tag: "path", d: "m 12.5 2.73a 0.5 0.5 0 1 1-1 0 0.5 0.5 0 1 1 1 0z"},
				{tag: "path", d: "m 14 46a 2 2 0 1 1-4 0 2 2 0 1 1 4 0z"},
				{tag: "path", d: "m 8 5 7 0"},
				{tag: "path", d: "m 1.63 7.54 20.73 0 0 34-20.73 0z"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawWall = function (node) {
		return {
			x: node.x || 0,
			y: node.y || 0,
			width: 25,
			height: 50,
			items: [
				{tag: "path", d: "m26,45.44l-5,3.56l-21,-9l0,-36.41l5,-3.56l20.96,9l-0,36.4z"},
				{tag: "path", stroke: "white", d: "m2.21,11l18.34,7.91m-14.46,-12.57l0,6.3m8.2,21.74l0,6.35m-8.6,-10l0,6.351m4.1,-10.67l0,6.3m4.8,-10.2l0,6.3m-8.87,-10.23l0,6.35m4.78,-10.22l0,6.35m-8,14.5l18.34,7.91m-18.34,-13.91l18.34,7.91m-18.34,-13.91l18.34,7.91m-18.34,-13.91l18.34,7.91m0,-13l0,34m-18.23,-41.84l18.3,8m0,0.11l5,-3.57"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawActor = function (node) {
		return {
			x: node.x || 0,
			y: node.y || 0,
			width: 25,
			height: 50,
			items: [
				{tag: "line", stroke: "#000", x1: "12", y1: "10", x2: "12", y2: "30"},
				{tag: "circle", stroke: "#000", cy: "5", cx: "12", r: "5"},
				{tag: "line", stroke: "#000", y2: "18", x2: "25", y1: "18", x1: "0"},
				{tag: "line", stroke: "#000", y2: "39", x2: "5", y1: "30", x1: "12"},
				{tag: "line", stroke: "#000", y2: "39", x2: "20", y1: "30", x1: "12"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawLamp = function (node) {
		return {
			x: node.x || 0,
			y: node.y || 0,
			width: 25,
			height: 50,
			items: [
				{tag: "path", d: "m 22.47 10.58c-6.57 0-11.89 5.17-11.89 11.54 0 2.35 0.74 4.54 2 6.36 2 4 4.36 5.63 4.42 10.4l 11.15 0c 0.12-4.9 2.5-6.8 4.43-10.4 1.39-1.5 1.8-4.5 1.8-6.4 0-6.4-5.3-11.5-11.9-11.5z", fill: "white", stroke: "black"},
				{tag: "path", d: "m 18.4 40 8 0c 0.58 0 1 0.5 1 1 0 0.6-0.5 1-1 1l-8 0c-0.6 0-1-0.47-1-1 0-0.58 0.47-1 1-1z"},
				{tag: "path", d: "m 18.4 42.7 8 0c 0.58 0 1 0.47 1 1 0 0.58-0.47 1-1 1l-8 0c-0.58 0-1-0.47-1-1 0-0.58 0.46-1 1-1z"},
				{tag: "path", d: "m 18.4 45.3 8 0c 0.58 0 1 0.47 1 1 0 0.58-0.47 1-1 1l-8 0c-0.58 0-1-0.47-1-1 0-0.58 0.46-1 1-1z"},
				{tag: "path", d: "m 19.5 48c 0.37 0.8 1 1.3 1.9 1.7 0.6 0.3 1.5 0.3 2 0 0.8-0.3 1.4-0.8 1.9-1.8z"},
				{tag: "path", d: "m 6 37.5 4.2-4c 0.3-0.3 0.8-0.3 1 0 0.3 0.3 0.3 0.8 0 1.1l-4.2 4c-0.3 0.3-0.8 0.3-1.1 0-0.3-0.3-0.3-0.8 0-1z"},
				{tag: "path", d: "m 39 37.56-4.15-4c-0.3-0.3-0.8-0.3-1 0-0.3 0.3-0.3 0.8 0 1l 4.2 4c 0.3 0.3 0.8 0.3 1 0 0.3-0.3 0.3-0.8 0-1z"},
				{tag: "path", d: "m 38 23 5.8 0c 0.4 0 0.8-0.3 0.8-0.8 0-0.4-0.3-0.8-0.8-0.8l-5.8 0c-0.4 0-0.8 0.3-0.8 0.8 0 0.4 0.3 0.8 0.8 0.8z"},
				{tag: "path", d: "m 1.3 23 6 0c 0.4 0 0.8-0.3 0.8-0.8 0-0.4-0.3-0.8-0.8-0.8l-5.9 0c-0.4 0-0.8 0.3-0.8 0.8 0 0.4 0.3 0.8 0.8 0.8z"},
				{tag: "path", d: "m 34.75 11.2 4-4.1c 0.3-0.3 0.3-0.8 0-1-0.3-0.3-0.8-0.3-1 0l-4 4.1c-0.3 0.3-0.3 0.8 0 1 0.3 0.3 0.8 0.3 1 0z"},
				{tag: "path", d: "m 11.23 10-4-4c-0.3-0.3-0.8-0.3-1 0-0.3 0.3-0.3 0.8 0 1l 4.2 4c 0.3 0.3 0.8 0.3 1 0 0.3-0.3 0.3-0.8 0-1z"},
				{tag: "path", d: "m 21.64 1.3 0 5.8c 0 0.4 0.3 0.8 0.8 0.8 0.4 0 0.8-0.3 0.8-0.8l 0-5.8c 0-0.4-0.3-0.8-0.8-0.8-0.4 0-0.8 0.3-0.8 0.8z"},
				{tag: "path", d: "m 26.1 24.3c-0.5 0-1 0.2-1.3 0.4-1.1 0.6-2 3-2.27 3.5-0.26-0.69-1.14-2.9-2.2-3.5-0.7-0.4-2-0.7-2.5 0-0.6 0.8 0.2 2.2 0.9 2.9 1 0.9 3.9 0.9 3.9 0.9 0 0 0 0 0 0 0.54 0 2.8 0 3.7-0.9 0.7-0.7 1.5-2 0.9-2.9-0.2-0.3-0.7-0.4-1.2-0.4z"},
				{tag: "path", d: "m 22.5 28.57 0 10.7"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawStop = function (node) {
		return {
			x: node.x || 0,
			y: node.y || 0,
			width: 30,
			height: 30,
			items: [
				{tag: "path", fill: "#FFF", "stroke-width": "2", stroke: "#B00", d: "m 6,6 a 14,14 0 1 0 0.06,-0.06 z m 0,0 20,21"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawMin = function (node) {
		return {
			x: node.x || 0,
			y: node.y || 0,
			width: 20,
			height: 20,
			items: [
				{tag: "path", fill: "white", stroke: "#000", "stroke-width": 0.2, "stroke-linejoin": "round", d: "m 0,0 19,0 0,19 -19,0 z"},
				{tag: "path", fill: "none", stroke: "#000", "stroke-width": "1px", "stroke-linejoin": "miter", d: "m 4,10 13,-0.04"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawArrow = function (node) {
		return {
			x: node.x || 0,
			y: node.y || 0,
			width: 10,
			height: 9,
			rotate: node.rotate,
			items: [
				{tag: "path", fill: "#000", stroke: "#000", d: "M 0,0 10,4 0,9 z"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawMax = function (node) {
		return {
			x: node.x || 0,
			y: node.y || 0,
			width: 20,
			height: 20,
			items: [
				{tag: "path", fill: "white", stroke: "#000", "stroke-width": 0.2, "stroke-linejoin": "round", "stroke-dashoffset": 2, "stroke-dasharray": "4.8,4.8", d: "m 0,0 4.91187,0 5.44643,0 9.11886,0 0,19.47716 -19.47716,0 0,-15.88809 z"},
				{tag: "path", fill: "none", stroke: "#000", "stroke-width": "1px", "stroke-linejoin": "miter", d: "m 4,10 6,0.006 0.02,5 0.01,-11 -0.03,6.02 c 2,-0.01 4,-0.002 6,0.01"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawButton = function (node) {
		var btnX, btnY, btnWidth, btnHeight, btnValue;

		btnX = node.x || 0;
		btnY = node.y || 0;
		btnWidth = node.width || 60;
		btnHeight = node.height || 28;
		btnValue = node.value || "";
		return {
			x: btnX,
			y: btnY,
			width: 60,
			height: 28,
			items: [
				{tag: "rect", rx: 8, x: 0, y: 0, width: btnWidth, height: btnHeight, stroke: "#000", filter: "url(#drop-shadow)", "class": "SVGBtn"},
				{tag: "text", $font: true, x: 10, y: 18, fill: "black", value: btnValue, "class": "hand"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawDropdown = function (node) {
		var btnX, btnY, btnWidth, btnHeight;

		btnX = node.x || 0;
		btnY = node.y || 0;
		btnWidth = node.width || 60;
		btnHeight = node.height || 28;
		return {
			x: btnX,
			y: btnY,
			width: btnWidth,
			height: btnHeight,
			items: [
				{tag: "rect", rx: 0, x: 0, y: 0, width: btnWidth - 20, height: btnHeight, stroke: "#000", fill: "none"},
				{tag: "rect", rx: 2, x: btnWidth - 20, y: 0, width: 20, height: 28, stroke: "#000", "class": "SVGBtn"},
				{tag: "path", style: "fill:#000000;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1;fill-opacity:1", d: "m " + (btnWidth - 15) + ",13 10,0 L " + (btnWidth - 10) + ",20 z"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawClassicon = function (node) {
		var btnX, btnY, btnWidth, btnHeight;

		btnX = node.x || 0;
		btnY = node.y || 0;
		btnWidth = node.width || 60;
		btnHeight = node.height || 28;
		return {
			x: btnX,
			y: btnY,
			width: btnWidth,
			height: btnHeight,
			items: [
				{tag: "path", d: "m0,0l10.78832,0l0,4.49982l-10.78832,0.19999l0,9.19963l10.78832,0l0,-9.49962l-10.78832,0.19999l0,-4.59982z", style: "fill:none;stroke:#000000;"},
				{tag: "path", d: "m25.68807,0l10.78832,0l0,4.49982l-10.78832,0.19999l0,9.19963l10.78832,0l0,-9.49962l-10.78832,0.2l0,-4.59982z", style: "fill:none;stroke:#000000;"},
				{tag: "line", x1: 11, y1: 7, x2: 25, y2: 7, stroke: "#000"}
			]
		};
	};
	typ.SymbolLibary.prototype.drawEdgeicon = function (node) {
		var btnX, btnY, btnWidth, btnHeight;

		btnX = node.x || 0;
		btnY = node.y || 0;
		btnWidth = node.width || 30;
		btnHeight = node.height || 35;
		return {
			x: btnX,
			y: btnY,
			width: btnWidth,
			height: btnHeight,
			items: [
				{tag: "path", d: "M2,10 20,10 20,35 2,35 Z M2,17 20,17 M20,10 28,5 28,9 M 28.5,4.7 24,4", style: "fill:none;stroke:#000000;transform:scale(0.4);"}
			]
		};
	};
	// TODO
	// Validate input
	// Create Assocs
	// Edit Assocs
	// Delete Assocs
	// Edit Attribute and Methods
	// ################################## ClassEditor ####################################################
	typ.ClassEditor = function (element, diagramTyp) {
		var parent, i, intern;
		this.isIE = document.all && !window.opera;
		this.inputEvent = true;
		this.nodes = {};
		this.noButtons = true;
		this.model = new typ.GraphModel(this, {buttons: [], typ: diagramTyp});
		if (element) {
			if (typeof (element) === "string") {
				this.board = this.drawer.getBoard(this);
				parent = document.getElementById(element);
				if (!parent) {
					document.body.appendChild(this.board);
				} else {
					for (i = parent.children.length - 1; i >= 0; i -= 1) {
						parent.removeChild(parent.children[i]);
					}
					parent.appendChild(this.board);
					parent.style.height = "100%";
					parent.style["min-height"] = "";
					parent.style["min-width"] = "";
					parent.style.width = "100%";
				}
			} else {
				this.board = element;
			}
		} else {
			this.board = document.body;
		}
		this.inputNode = new InputNode(this);
		this.editNode = new EditNode(this);
		this.createEdge = new CreateEdge(this);
		this.actions = [ new Selector(this), new MoveNode(this), this.createEdge, new CreateNode(this)];

		intern = new LocalEditor(this);
		typ.util.bind(this.board, "mousedown", function (e) {intern.doAction(e, "startAction"); });
		typ.util.bind(this.board, "mousemove", function (e) {intern.doAction(e, "doAction"); });
		typ.util.bind(this.board, "mouseup", function (e) {intern.doAction(e, "stopAction"); });
		typ.util.bind(this.board, "mouseout", function (e) {intern.doAction(e, "outAction"); });
		typ.util.bind(this.board, "dragover", function (e) {intern.dragClass(e); });
		typ.util.bind(this.board, "dragleave", function (e) {intern.dragClass(e); });
		typ.util.bind(this.board, "drop", function (e) {intern.dropModel(e); });
		this.loadModel(this.model);
	};
	typ.ClassEditor.prototype.setBoardStyle = function (typ) {
		var b = this.board;
		this.removeClass(b, "Error");
		this.removeClass(b, "Ok");
		this.removeClass(b, "Add");
		if (typ === "dragleave") {
			if (b.errorText) {
				b.removeChild(b.errorText);
				b.errorText = null;
			}
			return true;
		}
		this.addClass(b, typ);
		if (typ === "Error") {
			if (!b.errorText) {
				b.errorText = this.create({tag: "div", style: "margin-top: 30%", value: "NO TEXTFILE"});
				b.appendChild(b.errorText);
			}
			return true;
		}
		return false;
	};
	typ.ClassEditor.prototype.download = function (typ, data, name) {
		var a = document.createElement("a");
		a.href = window.URL.createObjectURL(new Blob([data], {type: typ}));
		a.download = name;
		a.click();
	};
	typ.ClassEditor.prototype.save = function () {
		var data, hasJava, result = {};
		this.copy(result, this.model);
		data = JSON.stringify(result, null, "\t");
		hasJava = typeof (java);
		if (hasJava !== 'undefined') {
			java.save(data);
		} else {
			this.download("text/json", data, "model.json");
		}
	};
	typ.ClassEditor.prototype.generate = function () {
		var data, result = this.minJson({}, this.model);
		data = JSON.stringify(result, null, "\t");
		java.generate(data);
	};
	typ.ClassEditor.prototype.close = function () {
		window.java.exit();
	};
	typ.ClassEditor.prototype.loadModel = function (model, addFile, file) {
		var i, that = this;
		if (!addFile) {
			this.model = new typ.GraphModel(that, {buttons: []});
			//this.model = that.copy(newModel, model);
		}
		this.getAction("Selector").setNode(null);
		for (i = this.board.children.length - 1; i >= 0; i -= 1) {
			this.board.removeChild(this.board.children[i]);
		}
		for (i in this.model.nodes) {
			if (!this.model.nodes.hasOwnProperty(i)) {
				continue;
			}
			this.addNode(this.model.nodes[i]);
		}
		for (i in model.nodes) {
			if (!model.nodes.hasOwnProperty(i)) {
				continue;
			}
			this.addNode(model.nodes[i]);
		}
		this.toolbar = typ.util.create({tag: "div", id: "toolbar", "class": "Toolbar", style: "width:6px;height:120px", onMouseOver: function () {that.maxToolbar(); }, onMouseOut: function (e) {that.minToolbar(e); }, $parent: this.board});

		this.itembar = typ.util.create({tag: "div", id: "itembar", "class": "Itembar", style: "width:6px;height:200px", onMouseOver: function () {that.maxItembar(); }, onMouseOut: function (e) {that.minItembar(e); }, $parent: this.board});
		this.codeView = typ.util.create({tag: "div", "class": "CodeView", $parent: this.board});
		typ.util.create({tag: "div", "class": "pi", $parent: this.codeView, value: "&pi;", onMouseOver: function () {that.maxCodeView(); }, onMouseOut: function (e) {that.minCodeView(e); }});
	};
	typ.ClassEditor.prototype.maxCodeView = function () {
		if (this.codeViewer) {return; }
		var html, rect, data, result = typ.util.minJson({}, this.model);
		data = JSON.stringify(result, null, "\t");
		data = data.replace(new RegExp("\n", 'g'), "<br/>").replace(new RegExp(" ", 'g'), "&nbsp;");

		html = typ.util.create({tag: "div", style: "position:absolute;", value: data});
		this.board.appendChild(html);
		rect = html.getBoundingClientRect();
		this.board.removeChild(html);
		this.codeViewer = typ.util.create({tag: "div", "class": "code_box", style: {width: rect.width, height: rect.height}, $parent: this.board, value: data});
	};
	typ.ClassEditor.prototype.minCodeView = function () {
		if (!this.codeViewer) {
			return;
		}
		this.board.removeChild(this.codeViewer);
		this.codeViewer = null;
	};
	typ.ClassEditor.prototype.maxToolbar = function () {
		if (this.toolbar.clientWidth > 100) {
			return;
		}
		var that = this, table, tr, cell, hasJava;

		this.toolbar.minWidth = this.toolbar.clientWidth;
		this.toolbar.style.width = 300;
		table = typ.util.create({tag: "table", $parent: this.toolbar});
		typ.util.createCell({"tag": "th", colspan: 2, value: "Properties"}, table);

		tr = typ.util.create({tag: 'tr', $parent: table});
		typ.util.create({"tag": "td", value: "Workspace:", $parent: tr});
		cell = typ.util.create({"tag": "td", $parent: tr});
		this.createInputField({value: this.model["package"], $parent: cell, onChange: function (e) {that.savePackage(e); }});

		cell = typ.util.createCell({"tag": "td", colspan: 2, style: "text-align:right;padding:10px 10px 0 0"}, table);
		typ.util.create({tag: 'button', $parent: cell, style: "margin-left:10px;", value: "Save", onClick: function () {that.save(); }});
		hasJava = typeof (java);
		if (hasJava !== 'undefined') {
			typ.util.create({tag: 'button', $parent: cell, style: "margin-left:10px;", value: "Generate", onClick: function () {that.generate(); }});
			typ.util.create({tag: 'button', $parent: cell, style: "margin-left:10px;", value: "Exit", onClick: function () {that.close(); }});
		}
	};
	typ.ClassEditor.prototype.maxItembar = function () {
		if (this.itembar.clientWidth > 10) {
			return;
		}
		var that = this, table, th, item, node;

		this.itembar.minWidth = this.itembar.clientWidth;
		this.itembar.style.width = 80;

		table = typ.util.create({tag: "table", style: "padding-left:10px", $parent: this.itembar});
		typ.util.createCell({"tag": "th", value: "Item"}, table);
		th = typ.util.createCell({"tag": "th"}, table);
		item = typ.util.create({"tag": "table", id: "node", draggable: "true", cellspacing: "0", ondragstart: function (e) {that.startDrag(e); }, style: "border:1px solid #000;width:30px;height:30px;cursor: pointer", $parent: th});
		typ.util.createCell({"tag": "td", style: "height:10px;border-bottom:1px solid #000;"}, item);
		typ.util.createCell({"tag": "td"}, item);
		node = this.getAction("Selector").node;

		if (node) {
			th = typ.util.createCell({"tag": "th"}, table);
			typ.util.create({tag: "button", id: "Attribute", value: "Attribute", onclick: function (e) {that.executeClassAdd(e); }, "style": "margin-top:5px;", $parent: th});
			typ.util.create({tag: "button", id: "Method", value: "Method", onclick: function (e) {that.executeClassAdd(e); }, "style": "margin-top:5px;", $parent: th});
		}
	};
	typ.ClassEditor.prototype.createInputField = function (option) {
		var that = this, node;
		node = typ.util.copy({tag: "input", type: "text", width: "100%", onFocus: function () {that.inputEvent = false; }, onBlur: function () {that.inputEvent = true; }}, option);
		if (option.$parent) {
			node.$parent = option.$parent;
		}
		if (option.onChange) {
			node.onChange = option.onChange;
		}
		return typ.util.create(node);
	};
	typ.ClassEditor.prototype.minToolbar = function (e) {
		if (this.toolbar.clientWidth < 100 || this.getId(e.toElement, "toolbar")) {
			return;
		}
		var i;
		for (i = this.toolbar.children.length - 1; i >= 0; i -= 1) {
			this.toolbar.removeChild(this.toolbar.children[i]);
		}
		this.toolbar.style.width = this.toolbar.minWidth;
		this.inputEvent = true;
	};
	typ.ClassEditor.prototype.minItembar = function (e) {
		if (this.itembar.clientWidth < 50 || this.getId(e.toElement, "itembar")) {
			return;
		}
		var i;
		for (i = this.itembar.children.length - 1; i >= 0; i -= 1) {
			this.itembar.removeChild(this.itembar.children[i]);
		}
		this.itembar.style.width = this.itembar.minWidth;
		this.inputEvent = true;
	};
	typ.ClassEditor.prototype.getId = function (element, id) {
		if (element === null) {
			return false;
		}
		if (element.id === id) {
			return true;
		}
		return this.getId(element.parentElement, id);
	};
	typ.ClassEditor.prototype.getAction = function (name) {
		var i;
		for (i = 0; i < this.actions.length; i += 1) {
			if (name === this.actions[i].name) {
				return this.actions[i];
			}
		}
		return null;
	};
	typ.ClassEditor.prototype.addNode = function (node) {
		var i, html = null, size, that = this;
		for (i = 0; i < this.model.nodes.length; i += 1) {
			if (this.model.nodes[i].id === node.id) {
				html = this.drawer.getNode(this.model.nodes[i], false);
				break;
			}
		}
		if (!html) {
			node = this.model.addNode(node);
			html = this.drawer.getNode(node, false);
		}
		if (this.getAction("Selector").node) {
			this.getAction("Selector").node = html;
		}
		this.board.appendChild(html);

		size = this.drawer.getSize(html);
		node.$minWidth = size.x;
		node.$minHeight = size.y;
		this.drawer.setSize(html, Math.max(Number(node.width), Number(node.$minWidth)), Math.max(Number(node.height), Number(node.$minHeight)));

		typ.util.bind(html, "mouseup", function (e) {
			var n = typ.util.getModelNode(e.target);
			if (n) {
				that.getAction("Selector").setNode(n);
			}
		});
	};
	typ.ClassEditor.prototype.removeNode = function (id) {
		this.model.removeNode(id);
	};
	typ.ClassEditor.prototype.clearLines = function () {
		var i;
		for (i = 0; i < this.model.edges.length; i += 1) {
			this.model.edges[i].removeFromBoard(this.board);
		}
	};
	typ.ClassEditor.prototype.drawlines = function () {
		this.clearLines();
		var infoTxt, e, i;
		for (i = 0; i < this.model.edges.length; i += 1) {
			e = this.model.edges[i];
			infoTxt = e.getInfo(e.source);
			if (infoTxt.length > 0) {
				this.sizeHTML(this.drawer.getInfo(e.source, infoTxt, 0), e.source);
			}
			infoTxt = e.getInfo(e.target);
			if (infoTxt.length > 0) {
				this.sizeHTML(this.drawer.getInfo(e.target, infoTxt, 0), e.target);
			}
		}
		this.model.calcLines(this.drawer);
		for (i = 0; i < this.model.edges.length; i += 1) {
			this.model.edges[i].draw();
		}
	};
	typ.ClassEditor.prototype.removeCurrentNode = function () {
		var i, n, item, selector = this.getAction("Selector");
		item = selector.node;
		if (item) {
			selector.removeAll();
			this.board.removeChild(item);
			n = item.model;
			for (i = 0; i < this.model.nodes.length; i += 1) {
				if (this.model.nodes[i].id === n.id) {
					this.model.nodes.splice(i - 1, 1);
					i -= 1;
				}
			}
		}
	};
	typ.ClassEditor.prototype.createdElement = function (element, type, node) {
		if (type) {
			if (type === "empty" || type === "attribute" || type === "method") {
				this.createEdge.addElement(element, node);
			} else {
				if (type !== "info") {
					var that = this;
					typ.util.bind(element, "mousedown", function (e) {
						that.getAction("MoveNode").callBack(type, e);
					});
				}
				this.editNode.addElement(element, type);
			}
		}
	};

	// ################################## DragClassEditor ####################################################
	LocalEditor = function (parent) {this.parent = parent; };
	LocalEditor.prototype.dragStyler = function (e, typ) {
		e.stopPropagation();
		e.preventDefault();
		this.parent.setBoardStyle(typ);
	};
	LocalEditor.prototype.dragClass = function (e) {
		if (this.dragStyler(e, e.type)) {
			return;
		}
		if (e.target !== this.parent.board) {
			return;
		}
		var error = true, n, i, f, files = e.target.files || e.dataTransfer.files;
		// process all File objects
		if (!files || files.length < 1) {
			return;
		}
		for (i = 0; i < files.length; i += 1) {
			f = files[i];
			if (f.type.indexOf("text") === 0) {
				error = false;
			} else if (f.type === "") {
				n = f.name.toLowerCase();
				if (n.indexOf("json", n.length - 4) !== -1) {
					error = false;
				}
			}
		}
		if (error) {
			this.dragStyler(e, "Error");
		} else if (e.ctrlKey) {
			this.dragStyler(e, "Add");
		} else {
			this.dragStyler(e, "Ok");
		}
	};
	LocalEditor.prototype.dropFile = function (content, file) {
		this.parent.loadModel(JSON.parse(content), false, file);
	};
	LocalEditor.prototype.dropModel = function (e) {
		var i, n, f, files, x, y, that = this.parent, func, data, load, reader;
		this.dragStyler(e, "dragleave");

		data = e.dataTransfer.getData("Text");
		if (data) {
			x = typ.util.getEventX(e);
			y = typ.util.getEventY(e);
			this.parent.getAction("CreateNode").setValue(x, y, x + 100, y + 100);
			return;
		}

		files = e.target.files || e.dataTransfer.files;
		func = function (r) { that.loadModel(JSON.parse(r.target.result), e.ctrlKey, f); };
		for (i = 0; i < files.length; i += 1) {
			f = files[i];
			load = f.type.indexOf("text") === 0;
			if (!load && f.type === "") {
				n = f.name.toLowerCase();
				if (n.indexOf("json", n.length - 4) !== -1) {
					load = true;
				}
			}
			if (load) {
				e.stopPropagation();
				// file.name
				reader = new FileReader();
				reader.onload = func;
				reader.readAsText(f);
				break;
			}
		}
	};
	LocalEditor.prototype.executeClassAdd = function (e) {
		var node = this.getAction("Selector").node;
		if (e.target.id === "Attribute") {
			this.inputNode.accept("attribute:Object", node);
		} else if (e.target.id === "Method") {
			this.inputNode.accept("methods()", node);
		}
	};
	LocalEditor.prototype.startDrag = function (e) {e.dataTransfer.setData("Text", e.target.id); };
	LocalEditor.prototype.savePackage = function (e) {
		this.model["package"] = e.target.value;
	};
	LocalEditor.prototype.doAction = function (event, functionName) {
		var i;
		for (i = 0; i < this.parent.actions.length; i += 1) {
			if (typeof this.parent.actions[i][functionName] === "function" && this.parent.actions[i][functionName](event)) {
				return;
			}
		}
		if (functionName === "stopAction" && event.target === this.board) {
			this.getAction("Selector").setNode(null);
		}
	};
	// ################################## CREATE ####################################################
	CreateNode = function (parent) {
		this.name = "CreateNode";
		this.$parent = parent;
		this.minSize = 20;
		this.offset = new Pos();
		this.mouse = new Pos();
		this.createClass = false;
	};
	CreateNode.prototype.startAction = function (event) {
		if (event.button === 2) {return; }
		if (event.target !== this.$parent.board) {return; }
		this.createClass = true;
		this.offset.x = this.mouse.x = this.getX(event);
		this.offset.y = this.mouse.y = this.getY(event);
		return true;
	};
	CreateNode.prototype.doAction = function (event) {
		if (!this.createClass) {return; }
		this.mouse.x = this.getX(event);
		this.mouse.y = this.getY(event);
		this.createNode();
	};
	CreateNode.prototype.setValue = function (x1, y1, x2, y2) {
		this.offset.x = x1;
		this.offset.y = y1;
		this.mouse.x = x2;
		this.mouse.y = y2;
		this.createNode();
	};
	CreateNode.prototype.createNode = function () {
		var height, width = Math.abs(this.mouse.x - this.offset.x);
		height = Math.abs(this.mouse.y - this.offset.y);
		if (width > this.minSize && height > this.minSize) {
			if (!this.newClass) {
				this.newClass = typ.util.create({tag: "div", style: "position:absolute;opacity: 0.2;background-color:#ccc;"});
				this.$parent.board.appendChild(this.newClass);
			}
			this.newClass.style.width = width;
			this.newClass.style.height = height;
			this.newClass.style.left = Math.min(this.mouse.x, this.offset.x);
			this.newClass.style.top = Math.min(this.mouse.y, this.offset.y);
		} else {
			if (this.newClass) {
				this.$parent.board.removeChild(this.newClass);
				this.newClass = null;
			}
		}
		return true;
	};
	CreateNode.prototype.getX = function (event) {
		return typ.util.getEventX(event) - this.$parent.board.offsetLeft;
	};
	CreateNode.prototype.getY = function (event) {
		return typ.util.getEventY(event) - this.$parent.board.offsetTop;
	};
	CreateNode.prototype.outAction = function (event) {return this.stopAction(event); };
	CreateNode.prototype.stopAction = function () {
		this.createClass = false;
		if (!this.newClass) {
			return false;
		}
		var node = {"typ": "node", "id": "Class" + (this.$parent.model.$nodeCount + 1)};
		node.x = typ.util.getValue(this.newClass.style.left);
		node.y = typ.util.getValue(this.newClass.style.top);
		node.width = typ.util.getValue(this.newClass.style.width);
		node.height = typ.util.getValue(this.newClass.style.height);

		this.$parent.board.removeChild(this.newClass);
		this.newClass = null;
		this.$parent.addNode(node);
		return true;
	};
// ################################## SELECTOR ####################################################
	Selector = function (parent) {
		this.name = "Selector";
		this.$parent = parent;
		this.size = 6;
		this.nodes = {};
		this.mouse = new Pos();
		this.offset = new Pos();
		this.resizeNode = null;
	};
	Selector.prototype.start = function (e) {
		this.resizeNode = e.target.id;
		this.sizeNode = new Pos(this.node.model.width, this.node.model.height);
		this.offset.x = this.mouse.x = typ.util.getEventX(e);
		this.offset.y = this.mouse.y = typ.util.getEventY(e);
	};
	Selector.prototype.doit = function (e) {
		if (!this.resizeNode) {
			return;
		}
		this.mouse.x = typ.util.getEventX(e);
		this.mouse.y = typ.util.getEventY(e);

		var n, multiX = 1, multiY = 1, diffX = 0, diffY = 0, newWidth, newHeight;
		if (this.resizeNode.charAt(0) === "n") {
			multiY = -1;
		}
		if (this.resizeNode.indexOf("w") >= 0) {
			multiX = -1;
		}
		n = this.node.model;

		newWidth = Math.max(n.$minWidth, this.sizeNode.x + (this.mouse.x - this.offset.x) * multiX);
		newHeight = Math.max(n.$minHeight, this.sizeNode.y + (this.mouse.y - this.offset.y) * multiY);

		if (this.resizeNode === "n") {
			diffY = n.height - newHeight;
			n.height = this.node.style.height = newHeight;
		} else if (this.resizeNode === "nw") {
			diffY = n.height - newHeight;
			n.height = this.node.style.height = newHeight;
			diffX = n.width - newWidth;
			n.width = this.node.style.width = newWidth;
		} else if (this.resizeNode === "ne") {
			diffY = n.height - newHeight;
			n.height = this.node.style.height = newHeight;
			n.width = this.node.style.width = newWidth;
		} else if (this.resizeNode === "sw") {
			diffX = n.width - newWidth;
			n.height = this.node.style.height = newHeight;
			n.width = this.node.style.width = newWidth;
		} else if (this.resizeNode === "s") {
			n.height = this.node.style.height = newHeight;
		} else if (this.resizeNode === "w") {
			diffX = n.width - newWidth;
			n.width = this.node.style.width = newWidth;
		} else if (this.resizeNode === "e") {
			n.width = this.node.style.width = newWidth;
		} else {
			n.width = this.node.style.width = newWidth;
			n.height = this.node.style.height = newHeight;
		}
		if (diffY !== 0) {
			n.y += diffY;
			this.node.style.top = n.y;
		}
		if (diffX !== 0) {
			n.x += diffX;
			this.node.style.left = n.x;
		}
		this.refreshNode();
	};
	Selector.prototype.stop = function () {this.resizeNode = null; };
	Selector.prototype.removeAll = function () {
		var i, select;
		for (i in this.nodes) {
			if (!this.nodes.hasOwnProperty(i)) {
				continue;
			}
			select = this.nodes[i];
			this.$parent.board.removeChild(select);
		}
		this.nodes = {};
	};
	Selector.prototype.setNode = function (node) {
		if (this.node) {
			this.removeAll();
		}
		this.node = node;
		this.refreshNode();
	};
	Selector.prototype.refreshNode = function () {
		if (!this.node) {
			return;
		}
		var x, y, width, height, s, sh;
		x = typ.util.getValue(this.node.style.left);
		y = typ.util.getValue(this.node.style.top);
		width = typ.util.getValue(this.node.clientWidth);
		height = typ.util.getValue(this.node.clientHeight);
		s = this.size + 1;
		sh = this.size / 2 + 1;
		this.selector("nw", x - s, y - s);
		this.selector("n", x + (width / 2) - sh, y - s);
		this.selector("ne", x + width + 1, y - s);
		this.selector("w", x - s, y + height / 2 - sh);
		this.selector("sw", x - s, y + height + 1);
		this.selector("s", x + (width / 2) - sh, y + height + 1);
		this.selector("se", x + width + 1, y + height + 1);
		this.selector("e", x + width + 1, y + height / 2 - sh);
		this.addCreateAssoc(x + width, y);
	};
	Selector.prototype.addCreateAssoc = function (x, y) {
		var n = this.nodes.assoc, symbolLib;
		if (!n) {
			n = {typ: "EdgeIcon", transform: "scale(0.2)", style: "cursor:pointer;top: " + x + "px;left:" + y + "px;" };
			symbolLib = new typ.SymbolLibary();
			n = symbolLib.draw(null, n);
			n.style.left = x + 10;
			n.style.width = 40;
			n.style.height = 30;
			n.style.position = "absolute";
			n.style.top = y - 10;
			this.nodes.assoc = n;
			this.$parent.board.appendChild(n);
		}
	};
	Selector.prototype.selector = function (id, x, y) {
		var n = this.nodes[id], that = this;
		if (!n) {
			n = typ.util.create({tag: "div", "id": id, style: "position:absolute;background:#00F;width:" + this.size + "px;height:" + this.size + "px;cursor:" + id + "-resize;"});
			this.nodes[id] = n;
			typ.util.bind(n, "mousedown", function (e) {that.start(e); });
			typ.util.bind(n, "mousemove", function (e) {that.doit(e); });
			typ.util.bind(n, "mouseup", function (e) {that.stop(e); });
			this.$parent.board.appendChild(n);
		}
		n.style.left = x;
		n.style.top = y;
	};
	Selector.prototype.startAction = function () {
		if (!this.node) {
			return false;
		}
	};
	Selector.prototype.doAction = function (event) {
		if (!this.resizeNode) {
			return false;
		}
		this.doit(event);
		return true;
	};
	Selector.prototype.stopAction = function () {
		if (this.resizeNode) {
			this.resizeNode = false;
			return true;
		}
		return false;
	};
	// ################################## MoveNode ####################################################
	MoveNode = function (parent) { this.name = "MoveNode"; this.$parent = parent; this.mouse = new Pos(); this.offset = new Pos(); };
	MoveNode.prototype.callBack = function (type, e) {
		if (type === "id") {
			var th = e.target, that = this;
			typ.util.bind(th, "mousedown", function (e) {that.start(e); });
			typ.util.bind(th, "mousemove", function (e) {that.doit(e); });
			typ.util.bind(th, "mouseup", function (e) {that.stop(e); });
		}
	};
	MoveNode.prototype.start = function (e) {
		this.node = typ.util.getModelNode(e.target).model;
		this.posNode = new Pos(this.node.x, this.node.y);
		// SAVE ID
		this.offset.x = this.mouse.x = typ.util.getEventX(e);
		this.offset.y = this.mouse.y = typ.util.getEventY(e);
	};
	MoveNode.prototype.doAction = function () {return this.node; };
	MoveNode.prototype.doit = function (e) {
		if (!this.node) {
			return;
		}
		this.mouse.x = this.getEventX(e);
		this.mouse.y = this.getEventY(e);
		var newX, newY;
		newX = this.posNode.x + (this.mouse.x - this.offset.x);
		newY = this.posNode.y + (this.mouse.y - this.offset.y);

		this.node.x = this.node.$gui.style.left = newX;
		this.node.y = this.node.$gui.style.top = newY;
		this.$parent.getAction("Selector").refreshNode();
	};
	MoveNode.prototype.stop = function () {
		this.node = null;
		this.$parent.drawlines();
	};
	// ################################## InputNode ####################################################
	InputNode = function (parent) {
		this.name = "InputNode";
		this.$parent = parent;
		var that = this;
		document.body.addEventListener("keyup", function (e) {
			that.keyup(e);
		});
	};
	InputNode.prototype.keyup = function (e) {
		if (!this.$parent.inputEvent) {
			return;
		}
		var x = e.keyCode, selector, item, m, that = this;
		if (e.altKey || e.ctrlKey) {
			return;
		}
		if (x === 46) {
			this.$parent.removeCurrentNode();
		}
		if ((x > 64 && x < 91) && !e.shiftKey) {
			x += 32;
		}
		if ((x > 64 && x < 91) || (x > 96 && x < 123) || (x > 127 && x < 155) || (x > 159 && x < 166)) {
			selector = this.$parent.getAction("Selector");
			item = selector.node;
			if (item && !this.inputItem) {
				m = item.model;
				this.inputItem = this.$parent.create({tag: "input", type: "text", "#node": item, "value": String.fromCharCode(x), style: "position:absolute;left:" + m.x + "px;top:" + (m.y + m.height) + "px;width:" + m.width});
				this.$parent.board.appendChild(this.inputItem);
				this.choiceBox = new ChoiceBox(this.inputItem, this.$parent);
				this.inputItem.addEventListener("keyup", function (e) {
					that.changeText(e);
				});
				this.inputItem.focus();
			}
		}
	};
	InputNode.prototype.accept = function (text, n) {
		var id, model = n.model;
		id = n.model.id;
		if (this.addValue(text, model)) {
			if (id !== n.model.id) {
				this.$parent.removeNode(id);
				this.$parent.addNode(n.model);
			} else {
				this.$parent.board.removeChild(n);
				this.$parent.addNode(n.model);
			}
			this.$parent.getAction("Selector").refreshNode();
			return true;
		}
		return false;
	};
	InputNode.prototype.addValue = function (text, model) {
		if (text.length < 1) {
			return false;
		}
		if (text.indexOf(":") >= 0) {
			if (!model.attributes) {
				model.attributes = [];
			}
			model.attributes.push(text);
			return true;
		}
		if (text.indexOf("(") > 0) {
			if (!model.methods) {
				model.methods = [];
			}
			model.methods.push(text);
			return true;
		}
		//typ ClassEditor
		if (model.$parent.typ === "classdiagram") {
			model.id = this.fristUp(text);
		} else {
			model.id = text;
		}
		return true;
	};
	InputNode.prototype.fristUp = function (string) {
		return string.charAt(0).toUpperCase() + string.slice(1);
	};
	InputNode.prototype.changeText = function (e) {
		if (!this.inputItem) {
			return;
		}
		var close = false, n, text;
		if (e.keyCode === 27) {close = true; }
		if (e.keyCode === 13) {
			n = this.inputItem.node;
			text = this.inputItem.value;
			if (this.accept(text, n)) {
				close = true;
			}
		}
		if (close) {
			this.$parent.board.removeChild(this.inputItem);
			this.inputItem = null;
			if (this.choiceBox && this.choiceBox.div) {
				this.graph.board.removeChild(this.choiceBox.div);
				this.choiceBox.div = null;
				this.choiceBox = null;
			}
		}
	};
	// ################################## ChoiceBox ####################################################
	ChoiceBox = function (field, graph) {
		this.field = field;
		this.graph = graph;
		this.list = [];
		var that = this;
		this.bind(field, "keyup", function (e) {that.change(e); });
	};
	ChoiceBox.prototype.initAttributes = function () {
		this.list = ["Boolean", "Byte", "Character", "Double", "Float", "Integer", "Long", "Number", "Object", "Short", "String"];
		this.addFromGraph(this.graph.model, "nodes.id");
		this.list.sort();
	};
	ChoiceBox.prototype.addFromGraph = function (item, filter) {
		var i, z;
		for (i in item) {
			if (!item.hasOwnProperty(i)) {
				continue;
			}
			if (item[i] instanceof Array) {
				for (z = 0; z < item[i].length; z += 1) {
					this.addFromGraph(item[i][z], filter.substring(filter.indexOf(".") + 1));
				}
			}
			if (filter.indexOf(".") < 0 && i === filter) {
				this.list.push(item[i]);
			}
		}
	};
	ChoiceBox.prototype.change = function (e) {
		if (this.div) {
			this.graph.board.removeChild(this.div);
			this.div = null;
		}
		if (e.keyCode === 27 || e.keyCode === 13) {
			return;
		}
		var t = e.target.value.toLowerCase(), that = this, i, div, func;
		this.typ = "";
		if (t.indexOf(":") >= 0) {
			this.initAttributes();
			this.typ = ":";
		}
		if (this.typ === "") {
			return;
		}
		t = t.substring(t.lastIndexOf(this.typ) + 1);
		div = this.create({tag: "div", "class": "ChoiceBox", style: "left:" + this.field.style.left + ";top:" + (this.getValue(this.field.style.top) + this.field.clientHeight + 4) + ";width:" + this.field.clientWidth});
		func = function () {that.select(this); };
		for (i = 0; i < this.list.length; i += 1) {
			if (this.list[i].toLowerCase().indexOf(t) >= 0) {
				if (i % 2 === 0) {
					this.create({tag: "div", value: this.list[i], $parent: div, onMouseup: func});
				} else {
					this.create({tag: "div", value: this.list[i], "class": "alt", $parent: div, onMouseup: func});
				}
			}
		}
		if (div.children.length > 0) {
			this.div = div;
			this.graph.board.appendChild(div);
		}
	};
	ChoiceBox.prototype.select = function (input) {
		var pos = this.field.value.lastIndexOf(this.typ);
		this.field.value = this.field.value.substring(0, pos + 1) + input.innerHTML;
		if (this.div) {
			this.graph.board.removeChild(this.div);
			this.div = null;
		}
		this.field.focus();
	};
	// ################################## EditNode ####################################################
	EditNode = function (graph) {this.graph = graph; };
	EditNode.prototype.addElement = function (element, type) {
		var that = this;
		typ.util.bind(element, "dblclick", function (e) {that.click(e, element, type); });
	};
	EditNode.prototype.click = function (e, control, type) {
		var that = this;
		control.oldValue = control.innerHTML;
		control.contentEditable = true;
		control.typ = type;
		this.graph.inputEvent = false;
		typ.util.selectText(control);
		control.onkeydown = function (e) {that.change(e, control); };
		control.onblur = function (e) {that.cancel(e, control); };
	};
	EditNode.prototype.cancel = function (e, control) {
		if (control.oldValue) {
			control.oldValue = null;
		}
		control.contentEditable = false;
	};
	EditNode.prototype.change = function (e, control) {
		if (e.keyCode !== 27 && e.keyCode !== 13) {
			return;
		}
		var value, t, i, node = this.getModelNode(control);
		control.contentEditable = false;
		this.graph.inputEvent = true;
		if (e.keyCode === 27) {
			control.innerHTML = control.oldValue;
			control.oldValue = null;
			return;
		}
		value = control.innerHTML;
		control.oldValue = null;
		while (value.substring(value.length - 4) === "<br>") {
			value = value.substring(0, value.length - 4);
		}
		if (control.typ === "id") {
			node.model.id = value;
		} else if (control.typ === "attribute" || control.typ === "method") {
			t = control.typ + "s";
			for (i = 0; i < node.model[t].length; i += 1) {
				if (node.model[t][i] === control.oldValue) {
					if (value.length > 0) {
						node.model[t][i] = value;
					} else {
						node.model[t].splice(i, 1);
					}
					break;
				}
			}
		} else if (control.typ === "info") {
			node.model.property = value;
		}
		control.innerHTML = value;
	};
	// ################################## CreateEdge ####################################################
	CreateEdge = function (graph) {this.graph = graph; };
	CreateEdge.prototype.addElement = function (element, node) {
		var that = this;
		typ.util.bind(element, "mouseup", function (e) {that.up(e, element, node); });
		typ.util.bind(element, "mousedown", function (e) {that.down(e, element, node); });
	};
	CreateEdge.prototype.down = function (e, element, node) {
		this.fromElement = element;
		this.fromNode = node;
		this.x = e.x;
		this.y = e.y;
	};
	CreateEdge.prototype.up = function (e, element, node) {
		if (!this.fromElement) {
			return;
		}
		if (this.graph.getAction("Selector").node || Math.abs(e.x - this.x) + Math.abs(e.y - this.y) < 10) {
			this.fromElement = null;
			this.fromNode = null;
			return;
		}
		//this.getAction("Selector").setNode(null);
		e.stopPropagation();
		this.toElement = element;
		this.toNode = node;

		var i, div, width = 120, that = this, func;

		if (this.div) {
			return;
		}
		this.list = ["Generalisation", "Assoziation", "Abort"];

		div = this.create({tag: "div", "class": "ChoiceBox", style: {left: e.x, top: e.y, "width": width, zIndex: 6000}});
		func = function () {that.select(this); };

		for (i = 0; i < this.list.length; i += 1) {
			if (i % 2 === 0) {
				this.create({tag: "div", value: this.list[i], $parent: div, onMouseup: func});
			} else {
				this.create({tag: "div", value: this.list[i], "class": "alt", $parent: div, onMouseup: func});
			}
		}
		this.div = div;
		this.graph.board.appendChild(div);
	};
	CreateEdge.prototype.startAction = function (e) {
		if (e.target === this.graph.board && this.div) {
			this.graph.board.removeChild(this.div);
			this.div = null;
		}
	};
	CreateEdge.prototype.select = function (e) {
		var edge, t = e.innerHTML;
		if (t === this.list[0]) {
			edge = this.graph.model.addEdgeModel({"typ": "Generalisation", "source": {id: this.fromNode.id}, target: {id: this.toNode.id}});
			this.graph.drawlines();
		}
		if (t === this.list[1]) {
			edge = this.graph.model.addEdgeModel({"typ": "edge", "source": {id: this.fromNode.id, property: "from"}, target: {id: this.toNode.id, property: "to"}});
			this.graph.drawlines();
		}
		this.graph.board.removeChild(this.div);
		this.div = null;
		this.fromElement = null;
		this.fromNode = null;
		this.toNode = null;
		this.toElement = null;
	};


	global.Graph = typ.Graph;
	global.ClassEditor = typ.ClassEditor;
	svgUtil = new typ.SymbolLibary();
	// Return Definition
	return typ;
}(this));