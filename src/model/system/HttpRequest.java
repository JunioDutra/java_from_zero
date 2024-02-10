package model.system;

import java.util.Map;
import java.util.Optional;

public record HttpRequest(Map<String, String> queryParams, String body, String method, String path, Optional<String> pathVariable) {
    
}
