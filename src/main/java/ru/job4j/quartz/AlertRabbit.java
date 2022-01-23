package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.sql.*;
import java.time.LocalDateTime;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        try (ConnectSQL connection = new ConnectSQL()) {
            connection.init();
            try {
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                JobDataMap data = new JobDataMap();
                data.put("connection", connection);
                JobDetail job = newJob(Rabbit.class)
                        .usingJobData(data)
                        .build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(connection.getTimes())
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(10000);
                scheduler.shutdown();
            } catch (SchedulerException | InterruptedException se) {
                se.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.printf("Rabbit runs here ... [%s]%s",
                    context.getFireTime(), System.lineSeparator());
            Connection connection =
                    (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement statement =
                         connection.prepareStatement("insert into rabbit (created_date) values (?)")
            ) {
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

