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
/*global jsPDF: false, svgConverter: false, dagre: false */
/**
 * Creates new Graph document object instance.
 *
 * @class
 * @returns {Graph}
 * @name Graph
 */
var Diagram = (function (global) {
	'use strict';
	var typ, Pos, Loader, Info, Line, DragAndDrop, DagreLayout, Edge, Generalisation, Implements, Unidirectional, Aggregation, Composition, Drawer, HTMLDrawer, SVGDrawer, CreateNode, InputNode, EditNode, CreateEdge, Selector, MoveNode, LocalEditor, ChoiceBox;
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
					this.copy(ref[i], src[i], full);
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
	typ.util.createCell = function (node, table) {
		var tr = this.create({tag: 'tr', $parent: table});
		node.$parent = tr;
		return this.create(node);
	};

	typ.util.create = function (node, parent) {
		var style, item, xmlns, key, tag, k;
		if (document.createElementNS && (node.xmlns || (parent && parent.ns))) {
			if (node.xmlns) {
				xmlns = node.xmlns;
			} else {
				xmlns = parent.ns;
			}
			if (node.tag === "img" && xmlns === "http://www.w3.org/2000/svg") {
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
	typ.util.sizeHTML = function (html, node, model) {
		if (!html) {return; }
		if (model.$parent) {
			return model.$parent.sizeHTML(html, node, model.$parent);
		}
		model.board.appendChild(html);
		var rect = html.getBoundingClientRect();
		model.board.removeChild(html);
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
	typ.util.serializeXmlNode = function (xmlNode) {
		if (window.XMLSerializer !== undefined) {
			return (new window.XMLSerializer()).serializeToString(xmlNode);
		}
		if (xmlNode.xml !== undefined) {
			return xmlNode.xml;
		}
		return xmlNode.outerHTML;
	};
	typ.util.utf8$to$b64 = function (str) {
		return window.btoa(unescape(encodeURIComponent(str)));
	};
	typ.util.getStyleString = function (el) {
		var i, cssList = [], test, value, border, prop;
		border = el.getPropertyValue("border");
		for (i in el) {
			prop = i;
			value = el.getPropertyValue(prop);
			if (value && value !== "") {
				// optimize CSS	
				if (border) {
					if (prop === "border-bottom" || prop === "border-right" || prop === "border-top" || prop === "border-left") {
						if (value !== border) {
							cssList[prop] = value;
						}
					} else if (prop === "border-color" || prop === "border-bottom-color" || prop === "border-right-color" || prop === "border-top-color" || prop === "border-left-color") {
						if (border.substring(border.length - value.length) !== value) {
							cssList[prop] = value;
						}
					} else if (prop === "border-width") {
						if (border.substring(0, value.length) !== value) {
							cssList[prop] = value;
						}
					} else {
						cssList[prop] = value;
					}
				} else {
					cssList[prop] = value;
				}
			}
		}
		return cssList;
	};
	typ.util.getNumber = function (str) {
		return parseInt((str || "0").replace("px", ""), 10);
	};
	typ.util.getStyle = function (styleProp) {
		var i, style, diff, current, ref, el = document.createElement("div"), css = {};
		document.body.appendChild(el);
		ref = typ.util.getStyleString(window.getComputedStyle(el, null));
		style = window.getComputedStyle(el, null);
		el.className = styleProp;
		current = typ.util.getStyleString(style);
		diff = typ.util.getNumber(style.getPropertyValue("border-width"));
		for (i in current) {
			if (i === "width" || i === "height") {
				if (typ.util.getNumber(current[i]) !== 0 && typ.util.getNumber(current[i]) + diff * 2 !== typ.util.getNumber(ref[i])) {
					css[i] = current[i];
				}
			} else if (current[i] !== ref[i]) {
				css[i] = current[i];
			}
		}
		document.body.removeChild(el);
		return css;
	};
	
	//				######################################################### Pos #########################################################
	/**
	* Creates new Pos document object instance. Position with X,Y and ID
	* @class
	* @returns {Pos}
	* @name Pos
	*/
	Pos = function (x, y, id) {this.x = Math.round(x || 0); this.y = Math.round(y || 0); if (id) {this.$id = id; } };

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
	Info.prototype.getX = function () {return this.x; };
	Info.prototype.getY = function () {return this.y; };

	//				######################################################### Line #########################################################
	Line = function (source, target, line, style) {this.source = source; this.target = target; this.line = line; this.style = style; };
	Line.Format = {SOLID: "SOLID", DOTTED: "DOTTED"};

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
		this.$edges = [];
		this.attributes = [];
		this.methods = [];
		this.$parent = null;
		this.x = this.y = this.width = this.height = 0;
		this.$isdraggable = true;
	};
	typ.GraphNode.prototype.getX = function () {if (this.$parent) {return this.$parent.getX() + this.x; } return this.x; };
	typ.GraphNode.prototype.getY = function () {if (this.$parent) {return this.$parent.getY() + this.y; } return this.y; };
	typ.GraphNode.prototype.getEdges = function () {return this.$edges; };
	typ.GraphNode.prototype.clear = function () {this.$RIGHT = this.$LEFT = this.$UP = this.$DOWN = 0; };
	typ.GraphNode.prototype.removeFromBoard = function (board) {if (this.$gui) {board.removeChild(this.$gui); this.$gui = null; } };
	typ.GraphNode.prototype.set = function (id, value) {if (value) {this[id] = value; } };
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

	//				######################################################### GraphModel #########################################################
	typ.GraphModel = function (json, options) {
		this.typ = "classdiagram";
		this.$isdraggable = true;
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
		//if (typeof window[typ] === "function") {
		//	edge = new window[typ]();
		} else {
			edge = new Edge();
		}
		edge.source = new Info(e.source, this, edge);
		edge.target = new Info(e.target, this, edge);
		edge.$sNode = this.getNode(edge.source.id, true);
		edge.$sNode.$edges.push(edge);
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
		edge.$tNode.$edges.push(edge);
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
		node.typ = node.typ.toLowerCase();
		if (!(node.id)) {
			node.id = node.typ + "$" + (this.$nodeCount + 1);
		}
		if (this.nodes[node.id] !== undefined) {
			return this.nodes[node.id];
		}
		if (node.typ.indexOf("diagram", node.typ.length - 7) !== -1) {
			node = new typ.GraphModel(node, new typ.Options());
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
	typ.GraphModel.prototype.createElement = function (element, typ) {this.$parent.createElement(element, typ); };
	typ.GraphModel.prototype.removeFromBoard = function (board) {
		if (this.$gui) {
			board.removeChild(this.$gui);
			this.$gui = null;
		}
	};
	typ.GraphModel.prototype.resize = function (mode) {};
	typ.GraphModel.prototype.getEdges = function () {return this.edges; };
	typ.GraphModel.prototype.calcLines = function (drawer) {
		var i, n, sourcePos, e, ownAssoc = [];
		for (i in this.nodes) {
			if (!this.nodes.hasOwnProperty(i) || typeof (this.nodes[i]) === "function") {
				continue;
			}
			this.nodes[i].clear();
		}
		for (i = 0; i < this.edges.length; i += 1) {
			e = this.edges[i];
			if (!e.calculate(this.$gui, drawer)) {
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

	//				######################################################### Graph #########################################################	
	typ.Graph = function (json, options) {
		this.x = this.y = this.width = this.height = 0;
		json = json || {};
		json.top = json.top || 50;
		json.left = json.left || 10;
		this.model = new typ.GraphModel(json, options);
		this.layouts = [{name: "dagre", value: new DagreLayout()}];
		this.initLayouts();
		this.loader = new Loader(this);
		this.initOption();
	};
	typ.Graph.prototype = new typ.GraphNode();
	typ.Graph.prototype.initOption = function (typ, value) {
		this.init = true;
		if (this.model.options.display.toLowerCase() === "html") {
			this.drawer = new Drawer.HTMLDrawer();
		} else {
			this.initDrawer("svg");
		}
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
			if (this.model.options.canvasid) {
				this.root.id = this.model.options.canvasid;
			}
			document.body.appendChild(this.root);
		}
	};
	typ.Graph.prototype.addOption = function (typ, value) {
		this.model.options[typ] = value;
		this.init = false;
	};
	typ.Graph.prototype.initLayouts = function () {};
	typ.Graph.prototype.initInfo = function (edge, info) {
		if (!this.model.options.CardinalityInfo && !this.model.options.propertyinfo) {
			return null;
		}
		var infoTxt = edge.getInfo(info);
		if (infoTxt.length > 0) {
			typ.util.sizeHTML(this.drawer.getInfo(info, infoTxt, 0), info, this);
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
				e.draw(this.board, this.drawer);
			} else if ((startShow && !endShow) || (!startShow && endShow)) {
				id = e.$sNode.getShowed().id + "-" + e.$tNode.getShowed().id;
				if (items.indexOf(id) < 0) {
					items.push(id);
					e.draw(this.board, this.drawer);
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
		this.drawer.setSize(model.$gui, max.x, max.y);
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
		var i, n, nodes = model.nodes;
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
		model.minSize = new Pos(width || 0, height || 0);
		if (this.loader.abort && this.loader.images.length > 0) {
			return;
		}
		this.resize(model);
		for (i in nodes) {
			if (!nodes.hasOwnProperty(i) || typeof (nodes[i]) === "function") {
				continue;
			}
			n = nodes[i];
			n.$gui = this.drawer.getNode(n, true);
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
			if (typeof (model.nodes[i]) === "function") {
				continue;
			}
			n = model.nodes[i];
			isDiag = n.typ.indexOf("diagram", n.typ.length - 7) !== -1;
			if (isDiag) {
				this.initGraph(n);
			}
			html = this.drawer.getNode(n);
			if (html) {
				typ.util.sizeHTML(html, n, this);
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
		if (!this.init) {
			this.initOption();
		}
		if (model) {
			this.initGraph(model);
		} else {
			model = this.model;
			this.initDrawer();
			this.initGraph(model);
		}
		if (this.loader.images.length < 1) {
			this.layouter.layout(this, model, Math.max(minwidth || 0, 100), Math.max(minHeight || 0, 100));
		} else {
			this.loader.width = minwidth;
			this.loader.height = minHeight;
		}
	};
	typ.Graph.prototype.initDrawer = function (typ) {
		if (typ) {
			typ = typ.toLowerCase();
			if (this.model.options.display === typ) {
				return;
			}
			this.model.options.display = typ;
		} else {
			typ = this.model.options.display;
		}
		this.clearBoard();
		if (typ === "html") {
			this.drawer = new HTMLDrawer();
		} else if (typ === "svg") {
			this.drawer = new SVGDrawer();
		}
		this.board = this.drawer.getBoard(this);
		this.model.$gui = this.board;
		this.DragAndDrop = new DragAndDrop(this);
		this.root.appendChild(this.board);
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
		var data, json = this.model.toJson();
		data = "<html><head>" + document.head.innerHTML.trim() + "</head><body><script>"
			+ "new Graph(" + JSON.stringify(json, null, "\t") + ").layout();<" + "/script></body></html>";
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
	typ.Graph.prototype.createdElement = function (element, type, node) {
		var that = this.DragAndDrop;
		element.node = node;
		typ.util.bind(element, "mousedown", function (e) {that.startDrag(e); });
	};
	//				######################################################### DRAG AND DROP #########################################################
	DragAndDrop = function (parent) {
		this.parent = parent;
		this.objDrag = null;
		this.mouse = new Pos();
		this.offset = new Pos();
		this.startObj = new Pos();
		var that = this;
		typ.util.bind(parent.board, "mousemove", function (e) {that.doDrag(e); });
		typ.util.bind(parent.board, "mouseup", function (e) {that.stopDrag(e); });
		typ.util.bind(parent.board, "mouseout", function (e) {that.stopDrag(e); });
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
		if (!(event.type === "mouseup" || event.type === "mouseout") && !event.currentTarget.isdraggable) {
			return;
		}
		if (event.type === "mouseout") {
			x = this.isIE ? window.event.clientX : event.pageX;
			y = this.isIE ? window.event.clientY : event.pageY;
			if (x < this.board.offsetWidth && y < this.board.offsetHeight) {
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
				if (item.getAttributeNS(null, "transform")) {
					z = item.getAttributeNS(null, "transform");
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
				item.model.$gui = this.parent.drawer.getNode(item.model, true);
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
			node.$gui = this.drawer.getNode(node, draw);
			if (node.$gui) {
				parent.appendChild(node.$gui);
			}
		}
		node.$center = new Pos(node.x + (node.width / 2), node.y + (node.height / 2));
		this.resize(node.$parent);
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
			g.setEdge(this.getRootNode(n.$sNode).id, this.getRootNode(n.$tNode).id);
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
	DagreLayout.prototype.getRootNode = function (node, child) {
		if (node.$parent) {
			return this.getRootNode(node.$parent, node);
		}
		if (!child) {
			return node;
		}
		return child;
	};

	//				######################################################### LINES #########################################################
	//				######################################################### Edge #########################################################
	Edge = function () {
		this.$path = [];
		this.$sNode = null;
		this.$tNode = null;
		this.$gui = [];
		this.$m = 0;
		this.$n = 0;
		this.$lineStyle = Line.Format.SOLID;
		this.typ = "EDGE";
	};
	Edge.Layout = { DIAG : 1, RECT : 0 };
	Edge.Position = {UP: "UP", LEFT: "LEFT", RIGHT: "RIGHT", DOWN: "DOWN"};
	Edge.prototype.set = function (id, value) {if (value) {this[id] = value; } };
	Edge.prototype.addElement = function (board, element) {
		if (element) {this.$gui.push(element); board.appendChild(element); }
	};
	Edge.prototype.removeElement = function (element) {
		if (element) {
			var i;
			for (i = 0; i < this.$gui.length; i += 1) {
				if (this.$gui[i] === element) {
					this.$gui.splice(i, 1);
					i -= 1;
				}
			}
		}
	};
	Edge.prototype.removeFromBoard = function (board) {
		if (this.$gui) {
			while (this.$gui.length > 0) {
				board.removeChild(this.$gui.pop());
			}
		}
	};
	// TODO
	// many Edges SOME DOWN AND SOME RIGHT OR LEFT
	// INFOTEXT DONT SHOW IF NO PLACE
	// INFOTEXT CALCULATE POSITION
	Edge.prototype.calculate = function () {
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
	Edge.prototype.draw = function (board, drawer) {
		var i, style, item, angle;
		for (i = 0; i < this.$path.length; i += 1) {
			item = this.$path[i];
			style = item.style;
			this.addElement(board, drawer.getLine(item.source.x, item.source.y, item.target.x, item.target.y, item.line, style));
		}
		item = drawer.model.options;
		this.drawSourceText(board, drawer, style);
		if (this.info) {
			angle = this.drawText(board, drawer, this.info, this.infoPos);
			this.addElement(board, new typ.SymbolLibary().create({typ: "Arrow", x: this.infoPos.x, y: this.infoPos.y, rotate: angle}, drawer));
		}
		this.drawTargetText(board, drawer, style);
	};
	Edge.prototype.drawText = function (board, drawer, text, pos, style) {
		if (this.$path.length < 1) {
			return;
		}
		if (text.length < 1) {
			return;
		}
		var options, angle, p = this.$path[this.$path.length - 1];
		options = drawer.model.model.options;
		if (options.rotatetext) {
			angle = Math.atan((p.source.y - p.target.y) / (p.source.x - p.target.x)) * 60;
		}
		this.addElement(board, drawer.getInfo(pos, text, angle, style));
		return angle;
	};
	Edge.prototype.drawSourceText = function (board, drawer, style) {
		var infoTxt = this.getInfo(this.source);
		this.drawText(board, drawer, infoTxt, this.source, style);
	};
	Edge.prototype.drawTargetText = function (board, drawer, style) {
		var infoTxt = this.getInfo(this.target);
		this.drawText(board, drawer, infoTxt, this.target, style);
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
	Edge.prototype.getInfo = function (info) {
		var isProperty, isCardinality, infoTxt = "";
		isCardinality = this.$parent.typ === "classdiagram" && this.$parent.options.CardinalityInfo;
		isProperty = this.$parent.options.propertyinfo;

		if (isProperty && info.property) {
			infoTxt = info.property;
		}
		if (isCardinality && info.cardinality) {
			if (infoTxt.length > 0) {
				infoTxt += "\n";
			}
			if (info.cardinality.toLowerCase() === "one") {
				infoTxt += "0..1";
			} else if (info.cardinality.toLowerCase() === "many") {
				infoTxt += "0..*";
			}
		}
		if (info.edge && info.edge.counter && info.edge.counter > 0) {
			infoTxt += " (" + info.edge.counter + ")";
		}
		return infoTxt;
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
			if (node.hasOwnProperty("$" + i) && node["$" + i] === 0) {
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
	Generalisation.prototype.calculate = function (board, drawer) {
		if (!Edge.prototype.calculate.call(this, board, drawer)) {
			return false;
		}
		this.calcMoveLine(16, 50, true);
		return true;
	};
	Generalisation.prototype.draw = function (board, drawer) {
		Edge.prototype.draw.call(this, board, drawer);
		if (this.$path.length > 0) {
			this.addElement(board, drawer.getLine(this.$top.x, this.$top.y, this.$end.x, this.$end.y, this.$lineStyle));
			this.addElement(board, drawer.getLine(this.$bot.x, this.$bot.y, this.$end.x, this.$end.y, this.$lineStyle));
			this.addElement(board, drawer.getLine(this.$top.x, this.$top.y, this.$bot.x, this.$bot.y, this.$lineStyle));
		}
	};
	Generalisation.prototype.drawSourceText = function (board, drawer, style) {};
	Generalisation.prototype.drawTargetText = function (board, drawer, style) {};

	//				######################################################### Implements #########################################################
	Implements = function () { Edge.call(this); this.typ = "Implements"; this.$lineStyle = Line.Format.DOTTED; };
	Implements.prototype = new Generalisation();

	//				######################################################### Unidirectional #########################################################
	Unidirectional = function () { Edge.call(this); this.typ = "Unidirectional"; };
	Unidirectional.prototype = new Generalisation();
	Unidirectional.prototype.calculate = function (board, drawer) {
		if (!Edge.prototype.calculate.call(this, board, drawer)) {
			return false;
		}
		this.calcMoveLine(16, 50, false);
		return true;
	};
	Unidirectional.prototype.draw = function (board, drawer) {
		Edge.prototype.draw.call(this, board, drawer);
		this.addElement(board, drawer.getLine(this.$top.x, this.$top.y, this.$end.x, this.$end.y, this.$lineStyle));
		this.addElement(board, drawer.getLine(this.$bot.x, this.$bot.y, this.$end.x, this.$end.y, this.$lineStyle));
	};

	//				######################################################### Aggregation #########################################################
	Aggregation = function () { Edge.call(this); this.typ = "Aggregation"; };
	Aggregation.prototype = new Generalisation();
	Aggregation.prototype.calculate = function (board, drawer) {
		if (!Edge.prototype.calculate.call(this, board, drawer)) {
			return false;
		}
		this.calcMoveLine(16, 49.8, true);
		return true;
	};
	Aggregation.prototype.draw = function (board, drawer) {
		Edge.prototype.draw.call(this, board, drawer);
		this.addElement(board, drawer.createPath(true, "none", [this.endPos().target, this.$topCenter, this.$end, this.$botCenter]));
	};

	//				######################################################### Composition #########################################################
	Composition = function () { Edge.call(this); this.typ = "Composition"; };
	Composition.prototype = new Aggregation();
	Composition.prototype.draw = function (board, drawer) {
		Edge.prototype.draw.call(this, board, drawer);
		var lineangle, start = this.$path[0].source;
		lineangle = Math.atan2(this.$end.y - start.y, this.$end.x - start.x);
		this.addElement(board, drawer.createPath(true, "#000", [this.endPos().target, this.$topCenter, this.$end, this.$botCenter], lineangle));
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
	typ.SymbolLibary.prototype.create = function (node, drawer) {
		if (this.isSymbol(node)) {
			return this.draw(drawer, node);
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
	typ.SymbolLibary.prototype.draw = function (drawer, node) {
		var group, board, item, fn = this[this.getName(node)];
		if (typeof fn === "function") {
			group = fn.apply(this, [node]);
			if (!drawer || typeof drawer.createGroup !== "function") {
				drawer = new SVGDrawer();
				drawer.showButton = false;
				board = drawer.getBoard(null);
				board.setAttribute("style", "border:none;");
				drawer.setSize(board, node.width + node.x + 10, node.height + node.y + 10);
				item = drawer.createGroup(node, group, board);
				board.appendChild(item);
				return board;
			}
			return drawer.createGroup(node, group);
		}
	};
	typ.SymbolLibary.prototype.drawSmily = function (node) {
		return {
			x: node.x || 0,
			y: node.y || 0,
			width: 100,
			height: 100,
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
			width: 100,
			height: 100,
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
			width: 100,
			height: 50,
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
			width: 20,
			height: 20,
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
				{tag: "rect", rx: 8, x: 0, y: 0, width: btnWidth, height: btnHeight, stroke: "#000", filter: "url(#drop-shadow)", "class": "saveBtn"},
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
				{tag: "rect", rx: 2, x: btnWidth - 20, y: 0, width: 20, height: 28, stroke: "#000", "class": "saveBtn"},
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

	//				######################################################### Drawer #########################################################
	Drawer = function () {this.symbolLib = new typ.SymbolLibary(); };
	Drawer.prototype.clearBoard = function () {};
	Drawer.prototype.setPos = function (item, x, y) {item.x = x; item.y = y; };
	Drawer.prototype.setSize = function (item, x, y) {item.width = x; item.height = y; };
	Drawer.prototype.getColor = function (style, defaultColor) {
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
	Drawer.prototype.removeToolItems = function (board) {
		var i;
		for (i = 0; i < this.toolitems.length; i += 1) {
			this.toolitems[i].close();
			if (this.toolitems[i].showed) {
				board.removeChild(this.toolitems[i]);
				this.toolitems[i].showed = false;
			}
		}
	};
	Drawer.prototype.createImage = function (node) {
		var n, img;
		node.model = node;
		if (this.symbolLib.isSymbol(node)) {
			return this.symbolLib.draw(null, node);
		}
		n = {tag: "img", model: node, src: node.src};
		if (node.width || node.height) {
			n.width = node.width;
			n.height = node.height;
		} else {
			n.xmlns = "http://www.w3.org/1999/xhtml";
		}
		img = typ.util.create(n, this);
		if (!node.width && !node.height) {
			this.model.appendImage(img);
			return null;
		}
		return img;
	};
	Drawer.prototype.showToolItems = function (board) {
		var i;
		for (i = 0; i < this.toolitems.length; i += 1) {
			board.appendChild(this.toolitems[i]);
			this.toolitems[i].showed = true;
		}
	};
	Drawer.prototype.isInTool = function (x, y, ox, oy) {
		var i, g, gx, gy, gw, gh;
		// Mode x,y
		x -= ox;
		y -= oy;
		for (i = 0; i < this.toolitems.length; i += 1) {
			g = this.toolitems[i];
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
	Drawer.prototype.createBoard = function (node, graph, listener) {
		var i, that = this, board;
		this.model = graph;
		this.toolitems = [];
		if (listener) {
			for (i = 0; i < listener.length; i += 1) {
				this.toolitems.push(listener[i]);
			}
		}
		board = typ.util.create(node, this);
		node.model = graph;
		board.setAttribute('class', "Board");
		board.rasterElements = [];
		board.saveShow = false;
		board.onmouseover = function () {
			that.showToolItems(board);
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
			if (!that.isInTool(x, y, left, top)) {
				that.removeToolItems(board);
			}
		};
		return board;
	};
	Drawer.prototype.getButtons = function (graph, notTyp) {
		var i, buttons = [], btn, func, that = this, item, type, node;
		if (graph && graph.model.options) {
			item = graph.model.options.buttons;
			func = function (e) {
				var t = e.currentTarget.typ;
				that.model.initDrawer(t);
				that.model.layout();
			};
			for (i = 0; i < item.length; i += 1) {
				type = item[i];
				if (type !== notTyp) {
					node = {typ: "Button", value: type, y: 8, x: 2, height: 28, width: 60};
					btn = this.symbolLib.draw(this, node);
					btn.style.verticalAlign = "top";
					typ.util.bind(btn, "mousedown", func);
					btn.typ = type;
					buttons.push(btn);
				}
			}
		}
		if (notTyp === "HTML" && !graph.noButtons && graph.model.id) {
			func = function (e) {
				var t = e.currentTarget.value;
				if (t === "Save") {
					that.model.SavePosition();
				} else if (t === "Load") {
					that.model.LoadPosition();
				}
			};
			btn = {typ: "Dropdown", x: 2, y: 8, width: 120, elements: ["Save", "Load"], activText: "Localstorage", action: func};
			item = this.symbolLib.draw(this, btn);
			buttons.push(item);
		}
		return buttons;
	};
	//				###################################################### HTMLDrawer ####################################################################################
	HTMLDrawer = function () {};
	HTMLDrawer.prototype = new Drawer();
	HTMLDrawer.prototype.setPos = function (item, x, y) {item.style.left = x + "px"; item.style.top = y + "px"; };
	HTMLDrawer.prototype.setSize = function (item, x, y) {item.style.width = x + "px"; item.style.height = y + "px"; };
	HTMLDrawer.prototype.getSize = function (item) {return {x: item.clientWidth, y: item.clientHeight}; };
	HTMLDrawer.prototype.getBoard = function (graph) {
		return this.createBoard({tag: "div"}, graph, this.getButtons(graph, "HTML"));
	};
	HTMLDrawer.prototype.createCell = function (parent, tag, node, innerHTML, type) {
		var tr = typ.util.create({"tag": 'tr'}, this), cell;
		cell = typ.util.create({"tag": tag, $font: true, value: innerHTML}, this);
		this.model.createdElement(cell, type, node);
		tr.appendChild(cell);
		parent.appendChild(tr);
		return cell;
	};
	HTMLDrawer.prototype.getNode = function (node, draw) {
		var first, z, cell, item, model, htmlElement = typ.util.create({tag: "div", model: node}, this);
		model = this.model.model;
		if (node.typ === "patternobject") {
			htmlElement.className = "patternElement";
		} else if (this.symbolLib.isSymbol(node)) {
			return this.symbolLib.draw(null, node);
		}
		if (node.typ === "classdiagram") {
			htmlElement.className = "classdiagram";
		} else if (node.typ === "objectdiagram") {
			htmlElement.className = "objectdiagram";
		} else if (model.typ.toLowerCase() === "objectdiagram") {
			htmlElement.className = "objectElement";
		} else {
			htmlElement.className = "classElement";
		}
		this.setPos(htmlElement, node.x, node.y);
		htmlElement.style.zIndex = 5000;

		if (node.typ === "objectdiagram" || node.typ === "classdiagram") {
			node.left = node.top = 30;
			node.$gui = htmlElement;
			if (draw) {
				this.model.draw(node);
				htmlElement.style.borderColor = "red";
				if (node.style && node.style.toLowerCase() === "nac") {
					htmlElement.appendChild(this.symbolLib.draw(null, {typ: "stop", x: 0, y: 0}));
				}
			} else {
				this.model.layout(0, 0, node);
			}
			this.setSize(htmlElement, node.$gui.style.width, node.$gui.style.height);
			return htmlElement;
		}
		this.model.createdElement(htmlElement, "class", node);
		if (node.content) {
			node.content.width = node.content.width || 0;
			node.content.height = node.content.height || 0;
			if (node.content.src) {
				item = this.createImage(node.content);
				if (!item) {return null; }
				htmlElement.appendChild(item);
				return htmlElement;
			}
			if (node.content.html) {
				htmlElement.innerHTML = node.content.html;
				return htmlElement;
			}
		}
		item = typ.util.create({tag: 'table', border: "0"}, this);
		item.style.width = "100%";
		item.style.height = "100%";
		htmlElement.appendChild(item);
		if (node.head$src) {
			cell = this.createCell(item, "td", node);
			cell.style.textAlign = "center";
			if (!node.head$img) {
				node.head$img = {};
				node.head$img.src = node.head$src;
				node.head$img.width = node.head$width;
				node.head$img.height = node.head$height;
			}
			z = this.createImage(node.head$img);
			if (z) {
				cell.appendChild(z);
			}
		}
		if (node.headinfo) {
			this.createCell(item, "td", node, node.headinfo).className = "head";
		}

		if (model.typ.toLowerCase() === "objectdiagram") {
			z = node.id.charAt(0).toLowerCase() + node.id.slice(1);
		} else {
			z = node.id;
		}
		if (node.href) {
			z = "<a href=\"" + node.href + "\">" + z + "</a>";
		}
		cell = this.createCell(item, "th", node, z, "id");
		if (model.typ.toLowerCase() === "objectdiagram") {
			cell.style.textDecorationLine = "underline";
		}
		cell = null;
		if (node.attributes) {
			first = true;
			for (z = 0; z < node.attributes.length; z += 1) {
				cell = this.createCell(item, "td", node, node.attributes[z], "attribute");
				if (!first) {
					cell.className = 'attributes';
				} else {
					cell.className = 'attributes first';
					first = false;
				}
			}
		}
		if (node.methods) {
			first = true;
			for (z = 0; z < node.methods.length; z += 1) {
				cell = this.createCell(item, "td", node, node.methods[z], "method");
				if (!first) {
					cell.className = 'methods';
				} else {
					cell.className = 'methods first';
					first = false;
				}
			}
		}
		if (!cell) {
			cell = this.createCell(item, "td", node, "&nbsp;");
			cell.className = 'first';
			this.model.createdElement(cell, "empty", node);
		}
		htmlElement.appendChild(item);
		htmlElement.node = node;
		node.$gui = htmlElement;
		return htmlElement;
	};
	HTMLDrawer.prototype.getInfo = function (item, text, angle, style) {
		var info = typ.util.create({tag: "div", $font: true, model: item, "class": "EdgeInfo", value: text, style: "color:" + this.getColor(style, "#CCC")}, this);

		if (angle !== 0) {
			info.style.transform = "rotate(" + angle + "deg)";
			info.style.msTransform = info.style.MozTransform = info.style.WebkitTransform = info.style.OTransform = "rotate(" + angle + "deg)";
		}
		this.setPos(info, item.x, item.y);
		this.model.createdElement(info, "info", item);
		return info;
	};
	HTMLDrawer.prototype.getLine = function (x1, y1, x2, y2, lineStyle) {
		var temp, angle, length, line;
		if (x2 < x1) {
			temp = x1;
			x1 = x2;
			x2 = temp;
			temp = y1;
			y1 = y2;
			y2 = temp;
		}
		// Formula for the distance between two points
		// http://www.mathopenref.com/coorddist.html
		length = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

		line = typ.util.create({tag: "div", "class": "lineElement", style: {width: length + "px", position: "absolute", zIndex: 42}}, this);
		line.style.borderBottomStyle = lineStyle;

		angle = Math.atan((y1 - y2) / (x1 - x2));
		if (x1 === x2) {
			angle = Math.atan((y1 - y2) / (x1 - x2)) * -1;
		}
		line.style.top = y1 + 0.5 * length * Math.sin(angle) + "px";
		line.style.left = x1 - 0.5 * length * (1 - Math.cos(angle)) + "px";
		line.style.transform = "rotate(" + angle + "rad)";
		line.style.msTransform = line.style.MozTransform = line.style.WebkitTransform = line.style.OTransform = "rotate(" + angle + "rad)";
		return line;
	};
	HTMLDrawer.prototype.createPath = function (close, fill, path, angle) {
		var line, i;
		if (fill === "none") {
			line = typ.util.create({tag: "div"}, this);
			for (i = 1; i < path.length; i += 1) {
				line.appendChild(this.getLine(path[i - 1].x, path[i - 1].y, path[i].x, path[i].y));
			}
			if (close) {
				line.appendChild(this.getLine(path[path.length - 1].x, path[path.length - 1].y, path[0].x, path[0].y));
			}
			return line;
		}
		line = typ.util.create({tag: "div", style: {position: "absolute", left: path[0].x - 8, top: path[0].y, transform: "rotate(" + angle + "rad)"}}, this);
		line.appendChild(typ.util.create({tag: "div", style: {background: "#000", width: 8, height: 8, transform: "rotate(45rad) skew(170deg, 170deg)"}}, this));
		return line;
	};
//				###################################################### SVG ####################################################################################
	SVGDrawer = function () {this.ns = "http://www.w3.org/2000/svg"; this.showButton = true; };
	SVGDrawer.prototype = new Drawer();
	SVGDrawer.prototype.getWidth = function (label) {
		var board, width, text = typ.util.create({tag: "text", $font: true, value: label}, this);
		text.setAttribute("width", "5px");
		board = this.model.board;
		board.appendChild(text);
		width = text.getBoundingClientRect().width;
		board.removeChild(text);
		return width;
	};
	SVGDrawer.prototype.drawDef = function () {
		var child, def = typ.util.create({tag: "defs"}, this);

		child = typ.util.create({tag: "filter", id: "drop-shadow"}, this);
		child.appendChild(typ.util.create({tag: "feGaussianBlur", "in": "SourceAlpha", result: "blur-out", stdDeviation: 2}, this));
		child.appendChild(typ.util.create({tag: "feOffset", "in": "blur-out", dx: 2, dy: 2}, this));
		child.appendChild(typ.util.create({tag: "feBlend", "in": "SourceGraphic", mode: "normal"}, this));
		def.appendChild(child);
		child = typ.util.create({tag: "linearGradient", id: "reflect", x1: "0%", x2: "0%", y1: "50%", y2: "0%", spreadMethod: "reflect"}, this);
		child.appendChild(typ.util.create({tag: "stop", "stop-color": "#aaa", offset: "0%"}, this));
		child.appendChild(typ.util.create({tag: "stop", "stop-color": "#eee", offset: "100%"}, this));
		def.appendChild(child);

		child = typ.util.create({tag: "linearGradient", id: "classelement", x1: "0%", x2: "100%", y1: "100%", y2: "0%"}, this);
		child.appendChild(typ.util.create({tag: "stop", "stop-color": "#ffffff", offset: "0"}, this));
		child.appendChild(typ.util.create({tag: "stop", "stop-color": "#d3d3d3", offset: "1"}, this));
		def.appendChild(child);
		return def;
	};
	SVGDrawer.prototype.getBoard = function (graph) {
		var hasJS, buttons, board, node, list, that = this;
		list = ["HTML", "SVG", "PNG"];
		hasJS = typeof (svgConverter);
		if (hasJS !== "undefined") {
			hasJS = typeof (svgConverter);
			list.push(hasJS !== "undefined" ? "EPS" : "");
			hasJS = typeof (jsPDF);
			list.push(hasJS !== "undefined" ? "PDF" : "");
		}
		buttons = [];


		if (this.showButton) {
			buttons = this.getButtons(graph, "SVG");
			node = {typ: "Dropdown", x: 66, y: 8, minheight: 28, maxheight: 28, width: 80, elements: list, activText: "Save", action: function (e) {that.removeToolItems(that.board); that.model.SaveAs(e.currentTarget.value); }};
			buttons.push(this.symbolLib.draw(this, node));
		}
		board = this.createBoard({tag: "svg", "xmlns:svg": "http://www.w3.org/2000/svg", "xmlns:xlink": "http://www.w3.org/1999/xlink"}, graph, buttons);
		board.appendChild(this.drawDef());
		this.board = board;

		return board;
	};
	SVGDrawer.prototype.setSize = function (item, x, y) {
		x = typ.util.getValue(x);
		y = typ.util.getValue(y);
		item.setAttribute("width", x);
		item.setAttribute("height", y);
		item.style.width = Math.ceil(x);
		item.style.height = Math.ceil(y);
	};
	SVGDrawer.prototype.getNode = function (node, draw) {
		var rect, type, z, x, y, id, textWidth, g, item, width, height, that = this, symbolLib = new typ.SymbolLibary(), create;
		create = typ.util.create;
		if (symbolLib.isSymbol(node)) {
			return symbolLib.draw(this, node);
		}
		if (node.content) {
			node.content.width = node.content.width || 0;
			node.content.height = node.content.height || 0;
			if (node.content.src) {
				item = this.createImage(node.content);
				if (!item) {return null; }
				return item;
			}
			g = create({tag: "g", model: node}, this);
			if (node.content.svg) {
				g.setAttribute('transform', "translate(" + node.x + " " + node.y + ")");
				g.innerHTML = node.content$svg;
				return g;
			}
			if (node.content.html) {
				g.setAttribute('transform', "translate(" + node.x + " " + node.y + ")");
				g.innerHTML = node.content$svg;
				return g;
			}
		}
		g = create({tag: "g", model: node}, this);
		if (node.typ === "objectdiagram" || node.typ === "classdiagram") {
			if (node.status === "close") {
				width = this.getWidth(node.minid || node.id) + 30;
				height = 40;
				this.addChild(node, g, create({tag: "text", $font: true, "text-anchor": "left", "x": (node.x + 2), "y": node.y + 12, value: node.minid || node.id }, this));
			} else {
				node.left = node.top = 30;
				node.$gui = g;
				if (draw) {
					this.model.draw(node);
				} else {
					this.model.layout(0, 0, node);
				}

				width = typ.util.getValue(node.$gui.style.width);
				height = typ.util.getValue(node.$gui.style.height);
				if (node.style && node.style.toLowerCase() === "nac") {
					this.addChild(node, g, this.createGroup(node, symbolLib.drawStop(node)));
				}
			}
			this.setSize(g, width, height);
			this.addChild(node, g, create({tag: "rect", "width": width, "height": height, "fill": "none", "strokeWidth": "1px", "stroke": this.getColor(node.style, "#CCC"), "x": node.getX(), "y": node.getY(), "class": "draggable"}, this));
			if (width > 0 && width !== node.width) {node.width = width; }
			if (node.status === "close") {
				// Open Button
				item = this.createGroup(node, symbolLib.drawMax({x: (node.x + width - 20), y: node.y}));
				node.height = height;
			} else {
				item = this.createGroup(node, symbolLib.drawMin({x: (node.x + width - 20), y: node.y}));
			}
			item.setAttribute("class", "hand");

			typ.util.bind(item, "mousedown", function (e) {
				var name;
				if (node.status === "close") {
					node.status = "open";
					that.model.redrawNode(node);
				} else {
					node.status = "close";
					// try to cleanup
					for (name in node.nodes) {
						if (node.nodes.hasOwnProperty(name)) {
							node.nodes[name].$gui = null;
						}
					}
					that.model.redrawNode(node);
				}
				if (e.stopPropagation) {e.stopPropagation(); }
				if (e.cancelBubble !== null) {e.cancelBubble = true; }
			});
			g.appendChild(item);
			this.model.createdElement(g, "class", node);
			return g;
		}

		if (node.content$plain) {
			return create({tag: "text", $font: true, "text-anchor": "left", "x": (node.x + 10), value: node.content$plain}, this);
		}

		width = 0;
		height = 40;

		if (this.model.model.typ.toLowerCase() === "objectdiagram") {
			id = node.id.charAt(0).toLowerCase() + node.id.slice(1);
		} else {
			id = node.id;
			if (node.counter) {
				id += " (" + node.counter + ")";
			}
		}
		textWidth = this.getWidth(id);

		width = Math.max(width, textWidth);
		if (node.attributes && node.attributes.length > 0) {
			height = height + node.attributes.length * 25;
			for (z = 0; z < node.attributes.length; z += 1) {
				width = Math.max(width, this.getWidth(node.attributes[z]));
			}
		} else {
			height += 20;
		}
		if (node.methods && node.methods.length > 0) {
			height = height + node.methods.length * 25;
			for (z = 0; z < node.methods.length; z += 1) {
				width = Math.max(width, this.getWidth(node.methods[z]));
			}
		}
		width += 20;

		y = node.getY();
		x = node.getX();

		this.model.createdElement(g, "class", node);
		rect = {tag: "rect", "width": width, "height": height, "x": x, "y": y, "fill": "#fff", "class": "draggable"};
		type = node.typ.toLowerCase();
		if (type === "patternobject") {
			rect.fill = "lightblue";
		}

		rect.stroke = this.getColor(node.style);
		g.appendChild(create(rect, this));

		if (type !== "patternobject") {
			g.appendChild(create({tag: "rect", rx: 0, "x": x, "y": y, "width": width, height: 30, fill: "none", style: "fill:url(#classelement);"}, this));
		}

		item = create({tag: "text", $font: true, "text-anchor": "right", "x": x + width / 2 - textWidth / 2, "y": y + 20, "width": textWidth}, this);

		if (this.model.model.typ.toLowerCase() === "objectdiagram") {
			item.setAttribute("text-decoration", "underline");
		}
		item.appendChild(document.createTextNode(id));

		g.appendChild(item);
		g.appendChild(create({tag: "line", x1: x, y1: y + 30, x2: x + width, y2: y + 30, stroke: rect.stroke}, this));
		y += 50;

		if (node.attributes) {
			for (z = 0; z < node.attributes.length; z += 1) {
				g.appendChild(create({tag: "text", $font: true, "text-anchor": "left", "width": width, "x": (x + 10), "y": y, value: node.attributes[z]}, this));
				y += 20;
			}
			if (node.attributes.length > 0) {
				y -= 10;
			}
		}
		if (node.methods && node.methods.length > 0) {
			g.appendChild(create({tag: "line", x1: x, y1: y, x2: x + width, y2: y, stroke: "#000"}, this));
			y += 20;
			for (z = 0; z < node.methods.length; z += 1) {
				g.appendChild(create({tag: "text", $font: true, "text-anchor": "left", "width": width, "x": x + 10, "y": y, value: node.methods[z]}, this));
				y += 20;
			}
		}
		return g;
	};
	SVGDrawer.prototype.addChild = function (node, parent, child) {
		child.setAttribute("class", "draggable");
		parent.appendChild(child);
		this.model.createdElement(child, "class", node);
	};
	SVGDrawer.prototype.getInfo = function (item, text, angle, style) {
		var child, group, i, items = text.split("\n"), create = typ.util.create;
		if (items.length > 1) {
			group = this.util.create({tag: "g", "class": "draggable", rotate: angle, model: item}, this);
			for (i = 0; i < items.length; i += 1) {
				child = this.util.create({tag: "text", $font: true, "text-anchor": "left", "x": item.x, "y": item.y + (item.height * i), fill: this.getColor(style, "#CCC")}, this);
				child.appendChild(document.createTextNode(items[i]));
				group.appendChild(child);
			}
			this.model.createdElement(group, "info", item);
			return group;
		}
		group = create({tag: "text", "#$font": true, "text-anchor": "left", "x": item.x, "y": item.y, value: text, "id": item.id, "class": "draggable", rotate: angle, model: item, fill: this.getColor(style, "#CCC")}, this);
		this.model.createdElement(group, "info", item);
		return group;
	};
	SVGDrawer.prototype.getLine = function (x1, y1, x2, y2, lineStyle, style) {
		var line = typ.util.create({tag: "line", 'x1': x1, 'y1': y1, 'x2': x2, 'y2': y2, "stroke": this.getColor(style)}, this);
		if (lineStyle && lineStyle.toLowerCase() === "dotted") {
			line.setAttribute("stroke-miterlimit", "4");
			line.setAttribute("stroke-dasharray", "1,1");
		}
		return line;
	};
	SVGDrawer.prototype.createPath = function (close, fill, path, angle) {
		var i, d = "M" + path[0].x + " " + path[0].y;
		for (i = 1; i < path.length; i += 1) {
			d = d + "L " + path[i].x + " " + path[i].y;
		}
		if (close) {
			d = d + " Z";
		}
		return typ.util.create({tag: "path", "d": d, "fill": fill, stroke: "#000", "stroke-width": "1px"}, this);
	};
	SVGDrawer.prototype.createGroup = function (node, group, parent) {
		var func, y, yr, z, box, item, transform, that = this, i, g, create = typ.util.create, offsetX = 0, offsetY = 0;
		g = create({tag: "g"}, this);
		if (parent) {
			offsetX = group.x;
			offsetY = group.y;
		} else {
			parent = g;
		}
		transform = "translate(" + group.x + " " + group.y + ")";
		if (group.scale) { transform += " scale(" + group.scale + ")"; }
		if (group.rotate) { transform += " rotate(" + group.rotate + ")"; }
		g.setAttribute('transform', transform);
		g.setAttribute("height", group.height);
		g.setAttribute("width", group.width);

		for (i = 0; i < group.items.length; i += 1) {
			g.appendChild(create(group.items[i], this));
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
			box = create({tag: "g"}, this);
			z = node.elements.length * 25 + 6;
			box.appendChild(create({tag: "rect", rx: 0, x: offsetX, y: (offsetY + 28), width: 60, height: z, stroke: "#000", fill: "#fff", opacity: "0.7"}, this));
			node.maxheight = z + node.minheight;

			parent.elements = node.elements;
			parent.activ = create({tag: "text", $font: true, "text-anchor": "left", "width": 60, "x": (10 + offsetX), "y": 20, value: node.activText}, this);
			g.appendChild(parent.activ);
			y = offsetY + 46;
			yr = offsetY + 28;

			func = function (event) {
				parent.activ.textContent = event.currentTarget.value;
			};
			for (z = 0; z < node.elements.length; z += 1) {
				box.appendChild(create({tag: "text", $font: true, "text-anchor": "left", "width": 60, "x": 10, "y": y, value: node.elements[z]}, this));
				item = box.appendChild(create({tag: "rect", rx: 0, x: offsetX, y: yr, width: 60, height: 24, stroke: "none", "class": "selection"}, this));
				item.value = node.elements[z];
				if (node.action) {
					item.onclick = node.action;
				} else {
					item.onclick = func;
				}
				y += 26;
				yr += 26;
			}
			parent.choicebox = box;
		}
		parent.tool = node;
		parent.onclick = function () {
			if (parent.status === "close") {
				parent.open();
			} else {
				parent.close();
			}
		};
		parent.close = function () {
			if (parent.status === "open") {
				this.removeChild(parent.choicebox);
			}
			parent.status = "close";
			parent.tool.height = parent.tool.minheight;
			that.setSize(parent, parent.tool.width + parent.tool.x + 10, parent.tool.height + parent.tool.y + 10);
		};
		parent.open = function () {
			if (this.tagName === "svg") {
				return;
			}
			if (parent.status === "close") {
				this.appendChild(parent.choicebox);
			}
			parent.status = "open";
			parent.tool.height = parent.tool.maxheight;
			that.setSize(parent, parent.tool.width + parent.tool.x + 10, parent.tool.height + parent.tool.y + 10);
		};
		parent.close();

		return g;
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
		this.drawer = new HTMLDrawer();
		this.inputEvent = true;
		this.nodes = {};
		this.noButtons = true;
		this.model = new typ.GraphModel(this, {buttons: [], typ: diagramTyp});
		if (element) {
			if (typeof (element) === "string") {
				this.board = this.drawer.getBoard(this);
				this.board.className = "ClassEditor";
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
		java.exit();
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
			this.addNode(this.model.nodes[i]);
		}
		for (i in model.nodes) {
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
		data = JSON.stringify(result, null, "    ");
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
			this.model.edges[i].draw(this.board, this.drawer);
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
		var n = this.nodes.assoc, symbolLib, that = this;
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
	// Return Definition
	return typ;
}(this));