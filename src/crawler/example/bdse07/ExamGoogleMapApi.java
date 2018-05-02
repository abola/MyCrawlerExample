package crawler.example.bdse07;

import com.github.abola.crawler.CrawlerPack;
import org.apache.commons.logging.impl.SimpleLog;

/**
 * 練習題：請使用 Google direction API (導航)功能，完成API call，以及印出 distance
 *
 * 完成後同學請記得兩步動作上傳
 * 1. git > add
 * 2. git > commit file (一定要記得PUSH!!)
 */
public class ExamGoogleMapApi {

    public static void main(String[] args) {
        CrawlerPack.setLoggerLevel(SimpleLog.LOG_LEVEL_OFF);

        String api_key = "AIzaSyCE3rhrAg9_Nuxr1i-lfwTnbZ48ECkc-9c";
        String username = "kos44444";

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
