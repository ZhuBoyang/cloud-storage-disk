package online.yangcloud.common.model.request.user;

/**
 * 注册请求
 *
 * @author zhuboyang
 * @since 2023年06月03 18:23:22
 */
public class UserRegister {

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮箱验证码
     */
    private String code;

    /**
     * 密码
     */
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserRegister["
                + " email=" + email + ","
                + " code=" + code + ","
                + " password=" + password
                + " ]";
    }
}
