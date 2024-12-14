package MV;

import java.util.HashMap;

public class ModelView {
    String url;
    String urlError;
    HashMap<String,Object> hashMap= new HashMap<>();
    
    public ModelView() {
    }

    public ModelView(String url, String urlError, HashMap<String, Object> hashMap) {
        this.setUrl(url);
        this.setUrlError(urlError);
        this.setHashMap(hashMap);
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlError() {
        return this.urlError;
    }

    public void setUrlError(String urlError) {
        this.urlError = urlError;
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