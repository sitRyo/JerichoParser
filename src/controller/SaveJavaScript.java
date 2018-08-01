package controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

public class SaveJavaScript {
	
	Source source;
	Parser parser;
	OutputDocument document;

	CheckFile cf;
	
	public SaveJavaScript (Source source, Parser parser, OutputDocument document) {
		this.source = source;
		this.parser = parser;
		this.document = document;
		cf = new CheckFile("js", parser.getTargetURL());
	}
	
	public OutputDocument go() {
		List<StartTag> scriptStartTags = source.getAllStartTags(HTMLElementName.SCRIPT);
		String local = parser.getLocal();
		String hostURL = parser.getHostURL();
		
		for (StartTag startTag : scriptStartTags) {
			Attributes attributes = startTag.getAttributes();
			String type = attributes.getValue("type");

			// javascript以外を排除
			if (!"text/javascript".equalsIgnoreCase(type)) {
				continue;
			}
			
			// src(=jsの絶対パスorURL)がnullだったらスキップ
			String src = attributes.getValue("src");
			if (src == null) {
				continue;
			}
			
			// 受け取ったsrcをローカルに保存できるように変更する
			String path = local;
			try {
				path += cf.changeFileName(src);
			} catch (MalformedURLException e) {
				System.out.println("このファイルは存在しないかアクセスが拒否されました。");
				continue;
			}
			
			// checkURLから正しいURLの返り値を受け取る
			src = cf.checkURL(src, hostURL);

			document = cf.replaceAttributes(attributes, path, "src", document);
			
			try {
				IOProcesser io = new IOProcesser(new URL(src), path, true);
				Thread thread = new Thread(io);
				thread.start();
			} catch (MalformedURLException e) {
				System.out.println("エラー発生");
			}
			
		}
		return document;
	}
}
