CLASSES_DIR="bin"
DESTINATION_DIR="C:\Users\ITU\Desktop\ITU\S4\Web dynamique\Sprint\Sprint3\Sprint3_WEB\lib"
JAR_FILE="FrontController.jar"

jar -cvf "$JAR_FILE" -C "$CLASSES_DIR" .
echo "Archivage terminer"

cp -r "$JAR_FILE" "$DESTINATION_DIR"
echo "copie terminer"