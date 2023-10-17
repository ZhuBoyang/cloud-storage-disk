package online.yangcloud.web.processor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.enumration.OfficeTypeEnum;
import online.yangcloud.common.tools.ExceptionTools;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * office word 处理器
 *
 * @author zhuboyang
 * @since 2023年10月14 13:37:55
 */
@Component
public class OfficeProcessor {

    @Resource
    private DocumentConverter converter;

    /**
     * 将 word 文档转换为 pdf
     *
     * @param source word 文档文件路径
     * @param target 目标文件路径
     */
    public void convertToPdf(String source, String target, OfficeTypeEnum officeType) throws OfficeException {
        String suffix = source.substring(source.lastIndexOf(StrUtil.DOT) + 1);

        // 检测是否是支持的文件类型
        boolean support = Boolean.TRUE;
        if (OfficeTypeEnum.WORD.equals(officeType)) {
            if (!DefaultDocumentFormatRegistry.DOC.getExtension().equals(suffix)
                    && !DefaultDocumentFormatRegistry.DOCX.getExtension().equals(suffix)) {
                support = Boolean.FALSE;
            }
        }
        if (OfficeTypeEnum.PPT.equals(officeType)) {
            if (!DefaultDocumentFormatRegistry.PPT.getExtension().equals(suffix)
                    && !DefaultDocumentFormatRegistry.PPTX.getExtension().equals(suffix)) {
                support = Boolean.FALSE;
            }
        }
        if (OfficeTypeEnum.EXCEL.equals(officeType)) {
            if (!DefaultDocumentFormatRegistry.XLS.getExtension().equals(suffix)
                    && !DefaultDocumentFormatRegistry.XLSX.getExtension().equals(suffix)
                    && !DefaultDocumentFormatRegistry.CSV.getExtension().equals(suffix)) {
                support = Boolean.FALSE;
            }
        }
        if (!support) {
            ExceptionTools.businessLogger("文档类型不支持");
        }

        // 将 word 文档转换为 pdf
        converter.convert(FileUtil.file(source))
                .to(FileUtil.file(target))
                .as(DefaultDocumentFormatRegistry.PDF)
                .execute();
    }

}
