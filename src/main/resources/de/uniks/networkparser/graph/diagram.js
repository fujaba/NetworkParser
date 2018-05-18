var DiagramJS =
/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = "./main.ts");
/******/ })
/************************************************************************/
/******/ ({

/***/ "./Adapter.ts":
/*!********************!*\
  !*** ./Adapter.ts ***!
  \********************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var Adapter = (function () {
    function Adapter() {
        this.id = null;
    }
    return Adapter;
}());
exports.Adapter = Adapter;


/***/ }),

/***/ "./Bridge.ts":
/*!*******************!*\
  !*** ./Bridge.ts ***!
  \*******************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var controls = __webpack_require__(/*! ./elements/nodes */ "./elements/nodes/index.ts");
var adapters = __webpack_require__(/*! ./adapters */ "./adapters/index.ts");
var Data_1 = __webpack_require__(/*! ./Data */ "./Data.ts");
var Control_1 = __webpack_require__(/*! ./Control */ "./Control.ts");
var Adapter_1 = __webpack_require__(/*! ./Adapter */ "./Adapter.ts");
var Graph_1 = __webpack_require__(/*! ./elements/Graph */ "./elements/Graph.ts");
var util_1 = __webpack_require__(/*! ./util */ "./util.ts");
var Bridge = (function (_super) {
    __extends(Bridge, _super);
    function Bridge(viewRoot) {
        var _this = _super.call(this) || this;
        _this.controlFactory = {};
        _this.adapterFactory = {};
        _this.controls = {};
        _this.adapters = {};
        _this.items = {};
        _this.controlNo = 1;
        _this.online = true;
        _this.language = navigator.language.toUpperCase();
        _this.addListener = function (listener) {
            this.listener.push(listener);
        };
        var i;
        if (viewRoot) {
            _this.$view = viewRoot;
        }
        var keys = Object.keys(adapters);
        for (i = 0; i < keys.length; i++) {
            var child = adapters[keys[i]];
            if (child && child.id) {
                _this.adapterFactory[child.id.toLowerCase()] = child;
            }
        }
        keys = Object.keys(controls);
        for (i = 0; i < keys.length; i++) {
            _this.addControl(controls[keys[i]]);
        }
        _this.addControl(Graph_1.Graph);
        var that = _this;
        window.addEventListener('load', function () {
            var updateOnlineStatus = function updateOnlineStatus() {
                that.setOnline(navigator.onLine);
            };
            window.addEventListener('online', updateOnlineStatus);
            window.addEventListener('offline', updateOnlineStatus);
        });
        return _this;
    }
    Bridge.prototype.setOnline = function (value) {
        this.online = value;
        if (this.toolBar.children[0]) {
            this.toolBar.children[0].className = value ? 'online' : 'offline';
        }
    };
    Bridge.prototype.addToolbar = function () {
        if (this.toolBar) {
            return false;
        }
        this.toolBar = document.createElement('div');
        this.toolBar.className = 'onlineStatus';
        var child = document.createElement('div');
        child.className = 'online';
        this.toolBar.appendChild(child);
        child = document.createElement('div');
        child.className = 'lang';
        child.innerHTML = this.language;
        this.toolBar.appendChild(child);
        var body = document.getElementsByTagName('body')[0];
        body.insertBefore(this.toolBar, body.firstChild);
        this.setOnline(this.online);
        return true;
    };
    Bridge.prototype.addControl = function (control) {
        if (control && control.name) {
            this.controlFactory[control.name.toLowerCase()] = control;
        }
    };
    Bridge.prototype.getId = function () {
        return 'control' + (this.controlNo++);
    };
    Bridge.prototype.adapterUpdate = function (message) {
        var keys = Object.keys(this.adapters);
        if (keys.length > 0) {
            var i = void 0;
            for (i = 0; i < keys.length; i++) {
                var adapterList = this.adapters[keys[i]];
                if (adapterList instanceof Adapter_1.Adapter) {
                    adapterList.update(message);
                }
                else {
                    for (var _i = 0, adapterList_1 = adapterList; _i < adapterList_1.length; _i++) {
                        var adapter = adapterList_1[_i];
                        adapter.update(message);
                    }
                }
            }
        }
    };
    Bridge.prototype.load = function (json, owner) {
        var config = {}, className, id;
        if (typeof (json) === 'string') {
            config['id'] = '' + json;
            var item = document.getElementById(config['id']);
            var className_1;
            if (item) {
                className_1 = item.getAttribute('class');
                if (!className_1) {
                    className_1 = item.getAttribute('classname') || '';
                }
                if (item.getAttribute('property')) {
                    if (this.hasItem(item.getAttribute('property'))) {
                        var data = this.getItem(item.getAttribute('property'));
                        for (var key in data.prop) {
                            if (item.getAttribute(key)) {
                                data.setValue(key, item.getAttribute(key));
                            }
                        }
                        if (item.getAttribute('property')) {
                        }
                    }
                }
            }
            else {
                className_1 = '' + json;
            }
            className_1 = className_1.toLowerCase();
            config['className'] = className_1;
        }
        else {
            config = json;
        }
        if (!config['id']) {
            config['id'] = this.getId();
        }
        if (!config['className'] && (config['type'] === 'clazzdiagram' || config['type'] === 'objectdiagram')) {
            config['className'] = 'graph';
        }
        className = config['className'] || config['class'];
        className = className.toLocaleLowerCase();
        id = config['id'];
        if ((config['prop'] || config['upd'] || config['rem']) && this.controls[id] === null) {
            var newData = !this.hasItem(config['id']);
            var item = this.getItem(config['id']);
            if (newData) {
                for (var i in this.controls) {
                    if (this.controls.hasOwnProperty(i) === false) {
                        continue;
                    }
                    this.controls[i].addItem(this, item);
                }
            }
            item.addProperties(config);
            this.adapterUpdate(JSON.stringify(config));
            return item;
        }
        var control;
        if (this.controls[id]) {
            control = this.controls[id];
            control.initControl(json);
            this.adapterUpdate(JSON.stringify(config));
            return control;
        }
        if (typeof (this.controlFactory[className]) === 'object' || typeof (this.controlFactory[className]) === 'function') {
            var obj = this.controlFactory[className];
            control = new obj(json);
            util_1.Util.initControl(owner || this, control, config['property'], id, json);
            if (control.id) {
                this.controls[control.id] = control;
            }
            else {
                this.controls[id] = control;
            }
            if (typeof control.getSVG === 'function' && typeof control.getSize === 'function') {
                var size = control.getSize();
                var svg = util_1.Util.createShape({
                    tag: 'svg',
                    id: 'root',
                    width: size.x,
                    height: size.y
                });
                var view = control.getSVG();
                svg.appendChild(view);
                document.getElementsByTagName('body')[0].appendChild(svg);
            }
            return control;
        }
        return null;
    };
    Bridge.prototype.hasItem = function (id) {
        if (this.items[id] !== undefined) {
            return true;
        }
        id = id.split('.')[0];
        return (this.items[id] !== undefined);
    };
    Bridge.prototype.getItems = function () {
        return this.items;
    };
    Bridge.prototype.getItem = function (id) {
        var item = this.items[id];
        if (!item) {
            id = id.split('.')[0];
            item = this.items[id];
            if (!item) {
                item = new Data_1.default();
                item.id = id;
                this.items[id] = item;
            }
        }
        return item;
    };
    Bridge.prototype.setValue = function (object, attribute, newValue, oldValue) {
        alert('Bridge.setValue: oldVal:' + oldValue + ', newVal: ' + +newValue + ', attribute: ' + attribute + ', object: ' + JSON.stringify(object));
        var obj;
        var id;
        if (object instanceof String || typeof object === 'string') {
            id = object.toString();
            obj = this.getItem(id);
        }
        else if (object instanceof Data_1.default) {
            obj = object;
            id = object.id;
        }
        else if (object.hasOwnProperty('id')) {
            obj = object;
            id = object['id'];
        }
        else {
            console.log('object is neither Data nor String..');
            return false;
        }
        if (obj) {
            obj.setValue(attribute, newValue);
        }
        return true;
    };
    Bridge.prototype.getValue = function (object, attribute) {
        var obj;
        var id;
        if (object instanceof String || typeof object === 'string') {
            id = object.toString();
            obj = this.getItem(id);
        }
        else if (object.hasOwnProperty('id')) {
            obj = object;
        }
        else {
            console.log('object is neither Data nor String..');
            return;
        }
        if (obj) {
            if (obj.hasOwnProperty(attribute)) {
                return obj[attribute];
            }
            else if (obj instanceof Data_1.default) {
                return obj.getValue(attribute);
            }
            else {
                return null;
            }
        }
    };
    Bridge.prototype.getNumber = function (object, attribute, defaultValue) {
        if (defaultValue === void 0) { defaultValue = 0; }
        var res = this.getValue(object, attribute);
        if (typeof res === 'number') {
            return res;
        }
        else if (typeof res === 'string') {
            var value = Number(res);
            if (value || value === 0) {
                return value;
            }
        }
        return defaultValue;
    };
    Bridge.prototype.getControl = function (controlId) {
        return this.controls[controlId];
    };
    Bridge.prototype.registerListener = function (eventType, control, callBackfunction) {
        if (typeof control === 'string') {
            control = this.getControl(control);
        }
        if (!control) {
            return null;
        }
        if (eventType) {
            eventType = eventType.toLowerCase();
        }
        control.registerListenerOnHTMLObject(eventType);
        if (callBackfunction) {
            var adapter = new DelegateAdapter();
            adapter.callBackfunction = callBackfunction;
            adapter.id = control.getId();
            this.addAdapter(adapter, eventType);
        }
        return control;
    };
    Bridge.prototype.addAdapter = function (adapter, eventType) {
        if (!eventType) {
            eventType = '';
        }
        var result;
        if (adapter instanceof String) {
            var obj = this.adapterFactory[adapter.toLowerCase()];
            result = new obj();
        }
        else {
            result = adapter;
        }
        var handlers = this.adapters[eventType];
        if (handlers === null || handlers === undefined) {
            handlers = [];
            this.adapters[eventType] = handlers;
        }
        handlers.push(result);
        return result;
    };
    Bridge.prototype.fireEvent = function (evt) {
        var handlers = this.adapters[''];
        if (handlers) {
            for (var i = 0; i < handlers.length; i++) {
                var adapter = handlers[i];
                if (adapter.id === null || adapter.id === evt['id']) {
                    adapter.update(evt);
                }
            }
        }
        handlers = this.adapters[evt['eventType']];
        if (handlers) {
            for (var i = 0; i < handlers.length; i++) {
                var adapter = handlers[i];
                if (adapter.id === null || adapter.id === evt['id']) {
                    adapter.update(evt);
                }
            }
        }
    };
    Bridge.version = '0.42.01.1601007-1739';
    return Bridge;
}(Control_1.Control));
exports.Bridge = Bridge;
var DelegateAdapter = (function (_super) {
    __extends(DelegateAdapter, _super);
    function DelegateAdapter() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    DelegateAdapter.prototype.update = function (evt) {
        if (this.adapter) {
            this.adapter.update(evt);
            return true;
        }
        else if (this.callBackfunction) {
            return this.executeFunction(this.callBackfunction, evt);
        }
        return false;
    };
    DelegateAdapter.prototype.setAdapter = function (adapter) {
        this.adapter = adapter;
        return true;
    };
    DelegateAdapter.prototype.executeFunction = function (strValue, evt) {
        var scope = window;
        var scopeSplit = strValue.split('.');
        for (var i = 0; i < scopeSplit.length - 1; i++) {
            scope = scope[scopeSplit[i]];
            if (scope === undefined) {
                return false;
            }
        }
        var fn = scope[scopeSplit[scopeSplit.length - 1]];
        if (typeof fn === 'function') {
            fn.call(scope);
            return true;
        }
        else {
            window['callBack1'].update(evt);
        }
        return false;
    };
    return DelegateAdapter;
}(Adapter_1.Adapter));
exports.DelegateAdapter = DelegateAdapter;


/***/ }),

/***/ "./BridgeElement.ts":
/*!**************************!*\
  !*** ./BridgeElement.ts ***!
  \**************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var BridgeElement = (function () {
    function BridgeElement(model) {
        this.model = model;
        this.id = model.id;
        BridgeElement.elementSet.push(this);
    }
    BridgeElement.elementSet = [];
    return BridgeElement;
}());
exports.default = BridgeElement;


/***/ }),

/***/ "./CSS.ts":
/*!****************!*\
  !*** ./CSS.ts ***!
  \****************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var util_1 = __webpack_require__(/*! ./util */ "./util.ts");
var CSS = (function () {
    function CSS(name, item) {
        var i, value, border, prop, el;
        this.name = name;
        this.css = {};
        if (!item) {
            return;
        }
        el = window.getComputedStyle(item, null);
        border = el.getPropertyValue('border');
        for (i in el) {
            prop = i;
            value = el.getPropertyValue(prop);
            if (value && value !== '') {
                if (border) {
                    if (prop === 'border-bottom' || prop === 'border-right' || prop === 'border-top' || prop === 'border-left') {
                        if (value !== border) {
                            this.css[prop] = value;
                        }
                    }
                    else if (prop === 'border-color' || prop === 'border-bottom-color' || prop === 'border-right-color' || prop === 'border-top-color' || prop === 'border-left-color') {
                        if (border.substring(border.length - value.length) !== value) {
                            this.css[prop] = value;
                        }
                    }
                    else if (prop === 'border-width') {
                        if (border.substring(0, value.length) !== value) {
                            this.css[prop] = value;
                        }
                    }
                    else {
                        this.css[prop] = value;
                    }
                }
                else {
                    this.css[prop] = value;
                }
            }
        }
    }
    CSS.getStdDef = function () {
        var child, def = util_1.Util.create({ tag: 'defs' });
        child = util_1.Util.create({ tag: 'filter', id: 'drop-shadow' });
        child.appendChild(util_1.Util.create({ tag: 'feGaussianBlur', 'in': 'SourceAlpha', result: 'blur-out', stdDeviation: 2 }));
        child.appendChild(util_1.Util.create({ tag: 'feOffset', 'in': 'blur-out', dx: 2, dy: 2 }));
        child.appendChild(util_1.Util.create({ tag: 'feBlend', 'in': 'SourceGraphic', mode: 'normal' }));
        def.appendChild(child);
        child = util_1.Util.create({ tag: 'linearGradient', id: 'reflect', x1: 0, x2: 0, y1: '50%', y2: 0, spreadMethod: 'reflect' });
        child.appendChild(util_1.Util.create({ tag: 'stop', 'stop-color': '#aaa', offset: 0 }));
        child.appendChild(util_1.Util.create({ tag: 'stop', 'stop-color': '#eee', offset: '100%' }));
        def.appendChild(child);
        child = util_1.Util.create({ tag: 'linearGradient', id: 'classelement', x1: 0, x2: '100%', y1: '100%', y2: 0 });
        child.appendChild(util_1.Util.create({ tag: 'stop', 'stop-color': '#fff', offset: 0 }));
        child.appendChild(util_1.Util.create({ tag: 'stop', 'stop-color': '#d3d3d3', offset: 1 }));
        def.appendChild(child);
        return def;
    };
    CSS.getSubstring = function (str, search, startChar, endChar, splitter) {
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
        return '';
    };
    CSS.addStyle = function (board, styleName) {
        var defs, style, css;
        if (styleName.baseVal || styleName.baseVal === '') {
            styleName = styleName.baseVal;
        }
        if (!styleName) {
            return;
        }
        defs = CSS.getDefs(board);
        if (defs.getElementsByTagName('style').length > 0) {
            style = defs.getElementsByTagName('style')[0];
        }
        else {
            style = util_1.Util.create({ tag: 'style' });
            style.item = {};
            defs.appendChild(style);
        }
        if (!style.item[styleName]) {
            css = util_1.Util.getStyle(styleName);
            style.item[styleName] = css;
            style.innerHTML = style.innerHTML + '\n.' + styleName + css.getSVGString(board);
        }
    };
    CSS.addStyles = function (board, item) {
        if (!item) {
            return;
        }
        var items, i, className = item.className;
        if (className) {
            if (className.baseVal || className.baseVal === '') {
                className = className.baseVal;
            }
        }
        if (className) {
            items = className.split(' ');
            for (i = 0; i < items.length; i += 1) {
                CSS.addStyle(board, items[i].trim());
            }
        }
        for (i = 0; i < item.childNodes.length; i += 1) {
            this.addStyles(board, item.childNodes[i]);
        }
    };
    CSS.getDefs = function (board) {
        var defs;
        if (board.getElementsByTagName('defs').length < 1) {
            defs = util_1.Util.create({ tag: 'defs' });
            board.insertBefore(defs, board.childNodes[0]);
        }
        else {
            defs = board.getElementsByTagName('defs')[0];
        }
        return defs;
    };
    CSS.prototype.add = function (key, value) {
        this.css[key] = value;
    };
    CSS.prototype.get = function (key) {
        var i;
        for (i in this.css) {
            if (i === key) {
                return this.css[key];
            }
        }
        return null;
    };
    CSS.prototype.getNumber = function (key) {
        return parseInt((this.get(key) || '0').replace('px', ''), 10);
    };
    CSS.prototype.getSVGString = function (board) {
        var str, pos, style, defs, value, filter, z;
        str = '{';
        for (style in this.css) {
            if (!this.css.hasOwnProperty(style)) {
                continue;
            }
            if (style === 'border') {
                pos = this.css[style].indexOf(' ');
                str = str + 'stroke-width: ' + this.css[style].substring(0, pos) + ';';
                pos = this.css[style].indexOf(' ', pos + 1);
                str = str + 'stroke:' + this.css[style].substring(pos) + ';';
            }
            else if (style === 'background-color') {
                str = str + 'fill: ' + this.css[style] + ';';
            }
            else if (style === 'background') {
                value = CSS.getSubstring(this.css[style], 'linear-gradient', '(', ')', ',');
                if (value.length > 0) {
                    defs = CSS.getDefs(board);
                    if (value[0] === '45deg') {
                        pos = 1;
                        filter = util_1.Util.create({
                            tag: 'linearGradient',
                            'id': this.name,
                            x1: '0%',
                            x2: '100%',
                            y1: '100%',
                            y2: '0%'
                        });
                    }
                    else {
                        filter = util_1.Util.create({
                            tag: 'linearGradient',
                            'id': this.name,
                            x1: '0%',
                            x2: '0%',
                            y1: '100%',
                            y2: '0%'
                        });
                        pos = 0;
                    }
                    defs.appendChild(filter);
                    while (pos < value.length) {
                        value[pos] = value[pos].trim();
                        z = value[pos].lastIndexOf(' ');
                        filter.appendChild(util_1.Util.create({
                            tag: 'stop',
                            'offset': value[pos].substring(z + 1),
                            style: { 'stop-color': value[pos].substring(0, z) }
                        }));
                        pos += 1;
                    }
                    str = str + 'fill: url(#' + this.name + ');';
                    continue;
                }
                str = str + style + ': ' + this.css[style] + ';';
            }
            else {
                str = str + style + ': ' + this.css[style] + ';';
            }
        }
        str = str + '}';
        return str;
    };
    return CSS;
}());
exports.CSS = CSS;


/***/ }),

/***/ "./Control.ts":
/*!********************!*\
  !*** ./Control.ts ***!
  \********************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var Data_1 = __webpack_require__(/*! ./Data */ "./Data.ts");
var EventListener_1 = __webpack_require__(/*! ./EventListener */ "./EventListener.ts");
var Control = (function () {
    function Control() {
        this.$viewData = null;
        this.$viewData = this.initViewDataProperties(this.$viewData);
    }
    Control.prototype.getToolBarIcon = function () {
        return null;
    };
    Control.prototype.initViewDataProperties = function (oldData) {
        var data = new Data_1.default();
        if (oldData) {
            oldData.removeListener(this);
            var keys = oldData.getKeys();
            for (var i = 0; i < keys.length; i++) {
                var attr = keys[i];
                if (this.$view) {
                    if (this.$view[attr] === null) {
                        continue;
                    }
                    data.setValue(attr, this.$view[attr]);
                }
                else {
                    data.setValue(attr, null);
                }
            }
        }
        data.addListener(this);
        return data;
    };
    Control.prototype.setView = function (element) {
        var oldElement = null;
        if (this.$view) {
            oldElement = this.$view;
            if (this.$viewListener) {
                oldElement.removeEventListener('change', this.$viewListener);
            }
        }
        this.$view = element;
        if (this.$viewListener) {
            element.addEventListener('change', this.$viewListener);
        }
        this.$viewData = this.initViewDataProperties(this.$viewData);
        return element;
    };
    Control.prototype.init = function (owner, property, id) {
        if (!this.$owner) {
            this.$owner = owner;
        }
        if (!this.id) {
            this.id = id;
        }
        if (!this.property) {
            this.property = this.generateID(property, id);
        }
        return this;
    };
    Control.prototype.createEventListener = function () {
        return new EventListener_1.default();
    };
    Control.prototype.getRoot = function () {
        if (this.$owner) {
            return this.$owner.getRoot();
        }
        return this;
    };
    Control.prototype.initControl = function (data) {
        var _this = this;
        if (this.$view === null || this.$viewData === null) {
            return;
        }
        if (data.hasOwnProperty('prop')) {
            for (var key in data.prop) {
                var oldValue = this.$viewData.getValue(key);
                if (this.$view) {
                    this.updateElement(key, oldValue, data.prop[key]);
                }
            }
            return;
        }
        var hasRem = data.hasOwnProperty('rem');
        var removed = [];
        if (data.hasOwnProperty('upd')) {
            for (var key in data.upd) {
                var oldValue = void 0;
                var newValue = data.upd[key];
                var entity = void 0;
                var temp = false;
                if (temp) {
                    if (hasRem && data.rem.hasOwnProperty(key)) {
                        removed.push(data.rem[key]);
                    }
                    continue;
                }
                if (hasRem && data.rem.hasOwnProperty(key)) {
                    oldValue = data.rem[key];
                    if (this.$model && this.$model.getValue(key) == oldValue) {
                        entity = this.$model;
                    }
                    delete data.rem[key];
                    if (entity === null) {
                        continue;
                    }
                }
                if (entity) {
                    if (!hasRem) {
                        if (entity === this.$model) {
                            oldValue = this.$model.getValue(key);
                        }
                        else {
                        }
                    }
                }
                else {
                    if (this.$model) {
                        oldValue = this.$model.getValue(key);
                        entity = this.$model;
                    }
                    if (oldValue === null) {
                    }
                }
                if (newValue === oldValue) {
                    continue;
                }
                var viewDataOldValue = this.$viewData.getValue(key);
                if (entity === this.$viewData) {
                    if (this.$view) {
                        this.updateElement(key, viewDataOldValue, newValue);
                    }
                }
                else {
                    this.updateElement(key, viewDataOldValue, newValue);
                }
                this.getRoot().setValue(entity, key, newValue, oldValue);
            }
        }
        if (hasRem) {
            for (var key in data.rem) {
                if (removed.hasOwnProperty(key)) {
                    continue;
                }
                var oldValue = void 0;
                this.updateElement(key, null, null);
                if (this.$model) {
                }
            }
        }
        if (this.property) {
            this.$view['onchange'] = (function (ev) {
                _this.controlChanged(ev);
            });
        }
    };
    Control.prototype.getItem = function (id) {
        return null;
    };
    Control.prototype.hasItem = function (id) {
        return false;
    };
    Control.prototype.getItems = function () {
        return new Object();
    };
    Control.prototype.setValue = function (object, attribute, newValue, oldValue) {
        return false;
    };
    Control.prototype.propertyChange = function (entity, property, oldValue, newValue) {
        if (oldValue === newValue || this.$viewData === null) {
            return;
        }
        if (oldValue === this.$viewData.getValue(property)) {
            return;
        }
        this.$viewData.setValue(property, newValue);
        if (this.$viewData) {
            this.$viewData.setValue(property, newValue);
        }
        if (this.$model) {
            this.$model.setValue(property, newValue);
        }
        this.updateElement(property, oldValue, newValue);
    };
    Control.prototype.controlChanged = function (ev) {
        if (this.$view instanceof HTMLInputElement === false) {
            return;
        }
        var element = this.$view;
        if (element.checkValidity()) {
        }
    };
    Control.prototype.updateElement = function (property, oldValue, newValue) {
        if (this.$view && this.$view.hasAttribute(property)) {
            this.$view.setAttribute(property, newValue);
        }
    };
    Control.prototype.getId = function () {
        return this.id;
    };
    Control.prototype.load = function (json, owner) {
    };
    Control.prototype.addItem = function (source, entity) {
        if (entity) {
            if (!this.property || entity.hasProperty(this.property)) {
                entity.addListener(this, this.property);
                this.$model = entity;
            }
        }
    };
    Control.prototype.appendChild = function (child) {
        if (this.$view) {
            this.$view.appendChild(child.$view);
        }
        else {
            document.getElementsByTagName('body')[0].appendChild(child.$view);
        }
    };
    Control.prototype.removeChild = function (child) {
        if (this.$view) {
            this.$view.removeChild(child.$view);
        }
        else {
            document.getElementsByTagName('body')[0].removeChild(child.$view);
        }
    };
    Control.prototype.setProperty = function (property) {
        if (!this.property) {
            return;
        }
        var objId = property.split('.')[0];
        var object = null;
        if (this.$owner.hasItem(objId)) {
            object = this.$owner.getItem(objId);
        }
        if (this.$model) {
            this.$model.removeListener(this, this.lastProperty);
        }
        this.property = property;
        if (object) {
            object.addListener(this, this.lastProperty);
            this.$model = object;
            this.updateElement(this.lastProperty, this.$viewData.getValue(this.lastProperty), object.prop[this.lastProperty]);
        }
    };
    Control.prototype.registerListenerOnHTMLObject = function (eventType) {
        return this.registerEventListener(eventType, this.$view);
    };
    Control.prototype.fireEvent = function (evt) {
    };
    Control.prototype.isClosed = function () {
        return this['closed'];
    };
    Control.prototype.getShowed = function () {
        if (this.isClosed()) {
            return this.$owner.getShowed();
        }
        return this;
    };
    Control.prototype.getControlDataID = function () {
        return this.id + '_data';
    };
    Control.prototype.generateID = function (property, id) {
        if (property) {
            return property;
        }
        if (id) {
            return id + '.' + '_data';
        }
        return null;
    };
    Control.prototype.updateViewData = function () {
        if (!this.$view) {
            return;
        }
        var keys = this.$viewData.getKeys();
        for (var i = 0; i < keys.length; i++) {
            var attr = keys[i];
            if (this.$view[attr] === null) {
                continue;
            }
            this.$viewData.setValue(attr, this.$view[attr]);
        }
    };
    Control.prototype.registerEventListener = function (eventType, htmlElement) {
        if (!htmlElement) {
            return false;
        }
        if (htmlElement instanceof HTMLElement === false) {
            return false;
        }
        var control = this;
        var listener = function (t) {
            t.eventType = eventType;
            t.id = control.id;
            control.$owner.fireEvent(t);
        };
        htmlElement.addEventListener(eventType, listener);
        return true;
    };
    Object.defineProperty(Control.prototype, "lastProperty", {
        get: function () {
            if (!this.property) {
                return '';
            }
            var arr = this.property.split('.');
            return arr[arr.length - 1];
        },
        enumerable: true,
        configurable: true
    });
    return Control;
}());
exports.Control = Control;


/***/ }),

/***/ "./Data.ts":
/*!*****************!*\
  !*** ./Data.ts ***!
  \*****************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var Data = (function () {
    function Data() {
        this.prop = {};
        this.$listener = {};
    }
    Data.prototype.getKeys = function () {
        return Object.keys(this.prop);
    };
    Data.prototype.addProperties = function (values) {
        if (!values) {
            return;
        }
        if (values['prop']) {
            var prop = values['prop'];
            for (var property in prop) {
                if (prop.hasOwnProperty(property) === false) {
                    continue;
                }
                if (prop[property] !== null && '' !== prop[property]) {
                    this.setValue(property, prop[property]);
                }
            }
        }
        else {
            var upd = values['upd'] || {};
            var rem = values['rem'] || {};
            for (var property in upd) {
                if (upd.hasOwnProperty(property) === false) {
                    continue;
                }
                if (rem.hasOwnProperty(property) === false) {
                    this.setValue(property, upd[property]);
                }
                else {
                    if (this.getValue(property) === rem[property]) {
                        this.setValue(property, upd[property]);
                    }
                }
            }
        }
    };
    Data.nullCheck = function (property) {
        if (property === undefined || property == null) {
            property = "";
        }
        return property;
    };
    Data.prototype.getListeners = function (property) {
        property = Data.nullCheck(property);
        return this.$listener[property];
    };
    Data.prototype.getValue = function (attribute) {
        return this.prop[attribute];
    };
    Data.prototype.setValue = function (attribute, newValue) {
        var oldValue = this.prop[attribute];
        if (oldValue == newValue && newValue !== null) {
            return;
        }
        this.prop[attribute] = newValue;
        this.firePropertyChange(attribute, oldValue, newValue);
    };
    Data.prototype.firePropertyChange = function (attribute, oldValue, newValue) {
        attribute = Data.nullCheck(attribute);
        var listeners = this.getListeners(attribute);
        if (listeners) {
            for (var i in listeners) {
                listeners[i].propertyChange(this, attribute, oldValue, newValue);
            }
        }
        listeners = this.getListeners(null);
        if (listeners) {
            for (var i in listeners) {
                listeners[i].propertyChange(this, attribute, oldValue, newValue);
            }
        }
    };
    Data.prototype.addTo = function (attribute, newValue) {
        var add;
        if (this.prop[attribute]) {
            if (this.prop[attribute].contains(newValue) === false) {
                add = true;
            }
        }
        else {
            this.prop[attribute] = [];
            add = true;
        }
        if (add) {
            this.prop[attribute].push(newValue);
            this.firePropertyChange(attribute, null, newValue);
        }
        return add;
    };
    Data.prototype.removeFrom = function (attribute, newValue) {
        if (!this.prop[attribute]) {
            return true;
        }
        var pos = this.prop[attribute].indexOf(newValue);
        if (pos < 0) {
            return true;
        }
        this.prop[attribute].splice(pos, 1);
        this.firePropertyChange(attribute, newValue, null);
        return true;
    };
    Data.prototype.addListener = function (control, property) {
        var listeners = this.getListeners(property);
        if (!listeners) {
            listeners = [];
            this.$listener[Data.nullCheck(property)] = listeners;
        }
        listeners.push(control);
    };
    Data.prototype.removeListener = function (control, property) {
        var listeners = this.getListeners(property);
        if (listeners === null) {
            return;
        }
        var pos = listeners.indexOf(control);
        if (pos >= 0) {
            listeners.splice(pos, 1);
        }
        if (listeners.length == 0 && Data.nullCheck(property) != "") {
            delete this.$listener[property];
        }
    };
    Data.prototype.hasProperty = function (property) {
        return this.prop.hasOwnProperty(property);
    };
    Data.prototype.addFrom = function (attribute, oldData) {
        if (oldData) {
            this.setValue(attribute, oldData.getValue(attribute));
        }
        else {
            this.setValue(attribute, null);
        }
    };
    Data.prototype.removeKey = function (key) {
        if (this.hasProperty(key)) {
            var oldValue = this.prop[key];
            delete this.prop[key];
            return oldValue;
        }
        return null;
    };
    return Data;
}());
exports.default = Data;


/***/ }),

/***/ "./EventBus.ts":
/*!*********************!*\
  !*** ./EventBus.ts ***!
  \*********************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var EventBus = (function () {
    function EventBus() {
    }
    EventBus.setActiveHandler = function (handler) {
        this.$activeHandler = handler;
    };
    EventBus.isHandlerActiveOrFree = function (handler, notEmpty) {
        if (notEmpty) {
            return this.$activeHandler === handler;
        }
        return this.$activeHandler === handler || this.$activeHandler === '' || this.$activeHandler === undefined;
    };
    EventBus.isAnyHandlerActive = function () {
        return !(this.$activeHandler === '' || this.$activeHandler === undefined);
    };
    EventBus.releaseActiveHandler = function () {
        this.$activeHandler = '';
    };
    EventBus.getActiveHandler = function () {
        return this.$activeHandler;
    };
    EventBus.register = function (control, view) {
        var events;
        if (typeof control['getEvents'] === 'function') {
            events = control['getEvents']();
        }
        if (!events || !view) {
            return;
        }
        for (var _i = 0, events_1 = events; _i < events_1.length; _i++) {
            var event_1 = events_1[_i];
            this.registerEvent(view, event_1, control);
        }
    };
    EventBus.registerEvent = function (view, event, control) {
        var pos = event.indexOf(':');
        if (pos > 0) {
            view.addEventListener(event.substr(pos + 1).toLowerCase(), function (evt) { EventBus.publish(control, evt); });
        }
        else {
            view.addEventListener(event.substr(pos + 1).toLowerCase(), function (evt) { EventBus.publish(control, evt); });
        }
    };
    EventBus.publish = function (element, evt) {
        var handlers = EventBus.handlers[evt.type];
        if (handlers) {
            for (var _i = 0, handlers_1 = handlers; _i < handlers_1.length; _i++) {
                var handler = handlers_1[_i];
                handler.handle(evt, element);
            }
        }
    };
    EventBus.subscribe = function (handler) {
        var eventTypes = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            eventTypes[_i - 1] = arguments[_i];
        }
        for (var _a = 0, eventTypes_1 = eventTypes; _a < eventTypes_1.length; _a++) {
            var event_2 = eventTypes_1[_a];
            var handlers = EventBus.handlers[event_2];
            if (handlers === null || handlers === undefined) {
                handlers = [];
                EventBus.handlers[event_2] = handlers;
            }
            handlers.push(handler);
        }
    };
    EventBus.CREATE = 'Create';
    EventBus.EDITOR = 'Editor';
    EventBus.OPENPROPERTIES = 'openProperties';
    EventBus.RELOADPROPERTIES = 'reloadProperties';
    EventBus.ELEMENTMOUSEDOWN = 'ELEMENT:MOUSEDOWN';
    EventBus.ELEMENTMOUSEUP = 'ELEMENT:MOUSEUP';
    EventBus.ELEMENTMOUSELEAVE = 'ELEMENT:MOUSELEAVE';
    EventBus.ELEMENTMOUSEMOVE = 'ELEMENT:MOUSEMOVE';
    EventBus.ELEMENTMOUSEWHEEL = 'ELEMENT:MOUSEWHEEL';
    EventBus.ELEMENTCLICK = 'ELEMENT:CLICK';
    EventBus.ELEMENTDBLCLICK = 'ELEMENT:DBLCLICK';
    EventBus.ELEMENTDRAG = 'ELEMENT:DRAG';
    EventBus.ELEMENTDRAGOVER = 'ELEMENT:DRAGOVER';
    EventBus.ELEMENTDROP = 'ELEMENT:DROP';
    EventBus.ELEMENTDRAGLEAVE = 'ELEMENT:DRAGLEAVE';
    EventBus.EVENTS = [
        EventBus.CREATE,
        EventBus.EDITOR,
        EventBus.OPENPROPERTIES,
        EventBus.RELOADPROPERTIES,
        EventBus.ELEMENTMOUSEDOWN,
        EventBus.ELEMENTMOUSEUP,
        EventBus.ELEMENTMOUSELEAVE,
        EventBus.ELEMENTMOUSEMOVE,
        EventBus.ELEMENTMOUSEWHEEL,
        EventBus.ELEMENTCLICK,
        EventBus.ELEMENTDRAG,
        EventBus.ELEMENTDBLCLICK,
        EventBus.ELEMENTDRAGOVER,
        EventBus.ELEMENTDROP,
        EventBus.ELEMENTDRAGLEAVE,
    ];
    EventBus.handlers = {};
    EventBus.$activeHandler = '';
    return EventBus;
}());
exports.EventBus = EventBus;


/***/ }),

/***/ "./EventListener.ts":
/*!**************************!*\
  !*** ./EventListener.ts ***!
  \**************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var EventListener = (function () {
    function EventListener() {
    }
    Object.defineProperty(EventListener.prototype, "onUpdate", {
        get: function () {
            return this.$onUpdate;
        },
        set: function (value) {
            this.$onUpdate = value;
        },
        enumerable: true,
        configurable: true
    });
    EventListener.prototype.update = function (event) {
        this.$onUpdate(event);
    };
    return EventListener;
}());
exports.default = EventListener;


/***/ }),

/***/ "./JSEPS.ts":
/*!******************!*\
  !*** ./JSEPS.ts ***!
  \******************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var JSEPS = (function () {
    function JSEPS(options) {
        this.max = 0;
        this.min = 999;
        var hasInverting = typeof (options.inverting);
        this.inverting = (options && hasInverting !== 'undefined' ? options.inverting : true);
        this.output = ['%!PS-Adobe-3.0 EPSF-3.0', '1 setlinewidth'];
        this.out('/FSD {findfont exch scalefont def} bind def % In the document prolog: define');
        this.out('/SMS {setfont moveto show} bind def % some useful procedures');
        this.out('/MS {moveto show} bind def');
        this.out('/F1  10  /Helvetica  FSD % At the start of the script: set up');
        this.font = 1;
    }
    JSEPS.prototype.out = function (value) { this.output.push(value); };
    JSEPS.prototype.rect = function (x, y, width, height, style) {
        y = y + (this.inverting ? height : 0);
        if (style && style.indexOf('fill:url(#classelement);') >= 0) {
            this.out('gsave 0.93 0.93 0.93 setrgbcolor newpath ' + x + ' ' + this.y(y) + ' ' + width + ' ' + height + ' rectfill grestore');
        }
        else {
            this.out('newpath ' + x + ' ' + this.y(y) + ' ' + width + ' ' + height + ' rectstroke');
        }
    };
    JSEPS.prototype.setFillColor = function (r, g, b) { };
    JSEPS.prototype.y = function (value) { this.max = Math.max(this.max, value); this.min = Math.min(this.min, value); return this.inverting ? '%y(' + value + ')' : value; };
    JSEPS.prototype.getType = function () {
        return 'application/postscript';
    };
    JSEPS.prototype.getData = function () {
        var t, end, url, text, typ = 'application/postscript', a = document.createElement('a'), data = '', pos, i;
        for (i = 0; i < this.output.length; i += 1) {
            text = this.output[i];
            if (this.inverting) {
                while ((pos = text.indexOf('%y')) >= 0) {
                    end = text.indexOf(')', pos);
                    t = this.max - parseInt(text.substring(pos + 3, end), 10);
                    text = text.substring(0, pos) + t + text.substring(end + 1);
                }
            }
            data = data + text + '\r\n';
        }
        return data;
    };
    JSEPS.prototype.setDrawColor = function (r, g, b) { };
    JSEPS.prototype.ellipse = function (cx, cy, rx, ry, colorMode) { };
    JSEPS.prototype.circle = function (cx, cy, r, colorMode) { };
    JSEPS.prototype.setTextColor = function (r, g, b) { };
    JSEPS.prototype.text = function (x, y, text) { this.out('(' + text.replace('&lt;', '<').replace('&gt;', '>') + ') ' + x + ' ' + this.y(y) + ' F1 SMS'); };
    JSEPS.prototype.lineto = function (x, y) { this.out(x + ' ' + this.y(y) + ' lineto'); this.out('stroke'); };
    JSEPS.prototype.moveto = function (x, y) { this.out(x + ' ' + this.y(y) + ' moveto'); };
    JSEPS.prototype.line = function (x1, y1, x2, y2) { this.out('newpath ' + x1 + ' ' + this.y(y1) + ' moveto ' + x2 + ' ' + this.y(y2) + ' lineto stroke'); };
    JSEPS.prototype.setLineWidth = function (value) { this.out(value + ' setlinewidth'); };
    JSEPS.prototype.setFont = function (value) { this.out('/F' + (this.font += 1) + ' 10 /' + value + ' FSD'); };
    return JSEPS;
}());
exports.JSEPS = JSEPS;


/***/ }),

/***/ "./Palette.ts":
/*!********************!*\
  !*** ./Palette.ts ***!
  \********************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var Palette = (function () {
    function Palette(graph) {
        var _this = this;
        this.graph = graph;
        var div = document.createElement('div');
        div.className = 'palette';
        div.id = 'palette';
        this.root = div;
        this.palette = div;
        var _loop_1 = function (key) {
            var element = graph.nodeFactory[key];
            var control = new element();
            var icon = control.getToolBarIcon();
            if (icon) {
                var button = document.createElement('button');
                button.className = 'add' + key + 'Btn';
                button.innerHTML = icon.outerHTML;
                button.onclick = function (e) {
                    var nextFreePosition = _this.graph.getNextFreePosition();
                    var node = _this.graph.addElementWithValues(graph.nodeFactory[key], { x: nextFreePosition.x, y: nextFreePosition.y }, false);
                    _this.graph.drawElement(node);
                };
                this_1.palette.appendChild(button);
            }
        };
        var this_1 = this;
        for (var key in graph.nodeFactory) {
            _loop_1(key);
        }
    }
    Palette.prototype.show = function () {
        document.body.appendChild(this.root);
    };
    Palette.prototype.addButtons = function () {
    };
    return Palette;
}());
exports.default = Palette;


/***/ }),

/***/ "./PropertiesPanel.ts":
/*!****************************!*\
  !*** ./PropertiesPanel.ts ***!
  \****************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var EventBus_1 = __webpack_require__(/*! ./EventBus */ "./EventBus.ts");
var util_1 = __webpack_require__(/*! ./util */ "./util.ts");
var PanelGroup = (function () {
    function PanelGroup(graph) {
        this.graph = graph;
        this.clearPanel = new ClearPanel(this);
        this.generatePanel = new GeneratePanel(this);
    }
    PanelGroup.prototype.handle = function (event, element) {
        this.handleOpenProperties(event, element);
        if (event.type === EventBus_1.EventBus.RELOADPROPERTIES
            && this.selectedElement && element.id === this.selectedElement.id) {
            this.handleEvent(event, element);
        }
        if (this.selectedElement && this.selectedElement.id === element.id) {
            return true;
        }
        if (element.id === 'RootElement') {
            this.setActivePanel(this.clearPanel);
        }
        if (element.id === 'GenerateProp') {
            this.setActivePanel(this.generatePanel);
        }
        this.selectedElement = element;
        return true;
    };
    PanelGroup.prototype.getGraph = function () {
        return this.graph;
    };
    PanelGroup.prototype.canHandle = function () {
        return EventBus_1.EventBus.isHandlerActiveOrFree(PanelGroup.name);
    };
    PanelGroup.prototype.setActive = function (active) {
        if (active) {
            EventBus_1.EventBus.setActiveHandler(PanelGroup.name);
        }
        else {
            EventBus_1.EventBus.releaseActiveHandler();
        }
    };
    PanelGroup.prototype.handleEvent = function (event, element) {
    };
    PanelGroup.prototype.show = function () {
        var _this = this;
        this.propertiesMasterPanel = document.createElement('div');
        this.propertiesMasterPanel.className = 'propertiespanel-hidden';
        this.propertiesContent = document.createElement('div');
        this.propertiesContent.className = 'properties-hidden';
        this.propHeaderLabel = document.createElement('div');
        this.propHeaderLabel.style.display = 'inherit';
        this.propHeaderLabel.style.cursor = 'pointer';
        this.propHeaderLabel.onclick = function (e) { return _this.toogleProperties(e); };
        this.propHeaderButton = document.createElement('button');
        this.propHeaderButton.className = 'btnHideProp';
        this.propHeaderButton.style.cssFloat = 'right';
        this.propHeaderButton.onclick = function (e) { return _this.toogleProperties(e); };
        var propertiesHeader = document.createElement('div');
        propertiesHeader.style.display = 'inline';
        propertiesHeader.appendChild(this.propHeaderLabel);
        propertiesHeader.appendChild(this.propHeaderButton);
        this.propertiesMasterPanel.appendChild(propertiesHeader);
        this.propertiesMasterPanel.appendChild(this.propertiesContent);
        document.body.appendChild(this.propertiesMasterPanel);
        this.setActivePanel(this.clearPanel);
    };
    PanelGroup.prototype.setActivePanel = function (panel) {
        this.selectedPanel = panel;
        this.propHeaderLabel.innerHTML = panel.getHeaderText();
        if (this.propertiesContent) {
            while (this.propertiesContent.hasChildNodes()) {
                this.propertiesContent.removeChild(this.propertiesContent.childNodes[0]);
            }
        }
        panel.show();
        panel.showFirstTab();
        if (panel !== this.clearPanel) {
            this.showProperties(null);
        }
        else {
            this.hideProperties(null);
        }
    };
    PanelGroup.prototype.getProperiesContent = function () {
        return this.propertiesContent;
    };
    PanelGroup.prototype.handleOpenProperties = function (event, element) {
        if (event.type === 'dblclick') {
            this.showProperties(event);
        }
    };
    PanelGroup.prototype.showProperties = function (evt) {
        if (evt) {
            evt.stopPropagation();
        }
        this.propHeaderButton.innerHTML = '&#8897;';
        this.propHeaderButton.title = 'Hide properties';
        this.propertiesMasterPanel.className = 'propertiespanel';
        this.propertiesContent.className = 'properties';
    };
    PanelGroup.prototype.toogleProperties = function (evt) {
        if (this.propHeaderButton.title === 'Show properties') {
            this.showProperties(evt);
        }
        else {
            this.hideProperties(evt);
        }
    };
    PanelGroup.prototype.hideProperties = function (evt) {
        if (evt) {
            evt.stopPropagation();
        }
        this.propHeaderButton.innerHTML = '&#8896;';
        this.propHeaderButton.title = 'Show properties';
        this.propertiesMasterPanel.className = 'propertiespanel-hidden';
        this.propertiesContent.className = 'properties-hidden';
    };
    return PanelGroup;
}());
exports.PanelGroup = PanelGroup;
var Panel = (function () {
    function Panel(group, element) {
        this.panelItem = [];
        this.divPropertiesPanel = document.createElement('div');
        this.element = element;
        this.group = group;
        this.divPropertiesTabbedPanel = document.createElement('div');
        this.divPropertiesTabbedPanel.className = 'tabbedpane';
        this.divPropertiesPanel.appendChild(this.divPropertiesTabbedPanel);
    }
    Panel.prototype.show = function () {
        var propertiesContent = this.group.getProperiesContent();
        if (this.panelItem.length > 1) {
            propertiesContent.appendChild(this.getPropertiesTabbedPanel());
        }
        propertiesContent.appendChild(this.getPropertiesPanel());
    };
    Panel.prototype.getPropertiesTabbedPanel = function () {
        return this.divPropertiesTabbedPanel;
    };
    Panel.prototype.getPropertiesPanel = function () {
        return this.divPropertiesPanel;
    };
    Panel.prototype.getPanel = function () {
        return this.divPropertiesPanel;
    };
    Panel.prototype.getHeaderText = function () {
        return '';
    };
    Panel.prototype.showFirstTab = function () {
        if (this.panelItem.length > 0) {
            this.openTab(this.panelItem[0]);
        }
    };
    Panel.prototype.createTabElement = function (tabText, tabValue, item) {
        var _this = this;
        var tabElementBtn = document.createElement('button');
        tabElementBtn.className = 'tablinks';
        tabElementBtn.innerText = tabText;
        tabElementBtn.value = tabValue;
        if (item === null) {
            item = new PanelItem(this);
        }
        item.withButton(tabElementBtn);
        tabElementBtn.onclick = function () { return _this.openTab(item); };
        this.divPropertiesTabbedPanel.appendChild(tabElementBtn);
        this.panelItem.push(item);
        return item;
    };
    Panel.prototype.openTab = function (panelItem) {
        for (var key in this.panelItem) {
            var child = this.panelItem[key];
            if (child !== panelItem) {
                child.deactive();
            }
        }
        panelItem.active();
        if (this.divPropertiesPanel) {
            while (this.divPropertiesPanel.hasChildNodes()) {
                this.divPropertiesPanel.removeChild(this.divPropertiesPanel.childNodes[0]);
            }
        }
        if (panelItem.getContent()) {
            this.divPropertiesPanel.appendChild(panelItem.getContent());
        }
    };
    return Panel;
}());
exports.Panel = Panel;
var GeneratePanel = (function (_super) {
    __extends(GeneratePanel, _super);
    function GeneratePanel(group) {
        var _this = _super.call(this, group, null) || this;
        var item = _this.createTabElement('General', 'General', null);
        var inputGenerateWorkspace = util_1.Util.createHTML({ tag: 'input', id: 'workspace', type: 'text', placeholder: 'Type your Folder for generated code...', value: 'src/main/java', style: { marginRight: '5px', width: '260px' } });
        item.withInput('Folder:', inputGenerateWorkspace);
        var inputGeneratePackage = util_1.Util.createHTML({ tag: 'input', id: 'package', type: 'text', placeholder: 'Type your workspace for generated code...', value: '', style: { marginRight: '5px', width: '260px' } });
        item.withInput('Package:', inputGeneratePackage);
        var options = document.createElement('div');
        options.style.textAlign = 'center';
        options.style.margin = '3';
        options.style.padding = '5';
        item.withContent(document.createElement('br'));
        item.withContent(document.createElement('br'));
        item.withContent(options);
        options.style.borderStyle = 'groove';
        options.style.borderRadius = '10px';
        var btnGenerate = document.createElement('button');
        btnGenerate.textContent = 'Generate';
        btnGenerate.title = 'Generate code into your workspace';
        btnGenerate.className = 'OptionElement';
        var that = _this;
        btnGenerate.onclick = function () {
            var workspace = inputGeneratePackage.value;
            if (workspace.length === 0) {
                alert('No workspace set.\nEnter first your workspace');
                inputGeneratePackage.focus();
                return;
            }
            that.group.getGraph().generate(workspace, inputGenerateWorkspace.value);
        };
        options.appendChild(btnGenerate);
        options.appendChild(document.createElement('hr'));
        options.appendChild(document.createElement('br'));
        var btnAutoLayout = util_1.Util.createHTML({ tag: 'button', className: 'OptionElement', value: 'Auto Layout', style: { marginRight: '10px' }, onclick: function () {
                that.group.getGraph().layout();
            } });
        options.appendChild(btnAutoLayout);
        var btnDeleteAll = document.createElement('button');
        btnDeleteAll.className = 'OptionElement';
        btnDeleteAll.textContent = 'Delete All';
        btnDeleteAll.title = 'Delete all nodes from diagram';
        btnDeleteAll.onclick = function () {
            var confirmDelete = confirm('All classes will be deleted!');
            if (!confirmDelete) {
                return;
            }
            that.group.getGraph().$graphModel.removeAllElements();
        };
        btnDeleteAll.style.marginRight = '10px';
        options.appendChild(btnDeleteAll);
        var exportTypes = ['Export', 'HTML', 'JSON', 'PDF', 'PNG', 'SVG'];
        var selectExport = document.createElement('select');
        exportTypes.forEach(function (type) {
            if (!(!window['jsPDF'] && type === 'PDF')) {
                var option = document.createElement('option');
                option.value = type;
                option.textContent = type;
                selectExport.appendChild(option);
            }
        });
        selectExport.onchange = function (evt) {
            var selectedExportType = selectExport.options[selectExport.selectedIndex].value;
            selectExport.selectedIndex = 0;
            that.group.getGraph().saveAs(selectedExportType);
        };
        selectExport.className = 'OptionElement';
        options.appendChild(selectExport);
        options.appendChild(document.createElement('br'));
        return _this;
    }
    GeneratePanel.prototype.getHeaderText = function () {
        return 'Properties';
    };
    return GeneratePanel;
}(Panel));
exports.GeneratePanel = GeneratePanel;
var ClearPanel = (function (_super) {
    __extends(ClearPanel, _super);
    function ClearPanel(group) {
        return _super.call(this, group, null) || this;
    }
    ClearPanel.prototype.getHeaderText = function () {
        return 'Select any element to see its properties';
    };
    return ClearPanel;
}(Panel));
exports.ClearPanel = ClearPanel;
var PanelItem = (function () {
    function PanelItem(panel, label) {
        this.content = util_1.Util.create({ tag: 'div', className: 'tabContent' });
        this.panel = panel;
        this.label = label;
    }
    PanelItem.prototype.active = function () {
        if (this.getButton()) {
            this.getButton().className += ' active';
        }
    };
    PanelItem.prototype.deactive = function () {
        if (this.getButton()) {
            util_1.Util.removeClass(this.getButton(), 'active');
        }
    };
    PanelItem.prototype.withButton = function (button) {
        this.button = button;
        return this;
    };
    PanelItem.prototype.withContent = function (element) {
        this.content.appendChild(element);
        return this;
    };
    PanelItem.prototype.withInput = function (labelText, element) {
        var group = util_1.Util.createHTML({ tag: 'div' });
        var label = util_1.Util.createHTML({ tag: 'label', for: element.id, value: labelText });
        group.appendChild(label);
        group.appendChild(element);
        this.content.appendChild(group);
        return this;
    };
    PanelItem.prototype.getButton = function () {
        return this.button;
    };
    PanelItem.prototype.getContent = function () {
        return this.content;
    };
    PanelItem.prototype.getHeader = function () {
        return this.label;
    };
    return PanelItem;
}());
exports.PanelItem = PanelItem;


/***/ }),

/***/ "./PropertyBinder.ts":
/*!***************************!*\
  !*** ./PropertyBinder.ts ***!
  \***************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var PropertyBinder = (function () {
    function PropertyBinder(data1, data2, propertyClass1, propertyClass2) {
        this.applyingChange = false;
        this.data1 = data1;
        this.data2 = data2;
        this.propertyClass1 = propertyClass1;
        this.propertyClass2 = propertyClass2;
    }
    PropertyBinder.bind = function (data1, data2, property1, property2) {
        if (!data1 || !data2 || !property1) {
            console.error('NullValue!!');
            return null;
        }
        var propertyBinder = new PropertyBinder(data1, data2, property1, property2);
        propertyBinder.bind();
        return propertyBinder;
    };
    PropertyBinder.prototype.propertyChange = function (entity, property, oldValue, newValue) {
        if (!this.applyingChange) {
            this.applyingChange = true;
            if (entity === this.data1) {
                this.data2.setValue(this.propertyClass2, newValue);
            }
            else if (entity === this.data2) {
                this.data1.setValue(this.propertyClass1, newValue);
            }
            this.applyingChange = false;
        }
    };
    PropertyBinder.prototype.bind = function () {
        this.data1.setValue(this.propertyClass1, this.data2.getValue(this.propertyClass2));
        this.data1.addListener(this, this.propertyClass1);
        this.data2.addListener(this, this.propertyClass2);
    };
    PropertyBinder.prototype.unbind = function () {
        this.data1.removeListener(this, this.propertyClass2);
        this.data1.removeListener(this, this.propertyClass2);
    };
    return PropertyBinder;
}());
exports.PropertyBinder = PropertyBinder;


/***/ }),

/***/ "./RGBColor.ts":
/*!*********************!*\
  !*** ./RGBColor.ts ***!
  \*********************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var RGBColor = (function () {
    function RGBColor(value) {
        this.ok = false;
        if (value === 'none') {
            return;
        }
        var computedColor, div = document.createElement('div');
        div.style.backgroundColor = value;
        document.body.appendChild(div);
        computedColor = window.getComputedStyle(div).backgroundColor;
        document.body.removeChild(div);
        this.convert(computedColor);
    }
    RGBColor.prototype.convert = function (value) {
        var values, regex = /rgb *\( *([0-9]{1,3}) *, *([0-9]{1,3}) *, *([0-9]{1,3}) *\)/;
        values = regex.exec(value);
        this.r = parseInt(values[1], 10);
        this.g = parseInt(values[2], 10);
        this.b = parseInt(values[3], 10);
        this.ok = true;
    };
    RGBColor.prototype.toRGB = function () { return 'rgb(' + this.r + ', ' + this.g + ', ' + this.b + ')'; };
    RGBColor.prototype.toHex = function () {
        return '#' + (this.r + 0x10000).toString(16).substring(3).toUpperCase() + (this.g + 0x10000).toString(16).substring(3).toUpperCase() + (this.b + 0x10000).toString(16).substring(3).toUpperCase();
    };
    return RGBColor;
}());
exports.RGBColor = RGBColor;


/***/ }),

/***/ "./SVGConverter.ts":
/*!*************************!*\
  !*** ./SVGConverter.ts ***!
  \*************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var RGBColor_1 = __webpack_require__(/*! ./RGBColor */ "./RGBColor.ts");
var JSEPS_1 = __webpack_require__(/*! ./JSEPS */ "./JSEPS.ts");
var epsSvgAttr = {
    g: ['stroke', 'fill', 'stroke-width'],
    line: ['x1', 'y1', 'x2', 'y2', 'stroke', 'stroke-width'],
    rect: ['x', 'y', 'width', 'height', 'stroke', 'fill', 'stroke-width'],
    ellipse: ['cx', 'cy', 'rx', 'ry', 'stroke', 'fill', 'stroke-width'],
    circle: ['cx', 'cy', 'r', 'stroke', 'fill', 'stroke-width'],
    text: ['x', 'y', 'font-size', 'font-family', 'text-anchor', 'font-weight', 'font-style', 'fill'],
    path: ['']
};
var SVGConverter = (function () {
    function SVGConverter(element, target, options) {
        this.k = 1.0;
        var hasScale = typeof (options.scale), hasRemoveInvalid = typeof (options.removeInvalid);
        this.k = (options && hasScale !== 'undefined' ? options.scale : 1.0);
        this.remove = (options && hasRemoveInvalid !== 'undefined' ? options.removeInvalid : false);
        this.target = target;
        this.parse(element);
    }
    SVGConverter.prototype.parse = function (element) {
        var el, i, n, colorMode, hasFillColor, fillRGB, fillColor, strokeColor, strokeRGB, fontType, pdfFontSize, x, y, box, xOffset;
        if (!element) {
            return;
        }
        if (typeof element === 'string') {
            el = document.createElement('div');
            el.innerHTML = element;
            element = el.childNodes[0];
        }
        for (i = 0; i < element.children.length; i += 1) {
            n = element.children[i];
            colorMode = null;
            hasFillColor = false;
            if ('g,line,rect,ellipse,circle,text'.indexOf(n.tagName) >= 0) {
                fillColor = n.getAttribute('fill');
                if (fillColor) {
                    fillRGB = new RGBColor_1.RGBColor(fillColor);
                    if (fillRGB.ok) {
                        hasFillColor = true;
                        colorMode = 'F';
                    }
                }
            }
            if ('g,line,rect,ellipse,circle'.indexOf(n.tagName) >= 0) {
                if (hasFillColor) {
                    this.target.setFillColor(fillRGB.r, fillRGB.g, fillRGB.b);
                }
                strokeColor = n.getAttribute('stroke');
                if (n.hasAttribute('stroke-width')) {
                    this.target.setLineWidth(this.attr(n, 'stroke-width'));
                }
                if (strokeColor) {
                    strokeRGB = new RGBColor_1.RGBColor(strokeColor);
                    if (strokeRGB.ok) {
                        this.target.setDrawColor(strokeRGB.r, strokeRGB.g, strokeRGB.b);
                        if (colorMode === 'F') {
                            colorMode = 'FD';
                        }
                        else if (!hasFillColor) {
                            colorMode = 'S';
                        }
                    }
                    else {
                        colorMode = null;
                    }
                }
            }
            switch (n.tagName.toLowerCase()) {
                case 'svg':
                case 'a':
                case 'g':
                    this.parse(n);
                    break;
                case 'line':
                    this.target.line(this.attr(n, 'x1'), this.attr(n, 'y1'), this.attr(n, 'x2'), this.attr(n, 'y2'));
                    break;
                case 'rect':
                    this.target.rect(this.attr(n, 'x'), this.attr(n, 'y'), this.attr(n, 'width'), this.attr(n, 'height'), n.getAttribute('style'));
                    break;
                case 'ellipse':
                    this.target.ellipse(this.attr(n, 'cx'), this.attr(n, 'cy'), this.attr(n, 'rx'), this.attr(n, 'ry'), colorMode);
                    break;
                case 'circle':
                    this.target.circle(this.attr(n, 'cx'), this.attr(n, 'cy'), this.attr(n, 'r'), colorMode);
                    break;
                case 'text':
                    if (n.hasAttribute('font-family')) {
                        switch (n.getAttribute('font-family').toLowerCase()) {
                            case 'serif':
                                this.target.setFont('times');
                                break;
                            case 'monospace':
                                this.target.setFont('courier');
                                break;
                            default:
                                n.getAttribute('font-family', 'sans-serif');
                                this.target.setFont('Helvetica');
                        }
                    }
                    if (hasFillColor) {
                        this.target.setTextColor(fillRGB.r, fillRGB.g, fillRGB.b);
                    }
                    if (this.target instanceof JSEPS_1.JSEPS) {
                        this.target.text(this.attr(n, 'x'), this.attr(n, 'y'), n.innerHTML);
                        break;
                    }
                    fontType = '';
                    if (n.hasAttribute('font-weight')) {
                        if (n.getAttribute('font-weight') === 'bold') {
                            fontType = 'bold';
                        }
                    }
                    if (n.hasAttribute('font-style')) {
                        if (n.getAttribute('font-style') === 'italic') {
                            fontType += 'italic';
                        }
                    }
                    this.target.setFontType(fontType);
                    pdfFontSize = 16;
                    if (n.hasAttribute('font-size')) {
                        pdfFontSize = parseInt(n.getAttribute('font-size'), 10);
                    }
                    box = n.getBBox();
                    x = this.attr(n, 'x');
                    y = this.attr(n, 'y');
                    xOffset = 0;
                    if (n.hasAttribute('text-anchor')) {
                        switch (n.getAttribute('text-anchor')) {
                            case 'end':
                                xOffset = box.width;
                                break;
                            case 'middle':
                                xOffset = box.width / 2;
                                break;
                            case 'start':
                                break;
                            case 'default':
                                n.getAttribute('text-anchor', 'start');
                                break;
                        }
                        x = x - (xOffset * this.k);
                    }
                    this.target.setFontSize(pdfFontSize).text(x, y, n.innerHTML);
                    break;
                default:
                    if (this.remove) {
                        console.log('cant translate to target:', n);
                        element.removeChild(n);
                        i -= 1;
                    }
            }
        }
    };
    SVGConverter.prototype.attr = function (node, name) {
        return this.k * parseInt(node.getAttribute(name), 10);
    };
    return SVGConverter;
}());
exports.SVGConverter = SVGConverter;


/***/ }),

/***/ "./Toolbar.ts":
/*!********************!*\
  !*** ./Toolbar.ts ***!
  \********************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var Symbol_1 = __webpack_require__(/*! ./elements/nodes/Symbol */ "./elements/nodes/Symbol.ts");
var EventBus_1 = __webpack_require__(/*! ./EventBus */ "./EventBus.ts");
var Toolbar = (function () {
    function Toolbar(graph) {
        this.graph = graph;
    }
    Toolbar.prototype.show = function () {
        if (this.mainDiv) {
            return;
        }
        this.mainDiv = document.createElement('div');
        this.mainDiv.className = 'toolbar';
        var h1Logo = document.createElement('h1');
        h1Logo.className = 'logo';
        h1Logo.textContent = 'DiagramJS';
        var node = { type: 'Hamburger', property: 'HTML', width: 24, height: 24, id: 'GenerateProp' };
        var hamburger = Symbol_1.SymbolLibary.draw(node);
        EventBus_1.EventBus.registerEvent(hamburger, 'click', node);
        this.mainDiv.appendChild(hamburger);
        this.mainDiv.appendChild(h1Logo);
        document.body.appendChild(this.mainDiv);
    };
    return Toolbar;
}());
exports.Toolbar = Toolbar;


/***/ }),

/***/ "./UML.ts":
/*!****************!*\
  !*** ./UML.ts ***!
  \****************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Data_1 = __webpack_require__(/*! ./Data */ "./Data.ts");
var UML;
(function (UML) {
    var Clazz = (function (_super) {
        __extends(Clazz, _super);
        function Clazz() {
            var _this = _super.call(this) || this;
            _this.property = 'Clazz';
            return _this;
        }
        Clazz.prototype.getName = function () {
            return this.prop[Clazz.NAME];
        };
        Clazz.prototype.setName = function (newValue) {
            this.setValue(Clazz.NAME, newValue);
        };
        Clazz.prototype.getAttributes = function () {
            return this.prop[Clazz.ATTRIBUTES];
        };
        Clazz.prototype.addToAttributes = function (newValue) {
            this.addTo(Clazz.ATTRIBUTES, newValue);
        };
        Clazz.prototype.removeFromAttributes = function (newValue) {
            this.removeFrom(Clazz.ATTRIBUTES, newValue);
        };
        Clazz.NAME = 'name';
        Clazz.ATTRIBUTES = 'attributes';
        Clazz.METHODS = 'methods';
        return Clazz;
    }(Data_1.default));
    UML.Clazz = Clazz;
    var Attribute = (function (_super) {
        __extends(Attribute, _super);
        function Attribute() {
            var _this = _super.call(this) || this;
            _this.property = 'Attribute';
            return _this;
        }
        Attribute.prototype.getName = function () {
            return this.prop[Clazz.NAME];
        };
        Attribute.prototype.setName = function (newValue) {
            this.setValue(Clazz.NAME, newValue);
        };
        return Attribute;
    }(Data_1.default));
    UML.Attribute = Attribute;
    var Methods = (function (_super) {
        __extends(Methods, _super);
        function Methods() {
            return _super !== null && _super.apply(this, arguments) || this;
        }
        return Methods;
    }(Data_1.default));
    UML.Methods = Methods;
})(UML = exports.UML || (exports.UML = {}));
window['UML'] = UML;


/***/ }),

/***/ "./adapters/JavaAdapter.ts":
/*!*********************************!*\
  !*** ./adapters/JavaAdapter.ts ***!
  \*********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Adapter_1 = __webpack_require__(/*! ../Adapter */ "./Adapter.ts");
var JavaAdapter = (function (_super) {
    __extends(JavaAdapter, _super);
    function JavaAdapter() {
        var _this = _super.call(this) || this;
        _this.id = 'JavaAdapter';
        return _this;
    }
    JavaAdapter.prototype.update = function (evt) {
        if (this.isActive()) {
            window['JavaBridge'].executeChange(evt);
            return true;
        }
        return false;
    };
    JavaAdapter.prototype.isActive = function () {
        return window['JavaBridge'];
    };
    return JavaAdapter;
}(Adapter_1.Adapter));
exports.JavaAdapter = JavaAdapter;


/***/ }),

/***/ "./adapters/index.ts":
/*!***************************!*\
  !*** ./adapters/index.ts ***!
  \***************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

function __export(m) {
    for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
}
Object.defineProperty(exports, "__esModule", { value: true });
__export(__webpack_require__(/*! ./JavaAdapter */ "./adapters/JavaAdapter.ts"));


/***/ }),

/***/ "./elements/BaseElements.ts":
/*!**********************************!*\
  !*** ./elements/BaseElements.ts ***!
  \**********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var util_1 = __webpack_require__(/*! ../util */ "./util.ts");
var Control_1 = __webpack_require__(/*! ../Control */ "./Control.ts");
var DiagramElement = (function (_super) {
    __extends(DiagramElement, _super);
    function DiagramElement() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.$isDraggable = true;
        _this.$labelHeight = 25;
        _this.$labelFontSize = 14;
        _this.$pos = new Point();
        _this.$size = new Point();
        return _this;
    }
    DiagramElement.prototype.getToolBarIcon = function () {
        return null;
    };
    DiagramElement.prototype.getPos = function () {
        return this.$pos;
    };
    DiagramElement.prototype.getSize = function () {
        return this.$size;
    };
    DiagramElement.prototype.getCenter = function () {
        var pos = this.getPos();
        var size = this.getSize();
        return new Point(pos.x + size.x / 2, pos.y + size.y / 2);
    };
    DiagramElement.prototype.getCenterPosition = function (p) {
        var pos = this.getPos();
        var size = this.getSize();
        var offset = this['$' + p];
        var center = this.getCenter();
        if (p === Point.DOWN) {
            return new Point(Math.min(center.x + offset, center.x), pos.y + size.y, Point.DOWN);
        }
        if (p === Point.UP) {
            return new Point(Math.min(center.x + offset, center.x), pos.y, Point.UP);
        }
        if (p === Point.LEFT) {
            return new Point(pos.x, Math.min(center.y + offset, pos.y + size.y), Point.LEFT);
        }
        if (p === Point.RIGHT) {
            return new Point(pos.x + size.x, Math.min(center.y + offset, pos.y + size.y), Point.RIGHT);
        }
        return new Point();
    };
    DiagramElement.prototype.getSVG = function () {
        return null;
    };
    DiagramElement.prototype.getCanvas = function () {
        return null;
    };
    DiagramElement.prototype.getEvents = function () {
        return null;
    };
    DiagramElement.prototype.getAlreadyDisplayingSVG = function () {
        return document.getElementById(this.id) || this.getSVG();
    };
    DiagramElement.prototype.load = function (data) {
    };
    DiagramElement.prototype.withPos = function (x, y) {
        if (x && y) {
            this.$pos = new Point(x, y);
        }
        else {
            if (typeof (x) !== 'undefined' && x !== null) {
                this.$pos.x = x;
            }
            if (typeof (y) !== 'undefined' && y !== null) {
                this.$pos.y = y;
            }
        }
        return this;
    };
    DiagramElement.prototype.withSize = function (width, height) {
        if (width && height) {
            this.$size = new Point(width, height);
        }
        else {
            if (typeof (width) !== 'undefined' && width !== null) {
                this.$size.x = width;
            }
            if (typeof (height) !== 'undefined' && height !== null) {
                this.$size.y = height;
            }
        }
        return this;
    };
    DiagramElement.prototype.getShowed = function () {
        return _super.prototype.getShowed.call(this);
    };
    DiagramElement.prototype.loadProperties = function (properties) {
    };
    DiagramElement.prototype.createShape = function (attrs) {
        return util_1.Util.createShape(attrs);
    };
    return DiagramElement;
}(Control_1.Control));
exports.DiagramElement = DiagramElement;
var Point = (function () {
    function Point(x, y, pos) {
        this.x = 0;
        this.y = 0;
        this.x = Math.ceil(x || 0);
        this.y = Math.ceil(y || 0);
        if (pos) {
            this['pos'] = pos;
        }
    }
    Point.prototype.add = function (pos) {
        this.x += pos.x;
        this.y += pos.y;
        return this;
    };
    Point.prototype.getPosition = function () {
        if (!this['pos']) {
            return '';
        }
        return this['pos'];
    };
    Point.prototype.addNum = function (x, y) {
        this.x += x;
        this.y += y;
        return this;
    };
    Point.prototype.sum = function (pos) {
        var sum = new Point(this.x, this.y);
        sum.add(pos);
        return sum;
    };
    Point.prototype.center = function (posA, posB) {
        var count = 0;
        if (posA) {
            this.x += posA.x;
            this.y += posA.y;
            count++;
        }
        if (posB) {
            this.x += posB.x;
            this.y += posB.y;
            count++;
        }
        if (count > 0) {
            this.x = (this.x / count);
            this.y = (this.y / count);
        }
    };
    Point.prototype.isEmpty = function () {
        return this.x < 1 && this.y < 1;
    };
    Point.prototype.size = function (posA, posB) {
        var x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        if (posA) {
            x1 = posA.x;
            y1 = posA.y;
        }
        if (posB) {
            x2 = posB.x;
            y2 = posB.y;
        }
        if (x1 > x2) {
            this.x = x1 - x2;
        }
        else {
            this.x = x2 - x1;
        }
        if (y1 > y2) {
            this.y = y1 - y2;
        }
        else {
            this.y = y2 - y1;
        }
    };
    Point.UP = 'UP';
    Point.LEFT = 'LEFT';
    Point.RIGHT = 'RIGHT';
    Point.DOWN = 'DOWN';
    return Point;
}());
exports.Point = Point;
var Line = (function (_super) {
    __extends(Line, _super);
    function Line(lineType) {
        var _this = _super.call(this) || this;
        _this.lineType = lineType;
        return _this;
    }
    Line.prototype.getTyp = function () {
        return 'SVG';
    };
    Line.prototype.getPos = function () {
        var pos = new Point();
        pos.center(this.source, this.target);
        return pos;
    };
    Line.prototype.getSize = function () {
        var pos = new Point();
        pos.size(this.source, this.target);
        return pos;
    };
    Line.prototype.withColor = function (color) {
        this.color = color;
        return this;
    };
    Line.prototype.withSize = function (x, y) {
        return this;
    };
    Line.prototype.withPath = function (path, close, angle) {
        var i, d = 'M' + path[0].x + ' ' + path[0].y;
        this.lineType = Line.FORMAT.PATH;
        for (i = 1; i < path.length; i += 1) {
            d = d + 'L ' + path[i].x + ' ' + path[i].y;
        }
        if (close) {
            d = d + ' Z';
            this.target = path[0];
        }
        else {
            this.target = path[path.length - 1];
        }
        this.path = d;
        if (angle instanceof Number) {
            this.angle = angle;
        }
        else if (angle) {
        }
        return this;
    };
    Line.prototype.getSVG = function () {
        if (this.lineType === 'PATH') {
            return util_1.Util.create({
                tag: 'path',
                'd': this.path,
                'fill': this.color,
                stroke: '#000',
                'stroke-width': '1px'
            });
        }
        var line = util_1.Util.create({
            tag: 'line',
            'x1': this.source.x,
            'y1': this.source.y,
            'x2': this.target.x,
            'y2': this.target.y,
            'stroke': util_1.Util.getColor(this.color)
        });
        if (this.lineType && this.lineType.toLowerCase() === 'dotted') {
            line.setAttribute('stroke-miterlimit', '4');
            line.setAttribute('stroke-dasharray', '1,1');
        }
        return line;
    };
    Line.FORMAT = { SOLID: 'SOLID', DOTTED: 'DOTTED', PATH: 'PATH' };
    return Line;
}(DiagramElement));
exports.Line = Line;


/***/ }),

/***/ "./elements/ClassEditor.ts":
/*!*********************************!*\
  !*** ./elements/ClassEditor.ts ***!
  \*********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Graph_1 = __webpack_require__(/*! ./Graph */ "./elements/Graph.ts");
var ClassEditor = (function (_super) {
    __extends(ClassEditor, _super);
    function ClassEditor(json, options) {
        var _this = this;
        if (!options) {
            options = {};
        }
        options.canvas = options.canvas || 'canvas';
        options.autoSave = options.autoSave || true;
        if (!options.features) {
            options.features = {
                drag: true,
                editor: true,
                palette: true,
                select: true,
                zoom: true,
                toolbar: true,
                import: true,
                properties: true,
                addnode: true,
                newedge: true
            };
        }
        _this = _super.call(this, json, options) || this;
        return _this;
    }
    ClassEditor.prototype.setBoardStyle = function (value) {
        console.log(value);
        this.importFile.setBoardStyle(value);
    };
    return ClassEditor;
}(Graph_1.Graph));
exports.ClassEditor = ClassEditor;


/***/ }),

/***/ "./elements/Graph.ts":
/*!***************************!*\
  !*** ./elements/Graph.ts ***!
  \***************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var edges = __webpack_require__(/*! ./edges */ "./elements/edges/index.ts");
var edges_1 = __webpack_require__(/*! ./edges */ "./elements/edges/index.ts");
var nodes = __webpack_require__(/*! ./nodes */ "./elements/nodes/index.ts");
var layouts = __webpack_require__(/*! ../layouts */ "./layouts/index.ts");
var Model_1 = __webpack_require__(/*! ./Model */ "./elements/Model.ts");
var BaseElements_1 = __webpack_require__(/*! ./BaseElements */ "./elements/BaseElements.ts");
var util_1 = __webpack_require__(/*! ../util */ "./util.ts");
var Control_1 = __webpack_require__(/*! ../Control */ "./Control.ts");
var EventBus_1 = __webpack_require__(/*! ../EventBus */ "./EventBus.ts");
var handlers_1 = __webpack_require__(/*! ../handlers */ "./handlers/index.ts");
var ImportFile_1 = __webpack_require__(/*! ../handlers/ImportFile */ "./handlers/ImportFile.ts");
var Toolbar_1 = __webpack_require__(/*! ../Toolbar */ "./Toolbar.ts");
var JSEPS_1 = __webpack_require__(/*! ../JSEPS */ "./JSEPS.ts");
var SVGConverter_1 = __webpack_require__(/*! ../SVGConverter */ "./SVGConverter.ts");
var Palette_1 = __webpack_require__(/*! ../Palette */ "./Palette.ts");
var PropertiesPanel_1 = __webpack_require__(/*! ../PropertiesPanel */ "./PropertiesPanel.ts");
var Graph = (function (_super) {
    __extends(Graph, _super);
    function Graph(json, options) {
        var _this = _super.call(this) || this;
        _this.containerElements = ['svg', 'g'];
        _this.relevantStyles = {
            'rect': ['fill', 'stroke', 'stroke-width'],
            'path': ['fill', 'stroke', 'stroke-width', 'opacity'],
            'circle': ['fill', 'stroke', 'stroke-width'],
            'line': ['stroke', 'stroke-width'],
            'text': ['fill', 'font-size', 'text-anchor', 'font-family'],
            'polygon': ['stroke', 'fill']
        };
        json = json || {};
        if (json['data']) {
            options = json['options'];
            json = json['data'];
            _this.id = json['id'];
        }
        _this.options = options || { features: { drag: true } };
        if (json['init']) {
            return _this;
        }
        if (!_this.options.origin) {
            _this.options.origin = new BaseElements_1.Point(150, 45);
        }
        if (!_this.options.style) {
            _this.options.style = 'classic';
        }
        if (_this.options.autoSave) {
            util_1.Util.isAutoSave = options.autoSave;
        }
        _this.initFactories();
        _this.initCanvas();
        _this.initFeatures(_this.options.features);
        if (!_this.lookupInLocalStorage()) {
            _this.load(json);
        }
        EventBus_1.EventBus.register(_this, _this.$view);
        return _this;
    }
    Graph.prototype.lookupInLocalStorage = function () {
        if (!this.options.autoSave) {
            return false;
        }
        if (!util_1.Util.isLocalStorageSupported()) {
            return false;
        }
        var diagram = util_1.Util.getDiagramFromLocalStorage();
        if (diagram && diagram.length > 0) {
            if (confirm('Restore previous session?')) {
                var jsonData = JSON.parse(diagram);
                this.load(jsonData);
                this.layout();
                return true;
            }
            else {
                util_1.Util.saveToLocalStorage(null);
            }
        }
        return false;
    };
    Graph.prototype.fitSizeOnNodes = function () {
        var maxWidth = 0;
        var maxHeight = 0;
        for (var _i = 0, _a = this.$graphModel.nodes; _i < _a.length; _i++) {
            var node = _a[_i];
            var nodePos = node.getPos();
            var nodeSize = node.getSize();
            var nodeWidestPosX = nodePos.x + nodeSize.x;
            var nodeWidestPosY = nodePos.y + nodeSize.y;
            if (nodeWidestPosX > maxWidth) {
                maxWidth = nodeWidestPosX;
            }
            if (nodeWidestPosY > maxHeight) {
                maxHeight = nodeWidestPosY;
            }
        }
        this.root.setAttributeNS(null, 'width', '' + (maxWidth + 100));
        this.root.setAttributeNS(null, 'height', '' + (maxHeight + 50));
    };
    Graph.prototype.saveAs = function (typ) {
        typ = typ.toLowerCase();
        var currentSize = this.getRootSize();
        this.fitSizeOnNodes();
        if (typ === 'svg') {
            this.exportSvg();
        }
        else if (typ === 'png') {
            this.exportPng();
        }
        else if (typ === 'html') {
            this.exportHtml();
        }
        else if (typ === 'pdf') {
            this.exportPdf();
        }
        else if (typ === 'json') {
            this.exportJson();
        }
        this.root.setAttributeNS(null, 'width', '' + currentSize.width);
        this.root.setAttributeNS(null, 'height', '' + currentSize.height);
    };
    Graph.prototype.save = function (type, data, name, context) {
        if (window['java']) {
            window['java'].export(type, data, name, context);
            return;
        }
        var a = document.createElement('a');
        a.href = window.URL.createObjectURL(new Blob([data], { type: context }));
        a.download = name;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    };
    Graph.prototype.exportSvg = function () {
        var wellFormatedSvgDom = this.getSvgWithStyleAttributes();
        this.save('svg', this.serializeXmlNode(wellFormatedSvgDom), 'class_diagram.svg', 'image/svg+xml');
    };
    Graph.prototype.exportHtml = function () {
        var htmlFacade = '<html><head><title>DiagramJS - Classdiagram</title></head><body>$content</body></html>';
        var wellFormatedSvgDom = this.getSvgWithStyleAttributes();
        var svgAsXml = this.serializeXmlNode(wellFormatedSvgDom);
        var htmlResult = htmlFacade.replace('$content', svgAsXml);
        this.save('html', htmlResult, 'class_diagram.htm', 'text/plain');
    };
    Graph.prototype.exportJson = function () {
        var type = 'text/plain';
        var jsonObj = util_1.Util.toJson(this.$graphModel);
        var data = JSON.stringify(jsonObj, null, '\t');
        this.save('json', data, 'class_diagram.json', type);
    };
    Graph.prototype.exportPdf = function () {
        if (!window['jsPDF']) {
            console.log('jspdf n.a.');
            return;
        }
        var type = 'image/svg+xml';
        var converter, pdf = new window['jsPDF']('l', 'px', [this.$graphModel.getSize().x, this.$graphModel.getSize().y]);
        converter = new SVGConverter_1.SVGConverter(this.$view, pdf, { removeInvalid: false });
        pdf.save('Download.pdf');
    };
    Graph.prototype.import = function (data) {
        var rootElement = this.$graphModel.$view;
        while (rootElement.hasChildNodes()) {
            rootElement.removeChild(rootElement.firstChild);
        }
        while (this.$view.hasChildNodes()) {
            this.$view.removeChild(this.$view.firstChild);
        }
        this.clearModel();
        var jsonData = JSON.parse(data);
        this.load(jsonData);
        this.layout();
    };
    Graph.prototype.exportEPS = function () {
        var converter, doc = new JSEPS_1.JSEPS({ inverting: true });
        converter = new SVGConverter_1.SVGConverter(this.$view, doc, { removeInvalid: false });
        this.save('eps', doc.getData(), 'diagram.eps', doc.getType());
    };
    Graph.prototype.exportPng = function () {
        var canvas, context, a, image = new Image();
        var xmlNode = this.serializeXmlNode(this.getSvgWithStyleAttributes());
        var typ = 'image/svg+xml';
        var url = window.URL.createObjectURL(new Blob([xmlNode], { type: typ }));
        var size = this.getRootSize();
        image.onload = function () {
            canvas = document.createElement('canvas');
            canvas.width = size.width;
            canvas.height = size.height;
            context = canvas.getContext('2d');
            context.drawImage(image, 0, 0);
            a = document.createElement('a');
            a.download = 'class_diagram.png';
            a.href = canvas.toDataURL('image/png');
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
        };
        image.src = url;
    };
    Graph.prototype.getSvgWithStyleAttributes = function () {
        var oDOM = this.$graphModel.$view.cloneNode(true);
        this.readElement(oDOM, this.$graphModel.$view);
        return oDOM;
    };
    Graph.prototype.serializeXmlNode = function (xmlNode) {
        if (window['XMLSerializer'] !== undefined) {
            return (new window['XMLSerializer']()).serializeToString(xmlNode);
        }
        if (xmlNode.xml !== undefined) {
            return xmlNode.xml;
        }
        return xmlNode.outerHTML;
    };
    Graph.prototype.getRootSize = function () {
        var width;
        var height;
        width = +this.root.getAttribute('width');
        height = +this.root.getAttribute('height');
        return { width: width, height: height };
    };
    Graph.prototype.load = function (json, owner) {
        this.$graphModel = new Model_1.GraphModel();
        this.$graphModel.init(this);
        this.$graphModel.load(json);
    };
    Graph.prototype.clearModel = function () {
        this.$graphModel.removeAllElements();
        this.clearSvgRoot();
    };
    Graph.prototype.init = function (owner, property, id) {
        _super.prototype.init.call(this, owner, property, id);
        this.layout();
        return this;
    };
    Graph.prototype.propertyChange = function (entity, property, oldValue, newValue) {
        return;
    };
    Graph.prototype.getNextFreePosition = function () {
        if (!this.$graphModel) {
            return new BaseElements_1.Point(50, 50);
        }
        var point = new BaseElements_1.Point(0, 50);
        var maxX = 0;
        var minX = 9000;
        for (var _i = 0, _a = this.$graphModel.nodes; _i < _a.length; _i++) {
            var node = _a[_i];
            maxX = Math.max(maxX, node.getPos().x);
            minX = Math.min(minX, node.getPos().x);
        }
        if (minX > 170) {
            point.x = 10;
        }
        else {
            point.x = maxX + 200;
        }
        return point;
    };
    Graph.prototype.addElement = function (type, dontDraw) {
        var success = this.$graphModel.addElement(type);
        if (success === true) {
            this.layout(dontDraw);
        }
        return success;
    };
    Graph.prototype.addElementWithValues = function (type, optionalValues, layout, dontDraw) {
        var element = this.$graphModel.addElementWithValues(type, optionalValues);
        if (element && layout) {
            this.layout(dontDraw);
        }
        return element;
    };
    Graph.prototype.layout = function (dontDraw) {
        this.getLayout().layout(this, this.$graphModel);
        if (dontDraw) {
            return;
        }
        this.draw();
    };
    Graph.prototype.getEvents = function () {
        return [EventBus_1.EventBus.ELEMENTDRAGOVER, EventBus_1.EventBus.ELEMENTDRAGLEAVE, EventBus_1.EventBus.ELEMENTDROP];
    };
    Graph.prototype.draw = function () {
        this.clearSvgRoot();
        var model = this.$graphModel;
        var root = this.root;
        var max = new BaseElements_1.Point();
        if (this.options) {
            max.x = this.options.minWidth || 0;
            max.y = this.options.minHeight || 0;
        }
        for (var _i = 0, _a = model.nodes; _i < _a.length; _i++) {
            var node = _a[_i];
            var svg = node.getSVG();
            EventBus_1.EventBus.register(node, svg);
            root.appendChild(svg);
            var temp = void 0;
            temp = node.getPos().x + node.getSize().x;
            if (temp > max.x) {
                max.x = temp;
            }
            temp = node.getPos().y + node.getSize().y;
            if (temp > max.y) {
                max.y = temp;
            }
        }
        util_1.Util.setAttributeSize(this.root, max.x + 60, max.y + 40);
        for (var _b = 0, _c = model.edges; _b < _c.length; _b++) {
            var edge = _c[_b];
            var svg = edge.getSVG();
            EventBus_1.EventBus.register(edge, svg);
            root.appendChild(svg);
        }
    };
    Graph.prototype.drawElement = function (element) {
        if (!element) {
            return;
        }
        var svg = element.getSVG();
        this.root.appendChild(svg);
        var rootSize = this.getRootSize();
        var newWidth = element.getPos().x + element.getSize().x + 40;
        var newHeight = element.getPos().y + element.getSize().y;
        if (rootSize.width < newWidth) {
            this.root.setAttributeNS(null, 'width', '' + newWidth);
        }
        if (rootSize.height < newHeight) {
            this.root.setAttributeNS(null, 'height', '' + newHeight);
        }
        if (element instanceof edges_1.Association) {
            var edge = element;
            edge.redraw(edge.$sNode);
            var srcSvg = element.$sNode.getAlreadyDisplayingSVG();
            var targetSvg = element.$tNode.getAlreadyDisplayingSVG();
            this.root.appendChild(srcSvg);
            this.root.appendChild(targetSvg);
        }
        EventBus_1.EventBus.register(element, svg);
    };
    Graph.prototype.removeElement = function (element) {
        if (!element) {
            return;
        }
        var alreadyDisplayingSvg = element.getAlreadyDisplayingSVG();
        if (util_1.Util.isParentOfChild(this.root, alreadyDisplayingSvg)) {
            this.root.removeChild(alreadyDisplayingSvg);
        }
    };
    Graph.prototype.generate = function (packageName, path) {
        this.$graphModel.package = packageName;
        this.$graphModel.genPath = path;
        var data, result = util_1.Util.toJson(this.$graphModel);
        data = JSON.stringify(result, null, '\t');
        if (window['java'] && typeof window['java'].generate === 'function') {
            window['java'].generate(data);
        }
    };
    Graph.prototype.readElement = function (parent, origData) {
        var children = parent.childNodes;
        var origChildDat = origData.childNodes;
        for (var cd = 0; cd < children.length; cd++) {
            var child = children[cd];
            var tagName = child.tagName;
            if (this.containerElements.indexOf(tagName) !== -1) {
                this.readElement(child, origChildDat[cd]);
            }
            else if (tagName in this.relevantStyles) {
                var styleDef = window.getComputedStyle(origChildDat[cd]);
                var styleString = '';
                for (var st = 0; st < this.relevantStyles[tagName].length; st++) {
                    styleString += this.relevantStyles[tagName][st] + ':' + styleDef.getPropertyValue(this.relevantStyles[tagName][st]) + '; ';
                }
                child.setAttribute('style', styleString);
            }
        }
    };
    Graph.prototype.createPattern = function () {
        var defs = util_1.Util.createShape({ tag: 'defs' });
        var pattern = util_1.Util.createShape({
            tag: 'pattern',
            id: 'raster',
            patternUnits: 'userSpaceOnUse',
            width: 40,
            height: 40
        });
        var path = 'M0 4 L0 0 L4 0 M36 0 L40 0 L40 4 M40 36 L40 40 L36 40 M4 40 L0 40 L0 36';
        var cross = util_1.Util.createShape({
            tag: 'path',
            d: path,
            stroke: '#DDD',
            'stroke-width': 1,
            fill: 'none'
        });
        var rect = util_1.Util.createShape({
            tag: 'rect',
            x: 0,
            y: 0,
            width: 40,
            height: 40,
            fill: 'none'
        });
        pattern.appendChild(rect);
        pattern.appendChild(cross);
        defs.appendChild(pattern);
        return defs;
    };
    Graph.prototype.clearSvgRoot = function () {
        var root = this.root;
        this.$graphModel.$view.dispatchEvent(util_1.Util.createCustomEvent('click'));
        while (root.firstChild) {
            root.removeChild(root.firstChild);
        }
        root.appendChild(this.createPattern());
        var fillValue = 'none';
        if (this.options.raster) {
            fillValue = 'url(#raster)';
        }
        var background = util_1.Util.createShape({
            tag: 'rect',
            id: 'background',
            width: 5000,
            height: 5000,
            x: 0,
            y: 0,
            stroke: '#999',
            'stroke-width': '1',
            fill: fillValue
        });
        root.appendChild(background);
        var inlineEdit = document.getElementById('inlineEdit');
        if (inlineEdit && document.body.contains(inlineEdit)) {
            document.body.removeChild(inlineEdit);
        }
    };
    Graph.prototype.getLayout = function () {
        if (this.currentlayout) {
            return this.currentlayout;
        }
        var layout = this.options.layout || '';
        if (this.layoutFactory[layout]) {
            this.currentlayout = new this.layoutFactory[layout]();
        }
        else {
            this.currentlayout = new layouts.DagreLayout();
        }
        return this.currentlayout;
    };
    Graph.prototype.initFactories = function () {
        var noder = nodes;
        this.nodeFactory = {};
        for (var id in noder) {
            if (noder.hasOwnProperty(id) === true) {
                this.nodeFactory[id] = noder[id];
            }
        }
        var edger = edges;
        this.edgeFactory = {};
        for (var id in edger) {
            if (edger.hasOwnProperty(id) === true) {
                this.edgeFactory[id] = edger[id];
            }
        }
        var layouter = layouts;
        this.layoutFactory = {};
        for (var id in layouter) {
            if (layouter.hasOwnProperty(id) === true) {
                this.layoutFactory[id] = layouter[id];
            }
        }
    };
    Graph.prototype.initCanvas = function () {
        if (this.options.canvas) {
            this.$view = document.getElementById(this.options.canvas);
        }
        if (!this.$view) {
            this.$view = document.createElement('div');
            this.$view.setAttribute('class', 'diagram');
            document.body.appendChild(this.$view);
        }
    };
    Graph.prototype.initFeatures = function (features) {
        if (features) {
            if (features.newedge) {
                EventBus_1.EventBus.subscribe(new handlers_1.NewEdge(this), 'mousedown', 'mouseup', 'mousemove', 'mouseleave');
            }
            this.importFile = new ImportFile_1.ImportFile(this);
            if (features.import) {
                EventBus_1.EventBus.subscribe(this.importFile, 'dragover', 'dragleave', 'drop');
            }
            if (features.zoom) {
                var mousewheel = 'onwheel' in document.createElement('div') ? 'wheel' : document.onmousewheel !== undefined ? 'mousewheel' : 'DOMMouseScroll';
                EventBus_1.EventBus.subscribe(new handlers_1.Zoom(this), mousewheel);
            }
            if (features.drag) {
                EventBus_1.EventBus.subscribe(new handlers_1.Drag(this), 'mousedown', 'mouseup', 'mousemove', 'mouseleave');
            }
            if (features.select) {
                EventBus_1.EventBus.subscribe(new handlers_1.Select(this), 'click', 'drag');
            }
            if (features.palette) {
                new Palette_1.default(this).show();
            }
            if (features.toolbar) {
                new Toolbar_1.Toolbar(this).show();
            }
            if (features.properties) {
                var propertyPanel = new PropertiesPanel_1.PanelGroup(this);
                EventBus_1.EventBus.subscribe(propertyPanel, 'dblclick', 'click', EventBus_1.EventBus.RELOADPROPERTIES);
                propertyPanel.show();
            }
            if (features.addnode) {
                EventBus_1.EventBus.subscribe(new handlers_1.AddNode(this), 'mousedown', 'mouseup', 'mousemove', 'mouseleave');
            }
        }
    };
    return Graph;
}(Control_1.Control));
exports.Graph = Graph;


/***/ }),

/***/ "./elements/Model.ts":
/*!***************************!*\
  !*** ./elements/Model.ts ***!
  \***************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var BaseElements_1 = __webpack_require__(/*! ./BaseElements */ "./elements/BaseElements.ts");
var edges_1 = __webpack_require__(/*! ./edges */ "./elements/edges/index.ts");
var nodes_1 = __webpack_require__(/*! ./nodes */ "./elements/nodes/index.ts");
var util_1 = __webpack_require__(/*! ../util */ "./util.ts");
var EventBus_1 = __webpack_require__(/*! ../EventBus */ "./EventBus.ts");
var GraphModel = (function (_super) {
    __extends(GraphModel, _super);
    function GraphModel() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.nodes = [];
        _this.edges = [];
        return _this;
    }
    GraphModel.prototype.load = function (data) {
        this.$isLoading = true;
        data = data || {};
        this.property = data.type || data.property || 'classdiagram';
        this.id = 'RootElement';
        if (data.nodes) {
            for (var _i = 0, _a = data.nodes; _i < _a.length; _i++) {
                var node = _a[_i];
                this.addNode(node);
            }
        }
        if (data.edges) {
            for (var _b = 0, _c = data.edges; _b < _c.length; _b++) {
                var edge = _c[_b];
                this.addEdge(edge);
            }
        }
        this.$isLoading = false;
    };
    GraphModel.prototype.getNodeByPosition = function (x, y) {
        for (var _i = 0, _a = this.nodes; _i < _a.length; _i++) {
            var node = _a[_i];
            var posOfNode = node.getPos();
            var sizeOfNode = node.getSize();
            if ((posOfNode.x <= x && (posOfNode.x + sizeOfNode.x) >= x)
                && (posOfNode.y <= y && (posOfNode.y + sizeOfNode.y) >= y)) {
                return node;
            }
        }
        return null;
    };
    GraphModel.prototype.init = function (owner, property, id) {
        _super.prototype.init.call(this, owner, property, id);
        this.initCanvas();
        return this;
    };
    GraphModel.prototype.addElement = function (type) {
        type = util_1.Util.toPascalCase(type);
        var id = this.getNewId(type);
        var element = this.createElement(type, id, {});
        if (element) {
            util_1.Util.saveToLocalStorage(this);
            return true;
        }
        return false;
    };
    GraphModel.prototype.addElementWithValues = function (type, optionalValues) {
        type = util_1.Util.toPascalCase(type);
        var id = this.getNewId(type);
        var element = this.createElement(type, id, {});
        if (optionalValues) {
            if (optionalValues.hasOwnProperty('x') && optionalValues.hasOwnProperty('y')) {
                var x = optionalValues['x'];
                var y = optionalValues['y'];
                element.withPos(x, y);
            }
        }
        util_1.Util.saveToLocalStorage(this);
        return element;
    };
    GraphModel.prototype.removeAllElements = function () {
        var nodesLength = this.nodes.length;
        for (var i = 0; i < nodesLength; i++) {
            this.removeElement(this.nodes[0].id);
        }
        this.$view.dispatchEvent(util_1.Util.createCustomEvent('click'));
    };
    GraphModel.prototype.removeElement = function (id) {
        var element = this.getDiagramElementById(id);
        if (!element) {
            return false;
        }
        this.$owner.removeElement(element);
        if (element instanceof nodes_1.Node) {
            var idxOfNode = this.nodes.indexOf(element);
            if (idxOfNode > -1) {
                this.nodes.splice(idxOfNode, 1);
            }
            while (element.$edges.length > 0) {
                this.removeElement(element.$edges[0].id);
            }
            element.$edges = [];
        }
        else if (element instanceof edges_1.Association) {
            var idxOfEdge = this.edges.indexOf(element);
            if (idxOfEdge > -1) {
                this.edges.splice(idxOfEdge, 1);
            }
            idxOfEdge = element.$sNode.$edges.indexOf(element);
            if (idxOfEdge > -1) {
                element.$sNode.$edges.splice(idxOfEdge, 1);
            }
            idxOfEdge = element.$tNode.$edges.indexOf(element);
            if (idxOfEdge > -1) {
                element.$tNode.$edges.splice(idxOfEdge, 1);
            }
        }
        util_1.Util.saveToLocalStorage(this);
        return true;
    };
    GraphModel.prototype.getSVG = function () {
        var size = 10;
        var path = "M" + -size + " 0 L" + +size + " 0 M0 " + -size + " L0 " + +size;
        var attr = {
            tag: 'path',
            id: 'origin',
            d: path,
            stroke: '#999',
            'stroke-width': '1',
            fill: 'none'
        };
        var shape = this.createShape(attr);
        var attrText = {
            tag: 'text',
            x: 0 - size,
            y: 0 - size / 1.5,
            'text-anchor': 'end',
            'font-family': 'Verdana',
            'font-size': '9',
            fill: '#999'
        };
        var text = this.createShape(attrText);
        text.textContent = '(0, 0)';
        var group = this.createShape({ tag: 'g' });
        group.appendChild(shape);
        group.appendChild(text);
        return group;
    };
    GraphModel.prototype.getEvents = function () {
        return [EventBus_1.EventBus.ELEMENTMOUSEDOWN, EventBus_1.EventBus.ELEMENTMOUSEUP, EventBus_1.EventBus.ELEMENTMOUSELEAVE, EventBus_1.EventBus.ELEMENTMOUSEMOVE, EventBus_1.EventBus.ELEMENTMOUSEWHEEL, EventBus_1.EventBus.ELEMENTCLICK, EventBus_1.EventBus.ELEMENTDRAG];
    };
    GraphModel.prototype.getNewId = function (prefix) {
        var id = (prefix ? prefix.toLowerCase() + '-' : '') + Math.floor(Math.random() * 100000);
        return id;
    };
    GraphModel.prototype.getEdgeById = function (id) {
        for (var _i = 0, _a = this.edges; _i < _a.length; _i++) {
            var edge = _a[_i];
            if (edge.id === id) {
                return edge;
            }
        }
        return undefined;
    };
    GraphModel.prototype.getDiagramElementById = function (id) {
        return this.getNodeById(id) || this.getEdgeById(id);
    };
    GraphModel.prototype.addEdge = function (edge, withPosOfNodes) {
        if (edge && edge.type) {
            var graph = this.$owner;
            var typeExists = false;
            for (var edgeType in graph.edgeFactory) {
                if (edgeType === edge.type) {
                    typeExists = true;
                    break;
                }
            }
            if (!typeExists) {
                edge.type = 'Association';
            }
        }
        var type = edge.type || 'Association';
        type = util_1.Util.toPascalCase(type);
        var id = this.getNewId(type);
        var newEdge = this.createElement(type, id, edge);
        newEdge.type = type;
        var source;
        var sourceAsString = edge.source.id || edge.source;
        if (sourceAsString) {
            source = this.getNodeById(sourceAsString);
            if (!source) {
                var id_1 = edge.source;
                if (typeof id_1 === 'object') {
                    id_1 = id_1.id;
                }
                source = this.createElement('Clazz', this.getNewId('Clazz'), { name: id_1 });
                source.init(this);
            }
        }
        var target;
        var targetAsString = edge.target.id || edge.target;
        if (targetAsString) {
            target = this.getNodeById(targetAsString);
            if (!target) {
                var id_2 = edge.target;
                if (typeof id_2 === 'object') {
                    id_2 = id_2.id;
                }
                target = this.createElement('Clazz', this.getNewId('Clazz'), { name: id_2 });
                target.init(this);
            }
        }
        newEdge.withItem(source, target);
        if (withPosOfNodes) {
            var srcX = source.getPos().x + (source.getSize().x / 2);
            var srcY = source.getPos().y + (source.getSize().y / 2);
            var targetX = target.getPos().x + (target.getSize().x / 2);
            var targetY = target.getPos().y + (target.getSize().y / 2);
            newEdge.addPoint(srcX, srcY);
            newEdge.addPoint(targetX, targetY);
        }
        util_1.Util.saveToLocalStorage(this);
        return newEdge;
    };
    GraphModel.prototype.createElement = function (type, id, data) {
        var graph = this.$owner;
        var element;
        if (graph.nodeFactory[type]) {
            element = new graph.nodeFactory[type](data);
            util_1.Util.initControl(this, element, type, id, data);
            this.nodes.push(element);
        }
        if (graph.edgeFactory[type]) {
            element = new graph.edgeFactory[type](data);
            util_1.Util.initControl(this, element, type, id, data);
            this.edges.push(element);
        }
        return element;
    };
    GraphModel.prototype.initCanvas = function () {
        var graph = this.$owner;
        graph.canvasSize = { width: graph.$view.clientWidth, height: graph.$view.clientHeight };
        graph.root = util_1.Util.createShape({
            tag: 'svg',
            id: 'root',
            width: graph.canvasSize.width,
            height: graph.canvasSize.height
        });
        this.$view = graph.root;
        graph.$view.appendChild(graph.root);
        var mousewheel = 'onwheel' in document.createElement('div') ? 'wheel' : document.onmousewheel !== undefined ? 'mousewheel' : 'DOMMouseScroll';
        EventBus_1.EventBus.register(this, this.$view);
    };
    GraphModel.prototype.addNode = function (node) {
        var type = node['type'] || node.property || 'Node';
        type = util_1.Util.toPascalCase(type);
        var id = node['id'] || node['name'] || this.getNewId(type);
        return this.createElement(type, id, node);
    };
    GraphModel.prototype.getNodeById = function (id) {
        for (var _i = 0, _a = this.nodes; _i < _a.length; _i++) {
            var node = _a[_i];
            if (node.id === id) {
                return node;
            }
        }
        return undefined;
    };
    return GraphModel;
}(BaseElements_1.DiagramElement));
exports.GraphModel = GraphModel;


/***/ }),

/***/ "./elements/edges/Aggregate.ts":
/*!*************************************!*\
  !*** ./elements/edges/Aggregate.ts ***!
  \*************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Association_1 = __webpack_require__(/*! ./Association */ "./elements/edges/Association.ts");
var Aggregate = (function (_super) {
    __extends(Aggregate, _super);
    function Aggregate() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Aggregate.prototype.getSVG = function () {
        var startPoint = this.$points[0];
        var direction = this.getDirectionOfPointToNode(this.$sNode, startPoint);
        var path = this.calcCorrectPath(startPoint, direction);
        var group = _super.prototype.getSVG.call(this);
        var attr = {
            tag: 'path',
            d: path,
        };
        this.$diamond = this.createShape(attr);
        group.appendChild(this.$diamond);
        return group;
    };
    Aggregate.prototype.redraw = function (startNode, dontDrawPoints) {
        _super.prototype.redraw.call(this, startNode, true);
        var startPoint = this.$points[0];
        var direction = 1;
        if (this.$sNode.id === startNode.id || this.$points.length == 2) {
            direction = this.getDirectionOfPointToNode(this.$sNode, startPoint);
            var path = this.calcCorrectPath(startPoint, direction);
            this.$diamond.setAttributeNS(null, 'd', path);
        }
        this.redrawPointsAndInfo();
    };
    Aggregate.prototype.calcCorrectPath = function (startPoint, direction) {
        var startX = startPoint.x;
        var startY = startPoint.y;
        var path;
        switch (direction) {
            case 0:
                path = "M" + startX + " " + startY + " L" + (startX + 6) + " " + (startY + 10) + " L" + startX + " " + (startY + 20) + " L" + (startX - 6) + " " + (startY + 10) + " Z";
                startPoint.y = startPoint.y + 20;
                break;
            case 3:
                path = "M" + startX + " " + startY + " L" + (startX - 10) + " " + (startY + 6) + " L" + (startX - 20) + " " + startY + " L" + (startX - 10) + " " + (startY - 6) + " Z";
                startPoint.x = startPoint.x - 20;
                break;
            case 2:
                path = "M" + startX + " " + startY + " L" + (startX + 10) + " " + (startY - 6) + " L" + (startX + 20) + " " + startY + " L" + (startX + 10) + " " + (startY + 6) + " Z";
                startPoint.x = startPoint.x + 20;
                break;
            case 1:
                path = "M" + startX + " " + startY + " L" + (startX - 6) + " " + (startY - 10) + " L" + startX + " " + (startY - 20) + " L" + (startX + 6) + " " + (startY - 10) + " Z";
                startPoint.y = startPoint.y - 20;
                break;
            default:
                break;
        }
        return path;
    };
    return Aggregate;
}(Association_1.Association));
exports.Aggregate = Aggregate;


/***/ }),

/***/ "./elements/edges/Aggregation.ts":
/*!***************************************!*\
  !*** ./elements/edges/Aggregation.ts ***!
  \***************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var index_1 = __webpack_require__(/*! ./index */ "./elements/edges/index.ts");
var Aggregation = (function (_super) {
    __extends(Aggregation, _super);
    function Aggregation() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Aggregation.prototype.getSVG = function () {
        var group = _super.prototype.getSVG.call(this);
        this.$diamond.setAttributeNS(null, 'fill', 'white');
        return group;
    };
    return Aggregation;
}(index_1.Aggregate));
exports.Aggregation = Aggregation;


/***/ }),

/***/ "./elements/edges/Association.ts":
/*!***************************************!*\
  !*** ./elements/edges/Association.ts ***!
  \***************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var BaseElements_1 = __webpack_require__(/*! ../BaseElements */ "./elements/BaseElements.ts");
var InfoText_1 = __webpack_require__(/*! ../nodes/InfoText */ "./elements/nodes/InfoText.ts");
var util_1 = __webpack_require__(/*! ../../util */ "./util.ts");
var EventBus_1 = __webpack_require__(/*! ../../EventBus */ "./EventBus.ts");
var edges = __webpack_require__(/*! ../edges */ "./elements/edges/index.ts");
var Direction;
(function (Direction) {
    Direction[Direction["Up"] = 0] = "Up";
    Direction[Direction["Down"] = 1] = "Down";
    Direction[Direction["Left"] = 2] = "Left";
    Direction[Direction["Right"] = 3] = "Right";
})(Direction = exports.Direction || (exports.Direction = {}));
var Association = (function (_super) {
    __extends(Association, _super);
    function Association(data) {
        var _this = _super.call(this) || this;
        _this.$points = [];
        _this.withData(data);
        return _this;
    }
    Association.prototype.withData = function (data) {
        if (!data) {
            return this;
        }
        var srcInfo;
        var trgInfo;
        if (data.source && typeof data.source !== 'string') {
            srcInfo = data.source;
        }
        else if (data.sourceInfo && typeof data.sourceInfo !== 'string') {
            srcInfo = data.sourceInfo;
        }
        if (srcInfo) {
            this.sourceInfo = new InfoText_1.InfoText(srcInfo);
            this.sourceInfo.$owner = this;
        }
        if (data.target && typeof data.target !== 'string') {
            trgInfo = data.target;
        }
        else if (data.targetInfo && typeof data.targetInfo !== 'string') {
            trgInfo = data.targetInfo;
        }
        if (trgInfo) {
            this.targetInfo = new InfoText_1.InfoText(trgInfo);
            this.targetInfo.$owner = this;
        }
        return this;
    };
    Association.prototype.updateSrcCardinality = function (cardinality) {
        this.sourceInfo = this.updateCardinality(this.$sNode, this.sourceInfo, cardinality);
        this.redrawSourceInfo();
        util_1.Util.saveToLocalStorage(this.$owner);
    };
    Association.prototype.updateTargetCardinality = function (cardinality) {
        this.targetInfo = this.updateCardinality(this.$tNode, this.targetInfo, cardinality);
        this.redrawTargetInfo();
        util_1.Util.saveToLocalStorage(this.$owner);
    };
    Association.prototype.updateSrcProperty = function (property) {
        this.sourceInfo = this.updateProperty(this.$sNode, this.sourceInfo, property);
        this.redrawSourceInfo();
        util_1.Util.saveToLocalStorage(this.$owner);
    };
    Association.prototype.updateTargetProperty = function (property) {
        this.targetInfo = this.updateProperty(this.$tNode, this.targetInfo, property);
        this.redrawTargetInfo();
        util_1.Util.saveToLocalStorage(this.$owner);
    };
    Association.prototype.withItem = function (source, target) {
        source.$edges.push(this);
        target.$edges.push(this);
        this.$sNode = source;
        this.$tNode = target;
        this.source = source.id;
        this.target = target.id;
        return this;
    };
    Association.prototype.getSVG = function () {
        var group = util_1.Util.createShape({ tag: 'g', id: this.id, class: 'SVGEdge' });
        var path = this.getPath();
        var attr = {
            tag: 'path',
            d: path,
            fill: 'none'
        };
        var pathLine = this.createShape(attr);
        attr['style'] = 'stroke-width:20;opacity:0;width:20;height:20';
        var extendedPathLine = util_1.Util.createShape(attr);
        group.appendChild(extendedPathLine);
        group.appendChild(pathLine);
        if (this.sourceInfo) {
            var calcPos = this.calcInfoPosNew(this.sourceInfo, this.$sNode);
            this.sourceInfo.withPos(calcPos.x, calcPos.y);
            group.appendChild(this.sourceInfo.getSVG());
        }
        if (this.targetInfo) {
            var calcPos = this.calcInfoPosNew(this.targetInfo, this.$tNode);
            this.targetInfo.withPos(calcPos.x, calcPos.y);
            group.appendChild(this.targetInfo.getSVG());
        }
        this.$pathWideSvg = extendedPathLine;
        this.$pathSvg = pathLine;
        this.$view = group;
        return group;
    };
    Association.prototype.getEvents = function () {
        return [EventBus_1.EventBus.ELEMENTCLICK, EventBus_1.EventBus.ELEMENTDBLCLICK, EventBus_1.EventBus.EDITOR, EventBus_1.EventBus.OPENPROPERTIES];
    };
    Association.prototype.convertEdge = function (type, newId, redraw) {
        if (!edges[type]) {
            return this;
        }
        var newEdge = new edges[type]();
        newEdge.withItem(this.$sNode, this.$tNode);
        newEdge.id = newId;
        newEdge.type = type;
        newEdge.lineStyle = this.lineStyle;
        newEdge.$owner = this.$owner;
        if (this.sourceInfo) {
            newEdge.sourceInfo = new InfoText_1.InfoText({ property: this.sourceInfo.property, cardinality: this.sourceInfo.cardinality });
            newEdge.sourceInfo.$owner = newEdge;
        }
        if (this.targetInfo) {
            newEdge.targetInfo = new InfoText_1.InfoText({ property: this.targetInfo.property, cardinality: this.targetInfo.cardinality });
            newEdge.targetInfo.$owner = newEdge;
        }
        this.$points.forEach(function (point) {
            newEdge.addPoint(point.x, point.y);
        });
        var graph = this.getRoot();
        if (!graph) {
            return this;
        }
        var idx = graph.$graphModel.edges.indexOf(this);
        graph.$graphModel.removeElement(this.id);
        if (idx > -1) {
            graph.$graphModel.edges.splice(idx, 0, newEdge);
        }
        else {
            graph.$graphModel.edges.push(newEdge);
        }
        if (!redraw) {
            return newEdge;
        }
        var svgRoot;
        if (graph) {
            svgRoot = graph.root;
        }
        else {
            svgRoot = document.getElementById('root');
        }
        var newEdgeSvg = newEdge.getSVG();
        graph.removeElement(this);
        svgRoot.appendChild(newEdgeSvg);
        var dontDrawPath = (type !== 'Edge');
        newEdge.redraw(newEdge.$sNode, dontDrawPath);
        newEdge.redraw(newEdge.$tNode, dontDrawPath);
        EventBus_1.EventBus.register(newEdge, newEdgeSvg);
        this.sourceInfo = undefined;
        this.targetInfo = undefined;
        return newEdge;
    };
    Association.prototype.redraw = function (startNode, dontDrawPoints) {
        if (!startNode) {
            return;
        }
        var endPoint;
        var recalcPoint;
        var endPointIdx;
        if (this.$sNode.id === startNode.id) {
            recalcPoint = this.$points[0];
            endPointIdx = 1;
        }
        else if (this.$tNode.id === startNode.id) {
            recalcPoint = this.$points[this.$points.length - 1];
            endPointIdx = this.$points.length - 2;
        }
        endPoint = this.$points[endPointIdx];
        this.calcIntersection(startNode, recalcPoint, endPoint);
        if (this.$points.length > 2 && this.$tNode.id === startNode.id && endPoint.y > (startNode.getPos().y + (startNode.getSize().y / 2))) {
            this.$points.splice(endPointIdx, 1);
        }
        if (this.$tNode.id === startNode.id && this.$points.length === 2) {
            this.calcIntersection(this.$sNode, endPoint, recalcPoint);
        }
        if (this.$points.length > 2 && this.$sNode.id === startNode.id && (startNode.getPos().y + (startNode.getSize().y / 2) > endPoint.y)) {
            this.$points.splice(endPointIdx, 1);
        }
        if (this.$sNode.id === startNode.id && this.$points.length === 2) {
            this.calcIntersection(this.$tNode, endPoint, recalcPoint);
        }
        if (!dontDrawPoints) {
            this.redrawPointsAndInfo();
        }
    };
    Association.prototype.getPath = function () {
        if (this.$points.length === 0) {
            return '';
        }
        var path = 'M';
        for (var i = 0; i < this.$points.length; i++) {
            var point = this.$points[i];
            if (i > 0) {
                path += 'L';
            }
            path += Math.floor(point.x) + ' ' + Math.floor(point.y) + ' ';
        }
        return path;
    };
    Association.prototype.calcInfoPosNew = function (infoTxt, node) {
        if (!infoTxt || !node) {
            return null;
        }
        var startPoint;
        var nextToStartPoint;
        if (this.$sNode.id === node.id) {
            startPoint = this.$points[0];
            nextToStartPoint = this.$points[1];
        }
        else if (this.$tNode.id === node.id) {
            startPoint = this.$points[this.$points.length - 1];
            nextToStartPoint = this.$points[this.$points.length - 2];
        }
        var direction = this.getDirectionOfPointToNode(node, startPoint);
        var x;
        var y;
        switch (direction) {
            case 0:
                if (startPoint.x >= nextToStartPoint.x) {
                    x = startPoint.x + 5;
                }
                else {
                    x = startPoint.x - (infoTxt.getSize().x);
                }
                y = startPoint.y + (infoTxt.getSize().y / 2);
                break;
            case 3:
                if (startPoint.y >= nextToStartPoint.y) {
                    y = startPoint.y + (infoTxt.getSize().y / 2);
                }
                else {
                    y = startPoint.y - (infoTxt.getSize().y / 2) - 5;
                }
                x = startPoint.x - (infoTxt.getSize().x) - 5;
                break;
            case 2:
                if (startPoint.y >= nextToStartPoint.y) {
                    y = startPoint.y + (infoTxt.getSize().y / 2);
                }
                else {
                    y = startPoint.y - (infoTxt.getSize().y / 2) - 5;
                }
                x = startPoint.x + 5;
                break;
            case 1:
                if (startPoint.x >= nextToStartPoint.x) {
                    x = startPoint.x + 5;
                }
                else {
                    x = startPoint.x - (infoTxt.getSize().x);
                }
                y = startPoint.y - (infoTxt.getSize().y / 2) - 5;
                break;
            default:
                break;
        }
        return new BaseElements_1.Point(x, y);
    };
    Association.prototype.clearPoints = function () {
        this.$points = [];
        this.$points = [];
    };
    Association.prototype.addPoint = function (x, y) {
        this.$points.push(new BaseElements_1.Point(x, y));
        return this.$points;
    };
    Association.prototype.redrawPointsAndInfo = function () {
        var path = this.getPath();
        this.$pathSvg.setAttributeNS(null, 'd', path);
        this.$pathWideSvg.setAttributeNS(null, 'd', path);
        this.redrawSourceInfo();
        this.redrawTargetInfo();
    };
    Association.prototype.redrawSourceInfo = function () {
        if (this.sourceInfo) {
            var newPosOfSrc = this.calcInfoPosNew(this.sourceInfo, this.$sNode);
            this.sourceInfo.redrawFromEdge(newPosOfSrc);
        }
    };
    Association.prototype.redrawTargetInfo = function () {
        if (this.targetInfo) {
            var newPosOfTarget = this.calcInfoPosNew(this.targetInfo, this.$tNode);
            this.targetInfo.redrawFromEdge(newPosOfTarget);
        }
    };
    Association.prototype.getDirectionOfPointToNode = function (node, pointNearNode) {
        var x1 = node.getPos();
        var x2 = new BaseElements_1.Point((x1.x + node.getSize().x), (x1.y + node.getSize().y));
        var direction = 1;
        if (x1.y >= pointNearNode.y) {
            direction = 1;
        }
        if (x2.y <= pointNearNode.y) {
            direction = 0;
        }
        if (x1.x >= pointNearNode.x) {
            direction = 3;
        }
        if (x2.x <= pointNearNode.x) {
            direction = 2;
        }
        return direction;
    };
    Association.prototype.updateCardinality = function (node, infoText, cardinality) {
        if (!infoText) {
            infoText = new InfoText_1.InfoText({ 'cardinality': cardinality });
            infoText.$owner = this;
            var calcPos = this.calcInfoPosNew(infoText, node);
            infoText.withPos(calcPos.x, calcPos.y);
            this.$view.appendChild(infoText.getSVG());
            return infoText;
        }
        infoText.cardinality = cardinality;
        if (infoText.isEmpty()) {
            this.$view.removeChild(infoText.$view);
            return undefined;
        }
        infoText.updateCardinality(cardinality);
        return infoText;
    };
    Association.prototype.updateProperty = function (node, infoText, property) {
        if (!infoText) {
            infoText = new InfoText_1.InfoText({ 'property': property });
            infoText.$owner = this;
            var calcPos = this.calcInfoPosNew(infoText, node);
            infoText.withPos(calcPos.x, calcPos.y);
            this.$view.appendChild(infoText.getSVG());
            return infoText;
        }
        infoText.property = property;
        if (infoText.isEmpty()) {
            this.$view.removeChild(infoText.$view);
            return undefined;
        }
        infoText.updateProperty(property);
        return infoText;
    };
    Association.prototype.calcIntersection = function (startNode, recalcPoint, endPoint) {
        var h = startNode.getSize().y;
        var w = startNode.getSize().x;
        var x1 = startNode.getPos().x + (w / 2);
        var y1 = startNode.getPos().y + (h / 2);
        var x2 = endPoint.x;
        var y2 = endPoint.y;
        var newX = recalcPoint.x;
        var newY = recalcPoint.y;
        if (x2 > x1) {
            newX = x1 + (w / 2);
        }
        else if (x2 < x1) {
            newX = x1 - (w / 2);
        }
        else {
            newX = x1;
        }
        if ((x2 - x1) !== 0) {
            newY = ((y2 - y1) / (x2 - x1) * (newX - x1)) + y1;
        }
        else {
            if (y1 > y2) {
                newY = startNode.getPos().y;
            }
            else {
                newY = startNode.getPos().y + h;
            }
        }
        if (!((y1 - (h / 2) <= newY) && newY <= y1 + (h / 2))) {
            if (y2 > y1) {
                newY = y1 + (h / 2);
            }
            else {
                newY = y1 - (h / 2);
            }
            if ((x2 - x1) !== 0) {
                var tmp = ((y2 - y1) / (x2 - x1));
                newX = (newY + (tmp * x1) - y1) / tmp;
            }
            else {
                newX = x1;
            }
        }
        recalcPoint.x = Math.ceil(newX);
        recalcPoint.y = Math.ceil(newY);
        return null;
    };
    return Association;
}(BaseElements_1.DiagramElement));
exports.Association = Association;


/***/ }),

/***/ "./elements/edges/Composition.ts":
/*!***************************************!*\
  !*** ./elements/edges/Composition.ts ***!
  \***************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var index_1 = __webpack_require__(/*! ./index */ "./elements/edges/index.ts");
var Composition = (function (_super) {
    __extends(Composition, _super);
    function Composition() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Composition.prototype.getSVG = function () {
        var group = _super.prototype.getSVG.call(this);
        this.$diamond.setAttributeNS(null, 'fill', 'black');
        return group;
    };
    return Composition;
}(index_1.Aggregate));
exports.Composition = Composition;


/***/ }),

/***/ "./elements/edges/Generalisation.ts":
/*!******************************************!*\
  !*** ./elements/edges/Generalisation.ts ***!
  \******************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Association_1 = __webpack_require__(/*! ./Association */ "./elements/edges/Association.ts");
var Generalisation = (function (_super) {
    __extends(Generalisation, _super);
    function Generalisation() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.$TARGET_ELEMENT_HEIGHT = 12;
        return _this;
    }
    Generalisation.prototype.getSVG = function () {
        var startPoint = this.$points[0];
        var direction = this.getDirectionOfPointToNode(this.$sNode, startPoint);
        var path = this.calcCorrectPath(startPoint, direction);
        var group = _super.prototype.getSVG.call(this);
        var attr = {
            tag: 'path',
            d: path,
            fill: 'white'
        };
        this.$targetElement = this.createShape(attr);
        group.appendChild(this.$targetElement);
        return group;
    };
    Generalisation.prototype.redraw = function (startNode, dontDrawPoints) {
        _super.prototype.redraw.call(this, startNode, true);
        var startPoint = this.$points[0];
        var direction = 0;
        if (this.$sNode.id === startNode.id || this.$points.length == 2) {
            direction = this.getDirectionOfPointToNode(this.$sNode, startPoint);
            var path = this.calcCorrectPath(startPoint, direction);
            this.$targetElement.setAttributeNS(null, 'd', path);
        }
        this.redrawPointsAndInfo();
    };
    Generalisation.prototype.calcCorrectPath = function (startPoint, direction) {
        var startX = startPoint.x;
        var startY = startPoint.y;
        var path;
        switch (direction) {
            case 0:
                path = "M" + startX + " " + (startY + 3) + " L" + (startX + this.$TARGET_ELEMENT_HEIGHT) + " " + (startY + this.$TARGET_ELEMENT_HEIGHT) + " L" + (startX - this.$TARGET_ELEMENT_HEIGHT) + " " + (startY + this.$TARGET_ELEMENT_HEIGHT) + " Z";
                startPoint.y = startPoint.y + 12;
                break;
            case 3:
                path = "M" + (startX - 3) + " " + startY + " L" + (startX - this.$TARGET_ELEMENT_HEIGHT) + " " + (startY + this.$TARGET_ELEMENT_HEIGHT) + " L" + (startX - this.$TARGET_ELEMENT_HEIGHT) + " " + (startY - this.$TARGET_ELEMENT_HEIGHT) + " Z";
                startPoint.x = startPoint.x - 12;
                break;
            case 2:
                path = "M" + (startX + 3) + " " + startY + " L" + (startX + this.$TARGET_ELEMENT_HEIGHT) + " " + (startY - this.$TARGET_ELEMENT_HEIGHT) + " L" + (startX + this.$TARGET_ELEMENT_HEIGHT) + " " + (startY + this.$TARGET_ELEMENT_HEIGHT) + " Z";
                startPoint.x = startPoint.x + 12;
                break;
            case 1:
                path = "M" + startX + " " + (startY - 3) + " L" + (startX + this.$TARGET_ELEMENT_HEIGHT) + " " + (startY - this.$TARGET_ELEMENT_HEIGHT) + " L" + (startX - this.$TARGET_ELEMENT_HEIGHT) + " " + (startY - this.$TARGET_ELEMENT_HEIGHT) + " Z";
                startPoint.y = startPoint.y - 12;
                break;
            default:
                break;
        }
        return path;
    };
    return Generalisation;
}(Association_1.Association));
exports.Generalisation = Generalisation;


/***/ }),

/***/ "./elements/edges/Implements.ts":
/*!**************************************!*\
  !*** ./elements/edges/Implements.ts ***!
  \**************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Generalisation_1 = __webpack_require__(/*! ./Generalisation */ "./elements/edges/Generalisation.ts");
var Implements = (function (_super) {
    __extends(Implements, _super);
    function Implements() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Implements.prototype.getSVG = function () {
        var group = _super.prototype.getSVG.call(this);
        this.$pathSvg.setAttributeNS(null, 'stroke-dasharray', '3, 3');
        return group;
    };
    return Implements;
}(Generalisation_1.Generalisation));
exports.Implements = Implements;


/***/ }),

/***/ "./elements/edges/index.ts":
/*!*********************************!*\
  !*** ./elements/edges/index.ts ***!
  \*********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

function __export(m) {
    for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
}
Object.defineProperty(exports, "__esModule", { value: true });
__export(__webpack_require__(/*! ./Association */ "./elements/edges/Association.ts"));
__export(__webpack_require__(/*! ./Aggregate */ "./elements/edges/Aggregate.ts"));
__export(__webpack_require__(/*! ./Aggregation */ "./elements/edges/Aggregation.ts"));
__export(__webpack_require__(/*! ./Composition */ "./elements/edges/Composition.ts"));
__export(__webpack_require__(/*! ./Generalisation */ "./elements/edges/Generalisation.ts"));
__export(__webpack_require__(/*! ./Implements */ "./elements/edges/Implements.ts"));


/***/ }),

/***/ "./elements/nodes/Attribute.ts":
/*!*************************************!*\
  !*** ./elements/nodes/Attribute.ts ***!
  \*************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var ClazzProperty_1 = __webpack_require__(/*! ./ClazzProperty */ "./elements/nodes/ClazzProperty.ts");
var Attribute = (function (_super) {
    __extends(Attribute, _super);
    function Attribute(data) {
        return _super.call(this, data) || this;
    }
    return Attribute;
}(ClazzProperty_1.default));
exports.default = Attribute;


/***/ }),

/***/ "./elements/nodes/AutoComplete.ts":
/*!****************************************!*\
  !*** ./elements/nodes/AutoComplete.ts ***!
  \****************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Control_1 = __webpack_require__(/*! ../../Control */ "./Control.ts");
var AutoComplete = (function (_super) {
    __extends(AutoComplete, _super);
    function AutoComplete() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    AutoComplete.prototype.load = function (json, owner) {
        this.createControl(this.$owner, json);
    };
    AutoComplete.prototype.createControl = function (parent, data) {
        if (typeof (data) === 'string') {
            this.id = data;
        }
        else {
            this.id = data['id'];
        }
        var div = document.createElement('div');
        this.$view = div;
        this.$inputField = document.createElement('input');
        this.$dataList = document.createElement("datalist");
        this.$dataList.id = "data_" + this.id;
        this.$inputField.setAttribute("list", "data_" + this.id);
        if (data["value"]) {
            var values = data["value"];
            this.isMultiple = data["multiple"] != null;
            var option = void 0;
            if (this.isMultiple) {
                this.$selected = document.createElement("select");
                this.$selected.className = "hide";
                this.$selected.multiple = true;
                this.$selected.id = this.id;
                this.$items = document.createElement("div");
                this.$items.className = "selectedList";
                div.appendChild(this.$items);
                this.$inputField.className = "selectedInput";
                div.appendChild(this.$selected);
                this.$view["style"].setProperty("float", "left");
                var that_1 = this;
                this.$inputField.oninput = function () { that_1.onChange(); };
            }
            else {
                this.$inputField.id = this.id;
            }
            for (var attr in values) {
                if (!values.hasOwnProperty(attr)) {
                    continue;
                }
                option = document.createElement("option");
                option.value = values[attr];
                this.$dataList.appendChild(option);
                if (this.isMultiple) {
                    option = document.createElement("option");
                    option.value = values[attr];
                    option.innerHTML = values[attr];
                    this.$selected.appendChild(option);
                }
            }
            div.appendChild(this.$inputField);
            div.appendChild(this.$dataList);
        }
        if (data instanceof Object) {
            for (var attr in data) {
                if (!data.hasOwnProperty(attr)) {
                    continue;
                }
                this.$view.setAttribute(attr, data[attr]);
            }
            if (this.isMultiple) {
                div.appendChild(this.$selected);
            }
        }
        parent.appendChild(this);
    };
    AutoComplete.prototype.onChange = function () {
        var textValue = this.$inputField.value;
        if (textValue.length < 1) {
            return;
        }
        var _loop_1 = function (i) {
            var item = this_1.$selected.children[i];
            if (item.value == textValue) {
                if (item.selected == false) {
                    item.selected = true;
                    item.defaultSelected = true;
                    var test_1 = document.createElement("li");
                    var that_2 = this_1;
                    test_1.onclick = function () { that_2.onDelete(item.value, test_1); };
                    test_1.className = "selectedItem";
                    test_1.innerHTML = item.value;
                    this_1.$items.appendChild(test_1);
                    this_1.$inputField.value = "";
                }
            }
        };
        var this_1 = this;
        for (var i = 0; i < this.$selected.children.length; i++) {
            _loop_1(i);
        }
    };
    AutoComplete.prototype.onDelete = function (value, selectedItem) {
        this.$items.removeChild(selectedItem);
        for (var i = 0; i < this.$selected.children.length; i++) {
            var item = this.$selected.children[i];
            if (item.value == value) {
                if (item.selected) {
                    item.selected = false;
                }
            }
        }
    };
    return AutoComplete;
}(Control_1.Control));
exports.AutoComplete = AutoComplete;


/***/ }),

/***/ "./elements/nodes/BR.ts":
/*!******************************!*\
  !*** ./elements/nodes/BR.ts ***!
  \******************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Control_1 = __webpack_require__(/*! ../../Control */ "./Control.ts");
var BR = (function (_super) {
    __extends(BR, _super);
    function BR() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    BR.prototype.load = function (json) {
        this.createControl(this.$owner, json);
    };
    BR.prototype.createControl = function (parent, data) {
        this.$view = document.createElement('br');
        for (var attr in data) {
            if (!data.hasOwnProperty(attr)) {
                continue;
            }
            this.$view.setAttribute(attr, data[attr]);
        }
        parent.appendChild(this);
    };
    return BR;
}(Control_1.Control));
exports.BR = BR;


/***/ }),

/***/ "./elements/nodes/Button.ts":
/*!**********************************!*\
  !*** ./elements/nodes/Button.ts ***!
  \**********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Control_1 = __webpack_require__(/*! ../../Control */ "./Control.ts");
var Button = (function (_super) {
    __extends(Button, _super);
    function Button() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Button.prototype.load = function (json, owner) {
        this.createControl(this.$owner, json);
    };
    Button.prototype.createControl = function (parent, data) {
        if (typeof (data) === 'string') {
            this.id = data;
        }
        else {
            this.id = data['id'];
        }
        this.$view = document.createElement('button');
        if (data instanceof Object) {
            for (var attr in data) {
                if (!data.hasOwnProperty(attr)) {
                    continue;
                }
                this.$view.setAttribute(attr, data[attr]);
            }
        }
        parent.appendChild(this);
    };
    return Button;
}(Control_1.Control));
exports.Button = Button;


/***/ }),

/***/ "./elements/nodes/Clazz.ts":
/*!*********************************!*\
  !*** ./elements/nodes/Clazz.ts ***!
  \*********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Node_1 = __webpack_require__(/*! ./Node */ "./elements/nodes/Node.ts");
var EventBus_1 = __webpack_require__(/*! ../../EventBus */ "./EventBus.ts");
var util_1 = __webpack_require__(/*! ../../util */ "./util.ts");
var Attribute_1 = __webpack_require__(/*! ./Attribute */ "./elements/nodes/Attribute.ts");
var Method_1 = __webpack_require__(/*! ./Method */ "./elements/nodes/Method.ts");
var Symbol_1 = __webpack_require__(/*! ./Symbol */ "./elements/nodes/Symbol.ts");
var StereoType_1 = __webpack_require__(/*! ./StereoType */ "./elements/nodes/StereoType.ts");
var Clazz = (function (_super) {
    __extends(Clazz, _super);
    function Clazz(json) {
        var _this = _super.call(this, json) || this;
        _this.attributes = [];
        _this.methods = [];
        _this.$attrHeight = 25;
        _this.$attrFontSize = 12;
        return _this;
    }
    Clazz.prototype.load = function (json) {
        if (!json) {
            json = {};
        }
        var y = this.$labelHeight;
        var labelObj = json.name || json.id || ('New ' + this.property);
        var width = 150;
        width = Math.max(width, util_1.Util.sizeOf(labelObj).width + 60);
        if (json['attributes']) {
            for (var _i = 0, _a = json['attributes']; _i < _a.length; _i++) {
                var attr = _a[_i];
                var attrObj = new Attribute_1.default(attr);
                attrObj.$owner = this;
                this.attributes.push(attrObj);
                y += this.$attrHeight;
                width = Math.max(width, util_1.Util.sizeOf(attrObj.toString()).width);
            }
        }
        if (json['stereotype']) {
            this.stereoType = json['stereotype'];
        }
        if (json['methods']) {
            for (var _b = 0, _c = json['methods']; _b < _c.length; _b++) {
                var method = _c[_b];
                var methodObj = new Method_1.default(method);
                methodObj.$owner = this;
                this.methods.push(methodObj);
                y += this.$attrHeight;
                width = Math.max(width, util_1.Util.sizeOf(methodObj.toString()).width);
            }
            y += this.$attrHeight;
        }
        this.withSize(width, y);
    };
    Clazz.prototype.getAttributes = function () {
        return this.attributes;
    };
    Clazz.prototype.getMethods = function () {
        return this.methods;
    };
    Clazz.prototype.getToolBarIcon = function () {
        var icon = Symbol_1.SymbolLibary.draw({ type: 'Clazz', property: 'HTML', width: '50', height: '50', transform: 'translate(-26,-21)' });
        return icon;
    };
    Clazz.prototype.getSVG = function () {
        var pos = this.getPos();
        var size = this.getSize();
        var group = this.createShape({ tag: 'g', id: this.id, class: 'SVGClazz', transform: 'translate(0 0)' });
        if (this.stereoType) {
            var type = new StereoType_1.StereoType(this.stereoType, pos.x, pos.y);
            group.appendChild(type.getSVG());
        }
        var options = null;
        var style;
        var clazzName;
        if (this.$owner['options']) {
            var options_1 = this.$owner['options'];
            if (options_1) {
                style = options_1.style;
            }
        }
        if (style === 'modern') {
            clazzName = 'ClazzHeader';
        }
        clazzName = 'ClazzHeader';
        var nodeShape = this.createShape({
            tag: 'rect',
            x: pos.x,
            y: pos.y,
            height: size.y,
            width: size.x,
            rx: 5,
            ry: 5,
            fill: 'none',
            stroke: 'black',
            'stroke-width': 1
        });
        if (clazzName) {
            nodeShape.setAttribute('className', clazzName);
            var styleHeader = util_1.Util.getStyle('ClazzHeader');
        }
        var label = this.createShape({
            tag: 'text',
            x: pos.x + size.x / 2,
            y: pos.y + this.$labelHeight / 2,
            'text-anchor': 'middle',
            'alignment-baseline': 'central',
            'font-family': 'Verdana',
            'font-size': this.$labelFontSize,
            fill: 'black'
        });
        label.textContent = this.id;
        this.$labelView = label;
        group.appendChild(nodeShape);
        group.appendChild(label);
        if (this.attributes.length > 0) {
            var separatorLabelAttr = this.createShape({
                tag: 'line',
                x1: pos.x,
                y1: pos.y + this.$labelHeight,
                x2: pos.x + size.x,
                y2: pos.y + this.$labelHeight,
                'stroke-width': 1
            });
            group.appendChild(separatorLabelAttr);
            var groupOfAttributes = this.createShape({ tag: 'g', id: (this.id + 'Attributes') });
            groupOfAttributes.setAttributeNS(null, 'class', 'SVGClazzProperty SVGClazzAttribute');
            group.appendChild(groupOfAttributes);
            var y_1 = pos.y + this.$labelHeight + this.$attrHeight / 2;
            for (var _i = 0, _a = this.attributes; _i < _a.length; _i++) {
                var attr = _a[_i];
                var attrSvg = attr.getSVG();
                attr.$owner = this;
                attrSvg.setAttributeNS(null, 'x', '' + (pos.x + 10));
                attrSvg.setAttributeNS(null, 'y', '' + y_1);
                groupOfAttributes.appendChild(attrSvg);
                y_1 += this.$attrHeight;
            }
        }
        var height = this.attributes.length * this.$attrHeight;
        var y = pos.y + this.$labelHeight + height + this.$attrHeight / 2;
        if (this.methods.length > 0) {
            var separatorAttrMethods = this.createShape({
                tag: 'line',
                x1: pos.x,
                y1: pos.y + this.$labelHeight + (this.$attrHeight * this.attributes.length),
                x2: pos.x + size.x,
                y2: pos.y + this.$labelHeight + (this.$attrHeight * this.attributes.length),
                'stroke-width': 1
            });
            group.appendChild(separatorAttrMethods);
            var groupOfMethods = this.createShape({ tag: 'g', id: (this.id + 'Methods') });
            groupOfMethods.setAttributeNS(null, 'class', 'SVGClazzProperty SVGClazzMethod');
            group.appendChild(groupOfMethods);
            y += this.$attrHeight / 2;
            for (var _b = 0, _c = this.methods; _b < _c.length; _b++) {
                var method = _c[_b];
                var methodSvg = method.getSVG();
                method.$owner = this;
                methodSvg.setAttributeNS(null, 'x', '' + (pos.x + 10));
                methodSvg.setAttributeNS(null, 'y', '' + y);
                groupOfMethods.appendChild(methodSvg);
                y += this.$attrHeight;
            }
        }
        this.$view = group;
        return group;
    };
    Clazz.prototype.copy = function () {
        var copy;
        copy = _super.prototype.copy.call(this);
        copy.id = this.id + 'Copy';
        this.attributes.forEach(function (attr) {
            copy.attributes.push(new Attribute_1.default(attr.toString()));
        });
        this.methods.forEach(function (method) {
            copy.methods.push(new Method_1.default(method.toString()));
        });
        copy.reCalcSize();
        return copy;
    };
    Clazz.prototype.getEvents = function () {
        return [EventBus_1.EventBus.ELEMENTMOUSEDOWN, EventBus_1.EventBus.ELEMENTMOUSEMOVE, EventBus_1.EventBus.ELEMENTCLICK,
            EventBus_1.EventBus.ELEMENTDRAG, EventBus_1.EventBus.ELEMENTDBLCLICK, EventBus_1.EventBus.OPENPROPERTIES, EventBus_1.EventBus.RELOADPROPERTIES];
    };
    Clazz.prototype.addProperty = function (value, type) {
        if (!this[type] || !value || value.length === 0) {
            return;
        }
        var extractedValue;
        if (type === 'attributes') {
            extractedValue = new Attribute_1.default(value);
        }
        else if (type === 'methods') {
            extractedValue = new Method_1.default(value);
        }
        for (var _i = 0, _a = this[type]; _i < _a.length; _i++) {
            var valueOfType = _a[_i];
            if (valueOfType.toString() === extractedValue.toString()) {
                alert(extractedValue.toString() + ' already exists.');
                extractedValue = undefined;
                return;
            }
        }
        this[type].push(extractedValue);
        util_1.Util.saveToLocalStorage(this.$owner);
        return extractedValue;
    };
    Clazz.prototype.addAttribute = function (value) {
        return this.addProperty(value, 'attributes');
    };
    Clazz.prototype.addAttributeObj = function (attr) {
        this.attributes.push(attr);
        return this.getAttributes();
    };
    Clazz.prototype.addMethodObj = function (method) {
        this.methods.push(method);
        return this.getMethods();
    };
    Clazz.prototype.addMethod = function (value) {
        return this.addProperty(value, 'methods');
    };
    Clazz.prototype.removeAttribute = function (attr) {
        var idx = this.attributes.indexOf(attr);
        this.attributes.splice(idx, 1);
    };
    Clazz.prototype.removeMethod = function (method) {
        var idx = this.methods.indexOf(method);
        this.methods.splice(idx, 1);
    };
    Clazz.prototype.removeProperty = function (property) {
        if (property instanceof Attribute_1.default) {
            this.removeAttribute(property);
        }
        if (property instanceof Method_1.default) {
            this.removeMethod(property);
        }
        util_1.Util.saveToLocalStorage(this.$owner);
    };
    Clazz.prototype.reDraw = function (drawOnlyIfSizeChanged) {
        var hasSizeChanged = this.hasSizeChanged();
        if (drawOnlyIfSizeChanged) {
            if (!hasSizeChanged[0]) {
                return;
            }
        }
        if (!this.$view) {
            return;
        }
        this.$owner.$view.removeChild(this.$view);
        var newSvg = this.getSVG();
        this.$owner.$view.appendChild(newSvg);
        this.$view = newSvg;
        EventBus_1.EventBus.register(this, newSvg);
        this.redrawEdges();
    };
    Clazz.prototype.hasSizeChanged = function () {
        var oldSize = { width: this.getSize().x, height: this.getSize().y };
        var newSize = this.reCalcSize();
        if (oldSize.width === newSize.width && oldSize.height === newSize.height) {
            return [false, newSize];
        }
        return [true, newSize];
    };
    Clazz.prototype.updateLabel = function (newLabel) {
        var _this = this;
        if (this.$labelView) {
            this.$labelView.textContent = newLabel;
        }
        this.$edges.forEach(function (edge) {
            if (_this.id === edge.$sNode.id) {
                edge.source = newLabel;
            }
            else if (_this.id === edge.$tNode.id) {
                edge.target = newLabel;
            }
        });
        util_1.Util.saveToLocalStorage(this.$owner);
        this.reDraw(true);
    };
    Clazz.prototype.updateModifier = function (modifier) {
        this.modifier = modifier;
        util_1.Util.saveToLocalStorage(this.$owner);
    };
    Clazz.prototype.reCalcSize = function () {
        var newWidth = 150;
        newWidth = Math.max(newWidth, util_1.Util.sizeOf(this.id).width + 30);
        this.attributes.forEach(function (attrEl) {
            var widthOfAttr;
            if (attrEl.$view) {
                widthOfAttr = attrEl.$view.getBoundingClientRect().width;
            }
            else {
                widthOfAttr = util_1.Util.sizeOf(attrEl.toString()).width;
            }
            newWidth = Math.max(newWidth, widthOfAttr + 15);
        });
        this.methods.forEach(function (methodEl) {
            var widthOfMethod;
            if (methodEl.$view) {
                widthOfMethod = methodEl.$view.getBoundingClientRect().width;
            }
            else {
                widthOfMethod = util_1.Util.sizeOf(methodEl.toString()).width;
            }
            newWidth = Math.max(newWidth, widthOfMethod + 15);
        });
        this.getSize().x = newWidth;
        this.getSize().y = this.$labelHeight + ((this.attributes.length + this.methods.length) * this.$attrHeight)
            + this.$attrHeight;
        var newSize = { width: newWidth, height: this.getSize().y };
        return newSize;
    };
    Clazz.prototype.redrawEdges = function () {
        for (var _i = 0, _a = this.$edges; _i < _a.length; _i++) {
            var edge = _a[_i];
            edge.redraw(this);
        }
    };
    Clazz.prototype.getModernStyle = function () {
        var width, height, id, size, z, item, rect, g, board, styleHeader, headerHeight, x, y;
        board = this.getRoot()['board'];
        styleHeader = util_1.Util.getStyle('ClazzHeader');
        headerHeight = styleHeader.getNumber('height');
        width = 0;
        height = 10 + headerHeight;
        if (this.property === 'Object' || this.getRoot()['$graphModel'].getType().toLowerCase() === 'objectdiagram') {
            id = this.id.charAt(0).toLowerCase() + this.id.slice(1);
            item = 'Object';
        }
        else {
            id = this.id;
            item = 'Clazz';
            if (this['counter']) {
                id += ' (' + this['counter'] + ')';
            }
        }
        g = util_1.Util.create({ tag: 'g', model: this });
        size = util_1.Util.sizeOf(id, this);
        width = Math.max(width, size.width);
        if (this.attributes && this.attributes.length > 0) {
            height = height + this.attributes.length * 25;
            for (z = 0; z < this.attributes.length; z += 1) {
                width = Math.max(width, util_1.Util.sizeOf(this.attributes[z], this).width);
            }
        }
        else {
            height += 20;
        }
        if (this.methods && this.methods.length > 0) {
            height = height + this.methods.length * 25;
            for (z = 0; z < this.methods.length; z += 1) {
                width = Math.max(width, util_1.Util.sizeOf(this.methods[z], this).width);
            }
        }
        width += 20;
        var pos = this.getPos();
        y = pos.y;
        x = pos.x;
        rect = {
            tag: 'rect',
            'width': width,
            'height': height,
            'x': x,
            'y': y,
            'class': item + ' draggable',
            'fill': 'none'
        };
        g.appendChild(util_1.Util.create(rect));
        g.appendChild(util_1.Util.create({
            tag: 'rect',
            rx: 0,
            'x': x,
            'y': y,
            height: headerHeight,
            'width': width,
            'class': 'ClazzHeader'
        }));
        item = util_1.Util.create({
            tag: 'text',
            $font: true,
            'class': 'InfoText',
            'text-anchor': 'right',
            'x': x + width / 2 - size.width / 2,
            'y': y + (headerHeight / 2) + (size.height / 2),
            'width': size.width
        });
        if (this.property === 'Object' || this.getRoot()['$graphModel'].type.toLowerCase() === 'objectdiagram') {
            item.setAttribute('text-decoration', 'underline');
        }
        item.appendChild(document.createTextNode(id));
        g.appendChild(item);
        g.appendChild(util_1.Util.create({
            tag: 'line',
            x1: x,
            y1: y + headerHeight,
            x2: x + width,
            y2: y + headerHeight,
            stroke: '#000'
        }));
        y += headerHeight + 20;
        if (this.attributes) {
            for (z = 0; z < this.attributes.length; z += 1) {
                g.appendChild(util_1.Util.create({
                    tag: 'text',
                    $font: true,
                    'text-anchor': 'left',
                    'width': width,
                    'x': (x + 10),
                    'y': y,
                    value: this.attributes[z]
                }));
                y += 20;
            }
            if (this.attributes.length > 0) {
                y -= 10;
            }
        }
        if (this.methods && this.methods.length > 0) {
            g.appendChild(util_1.Util.create({ tag: 'line', x1: x, y1: y, x2: x + width, y2: y, stroke: '#000' }));
            y += 20;
            for (z = 0; z < this.methods.length; z += 1) {
                g.appendChild(util_1.Util.create({
                    tag: 'text',
                    $font: true,
                    'text-anchor': 'left',
                    'width': width,
                    'x': x + 10,
                    'y': y,
                    value: this.methods[z]
                }));
                y += 20;
            }
        }
        return g;
    };
    return Clazz;
}(Node_1.Node));
exports.Clazz = Clazz;


/***/ }),

/***/ "./elements/nodes/ClazzProperty.ts":
/*!*****************************************!*\
  !*** ./elements/nodes/ClazzProperty.ts ***!
  \*****************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var util_1 = __webpack_require__(/*! ../../util */ "./util.ts");
var BaseElements_1 = __webpack_require__(/*! ../BaseElements */ "./elements/BaseElements.ts");
var ClazzProperty = (function (_super) {
    __extends(ClazzProperty, _super);
    function ClazzProperty(data) {
        var _this = _super.call(this) || this;
        _this.modifier = '+';
        _this.extractData(data);
        return _this;
    }
    ClazzProperty.prototype.update = function (data) {
        this.extractData(data);
        this.updateTextOfView();
    };
    ClazzProperty.prototype.updateModifier = function (modifier) {
        this.modifier = modifier;
        this.updateTextOfView();
    };
    ClazzProperty.prototype.updateType = function (type) {
        this.type = type;
        this.updateTextOfView();
    };
    ClazzProperty.prototype.updateName = function (name) {
        this.name = name;
        this.updateTextOfView();
    };
    ClazzProperty.prototype.getSVG = function () {
        var attrText = {
            tag: 'text',
            'text-anchor': 'start',
            'alignment-baseline': 'middle',
        };
        var attrSvg = util_1.Util.createShape(attrText);
        attrSvg.textContent = this.toString();
        this.$view = attrSvg;
        return attrSvg;
    };
    ClazzProperty.prototype.toString = function () {
        return this.modifier + " " + this.name + " : " + this.type;
    };
    ClazzProperty.prototype.extractData = function (data) {
        if (!data) {
            return;
        }
        if (data.type) {
            this.type = data.type;
        }
        if (data.name) {
            this.name = data.name;
        }
        if (data.modifier) {
            this.modifier = data.modifier;
        }
        if (typeof data === 'string') {
            var dataSplitted = data.split(':');
            if (dataSplitted && dataSplitted.length === 2) {
                var modifierAndNameSplitted = dataSplitted[0].trim();
                var firstChar = modifierAndNameSplitted[0];
                if (firstChar === '+' || firstChar === '-' || firstChar === '#') {
                    this.modifier = firstChar;
                    this.name = modifierAndNameSplitted.substring(1, modifierAndNameSplitted.length).trim();
                }
                else {
                    this.name = modifierAndNameSplitted;
                }
                this.name = this.name.replace(/ /g, '_');
                this.type = dataSplitted[1].trim() || 'String';
                if (this.type.toLowerCase() === 'string') {
                    this.type = 'String';
                }
                this.type = this.type.replace(/ /g, '_');
            }
        }
    };
    ClazzProperty.prototype.updateTextOfView = function () {
        this.$view.textContent = this.toString();
        util_1.Util.saveToLocalStorage(this.$owner.$owner);
    };
    return ClazzProperty;
}(BaseElements_1.DiagramElement));
exports.default = ClazzProperty;


/***/ }),

/***/ "./elements/nodes/Dice.ts":
/*!********************************!*\
  !*** ./elements/nodes/Dice.ts ***!
  \********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Node_1 = __webpack_require__(/*! ./Node */ "./elements/nodes/Node.ts");
var Dice = (function (_super) {
    __extends(Dice, _super);
    function Dice(data) {
        var _this = _super.call(this, data) || this;
        _this.max = 6;
        _this.$zoom = 0.6;
        _this.$border = 0.2;
        _this.withSize(100, 100);
        return _this;
    }
    Dice.prototype.setNumber = function (number) {
        this.value = number;
        this.refresh();
    };
    Dice.prototype.refresh = function () {
        if (this.$view) {
            this.reset();
            var group = this.createPointValue();
            if (group) {
                this.$view.appendChild(group);
            }
        }
    };
    Dice.prototype.reset = function () {
        if (this.$view) {
            while (this.$view.children.length > 1) {
                if (this.$view.children.item(this.$view.children.length - 1).tagName !== "animateTransform") {
                    console.log(this.$view.children.item(this.$view.children.length - 1));
                    this.$view.removeChild(this.$view.children.item(this.$view.children.length - 1));
                }
                else {
                    break;
                }
            }
        }
    };
    Dice.prototype.getSVG = function () {
        var pos = this.getPos();
        var size = this.getSize();
        var dice = this.createShape({ tag: 'g' });
        var attr = {
            tag: 'rect',
            x: pos.x + size.x * this.$border,
            y: pos.y + size.y * this.$border,
            rx: 4,
            ry: 4,
            height: size.y * this.$zoom,
            width: size.x * this.$zoom,
            style: 'fill:white;stroke:black;stroke-width:2'
        };
        dice.appendChild(this.createShape(attr));
        var group = this.createPointValue();
        if (group) {
            dice.appendChild(group);
        }
        this.$view = dice;
        return dice;
    };
    Dice.prototype.animationTimeout = function (newValues) {
        if (newValues.length > 0) {
            var newValue = newValues.shift();
            this.setNumber(newValue);
            var that_1 = this;
            setTimeout(function () { that_1.animationTimeout(newValues); }, 100);
        }
    };
    Dice.prototype.roll = function () {
        this.startAnimation();
        var values = [];
        var i;
        for (i = 1; i < this.max; i++) {
            values.push(i);
        }
        for (i = this.max; i > 0; i--) {
            values.push(i);
        }
        var that = this;
        values.push(Math.floor(Math.random() * this.max) + 1);
        setTimeout(function () { that.animationTimeout(values); }, 100);
    };
    Dice.prototype.startAnimation = function () {
        if (this.$animation) {
            return;
        }
        var center = this.getPos().x + this.getSize().x / 2;
        var attr = {
            tag: 'animateTransform',
            attributeType: "xml",
            attributeName: "transform",
            type: "rotate",
            dur: "1s",
            repeatCount: "1",
            from: "0 " + center + " " + center,
            to: "360 " + center + " " + center
        };
        this.$animation = this.createShape(attr);
        this.$view.appendChild(this.$animation);
    };
    Dice.prototype.stopAnimation = function () {
        if (this.$animation) {
            this.$view.removeChild(this.$animation);
            this.$animation = null;
        }
    };
    Dice.prototype.createPointValue = function () {
        if (this.value == 1) {
            return this.getCircle(2, 2);
        }
        else if (this.value == 2) {
            return this.getCircle(1, 1, 3, 3);
        }
        else if (this.value == 3) {
            return this.getCircle(1, 1, 2, 2, 3, 3);
        }
        else if (this.value == 4) {
            return this.getCircle(1, 1, 1, 3, 3, 1, 3, 3);
        }
        else if (this.value == 5) {
            return this.getCircle(1, 1, 1, 3, 2, 2, 3, 1, 3, 3);
        }
        else if (this.value == 6) {
            return this.getCircle(1, 1, 1, 2, 1, 3, 3, 1, 3, 2, 3, 3);
        }
        else if (this.value == 7) {
            return this.getCircle(1, 1, 1, 2, 1, 3, 2, 2, 3, 1, 3, 2, 3, 3);
        }
        else if (this.value == 8) {
            return this.getCircle(1, 1, 1, 2, 1, 3, 2, 1, 2, 3, 3, 1, 3, 2, 3, 3);
        }
        else if (this.value == 9) {
            return this.getCircle(1, 1, 1, 2, 1, 3, 2, 1, 2, 2, 2, 3, 3, 1, 3, 2, 3, 3);
        }
        return null;
    };
    Dice.prototype.getCircle = function () {
        var values = [];
        for (var _i = 0; _i < arguments.length; _i++) {
            values[_i] = arguments[_i];
        }
        if (values.length % 2 > 0) {
            return null;
        }
        var size = this.getSize();
        var pos = this.getPos();
        var group = this.createShape({ tag: 'g' });
        for (var i = 0; i < values.length; i += 2) {
            group.appendChild(this.createCircle(values[i], values[i + 1]));
        }
        return group;
    };
    Dice.prototype.createCircle = function (x, y) {
        var size = this.getSize();
        var radius = size.x / 10 * this.$zoom;
        var border = size.y * this.$border;
        var zoom = size.y * this.$zoom;
        var attr = {
            tag: 'circle',
            r: radius,
            cx: (size.x * this.$zoom * x) / 4 + border,
            cy: (size.y * this.$zoom * y) / 4 + border,
            stroke: "black",
            "stroke-width": "3",
            fill: "red",
            style: 'fill:black;stroke:black;stroke-width:2'
        };
        return this.createShape(attr);
    };
    return Dice;
}(Node_1.Node));
exports.Dice = Dice;


/***/ }),

/***/ "./elements/nodes/Div.ts":
/*!*******************************!*\
  !*** ./elements/nodes/Div.ts ***!
  \*******************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Control_1 = __webpack_require__(/*! ../../Control */ "./Control.ts");
var Div = (function (_super) {
    __extends(Div, _super);
    function Div() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Div.prototype.load = function (data) {
        var id;
        if (typeof (data) === 'string') {
            id = data;
        }
        else {
            id = data.id;
            this.className = data.class;
            this.property = data.property;
        }
        if (!id) {
            return;
        }
        this.id = id;
        var div = document.getElementById(id);
        if (!this.property) {
            this.property = div.getAttribute('Property');
        }
        if (div instanceof HTMLDivElement) {
            this.$view = div;
        }
        else {
            if (!div) {
                this.$view = document.createElement('div');
                this.$view.setAttribute('id', this.id);
                this.$view.setAttribute('property', this.property);
                this.$owner.appendChild(this);
            }
            else {
                return;
            }
        }
        if (data.hasOwnProperty('property')) {
            this.setProperty(data['property']);
        }
    };
    Div.prototype.addItem = function (source, entity) {
        this.$model = entity;
        if (entity) {
            if (!this.className || entity.hasProperty(this.className)) {
                if (entity.id === this.property.split('.')[0]) {
                    entity.addListener(this, this.className);
                }
            }
        }
    };
    Div.prototype.updateElement = function (property, oldValue, newValue) {
        this.$view.innerHTML = newValue;
    };
    return Div;
}(Control_1.Control));
exports.Div = Div;


/***/ }),

/***/ "./elements/nodes/Form.ts":
/*!********************************!*\
  !*** ./elements/nodes/Form.ts ***!
  \********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Control_1 = __webpack_require__(/*! ../../Control */ "./Control.ts");
var Form = (function (_super) {
    __extends(Form, _super);
    function Form() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.children = {};
        return _this;
    }
    Form.prototype.load = function (data) {
        var id;
        if (typeof (data) === 'string') {
            id = data;
        }
        else {
            id = data.id;
        }
        if (!id) {
            return;
        }
        this.id = id;
        var form = document.getElementById(id);
        if (form instanceof HTMLFormElement) {
            this.$view = form;
            if (this.$view.hasAttribute('property')) {
                this.property = this.$view.getAttribute('property');
            }
        }
        else {
            if (!form) {
                this.$view = document.createElement('form');
                this.$view.setAttribute('id', this.id);
                if (data.hasOwnProperty('property')) {
                    this.property = data['property'];
                }
                for (var attr in data) {
                    if (data.hasOwnProperty(attr) === false) {
                        continue;
                    }
                    if (attr === 'elements') {
                        continue;
                    }
                    this.$view.setAttribute(attr, data[attr]);
                }
                this.$owner.appendChild(this);
            }
            else {
                return;
            }
        }
        var objId = this.property;
        var hasItem = this.$owner.hasItem(objId);
        if (hasItem) {
            var item = this.$owner.getItem(objId);
            item.addListener(this);
            this.$model = item;
        }
        for (var _i = 0, _a = data.elements; _i < _a.length; _i++) {
            var field = _a[_i];
            if (field.hasOwnProperty('property')) {
                var property = field['property'];
                property = this.property + '.' + property;
                field['property'] = property;
            }
            if (!field.hasOwnProperty('class')) {
                field['class'] = 'input';
            }
            var control = this.$owner.load(field, this);
            this.children[control.getId()] = control;
        }
    };
    Form.prototype.setProperty = function (id) {
        this.property = id;
        var keys = Object.keys(this.children);
        for (var k = 0; k < keys.length; k++) {
            var key = keys[k];
            var childControl = this.children[key];
            if (childControl.property) {
                childControl.setProperty(this.property + '.' + childControl.lastProperty);
            }
        }
    };
    Object.defineProperty(Form.prototype, "lastProperty", {
        get: function () {
            return this.property.split('.')[0];
        },
        enumerable: true,
        configurable: true
    });
    Form.prototype.setValue = function (object, attribute, newValue, oldValue) {
        if (this.$owner != null) {
            return this.getRoot().setValue(object, attribute, newValue, oldValue);
        }
        return _super.prototype.setValue.call(this, object, attribute, newValue, oldValue);
    };
    return Form;
}(Control_1.Control));
exports.Form = Form;


/***/ }),

/***/ "./elements/nodes/HTML.ts":
/*!********************************!*\
  !*** ./elements/nodes/HTML.ts ***!
  \********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Control_1 = __webpack_require__(/*! ../../Control */ "./Control.ts");
var HTML = (function (_super) {
    __extends(HTML, _super);
    function HTML(data) {
        var _this = _super.call(this) || this;
        var id;
        var tag;
        if (typeof (data) === 'string') {
            id = data;
            data = {};
        }
        else if (data) {
            id = data.id;
        }
        if (id) {
            _this.id = id;
            _this.$view = document.getElementById(id);
        }
        if (!_this.$view) {
            if (data) {
                tag = data['tag'] || 'div';
            }
            else {
                tag = 'div';
            }
            _this.$view = document.createElement(tag);
            var parent_1 = document.getElementsByTagName('body')[0];
            parent_1.appendChild(_this.$view);
        }
        if (!parent) {
            return _this;
        }
        _this.writeAttribute(data, _this.$view);
        return _this;
    }
    HTML.prototype.writeAttribute = function (properties, entity) {
        var lowKey;
        if (!entity) {
            lowKey = properties['tag'] || 'div';
            entity = document.createElement(lowKey);
        }
        for (var key in properties) {
            if (!properties.hasOwnProperty(key)) {
                continue;
            }
            lowKey = key.toLowerCase();
            if (properties[key] === null) {
                entity.setAttribute(key, '');
                continue;
            }
            if (lowKey === 'tag' || lowKey.charAt(0) === '$' || lowKey === '$graphModel' || lowKey === 'class') {
                continue;
            }
            if (lowKey === 'children') {
                if (Array.isArray(properties[key])) {
                    for (var item in properties[key]) {
                        if (properties[key].hasOwnProperty(item) === false) {
                            continue;
                        }
                        var child = this.writeAttribute(item);
                        if (child) {
                            entity.appendChild(child);
                        }
                    }
                }
                else {
                    var child = this.writeAttribute(properties[key]);
                    if (child) {
                        entity.appendChild(child);
                    }
                }
                continue;
            }
            entity[key] = properties[key];
        }
        return entity;
    };
    return HTML;
}(Control_1.Control));
exports.HTML = HTML;


/***/ }),

/***/ "./elements/nodes/InfoText.ts":
/*!************************************!*\
  !*** ./elements/nodes/InfoText.ts ***!
  \************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var BaseElements_1 = __webpack_require__(/*! ../BaseElements */ "./elements/BaseElements.ts");
var util_1 = __webpack_require__(/*! ../../util */ "./util.ts");
var Node_1 = __webpack_require__(/*! ./Node */ "./elements/nodes/Node.ts");
var EventBus_1 = __webpack_require__(/*! ../../EventBus */ "./EventBus.ts");
var InfoText = (function (_super) {
    __extends(InfoText, _super);
    function InfoText(info) {
        var _this = _super.call(this, info) || this;
        _this.cardinality = '';
        if (typeof (info) === 'string') {
            _this.id = info;
        }
        else {
            if (info.property) {
                _this.property = info.property;
            }
            if (info.cardinality) {
                _this.cardinality = info.cardinality;
            }
        }
        _this.$isDraggable = true;
        var calcSize = _this.calcSize();
        _this.withSize(calcSize.x, calcSize.y);
        return _this;
    }
    InfoText.prototype.updateCardinality = function (cardinality) {
        this.cardinality = cardinality;
        var calcSize = this.calcSize();
        this.withSize(calcSize.x, calcSize.y);
        if (this.$rectBackground) {
            this.$rectBackground.setAttributeNS(null, 'width', '' + calcSize.x);
            this.$rectBackground.setAttributeNS(null, 'height', '' + calcSize.y);
        }
        if (!this.$view) {
            var svg = this.getSVG();
            this.$owner.$view.appendChild(svg);
            return;
        }
        if ((cardinality.length === 0 && this.property.length > 0) || !this.$cardinalitySvg) {
            this.$owner.$view.removeChild(this.$view);
            this.resetAllSvgElements();
            var svg = this.getSVG();
            this.$owner.$view.appendChild(svg);
            return;
        }
        if (this.$cardinalitySvg) {
            this.$cardinalitySvg.textContent = cardinality;
            if (this.$rectBackground) {
                this.$rectBackground.setAttributeNS(null, 'width', '' + calcSize.x);
                this.$rectBackground.setAttributeNS(null, 'height', '' + calcSize.y);
            }
            return;
        }
        if (this.property.length === 0) {
            this.$owner.$view.removeChild(this.$view);
            this.resetAllSvgElements();
            return;
        }
    };
    InfoText.prototype.updateProperty = function (property) {
        this.property = property;
        var calcSize = this.calcSize();
        this.withSize(calcSize.x, calcSize.y);
        if (!this.$view) {
            var svg = this.getSVG();
            this.$owner.$view.appendChild(svg);
            return;
        }
        if ((property.length === 0 && this.cardinality.length > 0) || !this.$propertySvg) {
            this.$owner.$view.removeChild(this.$view);
            this.resetAllSvgElements();
            var svg = this.getSVG();
            this.$owner.$view.appendChild(svg);
            return;
        }
        if (this.$propertySvg) {
            this.$propertySvg.textContent = property;
            if (this.$rectBackground) {
                this.$rectBackground.setAttributeNS(null, 'width', '' + calcSize.x);
                this.$rectBackground.setAttributeNS(null, 'height', '' + calcSize.y);
            }
            return;
        }
        if (this.cardinality.length === 0) {
            this.$owner.$view.removeChild(this.$view);
            this.resetAllSvgElements();
        }
    };
    InfoText.prototype.getSVG = function () {
        var pos = this.getPos();
        var group = util_1.Util.create({ tag: 'g', class: 'SVGEdgeInfo', transform: 'translate(0, 0)' });
        this.$rectBackground = util_1.Util.createShape({
            tag: 'rect',
            x: pos.x,
            y: pos.y - this.$heightOfOneTextItem + 3,
            width: this.getSize().x,
            height: this.getSize().y,
            fill: '#DDD',
            'stroke-width': 0,
            rx: '5',
            ry: '5'
        });
        group.appendChild(this.$rectBackground);
        var y = pos.y;
        if (this.property) {
            this.$propertySvg = util_1.Util.createShape({
                tag: 'text',
                x: pos.x + 3,
                y: y,
                'text-anchor': 'left'
            });
            this.$propertySvg.textContent = this.property;
            group.appendChild(this.$propertySvg);
            y += this.$heightOfOneTextItem;
        }
        if (this.cardinality) {
            this.$cardinalitySvg = util_1.Util.createShape({
                tag: 'text',
                x: pos.x + 3,
                y: y,
                'text-anchor': 'left'
            });
            this.$cardinalitySvg.textContent = this.cardinality;
            group.appendChild(this.$cardinalitySvg);
        }
        this.$view = group;
        return group;
    };
    InfoText.prototype.isEmpty = function () {
        var cardinalityAvailable = this.cardinality && this.cardinality.length > 0;
        var propertyAvailable = this.property && this.property.length > 0;
        return !propertyAvailable && !cardinalityAvailable;
    };
    InfoText.prototype.redrawFromEdge = function (newPos) {
        if (!newPos) {
            return;
        }
        var oldPos = this.getPos();
        var diffPos = new BaseElements_1.Point();
        diffPos.x = newPos.x - oldPos.x;
        diffPos.y = newPos.y - oldPos.y;
        var translation = this.$view.getAttributeNS(null, 'transform').slice(10, -1).split(' ');
        var sx = parseInt(translation[0]);
        var sy = 0;
        if (translation.length > 1) {
            sy = parseInt(translation[1]);
        }
        var newTransX = (sx + diffPos.x);
        var newTransY = (sy + diffPos.y);
        this.$view.setAttributeNS(null, 'transform', 'translate(' + newTransX + ' ' + newTransY + ')');
        this.getPos().x = newPos.x;
        this.getPos().y = newPos.y;
    };
    InfoText.prototype.getText = function () {
        var infoTxt = '';
        if (this.property) {
            infoTxt = this.property;
        }
        if (this.cardinality) {
            if (infoTxt.length > 0) {
                infoTxt += '\n';
            }
            infoTxt += this.cardinality;
        }
        return infoTxt;
    };
    InfoText.prototype.getEvents = function () {
        return [EventBus_1.EventBus.ELEMENTCLICK, EventBus_1.EventBus.ELEMENTDBLCLICK, EventBus_1.EventBus.OPENPROPERTIES];
    };
    InfoText.prototype.calcSize = function () {
        var text = this.getText();
        var items = text.split('\n');
        var maxSize = new BaseElements_1.Point(0, 0);
        if (text.length === 0) {
            return maxSize;
        }
        for (var i = 0; i < items.length; i += 1) {
            var sizeOfText = util_1.Util.sizeOf(items[i]);
            maxSize.x = Math.max(maxSize.x, sizeOfText.width);
            maxSize.y += sizeOfText.height;
            this.$heightOfOneTextItem = sizeOfText.height;
        }
        return maxSize;
    };
    InfoText.prototype.resetAllSvgElements = function () {
        this.$cardinalitySvg = undefined;
        this.$view = undefined;
        this.$propertySvg = undefined;
    };
    return InfoText;
}(Node_1.Node));
exports.InfoText = InfoText;


/***/ }),

/***/ "./elements/nodes/Input.ts":
/*!*********************************!*\
  !*** ./elements/nodes/Input.ts ***!
  \*********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Control_1 = __webpack_require__(/*! ../../Control */ "./Control.ts");
var PropertyBinder_1 = __webpack_require__(/*! ../../PropertyBinder */ "./PropertyBinder.ts");
var Input = (function (_super) {
    __extends(Input, _super);
    function Input() {
        return _super.call(this) || this;
    }
    Input.prototype.initViewDataProperties = function (oldData) {
        var data = _super.prototype.initViewDataProperties.call(this, oldData);
        if ('checkbox' === this.type || 'radio' === this.type) {
            data.addFrom('checked', oldData);
        }
        data.addFrom('value', oldData);
        data.addFrom('type', oldData);
        return data;
    };
    Input.prototype.load = function (data) {
        var id;
        var inputField;
        var useData;
        if (typeof (data) === 'string') {
            id = data;
            useData = true;
        }
        else {
            id = data.id;
            if (data.type) {
                this.setType(data['type']);
            }
            else {
                this.setType('text');
            }
            if (data.hasOwnProperty('property')) {
                this.setProperty(data['property']);
            }
            useData = false;
        }
        if (!id) {
            return;
        }
        this.id = id;
        inputField = document.getElementById(id);
        if (useData) {
            if (inputField) {
                if (inputField.hasAttribute('Property')) {
                    this.setProperty(inputField.getAttribute('Property'));
                }
            }
        }
        if (inputField instanceof HTMLInputElement) {
            this.setView(inputField);
            this.type = inputField.type;
        }
        else {
            if (!inputField) {
                this.setView(document.createElement('input'));
                this.$viewData = this.initViewDataProperties(this.$viewData);
                if (typeof (data) !== 'string') {
                    for (var attr in data) {
                        if (data.hasOwnProperty(attr) === false) {
                            continue;
                        }
                        this.$viewData.setValue(attr, data[attr]);
                    }
                }
                else {
                    if (this.type) {
                        this.$view.setAttribute('type', this.type);
                    }
                    if (data.hasOwnProperty('class')) {
                        this.$view.setAttribute('class', data['class']);
                    }
                    this.$view.setAttribute('id', this.id);
                    this.$view.setAttribute('property', this.property);
                }
                if (data.value) {
                    if (this.$model) {
                        this.$model.setValue(this.lastProperty, data.value);
                    }
                }
                if (this.$model) {
                    PropertyBinder_1.PropertyBinder.bind(this.$viewData, this.$model, 'value', this.lastProperty);
                }
                this.$owner.appendChild(this);
            }
            else {
                return;
            }
        }
    };
    Input.prototype.addItem = function (source, entity) {
        if (this.property && entity) {
            if (entity.id === this.property.split('.')[0]) {
                this.$model = entity;
                PropertyBinder_1.PropertyBinder.bind(this.$viewData, this.$model, 'value', this.lastProperty);
            }
        }
    };
    Input.prototype.controlChanged = function (ev) {
        if (this.$view instanceof HTMLInputElement === false) {
            return;
        }
        var element = this.$view;
        if (element.checkValidity()) {
            _super.prototype.controlChanged.call(this, ev);
        }
    };
    Input.prototype.setType = function (type) {
        var oldValue = this.type;
        if (oldValue === type) {
            return;
        }
        if (type === 'radio') {
            this.$viewData.setValue('checked', null);
        }
        else {
            this.$viewData.removeKey('checked');
        }
    };
    return Input;
}(Control_1.Control));
exports.Input = Input;


/***/ }),

/***/ "./elements/nodes/Label.ts":
/*!*********************************!*\
  !*** ./elements/nodes/Label.ts ***!
  \*********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Control_1 = __webpack_require__(/*! ../../Control */ "./Control.ts");
var Label = (function (_super) {
    __extends(Label, _super);
    function Label() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Label.prototype.load = function (data) {
        this.createControl(this.$owner, data);
    };
    Label.prototype.createControl = function (parent, data) {
        this.$view = document.createElement('label');
        for (var attr in data) {
            if (!data.hasOwnProperty(attr)) {
                continue;
            }
            if (attr === 'textContent') {
                this.$view.textContent = data['textContent'];
            }
            else {
                this.$view.setAttribute(attr, data[attr]);
            }
        }
        parent.appendChild(this);
    };
    return Label;
}(Control_1.Control));
exports.Label = Label;


/***/ }),

/***/ "./elements/nodes/Method.ts":
/*!**********************************!*\
  !*** ./elements/nodes/Method.ts ***!
  \**********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var util_1 = __webpack_require__(/*! ../../util */ "./util.ts");
var ClazzProperty_1 = __webpack_require__(/*! ./ClazzProperty */ "./elements/nodes/ClazzProperty.ts");
var Method = (function (_super) {
    __extends(Method, _super);
    function Method(data) {
        return _super.call(this, data) || this;
    }
    Method.prototype.extractData = function (data) {
        if (!data) {
            return;
        }
        if (data.type) {
            this.type = data.type;
        }
        if (data.name) {
            this.name = data.name;
        }
        if (data.modifier) {
            this.modifier = data.modifier;
        }
        if (typeof data === 'string') {
            var dataSplitted = data.split(':');
            if (dataSplitted && dataSplitted.length === 2) {
                var modifierAndNameSplitted = dataSplitted[0].trim();
                var firstChar = modifierAndNameSplitted[0];
                if (firstChar === '+' || firstChar === '-' || firstChar === '#') {
                    this.modifier = firstChar;
                    this.name = modifierAndNameSplitted.substring(1, modifierAndNameSplitted.length).trim();
                }
                else {
                    this.name = modifierAndNameSplitted;
                }
                this.type = dataSplitted[1].trim() || 'void';
            }
            else {
                var modifierAndNameSplitted = data.trim();
                var firstChar = modifierAndNameSplitted[0];
                if (firstChar === '+' || firstChar === '-' || firstChar === '#') {
                    this.modifier = firstChar;
                    this.name = modifierAndNameSplitted.substring(1, modifierAndNameSplitted.length).trim();
                }
                else {
                    this.name = modifierAndNameSplitted;
                }
                this.type = 'void';
            }
        }
        if (!util_1.Util.includes(this.name, '(') && !util_1.Util.includes(this.name, ')')) {
            this.name += '()';
        }
    };
    return Method;
}(ClazzProperty_1.default));
exports.default = Method;


/***/ }),

/***/ "./elements/nodes/Node.ts":
/*!********************************!*\
  !*** ./elements/nodes/Node.ts ***!
  \********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var BaseElements_1 = __webpack_require__(/*! ../BaseElements */ "./elements/BaseElements.ts");
var Node = (function (_super) {
    __extends(Node, _super);
    function Node(data) {
        var _this = _super.call(this) || this;
        _this.$edges = [];
        _this.$minWidth = 150;
        _this.$minheight = 25;
        _this.withSize(_this.$minWidth, _this.$minheight);
        if (data) {
            if (data['x'] && data['y']) {
                _this.withPos(data['x'], data['y']);
            }
            if (data['width'] || data['height']) {
                _this.withSize(data['width'], data['height']);
            }
        }
        return _this;
    }
    Node.prototype.getSVG = function () {
        var pos = this.getPos();
        var size = this.getSize();
        var attr = {
            tag: 'rect',
            x: pos.x - size.x / 2,
            y: pos.y - size.y / 2,
            rx: 4,
            ry: 4,
            height: size.y,
            width: size.x,
            style: 'fill:white;stroke:black;stroke-width:2'
        };
        var shape = this.createShape(attr);
        var attrText = {
            tag: 'text',
            x: pos.x,
            y: pos.y,
            'text-anchor': 'middle',
            'alignment-baseline': 'middle',
            'font-family': 'Verdana',
            'font-size': '14',
            fill: 'black'
        };
        var text = this.createShape(attrText);
        text.textContent = this.id;
        var group = this.createShape({ tag: 'g', id: this.id });
        group.appendChild(shape);
        group.appendChild(text);
        return group;
    };
    Node.prototype.copy = function () {
        var copy;
        var model = this.$owner || this.getRoot();
        if (model) {
            var type = this.property || Node.name;
            var newId = model.getNewId(type);
            copy = model.createElement(type, newId, null);
            copy.withSize(this.getSize().x, this.getSize().y);
            copy.$owner = model;
        }
        else {
            copy.id = this.id + '-copy';
            copy.$owner = this.getRoot();
        }
        return copy;
    };
    Node.prototype.redrawEdges = function () {
        for (var _i = 0, _a = this.$edges; _i < _a.length; _i++) {
            var edge = _a[_i];
            edge.redraw(this);
        }
    };
    return Node;
}(BaseElements_1.DiagramElement));
exports.Node = Node;


/***/ }),

/***/ "./elements/nodes/SO.ts":
/*!******************************!*\
  !*** ./elements/nodes/SO.ts ***!
  \******************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var BaseElements_1 = __webpack_require__(/*! ../BaseElements */ "./elements/BaseElements.ts");
var SO = (function (_super) {
    __extends(SO, _super);
    function SO() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    SO.create = function (element) {
        var result = new SO();
        for (var key in element) {
            if (element.hasOwnProperty(key) === false) {
                continue;
            }
            result.withKeyValue(key, element[key]);
        }
        return result;
    };
    SO.prototype.withKeyValue = function (key, value) {
        if (key === 'typ') {
            this.property = value;
        }
        else if (key === 'x') {
            this.withPos(value, null);
        }
        else if (key === 'y') {
            this.withPos(null, value);
        }
        else if (key === 'width') {
            this.withSize(value, null);
        }
        else if (key === 'height') {
            this.withSize(null, value);
        }
        else {
            this[key] = value;
        }
        return this;
    };
    return SO;
}(BaseElements_1.DiagramElement));
exports.SO = SO;


/***/ }),

/***/ "./elements/nodes/StereoType.ts":
/*!**************************************!*\
  !*** ./elements/nodes/StereoType.ts ***!
  \**************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Node_1 = __webpack_require__(/*! ./Node */ "./elements/nodes/Node.ts");
var StereoType = (function (_super) {
    __extends(StereoType, _super);
    function StereoType(type, x, y) {
        var _this = _super.call(this, '') || this;
        _this.withPos(x, y);
        _this.setStereoType(type);
        return _this;
    }
    StereoType.prototype.getSVG = function () {
        var pos = this.getPos();
        var size = this.getSize();
        var stereoType = this.createShape({
            tag: 'text',
            x: pos.x + size.x / 2,
            y: pos.y - this.$labelHeight / 2,
            'text-anchor': 'middle',
            'alignment-baseline': 'central',
            'font-family': 'Verdana',
            'font-size': 10,
            'font-weight': 'bold',
            fill: 'black'
        });
        stereoType.textContent = this.stereoType;
        this.$view = stereoType;
        return stereoType;
    };
    StereoType.prototype.setStereoType = function (value) {
        this.stereoType = '<<' + value + '>>';
    };
    StereoType.prototype.getStereoType = function () {
        return this.stereoType;
    };
    return StereoType;
}(Node_1.Node));
exports.StereoType = StereoType;


/***/ }),

/***/ "./elements/nodes/Symbol.ts":
/*!**********************************!*\
  !*** ./elements/nodes/Symbol.ts ***!
  \**********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Node_1 = __webpack_require__(/*! ./Node */ "./elements/nodes/Node.ts");
var SO_1 = __webpack_require__(/*! ./SO */ "./elements/nodes/SO.ts");
var BaseElements_1 = __webpack_require__(/*! ../BaseElements */ "./elements/BaseElements.ts");
var util_1 = __webpack_require__(/*! ../../util */ "./util.ts");
var Symbol = (function (_super) {
    __extends(Symbol, _super);
    function Symbol(typ) {
        var _this = _super.call(this, typ) || this;
        _this.$heightMax = 0;
        _this.$heightMin = 0;
        return _this;
    }
    Symbol.prototype.draw = function (typ) {
        return SymbolLibary.draw(this);
    };
    return Symbol;
}(Node_1.Node));
exports.Symbol = Symbol;
var SymbolLibary = (function () {
    function SymbolLibary() {
    }
    SymbolLibary.drawSVG = function (node) {
        var symbol, fn = this[SymbolLibary.getName(node)];
        if (typeof fn === 'function') {
            var parent_1 = SO_1.SO.create(node);
            parent_1['property'] = 'SVG';
            symbol = fn(parent_1);
            return SymbolLibary.createGroup(parent_1, symbol);
        }
        return symbol;
    };
    SymbolLibary.draw = function (node, parent) {
        var symbol, fn = this[SymbolLibary.getName(node)];
        if (typeof fn === 'function') {
            if (!(node instanceof BaseElements_1.DiagramElement)) {
                node = SO_1.SO.create(node);
                if (!node.property) {
                    node.property = 'SVG';
                }
            }
            symbol = fn.apply(this, [node]);
            if (!parent) {
                return SymbolLibary.createGroup(node, symbol);
            }
            return SymbolLibary.createGroup(node, symbol);
        }
        else if (node.property) {
            symbol = new Symbol(node.property);
            symbol.init(node);
            var pos = node.getPos();
            var size = node.getSize();
            symbol.withPos(pos.x, pos.y);
            symbol.withSize(size.x, size.y);
            symbol['value'] = node['value'];
            parent = node['$parent'];
            return SymbolLibary.draw(symbol, parent);
        }
        return null;
    };
    SymbolLibary.upFirstChar = function (txt) {
        return txt.charAt(0).toUpperCase() + txt.slice(1).toLowerCase();
    };
    SymbolLibary.isSymbolName = function (typ) {
        var fn = SymbolLibary['draw' + SymbolLibary.upFirstChar(typ)];
        return typeof fn === 'function';
    };
    SymbolLibary.isSymbol = function (node) {
        var fn = SymbolLibary[SymbolLibary.getName(node)];
        return typeof fn === 'function';
    };
    SymbolLibary.getName = function (node) {
        if (node['type']) {
            return 'draw' + SymbolLibary.upFirstChar(node['type']);
        }
        if (node.property) {
            return 'draw' + SymbolLibary.upFirstChar(node.property);
        }
        if (node['src']) {
            return 'draw' + SymbolLibary.upFirstChar(node['src']);
        }
        return 'drawNode';
    };
    SymbolLibary.createGroup = function (node, group) {
        var func, y, z, box, item, transform, i, offsetX = 0, offsetY = 0;
        var svg;
        if (node.property.toUpperCase() === 'HTML') {
            var so = {
                tag: 'svg',
                style: { left: (group.x | 0) + node.getPos().x, top: (group.y | 0) + node.getPos().y, position: 'absolute' }
            };
            if (node['transform']) {
                so['transform'] = node['transform'];
            }
            svg = util_1.Util.create(so);
        }
        else {
            svg = util_1.Util.create({ tag: 'g' });
            transform = 'translate(' + group.getPos().x + ' ' + group.getPos().y + ')';
            if (group.scale) {
                transform += ' scale(' + group.scale + ')';
            }
            if (group.rotate) {
                transform += ' rotate(' + group.rotate + ')';
            }
            svg.setAttribute('transform', transform);
            if (group['id']) {
                svg.id = group['id'];
            }
        }
        if (node['tooltip']) {
            var tooltipTitleAttr = {
                tag: 'title'
            };
            var tooltipTitle = util_1.Util.createShape(tooltipTitleAttr);
            tooltipTitle.textContent = node['tooltip'];
            svg.appendChild(tooltipTitle);
        }
        if (node['background']) {
            var attrCircle = {
                tag: 'circle',
                cx: 20,
                cy: 20,
                r: 17,
                stroke: '#888',
                'stroke-width': 0,
                fill: '#DDD'
            };
            svg.appendChild(util_1.Util.create(attrCircle));
        }
        for (i = 0; i < group.items.length; i += 1) {
            svg.appendChild(util_1.Util.create(group.items[i]));
        }
        var elements = node['elements'];
        util_1.Util.setSize(svg, group.width + node.getSize().x, group.height + node.getSize().y);
        node['$heightMin'] = node.getSize().y;
        if (elements) {
            for (i = 0; i < elements.length; i += 1) {
                if (!elements[i] && elements[i].length < 1) {
                    elements.splice(i, 1);
                    i -= 1;
                }
            }
            box = util_1.Util.create({ tag: 'g' });
            var c = void 0;
            z = 0;
            for (c = 0; c < elements.length; c += 1) {
                if (typeof elements[c] === 'string') {
                    z += 1;
                }
                else {
                    z += elements[c].length;
                }
            }
            z = z * 25 + 6;
            box.appendChild(util_1.Util.create({
                tag: 'rect',
                rx: 0,
                x: offsetX,
                y: (offsetY + 28),
                width: 70,
                height: z,
                stroke: '#000',
                fill: '#fff',
                opacity: '0.7'
            }));
            node['$heightMax'] = z + node['$heightMin'];
            svg['elements'] = elements;
            if (node['type'] === 'DropDown') {
                svg['activ'] = util_1.Util.create({
                    tag: 'text',
                    $font: true,
                    'text-anchor': 'left',
                    'width': 60,
                    'x': (10 + offsetX),
                    'y': 20,
                    value: node['activText']
                });
                svg.appendChild(svg.activ);
            }
            y = offsetY + 46;
            func = function (event) {
                if (svg.activ) {
                    svg.activ.textContent = event.currentTarget.value;
                }
            };
            var txt = void 0;
            var textClass = 'SVGTEXT';
            for (z = 0; z < elements.length; z += 1) {
                if (typeof elements[z] === 'string') {
                    txt = elements[z];
                }
                else {
                    item = this.addText(y, offsetX, box, elements[z][0], 'SVGTEXT');
                    y += 26;
                    for (c = 1; c < elements[z].length; c += 1) {
                        item = this.addText(y, offsetX, box, '* ' + elements[z][c], 'SVGTEXTITEM');
                        item['value'] = elements[z];
                        if (node['action']) {
                            item['onclick'] = node['action'];
                        }
                        else {
                            item['onclick'] = func;
                        }
                        y += 26;
                    }
                    txt = elements[z];
                    var subBox = util_1.Util.create({
                        tag: 'rect',
                        rx: 0,
                        x: offsetX,
                        y: (offsetY + 28),
                        width: 60,
                        height: z,
                        stroke: '#000',
                        fill: '#fff',
                        opacity: '0.7'
                    });
                    continue;
                }
                item = this.addText(y, offsetX, box, txt, textClass);
                item['value'] = elements[z];
                if (node['action']) {
                    item['onclick'] = node['action'];
                }
                else {
                    item['onclick'] = func;
                }
                y += 26;
            }
            svg.choicebox = box;
        }
        svg.tool = node;
        svg.onclick = function () {
            if (svg.status === 'close') {
                svg.open();
            }
            else {
                svg.close();
            }
        };
        svg.close = function () {
            if (svg.status === 'open' && svg.choicebox) {
                this.removeChild(svg.choicebox);
            }
            svg.status = 'close';
            svg.tool.$size.height = svg.tool.heightMin;
            util_1.Util.setSize(svg, svg.tool.$size.x, svg.tool.$size.y);
        };
        svg.open = function () {
            if (this.tagName === 'svg') {
                return;
            }
            if (svg.status === 'close' && svg.choicebox) {
                this.appendChild(svg.choicebox);
            }
            svg.status = 'open';
            svg.tool.$size.height = svg.tool.heightMax;
            util_1.Util.setSize(svg, svg.tool.width, svg.tool.height);
        };
        svg.close();
        return svg;
    };
    SymbolLibary.addChild = function (parent, json) {
        var item;
        if (json.offsetLeft) {
            item = json;
        }
        else {
            item = util_1.Util.create(json);
        }
        item.setAttribute('class', 'draggable');
        parent.appendChild(item);
    };
    SymbolLibary.all = function (node) {
        SymbolLibary.drawSmiley(node);
        SymbolLibary.drawDatabase(node);
        SymbolLibary.drawLetter(node);
        SymbolLibary.drawMobilephone(node);
        SymbolLibary.drawWall(node);
        SymbolLibary.drawActor(node);
        SymbolLibary.drawLamp(node);
        SymbolLibary.drawArrow(node);
        SymbolLibary.drawButton(node);
        SymbolLibary.drawDropdown(node);
        SymbolLibary.drawClassicon(node);
        SymbolLibary.drawClassWithEdgeicon(node);
    };
    SymbolLibary.drawHamburger = function (node) {
        return SO_1.SO.create({
            x: node.getPos().x,
            y: node.getPos().y,
            width: 50,
            height: 52,
            items: [
                { tag: 'circle', r: 10, fill: '#ccc', cx: 12, cy: 12, 'stroke-width': 1, stroke: 'black' },
                { tag: 'path', d: 'M 8,7 H 16 M 8,12 H 16 M 8,17 H 16', stroke: 'black', fill: 'none' }
            ]
        });
    };
    SymbolLibary.drawSmiley = function (node) {
        return SO_1.SO.create({
            x: node.getPos().x,
            y: node.getPos().y,
            width: 50,
            height: 52,
            items: [
                { tag: 'path', d: 'm49.01774,25.64542a24.5001,24.5 0 1 1 -49.0001,0a24.5001,24.5 0 1 1 49.0001,0z', stroke: 'black', fill: 'none' }, { tag: 'path', d: 'm8,31.5c16,20 32,0.3 32,0.3' },
                { tag: 'path', d: 'm19.15,20.32a1.74,2.52 0 1 1 -3.49,0a1.74,2.52 0 1 1 3.49,0z' },
                { tag: 'path', d: 'm33,20.32a1.74,2.52 0 1 1 -3.48,0a1.74,2.52 0 1 1 3.48,0z' },
                { tag: 'path', d: 'm5.57,31.65c3.39,0.91 4.03,-2.20 4.03,-2.20' },
                { tag: 'path', d: 'm43,32c-3,0.91 -4,-2.20 -4.04,-2.20' }
            ]
        });
    };
    SymbolLibary.drawDatabase = function (node) {
        return SO_1.SO.create({
            x: node.getPos().x || 0,
            y: node.getPos().y || 0,
            width: 25,
            height: 40,
            items: [
                {
                    tag: 'path',
                    d: 'm0,6.26c0,-6.26 25.03,-6.26 25.03,0l0,25.82c0,6.26 -25.03,6.26 -25.03,0l0,-25.82z',
                    stroke: 'black',
                    fill: 'none'
                },
                {
                    tag: 'path',
                    d: 'm0,6.26c0,4.69 25.03,4.69 25.03,0m-25.03,2.35c0,4.69 25.03,4.69 25.03,0m-25.03,2.35c0,4.69 25.03,4.69 25.03,0',
                    stroke: 'black',
                    fill: 'none'
                }
            ]
        });
    };
    SymbolLibary.drawLetter = function (node) {
        return SO_1.SO.create({
            x: node.getPos().x || 0,
            y: node.getPos().y || 0,
            width: 25,
            height: 17,
            items: [
                { tag: 'path', stroke: 'black', fill: 'none', d: 'm1,1l22,0l0,14l-22,0l0,-14z' },
                { tag: 'path', stroke: 'black', fill: 'none', d: 'm1.06,1.14l10.94,6.81l10.91,-6.91' }
            ]
        });
    };
    SymbolLibary.drawMobilephone = function (node) {
        return SO_1.SO.create({
            x: node.getPos().x || 0,
            y: node.getPos().y || 0,
            width: 25,
            height: 50,
            items: [
                {
                    tag: 'path',
                    d: 'm 4.2 0.5 15.61 0c 2 0 3.7 1.65 3.7 3.7l 0 41.6c 0 2-1.65 3.7-3.7 3.7l-15.6 0c-2 0-3.7-1.6-3.7-3.7l 0-41.6c 0-2 1.6-3.7 3.7-3.7z',
                    fill: 'none',
                    stroke: 'black'
                },
                { tag: 'path', d: 'm 12.5 2.73a 0.5 0.5 0 1 1-1 0 0.5 0.5 0 1 1 1 0z' },
                { tag: 'path', d: 'm 14 46a 2 2 0 1 1-4 0 2 2 0 1 1 4 0z' },
                { tag: 'path', d: 'm 8 5 7 0' },
                { tag: 'path', d: 'm 1.63 7.54 20.73 0 0 34-20.73 0z' }
            ]
        });
    };
    SymbolLibary.drawWall = function (node) {
        return SO_1.SO.create({
            x: node.getPos().x || 0,
            y: node.getPos().y || 0,
            width: 25,
            height: 50,
            items: [
                { tag: 'path', d: 'm26,45.44l-5,3.56l-21,-9l0,-36.41l5,-3.56l20.96,9l-0,36.4z' },
                {
                    tag: 'path',
                    stroke: 'white',
                    d: 'm2.21,11l18.34,7.91m-14.46,-12.57l0,6.3m8.2,21.74l0,6.35m-8.6,-10l0,6.351m4.1,-10.67l0,6.3m4.8,-10.2l0,6.3m-8.87,-10.23l0,6.35m4.78,-10.22l0,6.35m-8,14.5l18.34,7.91m-18.34,-13.91l18.34,7.91m-18.34,-13.91l18.34,7.91m-18.34,-13.91l18.34,7.91m0,-13l0,34m-18.23,-41.84l18.3,8m0,0.11l5,-3.57'
                }
            ]
        });
    };
    SymbolLibary.drawActor = function (node) {
        return SO_1.SO.create({
            x: node.getPos().x || 0,
            y: node.getPos().y || 0,
            width: 25,
            height: 50,
            items: [
                { tag: 'line', stroke: '#000', x1: '12', y1: '10', x2: '12', y2: '30' },
                { tag: 'circle', stroke: '#000', cy: '5', cx: '12', r: '5' },
                { tag: 'line', stroke: '#000', y2: '18', x2: '25', y1: '18', x1: '0' },
                { tag: 'line', stroke: '#000', y2: '39', x2: '5', y1: '30', x1: '12' },
                { tag: 'line', stroke: '#000', y2: '39', x2: '20', y1: '30', x1: '12' }
            ]
        });
    };
    SymbolLibary.drawLamp = function (node) {
        return SO_1.SO.create({
            x: node.getPos().x || 0,
            y: node.getPos().y || 0,
            width: 25,
            height: 50,
            items: [
                {
                    tag: 'path',
                    d: 'm 22.47 10.58c-6.57 0-11.89 5.17-11.89 11.54 0 2.35 0.74 4.54 2 6.36 2 4 4.36 5.63 4.42 10.4l 11.15 0c 0.12-4.9 2.5-6.8 4.43-10.4 1.39-1.5 1.8-4.5 1.8-6.4 0-6.4-5.3-11.5-11.9-11.5z',
                    fill: 'white',
                    stroke: 'black'
                },
                {
                    tag: 'path',
                    d: 'm 18.4 40 8 0c 0.58 0 1 0.5 1 1 0 0.6-0.5 1-1 1l-8 0c-0.6 0-1-0.47-1-1 0-0.58 0.47-1 1-1z'
                },
                {
                    tag: 'path',
                    d: 'm 18.4 42.7 8 0c 0.58 0 1 0.47 1 1 0 0.58-0.47 1-1 1l-8 0c-0.58 0-1-0.47-1-1 0-0.58 0.46-1 1-1z'
                },
                {
                    tag: 'path',
                    d: 'm 18.4 45.3 8 0c 0.58 0 1 0.47 1 1 0 0.58-0.47 1-1 1l-8 0c-0.58 0-1-0.47-1-1 0-0.58 0.46-1 1-1z'
                },
                { tag: 'path', d: 'm 19.5 48c 0.37 0.8 1 1.3 1.9 1.7 0.6 0.3 1.5 0.3 2 0 0.8-0.3 1.4-0.8 1.9-1.8z' },
                {
                    tag: 'path',
                    d: 'm 6 37.5 4.2-4c 0.3-0.3 0.8-0.3 1 0 0.3 0.3 0.3 0.8 0 1.1l-4.2 4c-0.3 0.3-0.8 0.3-1.1 0-0.3-0.3-0.3-0.8 0-1z'
                },
                {
                    tag: 'path',
                    d: 'm 39 37.56-4.15-4c-0.3-0.3-0.8-0.3-1 0-0.3 0.3-0.3 0.8 0 1l 4.2 4c 0.3 0.3 0.8 0.3 1 0 0.3-0.3 0.3-0.8 0-1z'
                },
                {
                    tag: 'path',
                    d: 'm 38 23 5.8 0c 0.4 0 0.8-0.3 0.8-0.8 0-0.4-0.3-0.8-0.8-0.8l-5.8 0c-0.4 0-0.8 0.3-0.8 0.8 0 0.4 0.3 0.8 0.8 0.8z'
                },
                {
                    tag: 'path',
                    d: 'm 1.3 23 6 0c 0.4 0 0.8-0.3 0.8-0.8 0-0.4-0.3-0.8-0.8-0.8l-5.9 0c-0.4 0-0.8 0.3-0.8 0.8 0 0.4 0.3 0.8 0.8 0.8z'
                },
                {
                    tag: 'path',
                    d: 'm 34.75 11.2 4-4.1c 0.3-0.3 0.3-0.8 0-1-0.3-0.3-0.8-0.3-1 0l-4 4.1c-0.3 0.3-0.3 0.8 0 1 0.3 0.3 0.8 0.3 1 0z'
                },
                {
                    tag: 'path',
                    d: 'm 11.23 10-4-4c-0.3-0.3-0.8-0.3-1 0-0.3 0.3-0.3 0.8 0 1l 4.2 4c 0.3 0.3 0.8 0.3 1 0 0.3-0.3 0.3-0.8 0-1z'
                },
                {
                    tag: 'path',
                    d: 'm 21.64 1.3 0 5.8c 0 0.4 0.3 0.8 0.8 0.8 0.4 0 0.8-0.3 0.8-0.8l 0-5.8c 0-0.4-0.3-0.8-0.8-0.8-0.4 0-0.8 0.3-0.8 0.8z'
                },
                {
                    tag: 'path',
                    d: 'm 26.1 24.3c-0.5 0-1 0.2-1.3 0.4-1.1 0.6-2 3-2.27 3.5-0.26-0.69-1.14-2.9-2.2-3.5-0.7-0.4-2-0.7-2.5 0-0.6 0.8 0.2 2.2 0.9 2.9 1 0.9 3.9 0.9 3.9 0.9 0 0 0 0 0 0 0.54 0 2.8 0 3.7-0.9 0.7-0.7 1.5-2 0.9-2.9-0.2-0.3-0.7-0.4-1.2-0.4z'
                },
                { tag: 'path', d: 'm 22.5 28.57 0 10.7' }
            ]
        });
    };
    SymbolLibary.drawStop = function (node) {
        return SO_1.SO.create({
            x: node.getPos().x || 0,
            y: node.getPos().y || 0,
            width: 30,
            height: 30,
            items: [
                {
                    tag: 'path',
                    fill: '#FFF',
                    'stroke-width': '2',
                    stroke: '#B00',
                    d: 'm 6,6 a 14,14 0 1 0 0.06,-0.06 z m 0,0 20,21'
                }
            ]
        });
    };
    SymbolLibary.drawMin = function (node) {
        return SO_1.SO.create({
            x: node.getPos().x || 0,
            y: node.getPos().y || 0,
            width: 20,
            height: 20,
            items: [
                {
                    tag: 'path',
                    fill: 'white',
                    stroke: '#000',
                    'stroke-width': 0.2,
                    'stroke-linejoin': 'round',
                    d: 'm 0,0 19,0 0,19 -19,0 z'
                },
                {
                    tag: 'path',
                    fill: 'none',
                    stroke: '#000',
                    'stroke-width': '1px',
                    'stroke-linejoin': 'miter',
                    d: 'm 4,10 13,-0.04'
                }
            ]
        });
    };
    SymbolLibary.drawArrow = function (node) {
        return SO_1.SO.create({
            x: node.getPos().x || 0,
            y: node.getPos().y || 0,
            width: 10,
            height: 9,
            rotate: node['rotate'],
            items: [
                { tag: 'path', fill: '#000', stroke: '#000', d: 'M 0,0 10,4 0,9 z' }
            ]
        });
    };
    SymbolLibary.drawMax = function (node) {
        return SO_1.SO.create({
            x: node.getPos().x || 0,
            y: node.getPos().y || 0,
            width: 20,
            height: 20,
            items: [
                {
                    tag: 'path',
                    fill: 'white',
                    stroke: '#000',
                    'stroke-width': 0.2,
                    'stroke-linejoin': 'round',
                    'stroke-dashoffset': 2,
                    'stroke-dasharray': '4.8,4.8',
                    d: 'm 0,0 4.91187,0 5.44643,0 9.11886,0 0,19.47716 -19.47716,0 0,-15.88809 z'
                },
                {
                    tag: 'path',
                    fill: 'none',
                    stroke: '#000',
                    'stroke-width': '1px',
                    'stroke-linejoin': 'miter',
                    d: 'm 4,10 6,0.006 0.02,5 0.01,-11 -0.03,6.02 c 2,-0.01 4,-0.002 6,0.01'
                }
            ]
        });
    };
    SymbolLibary.drawButton = function (node) {
        var btnX, btnY, btnWidth, btnHeight, btnValue;
        btnX = node.getPos().x || 0;
        btnY = node.getPos().y || 0;
        btnWidth = node.getSize().x || 60;
        btnHeight = node.getSize().y || 28;
        btnValue = node['value'] || '';
        return SO_1.SO.create({
            x: btnX,
            y: btnY,
            width: 60,
            height: 28,
            items: [
                {
                    tag: 'rect',
                    rx: 8,
                    x: 0,
                    y: 0,
                    width: btnWidth,
                    height: btnHeight,
                    stroke: '#000',
                    filter: 'url(#drop-shadow)',
                    'class': 'SVGBtn'
                },
                { tag: 'text', $font: true, x: 10, y: 18, fill: 'black', value: btnValue, 'class': 'hand' }
            ]
        });
    };
    SymbolLibary.drawDropdown = function (node) {
        var btnX, btnY, btnWidth, btnHeight;
        btnX = node.getPos().x || 0;
        btnY = node.getPos().y || 0;
        btnWidth = node.getSize().x || 60;
        btnHeight = node.getSize().y || 28;
        return SO_1.SO.create({
            x: btnX,
            y: btnY,
            width: btnWidth,
            height: btnHeight,
            items: [
                {
                    tag: 'rect',
                    rx: 0,
                    x: 0,
                    y: 0,
                    width: btnWidth - 20,
                    height: btnHeight,
                    stroke: '#000',
                    fill: 'none'
                },
                {
                    tag: 'rect',
                    rx: 2,
                    x: btnWidth - 20,
                    y: 0,
                    width: 20,
                    height: 28,
                    stroke: '#000',
                    'class': 'SVGBtn'
                },
                {
                    tag: 'path',
                    style: 'fill:#000000;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1;fill-opacity:1',
                    d: 'm ' + (btnWidth - 15) + ',13 10,0 L ' + (btnWidth - 10) + ',20 z'
                }
            ]
        });
    };
    SymbolLibary.drawClassicon = function (node) {
        var btnX, btnY, btnWidth, btnHeight;
        btnX = node.getPos().x || 0;
        btnY = node.getPos().y || 0;
        btnWidth = node.getSize().x || 60;
        btnHeight = node.getSize().y || 28;
        return SO_1.SO.create({
            x: btnX,
            y: btnY,
            width: btnWidth,
            height: btnHeight,
            items: [
                {
                    tag: 'path',
                    d: 'm0,0l10.78832,0l0,4.49982l-10.78832,0.19999l0,9.19963l10.78832,0l0,-9.49962l-10.78832,0.19999l0,-4.59982z',
                    style: 'fill:none;stroke:#000000;'
                },
                {
                    tag: 'path',
                    d: 'm25.68807,0l10.78832,0l0,4.49982l-10.78832,0.19999l0,9.19963l10.78832,0l0,-9.49962l-10.78832,0.2l0,-4.59982z',
                    style: 'fill:none;stroke:#000000;'
                },
                { tag: 'line', x1: 11, y1: 7, x2: 25, y2: 7, stroke: '#000' }
            ]
        });
    };
    SymbolLibary.drawClassWithEdgeicon = function (node) {
        var btnX = 0, btnY = 0, btnWidth = 0, btnHeight = 0;
        return SO_1.SO.create({
            x: btnX,
            y: btnY,
            id: node['id'],
            width: btnWidth,
            height: btnHeight,
            items: [
                {
                    tag: 'path',
                    d: 'M2,10 20,10 20,35 2,35 Z M2,17 20,17 M20,10 28,5 28,9 M 28.5,4.7 24,4',
                    style: 'fill:none;stroke:#000000;transform:scale(0.8);'
                }
            ]
        });
    };
    SymbolLibary.drawClazz = function (node) {
        var btnX = 0, btnY = 0, btnWidth = 0, btnHeight = 0;
        return SO_1.SO.create({
            x: btnX,
            y: btnY,
            id: node['id'],
            width: '100%',
            height: '100%',
            items: [
                { tag: 'rect', width: 50, height: 40, x: 0, y: 0, 'stroke-width': 2, stroke: 'black', fill: 'none' },
                { tag: 'rect', width: 50, height: 12, x: 0, y: 18, 'stroke-width': 1, stroke: 'black', fill: 'none' },
                { tag: 'text', x: 27, y: 14, 'text-anchor': 'middle', 'font-size': 11, value: 'Class' },
                { tag: 'text', x: 5, y: 24, 'font-size': 5, value: '+ field: type' },
                { tag: 'text', x: 5, y: 36, 'font-size': 5, value: '+ method(type)' }
            ]
        });
    };
    SymbolLibary.drawEdgeicon = function (node) {
        var btnX = 0, btnY = 0, btnWidth = 0, btnHeight = 0;
        return SO_1.SO.create({
            x: btnX,
            y: btnY,
            id: node['id'],
            width: btnWidth,
            height: btnHeight,
            items: [
                {
                    tag: 'path',
                    d: 'M10,30 30,10 M19,10 30,10 30,21',
                    style: 'fill:none;stroke:#555;stroke-width:2;'
                }
            ]
        });
    };
    SymbolLibary.drawCopynode = function (node) {
        var btnX = 0, btnY = 0, btnWidth = 0, btnHeight = 0;
        return SO_1.SO.create({
            x: btnX,
            y: btnY,
            id: node['id'],
            width: btnWidth,
            height: btnHeight,
            items: [
                {
                    tag: 'path',
                    d: 'M5 5 L15 5 L15 20 L5 20 Z M5 10 L15 10',
                    style: 'fill:white;stroke:#000;stroke-width: 1;',
                    transform: 'scale(1.3) translate(4 4)'
                },
                {
                    tag: 'path',
                    d: 'M8 2 L18 2 L18 17 L8 17 Z M8 7 L18 7'
                        + 'M11 4.5 L15 4.5 '
                        + 'M10 10 L16 10 '
                        + 'M10 13 L16 13 ',
                    style: 'fill:white;stroke:#000;stroke-width: 1;',
                    transform: 'scale(1.3) translate(4 4)'
                }
            ]
        });
    };
    SymbolLibary.drawBasket = function (node) {
        var btnX = 0, btnY = 0, btnWidth = 0, btnHeight = 0;
        return SO_1.SO.create({
            x: btnX,
            y: btnY,
            id: node['id'],
            background: node['brackground'] || false,
            width: btnWidth,
            height: btnHeight,
            items: [
                {
                    tag: 'path',
                    d: 'M12 12 L18 12 L18 11 L22 11 L22 12 L28 12 L28 14 L27 14 L27 29 L13 29 L13 14 L12 14 Z M13 14 L27 14 M20 17 L20 26 M17 16 L17 27 M23 16 L23 27',
                    style: 'fill:white;stroke:#000;stroke-width: 1;'
                }
            ]
        });
    };
    SymbolLibary.drawPencil = function (node) {
        var btnX = 0, btnY = 0, btnWidth = 0, btnHeight = 0;
        return SO_1.SO.create({
            x: btnX,
            y: btnY,
            id: node['id'],
            background: node['brackground'] || false,
            width: btnWidth,
            height: btnHeight,
            items: [
                {
                    tag: 'path',
                    d: 'M6 20 L12 23 L33 23 L33 17 L12 17 Z M30 17 L30 23 M12 17 L12 23 M15 19 L28 19 M15 21 L28 21',
                    stroke: '#000',
                    'stroke-width': 1,
                    fill: 'white'
                }
            ]
        });
    };
    SymbolLibary.addText = function (y, offsetX, box, text, textClass) {
        var item;
        box.appendChild(util_1.Util.create({
            tag: 'text',
            $font: true,
            'text-anchor': 'left',
            width: 70,
            x: 10,
            y: y,
            class: textClass,
            value: text,
            eventValue: text
        }));
        if (textClass === 'SVGTEXT') {
            textClass = 'SVGChoiceText';
        }
        else {
            textClass = 'SVGChoice';
        }
        item = util_1.Util.create({
            tag: 'rect',
            rx: 0,
            x: offsetX,
            y: y - 18,
            width: 70,
            height: 24,
            stroke: 'none',
            class: textClass,
        });
        item['eventValue'] = text;
        box.appendChild(item);
        return item;
    };
    SymbolLibary.prototype.getToolBarIcon = function () {
        return null;
    };
    return SymbolLibary;
}());
exports.SymbolLibary = SymbolLibary;


/***/ }),

/***/ "./elements/nodes/Table.ts":
/*!*********************************!*\
  !*** ./elements/nodes/Table.ts ***!
  \*********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var Control_1 = __webpack_require__(/*! ../../Control */ "./Control.ts");
var BridgeElement_1 = __webpack_require__(/*! ../../BridgeElement */ "./BridgeElement.ts");
var Data_1 = __webpack_require__(/*! ../../Data */ "./Data.ts");
var util_1 = __webpack_require__(/*! ../../util */ "./util.ts");
var Table = (function (_super) {
    __extends(Table, _super);
    function Table() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.items = [];
        _this.columns = [];
        _this.cells = {};
        _this.showedItems = [];
        _this.itemsIds = {};
        _this.searchColumns = [];
        _this.searchText = [];
        _this.moveElement = null;
        _this.isDrag = false;
        return _this;
    }
    Object.defineProperty(Table.prototype, "lastProperty", {
        get: function () {
            return this.property.split('.')[0];
        },
        enumerable: true,
        configurable: true
    });
    Table.indexOfChild = function (item) {
        var i = 0;
        var child = item.gui;
        while ((child = child.previousSibling) !== null) {
            i++;
        }
        return i;
    };
    Table.prototype.load = function (data) {
        var id;
        if (typeof (data) === 'string') {
            id = data;
        }
        else {
            id = data.id;
            if (data.property) {
                this.property = data.property;
            }
            if (data.searchColumns) {
                var search_1 = [];
                if (typeof (data.searchColumns) === 'string') {
                    search_1 = data.searchColumns.split(' ');
                }
                else {
                    search_1 = data.searchColumns;
                }
                for (var z = 0; z < search_1.length; z++) {
                    var item = search_1[z].trim();
                    if (item.length > 0) {
                        if (this.searchColumns.indexOf(item) < 0) {
                            this.searchColumns.push(item);
                        }
                    }
                }
            }
        }
        if (!id) {
            return;
        }
        if (this.$view) {
            if (data['columns']) {
                for (var i in data['columns']) {
                    if (data['columns'].hasOwnProperty(i) === false) {
                        continue;
                    }
                    var col = this.parseData(data['columns'][i]);
                    this.addHeaderInfo(col);
                    this.columns.push(col);
                    this.tableOption.parentElement.insertBefore(col.$element, this.tableOption);
                }
                for (var i in this.showedItems) {
                    var item = this.showedItems[i];
                    var cell_1 = void 0;
                    while (item.gui.children.length < this.columns.length) {
                        cell_1 = document.createElement('td');
                        item.gui.appendChild(cell_1);
                    }
                    while (item.gui.children.length > this.columns.length) {
                        item.gui.removeChild(item.gui.children[item.gui.children.length - 1]);
                    }
                    for (var c = 0; c < this.columns.length; c++) {
                        var name_1 = this.columns[c].attribute;
                        cell_1 = item.gui.children[c];
                        cell_1.innerHTML = item.model.getValue(name_1);
                    }
                }
            }
            return;
        }
        this.$view = document.getElementById(id);
        var headerrow;
        if (this.$view) {
            if (!this.property) {
                this.property = this.$view.getAttribute('property');
            }
        }
        else {
            this.$view = document.createElement('table');
            this.$owner.appendChild(this);
        }
        if (!this.$bodysection) {
            this.$bodysection = document.createElement('tbody');
            this.$view.appendChild(this.$bodysection);
        }
        if (data['classname']) {
            this.$view.className = data['classname'];
        }
        else {
            this.$view.className = 'mdl-data-table mdl-js-data-table mdl-data-table--selectable mdl-shadow--2dp';
        }
        this.id = id;
        this.$view.id = id;
        this.$view.setAttribute('type', this.constructor['name'].toLowerCase());
        var counter = 0;
        for (var c = 0; c < this.$view.children.length; c++) {
            var row = this.$view.children[c];
            if (row instanceof HTMLTableSectionElement) {
                if (row.tagName === 'THEAD') {
                    headerrow = row;
                    var children = row.children;
                    for (var i = 0; i < children.length; i++) {
                        this.parsingHeader(row.children[i]);
                    }
                }
                else {
                    var children = row.children;
                    for (var i = 0; i < children.length; i++) {
                        this.parsingData(row.children[i]);
                    }
                }
            }
            else {
                if (counter === 0) {
                    headerrow = row;
                    this.parsingHeader(row);
                }
                else {
                    this.parsingData(row);
                }
            }
            counter++;
        }
        if (!headerrow || !this.$headersection) {
            if (!this.$headersection) {
                var header = this.$view.getElementsByTagName('thead');
                if (header.length === 0) {
                    this.$headersection = document.createElement('thead');
                    this.$view.appendChild(this.$headersection);
                }
                else {
                    this.$headersection = header.item(0);
                }
            }
            if (!headerrow) {
                headerrow = document.createElement('tr');
                this.$headersection.appendChild(headerrow);
            }
        }
        if (data['columns']) {
            for (var i in data['columns']) {
                if (data['columns'].hasOwnProperty(i) === false) {
                    continue;
                }
                var col = this.parseData(data['columns'][i]);
                this.addHeaderInfo(col);
                this.columns.push(col);
                headerrow.appendChild(col.$element);
            }
            this.tableOption = document.createElement('th');
            this.tableOption.classList.add('tableOption');
            headerrow.appendChild(this.tableOption);
            var context = this.addOptionItem(null, this.tableOption, true);
            var contentChild = this.addOptionItem('show', context, true);
            this.addOptionItem('show', contentChild, false);
        }
        this.registerEvents(['mousemove', 'mousedown', 'mouseup', 'resize', 'dragstart', 'dragover', 'drop', 'dragend']);
        var searchBar = document.createElement('tr');
        var cell = document.createElement('td');
        cell.setAttribute('colspan', '' + (this.columns.length));
        searchBar.appendChild(cell);
        var search = document.createElement('input');
        search.className = 'search';
        var that = this;
        search.addEventListener('keyup', function (evt) {
            that.search(evt.target['value']);
        });
        cell.appendChild(search);
        if (this.resultColumn) {
            if (this.resultColumn.indexOf('#') !== 0) {
                this.countElement = document.createElement('div');
                searchBar.appendChild(this.countElement);
            }
            else {
                for (var z = 0; z < this.$headersection.children.length; z++) {
                    if (this.$headersection.children[z].innerHTML === this.resultColumn) {
                        this.countColumn = this.$headersection.children[z];
                        this.countColumnPos = z;
                        break;
                    }
                }
            }
        }
        var first = this.$headersection.children.item(0);
        this.$headersection.insertBefore(searchBar, first);
        this.updateElement(this.property, null);
    };
    Table.prototype.tableEvent = function (type, e) {
        var button = 0;
        var eventX = 0;
        if (e instanceof MouseEvent) {
            button = e.buttons;
            eventX = e.pageX;
        }
        if (type === 'mouseup') {
            this.moveElement = null;
        }
        else if (type === 'mousedown' || type === 'resize') {
            this.moveElement = null;
            if (button === 1) {
                var c = void 0;
                for (c = 0; c < this.columns.length; c++) {
                    if (this.columns[c].$resize === e.target) {
                        this.moveElement = this.columns[c];
                        this.movePos = c;
                        this.isDrag = false;
                        break;
                    }
                    else if (this.columns[c].$element === e.target) {
                        this.moveElement = this.columns[c];
                        this.movePos = c;
                        this.isDrag = true;
                    }
                }
                this.moveTimeStamp = e.timeStamp;
                this.moveX = eventX;
            }
            else {
                this.moveTimeStamp = 0;
            }
        }
        else if (type === 'mousemove') {
            if (button === 1 && this.moveElement) {
                if (this.moveTimeStamp && e.timeStamp - this.moveTimeStamp < 2000) {
                    if (this.isDrag) {
                    }
                    else {
                        var x = eventX - this.moveX;
                        var width = this.moveElement.$element.offsetWidth;
                        this.moveElement.$element.width = '' + (width + x);
                        e.stopPropagation();
                    }
                }
                this.moveX = eventX;
                this.moveTimeStamp = e.timeStamp;
            }
        }
        else if (this.isDrag) {
            this.columnDragEvent(type, e);
        }
    };
    Table.prototype.parsingHeader = function (row) {
        for (var i in row.children) {
            if (row.children.hasOwnProperty(i) === false) {
                continue;
            }
            var column = row.children[i];
            var id = column.innerHTML.trim();
            var col = null;
            for (var c in this.columns) {
                if (this.columns.hasOwnProperty(i) === false) {
                    continue;
                }
                if (this.columns[c].label === id) {
                    col = this.columns[c];
                    col.$element = column;
                    break;
                }
            }
            if (col === null) {
                col = new Column();
                col.label = id;
                col.attribute = column.getAttribute('attribute');
                col.$element = column;
                this.columns.push(col);
            }
            this.addHeaderInfo(col);
        }
    };
    Table.prototype.parsingData = function (row) {
        var id = row.getAttribute('id');
        var item = this.$owner.getItem(id);
        for (var i in row.children) {
            if (row.children.hasOwnProperty(i) === false) {
                continue;
            }
            var cell = row.children[i];
            var colAttribute = this.columns[i].attribute;
            if (colAttribute.indexOf('\.') < 0) {
                item[colAttribute] = cell.innerHTML.trim();
            }
        }
    };
    Table.prototype.propertyChange = function (entity, property, oldValue, newValue) {
        if (entity) {
            if (this.property && !entity.hasProperty(property)) {
                return;
            }
        }
        if (entity.id === undefined) {
            return;
        }
        var item = this.itemsIds[entity.id];
        var row;
        if (!item) {
            item = new BridgeElement_1.default(entity);
            this.items.push(item);
            this.itemsIds[entity.id] = item;
        }
        row = this.cells[entity.id];
        if (row) {
            item.gui = row;
        }
        if (this.searching(item) === false) {
            return;
        }
        var cell;
        var showItem = false;
        if (!row) {
            showItem = true;
            row = document.createElement('tr');
            var count = this.columns.length;
            for (var i = 0; i < count; i++) {
                cell = document.createElement('td');
                row.appendChild(cell);
            }
            this.cells[entity.id] = row;
            item.gui = row;
        }
        for (var c = 0; c < this.columns.length; c++) {
            var name_2 = this.columns[c].attribute;
            if (name_2 === property) {
                cell = row.children[c];
                cell.innerHTML = newValue;
            }
        }
        if (showItem) {
            this.showItem(item, true);
        }
    };
    Table.prototype.sort = function (column) {
        if (this.sortColumn === column) {
            if (this.direction === 1) {
                this.direction = -1;
                column.$element.classList.remove('asc');
                column.$element.classList.add('desc');
            }
            else {
                this.direction = 1;
                column.$element.classList.remove('desc');
                column.$element.classList.add('asc');
            }
        }
        else {
            if (this.sortColumn !== null) {
                this.sortColumn.$element.classList.remove('desc');
                this.sortColumn.$element.classList.remove('asc');
            }
            this.sortColumn = column;
            this.sortColumn.$element.classList.add('asc');
            this.direction = 1;
        }
        var that = this;
        var sort = function (a, b) {
            return that.sorting(a, b);
        };
        this.showedItems.sort(sort);
        var len = this.showedItems.length;
        var body = this.$bodysection;
        var i = 0;
        while (i < len) {
            var item = this.showedItems[i];
            if (i !== Table.indexOfChild(item)) {
                break;
            }
            i = i + 1;
        }
        while (body.children.length > i) {
            body.removeChild(body.children.item(body.children.length - 1));
        }
        while (i < len) {
            var item = this.showedItems[i];
            body.appendChild(item.gui);
            i = i + 1;
        }
    };
    Table.prototype.sorting = function (a, b) {
        var path = this.sortColumn.attribute.split('.');
        var itemA = a.model.prop;
        var itemB = b.model.prop;
        var check = this.sortColumn.attribute;
        for (var p = 0; p < path.length; p++) {
            check = path[p];
            if (itemA[check]) {
                itemA = itemA[check];
            }
            else {
                return 0;
            }
            if (itemB[check]) {
                itemB = itemB[check];
            }
            else {
                return 0;
            }
        }
        if (itemA !== itemB) {
            if (this.direction === 1) {
                return (itemA < itemB) ? -1 : 1;
            }
            return (itemA < itemB) ? 1 : -1;
        }
        return 0;
    };
    Table.prototype.search = function (origSearchText) {
        if (!origSearchText) {
            origSearchText = '';
        }
        var searchText = origSearchText.trim().toLowerCase();
        if (searchText === this.lastSearchText && searchText !== '') {
            return;
        }
        var oldSearch = this.lastSearchText;
        this.lastSearchText = searchText;
        this.parseSearchArray();
        if (searchText !== '' && oldSearch !== null && searchText.indexOf(oldSearch) >= 0 && searchText.indexOf('|') < 0) {
            this.searchArray(this.showedItems);
        }
        else {
            this.searchSet(this.items);
        }
        this.refreshCounter();
    };
    Table.prototype.refreshCounter = function () {
        if (this.countColumn) {
            this.countColumn.innerHTML = this.columns[this.countColumnPos].label + ' (' + this.showedItems.length + ')';
        }
    };
    Table.prototype.parseSearchArray = function () {
        var pos = 0;
        var split = [];
        var quote = false;
        for (var i = 0; i < this.lastSearchText.length; i++) {
            if ((this.lastSearchText.charAt(i) === ' ') && !quote) {
                var txt = this.lastSearchText.substring(pos, i).trim();
                if (txt.length > 0) {
                    split.push(txt);
                }
                pos = i + 1;
            }
            else if (this.lastSearchText.charAt(i) === '\"') {
                if (quote) {
                    var txt = this.lastSearchText.substring(pos, i).trim();
                    if (txt.length > 0) {
                        split.push(txt);
                    }
                    pos = i + 1;
                }
                else {
                    pos = i + 1;
                }
                quote = !quote;
            }
        }
        if (pos < this.lastSearchText.length) {
            split.push(this.lastSearchText.substring(pos, this.lastSearchText.length).trim());
        }
        this.searchText = split;
        return split;
    };
    Table.prototype.searchArray = function (root) {
        this.showedItems = [];
        for (var i = 0; i < root.length; i++) {
            var item = root[i];
            this.showItem(item, this.searching(item));
        }
    };
    Table.prototype.searchSet = function (root) {
        this.showedItems = [];
        for (var _i = 0, root_1 = root; _i < root_1.length; _i++) {
            var item = root_1[_i];
            var child = item;
            this.showItem(child, this.searching(child));
        }
    };
    Table.prototype.showItem = function (item, visible) {
        if (visible) {
            this.showedItems.push(item);
            this.$bodysection.appendChild(item.gui);
        }
        else if (item.gui && item.gui.parentElement) {
            this.$bodysection.removeChild(item.gui);
        }
    };
    Table.prototype.searching = function (item) {
        var fullText = '';
        for (var i = 0; i < this.searchColumns.length; i++) {
            fullText = fullText + ' ' + item.model.getValue(this.searchColumns[i]);
        }
        fullText = fullText.trim().toLowerCase();
        for (var z = 0; z < this.searchText.length; z++) {
            if ('' !== this.searchText[z]) {
                var orSplit = void 0;
                if (this.searchText[z].indexOf('|') > 0) {
                    orSplit = this.searchText[z].split('|');
                }
                else {
                    orSplit = [this.searchText[z]];
                }
                var o = 0;
                for (; o < orSplit.length; o++) {
                    var pos = orSplit[o].indexOf(':');
                    if (orSplit[o].indexOf('#') === 0 && pos > 1) {
                        var value = orSplit[o].substring(pos + 1);
                        var column = orSplit[o].substring(1, pos - 1);
                        var dataValue = item.model.getValue(column);
                        if (dataValue) {
                            if (dataValue.toString().toLowerCase().indexOf(value) >= 0) {
                                break;
                            }
                        }
                    }
                    else if (orSplit[o].length > 1 && orSplit[o].indexOf('-') === 0) {
                        if (fullText.indexOf(orSplit[o].substring(1)) < 0) {
                            break;
                        }
                    }
                    else if (fullText.indexOf(orSplit[o]) >= 0) {
                        break;
                    }
                }
                if (o === orSplit.length) {
                    return false;
                }
            }
        }
        return true;
    };
    Table.prototype.getColumn = function () {
        return this.columns;
    };
    Table.prototype.updateElement = function (property, value) {
        for (var _i = 0, _a = this.items; _i < _a.length; _i++) {
            var item = _a[_i];
            if (item instanceof BridgeElement_1.default) {
                item.model.removeListener(this);
            }
        }
        this.items = [];
        this.itemsIds = {};
        if (this.property !== undefined) {
            var items = this.$owner.getItems();
            for (var j in items) {
                if (items.hasOwnProperty(j)) {
                    var item = items[j];
                    if (item instanceof Data_1.default) {
                        if (property === j) {
                            var i = new BridgeElement_1.default(item);
                            this.items.push(i);
                            this.itemsIds[item.id] = i;
                        }
                    }
                }
            }
            this.redrawAllElements();
        }
    };
    Table.prototype.setValue = function (object, attribute, newValue, oldValue) {
        if (this.$owner !== null) {
            return this.getRoot().setValue(object, attribute, newValue, oldValue);
        }
        return _super.prototype.setValue.call(this, object, attribute, newValue, oldValue);
    };
    Table.prototype.redrawAllElements = function () {
        var children = this.$bodysection.children;
        for (var i = 0; i < children.length; i++) {
            var child = children.item(i);
            this.$bodysection.removeChild(child);
        }
        for (var _i = 0, _a = this.items; _i < _a.length; _i++) {
            var obj = _a[_i];
            obj.model.addListener(this);
            var row = this.createRow(obj);
            this.$bodysection.appendChild(row);
            this.cells[obj.id] = row;
        }
    };
    Table.prototype.parseData = function (column) {
        var col = new Column();
        col.label = column.label || column.id;
        col.attribute = column.attribute || column.label || column.id;
        col.$element = document.createElement('th');
        col.$element.innerHTML = col.label;
        col.$element.draggable = true;
        col.$resize = document.createElement('div');
        col.$resize.classList.add('resize');
        col.$element.appendChild(col.$resize);
        return col;
    };
    Table.prototype.createRow = function (data) {
        var tr = document.createElement('tr');
        for (var _i = 0, _a = this.columns; _i < _a.length; _i++) {
            var id = _a[_i];
            var td = document.createElement('td');
            tr.appendChild(td);
            td.innerHTML = data.model.getValue(id.attribute);
        }
        return tr;
    };
    Table.prototype.addHeaderInfo = function (col) {
        var element = col.$element;
        var that = this;
        element.classList.add('sort');
        element.addEventListener('click', function () {
            that.sort(col);
        }, false);
    };
    Table.prototype.columnDragEvent = function (type, e) {
        if (type === 'dragstart') {
            this.moveElement.$element.style.opacity = '0.4';
            e.dataTransfer.effectAllowed = 'move';
            e.dataTransfer.setData('text/json', JSON.stringify(util_1.Util.toJson(this.moveElement)));
        }
        else if (type === 'dragenter') {
        }
        else if (type === 'dragleave') {
            this.moveElement.$element.classList.remove('over');
        }
        else if (type === 'dragover') {
            if (e.preventDefault) {
                e.preventDefault();
            }
            for (var c = 0; c < this.columns.length; c++) {
                if (this.columns[c].$element === e.target) {
                    this.dragColumn = this.columns[c];
                    this.dragPos = c;
                    this.columns[c].$element.classList.add('over');
                }
                else {
                    this.columns[c].$element.classList.remove('over');
                }
            }
            if (e.target === this.tableOption) {
                this.tableOption.classList.add('over');
                this.dragPos = this.columns.length;
            }
            else {
                this.tableOption.classList.remove('over');
            }
            e.dataTransfer.dropEffect = 'move';
        }
        else if (type === 'drop') {
            if (e.stopPropagation) {
                e.stopPropagation();
            }
            if (this.movePos === this.dragPos) {
                return;
            }
            this.columns.splice(this.movePos, 1);
            if (this.movePos >= this.dragPos) {
                this.columns.splice(this.dragPos, 0, this.moveElement);
            }
            else {
                this.columns.splice(this.dragPos - 1, 0, this.moveElement);
            }
            var line = this.moveElement.$element.parentElement;
            line.removeChild(this.moveElement.$element);
            if (this.dragPos < this.columns.length) {
                line.insertBefore(this.moveElement.$element, this.dragColumn.$element);
            }
            else {
                line.insertBefore(this.moveElement.$element, this.tableOption);
            }
            var oldElement = void 0;
            var newElement = void 0;
            for (var i = 0; i < this.$bodysection.children.length; i++) {
                line = this.$bodysection.children.item(i);
                oldElement = line.children.item(this.movePos);
                newElement = line.children.item(this.dragPos);
                line.removeChild(oldElement);
                line.insertBefore(oldElement, newElement);
            }
        }
        else if (type === 'dragend') {
            this.moveElement.$element.style.opacity = '1';
            for (var i = 0; i < this.columns.length; i++) {
                this.columns[i].$element.classList.remove('over');
            }
            this.tableOption.classList.remove('over');
        }
    };
    Table.prototype.addOptionItem = function (label, parent, sub) {
        var labelControl;
        if (label) {
            labelControl = document.createElement('a');
            labelControl.appendChild(document.createTextNode(label));
            labelControl.href = 'javascript:void(0);';
            parent.appendChild(labelControl);
        }
        if (sub) {
            var context_1 = document.createElement('div');
            context_1.classList.add('dropdown-content');
            context_1.style.setProperty('position', 'absolute');
            parent.appendChild(context_1);
            parent.addEventListener('click', function () {
                context_1.classList.toggle('show');
            }, false);
            return context_1;
        }
        return labelControl;
    };
    Table.prototype.registerEvents = function (events) {
        var that = this;
        var _loop_1 = function (i) {
            this_1.$view.addEventListener(events[i], function (evt) {
                return that.tableEvent(events[i], evt);
            });
        };
        var this_1 = this;
        for (var i = 0; i < events.length; i++) {
            _loop_1(i);
        }
    };
    return Table;
}(Control_1.Control));
exports.Table = Table;
var Column = (function () {
    function Column() {
    }
    return Column;
}());


/***/ }),

/***/ "./elements/nodes/index.ts":
/*!*********************************!*\
  !*** ./elements/nodes/index.ts ***!
  \*********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

function __export(m) {
    for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
}
Object.defineProperty(exports, "__esModule", { value: true });
var AutoComplete_1 = __webpack_require__(/*! ./AutoComplete */ "./elements/nodes/AutoComplete.ts");
var BR_1 = __webpack_require__(/*! ./BR */ "./elements/nodes/BR.ts");
var Div_1 = __webpack_require__(/*! ./Div */ "./elements/nodes/Div.ts");
var Label_1 = __webpack_require__(/*! ./Label */ "./elements/nodes/Label.ts");
__export(__webpack_require__(/*! ./Node */ "./elements/nodes/Node.ts"));
__export(__webpack_require__(/*! ./Clazz */ "./elements/nodes/Clazz.ts"));
__export(__webpack_require__(/*! ./SO */ "./elements/nodes/SO.ts"));
__export(__webpack_require__(/*! ./Symbol */ "./elements/nodes/Symbol.ts"));
__export(__webpack_require__(/*! ./BR */ "./elements/nodes/BR.ts"));
__export(__webpack_require__(/*! ./Button */ "./elements/nodes/Button.ts"));
__export(__webpack_require__(/*! ./Div */ "./elements/nodes/Div.ts"));
__export(__webpack_require__(/*! ./Form */ "./elements/nodes/Form.ts"));
__export(__webpack_require__(/*! ./Input */ "./elements/nodes/Input.ts"));
__export(__webpack_require__(/*! ./Label */ "./elements/nodes/Label.ts"));
__export(__webpack_require__(/*! ./Table */ "./elements/nodes/Table.ts"));
__export(__webpack_require__(/*! ./HTML */ "./elements/nodes/HTML.ts"));
__export(__webpack_require__(/*! ./Dice */ "./elements/nodes/Dice.ts"));
__export(__webpack_require__(/*! ./AutoComplete */ "./elements/nodes/AutoComplete.ts"));
__export(__webpack_require__(/*! ./Attribute */ "./elements/nodes/Attribute.ts"));
__export(__webpack_require__(/*! ./Method */ "./elements/nodes/Method.ts"));
__export(__webpack_require__(/*! ./ClazzProperty */ "./elements/nodes/ClazzProperty.ts"));
new AutoComplete_1.AutoComplete();
new BR_1.BR();
new Div_1.Div();
new Label_1.Label();


/***/ }),

/***/ "./handlers/AddNode.ts":
/*!*****************************!*\
  !*** ./handlers/AddNode.ts ***!
  \*****************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var EventBus_1 = __webpack_require__(/*! ../EventBus */ "./EventBus.ts");
var util_1 = __webpack_require__(/*! ../util */ "./util.ts");
var AddNode = (function () {
    function AddNode(graph) {
        this.MIN_SIZE_TO_ADD_NODE = 30;
        this.MIN_SIZE_TO_ADD_TEXT = 10;
        this.graph = graph;
    }
    AddNode.prototype.canHandle = function () {
        return EventBus_1.EventBus.isHandlerActiveOrFree(AddNode.name);
    };
    AddNode.prototype.setActive = function (active) {
        if (active) {
            EventBus_1.EventBus.setActiveHandler(AddNode.name);
        }
        else {
            EventBus_1.EventBus.releaseActiveHandler();
        }
    };
    AddNode.prototype.handle = function (event, element) {
        if (!this.canHandle()) {
            return true;
        }
        if (element.id !== 'RootElement') {
            return false;
        }
        switch (event.type) {
            case 'mousedown':
                if (element.id === 'RootElement') {
                    this.start(event, element);
                    this.setActive(true);
                }
                break;
            case 'mousemove':
                this.drawRect(event, element);
                break;
            case 'mouseleave':
                this.removeRect();
                this.setActive(false);
                break;
            case 'mouseup':
                this.addNode();
                this.setActive(false);
                break;
            default:
                break;
        }
        return true;
    };
    AddNode.prototype.drawRect = function (evt, element) {
        if (!this.isRectDrawing) {
            return;
        }
        evt.stopPropagation();
        var width = util_1.Util.getEventX(evt) - this.x;
        var height = util_1.Util.getEventY(evt) - this.y;
        if (width < 0) {
            this.isDrawToLeft = true;
            width *= -1;
        }
        else {
            this.isDrawToLeft = false;
        }
        if (height < 0) {
            this.isDrawToTop = true;
            height *= -1;
        }
        else {
            this.isDrawToTop = false;
        }
        if (width > this.MIN_SIZE_TO_ADD_NODE && height > this.MIN_SIZE_TO_ADD_NODE) {
            this.isBigEnoughForAddNode = true;
        }
        else {
            this.isBigEnoughForAddNode = false;
        }
        this.graph.root.style.cursor = 'pointer';
        if (!this.svgRect) {
            var rectAddNode = util_1.Util.createShape({
                tag: 'rect',
                id: 'addNodeRect',
                x: this.x,
                y: this.y,
                width: 1,
                height: 1,
                class: 'SVGAddNode'
            });
            var group = util_1.Util.createShape({ tag: 'g', id: 'groupAddNode' });
            group.appendChild(rectAddNode);
            this.graph.root.appendChild(group);
            this.svgRect = rectAddNode;
            this.svgGroupAddNode = group;
        }
        else {
            var svgRectBBox = this.svgRect.getBBox();
            if ((svgRectBBox.width > this.MIN_SIZE_TO_ADD_TEXT
                || svgRectBBox.height > this.MIN_SIZE_TO_ADD_TEXT) && !this.svgTextAddNode) {
                var textAddNode = util_1.Util.createShape({
                    tag: 'text',
                    x: this.x,
                    y: this.y - 5,
                    'font-family': 'Verdana',
                    'font-size': 12,
                    fill: 'black'
                });
                textAddNode.textContent = 'Hold on and move to create a new class';
                this.svgGroupAddNode.appendChild(textAddNode);
                var sizeClientRect = textAddNode.getBoundingClientRect();
                var rectBackgroundForText = util_1.Util.createShape({
                    tag: 'rect',
                    x: this.x,
                    y: this.y - sizeClientRect.height,
                    width: sizeClientRect.width,
                    height: sizeClientRect.height,
                    fill: '#DDD',
                    'stroke-width': 0
                });
                this.svgTextRectAddNode = rectBackgroundForText;
                this.svgTextAddNode = textAddNode;
                this.svgGroupAddNode.appendChild(rectBackgroundForText);
                this.svgGroupAddNode.appendChild(textAddNode);
            }
            if (this.isDrawToLeft) {
                this.svgRect.setAttributeNS(null, 'x', '' + util_1.Util.getEventX(evt));
            }
            if (this.isDrawToTop) {
                this.svgRect.setAttributeNS(null, 'y', '' + util_1.Util.getEventY(evt));
            }
            this.svgRect.setAttributeNS(null, 'width', width.toString());
            this.svgRect.setAttributeNS(null, 'height', height.toString());
            if (this.isBigEnoughForAddNode) {
                this.svgRect.setAttributeNS(null, 'class', 'SVGAddNode-ready');
            }
            else {
                this.svgRect.setAttributeNS(null, 'class', 'SVGAddNode');
            }
        }
    };
    AddNode.prototype.removeRect = function () {
        this.isRectDrawing = false;
        this.isBigEnoughForAddNode = false;
        this.graph.root.style.cursor = 'default';
        if (this.svgGroupAddNode) {
            this.graph.root.removeChild(this.svgGroupAddNode);
            this.svgGroupAddNode = undefined;
        }
        if (this.svgRect) {
            this.svgRect = undefined;
        }
        if (this.svgTextAddNode) {
            this.svgTextAddNode = undefined;
        }
        if (this.svgTextRectAddNode) {
            this.svgTextRectAddNode = undefined;
        }
    };
    AddNode.prototype.addNode = function () {
        if (!this.isBigEnoughForAddNode) {
            this.removeRect();
            return;
        }
        this.removeRect();
        var node = this.graph.addElementWithValues('Clazz', { x: this.x, y: this.y });
        this.graph.drawElement(node);
    };
    AddNode.prototype.start = function (evt, element) {
        if (this.isRectDrawing) {
            return;
        }
        this.isRectDrawing = true;
        this.x = util_1.Util.getEventX(evt);
        this.y = util_1.Util.getEventY(evt);
    };
    return AddNode;
}());
exports.AddNode = AddNode;


/***/ }),

/***/ "./handlers/Drag.ts":
/*!**************************!*\
  !*** ./handlers/Drag.ts ***!
  \**************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var EventBus_1 = __webpack_require__(/*! ../EventBus */ "./EventBus.ts");
var BaseElements_1 = __webpack_require__(/*! ../elements/BaseElements */ "./elements/BaseElements.ts");
var nodes_1 = __webpack_require__(/*! ../elements/nodes */ "./elements/nodes/index.ts");
var util_1 = __webpack_require__(/*! ../util */ "./util.ts");
var Drag = (function () {
    function Drag(graph) {
        this.dragging = false;
        this.reinsert = false;
        this.mouseOffset = new BaseElements_1.Point();
        this.graph = graph;
    }
    Drag.prototype.handle = function (event, element) {
        if (!this.canHandle()) {
            return true;
        }
        switch (event.type) {
            case 'mousedown':
                if ((!this.dragging) && (element.id !== 'RootElement')) {
                    this.element = element;
                    this.svgElement = element.$view;
                    this.start(event, element);
                    this.setActive(true);
                }
                break;
            case 'mouseup':
                if (this.dragging) {
                    this.reset();
                }
                this.setActive(false);
                break;
            case 'mousemove':
                if (this.dragging) {
                    this.drag(event, element);
                }
                break;
            case 'mouseleave':
                if (this.dragging) {
                    this.reset();
                }
                this.setActive(false);
                break;
            default:
                break;
        }
        return true;
    };
    Drag.prototype.canHandle = function () {
        return EventBus_1.EventBus.isHandlerActiveOrFree(Drag.name);
    };
    Drag.prototype.setActive = function (active) {
        if (active) {
            EventBus_1.EventBus.setActiveHandler(Drag.name);
        }
        else {
            EventBus_1.EventBus.releaseActiveHandler();
        }
    };
    Drag.prototype.reset = function () {
        this.dragging = false;
        this.svgElement.style.cursor = 'pointer';
        if (util_1.Util.isChrome()) {
            var clickEvt = util_1.Util.createCustomEvent('click');
            this.svgElement.dispatchEvent(clickEvt);
        }
    };
    Drag.prototype.start = function (evt, element) {
        this.dragging = true;
        this.mouseOffset.x = evt.clientX;
        this.mouseOffset.y = evt.clientY;
        this.reinsert = true;
        this.svgElement.style.cursor = 'move';
    };
    Drag.prototype.drag = function (evt, element) {
        if (this.reinsert) {
            if (this.element.id !== 'RootElement') {
                this.graph.root.appendChild(this.svgElement);
            }
            var dragEvent = util_1.Util.createCustomEvent('drag');
            element.$view.dispatchEvent(dragEvent);
        }
        this.reinsert = false;
        evt.stopPropagation();
        var translation = this.svgElement.getAttributeNS(null, 'transform').slice(10, -1).split(' ');
        var sx = parseInt(translation[0]);
        var sy = 0;
        if (translation.length > 1) {
            sy = parseInt(translation[1]);
        }
        var transX = sx + evt.clientX - this.mouseOffset.x;
        var transY = sy + evt.clientY - this.mouseOffset.y;
        this.svgElement.setAttributeNS(null, 'transform', 'translate(' + transX + ' ' + transY + ')');
        this.element.getPos().addNum(transX - sx, transY - sy);
        if (this.element instanceof nodes_1.Node) {
            this.element.redrawEdges();
        }
        this.mouseOffset.x = evt.clientX;
        this.mouseOffset.y = evt.clientY;
        var maxX = this.element.getPos().x + this.element.getSize().x;
        var maxY = this.element.getPos().y + this.element.getSize().y;
        var domRectRoot = this.graph.root.getBoundingClientRect();
        if (!domRectRoot) {
            return;
        }
        if (maxX > domRectRoot.width) {
            this.graph.root.setAttributeNS(null, 'width', '' + maxX);
        }
        if (maxY > domRectRoot.height) {
            this.graph.root.setAttributeNS(null, 'height', '' + maxY);
        }
    };
    return Drag;
}());
exports.Drag = Drag;


/***/ }),

/***/ "./handlers/ImportFile.ts":
/*!********************************!*\
  !*** ./handlers/ImportFile.ts ***!
  \********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var EventBus_1 = __webpack_require__(/*! ../EventBus */ "./EventBus.ts");
var util_1 = __webpack_require__(/*! ../util */ "./util.ts");
var ImportFile = (function () {
    function ImportFile(graph) {
        this.graph = graph;
    }
    ImportFile.prototype.setActive = function (active) {
        if (active) {
            EventBus_1.EventBus.setActiveHandler(ImportFile.name);
        }
        else {
            EventBus_1.EventBus.releaseActiveHandler();
        }
    };
    ImportFile.prototype.canHandle = function () {
        return EventBus_1.EventBus.isHandlerActiveOrFree(ImportFile.name);
    };
    ImportFile.prototype.handle = function (event, element) {
        var type = typeof event;
        if (type !== 'DragEvent') {
            return false;
        }
        var evt = event;
        if (evt.type === 'dragover') {
            this.handleDragOver(evt);
        }
        else if (evt.type === 'dragleave') {
            if (this.graph.$view !== evt.target) {
                return false;
            }
            this.setBoardStyle('dragleave');
        }
        else if (evt.type === 'drop') {
            this.handleLoadFile(evt);
        }
        return true;
    };
    ImportFile.prototype.setBoardStyle = function (typ) {
        var b = this.graph.$view;
        util_1.Util.removeClass(b, 'Error');
        util_1.Util.removeClass(b, 'Ok');
        util_1.Util.removeClass(b, 'Add');
        if (typ === 'dragleave') {
            if (b['errorText']) {
                b.removeChild(b['errorText']);
                b['errorText'] = null;
            }
            return true;
        }
        util_1.Util.addClass(b, typ);
        if (typ === 'Error') {
            if (!b['errorText']) {
                b['errorText'] = util_1.Util.create({ tag: 'div', style: 'margin-top: 30%', value: 'NO TEXTFILE' });
                b.appendChild(b['errorText']);
            }
            return true;
        }
        return false;
    };
    ImportFile.prototype.handleLoadFile = function (evt) {
        evt.stopPropagation();
        evt.preventDefault();
        var files = evt.dataTransfer.files;
        if (files.length > 1) {
            evt.dataTransfer.dropEffect = 'none';
            return;
        }
        var reader = new FileReader();
        var output = [];
        var htmlResult = '';
        var that = this;
        for (var i = 0, f = void 0; f = files[i]; i++) {
            reader.onload = function (event) {
                htmlResult = event.target['result'];
                console.log('fileContent: ' + htmlResult);
                if (that.graph) {
                    that.graph.import(htmlResult);
                }
            };
            reader.readAsText(f);
        }
        this.setBoardStyle('dragleave');
    };
    ImportFile.prototype.handleDragOver = function (evt) {
        var error = true, n, f;
        var files = evt.dataTransfer.files;
        if (files && files.length > 0) {
            for (var i = 0; i < files.length; i += 1) {
                f = files[i];
                if (f.type.indexOf('text') === 0) {
                    error = false;
                }
                else if (f.type === '') {
                    n = f.name.toLowerCase();
                    if (n.indexOf('json', n.length - 4) !== -1) {
                        error = false;
                    }
                }
            }
        }
        else {
            var items = evt.dataTransfer.items;
            if (items && items.length > 0) {
                for (var z = 0; z < items.length; z++) {
                    if (items[z].type === '' || items[z].type === 'text/plain') {
                        error = false;
                    }
                }
            }
            else {
                return;
            }
        }
        evt.dataTransfer.dropEffect = 'copy';
        if (error) {
            this.dragStyler(evt, 'Error');
        }
        else if (evt.ctrlKey) {
            this.dragStyler(evt, 'Add');
        }
        else {
            this.dragStyler(evt, 'Ok');
        }
    };
    ImportFile.prototype.dragStyler = function (event, typ) {
        event.stopPropagation();
        event.preventDefault();
        this.setBoardStyle(typ);
    };
    ImportFile.prototype.handleDragLeave = function (evt) {
        evt.stopPropagation();
        evt.preventDefault();
        evt.dataTransfer.dropEffect = 'link';
        evt.target['className'] = 'diagram';
        console.log('handDragLeave');
    };
    return ImportFile;
}());
exports.ImportFile = ImportFile;


/***/ }),

/***/ "./handlers/NewEdge.ts":
/*!*****************************!*\
  !*** ./handlers/NewEdge.ts ***!
  \*****************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var util_1 = __webpack_require__(/*! ../util */ "./util.ts");
var EventBus_1 = __webpack_require__(/*! ../EventBus */ "./EventBus.ts");
var index_1 = __webpack_require__(/*! ../elements/nodes/index */ "./elements/nodes/index.ts");
var NewEdge = (function () {
    function NewEdge(graph) {
        this.graph = graph;
    }
    NewEdge.prototype.setActive = function (active) {
        if (active) {
            EventBus_1.EventBus.setActiveHandler(NewEdge.name);
        }
        else {
            EventBus_1.EventBus.releaseActiveHandler();
        }
    };
    NewEdge.prototype.canHandle = function () {
        return EventBus_1.EventBus.isHandlerActiveOrFree(NewEdge.name);
    };
    NewEdge.prototype.handle = function (event, element) {
        if (!(event.ctrlKey || EventBus_1.EventBus.isHandlerActiveOrFree('NewEdge', true))) {
            this.removeLine();
            return true;
        }
        switch (event.type) {
            case 'mousedown':
                if (element instanceof index_1.Node) {
                    this.start(event, element);
                    this.setActive(true);
                }
                break;
            case 'mousemove':
                this.drawEdge(event, element);
                break;
            case 'mouseleave':
                this.setActive(false);
                break;
            case 'mouseup':
                this.setNewEdgeToNode(event);
                this.setActive(false);
                break;
            default: break;
        }
        return true;
    };
    NewEdge.prototype.drawEdge = function (evt, element) {
        if (!this.isEdgeDrawing) {
            return;
        }
        var lineToX = util_1.Util.getEventX(evt);
        var lineToy = util_1.Util.getEventY(evt);
        var path = "M" + this.x + " " + this.y + " L" + lineToX + " " + lineToy;
        if (!this.svgLine) {
            var attr = {
                tag: 'path',
                id: 'newEdgePath',
                d: path,
                class: 'SVGEdge'
            };
            var shape = util_1.Util.createShape(attr);
            this.svgLine = shape;
            this.graph.root.appendChild(shape);
            this.graph.root.appendChild(this.sourceNode.$view);
        }
        else {
            this.svgLine.setAttributeNS(null, 'd', path);
            var targetNode = this.graph.$graphModel.getNodeByPosition(util_1.Util.getEventX(evt), util_1.Util.getEventY(evt));
            if (targetNode) {
                if (this.lastTargetNode && this.lastTargetNode.id !== targetNode.id) {
                    this.lastTargetNode.$view.setAttributeNS(null, 'class', 'SVGClazz');
                }
                this.lastTargetNode = targetNode;
                this.lastTargetNode.$view.setAttributeNS(null, 'class', 'SVGClazz-drawedge');
            }
            else if (this.lastTargetNode) {
                this.lastTargetNode.$view.setAttributeNS(null, 'class', 'SVGClazz');
            }
        }
    };
    NewEdge.prototype.removeLine = function () {
        this.isEdgeDrawing = false;
        if (this.svgLine) {
            this.graph.root.removeChild(this.svgLine);
            this.svgLine = null;
        }
        if (this.lastTargetNode) {
            this.lastTargetNode.$view.setAttributeNS(null, 'class', 'SVGClazz');
        }
    };
    NewEdge.prototype.setNewEdgeToNode = function (event) {
        var targetNode = this.graph.$graphModel
            .getNodeByPosition(util_1.Util.getEventX(event), util_1.Util.getEventY(event));
        if (!targetNode) {
            this.removeLine();
            return;
        }
        this.removeLine();
        var edgeType = this.sourceNode.$defaulEdgeType || 'Association';
        var jsonData = {
            type: edgeType,
            source: this.sourceNode.id,
            target: targetNode.id
        };
        var newEdge = this.graph.$graphModel.addEdge(jsonData, true);
        this.graph.drawElement(newEdge);
    };
    NewEdge.prototype.start = function (evt, element) {
        if (this.isEdgeDrawing) {
            return;
        }
        this.isEdgeDrawing = true;
        this.sourceNode = element;
        this.x = this.sourceNode.getPos().x + (this.sourceNode.getSize().x / 2);
        this.y = this.sourceNode.getPos().y + (this.sourceNode.getSize().y / 2);
        var lastInlineEdit = document.getElementById('inlineEdit');
        if (lastInlineEdit) {
            document.body.removeChild(lastInlineEdit);
        }
    };
    return NewEdge;
}());
exports.NewEdge = NewEdge;


/***/ }),

/***/ "./handlers/Select.ts":
/*!****************************!*\
  !*** ./handlers/Select.ts ***!
  \****************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var nodes_1 = __webpack_require__(/*! ../elements/nodes */ "./elements/nodes/index.ts");
var edges_1 = __webpack_require__(/*! ../elements/edges */ "./elements/edges/index.ts");
var util_1 = __webpack_require__(/*! ../util */ "./util.ts");
var Symbol_1 = __webpack_require__(/*! ../elements/nodes/Symbol */ "./elements/nodes/Symbol.ts");
var EventBus_1 = __webpack_require__(/*! ../EventBus */ "./EventBus.ts");
var main_1 = __webpack_require__(/*! ../main */ "./main.ts");
var Select = (function () {
    function Select(graph) {
        this.padding = 5;
        this.graph = graph;
        this.deleteShape = Symbol_1.SymbolLibary.drawSVG({ type: 'Basket', background: true, id: 'trashcan', tooltip: 'Delete class' });
        this.copyNodeShape = Symbol_1.SymbolLibary.drawSVG({ type: 'Copynode', background: true, id: 'copyNode', tooltip: 'Copy class' });
        this.addEdgeShape = Symbol_1.SymbolLibary.drawSVG({ type: 'Edgeicon', background: true, id: 'addEdge', tooltip: 'Click and drag to connect this class' });
    }
    Select.prototype.handle = function (event, element) {
        var _this = this;
        var x = util_1.Util.getEventX(event);
        var y = util_1.Util.getEventY(event);
        event.stopPropagation();
        if (event.type === 'drag') {
            this.isDragged = true;
            this.deleteShape.setAttributeNS(null, 'visibility', 'hidden');
            this.addEdgeShape.setAttributeNS(null, 'visibility', 'hidden');
            this.copyNodeShape.setAttributeNS(null, 'visibility', 'hidden');
            this.resetLastSelectedElements();
            if (element instanceof nodes_1.Node) {
                this.lastSelectedNode = element.$view;
            }
            util_1.Util.addClass(this.lastSelectedNode, 'SVGClazz-selected');
        }
        if (event.target['id'] === 'background' || element === this.graph.$graphModel) {
            this.resetLastSelectedElements();
            this.deleteShape.setAttributeNS(null, 'visibility', 'hidden');
            this.addEdgeShape.setAttributeNS(null, 'visibility', 'hidden');
            this.copyNodeShape.setAttributeNS(null, 'visibility', 'hidden');
            return true;
        }
        if (element instanceof nodes_1.Node && event.type === 'click') {
            var e = element;
            this.graph.root.appendChild(this.deleteShape);
            this.graph.root.appendChild(this.addEdgeShape);
            this.graph.root.appendChild(this.copyNodeShape);
            this.graph.root.appendChild(element.$view);
            this.deleteShape.setAttributeNS(null, 'visibility', 'visible');
            this.addEdgeShape.setAttributeNS(null, 'visibility', 'visible');
            this.copyNodeShape.setAttributeNS(null, 'visibility', 'visible');
            var x_1 = (e.getPos().x + e.getSize().x) + 5;
            var y_1 = e.getPos().y;
            this.deleteShape.setAttributeNS(null, 'transform', "translate(" + x_1 + " " + (y_1 + this.padding) + ")");
            this.deleteShape.onclick = function (e) { return _this.graph.$graphModel.removeElement(element.id); };
            this.copyNodeShape.setAttributeNS(null, 'transform', "translate(" + x_1 + " " + (y_1 + 40 + this.padding) + ")");
            this.copyNodeShape.onclick = function (evt) {
                var nextFreePosition = _this.graph.getNextFreePosition();
                var copyClass = element.copy();
                copyClass.withPos(nextFreePosition.x, nextFreePosition.y);
                _this.graph.drawElement(copyClass);
            };
            this.addEdgeShape.setAttributeNS(null, 'transform', "translate(" + x_1 + " " + (y_1 + 80 + this.padding) + ")");
            this.addEdgeShape.onmousedown = function () {
                EventBus_1.EventBus.setActiveHandler('NewEdge');
                element.$view.dispatchEvent(util_1.Util.createCustomEvent('mousedown'));
            };
        }
        if (element instanceof main_1.Clazz && event.type === 'click') {
            var clazz_1 = element;
            if (util_1.Util.isChrome()) {
                if (this.lastSelectedNode && element.id === this.lastSelectedNode.id && !this.isDragged) {
                    return true;
                }
            }
            this.isDragged = false;
            this.resetLastSelectedElements();
            this.lastSelectedNode = element.$view;
            util_1.Util.addClass(this.lastSelectedNode, 'SVGClazz-selected');
            this.setTooltipOfShape(this.deleteShape, 'Delete class');
            var divInlineEdit_1 = document.createElement('div');
            divInlineEdit_1.id = 'inlineEdit';
            divInlineEdit_1.style.position = 'absolute';
            divInlineEdit_1.style.top = (clazz_1.getPos().y + clazz_1.getSize().y) + 52 + 'px';
            divInlineEdit_1.style.left = clazz_1.getPos().x + 'px';
            divInlineEdit_1.style.width = clazz_1.getSize().x + 'px';
            divInlineEdit_1.style.zIndex = '42';
            var inputText_1 = document.createElement('input');
            inputText_1.type = 'text';
            inputText_1.style.width = '100%';
            inputText_1.placeholder = 'Add properties, edit label';
            divInlineEdit_1.appendChild(inputText_1);
            document.body.appendChild(divInlineEdit_1);
            inputText_1.addEventListener('focusout', function (evt) {
                if (util_1.Util.isChrome()) {
                    if ((!inputText_1.value || inputText_1.value.length === 0) && (!_this.lastSelectedNode || element.id !== _this.lastSelectedNode.id)) {
                        _this.removeLastInlineEdit();
                    }
                    return;
                }
                if ((!inputText_1.value || inputText_1.value.length === 0)) {
                    _this.removeLastInlineEdit();
                }
            });
            var g = this.graph;
            var propertyTypes_1 = ['boolean', 'byte', 'char', 'double', 'float', 'int', 'long', 'short', 'String', 'void'];
            inputText_1.addEventListener('keydown', function (evt) {
                var keyCode = evt.which;
                var inputValue = inputText_1.value;
                if (util_1.Util.endsWith(inputValue, ':') && !document.getElementById('selectPropertyType')) {
                    var selectType_1 = document.createElement('select');
                    selectType_1.id = 'selectPropertyType';
                    selectType_1.style.width = '100%';
                    for (var _i = 0, propertyTypes_2 = propertyTypes_1; _i < propertyTypes_2.length; _i++) {
                        var type = propertyTypes_2[_i];
                        var selectOption = document.createElement('option');
                        selectOption.value = type;
                        selectOption.innerHTML = type;
                        selectType_1.appendChild(selectOption);
                    }
                    selectType_1.addEventListener('change', function (evt) {
                        var inputValueSplitted = inputValue.split(':');
                        var selectedPropertyType = selectType_1.options[selectType_1.selectedIndex].value;
                        if (inputValueSplitted.length >= 1) {
                            inputText_1.value = inputValueSplitted[0].trim() + ' : ' + selectedPropertyType;
                            inputText_1.focus();
                        }
                    });
                    divInlineEdit_1.appendChild(selectType_1);
                }
                else if (!util_1.Util.includes(inputValue, ':')) {
                    var selectType_2 = document.getElementById('selectPropertyType');
                    if (selectType_2) {
                        selectType_2.remove();
                    }
                }
                if (keyCode !== 13) {
                    return;
                }
                if (util_1.Util.includes(inputValue, ':') && !(util_1.Util.includes(inputValue, '(') && util_1.Util.includes(inputValue, ')'))) {
                    clazz_1.addAttribute(inputValue.trim());
                    clazz_1.reDraw();
                }
                else if (util_1.Util.includes(inputValue, '(') && util_1.Util.includes(inputValue, ')')) {
                    clazz_1.addMethod(inputValue.trim());
                    clazz_1.reDraw();
                }
                else if (inputValue.trim().split(' ').length === 1 && inputValue.trim().length > 0) {
                    clazz_1.updateLabel(inputValue.trim());
                }
                divInlineEdit_1.style.top = (clazz_1.getPos().y + clazz_1.getSize().y) + 52 + 'px';
                divInlineEdit_1.style.left = clazz_1.getPos().x + 'px';
                divInlineEdit_1.style.width = clazz_1.getSize().x + 'px';
                inputText_1.value = '';
                var selectType = document.getElementById('selectPropertyType');
                if (selectType) {
                    selectType.remove();
                }
            });
            divInlineEdit_1.children[0].focus();
            return true;
        }
        if (element instanceof edges_1.Association) {
            this.graph.root.appendChild(element.$view);
            this.graph.root.appendChild(element.$sNode.$view);
            this.graph.root.appendChild(element.$tNode.$view);
            this.graph.root.appendChild(this.deleteShape);
            this.setTooltipOfShape(this.deleteShape, 'Delete edge');
            this.deleteShape.setAttributeNS(null, 'visibility', 'visible');
            this.addEdgeShape.setAttributeNS(null, 'visibility', 'hidden');
            this.copyNodeShape.setAttributeNS(null, 'visibility', 'hidden');
            this.deleteShape.setAttributeNS(null, 'transform', "translate(" + x + " " + y + ")");
            this.deleteShape.onclick = function (e) { return _this.graph.$graphModel.removeElement(element.id); };
            this.resetLastSelectedElements();
            var edge = element;
            this.lastSelectedEdge = edge.$view;
            util_1.Util.addClass(this.lastSelectedEdge, 'SVGEdge-selected');
        }
        return true;
    };
    Select.prototype.setTooltipOfShape = function (shape, tooltip) {
        if (!shape || !shape.hasChildNodes()) {
            return;
        }
        var titleElement = shape.childNodes[0];
        if (!titleElement || titleElement.tagName !== 'title') {
            return;
        }
        titleElement.textContent = tooltip;
    };
    Select.prototype.resetLastSelectedElements = function () {
        if (this.lastSelectedNode) {
            util_1.Util.removeClass(this.lastSelectedNode, 'SVGClazz-selected');
            this.lastSelectedNode = undefined;
        }
        if (this.lastSelectedEdge) {
            util_1.Util.removeClass(this.lastSelectedEdge, 'SVGEdge-selected');
            this.lastSelectedEdge = undefined;
        }
        this.removeLastInlineEdit();
    };
    Select.prototype.removeLastInlineEdit = function () {
        var lastInlineEdit = document.getElementById('inlineEdit');
        if (lastInlineEdit) {
            document.body.removeChild(lastInlineEdit);
        }
    };
    Select.prototype.canHandle = function () {
        return EventBus_1.EventBus.isHandlerActiveOrFree(Select.name);
    };
    Select.prototype.setActive = function (active) {
        if (active) {
            EventBus_1.EventBus.setActiveHandler(Select.name);
        }
        else {
            EventBus_1.EventBus.releaseActiveHandler();
        }
    };
    return Select;
}());
exports.Select = Select;


/***/ }),

/***/ "./handlers/Zoom.ts":
/*!**************************!*\
  !*** ./handlers/Zoom.ts ***!
  \**************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var EventBus_1 = __webpack_require__(/*! ../EventBus */ "./EventBus.ts");
var Zoom = (function () {
    function Zoom(graph) {
    }
    Zoom.prototype.handle = function (e, element) {
        var delta = e.deltaY || e.wheelDeltaY || -e.wheelDelta;
        var d = 1 + (delta / 1000);
        var values = this.graph.root.getAttribute('viewBox').split(' ');
        var newViewBox = values[0] + " " + values[1] + " " + parseInt(values[2]) * d + " " + parseInt(values[3]) * d;
        this.graph.root.setAttribute('viewBox', newViewBox);
        e.preventDefault();
        return true;
    };
    Zoom.prototype.canHandle = function () {
        return EventBus_1.EventBus.isHandlerActiveOrFree(Zoom.name);
    };
    Zoom.prototype.setActive = function (active) {
        if (active) {
            EventBus_1.EventBus.setActiveHandler(Zoom.name);
        }
        else {
            EventBus_1.EventBus.releaseActiveHandler();
        }
    };
    return Zoom;
}());
exports.Zoom = Zoom;


/***/ }),

/***/ "./handlers/index.ts":
/*!***************************!*\
  !*** ./handlers/index.ts ***!
  \***************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

function __export(m) {
    for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
}
Object.defineProperty(exports, "__esModule", { value: true });
__export(__webpack_require__(/*! ./Drag */ "./handlers/Drag.ts"));
__export(__webpack_require__(/*! ./Select */ "./handlers/Select.ts"));
__export(__webpack_require__(/*! ./Zoom */ "./handlers/Zoom.ts"));
__export(__webpack_require__(/*! ./NewEdge */ "./handlers/NewEdge.ts"));
__export(__webpack_require__(/*! ./ImportFile */ "./handlers/ImportFile.ts"));
__export(__webpack_require__(/*! ./AddNode */ "./handlers/AddNode.ts"));


/***/ }),

/***/ "./layouts/DagreLayout.ts":
/*!********************************!*\
  !*** ./layouts/DagreLayout.ts ***!
  \********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var DagreLayout = (function () {
    function DagreLayout() {
    }
    DagreLayout.prototype.layout = function (graph, node) {
        if (!window['dagre']) {
            return;
        }
        var model = graph.$graphModel;
        var g = new window['dagre'].graphlib.Graph();
        g.setGraph({ marginx: 100, marginy: 20 }).setDefaultEdgeLabel(function () {
            return {};
        });
        for (var _i = 0, _a = model.nodes; _i < _a.length; _i++) {
            var node_1 = _a[_i];
            g.setNode(node_1.id, { width: node_1.getSize().x, height: node_1.getSize().y });
        }
        for (var _b = 0, _c = model.edges; _b < _c.length; _b++) {
            var edge = _c[_b];
            g.setEdge(edge.$sNode.id, edge.$tNode.id);
        }
        window['dagre'].layout(g);
        g.nodes().forEach(function (nodeId) {
            for (var _i = 0, _a = model.nodes; _i < _a.length; _i++) {
                var node_2 = _a[_i];
                if (node_2.id === nodeId) {
                    node_2.withPos(g.node(nodeId).x - g.node(nodeId).width / 2, g.node(nodeId).y - g.node(nodeId).height / 2);
                }
            }
        });
        g.edges().forEach(function (e) {
            for (var _i = 0, _a = model.edges; _i < _a.length; _i++) {
                var edge = _a[_i];
                if (edge.$sNode.id === e.v && edge.$tNode.id === e.w) {
                    var size = g.edge(e).points.length;
                    edge.clearPoints();
                    for (var i = 0; i < size; i++) {
                        var point = g.edge(e).points[i];
                        edge.addPoint(point.x, point.y);
                    }
                }
            }
        });
    };
    return DagreLayout;
}());
exports.DagreLayout = DagreLayout;


/***/ }),

/***/ "./layouts/DagreLayoutMin.ts":
/*!***********************************!*\
  !*** ./layouts/DagreLayoutMin.ts ***!
  \***********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var LayoutGraphMin = (function () {
    function LayoutGraphMin() {
        this.nodes = {};
        this.edges = [];
        this.outEdges = {};
        this.inEdges = {};
        this.dummyNodes = [];
        this.dummyEdges = {};
        this.count = 0;
        this.minRank = Number.POSITIVE_INFINITY;
        this.maxRank = 0;
        this.maxHeight = 0;
        this.maxWidth = 0;
        this.ranksep = 0;
        this.edgesLabel = [];
    }
    LayoutGraphMin.prototype.nodeCount = function () {
        return this.count;
    };
    LayoutGraphMin.prototype.node = function (id) {
        return this.nodes[id];
    };
    LayoutGraphMin.prototype.setNode = function (id, n) {
        if (n && !this.nodes[id]) {
            this.nodes[id] = n;
            this.count = this.count + 1;
        }
        else if (!n && this.nodes[id]) {
            delete this.nodes[id];
        }
    };
    return LayoutGraphMin;
}());
exports.LayoutGraphMin = LayoutGraphMin;
var LayoutGraphNode = (function () {
    function LayoutGraphNode(id, width, height, x, y) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }
    return LayoutGraphNode;
}());
exports.LayoutGraphNode = LayoutGraphNode;
var LayoutGraphEdge = (function () {
    function LayoutGraphEdge() {
    }
    return LayoutGraphEdge;
}());
exports.LayoutGraphEdge = LayoutGraphEdge;
var DagreLayoutMin = (function () {
    function DagreLayoutMin() {
    }
    DagreLayoutMin.prototype.layout = function (graph, node) {
        var g, layoutNode, nodes, newEdge, edges;
        var i, n, x, y, sId, tId, split = DagreLayoutMin.EDGE_KEY_DELIM;
        var e;
        nodes = node['nodes'];
        edges = node['edges'];
        g = new LayoutGraphMin();
        for (i in nodes) {
            if (!nodes.hasOwnProperty(i) || typeof (nodes[i]) === 'function') {
                continue;
            }
            n = nodes[i];
            g.setNode(n.id, new LayoutGraphNode(n.id, n.getSize().x, n.getSize().y, n.getPos().x, n.getPos().y));
        }
        for (i in edges) {
            if (!edges.hasOwnProperty(i) || typeof (edges[i]) === 'function') {
                continue;
            }
            e = edges[i];
            sId = this.getNodeId(e.$sNode);
            tId = this.getNodeId(e.$tNode);
            if (sId > tId) {
                var tmp = tId;
                tId = sId;
                sId = tmp;
            }
            var idAB = sId + split + tId + split;
            var idBA = tId + split + sId + split;
            if (sId !== tId && g.edgesLabel.indexOf(idAB) < 0 && g.edgesLabel.indexOf(idBA) < 0) {
                newEdge = { source: sId, target: tId, minlen: 1, weight: 1 };
                g.edges.push(newEdge);
                g.edgesLabel.push(idAB);
                if (!g.inEdges[tId]) {
                    g.inEdges[tId] = [];
                }
                g.inEdges[tId].push(newEdge);
                if (!g.outEdges[sId]) {
                    g.outEdges[sId] = [];
                }
                g.outEdges[sId].push(newEdge);
            }
        }
        this.layouting(g);
        for (i in nodes) {
            if (!nodes.hasOwnProperty(i) || typeof (nodes[i]) === 'function') {
                continue;
            }
            n = nodes[i];
            layoutNode = g.node(n.id);
            x = n.getPos().x;
            y = n.getPos().y;
            if (x < 1 && y < 1) {
                n.withPos(Math.ceil(layoutNode.x), Math.ceil(layoutNode.y));
            }
        }
        for (i in edges) {
            if (!edges.hasOwnProperty(i) || typeof (edges[i]) === 'function') {
                continue;
            }
            e = edges[i];
        }
        graph.draw();
    };
    DagreLayoutMin.prototype.getNodeId = function (node) {
        if (node.$owner) {
            return this.getNodeId(node.$owner) || node.id;
        }
        return node.id;
    };
    DagreLayoutMin.prototype.layouting = function (g) {
        this.longestPath(g);
        this.normalizeRanks(g);
        this.normalizeEdge(g);
        this.order(g);
        g.ranksep = 25;
        this.removeDummy(g);
        this.position(g);
    };
    DagreLayoutMin.prototype.setSimpleOrder = function (g) {
        var i, n;
        for (i in g.nodes) {
            n = g.nodes[i];
            n.order = n.rank;
        }
    };
    DagreLayoutMin.prototype.order = function (g) {
        var layering = Array(g.maxRank + 1);
        var visited = {};
        var node, n, order, i;
        for (i = 0; i < layering.length; i++) {
            layering[i] = [];
        }
        for (n in g.nodes) {
            if (visited[n]) {
                continue;
            }
            visited[n] = true;
            node = g.nodes[n];
            if (node.rank !== undefined) {
                layering[node.rank].push(n);
            }
        }
        for (order in layering) {
            for (n in layering[order]) {
                if (layering[order].hasOwnProperty(n) === false) {
                    continue;
                }
                g.nodes[layering[order][n]].order = parseInt(n);
            }
        }
        for (order in layering) {
            if (layering[order].length > 1) {
                for (n in layering[order]) {
                    if (layering[order].hasOwnProperty(n) === false) {
                        continue;
                    }
                    var name_1 = layering[order][n];
                    var sum = 0;
                    var weight = 1;
                    var edges = g.dummyEdges[name_1];
                    if (edges) {
                        for (i in edges) {
                            if (edges.hasOwnProperty(i) === false) {
                                continue;
                            }
                            var edge = edges[i];
                            var nodeU = g.node(edge.target);
                            sum = sum + (edge.weight * nodeU.order);
                            weight = weight + edge.weight;
                        }
                    }
                    g.node(name_1).barycenter = sum / weight;
                    g.node(name_1).weight = weight;
                }
            }
            else if (layering[order].length > 0) {
                for (n in layering[order]) {
                    var name_2 = layering[order][n];
                    g.node(name_2).barycenter = 1;
                    g.node(name_2).weight = 1;
                }
            }
        }
        for (order in layering) {
            for (n in layering[order]) {
                if (layering[order].hasOwnProperty(n) === false) {
                    continue;
                }
                var node_1 = g.nodes[layering[order][n]];
                node_1.order = parseInt(n) + node_1.barycenter * node_1.weight;
                if (isNaN(node_1.order)) {
                    console.log('ERROR');
                }
            }
        }
    };
    DagreLayoutMin.prototype.removeDummy = function (g) {
        for (var z in g.dummyNodes) {
            var node = g.dummyNodes[z];
            g.setNode(node.id, null);
        }
        g.dummyNodes = [];
        g.dummyEdges = {};
    };
    DagreLayoutMin.prototype.normalizeEdge = function (g) {
        var i = 1;
        for (var id in g.edges) {
            var e = g.edges[id];
            var v = e.source;
            var vRank = g.node(v).rank;
            var w = e.target;
            var wRank = g.node(w).rank;
            var name_3 = void 0;
            if (wRank === vRank + 1) {
                continue;
            }
            var dummy = void 0;
            for (vRank = vRank + 1; vRank < wRank; ++vRank) {
                name_3 = '_d' + e.source + e.target + (i++);
                var newEdge = { source: v, target: name_3, minlen: 1, weight: 1 };
                dummy = new LayoutGraphNode(name_3, 0, 0, 0, 0);
                dummy.edgeObj = e;
                dummy.rank = vRank;
                if (!g.dummyEdges[v]) {
                    g.dummyEdges[v] = [];
                }
                g.dummyEdges[v].push(newEdge);
                g.dummyNodes.push(dummy);
                g.setNode(dummy.id, dummy);
                v = name_3;
            }
        }
    };
    DagreLayoutMin.prototype.longestPath = function (g) {
        var i, n, visited = [];
        for (i in g.nodes) {
            n = g.nodes[i];
            visited.push(i);
            n.rank = this.findAllPaths(g, n, 0, visited);
            g.minRank = Math.min(g.minRank, n.rank);
        }
    };
    DagreLayoutMin.prototype.findAllPaths = function (g, n, currentCost, path) {
        var min = 0;
        var id;
        var z;
        var target;
        if (g.outEdges[n.id]) {
            for (z = 0; z < g.outEdges[n.id].length; z++) {
                id = g.outEdges[n.id][z].target;
                target = g.nodes[id];
                if (path[id]) {
                    min = Math.min(min, target.rank);
                }
                else if (path.indexOf(id) < 0) {
                    min = Math.min(min, this.findAllPaths(g, target, currentCost - 2, path));
                }
                else {
                    min = currentCost;
                }
            }
            return min;
        }
        return currentCost;
    };
    DagreLayoutMin.prototype.normalizeRanks = function (g) {
        var min = g.minRank;
        var value;
        g.maxRank = Number.NEGATIVE_INFINITY;
        g.maxHeight = 0;
        g.maxWidth = 0;
        for (var i in g.nodes) {
            var node = g.nodes[i];
            if (node.rank !== undefined) {
                node.rank -= min;
                value = Math.abs(node.rank);
                if (value > g.maxRank) {
                    g.maxRank = value;
                }
                g.maxHeight = Math.max(g.maxHeight, node.height);
                g.maxWidth = Math.max(g.maxWidth, node.width);
            }
        }
    };
    DagreLayoutMin.prototype.position = function (g) {
        this.positionY(g);
        var list = this.positionX(g);
        for (var i in list) {
            for (var pos in list[i]) {
                if (list[i].hasOwnProperty(pos) === false) {
                    continue;
                }
                if (g.node(list[i][pos])) {
                    g.node(list[i][pos]).x = parseInt(pos) * g.maxWidth;
                }
            }
        }
    };
    DagreLayoutMin.prototype.positionY = function (g) {
        var layering = this.buildLayerMatrix(g);
        var rankSep = g.ranksep;
        var prevY = 0;
        for (var layer in layering) {
            var maxHeight = g.maxHeight;
            for (var v in layering[layer]) {
                if (layering[layer].hasOwnProperty(v) === false) {
                    continue;
                }
                var id = layering[layer][v];
                g.nodes[id].y = prevY + maxHeight / 2;
            }
            prevY += maxHeight + rankSep;
        }
    };
    DagreLayoutMin.prototype.buildLayerMatrix = function (g) {
        var layering = Array(g.maxRank + 1);
        for (var i = 0; i < layering.length; i++) {
            layering[i] = [];
        }
        for (var n in g.nodes) {
            var node = g.nodes[n];
            if (node.rank !== undefined) {
                layering[node.rank][node.order] = n;
            }
        }
        return layering;
    };
    DagreLayoutMin.prototype.positionX = function (g) {
        var layering = this.buildLayerMatrix(g);
        return layering;
    };
    DagreLayoutMin.EDGE_KEY_DELIM = '\x01';
    return DagreLayoutMin;
}());
exports.DagreLayoutMin = DagreLayoutMin;


/***/ }),

/***/ "./layouts/Random.ts":
/*!***************************!*\
  !*** ./layouts/Random.ts ***!
  \***************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var util_1 = __webpack_require__(/*! ../util */ "./util.ts");
var Random = (function () {
    function Random() {
    }
    Random.prototype.layout = function (graph) {
        var model = graph.$graphModel;
        if (model.nodes) {
            for (var _i = 0, _a = model.nodes; _i < _a.length; _i++) {
                var node = _a[_i];
                var pos = node.getPos();
                if (pos.x === 0 && pos.y === 0) {
                    var x = util_1.Util.getRandomInt(0, graph.canvasSize.width);
                    var y = util_1.Util.getRandomInt(0, graph.canvasSize.height);
                    node.withPos(x, y);
                }
            }
        }
    };
    return Random;
}());
exports.Random = Random;


/***/ }),

/***/ "./layouts/index.ts":
/*!**************************!*\
  !*** ./layouts/index.ts ***!
  \**************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

function __export(m) {
    for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
}
Object.defineProperty(exports, "__esModule", { value: true });
__export(__webpack_require__(/*! ./DagreLayout */ "./layouts/DagreLayout.ts"));
__export(__webpack_require__(/*! ./Random */ "./layouts/Random.ts"));
__export(__webpack_require__(/*! ./DagreLayoutMin */ "./layouts/DagreLayoutMin.ts"));
var DagreLayout_1 = __webpack_require__(/*! ./DagreLayout */ "./layouts/DagreLayout.ts");
var DagreLayoutMin_1 = __webpack_require__(/*! ./DagreLayoutMin */ "./layouts/DagreLayoutMin.ts");
var Random_1 = __webpack_require__(/*! ./Random */ "./layouts/Random.ts");
new DagreLayout_1.DagreLayout();
new DagreLayoutMin_1.DagreLayoutMin();
new Random_1.Random();


/***/ }),

/***/ "./main.ts":
/*!*****************!*\
  !*** ./main.ts ***!
  \*****************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

function __export(m) {
    for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
}
Object.defineProperty(exports, "__esModule", { value: true });
var BaseElements_1 = __webpack_require__(/*! ./elements/BaseElements */ "./elements/BaseElements.ts");
exports.Point = BaseElements_1.Point;
var Bridge_1 = __webpack_require__(/*! ./Bridge */ "./Bridge.ts");
exports.Bridge = Bridge_1.Bridge;
exports.DelegateAdapter = Bridge_1.DelegateAdapter;
var Graph_1 = __webpack_require__(/*! ./elements/Graph */ "./elements/Graph.ts");
exports.Graph = Graph_1.Graph;
__export(__webpack_require__(/*! ./elements/nodes */ "./elements/nodes/index.ts"));
__export(__webpack_require__(/*! ./elements/edges */ "./elements/edges/index.ts"));
__export(__webpack_require__(/*! ./adapters */ "./adapters/index.ts"));
__export(__webpack_require__(/*! ./UML */ "./UML.ts"));
var BaseElements_2 = __webpack_require__(/*! ./elements/BaseElements */ "./elements/BaseElements.ts");
var Graph_2 = __webpack_require__(/*! ./elements/Graph */ "./elements/Graph.ts");
var ClassEditor_1 = __webpack_require__(/*! ./elements/ClassEditor */ "./elements/ClassEditor.ts");
var Bridge_2 = __webpack_require__(/*! ./Bridge */ "./Bridge.ts");
var util_1 = __webpack_require__(/*! ./util */ "./util.ts");
var nodes = __webpack_require__(/*! ./elements/nodes */ "./elements/nodes/index.ts");
var edges = __webpack_require__(/*! ./elements/edges */ "./elements/edges/index.ts");
if (!window['Point']) {
    window['Point'] = BaseElements_2.Point;
    window['Graph'] = Graph_2.Graph;
    window['bridge'] = new Bridge_2.Bridge();
    window['Util'] = util_1.Util;
    window['Clazz'] = nodes.Clazz;
    window['Association'] = edges.Association;
    window['SymbolLibary'] = nodes.SymbolLibary;
    window['ClassEditor'] = ClassEditor_1.ClassEditor;
}


/***/ }),

/***/ "./util.ts":
/*!*****************!*\
  !*** ./util.ts ***!
  \*****************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var CSS_1 = __webpack_require__(/*! ./CSS */ "./CSS.ts");
var BaseElements_1 = __webpack_require__(/*! ./elements/BaseElements */ "./elements/BaseElements.ts");
var Util = (function () {
    function Util() {
    }
    Util.getRandomInt = function (min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    };
    Util.createShape = function (attrs) {
        var xmlns = attrs.xmlns || 'http://www.w3.org/2000/svg';
        var shape = document.createElementNS(xmlns, attrs.tag);
        for (var attr in attrs) {
            if (attr !== 'tag') {
                shape.setAttribute(attr, attrs[attr]);
            }
        }
        return shape;
    };
    Util.toPascalCase = function (value) {
        value = value.charAt(0).toUpperCase() + value.substring(1).toLowerCase();
        return value;
    };
    Util.isSVG = function (tag) {
        var i, list = ['svg', 'path', 'polygon', 'polyline', 'line', 'title', 'rect', 'filter', 'feGaussianBlur', 'feOffset', 'feBlend', 'linearGradient', 'stop', 'text', 'symbol', 'textPath', 'defs', 'fegaussianblur', 'feoffset', 'feblend', 'circle', 'ellipse', 'g'];
        for (i = 0; i < list.length; i += 1) {
            if (list[i] === tag) {
                return true;
            }
        }
        return false;
    };
    Util.createHTML = function (node) {
        return this.create(node);
    };
    Util.create = function (node) {
        var style, item, xmlns, key, tag, k;
        if (document.createElementNS && (this.isSVG(node.tag) || node.xmlns)) {
            if (node.xmlns) {
                xmlns = node.xmlns;
            }
            else {
                xmlns = 'http://www.w3.org/2000/svg';
            }
            if (node.tag === 'img' && xmlns) {
                item = document.createElementNS(xmlns, 'image');
                item.setAttribute('xmlns:xlink', 'http://www.w3.org/1999/xlink');
                item.setAttributeNS('http://www.w3.org/1999/xlink', 'href', node.src);
            }
            else {
                item = document.createElementNS(xmlns, node.tag);
            }
        }
        else {
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
            if (k === 'tag' || k.charAt(0) === '$' || k === '$graphModel') {
                continue;
            }
            if (k.charAt(0) === '#') {
                item[k.substring(1)] = node[key];
                continue;
            }
            if (k === 'rotate') {
                item.setAttribute('transform', 'rotate(' + node[key] + ',' + node.$graphModel.x + ',' + node.$graphModel.y + ')');
                continue;
            }
            if (k === 'value') {
                if (!node[key]) {
                    continue;
                }
                if (tag !== 'input') {
                    if (tag === 'text') {
                        item.appendChild(document.createTextNode(node[key]));
                    }
                    else {
                        item.innerHTML = node[key];
                    }
                }
                else {
                    item[key] = node[key];
                }
                continue;
            }
            if (k.indexOf('on') === 0) {
                this.bind(item, k.substring(2), node[key]);
                continue;
            }
            if (k.indexOf('-') >= 0) {
                item.style[key] = node[key];
            }
            else {
                if (k === 'style' && typeof (node[key]) === 'object') {
                    for (style in node[key]) {
                        if (!node[key].hasOwnProperty(style)) {
                            continue;
                        }
                        if (node[key][style]) {
                            if ('transform' === style) {
                                item.style.transform = node[key][style];
                                item.style.msTransform = item.style.MozTransform = item.style.WebkitTransform = item.style.OTransform = node[key][style];
                            }
                            else {
                                item.style[style] = node[key][style];
                            }
                        }
                    }
                }
                else {
                    item.setAttribute(key, node[key]);
                }
            }
        }
        if (node.$parent) {
            node.$parent.appendChild(item);
        }
        if (node.$graphModel) {
            item.$graphModel = node.$graphModel;
        }
        return item;
    };
    Util.setSize = function (item, width, height) {
        var value;
        value = Util.getValue(width);
        item.setAttribute('width', value);
        item.style.width = Math.ceil(value);
        value = Util.getValue(height);
        item.setAttribute('height', value);
        item.style.height = Math.ceil(value);
    };
    Util.setAttributeSize = function (item, width, height) {
        var value;
        value = Util.getValue(width);
        item.setAttributeNS(null, 'width', value);
        value = Util.getValue(height);
        item.setAttributeNS(null, 'height', value);
    };
    Util.setStyleSize = function (item, width, height) {
        var value;
        value = Util.getValue(width);
        item.style.width = Math.ceil(value);
        value = Util.getValue(height);
        item.style.height = Math.ceil(value);
    };
    Util.setPos = function (item, x, y) {
        if (item.x && item.x.baseVal) {
            item.style.left = x + 'px';
            item.style.top = y + 'px';
        }
        else {
            item.x = x;
            item.y = y;
        }
    };
    Util.getValue = function (value) {
        return parseInt(('0' + value).replace('px', ''), 10);
    };
    Util.isIE = function () {
        return navigator.userAgent.toLowerCase().indexOf('.net') > -1;
    };
    Util.isEdge = function () {
        return navigator.userAgent.toLowerCase().indexOf('edge') > -1;
    };
    Util.isFireFox = function () {
        return navigator.userAgent.toLowerCase().indexOf('firefox') > -1;
    };
    Util.isSafari = function () {
        var isEdge = Util.isEdge();
        return navigator.userAgent.toLowerCase().indexOf('safari') > -1 && !isEdge;
    };
    Util.isOpera = function () {
        return navigator.userAgent.toLowerCase().indexOf('opera') > -1;
    };
    Util.isChrome = function () {
        var isEdge = Util.isEdge();
        var isSafari = Util.isSafari();
        return navigator.userAgent.toLowerCase().indexOf('chrome') > -1 && !isEdge && isSafari;
    };
    Util.getEventX = function (event) {
        return (Util.isIE() || Util.isEdge()) ? event['offsetX'] : event.layerX;
    };
    Util.getEventY = function (event) {
        return (Util.isIE() || Util.isEdge()) ? event['offsetY'] : event.layerY;
    };
    Util.getNumber = function (str) {
        return parseInt((str || '0').replace('px', ''), 10);
    };
    Util.getStyle = function (styleProp) {
        var i, style, diff, current, ref, el = document.createElement('div'), css;
        document.body.appendChild(el);
        css = new CSS_1.CSS(styleProp);
        ref = new CSS_1.CSS(styleProp, el).css;
        style = window.getComputedStyle(el, null);
        el.className = styleProp;
        current = new CSS_1.CSS(styleProp, el).css;
        diff = Util.getNumber(style.getPropertyValue('border-width'));
        for (i in current) {
            if (!current.hasOwnProperty(i)) {
                continue;
            }
            if (i === 'width' || i === 'height') {
                if (Util.getNumber(current[i]) !== 0 && Util.getNumber(current[i]) + diff * 2 !== Util.getNumber(ref[i])) {
                    css.add(i, current[i]);
                }
            }
            else if (current[i] !== ref[i]) {
                css.add(i, current[i]);
            }
        }
        document.body.removeChild(el);
        return css;
    };
    Util.sizeOf = function (item, node) {
        var board;
        var rect;
        var addBoard;
        if (!item) {
            return undefined;
        }
        if (node) {
            board = node.$owner.$view;
            addBoard = false;
        }
        if (!board) {
            addBoard = true;
            board = Util.createShape({ tag: 'svg', id: 'root', width: 200, height: 200 });
            document.body.appendChild(board);
        }
        if (board.tagName === 'svg') {
            if (typeof item === 'string') {
                item = Util.create({ tag: 'text', $font: true, value: item });
                item.setAttribute('width', '5px');
            }
        }
        else if (typeof item === 'string') {
            item = document.createTextNode(item);
        }
        board.appendChild(item);
        rect = item.getBoundingClientRect();
        board.removeChild(item);
        if (addBoard) {
            document.body.removeChild(board);
        }
        return rect;
    };
    Util.getColor = function (style, defaultColor) {
        if (style) {
            if (style.toLowerCase() === 'create') {
                return '#008000';
            }
            if (style.toLowerCase() === 'nac') {
                return '#FE3E3E';
            }
            if (style.indexOf('#') === 0) {
                return style;
            }
        }
        if (defaultColor) {
            return defaultColor;
        }
        return '#000';
    };
    Util.utf8$to$b64 = function (str) {
        return window.btoa(encodeURIComponent(str));
    };
    Util.showSVG = function (control) {
        var svg = Util.create({
            tag: 'svg',
            style: { left: control.getPos().x, top: control.getPos().y, position: 'absolute' }
        });
        var child = control.getSVG();
        if (child) {
            svg.appendChild(child);
        }
        Util.setSize(svg, control.getSize().x, control.getSize().y);
        document.body.appendChild(svg);
    };
    Util.toJson = function (ref) {
        var result = {};
        return Util.copy(result, ref, false, false);
    };
    Util.initControl = function (parent, control, type, id, json) {
        if (typeof control.init === 'function') {
            control.init(parent, type, id);
        }
        if (typeof control.load === 'function') {
            control.load(json);
        }
    };
    Util.copy = function (ref, src, full, replace) {
        if (src) {
            var i = void 0;
            for (i in src) {
                if (!src.hasOwnProperty(i) || typeof (src[i]) === 'function') {
                    continue;
                }
                if (i.charAt(0) === '$') {
                    if (full) {
                        ref[i] = src[i];
                    }
                    continue;
                }
                if (typeof (src[i]) === 'object') {
                    if (replace) {
                        ref[i] = src[i];
                        continue;
                    }
                    if (!ref[i]) {
                        if (src[i] instanceof Array) {
                            ref[i] = [];
                        }
                        else {
                            ref[i] = {};
                        }
                    }
                    Util.copy(ref[i], src[i], full, false);
                }
                else {
                    if (src[i] === '') {
                        continue;
                    }
                    ref[i] = src[i];
                }
            }
        }
        return ref;
    };
    Util.xmlstringify = function (text) {
        text = text.replace('<', '&lt;');
        text = text.replace('>', '&gt;');
        return text;
    };
    Util.toXML = function (ref, src, full, doc) {
        var name;
        if (!ref) {
            name = src.constructor.name;
            doc = document.implementation.createDocument(null, name, null);
            ref = doc.childNodes[0];
        }
        if (src) {
            var i = void 0;
            for (i in src) {
                if (!src.hasOwnProperty(i) || typeof (src[i]) === 'function') {
                    continue;
                }
                if (i.charAt(0) === '$') {
                    if (full) {
                        ref[i] = src[i];
                    }
                    continue;
                }
                if (typeof (src[i]) === 'object') {
                    if (!ref.getAttribute(i)) {
                        if (src[i] instanceof Array) {
                            for (var c in src[i]) {
                                name = src[i][c].constructor.name;
                                var child = doc.createElement(name);
                                ref.appendChild(child);
                                Util.toXML(child, src[i][c], full, doc);
                            }
                        }
                        else {
                            name = src[i].constructor.name;
                            var child = doc.createElement(name);
                            ref.appendChild(child);
                            Util.toXML(child, src[i], full, doc);
                        }
                    }
                    else {
                        Util.toXML(ref.getAttribute(i), src[i], full, doc);
                    }
                }
                else {
                    if (src[i] === '') {
                        continue;
                    }
                    ref.setAttribute(i, src[i]);
                }
            }
        }
        return ref;
    };
    Util.Range = function (min, max, x, y) {
        max.x = Math.max(max.x, x);
        max.y = Math.max(max.y, y);
        min.x = Math.min(min.x, x);
        min.y = Math.min(min.y, y);
    };
    Util.getPosition = function (m, n, entity, refCenter) {
        var t, p = [], list, distance = [], min = 999999999, position, i, step = 15;
        var pos = entity.getPos();
        var size = entity.getSize();
        list = [BaseElements_1.Point.LEFT, BaseElements_1.Point.RIGHT];
        for (i = 0; i < 2; i += 1) {
            t = this.getLRPosition(m, n, entity, list[i]);
            if (t.y >= pos.y && t.y <= (pos.y + size.y + 1)) {
                t.y += (entity['$' + list[i]] * step);
                if (t.y > (pos.y + size.y)) {
                    t = Util.getUDPosition(m, n, entity, BaseElements_1.Point.DOWN, step);
                }
                p.push(t);
                distance.push(Math.sqrt((refCenter.x - t.x) * (refCenter.x - t.x) + (refCenter.y - t.y) * (refCenter.y - t.y)));
            }
        }
        list = [BaseElements_1.Point.UP, BaseElements_1.Point.DOWN];
        for (i = 0; i < 2; i += 1) {
            t = Util.getUDPosition(m, n, entity, list[i]);
            if (t.x >= pos.x && t.x <= (pos.x + size.x + 1)) {
                t.x += (entity['$' + list[i]] * step);
                if (t.x > (pos.x + size.x)) {
                    t = this.getLRPosition(m, n, entity, BaseElements_1.Point.RIGHT, step);
                }
                p.push(t);
                distance.push(Math.sqrt((refCenter.x - t.x) * (refCenter.x - t.x) + (refCenter.y - t.y) * (refCenter.y - t.y)));
            }
        }
        for (i = 0; i < p.length; i += 1) {
            if (distance[i] < min) {
                min = distance[i];
                position = p[i];
            }
        }
        return position;
    };
    Util.getUDPosition = function (m, n, e, p, step) {
        var pos = e.getPos();
        var size = e.getSize();
        var x, y = pos.y;
        if (p === BaseElements_1.Point.DOWN) {
            y += size.y;
        }
        x = (y - n) / m;
        if (step) {
            x += e['$' + p] * step;
            if (x < pos.x) {
                x = pos.x;
            }
            else if (x > (pos.x + size.x)) {
                x = pos.x + size.x;
            }
        }
        return new BaseElements_1.Point(x, y, p);
    };
    Util.getLRPosition = function (m, n, e, p, step) {
        var pos = e.getPos();
        var size = e.getSize();
        var y, x = pos.x;
        if (p === BaseElements_1.Point.RIGHT) {
            x += size.x;
        }
        y = m * x + n;
        if (step) {
            y += e['$' + p] * step;
            if (y < pos.y) {
                y = pos.y;
            }
            else if (y > (pos.y + size.y)) {
                y = pos.y + size.y;
            }
        }
        return new BaseElements_1.Point(x, y, p);
    };
    Util.hasClass = function (element, cls) {
        var className = element.getAttribute('class');
        return className.indexOf(cls) > 0;
    };
    Util.addClass = function (element, cls) {
        if (!Util.hasClass(element, cls)) {
            var className = element.getAttribute('class');
            element.setAttributeNS(null, 'class', className + ' ' + cls);
        }
    };
    Util.removeClass = function (element, cls) {
        if (Util.hasClass(element, cls)) {
            var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
            var className = element.getAttribute('class');
            element.setAttributeNS(null, 'class', className.replace(reg, ' ').trim());
        }
    };
    Util.startsWith = function (s, searchS) {
        if (!String.prototype.startsWith) {
            return s.indexOf(searchS) === 0;
        }
        return s.startsWith(searchS);
    };
    Util.endsWith = function (s, searchS) {
        if (!String.prototype.endsWith) {
            var lastIndex = s.lastIndexOf(searchS);
            return lastIndex !== -1 && lastIndex === (s.length - 1);
        }
        return s.endsWith(searchS);
    };
    Util.includes = function (s, searchS) {
        if (!String.prototype.includes) {
            var idx = s.indexOf(searchS);
            return idx > -1;
        }
        return s.includes(searchS);
    };
    Util.isParentOfChild = function (parent, child) {
        if (!parent || !child) {
            return false;
        }
        if (Util.isIE()) {
            var children = parent.childNodes;
            var found = false;
            for (var i = 0; i < children.length; i++) {
                var childItem = children[i];
                if (childItem === child) {
                    return true;
                }
            }
            return false;
        }
        return parent.contains(child);
    };
    Util.createCustomEvent = function (type, params) {
        var evt;
        if (typeof window['CustomEvent'] !== 'function') {
            params = params || { bubbles: false, cancelable: false, detail: undefined };
            evt = document.createEvent('CustomEvent');
            evt.initCustomEvent(type, params.bubbles, params.cancelable, params.detail);
            return evt;
        }
        evt = new CustomEvent(type);
        return evt;
    };
    Util.saveToLocalStorage = function (model) {
        if (!this.isAutoSave) {
            return false;
        }
        if (Util.isLocalStorageSupported()) {
            if (model) {
                if (model.$isLoading) {
                    return false;
                }
                var jsonObj = Util.toJson(model);
                var data = JSON.stringify(jsonObj, null, '\t');
                localStorage.setItem('diagram', data);
            }
            else {
                localStorage.removeItem('diagram');
            }
            return true;
        }
        return false;
    };
    Util.getDiagramFromLocalStorage = function () {
        if (Util.isLocalStorageSupported()) {
            return localStorage.getItem('diagram');
        }
        return undefined;
    };
    Util.isLocalStorageSupported = function () {
        if (this.isEdge()) {
            return false;
        }
        return localStorage !== undefined;
    };
    return Util;
}());
exports.Util = Util;


/***/ })

/******/ });
//# sourceMappingURL=diagram.js.map