package nyd;

import java.io.IOException;

public class UTreeDataRunner {

	public static void main(String[] args) throws IOException {
		nyd2();
	}
	
	public static void nyd1() throws IOException {
		NYDModel nyd = new NYDModel();
		long startTime=System.currentTimeMillis();
		nyd.start();
		long endTime=System.currentTimeMillis();
		System.out.println((endTime-startTime)/1000.0+"s");
	}

	
	public static void nyd2() throws IOException {
		NYDModel2 nyd = new NYDModel2();
		long startTime=System.currentTimeMillis();
		nyd.start();
		long endTime=System.currentTimeMillis();
		System.out.println((endTime-startTime)/1000.0+"s");
	}
	

}
