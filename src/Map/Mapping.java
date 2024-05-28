package Map;

import java.util.ArrayList;

public class Mapping {
    String className;
    ArrayList<String> methodName;

    public Mapping() {}
    
    public Mapping(String className, ArrayList<String> methodName) {
        this.setClassName(className);
        this.setMethodName(methodName);
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ArrayList<String> getMethodName() {
        return this.methodName;
    }

    public void setMethodName(ArrayList<String> methodName) {
        this.methodName = methodName;
    }
}