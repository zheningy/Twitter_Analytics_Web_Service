This repository include all codes of the team project (Twitter Analytics Web Service) in Cloud Computing 15619, which consists three parts: **High Performance Web Service for Data Retrieval**, **High Performance Big Data Analytics with SQL and NoSQL**, **Fast Interactive Web Service**.

We implemented the frontend based on Rapiod, backend based on two type of databases(Hbase, MySQL). We also used Hikara connection pool to speed up the query speed for MySQL. All mapreduced data was processed on GCP Dataproc.

# Queries
Query 2,3,4 use 1T data from real Twitter data set.

* Query 1: Enconding and deconding specific versions of QR codes.

* Query 2: Predict hashtags for a new tweet by a particular user based on all tweets information.

* Query 3: Calculate the topic score of requested tweet(specific time range) based on TF-IDF algorithnms. Return highest topic words and tweets.

* Query 4: Implement fast interactive service based on MySQL, included **read**, **wirite**, **set** and **delete**.


# How to run
## Frontend
### Rapiod

### Install Java and Maven and byobu in Ubuntu
```
sudo apt-get update
sudo apt-get install -y byobu maven openjdk-8-jdk
```
### Run the code
```
cd HalleMojah/frontend/Rapiod
mvn clean package
sudo java -cp target/rapidoid-server.jar  org.hallemojah.frontend.Rapidoid
```
## Backend
### MySQL
```
cd HalleMojah/scripts
source mysql_all.sh
```
### HBase
```
source hbase_allKeyword.sh
source hbase_userKeyword.sh
source hbase_topicword.sh
```
### Install byobu in EMR
```
sudo yum install --enablerepo=epel byobu -y
```
