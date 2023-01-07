package online.yangcloud.runner;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import online.yangcloud.enumration.FileTypeEnum;
import online.yangcloud.model.mapper.FileMetadataMapper;
import online.yangcloud.model.po.FileMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 项目初始化数据
 *
 * @author zhuboyang
 * @since 2023/1/5 14:48
 */
@Component
public class InitDataRunner implements ApplicationRunner {

    @Autowired
    private FileMetadataMapper fileMetadataMapper;

    @Override
    public void run(ApplicationArguments args) {
        boolean exist = checkExistFile();
        if (!exist) {
            initFile();
        }
    }

    /**
     * 检测是否存在文件
     *
     * @return result
     */
    private boolean checkExistFile() {
        FileMetadata file = fileMetadataMapper.findOne(fileMetadataMapper.query().where.id().gt(0).end().limit(0, 1));
        return ObjUtil.isNotNull(file);
    }

    /**
     * 初始化根文件
     */
    private void initFile() {
        FileMetadata file = new FileMetadata()
                .setId(IdUtil.fastSimpleUUID())
                .setPid(CharSequenceUtil.EMPTY)
                .setName("全部")
                .setHash(CharSequenceUtil.EMPTY)
                .setExt(CharSequenceUtil.EMPTY)
                .setPath(CharSequenceUtil.EMPTY)
                .setType(FileTypeEnum.DIR.getCode())
                .setSize(0L)
                .setAncestors(CharSequenceUtil.EMPTY)
                .setUploadTime(DateUtil.date())
                .setUpdateTime(DateUtil.date());
        fileMetadataMapper.insertWithPk(file);
    }

}
