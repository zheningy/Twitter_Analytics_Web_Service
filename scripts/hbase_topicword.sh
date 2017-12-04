cd ~
mkdir topicwords_db
cd topicwords_db
wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00000 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00001 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00002 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00003 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00004 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00005 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00006 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00007 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00008 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00009 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00010 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00011 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00012 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00013 & wget https://storage.googleapis.com/cc_phase2/hbaseDataNew/part-00014
echo 'Successfully downloaded the data'
hadoop fs -mkdir /user/hadoop/topicwords_db/
hadoop fs -put * /user/hadoop/topicwords_db/
echo 'Successfully put the files in HDFS'
hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.columns=HBASE_ROW_KEY,data:tid,data:uid,data:wordlist,data:scorelist,data:impactscore,data:text -Dimporttsv.bulk.output=/user/hadoop/topicword-hfiles topicWords /user/hadoop/topicwords_db/
hadoop fs -chown -R hbase:hbase /user/hadoop/topicword-hfiles
hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles /user/hadoop/topicword-hfiles topicWords
echo 'Successfully loaded into HBase'