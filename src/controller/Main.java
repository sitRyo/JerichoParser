package controller;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		// コピーしてくる対象のURLと保存するローカルの絶対パスを決定する
		String targetURL;
		
		targetURL = "https://www.apple.com/";
		String local;
		local = "D:";
		
		// 検索する深さの指定
		int depth = 2;
		
		Terminal terminal = new Terminal(targetURL, local, depth);
		
		// スタート!
		try {
			terminal.go();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
