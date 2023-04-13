package minesweeper

fun main() {
    println("How many mines do you want on the field?")
    val mineCount = readln().toInt()
    val filed = Field(9, 6, mineCount)
    filed.print()
    var firstStep = true
    while (!filed.gameEnd) {
        println("Set/unset mines marks or claim a cell as free:")
        val input = readln().trim().split(' ').toMutableList()
        if (input.size > 0 && input[0] == "exit") {
            println("Bye, thanks for playing!")
            break
        }
        if (input.size != 3) {
            println("Please enter a valid input (for example \"2 5 free\" or \"1 2 mine\")")
            continue
        }
        val x = input[0].toInt() - 1
        val y = input[1].toInt() - 1
        if (firstStep) {
            filed.generateMines(x, y)
            filed.generateHints()
            firstStep = false
        }
        when (input[2]) {
            "free" -> filed.cellOpen(x, y)
            "mine" -> filed.cellFlag(x, y)
        }
    }
}
