package lab4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Skeleton for a Viterbi POS tagger.
 *
 */
public class Viterbi {

    /** HMM we'll use */
    protected HiddenMarkovModel model;

    /** Constructs the parser from a model file */
    public Viterbi(File modelFile) throws FileNotFoundException, IOException {
        model = new HiddenMarkovModel(modelFile);
    }

    /** Parses a sentence and returns the list of POS tags */
    public List<String> parse(String sentence) {
        List<String> words = Arrays.asList((". " + sentence.toLowerCase() + " .").split(" "));
        int numWords = words.size();
        List<String> tags = new ArrayList<String>(model.emissionProb.keySet());
        int numTags = tags.size();
        // Smart things happen here!

        Double TagAndWord[][] = new Double[numTags][numWords];
        int TagPath[][] = new int[numTags][numWords];
        //WordProba[PrevTag][Tag][Word]
        //Double Tag_Tag_word[][][] = new Double[numTags][numTags][numWords];
        ListIterator<String> ItWords = words.listIterator();
        while(ItWords.hasNext()){
            String word = ItWords.next();
            int IndexOfWord = ItWords.previousIndex();
            ListIterator<String> ItTags = tags.listIterator();
            while(ItTags.hasNext()){
                Double Proba = .0;
                Double p = .0;
                int PrevTagIndex = 0;
                String tag = ItTags.next();
                int IndexOfTag = ItTags.previousIndex();
                if(IndexOfWord>1){
                    ListIterator<String> ItPrevTags = tags.listIterator();
                    while(ItPrevTags.hasNext()){
                        String PrevTag = ItPrevTags.next();
                        int IndexOfPrevTag = ItPrevTags.previousIndex();
                        if(model.emissionProb.get(tag).get(word)==null
                                ||model.transitionProb.get(PrevTag).get(tag)==null){p=.0;}
                        else{
                            p = TagAndWord[IndexOfPrevTag][IndexOfWord-1]
                                    * model.emissionProb.get(tag).get(word)
                                    * model.transitionProb.get(PrevTag).get(tag);
                        }
                        if(p>Proba){
                            Proba = p;
                            PrevTagIndex = IndexOfPrevTag;
                        }
                    }

                }
                else{
                    if(model.emissionProb.get(tag).get(word)!=null) {
                        Proba = model.emissionProb.get(tag).get(word);
                    }
                }
                TagAndWord[IndexOfTag][IndexOfWord] = Proba;
                TagPath[IndexOfTag][IndexOfWord] = PrevTagIndex;
            }
        }


        List<String> ReversedTagging = new ArrayList<>();

        int Winner=0;
        Double cmp = .0;
        for(int i=0;i<numTags;i++){
            if(TagAndWord[i][numWords-1]>cmp){
                cmp = TagAndWord[i][numWords-1];
                Winner = i;
            }
        }
        ReversedTagging.add(tags.get(Winner));
        for(int j=numWords-1;j>1;j--)
        {
            ReversedTagging.add(tags.get(TagPath[Winner][j]));
            Winner = TagPath[Winner][j];
        }
        Collections.reverse(ReversedTagging);
        return ReversedTagging;
    }


    /**
     * Given (1) a Hidden Markov Model file and (2) a sentence (in quotes),
     * prints the sequence of POS tags
     */
    public static void main(String[] args) throws Exception {
        args = new String[] {"src/lab4/target_file.txt","Elvis is in Krzdgwzy"};
        System.out.println(new Viterbi(new File(args[0])).parse(args[1]));
    }
}
