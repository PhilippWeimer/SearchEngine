package de.fernunihagen.dbis.anguillasearch;

import java.util.*;
 
/** 
 * The class "Cosine" calculates the Cosine similarity between two vectors.
 */
 
public class Cosine {
 
    private Cosine() {
        throw new UnsupportedOperationException("This class can`t be instantiated");
    }
  
    /**
     * The Cosine similarity is calculated as the dot product of the two vectors divided by the product of their magnitudes.
     * 
     * @param vector1  the first vector
     * @param vector2  the second vector
     * 
     * @return  the calculated Cosine similarity. The value reaches from -1 to 1.
     */

    public static double cosineSimilarity(Vector vector1, Vector vector2) {

        // Retrieve the token weights from both vectors.
        Map<String, Double> metrics1 = vector1.getAllMetrics();
        Map<String, Double> metrics2 = vector2.getAllMetrics();
 
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosine;

        // Calculates the dot product for the numerator
        for (String token : metrics1.keySet()) {
            double value1 = metrics1.getOrDefault(token, 0.0);
            double value2 = metrics2.getOrDefault(token, 0.0);
 
            dotProduct += value1 * value2;
        }

        // Calculates the magnitude of the first vector
        for (double value1 : metrics1.values()) {
            magnitude1 += value1 * value1; 
        }

        // Calculates the magnitude of the second vector
        for (double value2 : metrics2.values()) {
            magnitude2 += value2 * value2;
        }

        // Computes the square roots to finalize the magnitues
        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        // Calculates the Cosine similariy
        cosine = dotProduct / (magnitude1 * magnitude2);

        return cosine;
    }
}
