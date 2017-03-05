package lab3;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.StringJoiner;
import java.io.PrintWriter;


/**
 * Skeleton class to perform disambiguation
 *
 * @author Jonathan Lajus
 *
 */
public class Disambiguation {

    /** This program takes 3 command line arguments, namely the paths to:
     - yagoLinks.tsv
     - yagoLabels.tsv
     - wikipedia-ambiguous.txt
     in this order. You may also ignore the last argument at your will.
     The program prints statements of the form:
     <pageTitle>  TAB  <yagoEntity> NEWLINE
     It is OK to skip articles.
     */
    public static void main(String[] args) throws IOException {
        String[] paths = new String[3];
        paths[0] = "src/lab3/yago-ambiguous/yagoLinks.tsv";
        paths[1] = "src/lab3/yago-ambiguous/yagoLabels.tsv";
        paths[2] = "src/lab3/wikipedia-ambiguous.txt";
        if (paths.length < 3) {
            System.err.println("usage: Disambiguation <yagoLinks> <yagoLabels> <wikiText>");
            return;
        }
        File dblinks = new File(paths[0]);
        File dblabels = new File(paths[1]);
        File wiki = new File(paths[2]);

        SimpleDatabase db = new SimpleDatabase(dblinks, dblabels);
        PrintWriter writer = new PrintWriter("src/lab3/result.tsv", "Unicode");


        try (Parser parser = new Parser(wiki)) {
            while (parser.hasNext()) {
                Page nextPage = parser.next();
                String pageTitle = nextPage.title; // "Clinton_1"
                String pageContent = nextPage.content; // "Hillary Clinton was..."
                String pageLabel = nextPage.label(); // "Clinton"
                String correspondingYagoEntity = "<For_you_to_find>";

                /**
                 * TODO CODE HERE to disambiguate the entity.
                 */

                Set<String> Entities = (Set<String>) db.reverseLabels.get(pageLabel);
                for(String entity:Entities)
                {
                    float F1score = 0;
                    float precision = 0;
                    float recall = 0;
//                    System.out.println(entity);
                    Set<String> Links = (Set<String>) db.links.get(entity);
                    for(String link:Links)
                    {
//                        System.out.println(link);
                        if(db.labels.get(link)!=null)
                        {
                            Set<String> Labels = (Set<String>) db.labels.get(link);
                            for(String label:Labels)
                            {
                                if(label!=null) {
                                    int count = 0;
                                    float F1sco = 0;
//                                    System.out.println(label);
                                    String[] desc = label.toLowerCase().replace(",|;","").split(" ");
                                    float lenOfContent = pageContent.replace(",|;","").split(" ").length;
                                    float lenOfDesc = desc.length;
//                                    for(String des:desc)
//                                        System.out.print(des+"\t");
//                                    System.out.println();
                                    for(String des:desc)
                                    {
                                        if(pageContent.contains(des))
                                        {
                                            count+=1;
//                                            if(count>match)
//                                            {
//                                                match = count;
//                                                correspondingYagoEntity = entity;
//                                            }
                                        }
                                    }
                                    precision = count/lenOfDesc;
                                    recall = count/lenOfContent;
                                    F1sco = 2*(precision*recall)/(precision+recall);
                                    if(F1sco>F1score)
                                    {
                                        F1score = F1sco;
                                        correspondingYagoEntity = entity;
                                    }
                                }
                            }
                        }
                    }
                }
//                System.out.println(pageTitle + "\t" + correspondingYagoEntity + "\n");
                writer.println(pageTitle + "\t" + correspondingYagoEntity);


//
//                Iterator iterEnti = db.reverseLabels.get(pageLabel).iterator();
//                while(iterEnti.hasNext())
//                {
//
//                    if(iterEnti.next()!=null)
//                    {
//                        Iterator iterLinks = db.links.get(iterEnti.next()).iterator();
//                        while(iterLinks.hasNext())
//                        {
//
//                            if(db.labels.get(iterLinks.next())!=null)
//                            {
//                                Iterator iterLab = db.labels.get(iterLinks.next()).iterator();
//                                int count = 0;
//                                while (iterLab.hasNext())
//                                {
//                                    int tempo = 0;
//                                    if(iterLab.next()!=null) {
//                                        System.out.println(iterLab.next());
////                                        String[] Words = iterLab.next().toString().toLowerCase().split(",");
////                                        for(String word:Words)
////                                        {
////                                            if(pageContent.contains(word))
////                                            {
////                                                tempo += 1;
////                                            }
////                                        }
////                                        if(tempo>count)
////                                        {
////                                            count = tempo;
////                                            correspondingYagoEntity = iterLab.next().toString();
////                                        }
////                                        correspondingYagoEntity = iterLab.next().toString();
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                }
//
//
//                System.out.println(pageTitle + "\t" + correspondingYagoEntity + "\n");
            }
        }
    }
}