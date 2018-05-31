package crawler.example;

import com.github.abola.crawler.CrawlerPack;
import org.apache.commons.logging.impl.SimpleLog;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 591租屋資料下載範例
 *
 * @author Abola Lee
 *
 */
public class Example591 {


    static Integer firstRow = 0;
    static Integer totalRows = 0;

    public static void main(String[] args) {

        // turn off logging
        CrawlerPack.setLoggerLevel(SimpleLog.LOG_LEVEL_OFF);

        Set<String> idSet = new LinkedHashSet<>();

        idSet = loopQueryUntilEnd(idSet);

        System.out.println("Id List: " + idSet.toString() );
        System.out.println("Total id: " + idSet.size());

    }

    public static Set<String> loopQueryUntilEnd(Set<String> idSet){
        String uri = "https://business.591.com.tw/home/search/rsList?" +
                "is_new_list=1" +
                "&storeType=1" +
                "&type=1" +
                "&kind=5" +
                "&searchtype=1" +
                "&region=1";
        if ( firstRow > 0 )  uri = uri + "&firstRow="  + firstRow ;
        if ( totalRows > 0 ) uri = uri + "&totalRows=" + totalRows;


        Document jsoup = CrawlerPack.start()
                            .setRemoteEncoding("big5")
                            .addCookie("urlJumpIp","15")
                            //.addCookie("urlJumpIpByTxt", "%E5%8F%B0%E5%8D%97%E5%B8%82")
                            .getFromJson(uri);

        for(Element list: jsoup.select("data > data address") ){
            idSet.add(list.text());
        }

        firstRow += 30;
        totalRows = Integer.valueOf( jsoup.select("records").text() );

        System.out.println(totalRows);
        if (firstRow < totalRows ){
            return loopQueryUntilEnd(idSet);
        }
        else return idSet;
    }
}