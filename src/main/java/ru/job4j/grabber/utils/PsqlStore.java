package ru.job4j.grabber.utils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private final Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement("insert into post (name, link, text, created)"
                + "values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getLink());
            statement.setString(3, post.getDescription());
            Timestamp timestampFromLDT = Timestamp.valueOf(post.getCreated());
            statement.setTimestamp(4, timestampFromLDT);
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no id obtained.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement =
                     cnn.prepareStatement("select * from post")) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                posts.add(getPost(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    public Post getPost(ResultSet result) throws SQLException {
        return new Post(result.getInt("id"),
                result.getString("name"),
                result.getString("link"),
                result.getString("text"),
                result.getTimestamp("created").toLocalDateTime());
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement =
                     cnn.prepareStatement("select * from post where id=?")) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                post = getPost(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(String.valueOf(Path.of("./db/app.properties"))));
        PsqlStore psqlStore = new PsqlStore(properties);
        SqlRuParse parse = new SqlRuParse(new SqlRuDateTimeParser());
        List<Post> posts = parse
                .list("https://www.sql.ru/forum/job-offers/");
        posts.forEach(psqlStore::save);
        psqlStore.getAll().forEach(System.out::println);
    }
}
