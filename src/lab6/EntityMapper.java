package lab6;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.List;
import java.util.Arrays;

/**
 * Skeleton class for a program that maps the entities from one KB to the
 * entities of another KB.
 *
 * @author Fabian
 *
 */
public class EntityMapper {

    /**
     * Takes as input (1) one knowledge base (2) another knowledge base and (3)
     * an output file.
     *
     * Writes into the output file "entity1 TAB entity2 NEWLINE", if the first
     * entity from the first knowledge base is the same as the second entity
     * from the second knowledge base. Output 0 or 1 line per entity1.
     */
    public static void main(String[] args) throws IOException {
        // Uncomment for your convenience. Comment it again before submission!
        /*
         * args = new String[] {
         * "/Users/fabian/Data/KBC-2016-Entity-Mapping/yago-anonymous.tsv",
         * "/Users/fabian/Data/KBC-2016-Entity-Mapping/dbpedia.tsv",
         * "/Users/fabian/Data/KBC-2016-Entity-Mapping/result.tsv" };
         */
//        args = new String[] {"src/lab6/yago-anonymous.tsv",
//                            "src/lab6/dbpedia.tsv",
//                            "src/lab6/result.tsv",};
        KnowledgeBase kb1 = new KnowledgeBase(new File(args[0]));
        KnowledgeBase kb2 = new KnowledgeBase(new File(args[1]));
//        kb2.tellMeAbout("<Alexander_the_Great>");
//        kb1.tellMeAbout("<Q-1063841239>");
        try (Writer result = new OutputStreamWriter(new FileOutputStream(args[2]), "UTF-8")) {
            for (String entity1 : kb1.facts.keySet()) {
                String mostLikelyCandidate = null;

                for (String entity2 : kb2.facts.keySet()){
                    // Something smart here
                    Set<String> LabelsKb1 = kb1.facts.get(entity1).get("rdfs:label");
                    Set<String> LabelsKb2 = kb2.facts.get(entity2).get("rdfs:label");

                    if(LabelsKb1==null || LabelsKb2==null){
                        break;
                    }

                    for(String i: LabelsKb2){
                        if(LabelsKb1.contains(i)){
                            mostLikelyCandidate = entity2;
//                            System.out.println(entity2);
                        }
                    }
                    if(mostLikelyCandidate!=null){
                        break;
                    }

                }

                if (mostLikelyCandidate != null) {
                    result.write(entity1 + "\t" + mostLikelyCandidate + "\n");
                }
            }
        }
    }
}