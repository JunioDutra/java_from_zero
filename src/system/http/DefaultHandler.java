package system.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import model.system.HttpRequest;
import model.system.HttpResponse;

public class DefaultHandler implements HttpHandler {
    private static final Logger logger = Logger.getLogger(DefaultHandler.class.getName());

    private IResponseHandler basicHandler;

    public DefaultHandler(IResponseHandler basicHandler) {
        this.basicHandler = basicHandler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {

            var queryParams = getQueryParams(exchange);
            var body = new String(exchange.getRequestBody().readAllBytes());
            var method = exchange.getRequestMethod();
            var path = exchange.getRequestURI().getPath();
            var pathVariable = Optional.<String>empty();

            if (basicHandler instanceof IResponsePathVarialbeHandler) {
                var pathVariableHandler = (IResponsePathVarialbeHandler) basicHandler;
                pathVariable = Optional.of(path.replaceAll(pathVariableHandler.regexPathVariable(), "$1"));
            }

            var httpRequest = new HttpRequest(queryParams, body, method, path, pathVariable);

            var response = HttpResponse.badRequest("""
                    {
                        "error": "bad request",
                    }
                    """);

            if ("GET".equalsIgnoreCase(method)) {
                response = basicHandler.handleGet(httpRequest);
            }

            if ("POST".equalsIgnoreCase(method)) {
                response = basicHandler.handlePost(httpRequest);
            }

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(response.statusCode(), response.body().length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.body().getBytes());
            os.close();
        } catch (Exception e) {
            var msg = SimpleJsonParser.simpleMessage("Internal server error");

            OutputStream os = exchange.getResponseBody();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(503, msg.length());
            // exchange.sendResponseHeaders(200, msg.length());
            os.write(msg.getBytes());
            os.close();

            logger.severe("Error handling request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<String, String> getQueryParams(HttpExchange exchange) {
        try {
            URI requestedUri = exchange.getRequestURI();
            String query = requestedUri.getRawQuery();
            var params = query.split("&");
            return Stream.of(params)
                    .map(param -> {
                        var keyValue = param.split("=");
                        return new AbstractMap.SimpleEntry<>(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (Exception e) {
            return Map.of();
        }
    }
}
