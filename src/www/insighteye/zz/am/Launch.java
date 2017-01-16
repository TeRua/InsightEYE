package www.insighteye.zz.am;

import org.jsoup.nodes.Document;

public class Launch {
	public static void main(String[] args) {
		String response;

//		NaverAPIManger napi = new NaverAPIManger();
//		 response = napi.search("\"카카오\"");
//		 // \" 표시를 통해 형태소 분할되어 검색되지 않게 지정해야함!
//		 while (response != null) {
//		 response = napi.search();
//		 }
		
		Document doc;
		DaumSearchManager dsm = new DaumSearchManager("20170101", "20170131", 3);
		doc = dsm.search("\"동부발전\"");
		while(doc != null) {
			doc = dsm.search();
		}

	}
}
