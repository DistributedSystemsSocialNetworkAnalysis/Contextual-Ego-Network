package eu.h2020.helios_social.core.contextualegonetwork.examples;
import java.util.Arrays;

import eu.h2020.helios_social.core.contextualegonetwork.Context;
import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.core.contextualegonetwork.Edge;
import eu.h2020.helios_social.core.contextualegonetwork.Interaction;
import eu.h2020.helios_social.core.contextualegonetwork.Node;
import eu.h2020.helios_social.core.contextualegonetwork.Storage;
import eu.h2020.helios_social.core.contextualegonetwork.Utils;
import eu.h2020.helios_social.core.contextualegonetwork.storage.NativeStorage;

public class ExampleLoad {

	public static void main(String[] args) {
		Utils.development = false;//disable logging and make exceptions try to forcefully continue execution
		
		ContextualEgoNetwork egoNetwork = ContextualEgoNetwork.createOrLoad(Storage.getInstance("CEN\\", NativeStorage.class), "user-00001", null);
		
		//assert that deserialization loads the ego correctly
		System.out.println(egoNetwork.getEgo().getId());
		System.out.println(egoNetwork.getEgo().getData());
		for(Node alter : egoNetwork.getAlters())
			System.out.println(alter.getId());
		
		//assert that deserialization finds contexts (these aren't loaded yet)
		System.out.println(egoNetwork.getContexts());
		//assert that deserialization identifies the same data objects
		System.out.println(egoNetwork+" == "+egoNetwork.getContexts().get(0).getContextualEgoNetwork());
		//assert that deserialization works for nested data types
		//System.out.println(((DefaultContextData)egoNetwork.getContexts().get(0).getData()).getName());
		//assert that deserialization works for arrays (it would throw a nullptr exception if it didn't work)
		//System.out.println(egoNetwork.getContexts().get(1).getTimeCounter()[6][23]);
		//assert that created class instances work correctly
		System.out.println(egoNetwork.getEgo().getOrCreateInstance(ExampleSave.class).toString());
		System.out.println(egoNetwork.getEgo().getOrCreateInstance("custom data", ExampleSave::generateData).testInt);
		
		//print the edges of all contexts
		for(Context context : egoNetwork.getContexts()) {
			context.cleanup();
			System.out.println("Context: "+context.getData().toString());
			System.out.println(context.getEdges().size()+" edges");
			for(Edge edge : context.getEdges()) {
				System.out.println(edge.getSrc().getData()+" -> "+edge.getDst().getData());
				for(Interaction interaction : edge.getInteractions())
					System.out.println(interaction.getEdge().toString());//assert that edge has been loaded
			}
			System.out.println();
		}
	}
}
