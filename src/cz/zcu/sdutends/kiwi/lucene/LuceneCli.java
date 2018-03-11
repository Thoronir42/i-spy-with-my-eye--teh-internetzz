package cz.zcu.sdutends.kiwi.lucene;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LuceneCli {
    private OnQuery onQuery;

    private String promptApp = "Entities";

    public LuceneCli() {
    }

    public LuceneCli setPromptApp(String promptApp) {
        this.promptApp = promptApp;
        return this;
    }

    public LuceneCli setOnQuery(OnQuery onQuery) {
        this.onQuery = onQuery;
        return this;
    }

    public int start(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String query;
        int queriesExecuted = 0;
        Pattern p = Pattern.compile("(\\d+)\\|.*");

        try {
            while ((query = prompt(br)) != null) {
                if ("/exit".equals(query)) {
                    break;
                }
                int page = 1;
                Matcher matcher = p.matcher(query);
                if (matcher.matches()) {
                    page = Integer.parseInt(matcher.group(1));
                    query = query.substring(matcher.end(1) + 1);
                }
                this.onQuery.apply(query, page);
                queriesExecuted++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return queriesExecuted;
    }

    private String prompt(BufferedReader br) throws IOException {
        System.out.print("Lucene[" + promptApp + "] >");
        return br.readLine();
    }
}
