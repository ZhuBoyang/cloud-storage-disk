package online.yangcloud.model.view.user;

import online.yangcloud.model.User;

/**
 * @author zhuboyang
 * @since 2023年06月03 18:22:47
 */
public class UserView {

    /**
     * 主键
     */
    private String id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 生日
     */
    private Long birthday;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 账户总容量
     */
    private Long totalSpaceSize;

    /**
     * 账户已用容量
     */
    private Long usedSpaceSize;

    public static UserView convert(User user) {
        return new UserView()
                .setId(user.getId())
                .setNickName(user.getNickName())
                .setEmail(user.getEmail())
                .setAvatar(user.getAvatar())
                .setBirthday(user.getBirthday())
                .setAge(user.getAge())
                .setPhone(user.getPhone())
                .setTotalSpaceSize(user.getTotalSpaceSize())
                .setUsedSpaceSize(user.getUsedSpaceSize());
    }

    public String getId() {
        return id;
    }

    public UserView setId(String id) {
        this.id = id;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public UserView setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserView setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public UserView setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public Long getBirthday() {
        return birthday;
    }

    public UserView setBirthday(Long birthday) {
        this.birthday = birthday;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public UserView setAge(Integer age) {
        this.age = age;
        return this;
    }

    public Integer getGender() {
        return gender;
    }

    public UserView setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserView setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Long getTotalSpaceSize() {
        return totalSpaceSize;
    }

    public UserView setTotalSpaceSize(Long totalSpaceSize) {
        this.totalSpaceSize = totalSpaceSize;
        return this;
    }

    public Long getUsedSpaceSize() {
        return usedSpaceSize;
    }

    public UserView setUsedSpaceSize(Long usedSpaceSize) {
        this.usedSpaceSize = usedSpaceSize;
        return this;
    }

    @Override
    public String toString() {
        return "UserView["
                + " id=" + id + ","
                + " nickName=" + nickName + ","
                + " email=" + email + ","
                + " avatar=" + avatar + ","
                + " birthday=" + birthday + ","
                + " age=" + age + ","
                + " gender=" + gender + ","
                + " phone=" + phone + ","
                + " totalSpaceSize=" + totalSpaceSize + ","
                + " usedSpaceSize=" + usedSpaceSize
                + " ]";
    }
}
