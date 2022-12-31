package online.yangcloud.common.resultcode;

/**
 * @author zhuboyang
 * @since 2022年03月12 16:18:36
 */
public interface EmailResultCode {

    interface EmailMsg {
        /**
         * 验证码已发送
         */
        String EMAIL_CODE_HAS_BEEN_SEND = "验证码已发送";
        /**
         * 验证码已发送，请勿重复发送
         */
        String NEED_NOT_SEND_AGAIN = "验证码已发送，请勿重复发送";
    }

}
