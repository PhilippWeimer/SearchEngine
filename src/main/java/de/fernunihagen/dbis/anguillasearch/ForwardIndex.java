package de.fernunihagen.dbis.anguillasearch;

import java.util.*;

/**  
 * The class "ForwardIndex" maps Document-IDs from urls to the tokens they contain.
 */

public class ForwardIndex {
    
    // Map representing the Forward Index. Key is the Document-ID (String) and the Value is a Vector of terms and weights.
    private Map<String, Vector> forwardIndexValues = new HashMap<>();
  
    /** 
     * Adds a new entry to the Forward Index
     * 
     * @param id  the Document-ID 
     * @param vektor  the vektor containing terms and weights for the document
     */ 
 
    public void createForwardIndex (String id, Vector vektor) {
        forwardIndexValues.put(id, vektor); 
    }

    /**
     * Returns the vector associated with a specific Document-ID
     * 
     * @param id  the Document-ID
     * 
     * @return  the vector for the given Document-ID.
     */

    public Vector getForwardIndex (String id){
        return forwardIndexValues.get(id);
    }

    /**
     * @return  the entire Forward Index as a Map.
     */

    public Map<String, Vector> getAllVectors() {
        return forwardIndexValues;
    }
}
