cp mysql_allKeywords.txt ~/
cd ~
mkdir allkeywords_db
cd allkeywords_db
wget https://storage.googleapis.com/phase2/keyword_db/part-00000 & wget https://storage.googleapis.com/phase2/keyword_db/part-00001 & wget https://storage.googleapis.com/phase2/keyword_db/part-00002 & wget https://storage.googleapis.com/phase2/keyword_db/part-00003 & wget https://storage.googleapis.com/phase2/keyword_db/part-00004 & wget https://storage.googleapis.com/phase2/keyword_db/part-00005 & wget https://storage.googleapis.com/phase2/keyword_db/part-00006
mysql -ucc -pawesome < ../mysql_allKeywords.txt