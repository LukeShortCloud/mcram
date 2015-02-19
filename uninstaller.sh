#!/bin/sh
#MCRAM - Uninstaller - uninstaller.sh

echo "Removing RAM mounts..."
sudo chmod 666 /etc/fstab
sed -i '/'"$(head -n 1 ~/mcram/mount.uninstall)"'/d'
sudo chmod 644 /etc/fstab

#Delete MCRAM files
echo "Removing all MCRAM files..."
sudo rm -rvf ~/mcram/ /usr/lib/systemd/system/mcram.service

#Remove cron if it exists
echo "Removing any possible crons set"
crontab -l | grep -v "/mcram/" | crontab -
