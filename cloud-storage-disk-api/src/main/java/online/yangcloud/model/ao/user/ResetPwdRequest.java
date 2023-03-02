package online.yangcloud.model.ao.user;

import javax.validation.constraints.NotBlank;

/**
 * 重置密码的请求
 *
 * @author zhuboyang
 * @since 2023年01月19 14:04:52
 */
public class ResetPwdRequest {

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
        return "ResetPwdRequest["
                + " email=" + email + ","
                + " password=" + password
                + " ]";
    }
}
