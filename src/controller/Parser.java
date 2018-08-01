package controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

public class Parser implements Callable<ArrayList<URL>>{

	// 対象のURLと保存するディレクトリの絶対パス
	URL targetURL;
	String local;

	// arraylist
	private ArrayList<URL> array = new ArrayList<>();

	public Parser(URL targetURL, String local) {
		this.targetURL = targetURL;
		this.local = local;
	}
	
	// 保存するディレクトリの絶対パスを返す
	public String getLocal() {
		return local;
	}

	// ホストURLの文字列を返す
	public String getHostURL() {
		String hostURL = targetURL.toString().replace(targetURL.getFile(), "/");
		return hostURL;
	}

	// URLを返す
	public URL getTargetURL() {
		return targetURL;
	}

	// URLのセッター
	public void setHrefLinks(ArrayList<URL> links) {
		this.array = links;
	}
	
	// Parseの実行
	public ArrayList<URL> call() {
		Source source = null;
		try {
			source = new Source(targetURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputDocument document = new OutputDocument(source);

		SaveLinks sl = new SaveLinks(source, this, document);
		document = sl.go();

		SaveCSS sc = new SaveCSS(source, this, document);
		document = sc.go();

		SaveJavaScript sjs = new SaveJavaScript(source, this, document);
		document = sjs.go();
		
		CheckFile cf = new CheckFile("html",targetURL);

		String path = local;
		try {
			path += cf.changeFileName(targetURL.getFile());
		} catch (MalformedURLException e) {
			System.out.println("URLが不正です。");
		}

		IOProcesser io = new IOProcesser(document, path, false);
		Thread thread = new Thread(io);
		thread.start();
		
		return array;
	}
}
