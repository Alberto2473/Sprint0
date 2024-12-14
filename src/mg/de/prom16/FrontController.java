package mg.de.prom16;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;

import MV.ModelView;
import Map.Mapping;
import annotation.*;
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
                                    ArrayList<String> typeClasse = new ArrayList<>();
                                    ArrayList<Method> listeMethode = new ArrayList<>();
                                    
                                    for (int j = 0; j < method.length; j++) {
                                        if (method[j].isAnnotationPresent(GetMethode.class)) {
                                            GetMethode annotation = method[j].getAnnotation(GetMethode.class);
                                            methodName.add(method[j].getName());
                                            annotationMethod.add(annotation.value());
                                            typeClasse.add(method[j].getReturnType().getSimpleName());
                                            listeMethode.add(method[j]);
                                        }
                                    }

                                    Mapping map = new Mapping(classe.getSimpleName(), methodName);
                                    HashMap<String, Mapping> hashMap = new HashMap<>();
                                    hashMap.put(requestedPage, map);

                                    ArrayList<String> listParamName = new ArrayList<>();
                                    ArrayList<String> listParamValue = new ArrayList<>();

                                    this.getParamNameAndValue(listParamName, listParamValue, request);

                                    int indiceAssociation = this.associer(requestedPage, annotationMethod);

                                    if (indiceAssociation > -1) {
                                        Method methodSimple = this.getMethodSimple(hashMap.get(requestedPage).getMethodName().get(indiceAssociation),listeMethode);
                                        Parameter[] parameters = methodSimple.getParameters();
                                        
                                        ArrayList<String> paramExistant=new ArrayList<>();
                                        ArrayList<String> valueExistant=new ArrayList<>();
                                        this.paramValueExistant(listParamName, listParamValue, paramExistant,
                                                valueExistant, parameters);

                                        Field[] attribut = classe.getDeclaredFields();
                                        Object o = classe.getDeclaredConstructor().newInstance();

                                        this.objectParameter(o, attribut, paramExistant, valueExistant);

                                        Object[] objectParameter = new Object[1];
                                        objectParameter[0] = o;
                                        
                                        if (objectParameter.length > 0) {

                                            Object object2 = this.execMethodParams(classe, hashMap.get(requestedPage).getMethodName().get(indiceAssociation),objectParameter);

                                            ModelView modelView = new ModelView();
                                            modelView = (ModelView) object2;

                                            for (Entry<String, Object> entry : modelView.getHashMap().entrySet()) {
                                                String key = entry.getKey();
                                                Object value = entry.getValue();
                                                request.setAttribute(key, value);
                                            }

                                            // out.println("url: "+modelView.getUrl());
                                            // out.println(request.getAttribute("reponse"));

                                            request.getRequestDispatcher(modelView.getUrl()).forward(request, response);
                                        }
                                        else {
                                            if (typeClasse.get(indiceAssociation).equals("String")) {
                                                String execution = String.valueOf(this.execMethod(classe,hashMap.get(requestedPage).getMethodName().get(indiceAssociation)));

                                                out.println("Classe: " + classe.getSimpleName());
                                                out.println("Method: " + hashMap.get(requestedPage).getMethodName().get(indiceAssociation));
                                                out.println("Reponse de l'execution: " + execution);    
                                            }
                                            else {
                                                Object object2=this.execMethod(classe,hashMap.get(requestedPage).getMethodName().get(indiceAssociation));
                                                
                                                ModelView modelView = new ModelView();
                                                modelView = (ModelView) object2;

                                                for (Entry<String, Object> entry : modelView.getHashMap().entrySet()) {
                                                    String key = entry.getKey();
                                                    Object value = entry.getValue();
                                                    request.setAttribute(key, value);
                                                }

                                                request.getRequestDispatcher(modelView.getUrl()).forward(request, response);
                                            }
                                        }
                                        
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
            out.println(e.getMessage());
            for (StackTraceElement elem : e.getStackTrace()) {
                out.println(elem.toString());
            }
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
    public Object execMethod(Class<?> cla, String methodName) throws Exception {
        Method method = cla.getMethod(methodName);
        Object controllerInstance = cla.getDeclaredConstructor().newInstance();
        Object result = method.invoke(controllerInstance);
        return result;
    }

    // Sprint 6

    public void getParamNameAndValue(ArrayList<String> listParamName, ArrayList<String> listParamValue,
            HttpServletRequest request) {
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);

            listParamName.add(paramName);
            listParamValue.add(paramValue);
        }
    }

    // Prendre en tant que type Method le methode lies a l'url 
    public Method getMethodSimple(String methodeAssocie, ArrayList<Method> listeMethode) {
        Method methodSimple = null;
        for (int l = 0; l < listeMethode.size(); l++) {
            if (methodeAssocie.equals(listeMethode.get(l).getName())) {
                methodSimple = listeMethode.get(l);
            }
        }
        return methodSimple;
    }
    
    public Object[] listeObjet(ArrayList<String> listParamName, ArrayList<String> listParamValue,
            Parameter[] parameters) {
        ArrayList<Object> listeObjet = new ArrayList<>();

        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam annotation2 = parameter.getAnnotation(RequestParam.class);
                for (int j = 0; j < listParamName.size(); j++) {
                    if (annotation2.value().equals(listParamName.get(j))) {
                        listeObjet.add(listParamValue.get(j));
                    }
                }
            }
        }

        // Caster la liste d'objet en tableau d'objet pour l'execution de methode
        Object[] objectParameter = new Object[listeObjet.size()];
        for (int index = 0; index < objectParameter.length; index++) {
            objectParameter[index] = listeObjet.get(index);
        }

        return objectParameter;
    }

    protected Object execMethodParams(Class<?> cla, String methodName, Object[] param) throws Exception {
        Class<?>[] paramTypes = new Class<?>[param.length];

        for (int i = 0; i < param.length; i++) {
            if (param[i] == null) {
                paramTypes[i] = Object.class;
            } else {
                paramTypes[i] = param[i].getClass();
            }
        }
        Method method = cla.getMethod(methodName, paramTypes);
        Object controllerInstance = cla.getDeclaredConstructor().newInstance();
        Object result = method.invoke(controllerInstance, param);

        return result;
    }

    // Sprint 7
    public void paramValueExistant(ArrayList<String> listParamName, ArrayList<String> listParamValue,
            ArrayList<String> paramExistant, ArrayList<String> valueExistant, Parameter[] parameters) {
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(ObjectParam.class)) {
                ObjectParam annotation2 = parameter.getAnnotation(ObjectParam.class);
                for (int j = 0; j < listParamName.size(); j++) {
                    String[] splitParamName = listParamName.get(j).split("\\.");
                    if (annotation2.value().equals(splitParamName[0])) {
                        paramExistant.add(splitParamName[1]);
                        valueExistant.add(listParamValue.get(j));
                    }
                }
            }
        }
    }
    
    public void objectParameter(Object o,Field[] attribut,ArrayList<String> paramExistant,ArrayList<String> valueExistant) throws Exception {
        for (Field attrib : attribut) {
            if (attrib.isAnnotationPresent(AnnotationAttribut.class)) {
                AnnotationAttribut annotation3 = attrib.getAnnotation(AnnotationAttribut.class);
                // out.println("Annotation: " + annotation3.value());
                attrib.setAccessible(true);
                for (int j = 0; j < paramExistant.size(); j++) {
                    if (annotation3.value().equals(paramExistant.get(j))) {
                        Object ob = this.convertParameterValue(valueExistant.get(j), attrib.getType());
                        attrib.set(o, ob);
                        // out.println(attrib.get(o));
                    }
                }
            }
        }
    }

    private Object convertParameterValue(String paramValue, Class<?> targetType) {
        if (paramValue == null) {
            return null;
        }
        if (targetType.equals(String.class)) {
            return paramValue;
        } else if (targetType.equals(int.class) || targetType.equals(Integer.class)) {
            return Integer.parseInt(paramValue);
        } else if (targetType.equals(double.class) || targetType.equals(Double.class)) {
            return Double.parseDouble(paramValue);
        } else if (targetType.equals(Date.class)) {
            return Date.valueOf(paramValue);
        } else if (targetType.equals(boolean.class) || targetType.equals(Boolean.class)) {
            return Boolean.parseBoolean(paramValue);
        } else if (targetType.equals(long.class) || targetType.equals(Long.class)) {
            return Long.parseLong(paramValue);
        } else if (targetType.equals(float.class) || targetType.equals(Float.class)) {
            return Float.parseFloat(paramValue);
        } else if (targetType.equals(short.class) || targetType.equals(Short.class)) {
            return Short.parseShort(paramValue);
        } else if (targetType.equals(byte.class) || targetType.equals(Byte.class)) {
            return Byte.parseByte(paramValue);
        } else {
            // Ajouter d'autres types selon vos besoins
            throw new IllegalArgumentException("Type de paramètre non pris en charge: " + targetType.getName());
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