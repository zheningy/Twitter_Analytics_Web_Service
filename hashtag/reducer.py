#!/usr/bin/env python3
"""
Reducer based on tweet_id
"""

import sys
from collections import defaultdict

current_keyword_uid = None
keyword_uid = None

current_counter = defaultdict(int)

for line in sys.stdin:

    line = line.strip()
    keyword_uid, hashtags_str = line.split('\t')
    hashtags = hashtags_str.split(',')
    # this IF-switch only works because Hadoop sorts map output
    # by key (here: word) before it is passed to the reducer
    if current_keyword_uid == keyword_uid:
        for tag in hashtags:
            current_counter[tag]+=1
    else:
        if current_keyword_uid:
            # write result to STDOUT
            print('{}\t{}\t{}'.format(current_keyword_uid, ','.join(current_counter.keys()), ','.join([str(x) for x in current_counter.values()])))
        current_counter = defaultdict(int)
        for tag in hashtags:
            current_counter[tag]+=1
        current_keyword_uid = keyword_uid

# do not forget to output the last keyword if needed!
print('{}\t{}\t{}'.format(current_keyword_uid, ','.join(current_counter.keys()), ','.join([str(x) for x in current_counter.values()])))
