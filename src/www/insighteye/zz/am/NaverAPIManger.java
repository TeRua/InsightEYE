package www.insighteye.zz.am;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.opencsv.CSVWriter;

import www.insighteye.zz.am.article.Article;

public class NaverAPIManger {
	private SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");

	private String clientID = "DdhFONGBHq8r_xCqRxW1"; // API Key ID
	private String clientSecret = "fqpyz0q94H"; // API Secret
	private String SORTING = "date"; // simor date
	private int curPage = 1; // 현재 페이지
	private int maxPage; // 최대 페이지
	private int total; // 최대 검색 결과

	private String keyword;
	private String query; // 검색문 쿼리, URLEncoder로 UTF-8 인코딩 필요
	private StringBuffer response; // 결과 저장 및 반환용 변수

	ArrayList<Article> list = new ArrayList<Article>();

	@SuppressWarnings("finally")
	public String search(String _keyword) {
		// Initial Searching for Keyword Input
		Document doc; // xml 파싱용 document
		try {
			keyword = _keyword.replaceAll("\"", "");
			query = URLEncoder.encode(_keyword, "UTF-8");
			String apiURL = "https://openapi.naver.com/v1/search/news.xml?query="; // 뉴스검색
			// API URL [XML]
			// String apiURL =
			// "https://openapi.naver.com/v1/search/news.json?query=";
			// API URL [JSON]
			apiURL = apiURL + query;
			apiURL = apiURL + "&display=100"; // 100개씩 보여주기
			apiURL = apiURL + "&sort=" + SORTING; // 정렬옵션
			System.out.println(apiURL);
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			// HTTP커넥션생성
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
			doc = makeDocument(response);
			setMax(getTotal(doc));
			parseArticle(doc);
			// saveAsXML(response.toString());
			curPage = curPage + 100;
			return response.toString(); // response 반환
		}
	}

	// 멀티스레딩 시, curPage에 락 걸어야함, 아래의 search() 메소드를 멀티스레딩으로 동작
	@SuppressWarnings("finally")
	public String search() {
		Document doc = null; // xml 파싱용 document
		System.out.println(curPage);
		if (curPage > maxPage) {
			saveAsCSV();
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
				apiURL = apiURL + "&sort=" + SORTING; // 정렬 옵션
				System.out.println(apiURL);
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
				System.out.println("[SEARCH]" + "NAVER search " + curPage + "/" + maxPage + " done.");
				doc = makeDocument(response);
				parseArticle(doc);
				// saveAsXML(response.toString());
				curPage = curPage + 100;
				return response.toString(); // response 반환
			}
		}
	}

	private Document makeDocument(StringBuffer response) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		InputStream is;
		try {
			is = new ByteArrayInputStream(response.toString().getBytes("UTF-8"));
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	public int getTotal(Document doc) {
		// get Total from search result
		try {
			NodeList nList = doc.getElementsByTagName("total");
			if (nList.getLength() != 0) {
				Node n = nList.item(0);
				System.out.println("Original Total Page is " + n.getTextContent());
				total = Integer.parseInt(n.getTextContent());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return total;
	}

	public int setMax(int _total) {
		maxPage = _total;
		if (maxPage > 1000) {
			maxPage = 1000;
		}
		return maxPage;
	}

	private void parseArticle(Document doc) {
//		System.out.println(doc.getElementsByTagName("channel").item(0).getTextContent());
		NodeList nList = doc.getElementsByTagName("item");
		Element elem;
		Article art;
		DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
		
		if (nList.getLength() != 0) {
			for (int i = 0; i < nList.getLength(); i++) {
				try {
					art = new Article();
					elem = (Element) nList.item(i);
					art.setTitle(elem.getElementsByTagName("title").item(0).getTextContent().toString());
					art.setDate(sd.format(
							df.parse(elem.getElementsByTagName("pubDate").item(0).getTextContent().toString())));
					art.setUrl(elem.getElementsByTagName("originallink").item(0).getTextContent().toString());
					art.setDescription(elem.getElementsByTagName("description").item(0).getTextContent().toString());
					art.setPublisher("null");
					list.add(art);
				} catch (DOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public Boolean saveAsXML(String contents) {
		boolean success = false;
		String fileName;
		try {

			fileName = "NAVER-" + keyword + "-" + curPage + "-" + maxPage + ".xml";
			FileOutputStream fos = new FileOutputStream(fileName);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			BufferedWriter out = new BufferedWriter(osw);
			out.write(contents);
			out.close();
			success = true;
			System.out.println("[SAVE] :: " + fileName + " saved.");
			return success;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}

	public void saveAsCSV() {
		String fileName = "NAVER-" + keyword + "-" + SORTING +".csv";
		FileOutputStream fos;
		OutputStreamWriter osw;
		CSVWriter csv = null;
		try {
			fos = new FileOutputStream(fileName, true);
			osw = new OutputStreamWriter(fos, "euc-kr");
			csv = new CSVWriter(osw, ',', '"');

			for (Article art : list) {
				csv.writeNext(new String[] { String.valueOf(art.getTitle()), String.valueOf(art.getDate()),
						String.valueOf(art.getPublisher()), String.valueOf(art.getDescription()),
						String.valueOf(art.getUrl()) });
			}
		} catch (

		FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				csv.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}