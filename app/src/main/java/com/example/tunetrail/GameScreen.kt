package com.example.tunetrail

import android.media.MediaPlayer
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.RawRes
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * The main screen for the game, holding the game board, command palette, and game logic.
 * This composable function manages the entire game state, including player position,
 * command sequences, and game flow (running, success, failure). It orchestrates
 * the UI components like the game board, command palette, and control buttons.
 *
 * @param level The current level number, used to load the appropriate game configuration.
 * @param game The current game number within the level, used to load the specific game configuration.
 */
@Composable
fun GameScreen(level: Int, game: Int) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // Load the game configuration based on the provided level and game numbers.
    // `remember` ensures this is only re-calculated if level or game changes.
    val cfg = remember(level, game) { LevelConfigs.config(level, game) }

    // State for the player's current position on the board (row, column).
    var row by remember { mutableStateOf(cfg.startRow) }
    var col by remember { mutableStateOf(cfg.startCol) }

    // State for the sequence of commands built by the user.
    // The commands are "STEP" (right), "UP", "DOWN", "LEFT".
    var commands by remember { mutableStateOf(listOf<String>()) }
    // State to track if the program (command sequence) is currently being executed.
    // Used to disable UI elements like buttons and the command palette during execution.
    var running by remember { mutableStateOf(false) }

    // State to control the visibility of the success dialog.
    var success by remember { mutableStateOf(false) }
    // State to control the visibility of the failure dialog.
    var failed by remember { mutableStateOf(false) }

    // State to keep track of the number of moves made during a run.
    var moveCount by remember { mutableStateOf(0) }
    // State to store the coordinates of the musical notes that have been collected.
    var collectedNotes by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }

    /**
     * Resets the game board to its initial state for the current level.
     * Player position, move count, and collected notes are reset.
     * The `running` flag is also cleared.
     */
    fun resetBoard() {
        row = cfg.startRow
        col = cfg.startCol
        running = false
        moveCount = 0
        collectedNotes = emptySet()
    }

    // Main container for the screen with a gradient background.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB3E5FC),  // top light blue
                        Color(0xFFE1F5FE)   // bottom lighter blue
                    )
                )
            )
            .padding(16.dp)
    ) {

        // The main column layout for all UI elements.
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Top bar: title + Exit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Level $level — Game $game")
                Button(onClick = { backDispatcher?.onBackPressed() }) {
                    Text("Exit")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Maze board
            GameBoard(
                row = row,
                col = col,
                cfg = cfg,
                collectedNotes = collectedNotes
            )

            Spacer(Modifier.height(4.dp))

            // UI to display the current move count and remaining notes.
            // The move count text changes color to warn the player as they approach the limit.
            val movesColor = when {
                moveCount > cfg.maxMoves -> Color.Red
                moveCount >= cfg.maxMoves - 1 -> Color(0xFFFFA000)
                else -> Color.Black
            }
            Text("Moves: $moveCount / ${cfg.maxMoves}", color = movesColor)

            // Calculate and display the number of notes left to collect.
            val notesLeft = cfg.notes.size - collectedNotes.size
            Text(
                text = "Notes left: $notesLeft",
                color = if (notesLeft == 0) Color(0xFF2E7D32) else Color.Black
            )

            Spacer(Modifier.height(12.dp))

            // The area where users can drag commands from.
            CommandPalette(
                enabled = !running,
                onDropCommand = { cmd ->
                    if (!running) {
                        commands = commands + cmd
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            // The area that displays the user's constructed command sequence.
            ProgramBar(commands)

            Spacer(Modifier.height(8.dp))

            // Row containing the main game control buttons (Reset, Undo, Start).
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // "Reset" button: Resets the board and clears the entire command sequence.
                Button(
                    onClick = {
                        resetBoard()
                        commands = emptyList()
                    }
                ) {
                    Text("Reset")
                }

                // "Undo last" button: Removes the last command from the sequence.
                Button(
                    onClick = {
                        if (!running && commands.isNotEmpty()) {
                            commands = commands.dropLast(1)
                        }
                    }
                ) {
                    Text("Undo last")
                }

                // "Start" button: Executes the command sequence.
                Button(
                    enabled = !running && commands.isNotEmpty(),
                    onClick = {
                        // Set the running flag to true to start execution and disable controls.
                        running = true
                        scope.launch {
                            val startTime = System.currentTimeMillis()
                            var moves = 0
                            var ok = true

                            try {
                                // Iterate through each command in the sequence.
                                for (cmd in commands) {
                                    delay(300)

                                    val (dr, dc) = when (cmd) {
                                        // Determine the change in row/col based on the command.
                                        "STEP" -> 0 to 1   // right
                                        "LEFT" -> 0 to -1  // left
                                        "UP"   -> -1 to 0
                                        "DOWN" -> 1 to 0
                                        else   -> 0 to 0
                                    }

                                    val newRow = row + dr
                                    val newCol = col + dc

                                    // Check for invalid moves: out of bounds or hitting a wall.
                                    if (newRow !in 0..4 ||
                                        newCol !in 0..4 ||
                                        (newRow to newCol) in cfg.walls
                                    ) {
                                        ok = false
                                        break
                                    }

                                    // Update player position.
                                    row = newRow
                                    col = newCol

                                    // Play a sound for each move.
                                    playSound(ctx, R.raw.game_unlock)

                                    // Increment and update the move counter.
                                    moves++
                                    moveCount = moves

                                    // If the new position contains a note, add it to the collected set.
                                    val pos = row to col
                                    if (pos in cfg.notes) {
                                        collectedNotes = collectedNotes + pos
                                    }

                                    // Check if the move limit has been exceeded.
                                    if (moves > cfg.maxMoves) {
                                        ok = false
                                        break
                                    }
                                }

                                delay(200)

                                // Final checks after the loop finishes.
                                val atGoal = (row == cfg.goalRow && col == cfg.goalCol)
                                val gotAllNotes = collectedNotes.size == cfg.notes.size
                                ok = ok && atGoal && gotAllNotes && moves <= cfg.maxMoves

                                val endTime = System.currentTimeMillis() // Record end time for logging.

                                // Log attempt; kidId fixed to 1L for report
                                AttemptLogger.append(
                                    ctx = ctx,
                                    kidId = 1L,
                                    level = level,
                                    game = game,
                                    success = ok,
                                    moves = moves,
                                    timeMs = endTime - startTime
                                )

                                // Based on the outcome, show the success or failure dialog.
                                if (ok) {
                                    playSound(ctx, R.raw.winning_coin)
                                    success = true
                                } else {
                                    playSound(ctx, R.raw.losing_and_falling)
                                    failed = true
                                } // `finally` block will run regardless of success or failure.
                            } finally {
                                running = false
                            }
                        }
                    }
                ) {
                    Text("Start")
                }
            }
        }

        // Dialog displayed when the player successfully completes the level.
        if (success) {
            AlertDialog(
                onDismissRequest = { success = false },
                confirmButton = {
                    Button(
                        onClick = {
                            success = false
                            resetBoard()
                            commands = emptyList()
                        }
                    ) {
                        Text("OK")
                    }
                },
                title = { Text("Great job!") },
                text = { Text("You reached the goal and collected all notes. Attempt saved.") }
            )
        }

        // Dialog displayed when the player fails the level.
        if (failed) {
            AlertDialog(
                onDismissRequest = { failed = false },
                confirmButton = {
                    Button(
                        onClick = {
                            failed = false
                            // Reset board state but keep the commands for the user to edit.
                            resetBoard()
                        }
                    ) {
                        Text("Try Again")
                    }
                },
                title = { Text("Not yet") },
                text = {
                    Text(
                        "You hit a wall, went out of bounds, missed a note, " +
                                "or used too many moves.\nUse Undo or Reset to change your program."
                    )
                }
            )
        }
    }
}

/**
 * Composable for rendering the 5x5 game board.
 * @param row The player's current row.
 * @param col The player's current column.
 * @param cfg The game configuration, containing wall, note, and goal positions.
 * @param collectedNotes A set of coordinates for notes that have already been collected.
 */
@Composable
private fun GameBoard(
    row: Int,
    col: Int,
    cfg: GameConfig,
    collectedNotes: Set<Pair<Int, Int>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFB0BEC5)),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(5) { r ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(5) { c ->
                    // Determine the state of the current tile (r, c).
                    val isPlayer = (r == row && c == col)
                    val isGoal = (r == cfg.goalRow && c == cfg.goalCol)
                    val isWall = (r to c) in cfg.walls
                    val hasNote = (r to c) in cfg.notes && (r to c) !in collectedNotes

                    // Set the tile color based on whether it's a wall or a path.
                    val tileColor = if (isWall) {
                        Color(0xFF455A64)
                    } else {
                        Color(0xFFF5F5F5)
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(tileColor, RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFF9E9E9E), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Display an emoji based on what the tile represents.
                        when {
                            isPlayer -> Text("🎵")
                            isGoal -> Text("⭐")
                            hasNote -> Text("♪")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable for the palette of draggable command tiles.
 * @param enabled Whether the tiles can be dragged (disabled while the program is running).
 * @param onDropCommand A callback function invoked when a command tile is dropped.
 */
@Composable
private fun CommandPalette(
    enabled: Boolean,
    onDropCommand: (String) -> Unit
) {
    Column {
        Text("Drag a command into the program area")
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DraggableCommandTile(
                label = "STEP",
                symbol = "→",
                color = Color(0xFF42A5F5),
                enabled = enabled,
                onDrop = { onDropCommand("STEP") }
            )
            DraggableCommandTile(
                label = "UP",
                symbol = "↑",
                color = Color(0xFFAB47BC),
                enabled = enabled,
                onDrop = { onDropCommand("UP") }
            )
            DraggableCommandTile(
                label = "DOWN",
                symbol = "↓",
                color = Color(0xFFFFA726),
                enabled = enabled,
                onDrop = { onDropCommand("DOWN") }
            )
            DraggableCommandTile(
                label = "LEFT",
                symbol = "←",
                color = Color(0xFF66BB6A),
                enabled = enabled,
                onDrop = { onDropCommand("LEFT") }
            )
        }
    }
}

/**
 * Composable for the bar that displays the sequence of commands the user has chosen.
 * @param commands The list of command strings to display.
 */
@Composable
private fun ProgramBar(commands: List<String>) {
    Column {
        Text("Program (your steps):")
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0E0E0))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Show a placeholder text if no commands have been added yet.
            if (commands.isEmpty()) {
                Text(
                    "Drop commands here in the order you want to run them",
                    color = Color.Gray
                )
            } else {
                // Otherwise, display each command as a colored tile with a symbol.
                commands.forEach { cmd ->
                    val (color, symbol) = when (cmd) {
                        "STEP" -> Color(0xFF42A5F5) to "→"
                        "UP"   -> Color(0xFFAB47BC) to "↑"
                        "DOWN" -> Color(0xFFFFA726) to "↓"
                        "LEFT" -> Color(0xFF66BB6A) to "←"
                        else   -> Color.LightGray to "?"
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, RoundedCornerShape(10.dp))
                            .shadow(2.dp, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(symbol)
                    }
                }
            }
        }
    }
}

/**
 * A single draggable command tile.
 * @param label The text label for the command (e.g., "STEP").
 * @param symbol The symbol for the command (e.g., "→").
 * @param color The background color of the tile.
 * @param enabled Whether the tile is draggable.
 * @param onDrop A callback executed when the drag gesture ends.
 */
@Composable
private fun DraggableCommandTile(
    label: String,
    symbol: String,
    color: Color,
    enabled: Boolean,
    onDrop: () -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    // Animate the tile's return to its original position after being dragged.
    val animatedOffset by animateOffsetAsState(targetValue = offset, label = "drag")

    // The visual representation of the draggable tile.
    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    animatedOffset.x.roundToInt(),
                    animatedOffset.y.roundToInt()
                )
            }
            .size(60.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .background(color, RoundedCornerShape(14.dp))
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                // Detect drag gestures on this tile.
                detectDragGestures(
                    onDragEnd = {
                        offset = Offset.Zero // Reset position on drop.
                        onDrop()
                    },
                    // Also reset position if the drag is canceled.
                    onDragCancel = { offset = Offset.Zero },
                    onDrag = { _, dragAmount ->
                        offset += dragAmount
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Display the symbol and label inside the tile.
            Text(symbol)
            Text(label)
        }
    }
}

/**
 * A utility function to play a sound effect.
 * It creates a MediaPlayer instance, plays the sound, and releases resources on completion.
 * @param ctx The Android context.
 * @param resId The raw resource ID of the sound file to play.
 */
private fun playSound(ctx: android.content.Context, @RawRes resId: Int) {
    MediaPlayer.create(ctx, resId).apply {
        setOnCompletionListener { it.release() }
        start()
    }
}