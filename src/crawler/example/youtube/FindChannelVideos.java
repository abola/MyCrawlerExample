package crawler.example.youtube;

import com.github.abola.crawler.CrawlerPack;
import org.apache.commons.logging.impl.SimpleLog;

/**
 * 透過 channel 找出相關的 videos
 */
public class FindChannelVideos {

    public static void main(String[] args) {
        CrawlerPack.setLoggerLevel(SimpleLog.LOG_LEVEL_OFF);

        String api_key = "AIzaSyCE3rhrAg9_Nuxr1i-lfwTnbZ48ECkc-9c";
        String username = "kos44444";

        // 遠端資料路徑
        String uri = "https://www.googleapis.com/youtube/v3/channels?forUsername="+username+"&part=id&key="+api_key;

        System.out.println(
            CrawlerPack.start()
                .getFromJson(uri)
                .select("items id")
                .text() // 取出文字內容
        );
    }
}
