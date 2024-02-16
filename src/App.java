import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

import controller.ClientController;
import controller.UserController;
import system.http.DefaultHandler;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/user/", new DefaultHandler(new UserController()));
        server.createContext("/clientes", new DefaultHandler(new ClientController()));

        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        
        logger.info("Server started on port 8080");
        server.start();
    }
}
