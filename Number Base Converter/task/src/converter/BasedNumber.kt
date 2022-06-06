package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

/**
 * Class that represents a number written in a base
 *
 * @param intRepr list with the value of each digit from units upwards
 * @param fracRepr list with the value of each digit from tenths downwards
 * @param base the base the number is written in
 */
class BasedNumber private constructor(private val intRepr: List<Int>,
                                      private val fracRepr: List<Int>,
                                      private val base: Int = 10) {

    /**
     * String representing this number
     *
     * @throws UnsupportedOperationException if the base is grater than 64 or smaller than 2
     */
    val stringRepr: String by lazy {
        if (base !in 2..64) throw UnsupportedOperationException("String representation only supported up to base 64")
        var str = ""
        for (i in intRepr) {
            val char = symbols[i].toString()
            str = char + str
        }
        if (fracRepr.isNotEmpty()) {
            str += "."
            for (i in fracRepr) {
                str += symbols[i].toString()
            }
        }
        str
    }

    /** Transforms this number into a base 10 BigDecimal */
    private fun toBigDecimal(): BigDecimal {
        var intResult = BigDecimal.ZERO
        for (i in intRepr.indices) {
            intResult += intRepr[i].toBigDecimal() * base.toBigDecimal().pow(i)
        }
        var fracResult = BigDecimal.ZERO
        for (i in fracRepr.indices) {
            fracResult +=
                fracRepr[i].toBigDecimal().setScale(10, RoundingMode.HALF_UP) /
                    base.toBigDecimal().pow(i + 1)
        }
        return intResult + fracResult
    }

    /**
     * Returns a new BasedNumber representing the same number as this in a new base
     *
     * @param targetBase the new base
     */
    fun toBase(targetBase: Int): BasedNumber {
        return fromBigDecimal(this.toBigDecimal(), targetBase)
    }

    companion object {
        /** The symbol used up to base 64 */
        const val symbols = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZÑabcdefghijklmnopqrstuvwxyzñ"

        /**
         * Creates a BasedNumber instance from a base 10 BigDecimal
         *
         * @param decimal the base 10 BigDecimal to convert
         * @param targetBase the base to convert to
         */
        fun fromBigDecimal(decimal: BigDecimal, targetBase: Int): BasedNumber {
            val intRepr = mutableListOf<Int>()

            var num = decimal.toBigInteger()
            while (num != BigInteger.ZERO) {
                intRepr.add((num % targetBase.toBigInteger()).toInt())
                num /= targetBase.toBigInteger()
            }

            val fracRepr = mutableListOf<Int>()

            var frac = decimal.dropIntegerPart()
            var count = 0
            while (frac.compareTo(BigDecimal.ZERO) != 0 && count < 10) {
                fracRepr.add((targetBase.toBigDecimal() * frac).toInt())
                frac = (targetBase.toBigDecimal() * frac).dropIntegerPart()
                count++
            }

            return BasedNumber(intRepr.toList(), fracRepr.toList(), targetBase)
        }

        /**
         * Creates a BasedNumber from a string representation in a given base
         *
         * @param stringRepr the string representation
         * @param sourceBase the base in which stringRepr represents the number
         *
         * @throws UnsupportedOperationException if the base is greater than 64 or smaller than 2
         */
        fun fromStringRepr(stringRepr: String, sourceBase: Int): BasedNumber {
            if (sourceBase !in  2..64) throw UnsupportedOperationException("String representation only supported up to base 64")
            val newStringRepr = if (stringRepr.contains(".")) stringRepr else "$stringRepr."
            val (stringReprInt, stringReprFrac) = newStringRepr.split(".").map {
                if (sourceBase <= 37) it.uppercase() else it
            }
            val intRepr = mutableListOf<Int>()
            for (i in stringReprInt.reversed()) {
                intRepr.add(symbols.indexOf(i))
            }
            val fracRepr = mutableListOf<Int>()
            for (i in stringReprFrac) {
                fracRepr.add(symbols.indexOf(i))
            }
            return BasedNumber(intRepr.toList(), fracRepr.toList(), sourceBase)
        }
    }

}
