/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
Graph.prototype.stdLayouts = Graph.prototype.initLayouts;
Graph.prototype.initLayouts = function(){
	this.stdLayouts();
	this.layouts.push({name: "ordered", value: new OrderedLayout()});
	this.layouts.push({name: "fixed", value: new FixedLayout()});
	this.layouts.push({name: "circular", value: new CircularLayout()});
	this.layouts.push({name: "spring", value: new SpringLayout()});
	
	
};

//	######################################################### ORDERED #########################################################
OrderedLayout = function(order) {this.order = order;this.radius = 40;};
OrderedLayout.prototype = {
	layoutPrepare: function(order) {
		for (i in this.graph.nodes) {
			var node = this.graph.nodes[i];
			node.x = 0;
			node.y = 0;
		}
		var counter = 0;
		for (i in this.order) {
			var node = this.order[i];
			node.x = counter;
			node.y = Math.random();
			counter++;
		}
	},
	layoutCalcBounds: function() {
		var minx = Infinity, maxx = -Infinity, miny = Infinity, maxy = -Infinity;
		for (i in this.graph.nodes) {
			var x = this.graph.nodes[i].x;
			var y = this.graph.nodes[i].y;
			if(x > maxx) maxx = x;
			if(x < minx) minx = x;
			if(y > maxy) maxy = y;
			if(y < miny) miny = y;
		}

		this.graph.layoutMinX = Math.max(minx, 100);
		this.graph.layoutMaxX = Math.max(maxx, 200);
		this.graph.layoutMinY = Math.max(miny, 100);
		this.graph.layoutMaxY = Math.max(maxy, 200);
	},
	layout: function(graph, width, height) {
		this.graph = graph;
		this.layoutPrepare();
		this.layoutCalcBounds();
		this.width = width;
		this.height = height;
		this.factorX = (width - 2 * this.radius) / (this.graph.layoutMaxX - this.graph.layoutMinX);
		this.factorY = (height - 2 * this.radius) / (this.graph.layoutMaxY - this.graph.layoutMinY);
		var list = this.graph.nodes;
		for (var i in list) {
			var node = list[i];
			node.x = Math.max((node.x - this.graph.layoutMinX), 1) * this.factorX + this.radius;
			node.y = Math.max((node.y - this.graph.layoutMinY), 1) * this.factorY + this.radius;
		}
	this.graph.drawGraph(width, height);
	}
};
//	######################################################### FIXED #########################################################
FixedLayout = function() {this.items = new Array();};
FixedLayout.prototype = {
	addNode: function(nodeid, left, top) {
		this.items.push(new Array(nodeid, left, top));
	},
	layout: function(graph, width, height) {
		this.graph = graph;
		var newNode = {}
		for (i in this.items) {
			var nodeid = this.items[i][0];
			var node = this.graph.nodes[nodeid];
			if(node){
				node.x = this.items[i][1];
				node.y = this.items[i][2];
				newNode[nodeid] = node;
			}
		}
		this.graph.nodes = newNode;
		var newEdges = [];
		for (i in this.graph.edges) {
			var edge = this.graph.edges[i];
			if(newNode[edge.source.id] && newNode[edge.target.id]){
				newEdges.push(edge);
			}
		}
		this.graph.edges = newEdges;
		this.width = width;
		this.height = height;
		this.factorX = (width - 2 * this.radius) / (g.layoutMaxX - g.layoutMinX);
		this.factorY = (height - 2 * this.radius) / (g.layoutMaxY - g.layoutMinY);
		this.graph.drawGraph(width, height);
	}
};
//	######################################################### CIRCULAR #########################################################
CircularLayout = function(){};
/**
 * Spreads the vertices evenly in a circle. No cross reduction.
 *@param graph A valid graph instance
 */
CircularLayout.prototype = {
	postlayout: function() {
		/* Radius. */
		var r = Math.min(this.width, this.height) / 2;
		/* Where to start the circle. */
		var dx = this.width / 2;
		var dy = this.height / 2;
		/* Calculate the step so that the vertices are equally apart. */
		var step = 2*Math.PI / this.graph.nodeCount; 
		var t = 0; // Start at "angle" 0.
		for (var i in this.graph.nodes) {
			var v = this.graph.nodes[i];
			v.x = Math.round(r*Math.cos(t) + dx);
			v.y = Math.round(r*Math.sin(t) + dy);
			t = t + step;
		}
	},
	layout : function(graph, width, height){
		this.graph = graph;
		this.width = width;
		this.height = height;
		this.postlayout();
		this.graph.drawGraph(width, height);
	}
};
//	######################################################### SPRING #########################################################
SpringLayout = function() {
	this.iterations = 500;
	this.maxRepulsiveForceDistance = 6;
	this.k = 2;
	this.c = 0.01;
	this.maxVertexMovement = 5;
	this.radius = 40;
};
SpringLayout.prototype = {
	layout: function(graph, width, height) {
		this.graph = graph;
		forceNodes = [];
		for (var i in this.graph.nodes) {
			var node = this.getNode(i);
			// layoutPosX = X , layoutPosY, Y
			forceNodes.push({"id": node.id,x:0,y:0,layoutForceX:0,layoutForceY:0});
		}
		for (var i = 0; i < this.iterations; i++) {
			this.layoutIteration(forceNodes);
		}

		for (var i=0;i<forceNodes.length;i++) {
			var node=this.getNode(forceNodes[i].id);
			node.x = forceNodes[i].x;
			node.y = forceNodes[i].y;
		}
		this.graph.drawGraph(width, height);
	},
	getNode: function(pos) {
		return this.graph.nodes[pos];
	},
	getEdge: function(pos) {
		return this.graph.edges[pos];
	},
	layoutIteration: function(forceNodes) {
		// Forces on nodes due to node-node repulsions
		//var prev = new Array();
		//var forceList = new Array();
		for (var i = 0; i < forceNodes.length; i++) {
			var node1 = forceNodes[i];
			for (var j = i + 1; j < forceNodes.length; j++) {
				var node2 = forceNodes[j];
				this.layoutRepulsive(node1, node2);
			}
		}
		for (var i=0;i<this.graph.edges.length;i++){
			this.layoutAttractive(this.getEdge(i), forceNodes);
		}

		// Move by the given force
		for (var i=0;i<forceNodes.length;i++) {
			//var node = this.graph.nodes[i];
			var force = forceNodes[i];
			var xmove = this.c * force.layoutForceX;
			var ymove = this.c * force.layoutForceY;

			var max = this.maxVertexMovement;
			if(xmove > max) xmove = max;
			if(xmove < -max) xmove = -max;
			if(ymove > max) ymove = max;
			if(ymove < -max) ymove = -max;

			force.x = Math.max(0, force.x + xmove);
			force.y = Math.max(0, force.y + ymove);
			force.layoutForceX =0;
			force.layoutForceY =0;
		}
	},
	layoutRepulsive: function(node1, node2) {
		var dx = node2.x - node1.x;
		var dy = node2.y - node1.y;
		var d2 = dx * dx + dy * dy;
		if(d2 < 0.01) {
			dx = 0.1 * Math.random() + 0.1;
			dy = 0.1 * Math.random() + 0.1;
			d2 = dx * dx + dy * dy;
		}
		var d = Math.sqrt(d2);
		if(d < this.maxRepulsiveForceDistance) {
			var repulsiveForce = this.k * this.k / d;
			node2.layoutForceX += repulsiveForce * dx / d;
			node2.layoutForceY += repulsiveForce * dy / d;
			node1.layoutForceX -= repulsiveForce * dx / d;
			node1.layoutForceY -= repulsiveForce * dy / d;
		}
	},
	getForceNode: function(forceNodes, id) {
		for (var i=0;i<forceNodes.length;i++) {
			if(forceNodes[i].id==id){
				return forceNodes[i];
			}
		}
		return null;
	},
	layoutAttractive: function(edge, forceNodes) {
		var node1 = edge.source;
		var node2 = edge.target;
		var force1 = this.getForceNode(forceNodes, node1.id);
		var force2 = this.getForceNode(forceNodes, node2.id);
		if(!force1 || !force2){
			return;
		}

		var dx = node2.x - node1.x;
		var dy = node2.y - node1.y;
		var d2 = dx * dx + dy * dy;
		if(d2 < 0.01) {
			dx = 0.1 * Math.random() + 0.1;
			dy = 0.1 * Math.random() + 0.1;
			d2 = dx * dx + dy * dy;
		}
		var d = Math.sqrt(d2);
		if(d > this.maxRepulsiveForceDistance) {
			d = this.maxRepulsiveForceDistance;
			d2 = d * d;
		}
		var attractiveForce = (d2 - this.k * this.k) / this.k;
		if(edge.attraction == undefined) edge.attraction = 1;
		attractiveForce *= Math.log(edge.attraction) * 0.5 + 1;

		force2.x = Math.max(0, force2.x - attractiveForce * dx / d);
		force2.y = Math.max(0, force2.y - attractiveForce * dy / d);
		force1.x = Math.max(0, force1.x + attractiveForce * dx / d);
		force1.y = Math.max(0, force1.y +attractiveForce * dy / d);
	}
};
