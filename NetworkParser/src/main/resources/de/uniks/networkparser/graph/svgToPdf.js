/*
 * svgToPdf.js
 *
 * Copyright 2012-2014 Florian Hülsmann <fh@cbix.de>
 * Copyright 2014 Ben Gribaudo <www.bengribaudo.com>
 *
 * This script is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This script is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this file.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

var pdfSvgAttr = {
	// allowed attributes. all others are removed from the preview.
	g: ['stroke', 'fill', 'stroke-width'],
	line: ['x1', 'y1', 'x2', 'y2', 'stroke', 'stroke-width', 'fill'],
	rect: ['x', 'y', 'width', 'height', 'stroke', 'fill', 'stroke-width'],
	ellipse: ['cx', 'cy', 'rx', 'ry', 'stroke', 'fill', 'stroke-width'],
	circle: ['cx', 'cy', 'r', 'stroke', 'fill', 'stroke-width'],
	text: ['x', 'y', 'font-size', 'font-family', 'text-anchor', 'font-weight', 'font-style', 'fill'],
	path: []
};

var svgElementToPdf = function(element, pdf, options) {
	// pdf is a jsPDF object
	//console.log("options =", options);
	var remove = (typeof(options.removeInvalid) == 'undefined' ? false : options.removeInvalid);
	var k = (typeof(options.scale) == 'undefined' ? 1.0 : options.scale);
	var colorMode = null;
	$(element).children().each(function(i, node) {
		//console.log("passing: ", node);
		var n = $(node);
		var hasFillColor = false;
		var hasStrokeColor = false;
		if(n.is('g,line,rect,ellipse,circle,text')) {
			var fillColor = n.attr('fill');
			if(typeof(fillColor) != 'undefined') {
				var fillRGB = new RGBColor(fillColor);
				if(fillRGB.ok) {
					hasFillColor = true;
					colorMode = 'F';
				} else {
					colorMode = null;
				}
			}
		}
		if(n.is('g,line,rect,ellipse,circle')) {
			if(hasFillColor) {
				pdf.setFillColor(fillRGB.r, fillRGB.g, fillRGB.b);
			}
			if(typeof(n.attr('stroke-width')) != 'undefined') {
				pdf.setLineWidth(k * parseInt(n.attr('stroke-width')));
			}
			var strokeColor = n.attr('stroke');
			if(typeof(strokeColor) != 'undefined') {
				var strokeRGB = new RGBColor(strokeColor);
				if(strokeRGB.ok) {
					hasStrokeColor = true;
					pdf.setDrawColor(strokeRGB.r, strokeRGB.g, strokeRGB.b);
					if(colorMode == 'F') {
						colorMode = 'FD';
					} else {
						colorMode = null;
					}
				} else {
					colorMode = null;
				}
			}
		}
		switch(n.get(0).tagName.toLowerCase()) {
			case 'svg':
			case 'a':
			case 'g':
				svgElementToPdf(node, pdf, options);
				break;
			case 'line':
				console.log("line: "+(k*parseInt(n.attr('x1')))+":"+(k*parseInt(n.attr('y1')))+"-"+(k*parseInt(n.attr('x2')))+":"+(k*parseInt(n.attr('y2'))));
				pdf.line(
					k*parseInt(n.attr('x1')),
					k*parseInt(n.attr('y1')),
					k*parseInt(n.attr('x2')),
					k*parseInt(n.attr('y2'))
				);
				break;
			case 'rect':
				console.log("rect: "+(k*parseInt(n.attr('x')))+":"+(k*parseInt(n.attr('y')))+"-"+((k*parseInt(n.attr('x')))+(k*parseInt(n.attr('width'))))+":"+((k*parseInt(n.attr('y')))+(k*parseInt(n.attr('height')))));
				pdf.rect(
					k*parseInt(n.attr('x')),
					k*parseInt(n.attr('y')),
					k*parseInt(n.attr('width')),
					k*parseInt(n.attr('height')),
					colorMode
				);
				break;
			case 'ellipse':
				console.log("ellipse: "+"cx:"+(k*parseInt(n.attr('cx')))+"cy:"+(k*parseInt(n.attr('cy')))+"rx:"+(k*parseInt(n.attr('rx')))+"ry:"+(k*parseInt(n.attr('ry'))));
				pdf.ellipse(
					k*parseInt(n.attr('cx')),
					k*parseInt(n.attr('cy')),
					k*parseInt(n.attr('rx')),
					k*parseInt(n.attr('ry')),
					colorMode
				);
				break;
			case 'circle':
				pdf.circle(
					k*parseInt(n.attr('cx')),
					k*parseInt(n.attr('cy')),
					k*parseInt(n.attr('r')),
					colorMode
				);
				break;
			case 'text':
				if(node.hasAttribute('font-family')) {
					switch(n.attr('font-family').toLowerCase()) {
						case 'serif': pdf.setFont('times'); break;
						case 'monospace': pdf.setFont('courier'); break;
						default:
							n.attr('font-family', 'sans-serif');
							pdf.setFont('Helvetica');
					}
				}
				if(hasFillColor) {
					pdf.setTextColor(fillRGB.r, fillRGB.g, fillRGB.b);
				}
				var fontType = "";
				if(node.hasAttribute('font-weight')) {
					if(n.attr('font-weight') == "bold") {
						fontType = "bold";
					} else {
						node.removeAttribute('font-weight');
					}
				}
				if(node.hasAttribute('font-style')) {
					if(n.attr('font-style') == "italic") {
						fontType += "italic";
					} else {
						node.removeAttribute('font-style');
					}
				}
				pdf.setFontType(fontType);
				var pdfFontSize = 16;
				if(node.hasAttribute('font-size')) {
					pdfFontSize = parseInt(n.attr('font-size'));
				}
				var box = node.getBBox();
				//FIXME: use more accurate positioning!!
				var x = parseInt(n.attr('x'));
				var y = parseInt(n.attr('y'));
				var xOffset = 0;
				if(node.hasAttribute('text-anchor')) {
					switch(n.attr('text-anchor')) {
						case 'end': xOffset = box.width; break;
						case 'middle': xOffset = box.width / 2; break;
						case 'start': break;
						case 'default': n.attr('text-anchor', 'start');
					}
					x = parseInt(n.attr('x')) - xOffset;
					y = parseInt(n.attr('y'));
				}
				
				console.log("text: "+(k * x)+":"+(k * y)+":"+n.text());
				pdf.setFontSize(pdfFontSize).text(
					k * x,
					k * y,
					n.text()
				);
				break;
			case 'path':
				// //console.log("node is: ",node.attributes)
				// $.each(node.attributes, function(i, a) {
				//  //console.log(pdfSvgAttr.path.indexOf(a.name.toLowerCase()));
				// });

				var updated_path = n.attr('d');
				// Separate the svg 'd' string to a list of letter
				// and number elements. Iterate on this list.
				// console.log('path before',path);
				svg_regex = /(m|l|h|v|c|s|a|z)/gi;
				// console.log('TESTING REGULAR
				// EXPRESSION',svg_regex.test('m')); The crazy ie9 case
				// where they take our path and make some crazy scientific
				// notation
				updated_path = updated_path.replace(/(e)?-/g, function($0, $1) {
					return $1 ? $0 : ' -';
				})
				// .replace(/-/g, ' -')
				.replace(svg_regex, ' $1 ')
					.replace(/,+/g, ' ')
					.replace(/^\s+|\s+$/g, '')
					.split(/[\s]+/);

				var svg_element = null;
				var i = 0;

				// mx and my will define the starting points from each m/M case
				var mx = null;
				var my = null;
				// x and y will redefine the starting points
				var x = null;
				var y = null;

				// big list contains the large list of lists to pass to
				// jspdf to render the appropriate path
				var big_list = [];

				// for S/s shorthand bezier calculations of 2nd control pts
				var previous_element = {
					element: null,
					prev_numbers: [],
					point: []
				};
				var m_flag = 0;
				// Go through our list until we are done with the updated_path
				while (i < updated_path.length) {

					// Numbers will hold the list of numbers for the
					// appropriate updated_path
					var numbers = [];

					// svg_element is a letter corresponding to the type
					// of updated_path to draw
					var sci_regex = /[+\-]?(?:0|[1-9]\d*)(?:\.\d*)?(?:[eE][+\-]?\d+)?/;
					var svg_element = updated_path[i];
					if (sci_regex.test(svg_element)) {
						svg_element = String(Number(svg_element));
					}
					i++
					//if svg_element is s/S, need to find 1st control pts
					if (/s/i.test(svg_element)) {
						previous_element.point = find_s_points(svg_element,
						previous_element);
					}
					// for some reason z followed by another letter
					// i.e. 'z m' skips that 2nd letter, so added if
					// statement to get around that.
					if (/z/i.test(svg_element) == false) {
						// Parse through the updated_path until we find the next
						// letter or we are at the end of the updated_path
						while ((svg_regex.test(updated_path[i]) == false) && (i != updated_path.length)) {
							numbers.push(k * parseFloat(updated_path[i]));
							i++;
						}
					}

					switch (svg_element) {
						case 'm':
							//paths and subpaths must always start with m/M.
							//thus we call pdf.lines 
							if (big_list.length != 0) {
								pdf.lines(big_list, mx, my, [1, 1], null);
							}
							big_list = [];
							// check if this is 1st command in the path
							if (previous_element.element == null) {
								x = numbers[0];
								mx = numbers[0];
								y = numbers[1];
								my = numbers[1];
							} else {
								x += numbers[0];
								mx += numbers[0];
								y += numbers[1];
								my += numbers[1];
							}
							if (numbers.length != 2) {
								var lines_numbers = numbers.slice(2, numbers.length);
								var new_numbers = change_numbers(lines_numbers,
								x, y, true);
								$.each(new_numbers, function(num) {
									big_list.push(num);
								});
								// pdf.lines(new_numbers,x,y,[1,1],null);
								x += sums(new_numbers, true);
								y += sums(new_numbers, false);
							}
							break;
						case 'M':
							if (big_list.length != 0) {
								pdf.lines(big_list, mx, my, [1, 1], null);
							}
							big_list = [];
							if (previous_element == null) {
								x = numbers[0];
								mx = numbers[0];
								y = numbers[1];
								my = numbers[1];
							}
							if (numbers.length != 2) {
								x = numbers[0];
								y = numbers[1];
								var lines_numbers = numbers.slice(2, numbers.length);
								var new_numbers = change_numbers(lines_numbers,
								x, y, false);
								pdf.lines(new_numbers, x, y, [1, 1], null);
								x += new_numbers[new_numbers.length - 1][0];
								y += new_numbers[new_numbers.length - 1][1];

							}
							break;
						case 'l':
							var new_numbers = change_numbers(numbers, x, y, true);
							$.each(new_numbers, function(num) {
								big_list.push(num);
							});
							// pdf.lines(new_numbers,x,y,[1,1],null);
							x += sums(new_numbers, true);
							y += sums(new_numbers, false);
							break;
						case 'L':
							var new_numbers = change_numbers(numbers, x, y, false);
							$.each(new_numbers, function(num) {
								big_list.push(num);
							});
							//pdf.lines(new_numbers,x,y,[1,1],null);
							x += new_numbers[new_numbers.length - 1][0];
							y += new_numbers[new_numbers.length - 1][1];
							break;
						case 'h':
							// x does not change. Only y changes
							var sum = $.reduce(numbers,

							function(memo, num) {
								return memo + num;
							}, 0);
							var new_numbers = [
								[sum, 0]
							];
							$.each(new_numbers, function(num) {
								big_list.push(num);
							});
							// pdf.lines([[sum,0]],x,y,[1,1],null);   
							x += sum;
							break;
						case 'H':
							big_list.push([numbers[numbers.length - 1] - x, 0]);
							// pdf.lines([[numbers[numbers.length-1]-x,0]],
							//	   x,y,[1,1],null);
							x = numbers[numbers.length - 1];
							break;
						case 'v':
							var sum = $.reduce(numbers,

							function(memo, num) {
								return memo + num;
							}, 0);
							var new_numbers = [
								[0, sum]
							];
							$.each(new_numbers, function(num) {
								big_list.push(num);
							});
							// pdf.lines([[0,sum]],x,y,[1,1],null);   
							y += sum;
							break;
						case 'V':
							big_list.push([0, numbers[numbers.length - 1] - y]);
							// pdf.lines([[0,numbers[numbers.length-1]-y]],
							//		x,y,[1,1],null);
							y = numbers[numbers.length - 1];
							break;
						case 'c':
							var new_numbers = bezier_numbers(numbers, x, y, true);
							$.each(new_numbers, function(num) {
								big_list.push(num);
							});
							// pdf.lines(new_numbers,x,y,[1,1],null);
							x += sums(new_numbers, true);
							y += sums(new_numbers, false);
							break;
						case 'C':
							var new_numbers = bezier_numbers(numbers, x, y, false);
							$.each(new_numbers, function(num) {
								big_list.push(num);
							});
							// pdf.lines(new_numbers,x,y,[1,1],null);
							x = numbers[numbers.length - 2];
							y = numbers[numbers.length - 1];
							break;
						case 's':
							var new_numbers = s_bezier_numbers(numbers, x, y,
							true, previous_element);
							$.each(new_numbers, function(num) {
								big_list.push(num);
							});
							// pdf.lines(new_numbers,x,y,[1,1],null);
							x += sums(new_numbers, true);
							y += sums(new_numbers, false);
							break;
						case 'S':
							var new_numbers = s_bezier_numbers(numbers, x, y, false,
							previous_element);
							$.each(new_numbers, function(num) {
								big_list.push(num);
							});
							// pdf.lines(new_numbers,x,y,[1,1],null);
							x = numbers[numbers.length - 2];
							y = numbers[numbers.length - 1];
							break;
						case 'A':
							// for now a hack.treat this as a line
							break;
						case 'a':

							break;
						case 'z':
							big_list.push([mx - x, my - y]);
							x = mx;
							y = my;
							// pdf.lines([[mx-x,my-y]],x,y,[1,1],null);
							break;
						case 'Z':
							big_list.push([mx - x, my - y]);
							x = mx;
							y = my;
							// pdf.lines([[mx-x,my-y]],x,y,[1,1],null);
							break;
						default:
							console.log('Sorry, the', svg_element, 'svg command is not yet available.');
					}
					previous_element.element = svg_element;
					previous_element.prev_numbers = numbers;
				}
				pdf.lines(big_list, mx, my, [1, 1], colorMode);
				var numbs = null;
				break;
			//TODO: image
			default:
				if(remove) {
					console.log("can't translate to pdf:", node);
					n.remove();
				}
		}
	});
	return pdf;
};


function sums(ListOfLists, is_x) {
	if (is_x) {
		var sum = _.reduce(ListOfLists,

		function(memo, num) {
			return memo + num[num.length - 2];
		}, 0);
	} else {
		var sum = _.reduce(ListOfLists,

		function(memo, num) {
			return memo + num[num.length - 1];
		}, 0);
	}
	return sum;
}

function change_numbers(numbers, x, y, relative) {
	var i = 0;
	var prev_x = x;
	var prev_y = y;
	var new_numbers = [];
	while (i < numbers.length) {
		if (relative) {
			x = numbers[i];
			y = numbers[i + 1];
		} else {
			x = numbers[i] - prev_x;
			y = numbers[i + 1] - prev_y;
		}
		prev_x = numbers[i];
		prev_y = numbers[i + 1];
		new_numbers.push([x, y]);
		i += 2;
	}
	return new_numbers;
}

function bezier_numbers(numbers, x, y, relative) {
	// the bezier numbers are ALL relative to the
	// previous case line's (x,y), not all relative to
	// each other.
	var i = 0;
	var prev_x = x;
	var prev_y = y;
	var new_numbers = [];
	while (i < numbers.length) {
		if (relative) {
			var numbers_to_push = numbers.slice(i, i + 6);
		} else {
			var numbers_to_push = [];
			for (var k = i; k < i + 6; k = k + 2) {
				numbers_to_push.push(
				numbers[k] - prev_x,
				numbers[k + 1] - prev_y);
			}
		}
		prev_x = numbers[i + 4];
		prev_y = numbers[i + 5];
		new_numbers.push(numbers_to_push);
		i += 6;
	}
	return new_numbers;
}

function s_bezier_numbers(numbers, x, y, relative, previous_element) {
	var i = 0;
	var prev_x = x;
	var prev_y = y;
	var new_numbers = [];
	while (i < numbers.length) {
		var numbers_to_push = [];
		//need to check if relative for the 4 s/S numbers
		if (relative) {
			//find that 1st control point
			if (i < 4 && (previous_element.element == 'c' || previous_element.element == 's')) {
				// case 1: there was a prev c/C/s/S
				// outside this numbers segment
				numbers_to_push.push(previous_element.point[0],
				previous_element.point[1]);
			} else if (i >= 4) {
				//case 1: there was a prev s/S
				//within this numbers segment
				numbers_to_push.push(numbers[i - 2] - numbers[i - 4],
				numbers[i - 1] - numbers[i - 3]);
			} else {
				// case 2: no prev c/C/s/S, therefore 
				// 1st control pt = current pt.
				numbers_to_push.push(prev_x, prev_y);
			}
			//then add the rest of the s numbers
			for (var k = i; k < i + 4; k++) {
				numbers_to_push.push(numbers[k]);
			}
		} else {
			//find that 1st control point
			if (i < 4 && (previous_element.element == 'C' || previous_element.element == 'S')) {
				// case 1: there was a prev c/C/s/S
				// outside this numbers segment
				numbers_to_push.push(previous_element.point[0] - prev_x,
				previous_element.point[1] - prev_y);
			} else if (i >= 4) {
				//case 1: there was a prev s/S
				//within this numbers segment
				numbers_to_push.push(numbers[i - 2] + numbers[i - 2] - numbers[i - 4],
				numbers[i - 1] + numbers[i - 1] - numbers[i - 3]);
			} else {
				// case 2: no prev c/C/s/S, therefore 
				// 1st control pt = current pt.
				numbers_to_push.push(prev_x, prev_y);
			}
			//then add the rest of the s numbers
			for (var k = i; k < i + 4; k = k + 2) {
				numbers_to_push.push(numbers[k] - prev_x);
				numbers_to_push.push(numbers[k + 1] - prev_y);
			}
		}

		prev_x = numbers[i + 2];
		prev_y = numbers[i + 3];

		new_numbers.push(numbers_to_push);
		i += 4;
	}
	return new_numbers;
}

function find_s_points(svg_element, previous_element) {
	if (/(s|c)/.test(previous_element.element)) {
		numbs = previous_element.prev_numbers;
		previous_element.point = [numbs[numbs.length - 2] - numbs[numbs.length - 4],
		numbs[numbs.length - 1] - numbs[numbs.length - 3]];
	} else if (/(S|C)/.test(previous_element.element)) {
		numbs = previous_element.prev_numbers;
		previous_element.point = [2 * numbs[numbs.length - 2] - numbs[numbs.length - 4],
		2 * numbs[numbs.length - 1] - numbs[numbs.length - 3]];
	}
	return previous_element.point
}

(function(jsPDFAPI) {
'use strict';

	jsPDFAPI.addSVG = function(element, x, y, options) {
		'use strict'

		options = (typeof(options) == 'undefined' ? {} : options);
		options.x_offset = x;
		options.y_offset = y;

		svgElementToPdf(element, this, options);
		return this;
	};
})(jsPDF.API);

