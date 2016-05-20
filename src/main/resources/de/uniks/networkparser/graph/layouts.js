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
// VERSION: 2015.09.02 18:00
/*jslint node: true, forin:true */
/*global Graph: false, OrderedLayout: false, FixedLayout: false, CircularLayout: false, SpringLayout: false */
"use strict";
Graph.prototype.stdLayouts = Graph.prototype.initLayouts;
Graph.prototype.initLayouts = function () {
	this.stdLayouts();
	this.layouts.push({name: "ordered", value: new OrderedLayout()});
	this.layouts.push({name: "fixed", value: new FixedLayout()});
	this.layouts.push({name: "circular", value: new CircularLayout()});
	this.layouts.push({name: "spring", value: new SpringLayout()});
};

//	######################################################### ORDERED #########################################################
var OrderedLayout = function (order) {this.order = order; this.radius = 40; };
OrderedLayout.prototype = {
	layoutPrepare: function (order) {
		var i, node, counter;
		for (i in this.graph.nodes) {
			node = this.graph.nodes[i];
			node.x = 0;
			node.y = 0;
		}
		counter = 0;
		for (i in this.order) {
			node = this.order[i];
			node.x = counter;
			node.y = Math.random();
			counter += 1;
		}
	},
	layoutCalcBounds: function () {
		var x, y, i, minx = Infinity, maxx = -Infinity, miny = Infinity, maxy = -Infinity;
		for (i in this.graph.nodes) {
			x = this.graph.nodes[i].x;
			y = this.graph.nodes[i].y;
			if (x > maxx) {maxx = x; }
			if (x < minx) {minx = x; }
			if (y > maxy) {maxy = y; }
			if (y < miny) {miny = y; }
		}

		this.graph.layoutMinX = Math.max(minx, 100);
		this.graph.layoutMaxX = Math.max(maxx, 200);
		this.graph.layoutMinY = Math.max(miny, 100);
		this.graph.layoutMaxY = Math.max(maxy, 200);
	},
	layout: function (graph, width, height) {
		this.graph = graph;
		this.layoutPrepare();
		this.layoutCalcBounds();
		this.width = width;
		this.height = height;
		this.factorX = (width - 2 * this.radius) / (this.graph.layoutMaxX - this.graph.layoutMinX);
		this.factorY = (height - 2 * this.radius) / (this.graph.layoutMaxY - this.graph.layoutMinY);
		var list = this.graph.nodes, i, node;
		for (i in list) {
			node = list[i];
			node.x = Math.max((node.x - this.graph.layoutMinX), 1) * this.factorX + this.radius;
			node.y = Math.max((node.y - this.graph.layoutMinY), 1) * this.factorY + this.radius;
		}
		this.graph.draw(width, height);
	}
};
//	######################################################### FIXED #########################################################
var FixedLayout = function () {this.items = []; };
FixedLayout.prototype = {
	addNode: function (nodeid, left, top) {
		this.items.push([nodeid, left, top]);
	},
	layout: function (graph, width, height) {
		this.graph = graph;
		var nodeid, item, i, newNode = {}, newEdges = [];
		for (i in this.items) {
			nodeid = this.items[i][0];
			item = this.graph.nodes[nodeid];
			if (item) {
				item.x = this.items[i][1];
				item.y = this.items[i][2];
				newNode[nodeid] = item;
			}
		}
		this.graph.nodes = newNode;
		for (i in this.graph.edges) {
			item = this.graph.edges[i];
			if (newNode[item.source.id] && newNode[item.target.id]) {
				newEdges.push(item);
			}
		}
		this.graph.edges = newEdges;
		this.width = width;
		this.height = height;
		this.factorX = (width - 2 * this.radius) / (graph.layoutMaxX - graph.layoutMinX);
		this.factorY = (height - 2 * this.radius) / (graph.layoutMaxY - graph.layoutMinY);
		this.graph.draw(width, height);
	}
};
//	######################################################### CIRCULAR #########################################################
var CircularLayout = function () {};
/**
 * Spreads the vertices evenly in a circle. No cross reduction.
 *@param graph A valid graph instance
 */
CircularLayout.prototype = {
	postlayout: function () {
		/* Radius. */
		var t, i, v, step, dx, dy, r = Math.min(this.width, this.height) / 2;
		/* Where to start the circle. */
		dx = this.width / 2;
		dy = this.height / 2;
		/* Calculate the step so that the vertices are equally apart. */
		step = 2 * Math.PI / this.graph.nodeCount;
		t = 0; // Start at "angle" 0.
		for (i in this.graph.nodes) {
			v = this.graph.nodes[i];
			v.x = Math.round(r * Math.cos(t) + dx);
			v.y = Math.round(r * Math.sin(t) + dy);
			t = t + step;
		}
	},
	layout : function (graph, width, height) {
		this.graph = graph;
		this.width = width;
		this.height = height;
		this.postlayout();
		this.graph.draw(width, height);
	}
};
//	######################################################### SPRING #########################################################
var SpringLayout = function () {
	this.iterations = 500;
	this.maxRepulsiveForceDistance = 6;
	this.k = 2;
	this.c = 0.01;
	this.maxVertexMovement = 5;
	this.radius = 40;
};
SpringLayout.prototype = {
	layout: function (graph, width, height) {
		this.graph = graph;
		var forceNodes = [], i, node;
		for (i in this.graph.nodes) {
			node = this.getNode(i);
			// layoutPosX = X , layoutPosY, Y
			forceNodes.push({"id": node.id, x: 0, y: 0, layoutForceX: 0, layoutForceY: 0});
		}
		for (i = 0; i < this.iterations; i += 1) {
			this.layoutIteration(forceNodes);
		}

		for (i = 0; i < forceNodes.length; i += 1) {
			node = this.getNode(forceNodes[i].id);
			node.x = forceNodes[i].x;
			node.y = forceNodes[i].y;
		}
		this.graph.draw(width, height);
	},
	getNode: function (pos) {
		return this.graph.nodes[pos];
	},
	getEdge: function (pos) {
		return this.graph.edges[pos];
	},
	layoutIteration: function (forceNodes) {
		// Forces on nodes due to node-node repulsions
		//var prev = new Array();
		//var forceList = new Array();
		var i, node1, node2, j, force, xmove, ymove, max;
		for (i = 0; i < forceNodes.length; i += 1) {
			node1 = forceNodes[i];
			for (j = i + 1; j < forceNodes.length; j += 1) {
				node2 = forceNodes[j];
				this.layoutRepulsive(node1, node2);
			}
		}
		for (i = 0; i < this.graph.edges.length; i += 1) {
			this.layoutAttractive(this.getEdge(i), forceNodes);
		}
		// Move by the given force
		for (i = 0; i < forceNodes.length; i += 1) {
			//var node = this.graph.nodes[i];
			force = forceNodes[i];
			xmove = this.c * force.layoutForceX;
			ymove = this.c * force.layoutForceY;

			max = this.maxVertexMovement;
			if (xmove > max) {xmove = max; }
			if (xmove < -max) {xmove = -max; }
			if (ymove > max) {ymove = max; }
			if (ymove < -max) {ymove = -max; }

			force.x = Math.max(0, force.x + xmove);
			force.y = Math.max(0, force.y + ymove);
			force.layoutForceX = 0;
			force.layoutForceY = 0;
		}
	},
	layoutRepulsive: function (node1, node2) {
		var repulsiveForce, d, d2, dx = node2.x - node1.x, dy = node2.y - node1.y;
		d2 = dx * dx + dy * dy;
		if (d2 < 0.01) {
			dx = 0.1 * Math.random() + 0.1;
			dy = 0.1 * Math.random() + 0.1;
			d2 = dx * dx + dy * dy;
		}
		d = Math.sqrt(d2);
		if (d < this.maxRepulsiveForceDistance) {
			repulsiveForce = this.k * this.k / d;
			node2.layoutForceX += repulsiveForce * dx / d;
			node2.layoutForceY += repulsiveForce * dy / d;
			node1.layoutForceX -= repulsiveForce * dx / d;
			node1.layoutForceY -= repulsiveForce * dy / d;
		}
	},
	getForceNode: function (forceNodes, id) {
		var i;
		for (i = 0; i < forceNodes.length; i += 1) {
			if (forceNodes[i].id === id) {
				return forceNodes[i];
			}
		}
		return null;
	},
	layoutAttractive: function (edge, forceNodes) {
		var dx, dy, d2, d, attractiveForce, node1 = edge.source, node2 = edge.target, force1 = this.getForceNode(forceNodes, node1.id), force2 = this.getForceNode(forceNodes, node2.id);
		if (!force1 || !force2) {
			return;
		}

		dx = node2.x - node1.x;
		dy = node2.y - node1.y;
		d2 = dx * dx + dy * dy;
		if (d2 < 0.01) {
			dx = 0.1 * Math.random() + 0.1;
			dy = 0.1 * Math.random() + 0.1;
			d2 = dx * dx + dy * dy;
		}
		d = Math.sqrt(d2);
		if (d > this.maxRepulsiveForceDistance) {
			d = this.maxRepulsiveForceDistance;
			d2 = d * d;
		}
		attractiveForce = (d2 - this.k * this.k) / this.k;
		if (edge.attraction === undefined) {
			edge.attraction = 1;
		}
		attractiveForce *= Math.log(edge.attraction) * 0.5 + 1;

		force2.x = Math.max(0, force2.x - attractiveForce * dx / d);
		force2.y = Math.max(0, force2.y - attractiveForce * dy / d);
		force1.x = Math.max(0, force1.x + attractiveForce * dx / d);
		force1.y = Math.max(0, force1.y + attractiveForce * dy / d);
	}
};
