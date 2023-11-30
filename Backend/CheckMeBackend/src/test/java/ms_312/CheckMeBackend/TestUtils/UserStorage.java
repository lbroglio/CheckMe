package ms_312.CheckMeBackend.TestUtils;

import java.util.Base64;

public class UserStorage {
    public String username;
    public String password;
    public String auth;

    public UserStorage(String username, String password) {
        this.username = username;
        this.password = password;
        this.auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

}