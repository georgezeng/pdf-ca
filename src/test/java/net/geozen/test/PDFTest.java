package net.geozen.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;

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

public class PDFTest {
	@Test
	public void test() throws Exception {
		CARequest data = new CARequest();

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
		for(int i = 0 ; i < 2; i++) {
			PDPage page = new PDPage(PDRectangle.A4);
			doc.addPage(page);
			PDPageContentStream contents = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);
			contents.drawImage(pdImage, 0, 0, pdImage.getWidth() * widthScale, pdImage.getHeight() * heightScale);
			writeFont(contents, "中国", font, fontSize, 300, 300);
			contents.close();
		}
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
