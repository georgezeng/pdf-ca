package net.geozen.pdf.enums;

public enum Grade {
	G1(1, "壹级"), 
	G2(2, "贰级"),
	G3(3, "叁级"),
	G4(4, "肆级"),
	G5(5, "伍级"),
	G6(6, "陆级"),
	G7(7, "柒级"),
	G8(8, "捌级"),
	G9(9, "玖级"),
	G10(10, "拾级"),
	G11(11, "拾壹级"),
	G12(12, "拾贰级");

	private final int level;
	private final String text;

	private Grade(int level, String text) {
		this.level = level;
		this.text = text;
	}

	public int getLevel() {
		return level;
	}

	public String getText() {
		return this.text;
	}

	public static Grade levelOf(int level) {
		for (Grade grade : Grade.values()) {
			if (grade.level == level) {
				return grade;
			}
		}
		return null;
	}
}
