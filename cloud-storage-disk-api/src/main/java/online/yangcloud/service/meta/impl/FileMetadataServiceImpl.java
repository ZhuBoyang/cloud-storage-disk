package online.yangcloud.service.meta.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.enumration.FileTypeEnum;
import online.yangcloud.enumration.YesOrNoEnum;
import online.yangcloud.mapper.FileMetadataMapper;
import online.yangcloud.model.FileMetadata;
import online.yangcloud.model.vo.PagerView;
import online.yangcloud.service.meta.FileMetadataService;
import online.yangcloud.utils.ExceptionTools;
import online.yangcloud.wrapper.FileMetadataQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年06月10 22:15:33
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FileMetadataServiceImpl implements FileMetadataService {

    @Resource
    private FileMetadataMapper fileMetadataMapper;

    @Override
    public void insertWidthPrimaryKey(FileMetadata file) {
        int updateResult = fileMetadataMapper.insertWithPk(file);
        if (updateResult == 0) {
            ExceptionTools.businessLogger();
        }
    }

    @Override
    public void batchInsert(List<FileMetadata> files) {
        while (files.size() > 0) {
            List<FileMetadata> tmp = files.subList(0, Math.min(AppConstants.Batch.COUNT, files.size()));
            files = files.subList(Math.min(AppConstants.Batch.COUNT, files.size()), files.size());
            int updateResult = fileMetadataMapper.insertBatchWithPk(tmp);
            if (updateResult != tmp.size()) {
                ExceptionTools.businessLogger();
            }
        }
    }

    @Override
    public void batchRemove(List<String> ids, String userId) {
        while (ids.size() > 0) {
            List<String> tmp = ids.subList(0, Math.min(AppConstants.Batch.COUNT, ids.size()));
            ids = ids.subList(Math.min(AppConstants.Batch.COUNT, ids.size()), ids.size());
            int updateResult = fileMetadataMapper.updateBy(fileMetadataMapper.updater()
                    .set.isDelete().is(YesOrNoEnum.YES.code()).end()
                    .where.id().in(tmp).and.userId().eq(userId).end());
            if (updateResult != tmp.size()) {
                ExceptionTools.businessLogger();
            }
        }
    }

    @Override
    public void updateById(FileMetadata file) {
        int updateResult = fileMetadataMapper.updateById(file);
        if (updateResult == 0) {
            ExceptionTools.businessLogger();
        }
    }

    @Override
    public void batchUpdate(List<FileMetadata> files) {
        // 分批次更新
        while (files.size() != 0) {
            List<FileMetadata> tmp = files.subList(0, Math.min(AppConstants.Batch.COUNT, files.size()));
            files = files.subList(Math.min(AppConstants.Batch.COUNT, files.size()), files.size());

            // 构建 case when 语句。以及构建要更新的数据属性
            StringBuilder caseWhen = new StringBuilder(" case id ");
            tmp.forEach(o -> caseWhen.append(" when '").append(o.getId()).append("' then ? "));
            caseWhen.append(" end ");

            // 执行更新操作
            int updateResult = fileMetadataMapper.updateBy(fileMetadataMapper.updater()
                    .set.pid().applyFunc(caseWhen.toString(), FileMetadata.getFields(tmp, FileMetadata::getPid))
                    .set.ancestors().applyFunc(caseWhen.toString(), FileMetadata.getFields(tmp, FileMetadata::getAncestors))
                    .end()
                    .where.id().in(FileMetadata.getFields(tmp, FileMetadata::getId)).end());
            if (updateResult != tmp.size()) {
                ExceptionTools.businessLogger();
            }
        }
    }

    @Override
    public FileMetadata queryById(String id) {
        return fileMetadataMapper.findOne(fileMetadataMapper.query()
                .where.id().eq(id).and.isDelete().eq(YesOrNoEnum.NO.code()).end());
    }

    @Override
    public FileMetadata queryById(String id, String userId) {
        return fileMetadataMapper.findOne(fileMetadataMapper.query()
                .where.id().eq(id).and.userId().eq(userId).and.isDelete().eq(YesOrNoEnum.NO.code()).end());
    }

    @Override
    public FileMetadata queryByPid(String pid, String userId) {
        return fileMetadataMapper.findOne(fileMetadataMapper.query()
                .where.pid().eq(pid).and.userId().eq(userId).and.isDelete().eq(YesOrNoEnum.NO.code()).end());
    }

    @Override
    public List<FileMetadata> queryListByIds(List<String> fileIds, String userId) {
        return fileMetadataMapper.listEntity(fileMetadataMapper.query()
                .where.id().in(fileIds).and.userId().eq(userId).and.isDelete().eq(YesOrNoEnum.NO.code()).end());
    }

    @Override
    public List<FileMetadata> queryListByPid(String pid, String userId) {
        return fileMetadataMapper.listEntity(fileMetadataMapper.query()
                .where.pid().eq(pid).and.userId().eq(userId).and.isDelete().eq(YesOrNoEnum.NO.code()).end());
    }

    @Override
    public List<FileMetadata> queryChildDirsListByPid(String pid, String userId) {
        return queryChildListByPid(pid, userId, Boolean.TRUE);
    }

    @Override
    public List<FileMetadata> queryChildListByPid(String pid, String userId, Boolean isOnlyDir) {
        List<FileMetadata> files = fileMetadataMapper.listEntity(fileMetadataMapper.query()
                .where.pid().eq(pid).and.userId().eq(userId).and.isDelete().eq(YesOrNoEnum.NO.code()).end());
        List<FileMetadata> tmp = new ArrayList<>();
        for (FileMetadata file : files) {
            if (!isOnlyDir) {
                if (FileTypeEnum.FILE.is(file.getType())) {
                    tmp.add(file);
                }
            }
            if (FileTypeEnum.DIR.is(file.getType())) {
                tmp.add(file);
                tmp.addAll(queryChildListByPid(file.getId(), userId, isOnlyDir));
            }
        }
        return tmp;
    }

    @Override
    public List<FileMetadata> queryLikePrefix(String pid, String fileName, FileTypeEnum type) {
        return fileMetadataMapper.listEntity(fileMetadataMapper.query()
                .where.pid().eq(pid)
                .and.name().like(fileName.trim() + AppConstants.PERCENT)
                .and.type().eq(type.code())
                .and.isDelete().eq(YesOrNoEnum.NO.code()).end());
    }

    @Override
    public List<FileMetadata> queryListAfter(String pid, Long createTime, Integer size, String userId, FileTypeEnum flag) {
        FileMetadataQuery query = fileMetadataMapper.query()
                .where.pid().eq(pid)
                .and.userId().eq(userId)
                .and.createTime().gt(createTime)
                .and.isDelete().eq(YesOrNoEnum.NO.code())
                .end()
                .orderBy.uploadTime().asc().end()
                .limit(0, size);
        if (ObjectUtil.isNotNull(flag)) {
            query.where.type().eq(flag.code());
        }
        return fileMetadataMapper.listEntity(query);
    }

    @Override
    public PagerView<FileMetadata> queryFilesInPager(String pid, String name, String userId, Boolean isOnlyDir,
                                                     Integer pageIndex, Integer pageSize) {
        // 构建 SQL 搜索条件
        FileMetadataQuery query = fileMetadataMapper.query()
                .where.isDelete().eq(YesOrNoEnum.NO.code()).and.userId().eq(userId).end()
                .orderBy.uploadTime().asc().name().asc().end();
        if (CharSequenceUtil.isNotBlank(pid)) {
            // 如果要查询的父级文件 id 为 '_'，那说明要查询的是根目录下的
            if (StrUtil.UNDERLINE.equals(pid)) {
                FileMetadata root = fileMetadataMapper.findOne(fileMetadataMapper.query()
                        .where.pid().eq(CharSequenceUtil.EMPTY).and.isDelete().eq(YesOrNoEnum.NO.code()).end());
                pid = root.getPid();
            }
            query.where.pid().eq(pid);
        }
        if (CharSequenceUtil.isNotBlank(name)) {
            query.where.name().like(name.trim().replaceAll(CharSequenceUtil.EMPTY, AppConstants.PERCENT));
        }
        if (isOnlyDir) {
            query.where.type().eq(FileTypeEnum.DIR.code());
        }
        if (ObjectUtil.isNotNull(pageIndex) && ObjectUtil.isNotNull(pageSize)) {
            query.limit((pageIndex - 1) * pageSize, pageSize);
        }

        // 封装返回数据
        return new PagerView<FileMetadata>()
                .setData(fileMetadataMapper.listEntity(query))
                .setTotal(fileMetadataMapper.countNoLimit(query));
    }

}
