package ru.job4j.grabber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.html.Post;
import ru.job4j.html.SqlRuParse;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private final Connection cnn;
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());
    private final static String SQL_SAVE_IN_POST = "insert into post(name, text, link, created) values(?, ?, ?, ?)";
    private final static String GET_ALL_FROM_POST = "select * from post";
    private final static String GET_BY_ID_FROM_POST = "select * from post where id = ?";
    private final static String IS_EXIST = "select link from post where link like ?";
    private final static String IS_EXIST_INFO = "Запись %s уже присутствует в БД";

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("jdbc.url"),
                    cfg.getProperty("jdbc.username"),
                    cfg.getProperty("jdbc.password"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean isExist(String link) {
        try (PreparedStatement statement = cnn.prepareStatement(IS_EXIST)) {
            statement.setString(1, link);
            if (statement.executeQuery().next()) {
                LOG.info(String.format(IS_EXIST_INFO, link));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     cnn.prepareStatement(SQL_SAVE_IN_POST,
                             Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            if (isExist(post.getLink())) {
                return;
            }
            statement.execute();
            try (ResultSet generatedKey = statement.getGeneratedKeys()) {
                if (generatedKey.next()) {
                    post.setId(generatedKey.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> list = new ArrayList<>();
        try (PreparedStatement statement =
                     cnn.prepareStatement(GET_ALL_FROM_POST)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Post post = setPostModelFromResultSet(resultSet);
                    list.add(post);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement =
                     cnn.prepareStatement(GET_BY_ID_FROM_POST)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post = setPostModelFromResultSet(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    private Post setPostModelFromResultSet(ResultSet resultSet) throws SQLException {
        return new Post(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("link"),
                resultSet.getString("text"),
                resultSet.getTimestamp("created").toLocalDateTime());
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        Properties cfg = new Properties();
        SqlRuParse sqlRuParse = new SqlRuParse(new SqlRuDateTimeParser());
        List<Post> list;
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("psqlStore.properties")) {
            cfg.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PsqlStore psqlStore = new PsqlStore(cfg);
        list = sqlRuParse.list("https://www.sql.ru/forum/job-offers");
        list.forEach(psqlStore::save);
        psqlStore.getAll().forEach(System.out::println);
    }
}