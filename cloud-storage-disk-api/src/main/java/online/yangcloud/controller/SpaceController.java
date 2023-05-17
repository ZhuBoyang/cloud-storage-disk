package online.yangcloud.controller;

import cn.hutool.core.util.StrUtil;
import online.yangcloud.annotation.SessionValid;
import online.yangcloud.common.ResultData;
import online.yangcloud.model.vo.SystemDiskInfoView;
import online.yangcloud.utils.SystemTools;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统空间接口
 *
 * @author zhuboyang
 * @since 2023年02月01 14:33:34
 */
@RestController
@RequestMapping("/space")
public class SpaceController {

    /**
     * 查询磁盘使用率
     *
     * @return result
     */
    @SessionValid
    @GetMapping("/disk")
    public ResultData getDiskSpaceInfo() {
        SystemDiskInfoView diskInfo = SystemTools.findDiskInfo(StrUtil.SLASH);
        return ResultData.success(diskInfo);
    }

}
