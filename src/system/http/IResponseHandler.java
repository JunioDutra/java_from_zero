package system.http;

import model.system.HttpRequest;
import model.system.HttpResponse;

public interface IResponseHandler {
    HttpResponse handleGet(HttpRequest httpRequest);
    HttpResponse handlePost(HttpRequest httpRequest);
}
