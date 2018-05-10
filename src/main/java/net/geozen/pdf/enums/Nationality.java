package net.geozen.pdf.enums;

public enum Nationality {
	CN("中国"), Oversea("海外");

	private String text;

	private Nationality(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static Nationality textOf(String text) {
		if ("中国".equals(text)) {
			return CN;
		}
		return Oversea;
	}
}
