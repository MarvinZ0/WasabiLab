package lab5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.ListIterator;

/**
 * Empty skeleton for a location extractor with POS tags.
 *
 * @author Fabian M. Suchanek
 */
public class POSLocationExtractor {

    /**
     * Given a POS-tagged Wikipedia article, returns extract wherever possible
     * the location of a geographic entity. For example, from a page starting
     * with "Leicester/NNP is/VBZ a/DT city/NN in/IN the/DT Midlands/NNP", you
     * should extract "Midlands".
     *
     * Try to extract exactly the region, country, or
     * city. Do not extract locations for non-geographical entities.
     * You can also skip articles by returning NULL.
     */
    public static String findLocation(Page nextPage) {
        String[] taggedText = nextPage.content.split(" ");
        String result =" ";
        boolean startRecord = false;
//        boolean multiNN = false;

        List<String> words = Arrays.asList(nextPage.content.split(" "));

        List<String> startTag = Arrays.asList("IN");
        List<String> stopTag = Arrays.asList("NN","NNS","NNP","NNPS");
        List<String> TagToRemove = Arrays.asList("RBS","DT",".");

        ListIterator<String> Itword = words.listIterator();

        while(Itword.hasNext()){
            String WordAndTag = Itword.next();
            if((startTag.contains(WordAndTag.split(".*/")[1]) && WordAndTag.split("/[^a-z0-9]+")[0].equals("in"))
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
//            if((startTag.contains(taggedWord.split(".*/")[1]) && taggedWord.split("/[^a-z0-9]+")[0].equals("in"))
//                    || startRecord==true){
//                //Turn switch to True if it was false.
//                if(startRecord == false){startRecord = true;}
//
//                //When read the TagToRemove, do nothing and continue.
//                if(TagToRemove.contains(taggedWord.split(".*/")[1])
//                        || startTag.contains(taggedWord.split(".*/")[1])){continue;}
//                else{result = result + " " + taggedWord.split("/[^a-z0-9]+")[0];
//                }
//
//            }
//
//            //When read the stopTag, Turn switch to False and record the last one parttern then break the loop.
//            if(stopTag.contains(taggedWord.split(".*/")[1]) && startRecord == true){
//                startRecord = false;
//                if(multiNN==true){
//                result = result + " " + taggedWord.split("/[^a-z0-9]+")[0].toLowerCase();}
////                else{
////                    break;}
//            }
//            if(!stopTag.contains(taggedWord.split(".*/")[1]) && startRecord == false && multiNN == true){
//                multiNN = false;
//                break;
//            }
//
//
//
//        }


        return result;
    }

    /**
     * Given as arguments (1) a POS-tagged Wikipedia and (2) a target file,
     * writes "Title TAB location NEWLINE" to the target file
     */
    public static void main(String args[]) throws IOException {
        // Uncommment the following line for your convenience. Comment it out
        // again before submitting!
        // args = new String[] { "c:/fabian/data/wikipedia/wikipedia_pos.txt", "c:/fabian/data/my-results.txt" };
//        args = new String[] { "src/lab5/wikipedia-first-pos.txt", "src/lab5/my_output_file_location.txt" };
        try (Parser parser = new Parser(new File(args[0]))) {
            try (Writer result = new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8")) {
                while (parser.hasNext()) {
                    Page nextPage = parser.next();
                    String type = findLocation(nextPage);
                    if (type == " ") continue;
                    result.write(nextPage.title + "\t" + type + "\n");
                }
            }
        }
    }

}