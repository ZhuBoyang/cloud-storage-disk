package online.yangcloud.common.model.request.user;

import javax.validation.constraints.NotBlank;

/**
 * @author zhuboyang
 * @since 2023年08月17 09:37:12
 */
public class PasswordUpdater {

    /**
     * 新密码
     */
    @NotBlank
    private String password;

    /**
     * 重复输入
     */
    @NotBlank
    private String repeat;

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
        return "PasswordUpdater["
                + " password=" + password + ","
                + " repeat=" + repeat
                + " ]";
    }
}
