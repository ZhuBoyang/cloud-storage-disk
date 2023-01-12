package online.yangcloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.ResultBean;
import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.common.resultcode.AppResultCode;
import online.yangcloud.enumration.FileTypeEnum;
import online.yangcloud.enumration.YesOrNoEnum;
import online.yangcloud.exception.BusinessException;
import online.yangcloud.model.ao.file.FileSearchRequest;
import online.yangcloud.model.mapper.FileBlockMapper;
import online.yangcloud.model.mapper.FileMetadataMapper;
import online.yangcloud.model.po.FileBlock;
import online.yangcloud.model.po.FileMetadata;
import online.yangcloud.model.vo.file.FileBreadView;
import online.yangcloud.model.vo.file.FileMetadataView;
import online.yangcloud.model.wrapper.FileMetadataQuery;
import online.yangcloud.service.FileMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhuboyang
 * @since 2023年01月03 11:08:47
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FileMetadataServiceImpl implements FileMetadataService {

    private static final Logger logger = LoggerFactory.getLogger(FileMetadataServiceImpl.class);

    @Autowired
    private FileMetadataMapper fileMetadataMapper;

    @Autowired
    private FileBlockMapper fileBlockMapper;

    @Override
    public FileMetadata insertOne(FileMetadata file) {
        int updateResult = fileMetadataMapper.insertWithPk(file);
        if (updateResult == 0) {
            throw new BusinessException("文件添加失败，请重试");
        }
        return file;
    }

    @Override
    public FileMetadataView mkdir(String pid, String fileName) {
        // 查询父级目录的数据，以便获取所有父级目录 id 信息
        FileMetadata parent = fileMetadataMapper.findById(pid);
        if (ObjUtil.isNull(parent)) {
            logger.error("父级目录不存在，操作失败，请重试");
            throw new BusinessException("父级目录不存在，操作失败，请重试");
        }

        // 查询与文件夹名称有关的文件夹
        List<FileMetadata> existDirectories = queryLikePrefix(pid, fileName, FileTypeEnum.DIR);

        // 计算存储文件的文件名后的后缀数字
        int fileNumber = calculateFileNumber(existDirectories, fileName);

        // 封装文件元数据并入库
        FileMetadata file = new FileMetadata()
                .setId(IdUtil.fastSimpleUUID())
                .setPid(pid)
                .setName(fileNumber == 0 ? fileName : fileName + AppConstants.LEFT_BRACKET + fileNumber + AppConstants.RIGHT_BRACKET)
                .setExt(CharSequenceUtil.EMPTY)
                .setHash(CharSequenceUtil.EMPTY)
                .setPath(CharSequenceUtil.EMPTY)
                .setType(FileTypeEnum.DIR.getCode())
                .setSize(0L)
                .setAncestors(CharSequenceUtil.isBlank(parent.getAncestors()) ? parent.getId() : parent.getAncestors() + StrUtil.COMMA + parent.getId())
                .setUploadTime(DateUtil.date())
                .setUpdateTime(DateUtil.date());
        int updateResult = fileMetadataMapper.insertWithPk(file);
        if (updateResult == 0) {
            logger.error("文件夹新建失败，请重试");
            throw new BusinessException("文件夹新建失败，请重试");
        }

        // 返回视图数据
        return BeanUtil.copyProperties(file, FileMetadataView.class);
    }

    @Override
    public ResultBean<?> batchDeleteFile(List<String> fileIds) {
        // 检测存在的文件
        List<FileMetadata> files = fileMetadataMapper.listByIds(fileIds);

        // 查询文件对应的，文件与文件块的关联记录
        fileIds = files.stream().map(FileMetadata::getId).collect(Collectors.toList());
        List<FileBlock> fileBlocks = fileBlockMapper.listEntity(fileBlockMapper.query().where.fileId().in(fileIds).end());
        List<String> fileBlocksIds = fileBlocks.stream().map(FileBlock::getId).collect(Collectors.toList());

        // 删除文件
        int updateResult = fileMetadataMapper.updateBy(fileMetadataMapper.updater()
                .set.isDelete().is(YesOrNoEnum.YES.getCode()).end()
                .where.id().in(fileIds).end());
        if (updateResult != fileIds.size()) {
            logger.error("文件删除失败，请重试");
            throw new BusinessException("文件删除失败，请重试");
        }

        // 删除文件与文件块的关联关系
        if (fileBlocksIds.size() > 0) {
            updateResult = fileBlockMapper.updateBy(fileBlockMapper.updater()
                    .set.isDelete().is(YesOrNoEnum.YES.getCode()).end()
                    .where.id().in(fileBlocksIds).end());
            if (updateResult != fileBlocksIds.size()) {
                logger.error("文件块删除失败，导致文件删除失败，请重试");
                throw new BusinessException("文件删除失败，请重新尝试");
            }
        }
        return ResultBean.success();
    }

    /**
     * 计算文件名名后的后缀数字
     *
     * @param files    计算文件范围列表
     * @param fileName 文件名
     * @return result
     */
    public static int calculateFileNumber(List<FileMetadata> files, String fileName) {
        int fileNumber = 0;
        if (files.size() > 0) {
            // 1. 将文件夹名称刨除掉，过滤掉名称不同的文件夹。如：test（1）切分为 1
            // 2. 将名称刨除掉后，剩下的名称部分，留下是数字的
            // 3. 将数字（字符串）转为数字，并升序排序
            List<Integer> fileNumbers = files.stream()
                    .map(file -> fileName.equals(file.getName()) ?
                            CharSequenceUtil.EMPTY : file.getName().substring(fileName.length() + 1, file.getName().length() - 1))
                    .filter(nameNumber -> CharSequenceUtil.isBlank(nameNumber) || NumberUtil.isInteger(nameNumber))
                    .map(nameNumber -> CharSequenceUtil.isBlank(nameNumber) ? 0 : Integer.parseInt(nameNumber))
                    .sorted(Comparator.comparingInt(Integer::intValue))
                    .collect(Collectors.toList());
            if (fileNumbers.size() > 0) {
                fileNumber = fileNumbers.get(fileNumbers.size() - 1) + 1;
            }
        }
        return fileNumber;
    }

    @Override
    public List<FileBreadView> queryFileBreads(String id) {
        // 查询顶级目录
        if (CharSequenceUtil.isBlank(id)) {
            FileMetadata rootFile = fileMetadataMapper.findOne(fileMetadataMapper.query().where.pid().eq(id).end());
            return Collections.singletonList(FileBreadView.packageData(rootFile.getId(), rootFile.getName()));
        }

        // 校验目录是否存在
        FileMetadata file = fileMetadataMapper.findById(id);
        if (ObjUtil.isNull(file)) {
            logger.error("文件夹不存在，操作失败，请重试");
            throw new BusinessException("文件夹不存在，操作失败，请重试");
        }

        if (CharSequenceUtil.isBlank(file.getAncestors())) {
            // 自己就是最顶级的目录，没有父级目录
            return Collections.singletonList(FileBreadView.packageData(file.getId(), file.getName()));
        }

        // 获取所有的父级目录，并查询父级目录信息
        List<String> parentIds = CharSequenceUtil.split(file.getAncestors(), StrUtil.COMMA);
        List<FileMetadata> parents = fileMetadataMapper.listByIds(parentIds);
        parents.sort((o1, o2) -> DateUtil.compare(o1.getUploadTime(), o2.getUploadTime()));
        List<FileBreadView> breadsViews =
                parents.stream().map(parent -> FileBreadView.packageData(parent.getId(), parent.getName())).collect(Collectors.toList());
        breadsViews.add(FileBreadView.packageData(file.getId(), file.getName()));
        return breadsViews;
    }

    @Override
    public FileMetadata queryById(String id) {
        return fileMetadataMapper.findById(id);
    }

    @Override
    public List<FileMetadataView> queryFiles(FileSearchRequest searchRequest) {
        // 查询起始点文件元数据
        FileMetadata beginFile;
        if (CharSequenceUtil.isBlank(searchRequest.getFileId())) {
            beginFile = fileMetadataMapper.findOne(fileMetadataMapper.query().where.pid().eq(searchRequest.getFileId()).end());
        } else {
            beginFile = fileMetadataMapper.findById(searchRequest.getFileId());
        }
        if (ObjUtil.isNull(beginFile)) {
            logger.error(AppResultCode.FAILURE.getMessage());
            throw new BusinessException(AppResultCode.FAILURE.getMessage());
        }

        // 构建查询条件
        FileMetadataQuery query = fileMetadataMapper.query().where.uploadTime().gt(beginFile.getUploadTime()).end().limit(0, searchRequest.getSize());
        if (CharSequenceUtil.isNotBlank(searchRequest.getPid())) {
            query.where.pid().eq(searchRequest.getPid());
        }
        if (CharSequenceUtil.isNotBlank(searchRequest.getFileName())) {
            query.where.name().like(searchRequest.getFileName().replaceAll(CharSequenceUtil.EMPTY, AppConstants.PERCENT));
        }
        List<FileMetadata> files = fileMetadataMapper.listEntity(query);
        return files.stream()
                .map(file -> BeanUtil.copyProperties(file, FileMetadataView.class))
                .sorted((o1, o2) -> DateUtil.compare(o1.getUploadTime(), o2.getUploadTime())).collect(Collectors.toList());
    }

    @Override
    public List<FileMetadata> queryLikePrefix(String pid, String fileName, FileTypeEnum type) {
        return fileMetadataMapper.listEntity(fileMetadataMapper.query()
                .where.pid().eq(pid)
                .and.name().like(fileName.trim() + AppConstants.PERCENT)
                .and.type().eq(type.getCode()).end());
    }

}
