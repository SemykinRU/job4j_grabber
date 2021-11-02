package ru.job4j.grabber;

import ru.job4j.html.PostModel;
import java.util.List;

public interface Store {
    void save(PostModel post);
    List<PostModel> getAll();
    PostModel findById(int id);
}