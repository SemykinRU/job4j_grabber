package ru.job4j.grabber;

import ru.job4j.html.PostModel;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface Parse {
    List<PostModel> list(String link) throws IOException, ParseException;
    PostModel detail(String link) throws IOException, ParseException;
}