importClass(org.openscada.core.Variant);
importClass(org.openscada.da.server.common.AttributeMode);
importClass(java.lang.Thread);
importClass(java.lang.Integer);
importClass(java.lang.Long);
importClass(java.lang.Double);
importClass(java.lang.System);
importClass(java.util.HashMap);

Variant = org.openscada.core.Variant;
AttributeMode = org.openscada.da.server.common.AttributeMode;

// initial setup

var items = new Array();

function registerItem(server, item, callback, writehandler) {
	var o = {
		'server' : server,
		'item' : item,
		'callback' : callback,
		'writehandler' : writehandler
	};
	var i = items.push(o);
	logger.info('register item ' + item.getInformation().getName());
	return i - 1;
}

function writeValue(i, value) {
	logger.info('write Value ' + value + ' for ' + items[i].item.getInformation().getName());
	try {
		items[i].writehandler(items[i].item, value);
	} catch (e) {
		logger.warn(e);
	}
}

function startSimulation() {
	logger.info('start simulation ...');
	for (i in items) {
		startItemSimulator(items[i].item, items[i].callback);
	}
}

function startItemSimulator(item, callback) {
	new java.lang.Thread(function() {
		callback(item);
    }).start();
}

function timeStampMap() {
	var ts = java.lang.System.currentTimeMillis();
	var attr = new java.util.HashMap();
	attr.put("timestamp", new Variant(new java.lang.Long(ts)));
	return attr;
}
// scheduling functions

function onInterval(item, millisec, changevalue) {
	var x = 0;
	while (true) {
		changevalue(item, x);
		x = x + 1;
		java.lang.Thread.sleep(millisec);
	}
}

function onProgression(item, millisec, factor, changevalue) {
	var x = 0;
	var waitfor = millisec;
	while (true) {
		changevalue(item, x);
		x = x + 1;
		waitfor = waitfor * factor;
		java.lang.Thread.sleep(waitfor);
	}
}

function onWaitentry(item, waitlist, changevalue) {
	var x = 0;
	while (true) {
		for (waitfor in waitlist) {
			changevalue(item, x);
			x = x + 1;
			java.lang.Thread.sleep(waitfor);
		}
	}
}

function onRandomInterval(item, from, to, changevalue) {
	var x = 0;
	while (true) {
		changevalue(item, x);
		var millisec = Math.round(Math.random() * (1 + to - from) + from);
		x = x + 1;
		java.lang.Thread.sleep(millisec);
	}
}

// data change functions

// -- increment

function doIncrementInt32(start, by, item) {
	logger.info('doIncrementInt32 for ' + item.getInformation().getName());
	var oldVariant = item.readValue();
	var oldValue = start;
	var newValue = 0;
	if (oldVariant != null && !oldVariant.isNull()) {
		oldValue = oldVariant.asInteger();
	}
	newValue = oldValue + by;
	var newVariant = new Variant(new java.lang.Integer(newValue));
	item.updateData(newVariant, timeStampMap(), AttributeMode.UPDATE);
}

function doIncrementInt64(start, by, item) {
	logger.info('doIncrementInt64 for ' + item.getInformation().getName());
	var oldVariant = item.readValue();
	var oldValue = start;
	var newValue = 0;
	if (oldVariant != null && !oldVariant.isNull()) {
		oldValue = oldVariant.asLong();
	}
	newValue = oldValue + by;
	var newVariant = new Variant(new java.lang.Long(newValue));
	item.updateData(newVariant, timeStampMap(), AttributeMode.UPDATE);
}

function doIncrementDouble(start, by, item) {
	logger.info('doIncrementDouble for ' + item.getInformation().getName());
	var oldVariant = item.readValue();
	var oldValue = start;
	var newValue = 0;
	if (oldVariant != null && !oldVariant.isNull()) {
		oldValue = oldVariant.asDouble();
	}
	newValue = oldValue + by;
	var newVariant = new Variant(new java.lang.Double(newValue));
	item.updateData(newVariant, timeStampMap(), AttributeMode.UPDATE);
}

function doToggleBoolean(item) {
	logger.info('doToggleBoolean for ' + item.getInformation().getName());
	var oldVariant = item.readValue();
	var oldValue = false;
	var newValue = false;
	if (oldVariant != null && !oldVariant.isNull()) {
		oldValue = oldVariant.asBoolean();
	}
	newValue = !oldValue;
	var newVariant = new Variant(new java.lang.Boolean(newValue));
	item.updateData(newVariant, timeStampMap(), AttributeMode.UPDATE);
}

//-- sinus

function doSinusInt32(x, a, c, item) {
	logger.info('doSinusInt32 for ' + item.getInformation().getName());
	var newValue = a * Math.sin(x / Math.PI) + c;
	var newVariant = new Variant(new java.lang.Integer(newValue));
	item.updateData(newVariant, timeStampMap(), AttributeMode.UPDATE);
}

function doSinusInt64(x, a, c, item) {
	logger.info('doSinusInt64 for ' + item.getInformation().getName());
	var newValue = a * Math.sin(x / Math.PI) + c;
	var newVariant = new Variant(new java.lang.Long(newValue));
	item.updateData(newVariant, timeStampMap(), AttributeMode.UPDATE);
}

function doSinusDouble(x, a, c, item) {
	logger.info('doSinusDouble for ' + item.getInformation().getName());
	var newValue = a * Math.sin(x / Math.PI) + c;
	var newVariant = new Variant(new java.lang.Double(newValue));
	item.updateData(newVariant, timeStampMap(), AttributeMode.UPDATE);
}

//-- random

function doRandomInt32(from, to, item) {
	logger.info('doRandomInt32 for ' + item.getInformation().getName());
	var newValue = Math.random() * (1 + to - from) + from;
	var newVariant = new Variant(new java.lang.Integer(newValue));
	item.updateData(newVariant, timeStampMap(), AttributeMode.UPDATE);
}

function doRandomInt64(from, to, item) {
	logger.info('doRandomInt64 for ' + item.getInformation().getName());
	var newValue = Math.random() * (1 + to - from) + from;
	var newVariant = new Variant(new java.lang.Long(newValue));
	item.updateData(newVariant, timeStampMap(), AttributeMode.UPDATE);
}

function doRandomDouble(from, to, item) {
	logger.info('doRandomDouble for ' + item.getInformation().getName());
	var newValue = Math.random() * (1 + to - from) + from;
	var newVariant = new Variant(new java.lang.Double(newValue));
	item.updateData(newVariant, timeStampMap(), AttributeMode.UPDATE);
}

//-- sawtooth

function doSawtoothInt32(x, a, c, flat, item) {
	logger.info('doSawtoothInt32 for ' + item.getInformation().getName());
	var k = x % flat_to;
	var newValue = k * a + c;
	if (k >= flat_from) {
		newValue = c;
	}
	var newVariant = new Variant(new java.lang.Integer(newValue));
	item.updateData(newVariant, timeStampMap(), AttributeMode.UPDATE);
}

function doSawtoothInt64(from, to, item) {
	logger.info('doSawtoothInt64 for ' + item.getInformation().getName());
	var k = x % flat_to;
	var newValue = k * a + c;
	if (k >= flat_from) {
		newValue = c;
	}
	var newVariant = new Variant(new java.lang.Long(newValue));
	item.updateData(newVariant, timeStampMap(), AttributeMode.UPDATE);
}

function doSawtoothDouble(x, a, c, flat_from, flat_to, item) {
	logger.info('doSawtoothDouble for ' + item.getInformation().getName());
	var k = x % flat_to;
	var newValue = k * a + c;
	if (k >= flat_from) {
		newValue = c;
	}
	var newVariant = new Variant(new java.lang.Double(newValue));
	item.updateData(newVariant, timeStampMap(), AttributeMode.UPDATE);
}
