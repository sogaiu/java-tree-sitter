import ai.serenade.treesitter.*;

import java.io.UnsupportedEncodingException;

class jts {

    static {
        System.load(System.getenv("JAVA_TREE_SITTER"));
    }

    public static void main(String[] args)
    {
        //System.out.println(System.getenv("JAVA_TREE_SITTER"));
        try (Parser parser = new Parser()) {
            parser.setLanguage(Languages.sql_bigquery());
            try (Tree tree = parser.parseString("DROP MODEL IF EXISTS `awesome_bigquery.model`;")) {
                System.out.println(tree.getRootNode().getNodeString());
            }
            catch (java.io.UnsupportedEncodingException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
