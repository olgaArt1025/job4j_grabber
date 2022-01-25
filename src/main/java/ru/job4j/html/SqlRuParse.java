package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.Post;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        for (int i = 1; i < 6; i++) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + i).get();
            Elements row = doc.select(".postslisttopic");
            System.out.println("Page: " + i);
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                SqlRuDateTimeParser parseDate = new SqlRuDateTimeParser();
                System.out.println(parseDate.parse(td.parent().child(5).text()));
            }
        }
    }

    public static Post uploadingDetailsPost(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        String description = doc.select(".msgBody").get(1).text();
        LocalDateTime created = new SqlRuDateTimeParser()
                .parse(doc.select(".msgFooter")
                        .get(0).text().split(" \\[")[0]
                );
        return new Post(link, description, created);
    }
}
