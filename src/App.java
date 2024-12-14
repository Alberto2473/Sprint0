import java.lang.reflect.Method;

public class App {

    public void methode(String valeur,int nombre,double dou) {
        System.out.println(valeur);
        System.out.println(nombre);
        System.out.println(dou);
    }

    public static void main(String[] args) {
        App app=new App();
        Method[] method=app.getClass().getMethods();
        for (int i = 0; i < method.length; i++) {
            Class<?>[] listeTypeParameter = method[i].getParameterTypes();
            for (int j = 0; j < listeTypeParameter.length; j++) {
                String value =String.valueOf(listeTypeParameter[j]);
                System.out.println(value);
            }
        }
    }
}
