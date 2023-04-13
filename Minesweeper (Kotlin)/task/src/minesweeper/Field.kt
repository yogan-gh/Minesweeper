package minesweeper

class Field(val widthX: Int = 9, val heightY: Int = 9, _mines: Int = 9) {
    val size = heightY * widthX
    val mines = if (_mines in 1..size) _mines else if (_mines < 1) 1 else size - 1
    val field = MutableList(heightY) { MutableList(widthX) { Cell(Type.EMPTY) } } //filed[Y][X]
    var gameEnd = false
    fun getCell(x: Int, y: Int): Cell = field[y][x]
    fun setCell(x: Int, y: Int, cell: Cell) {
        field[y][x] = cell
    }
    fun generateMines(startX: Int = -1, startY: Int = -1) {
        field.clear()
        val fieldLine =
            (MutableList(size - mines) {Cell(Type.EMPTY)} + MutableList(mines) {Cell(Type.MINE)}).shuffled().toMutableList()
        val start = (startY * widthX) + startX
        if (fieldLine[start].type == Type.MINE) {
            fieldLine[start] = Cell(Type.EMPTY)
            val emptyIndexes = mutableListOf<Int>()
            for (index in 0..fieldLine.lastIndex) {
                if (fieldLine[index].type == Type.EMPTY) emptyIndexes.add(index)
            }
            fieldLine[emptyIndexes.shuffled().first()] = Cell(Type.MINE)
        }
        repeat(heightY) {
            val line = fieldLine.subList(it * widthX, (it + 1) * widthX).toMutableList()
            field.add(line)
        }
    }
    fun generateHints() {
        repeat(widthX) {x ->
            repeat(heightY) {y ->
                if (getCell(x, y).type != Type.MINE) {
                    var minesAround = 0
                    val nearCells = getNearCells(x, y)
                    for (cell in nearCells) if (cell.type == Type.MINE) minesAround += 1
                    if (minesAround != 0) setCell(x, y, Cell(minesAround))
                }
            }
        }
    }
    fun getAllIndexes(): MutableList<MutableList<Int>> {
        val indexes = mutableListOf<MutableList<Int>>()
        repeat(widthX) {x ->
            repeat(heightY) {y ->
                indexes.add(mutableListOf(x, y))
            }
        }
        return indexes
    }
    fun getAllCells(): MutableList<Cell> {
        val nearCells = mutableListOf<Cell>()
        for (indexes in getAllIndexes())
            nearCells.add(getCell(indexes[0], indexes[1]))
        return nearCells
    }
    fun getNearIndexes(x: Int, y: Int): MutableList<MutableList<Int>> {
        val nearCells = mutableListOf<MutableList<Int>>()
        for (offset_y in -1..1) {
            for (offset_x in -1..1) {
                val chek_y = y + offset_y
                val chek_x = x + offset_x
                if (chek_y in 0 until  heightY &&
                    chek_x in 0 until  widthX &&
                    !(offset_y == 0 && offset_x == 0))
                    nearCells.add(mutableListOf(chek_x, chek_y))
            }
        }

        return nearCells
    }
    fun getNearCells(x: Int, y: Int): MutableList<Cell> {
        val nearCells = mutableListOf<Cell>()
        for (indexes in getNearIndexes(x, y))
            nearCells.add(getCell(indexes[0], indexes[1]))
        return nearCells
    }
    fun checkWin(): Boolean {
        var hideCellCount = 0
        var minesFind = 0
        for (cell in getAllCells()) {
            if (!cell.isShow) hideCellCount += 1
            if (cell.type == Type.MINE && cell.isFlag) minesFind += 1
        }
        return (hideCellCount == mines) || (minesFind == mines)
    }
    fun cellFlag(x: Int, y: Int) {
        val checkCell = getCell(x, y)
        if (checkCell.isShow) {
            println("Cell is already open")
        } else {
            checkCell.isFlag = !checkCell.isFlag
            this.print()
            if (checkWin()) {
                println("Congratulations! You found all the mines!")
                gameEnd = true
            }
        }
    }
    fun cellOpen(x: Int, y: Int) {
        val openCell = getCell(x, y)
        var printField = false
        var showMsg = ""
        when {
            openCell.isShow -> {
                showMsg = "Cell is already open"
            }
            openCell.isFlag -> {
                showMsg = "Cell marked as mine"
            }
            openCell.type.promptIndex > 0 -> {
                openCell.show()
                printField = true
            }
            openCell.type == Type.EMPTY -> {
                val listEmpty = mutableListOf(mutableListOf(x, y))
                while (listEmpty.isNotEmpty()) {
                    val emptyX = listEmpty.first()[0]
                    val emptyY = listEmpty.first()[1]
                    getCell(emptyX, emptyY).show()
                    val nearIndexes = getNearIndexes(emptyX, emptyY)
                    listEmpty.remove(listEmpty.first())
                    for (near in nearIndexes) {
                        val nearX = near[0]
                        val nearY = near[1]
                        val nearCell = getCell(nearX, nearY)
                        when {
                            nearCell.type == Type.EMPTY && !nearCell.isShow ->
                                listEmpty.add(mutableListOf(nearX, nearY))
                            nearCell.type.promptIndex > 0 -> {
                                if (!nearCell.isShow) {
                                    nearCell.show()
                                }
                            }
                        }
                    }
                }
                printField = true
            }
            openCell.type == Type.MINE -> {
                for (cell in getAllCells()) if (cell.type == Type.MINE) cell.isShow = true
                gameEnd = true
                printField = true
                showMsg = "You stepped on a mine and failed!"
            }
        }
        if (checkWin()) {
            gameEnd = true
            printField = true
            showMsg = "Congratulations! You found all the mines!"
        }
        if (printField) this.print()
        if (showMsg != "") println(showMsg)
    }
    fun print(markup: Boolean = true) {
        if (markup) {
            var markupX = ""
            repeat(widthX) {markupX += (it + 1).toString()}
            println(" │$markupX│")
            println("—│${"—".repeat(widthX)}│")
        }
        repeat(heightY) {y ->
            if (markup) print("${y + 1}│")
            repeat(widthX) {x ->
                print(getCell(x, y).symbol)
            }
            if (markup) print("│")
            println()
        }
        if (markup) {
            println("—│${"—".repeat(widthX)}│")
        }
    }
}