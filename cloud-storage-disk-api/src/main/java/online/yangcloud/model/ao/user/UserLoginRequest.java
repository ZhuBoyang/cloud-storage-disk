package online.yangcloud.model.ao.user;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录请求
 *
 * @author zhuboyang
 * @since 2023年01月18 15:14:13
 */
public class UserLoginRequest {

    /**
     * 邮箱
     */
    @NotBlank
    private String email;

    /**
     * 密码
     */
    @NotBlank
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserLoginRequest["
                + " email=" + email + ","
                + " password=" + password
                + " ]";
    }
}
