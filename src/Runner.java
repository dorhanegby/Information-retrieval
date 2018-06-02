import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Runner {

    public static void main(String[] args) throws IOException {
       File paramFile = new File(args[0]);
        Map<String,String> params = Helpers.getParams(paramFile);
        File outputFile = new File(params.get("outputFile"));
        File queryFile = new File(params.get("queryFile"));
        File docsFile = new File(params.get("docsFile"));
        String searchAlgo = params.get("retrievalAlgorithm");
        Map<Integer,String> documentsMap = Helpers.basicParser(docsFile,"*TEXT");
        Map<Integer,String> queriesMap = Helpers.basicParser(queryFile, "*FIND");
        System.out.println("Fin");

    }
}

