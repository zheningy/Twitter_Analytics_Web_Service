package org.hallemojah.frontend;

import org.apache.commons.lang.ObjectUtils;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;

import java.sql.*;
import java.io.IOException;
import java.util.List;

public class Rapidoid {

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {

        App.bootstrap(args);

        SqlSource commonSource = new SqlSource();

        On.get("/q1").html((Req req) -> {
            String type = req.param("type");
            String data = req.param("data");
            String result;
            if (type.equals("encode")){
                result = Decoder.enAndDe("encode", data);
            } else if (type.equals("decode")){
                result = Decoder.enAndDe("decode", data);
            } else {
                result = "Invalid";
            }
            Resp resp = req.response();
            resp.contentType(MediaType.PLAIN_TEXT_UTF_8);
            resp.result(result);
            return resp;
        });
        KeywordQueryMysql keywordStore = new KeywordQueryMysql(commonSource);
        On.get("/q2").html((Req req) -> {
            String keywords = req.param("keywords");
            String user_id = req.param("user_id");
            int n = Integer.parseInt(req.param("n"));
            List<String> recommendation = keywordStore.query(keywords, user_id, n);
            String prefix_info = "HalleMojah,9838-1738-3880";
            String recommend_tags =  String.join(",", recommendation);
            String result = prefix_info +  '\n' + recommend_tags + '\n';
            Resp resp = req.response();
            resp.contentType(MediaType.PLAIN_TEXT_UTF_8);
            resp.result(result);
            return resp;
        });

        TopicwordQueryMysql topicwordStore = new TopicwordQueryMysql(commonSource);
        On.get("/q3").html((Req req) -> {
            String uid_start = req.param("uid_start");
            String uid_end = req.param("uid_end");
            String time_start = req.param("time_start");
            String time_end = req.param("time_end");
            int n1 = Integer.parseInt(req.param("n1"));
            int n2 = Integer.parseInt(req.param("n2"));
            Query3Result recommendation = topicwordStore.query(time_start, time_end, uid_start, uid_end, n1, n2);
            String prefix_info = "HalleMojah,9838-1738-3880";
            String recommend_words =  String.join("\t", recommendation.wordResult);
            String recommend_tweets = String.join("\n", recommendation.textResult);
            String result = prefix_info +  '\n' + recommend_words + '\n' + recommend_tweets;
            Resp resp = req.response();
            resp.contentType(MediaType.PLAIN_TEXT_UTF_8);
            resp.result(result);
            return resp;
        });

        Query4 query4Store = new Query4();
        On.get("/q4").html((Req req) -> {
            String op = req.param("op");
            String result = "something";
            switch (op) {
                case "read": {
                    String uid1 = req.param("uid1");
                    String uid2 = req.param("uid2");
                    int n = Integer.parseInt(req.param("n"));
                    String uuid = req.param("uuid");
                    int seq = Integer.parseInt(req.param("seq"));
                    List<String> read_res = query4Store.read_query(uid1, uid2, n, uuid, seq);
                    result = String.join("\n", read_res);
                    break;
                }
                case "write": {
                    String payload = req.param("payload");
                    String uuid = req.param("uuid");
                    int seq = Integer.parseInt(req.param("seq"));
                    result = query4Store.write_query(payload, uuid, seq);
                    break;
                }
                case "set": {
                    String field = req.param("field");
                    String tid = req.param("tid");
                    String payload = req.param("payload");
                    String uuid = req.param("uuid");
                    int seq = Integer.parseInt(req.param("seq"));
                    result = query4Store.set_query(field, tid, payload, uuid, seq);
                    break;
                }
                case "delete": {
                    String tid = req.param("tid");
                    String uuid = req.param("uuid");
                    int seq = Integer.parseInt(req.param("seq"));
                    result = query4Store.delete_query(tid, uuid, seq);
                    break;
                }
                default:
                    System.out.println("???");
                    break;
            }
            ;

            String prefix_info = "HalleMojah,9838-1738-3880";
            result = prefix_info + '\n' + result + '\n';
            Resp resp = req.response();
            resp.contentType(MediaType.PLAIN_TEXT_UTF_8);
            resp.result(result);
            return resp;
        });
    }
}
