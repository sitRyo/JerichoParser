package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

public class SaveImageInHTML {

	Source source;
	Parser parser;
	OutputDocument document;
	
	CheckFile cf;
	
	public SaveImageInHTML(Source source, Parser parser, OutputDocument document) {
		this.source = source;
		this.parser = parser;
		this.document = document;
	}
	
	public OutputDocument go() {
		List<StartTag> imgStartTags = source.getAllStartTags(HTMLElementName.IMG);
		String local = parser.getLocal();
		String hostURL = parser.getHostURL();
		
		for(StartTag startTag : imgStartTags) {
			Attributes attributes = startTag.getAttributes();
			String src = attributes.getValue("src");
			
			String imageTypeArray[] = src.split(Pattern.quote("."));
			String type = imageTypeArray[imageTypeArray.length - 1];

			CheckFile cf = new CheckFile(type);
			String path = local;
			try {
				path += cf.changeFileName(src);
				src = cf.checkURL(src, hostURL);
				document = cf.replaceAttributes(attributes, path, "src", document);
				URL imageURL = new URL(src);
				IOProcesser io = new IOProcesser();
				io.saveImageFile(imageURL, local+imageURL.getHost() + imageURL.getFile());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}


		}
		
		return document;
	}
}