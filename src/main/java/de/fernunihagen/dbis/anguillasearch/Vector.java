package de.fernunihagen.dbis.anguillasearch;

import java.util.*;

/*
 * The class "Vector" is a datastructure, which represents a vector of tokens. Each token in the vector 
 * is associated with a specific weight of its related TF-IDF value. 
 */

public class Vector {
    private Map<String, Double> tokenMetrics;

    public Vector() {
        tokenMetrics = new HashMap<>();
    }
  
    public void addToken(String token, double weight) {
        tokenMetrics.put(token, weight);
    }

    public double getMetrics(String token) {
        return tokenMetrics.getOrDefault(token, 0.0);
    }

    public Map<String, Double> getAllMetrics() {
        return tokenMetrics;
    }
}
