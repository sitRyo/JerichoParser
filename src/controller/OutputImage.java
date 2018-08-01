package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class OutputImage implements Runnable{
	
	FileOutputStream fos;
	InputStream in;
	
	public OutputImage(FileOutputStream fos, InputStream in) {
		this.fos = fos;
		this.in = in;
	}
	
	private synchronized void write(FileOutputStream out, InputStream in) {
		try {
			byte[] bytes = new byte[512];
			int read = 0;
			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		write(fos, in);
	}

}
