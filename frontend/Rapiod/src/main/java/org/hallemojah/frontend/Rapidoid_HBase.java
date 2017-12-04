package org.hallemojah.frontend;

import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Rapidoid_HBase {

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {

        App.bootstrap(args);

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

        
        KeywordQuery keywordStore = new KeywordQuery();
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
        
        TopicwordQuery topicwordStore = new TopicwordQuery();
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
    }
}

