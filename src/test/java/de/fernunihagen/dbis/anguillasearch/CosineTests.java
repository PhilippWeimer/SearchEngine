package de.fernunihagen.dbis.anguillasearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
 
/**
 * Unit tests for cosine similarity.
 */
  
class CosineTests { 
 
    @Test 
    void equalVectors() {
        // Define two identical vectors
        double[] vectorA = { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] vectorB = { 0.1, 0.2, 0.3, 0.4, 0.5 };

        Vector vektor1 = ArrayToVektor(vectorA);
        Vector vektor2 = ArrayToVektor(vectorB);

        assertEquals(1.0, Cosine.cosineSimilarity(vektor1, vektor2));
    }

    @Test
    void orthogonalVectors() {
        // Define two orthogonal vectors
        double[] vectorA = { 1.0, 0.0, 0.0 };
        double[] vectorB = { 0.0, 1.0, 0.0 };

        Vector vektor1 = ArrayToVektor(vectorA);
        Vector vektor2 = ArrayToVektor(vectorB);

        assertEquals(0.0, Cosine.cosineSimilarity(vektor1, vektor2));
    }

    @Test
    void randomVectors() {
        // Define two random vectors
        double[] vectorA = { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] vectorB = { 0.5, 0.4, 0.3, 0.2, 0.1 };

        Vector vektor1 = ArrayToVektor(vectorA);
        Vector vektor2 = ArrayToVektor(vectorB);

        assertTrue(Cosine.cosineSimilarity(vektor1, vektor2) > 0.0);
        assertTrue(Cosine.cosineSimilarity(vektor1, vektor2) < 1.0);
    }

    @Test
    void specificResults() {
        // Define two vectors with specific values
        double[] vectorA = { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] vectorB = { 0.5, 0.4, 0.3, 0.2, 0.1 };

        Vector vektor1 = ArrayToVektor(vectorA);
        Vector vektor2 = ArrayToVektor(vectorB);

        assertTrue(Math.abs(Cosine.cosineSimilarity(vektor1, vektor2) - 0.6364) < 0.0001);
    }

    /** 
     * Converts an array of doubles into a Vektor
     * 
     * @param array  the array of doubles that should be converted
     * 
     * @return  a Vektor Object representing the array
     */

    public static Vector ArrayToVektor(double[] array) {
        Vector vektor = new Vector();

        for (int i = 0; i < array.length; i++) {
            vektor.addToken("dimension" + i, array[i]);
        }

        return vektor;
    }
}