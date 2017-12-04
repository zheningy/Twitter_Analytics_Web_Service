package org.hallemojah.frontend;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;

import static java.util.Comparator.reverseOrder;

public class KeywordQuery {

    static String zkAddr = "172.31.29.27";
    static TableName tableName1 = TableName.valueOf("userKeyWords");
    static TableName tableName2 = TableName.valueOf("allKeyWords");
    static Connection connHbase;

    class DBValue {
        public List<String> userHashTags;
        public List<Integer> userScores;

        public DBValue(List<String> userHashTags, List<Integer> userScores) {
            this.userHashTags = userHashTags;
            this.userScores = userScores;
        }
    }

    public KeywordQuery() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.master", zkAddr + ":14000");
        conf.set("hbase.zookeeper.quorum", zkAddr);
        conf.set("hbase.zookeeper.property.clientport", "2181");
        connHbase = ConnectionFactory.createConnection(conf);
    }

    public List<String> query(String keyWords, String userId, int cap) throws IOException {

        final Table keyword_uid_table = connHbase.getTable(tableName1);

        byte[] cCol = Bytes.toBytes("hashtags");
        byte[] dCol = Bytes.toBytes("scores");
        byte[] bColFamily = Bytes.toBytes("data");

        Map<String, Integer> hashtag_map = new HashMap<String, Integer>();
        String[] keywordArray = keyWords.split(",");
        List<Get> keyword_uid_gets = new ArrayList<>();
        List<Get> keyword_gets = new ArrayList<>();

        for (String keyword : keywordArray) {

            String index = keyword + "\\_" + userId;

            byte[] bCol = Bytes.toBytes(index);
            Get get = new Get(bCol);
            get.addColumn(bColFamily, cCol);
            get.addColumn(bColFamily, dCol);

            keyword_uid_gets.add(get);

            byte[] eCol = Bytes.toBytes(keyword);
            Get getAll = new Get(eCol);
            getAll.addColumn(bColFamily, cCol);
            getAll.addColumn(bColFamily, dCol);

            keyword_gets.add(getAll);
        }

        List<String> userHashtagArray;
        List<Integer> userScoreArray;

        Result[] rs1 = keyword_uid_table.get(keyword_uid_gets);

        for (Result r : rs1) {
            if (!r.isEmpty()) {
                String userHashTag = Bytes.toString(r.getValue(bColFamily, cCol));
                userHashtagArray = Arrays.stream(userHashTag.split(",")).map(String::toLowerCase).collect(toList());
                String userScore = Bytes.toString(r.getValue(bColFamily, dCol));
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
        }
        keyword_uid_table.close();

        final Table keyword_table = connHbase.getTable(tableName2);

        List<String> allHashtagArray;
        List<Integer> allScoreArray;

        Result[] rs2 = keyword_table.get(keyword_gets);
        for (Result r : rs2) {
            if (!r.isEmpty()) {
                String userHashTag = Bytes.toString(r.getValue(bColFamily, cCol));
                allHashtagArray = Arrays.stream(userHashTag.split(",")).map(String::toLowerCase).collect(toList());
                String userScore = Bytes.toString(r.getValue(bColFamily, dCol));
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
        }
        keyword_table.close();
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
