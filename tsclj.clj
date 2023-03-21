;; to get this all to work, one needs at least:
;;
;; * libjava-tree-sitter.(so|dylib|dll) (with tree-sitter-clojure bits in it)
;;   * ...and java.library.path jvm prop set to pick up right dyn libs
;; * class files for ai.serenade.treesitter.* compiled appropriately
;;   * ...classpath set appropriately to make them available

;; XXX: see the following for possible improvement points:
;;
;;        https://groups.google.com/g/clojure/c/oaxtS3waJ_g
;;
;;      specifically the bit about LD_LIBRARY_PATH or equivalent for
;;      other OS

;; the following invocation worked on a linux box, but java.library.path might need
;; to be adjusted appropriately
;;
;; rlwrap clojure -J-Djava.library.path=/usr/java/packages/lib:/usr/lib64:/lib64:/lib:/usr/lib:. -Scp `clojure -Spath`:. tsclj.clj

;; needed to augment this with directory where libjava-tree-sitter.so lived
(println "java.library.path:" (System/getProperty "java.library.path"))

;; XXX: these lead to problems later when calling the constructor for Parser,
;;      for example
;;(System/load (str (System/getenv "HOME") "/src/java-tree-sitter.sogaiu/libjava-tree-sitter.so"))
;;(System/loadLibrary "java-tree-sitter")

;; https://stackoverflow.com/a/24501551
;; https://github.com/kevinjohnston/lein-native/blob/ba8535b8bff08a8a301156cae91b6e76dab4ae51/src/lein_native/core.clj#L8
(clojure.lang.RT/loadLibrary "java-tree-sitter")

;; getting this to work involved setting the class path appropriately (-Scp above)
;; but also getting the .class files created in the first place
(import ai.serenade.treesitter.Parser)
(import ai.serenade.treesitter.TreeSitter)
(import ai.serenade.treesitter.Node)
(import ai.serenade.treesitter.Tree)
(import ai.serenade.treesitter.Languages)
(import ai.serenade.treesitter.TreeCursorNode)

;; getting this to work involved using clojure.lang.RT/loadLibrary above
(def p (ai.serenade.treesitter.Parser.))

(.setLanguage p (Languages/clojure))

(def source-str "(+ 1 1)")

(def tree (.parseString p source-str))

(def result (.getNodeString (.getRootNode tree)))

(println "source string:" source-str)
(println "parsed result:" result)

