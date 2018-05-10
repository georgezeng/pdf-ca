package net.geozen.pdf;

public enum Sex {
	Male("男"), Female("女");

	private String text;

	private Sex(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static Sex textOf(String text) {
		for (Sex sex : Sex.values()) {
			if (sex.text.equals(text)) {
				return sex;
			}
		}
		return null;
	}
}
