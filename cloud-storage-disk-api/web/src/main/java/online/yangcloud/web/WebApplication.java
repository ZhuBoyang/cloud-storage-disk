package online.yangcloud.web;

import cn.hutool.core.io.FileUtil;
import cn.org.atool.fluent.mybatis.spring.MapperFactory;
import online.yangcloud.common.utils.SystemTools;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhuboyang
 * @since 2023/8/14 21:57
 */
@SpringBootApplication
@ComponentScan(value = "online.yangcloud")
@MapperScan(basePackages = "online.yangcloud.common.mapper")
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
        validEmailConfig();
    }

    @Bean
    public MapperFactory mapperFactory() {
        return new MapperFactory();
    }

    /**
     * 检测邮件配置文件是否存在，如果不存在，则直接中断启动
     */
    private static void validEmailConfig() {
        if (!FileUtil.exist(FileUtil.file(SystemTools.systemPath() + "mail_config.txt"))) {
            System.out.println("\n````````````````````    文件内容    ````````````````````");
            System.out.println("# 邮件服务器的SMTP地址，可选，默认为smtp.<发件人邮箱后缀>");
            System.out.println("host=smtp.qq.com");
            System.out.println("# 邮件服务器的SMTP端口，可选，默认25");
            System.out.println("port=587");
            System.out.println("# 发件人（必须正确，否则发送失败）(例：123@qq.com)");
            System.out.println("from=");
            System.out.println("# 用户名，默认为发件人邮箱前缀(例：123)");
            System.out.println("user=");
            System.out.println("# 密码（注意，某些邮箱需要为SMTP服务单独设置授权码，详情查看相关帮助）");
            System.out.println("qq 邮箱开启 smtp 请参照此链接：https://www.juziyu.cn/2143.html");
            System.out.println("pass=");
            System.out.println("````````````````````       结束      ````````````````````\n");
            System.out.println("请将上述内容补充完整，并放到文件【" + SystemTools.systemPath() + "mail_config.txt】中，并重新启动");
            System.exit(1);
        }
    }

}
