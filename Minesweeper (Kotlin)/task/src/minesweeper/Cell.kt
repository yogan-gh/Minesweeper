package minesweeper

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
enum class Type(val symbol: Char, val promptIndex: Int = 0) {
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
class Cell {
    val type: Type
    var isShow: Boolean
    var isFlag: Boolean = false

    constructor(type: Type, isShow: Boolean = false) {
        this.type = type
        this.isShow = isShow
    }

    constructor(promptIndex: Int, isShow: Boolean = false) {
        var promptType = Type.EMPTY
        for (enum in Type.values()) {
            if (promptIndex == enum.promptIndex) promptType = enum
        }
        this.type = promptType
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