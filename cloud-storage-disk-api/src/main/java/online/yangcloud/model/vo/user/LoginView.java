package online.yangcloud.model.vo.user;

/**
 * @author zhuboyang
 * @since 2023年01月19 09:13:37
 */
public class LoginView extends UserView {

    /**
     * 会话 id
     */
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public LoginView setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    @Override
    public String toString() {
        return "LoginView["
                + " sessionId=" + sessionId
                + " ]"
                + " "
                + super.toString();
    }
}
