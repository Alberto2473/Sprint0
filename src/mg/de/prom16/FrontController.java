package mg.de.prom16;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import Map.Mapping;
import annotation.AnnotationController;
import annotation.GetMethode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet
{
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            String requestedPage = request.getPathInfo();
            if (requestedPage == null) {
                requestedPage = request.getServletPath();
            }
            out.println("www.Sprint.com" + requestedPage);

            String controllerPackage = getServletConfig().getInitParameter("Controllers");
            // out.println(controllerPackage);
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL url = loader.getResource(controllerPackage);

            if (url != null) {
                File directory = new File(url.getFile().replace("%20", " "));
                if (directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles();
                    if (files != null) {
                        // out.println(files.length);
                        int confirmation = 0;
                        for (int i = 0; i < files.length; i++) {
                            File file = files[i];
                            if (file.isFile() && file.getName().endsWith(".class")) {
                                String nom = file.getName().split("\\.")[0];
                                // out.println(String.format("%s.%s" , controllerPackage, nom));
                                Class<?> classe = Class.forName(String.format("%s.%s", controllerPackage, nom));
                                if (classe.isAnnotationPresent(AnnotationController.class)) {
                                    // out.println(classe.getSimpleName());
                                    Method[] method = classe.getMethods();
                                    ArrayList<String> methodName = new ArrayList<>();
                                    ArrayList<String> annotationMethod = new ArrayList<>();
                                    for (int j = 0; j < method.length; j++) {
                                        if (method[j].isAnnotationPresent(GetMethode.class)) {
                                            GetMethode annotation = method[j].getAnnotation(GetMethode.class);
                                            methodName.add(method[j].getName());
                                            annotationMethod.add(annotation.value());
                                        }
                                    }

                                    Mapping map = new Mapping(classe.getSimpleName(), methodName);
                                    HashMap<String, Mapping> hashMap = new HashMap<>();
                                    hashMap.put(requestedPage, map);

                                    int indiceAssociation = this.associer(requestedPage, annotationMethod);
                                    if (indiceAssociation != -1) {
                                        String execution = String.valueOf(this.execMethod(classe, hashMap.get(requestedPage).getMethodName().get(indiceAssociation)));
                                        
                                        out.println("Classe: " + classe.getSimpleName());
                                        out.println("Method: "+ hashMap.get(requestedPage).getMethodName().get(indiceAssociation));
                                        out.println("Reponse de l'execution: " + execution);
                                        
                                        // Quitter le scan des classes et confirmer la liaison  de l'url et le methode
                                        confirmation = 1;
                                        i = files.length;
                                    }
                                }
                            }
                        }
                        if (confirmation==0) {
                            out.println("Aucune methode est lies a l'url tapez");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    // Sprint 2
    public int associer(String url, ArrayList<String> listAnnotationMethod) {
        int valiny = -1;
        for (int i = 0; i < listAnnotationMethod.size(); i++) {
            String compare = listAnnotationMethod.get(i);
            if (url.equals(compare)) {
                valiny = i;
            }
        }
        return valiny;
    }

    //Sprint3
    protected Object execMethod(Class<?> cla, String methodName) throws Exception {
        Method method = cla.getMethod(methodName);
        Object controllerInstance = cla.getDeclaredConstructor().newInstance();
        Object result = method.invoke(controllerInstance);
        return result;
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
