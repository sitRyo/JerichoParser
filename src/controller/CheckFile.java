package controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.OutputDocument;

public class CheckFile {

	String type;
	URL target;
	
	public CheckFile(String type) {
		this.type = type;
	}
	
	public CheckFile(String type, URL target) {
		this.type = type;
		this.target = target;
	}

	// URLをチェックし保存する
	public String checkURL(String href, String hostURL) {
		if (href.startsWith("http")) {
			return href;
		} else {
			return hostURL + href;
		}
	}

	// 保存するディレクトリ名を決定する
	public String changeFileName(String name) throws MalformedURLException {
		if (name.startsWith("http")) {
			URL temp = new URL(name);
			// 例:http://www.apple.com/v/com/index.html の場合-> fileName =
			// www.apple.com/v/com/index.html
			String fileName = temp.getHost() + temp.getFile();
			fileName = checkFileName(fileName);
			return fileName;
		} else {
			return checkFileName(name);
		}
	}

	// ファイル名がWindows上に保存できるかを判断し、適宜修正する
	public String checkFileName(String name) {
		// ファイル名の先頭にサイトのホスト部分もくっつける。
		name = target.getHost() + "/" + name;
		name = name.replaceAll("[\\ | : | * | ? | \" | < | > | | ]", "_");
		if (!(name.endsWith("." + this.type))) {
			// もしURLの終端が"/"で終わっていた場合はindex.htmlという名前を付ける。
			if (name.endsWith("/")) {
				name = name + "index";
			}
			name = name + "." + this.type;
		}
		// System.out.println(name);
		return name;
	}

	// 保存するHTMLの属性書き換え
	public OutputDocument replaceAttributes(Attributes attributes, String alt, String type, OutputDocument doc) {
		Map<String, String> attributesMap = doc.replace(attributes, true);
		attributesMap.put(type, alt);
		return doc;
	}

}
