package www.insighteye.zz.am;

public class Launch {
	public static void main(String[] args) {
		String response;

		NaverAPIManger napi = new NaverAPIManger();
		response = napi.search("\"삼성전자\""); //\" 표시를 통해 형태소 분할되어 검색되지 않게 지정해야함!
		
		while(!response.equals(null))
		{
			response = napi.search();
		}
	}
}
