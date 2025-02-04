package de.fernunihagen.dbis.anguillasearch;
 
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties; 
 
/** 
 * The class "Lemmatizer" uses the Standford CoreNLP library to perform lemmatization. 
 * Lemmatization reduces words into their base form. For example: The word "happier" is reduces into "happy".
 * This is important for natural languagae processing tasks, like in this search machine.
 */
  
public class Lemmatizer {

    // Stanford CoreNLP pipeline used for processing text 
    private StanfordCoreNLP pipeline;
 
    /** 
     * Constructs a Lemmatizer instance.
     * Stanford CoreNLP pipelie is initialized with the following necessary annotators:
     * "tokenize" is used for splitting the text into tokens
     * "ssplit" is used for sentece splitting 
     * "pos" is used for part-of-speech tagging 
     * "lemma" is used for lemmatization 
     */
 
    public Lemmatizer() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        this.pipeline = new StanfordCoreNLP(props);
    }

    /** 
     * Lemmatizes a given list of tokens and processes them using the CoreNLP pipeline.
     * 
     * @param tokens  a list of Strings representing the tokens which should be lemmatized.
     * 
     * @return  a list of the lemmatized input tokens. 
     */

    public List<String> lemmatize(List<String> tokens) {
        List<String> lemmas = new ArrayList<>();

        Annotation document = new Annotation(String.join(" ", tokens));
        
        pipeline.annotate(document);

        for (CoreLabel token : document.get(CoreAnnotations.TokensAnnotation.class)) {
            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            lemmas.add(lemma);
        }
        
        return lemmas;
    }
}
