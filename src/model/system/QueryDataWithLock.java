package model.system;

import java.sql.Connection;
import java.sql.Statement;

public record QueryDataWithLock<T>(T data, Statement statement) {

}
