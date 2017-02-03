package ca.uwaterloo.stock_trends_analyzer.utils;

import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.ner.NERAnnotator;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NERHelper
{
    private static Logger log = LogManager.getLogger(NERHelper.class);
    private NERAnnotator nerAnnotator;

    public NERHelper()
        throws IOException
    {
        log.info("NERAnnotator initializing");
        nerAnnotator = new NERAnnotator(ViewNames.NER_CONLL);
        nerAnnotator.doInitialize();
        log.info("NERAnnotator initialized");
    }

    public List<String> extractEntities(String sentence, String corpus, String textId)
    {
        TextAnnotationBuilder annotationBuilder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
        TextAnnotation textAnnotation = annotationBuilder.createTextAnnotation(corpus, textId, sentence);

        nerAnnotator.addView(textAnnotation);
        View taggedText = textAnnotation.getView(ViewNames.NER_CONLL);

        List<Constituent> constituents = taggedText.getConstituents();
        log.info(constituents);
        List<String> orgs = new ArrayList<>();

        for (Constituent constituent : constituents)
        {
            orgs.add(constituent.toString());
        }

        return orgs;
    }
}
