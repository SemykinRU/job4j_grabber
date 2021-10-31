package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SqlRuParse {

    public String description(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements row = doc.select(".msgBody");
        String str = "";
        for (var s : row) {
            str = s.parent().child(1).text();
            break;
        }
        return str;
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse sqlRuParse = new SqlRuParse();
        String url = "https://www.sql.ru/forum/job-offers";
        for (int i = 1; i < 6; i++) {
            Document doc = Jsoup.connect(String.format("%s/%d", url, i)).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                System.out.println(td.parent().child(5).text());
                System.out.println(sqlRuParse.description(href.attr("href")));
            }
        }
    }
}
