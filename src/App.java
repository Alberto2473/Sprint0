import java.util.ArrayList;

import Map.HashMap;
import Map.Mapping;

public class App {
    public static void main(String[] args) {
        String className="Controller";
        String url="/nomClasse";
        ArrayList<String> liste=new ArrayList<>();
        liste.add(url);
        Mapping map=new Mapping(className,liste);
        HashMap hashMap=new HashMap(url,map);
        String leMethode=hashMap.leMethode();
        String association=hashMap.associer();
        System.out.println(association);
        System.out.println(leMethode);
    }
}