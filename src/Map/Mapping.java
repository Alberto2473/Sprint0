package Map;

import java.util.ArrayList;
import Verb.VerbAction;

public class Mapping {
    String className;
    ArrayList<String> methodName;
    ArrayList<VerbAction> verbAction;

    public Mapping() {}
    
    public Mapping(String className, ArrayList<String> methodName,ArrayList<VerbAction> verbAction) {
        this.setClassName(className);
        this.setMethodName(methodName);
        this.setVerbAction(verbAction);
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

    public ArrayList<VerbAction> getVerbAction() {
        return this.verbAction;
    }

    public void setVerbAction(ArrayList<VerbAction> verbAction) {
        this.verbAction = verbAction;
    }
}