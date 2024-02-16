import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

import controller.ClientController;
import system.common.Env;
import system.http.DefaultHandler;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    private static final int PORT = Env.getEnvInt("PORT", 8080);
    private static final int CONCURRENCY_SIZE = Env.getEnvInt("CONCURRENCY_SIZE", 10);

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/clientes", new DefaultHandler(new ClientController()));

        server.setExecutor(Executors.newFixedThreadPool(CONCURRENCY_SIZE));

        logger.info("Server started on port 8080");
        server.start();
    }
}
