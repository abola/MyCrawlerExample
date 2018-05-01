package crawler.example.youtube;

import com.github.abola.crawler.CrawlerPack;
import org.apache.commons.logging.impl.SimpleLog;

/**
 * 透過 userid 找出相關的 channels
 */
public class FindVideoComments {

    public static void main(String[] args) {
        CrawlerPack.setLoggerLevel(SimpleLog.LOG_LEVEL_OFF);

        String api_key = "AIzaSyCE3rhrAg9_Nuxr1i-lfwTnbZ48ECkc-9c";
        String videoId = "xkcPPjPNU6A";

        // 遠端資料路徑
        String uri = "https://www.googleapis.com/youtube/v3/commentThreads?videoId="+videoId+"&part=snippet&order=relevance&maxResults=100&key="+api_key;

        System.out.println(
            CrawlerPack.start()
                .getFromJson(uri)
                .select("snippet snippet textDisplay,authorChannelId value")
                .text() // 取出文字內容
        );
    }
}
