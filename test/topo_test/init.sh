mkdir -p data/backup
sudo cp -r data/result data/backup/result
sudo cp -r data/running data/backup/running
sudo cp -r data/testConf data/backup/testConf
sudo cp -r data/check data/backup/check

sudo rm -r data/result
sudo rm -r data/running
sudo rm -r data/testConf
sudo rm -r data/check
mkdir data/result
mkdir data/running
mkdir data/testConf
mkdir data/check