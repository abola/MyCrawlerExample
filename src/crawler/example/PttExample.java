package crawler.example;

import com.github.abola.crawler.CrawlerPack;
import org.apache.commons.logging.impl.SimpleLog;
import org.jsoup.nodes.Document;

/**
 * 爬蟲包程式的全貌，就只有這固定的模式
 * 
 * @author Abola Lee
 *
 */
public class PttExample {
	// commit test test
	public static void main(String[] args) {

		// set to debug level
		//CrawlerPack.setLoggerLevel(SimpleLog.LOG_LEVEL_DEBUG);

		// turn off logging
		CrawlerPack.setLoggerLevel(SimpleLog.LOG_LEVEL_OFF);

		// 遠端資料路徑
		String uri = "https://www.ptt.cc/bbs/Gossiping/M.1524893176.A.DED.html";

		/*
		System.out.println(
				CrawlerPack.start()
				// 參數設定
			    .addCookie("over18","1")	// 設定cookie
				//.setRemoteEncoding("big5")// 設定遠端資料文件編碼
				
				// 選擇資料格式 (三選一)
				//.getFromJson(uri)
			    .getFromHtml(uri)
			    //.getFromXml(uri)
			    
			    // 這兒開始是 Jsoup Document 物件操作
			    //.select(".article-meta-value")
				//.select("#main-content div.push:contains(噓) .f3.push-content")
				//.select("#main-content ")
		);
		*/

		System.out.println(
				CrawlerPack.start()
						// 參數設定
						.addCookie("over18","1")	// 設定cookie
						//.setRemoteEncoding("big5")// 設定遠端資料文件編碼

						// 選擇資料格式 (三選一)
						//.getFromJson(uri)
						.getFromHtml(uri)
				//.getFromXml(uri)

				// 這兒開始是 Jsoup Document 物件操作
				.select(".main-content div").remove()
						.select(".main-content span").remove()
				.select("#main-content").text()
				//.select("#main-content div.push:contains(噓) .f3.push-content")
				//.select("#main-content ")
		);



		/*
		//只取內容，刪除div、span作法
		Document jsoupObject = CrawlerPack.start().addCookie("over18", "1").getFromHtml(uri);

		jsoupObject.select("#main-content div").remove();
		jsoupObject.select("#main-content span").remove();

		System.out.println( jsoupObject.select("#main-content").text());
		*/
	}
}
