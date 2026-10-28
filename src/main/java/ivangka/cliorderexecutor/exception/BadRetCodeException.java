package ivangka.cliorderexecutor.exception;

import java.util.HashMap;
import java.util.Map;

public class BadRetCodeException extends Exception {

    public static final Map<String, String> RETCODES = new HashMap<>();
    static {
        RETCODES.put("10001", "Request parameter error");
        RETCODES.put("10002", "The request time exceeds the time window range");
        RETCODES.put("10003", "API key is invalid. Check whether the key and domain are matched,\n  " +
                "there are 4 env: mainnet, testnet, mainnet-demo, testnet-demo");
        RETCODES.put("10005", "Permission denied, please check your API key permissions");
        RETCODES.put("10007", "User authentication failed");
        RETCODES.put("10010", "Unmatched IP, please check your API key's bound IP addresses");
        RETCODES.put("110003", "Order price exceeds the allowable range");
        RETCODES.put("110004", "Wallet balance is insufficient");
        RETCODES.put("110007", "Available balance is insufficient");
        RETCODES.put("110012", "Insufficient available balance");
        RETCODES.put("110018", "User ID is illegal");
        RETCODES.put("110043", "Set leverage has not been modified");
        RETCODES.put("33004", "Your api key has expired");
        RETCODES.put("34040", "Not modified. Indicates you already set this TP/SL value\n  " +
                        "or you didn't pass a required parameter");
    }

    public BadRetCodeException(String message) {
        super(message);
    }

}
