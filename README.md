#MCRAM v0.2

MCRAM is designed to help run your Minecraft server entirely in RAM on UNIX-based systems. It will set up the automation of starting your server 100% in memory and then saving/syncing it back to your drive occasionally. This helps to improve performance and extend your drive's health and longevity.

### SETUP
For installation simply execute the install file:
```bash
sh installer.sh
```
The install will walk you through and help answer your questions to make the configuration file that will be run as a system cron. Currently the cron is setup to be run (A) after the installer has finished and then (B) after that it will always run on the boot-up of your server/PC. 

### SAMPLE SETUP
````
# sh installer.sh
Where is your server located? /home/myuser/games/Minecraft/
What directory do you want RAM mounted onto for MC? /ram/
Your computer has 8192MB of RAM
What percentage of RAM would you like to use for mounting Minecraft into RAM? (Default is 10): 15
What amount of RAM (in MB) would you like to use for running Minecraft? (Default is 256): 1024
Mounting 819MB of RAM onto /ram/
```

#### DISCLAIMER
This program does NOT provide any binaries of the Minecraft game. These must be obtained by Mojang, Microsoft, and/or any other appropriate companies officially associated with the Minecraft brand. All intellectual properties, brands, trandmarks, and/or copyrights are owned by their respective owners/companies.
