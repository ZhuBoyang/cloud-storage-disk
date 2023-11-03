package online.yangcloud.web.processor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import online.yangcloud.common.enumration.FileExtTypeEnum;
import online.yangcloud.common.tools.ExceptionTools;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * office word 处理器
 *
 * @author zhuboyang
 * @since 2023年10月14 13:37:55
 */
@Component
public class DocumentProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DocumentProcessor.class);

    /**
     * 将 word 文档转换为 pdf
     *
     * @param source     word 文档文件路径
     * @param target     目标文件路径
     * @param officeType 文档类型
     */
    public void convertToPdf(String source, String target, FileExtTypeEnum officeType) throws OfficeException {
        boolean support = isSupport(source, officeType);
        if (!support) {
            ExceptionTools.businessLogger("文档类型不支持");
        }

        OpenOfficeConnection connection = null;
        logger.info("connecting start...");
        try {
            // 连接 openoffice
            connection = new SocketOpenOfficeConnection("127.0.0.1", 2002);
            connection.connect();
            logger.info("connected...> " + connection);
            DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
            // 转换文档并记录转换时间
            logger.info("convert starting.....");
            long startMs = System.currentTimeMillis();
            if (!FileUtil.exist(target)) {
                if (!FileUtil.exist(FileUtil.file(target).getParent())) {
                    FileUtil.mkdir(FileUtil.file(target).getParent());
                }
                FileUtil.newFile(target);
            }
            converter.convert(FileUtil.file(source), FileUtil.file(target));
            // 转换完成
            long endMs = System.currentTimeMillis();
            logger.info("convert success, spend time is : {} ms.", (endMs - startMs));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
                logger.info("connection disconnected. ");
            }
        }
    }

    private static boolean isSupport(String source, FileExtTypeEnum officeType) {
        String suffix = source.substring(source.lastIndexOf(StrUtil.DOT) + 1);

        // 检测是否是支持的文件类型
        boolean support = Boolean.TRUE;
        if (FileExtTypeEnum.WORD.equals(officeType)) {
            if (!DefaultDocumentFormatRegistry.DOC.getExtension().equals(suffix)
                    && !DefaultDocumentFormatRegistry.DOCX.getExtension().equals(suffix)) {
                support = Boolean.FALSE;
            }
        }
        if (FileExtTypeEnum.PPT.equals(officeType)) {
            if (!DefaultDocumentFormatRegistry.PPT.getExtension().equals(suffix)
                    && !DefaultDocumentFormatRegistry.PPTX.getExtension().equals(suffix)) {
                support = Boolean.FALSE;
            }
        }
        if (FileExtTypeEnum.EXCEL.equals(officeType)) {
            if (!DefaultDocumentFormatRegistry.XLS.getExtension().equals(suffix)
                    && !DefaultDocumentFormatRegistry.XLSX.getExtension().equals(suffix)
                    && !DefaultDocumentFormatRegistry.CSV.getExtension().equals(suffix)) {
                support = Boolean.FALSE;
            }
        }
        return support;
    }

}
