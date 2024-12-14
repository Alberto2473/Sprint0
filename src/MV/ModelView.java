package MV;

import java.util.HashMap;

public class ModelView {
    String url;
    HashMap<String,Object> hashMap;
    
    public ModelView() {
    }

    public ModelView(String url, HashMap<String, Object> hashMap) {
        this.setUrl(url);
        this.setHashMap(hashMap);
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, Object> getHashMap() {
        return this.hashMap;
    }

    public void setHashMap(HashMap<String, Object> hashMap) {
        this.hashMap = hashMap;
    }
    
    public void addObject(String variable, Object valeur) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put(variable, valeur);
        this.setHashMap(hashMap);
    }
}