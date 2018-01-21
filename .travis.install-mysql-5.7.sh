echo mysql-apt-config mysql-apt-config/select-server select mysql-5.7 | sudo debconf-set-selections
wget https://dev.mysql.com/get/mysql-apt-config_0.7.3-1_all.deb
sudo dpkg --install mysql-apt-config_0.7.3-1_all.deb
sudo apt-get update -q
sudo apt-get install -q -y --allow-unauthenticated -o Dpkg::Options::=--force-confnew mysql-server
sudo mysql_upgrade -u root sudo service mysql restart
mysql -uroot -pmysql -e 'create database messagegateway;'
mysql -uroot -pmysql messagegateway < "src/main/resources/db/migration/V1__Initial_Setup.sql"