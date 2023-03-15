package ai.serenade.treesitter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.Test;

public class ParserTestSql extends TestBase {

  @Test
  void testParseSql() throws UnsupportedEncodingException {
    try (Parser parser = new Parser()) {
      parser.setLanguage(Languages.sql());
      try (Tree tree = parser.parseString("SELECT 1 + 2, NULL, col1::INT, a <> b, TRUE, false WHERE a = b;")) {
        assertEquals(
          "(source_file (select_statement (select_clause (select_clause_body (binary_expression left: (number) right: (number)) (NULL) (type_cast (identifier) type: (type (identifier))) (binary_expression left: (identifier) right: (identifier)) (TRUE) (FALSE))) (where_clause (binary_expression left: (identifier) right: (identifier)))))",
          tree.getRootNode().getNodeString()
        );
      }
    }
  }
}
