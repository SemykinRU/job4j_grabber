package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.utils.DateTimeParser;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.text.ParseException;

public class SqlRuParse {
    private static final String POSTSLISTTOPIC = ".postslisttopic";
    private static final String MSG_BODY = ".msgBody";
    private static final String HREF = "href";
    private final static String BASE_URL = "https://www.sql.ru/forum/job-offers";
    private static DateTimeParser dateTimeParser;
    private static Elements row = null;
    private static int count = 0;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        SqlRuParse.dateTimeParser = dateTimeParser;
    }

    private void setConnectAndSelect(String url, String select) throws IOException {
        Document doc = Jsoup.connect(url).get();
        row = doc.select(select);
    }

    public PostModel detail(String baseUrl) throws IOException, ParseException {
        PostModel postModel = new PostModel();
        setConnectAndSelect(baseUrl, POSTSLISTTOPIC);
        int maxCount = row.size();
        Element el = row.get(count).child(0);
        postModel.setLink(el.attr(HREF));
        postModel.setTitle(el.text());
        postModel.setUpdate(dateTimeParser.parse(
                el.parent()
                        .parent()
                        .child(5)
                        .text()));
        setDescription(el.attr(HREF), postModel);
        count++;
        count = count >= maxCount ? 0 : count;
        return postModel;
    }

    private void setDescription(String desLink, PostModel postModel) throws IOException, ParseException {
        setConnectAndSelect(desLink, MSG_BODY);
        postModel.setDescription(row.first()
                .parent()
                .child(1)
                .text());
        postModel.setCreated(dateTimeParser.parse(
                row.first()
                        .parent()
                        .siblingElements()
                        .get(1)
                        .child(0)
                        .text()));
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse sqlRuParse = new SqlRuParse(new SqlRuDateTimeParser());
        System.out.println(sqlRuParse.detail(BASE_URL));
        System.out.println(sqlRuParse.detail(BASE_URL));
        System.out.println(sqlRuParse.detail(BASE_URL));
        System.out.println(sqlRuParse.detail(BASE_URL));
        System.out.println(sqlRuParse.detail(BASE_URL));
        System.out.println(sqlRuParse.detail(BASE_URL));
    }
}
