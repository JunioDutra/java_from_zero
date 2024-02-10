package model.system;

public record HttpResponse(String body, int statusCode, String contentType) {
    
    public static HttpResponse ok(String body) {
        return new HttpResponse(body, 200, "application/json");
    }

    public static HttpResponse notFound(String body) {
        return new HttpResponse(body, 404, "application/json");
    }

    public static HttpResponse unprocessableEntity(String body) {
        return new HttpResponse(body, 422, "application/json");
    }

    public static HttpResponse badRequest(String body) {
        return new HttpResponse(body, 400, "application/json");
    }
}
