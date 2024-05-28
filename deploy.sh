SRC_DIR="src/mg/de/prom16"
LIB_DIR="lib"
CLASSES_DIR="bin"
DESTINATION_DIR="C:\Users\hp\Desktop\ITU\S4\Web dynamique\Sprint2\Sprint2_WEB\WEB-INF\lib"
JAR_FILE="FrontController.jar"
JAR_FILE2="Annotation.jar"

jar -cvf "$JAR_FILE" -C "$CLASSES_DIR" mg
jar -cvf "$JAR_FILE2" -C "$CLASSES_DIR" annotation
echo "Archivage terminer"

cp -r "$JAR_FILE" "$DESTINATION_DIR"
cp -r "$JAR_FILE2" "$DESTINATION_DIR"
echo "copie terminer"