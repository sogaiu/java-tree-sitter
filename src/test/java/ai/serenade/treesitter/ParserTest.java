package ai.serenade.treesitter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.Test;

public class ParserTest extends TestBase {

  @Test
  void testParse() throws UnsupportedEncodingException {
    try (Parser parser = new Parser()) {
      parser.setLanguage(Languages.clojure());
      try (Tree tree = parser.parseString("(+ 1 1)")) {
        assertEquals(
          "(source (list_lit value: (sym_lit name: (sym_name)) value: (num_lit) value: (num_lit)))",
          tree.getRootNode().getNodeString()
        );
      }
    }
  }
}
