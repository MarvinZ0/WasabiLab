package lab5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Arrays;
import java.util.ListIterator;

/**
 * Empty skeleton for a type extractor with POS tags.
 *
 * @author Fabian M. Suchanek
 */
public class POSTypeExtractor {

    /**
     * Given a POS-tagged Wikipedia article, returns the type (=class) of which
     * the article entity is an instance. For example, from a page starting with
     * "Leicester/NNP is/VBZ a/DT city/NN in/IN the/DT Midlands/NNP", you should
     * extract "city".
     *
     * - extract the longest possible type ("American rock star")
     * consisting of adjectives and nouns, including nationalities
     * - do not extract provenance or specifics ("from...", "in...", "by...")
     * - do not extract too general words ("type of", "way", "form of"), but resolve like
     * a human ("A Medusa is one of two forms of certain animals" -> "animals")
     * - keep the plural
     * - do not restrict the output to hard-coded types
     * - in case of uncertainty, skip the article by returning NULL
     */
    public static String findType(Page nextPage) {
        String[] taggedText = nextPage.content.split(" ");
        String result =" ";
        boolean startRecord = false;
//        boolean multiNN = true;
        List<String> startTag = Arrays.asList("VBZ","VBP","VBN","VBD");
        List<String> stopTag = Arrays.asList("NN","NNS","NNP","NNPS");
        List<String> TagToRemove = Arrays.asList("RBS","DT",".");
        List<String> words = Arrays.asList(nextPage.content.split(" "));
        ListIterator<String> Itword = words.listIterator();

        while(Itword.hasNext()){
            String WordAndTag = Itword.next();
            if(startTag.contains(WordAndTag.split(".*/")[1])
                    || startRecord == true){
                if(startRecord == false){startRecord = true;}
                if(TagToRemove.contains(WordAndTag.split(".*/")[1])
                        || startTag.contains(WordAndTag.split(".*/")[1])){continue;}
                else{result = result + " " + WordAndTag.split("/[^a-z0-9]+")[0];
                }

            }
            if(stopTag.contains(WordAndTag.split(".*/")[1]) && startRecord == true){
                String NextWordAndTag = Itword.next();
                if(stopTag.contains(NextWordAndTag.split(".*/")[1])){
                    Itword.previous();
                }
                else{
//                    startRecord=false;
                    break;
                }
            }
        }

//        for(String taggedWord:taggedText){
//
//
//
////            if(startRecord==true){result = result + " " + taggedWord.split("/[^a-z0-9]+")[0].toLowerCase();}
////            if(startRecord==false && multiNN == false){break;}
////            if(stopTag.contains(taggedWord.split(".*/")[1])==false
////                    && startRecord == false && multiNN == true){multiNN = false;}
////            if(startTag.contains(taggedWord.split(".*/")[1])
////                    && startRecord == true){startRecord = true;continue;}
////            if(stopTag.contains(taggedWord.split(".*/")[1])){
////                if(startRecord == true){startRecord=false;}
////                if(startRecord==false && multiNN==true){result = result + " " + taggedWord.split("/[^a-z0-9]+")[0].toLowerCase();}
////            }
//
//
//
////            When read the startTag,Turn switch to True and start to record
//            if(startTag.contains(taggedWord.split(".*/")[1]) || startRecord==true){
//                //Turn switch to True if it was false.
//                if(startRecord == false){startRecord = true;}
//
//                //When read the TagToRemove, do nothing and continue.
//                if(TagToRemove.contains(taggedWord.split(".*/")[1])
//                        || startTag.contains(taggedWord.split(".*/")[1])){continue;}
//                else{result = result + " " + taggedWord.split("/[^a-z0-9]+")[0].toLowerCase();
//                }
//
//            }
//
//            //When read the stopTag, Turn switch to False and record the last one parttern then break the loop.
//            if(stopTag.contains(taggedWord.split(".*/")[1]) && startRecord == true){
//                startRecord = false;
////                if(multiNN==true){
////                result = result + " " + taggedWord.split("/[^a-z0-9]+")[0].toLowerCase();}
////                else{
//                break;
//            }
//        }
        return result;
    }

    /**
     * Given as arguments (1) a POS-tagged Wikipedia and (2) a target file, writes "Title
     * TAB class NEWLINE" to the target file
     */
    public static void main(String args[]) throws IOException {
        // Uncommment the following line for your convenience. Comment it out
        // again before submitting!
//        args = new String[] { "src/lab5/wikipedia-first-pos.txt", "src/lab5/my_output_file.txt" };
        try (Parser parser = new Parser(new File(args[0]))) {
            try (Writer result = new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8")) {
                while (parser.hasNext()) {
                    Page nextPage = parser.next();
                    String type = findType(nextPage);
                    if (type == " ") continue;
                    result.write(nextPage.title + "\t" + type + "\n");
                }
            }
        }
    }

}