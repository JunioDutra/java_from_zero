import static system.common.Env.CONCURRENCY_SIZE;
import static system.common.Env.PORT;
import static system.common.Env.getEnvInt;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

import controller.ClientController;
import system.database.ConnectionPool;
import system.http.DefaultHandler;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {
        ConnectionPool.getInstance();

        var port = getEnvInt(PORT, 9999);
        var concurrency = getEnvInt(CONCURRENCY_SIZE, 10);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(concurrency));

        server.createContext("/clientes", new DefaultHandler(new ClientController()));
        logger.info("Server started on port %s with concurrency %s".formatted(port, concurrency));
        server.start();
    }
}
