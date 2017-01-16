package www.insighteye.zz.am;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class DaumSearchManager {
	private String url = "http://search.daum.net/search?w=news&cluster=n&req=tab&period=u&DA=STC"; // 검색
	// URL
	final private SimpleDateFormat sd = new SimpleDateFormat("yyyymmdd");
	// 날짜 포맷, 수정 금지
	final private int NUM = 50; // 기사 표시 개수
	final private int SORT = 3; // 정렬방법, 1이 최신, 3이 정확도
	private boolean done = false;

	private String tempUrl;
	private HttpGet http;
	private HttpClient httpClient;
	private HttpResponse response;
	private HttpEntity entity;

	private BufferedReader br;
	private StringBuffer sb;

	private String query; // 검색어
	private String keyword; // 저장시 키워드
	private Date startDate; // 최초 검색일
	private Date endDate; // 최종 검색일
	private int term; // 검색 날짜 텀

	private Date prevSdate;
	// 이전 검색 시작일, 뒤에 시간값 000000 필요
	private Date prevEdate;
	// 이전 검색 종료일, 뒤에 시간값 235959 필요

	private Date curSdate; // 현재 검색 시작일
	private Date curEdate; // 현재 검색 종료일

	private String stime = "000000";
	private String etime = "235959";

	private int page; // 현재 페이지 p
	private int total; // 검색 건수
	private int maxPage; // 최대 페이지

	public DaumSearchManager(String sdate, String edate, int _term) {
		try {
			startDate = sd.parse(sdate); // 최초일
			endDate = sd.parse(edate); // 최종일
			term = _term; // 검색 텀 지정
			url = url + "&n=" + NUM; // 페이지당 표시개수
		} catch (ParseException e) {
			System.out.println("Informal date format error!!");
		}
	}

	@SuppressWarnings("finally")
	public Document search(String _keyword) {
		// 다음 페이지 검색

		keyword = _keyword.replaceAll("\"", "");
		// 저장용 키워드
		String result = "";
		Document doc = null;

		try {
			query = URLEncoder.encode(_keyword, "UTF-8");
			tempUrl = url + "&q=" + query;

			curSdate = startDate;
			curEdate = addTerm(curSdate);
			// curEdate = endDate;
			if (curEdate.after(endDate) && !done) {
				curEdate = endDate;
				done = true;
			}
			tempUrl = tempUrl + "&sd=" + sd.format(curSdate) + stime + "&ed=" + sd.format(curEdate) + etime;

			System.out.println(tempUrl);

			// Http 요청
			http = new HttpGet(tempUrl);
			httpClient = HttpClientBuilder.create().build();
			response = httpClient.execute(http);
			entity = response.getEntity();
			ContentType content = ContentType.getOrDefault(entity);
			Charset charset = content.getCharset();
			br = new BufferedReader(new InputStreamReader(entity.getContent(), charset));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
			doc = Jsoup.parse(result);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			total = getTotal(doc);
			setPage();
			// 최대 페이지 수
			page = 1;
			prevSdate = curSdate; // 필요없음
			prevEdate = curEdate;
			saveAsHTML(result);
			return doc;
		}
	}

	private int getTotal(Document doc) {
		try {
			Elements elem = doc.select("span#resultCntArea");
			Pattern pat = Pattern.compile("[0-9]*건");
			Matcher mat = pat.matcher(elem.text());
			mat.find();

			total = Integer.parseInt(mat.group().replaceAll("건", ""));
			// 전체 검색 결과 개수
			System.out.println(total);
		} catch (IllegalStateException e) {
			total = 0;
		}
		return total;
	}

	@SuppressWarnings("finally")
	public Document search() {
		String result = "";
		Document doc = null;
		boolean renew = false;
		String tempUrl2;
		System.out.println("done = " + done);
		// 저장용 키워드
		try {
			// 마지막 페이지 도달 여부 확인
			if (page >= maxPage) {
				System.out.println("Page done");
				if (!done) {
					changeDate();
					renew = true;
				} else if (done){
					doc = null;
					return null;
				}
				// total 갱신
			} else {
				// 마지막 페이지가 아닌 경우, 페이지 증가 후 검색
				page++;
			}
			if (renew)
				tempUrl2 = tempUrl;
			else
				tempUrl2 = tempUrl + "&p=" + page; // 페이지
			// Http 요청
			System.out.println(tempUrl2);
			http = new HttpGet(tempUrl2);
			httpClient = HttpClientBuilder.create().build();
			response = httpClient.execute(http);
			entity = response.getEntity();
			ContentType content = ContentType.getOrDefault(entity);
			Charset charset = content.getCharset();
			br = new BufferedReader(new InputStreamReader(entity.getContent(), charset));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
			doc = Jsoup.parse(result);
		} catch (

		UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			if (renew) {
				total = getTotal(doc);
				setPage();
				page = 1;
			}
			saveAsHTML(result);

			return doc;
		}
	}

	private void changeDate() {
		prevSdate = curSdate;
		prevEdate = curEdate;

		curSdate = addTerm(prevEdate, 1);
		curEdate = addTerm(curSdate);

		if (curEdate.after(endDate)) {
			curEdate = endDate;
			done = true;
		}
		tempUrl = url + "&q=" + query;
		tempUrl = tempUrl + "&sd=" + sd.format(curSdate) + stime + "&ed=" + sd.format(curEdate) + etime;
	}

	private void setPage() {
		System.out.println(total);
		maxPage = total / NUM;

		if (total % NUM != 0) {
			maxPage++;
		}
		if (maxPage > 80) {
			maxPage = 80;
		}
		System.out.println("maxPage = " + maxPage);
	}

	private Date addTerm(Date _date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(_date);
		cal.add(Calendar.DAY_OF_YEAR, term);

		return cal.getTime();
	}

	private Date addTerm(Date _date, int _term) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(_date);
		cal.add(Calendar.DAY_OF_YEAR, _term);
		return cal.getTime();
	}

	private Boolean saveAsHTML(String contents) {
		boolean success = false;
		String fileName;
		try {

			fileName = "DAUM-" + keyword + "-" + sd.format(curSdate) + "-" + sd.format(curEdate) + "-" + page + "-"
					+ total + ".html";
			FileOutputStream fos = new FileOutputStream(fileName);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			BufferedWriter out = new BufferedWriter(osw);
			out.write(contents);
			out.close();
			success = true;
			System.out.println("[SAVE] :: " + fileName + " saved.");
			return success;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	private Boolean parseIntoXML(Document doc) {
		boolean success = false;
		
		
		return success;
	}
}

// getTotal 등의 메소드에 로깅용 출력 추가 [getTotal()] : ~
// finally는 return 전에 수행됨