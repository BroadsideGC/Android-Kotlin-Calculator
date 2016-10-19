package com.bigz.kotlin_calc

import android.os.Bundle
import android.app.Activity
import android.view.View
import android.widget.TextView
import android.widget.Toast

import java.math.BigInteger
import java.util.Stack

class MainActivity : Activity() {

    private var wind: TextView? = null
    private var st = Stack<Double>()
    private var cur: String = "0"
    private val num = mutableMapOf<Int, String>()
    private val bop = mutableMapOf<Int, BinOp>()
    private val uop = mutableMapOf<Int, UnOp>()
    private var t: Double = 0.0
    private var bad: Boolean = false
    private var op: BinOp = BinOp.NONE

    enum class BinOp {
        MUL, DIV, ADD, SUB, NONE
    }

    enum class UnOp {
        SQRT, UMIN
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        wind = findViewById(R.id.textView) as TextView
        num.put(R.id.button0, "0")
        num.put(R.id.button1, "1")
        num.put(R.id.button2, "2")
        num.put(R.id.button3, "3")
        num.put(R.id.button4, "4")
        num.put(R.id.button5, "5")
        num.put(R.id.button6, "6")
        num.put(R.id.button7, "7")
        num.put(R.id.button8, "8")
        num.put(R.id.button9, "9")
        num.put(R.id.button00, "00")
        bop.put(R.id.buttonDiv, BinOp.DIV)
        bop.put(R.id.buttonMul, BinOp.MUL)
        bop.put(R.id.buttonAdd, BinOp.ADD)
        bop.put(R.id.buttonSub, BinOp.SUB)
        uop.put(R.id.buttonSQRT, UnOp.SQRT)
        uop.put(R.id.buttonUm, UnOp.UMIN)
    }

    fun clickNum(v: View) {
        try {
            t = cur.toDouble()
        } catch (e: Exception) {
            if (cur.length != 0) bad = true
        }

        if (cur == "0") cur = ""
        if (num.containsKey(v.id) && !bad) {
            if (v.id == R.id.button00 && cur.length == 0) {
                cur = "0"
            } else {
                cur += num[v.id]
            }
        } else if (v.id == R.id.buttonPoint && !bad) {
            if (!cur.contains(".")) {
                if (cur.length == 0) {
                    cur += "0"
                }
                cur += "."
            }
        } else if (v.id == R.id.buttonC) {
            cur = "0"
            st.clear()
            bad = false
            t = 0.0
        }
        wind!!.text = cur
    }

    fun clickOp(v: View) {
        if (cur.length != 0) {
            try {
                t = cur.toDouble()
            } catch (e: Exception) {
                wind!!.text = "Очень жаль"
                Toast.makeText(this, "Очень жаль", Toast.LENGTH_SHORT).show()
                return
            }

        }
        if (v.id != R.id.buttonE) cur = ""
        if (v.id == R.id.buttonE && !bad && op != BinOp.NONE) {
            if (st.size == 1) {
                st.push(t)
                binOp()
                cur = print(st.peek() as Double)
                cur = if (cur == "NaN") "Не число" else if (cur.contains("Infinity")) "Ошибка" else cur
                wind!!.text = cur
            }

        } else if (bop.containsKey(v.id) && !bad) {
            st.push(t)
            if (st.size == 2) {
                binOp()
            }
            op = bop[v.id] as BinOp
        } else if (uop.containsKey(v.id) && !bad) {
            t = unOp(uop[v.id] as UnOp)
            cur = print(t)
            cur = if (cur == "NaN") "Не число" else cur
            wind!!.text = cur
        }
    }

    private fun unOp(o: UnOp): Double {
        when (o) {
            UnOp.SQRT -> {
                return Math.sqrt(t)
            }
            UnOp.UMIN -> {
                return if (Math.abs(t - 0) < 0.00000000001) 0.0 else t * -1
            }
        }
    }

    private fun binOp() {
        println("stack size "+st.size)
        val right = st.pop()
        val left = st.pop()
        when (op) {
            BinOp.ADD -> {
                st.push(left + right)
            }
            BinOp.SUB -> {
                st.push(left - right)
            }
            BinOp.MUL -> {
                st.push(left * right)
            }
            BinOp.DIV -> {
                if (Math.abs(t - 0) < 0.00000000001) {
                    bad = true
                }
                st.push(left / right)
            }
            BinOp.NONE -> {
                st.push(left)
            }
        }
        op = BinOp.NONE
        t = 0.0
        if (bad) {
            cur = if (st.peek().isNaN()) "Не число" else "Ошибка"
            wind!!.text = cur
            return
        }

        wind!!.text = print(st.peek())
    }

    private fun print(n: Double): String {
        if (n % 1 == 0.0) {
            return BigInteger.valueOf(n.toLong()).toString()
        } else {
            return n.toString()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        t = savedInstanceState.getDouble("last")
        cur = savedInstanceState.getString("curr")
        op = savedInstanceState.get("oper") as BinOp
        st = savedInstanceState.get("stack") as Stack<Double>
        wind!!.text = savedInstanceState.getString("disp")
        bad = savedInstanceState.getBoolean("flag")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putDouble("last", t)
        outState.putString("curr", cur)
        outState.putSerializable("oper", op)
        outState.putSerializable("stack", st)
        outState.putString("disp", wind!!.text.toString())
        outState.putSerializable("flag", bad)
        super.onSaveInstanceState(outState)
    }
}
