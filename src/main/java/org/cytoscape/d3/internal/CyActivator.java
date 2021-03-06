package org.cytoscape.d3.internal;

import static org.cytoscape.work.ServiceProperties.ID;

import java.util.Properties;

import org.cytoscape.d3.internal.serializer.D3TreeModule;
import org.cytoscape.d3.internal.serializer.D3jsModule;
import org.cytoscape.d3.internal.writer.D3NetworkWriterFactory;
import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Activator for D3.js support module.
 * 
 */
public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		// Importing Services
		final StreamUtil streamUtil = getService(bc, StreamUtil.class);

		// ///////////////// Writers ////////////////////////////
		final ObjectMapper d3jsMapper = new ObjectMapper();
		d3jsMapper.registerModule(new D3jsModule());

		final ObjectMapper d3jsTreeMapper = new ObjectMapper();
		d3jsTreeMapper.registerModule(new D3TreeModule());

		final BasicCyFileFilter d3jsFilter = new BasicCyFileFilter(new String[] { "json" },
				new String[] { "application/json" }, "JSON for D3.js Force Layout", DataCategory.NETWORK, streamUtil);
		final BasicCyFileFilter d3jsTreeFilter = new BasicCyFileFilter(new String[] { "json" },
				new String[] { "application/json" }, "JSON for D3.js Tree", DataCategory.NETWORK, streamUtil);

		// For D3.js Force layout
		final D3NetworkWriterFactory d3jsWriterFactory = new D3NetworkWriterFactory(d3jsFilter, d3jsMapper);
		final Properties d3WriterFactoryProperties = new Properties();
		d3WriterFactoryProperties.put(ID, "d3jsWriterFactory");
		registerAllServices(bc, d3jsWriterFactory, d3WriterFactoryProperties);

		// For Tree Layout
		final D3NetworkWriterFactory d3jsTreeWriterFactory = new D3NetworkWriterFactory(d3jsTreeFilter, d3jsTreeMapper);
		final Properties d3TreeWriterFactoryProperties = new Properties();
		d3TreeWriterFactoryProperties.put(ID, "d3jsTreeWriterFactory");
		registerAllServices(bc, d3jsTreeWriterFactory, d3TreeWriterFactoryProperties);
	}
}