#!/bin/sh
##MCRAM - Installer - install.sh
##0.5 BETA
mcramv="0.5.0"

echo -e "\e[1m \e[36m"; #Changes color to light blue 

req1="screen"; 
req2="sudo"; 

if [[ $(type $req1 2>&1 | grep -c 'not found') -eq 1 ]]; 
	then echo "Dependency $(echo $req1) not found. Please install it via your package manager."; exit
elif [[ $(type $req2 2>&1 | grep -c 'not found') -eq 1 ]]; 	
	then echo "Dependency $(echo $req2) not found. Please install it via your package manager"; exit
else echo 'All dependencies met!'; 
fi

echo 'Please provide full folder paths. For example: /home/myuser/Documents/MC/'
read -p 'Where is your server located? ' mclocation; while [[ ! -f "$mclocation/server.properties" ]]; do read -p 'Invalid location. Please try again: ' mclocation; done 
if [[ $(\ls $mclocation | grep -c jar) -eq 1 ]]; 
	then mcstart=$(\ls $mclocation | grep jar)
elif [[ $(\ls $mclocation | grep -c jar) -eq 0 ]];
	then echo "No executable server jar file was found in $mclocation";
	echo "Try re-downloading the minecaft_server.jar from minecraft.net"; exit
else 
	echo "Here is a list of possible server jar files: "; \ls $mclocation | grep jar; 
	read -p 'Which one would you like to use to start your server with? ' mcstart; 
	while [[ $(\ls $mclocation | grep -Pc "^${mcstart}$") -ne 1  ]]; 
		do read -p 'Invalid entry. Please try again: ' mcstart; 
	done
fi

read -p 'What directory do you want RAM mounted onto for MC? ' ramlocation;
sudo mkdir -p $ramlocation

availableRam=$(expr $(grep MemTotal /proc/meminfo | awk {'print $2'}) / 1024)
echo "Your computer has ${availableRam}MB of RAM"

read -p 'How much RAM (in MB) would you like to use for mounting Minecraft into RAM (default is 256)? ' mountRam;
while [[ ${mountRam} -gt ${availableRam} ]];
	do read -p "Incorrect value. Please pick a value lower than ${availableRam}MB" mountRam;
done
if [[ $(echo ${mountRam}) -eq "" ]]; 
	then mountRam="256"
fi

read -p 'What amount of RAM (in MB) would you like to use for running Minecraft? (Default is 256): ' mcstartram;
while [[ ${mcstartram} -gt ${availableRam} ]];
	do read -p "Incorrect value. Please pick a value lower than ${availableRam}MB" mcstartram;
done
if [[ $(echo ${mcstartram}) -eq "" ]]; 
	then mcstartram="256"
fi

read -p 'How long (in minutes) do you want to wait for the server to sync to the drive again? (Default is 60): ' synctime;
if [[ $(echo $synctime) -eq "" ]]; 
	then synctime="60"
fi

if [[ -f /etc/sysctl.conf ]]; then\
	echo "EXPERIMENTAL. Adjusting the swappiness settings does not work on all systems."
	echo "Your swappiness level is $(cat /proc/sys/vm/swappiness) out of 100."
        read -p "The lower the number, the less likely your system and the tmpfs is to use swap. Would you like to adjust this (Y/N)? " answer;
        if [[ "$answer" == "Y" || "$answer" == "y" ]]; then\
            read -p "What level number do you want to use? " answer;
		#Update the system configuration to use this swappiness level after reboots
		sed -i s/^vm.swappiness/\#vm.swappiness/g /etc/sysctl.conf
		echo "vm.swappiness=${answer}" >> /etc/sysctl.conf
		#Apply the new setting now
		sudo swapoff -a; sudo swapon -a
        fi
fi
	
#currentdate=$(date +%Y-%m-%d_%Hh.%Mm.%Ss)

echo "Setting up the automted MCRAM service now..."
#Setup the cron
	#The sed replace commands need an escape character for every directory slash "/" in the bash variables. The below fixes that.
mclocation_sedfix=$(echo $mclocation | sed 's/\//\\\//g')
ramlocation_sedfix=$(echo $ramlocation | sed 's/\//\\\//g')

#Create the MCRAM install directory
mkdir ~/mcram/

mccronlocation=$(echo ~/mcram/mccron.sh)
cp -af ./mccron-template.sh ~/mcram/mccron.sh
sed -i s'/${mountRam}/'"$mountRam"'/g' ~/mcram/mccron.sh
sed -i s'/$mclocation/'"$mclocation_sedfix"'/g' ~/mcram/mccron.sh
sed -i s'/$ramlocation/'"$ramlocation_sedfix"'/g' ~/mcram/mccron.sh
sed -i s'/${mcstartram}/'"$mcstartram"'/g' ~/mcram/mccron.sh
sed -i s'/$mcstart/'"$mcstart"'/g' ~/mcram/mccron.sh
sed -i s'/ ${synctime}/'" $synctime"'/g' ~/mcram/mccron.sh


if [[ $(type systemctl 2>&1 | grep -c "not found") -eq 0 ]];
	then mccronlocation_sedfix=$(echo $mccronlocation | sed 's/\//\\\//g')
	sed -i s'/$mccronlocation/'"$mccronlocation_sedfix"'/g' ./mcram.service
	chmod 750 ./mcram.service
	sudo cp -af ./mcram.service /usr/lib/systemd/system/mcram.service
	sudo systemctl enable mcram; sudo systemctl start mcram &
	mcramJobID=$(jobs | grep "sudo systemctl start mcram" | grep -oP "[0-9][0-9]*")
	disown %${mcramJobID}
elif [[ $(type crontab 2>&1 | grep -c "not found") -eq 0 ]];
	then echo -e "$(crontab -l)\n@reboot /bin/sh $HOME/mcram/mccron.sh" | crontab -
else echo "systemd and crontab are not installed, please install one of these services"
fi

sudo chmod o+rw /dev/pts/2; sudo chmod 755 /run/screens; #Fixes screen issues when running as the user
chmod 750 ~/mcram/mccron.sh #Makes the cron executable

echo -e "MCRAM ${mcramv} has been installed.\nPlease report any problems or suggestions to https://github.com/ekultails/mcram/"
sh ~/mcram/mccron.sh &

echo -e "\e[0;00m"; #Resets the colors

###CHANGELOG
##FUTURE RELEASE
#	Automatically install dependencies on Debian, RHEL, and Arch based hosts
#	Create/enforce strict permissions
#	Add the ability to automatically generate backups before setting up the server in RAM
#
#=-=-=CHANGES SINCE LAST MAJOR RELEASE=-=-=#
##0.4 Beta
#	Implemented:
#		Mac OSX (Darwin) support
#	Bug fix:
#		mounts are now done manually to avoid /etc/fstab issues
#		
##0.3
#	Implemented:
#		Uninstaller created
#		Amount of mounted RAM can is now determined by megabytes instead of a percentage of available RAM
#	Bug Fix:
#		rsync wasn't always syncing properly; Switched arguments for rsync from -varP to -varuP
#		systemd service installation no longer hangs; the mcram.service is now started detatched in the background
#		Do not allow memory usage higher than the available systems RAM
#
##0.2
#	Implemented:
#		"sudo" is now used for non-root sudo users to install/set-up MCRAM
#		Added a systemd start-up script "mcram.service"
#		systemd timer unit is used instead of the crontab if systemd is available
#		Sync time intervals can now be changed
#		MCRAM now installs itself to the user's home directory in a folder called "mcram"
#		"replace" has been replaced with "sed" commands for better compatibility across platforms
#
##0.1 Alpha
#	Implemented:
#		Sets up a cron for the Minecraft server to start on boot
#		Mounts MC server into memory/RAM
#		Syncs the server from RAM every hour
#


