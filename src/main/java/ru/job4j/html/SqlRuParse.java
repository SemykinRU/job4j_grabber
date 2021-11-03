package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.utils.DateTimeParser;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    private static final String POSTSLISTTOPIC = ".postslisttopic";
    private static final String MSG_BODY = ".msgBody";
    private static final String MSG_TABLE = ".msgTable";
    private static final String MESSAGE_HEADER = ".messageHeader";
    private static final String MSG_FOOTER = ".msgFooter";
    private static final Integer HREF_ELEMENT = 0;
    private static final String HREF = "href";
    private final static String BASE_URL = "https://www.sql.ru/forum/job-offers";
    private static DateTimeParser dateTimeParser;
    private static Elements row = null;
    private static final Integer PAGE_COUNT = 2;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        SqlRuParse.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String baseUrl) throws IOException, ParseException {
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i <= PAGE_COUNT; i++) {
            Document doc = Jsoup.connect(String.format("%s/%d", BASE_URL, i)).get();
            row = doc.select(POSTSLISTTOPIC);
            for (var element : row) {
                Element href = element.child(HREF_ELEMENT);
                Post post = detail(href.attr(HREF));
                postList.add(post);
            }
        }
        return postList;
    }

    @Override
    public Post detail(String link) throws IOException, ParseException {
        Document doc = Jsoup.connect(link).get();
        row = doc.select(MSG_TABLE);
        Post post = new Post();
        post.setLink(link);
        post.setTitle(row.first()
                .select(MESSAGE_HEADER)
                .text());
       post.setDescription(row.first()
               .select(MSG_BODY)
               .next()
               .text());
        post.setCreated(dateTimeParser.parse(
                row.first()
                        .select(MSG_FOOTER)
                        .text()));
        return post;
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse sqlRuParse = new SqlRuParse(new SqlRuDateTimeParser());
        sqlRuParse.list(BASE_URL).forEach(System.out::println);
    }
}
