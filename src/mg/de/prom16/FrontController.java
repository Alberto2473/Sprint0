package mg.de.prom16;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import annotation.AnnotationController;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet
{
    int checked = 0;
    List<String> listeControllers = new ArrayList<>();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String requestedPage = request.getPathInfo();
        
        out.println("www.sprint0.com" + requestedPage);

        if (checked==0) {
            ServletConfig config = getServletConfig();
            scan(config);
            checked = 0;
        }

        out.println("Liste des contrôleurs annotés avec @AnnotationController :");
        for (String controller : listeControllers) {
            out.println(controller);
        }

        out.close();
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

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        scan(config);
    }

    private void scan(ServletConfig config) { // pour voir ce qui est Controller
        try {
            String controllerPackage = config.getInitParameter("controller-package");
            String path = "WEB-INF/classes/" + controllerPackage.replace('.', '/');
            File directory = new File(getServletContext().getRealPath(path));
            if (directory.exists()) {
                scan2(directory, controllerPackage);
            } else {
                System.out.println("Directory does not exist: " + directory.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scan2(File dossier, String packageName) { // pour scanner les sous repertoire
        for (File file : dossier.listFiles()) {
            if (file.isDirectory()) {
                scan2(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(AnnotationController.class)) {
                        listeControllers.add(clazz.getName());
                        System.out.println("Added controller: " + clazz.getName());
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}