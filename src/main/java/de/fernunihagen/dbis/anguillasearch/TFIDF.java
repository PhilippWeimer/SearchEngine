package de.fernunihagen.dbis.anguillasearch;

import java.util.*;

/* 
 * The class "TFIDF" calculates the Term-Frequency values for the tokens in a document. 
 */ 

public class TFIDF {

    /* 
     * Private constructor is needed to prevent instantiation, because this is a utility class. 
     */ 

    private TFIDF() {
        throw new UnsupportedOperationException("This class can`t be instantiated");
    } 

    /**
     * Calculates the TF-IDF values for a given list of tokens in a document. 
     * 
     * @param tokens  a list of tokens from a document 
     * @param totalDocuments  the total number of documents in the intranet
     * @param documentFrequencies  a Map containing the document frequencies of each token in the intranet.
     * 
     * @return  a Map with tokens as keys and their TF-IDF-values as values. 
     */

    public static Map<String, Double> calculate(List<String> tokens, 
    int totalDocuments, Map<String, Integer> documentFrequencies) {

        Map<String, Integer> termFrequencies = new HashMap<>();

        for (String token : tokens) {
            termFrequencies.put(token, termFrequencies.getOrDefault(token, 0) + 1);
        }

        int totalWordsInDoc = tokens.size();
        Map<String, Double> tfidfValues = new HashMap<>();

        for (Map.Entry<String, Integer> entry : termFrequencies.entrySet()) {
            String token = entry.getKey();

            int documentFrequency = documentFrequencies.get(token);

            double tf = (double) termFrequencies.get(token) / totalWordsInDoc;
            double idf = Math.log((double) totalDocuments / (documentFrequency));
            double tfidf = tf * idf;

            tfidfValues.put(token, tfidf);
        }

        return tfidfValues;
    }
}