package ishiyama.square

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

class BoardView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    var board = Board(9, 9)
    private var uniteSize = 0F

    var select = false
    private var point: Point = Point()
    private var menu: PointF = PointF()

    override fun onDraw(canvas: Canvas) {
        if (context is MainActivity)
            (context as MainActivity).textView.text = "turn:${board.turn}, score:${board.score*10000}" + when (board.status) {
                1 -> ", 1P won"
                2 -> ", 2P won"
                3 -> ", Draw"
                else -> ""
            }

        uniteSize = min(width.toFloat()/board.width, height.toFloat()/board.height)
        if (board.cpuMoved) {
            paint.apply {
                color = Color.GREEN
                isAntiAlias = true
                style = Paint.Style.FILL
            }
            canvas.drawRect(board.cpuPoint.x*uniteSize, board.cpuPoint.y*uniteSize, (board.cpuPoint.x+1)*uniteSize, (board.cpuPoint.y+1)*uniteSize, paint)
        }
        paint.apply {
            color = Color.GRAY
            strokeWidth = 4F
            isAntiAlias = true
            style = Paint.Style.STROKE
        }
        for (i in 0..board.width)
            canvas.drawLine(i*uniteSize, 0F, i*uniteSize, board.height*uniteSize, paint)
        for (i in 0..board.height)
            canvas.drawLine(0F, i*uniteSize, board.width*uniteSize, i*uniteSize, paint)
        paint.strokeWidth = 6F
        for (i in 0 until board.width) for (j in 0 until board.height) {
            paint.color = when (board.lines[i][j].slash) {
                1 -> Color.RED
                2 -> Color.BLUE
                else -> Color.TRANSPARENT
            }
            canvas.drawLine(i*uniteSize, (j+1)*uniteSize, (i+1)*uniteSize, j*uniteSize, paint)
            paint.color = when (board.lines[i][j].backslash) {
                1 -> Color.RED
                2 -> Color.BLUE
                else -> Color.TRANSPARENT
            }
            canvas.drawLine(i*uniteSize, j*uniteSize, (i+1)*uniteSize, (j+1)*uniteSize, paint)
        }
        if (select) {
            paint.apply {
                color = Color.GREEN
                strokeWidth = 8F
                style = Paint.Style.STROKE
            }
            canvas.drawRect(point.x*uniteSize, point.y*uniteSize, (point.x+1)*uniteSize, (point.y+1)*uniteSize, paint)
            paint.apply {
                color = Color.WHITE
                style = Paint.Style.FILL
            }
            canvas.drawRect(menu.x, menu.y, menu.x+3*uniteSize, menu.y+(5/3F)*uniteSize, paint)
            paint.apply {
                color = Color.BLACK
                strokeWidth = 6F
                style = Paint.Style.STROKE
            }
            canvas.drawRect(menu.x, menu.y, menu.x+3*uniteSize, menu.y+(5/3F)*uniteSize, paint)
            paint.strokeWidth = 4F
            canvas.drawRect(menu.x+(1/3F)*uniteSize, menu.y+(1/3F)*uniteSize, menu.x+(4/3F)*uniteSize, menu.y+(4/3F)*uniteSize, paint)
            canvas.drawRect(menu.x+(5/3F)*uniteSize, menu.y+(1/3F)*uniteSize, menu.x+(8/3F)*uniteSize, menu.y+(4/3F)*uniteSize, paint)
            paint.strokeWidth = 6F
            paint.color = when (board.turn%2) {
                1 -> Color.RED
                else -> Color.BLUE
            }
            canvas.drawLine(menu.x+(1/3F)*uniteSize, menu.y+(4/3F)*uniteSize, menu.x+(4/3F)*uniteSize, menu.y+(1/3F)*uniteSize, paint)
            canvas.drawLine(menu.x+(5/3F)*uniteSize, menu.y+(1/3F)*uniteSize, menu.x+(8/3F)*uniteSize, menu.y+(4/3F)*uniteSize, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null || event.action != MotionEvent.ACTION_DOWN || board.status != 0)
            return true
        if (board.cpuMoved) {
            board.cpuMoved = false
            board.update()
        }
        if (!board.cpuMoved) {
            if (select) {
                if (event.x < menu.x || event.x > menu.x+uniteSize*3
                    || event.y < menu.y || event.y > menu.y+uniteSize*4/3F) {
                    select = false
                } else if (event.x > menu.x+uniteSize/3 && event.x < menu.x+uniteSize*4/3
                    && event.y > menu.y+uniteSize/3 && event.y < menu.y+uniteSize*4/3) {
                    if (board.slash(point.x, point.y))
                        select = false
                } else if (event.x > menu.x+uniteSize*5/3 && event.x < menu.x+uniteSize*8/3
                    && event.y > menu.y+uniteSize/3 && event.y < menu.y+uniteSize*4/3) {
                    if (board.backslash(point.x, point.y))
                        select = false
                }
            } else {
                val x = (event.x / uniteSize).toInt()
                val y = (event.y / uniteSize).toInt()
                if (x >= board.width || y >= board.height)
                    return true
                if (board.lines[x][y].slash != 0 && board.lines[x][y].backslash != 0)
                    return true
                select = true
                point = Point(x, y)
                menu.x = when (x) {
                    0 -> 0F
                    board.width-1 -> (board.width-3)*uniteSize
                    else -> (x-1)*uniteSize
                }
                menu.y = when {
                    y < board.height-2 -> (y+4/3F)*uniteSize
                    else -> (y-2)*uniteSize
                }
            }
        }
        invalidate()
        return true
    }
}