package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.OutputDocument;

public class IOProcesser implements Runnable {

	URL source;
	String target;
	String local;
	boolean judge;
	int a;

	OutputDocument doc;
	String path;

	public IOProcesser(URL source, String target, boolean judge, String local) {
		this.target = target;
		this.source = source;
		this.judge = judge; // true
		this.local = local;
		this.a = 50;
	}
	
	public IOProcesser(URL source, String target, boolean judge) {
		this.target = target;
		this.source = source;
		this.judge = judge; // true
	}

	public IOProcesser(OutputDocument doc, String path, boolean judge) {
		this.doc = doc;
		this.path = path;
		this.judge = judge; // false
	}
	
	public IOProcesser() {
		
	}

	// WebPageをコピーし保存
	public void doWebPageCopy(URL source, String target) throws IOException {
		File file = new File(target);
		File newDirectory = file.getParentFile();
		if (!newDirectory.exists()) {
			newDirectory.mkdirs();
		}
		System.out.println(target);
		Path path = Paths.get(target);
		try (InputStream in = source.openStream()) {
			Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	// ローカルディレクトリ用に書き換えたHTMLを保存する
	public void saveHTML(OutputDocument document, String path) {
		Writer fileWriter;
		File file = new File(path);
		File newDirectory = file.getParentFile();
		if (!newDirectory.exists()) {
			newDirectory.mkdirs();
		}
		System.out.println(path);
		try {
			fileWriter = new FileWriter(path);
			document.writeTo(fileWriter);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createCSS(URL targetCSS) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(targetCSS.openStream()));
		String a = bf.readLine();
		String regax = "background-image:url\\([\"']([^\"]+)[\"']\\)";
		Pattern p = Pattern.compile(regax, Pattern.COMMENTS);
		Matcher m = p.matcher(a);

		// 元のURLのディレクトリの配列
		String[] URLArray = targetCSS.toString().split("/");
		while (m.find()) {

			// ここに最終的な画像のURLを格納する。
			String targetURL = "";

			// 画像pathのディレクトリの配列 ex) image/a/b/c/abc.gif = [image, a ,b, c, abc.gif]
			String[] imagePathArray = m.group(1).split("/");

			// Pathがhttpから始まる場合はそこがtargetURL
			if (imagePathArray[0].startsWith("http")) {
				targetURL = m.group(1);
			} else {

				int num = 0;
				for (int i = 0; i < imagePathArray.length - 1; i++) {
					String relPath = imagePathArray[i];
					if (relPath.equals("..")) {
						num++;
					}
				}

				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < URLArray.length - 1 - num; i++) {
					sb.append(URLArray[i] + "/");
				}

				for (int i = 0; i < imagePathArray.length; i++) {
					String relPath = imagePathArray[i];
					if (relPath.equals(".."))
						continue;
					if (i == imagePathArray.length - 1) {
						sb.append(relPath);
					} else {
						sb.append(relPath + "/");
					}
				}

				targetURL = sb.toString();
			}
			URL imageURL = new URL(targetURL);
			String localPath = local + imageURL.getHost() + imageURL.getFile();
			System.out.println("URL : " + imageURL + " path : " + localPath);
			saveImageFile(imageURL, localPath);
			a.replace(m.group(1), localPath);
		}
		File file = new File(target);
		File newDirectory = file.getParentFile();
		if (!newDirectory.exists()) {
			newDirectory.mkdirs();
		}
		FileWriter fw = new FileWriter(file);
		fw.write(a);
		fw.close();
	}

	public void saveImageFile(URL imageURL, String local) {
		File file = new File(local);
		File newDirectory = file.getParentFile();
		if (!newDirectory.exists()) {
			newDirectory.mkdirs();
		}
		try {
			FileOutputStream fos = new FileOutputStream(file, false);
			URLConnection con = imageURL.openConnection();
			InputStream in = con.getInputStream();
			OutputImage opi = new OutputImage(fos,in);
			Thread th = new Thread(opi);
			th.start();
			//InputStream in = new InputStream
		} catch (FileNotFoundException e) {
			System.out.println("ファイルが見つかりませんでした");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void run() {
		System.out.println("run");
		if (a == 50) {
			try {
				createCSS(source);
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (judge) {
			try {
				doWebPageCopy(this.source, this.target);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			saveHTML(doc, path);
		}
	}
}
