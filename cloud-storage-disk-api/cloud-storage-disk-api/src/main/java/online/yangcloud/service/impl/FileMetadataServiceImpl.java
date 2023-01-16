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
import online.yangcloud.model.bo.FileOperationValidate;
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

import java.util.*;
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
    public ResultBean<?> batchMoveFiles(List<String> sources, String target) {
        // 对待操作文件和文件夹与目标目录进行校验
        FileOperationValidate validate = validateFiles(sources, target);
        FileMetadata targetFile = validate.getTargetFile();
        List<FileMetadata> sourceFiles = validate.getSourceFiles();

        // 修改待移动文件的父级文件 id 和所有祖级 id
        String ancestor = targetFile.getId();
        if (CharSequenceUtil.isNotBlank(targetFile.getAncestors())) {
            ancestor += StrUtil.COMMA + targetFile.getAncestors();
        } 
        int updateResult = fileMetadataMapper.updateBy(fileMetadataMapper.updater()
                .set.pid().is(target).ancestors().is(ancestor).end()
                .where.id().in(sources).end());
        if (updateResult != sourceFiles.size()) {
            logger.error("文件移动失败，请重试");
            throw new BusinessException("文件移动失败，请重试");
        }

        // 修改所有子级文件的祖级 id
        List<FileMetadata> dirFiles = sourceFiles.stream().filter(file -> YesOrNoEnum.YES.is(file.getType())).collect(Collectors.toList());
        for (FileMetadata dir : dirFiles) {
            List<FileMetadata> childList = queryChildFiles(dir.getId());
            for (FileMetadata child : childList) {
                String childAncestor = child.getAncestors();
                String sourceId = sourceFiles.get(0).getId();
                childAncestor = childAncestor.substring(0, childAncestor.indexOf(sourceId) + sourceId.length());
                childAncestor = childAncestor + StrUtil.COMMA + ancestor;
                child.setAncestors(childAncestor);
                updateResult = fileMetadataMapper.updateById(child);
                if (updateResult == 0) {
                    logger.error("文件移动失败，请重新尝试");
                    throw new BusinessException("文件移动失败，请重新尝试");
                }
            }
        }
        return ResultBean.success();
    }

    @Override
    public ResultBean<?> batchCopyFiles(List<String> sources, String target) {
        // 对待操作文件和文件夹与目标目录进行校验
        FileOperationValidate validate = validateFiles(sources, target);
        FileMetadata targetFile = validate.getTargetFile();
        List<FileMetadata> sourceFiles = validate.getSourceFiles();

        // 修改呗复制文件的父级文件 id 和所有祖级 id
        String ancestor;
        if (CharSequenceUtil.isBlank(targetFile.getAncestors())) {
            ancestor = targetFile.getId();
        } else {
            ancestor = targetFile.getId() + StrUtil.COMMA + targetFile.getAncestors();
        }

        // 构建复制后的文件及文件夹，并入库
        for (FileMetadata file : sourceFiles) {
            String fileId = file.getId();
            // 查询与文件夹名称有关的文件夹
            List<FileMetadata> existDirectories = queryLikePrefix(target, file.getName(), FileTypeEnum.DIR);
            // 计算存储文件的文件名后的后缀数字
            int fileNumber = calculateFileNumber(existDirectories, file.getName());
            file.setId(IdUtil.fastSimpleUUID())
                    .setPid(target)
                    .setAncestors(ancestor)
                    .setName(fileNumber == 0 ? file.getName() : file.getName() + AppConstants.LEFT_BRACKET + fileNumber + AppConstants.RIGHT_BRACKET);
            int updateResult = fileMetadataMapper.insertWithPk(file);
            if (updateResult == 0) {
                logger.error("文件复制失败，请重试");
                throw new BusinessException("");
            }
            // 递归复制下级的所有文件与文件夹
            List<FileMetadata> childList = fileMetadataMapper.listEntity(fileMetadataMapper.query()
                    .where.pid().eq(fileId)
                    .and.isDelete().eq(YesOrNoEnum.NO.getCode()).end());
            copyChildFiles(childList, file);
        }
        return ResultBean.success();
    }

    /**
     * 递归复制文件及文件夹
     *
     * @param sources 待复制文件及文件夹
     * @param target  目标目录
     */
    private void copyChildFiles(List<FileMetadata> sources, FileMetadata target) {
        for (FileMetadata source : sources) {
            String fileId = source.getId();
            // 复制文件
            source.setId(IdUtil.fastSimpleUUID())
                    .setPid(target.getId())
                    .setAncestors(target.getId() + StrUtil.COMMA + target.getAncestors());
            int updateResult = fileMetadataMapper.insertWithPk(source);
            if (updateResult == 0) {
                logger.error("文件复制失败，请重新尝试");
                throw new BusinessException("文件复制失败，请重新尝试");
            }
            // 如果是文件夹的话，需要复制文件夹下的所有的文件及文件夹
            if (YesOrNoEnum.YES.is(source.getType())) {
                List<FileMetadata> childList = fileMetadataMapper.listEntity(fileMetadataMapper.query()
                        .where.pid().eq(fileId)
                        .and.isDelete().eq(YesOrNoEnum.NO.getCode()).end());
                copyChildFiles(childList, source);
            }
        }
    }

    /**
     * 移动、复制时的文件校验
     *
     * @param sources 待操作文件
     * @param target  目标目录
     * @return result
     */
    private FileOperationValidate validateFiles(List<String> sources, String target) {
        // 校验目标目录是否存在
        FileMetadata targetFile = fileMetadataMapper.findById(target);
        if (ObjUtil.isNull(targetFile) || YesOrNoEnum.YES.is(targetFile.getIsDelete())) {
            logger.error("目标目录不存在，请刷新后重试");
            throw new BusinessException("目标目录不存在，请刷新后重试");
        }

        // 检测目标文件是否是目录
        if (YesOrNoEnum.NO.is(targetFile.getType())) {
            logger.error("请选择目录");
            throw new BusinessException("请选择目录");
        }

        // 校验待移动文件是否都存在
        List<FileMetadata> sourceFiles = fileMetadataMapper.listByIds(sources);
        if (sourceFiles.size() != sources.size()) {
            logger.error("待操作文件非全部存在，请刷新后重试");
            throw new BusinessException("待操作文件非全部存在，请刷新后重试");
        }

        // 检测目标目录是否为待移动文件的直接父级目录。如果是的话，抛出异常
//        if (target.equals(sourceFiles.get(0).getPid())) {
//            logger.error("已在目标目录下，无需操作");
//            throw new BusinessException("已在目标目录下，无需操作");
//        }

        // 检测目标目录是否为待移动文件或文件夹的子级目录
        String targetAncestors = targetFile.getAncestors();
        List<String> targetIdList = CharSequenceUtil.split(targetAncestors, StrUtil.COMMA);
        targetIdList.add(targetFile.getId());
        for (FileMetadata source : sourceFiles) {
            if (YesOrNoEnum.YES.is(source.getType())) {
                if (targetIdList.contains(source.getId())) {
                    logger.error("无法操作至下级目录，请选择其他目录");
                    throw new BusinessException("无法操作至下级目录，请选择其他目录");
                }
            }
        }
        return new FileOperationValidate()
                .setTargetFile(targetFile)
                .setSourceFiles(sourceFiles);
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
        if (ObjUtil.isNull(file) || YesOrNoEnum.YES.is(file.getIsDelete())) {
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
            beginFile = fileMetadataMapper.findOne(fileMetadataMapper.query()
                    .where.pid().eq(searchRequest.getFileId()).end());
        } else {
            beginFile = fileMetadataMapper.findById(searchRequest.getFileId());
        }
        if (ObjUtil.isNull(beginFile) || YesOrNoEnum.YES.is(beginFile.getIsDelete())) {
            logger.error(AppResultCode.FAILURE.getMessage());
            throw new BusinessException(AppResultCode.FAILURE.getMessage());
        }

        // 构建查询条件
        FileMetadataQuery query = fileMetadataMapper.query()
                .where.uploadTime().gt(beginFile.getUploadTime())
                .and.isDelete().eq(YesOrNoEnum.NO.getCode()).end()
                .limit(0, searchRequest.getSize());
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
    public List<FileMetadata> queryChildFiles(String fileId) {
        return fileMetadataMapper.listEntity(fileMetadataMapper.query()
                .where.ancestors().like(AppConstants.PERCENT + fileId.trim() + AppConstants.PERCENT)
                .and.isDelete().eq(YesOrNoEnum.NO.getCode()).end());
    }

    @Override
    public List<FileMetadata> queryLikePrefix(String pid, String fileName, FileTypeEnum type) {
        return fileMetadataMapper.listEntity(fileMetadataMapper.query()
                .where.pid().eq(pid)
                .and.name().like(fileName.trim() + AppConstants.PERCENT)
                .and.type().eq(type.getCode())
                .and.isDelete().eq(YesOrNoEnum.NO.getCode()).end());
    }

    @Override
    public List<FileBreadView> queryDirBreads(String parentId) {
        if (CharSequenceUtil.isBlank(parentId)) {
            FileMetadata parent = fileMetadataMapper.findOne(fileMetadataMapper.query().where.pid().eq(parentId).end());
            return Collections.singletonList(BeanUtil.copyProperties(parent, FileBreadView.class));
        }

        // 查询父级目录本身
        FileMetadata parent = fileMetadataMapper.findById(parentId);

        // 查询祖级目录
        List<String> parentIds = CharSequenceUtil.split(parent.getAncestors(), StrUtil.COMMA);
        List<FileMetadata> parentsList = fileMetadataMapper.listByIds(parentIds);

        // 合并父级目录本身与祖级目录
        parentsList.add(parent);

        // 构建视图数据并返回
        return parentsList.stream().map(file -> BeanUtil.copyProperties(file, FileBreadView.class)).collect(Collectors.toList());
    }

    @Override
    public List<FileMetadata> queryDirs(String parentId, Integer size) {
        // 计算父级目录 id
        String pid = CharSequenceUtil.isBlank(parentId) ?
                fileMetadataMapper.findOne(fileMetadataMapper.query().where.pid().eq(CharSequenceUtil.EMPTY).end()).getId() : parentId;

        // 构建查询语句
        FileMetadataQuery query = fileMetadataMapper.query()
                .where.type().eq(YesOrNoEnum.YES.getCode())
                .and.pid().eq(pid).end()
                .orderBy.uploadTime().asc().end()
                .limit(0, size);

        // 查询文件夹并返回
        return fileMetadataMapper.listEntity(query);
    }

}
