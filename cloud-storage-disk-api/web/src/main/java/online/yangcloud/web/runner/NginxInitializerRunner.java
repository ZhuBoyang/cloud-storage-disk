package online.yangcloud.web.runner;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import online.yangcloud.common.enumration.SystemTypeEnum;
import online.yangcloud.common.tools.SystemTools;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年10月07 13:33:15
 */
@Order(1)
@Component
public class NginxInitializerRunner implements ApplicationRunner {

    /**
     * Nginx 主配置文件目录
     */
    private static final String NGINX_CONF = "/usr/local/nginx/conf/nginx.conf";

    /**
     * 服务配置文件目录
     */
    private static final String PAN_CONF = "/usr/local/nginx/conf/vhost/pan.conf";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String systemType = SystemTools.acquireSystemType();
        // Linux 系统下的设置
        if (SystemTypeEnum.LIN.toString().equalsIgnoreCase(systemType)) {
            if (FileUtil.exist(NGINX_CONF)) {
                FileUtil.del(NGINX_CONF);
                String nginxConfResponse = HttpUtil.get("http://storage.yangcloud.online/pan/nginx.conf");
                FileUtil.newFile(NGINX_CONF);
                FileUtil.writeLines(StrUtil.split(nginxConfResponse, StrUtil.LF), NGINX_CONF, StandardCharsets.UTF_8);
            }
            if (!FileUtil.exist(PAN_CONF)) {
                FileUtil.mkdir(PAN_CONF.substring(0, PAN_CONF.lastIndexOf(StrUtil.SLASH)));
                String panConfResponse = HttpUtil.get("http://storage.yangcloud.online/pan/pan.conf");
                List<String> responses = StrUtil.split(panConfResponse, StrUtil.LF);
                for (int i = 0; i < responses.size(); i++) {
                    if (responses.get(i).contains("cloud-storage-disk-api")) {
                        String response = responses.get(i);
                        responses.set(i, response.replace("cloud-storage-disk-api", "127.0.0.1"));
                    }
                }
                FileUtil.writeLines(responses, PAN_CONF, StandardCharsets.UTF_8);
            }

            // 启动 Nginx
            Process nginxProcess = RuntimeUtil.exec("nginx");
            nginxProcess.waitFor();
            String response = IoUtil.read(nginxProcess.getInputStream(), StandardCharsets.UTF_8);
            System.out.println("start nginx response :=> " + response);
        }
    }

}
