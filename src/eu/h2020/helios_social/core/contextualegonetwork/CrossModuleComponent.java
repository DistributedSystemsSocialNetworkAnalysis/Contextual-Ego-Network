package eu.h2020.helios_social.core.contextualegonetwork;

import java.util.HashMap;

/**
 * This class is used as a base class by HELIOS components, such as {@link Node} and {@link Edge} that
 * need to store data coming from multiple modules. The {@link #getOrCreateInstance} method can be used to create
 * and access instances of data structures needed by each module.
 */
public abstract class CrossModuleComponent {
	// module data stored in this component
    private HashMap<String, Object> moduleData;
    // contextual ego network
    ContextualEgoNetwork contextualEgoNetwork;
    
    protected CrossModuleComponent (ContextualEgoNetwork contextualEgoNetwork) {
        if(contextualEgoNetwork == null) Utils.error(new NullPointerException());
    	this.contextualEgoNetwork = contextualEgoNetwork;
    }
    
    /**
     * Default constructor.
     * Since it's used in serialization and deserialization and to save serialization space, module data are initialized on-demand.
     */
    protected CrossModuleComponent() {
    }

    /**
     * Returns the contextual ego network instance in whose hierarchy the component resides
     * @return The enclosing ContextualEgoNetwork
     */
    public ContextualEgoNetwork getContextualEgoNetwork() {
    	return contextualEgoNetwork;
    }
    
    /**
     * Returns an instance of the given class that is stored in the node. If no such instance exists,
     * a new one is created first using the default constructor.
     * Created instances are saved and loaded alongside nodes if these are part of a contextual ego network structure.
     * (Note, if they reference objects that aren't registered in the {@link Serializer}, these are loaded as separate instances.)
     * @param moduleClass A given class (e.g. that stores the node's data needed by a HELIOS module)
     * @param ModuleObjectDataType The type of the returned object (is resolved to the same as the type of the class)
     * @return An instance of the given class
     */
    @SuppressWarnings("unchecked")
	public <ModuleObjectDataType> ModuleObjectDataType getOrCreateInstance(Class<ModuleObjectDataType> moduleClass) {
    	if(moduleData==null)
    		moduleData = new HashMap<String, Object>();
    	String dataTypeName = moduleClass.getCanonicalName();
    	Object found = moduleData.get(dataTypeName);
    	if(found==null) {
    		try {
    			found = moduleClass.getConstructor().newInstance();
    			moduleData.put(dataTypeName, found);
    		}
    		catch(Exception e) {
    			Utils.error("Failed to initialized default instance of "+dataTypeName);
    		}
    	}
    	return (ModuleObjectDataType) found;
    }
    
    /*
    public Object getModuleData() {
    	StackTraceElement[] trace = Thread.currentThread().getStackTrace();
    	if(trace.length<2)
    		return null;
    	return getModuleData(trace[1].getClassName());
    }

    public void setModuleData(Object data) {
    	StackTraceElement[] trace = Thread.currentThread().getStackTrace();
    	if(trace.length<2)
    		Utils.error("Trace too shallow");
    	else
    		setModuleData(trace[1].getClassName());
    }
    
    public void setModuleData(String module, Object data) {
    	moduleData.put(module, data);
    }
    
    public Object getModuleData(String module) {
    	return moduleData.get(module);
    }
    */
}
