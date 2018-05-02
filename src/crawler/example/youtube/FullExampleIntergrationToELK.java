package crawler.example.youtube;

import com.github.abola.crawler.CrawlerPack;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
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
        // 確認要查詢 channels 清單
        List<String> channels = getChannels();

        // 使用 Guava 物件 Table 資料會像以下
        // | row | column | value|
        // |-----|--------|------|
        // | id  | item1  | aaa  |
        // | id  | item2  | bbb  |
        // | id  | item3  | ccc  |
        Table<String, String, String> videos = null;
        // 讀取 channels 的 videos
        for(String ch: channels ){
            videos = getVideos(ch);
        }

        // 取得

    }



    /**
     * 讀取指定 username or channelId 所有的頻道清單
     * @return
     */
    public List<String> getChannels() throws Exception{
        List<String> channels = new ArrayList<>() ;

        // 有指定 username，就用 username來找channels
        if (!"".equals(username)){
            // 讀取指定 username 所有的頻道清單
            String uri = "https://www.googleapis.com/youtube/v3/channels?forUsername=" + username + "&part=snippet,id&key=" + api_key;

            for (Element elem : CrawlerPack.start().getFromJson(uri).select("items id")) {
                //System.out.println(elem);
                String channelId = elem.select("id").text();
                //String channelTitle = elem.select("title").text();
                channels.add(channelId);

            }
        }
        // 沒有指定username，就用指定的 channelId
        else if(!"".equals(channelId)){
            channels.add(channelId);
        }
        else{
            throw new Exception("未輸入有效的username或channelId");
        }

        return channels;
    }

    public Table<String, String, String> getVideos(String channelId){
        return getVideos(channelId, "", null);
    }

    public Table<String, String, String> getVideos(String channelId, String pageToken, Table<String, String, String> videoTable){

        // 首次進入建立TABLE物件
        if (null == videoTable) {
            videoTable = HashBasedTable.create();
        }

        String uri = "https://www.googleapis.com/youtube/v3/search?channelId="+channelId+
                "&fields=items(id(videoId),snippet(title)),nextPageToken" +
                "&part=snippet&order=date&maxResults=50&key="+api_key;

        // 如果有指定換頁指標
        if( !"".equals(pageToken) ){
            uri += "&pageToken=" + pageToken;
        }

        Element results = null;
        // 如果已達最後一頁，會因為最後一頁無資料，出現IndexOutOfBoundsException
        try {
            results = CrawlerPack.start().getFromJson(uri);
        }
        catch(java.lang.IndexOutOfBoundsException outBounds){
            return videoTable;
        }

        for (Element elem : results.select("items")) {
            String videoId = elem.select("id").text();
            String title = elem.select("title").text();

            // 空ID資料不處理
            if ("".equals(videoId)) continue;

            videoTable.put(videoId, "videoid", videoId);
            videoTable.put(videoId, "title", title);

        }


        String nextPageToken = results.select("nextPageToken").text();
        if ( !"".equals(nextPageToken) ){
            // return
            return getVideos(channelId, nextPageToken, videoTable);
        }

        return videoTable;
    }


    public static void main(String[] args) {
        CrawlerPack.setLoggerLevel(SimpleLog.LOG_LEVEL_OFF);

        try {
            new FullExampleIntergrationToELK();
        }catch(Exception ex){
            ex.printStackTrace();
//            System.out.println(ex.getMessage());
        }
    }
}
