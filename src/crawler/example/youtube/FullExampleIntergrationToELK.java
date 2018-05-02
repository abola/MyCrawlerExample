package crawler.example.youtube;

import com.github.abola.crawler.CrawlerPack;
import org.apache.commons.logging.impl.SimpleLog;
import org.jsoup.nodes.Element;

import java.util.*;

/**
 * 透過 userid 找出相關的 channels
 */
public class FullExampleIntergrationToELK {

    String username = "kos44444";
    String channelId = "";
    String api_key = "AIzaSyCE3rhrAg9_Nuxr1i-lfwTnbZ48ECkc-9c";



    public FullExampleIntergrationToELK() throws Exception{

        Map<String, String> channels = getChannels();



        System.out.println(channels);

    }



    /**
     * 讀取指定 username or channelId 所有的頻道清單
     * @return
     */
    public Map<String, String> getChannels() throws Exception{
        Map<String, String> channels = new HashMap<>() ;

        // 有指定 username，就用 username來找channels
        if (!"".equals(username)){
            // 讀取指定 username 所有的頻道清單
            String uri = "https://www.googleapis.com/youtube/v3/channels?forUsername=" + username + "&part=snippet,id&key=" + api_key;

            for (Element elem : CrawlerPack.start().getFromJson(uri).select("items snippet > title,items id")) {
                System.out.println(elem);
                String channelId = elem.select("id").text();
                String channelTitle = elem.select("title").text();
                channels.put(channelId, channelTitle);
            }
        }
        // 沒有指定username，就用指定的 channelId
        else if(!"".equals(channelId)){
            channels = new HashMap<>();
            channels.put(channelId, "");
        }
        else{
            throw new Exception("未輸入有效的username或channelId");
        }

        return channels;
    }

    public static void main(String[] args) {
        CrawlerPack.setLoggerLevel(SimpleLog.LOG_LEVEL_OFF);

        try {
            new FullExampleIntergrationToELK();
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}
