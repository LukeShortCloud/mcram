#MCRAM v0.3

MCRAM is designed to help run your Minecraft server entirely in RAM on UNIX-based systems. It will set up the automation of starting your server 100% in memory and then saving/syncing it back to your drive occasionally. This helps to improve performance and extend your drive's health and longevity.

## INSTALL
For installation simply execute the install file:
```bash
sh installer.sh
```
The install will walk you through and help answer your questions to make the configuration file that will be run as a system cron. Currently the cron is setup to be run (A) after the installer has finished and then (B) after that it will always run on the boot-up of your server/PC. 

## SAMPLE SETUP
````
# sh install.sh
All dependencies met!
Please provide full folder paths. For example: /home/myuser/Documents/MC/
Where is your server located? /home/myuser/games/Minecraft/
Here is a list of possible server jar files:
minecraft_server.1.8.jar
spigot-1.8-R0.1-SNAPSHOT.jar
Which one would you like to use to start your server with? spigot-1.8-R0.1-SNAPSHOT.jar
What directory do you want RAM mounted onto for MC? /ram/
Your computer has 2048MB of RAM
How much RAM (in MB) would you like to use for mounting Minecraft into RAM (default is 256)?
What amount of RAM (in MB) would you like to use for running Minecraft? (Default is 256): 300
How long (in minutes) do you want to wait for the server to sync to the drive again? (Default is 60): 120
Mounting 256MB of RAM onto /ram/
MCRAM v0.3 has been installed.
Please report any problems or suggestions 
to https://github.com/ekultails/mcram/
```

## DISCLAIMER
This program does NOT provide any binaries of the Minecraft game. These must be obtained by Mojang, Microsoft, and/or any other appropriate companies officially associated with the Minecraft brand. All intellectual properties, brands, trandmarks, and/or copyrights are owned by their respective owners/companies.

## License
This software is licensed under the GPLv3. More information about this can be found in the included "LICENSE" file or online at: http://www.gnu.org/licenses/gpl-3.0.en.html
