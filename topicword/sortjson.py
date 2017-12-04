#!/usr/bin/env python3
"""
Sort result based on tweet id
"""

import collections
import sys
import re

d = {}
for line in sys.stdin:
    c+=1 
    t_id = re.search(r'\d+',line)
    t_id = t_id.group(0)
    d[t_id] = line

for key, value in sorted(d.items()):
    print(value)