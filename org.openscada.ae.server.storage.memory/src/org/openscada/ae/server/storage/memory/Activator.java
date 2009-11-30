package org.openscada.ae.server.storage.memory;

import java.util.Properties;

import org.openscada.ae.server.storage.Storage;
import org.openscada.ae.server.storage.memory.internal.EventMatcher.MatchMode;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private MemoryStorage memoryStorage;

	public void start(BundleContext context) throws Exception {
		memoryStorage = new MemoryStorage(MatchMode.HANDLE_UNKNOWN_AS_FALSE);
		Properties props = new Properties();
		context.registerService(new String[]{MemoryStorage.class.getName(), Storage.class.getName()}, memoryStorage, props);
	}

	public void stop(BundleContext context) throws Exception {
		memoryStorage = null;
		return;
	}
}
