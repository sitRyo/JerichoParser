package controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

public class SaveCSS {
	
	Source source;
	Parser parser;
	OutputDocument document;

	CheckFile cf;
	
	public SaveCSS(Source source, Parser parser, OutputDocument document) {
		this.source = source;
		this.parser = parser;
		this.document = document;
		cf = new CheckFile("css", parser.getTargetURL());
	}
	
	public OutputDocument go() {
		List<StartTag> linkStartTags = source.getAllStartTags(HTMLElementName.LINK);
		String local = parser.getLocal();
		String hostURL = parser.getHostURL();
		
		for (StartTag startTag : linkStartTags) {
			Attributes attributes = startTag.getAttributes();
			String rel = attributes.getValue("rel");
			
			// alternateなどをここで弾く
			if (!"stylesheet".equalsIgnoreCase(rel)) {
				continue;
			}
			
			String href = attributes.getValue("href");
			
			// 受け取ったhrefをローカルに保存できるように変更する
			String path = local;
			try {
				path += cf.changeFileName(href);
			} catch (MalformedURLException e) {
				System.out.println("このファイルは存在しないかアクセスが拒否されました。");
				continue;
			}
			
			// checkURLから正しいURLの返り値を受け取る
			href = cf.checkURL(href, hostURL);
			
			document = cf.replaceAttributes(attributes, path, "href", document);
			
			try {
				System.out.println(href);
				IOProcesser io = new IOProcesser(new URL(href), path, true, local);
				Thread thread = new Thread(io);
				thread.start();
			} catch (MalformedURLException e) {
				System.out.println("エラー発生");
			}
			
		}
		return document;
	}
}
