# Contextual Ego Network Library

## Introduction
The Contextual Ego Network (CEN) Library is responsible for the management of information stored in the local devices. The library manages the following type of information:
- The user's contexts.
- Alters in each context.
- The user's ego network and the interactions occuring in each context.
- Data structures that other modules (e.g. the graph mining module of D4.3 and the social media mining module of D4.8)

This information supports dynamic loading and unloading from memory while preserving pointers to data objects. Serialization is handled automatically. *All serialized classes require a (protected) default constructor.*

## API Usage
Adding an interaction to the contextual ego network by loading the respective objects or creating them when they don't already exist:
```javascript
import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.core.contextualegonetwork.Context;
import eu.h2020.helios_social.core.contextualegonetwork.Node;
import eu.h2020.helios_social.core.contextualegonetwork.Edge;

ContextualEgoNetwork cen = ContextualEgoNetwork.createOrLoad("ego_user_id", null);
Context context = cen.getOrCreateContext("context_name");
Node alter1 = cen.getOrCreateNode("alter_id", null);
Interaction interaction = context.getOrAddEdge(cen.getEgo(), alter).addDetectedInteraction("interaction_type");
cen.save(); //saves the contextual ego network
```

Storing node-related parameters in the library:
```java
public class ModuleNodeParameters {
	private Integer[] parameters;
	public ModuleNodeParameters() {
		parameters = new Integer[10];
		//your parameter classes should implement only the default constructor (this enables the constructor-as-default-value) logic
		//class members can be initialized either in the constructor or when first needed
	}
}

Node node = ...;
ModuleNodeParameters nodeParameters = node.getOrCreateInstance(ModuleNodeParameters.class); //calls the default constructor
```

For more usage examples refer to the ExampleSave.java and ExampleLoad.java files of the eu.h2020.helios_social.core.contextualegonetwork package.

## Project Structure
This project contains the following components:

src - The source code files.
doc - Additional documentation files.