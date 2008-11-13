importClass(org.openscada.core.Variant);
importClass(org.openscada.da.server.common.AttributeMode);
importClass(java.lang.Thread);
importClass(java.lang.Integer);
importClass(java.lang.Long);
importClass(java.lang.Double);

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

// scheduling functions

function onInterval(item, millisec, changevalue) {
	while (true) {
		java.lang.Thread.sleep(millisec);
		changevalue(item);
	}
}

function onProgression(item, millisec, factor, changevalue) {
	var waitfor = millisec;
	while (true) {
		java.lang.Thread.sleep(waitfor);
		changevalue(item);
		waitfor = waitfor * factor;
	}
}

// data change functions

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
	item.updateData(newVariant, null, AttributeMode.UPDATE);
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
	item.updateData(newVariant, null, AttributeMode.UPDATE);
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
	item.updateData(newVariant, null, AttributeMode.UPDATE);
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
	item.updateData(newVariant, null, AttributeMode.UPDATE);
}
