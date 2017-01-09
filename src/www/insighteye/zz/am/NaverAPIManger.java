package www.insighteye.zz.am;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NaverAPIManger {
	private String clientID = "DdhFONGBHq8r_xCqRxW1"; // API Key ID
	private String clientSecret = "fqpyz0q94H"; // API Secret
	private String SORTING = "sim"; // or date
	private int curPage = 1; // 현재 페이지
	private int maxPage; // 최대 페이지
	private int total; // 최대 검색 결과

	private String query; // 검색문 쿼리, URLEncoder로 UTF-8 인코딩 필요
	private StringBuffer response; // 결과 저장 및 반환용 변수

	@SuppressWarnings("finally")
	public String search(String _keyword) {
		try {
			query = URLEncoder.encode(_keyword, "UTF-8");
			String apiURL = "https://openapi.naver.com/v1/search/news.xml?query="; // 뉴스검색
			// API URL [XML]
			// String apiURL =
			// "https://openapi.naver.com/v1/search/news.json?query=";
			// API URL [JSON]
			apiURL = apiURL + query;
			apiURL = apiURL + "&display=100"; // 100개씩 보여주기
			apiURL = apiURL + "&start=1000&sort="; // 검색 시작지점
			apiURL = apiURL + SORTING; // 정렬 옵션
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection(); // HTTP
																				// 커넥션
																				// 생성
			con.setRequestMethod("GET"); // GET 방식 호출
			con.setRequestProperty("X-Naver-Client-Id", clientID);
			// Header에 API Key 지정
			con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
			int responseCode = con.getResponseCode(); // Response
			BufferedReader br;
			if (responseCode == 200) { // 성공시 Code 200
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else { // 에러시, 분기 여부 확인 필요, API 키 초과시
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				// BufferReader로 부터 Line 읽어오고 이를 inputLine에 저장, null일 경우 반복 종료
				response.append(inputLine);
				// null이 아닐 경우 response에 추가
			}
			br.close();
			// System.out.println(response.toString()); //response 출력
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			setMax(getTotal(response.toString()));
			saveAsXML(response.toString());
			curPage = 2;
			return response.toString(); // response 반환
		}
	}

	public int getTotal(String xml) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc;
		InputStream is;
		try {
			is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("total");
			if (nList.getLength() != 0) {
				Node n = nList.item(0);
				System.out.println(n.getTextContent());
				total = Integer.parseInt(n.getTextContent());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return total;
	}

	public int setMax(int _total) {
		maxPage = _total / 10;
		if (_total % 10 != 0) {
			maxPage += 1;
		}

		if (maxPage > 1000) {
			maxPage = 1000;
		}
		return maxPage;
	}

	@SuppressWarnings("finally")
	public String search() {
		if (curPage > maxPage) {
			System.out.println("It has no more pages now.");
			return null;
		} else {
			try {
				// query = URLEncoder.encode(_keyword, "UTF-8");
				String apiURL = "https://openapi.naver.com/v1/search/news.xml?query="; // 뉴스검색
				// API URL [XML]
				// String apiURL =
				// "https://openapi.naver.com/v1/search/news.json?query=";
				// API URL [JSON]
				apiURL = apiURL + query;
				apiURL = apiURL + "&start=" + curPage; // 페이지 입력
				apiURL = apiURL + "&display=100"; // 100개씩 보여주기
				apiURL = apiURL + "&start=1000&sort="; // 검색 시작지점
				apiURL = apiURL + SORTING; // 정렬 옵션

				URL url = new URL(apiURL);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				// HTTP 커넥션 생성
				con.setRequestMethod("GET"); // GET 방식 호출
				con.setRequestProperty("X-Naver-Client-Id", clientID);
				// Header에 API Key 지정
				con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
				int responseCode = con.getResponseCode(); // Response
				BufferedReader br;
				if (responseCode == 200) { // 성공시 Code 200
					br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				} else { // 에러시, 분기 여부 확인 필요, API 키 초과시
					br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				}
				String inputLine;
				response = new StringBuffer();
				while ((inputLine = br.readLine()) != null) {
					// BufferReader로 부터 Line 읽어오고 이를 inputLine에 저장, null일 경우 반복
					// 종료
					response.append(inputLine);
					// null이 아닐 경우 response에 추가
				}
				br.close();
				// System.out.println(response.toString()); //response 출력
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				saveAsXML(response.toString());
				curPage++;
				return response.toString(); // response 반환
			}
		}
	}

	public Boolean saveAsXML(String contents) {
		boolean success = false;
		try {
			BufferedWriter out = new BufferedWriter(
					new FileWriter("NAVER-" + query + "-" + curPage + "-" + maxPage + ".xml"));
			out.write(contents);
			out.close();
			success = true;
			return success;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}

}