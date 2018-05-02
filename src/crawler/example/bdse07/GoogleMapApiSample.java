package crawler.example.bdse07;

import com.github.abola.crawler.CrawlerPack;
import org.apache.commons.logging.impl.SimpleLog;

public class GoogleMapApiSample {

    public static void main(String[] args) {
        CrawlerPack.setLoggerLevel(SimpleLog.LOG_LEVEL_OFF);

        String api_key = "AIzaSyCE3rhrAg9_Nuxr1i-lfwTnbZ48ECkc-9c";

        // 遠端資料路徑 (可先在postman 完成查詢，再貼上)
        String uri = "";

        // 完成下方 select 部份的內容，使其可取得 distance 的內容
        String distance =
            CrawlerPack.start()
                    .getFromJson(uri)
                    .select("")
                    .text();

        System.out.println("result: " + distance);
    }
}
