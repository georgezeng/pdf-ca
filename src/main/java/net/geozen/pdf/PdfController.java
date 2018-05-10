package net.geozen.pdf;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PdfController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${pdf.filepath}")
	private String filePath;

	@RequestMapping(value = "/generate")
	public String generatePDF(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
		HSSFSheet sheet = wb.getSheetAt(0);
		String[] fields = { "name", "pinyin", "identity", "sex", "birthday", "specialty", "level", "nation", "score", "nationality", "unit", "exam",
				"address", "zipcode", "remark" };
		int rows = sheet.getPhysicalNumberOfRows();
		List<CARequest> list = new ArrayList<CARequest>();
		for (int r = 11; r < rows; r++) {
			HSSFRow row = sheet.getRow(r);
			if (row == null) {
				continue;
			}
			CARequest data = new CARequest();
			int cells = row.getLastCellNum();
			for (int c = 0; c < cells; c++) {
				HSSFCell cell = row.getCell(c);
				if (cell != null) {
					Method m = ReflectionUtils.findMethod(data.getClass(), "set" + StringUtils.capitalize(fields[c]), String.class);
					m.invoke(data, cell.getStringCellValue());
				}
			}
			list.add(data);
		}
		wb.close();

		PDDocument doc = new PDDocument();
		InputStream in = getClass().getResourceAsStream("/ca.jpeg");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int c = -1;
		while ((c = in.read()) != -1) {
			out.write(c);
		}
		in.close();
		PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, out.toByteArray(), "ca");
		float widthScale = new BigDecimal("0.36").floatValue();
		float heightScale = new BigDecimal("0.37").floatValue();
		float fontSize = new BigDecimal("20").floatValue();
		PDFont font = PDType0Font.load(doc, getClass().getResourceAsStream("/ms_song.ttf"));
		for (CARequest data : list) {
			PDPage page = new PDPage(PDRectangle.A4);
			doc.addPage(page);
			PDPageContentStream contents = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);
			contents.drawImage(pdImage, 0, 0, pdImage.getWidth() * widthScale, pdImage.getHeight() * heightScale);
			writeFont(contents, data.getName(), font, fontSize, 300, 300);
			writeFont(contents, data.getPinyin(), font, fontSize, 300, 300);
			writeFont(contents, data.getSex(), font, fontSize, 300, 300);
			writeFont(contents, data.getSpecialty(), font, fontSize, 300, 300);
			writeFont(contents, data.getLevel(), font, fontSize, 300, 300);
			writeFont(contents, data.getNation(), font, fontSize, 300, 300);
			writeFont(contents, data.getScore(), font, fontSize, 300, 300);
			writeFont(contents, data.getUnit(), font, fontSize, 300, 300);
			writeFont(contents, data.getExam(), font, fontSize, 300, 300);
			contents.close();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		doc.save(filePath + sdf.format(new Date()) + ".pdf");
		return null;
	}

	private void writeFont(PDPageContentStream contents, String text, PDFont font, float fontSize, int x, int y) throws Exception {
		if (!StringUtils.isEmpty(text)) {
			contents.beginText();
			contents.setTextMatrix(Matrix.getTranslateInstance(x, y));
			contents.setFont(font, fontSize);
			contents.showText(text);
			contents.endText();
		}
	}

	@ExceptionHandler(Exception.class)
	public void handleError(HttpServletRequest req, HttpServletResponse res, Exception ex) throws Exception {
		logger.error(ex.getMessage(), ex);
		res.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	}

}
