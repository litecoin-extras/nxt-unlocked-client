package nxt.http;

import nxt.Nxt;
import nxt.NxtException;
import nxt.Transaction;
import nxt.util.Convert;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

import static nxt.http.JSONResponses.INCORRECT_TRANSACTION_BYTES;
import static nxt.http.JSONResponses.MISSING_TRANSACTION_BYTES;

public final class ParseTransaction extends APIServlet.APIRequestHandler {

    static final ParseTransaction instance = new ParseTransaction();

    private ParseTransaction() {
        super("transactionBytes");
    }

    @Override
    JSONStreamAware processRequest(HttpServletRequest req) throws NxtException.ValidationException {

        String transactionBytes = req.getParameter("transactionBytes");
        if (transactionBytes == null) {
            return MISSING_TRANSACTION_BYTES;
        }

        try {
            JSONObject response;
            try {
                byte[] bytes = Convert.parseHexString(transactionBytes);
                Transaction transaction = Nxt.getTransactionProcessor().parseTransaction(bytes);
                response = JSONData.unconfirmedTransaction(transaction);
                response.put("verify", transaction.verify());
            } catch (NxtException.ValidationException e) {
                response = new JSONObject();
                response.put("error", e.getMessage());
            }
            return response;
        } catch (RuntimeException e) {
            return INCORRECT_TRANSACTION_BYTES;
        }

    }

}
