backdir=data/backup/$(date +"%Y-%m-%d_%H-%M-%S")
mkdir -p $backdir
sudo cp -r data/result $backdir/result
sudo cp -r data/running $backdir/running
sudo cp -r data/testConf $backdir/testConf

sudo rm -r data/result
sudo rm -r data/running
sudo rm -r data/testConf
sudo rm -r data/check
mkdir data/result
mkdir data/running
mkdir data/testConf
mkdir data/check