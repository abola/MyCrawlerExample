package crawler.example;

import com.github.abola.crawler.CrawlerPack;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 練習： 實價登錄資料取得
 * 
 * 重點
 * 1. 練習找出實價登錄公開資料源
 * 2. zip 格式資料如何取出指定檔案
 * 3. 爬蟲包可解析中文TagName，試著使用看看
 * 
 * @author Abola Lee
 *
 */
public class RealPrice {
	public static void main(String[] args) {

		/*
		String uri = "zip:http://plvr.land.moi.gov.tw"
				+ "/Download?type=zip&fileName=lvr_landxml.zip"
				+ "!/A_LVR_LAND_A.XML";
		*/

		String uri = "zip:http://plvr.land.moi.gov.tw/Download?type=zip&fileName=lvr_landxml.zip"
				+ "!/B_LVR_LAND_A.XML";

		Document jsoupDoc = CrawlerPack.start()
				.getFromXml(uri);

		// 印出整份 XML 資料
//		System.out.println(jsoupDoc.toString());

		// print head
		System.out.println("鄉鎮市區,都市土地使用分區,土地區段位置或建物區門牌,總價元,單價每平方公尺");

		for( Element elem: jsoupDoc.select("買賣") ){
			System.out.print("\""+elem.select("鄉鎮市區").text()+"\"");
			System.out.print(",\""+elem.select("都市土地使用分區").text()+"\"");
			System.out.print(",\""+elem.select("土地區段位置或建物區門牌").text()+"\"");
			System.out.print(","+elem.select("總價元").text());
			System.out.print(","+elem.select("單價每平方公尺").text());
			System.out.println();
		}



	}	
	
}
