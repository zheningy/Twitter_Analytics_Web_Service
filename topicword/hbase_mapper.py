#!/usr/bin/env python3
"""
time_Tweet id, uid, candiate topic word list, ln(impact score+1) * tf, impact score, censored txt
"""

import sys
import json
import re
import os 
import codecs
import time
import math
import dateutil.parser
from decimal import Decimal
from collections import OrderedDict

# turn int to base36
def base36encode(number, alphabet='0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'):
    """Converts an integer to a base36 string."""
    base36 = ''
    sign = ''

    if number < 0:
        sign = '-'
        number = -number

    if 0 <= number < len(alphabet):
        return sign + alphabet[number]

    while number != 0:
        number, i = divmod(number, len(alphabet))
        base36 = alphabet[i] + base36

    return sign + base36

# set timezone
os.environ['TZ'] = 'EST+00EDT,M4.1.0,M10.5.0'
time.tzset()

prev_id = 0
main_dir = os.getcwd()
stopWords_file = os.path.join(main_dir, "stopwords")
stopWords_list = []
with open(stopWords_file, "rb") as f:
    for i in f.readlines():
        word = i.strip();
        stopWords_list.append(word.decode('utf-8'))

stopWords_list = tuple(stopWords_list)

bannedWords_file = os.path.join(main_dir, "bannedwords")
bannedWords_list = []
with open(bannedWords_file, "rb") as f:
    for i in f.readlines():
        tem = i.strip().decode('utf-8')
        word = codecs.encode(tem, 'rot_13');
        bannedWords_list.append(word)

bannedWords_list = tuple(bannedWords_list)

for line in sys.stdin:
    info = json.loads(line);
    # id filter
    if ( ('id' not in info) or ('id_str' not in info) or info['id'] == "" or info['id_str'] == "" ):
        continue

    # Assume tweets with same id are adjacent. Might be wrong
    if prev_id == info['id']:
        continue
    prev_id = info['id']

    # user id filter 
    if (('id' not in info['user']) or info['user']['id'] == ""):
        continue

    # create at filter
    if (('created_at' not in info) or info['created_at'] == ""):
        continue

    # text filter
    if (('text' not in info) or info['text'] == ""):
        continue

    # lang filter
    if (('lang' not in info) or info['lang'] != "en"):
        continue

    # transfer to timestamp
    dt = dateutil.parser.parse(info['created_at'])
    create_time = int(time.mktime(dt.timetuple()))

    # text censor
    res_text = info['text']
    normal_pattern = r'[A-Za-z0-9]+'
    word_list = re.findall(normal_pattern, res_text)
    for word in word_list:
        curr_word = word.lower()
        if curr_word in bannedWords_list:
            tem_list = list(word)
            for x in range(1,len(tem_list)-1):
                tem_list[x] = '*'
            sub_word = ''.join(tem_list)
            word = r'(?:(?<=\b)|(?<=_)){0}(?:(?=\b)|(?=_))'.format(word)
            res_text = re.sub(word,sub_word,res_text)
            
    # short url substitue
    url_pattern  = r'(https?|ftp):\/\/[^\t\r\n /$.?#][^\t\r\n ]*'
    keyword_list = re.sub(url_pattern,"",info['text'])

    # caculate impact score and tf score
    word_pattern = r'[A-Za-z0-9\'-]*[A-Za-z][A-Za-z0-9\'-]*'
    keyword_list = re.findall(word_pattern, keyword_list)
    ewc = 0
    twc = 0
    tf_map = {}
    for keyword in keyword_list:
        twc += 1
        keyword = keyword.lower()
        if keyword in stopWords_list:
            continue
        if keyword != "":
            ewc += 1
            if keyword in tf_map:
                tf_map[keyword] += 1
            else:
                tf_map[keyword] = 1

    impact_score = ewc*(int(info['favorite_count']) + 
                        int(info['retweet_count']) + 
                        int(info['user']['followers_count']))
    if (impact_score < 0):
        impact_score = 0

    ln_imp_score = math.log(impact_score + 1)

    # caculate intermediate score of each candiate topic word
    topicword_list = []
    score_list = []
    for k,v in tf_map.items():
        topicword_list.append(k)
    for k,v in tf_map.items():
        score_list.append(str(round(v/twc*ln_imp_score,4)))

    res_topicword = ",".join(topicword_list)
    res_score = ",".join(score_list)

    # fullfill uid 
    uid = info['user']['id_str']
    length = len(uid)
    if (length < 18):
        for i in range(18 - length):
            uid = "0" + uid


    # create rowkey

    tid = base36encode(int(info['id']))
    res_time = str(create_time)
    uid_tid = uid + "_" + tid

    #time_tid = str(create_time) + "_" + tid

    res_text = res_text.replace("\n", "\\n").replace("\r","\\r")
    

    
    print ('{}\t{}\t{}\t{}\t{}\t{}\t{}'.format(uid_tid,
                                   str(info['id']),  
                                   res_time,
                                   res_topicword,
                                   res_score,
                                   impact_score,
                                   res_text))



