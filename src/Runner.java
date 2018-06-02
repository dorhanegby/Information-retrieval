import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Runner {

    public static void main(String[] args) throws Exception {
       File paramFile = new File(args[0]);
        Map<String,String> params = Helpers.getParams(paramFile);
        File outputFile = new File(params.get("outputFile"));
        File queryFile = new File(params.get("queryFile"));
        File docsFile = new File(params.get("docsFile"));
        String searchAlgo = params.get("retrievalAlgorithm");
        Map<String,String> documentsMap = Helpers.basicParser(docsFile,"*TEXT");
        Map<String,String> queriesMap = Helpers.basicParser(queryFile, "*FIND");

        IndexDocs index = new IndexDocs(documentsMap);

        Map<String,List> results;
        Retriever masterRetriever = new Retriever(index.getIndex(),index.getAnalyzer());

        results = masterRetriever.retrieve(queriesMap);
        Outputter.output(outputFile,results);


        System.out.println("Fin");

    }
}

