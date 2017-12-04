package org.hallemojah.frontend;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.DeploymentInfo;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;

import io.undertow.servlet.api.DeploymentManager;

import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.Handlers;

import org.hallemojah.frontend.Decoder;
/**
 * @author Zhening Yang
 */

class QR extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        String data = request.getParameter("data");
        String res = "";
        if (type.equals("encode")){
            res =  Decoder.enAndDe("encode", data);
        }
        else if(type.equals("decode"))
        {
            res =  Decoder.enAndDe("decode", data) ;
        }
        PrintWriter writer = response.getWriter();
        writer.write(res);
        writer.close();
    }
}

public class UndertowServer {

    public UndertowServer() throws Exception{
    }

    public static final String PATH = "/";

    public static void main(String[] args) throws Exception{
        try {
            DeploymentInfo servletBuilder = deployment()
                    .setClassLoader(UndertowServer.class.getClassLoader())
                    .setContextPath("/q1")
                    .setDeploymentName("frontend")
                    .addServlets(
                            servlet("Q1", QR.class)
                                    .addMapping("/q1")
                    );


            DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
            manager.deploy();

            HttpHandler servletHandler = manager.start();
            PathHandler path = Handlers.path(Handlers.redirect(PATH))
                    .addPrefixPath(PATH, servletHandler);

            Undertow server = Undertow.builder()
                    .addHttpListener(80, "0.0.0.0")
                    .setHandler(path)
                    .build();
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
