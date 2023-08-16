package online.yangcloud.common.model.request.user;

/**
 * @author zhuboyang
 * @since 2023年08月08 14:21:05
 */
public class UserUpdater {

    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 出生日期
     */
    private Long birthday;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UserUpdater["
                + " avatar=" + avatar + ","
                + " nickName=" + nickName + ","
                + " birthday=" + birthday + ","
                + " gender=" + gender + ","
                + " phone=" + phone
                + " ]";
    }
}
