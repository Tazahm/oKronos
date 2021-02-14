# oKronos timetable

## General presentation
oKronos is a timekeeper and score manager for hockey. It is a standalone application 
that runs on a personal computer with an Unix or Windows systems.

It uses two screens, the main screen to handle the action of the operator and display 
all data needed for him, and a second screen to display the time, score and penalties 
to the teams.
The operator uses mainly the mouse of the PC, and sometime the keyboard.

The operator window is in french but can be customized for other language.

### The operator window
![The operator window](doc/images/operator-window.png "The operator window")

### The score window
![The score window](doc/images/score-window.png "The score window")

## Technical background
oKronos is written in java with the help of javafx graphics packages.

Any 64 bits modern O.S. can be used. oKronos build process produces a delivery for the Windows system
and another for the Linux system.

## Build

The application uses maven.
The final delivery is generated into the folder okronos-application/dist .
 
- Go into the directory 'okronos-parent'.
- Use the command 'mvn clean package'.

## Installation
Once build, the delivery is contains into files names okronos-\<system\>-\<version\>.zip
Simply unzip the file.

## Execution
- On Windows : use okronos.exe or okronos.bat file.
- On Linux : use okronos.sh file.
 
## Modification 
You can use any editor or IDE, the sources are provided into eclipse projects.
NB : build can be perform with eclipse.

## User documentation
You will find a general presentation into the folder doc/gen.

## Licence
The code is provided under the conditions of the GNU General Public License, version 3.
                       
## Design
Refer to the file okronos-application/src/main/javadoc/overview.html
or generate the javadoc.

## Customization
The application is customized with various configuration files. The file init.properties contains the paths of the
various folder that are used by the application and known as 'data set'. 
It also gives the name of the secondary configuration files, contain into the data set.
Therefore the application read first the init file and next the secondary configuration files.

The data set consist of:
  - Some configuration folders that contain the secondary configuration file and some files used to customized the display
  of the application.
  - Some image folders that contain the icones of the teams.
  - Some media folders that contain the music tracks and animation clips.

The init file can be used to control the configuration of the application : for example, to customize
the screen of score but keep the the other files unchanged, add a configuration folder, duplicate and modify the
score.fxml and score.css files, and add the path to this folder into the init file before any other configuration folder:
the application will scan the folders into the order they appear into the configuration file, and
as your file will be find first, it will be used. 

The name and location of the init file can be changed by the system property 'configfile'. 
In such a way you can build different launcher for different contexts.

## Contact
contact.okronos.scoretable@gmail.com

