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

    public String associer() {
        String valiny="";
        for (int i = 0; i < this.getMapping().getMethodName().size() ; i++) {
            String compare=this.getMapping().getMethodName().get(i);
            if(this.getUrl().equals(compare)) {
                valiny=compare;
            }
        }
        if (valiny.equals("")) {
            valiny="aucun methode lies a l'url";
        }
        return valiny;
    }

    public String leMethode() {
        String resultat="";
        String fonction=this.associer();
        if (fonction.equals("aucun methode lies a l'url")==false) {
            String[] valiny2=fonction.split("/");
            resultat=valiny2[1];
        }
        return resultat;
    }
}