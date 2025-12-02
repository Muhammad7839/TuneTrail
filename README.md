TuneTrail

TuneTrail is an educational Android app that helps kids understand basic programming concepts through a maze-based puzzle system. Kids solve each maze by dragging movement commands into a program bar and running them step-by-step. Parents can register, log in, and view their child’s performance through a progress report that includes a chart.

This project was developed for CSC 371 – Individual Project 3.

⸻

Main Features

Account System (SQLite)

• Parent and Kid registration
• Login for both roles
• Parents linked to their child’s progress
• Session storage to keep users logged in

⸻

Game Structure

Levels and Games

• Four levels
• Three games (mazes) per level
• Each maze includes:
• Walls
• Notes to collect
• A goal position
• A move limit

Commands

• Step (right)
• Left
• Up
• Down
• Commands are drag-and-drop and run in sequence

⸻

Game Mechanics

Interaction

• Drag and drop
• Undo and Reset
• Program bar shows final command sequence

Feedback

• Smooth movement animation
• Sound for movement, success, and failure
• Success dialog when all conditions are met
• Failure dialog when hitting walls, missing notes, or exceeding moves

Logging Attempts

Every play attempt is recorded in a CSV file:

attempts.csv
Format:
timestamp,kidId,level,game,success,moves,timeMs

⸻

Parent Report

• Reads the CSV log
• Calculates success rate per level
• Displays results using MPAndroidChart
• Includes text summaries under the chart

⸻

Device Compatibility

• Works on phones and tablets
• Supports portrait and landscape
• Layout scales across different screen sizes

⸻

How to Run the Project
	1.	Clone the repository
	2.	Open in Android Studio
	3.	Run on an emulator or physical device

⸻

Project Files (Simplified)

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
AppDatabase + DAO + Repository
SessionStore


⸻

What This Project Demonstrates

• SQLite
• DataStore sessions
• Animations
• Drag and drop
• Audio playback
• File logging
• Dialogs
• Multi-screen navigation
• Charting library usage
• Scalable UI design
