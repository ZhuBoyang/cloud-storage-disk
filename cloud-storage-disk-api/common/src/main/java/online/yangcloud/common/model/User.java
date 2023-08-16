package online.yangcloud.common.model;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import online.yangcloud.common.annotation.TokenEntity;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.utils.IdTools;

/**
 * 用户模型
 *
 * @author zhuboyang
 * @since 2023年01月18 11:20:25
 */
@FluentMybatis
@TokenEntity
public class User extends BaseParameter {

    /**
     * 主键
     */
    @TableId
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
     * 密码
     */
    private String password;

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

    public static User initial() {
        return new User();
    }

    public static User initial(String email, String password) {
        return new User()
                .setId(IdTools.fastSimpleUuid())
                .setNickName(email.substring(0, email.indexOf(StrUtil.AT)))
                .setEmail(email)
                .setPassword(password)
                .setAvatar(CharSequenceUtil.EMPTY)
                .setBirthday(0L)
                .setAge(0)
                .setGender(0)
                .setPhone(CharSequenceUtil.EMPTY)
                .setTotalSpaceSize(AppConstants.Account.TOTAL_SPACE_SIZE)
                .setUsedSpaceSize(0L);
    }

    public static User pack(String userId, Long usedSpaceSize) {
        return new User().setId(userId).setUsedSpaceSize(usedSpaceSize);
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public User setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public User setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public Long getBirthday() {
        return birthday;
    }

    public User setBirthday(Long birthday) {
        this.birthday = birthday;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public User setAge(Integer age) {
        this.age = age;
        return this;
    }

    public Integer getGender() {
        return gender;
    }

    public User setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Long getTotalSpaceSize() {
        return totalSpaceSize;
    }

    public User setTotalSpaceSize(Long totalSpaceSize) {
        this.totalSpaceSize = totalSpaceSize;
        return this;
    }

    public Long getUsedSpaceSize() {
        return usedSpaceSize;
    }

    public User setUsedSpaceSize(Long usedSpaceSize) {
        this.usedSpaceSize = usedSpaceSize;
        return this;
    }

    @Override
    public String toString() {
        return "User["
                + " id=" + id + ","
                + " nickName=" + nickName + ","
                + " email=" + email + ","
                + " password=" + password + ","
                + " avatar=" + avatar + ","
                + " birthday=" + birthday + ","
                + " age=" + age + ","
                + " gender=" + gender + ","
                + " phone=" + phone + ","
                + " totalSpaceSize=" + totalSpaceSize + ","
                + " usedSpaceSize=" + usedSpaceSize
                + " ]"
                + " "
                + super.toString();
    }
}
