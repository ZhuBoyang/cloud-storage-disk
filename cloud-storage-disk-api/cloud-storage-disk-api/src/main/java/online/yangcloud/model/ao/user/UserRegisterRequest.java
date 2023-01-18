package online.yangcloud.model.ao.user;

import javax.validation.constraints.NotBlank;

/**
 * 用户注册请求
 * @author zhuboyang
 * @since 2023年01月18 13:39:12
 */
public class UserRegisterRequest {

    /**
     * 用户名
     */
    @NotBlank
    private String userName;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

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
        return "UserRegisterRequest["
                + " userName=" + userName + ","
                + " email=" + email + ","
                + " password=" + password
                + " ]";
    }
}
