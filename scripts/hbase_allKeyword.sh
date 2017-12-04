cd ~
mkdir allkeywords_db
cd allkeywords_db
wget https://storage.googleapis.com/phase2/keyword_db/part-00000 & wget https://storage.googleapis.com/phase2/keyword_db/part-00001 & wget https://storage.googleapis.com/phase2/keyword_db/part-00002 & wget https://storage.googleapis.com/phase2/keyword_db/part-00003 & wget https://storage.googleapis.com/phase2/keyword_db/part-00004 & wget https://storage.googleapis.com/phase2/keyword_db/part-00005 & wget https://storage.googleapis.com/phase2/keyword_db/part-00006
echo 'Successfully downloaded the data'
hadoop fs -mkdir /user/hadoop/allkeywords_db/
hadoop fs -put * /user/hadoop/allkeywords_db/
echo 'Successfully put the files in HDFS'
hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.columns=HBASE_ROW_KEY,data:hashtags,data:scores -Dimporttsv.bulk.output=/user/hadoop/all-hfiles allKeyWords /user/hadoop/allkeywords_db/
hadoop fs -chown -R hbase:hbase /user/hadoop/all-hfiles
hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles /user/hadoop/all-hfiles allKeyWords
echo 'Successfully loaded into HBase'