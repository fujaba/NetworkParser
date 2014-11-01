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
    line: ['x1', 'y1', 'x2', 'y2', 'stroke', 'stroke-width'],
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
	if(typeof element ==="string") {
		var el = document.createElement('div');
		el.innerHTML = element;
		element = el.childNodes[0];
	}
	for(var i=0;i<element.children.length;i++) {
		var n = element.children[i];
		var hasFillColor = false;
		if('g,line,rect,ellipse,circle,text'.indexOf(n.tagName)>=0) {
            var fillColor = n.getAttribute('fill');
            if(fillColor) {
                var fillRGB = new RGBColor(fillColor);
                if(fillRGB.ok) {
					hasFillColor = true;
                    colorMode = 'F';
                } else {
                    colorMode = null;
                }
            }
        }
        if('g,line,rect,ellipse,circle'.indexOf(n.tagName)>=0) {
            if(hasFillColor) {
				pdf.setFillColor(fillRGB.r, fillRGB.g, fillRGB.b);
			}

			var strokeColor = n.getAttribute('stroke');
			var strokeWidth = n.getAttribute('stroke-width');
            if(strokeWidth) {
                pdf.setLineWidth(k * parseInt(strokeWidth));
			}
            
            if(strokeColor) {
                var strokeRGB = new RGBColor(strokeColor);
                if(strokeRGB.ok) {
                    pdf.setDrawColor(strokeRGB.r, strokeRGB.g, strokeRGB.b);
                    if(colorMode == 'F') {
                        colorMode = 'FD';
                    } else if(!hasFillColor) {
                        colorMode = 'S';
                    }
                } else {
                    colorMode = null;
                }
            }
		}
		
		console.log("write "+n.tagName);
        switch(n.tagName.toLowerCase()) {
            case 'svg':
            case 'a':
            case 'g':
                svgElementToPdf(n, pdf, options);
                break;
            case 'line':
				console.log(k*parseInt(n.getAttribute('x1'))+":"+k*parseInt(n.getAttribute('y1'))+"-"+k*parseInt(n.getAttribute('x2'))+":"+k*parseInt(n.getAttribute('y2'))+"="
					+colorMode+ " "+strokeColor+"-"+strokeWidth);
                pdf.line(
                    k*parseInt(n.getAttribute('x1')),
                    k*parseInt(n.getAttribute('y1')),
                    k*parseInt(n.getAttribute('x2')),
                    k*parseInt(n.getAttribute('y2'))
                );
                break;
            case 'rect':
                pdf.rect(
                    k*parseInt(n.getAttribute('x')),
                    k*parseInt(n.getAttribute('y')),
                    k*parseInt(n.getAttribute('width')),
                    k*parseInt(n.getAttribute('height')),
                    colorMode
                );
                break;
            case 'ellipse':
                pdf.ellipse(
                    k*parseInt(n.getAttribute('cx')),
                    k*parseInt(n.getAttribute('cy')),
                    k*parseInt(n.getAttribute('rx')),
                    k*parseInt(n.getAttribute('ry')),
                    colorMode
                );
                break;
            case 'circle':
                pdf.circle(
                    k*parseInt(n.getAttribute('cx')),
                    k*parseInt(n.getAttribute('cy')),
                    k*parseInt(n.getAttribute('r')),
                    colorMode
                );
                break;
            case 'text':
                if(n.hasAttribute('font-family')) {
                    switch(n.getAttribute('font-family').toLowerCase()) {
                        case 'serif': pdf.setFont('times'); break;
                        case 'monospace': pdf.setFont('courier'); break;
                        default:
                            n.getAttribute('font-family', 'sans-serif');
                            pdf.setFont('Helvetica');
                    }
                }
                if(hasFillColor) {
                    pdf.setTextColor(fillRGB.r, fillRGB.g, fillRGB.b);
                }
                var fontType = "";
                if(n.hasAttribute('font-weight')) {
                    if(n.getAttribute('font-weight') == "bold") {
                        fontType = "bold";
                    }
                }
                if(n.hasAttribute('font-style')) {
                    if(n.getAttribute('font-style') == "italic") {
                        fontType += "italic";
                    }
                }
                pdf.setFontType(fontType);
                var pdfFontSize = 16;
				if(n.hasAttribute('font-size')) {
                    pdfFontSize = parseInt(n.getAttribute('font-size'));
                }
                var box = n.getBBox();
                //FIXME: use more accurate positioning!!
                var x=parseInt(n.getAttribute('x')), y=parseInt(n.getAttribute('y')), xOffset = 0;
                if(n.hasAttribute('text-anchor')) {
                    switch(n.getAttribute('text-anchor')) {
                        case 'end': xOffset = box.width; break;
                        case 'middle': xOffset = box.width / 2; break;
                        case 'start': break;
                        case 'default': n.getAttribute('text-anchor', 'start');
                    }
                    x = parseInt(n.getAttribute('x')) - xOffset;
                }
				//console.log("fontSize:", pdfFontSize, "text:", n.text());
                pdf.setFontSize(pdfFontSize).text(
                    k * x,
                    k * y,
                    n.innerHTML
                );
                break;
            //TODO: image
            default:
                if(remove) {
                    console.log("can't translate to pdf:", n);
                    element.removeChild(n);
					i--;
                }
        }
    }
    return pdf;
};

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
