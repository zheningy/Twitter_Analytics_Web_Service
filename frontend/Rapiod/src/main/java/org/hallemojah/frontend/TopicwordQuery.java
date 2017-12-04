package org.hallemojah.frontend;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;

import java.io.IOException;
import java.util.*;

import static java.util.Comparator.reverseOrder;

public class TopicwordQuery {

    static String zkAddr = "172.31.29.27";
    static TableName tableName = TableName.valueOf("topicWords");
    static Connection connHbase;
    static Police police = new Police();

    public TopicwordQuery() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.master", zkAddr + ":14000");
        conf.set("hbase.zookeeper.quorum", zkAddr);
        conf.set("hbase.zookeeper.property.clientport", "2181");
        connHbase = ConnectionFactory.createConnection(conf);
        //    System.out.println("connected");
    }

    public Query3Result query(String startTime, String endTime, String startUid, String endUid, int n1, int n2) throws IOException {
        Map<String, WordInfo> word_score = new HashMap<>();
        Map<String, TwitterInfo> tid_info = new HashMap<>();

        List<String> topicWordArray;
        List<Double> scoreArray;

        Scan scan = new Scan();
        List<Filter> list = new ArrayList<Filter>();
        byte[] aCol = Bytes.toBytes("time");
        byte[] bCol = Bytes.toBytes("wordlist");
        byte[] cCol = Bytes.toBytes("scorelist");
        byte[] dCol = Bytes.toBytes("tid");
        byte[] eCol = Bytes.toBytes("text");
        byte[] fCol = Bytes.toBytes("impactscore");
        byte[] bColFamily = Bytes.toBytes("data");
        scan.addColumn(bColFamily, aCol);
        scan.addColumn(bColFamily, bCol);
        scan.addColumn(bColFamily, cCol);
        scan.addColumn(bColFamily, dCol);
        scan.addColumn(bColFamily, eCol);
        scan.addColumn(bColFamily, fCol);
        scan.setCaching(10);
        // scan.setStartRow(Bytes.toBytes(startTime));
        // scan.setStopRow(Bytes.toBytes(Integer.toString(Integer.parseInt(endTime) + 1)));
        String tmp = startUid;
        for (int i = 0; i < 18 - startUid.length(); i++)
            tmp = '0' + tmp;
        startUid = tmp;
        //System.out.println(startUid);
        tmp = Long.toString(Long.parseLong(endUid) + 1);
        for (int i = 0; i < 18 - endUid.length(); i++)
            tmp = '0' + tmp;
        endUid = tmp;
        //      System.out.println(endUid);
        scan.setStartRow(Bytes.toBytes(startUid));
        scan.setStopRow(Bytes.toBytes(endUid));
        //  BinaryComparator comp1 = new BinaryComparator(Bytes.toBytes(endUid));
        Filter filter1 = new SingleColumnValueFilter(bColFamily, aCol, CompareFilter.CompareOp.LESS_OR_EQUAL, Bytes.toBytes(endTime));
        //    BinaryComparator comp2 = new BinaryComparator(Bytes.toBytes(startUid));
        Filter filter2 = new SingleColumnValueFilter(bColFamily, aCol, CompareFilter.CompareOp.GREATER_OR_EQUAL, Bytes.toBytes(startTime));
        list.add(filter1);
        list.add(filter2);
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL, list);
        scan.setFilter(filterList);

        Table bizTable = connHbase.getTable(tableName);
        ResultScanner rs = bizTable.getScanner(scan);
        bizTable.close();
        int tweet_cnt = 0;
        for (Result r = rs.next(); r != null; r = rs.next()) {

            String uid = Bytes.toString(r.getValue(bColFamily, aCol));
//            System.out.println(uid);
//    if (Long.parseLong(uid) < Long.parseLong(startUid) || Long.parseLong(uid) > Long.parseLong(endUid)) continue;
            tweet_cnt += 1;
            String topicWords = Bytes.toString(r.getValue(bColFamily, bCol));
            if (topicWords == null || topicWords == "") continue;
            topicWordArray = Arrays.stream(topicWords.split(",")).map(String::toLowerCase).collect(toList());
            String scores = Bytes.toString(r.getValue(bColFamily, cCol));
            scoreArray = Arrays.stream(scores.split(",")).map(Double::parseDouble).collect(toList());
            String tid = Bytes.toString(r.getValue(bColFamily, dCol));
            String text = Bytes.toString(r.getValue(bColFamily, eCol));
            String impactScore_tmp = Bytes.toString(r.getValue(bColFamily, fCol));
            int impactScore = Integer.parseInt(impactScore_tmp);
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
            twitterInfo.text = text;
            twitterInfo.impactScore = impactScore;
            tid_info.put(tid, twitterInfo);
        }
        for (WordInfo w : word_score.values()) {
            w.getFinalScore(tweet_cnt);
        }

        List<String> resWords = word_score.entrySet().stream()
                .sorted(
                        Map.Entry.<String, WordInfo>comparingByValue(reverseOrder())
                                .thenComparing(comparingByKey()))
                .limit(n1)
                .map(x -> x.getKey())
                .collect(toList());

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
                    resTweet.add(Integer.toString(tweet_info.impactScore) + '\t' + tweet_info.tid + '\t' + tweet_info.text.replace("\\n","\n").replace("\\r","\r"));
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

