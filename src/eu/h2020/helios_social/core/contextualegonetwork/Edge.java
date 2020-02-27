package eu.h2020.helios_social.core.contextualegonetwork;

import java.util.ArrayList;

import eu.h2020.helios_social.core.contextualegonetwork.Node;

/**
 * This class implements an edge of the Social Graph. An edge, in a context, can link the ego of the Contextual Ego Network to one of the alters.
 * Edges comprise multiple interactions.
 */
public final class Edge extends CrossModuleComponent {
    /**
     * UNIX timestamp of the creation time of the edge
     */
    private long timeCreated;
    /**
     * Source node
     */
    private Node src;
    /**
     * Destination node
     */
    private Node dst;
    /**
     * The context of the edge
     */
    private Context context;
    /**
     * The list of interactions
     */
    private ArrayList<Interaction> interactions;

    /**
     * Constructor method
     * @param src The source node of the edge
     * @param dst The destination node of the edge
     * @param identifier Id of the edge
     * @throws NullPointerException if src or dst are null
     */
    Edge(Node src, Node dst, Context context)
    {
        if(src == null || dst == null) Utils.error(new NullPointerException());
        this.timeCreated = Utils.getCurrentTimestamp();
        this.src = src;
        this.dst = dst;
        this.context = context;
        interactions = new ArrayList<Interaction>();
    }

    /**
     * Used in deserialization
     */
    protected Edge()
    {}

    /**
     * @return The source node of the edge
     */
    public Node getSrc() {
        return src;
    }

    /**
     * @return The destination node of the edge
     */
    public Node getDst() {
        return dst;
    }
    
    /**
     * @return the ego node of the edge context's ego network if it's a member of the edge, null otherwise
     */
    public Node getEgo() {
    	Node ego = getContext().getContextualEgoNetwork().getEgo();
    	if(ego==src || ego==dst)
    		return ego;
    	return null;
    }
    
    /**
     * @return the edge node that is not the ego of the edge context's ego network
     * @exception runtime exception if {@link #getEgo()} is null
     */
    public Node getAlter() {
    	Node ego = getContext().getContextualEgoNetwork().getEgo();
    	if(ego==src)
    		return dst;
    	if(ego==dst)
    		return src;
    	Utils.error("Cannot retrieve alter for an edge that doesn't contain the ego");
    	return null;
    }

    /**
     * @return The context
     */
    public Context getContext() {
        return context;
    }

    /**
     * @return The timestamp of the edge
     */
    public long getCreationTime() {
        return this.timeCreated;
    }
    

    /**
     * Adds a new interaction with no duration on this edge
     * @param type The type of the interaction
     * @return the created interaction
     */
    public Interaction addDetectedInteraction(String type) {
    	return addInteraction(Utils.getCurrentTimestamp(), 0, type);
    }

    /**
     * Creates and adds a new interaction on this edge
     * @param timestamp The start timestamp of the interaction
     * @param duration The duration of the interaction
     * @param type The data stored in the interaction
     * @return the created interaction
     */
    public Interaction addInteraction(long timestamp, int duration, Object data) {
        if(timestamp < 0 || duration < 0) Utils.error(new IllegalArgumentException("Timestamp and duration cannot be negative"));
        Interaction interaction = new Interaction(this, timestamp, duration, data);
        interactions.add(interaction);
        return interaction;
    }

    /**
     * @return The weight of the edge. i.e. the tie strength between the source and the destination
     *          (the number of interactions that have taken place on this edge divided by the life span of the edge)
     */
    public double getTieStrength() {
        long now = Utils.getCurrentTimestamp();
        double elapsed = (double) (now - this.timeCreated);
        if(elapsed==0)
        	return 0;
        return (double) (this.interactions.size()) / (double) (now - this.timeCreated);
    }

    /**
     * @return A shallow copy of the edge's interaction list
     */
    public ArrayList<Interaction> getInteractions() {
    	return new ArrayList<Interaction>(interactions);
    }
    
}