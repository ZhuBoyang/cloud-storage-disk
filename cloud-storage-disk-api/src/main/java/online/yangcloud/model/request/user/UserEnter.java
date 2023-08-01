package online.yangcloud.model.request.user;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求
 *
 * @author zhuboyang
 * @since 2023年06月13 15:13:31
 */
public class UserEnter {

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
        return "UserEnter["
                + " email=" + email + ","
                + " password=" + password
                + " ]";
    }
}
