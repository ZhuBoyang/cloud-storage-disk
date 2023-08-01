package online.yangcloud.model.view.user;

/**
 * @author zhuboyang
 * @since 2023年06月14 09:47:24
 */
public class LoginView {

    /**
     * token
     */
    private String token;

    /**
     * 根目录文件 id
     */
    private String id;

    public static LoginView pack(String token, String id) {
        return new LoginView().setToken(token).setId(id);
    }

    public String getToken() {
        return token;
    }

    public LoginView setToken(String token) {
        this.token = token;
        return this;
    }

    public String getId() {
        return id;
    }

    public LoginView setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "LoginView["
                + " token=" + token + ","
                + " id=" + id
                + " ]";
    }
}
