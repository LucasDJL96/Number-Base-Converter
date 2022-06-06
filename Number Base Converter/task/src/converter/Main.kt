package converter

import java.math.BigDecimal
import java.math.RoundingMode

/** Main method. Controls the flow of the program */
fun main() {
    while (true) {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        val input = readln()
        if (input == "/exit") return
        val (source, target) = input.split(" ").map(String::toInt)
        while (true) {
            println("Enter number in base $source to convert to base $target (To go back type /back)")
            val newInput = readln()
            if (newInput == "/back") break
            val basedSource = BasedNumber.fromStringRepr(newInput, source)
            val basedTarget = basedSource.toBase(target)
            val conversion = formatConversion(basedTarget.stringRepr, newInput.contains("."))
            println("Conversion result: $conversion")
        }
    }
}
/** Returns a new BigDecimal with only de fractional part of this BigDecimal */
fun BigDecimal.dropIntegerPart(): BigDecimal {
    return this - this.setScale(0, RoundingMode.FLOOR)
}

/**
 * Formats the conversion result to have five digits after the decimal point
 *
 * @param convResult the string to format
 * @param withPoint whether to force the result to have a decimal point
 */
fun formatConversion(convResult: String, withPoint: Boolean): String {
    var result = convResult
    if (withPoint && !convResult.contains(".")) {
        result += "."
    }
    if (result.contains(".")) {
        repeat (5 - result.split(".")[1].length) {
            result += "0"
        }
        result = result.substring(0, 6 + result.indexOf("."))
    }
    return result
}
