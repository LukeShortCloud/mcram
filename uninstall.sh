#!/bin/sh
#MCRAM - Uninstaller - uninstall.sh

#Send commands to the screens to quit the sessions
echo "Removing active screens..."
screen -S mcrsync -X quit
screen -p 0 -S mcram -X eval 'stuff "save-all"\015';
sleep 5s;
screen -p 0 -S mcram -X eval 'stuff "stop"\015';
screen -S mcram -X quit

# BROKEN - automatic sed for removing the added MCRAM mounts; likely to be removed in a later version
#echo "Removing RAM mounts..."
#sudo chmod 666 /etc/fstab
#sed -i '/'"$(head -n 1 ~/mcram/mount.uninstall)"'/d' /etc/fstab
#sudo chmod 644 /etc/fstab
#sudo mount -a

echo "Removing RAM mounts..."
echo "Here are the differences between your original and current /etc/fstab"
diff -y /etc/fstab ~/mcram/etc-fstab
read -p "Would you like to restore the original /etc/fstab (Y/N)? " restorefstab;
if [[ $(echo ${restorefstab}) -eq "Y" || (echo ${restorefstab}) -eq "y" ]];
  then currentdate=$(date +%Y-%m-%d_%Hh.%Mm.%Ss)
  sudo mv -v /etc/fstab /etc/fstab${currentdate}
  sudo cp -v ~/mcram/etc-fstab /etc/fstab
  mount -a
fi

#Delete MCRAM files
echo "Removing all MCRAM files..."
rm -rvf ~/mcram/ /usr/lib/systemd/system/mcram.service

#Remove cron if it exists
echo "Removing any possible crons that were set..."
crontab -l | grep -v "/mcram/" | crontab -

echo "MCRAM has now be uninstalled."
