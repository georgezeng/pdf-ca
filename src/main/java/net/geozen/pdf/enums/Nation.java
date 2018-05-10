package net.geozen.pdf.enums;

public enum Nation {
	Han(1, "汉族"), 
	Zhuang(2, "壮族"), 
	Man(3, "满族"), 
	Hui(4, "回族"), 
	Miao(5, "苗族"), 
	Uygur(6, "维吾尔族"), 
	Yi(7, "彝族"), 
	Tujia(8, "土家族"), 
	Mongolian(9, "蒙古族"), 
	Tibetan(10, "藏族"), 
	Buyi(11, "布依族"), 
	Dong(12, "侗族"), 
	Yao(13, "瑶族"), 
	Korean(14, "朝鲜族"), 
	Bai(15, "白族"), 
	Hani(16, "哈尼族"), 
	Li(17, "黎族"), 
	Kazak(18, "哈萨克族"), 
	Dai(19, "傣族"), 
	She(20, "畲族"), 
	Lisu(21, "僳僳族"), 
	Gelao(22, "仡佬族"), 
	Lahu(23, "拉祜族"), 
	Dongxiang(24, "东乡族"), 
	Va(25, "佤族"), 
	Sui(26, "水族"), 
	Naxi(27, "纳西族"), 
	Qiang(28, "羌族"), 
	Tu(29, "土族"), 
	Xibe(30, "锡伯族"), 
	Mulao(31, "仫佬族"), 
	Kirgiz(32, "柯尔克孜族"), 
	Daur(33, "达斡尔族"), 
	Jingpo(34, "景颇族"), 
	Salar(35, "撒拉族"), 
	Blang(36, "布朗族"), 
	Maonan(37, "毛南族"), 
	Tajik(38, "塔吉克族"), 
	Pumi(39, "普米族"), 
	Achang(40, "阿昌族"), 
	Nu(41, "怒族"), 
	Ewenki(42, "鄂温克族"), 
	Gin(43, "京族"), 
	Jino(44, "基诺族"), 
	Deang(45, "德昂族"), 
	Uzbek(46, "乌孜别克族"), 
	Russians(47, "俄罗斯族"), 
	Yugur(48, "裕固族"), 
	Bonan(49, "保安族"), 
	Monba(50, "门巴族"), 
	Oroqen(51, "鄂伦春族"), 
	Derung(52, "独龙族"), 
	Tatar(53, "塔塔尔族"), 
	Hezhen(54, "赫哲族"), 
	Lhoba(55, "珞巴族"), 
	Gaoshan(56, "高山族"),
	Oversea(57, "海外");

	// 枚举值
	private final int key;

	// 枚举描述
	private final String text;

	private Nation(int key, String text) {
		this.key = key;
		this.text = text;
	}

	public int getKey() {
		return key;
	}

	public String getText() {
		return text;
	}
	
	public static Nation textOf(String text) {
		for (Nation nation : Nation.values()) {
			if (nation.text.equals(text)) {
				return nation;
			}
		}
		return Oversea;
	}

}