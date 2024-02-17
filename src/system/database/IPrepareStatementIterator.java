package system.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface IPrepareStatementIterator {
    void iterate(PreparedStatement  ps) throws SQLException;
}
