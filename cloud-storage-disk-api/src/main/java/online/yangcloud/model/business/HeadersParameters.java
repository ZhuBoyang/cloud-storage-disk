package online.yangcloud.model.business;

/**
 * @author zhuboyang
 * @since 2023年08月01 09:57:33
 */
public class HeadersParameters {

    /**
     * 登录令牌
     */
    private String authorization;

    public String getAuthorization() {
        return authorization;
    }

    public HeadersParameters setAuthorization(String authorization) {
        this.authorization = authorization;
        return this;
    }

    @Override
    public String toString() {
        return "HeadersParameters["
                + " authorization=" + authorization
                + " ]";
    }
}
