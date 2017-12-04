package org.hallemojah.frontend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.lang.Math.abs;
import static java.lang.Math.log;

class WordInfo implements Comparable<WordInfo> {
    public String word;
    public double partial_score = 0;
    public ArrayList <String> tids_has_me = new ArrayList<>();
    public double final_score = 0;


    public WordInfo(String word) {
        this.word = word;
    }

    public void updateScore(double adder) {
        partial_score += adder;
    }

    public void updateTid(String tid){
        tids_has_me.add(tid);
    }

    public int getTidCount() {
        return tids_has_me.size();
    }

    public void getFinalScore(int total_count){
        final_score = partial_score * Math.log((double)total_count/(double)getTidCount());
        //return partial_score * Math.log((double)total_count/(double)getTidCount());
    }

//    @Override
//    public String toString() {
//        return "WordInfo{" +
//                "partial_score=" + partial_score +
//                ", tids_has_me=" + Arrays.toString(tids_has_me) +
//                '}';
//    }

    @Override
    public int compareTo(WordInfo other) {
        if (abs(final_score - other.final_score) > 0.00001) {
            return Double.compare(final_score, other.final_score);
        } else {
            return -word.compareTo(other.word);
        }
    }

}

class TwitterInfo implements Comparable<TwitterInfo> {
    public String tid;
    public int impactScore;
    public String text;

    public TwitterInfo(String tid) {
        this.tid = tid;
    }

    @Override
    public int compareTo(TwitterInfo other) {
        if (Integer.compare(impactScore, other.impactScore) != 0) {
            return Integer.compare(impactScore, other.impactScore);
        } else {
            return tid.compareTo(other.tid);
        }
    }
}
class Query3Result {
    public List<String> wordResult;
    public List<String> textResult;

    public Query3Result(List<String> wordResult, List<String> textResult) {
        this.wordResult = wordResult;
        this.textResult = textResult;
    }
}
