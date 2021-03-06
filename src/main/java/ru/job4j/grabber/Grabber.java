package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.html.Post;
import ru.job4j.html.SqlRuParse;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Properties cfg = new Properties();

    public Store store() {
        return new PsqlStore(cfg);
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfg(String path) throws IOException {
        try (InputStream in = new FileInputStream(path)) {
            cfg.load(in);
        }
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public void web(Store store) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(Integer.parseInt(cfg.getProperty("port")))) {
                while (!server.isClosed()) {
                    Socket socket = server.accept();
                    try (OutputStream out = socket.getOutputStream()) {
                        out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                        for (Post post : store.getAll()) {
                            out.write(post.toString().getBytes(Charset.forName("Windows-1251")));
                            out.write(System.lineSeparator().getBytes());
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static class GrabJob implements Job {
        private static final String SQL_RU_JOB = "https://www.sql.ru/forum/job-offers";

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            try {
                List<Post> list = parse.list(SQL_RU_JOB);
                list.stream().filter(x -> x.getTitle().contains("Java") && !x.getTitle().contains("JavaScript")).forEach(store::save);
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg(Objects.requireNonNull(Grabber.class.getClassLoader().getResource("test.properties")).getPath());
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new SqlRuParse(new SqlRuDateTimeParser()), store, scheduler);
        grab.web(store);
    }
}