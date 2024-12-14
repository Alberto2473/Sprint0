package Verb;

public class VerbAction {
    String url;
    String verb; // post ou get
    
    public VerbAction(String url, String verb) {
        this.setUrl(url);
        this.setVerb(verb);
    }

    public VerbAction() {}
    
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public boolean verification(String url,String verb) {
        if (url.equals(this.getUrl()) && verb.equals(this.getVerb())) {
            return true;
        }
        return false;
    }
}