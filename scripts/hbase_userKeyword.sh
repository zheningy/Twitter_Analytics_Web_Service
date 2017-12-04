cd ~
mkdir userkeywords_db
cd userkeywords_db
wget https://storage.googleapis.com/phase2/keyword_uid_db/part-00000 & wget https://storage.googleapis.com/phase2/keyword_uid_db/part-00001 & wget https://storage.googleapis.com/phase2/keyword_uid_db/part-00002 & wget https://storage.googleapis.com/phase2/keyword_uid_db/part-00003 & wget https://storage.googleapis.com/phase2/keyword_uid_db/part-00004 & wget https://storage.googleapis.com/phase2/keyword_uid_db/part-00005 & wget https://storage.googleapis.com/phase2/keyword_uid_db/part-00006
echo 'Successfully downloaded the data'
hadoop fs -mkdir /user/hadoop/userkeywords_db/
hadoop fs -put * /user/hadoop/userkeywords_db/
echo 'Successfully put the files in HDFS'
hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.columns=HBASE_ROW_KEY,data:hashtags,data:scores -Dimporttsv.bulk.output=/user/hadoop/user-hfiles userKeyWords /user/hadoop/userkeywords_db/
hadoop fs -chown -R hbase:hbase /user/hadoop/user-hfiles
hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles /user/hadoop/user-hfiles userKeyWords
echo 'Successfully loaded into HBase'