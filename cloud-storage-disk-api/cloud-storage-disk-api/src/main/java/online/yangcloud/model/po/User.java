package online.yangcloud.model.po;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;

import java.util.Date;

/**
 * 用户模型
 *
 * @author zhuboyang
 * @since 2023年01月18 11:20:25
 */
@FluentMybatis(table = "user")
public class User extends RichEntity {

    /**
     * 主键
     */
    @TableId
    private String id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 生日
     */
    private Date birthday;

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
     * 账号创建时间
     */
    private Date createTime;

    /**
     * 账号数据修改时间
     */
    private Date updateTime;

    /**
     * 封装用户账户数据
     *
     * @param userName 用户名
     * @param email    邮箱
     * @param password 密码
     * @return result
     */
    public static User packageData(String userName, String email, String password) {
        return new User()
                .setId(IdUtil.fastSimpleUUID())
                .setUserName(userName)
                .setEmail(email)
                .setPassword(SecureUtil.md5(password))
                .setBirthday(null)
                .setAge(-1)
                .setGender(-1)
                .setPhone(CharSequenceUtil.EMPTY)
                .setCreateTime(DateUtil.date())
                .setUpdateTime(DateUtil.date());
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
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

    public Date getBirthday() {
        return birthday;
    }

    public User setBirthday(Date birthday) {
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

    public Date getCreateTime() {
        return createTime;
    }

    public User setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public User setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public String toString() {
        return "User["
                + " id=" + id + ","
                + " userName=" + userName + ","
                + " email=" + email + ","
                + " password=" + password + ","
                + " birthday=" + birthday + ","
                + " age=" + age + ","
                + " gender=" + gender + ","
                + " phone=" + phone + ","
                + " createTime=" + createTime + ","
                + " updateTime=" + updateTime
                + " ]"
                + " "
                + super.toString();
    }
}
