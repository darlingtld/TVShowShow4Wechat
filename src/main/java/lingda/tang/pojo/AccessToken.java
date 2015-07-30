package lingda.tang.pojo;

/**
 * Created by darlingtld on 2015/2/20.
 */

/**
 */
public class AccessToken {
    private String token;

    private int expiresIn;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }
}
