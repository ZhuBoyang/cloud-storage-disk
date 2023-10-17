package online.yangcloud.web.controller;

import cn.hutool.core.io.FileUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * @author zhuboyang
 * @since 2023年10月13 09:46:23
 */
@RestController
@RequestMapping("/converter")
public class ConverterController {

    String docx = ".docx";
    String pptx = ".pptx";
    String xlsx = ".xlsx";
    String pdf = ".pdf";
    String name = "2023年1月收支计划1.4";
    String path = "/Users/boyang/Desktop/rain/";

    @Resource
    private DocumentConverter converter;

    @GetMapping("/word")
    public void converter() throws OfficeException {
        DocumentFormat doc = DefaultDocumentFormatRegistry.DOC;
        System.out.println(doc);
        System.out.println(doc.getName());
        System.out.println(doc.getExtension());
//        DocumentFormat format = DefaultDocumentFormatRegistry.PDF;
//        converter.convert(FileUtil.file(path + name + xlsx)).to(FileUtil.file(path + name + pdf)).as(format).execute();
    }

    @GetMapping("/split")
    public void split() {
        try (PDDocument document = PDDocument.load(FileUtil.file(path + name + pdf))) {
            boolean encrypted = document.isEncrypted();
            int count = document.getNumberOfPages();
            System.out.println(count);

            PDFRenderer renderer = new PDFRenderer(document);

            for (int i = 0; i < document.getPages().getCount(); i++) {
                BufferedImage image = renderer.renderImage(i);
//                BufferedImage image = document.saveAsImage(i, PdfImageType.Bitmap, 500, 500);
                ImageIO.write(image, "PNG", FileUtil.file(path + name + i + ".png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
