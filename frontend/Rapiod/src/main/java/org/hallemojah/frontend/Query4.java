package org.hallemojah.frontend;

import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toList;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import com.google.gson.Gson;
import java.util.stream.Collectors;

import static java.util.Comparator.reverseOrder;
import static org.hallemojah.frontend.LockUtils.*;


public class Query4 {
    String[] URLs = {
            "localhost",
            "ec2-34-236-254-152.compute-1.amazonaws.com",
            "ec2-34-233-71-184.compute-1.amazonaws.com",
            "ec2-34-237-140-122.compute-1.amazonaws.com",
            "ec2-34-237-138-43.compute-1.amazonaws.com",
            "ec2-34-239-112-80.compute-1.amazonaws.com",
            "ec2-34-200-246-51.compute-1.amazonaws.com"
    };
    String DB_USER = "cc";
    String DB_PWD = "awesome";
    String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    List<HikariDataSource> dataSources;
    class DBValue {
        public List<String> userHashTags;
        public List<Integer> userScores;

        public DBValue(List<String> userHashTags, List<Integer> userScores) {
            this.userHashTags = userHashTags;
            this.userScores = userScores;
        }
    }

    public HikariDataSource getSource(int n){
//        int robin  = ThreadLocalRandom.current().nextInt(0, URLs.length);
//        return dataSources.get(robin);
        return dataSources.get(n);
    }

    public Query4() throws IOException, SQLException, ClassNotFoundException {
        dataSources = Arrays.stream(URLs).map( x -> {
            HikariConfig config = new HikariConfig();
            try {
                Class.forName(JDBC_DRIVER);
            } catch (ClassNotFoundException e) {
                System.out.println("Ohhhhh");
            }
            config.setJdbcUrl("jdbc:mysql://"+x+"/twitter?useUnicode=true&character_set_server=utf8mb4&characterEncoding=utf-8");
            config.setUsername(DB_USER);
            config.setPassword(DB_PWD);
            config.setMaximumPoolSize(50);
            config.setConnectionTimeout(500);
            config.setConnectionInitSql("SET NAMES utf8mb4");
            return new HikariDataSource(config);
        }).collect(toList());

    }

    public List<String> read_query(String uid_start, String uid_end, int n, String uuid, int sequence_number) throws IOException, SQLException {

        final LockUtils.lockStatusCV bundle = addToOp(uuid, sequence_number, true);
        acquire_lock_read(bundle, sequence_number);

        ArrayList<TweetInfo> tweetList = new ArrayList<>();

        Connection conn = getSource(choose_sql(uuid)).getConnection();

        String sql_str = "SELECT tid, timeid, uid, username, txt, favorite_cnt, retweet_cnt FROM tweetData WHERE uid between  ? and  ? ";
        PreparedStatement stmt = conn.prepareStatement(sql_str);
        stmt.setString(1, uid_start);
        stmt.setString(2, uid_end);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String tid = new String(rs.getBytes("tid"), StandardCharsets.UTF_8);
            String timeid = new String(rs.getBytes("timeid"), StandardCharsets.UTF_8);
            String uid = new String(rs.getBytes("uid"), StandardCharsets.UTF_8);
            String username = new String(rs.getBytes("username"), StandardCharsets.UTF_8);
            String txt = new String(rs.getBytes("txt"), StandardCharsets.UTF_8);
            int favorite_cnt = Integer.parseInt(new String(rs.getBytes("favorite_cnt"), StandardCharsets.UTF_8));
            int retweet_cnt = Integer.parseInt(new String(rs.getBytes("retweet_cnt"), StandardCharsets.UTF_8));
            TweetInfo tweetinfo = new TweetInfo(tid, timeid, uid, username, txt, favorite_cnt, retweet_cnt);
            tweetList.add(tweetinfo);
        }
        rs.close();

        stmt.close();
        conn.close();

        release_lock(bundle, sequence_number);

        tweetList.sort(Collections.reverseOrder());
        List<String> resTweet = new ArrayList<>();
        for (int i = 0; i < n && i <tweetList.size(); ++i){
            resTweet.add(tweetList.get(i).showInfo());
        }
        return resTweet;
    }

    public String write_query(String payload, String uuid, int sequence_number) throws IOException, SQLException {

        final LockUtils.lockStatusCV bundle = addToOp(uuid, sequence_number, false);
        acquire_lock_write(bundle, sequence_number);

        Connection conn = getSource(choose_sql(uuid)).getConnection();
        JsonObject loadData = new Gson().fromJson(payload, JsonObject.class);
        // Load writing data from payload
        long tid = loadData.get("id").getAsLong();
        String timeid = loadData.get("created_at").getAsString();
        long uid = loadData.getAsJsonObject("user").get("id").getAsLong();
        String username = loadData.getAsJsonObject("user").get("screen_name").getAsString();
        String txt = loadData.get("text").getAsString();
        int favorite_cnt = loadData.get("favorite_count").getAsInt();
        int retweet_cnt = loadData.get("retweet_count").getAsInt();

        String sql_str = "REPLACE INTO tweetData (tid, timeid, uid, username, txt, favorite_cnt, retweet_cnt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql_str);
        stmt.setLong(1,tid);
        stmt.setString(2,timeid);
        stmt.setLong(3,uid);
        stmt.setString(4,username);
        stmt.setString(5,txt);
        stmt.setInt(6,favorite_cnt);
        stmt.setInt(7,retweet_cnt);
        stmt.execute();
        stmt.close();
        conn.close();

        release_lock(bundle, sequence_number);

        return "success";
    }

    public String set_query(String field, String tid, String payload, String uuid, int sequence_number) throws IOException, SQLException {

        final LockUtils.lockStatusCV bundle = addToOp(uuid, sequence_number, false);
        acquire_lock_write(bundle, sequence_number);

        Connection conn = getSource(choose_sql(uuid)).getConnection();
        if (field.equals("text")) {
            String sql_str = "UPDATE tweetData SET txt = ? WHERE tid = ?";
            PreparedStatement stmt = conn.prepareStatement(sql_str);
            stmt.setString(1,payload);
            stmt.setString(2,tid);
            stmt.execute();
            stmt.close();
        }
        else if (field.equals("favorite_count")) {
            int f_cnt = Integer.parseInt(payload);
            String sql_str = "UPDATE tweetData SET favorite_cnt = ? WHERE tid = ?";
            PreparedStatement stmt = conn.prepareStatement(sql_str);
            stmt.setInt(1,f_cnt);
            stmt.setString(2,tid);

            stmt.execute();
            stmt.close();
        }
        else if (field.equals("retweet_count")) {
            int r_cnt = Integer.parseInt(payload);
            String sql_str = "UPDATE tweetData SET retweet_cnt = ? WHERE tid = ?";
            PreparedStatement stmt = conn.prepareStatement(sql_str);
            stmt.setInt(1,r_cnt);
            stmt.setString(2,tid);
            stmt.execute();
            stmt.close();
        }
        conn.close();

        release_lock(bundle, sequence_number);

        return "success";
    }

    public String delete_query(String tid, String uuid, int sequence_number) throws IOException, SQLException {

        final LockUtils.lockStatusCV bundle = addToOp(uuid, sequence_number, false);
        acquire_lock_write(bundle, sequence_number);

        Connection conn = getSource(choose_sql(uuid)).getConnection();

        String sql_str = "DELETE FROM tweetData WHERE tid = ?";
        PreparedStatement stmt = conn.prepareStatement(sql_str);
        stmt.setString(1,tid);
        stmt.execute();
        stmt.close();
        conn.close();

        release_lock(bundle, sequence_number);

        return "success";
    }

    public Integer choose_sql(String uuid) {
        String mark = uuid.substring(uuid.length() - 1);
        int num = Integer.parseInt(mark, 16);
        if (num >= 0 && num < 2) return 0;
        else if(num >= 2 && num < 4) return 1;
        else if(num >= 4 && num < 6) return 2;
        else if(num >= 6 && num < 8) return 3;
        else if(num >= 8 && num < 10) return 4;
        else if(num >= 10 && num < 13) return 5;
        else if(num >= 13 && num < 16) return 6;
        else return 7;
    }
}
