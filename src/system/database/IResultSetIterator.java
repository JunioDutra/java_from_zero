package system.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IResultSetIterator<T> {
    T iterate(ResultSet rs) throws SQLException;
}
