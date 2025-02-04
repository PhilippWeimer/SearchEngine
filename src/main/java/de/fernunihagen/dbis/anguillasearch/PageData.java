package de.fernunihagen.dbis.anguillasearch;

import java.util.*;
 
/** 
 * The class "PageData" is used to store and manage the details of a webpage.
 */
 
public class PageData {
    private String title;
    private String header;
    private String content;
    private String docID; 
    private String url; 
    private double pageRank = 0.0;
    private double score; 
    private List<String> tokens = new ArrayList<>();
    private Set<String> links = new HashSet<>();
    private Map <String, Double> tfIdfVektor = new HashMap<>();
 
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }  
    
    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setID(String docID) {
        this.docID = docID;
    }

    public String getID() {
        return docID;
    }

    public void setURL(String url){
        this.url = url;
    }

    public String getURL() {
        return url; 
    }

    public void setPageRank(double pageRank) {
        this.pageRank = pageRank;
    }

    public double getPageRank() {
        return pageRank;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public List<String> getTokens() {
        return tokens;
    }  

    public void addLink(String link) {
        if (link != null && !link.isEmpty()) {
            this.links.add(link);
        }
    }
    
    public Set<String> getLinks() {
        return links;
    }

    public void addTfIdf(String token, double tfidf) {
        tfIdfVektor.put(token, tfidf);
    }

    public Map<String, Double> getTfIdfVektor() {
        return tfIdfVektor;
    }

    public double getScore() {
        return score;
    } 

    public void setScore(double score) {
        this.score = score;
    }
}