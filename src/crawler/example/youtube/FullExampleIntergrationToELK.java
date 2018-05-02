package crawler.example.youtube;

import com.github.abola.crawler.CrawlerPack;
import com.google.common.base.Joiner;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mashape.unirest.http.Unirest;
import org.apache.commons.logging.impl.SimpleLog;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.util.*;

/**
 * 透過 userid 找出相關的 channels
 */
public class FullExampleIntergrationToELK {

    static String elasticHost = "localhost" ;
    static String elasticPort = "9200" ;
    static String elasticIndex = "youtube-"; // 請在後方加入帳號(ex: youtube-abola)，務必全小寫字母
    static String elasticIndexType = "data"; // 範例請不要改這行

    // 設定使用者ID或頻道ID任一
    String username = "e84768131";
    String channelId = "";
    String api_key = "AIzaSyCE3rhrAg9_Nuxr1i-lfwTnbZ48ECkc-9c";

    // 使用 Guava 物件 Table 資料會像以下
    // | row | column | value|
    // |-----|--------|------|
    // | id  | item1  | aaa  |
    // | id  | item2  | bbb  |
    // | id  | item3  | ccc  |
    Table<String, String, String> videoTable;


    public FullExampleIntergrationToELK() throws Exception{
        // 確認要查詢 channels 清單
        List<String> channels = getChannels();

        // 讀取 channels 的 videos
        for(String channelId: channels ){
            getVideos(channelId);
        }

        // 更新每一個影片的統計資料
        getVideoStatistics( videoTable.rowKeySet() );


        // 將資料寫入 Elasticsearch
        for(String row: videoTable.rowKeySet()){
            String elasticJson = new JSONObject(videoTable.row(row)).toString();
            sendPost("http://" + elasticHost + ":" + elasticPort
                            + "/" + elasticIndex + "/" + elasticIndexType
                    , elasticJson);
        }
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

    /**
     * 取得指定CHANNEL的影片清單
     * @param channelId
     * @return
     */
    public void getVideos(String channelId){
        getVideos(channelId, "");
    }

    /**
     * 取得指定CHANNEL的影片清單
     * @param channelId
     * @return
     */
    public void getVideos(String channelId, String pageToken){

        // 首次進入建立TABLE物件
        if (null == videoTable) {
            videoTable = HashBasedTable.create();
        }

        String uri = "https://www.googleapis.com/youtube/v3/search?channelId="+channelId+
                "&fields=items(id(videoId),snippet(title,channelTitle)),nextPageToken" +
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
            return ;
        }

        for (Element elem : results.select("items")) {
            String videoId = elem.select("id").text();
            String title = elem.select("title").text();
            String channelTitle = elem.select("channelTitle").text();

            // 空ID資料不處理
            if ("".equals(videoId)) continue;

            videoTable.put(videoId, "videoid", videoId);
            videoTable.put(videoId, "title", title);
            videoTable.put(videoId, "channelTitle", channelTitle);

        }


        String nextPageToken = results.select("nextPageToken").text();
        if ( !"".equals(nextPageToken) ){
            // return
            getVideos(channelId, nextPageToken);
        }
    }

    /**
     * 查詢每一部影片的統計資料，50筆資料送一次REQUEST，加速處理
     *
     * @param videos
     */
    public void getVideoStatistics(Set<String> videos){
        int idsLimitCounter = 50;
        List<String> ids = new ArrayList<>();
        // 取得 video 的統計資訊
        for(String videoId: videos){
            ids.add(videoId);
            // 計數，累計至最大值才執行
            idsLimitCounter--;
            if ( 0 >= idsLimitCounter ){
                // reset counter
                idsLimitCounter = 50;
                // Guava 指令：將集合物件使用指定的符號合併成一個字串
                getVideoStatistics( Joiner.on(",").join(ids) );
                ids = new ArrayList<>();
            }
        }
        if (0 < ids.size()) getVideoStatistics( Joiner.on(",").join(ids) );
    }

    /**
     * 查詢指定ID(s)的統計資料，並回填至 TABLE
     * @param ids
     */
    public void getVideoStatistics(String ids){
        System.out.println(ids);
        String uri = "https://www.googleapis.com/youtube/v3/videos?id="+ids+
                "&part=snippet,statistics&fields=items(id,snippet(publishedAt),statistics)"+
                "&key="+api_key;

        for (Element elem : CrawlerPack.start().getFromJson(uri).select("items")) {
            String videoId = elem.select("id").text();
            String publishedAt = elem.select("publishedAt").text();
            String viewCount = elem.select("viewCount").text();
            String likeCount = elem.select("likeCount").text();
            String dislikeCount = elem.select("dislikeCount").text();
            String commentCount = elem.select("commentCount").text();

            videoTable.put(videoId, "publishedAt", publishedAt);
            videoTable.put(videoId, "viewCount", viewCount);
            videoTable.put(videoId, "likeCount", likeCount);
            videoTable.put(videoId, "dislikeCount", dislikeCount);
            videoTable.put(videoId, "commentCount", commentCount);
        }
    }


    String sendPost(String url, String body){
        try{
            return Unirest.post(url)
                    .header("content-type", "text/plain")
                    .header("cache-control", "no-cache")
                    .body(body)
                    .asString().getBody();

        }catch(Exception e){return "Error:" + e.getMessage();}
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
