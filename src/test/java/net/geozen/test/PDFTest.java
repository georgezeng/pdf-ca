package net.geozen.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.junit.Test;
import org.springframework.util.StringUtils;

import net.geozen.pdf.CARequest;
import net.geozen.pdf.enums.Grade;
import net.geozen.pdf.enums.Nation;
import net.geozen.pdf.enums.Nationality;
import net.geozen.pdf.enums.Sex;

public class PDFTest {
	@Test
	public void test() throws Exception {
		CARequest data = new CARequest();
		data.setName("张三");
		data.setPinyin("Zhangsan");
		data.setSex("男");
		data.setNationality("中国");
		data.setNation("汉族");
		data.setBirthday("1983-12");
		data.setGrade("3");

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
		float fontSize = new BigDecimal("12").floatValue();
		PDFont font = PDType0Font.load(doc, getClass().getResourceAsStream("/ms_song.ttf"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		DecimalFormat df = new DecimalFormat("0000");
		PDPage page = new PDPage(PDRectangle.A4);
		doc.addPage(page);
		PDPageContentStream contents = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);
		contents.drawImage(pdImage, 0, 0, pdImage.getWidth() * widthScale, pdImage.getHeight() * heightScale);
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
		String code = "077" + sdf2.format(new Date()) + df.format(1);
		writeFont(contents, code, font, fontSize, 160, 331);
		writeFont(contents, code, font, fontSize, 135, 310);
		writeFont(contents, "1-12", font, fontSize, 160, 293);
		writeFont(contents, "1-12", font, fontSize, 240, 273);
		String date = sdf.format(new Date());
		writeFont(contents, date, font, fontSize, 160, 253);
		writeFont(contents, date, font, fontSize, 200, 232);
		contents.close();
		doc.save("test.pdf");
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
}
