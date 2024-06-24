package mg.de.prom16;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;

import Map.HashMap;
import Map.Mapping;
import annotation.AnnotationController;
import annotation.GetMethode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet
{
    protected String execMethod(Class<?> cla, String methodName) throws Exception {
        String valiny = "";
    
        // Trouver la méthode avec le nom methodName et sans paramètres
        Method method = cla.getMethod(methodName);
        
        // Créer une instance de la classe cla (Controller) pour invoquer la méthode
        Object controllerInstance = cla.getDeclaredConstructor().newInstance();
    
        // Appeler la méthode sur l'instance de controllerInstance sans aucun argument
        Object result = method.invoke(controllerInstance);
    
        // Convertir le résultat en chaîne de caractères
        valiny = String.valueOf(result);
    
        return valiny;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            try {
            PrintWriter out = response.getWriter();
            String requestedPage = request.getPathInfo();
            out.println("www.Sprint.com" + requestedPage);

            String controllerPackage = getServletConfig().getInitParameter("Controllers");
            // out.println("Package: "+controllerPackage);
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL url = loader.getResource(controllerPackage);

            if(url != null){
                File directory = new File(url.getFile().replace("%20", " "));
                if(directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles();
                    if(files!=null){
                        int confirmation=0;
                        for(int i=0 ; i<files.length ; i++){
                            File file = files[i];
                            if(file.isFile() && file.getName().endsWith(".class")){
                                String nom = file.getName().split("\\.")[0];
                                Class<?> classe = Class.forName(String.format("%s.%s" , controllerPackage, nom));
                                if(classe.isAnnotationPresent(AnnotationController.class)){
                                    Method[] method=classe.getMethods();
                                    ArrayList<String> methodName=new ArrayList<>();
                                    
                                    for (int j = 0; j < method.length; j++) {
                                        if(method[j].isAnnotationPresent(GetMethode.class))
                                        {
                                            GetMethode annotation=method[j].getAnnotation(GetMethode.class);
                                            methodName.add(annotation.value());
                                        }
                                    }

                                    Mapping map=new Mapping(classe.getSimpleName(),methodName);
                                    // out.println("Classe: "+map.getClassName());
                                    // out.println("Method: ");
                                    // for (int j = 0; j < map.getMethodName().size(); j++) {
                                    //     out.println(map.getMethodName().get(j));
                                    // }
                                    HashMap hashMap=new HashMap(requestedPage,map);
                                    // out.println("Url: "+hashMap.getUrl());
                                    // out.println("Mapping: "+hashMap.getMapping().getMethodName().get(0));
                                    String leMethode=hashMap.leMethode();
                                    String association=hashMap.associer();
                                    // out.println("Association: "+association);
                                    // out.println("Methode: "+leMethode);
                                    if (association.equals("aucun methode lies a l'url")==false) {
                                        out.println("Classe: "+classe.getSimpleName());
                                        out.println("Annotation: "+association);
                                        out.println("Method: "+leMethode);
                                        String execution=this.execMethod(classe, leMethode);
                                        out.println("Reponse du methode:"+ execution);
                                        confirmation=1;
                                        i=files.length;
                                    }
                                }
                            }
                        }
                        if (confirmation==0) {
                            out.println("aucun methode lies a l'url");
                        }
                    }
                }
            }
            out.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}