package ishiyama.square

import android.graphics.Point
import java.lang.Math.random
import java.lang.Thread.sleep
import java.util.*
import kotlin.math.pow

class Board(val width: Int, val height: Int) {

    class Grid {
        var slash = 0
        var backslash = 0
    }

    var turn = 1;
    var lines = Array(width) {Array(height) {Grid()} }
    var status = 0
    var score = 0F
    var cpu1 = false
    var cpu2 = false
    var cpuMoved = false
    var cpuPoint = Point(-1, -1)

    fun clone(): Board {
        return Board(width, height).let {
            it.turn = turn
            for (i in 0 until width) for (j in 0 until height) {
                it.lines[i][j].slash = lines[i][j].slash
                it.lines[i][j].backslash = lines[i][j].backslash
            }
            it.status = status
            it.score = score
            return it
        }
    }

    fun slash(x: Int, y: Int): Boolean {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return false
        if (lines[x][y].slash != 0)
            return false
        lines[x][y].slash = -(turn%2-2)
        println("($x, $y, s)")
        turn++
        update()
        return true
    }

    fun backslash(x: Int, y: Int): Boolean {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return false
        if (lines[x][y].backslash != 0)
            return false
        lines[x][y].backslash = -(turn%2-2)
        println("($x, $y, bs)")
        turn++
        update()
        return true
    }

    fun update() {
        score = 0F
        if (turn > width * height * 2) {
            status = 3
            return
        }
        for (x in 0 until width) for (y in 0 until height) {
            for (i in 1 until width) {
                if (x + i >= width || y - i / 2 < 0 || y + i / 2 >= height) break
                val count = Array(3) { 0 }
                for (j in 0..i / 2) {
                    count[lines[x + j][y - j].backslash]++
                    count[lines[x + j][y + j].slash]++
                    count[lines[x + i - j][y - j].slash]++
                    count[lines[x + i - j][y + j].backslash]++
                }
                addScore(count)
            }
            for (i in 0 until width) {
                if (x+i >= width || y-i/2 < 0 || y+1+i/2 >= height) break
                val count = Array(3) {0}
                for (j in 0..i/2) {
                    count[lines[x+j][y-j].slash]++
                    count[lines[x+j][y+1+j].backslash]++
                    count[lines[x+i-j][y-j].backslash]++
                    count[lines[x+i-j][y+1+j].slash]++
                }
                addScore(count)
            }
        }
        if (status == 0 && !cpuMoved)
            if ((cpu1 && turn%2 == 1) || (cpu2 && turn%2 == 0))
                moveBest()
    }

    private fun addScore(count: Array<Int>) {
        if ((count[1] == 0 && count[2] == 0) || (count[1] != 0 && count[2] != 0)) return
        for (i in 1..2) if (count[i] != 0) {
            if (count[0] == 0) status = i
            score += (if (i == 1) 1 else -1) * (if (i%2 == turn%2) 2 else 1) * (1/(count[0]+1F)).pow(4)
        }
    }

    fun moveBest() {
        var bestScore = -9999F
        var bestX = -1
        var bestY = -1
        var bestS = true
        val sign = if (turn%2 == 0) -1 else 1
        for (x in 0 until width) for (y in 0 until height) for (s in Array(2) { i -> i == 0 }) {
            val nextBoard = clone()
            val canPut = if (s) nextBoard.slash(x, y) else nextBoard.backslash(x, y)
            if (!canPut) continue
            val nextScore = sign * nextBoard.score * (random()/5+0.9).toFloat()
            if (bestScore < nextScore) {
                bestScore = nextScore
                bestX = x
                bestY = y
                bestS = s
            }
        }
        if (bestX == -1) {
            status = -1
        } else {
            cpuPoint.x = bestX
            cpuPoint.y = bestY
            cpuMoved = true
            if (bestS) slash(bestX, bestY)
            else backslash(bestX, bestY)
        }
    }

}
