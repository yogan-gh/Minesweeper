package minesweeper

class Field(val widthX: Int = 9, val heightY: Int = 9, _mines: Int = 9) {
    val size = heightY * widthX
    val mines = if (_mines in 1..size) _mines else if (_mines < 1) 1 else size - 1
    val field = MutableList(heightY) { MutableList(widthX) { Cell(CellType.EMPTY) } } //filed[Y][X]
    var gameEnd = false
    enum class Symbol(val symbol: Char) {
        MINE('X'),
        EMPTY('/'),
        MASK('.'),
        FLAG('*'),
        PROMPT_1('1'),
        PROMPT_2('2'),
        PROMPT_3('3'),
        PROMPT_4('4'),
        PROMPT_5('5'),
        PROMPT_6('6'),
        PROMPT_7('7'),
        PROMPT_8('8');
    }
    enum class CellType(val symbol: Char, val promptIndex: Int = 0) {
        MINE(Symbol.MINE.symbol),
        EMPTY(Symbol.EMPTY.symbol),
        PROMPT_1(Symbol.PROMPT_1.symbol, 1),
        PROMPT_2(Symbol.PROMPT_2.symbol, 2),
        PROMPT_3(Symbol.PROMPT_3.symbol, 3),
        PROMPT_4(Symbol.PROMPT_4.symbol, 4),
        PROMPT_5(Symbol.PROMPT_5.symbol, 5),
        PROMPT_6(Symbol.PROMPT_6.symbol, 6),
        PROMPT_7(Symbol.PROMPT_7.symbol, 7),
        PROMPT_8(Symbol.PROMPT_8.symbol, 8);
    }
    inner class Cell {
        val type: CellType
        var isShow: Boolean
        var isFlag: Boolean = false

        constructor(cellType: CellType, isShow: Boolean = false) {
            this.type = cellType
            this.isShow = isShow
        }

        constructor(promptIndex: Int, isShow: Boolean = false) {
            var promptCellType = CellType.EMPTY
            for (enum in CellType.values()) {
                if (promptIndex == enum.promptIndex) promptCellType = enum
            }
            this.type = promptCellType
            this.isShow = isShow
        }
        fun show() {
            this.isShow = true
            this.isFlag = true
        }
        val symbol: Char
            get() = when {
                isShow -> type.symbol
                isFlag -> Symbol.FLAG.symbol
                else -> Symbol.MASK.symbol
            }
    }
    fun getCell(x: Int, y: Int): Cell = field[y][x]
    fun setCell(x: Int, y: Int, cell: Cell) {
        field[y][x] = cell
    }
    fun generateMines(startX: Int = -1, startY: Int = -1) {
        field.clear()
        val fieldLine =
            (MutableList(size - mines) {Cell(CellType.EMPTY)} + MutableList(mines) {Cell(CellType.MINE)}).shuffled().toMutableList()
        val start = (startY * widthX) + startX
        if (fieldLine[start].type == CellType.MINE) {
            fieldLine[start] = Cell(CellType.EMPTY)
            val emptyIndexes = mutableListOf<Int>()
            for (index in 0..fieldLine.lastIndex) {
                if (fieldLine[index].type == CellType.EMPTY) emptyIndexes.add(index)
            }
            fieldLine[emptyIndexes.shuffled().first()] = Cell(CellType.MINE)
        }
        repeat(heightY) {
            val line = fieldLine.subList(it * widthX, (it + 1) * widthX).toMutableList()
            field.add(line)
        }
    }
    fun generateHints() {
        repeat(widthX) {x ->
            repeat(heightY) {y ->
                if (getCell(x, y).type != CellType.MINE) {
                    var minesAround = 0
                    val nearCells = getNearCells(x, y)
                    for (cell in nearCells) if (cell.type == CellType.MINE) minesAround += 1
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
            if (cell.type == CellType.MINE && cell.isFlag) minesFind += 1
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
            openCell.type == CellType.EMPTY -> {
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
                            nearCell.type == CellType.EMPTY && !nearCell.isShow ->
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
            openCell.type == CellType.MINE -> {
                for (cell in getAllCells()) if (cell.type == CellType.MINE) cell.isShow = true
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