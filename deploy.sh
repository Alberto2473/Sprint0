
SRC_DIR="src/mg/de/prom16"
LIB_DIR="lib"
CLASSES_DIR="bin"

# $CATALINA_HOME/bin/shutdown.bat

javac -cp "$LIB_DIR/*" -d "$CLASSES_DIR" "$SRC_DIR/*.java"
jar -cvf FrontServlet.jar -C "$CLASSES_DIR" .