package online.yangcloud.common.model.request.user;

import javax.validation.constraints.NotBlank;

/**
 * 注册请求
 *
 * @author zhuboyang
 * @since 2023年06月03 18:23:22
 */
public class UserInitializer {

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

    /**
     * 重复密码
     */
    @NotBlank
    private String repeat;

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

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    @Override
    public String toString() {
        return "UserInitializer["
                + " email=" + email + ","
                + " password=" + password + ","
                + " repeat=" + repeat
                + " ]";
    }
}
