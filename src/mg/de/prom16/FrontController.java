package mg.de.prom16;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.Gson;

import MV.ModelView;
import Map.Mapping;
import Customer.CustomerFile;
import Customer.CustomerSession;
import Verb.VerbAction;
import annotation.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@MultipartConfig
public class FrontController extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        // response.setContentType("text/html;charset=UTF-8");
        try {
            String requestedPage = request.getPathInfo();
            if (requestedPage == null) {
                requestedPage = request.getServletPath();
            }
            // out.println("www.Sprint.com" + requestedPage);

            HttpSession session = request.getSession();

            // Enumeration<String> attributeNames2 = session.getAttributeNames();
            // while (attributeNames2.hasMoreElements()) {
            // out.println(attributeNames2.nextElement());
            // }

            // for (Part part : request.getParts()) {
            //     out.println("Part Name: " + part.getName());
            // }
            
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
                        int confirmationMethode = 0;
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

                                    Mapping map = new Mapping(classe.getSimpleName(), methodName, null);
                                    HashMap<String, Mapping> hashMap = new HashMap<>();
                                    hashMap.put(requestedPage, map);

                                    ArrayList<String> listParamName = new ArrayList<>();
                                    ArrayList<Object> listParamValue = new ArrayList<>();

                                    this.getParamNameAndValue(listParamName, listParamValue, request);

                                    // out.println("Name: ");
                                    // for (String elem : listParamName) {
                                    //     out.println(elem);
                                    // }
                                    // out.println("Value: ");
                                    // for (Object elem : listParamValue) {
                                    //     out.println(elem);
                                    // }

                                    HashMap<String, Object> dataForm = new HashMap<>();
                                    for (int index = 0; index < listParamName.size(); index++) {
                                        dataForm.put(listParamName.get(index), listParamValue.get(index));
                                    }
                                    request.setAttribute("Data", dataForm);
                                    
                                    int indiceAssociation = this.associer(requestedPage, annotationMethod);

                                    if (indiceAssociation > -1) {
                                        confirmation = 1;
                                        Method methodSimple = this.getMethodSimple(
                                                hashMap.get(requestedPage).getMethodName().get(indiceAssociation),
                                                listeMethode);

                                        if (this.MethodVerb(methodSimple)) {
                                            // out.println("URL: "+requestedPage);
                                            // out.println("Method: "+ request.getMethod());
                                            VerbAction verbAction = new VerbAction();
                                            verbAction.setUrl(requestedPage);
                                            verbAction.setVerb(request.getMethod());
                                            // out.println("URL: " + verbAction.getUrl());
                                            // out.println("Method: " + verbAction.getVerb());
                                            if (methodSimple.isAnnotationPresent(GetMethode.class)
                                                    && methodSimple.isAnnotationPresent(POST.class)) {
                                                GetMethode getMethode = methodSimple.getAnnotation(GetMethode.class);
                                                String url2 = getMethode.value();
                                                if (verbAction.verification(url2, "POST")) {
                                                    confirmationMethode = 1;
                                                }
                                            } else if (methodSimple.isAnnotationPresent(GetMethode.class)) {
                                                GetMethode getMethode = methodSimple.getAnnotation(GetMethode.class);
                                                String url2 = getMethode.value();
                                                if (verbAction.verification(url2, "GET")) {
                                                    confirmationMethode = 1;
                                                }
                                            }
                                        }

                                        if (confirmationMethode == 1) {
                                            Parameter[] parameters = methodSimple.getParameters();

                                            ArrayList<String> paramExistant = new ArrayList<>();
                                            ArrayList<Object> valueExistant = new ArrayList<>();

                                            this.paramValueExistant(listParamName, listParamValue, paramExistant,
                                                    valueExistant, parameters, session, request, out);

                                            // out.println("Parametre:");
                                            // for(String elem:paramExistant) {
                                            //     out.println(elem);
                                            // }

                                            // out.println("Valeur:");
                                            // for(Object elem:valueExistant) {
                                            //     if (elem instanceof CustomerFile) {
                                            //         CustomerFile cf = (CustomerFile) elem;
                                            //         out.println(cf.getByteFile());
                                            //     } else {
                                            //         out.println(elem);
                                            //     }
                                            // }

                                            Field[] attribut = classe.getDeclaredFields();

                                            // for (int index = 0; index < attribut.length; index++) {
                                            // out.println(attribut[index].getType().getSimpleName());
                                            // }

                                            Object o = classe.getDeclaredConstructor().newInstance();
                                            // out.println(o.getClass().getSimpleName());

                                            HashMap<String,String> erreurValidation = this.objectParameter(o, attribut, paramExistant,
                                                    valueExistant, classe,
                                                    session, out);
                                            out.println("Nombre d'erreur: " + erreurValidation);

                                            if (paramExistant.size() > 0 || o != null) {
                                                ArrayList<Object> objectParameter = new ArrayList<>();
                                                Parameter[] parameter = methodSimple.getParameters();
                                                for (Parameter elem : parameter) {
                                                    if (elem.isAnnotationPresent(ObjectParam.class)) {
                                                        objectParameter.add(o);
                                                    } else if (elem.isAnnotationPresent(RequestParam.class)) {
                                                        for (Object elem2 : valueExistant) {
                                                            objectParameter.add(elem2);
                                                        }
                                                    }
                                                }

                                                for (int index = 0; index < paramExistant.size(); index++) {
                                                    if (paramExistant.get(index).equals("CustomerSession")) {
                                                        objectParameter.add(valueExistant.get(index));
                                                    }
                                                }

                                                Object[] allParameter = new Object[objectParameter.size()];
                                                for (int index = 0; index < allParameter.length; index++) {
                                                    allParameter[index] = objectParameter.get(index);
                                                }

                                                // out.println("Les parametres du methode: ");
                                                // for (Object elem : allParameter) {
                                                // if (elem instanceof CustomerFile) {
                                                // CustomerFile cf = (CustomerFile) elem;
                                                // out.println(cf.getByteFile());
                                                // }
                                                // else {
                                                // out.println(elem);
                                                // }
                                                // }

                                                Object object2 = this.execMethodParams(classe, hashMap
                                                        .get(requestedPage).getMethodName().get(indiceAssociation),
                                                        allParameter);

                                                if (methodSimple.isAnnotationPresent(RestAPI.class)) {
                                                    Gson gson = new Gson();
                                                    response.setContentType("application/json;charset=UTF-8");
                                                    String jsonResponse = "";
                                                    if (object2 instanceof ModelView) {
                                                        ModelView modelAndView = (ModelView) object2;
                                                        jsonResponse = gson.toJson(modelAndView.getHashMap());
                                                        out.println(jsonResponse);
                                                    } else {
                                                        jsonResponse = gson.toJson(object2);
                                                        out.println(jsonResponse);
                                                    }
                                                } else {
                                                    ModelView modelView = new ModelView();
                                                    modelView = (ModelView) object2;

                                                    for (Entry<String, Object> entry : modelView.getHashMap()
                                                            .entrySet()) {
                                                        String key = entry.getKey();
                                                        Object value = entry.getValue();
                                                        request.setAttribute(key, value);
                                                        if (value instanceof CustomerSession) {
                                                            CustomerSession cs = new CustomerSession();
                                                            cs = (CustomerSession) value;
                                                            int existe = 0;
                                                            for (Entry<String, Object> entry2 : cs.getSession()
                                                                    .entrySet()) {
                                                                session.setAttribute(entry2.getKey(),
                                                                        entry2.getValue());
                                                                existe++;
                                                            }
                                                            if (existe == 0) {
                                                                Enumeration<String> attributeNames = session
                                                                        .getAttributeNames();
                                                                while (attributeNames.hasMoreElements()) {
                                                                    session.removeAttribute(
                                                                            attributeNames.nextElement());
                                                                }
                                                            }
                                                        }
                                                    }

                                                    if (!request.getMethod().equalsIgnoreCase("GET")) {
                                                        HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                                                            @Override
                                                            public String getMethod() {
                                                                return "GET";  // Forcer la méthode GET pour la redirection vers l'URL d'erreur
                                                            }
                                                        };
                                            
                                                        // Forward vers l'URL d'erreur avec la méthode GET
                                                        if (!erreurValidation.isEmpty()) {
                                                            wrappedRequest.setAttribute("error", erreurValidation);
                                                            wrappedRequest.getRequestDispatcher(modelView.getUrlError()).forward(wrappedRequest, response);
                                                        } else {
                                                            request.getRequestDispatcher(modelView.getUrl()).forward(wrappedRequest, response);
                                                        }
                                                    } else {
                                                        if (!erreurValidation.isEmpty()) {
                                                            request.setAttribute("error", erreurValidation);
                                                            request.getRequestDispatcher(modelView.getUrlError()).forward(request, response);
                                                        } else {
                                                            request.getRequestDispatcher(modelView.getUrl()).forward(request, response);
                                                        }
                                                    }
                                                }
                                            } else {
                                                Object object2 = this.execMethod(classe, hashMap.get(requestedPage)
                                                        .getMethodName().get(indiceAssociation));
                                                if (classe.isAnnotationPresent(RestAPI.class)) {
                                                    Gson gson = new Gson();
                                                    response.setContentType("application/json;charset=UTF-8");
                                                    String jsonResponse;
                                                    if (object2 instanceof ModelView) {
                                                        ModelView modelAndView = (ModelView) object2;
                                                        jsonResponse = gson.toJson(modelAndView.getHashMap());
                                                        out.println(jsonResponse);
                                                    } else {
                                                        jsonResponse = gson.toJson(object2);
                                                        out.println(jsonResponse);
                                                    }
                                                } else {
                                                    if (typeClasse.get(indiceAssociation).equals("String")) {
                                                        String execution = String.valueOf(
                                                                this.execMethod(classe, hashMap.get(requestedPage)
                                                                        .getMethodName().get(indiceAssociation)));

                                                        out.println("Classe: " + classe.getSimpleName());
                                                        out.println("Method: " + hashMap.get(requestedPage)
                                                                .getMethodName().get(indiceAssociation));
                                                        out.println("Reponse de l'execution: " + execution);
                                                    } else {
                                                        ModelView modelView = new ModelView();
                                                        modelView = (ModelView) object2;

                                                        for (Entry<String, Object> entry : modelView.getHashMap()
                                                                .entrySet()) {
                                                            String key = entry.getKey();
                                                            Object value = entry.getValue();
                                                            request.setAttribute(key, value);
                                                        }
                                                        if (!request.getMethod().equalsIgnoreCase("GET")) {
                                                            HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(
                                                                    request) {
                                                                @Override
                                                                public String getMethod() {
                                                                    return "GET"; // Forcer la méthode GET pour la
                                                                                  // redirection vers l'URL d'erreur
                                                                }
                                                            };

                                                            // Forward vers l'URL d'erreur avec la méthode GET
                                                            if (!erreurValidation.isEmpty()) {
                                                                wrappedRequest.setAttribute("error", erreurValidation);
                                                                wrappedRequest
                                                                        .getRequestDispatcher(modelView.getUrlError())
                                                                        .forward(wrappedRequest, response);
                                                            } else {
                                                                request.getRequestDispatcher(modelView.getUrl())
                                                                        .forward(wrappedRequest, response);
                                                            }
                                                        } else {
                                                            if (!erreurValidation.isEmpty()) {
                                                                request.setAttribute("error", erreurValidation);
                                                                request.getRequestDispatcher(modelView.getUrlError())
                                                                        .forward(request, response);
                                                            } else {
                                                                request.getRequestDispatcher(modelView.getUrl())
                                                                        .forward(request, response);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            // Quitter le scan des classes et confirmer la liaison de l'url et le
                                            // methode
                                            i = files.length;

                                            // else {
                                            //     out.println("il y a une erreur de validation");
                                            // }
                                        }
                                    }
                                }
                            }
                        }
                        if (confirmation == 0) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page non trouvée");
                        }
                        if (confirmationMethode == 0) {
                            out.println("Methode non valider");
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

    // Sprint3
    public Object execMethod(Class<?> cla, String methodName) throws Exception {
        Method method = cla.getMethod(methodName);
        Object controllerInstance = cla.getDeclaredConstructor().newInstance();
        Object result = method.invoke(controllerInstance);
        return result;
    }

    // Sprint 6
    public void getParamNameAndValue(ArrayList<String> listParamName, ArrayList<Object> listParamValue,
            HttpServletRequest request) throws IOException, ServletException {
        if (request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart/")) {
            for (Part part : request.getParts()) {
                String paramName = part.getName();                      
                String submittedFileName = part.getSubmittedFileName(); 
                                                                        
                listParamName.add(paramName);

                if (submittedFileName != null) {
                    listParamValue.add(submittedFileName);
                } else {
                    // Si c'est un champ texte, récupérer sa valeur
                    try (InputStream inputStream = part.getInputStream()) {
                        String value = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        listParamValue.add(value);
                    }
                }
            }
        }
        else {
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = request.getParameter(paramName);

                listParamName.add(paramName);
                listParamValue.add(paramValue);
            }
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
    public void paramValueExistant(ArrayList<String> listParamName, ArrayList<Object> listParamValue,
            ArrayList<String> paramExistant, ArrayList<Object> valueExistant, Parameter[] parameters,
            HttpSession session,HttpServletRequest request,PrintWriter out)throws IOException,ServletException {
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
            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                // out.println("oui RequestParam");
                RequestParam annotation2 = parameter.getAnnotation(RequestParam.class);
                out.println(annotation2.value());
                for (int j = 0; j < listParamName.size(); j++) {
                    out.println(listParamName.get(j));
                    if (annotation2.value().equals(listParamName.get(j))) {
                            // out.println("oui mitovy ny inputName sy annotation.value()");
                        if (parameter.getType() == CustomerFile.class) {
                            // out.println("oui de type CustomerFile");

                            byte[] bytes = this.fillCustomerFile(request,listParamName.get(j),out);
                            
                            CustomerFile cf = new CustomerFile();

                            if (bytes != null) {
                                cf.setByteFile(bytes);
                            }

                            paramExistant.add(listParamName.get(j));
                            valueExistant.add(cf);

                        }
                        else {
                            paramExistant.add(listParamName.get(j));
                            valueExistant.add(listParamValue.get(j));
                        }
                    }
                }
            } else if (parameter.getType() == CustomerSession.class) {
                Enumeration<String> attributeNames = session.getAttributeNames();
                HashMap<String, Object> hashMap = new HashMap<>();
                CustomerSession customerSession = new CustomerSession();
                while (attributeNames.hasMoreElements()) {
                    String attributeName = attributeNames.nextElement();
                    hashMap.put(attributeName, session.getAttribute(attributeName));
                }
                customerSession.setSession(hashMap);
                paramExistant.add("CustomerSession");
                valueExistant.add(customerSession);
            }
        }
    }

    public HashMap<String,String> objectParameter(Object o, Field[] attribut, ArrayList<String> paramExistant,
            ArrayList<Object> valueExistant, Class<?> clazz, HttpSession session, PrintWriter out) throws Exception {
        HashMap<String,String> erreurValidation = new HashMap<>();
        for (Field attrib : attribut) {
            if (attrib.isAnnotationPresent(AnnotationAttribut.class)) {
                AnnotationAttribut annotation3 = attrib.getAnnotation(AnnotationAttribut.class);
                attrib.setAccessible(true);

                for (int j = 0; j < paramExistant.size(); j++) {
                    if (annotation3.value().equals(paramExistant.get(j))) {
                        if (attrib.isAnnotationPresent(Numerique.class)) {
                            String stringValueExistant = String.valueOf(valueExistant.get(j));
                            try {
                                int attribInt = Integer.parseInt(stringValueExistant);
                                attrib.set(o, attribInt);
                            } catch (Exception e) {
                                // e.getMessage();
                                erreurValidation.put(paramExistant.get(j),"Erreur de valeur sur: "+paramExistant.get(j));
                                out.println("valeur invalider sur l'input: " + paramExistant.get(j));
                            }
                        } else if (attrib.isAnnotationPresent(Range.class)) {
                            try {
                                Range longRange = attrib.getAnnotation(Range.class);
                                String stringValueExistant = String.valueOf(valueExistant.get(j));
                                long attribLong = Long.parseLong(stringValueExistant);
                                if (attribLong < longRange.min() || attribLong > longRange.max()) {
                                    erreurValidation.put(paramExistant.get(j),"Erreur de valeur sur: "+paramExistant.get(j));
                                    out.println("valeur invalider sur l'input: " + paramExistant.get(j));
                                } else {
                                    attrib.set(o, attribLong);
                                }
                            }
                            catch (Exception e) {
                                // e.getMessage();
                                erreurValidation.put(paramExistant.get(j),"Erreur de valeur sur: "+paramExistant.get(j));
                                out.println("valeur invalider sur l'input: " + paramExistant.get(j));
                            }
                            
                        } else {
                            String stringValueExistant = String.valueOf(valueExistant.get(j));
                            Object ob = this.convertParameterValue(stringValueExistant, attrib.getType());
                            attrib.set(o, ob);
                        }
                    }
                }
            } else if (attrib.getType() == CustomerSession.class) {
                Enumeration<String> attributeNames = session.getAttributeNames();
                HashMap<String, Object> hashMap = new HashMap<>();
                CustomerSession customerSession = new CustomerSession();
                while (attributeNames.hasMoreElements()) {
                    String attributeName = attributeNames.nextElement();
                    hashMap.put(attributeName, session.getAttribute(attributeName));
                }
                customerSession.setSession(hashMap);

                attrib.setAccessible(true);
                if (attrib.get(o) == null) {
                    attrib.set(o, customerSession);
                }
            }
        }
        return erreurValidation;
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

    // Sprint 10
    public boolean MethodVerb(Method method) throws Exception {
        if (method.isAnnotationPresent(POST.class) && method.isAnnotationPresent(GetMethode.class)) {
            return true;
        } else if (method.isAnnotationPresent(GetMethode.class)) {
            return true;
        }
        return false;
    }

    // Sprint 12
    public byte[] fillCustomerFile(HttpServletRequest request, String inputName, PrintWriter out)
            throws IOException, ServletException {
        byte[] fileBytes = null;

        out.println(inputName);
        Part part = request.getPart(inputName);

        if (part != null && part.getSubmittedFileName() != null && !part.getSubmittedFileName().isEmpty()) {
            out.println("Part valider");
            try (InputStream fileContent = part.getInputStream()) {
                try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileContent.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                    fileBytes = byteArrayOutputStream.toByteArray();
                }
            }
        }
        return fileBytes;
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