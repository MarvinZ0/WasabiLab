package lab2;

import sun.reflect.annotation.TypeNotPresentExceptionProxy;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Skeleton code for a type extractor.
 */
public class TypeExtractor {

    /**
     Given as argument a Wikipedia file, the task is to run through all Wikipedia articles,
     and to extract for each article the type (=class) of which the article
     entity is an instance. For example, from a page starting with "Leicester is a city",
     you should extract "city".

     * extract the longest possible type ("American rock-and roll star") consisting of adjectives,
     nationalities, and nouns
     * if the type cannot reasonably be extracted ("Mathematics was invented in the 19th century"),
     skip the article (do not output anything)
     * take only the first item of a conjunction ("and")
     * do not extract provenance ("from..", "in..", "by.."), but do extract complements
     ("body of water")
     * do not extract too general words ("type of", "way", "form of"), but resolve like a
     human ("A Medusa  is one of two forms of certain animals" -> "animals")
     * keep the plural

     The output shall be printed to the screen in the form
     entity TAB type NEWLINE
     with one or zero lines per entity.
     */
    public String filtr(String content,String re){
        String Re="";
        if(re=="sub_clause")
                Re = " (that|which|when|what|where|why|who) .*";
        if(re=="sub") {
            Re = ".* (is|are|was|were) ";
            Pattern re_s = Pattern.compile(Re);
            Matcher m_s = re_s.matcher(content);
            content = m_s.replaceFirst("");
            return content;
        }
        if(re=="preposition")
                Re = " (for|in|to|and|or|into|on|by|at|along|over|from)( .*|$)";
        if(re=="article")
                Re = "(a|an|the|one|any|some) ((.* )?(type|form|way|unit) of)?";
        if(re=="comma")
                Re = ", .*";
        if(re=="fullStop")
                Re = "[.]";
        Pattern re_s = Pattern.compile(Re);
        Matcher m_s = re_s.matcher(content);
        content = m_s.replaceAll("");
        return content;

    }
    public static void main(String args[]) throws IOException {
        args = new String[] { "/Users/Zheng/Desktop/D_K/Session2/DataKnowledgeBase/WasabiLab2/src/lab2/wikipedia-first.txt" };
        try (Parser parser = new Parser(new File(args[0]))) {
            while (parser.hasNext()) {
                Page nextPage = parser.next();
                String type=null;
                // Magic happens here

                String Mode = ".* +(is|are|was|were) +(a|an|the|any|some) +.*";
                Pattern p = Pattern.compile(Mode);
                Matcher m = p.matcher(nextPage.content);
                if(m.find()){

                    TypeExtractor te = new TypeExtractor();
                    String test = te.filtr(nextPage.content,"sub_clause");
                    test = te.filtr(test,"sub");
                    test = te.filtr(test,"comma");
                    test = te.filtr(test,"preposition");
                    test = te.filtr(test,"article");
                    test = te.filtr(test,"fullStop");
                    type = test;
                }
                else{
                    type=null;
                    continue;
                }


                if(type!=null) System.out.println(nextPage.title+"\t"+type);
            }
        }
    }

}