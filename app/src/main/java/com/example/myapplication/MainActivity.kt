package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Dialog
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), OnClickListener {

    // Звуки
    private lateinit var soundPool: SoundPool
    private var soundIdPong: Int = 0
    private var soundIdClick: Int = 0
    private var soundIdClickPause: Int = 0

    var gameView: GameView? = null
    var textScore: TextView? = null
    val game_window_percentage: Int = 78

    var dialog: Dialog? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализируем звук
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool =
            SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build()

        // Загрузка звуковых файлов
        soundIdPong = soundPool.load(this, R.raw.pong, 1)
        soundIdClick = soundPool.load(this, R.raw.click, 1)
        soundIdClickPause = soundPool.load(this, R.raw.click_pause, 1)

        // Создаем поле для игры
        gameView = findViewById<GameView>(R.id.game_view)

        // Создаем изменяемую надпись для счета
        // и передаем ее в gameView
        textScore = findViewById<TextView>(R.id.score_text)
        gameView?.setTextView(textScore!!)

        // Получаем размеры окна
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width_window = displayMetrics.widthPixels
        val height_window = displayMetrics.heightPixels

        // Изменяем размеры поля на идеальные размеры для размеров змейки
        val layoutParams = gameView?.layoutParams
        val block_size: Int = gameView!!.block_size
        layoutParams?.width = (width_window / block_size) * block_size
        layoutParams?.height = (((height_window - 200) * game_window_percentage / 100) / block_size) * block_size
        gameView?.layoutParams = layoutParams

        // Сообщаем змейке размеры поля
        gameView?.snake?.setFieldDimensions(layoutParams!!.width / block_size, layoutParams.height / block_size)
        // Сообщаем яблоку размеры поля
        gameView?.apple?.setFieldDimensions(layoutParams!!.width / block_size, layoutParams.height / block_size)
        // Генерируем начальное яблоко
        gameView?.apple?.generateNewApple(gameView?.snake!!.snake_body)
    }

    /**
     * Обработка нажатий кнопок
     */
    override fun onClick(p0: View?) {
        /**
         * Обработка нажатий кнопок направления движения для змейки
         *
         * Проверяем какая кнопка нажата и запускаем метод змейки с
         * соответствующим значением
         */
        if (p0!!.id == R.id.btn_left && gameView!!.snake.moveTo(Direction.left)) {
            soundPool.play(soundIdPong, 1f, 1f, 0, 0, 1f)
        }
        if (p0.id == R.id.btn_right && gameView!!.snake.moveTo(Direction.right)) {
            soundPool.play(soundIdPong, 1f, 1f, 0, 0, 1f)
        }
        if (p0.id == R.id.btn_up && gameView!!.snake.moveTo(Direction.up)) {
            soundPool.play(soundIdPong, 1f, 1f, 0, 0, 1f)
        }
        if (p0.id == R.id.btn_down && gameView!!.snake.moveTo(Direction.down)) {
            soundPool.play(soundIdPong, 1f, 1f, 0, 0, 1f)
        }
        /**
         * Обработка нажатий кнопок во время паузы
         *
         * Если нажата кнопка паузы показываем диалоговое окно
         *
         * Если на диалоговом окне нажата кнопка Resume
         * возобновляем игру
         *
         * Если нажата кнопка выхода из игры завершаем
         * работу приложения
         */
        if (p0.id == R.id.btn_pause) {
            soundPool.play(soundIdClickPause, 1f, 1f, 0, 0, 1f)
            gameView!!.onPause()
            // Создаем диалоговое окно паузы
            val inflater = LayoutInflater.from(this)
            val dialogLayout = inflater.inflate(R.layout.pause_game, null)
            dialog = Dialog(this)
            // Задаем начальные значения диалогового окна
            dialog?.setContentView(dialogLayout)
            dialog?.setCancelable(false)
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog?.show()
        }
        if (p0.id == R.id.btn_resume) {
            soundPool.play(soundIdClick, 5f, 5f, 0, 0, 1f)
            dialog?.dismiss()
            gameView!!.onStart()
        }
        if (p0.id == R.id.btn_exit2) {
            soundPool.play(soundIdClick, 5f, 5f, 0, 0, 1f)
            exitProcess(0)
        }
    }

    /**
     * Метод уничтожения объекта
     */
    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}