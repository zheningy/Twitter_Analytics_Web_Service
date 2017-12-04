#!/usr/bin/env bash

curl "http://localhost/read?uuid=a&seq=2" \
&& sleep 2 \ 
&& curl "http://localhost/read?uuid=a&seq=1" \
&& sleep 2 \
& curl "http://localhost/write?uuid=a&seq=4" \
& curl "http://localhost/write?uuid=a&seq=3" \
& curl "http://localhost/read?uuid=a&seq=5" \
& curl "http://localhost/read?uuid=a&seq=6" \
& curl "http://localhost/read?uuid=b&seq=1" \
& curl "http://localhost/read?uuid=b&seq=2" \
& curl "http://localhost/write?uuid=b&seq=3" \
