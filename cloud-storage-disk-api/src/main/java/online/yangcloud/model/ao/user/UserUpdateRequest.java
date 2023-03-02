package online.yangcloud.model.ao.user;

import javax.validation.constraints.NotBlank;

/**
 * 用户账户信息修改请求
 *
 * @author zhuboyang
 * @since 2023年01月19 14:24:46
 */
public class UserUpdateRequest {

    /**
     * 主键
     */
    @NotBlank
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
    private String birthday;

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

    public String getId() {
        return id;
    }

    public UserUpdateRequest setId(String id) {
        this.id = id;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public UserUpdateRequest setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserUpdateRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserUpdateRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public UserUpdateRequest setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public UserUpdateRequest setAge(Integer age) {
        this.age = age;
        return this;
    }

    public Integer getGender() {
        return gender;
    }

    public UserUpdateRequest setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserUpdateRequest setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Override
    public String toString() {
        return "UserUpdateRequest["
                + " id=" + id + ","
                + " userName=" + userName + ","
                + " email=" + email + ","
                + " password=" + password + ","
                + " birthday=" + birthday + ","
                + " age=" + age + ","
                + " gender=" + gender + ","
                + " phone=" + phone
                + " ]";
    }
}
