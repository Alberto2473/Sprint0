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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            PrintWriter out = response.getWriter();
            String requestedPage = request.getPathInfo();
            out.println("www.sprint02.com" + requestedPage);

            String controllerPackage = getServletConfig().getInitParameter("Controllers");
            out.println(controllerPackage);
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL url = loader.getResource(controllerPackage);

            if(url != null){
                File directory = new File(url.getFile().replace("%20", " "));
                if(directory.exists() && directory.isDirectory()){
                    File[] files = directory.listFiles();
                    if(files!=null){
                        // out.println(files.length);
                        for(int i=0 ; i<files.length ; i++){
                            File file = files[i];
                            if(file.isFile() && file.getName().endsWith(".class")){
                                String nom = file.getName().split("\\.")[0];
                                // out.println(String.format("%s.%s" , controllerPackage, nom));
                                Class<?> classe = Class.forName(String.format("%s.%s" , controllerPackage, nom));
                                if(classe.isAnnotationPresent(AnnotationController.class)){
                                    Method[] method=classe.getMethods();
                                    ArrayList<String> methodName=new ArrayList<>();
                                    for (int j = 0; j < method.length; j++) {
                                        if(method[j].isAnnotationPresent(GetMethode.class)) 
                                        {
                                            methodName.add(method[j].getName());
                                        }   
                                    }
                                    Mapping map=new Mapping(classe.getName(),methodName);
                                    HashMap hashMap=new HashMap(requestedPage,map);
                                    if (hashMap.associer()) {
                                        out.println("Controller: "+classe.getName());
                                        out.println("Method: ");
                                        for (int j = 0; j < hashMap.getMapping().getMethodName().size(); j++) {
                                            out.println(". "+methodName.get(j));
                                        }
                                    }
                                }
                            }
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