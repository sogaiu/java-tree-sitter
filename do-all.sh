#! /bin/sh

if [ -n "${JAVA_HOME+1}" ]; then
    printf "Ok, JAVA_HOME was already set to: %s\n\n" "$JAVA_HOME"
else
    export JAVA_HOME="$HOME"/src/amazon-corretto-17.0.6.10.1-linux-x64
fi
export PATH="$JAVA_HOME"/bin:"$PATH"

OS=$(uname -s)

printf "OS detected as: %s\n\n" "$OS"

if [ "$OS" = "Linux" ]; then
    ext=so
elif [ "$OS" = "Darwin" ]; then
    ext=dylib
else
    printf "Sorry only Linux and macos supported"
    exit 1
fi

dynlib="$(pwd)"/libjava-tree-sitter."$ext"

printf "Cleaning files ... \n"
rm -rf "$(pwd)"/ai
rm "$dynlib"
rm ./*.class
printf "Done\n\n"

printf "Building %s ... \n" "$dynlib"
./build.py tree-sitter-sql-bigquery
printf "Done\n\n"

printf "Building class files for ai.serenade.treesitter ... \n"
javac \
  -d "$(pwd)" \
  -sourcepath "$(pwd)"/src/main/java \
  "$(pwd)"/src/main/java/ai/serenade/treesitter/*.java
printf "Done\n\n"

printf "Building demo program ... \n"
javac \
  -sourcepath "$(pwd)"/src/main/java:"$(pwd)" \
  jts.java
printf "Done\n\n"

printf "Running demo program (jts) using %s ... \n" "$dynlib"
JAVA_TREE_SITTER="$dynlib" \
  java \
  -cp "$(pwd)" \
  jts
printf "Done\n\n"
