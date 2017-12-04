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

import static java.util.Comparator.reverseOrder;


public class KeywordQueryMysql {

    HikariDataSource dataSource;

    class DBValue {
        public List<String> userHashTags;
        public List<Integer> userScores;

        public DBValue(List<String> userHashTags, List<Integer> userScores) {
            this.userHashTags = userHashTags;
            this.userScores = userScores;
        }
    }

    public KeywordQueryMysql(SqlSource commonSource) throws IOException, SQLException, ClassNotFoundException {
        dataSource = commonSource.getSource();
    }

    public List<String> query(String keyWords, String userId, int cap) throws IOException, SQLException {

        Map<String, Integer> hashtag_map = new HashMap<String, Integer>();
        String keywordQuery = Arrays.stream(keyWords.split(",")).map(s -> "'"+s+"'").collect(Collectors.joining( "," ));
        String keywordUidQuery = Arrays.stream(keyWords.split(",")).map(s -> "'"+s+"_"+userId+"'").collect(Collectors.joining( "," ));

        Connection conn = dataSource.getConnection();

            List<String> userHashtagArray;
            List<Integer> userScoreArray;
            {
                String sql_str = "SELECT hashtags, scores FROM userKeyWords WHERE Keyword_Uid IN ("+keywordUidQuery+")";
                PreparedStatement stmt = conn.prepareStatement(sql_str);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String userHashTag = new String(rs.getBytes("hashtags"), StandardCharsets.UTF_8);
                    userHashtagArray = Arrays.stream(userHashTag.split(",")).map(String::toLowerCase).collect(toList());
                    String userScore = new String(rs.getBytes("scores"), StandardCharsets.UTF_8);
                    userScoreArray = Arrays.stream(userScore.split(",")).map(Integer::parseInt).collect(toList());
                    for (int j = 0; j < userHashtagArray.size(); j++) {
                        String tag = userHashtagArray.get(j);
                        if (hashtag_map.containsKey(tag)) {
                            hashtag_map.put(tag, userScoreArray.get(j) + hashtag_map.get(tag));
                        } else {
                            hashtag_map.put(tag, userScoreArray.get(j));
                        }
                    }
                }
                rs.close();
                stmt.close();
            }

            List<String> allHashtagArray;
            List<Integer> allScoreArray;

            {
                String sql_str = "SELECT hashtags, scores FROM allKeyWords WHERE Keyword IN ("+keywordQuery+")";
                PreparedStatement stmt = conn.prepareStatement(sql_str);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String userHashTag = new String(rs.getBytes("hashtags"), StandardCharsets.UTF_8);
                    allHashtagArray = Arrays.stream(userHashTag.split(",")).map(String::toLowerCase).collect(toList());
                    String userScore = new String(rs.getBytes("scores"), StandardCharsets.UTF_8);
                    allScoreArray = Arrays.stream(userScore.split(",")).map(Integer::parseInt).collect(toList());
                    for (int j = 0; j < allHashtagArray.size(); j++) {
                        String tag = allHashtagArray.get(j);
                        if (hashtag_map.containsKey(tag)) {
                            hashtag_map.put(tag, allScoreArray.get(j) + hashtag_map.get(tag));
                        } else {
                            hashtag_map.put(tag, allScoreArray.get(j));
                        }
                    }
                }
                rs.close();
                stmt.close();
            }
        conn.close();
        // http://www.mkyong.com/java8/java-8-how-to-sort-a-map/
        return hashtag_map.entrySet().stream()
                .sorted(
                        Map.Entry.<String, Integer>comparingByValue(reverseOrder())
                                .thenComparing(comparingByKey()))
                .limit(cap)
                .map(Map.Entry::getKey)
                .map(x -> '#' + x)
                .collect(toList());
    }
}
