import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Helpers {
    public static Map<String,String> getParams(File paramFile) throws IOException {
        Map<String, String> params = new HashMap<>();
        List<String> lines = Files.readAllLines(paramFile.toPath());
        for (String line : lines) {
            String[] opts = line.split("=");
            if(opts.length<2)
            {
                throw new Error("Not matching format");
            }
            params.put(opts[0], opts[1]);
        }
        return params;
    }

}
