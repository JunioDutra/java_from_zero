package controller;

import model.http.TransactionResponse;
import model.system.HttpRequest;
import model.system.HttpResponse;
import system.database.ConnectionPool;
import system.http.IResponseHandler;
import system.http.SimpleJsonParser;

public class UserController implements IResponseHandler {
    public UserController() {
    }

    @Override
    public HttpResponse handleGet(HttpRequest httpRequest) {
        Long limit = null;
        Long amount = null;

        try (var con = ConnectionPool.getInstance().getDataSource().getConnection();
                var pst = con.prepareStatement("SELECT * FROM update_balance_and_insert_transaction(?, ?, ?, ?)")) {
            pst.setInt(1, 1);
            pst.setInt(2, 10000);

            if(Math.random() > 0.5)
                pst.setString(3, "d");
            else
            pst.setString(3, "c");
            
            pst.setString(4, "ixi");

            try (var rs = pst.executeQuery()) {
                if (rs.next()) {
                    limit = rs.getLong("user_limit");
                    amount = rs.getLong("updated_amount");
                }
            } catch (Exception e) {
                if (e.getMessage().startsWith("ERROR: 422"))
                    return HttpResponse.unprocessableEntity(
                            SimpleJsonParser.simpleError("UnprocessableEntity", "Unprocessable entity"));

                if (e.getMessage().startsWith("ERROR: 404"))
                    return HttpResponse.notFound(SimpleJsonParser.simpleError("NotFound", "Not found"));

                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return HttpResponse.ok(new TransactionResponse(limit, amount).toString());
    }

    @Override
    public HttpResponse handlePost(HttpRequest request) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
