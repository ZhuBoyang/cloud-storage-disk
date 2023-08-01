package online.yangcloud.model.request.email;

import javax.validation.constraints.NotBlank;

/**
 * @author zhuboyang
 * @since 2023年06月12 16:08:21
 */
public class EmailSender {

    /**
     * 邮箱地址
     */
    @NotBlank
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "EmailSender["
                + " email=" + email
                + " ]";
    }
}
