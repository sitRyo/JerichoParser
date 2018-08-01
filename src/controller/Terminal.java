package controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Terminal {

	private int depth; // 検索する深さ
	private String targetURL; // 検索するURLの絶対パス
	private String local; // 保存するローカルディレクトリ
	private static LinkedList<URL> queue = new LinkedList<>(); // LinkedListを用いたキューの実装
	private static Map<Integer, URL> hashMap = new HashMap<>(); // 既知のWebサイトをマッピング
	private static int hashKey = 0; // HashMapのkey

	public Terminal(String targetURL, String local, int depth) {
		this.targetURL = targetURL;
		this.depth = depth;
		this.local = local;

		try {
			queue.offer(new URL(targetURL));
		} catch (MalformedURLException e) {
			System.out.println("このURLは無効です。正しいURLを入力しなおしてください。");
			System.exit(1);
		}

		System.out.println("アプリケーションを起動します。");
	}
	
	// enqueue
	public void setLink(ArrayList<URL> links) {
		synchronized(queue){
			for(URL href : links) {
				// 既知のページかチェック。初めての場合のみキューにURLを格納
				if(!(hashMap.containsValue(href))) {
					hashMap.put(hashKey, href);
					System.out.println(href.toString());
					hashKey += 1; // hashKeyのインクリメント
					queue.offer(href); // enqueue
				}
			}
		}
	}
	
	// dequeue
	private URL getLink() {
		synchronized (queue) {
			return queue.poll(); // dequeue, もしリンクが空の時はnullを返す。
		}
	}
	
	public void go() throws IOException {
		for(int i=0; i<depth; i++) {
			ExecutorService pool = Executors.newCachedThreadPool();
			LinkedList<URL> queueCopy = new LinkedList<>(queue);
			queue.clear();
			
			URL newURL;
			while((newURL = queueCopy.poll()) != null){
				Future<ArrayList<URL>> future = pool.submit(new Parser(newURL, local));
				ArrayList<URL> array = new ArrayList<>();
				try {
					array = future.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				setLink(array);
			}
			System.out.println(pool.isShutdown());
			pool.shutdown();
			while(!pool.isShutdown()) {}
		}
	}
}
