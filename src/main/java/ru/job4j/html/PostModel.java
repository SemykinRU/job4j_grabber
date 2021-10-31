package ru.job4j.html;

import java.time.LocalDateTime;
import java.util.Objects;

public class PostModel {
    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostModel postModel = (PostModel) o;
        return Objects.equals(title, postModel.title)
                && Objects.equals(link, postModel.link)
                && Objects.equals(description, postModel.description)
                && Objects.equals(created, postModel.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, link, description, created);
    }

    @Override
    public String toString() {
        return "PostModel{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", link='" + link + '\''
                + ", description='" + description + '\''
                + ", created=" + created
                + '}';
    }
}
