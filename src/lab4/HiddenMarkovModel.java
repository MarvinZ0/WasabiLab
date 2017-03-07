package lab4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

/**
 * Skeleton for a Hidden Markov Model
 *
 */
public class HiddenMarkovModel {

    /**
     * Stores transition probabilities of the form ADJ -> { NN -> 0.99, VBZ ->
     * 0.01 }. Should sum to 1 for each tag.
     */
    protected Map<String, Map<String, Double>> transitionProb = new TreeMap<String, Map<String, Double>>();

    /**
     * Stores emission probabilities of the form PN -> { "Elvis" -> 0.8,
     * "Priscilla" -> 0.2 }. Should sum to 1 for each tag.
     */
    protected Map<String, Map<String, Double>> emissionProb = new TreeMap<String, Map<String, Double>>();

    /** Retrieves the emission probability for this tag and this word */
    public double getEmissionProbability(String tag, String word) {
        if (!emissionProb.containsKey(tag)) return (0);
        if (!emissionProb.get(tag).containsKey(word)) return (0);
        return (emissionProb.get(tag).get(word));
    }

    /** Retrieves the transition probability for these tags */
    public double getTransitionProbability(String fromTag, String toTag) {
        if (!transitionProb.containsKey(fromTag)) return (0);
        if (!transitionProb.get(fromTag).containsKey(toTag)) return (0);
        return (transitionProb.get(fromTag).get(toTag));
    }

    /**
     * Constructs a Hidden Markov Model from a tagged Wikipedia corpus, i.e,
     * fills the fields transitionProb and emissionProb. Lowercase all words.
     */
    public HiddenMarkovModel(String wikipediaCorpus) throws IOException {
        try (Parser parser = new Parser(new File(wikipediaCorpus))) {
            while (parser.hasNext()) {
                Page nextPage = parser.next();
                String[] wordsWithTags = nextPage.content.split(" ");
                // Magic goes here
//                String Title = nextPage.title;

                String tagPrevious = ".";
                for(String WordAndTag:wordsWithTags)
                {
//                    String Mode = "/[A-Z]+";
//                    Pattern p = Pattern.compile(Mode);
//                    Matcher m = p.matcher(WordAndTag);

//                    String word = m.group(0).toLowerCase();
//                    String tag = m.replaceAll();
                    String word= WordAndTag.split("/[^a-z0-9]+")[0].toLowerCase();
                    String tag = WordAndTag.split(".*/")[1];

                    //update emissionProb,count the frequence of words for tag.
                    if(emissionProb.get(tag) == null) {
                        emissionProb.put(tag, new TreeMap<>());
                    }

                    {
                        if (emissionProb.get(tag).get(word) == null) {
                            emissionProb.get(tag).put(word, 1.0);
                        } else {
                            emissionProb.get(tag).put(word, emissionProb.get(tag).get(word) + 1);
                        }
                    }

                    //update transitionProb, count the frequence of tag follow another one.
                    if(transitionProb.get(tagPrevious) == null) {
                        transitionProb.put(tagPrevious,new TreeMap<>());
                    }

                    {
                        if(transitionProb.get(tagPrevious).get(tag) == null)
                        {
                            transitionProb.get(tagPrevious).put(tag, 1.0);
                        }
                        else
                        {
                            transitionProb.get(tagPrevious).put(tag, transitionProb.get(tagPrevious).get(tag) + 1);
                        }
                    }
                    tagPrevious = tag;
                }
            }
        }

        //Compute the percentage and update to emissionProb
        for(Map.Entry<String, Map<String, Double>> entry: emissionProb.entrySet()) {
            String tag = entry.getKey();
            Map<String, Double> words = entry.getValue();
            Double sum = .0;
            for(Double f : words.values())
            {
                sum += f;
            }
            for(Map.Entry<String,Double> word : words.entrySet())
            {
//                words.put(word.getKey(),(word.getValue()/sum));
                emissionProb.get(tag).put(word.getKey(),(word.getValue()/sum));
            }

        }

        //Compute the percentage and update to transitionProb
        for(Map.Entry<String, Map<String, Double>> entry: transitionProb.entrySet()){
            String tag = entry.getKey();
            Map<String, Double> tags = entry.getValue();
            Double sum = .0;
            for(Double f : tags.values())
            {
                sum += f;
            }
            for(Map.Entry<String,Double> word : tags.entrySet())
            {
//                words.put(word.getKey(),(word.getValue()/sum));
                transitionProb.get(tag).put(word.getKey(),(word.getValue()/sum));
            }
        }
    }

    /** Saves this model to a file */
    public void saveTo(File model) throws IOException {
        try (Writer out = new FileWriter(model)) {
            for (String fromTag : transitionProb.keySet()) {
                Map<String, Double> map = transitionProb.get(fromTag);
                for (String toTag : map.keySet()) {
                    out.write("T\t" + fromTag + "\t" + toTag + "\t" + map.get(toTag) + "\n");
                }
            }
            for (String tag : emissionProb.keySet()) {
                Map<String, Double> map = emissionProb.get(tag);
                for (String word : map.keySet()) {
                    out.write("E\t" + tag + "\t" + word + "\t" + map.get(word) + "\n");
                }
            }
        }
    }

    /**
     * Constructs a Hidden Markov Model from a previously stored model file.
     */
    public HiddenMarkovModel(File model) throws FileNotFoundException, IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(model))) {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                String[] split = line.split("\t");
                if (split[0].equals("T")) {
                    Map<String, Double> map = transitionProb.get(split[1]);
                    if (map == null) transitionProb.put(split[1], map = new TreeMap<>());
                    map.put(split[2], Double.parseDouble(split[3]));
                } else if (split[0].equals("E")) {
                    Map<String, Double> map = emissionProb.get(split[1]);
                    if (map == null) emissionProb.put(split[1], map = new TreeMap<>());
                    map.put(split[2], Double.parseDouble(split[3]));
                }
            }
        }
    }

    /**
     * Given (1) a POS-tagged Wikipedia corpus and (2) a target model file,
     * constructs the model and stores it in the target model file.
     */
    public static void main(String[] args) throws IOException {
//        args = new String[] {"src/lab4/wikipedia-first-pos.txt","src/lab4/target_file.txt"};
        HiddenMarkovModel model = new HiddenMarkovModel(args[0]);
        model.saveTo(new File(args[1]));
    }

}
