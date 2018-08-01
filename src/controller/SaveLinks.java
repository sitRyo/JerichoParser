package controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

public class SaveLinks {
	
	Source source;
	Parser parser;
	OutputDocument document;
	
	CheckFile cf;
	
	ArrayList<URL> array = new ArrayList<>();

	public SaveLinks(Source source, Parser parser, OutputDocument document) {
		this.source = source;
		this.parser = parser;
		this.document = document;
		cf = new CheckFile("html", parser.getTargetURL());
	}
	
	public OutputDocument go() {
		List<StartTag> aStartTags = source.getAllStartTags(HTMLElementName.A);
		String local = parser.getLocal(); // ローカルディレクトリの絶対パス
		String hostURL = parser.getHostURL();
		
		for(StartTag startTag : aStartTags) {
			Attributes attributes = startTag.getAttributes();
			String href = attributes.getValue("href");
			
			// hrefが空の場合
			if(href == null) {
				continue;
			}
			
			// 相対パスをURLに変更
			String newURL = cf.checkURL(href, hostURL);
			// 受け取ったhrefをローカルに保存できるように変更する
			String path = local;
			try {
				path += cf.changeFileName(href);
			} catch (MalformedURLException e) {
				System.out.println("このファイルは存在しないかアクセスが拒否されました。");
				continue;
			}
			
			document = cf.replaceAttributes(attributes, path, "href", document);
			
			
			// リストにaddしていく
			try {
				array.add(new URL(newURL));
			} catch (MalformedURLException e) {
				System.out.println("このURLは存在しません。");
				continue;
			}
		}
		
		parser.setHrefLinks(array);
		return document;
	}
}
