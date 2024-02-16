import static system.common.Env.CONCURRENCY_SIZE;
import static system.common.Env.PORT;
import static system.common.Env.getEnvInt;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

import controller.ClientController;
import system.http.DefaultHandler;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(getEnvInt(PORT, 8080)), 0);
        server.createContext("/clientes", new DefaultHandler(new ClientController()));

        server.setExecutor(Executors.newFixedThreadPool(getEnvInt(CONCURRENCY_SIZE, 10)));

        logger.info("Server started on port 8080");
        server.start();
    }
}
