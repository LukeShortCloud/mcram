#!`which bash`
#MCRAM - Installer - installer.sh
#v0.1

echo -e "\e[1m \e[36m"; #Changes color to light blue 

req1="screen"; 
req2="sudo"; 

if [[ $(type $req1 2>&1 | grep -c 'not found') -eq 1 ]]; 
	then echo "Dependency $(echo $req1) not found";
elif [[ $(type $req2 2>&1 | grep -c 'not found') -eq 1 ]]; 	
	then echo "Dependency $(echo $req2) not found";
else echo "All dependencies met"; 
fi

echo 'Please provide full folder paths. For example: /home/myuser/Documents/MC/'
read -p 'Where is your server located? ' mclocation; while [[ ! -f "$mclocation/server.properties" ]]; do read -p 'Invalid location. Please try again: ' mclocation; done 
if [[ $(\ls $mclocation | grep -c jar) -eq 1 ]]; 
	then mcstart=$(\ls $mclocation | grep jar)
else 
	echo "Here is a list of possible server jar files: "; \ls $mclocation | grep jar; 
	read -p 'Which one would you like to use to start your server with? ' mcstart; 
	while [[ $(\ls $mclocation | grep -Pc "^${mcstart}$") -ne 1  ]]; 
		do read -p 'Invalid entry. Please try again: ' mcstart; 
	done
fi

read -p 'What directory do you want RAM mounted onto for MC? ' ramlocation;
mkdir -p $ramlocation

echo "Your computer has $(expr $(grep MemTotal /proc/meminfo | awk {'print $2'}) / 1024)MB of RAM"
read -p 'What percentage of RAM would you like to use for mounting Minecraft into RAM? (Default is 10): ' rampercentage;
read -p 'What amount of RAM (in MB) would you like to use for running Minecraft? (Default is 256): ' mcstartram;

if [[ $rampercentage -eq "" ]]; 
	then rammb=$(expr $(grep MemTotal /proc/meminfo | awk {'print $2'}) / 1024 \* 10 / 100);
	else rammb=$(expr $(grep MemTotal /proc/meminfo | awk {'print $2'}) / 1024 \* $rampercentage / 100);
fi

if [[ $mcstartram -eq "" ]]; 
	then mcstartram="256"
fi
	
echo "Mounting ${rammb}MB of RAM onto $ramlocation"
echo "tmpfs ${ramlocation} tmpfs defaults,noatime,size=${rammb}M 0 0" >> /etc/fstab; mount -a

#Setup the cron
cp -af ./mccron-template.sh ./mccron.sh
replace '$mclocation' "$mclocation" -- ./mccron.sh
replace '$ramlocation' "$ramlocation" -- ./mccron.sh
replace '${mcstartram}' "$mcstartram" -- ./mccron.sh
replace '$mcstart' "$mcstart" -- ./mccron.sh

echo -e "$(crontab -l)\n@reboot /bin/sh `pwd`/mccron.sh" | crontab -

sh ./mccron.sh &

echo -e "\e[0;00m"; #Resets the colors

###CHANGELOG
##FUTURE RELEASE
#	Automatically install dependencies on Debian, RHEL, and Arch based hosts
#	Amount of RAM used can be specified in MB
#	Time before next rsync can be adjusted
#	Add check to see if Minecraft server is larger than RAM allocated
#	Systemd start-up script
#	Setup "sudo" for root-level operations
#	MacOSX support
#
##v0.1
#	Sets up a cron for the Minecraft server to start on boot
#	Mounts MC server into memory/RAM
#	Syncs the server from RAM every hour
#




