package ru.job4j.html;

import java.time.LocalDateTime;
import java.util.Objects;

public class PostModel {
    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime created;
    private LocalDateTime update;

    public PostModel() {
    }

    public PostModel(int id, String title, String link, String description, LocalDateTime created, LocalDateTime update) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = description;
        this.created = created;
        this.update = update;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdate() {
        return update;
    }

    public void setUpdate(LocalDateTime update) {
        this.update = update;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostModel postModel = (PostModel) o;
        return id == postModel.id
                && Objects.equals(title, postModel.title)
                && Objects.equals(link, postModel.link)
                && Objects.equals(created, postModel.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, link, created);
    }

    @Override
    public String toString() {
        return "PostModel{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", link='" + link + '\''
                + ", description='" + description + '\''
                + ", created=" + created
                + ", update=" + update
                + '}';
    }
}
