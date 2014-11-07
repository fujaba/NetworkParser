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
Object_create = Object.create || function (o) { var F = function() {};F.prototype = o; return new F();};
IE = document.all&&!window.opera;
DOM = document.getElementById&&!IE;

/* Pos */
Pos = function(x, y, id) {this.x = x || 0; this.y = y || 0; if(id){this.id = id;} }

/* Info */
Info = function(info, parent, edge) { 
	this.property = info.property; 
	this.cardinality = info.cardinality; 
	this.id = info.id; 
	this.typ = "Info";
	this.x = this.y = this.width = this.height = 0;
	this.center=new Pos();
	this.custom = false;
	this.parent = parent;
	this.edge = edge;
	this.isdraggable = true;
};

Line = function(source, target, line, style) {this.source = source; this.target = target; this.line = line; this.style = style;}
Line.Format={SOLID:"SOLID", DOTTED:"DOTTED"};
/* Options */
Options = function(){
	this.canvasid = null;
	this.parent = null;
	this.subgraphs = [];
	this.layout= "Dagre";
	this.raster = false;
	this.display = "svg";
	this.font={};
	this.font["font-size"] = "10px";
	this.font["font-family"] = "Verdana";
	this.rank = "TB";			// Dagre TB, LR
	this.nodeSep = 10;
	this.CardinalityInfo = true;
	this.PropertyInfo = true;
	/*this.buttons = ["HTML", "SVG", "CANVAS", "PNG", "PDF"];*/
	this.buttons = [];
	this.design="color" // Flat, Color
}

/* Node */
GraphNode = function(id) {
	this.init();
	this.typ = "node";
	this.id = id;
}
GraphNode.prototype.init = function() {
	this.x = this.y = this.width = this.height=0;
	this.edges = [];
	this.attributes = [];
	this.methods = [];
	this.RIGHT = this.LEFT = this.UP = this.DOWN=0;
	this.isdraggable = true;
};

GraphNode.prototype.removeFromBoard = function(board){
	if(this.htmlNode){
		board.removeChild(this.htmlNode);
		this.htmlNode = null;
	}
};
GraphNode.prototype.getX = function(){
	if(this.parent){
		return this.x + this.parent.getX();
	}
	return this.x;
};
GraphNode.prototype.getY = function(){
	if(this.parent){
		return this.y + this.parent.getY();
	}
	return this.y;
};
GraphNode.prototype.getRoot = function() {
	if(this.parent){
		return this.parent.getRoot();
	}
	return this;
}

/* Graph */
Graph = function(json, options) {
	this.init();
	this.nodeCount=0;
	this.nodes = {};
	this.layouts = 
	this.edges = [];
	this.typ = json.typ;
	this.initLayouts();
	if(json.info){
		this.info = json.info;
	}
	if(json.style){
		this.style = json.style;
	}
	this.options = this.merge(new Options(), json.options, options);
	this.parent = this.options.parent;
	this.loader = new Loader(this);
	if((""+this.options.display).toLowerCase()=="html"){
		this.drawer = new HTMLDrawer();
		this.loader.init(true);
	}else{
		this.drawer = new SVGDrawer();
		this.options.display = "svg";
	}
	var layout = this.layouts[0];

	for(var i=0;i<this.layouts.length;i++){
		if(this.layouts[i]["name"] === this.options.layout.toLowerCase()){
			layout = this.layouts[i];
			break;
		}
	}
	this.layouter = layout.value;

	if(json.nodes) {
		for (var i = 0; i < json.nodes.length; i++) {
			this.addNode(json.nodes[i], i);
		}
	}
	if(json.edges) {
		for (var i = 0; i < json.edges.length; i++){
			var e = json.edges[i];
			var edge;
			if(e.typ.toLowerCase()=="generalisation"){
				edge = new Generalisation();
			}else if(e.typ.toLowerCase()=="implements"){
				edge = new Implements();
			}else{
				edge = new Edge();
			}
			edge.source = this.getNode(e.source.id);
			edge.info = e.info;
			edge.style = e.style;
			edge.sourceInfo = new Info(e.source, this, edge);
			edge.targetInfo = new Info(e.target, this, edge);
			edge.source.edges.push(edge);
			edge.model = this;

			edge.target = this.getNode(e.target.id);
			edge.target.edges.push(edge);
			this.edges.push(edge);
		}
	}
	if(!this.options.parent){
		if(this.options.canvasid){
			this.root = document.getElementById(this.options.canvasid);
		}
		if(!this.root){
			this.root = document.createElement("div");
			if(this.options.canvasid){
				this.root.id = this.options.canvasid;
			}
			document.body.appendChild(this.root);document.body.appendChild(this.root);
		}
		if(this.options.buttons.length>0){
			this.optionbar = document.createElement("div");
			this.optionbar.className = "Options";
			this.root.appendChild(this.optionbar);
			for(var i=0; i< this.options.buttons.length; i++){
				this.optionbar.appendChild(this.getButton(this.options.buttons[i]));
			}
			this.optionbar.appendChild(document.createElement("br"));
		}
		this.initGraph();
	}
};
Graph.prototype = Object_create(GraphNode.prototype);
Graph.prototype.initLayouts=function(){ this.layouts=[{name:"dagre", value:new DagreLayout()}];};
Graph.prototype.copy = function(source, target){
	for (var key in source) {
		target[key] = source[key];
	}
	if(source.width){
		target.startWidth = source.width;
	}
	if(source.height){
		node.startHeight = node.height;
	}
	return target;
};
Graph.prototype.removeFromBoard = function(board){
	if(this.htmlNode){
		board.removeChild(this.htmlNode);
		this.htmlNode = null;
	}
	this.board = null;
};
Graph.prototype.merge = function(ref, sourceA, sourceB) {
	if(sourceA){
		for(var i in sourceA){
			ref[i] = sourceA[i];
		}
	}
	if(sourceB){
		for(var i in sourceB){
			ref[i] = sourceB[i];
		}
	}
	return ref;
};
Graph.prototype.initGraph = function(){
	if(this.root){
		this.clearBoard();
		this.board = this.drawer.createContainer(this);
		this.initDragAndDrop();
		this.root.appendChild(this.board);
	}

	for (var i in this.nodes) {
		var node = this.nodes[i];
		if(node.typ=="objectdiagram" || node.typ=="classdiagram"){
			node.root = this.board;
			node.initDrawer(this.options.display);
			node.drawer.model = node;
			node.initGraph();
		}
		var html = this.drawer.getNode(node, true);
		if(html){
			var pos = this.getDimension(html);
			if(!node.startWidth){
				node.width=pos.x;
			}
			if(!node.startHeight){
				node.height=pos.y;
			}
		}
		if(node.typ=="objectdiagram" || node.typ=="classdiagram"){
			node.center = new Pos(node.x + (node.width / 2), node.y + (node.height / 2));
			node.board=null;
		}
	}

	for (var i=0; i<this.edges.length;i++) {
		var edge = this.edges[i];
		this.initInfo(edge, edge.sourceInfo);
		this.initInfo(edge, edge.targetInfo);
	}
	this.drawer.clearBoard();
};
Graph.prototype.initInfo = function(edge, info){
	if(!this.options.CardinalityInfo && !this.options.PropertyInfo){
		return null;
	}	
	var infoTxt = edge.getInfo(info);
	if(infoTxt.length > 0) {
		var html = this.drawer.createInfo(info, true, infoTxt);
		if(html){
			var pos = this.getDimension(html);
			info.width = pos.x;
			info.height = pos.y;
		}
	}
	return infoTxt;
};
Graph.prototype.clearBoard = function(){
	if(this.board){
		this.clearLines();
		for(var i in this.nodes) {
			var n=this.nodes[i];
			n.removeFromBoard(this.board);
			n.RIGHT = n.LEFT = n.UP = n.DOWN=0;
		}
		this.root.removeChild(this.board);
	}
	this.drawer.clearBoard();
};
Graph.prototype.getDimension = function(html){
	if(this.parent){
		return this.parent.getDimension(html);
	}
	if(!this.board){
		return new Pos();
	}
	this.board.appendChild(html);
	var rect = html.getBoundingClientRect();
	var pos = new Pos(rect.width, rect.height);
	this.board.removeChild(html);
	return pos;
};
Graph.prototype.getButton = function(label){
	var button = document.createElement("button");
	button.innerHTML = label;
	button.className="ToolButton";
	button.model = this;
	var that = this;
	bindEvent(button, "click", function(e){that.setTyp(e.innerHTML);});
	return button;
};
Graph.prototype.getNode = function(id, isSub) {
	if(this.nodes[id]) {
		return this.nodes[id];
	}
	for(var i = 0;i < this.options.subgraphs.length;i++){
		var r = this.options.subgraphs[i].getNode(id, true);
		if(r) {
			return r;
		}
	}
	if(!isSub){
		this.nodes[id] = new GraphNode(id);
		this.nodes[id].parent = this;
		this.nodeCount++;
		return this.nodes[id];
	}
	return null;
};
Graph.prototype.addSubGraph = function(subgraph) {
	this.options.subgraphs.push(subgraph);
	if(this.parent) {
		this.parent.addSubGraph(subgraph);
	}
};

Graph.prototype.addNode = function(node, pos) {
	/* testing if node is already existing in the graph */
	node.typ = node.typ.toLowerCase();
	if(node.typ=="objectdiagram" || node.typ=="classdiagram") {
		if(!this.options) {
			return;
		}
		var options = new Options();
		options.parent = this;
		options = this.merge(options, node.options);
		node = new Graph(node, options);
		options.rootElement = node;
		this.addSubGraph(node);
	}else {
		node = this.copy(node, new GraphNode());
	}
	if(!(node.id)){
		node.id = node.typ+"_"+(pos || 0);
	}
	if(this.nodes[node.id] == undefined) {
		this.nodes[node.id] = node;
		node.parent = this;
		this.nodeCount++;
	}
	return this.nodes[node.id];
};
Graph.prototype.addEdge = function(source, target) {
	var edge = new Edge();
	edge.source = this.addNode(source);
	edge.target = this.addNode(target);
	edge.source.edges.push(edge);
	this.edges.push(edge);
	// NOTE: Even directed edges are added to both nodes.
	edge.target.edges.push(edge);
};
Graph.prototype.removeNode = function(id) {
	if(this.nodes[id].htmlNode){
		this.board.removeChild(this.nodes[id].htmlNode);
	}
	delete(this.nodes[id]);

	for(var i = 0; i < this.edges.length; i++) {
		if (this.edges[i].source.id == id || this.edges[i].target.id == id) {
			this.edges.splice(i, 1);
			i--;
		}
	}
};
Graph.prototype.calcLines = function(){
	var ownAssoc = [];
	for(var i in this.nodes) {
		this.nodes[i].RIGHT = this.nodes[i].LEFT = this.nodes[i].UP = this.nodes[i].DOWN=0;
	}
	for(var i = 0; i < this.edges.length; i++) {
		if(!this.edges[i].calculate(this.board, this.drawer)){
			ownAssoc.push(this.edges[i]);
		}
	}
	for(var i = 0; i < ownAssoc.length; i++) {
		ownAssoc[i].calcOwnEdge();
		var sourcePos = ownAssoc[i].getCenterPosition(ownAssoc[i].source, ownAssoc[i].start);
		ownAssoc[i].calcInfoPos( sourcePos, ownAssoc[i].source, ownAssoc[i].sourceInfo);
		
		sourcePos = ownAssoc[i].getCenterPosition(ownAssoc[i].target, ownAssoc[i].end);
		ownAssoc[i].calcInfoPos( sourcePos, ownAssoc[i].target, ownAssoc[i].targetInfo);
	}
};
Graph.prototype.drawLines = function(){
	this.clearLines();
	for(var i = 0; i < this.edges.length; i++) {
		this.edges[i].draw(this.board, this.drawer);
	}
};
Graph.prototype.clearLines = function(){
	for(var i=0; i<this.edges.length;i++) {
		var edge = this.edges[i];
		if(edge.htmlElement){
			while(edge.htmlElement.length>0){
				this.board.removeChild(edge.htmlElement.pop());
			}
		}
	}
	
};
Graph.prototype.MinMax = function(node, min, max){
	max.x = Math.max(max.x,node.x+node.width+10);
	max.y=Math.max(max.y,node.y+node.height+10);
	min.x=Math.max(min.x,node.x);
	min.y=Math.max(min.y,node.y);
};
Graph.prototype.resize = function(){
	var min=new Pos();
	var max=new Pos(this.minSize.x, this.minSize.y);
	for (var i in this.nodes) {
		var node = this.nodes[i];
		this.moveToRaster(node);
		this.MinMax(node, min, max);
	}
	this.calcLines();
	for(var i=0;i<this.edges.length;i++){
		var edge=this.edges[i];
		this.MinMax(edge.sourceInfo, min, max);
		this.MinMax(edge.targetInfo, min, max);
	}
	this.drawer.setSize(this.board, max.x, max.y);
	if(this.options.raster){
		this.drawRaster();
	}
	this.drawLines();
	return max;
};
Graph.prototype.drawRaster = function(){
	while(this.board.rasterElements.length>0){
		this.board.removeChild(this.board.rasterElements.pop());
	}
	var width = this.board.style.width.replace("px","");
	var height = this.board.style.height.replace("px","");
	for(var i=10;i<width;i+=10){
		var line = this.drawer.createLine(i, 0, i, height, null, "#ccc");
		line.className="lineRaster";
		this.board.rasterElements.push(line);
		this.board.appendChild(line);
	}
	for(var i=10;i<height;i+=10){
		var line = this.drawer.createLine(0, i, width, i, null, "#ccc");
		line.className="lineRaster";
		this.board.rasterElements.push(line);
		this.board.appendChild(line);
	}
};
Graph.prototype.drawGraph = function(width, height){
	this.minSize = new Pos(width, height);
	if(this.loader.abort && this.loader.images.length>0){
		return;
	}
	if(!this.board) {
		this.initGraph();
	}
	this.resize();

	for(var i in this.nodes) {
		var node = this.nodes[i];
		node.htmlNode = this.drawer.getNode(node, false);
		if(node.htmlNode){
			this.board.appendChild( node.htmlNode );
		}
	}
};
Graph.prototype.moveToRaster = function(node){
	if(this.options.raster){
		node.x = parseInt(node.x / 10) * 10;
		node.y = parseInt(node.y / 10) * 10;
		if(node.htmlNode){
			this.drawer.setPos(node.htmlNode, node.x, node.y);
		}
	}
}

Graph.prototype.layout = function(minwidth, minHeight){
	this.layouter.layout(this, Math.max(minwidth || 0, 100), Math.max(minHeight || 0, 100));
}
Graph.prototype.layouting = function(){
	this.initGraph();
	this.layout(this.minSize.x, this.minSize.y);
}
//				######################################################### DRAG AND DROP #########################################################
Graph.prototype.initDragAndDrop = function(){
	this.objDrag = null;
	this.mouse = new Pos();
	this.offset= new Pos();
	this.startObj= new Pos();
	var that = this;
	bindEvent(this.board, "mousemove", function(e){that.doDrag(e);});
	bindEvent(this.board, "mouseup", function(e){that.stopDrag(e);});
	bindEvent(this.board, "mouseout", function(e){that.stopDrag(e);});
};
Graph.prototype.addNodeLister = function(element, node){
	var that = this;
	element.node = node;
	bindEvent(element, "mousedown", function(e){that.startDrag(e);});
};
Graph.prototype.showinfo = function(event){
	var objElem = event.currentTarget;
	var node=this.getGraphNode(objElem);
	if(node){
		var x = Math.round( objElem.style.left.substring(0,objElem.style.left.length-2) * 100)/100;
		var y = Math.round( objElem.style.top.substring(0,objElem.style.top.length-2) * 100)/100;
		node.parent.showInfoText("Box-Position: " + x + ":" + y);
	}
};
Graph.prototype.setSelectable = function(node, value) {
	if (node.nodeType == 1) {
		if(value){
			node.setAttribute("unselectable", value);
		}else{
			node.removeAttribute("unselectable");
		}
	}
	var child = node.firstChild;
	while (child) {
		this.setSelectable(child, value);
		child = child.nextSibling;
	}
};
Graph.prototype.getDragNode = function(node) {
	if(node.model){
		if(!node.model.isdraggable){
			return null;
		}
		return node;
	}else if(node.parentElement.model) {
		if(!node.parentElement.model.isdraggable){
			return null;
		}
		return node.parentElement;
	}
	return null;
}
Graph.prototype.startDrag = function(event) {
	var n = this.getDragNode(event.currentTarget);
	if(!n){
		return;
	}
	if(this.objDrag){
		return;
	}
	this.objDrag = n;
	var graph = this.objDrag.parentElement;
	if(graph) {
		for(var i=0;i<graph.children.length;i++) {
			this.setSelectable(graph.children[i], "on");
		}
	}
	this.offset.x = (IE) ? window.event.clientX : event.pageX;
	this.offset.y = (IE) ? window.event.clientY : event.pageY;
	this.startObj.x = this.objDrag.model.x;
	this.startObj.y = this.objDrag.model.y;
};

Graph.prototype.doDrag = function(event) {
	this.mouse.x = (IE) ? window.event.clientX : event.pageX;
	this.mouse.y = (IE) ? window.event.clientY : event.pageY;

	if (this.objDrag != null) {
		var x =(this.mouse.x - this.offset.x) + this.startObj.x;
		var y =(this.mouse.y - this.offset.y) + this.startObj.y;

		if(this.options.display=="svg"){
			x = x - this.startObj.x;
			y = y - this.startObj.y;
			this.objDrag.setAttribute('transform', "translate("+x+" "+y+")");
		} else {
			this.drawer.setPos(this.objDrag, x, y);
			if(this.objDrag.model){
				this.objDrag.model.x = x;
				this.objDrag.model.y = y;
				this.objDrag.model.parent.resize();
			}
		}
	}
}
Graph.prototype.stopDrag = function(event) {
	if(!this.objDrag){
		return;
	}
	if(!(event.type=="mouseup"||event.type=="mouseout")&&!event.currentTarget.isdraggable){
		return;
	}
	var item = this.objDrag;
	this.objDrag = null;
	var graph = item.parentElement;
	if(graph) {
		for(var i=0;i<graph.children.length;i++) {
			this.setSelectable(graph.children[i], null);
		}
	}
	if(item.model){
		if(this.options.display=="svg"){
			if(item.getAttributeNS(null, "transform")){
				var pos = item.getAttributeNS(null, "transform").slice(10,-1).split(' ');
				item.model.x = item.model.x + Number(pos[0]);
				item.model.y = item.model.y + Number(pos[1]);
			}
			item.model.center = new Pos(item.model.x + (item.model.width / 2), item.model.y + (item.model.height / 2));
			
			this.board.removeChild(item);
			if(item.model.board) {
				item.model.board = null;
			}

			if(item.model.typ=="Info") {
				item.model.custom = true;
				item.model.edge.removeElement(item);
				var infoTxt = item.model.edge.getInfo(item.model);
				item.model.edge.addElement(this.board, this.drawer.createInfo(item.model, false, infoTxt));
			}else{
				item.model.htmlNode = this.drawer.getNode(item.model, false);
				if(item.model.htmlNode){
					this.board.appendChild( item.model.htmlNode );
				}
				for(var i=0;i<item.model.edges.length;i++){
					var edge = item.model.edges[i];
					edge.sourceInfo.custom = false;
					edge.targetInfo.custom = false;
				}
			}
		}
		item.model.parent.resize();
	}
};
Graph.prototype.redrawNode = function(node){
	this.board.removeChild(node.htmlNode);
	if(node.board) {
		node.board = null;
	}
	if(node.typ=="Info") {
		var infoTxt = node.edge.getInfo(node.node);
		node.edge.addElement(this.board, this.drawer.createInfo(node, false, infoTxt));
	}else{
		node.htmlNode = this.drawer.getNode(node, false);
		if(node.htmlNode){
			this.board.appendChild( node.htmlNode );
		}
	}
	node.center = new Pos(node.x + (node.width / 2), node.y + (node.height / 2));
	node.parent.resize();
};

Graph.prototype.getGraphNode = function(objElement){
	var obj=objElement;
	while(obj&&!obj.node){
		if(!obj.node){
			obj=obj.parentNode;
		}else{
			break;
		}
	}
	if(obj&&obj.node){
		return obj.node;
	}
	return null;
};
Graph.prototype.initDrawer = function(typ){
	typ = typ.toLowerCase();
	this.options.display = typ;
	if(typ=="html"){
		this.drawer = new HTMLDrawer();
	}else if(typ=="svg"){
		this.drawer = new SVGDrawer();
	}else if(typ=="canvas"){
		this.drawer = new CanvasDrawer();
	}
};
Graph.prototype.setTyp = function(typ){
	if(typ=="HTML"){
		this.initDrawer(typ);
		this.loader.init(true);
		this.initGraph();
		this.drawGraph(0,0);
	}else if(typ=="SVG"){
		this.initDrawer(typ);
		this.initGraph();
		this.drawGraph(0,0);
	}else if(typ=="SVG-Export"){
		this.drawer = new SVGDrawer();
		this.initGraph();
		this.drawGraph(0,0);
		var size = this.resize();
		var img = document.createElement("img");
		img.src = "data:image/svg+xml;base64," + this.utf8_to_b64(this.serializeXmlNode(this.board));
		this.clearBoard();
		this.board = img;
		this.board.width = size.x;
		this.board.height = size.y;
		this.root.appendChild(img);
	}else if(typ=="CANVAS"){
		this.initDrawer(typ);
		this.initGraph();
		this.drawGraph(0,0)
	}else if(typ=="PNG"){
		var oldDrawer = this.drawer;
		this.drawer = new CanvasDrawer();
		this.loader.init(false);
		this.loader.oldDrawer = oldDrawer;
		this.initGraph();
		this.drawGraph(0,0);
		this.loader.resetDrawer();
	}else if(typ=="PDF"){
		this.ExportPDF();
	}
}
Graph.prototype.serializeXmlNode = function(xmlNode) {
	if (typeof window.XMLSerializer != "undefined") {
		return (new window.XMLSerializer()).serializeToString(xmlNode);
	} else if (typeof xmlNode.xml != "undefined") {
		return xmlNode.xml;
	}
	return xmlNode.outerHTML;
}
Graph.prototype.utf8_to_b64 = function( str ) {
	return window.btoa(unescape(encodeURIComponent( str )));
}
Graph.prototype.ExportPDF = function () {
	var pdf = new jsPDF('l', 'px', 'a4');
	svgElementToPdf(this.board, pdf, {removeInvalid: true});
	pdf.save('Download.pdf');
};
Graph.prototype.ExportPNG = function () {
	var image = new Image();
	image.src = 'data:image/svg+xml;base64,' + this.utf8_to_b64(this.serializeXmlNode(this.board));
	var that = this;
	image.onload = function(e) {
		var canvas = document.createElement('canvas');
		canvas.width = image.width;
		canvas.height = image.height;
		var context = canvas.getContext('2d');
		context.drawImage(image, 0, 0);
		var a = document.createElement('a');
		a.download = "download.png";
		a.href = canvas.toDataURL('image/png');
		a.click();
	};
}
Graph.prototype.SaveAs = function (typ) {
	typ = typ.toLowerCase();
	if(typ=="svg") {
		this.Save("image/svg+xml", this.serializeXmlNode(this.board), "download.svg");
	}else if(typ=="html") {
		this.ExportHTML();
	}else if(typ=="htmlx") {
		this.ExportHTMLStandalone();
	}else if(typ=="png") {
		this.ExportPNG();
	}else if(typ=="pdf") {
		this.ExportPDF();
	}
};
Graph.prototype.Save = function (typ, data, name) {
	var a = document.createElement("a");
	var url = window.URL.createObjectURL(new Blob([data], {type: typ}));
	a.href = url;
	a.download = name;
	a.click();
}
Graph.prototype.ExportHTML = function () {
	var json = this.getHTML();
	var data="<html><head>"+document.head.innerHTML.trim()+"</head><body><script>"
		+"new Graph("+JSON.stringify(json, null, "\t") +").layout();</script></body></html>";
	this.Save("text/json", data, "download.html");
}
Graph.prototype.ExportHTMLStandalone = function () {
	var json = this.getHTML();

	var data="<html><head>"+document.head.innerHTML.trim()+"</head><body><script>"
		+"new Graph("+JSON.stringify(json, null, "\t") +").layout();</script></body></html>";
	this.Save("text/json", data, "download.html");
}
Graph.prototype.getHTML = function () {
	var result = {};
	result.typ = this.typ;
	result.options = {};

	for (var key in this.options) {
		if(key!="subgraphs" && key!="parent" && this.options[key] != null) {
			result.options[key] = this.options[key];
		}
	}
	var items = [];
	var add = false;
	for (var i in this.nodes) {
		var n = this.nodes[i];
		var newNode = {typ:n.typ, id:n.id, x: n.x, y:n.y, width:n.width, height:n.height, style:n.style };
		if(n instanceof GraphNode) {
			if(n.attributes && n.attributes.length > 0){
				newNode.attributes=[];
				for(var a=0;a<n.attributes.length;a++){
					newNode.attributes.push(n.attributes[a]);
				}
			}
			if(n.methods && n.methods.length > 0){
				newNode.methods=[];
				for(var m=0;m<n.methods.length;m++){
					newNode.methods.push(n.methods[m]);
				}
			}
		}
		if(n instanceof Graph) {
			var sub = n.getHTML();
			this.copy(sub, newNode);
		}
		items.push(newNode);
		add = true;
	}
	if(add){result.nodes=items;}
	items =[];add=false;
	for (var i=0;i< this.edges.length;i++) {
		var e = this.edges[i];
		var newEdge = {typ:e.typ, source: {id: e.source.id}, target: {id: e.target.id}};

		if(e.sourceInfo.cardinality){
			newEdge.source["cardinality"] = e.sourceInfo.cardinality;
		}
		if(e.sourceInfo.property){
			newEdge.source["property"] = e.sourceInfo.property;
		}
		if(e.targetInfo.cardinality){
			newEdge.target["cardinality"] = e.targetInfo.cardinality;
		}
		if(e.targetInfo.property){
			newEdge.target["property"] = e.targetInfo.property;
		}
		items.push(newEdge);
		add = true;
	}
	if(add){result.edges=items;}
	return result;
};

//				######################################################### GraphLayout-Dagre #########################################################
DagreLayout = function() {};
DagreLayout.prototype.layout = function(graph, width, height) {
	this.graph = graph;
	this.g = new dagre.graphlib.Graph({nodesep:this.graph.options.nodeSep, rankDir:this.graph.options.rank, directed:false});
	this.g.setGraph({});
	this.g.setDefaultEdgeLabel(function() { return {}; });
	for (var i in this.graph.nodes) {
		var node = this.graph.nodes[i];
		this.g.setNode(node.id, {label: node.id, width:node.width, height:node.height, x:node.x, y:node.y});
		
	}
	for (var i = 0; i < this.graph.edges.length; i++) {
		var edges = this.graph.edges[i];
		this.g.setEdge(this.getRootNode(edges.source).id, this.getRootNode(edges.target).id);
	}

	dagre.layout(this.g);
	// Set the layouting back
	for (var i in this.graph.nodes) {
		var node = this.graph.nodes[i];
		var layoutNode = this.g.node(node.id);
		node.x = layoutNode.x;
		node.y = layoutNode.y;
	}
	this.graph.drawGraph(width, height);
}
DagreLayout.prototype.getRootNode = function(node, child) {
	if(node.parent){
		return this.getRootNode(node.parent, node);
	}
	if(!child){
		return node;
	}
	return child;
};
//				######################################################### Loader #########################################################
Loader = function(graph) {this.init(false);this.graph=graph;};
Loader.prototype.init = function(abort){
	this.images = [];
	this.abort=abort;
}
Loader.prototype.resetDrawer = function(){
	if(this.images.length==0){
		this.graph.drawer.onFinishImage();
	}else{
		var img = this.images.pop();
		this.graph.root.appendChild(img);
	}
};
Loader.prototype.remove = function(img){this.images.remove(img);}
Loader.prototype.onLoad = function(img){
	var idx = this.images.indexOf(img);
	if (idx != -1) {
		this.images.splice(idx, 1);
	}
	if(this.images.length==0){
		this.graph.drawer.onFinishImage();
	}
};
Loader.prototype.appendImg = function(img){
	img.crossOrigin = 'anonymous';
	var that = this.graph.drawer;
	img.onload = function(e){that.onLoadImage(e);};
	this.images.push(img);
}

//				######################################################### LINES #########################################################
Edge = function() {this.init();this.typ="EDGE";}
Edge.prototype.init = function(){
	this.path = [];
	this.source=null;
	this.target=null;
	this.start = null;
	this.end = null;
	this.htmlElement = new Array();
	this.m = 0;
	this.n = 0;
	this.lineStyle = Line.Format.SOLID;
};
Edge.Layout ={ DIAG : 1, RECT : 0 };
Edge.Position={UP:"UP", LEFT:"LEFT", RIGHT:"RIGHT", DOWN:"DOWN"};

Edge.prototype.addElement = function(board, element){
	if(element){
		this.htmlElement.push(element);board.appendChild(element);
	}
};
Edge.prototype.removeElement = function(element){
	if(element){
		for(var i=0;i<this.htmlElement.length;i++){
			if(this.htmlElement[i]==element){
				this.htmlElement.splice(i, 1);
				i--;
			}
		}
	}
};
Edge.prototype.calculate = function(board, drawer){
	this.source.center = new Pos(this.source.getX() + (this.source.width / 2), this.source.getY() + (this.source.height / 2));
	this.target.center = new Pos(this.target.getX() + (this.target.width / 2), this.target.getY() + (this.target.height / 2));
	return this.calcCenterLine();
}
Edge.prototype.draw = function(board, drawer){
	for(var i=0;i<this.path.length;i++){
		var p = this.path[i];
		this.addElement(board, drawer.createLine(p.source.x, p.source.y, p.target.x, p.target.y, p.line, p.style));
	}
	var options = drawer.model.options;
	this.drawSourceText(board, drawer, options);
	this.drawTargetText(board, drawer, options);
};
Edge.prototype.drawSourceText = function(board, drawer, options){
	var infoTxt = this.getInfo(this.sourceInfo);
	if(infoTxt.length > 0 ){
		this.addElement(board, drawer.createInfo(this.sourceInfo, false, infoTxt));
	}
}
Edge.prototype.drawTargetText = function(board, drawer, options){
	var infoTxt = this.getInfo(this.targetInfo);
	if(infoTxt.length > 0 ){
		this.addElement(board, drawer.createInfo(this.targetInfo, false, infoTxt));
	}
}
Edge.prototype.endPos = function(){return this.path[this.path.length-1];}
Edge.prototype.edgePosition = function() {
	var pos=0;
	for(var i=0;i < this.source.edges.length; i++) {
		if(this.source.edges[i] == this){
			return pos;
		}
		if(this.source.edges[i].target == this.target){
			pos ++;
		}
	}
	return pos;
};
Edge.prototype.getTarget = function(node, startNode){
	if(node instanceof Graph){
		if(node.status=="close") {
			return node;
		}
		return startNode;
	}
	return this.getTarget(node.parent, startNode);
};
Edge.prototype.calcCenterLine = function(){
	var divisor = (this.target.center.x - this.source.center.x);
	var sourcePos,targetPos;
	var edgePos = this.edgePosition() * 20;

	this.path = new Array();
	var source = this.getTarget(this.source, this.source), target = this.getTarget(this.target, this.target);
	if(divisor==0){
		if(this.source==this.target){
			/* OwnAssoc */
			return false;
		}
		// Must be UP_DOWN or DOWN_UP
		if(this.source.center.y<this.target.center.y){
			// UP_DOWN
			sourcePos = this.getCenterPosition(source, Edge.Position.DOWN, edgePos);
			targetPos = this.getCenterPosition(target, Edge.Position.UP, edgePos);
		}else{
			sourcePos = this.getCenterPosition(source, Edge.Position.UP, edgePos);
			targetPos = this.getCenterPosition(target, Edge.Position.DOWN, edgePos);
		}
	}else{
		this.m = (target.center.y - source.center.y) / divisor;
		this.n = source.center.y - (source.center.x * this.m);
		sourcePos = this.getPosition(this.m,this.n, source, target.center, edgePos);
		targetPos = this.getPosition(this.m,this.n, target, sourcePos, edgePos);
	}
	if(sourcePos&&targetPos){
		this.calcInfoPos( sourcePos, source, this.sourceInfo, edgePos);
		this.calcInfoPos( targetPos, target, this.targetInfo, edgePos);
		this.addEdgeToNode(source, sourcePos.id);
		this.addEdgeToNode(target, targetPos.id);
		this.path.push ( new Line(sourcePos, targetPos, this.lineStyle, this.style));
	}
	return true;
};
Edge.prototype.getCenterPosition = function(node, pos, offset){
	offset = offset || 0;
	if(pos==Edge.Position.DOWN){
		return new Pos(node.center.x + offset, (node.y+node.height), Edge.Position.DOWN);
	}
	if(pos==Edge.Position.UP){
		return new Pos(node.center.x + offset, node.y, Edge.Position.UP);
	}
	if(pos==Edge.Position.LEFT){
		return new Pos(node.x, node.center.y + offset, Edge.Position.LEFT);
	}
	if(pos==Edge.Position.RIGHT){
		return new Pos(node.x+node.width, node.center.y + offset, Edge.Position.RIGHT);
	}
}
Edge.prototype.getInfo = function(info){
	var infoTxt = "";
	var isCardinality = this.model.typ=="classdiagram" && this.model.options.CardinalityInfo;
	var isProperty = this.model.options.PropertyInfo;

	if(isProperty && info.property){
		infoTxt = info.property;
	}
	if(isCardinality && info.cardinality){
		if(infoTxt.length > 0 ){
			infoTxt += "\n";
		}
		if(info.cardinality.toLowerCase() == "one"){
			infoTxt += "0..1";
		}else if(info.cardinality.toLowerCase() == "many"){
			infoTxt += "0..*";
		}
	}
	return infoTxt;
}
Edge.prototype.calcOwnEdge = function(){
	//this.source
	var offset = 20;
	this.start = this.getFree(this.source);
	if(this.start.length>0){
		this.end = this.getFreeOwn(this.source, this.start);
	}else{
		this.start = Edge.Position.RIGHT;
		this.end = Edge.Position.DOWN;
	}

	var sPos = this.getCenterPosition(this.source, this.start);
	var tPos;
	if(this.start==Edge.Position.UP){
		tPos = new Pos(sPos.x, sPos.y - offset);
	}else if(this.start==Edge.Position.DOWN){
		tPos = new Pos(sPos.x, sPos.y + offset);
	}else if(this.start==Edge.Position.RIGHT){
		tPos = new Pos(sPos.x + offset, sPos.y);
	}else if(this.start==Edge.Position.LEFT){
		tPos = new Pos(sPos.x - offset, sPos.y);
	}
	this.path.push (new Line(sPos, tPos, this.lineStyle));
	if(this.end==Edge.Position.LEFT || this.end==Edge.Position.RIGHT){
		if(this.start==Edge.Position.LEFT){
			sPos = tPos;
			tPos = new Pos(sPos.x, this.source.y - offset);
			this.path.push (new Line(sPos, tPos, this.lineStyle));
		}else if(this.start==Edge.Position.RIGHT){
			sPos = tPos;
			tPos = new Pos(sPos.x, this.source.y + offset);
			this.path.push (new Line(sPos, tPos, this.lineStyle));
		}
		sPos = tPos;
		if(this.end==Edge.Position.LEFT){
			tPos = new Pos(this.source.x - offset, sPos.y);
		}else{
			tPos = new Pos(this.source.x + this.source.width + offset, sPos.y);
		}
		this.path.push (new Line(sPos, tPos, this.lineStyle));
		sPos = tPos;
		tPos = new Pos(sPos.x, this.source.center.y);
		this.path.push (new Line(sPos, tPos, this.lineStyle));
	}else if(this.end==Edge.Position.UP || this.end==Edge.Position.DOWN){
		if(this.start==Edge.Position.UP){
			sPos = tPos;
			tPos = new Pos(this.source.x + this.source.width + offset, sPos.y);
			this.path.push (new Line(sPos, tPos, this.lineStyle));
		}else if(this.start==Edge.Position.DOWN){
			sPos = tPos;
			tPos = new Pos(this.source.x - offset, sPos.y);
			this.path.push (new Line(sPos, tPos, this.lineStyle));
		}
		sPos = tPos;
		if(this.end==Edge.Position.UP){
			tPos = new Pos(sPos.x, this.source.y - offset);
		}else{
			tPos = new Pos(sPos.x, this.source.y + this.source.height + offset);
		}
		this.path.push (new Line(sPos, tPos, this.lineStyle));
		sPos = tPos;
		tPos = new Pos(this.source.center.x, sPos.y);
		this.path.push (new Line(sPos, tPos, this.lineStyle));
	}
	sPos = tPos;
	this.path.push (new Line(sPos, this.getCenterPosition(this.source, this.end), this.lineStyle));
};
Edge.prototype.addEdgeToNode=function(node, pos){
	if(pos==Edge.Position.UP){
		node.UP+=1;
	}else if(pos==Edge.Position.DOWN){
		node.DOWN+=1;
	}else if(pos==Edge.Position.RIGHT){
		node.RIGHT+=1;
	}else if(pos==Edge.Position.LEFT){
		node.LEFT+=1;
	}
};
Edge.prototype.getFree = function(node){
	if(node.UP==0 ){
		node.UP +=1;
		return Edge.Position.UP;
	}else if(node.RIGHT==0 ){
		node.RIGHT +=1;
		return Edge.Position.RIGHT;
	}else if(node.DOWN==0 ){
		node.DOWN +=1;
		return Edge.Position.DOWN;
	}else if(node.LEFT==0 ){
		node.LEFT +=1;
		return Edge.Position.LEFT;
	}
	return "";
}

Edge.prototype.getFreeOwn = function(node, start){
	var list = new Array(Edge.Position.UP, Edge.Position.RIGHT, Edge.Position.DOWN, 
		Edge.Position.LEFT, Edge.Position.UP, Edge.Position.RIGHT, Edge.Position.DOWN);
	var result = new Array();
	var id=0;
	for(var i=0;i<list.length;i++) {
		if(list[i]==start) {
			id =i;
			break;
		}
	}
	if(node[list[id + 1]] == 0 || node[list[id + 1]] < node[list[id + 3]]) {
		node[list[id + 1]] ++;
		return list[id + 1];
	}
	node[list[id + 3]]++;
	return list[id + 3];
}
Edge.prototype.calcInfoPos = function(linePos, item, info, offset){
	// Manuell move the InfoTag
	offset = offset || 0;
	var spaceA = 20;
	var spaceB = 10;
	if(info.custom){
		return;
	}
	var newY = linePos.y;
	var newX = linePos.x;
	var yoffset = 0;
	if(linePos.id==Edge.Position.UP){
		newY = newY - info.height - offset - spaceA;
		if(this.m!=0){
			newX = (newY-this.n) / this.m + spaceB;
		}
	}else if(linePos.id==Edge.Position.DOWN){
		newY = newY + offset + spaceA;
		if(this.m!=0){
			newX = (newY-this.n) / this.m + spaceB;
		}
	}else if(linePos.id==Edge.Position.LEFT){
		newX = newX - info.width - offset - spaceA;
		if(this.m!=0){
			newY = (this.m * newX)+ this.n;
		}
	}else if(linePos.id==Edge.Position.RIGHT){
		newX += offset + spaceA;
		if(this.m!=0){
			newY = (this.m * newX)+ this.n;
		}
	}
	info.id = linePos.id;
	info.x = newX;
	info.y = newY;
};
Edge.prototype.getPosition= function(m , n, entity, refCenter, offset){
	if (!offset) {
		offset = 0;
	}
	var x,y;
	var pos=new Array();
	var distance=new Array();
	x = entity.getX()+entity.width;
	y = m*x+n;
	if(y>=entity.getY() && y<=(entity.getY()+entity.height)){
		pos.push(new Pos(x , y + offset, Edge.Position.RIGHT));
		distance.push(Math.sqrt((refCenter.x-x)*(refCenter.x-x)+(refCenter.y-y)*(refCenter.y-y)));
	}
	y = entity.getY();
	x = (y-n)/m;
	if(x>=entity.getX() && x<=(entity.getX()+entity.width)){
		pos.push(new Pos(x + offset, y, Edge.Position.UP));
		distance.push(Math.sqrt((refCenter.x-x)*(refCenter.x-x)+(refCenter.y-y)*(refCenter.y-y)));
	}
	x = entity.getX();
	y = m*x+n;
	if(y>=entity.getY() && y<=(entity.getY()+entity.height)){
		pos.push(new Pos(x , y + offset, Edge.Position.LEFT));
		distance.push(Math.sqrt((refCenter.x-x)*(refCenter.x-x)+(refCenter.y-y)*(refCenter.y-y)));
	}
	y = entity.getY()+entity.height;
	x = (y-n)/m;
	if(x>=entity.getX() && x<=(entity.getX()+entity.width)){
		pos.push(new Pos(x + offset, y, Edge.Position.DOWN));
		distance.push(Math.sqrt((refCenter.x-x)*(refCenter.x-x)+(refCenter.y-y)*(refCenter.y-y)));
	}
	var min=999999999;
	var position;
	for(var i=0;i<pos.length;i++){
		if(distance[i]<min){
			 min = distance[i];
			 position = pos[i];
		}
	}
	return position;
};
Generalisation = function() { this.init();this.typ="Generalisation";};
Generalisation.prototype = Object_create(Edge.prototype);
Generalisation.prototype.constructor = Generalisation;
Generalisation.prototype.initEdge = Generalisation.prototype.init;
Generalisation.prototype.init =function(){ this.initEdge(); this.size=16;this.angle = 50; }
Generalisation.prototype.calculateEdge = Generalisation.prototype.calculate;
Generalisation.prototype.calculate = function(board, drawer){
	if(!this.calculateEdge(board, drawer)){
		return false;
	}

	var startArrow	= this.endPos().source;
	var targetArrow	= this.endPos().target;
	// calculate the angle of the line
	var lineangle=Math.atan2(targetArrow.y-startArrow.y,targetArrow.x-startArrow.x);
	// h is the line length of a side of the arrow head
	var h=Math.abs(this.size/Math.cos(this.angle));
	var angle1=lineangle+Math.PI+this.angle;
	this.top = new Pos(targetArrow.x+Math.cos(angle1)*h, targetArrow.y+Math.sin(angle1)*h);
	var angle2=lineangle+Math.PI-this.angle;
	this.bot = new Pos(targetArrow.x+Math.cos(angle2)*h, targetArrow.y+Math.sin(angle2)*h);
	var pos = new Pos((this.top.x + this.bot.x) / 2, (this.top.y + this.bot.y) / 2);
	this.end = this.path[this.path.length-1].target;
	this.endPos().target = pos;
	return true;
};
Generalisation.prototype.drawSuper = Generalisation.prototype.draw;
Generalisation.prototype.draw = function(board, drawer){
	this.drawSuper(board, drawer);
	this.addElement(board, drawer.createLine(this.top.x, this.top.y, this.end.x, this.end.y, this.lineStyle));
	this.addElement(board, drawer.createLine(this.bot.x, this.bot.y, this.end.x, this.end.y, this.lineStyle));
	this.addElement(board, drawer.createLine(this.top.x, this.top.y, this.bot.x, this.bot.y, this.lineStyle));
};
Generalisation.prototype.drawSourceText = function(board, drawer, options){};
Generalisation.prototype.drawTargetText = function(board, drawer, options){};

Implements = function() { this.init();this.typ="Implements";}
Implements.prototype = Object_create(Generalisation.prototype);
Implements.prototype.constructor = Implements;
Implements.prototype.initGeneralisation = Implements.prototype.init;
Implements.prototype.init =function(){
	this.initGeneralisation();
	this.lineStyle = Line.Format.DOTTED;
}

String.prototype.endsWith = function(suffix) {return this.indexOf(suffix, this.length - suffix.length) !== -1;};

function bindEvent(el, eventName, eventHandler) {
	if (el.addEventListener){
		el.addEventListener(eventName, eventHandler, false); 
	} else if (el.attachEvent){
		el.attachEvent('on'+eventName, eventHandler);
	}
}


/**
 * A class to parse color values
 * @author Stoyan Stefanov <sstoo@gmail.com>
 * @link   http://www.phpied.com/rgb-color-parser-in-javascript/
 * @license Use it if you like it
 */
function RGBColor(value)
{
	this.ok = false;
	// strip any leading #
	if (value.charAt(0) == '#') { // remove # if any
		value = value.substr(1,6);
	}
	value = value.replace(/ /g,'').toLowerCase();

	// before getting into regexps, try simple matches
	// and overwrite the input
	var simple_colors = {
		aliceblue: 'f0f8ff', antiquewhite: 'faebd7',
		aqua: '00ffff', aquamarine: '7fffd4',
		azure: 'f0ffff', beige: 'f5f5dc',
		bisque: 'ffe4c4', black: '000000',
		blanchedalmond: 'ffebcd', blue: '0000ff',
		blueviolet: '8a2be2', brown: 'a52a2a',
		burlywood: 'deb887', cadetblue: '5f9ea0',
		chartreuse: '7fff00', chocolate: 'd2691e',
		coral: 'ff7f50', cornflowerblue: '6495ed',
		cornsilk: 'fff8dc', crimson: 'dc143c',
		cyan: '00ffff', darkblue: '00008b',
		darkcyan: '008b8b', darkgoldenrod: 'b8860b',
		darkgray: 'a9a9a9', darkgreen: '006400',
		darkkhaki: 'bdb76b', darkmagenta: '8b008b',
		darkolivegreen: '556b2f', darkorange: 'ff8c00',
		darkorchid: '9932cc', darkred: '8b0000',
		darksalmon: 'e9967a', darkseagreen: '8fbc8f',
		darkslateblue: '483d8b', darkslategray: '2f4f4f',
		darkturquoise: '00ced1', darkviolet: '9400d3',
		deeppink: 'ff1493', deepskyblue: '00bfff',
		dimgray: '696969', dodgerblue: '1e90ff',
		feldspar: 'd19275', firebrick: 'b22222',
		floralwhite: 'fffaf0', forestgreen: '228b22',
		fuchsia: 'ff00ff', gainsboro: 'dcdcdc',
		ghostwhite: 'f8f8ff', gold: 'ffd700',
		goldenrod: 'daa520', gray: '808080', 
		green: '008000', greenyellow: 'adff2f',
		honeydew: 'f0fff0', hotpink: 'ff69b4',
		indianred : 'cd5c5c', indigo : '4b0082',
		ivory: 'fffff0', khaki: 'f0e68c',
		lavender: 'e6e6fa', lavenderblush: 'fff0f5',
		lawngreen: '7cfc00', lemonchiffon: 'fffacd',
		lightblue: 'add8e6', lightcoral: 'f08080',
		lightcyan: 'e0ffff', lightgoldenrodyellow: 'fafad2',
		lightgrey: 'd3d3d3', lightgreen: '90ee90',
		lightpink: 'ffb6c1', lightsalmon: 'ffa07a',
		lightseagreen: '20b2aa', lightskyblue: '87cefa',
		lightslateblue: '8470ff', lightslategray: '778899',
		lightsteelblue: 'b0c4de', lightyellow: 'ffffe0',
		lime: '00ff00', limegreen: '32cd32',
		linen: 'faf0e6', magenta: 'ff00ff',
		maroon: '800000', mediumaquamarine: '66cdaa',
		mediumblue: '0000cd', mediumorchid: 'ba55d3',
		mediumpurple: '9370d8', mediumseagreen: '3cb371',
		mediumslateblue: '7b68ee', mediumspringgreen: '00fa9a',
		mediumturquoise: '48d1cc', mediumvioletred: 'c71585',
		midnightblue: '191970', mintcream: 'f5fffa',
		mistyrose: 'ffe4e1', moccasin: 'ffe4b5',
		navajowhite: 'ffdead', navy: '000080',
		oldlace: 'fdf5e6', olive: '808000',
		olivedrab: '6b8e23', orange: 'ffa500',
		orangered: 'ff4500', orchid: 'da70d6',
		palegoldenrod: 'eee8aa', palegreen: '98fb98',
		paleturquoise: 'afeeee', palevioletred: 'd87093',
		papayawhip: 'ffefd5', peachpuff: 'ffdab9',
		peru: 'cd853f', pink: 'ffc0cb',
		plum: 'dda0dd', powderblue: 'b0e0e6',
		purple: '800080', red: 'ff0000',
		rosybrown: 'bc8f8f', royalblue: '4169e1',
		saddlebrown: '8b4513', salmon: 'fa8072',
		sandybrown: 'f4a460', seagreen: '2e8b57',
		seashell: 'fff5ee', sienna: 'a0522d',
		silver: 'c0c0c0', skyblue: '87ceeb',
		slateblue: '6a5acd', slategray: '708090',
		snow: 'fffafa', springgreen: '00ff7f',
		steelblue: '4682b4', tan: 'd2b48c',
		teal: '008080', thistle: 'd8bfd8',
		tomato: 'ff6347', turquoise: '40e0d0',
		violet: 'ee82ee', violetred: 'd02090',
		wheat: 'f5deb3', white: 'ffffff',
		whitesmoke: 'f5f5f5', yellow: 'ffff00',
		yellowgreen: '9acd32'
	};
	if(simple_colors[value]){
		value = simple_colors[value];
	}
	// array of color definition objects
	var color_defs = [
		{
			re: /^rgb\((\d{1,3}),\s*(\d{1,3}),\s*(\d{1,3})\)$/,
			example: ['rgb(123, 234, 45)', 'rgb(255,234,245)'],
			process: function (bits){
				return [ parseInt(bits[1]), parseInt(bits[2]), parseInt(bits[3]) ];
			}
		},
		{
			re: /^(\w{2})(\w{2})(\w{2})$/,
			example: ['#00ff00', '336699'],
			process: function (bits){
				return [ parseInt(bits[1], 16), parseInt(bits[2], 16), parseInt(bits[3], 16) ];
			}
		},
		{
			re: /^(\w{1})(\w{1})(\w{1})$/,
			example: ['#fb0', 'f0f'],
			process: function (bits){
				return [ parseInt(bits[1] + bits[1], 16), parseInt(bits[2] + bits[2], 16), parseInt(bits[3] + bits[3], 16) ];
			}
		}
	];
	// search through the definitions to find a match
	for (var i = 0; i < color_defs.length; i++) {
		var re = color_defs[i].re;
		var processor = color_defs[i].process;
		var bits = re.exec(value);
		if (bits) {
			channels = processor(bits);
			this.r = channels[0];
			this.g = channels[1];
			this.b = channels[2];
			this.ok = true;
		}
	}

	// validate/cleanup values
	this.r = (this.r < 0 || isNaN(this.r)) ? 0 : ((this.r > 255) ? 255 : this.r);
	this.g = (this.g < 0 || isNaN(this.g)) ? 0 : ((this.g > 255) ? 255 : this.g);
	this.b = (this.b < 0 || isNaN(this.b)) ? 0 : ((this.b > 255) ? 255 : this.b);

	// some getters
	this.toRGB = function () {
		return 'rgb(' + this.r + ', ' + this.g + ', ' + this.b + ')';
	}
	this.toHex = function () {
		var r = this.r.toString(16);
		var g = this.g.toString(16);
		var b = this.b.toString(16);
		if (r.length == 1) r = '0' + r;
		if (g.length == 1) g = '0' + g;
		if (b.length == 1) b = '0' + b;
		return '#' + r + g + b;
	}
}
