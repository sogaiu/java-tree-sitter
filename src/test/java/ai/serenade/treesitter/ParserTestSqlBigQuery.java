package ai.serenade.treesitter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.Test;

public class ParserTestSqlBigQuery extends TestBase {

  @Test
  void testParseSqlBigQuery() throws UnsupportedEncodingException {
    try (Parser parser = new Parser()) {
      parser.setLanguage(Languages.sql_bigquery());
      try (Tree tree = parser.parseString("DROP MODEL IF EXISTS `awesome_bigquery.model`;")) {
        assertEquals(
          "(source_file (drop_model_statement (keyword_if_exists) model_name: (identifier)))",
          tree.getRootNode().getNodeString()
        );
      }
    }
  }
}
