#!/usr/bin/env python3
"""
Record keyword and tweet id
"""

import sys
import json
import re
import regex
import os 


prev_id = 0
lang_list = ('ar', 'en', 'fr', 'in', 'pt', 'es', 'tr')

main_dir = os.getcwd()
stopWords_file = os.path.join(main_dir, "stopwords")
stopWords_list = []
with open(stopWords_file, "rb") as f:
    for i in f.readlines():
        word = i.strip();
        stopWords_list.append(word.decode('utf-8'))

stopWords_list = tuple(stopWords_list)

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
    if (('lang' not in info) or info['lang'] == ""):
        continue

    # hashtag filter
    if ('hashtags' not in info['entities']):
        continue

    hashtag_list = info['entities']['hashtags']
    hashtag_str = []
    if (len(hashtag_list) == 0):
        continue
    for hashtag in hashtag_list:
        hashtag_str.append(hashtag['text'])
    hashtag_res = "\",\"".join(hashtag_str)


    # language filter
    if info['lang'] not in lang_list:
        continue

    # short url substitue
    url_pattern  = r'(https?|ftp):\/\/[^\t\r\n /$.?#][^\t\r\n ]*'
    keyword_list = re.sub(url_pattern,"",info['text'])

    # extract keyword
    #keyword_pattern = ur'^[\W][\\u0600-\\u06FF\\s\\w\\u1B00-\\u1B7F]+[\W]$'
    keyword_list = regex.findall('(?<!\p{L})\p{L}+(?!\p{L})', keyword_list)
    keyword_str = []
    for keyword in keyword_list:
        keyword = keyword.lower()
        if keyword != "":
            keyword_str.append(keyword)

    keyword_res = ",".join(keyword_str)

    print ('{{"uid":"{}","hashtags":["{}"],"text":"{}","tid":"{}"}}'.format(info['user']['id'],hashtag_res,keyword_res,info['id']))
