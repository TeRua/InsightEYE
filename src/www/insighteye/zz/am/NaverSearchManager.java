package www.insighteye.zz.am;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.opencsv.CSVWriter;

import www.insighteye.zz.am.article.Article;

public class NaverSearchManager {
	private String url = "https://search.naver.com/search.naver?where=news&ie=utf8&photo=0&field=0&pd=3"; // 검색
	// URL, field는 상의 필요
	private SimpleDateFormat sd_org = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd");
	// 날짜 포맷, 수정 금지
	final private int NUM = 10; // 기사 표시 개수
	final private int SORT = 3; // 정렬방법, 0:관련 / 1:최신 / 2:오래된
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

	private int page; // 현재 페이지 p
	private int total; // 검색 건수
	private int maxPage; // 최대 페이지
	private String path; // 경로

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	private ArrayList<Article> list; // 기사 csv 저장용 리스트

	public NaverSearchManager(String sdate, String edate, int _term) {
		try {
			list = new ArrayList<Article>();
			startDate = sd_org.parse(sdate); // 최초일
			endDate = sd_org.parse(edate); // 최종일
			term = _term; // 검색 텀 지정
		} catch (ParseException e) {
			System.out.println("Informal date format error!!");
		}
	}

	private HttpEntity getEntity(String _url) throws ClientProtocolException, IOException {
		http = new HttpGet(_url);
		httpClient = HttpClientBuilder.create().build();
		response = httpClient.execute(http);
		entity = response.getEntity();

		return entity;
	}

	@SuppressWarnings("finally")
	public Document search(String _keyword) {
		// 다음 페이지 검색 - 최초 수행

		keyword = _keyword.replaceAll("\"", "");
		// 저장용 키워드
		String result = "";
		Document doc = null;

		try {
			query = URLEncoder.encode(_keyword, "UTF-8");
			tempUrl = url + "&query=" + query + "&sort=" + SORT;

			curSdate = startDate;
			curEdate = addTerm(curSdate);

			if (curEdate.after(endDate) && !done) {
				curEdate = endDate;
			}

			tempUrl = tempUrl + "&ds=" + sd.format(curSdate) + "&de=" + sd.format(curEdate);
			System.out.println("시작일 : " + curSdate + " 종료일: " + curEdate);
			System.out.println("시작일 포맷 : " + sd.format(curSdate) + " 종료일 포맷 : " + sd.format(curEdate));
			System.out.println("init url : " + tempUrl);

			// Http 요청
			entity = getEntity(tempUrl);
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
			// System.out.println(doc);
			total = getTotal(doc);
			setPage();
			// 최대 페이지 수
			page = 11;
			prevSdate = curSdate; // 필요없음
			prevEdate = curEdate;
			// saveAsHTML(result);
			parseArticle(doc);
			return doc;
		}
	}

	private int getTotal(Document doc) {
		try {
			Elements elem = doc.select("div.title_desc");
			System.out.println(elem.toString());
			Pattern pat = Pattern.compile("[0-9,]*건");
			Matcher mat = pat.matcher(elem.text());
			mat.find();

			total = Integer.parseInt(mat.group().replaceAll("[건,]*", ""));
			// 전체 검색 결과 개수
			System.out.println("전체 건수 : " + total);
		} catch (IllegalStateException e) {
			total = 0;
		}
		return total;
	}

	public void saveAsCSV() {
		String fileName;
		if (path != null) {
			fileName = path + "\\NAVER-" + keyword + ".csv";
		} else {
			fileName = "NAVER-" + keyword + ".csv";
		}
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

	@SuppressWarnings("finally")
	public Document search() {
		String result = "";
		Document doc = null;
		boolean renew = false;
		String tempUrl2;
		// System.out.println("done = " + done);
		// 저장용 키워드
		try {
			// 마지막 페이지 도달 여부 확인
			if (page >= maxPage) {
				System.out.println("Page done");
				if (!done) {
					changeDate();
					renew = true;
				} else if (done) {
					saveAsCSV();
					doc = null;
					return null;
				}
				// total 갱신
			} else {
				// 마지막 페이지가 아닌 경우, 페이지 증가 후 검색
				page += 10;
			}
			if (renew)
				tempUrl2 = tempUrl;
			else
				tempUrl2 = tempUrl + "&start=" + page; // 페이지
			// Http 요청
			System.out.println("renewed url : " + tempUrl2);
			entity = getEntity(tempUrl2);
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
			// saveAsHTML(result);
			if (!done)
				parseArticle(doc);
			return doc;
		}
	}

	private void changeDate() {
		System.out.println("changeDate() called.");
		prevSdate = curSdate;
		prevEdate = curEdate;

		curSdate = addTerm(prevEdate, 1);
		curEdate = addTerm(curSdate);
		System.out.println("시작일 : " + curSdate + " 종료일: " + curEdate);
		System.out.println("시작일 포맷 : " + sd.format(curSdate) + " 종료일 포맷 : " + sd.format(curEdate));

		if (curEdate.equals(endDate)) {
			System.out.println("same");
			curEdate = endDate;
		} else if (curEdate.after(endDate)) {
			curEdate = endDate;
			done = true;
		}
		tempUrl = url + "&q=" + query;
		tempUrl = tempUrl + "&ds=" + sd.format(curSdate) + "&de=" + sd.format(curEdate);

		System.out.println("Updated url : " + tempUrl);
	}

	private void setPage() {
		// System.out.println(total);
		// 네이버는 page를 기사 개수로 표시해야함(start지점으로)
		maxPage = total;

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

	private Boolean parseArticle(Document doc) {
		boolean success = false;
		Article art = null;
		Elements divs = doc.select("li[id^=sp_nws");
		// css selector 사용, 더 유용한듯
		// https://www.w3schools.com/cssref/css_selectors.asp
		System.out.println("size of div = " + divs.size());
		for (Element elem : divs) {
			art = new Article();
			art.setTitle(elem.select("a._sp_each_title").get(0).text());
			System.out.println("추출된 제목 : " + art.getTitle());
			// 제목 추출
			art.setDescription(elem.select("dd").get(1).text());
			System.out.println("추출된 내용 : " + art.getDescription());
			// 내용 추출
			art.setDate(elem.select("dd.txt_inline").get(0).ownText().substring(0, 10));
			System.out.println("추출된 날짜 : " + art.getDate());
			// 날짜 추출
			art.setUrl(elem.select("a._sp_each_url").get(0).attr("abs:href"));
			System.out.println("추출된 URL : " + art.getUrl());
			// 링크 추출
			art.setPublisher(elem.select("span._sp_each_source").get(0).text());
			System.out.println("추출된 언론사 : " + art.getPublisher());
			// 언론사 추출

			list.add(art);

			// System.out.println(elem.select("div.wrap_tit a").get(0).text());
			// System.out.println(elem.select("span.f_nb").get(0).text().substring(0,10));
			// System.out.println(elem.select("div.wrap_tit
			// a").get(0).attr("abs:href"));
			// System.out.println(elem.select("p.f_eb").get(0).text());
			// System.out.println(elem.select("span.f_nb").get(0).text().substring(10).replaceAll("[|]",
			// "").trim());
		}

		return success;
	}

}
