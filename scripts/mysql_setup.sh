sudo apt-get update
sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password password awesome'
sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password_again password awesome'
sudo apt-get -y install mysql-server byobu fish
sudo cp ../mysqld.cnf /etc/mysql/mysql.conf.d/mysqld.cnf
sudo service mysql restart
sudo /usr/sbin/update-rc.d mysql defaults
mysql -uroot -pawesome < mysql_setup.txt