#!`which bash`
#MCRAM - Installer - installer.sh
#v0.2 (PRERELEASE) - ALPHA

echo -e "\e[1m \e[36m"; #Changes color to light blue 

req1="screen"; 
req2="sudo"; 

if [[ $(type $req1 2>&1 | grep -c 'not found') -eq 1 ]]; 
	then echo "Dependency $(echo $req1) not found. Please install it via your package manager."; exit
elif [[ $(type $req2 2>&1 | grep -c 'not found') -eq 1 ]]; 	
	then echo "Dependency $(echo $req2) not found. Please install it via your package manager"; exit
else echo "All dependencies met"; 
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

echo "Your computer has $(expr $(grep MemTotal /proc/meminfo | awk {'print $2'}) / 1024)MB of RAM"

read -p 'What percentage of RAM would you like to use for mounting Minecraft into RAM? (Default is 10): ' rampercentage;
if [[ $(echo $rampercentage) -eq "" ]]; 
	then rammb=$(expr $(grep MemTotal /proc/meminfo | awk {'print $2'}) / 1024 \* 10 / 100);
	else rammb=$(expr $(grep MemTotal /proc/meminfo | awk {'print $2'}) / 1024 \* $rampercentage / 100);
fi

read -p 'What amount of RAM (in MB) would you like to use for running Minecraft? (Default is 256): ' mcstartram;
if [[ $(echo $mcstartram) -eq "" ]]; 
	then mcstartram="256"
fi

read -p 'How long (in minutes) do you want to wait for the server to sync to the drive again? (Default is 60): ' synctime;
if [[ $(echo $synctime) -eq "" ]]; 
	then synctime="60"
fi
	
echo "Mounting ${rammb}MB of RAM onto $ramlocation"
sudo chmod 666 /etc/fstab
echo "tmpfs ${ramlocation} tmpfs defaults,noatime,size=${rammb}M 0 0" >> /etc/fstab; sudo mount -a
sudo chmod 644 /etc/fstab


#Setup the cron
	#The sed replace commands need an escape character for every directory slash "/" in the bash variables. The below fixes that.
mclocation_sedfix=$(echo $mclocation | sed 's/\//\\\//g')
ramlocation_sedfix=$(echo $ramlocation | sed 's/\//\\\//g')

#Create the MCRAM install directory
mkdir ~/mcram/

mccronlocation=$(echo ~/mcram/mccron.sh)
cp -af ./mccron-template.sh ~/mcram/mccron.sh
sed -i s'/$mclocation/'"$mclocation_sedfix"'/g' ~/mcram/mccron.sh
sed -i s'/$ramlocation/'"$ramlocation_sedfix"'/g' ~/mcram/mccron.sh
sed -i s'/${mcstartram}/'"$mcstartram"'/g' ~/mcram/mccron.sh
sed -i s'/$mcstart/'"$mcstart"'/g' ~/mcram/mccron.sh
sed -i s'/ ${synctime}/'" $synctime"'/g' ~/mcram/mccron.sh

#FIXME - Finish "Setup systemd"
#sed -i s'/$mccronlocation/'"$mccronlocation"'/g' ./mcram.service
#chmod 750 ./mcram.service
#sudo cp -af ./mcram.service /usr/lib/systemd/system/mcram.service

if [[ $(type crontab 2>&1 | grep -c 'not found') -eq 1 ]];
	then echo "The crontab service is not installed"
else 
	echo -e "$(crontab -l)\n@reboot /bin/sh ~/mcram/mccron.sh" | crontab -
fi

sudo chmod o+rw /dev/pts/2 #Fixes screen issues when running as the user
chmod 750 ~/mcram/mccron.sh #Makes the cron executable
sh ~/mcram/mccron.sh &

echo -e "\e[0;00m"; #Resets the colors

###CHANGELOG
##FUTURE RELEASE
#	Automatically install dependencies on Debian, RHEL, and Arch based hosts
#	Amount of RAM used can be specified in MB
#	Add check to see if the Minecraft server is larger than RAM allocated
#	Create an un-installer
#	Create/enforce strict permissions
#	Add the ability to automatically generate backups before setting up the server in RAM
#	MacOSX support
#
#=-=-=CHANGES SINCE LAST MAJOR RELEASE=-=-=#
##v0.2
#	"sudo" is now used for non-root users to install/set-up MCRAM
#	Added a systemd start-up script "mcram.service"
#	Sync time intervals can now be changed
#	MCRAM now installs itself to the user's home directory in a folder called "mcram"
#	"replace" has been replaced with "sed" commands for better compatibility across platforms
#
##v0.1
#	Sets up a cron for the Minecraft server to start on boot
#	Mounts MC server into memory/RAM
#	Syncs the server from RAM every hour
#


