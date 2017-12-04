#!/usr/bin/env python3
"""
Tweet filter, Time transfer, Text censor, Score caculation
"""

import sys
import json
import re
import os 
import codecs
import time
import dateutil.parser
from collections import OrderedDict

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
    #create_time = int(time.mktime(time.strptime(info['created_at'], 
    #                                            "%a %b %d %H:%M:%S +0000 %Y")))


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

    # cacluate impact score
    word_pattern = r'[A-Za-z0-9\'-]*[A-Za-z][A-Za-z0-9\'-]*'
    keyword_list = re.findall(word_pattern, keyword_list)
    ewc = 0
    for keyword in keyword_list:
        keyword = keyword.lower()
        if keyword in stopWords_list:
            continue
        if keyword != "":
            ewc += 1

    impact_score = ewc*(int(info['favorite_count']) + 
                        int(info['retweet_count']) + 
                        int(info['user']['followers_count']))
    if (impact_score < 0):
        impact_score = 0

    
    result = OrderedDict([('tweet_id',str(info['id'])), 
                          ('created_at', str(create_time)), 
                          ('user_id', str(info['user']['id'])), 
                          ('censored_text', res_text),
                          ('impactScore', str(impact_score) )])
    print(json.dumps(result,separators=(',', ':'),ensure_ascii=False))


     