package ru.job4j.quartz;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class AlertRabbitTest {

    @Deprecated
    @Test
    public  void whenSetIntervalFromFileThenGet() {
        AlertRabbit alertRabbit = new AlertRabbit();
        int interval = alertRabbit.getInterval();
        assertThat(interval, is(10));
    }
}