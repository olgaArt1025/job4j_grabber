package ru.job4j.grabber.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        try {
            for (int i = 1; i < 6; i++) {
                Document doc = Jsoup.connect(link + i).get();
                Elements row = doc.select(".postslisttopic");
                for (Element td : row) {
                    Element href = td.child(0);
                    String title = doc.title().split("/ Вакансии")[0];
                    if (title.contains("java") && !title.contains("javascript")) {
                        posts.add(detail(href.attr("href")));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post detail(String link) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String title = doc.title().split("/ Вакансии")[0];
        String description = doc.select(".msgBody").get(1).text();
        LocalDateTime created = null;
        try {
            created = dateTimeParser
                    .parse(doc.select(".msgFooter")
                            .get(0).text()
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Post(title, link, description, created);
    }
}
