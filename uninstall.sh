#!/bin/sh
##MCRAM - Uninstaller - uninstall.sh

echo "MCRAM is now being uninstalled..."

#Send commands to the screens to quit the sessions
mcrsyncScreen=$(sudo screen -ls | grep -c "mcrsync")
if [[ ${mcrsyncScreen} > 1 ]]; 
	then echo "You have more than one 'mcrsync' screen. Please terminate these manually."
elif [[ ${mcrsyncScreen} == 0 ]];
	then echo "There is no 'mcrsync' screen running."
else screen -S mcrsync -X quit	
fi

mcramScreen=$(sudo screen -ls | grep -c "mcram")
if [[ ${mcramScreen} > 1 ]]; 
	then echo "You have more than one 'mcram' screen. Please terminate these manually."
elif [[ ${mcramScreen} == 0 ]];
	then echo "There is no 'mcram' screen running."
else\
	screen -p 0 -S mcram -X eval 'stuff "save-all"\015';
	sleep 5s;
	screen -p 0 -S mcram -X eval 'stuff "stop"\015';
	screen -S mcram -X quit
fi

if [ -f ~/mcram/etc-fstab ];
	then "Attempting to remove the RAM mounts..."
	echo "Here are the differences between your original and current /etc/fstab" 
	diff -y /etc/fstab ~/mcram/etc-fstab
	read -p "Would you like to restore the original /etc/fstab (Y/N)? " restorefstab;
	if [[ ${restorefstab} == "Y" || ${restorefstab} == "y" ]];
		then currentdate=$(date +%Y-%m-%d_%Hh.%Mm.%Ss)
		sudo mv -v /etc/fstab /etc/fstab${currentdate}
		sudo cp -v ~/mcram/etc-fstab /etc/fstab
		mount -a
	fi
fi

#Delete MCRAM files
echo "Removing all MCRAM files..."
rm -rf ~/mcram/ /usr/lib/systemd/system/mcram.service > /dev/null 2>&1

#Remove cron if it exists
if [[ $(type crontab 2>&1| grep -c "not found") == 0 ]];
	then echo "Removing any possible crons that were set..." 
	crontab -l | grep -v "/mcram/" | crontab -
fi

echo "MCRAM has now been uninstalled."
