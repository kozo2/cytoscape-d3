package org.cytoscape.d3.internal.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;

public class D3JsonBuilder {

	private static final String NODE = "nodes";
	private static final String EDGE = "links";
	private static final String SOURCE = "source";
	private static final String TARGET= "target";
	private static final String ID = "id";
	private static final String X = "x";
	private static final String Y = "y";

	protected final void serializeNetwork(final CyNetwork network, final CyNetworkView view, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		// Write array
		final List<CyNode> nodes = network.getNodeList();
		final List<CyEdge> edges = network.getEdgeList();

		final Map<CyNode, Long> node2Index = new HashMap<CyNode, Long>();

		jgen.useDefaultPrettyPrinter();

		jgen.writeStartObject();

		long index = 0;
		jgen.writeArrayFieldStart(NODE);
		for (final CyNode node : nodes) {
			jgen.writeStartObject();

			jgen.writeStringField(ID, node.getSUID().toString());
			if(view != null) {
				// View is available.  Pick (x,y)
				final View<CyNode> nodeView = view.getNodeView(node);
				jgen.writeNumberField(X, nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION));
				jgen.writeNumberField(Y, nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION));
			}

			// Write CyRow in "data" field
			jgen.writeObject(network.getRow(node));

			jgen.writeEndObject();
			node2Index.put(node, index);
			index++;
		}
		jgen.writeEndArray();

		jgen.writeArrayFieldStart(EDGE);
		for (final CyEdge edge : edges) {
			jgen.writeStartObject();

			jgen.writeStringField(ID, edge.getSUID().toString());
			jgen.writeNumberField(SOURCE, node2Index.get(edge.getSource()));
			jgen.writeNumberField(TARGET, node2Index.get(edge.getTarget()));

			// Write CyRow in "data" field
			jgen.writeObject(network.getRow(edge));

			jgen.writeEndObject();

		}
		jgen.writeEndArray();

		jgen.writeEndObject();
	}
}