package crawler.example;

import com.github.abola.crawler.CrawlerPack;
import org.jsoup.nodes.Document;


/**
 * 簡易練習
 * 
 * 找出所有文章中按推的id
 * 
 * @author Abola Lee
 *
 */
public class PttGetContent {
	
	public static void main(String[] args) {
		String uri = "https://www.ptt.cc/bbs/Gossiping/M.1525278814.A.571.html";

		Document jsoupObject = CrawlerPack.start().addCookie("over18", "1").getFromHtml(uri);

		jsoupObject.select("#main-content div").remove();
		jsoupObject.select("#main-content span").remove();


		System.out.println( jsoupObject.select("#main-content").text());
	}
}
