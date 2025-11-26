// LevelConfigs.kt — FINAL HARD VERSION (2025)
package com.example.tunetrail

// This must match what GameScreen expects
data class GameConfig(
    val startRow: Int,
    val startCol: Int,
    val goalRow: Int,
    val goalCol: Int,
    val walls: Set<Pair<Int, Int>>,
    val notes: Set<Pair<Int, Int>>,
    val maxMoves: Int
)

/**
 * Defines the configuration for all levels and games in TuneTrail.
 * Each level has 3 games, with increasing difficulty or introducing new mechanics.
 *
 * The grid is 5x5, with (0, 0) at the top-left.
 *
 * - `startRow`, `startCol`: The player's starting position.
 * - `goalRow`, `goalCol`: The destination cell.
 * - `walls`: A set of cells the player cannot enter. Defined by specifying the `open` cells.
 * - `notes`: A set of cells the player must visit before reaching the goal.
 * - `maxMoves`: The maximum number of moves allowed to complete the level.
 */
object LevelConfigs {

    // A set of all possible cells on the 5x5 grid.
    private val allCells: Set<Pair<Int, Int>> =
        (0..4).flatMap { r -> (0..4).map { c -> r to c } }.toSet()

    /**
     * A helper function to define walls by specifying which cells are *open* (walkable).
     * This is often more intuitive than listing every single wall cell.
     * @param open The set of (row, col) pairs that are walkable.
     * @return A set of (row, col) pairs representing walls.
     */
    private fun wallsFromOpen(open: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> =
        allCells - open

    /**
     * Retrieves the specific [GameConfig] for a given level and game number.
     * @param level The level number (1-4).
     * @param game The game number within that level (1-3).
     * @return The corresponding [GameConfig].
     */
    fun config(level: Int, game: Int): GameConfig = when (level) {

        1 -> when (game) {  // Teaching basic movement + first real detour
            1 -> GameConfig(
                startRow = 4, startCol = 0,
                goalRow = 0, goalCol = 4,
                walls = wallsFromOpen( // Defines a simple S-shaped path
                    setOf(
                        4 to 0, 4 to 1, 3 to 1, 2 to 1, 2 to 2, 2 to 3, 2 to 4,
                        1 to 4, 0 to 4
                    )
                ),
                notes = setOf(2 to 2, 1 to 4),  // one note off the main path
                maxMoves = 12
            )

            2 -> GameConfig(
                startRow = 4, startCol = 0,
                goalRow = 0, goalCol = 4,
                walls = wallsFromOpen( // A slightly more complex path
                    setOf(
                        4 to 0, 3 to 0, 2 to 0, 2 to 1, 2 to 2, 1 to 2, 1 to 3, 1 to 4, 0 to 4
                    )
                ),
                notes = setOf(3 to 0, 1 to 3),
                maxMoves = 11
            )

            3 -> GameConfig(
                startRow = 4, startCol = 0,
                goalRow = 0, goalCol = 4,
                walls = wallsFromOpen( // Path with a dead-end to trap players
                    setOf(
                        4 to 0, 4 to 1, 4 to 2, 3 to 2, 2 to 2, 2 to 3, 2 to 4,
                        1 to 4, 0 to 4
                    )
                ),
                notes = setOf(4 to 1, 2 to 3),
                maxMoves = 10
            )

            else -> error("Invalid game")
        }

        2 -> when (game) {  // FORCE LEFT and UP/DOWN — no more right-only cheating
            1 -> GameConfig(  // You MUST go left multiple times
                startRow = 2, startCol = 4,
                goalRow = 4, goalCol = 0,
                walls = wallsFromOpen( // Start on the right, goal on the left
                    setOf(
                        2 to 4, 2 to 3, 2 to 2, 3 to 2,
                        4 to 2, 4 to 1, 4 to 0
                    )
                ),
                notes = setOf(2 to 2, 4 to 1),
                maxMoves = 9
            )

            2 -> GameConfig(
                startRow = 1, startCol = 4,
                goalRow = 3, goalCol = 0,
                walls = wallsFromOpen( // A C-shaped path forcing up/down/left movement
                    setOf(
                        1 to 4, 1 to 3, 2 to 3, 3 to 3, 3 to 2, 3 to 1, 3 to 0
                    )
                ),
                notes = setOf(1 to 3, 3 to 2),
                maxMoves = 10
            )

            3 -> GameConfig(
                startRow = 0, startCol = 4,
                goalRow = 4, goalCol = 0,
                walls = wallsFromOpen( // A "staple" shaped path across the grid
                    setOf(
                        0 to 4, 1 to 4, 2 to 4, 2 to 3, 2 to 2, 2 to 1, 2 to 0,
                        3 to 0, 4 to 0
                    )
                ),
                notes = setOf(2 to 3, 2 to 1),
                maxMoves = 11
            )

            else -> error("Invalid game")
        }

        3 -> when (game) {  // Tight snakes + notes that force backtracking
            1 -> GameConfig(
                startRow = 4, startCol = 0,
                goalRow = 0, goalCol = 4,
                walls = wallsFromOpen( // A tight path that requires a small backtrack for a note
                    setOf(
                        4 to 0, 3 to 0, 2 to 0, 1 to 0,
                        1 to 1, 1 to 2,
                        0 to 2, 0 to 3, 0 to 4
                    )
                ),
                notes = setOf(1 to 1, 0 to 3),
                maxMoves = 12
            )

            2 -> GameConfig(
                startRow = 4, startCol = 0,
                goalRow = 0, goalCol = 4,
                walls = wallsFromOpen( // Another snake-like path
                    setOf(
                        4 to 0, 4 to 1,
                        3 to 1, 3 to 2, 3 to 3,
                        2 to 3, 1 to 3, 0 to 3, 0 to 4
                    )
                ),
                notes = setOf(3 to 2, 1 to 3),
                maxMoves = 11
            )

            3 -> GameConfig(
                startRow = 4, startCol = 0,
                goalRow = 0, goalCol = 4,
                walls = wallsFromOpen( // A long path along the bottom and right edges
                    setOf(
                        4 to 0, 4 to 1, 4 to 2, 4 to 3,
                        3 to 3, 2 to 3, 1 to 3, 0 to 3, 0 to 4
                    )
                ),
                notes = setOf(4 to 2, 2 to 3),
                maxMoves = 10
            )

            else -> error("Invalid game")
        }

        4 -> when (game) {  // TRUE MASTERY — no mercy
            1 -> GameConfig(  // The infamous "Figure 8"
                startRow = 4, startCol = 0,
                goalRow = 0, goalCol = 4,
                walls = wallsFromOpen( // A complex path that crosses over itself
                    setOf(
                        4 to 0, 3 to 0, 2 to 0,
                        2 to 1, 2 to 2, 2 to 3, 2 to 4,
                        1 to 4, 0 to 4, 0 to 3, 1 to 3
                    )
                ),
                notes = setOf(2 to 1, 2 to 3, 1 to 3),
                maxMoves = 14
            )

            2 -> GameConfig(  // The "Spiral of Doom"
                startRow = 4, startCol = 0,
                goalRow = 0, goalCol = 4,
                walls = wallsFromOpen( // Path forces a spiral-in and spiral-out movement
                    setOf(
                        4 to 0, 4 to 1, 4 to 2,
                        3 to 2, 2 to 2, 1 to 2,
                        1 to 3, 1 to 4,
                        0 to 4
                    )
                ),
                notes = setOf(4 to 1, 1 to 3, 2 to 2),
                maxMoves = 13
            )

            3 -> GameConfig(  // Final boss: you must go LEFT and UP multiple times
                startRow = 3, startCol = 4,
                goalRow = 1, goalCol = 0,
                walls = wallsFromOpen( // A challenging final path combining all learned skills
                    setOf(
                        3 to 4, 3 to 3, 3 to 2, 3 to 1, 3 to 0,
                        2 to 0, 1 to 0, 1 to 1, 1 to 2
                    )
                ),
                notes = setOf(3 to 2, 1 to 1),
                maxMoves = 12
            )

            else -> error("Invalid game")
        }

        else -> error("Invalid level")
    }
}