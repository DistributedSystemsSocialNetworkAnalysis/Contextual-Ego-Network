package eu.h2020.helios_social.core.contextualegonetwork;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is a base class used by HELIOS components, such as {@link Node} and {@link Edge} that
 * need to store data coming from multiple modules. The {@link #getOrCreateInstance} method can be used to create
 * and access instances of data structures needed by each module.
 * 
 * @author Emmanouil Krasanakis (maniospas@iti.gr)
 */
public abstract class CrossModuleComponent {
	// module data stored in this component
    private HashMap<String, Object> moduleData;
    // contextual ego network
    ContextualEgoNetwork contextualEgoNetwork;
    
    protected CrossModuleComponent (ContextualEgoNetwork contextualEgoNetwork) {
        //if(contextualEgoNetwork == null) Utils.error(new NullPointerException());
    	this.contextualEgoNetwork = contextualEgoNetwork;
    }
    
    /**
     * Default constructor.
     * Since it's used in serialization and deserialization and to save serialization space, module data are initialized on-demand.
     */
    protected CrossModuleComponent() {
    }

    /**
     * Retrieves the contextual ego network instance in whose hierarchy the component resides.
     * @return The enclosing ContextualEgoNetwork.
     */
    public ContextualEgoNetwork getContextualEgoNetwork() {
    	return contextualEgoNetwork;
    }
    

    /**
     * Checks that this component belongs to the given contextual ego network.
     * @param contextualEgoNetwork The given contextual ego network.
     * @throws RuntimeException if the component is not part of the network.
     */
    public void assertSameContextualEgoNetwork(ContextualEgoNetwork contextualEgoNetwork) {
    	if(contextualEgoNetwork!=getContextualEgoNetwork() || getContextualEgoNetwork()==null)
    		Utils.error("Component not part of the given ContextualEgoNetwork instance");
    }
    
    /**
     * Checks that this component belongs to the same ego network as the compared component.
     * @param with The compared component.
     * @throws RuntimeException if the two components are part of different networks.
     */
    public void assertSameContextualEgoNetwork(CrossModuleComponent with) {
    	if(with.getContextualEgoNetwork()!=getContextualEgoNetwork() || getContextualEgoNetwork()==null)
    		Utils.error("Components not part of the same ContextualEgoNetwork instance");
    }
    
    /**
     * A wrapper for {@link #getOrCreateInstance(String, Class)} that automatically infers the module name as the given class's name.
     * @param <ModuleObjectDataType> The implicitly understood type of the returned object (is automatically resolved to the same as the type of the given class)
     * @param moduleClass The given class (e.g. that stores the node's data needed by a HELIOS module).
     * @return An instance of the given class.
     */
	public <ModuleObjectDataType> ModuleObjectDataType getOrCreateInstance(Class<ModuleObjectDataType> moduleClass) {
    	String dataTypeName = moduleClass.getCanonicalName();
    	return getOrCreateInstance(dataTypeName, moduleClass);
    }
    /**
     * Retrieves an instance of the given class that is stored in the component. If no such instance exists,
     * a new one is created first using either a constructor with this object as an argument or 
     * a default constructor (i.e. a constructor with no argument).
     * Both classes to be created and constructors need be of public visibility. The default constructor assumes lower priority.
     * <h2>Remarks</h2>
     * - Refrain from directly referencing other {@link CrossModuleComponent} objects (the one creating the instance is fine),
     * as these could be removed from the contextual ego network and keeping references to them can induce unexpected behavior.
     * <br>
     * - Created instances are saved and loaded alongside nodes if these are part of a contextual ego network structure.
     * Notably, if more than one created instances -for example of the same class with different module names- 
     * comprise references to the same object but the latter not registered in the {@link Serializer}, 
     * two separate instances of that object would be created upon loading the contextual ego network.
     * <br>
     * - If modifications of created instances occur, these <i>do not</i> trigger any listener callbacks of the contextual ego network.
     * For example, this means that changes to the created instances are not saved by
     * {@link eu.h2020.helios_social.core.contextualegonetwork.listeners.RecoveryListener}.
     * @param <ModuleObjectDataType> The implicitly understood type of the returned object (is automatically resolved to the same as the type of the given class)
     * @param moduleName A custom name to reference the class instance by future calls of this method.
     * @param moduleClass The given class (e.g. that stores the node's data needed by a HELIOS module).
     * @return An instance of the given class unique to the given module name.
     */
    @SuppressWarnings("unchecked")
	public <ModuleObjectDataType> ModuleObjectDataType getOrCreateInstance(String moduleName, Class<ModuleObjectDataType> moduleClass) {
    	if(moduleData==null)
    		moduleData = new HashMap<String, Object>();
    	Object found = moduleData.get(moduleName);
    	if(found==null) {
    		try {
    			try {
	    			if(moduleClass.getConstructor(CrossModuleComponent.class)!=null)
	        			found = moduleClass.getConstructor(CrossModuleComponent.class).newInstance(this);
    			}
    			catch(NoSuchMethodException e) {
    				found = moduleClass.getConstructor().newInstance();
    			}
    			moduleData.put(moduleName, found);
    		}
    		catch(Exception e) {
    			Utils.error("Failed to initialize default or (CrossModuleComponent) instance for module "+moduleName);
    		}
    	}
    	return (ModuleObjectDataType) found;
    }
    
    
    /**
     * A variation of {@link #getOrCreateInstance(String, Class)} that takes a supplier returning objects of the inferred class instead
     * of automatically determining the constructor. Similarly to the original functionality, if the object is already there it. The
     * supplier pattern is used to avoid passing instantiated objects that are never stored.
     * 
     * <br> Make sure that the supplier returns a <b>different</b> instance each time.
     * 
     * @param <ModuleObjectDataType> The implicitly understood type of the returned object (is automatically resolved to the same as the type of the given class)
     * @param moduleName A custom name to reference the class instance by future calls of this method.
     * @param moduleClassSupplier The method returning the instance.
     * @return An instance of the given class.
     */
    @SuppressWarnings("unchecked")
	public <ModuleObjectDataType> ModuleObjectDataType getOrCreateInstance(String moduleName, Supplier<ModuleObjectDataType> moduleClassSupplier) {
    	if(moduleData==null)
    		moduleData = new HashMap<String, Object>();
    	Object found = moduleData.get(moduleName);
    	if(found==null) {
    		try {
    			found = moduleClassSupplier.get();
    			moduleData.put(moduleName, found);
    		}
    		catch(Exception e) {
    			Utils.error("Failed to get supplier instance for "+moduleName);
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
