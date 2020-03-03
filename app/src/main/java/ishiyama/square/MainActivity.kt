package ishiyama.square

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var textView: TextView
    lateinit var boardView: BoardView
    lateinit var switch1: Switch
    lateinit var switch2: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        boardView = findViewById(R.id.boardView)
        switch1 = findViewById(R.id.switch1)
        switch2 = findViewById(R.id.switch2)
        textView.text = "start"
        switch1.setOnCheckedChangeListener { _, isChecked -> boardView.apply {
            board.cpu1 = isChecked
            board.update()
            invalidate()
        } }
        switch2.setOnCheckedChangeListener { _, isChecked -> boardView.apply {
            board.cpu2 = isChecked
            board.update()
            invalidate()
        } }
    }

    fun resetBoard(view: View) {
        boardView.apply {
            board = Board(9,9)
            board.apply {
                cpu1 = switch1.isChecked
                cpu2 = switch2.isChecked
                update()
            }
            select = false
            invalidate()
        }
    }

}
