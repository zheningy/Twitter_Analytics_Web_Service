#!/bin/bash

# map phase
for f in $*; do
    export mapreduce_map_input_file=$f
    zcat $f | ./user_mapper.py >> mapout.$$
done
# reduce phase
LC_ALL=C sort -k1,1i mapout.$$ | ./reducer.py >> output
