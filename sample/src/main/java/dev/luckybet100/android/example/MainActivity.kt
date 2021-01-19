package dev.luckybet100.android.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import dev.luckybet100.android.dragging.DragAndRemoveLayout
import dev.luckybet100.android.dragging.DraggingLayout
import dev.luckybet100.android.dragging.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (container as DragAndRemoveLayout).onDragStartedListener = object : DraggingLayout.OnDragStartedListener {
            override fun onDragStarted(started: Boolean) {
                Toast.makeText(this@MainActivity, started.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    }
}
