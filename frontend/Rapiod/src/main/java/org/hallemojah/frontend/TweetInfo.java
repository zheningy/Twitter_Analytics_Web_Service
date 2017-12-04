package org.hallemojah.frontend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TweetInfo implements Comparable<TweetInfo> {
    public String tid;
    public String timeid;
    public String uid;
    public String username;
    public String text;
    public int favorite_cnt;
    public int retweet_cnt;
    public int score;


    public TweetInfo(String tid, String timeid, String uid, String username, String text, int favorite_cnt, int retweet_cnt) {
        this.tid = tid;
        this.timeid = timeid;
        this.uid = uid;
        this.username = username;
        this.text = text;
        this.favorite_cnt = favorite_cnt;
        this.retweet_cnt = retweet_cnt;
        this.score = favorite_cnt + retweet_cnt;
    }

    public String showInfo() {
        String result = this.tid + '\t' + this.timeid + '\t' + this.uid + '\t' +
                this.username + '\t' + this.text + '\t' + Integer.toString(this.favorite_cnt) +
                '\t' + Integer.toString(this.retweet_cnt);
        return result;
    }

    @Override
    public int compareTo(TweetInfo other) {
        if (Integer.compare(score, other.score) != 0) {
            return Integer.compare(score, other.score);
        } else {
            return -tid.compareTo(other.tid);
        }
    }
}