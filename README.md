TuneTrail

TuneTrail is an educational Android app that teaches kids basic programming ideas through a simple maze game. Kids use drag-and-drop commands to move through the maze, collect items, and reach the goal. Parents can create accounts, view reports, and track their child’s progress.

This project was created for CSC 371 – Individual Project 3.

Features

Accounts (SQLite)
•	Parent and Kid registration
•	Parent and Kid login
•	Parent linked to kid accounts
•	Session storage so the user stays logged in

Game Levels
•	4 levels
•	Each level has 3 games
•	Each game has its own maze layout
•	Each maze includes:
•	Walls
•	Notes to collect
•	A goal cell
•	A move limit

Commands
•	The kid can drag and drop these commands:
•	Step (move right)
•	Left
•	Up
•	Down
•	Commands run in order to solve the maze

Animation and Audio
•	Smooth animation while moving through the maze
•	Sounds for:
•	Moving
•	Winning
•	Failing

Drag and Drop
•	Commands can be dragged into the “program bar”
•	The program bar shows the full sequence
•	Undo and Reset options are included

Success and Reattempt Handling
•	A success dialog appears when the goal and notes are collected
•	A failure dialog appears when:
•	Hitting a wall
•	Going out of bounds
•	Using too many moves
•	Missing required notes
•	Success resets the board fully
•	Failure keeps the commands to allow reattempts

Progress Logging

Every attempt is saved in a CSV file inside the app:

attempts.csv

Format:

timestamp,kidId,level,game,success,moves,timeMs

Parent Report (Chart)
•	The app reads the CSV file
•	Calculates success rate for each level
•	Displays a bar chart using MPAndroidChart

Device Support
•	Works in portrait and landscape
•	Works on phones and tablets
•	Uses layouts that scale on different screen sizes

How to Run
1.	Clone the project
2.	Open it in Android Studio
3.	Run it on an emulator or physical device

Project Structure (Simplified)

TuneTrail/
data/
db/        (Room database files)
repo/      (User repository)
session/   (User session)
di/
AppGraph.kt
GameScreen.kt
LevelConfigs.kt
ParentHomeScreen.kt
ParentReportScreen.kt
KidHomeScreenNav.kt
AttemptLogger.kt
NavGraph.kt
LoginScreen.kt
RegisterScreen.kt
MainActivity.kt

Purpose

This project demonstrates:
•	Animation
•	Drag and drop
•	Audio playback
•	SQLite usage
•	Dialog handling
•	Navigation
•	File logging
•	Third-party chart library integration
•	Multi-device support
•	Programmatic maze game design

