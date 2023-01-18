package online.yangcloud.model.vo.user;

import java.util.Date;

/**
 * 用户视图数据模型
 *
 * @author zhuboyang
 * @since 2023年01月18 15:21:36
 */
public class UserView {

    /**
     * 主键
     */
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

    public String getId() {
        return id;
    }

    public UserView setId(String id) {
        this.id = id;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public UserView setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserView setEmail(String email) {
        this.email = email;
        return this;
    }

    public Date getBirthday() {
        return birthday;
    }

    public UserView setBirthday(Date birthday) {
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

    public Date getCreateTime() {
        return createTime;
    }

    public UserView setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public UserView setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public String toString() {
        return "UserView["
                + " id=" + id + ","
                + " userName=" + userName + ","
                + " email=" + email + ","
                + " birthday=" + birthday + ","
                + " age=" + age + ","
                + " gender=" + gender + ","
                + " phone=" + phone + ","
                + " createTime=" + createTime + ","
                + " updateTime=" + updateTime
                + " ]";
    }
}
