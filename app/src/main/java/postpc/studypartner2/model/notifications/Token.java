package postpc.studypartner2.model.notifications;

/**
 * An FCM token (Registration Token) that allows the client to receive messages.
 */
public class Token {

    private String token;

    public Token(String token) {
        this.token = token;
    }

    public Token() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
