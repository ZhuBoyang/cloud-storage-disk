package online.yangcloud.common.model;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import online.yangcloud.common.annotation.DatabaseColumn;
import online.yangcloud.common.annotation.DatabaseIndex;
import online.yangcloud.common.annotation.TokenEntity;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.enumration.DatabaseColumnTypeEnum;
import online.yangcloud.common.tools.IdTools;

/**
 * 用户模型
 *
 * @author zhuboyang
 * @since 2023年01月18 11:20:25
 */
@FluentMybatis(table = "user", desc = "用户表")
@TokenEntity
public class User extends BaseParameter {

    /**
     * 主键
     */
    @TableId
    @DatabaseColumn(primary = true, name = "id", type = DatabaseColumnTypeEnum.VARCHAR, length = 32, canNull = false, comment = "用户 id")
    @DatabaseIndex(unique = true)
    private String id;

    /**
     * 昵称
     */
    @DatabaseColumn(name = "nick_name", type = DatabaseColumnTypeEnum.VARCHAR, length = 50, comment = "昵称")
    private String nickName;

    /**
     * 邮箱
     */
    @DatabaseColumn(name = "email", type = DatabaseColumnTypeEnum.VARCHAR, length = 50, canNull = false, comment = "邮箱")
    @DatabaseIndex(unique = true)
    private String email;

    /**
     * 密码
     */
    @DatabaseColumn(name = "password", type = DatabaseColumnTypeEnum.VARCHAR, length = 32, canNull = false, comment = "账户密码")
    private String password;

    /**
     * 头像地址
     */
    @DatabaseColumn(name = "avatar", type = DatabaseColumnTypeEnum.VARCHAR, length = 1024, canNull = false, comment = "头像地址")
    private String avatar;

    /**
     * 生日
     */
    @DatabaseColumn(name = "birthday", type = DatabaseColumnTypeEnum.BIGINT, defaultValue = "0", comment = "出生日期")
    private Long birthday;

    /**
     * 年龄
     */
    @DatabaseColumn(name = "age", type = DatabaseColumnTypeEnum.SMALLINT, defaultValue = "0", comment = "年龄")
    private Integer age;

    /**
     * 性别
     */
    @DatabaseColumn(name = "gender", type = DatabaseColumnTypeEnum.TINYINT, defaultValue = "-1", comment = "性别")
    private Integer gender;

    /**
     * 手机号
     */
    @DatabaseColumn(name = "phone", type = DatabaseColumnTypeEnum.VARCHAR, length = 13, comment = "联系方式")
    private String phone;

    /**
     * 账户总容量
     */
    @DatabaseColumn(name = "total_space_size", type = DatabaseColumnTypeEnum.BIGINT, defaultValue = "0", comment = "账户总空间")
    private Long totalSpaceSize;

    /**
     * 账户已用容量
     */
    @DatabaseColumn(name = "used_space_size", type = DatabaseColumnTypeEnum.BIGINT, defaultValue = "0", comment = "账户已用空间")
    private Long usedSpaceSize;

    public static User initial(String email, String password) {
        return new User()
                .setId(IdTools.fastSimpleUuid())
                .setNickName(email.substring(0, email.indexOf(StrUtil.AT)))
                .setEmail(email)
                .setPassword(password)
                .setAvatar(CharSequenceUtil.EMPTY)
                .setBirthday(0L)
                .setAge(0)
                .setGender(-1)
                .setPhone(CharSequenceUtil.EMPTY)
                .setTotalSpaceSize(AppConstants.Account.TOTAL_SPACE_SIZE)
                .setUsedSpaceSize(0L);
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
