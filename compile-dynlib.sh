#! /bin/sh

# XXX: only likely to work on linux
#
# XXX: for other platforms, if wanting to make a similar
#      script, first try to build using build.py and
#      look for the compilation invocations that show up
#
#      (the build.py in the branch this file lives in has been
#      modified to output compilation invocations)

# XXX: adjust the JAVA_TREE_SITTER_SRC and JAVA_HOME values below appropriately
#      to match your environment if nececssary

JAVA_TREE_SITTER_SRC=$(pwd)

# XXX: note, this may need to be a JDK -- JRE may not be enough
if [ -n "${JAVA_HOME+1}" ]; then
    printf "Ok, JAVA_HOME was already set to: %s\n\n" "$JAVA_HOME"
else
    export JAVA_HOME="$HOME"/src/amazon-corretto-17.0.6.10.1-linux-x64
fi

# where some of the intermediate compilation results end up (not the
# final product)
TMP=/tmp

# build tree-sitter c library
# should produce at least tree-sitter/libtree-sitter.a
make -C tree-sitter clean
make -C tree-sitter

# compile ai_serenade_treesitter_TreeSitter.cc
cc \
  -O3 \
  -fPIC \
  -DTS_LANGUAGE_CLOJURE=1 \
  -I"$JAVA_TREE_SITTER_SRC"/lib \
  -I"$JAVA_TREE_SITTER_SRC"/tree-sitter/lib/include \
  -I"$JAVA_HOME"/include \
  -I"$JAVA_HOME"/include/linux \
  -c \
  "$JAVA_TREE_SITTER_SRC"/lib/ai_serenade_treesitter_TreeSitter.cc \
  -o "$TMP"/ai_serenade_treesitter_TreeSitter.o

# compile ai_serenade_treesitter_Languages.cc
cc \
  -O3 \
  -fPIC \
  -DTS_LANGUAGE_CLOJURE=1 \
  -I"$JAVA_TREE_SITTER_SRC"/lib \
  -I"$JAVA_TREE_SITTER_SRC"/tree-sitter/lib/include \
  -I"$JAVA_HOME"/include \
  -I"$JAVA_HOME"/include/linux \
  -c \
  "$JAVA_TREE_SITTER_SRC"/lib/ai_serenade_treesitter_Languages.cc \
  -o "$TMP"/ai_serenade_treesitter_Languages.o

# compile parser.c
cc \
  -O3 \
  -fPIC \
  -std=c99 \
  -DTS_LANGUAGE_CLOJURE=1 \
  -Itree-sitter-clojure/src \
  -I"$JAVA_TREE_SITTER_SRC"/tree-sitter/lib/include \
  -I"$JAVA_HOME"/include \
  -I"$JAVA_HOME"/include/linux \
  -c \
  tree-sitter-clojure/src/parser.c \
  -o "$TMP"/parser.o

# link all the pieces to make dynamic library
#
# N.B. without -lstdc++ below, errors such as:
#
#        undefined symbol: __gxx_personality_v0
#
#      may result.  may be -lstdc++ is needed because
#      some of the compiled objects above are the
#      result of using c++ (the ai_*.cc stuff)
#
#      in any case, thanks to:
#
#      https://stackoverflow.com/questions/329059/what-is-gxx-personality-v0-for
cc \
  -shared \
  "$TMP"/ai_serenade_treesitter_TreeSitter.o \
  "$TMP"/ai_serenade_treesitter_Languages.o \
  "$TMP"/parser.o \
  -L"$JAVA_TREE_SITTER_SRC"/tree-sitter \
  -lstdc++ \
  -o libjava-tree-sitter.so \
  "$JAVA_TREE_SITTER_SRC"/tree-sitter/libtree-sitter.a

