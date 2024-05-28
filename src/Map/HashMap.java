package Map;

public class HashMap {
    String url;
    Mapping mapping;

    public HashMap() {

    }

    public HashMap(String url, Mapping mapping) {
        this.setUrl(url);
        this.setMapping(mapping);
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Mapping getMapping() {
        return this.mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    public boolean associer() {
        if(this.getUrl().equals(this.getMapping().getClassName())) {
            return true;
        }
        return false;
    }
}