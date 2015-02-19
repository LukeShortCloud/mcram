#!/bin/sh
#MCRAM - Uninstaller - uninstall.sh

#Send commands to the screens to quit the sessions
echo "Removing active screens..."
screen -S mcrsync -X quit
screen -p 0 -S mcram -X eval 'stuff "save-all"\015';
sleep 5s;
screen -p 0 -S mcram -X eval 'stuff "stop"\015';
screen -S mcram -X quit

echo "Removing RAM mounts..."
sudo chmod 666 /etc/fstab
sed -i '/'"$(head -n 1 ~/mcram/mount.uninstall)"'/d'
sudo chmod 644 /etc/fstab
sudo mount -a

#Delete MCRAM files
echo "Removing all MCRAM files..."
rm -rvf ~/mcram/ /usr/lib/systemd/system/mcram.service

#Remove cron if it exists
echo "Removing any possible crons that were set..."
crontab -l | grep -v "/mcram/" | crontab -

echo "MCRAM has now be uninstalled."
