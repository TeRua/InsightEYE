package www.insighteye.zz.am;

import org.jsoup.nodes.Document;

public class Launch {
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		String response;
//		NaverAPIManger napi = new NaverAPIManger();
//		 response = napi.search("\"AJ 렌터카\"");
//		 // \" 표시를 통해 형태소 분할되어 검색되지 않게 지정해야함!
//		 while (response != null) {
//		 response = napi.search();
//		 }
		
		 Document doc;
		 NaverSearchManager nsm = new NaverSearchManager("2005.01.01", "2017.01.31", 300);
		 doc = nsm.search("\"동부발전\"");
		 while(doc != null) {
		 doc = nsm.search();
		 }
		 
//		 Document doc;
//		 DaumSearchManager dsm = new DaumSearchManager("20050101", "20170131", 300);
//		 doc = dsm.search("\"동부발전\"");
//		 while(doc != null) {
//		 doc = dsm.search();
//		 }
		long end = System.currentTimeMillis();
		System.out.println("실행시간 : " + (end - start)/1000.0);

	}
}
