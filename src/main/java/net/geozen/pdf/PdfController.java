package net.geozen.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
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
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import net.geozen.pdf.enums.Grade;
import net.geozen.pdf.enums.Nation;
import net.geozen.pdf.enums.Nationality;
import net.geozen.pdf.enums.Sex;

@Controller
public class PdfController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${pdf.filepath}")
	private String filePath;
	private int count;

	@RequestMapping(value = "/generate")
	public String generatePDF(@RequestParam("file") MultipartFile file, @RequestParam("type") String type, HttpServletResponse response)
			throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
		HSSFSheet sheet = wb.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();
		List<CARequest> list = new ArrayList<CARequest>();
		for (int r = 1; r < rows; r++) {
			HSSFRow row = sheet.getRow(r);
			if (row == null) {
				continue;
			}
			CARequest data = new CARequest();
			data.setName(row.getCell(0).getStringCellValue());
			if (!StringUtils.isEmpty(data.getName())) {
				data.setPinyin(row.getCell(1).getStringCellValue());
				data.setSex(row.getCell(3).getStringCellValue());
				data.setBirthday(row.getCell(4).getStringCellValue());
				data.setGrade((int) row.getCell(6).getNumericCellValue());
				data.setNation(row.getCell(7).getStringCellValue());
				data.setNationality(row.getCell(9).getStringCellValue());
				list.add(data);
			}
		}
		wb.close();

		PDDocument doc = new PDDocument();
		PDImageXObject pdImage = null;
		if (type.equals("preview")) {
			InputStream in = getClass().getResourceAsStream("/ca.jpeg");
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int c = -1;
			while ((c = in.read()) != -1) {
				os.write(c);
			}
			in.close();
			pdImage = PDImageXObject.createFromByteArray(doc, os.toByteArray(), "ca");
		}
		float widthScale = new BigDecimal("0.36").floatValue();
		float heightScale = new BigDecimal("0.37").floatValue();
		float fontSize = new BigDecimal("12").floatValue();
		PDFont font = PDType0Font.load(doc, getClass().getResourceAsStream("/ms_song.ttf"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		DecimalFormat df = new DecimalFormat("0000");
		for (CARequest data : list) {
			PDPage page = new PDPage(PDRectangle.A4);
			doc.addPage(page);
			PDPageContentStream contents = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);
			if (pdImage != null) {
				contents.drawImage(pdImage, 0, 0, pdImage.getWidth() * widthScale, pdImage.getHeight() * heightScale);
			}
			writeFont(contents, data.getName(), font, fontSize, 385, 527);
			writeFont(contents, data.getPinyin(), font, fontSize, 385, 501);
			Sex sex = Sex.textOf(data.getSex());
			if (sex != null) {
				writeFont(contents, sex.getText(), font, fontSize, 385, 476);
				writeFont(contents, sex.name(), font, fontSize, 385, 453);
			}
			Nationality gj = Nationality.textOf(data.getNationality());
			writeFont(contents, gj.getText(), font, fontSize, 385, 430);
			writeFont(contents, gj.name(), font, fontSize, 430, 403);
			Nation nation = Nation.textOf(data.getNation());
			if (nation != null) {
				writeFont(contents, nation.getText(), font, fontSize, 385, 380);
				writeFont(contents, nation.name(), font, fontSize, 385, 357);
			}
			writeFont(contents, data.getBirthday(), font, fontSize, 385, 333);
			writeFont(contents, data.getBirthday(), font, fontSize, 430, 312);
			writeFont(contents, "国际标准舞", font, fontSize, 385, 284);
			writeFont(contents, "Ballroom Dance/DanceSport", font, fontSize, 410, 261);
			Grade grade = Grade.levelOf(Integer.valueOf(data.getGrade()));
			if (grade != null) {
				writeFont(contents, grade.getText(), font, fontSize, 385, 238);
				writeFont(contents, grade.getLevel() + "", font, fontSize, 410, 215);
			}
			String code = null;
			synchronized (this) {
				code = "077" + sdf2.format(new Date()) + df.format(count++);
			}
			writeFont(contents, code, font, fontSize, 160, 331);
			writeFont(contents, code, font, fontSize, 135, 310);
			writeFont(contents, "1-12", font, fontSize, 160, 293);
			writeFont(contents, "1-12", font, fontSize, 240, 273);
			String date = sdf.format(new Date());
			writeFont(contents, date, font, fontSize, 160, 253);
			writeFont(contents, date, font, fontSize, 200, 232);
			contents.close();
		}
		String filename = sdf.format(new Date()) + ".pdf";
		doc.save(this.filePath + filename);

		// response.addHeader("Content-Disposition", "attachment;filename=" + filename);
		InputStream in = new FileInputStream(this.filePath + filename);
		OutputStream out = response.getOutputStream();
		int c = -1;
		while ((c = in.read()) != -1) {
			out.write(c);
		}
		in.close();
		out.flush();
		out.close();
		return null;
	}

	@PostConstruct
	public void init() {
		new File(filePath).mkdirs();
	}

	@Scheduled(cron = "0 0 0 * * ?")
	public void clearCount() {
		count = 0;
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
