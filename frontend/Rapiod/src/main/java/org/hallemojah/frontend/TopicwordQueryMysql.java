package org.hallemojah.frontend;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toList;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.lang.Math;

import static java.util.Comparator.reverseOrder;

public class TopicwordQueryMysql {
    Police police = new Police();
    HikariDataSource dataSource;

    public TopicwordQueryMysql(SqlSource commonSource) throws IOException, SQLException, ClassNotFoundException {
        dataSource = commonSource.getSource();
    }

    public Query3Result query(String time_start, String time_end, String uid_start, String uid_end, int n1, int n2) throws IOException, SQLException {

        Map<String, WordInfo> word_score = new HashMap<>();
        Map<String, TwitterInfo> tid_info = new HashMap<>();


        Connection conn = dataSource.getConnection();
        List<String> topicWordArray;
        List<Double> scoreArray;

        int tweet_cnt = 0;
        {
            String sql_str = "SELECT tid, topicwords, scores, impscore,txt FROM topicWords WHERE timeid between  ? and  ? and uid between ? and  ?";
            PreparedStatement stmt = conn.prepareStatement(sql_str);
            stmt.setString(1, time_start);
            stmt.setString(2, time_end);
            stmt.setString(3, uid_start);
            stmt.setString(4, uid_end);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tweet_cnt += 1;
                String topicWords = new String(rs.getBytes("topicwords"), StandardCharsets.UTF_8);
                if (topicWords.isEmpty()) continue;
                topicWordArray = Arrays.stream(topicWords.split(",")).map(String::toLowerCase).collect(toList());
                String scores = new String(rs.getBytes("scores"), StandardCharsets.UTF_8);
                scoreArray = Arrays.stream(scores.split(",")).map(Double::parseDouble).collect(toList());
                String tid = new String(rs.getBytes("tid"), StandardCharsets.UTF_8);
                for (int j = 0; j < topicWordArray.size(); j++) {
                    String word = topicWordArray.get(j);
                    if (word_score.containsKey(word)) {
                        word_score.get(word).updateScore(scoreArray.get(j));
                        word_score.get(word).updateTid(tid);
                    } else {
                        WordInfo wordInfo = new WordInfo(word);
                        wordInfo.updateTid(tid);
                        wordInfo.updateScore(scoreArray.get(j));
                        word_score.put(word, wordInfo);
                    }
                }
                TwitterInfo twitterInfo = new TwitterInfo(tid);
                twitterInfo.text = new String(rs.getBytes("txt"), StandardCharsets.UTF_8);
                String tmpScore = new String(rs.getBytes("impscore"), StandardCharsets.UTF_8);
                twitterInfo.impactScore = Integer.parseInt(tmpScore);
                tid_info.put(tid, twitterInfo);

            }
            rs.close();
            stmt.close();
        }

        conn.close();

        // calculate TF-IDF and get highest topword by sorting
        for (WordInfo w : word_score.values()) {
            w.getFinalScore(tweet_cnt);
        }


//        for (Map.Entry<String, Double> entry : topicWord_score.entrySet()) {
//            String key = entry.getKey();
//            Double value = entry.getValue();
//            entry.setValue(value * Math.log((double)tweet_cnt/(double)topicWord_cnt.get(key)));
//        }
        List<String> resWords = word_score.entrySet().stream()
                .sorted(
                        Map.Entry.<String, WordInfo>comparingByValue(reverseOrder())
                                .thenComparing(comparingByKey()))
                .limit(n1)
                .map(x -> x.getKey())
                .collect(toList());

        // Pick tweets
        List<String> candTids = tid_info.entrySet().stream()
                .sorted(
                        Map.Entry.<String, TwitterInfo>comparingByValue(reverseOrder())
                                .thenComparing(comparingByKey()))
                .map(x -> x.getKey())
                .collect(toList());

        List<String> resTweet = new ArrayList<>();
        int num_tweet = 0;
        for (String now_tid : candTids) {
            if (num_tweet >= n2) break;
            for (String resWord : resWords) {
                if (word_score.get(resWord).tids_has_me.contains(now_tid)) {
                    num_tweet += 1;
                    TwitterInfo tweet_info = tid_info.get(now_tid);
                    resTweet.add(Integer.toString(tweet_info.impactScore) + '\t' + tweet_info.tid + '\t' + tweet_info.text);
                    break;
                }
            }
        }

        List<String> wordResult = word_score.entrySet().stream()
                .sorted(
                        Map.Entry.<String, WordInfo>comparingByValue(reverseOrder())
                                .thenComparing(comparingByKey()))
                .limit(n1)
                .map(x -> police.purify(x.getKey()) + ":" + String.format("%.2f", x.getValue().final_score))
                .collect(toList());

        Query3Result result = new Query3Result(wordResult, resTweet);
        return result;
    }
}
