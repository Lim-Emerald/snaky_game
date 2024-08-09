package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlin.system.exitProcess

class GameView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    // Размер блока поля
    val block_size: Int = 100

    // Создаем змейку
    var snake: Snake = Snake(block_size)
    // Создаем яблоко
    var apple: Apple = Apple(block_size)
    // Счет игры
    var score: Int = 0
    // Булева переменная о работе игры
    var is_running: Boolean = false
    // Текст счета
    var textScore: TextView? = null
    // Скорость таймера
    val timer_speed: Long = 200

    // Звуки
    private lateinit var soundPool: SoundPool
    private var soundIdGameOver: Int = 0
    private var soundIdScoreUp: Int = 0
    private var soundIdClick: Int = 0

    init {
        // Инициализация SoundPool
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool =
            SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build()

        // Загрузка звуковых файлов
        soundIdGameOver = soundPool.load(context, R.raw.game_over, 1)
        soundIdScoreUp = soundPool.load(context, R.raw.score_up, 1)
        soundIdClick = soundPool.load(context, R.raw.click, 1)
    }

    // Таймер
    var timer = object : CountDownTimer(Long.MAX_VALUE, timer_speed) {
        override fun onTick(p0: Long) {
            if (is_running) {
                if (snake.move()) {
                    // Если змейка "съела" увеличиваем счет и
                    // генерируем новое яблоко, при этом звучит
                    // музыкальное сопровождение
                    if (snake.snake_body[0].first == apple.position) {
                        soundPool.play(soundIdScoreUp, 1.2f, 1.2f, 0, 0, 1f)
                        textScore?.text = (++score).toString()
                        snake.increaseTail()
                        apple.generateNewApple(snake.snake_body)
                    }
                } else {
                    // Если змейка не смогла переместиться останавливаем
                    // игру и показываем окно окончания игры
                    onPause()
                    showFinishGameDialog()
                }
            }
            invalidate()
        }

        override fun onFinish() {

        }
    }

    // Запускаем игру во время создания
    init {
        onStart()
    }

    /**
     * Метод передачи текста счета
     */
    fun setTextView(textView: TextView) {
        textScore = textView
    }

    /**
     * Функция показа диалогового онка об окончании игры
     *
     * Создаем диалоговое окно на основе finish_game.xml
     * Обрабатываем нажатие кнопок при рестарте или выходе
     * из игры
     */
    @SuppressLint("InflateParams")
    private fun showFinishGameDialog() {

        // Создаем диалоговое окно
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.finish_game, null)
        val dialog = Dialog(context)
        dialog.setContentView(dialogLayout)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Показываем окно и запускаем мелодию
        dialog.show()
        soundPool.play(soundIdGameOver, 1f, 1f, 0, 0, 1f)

        // Создаем кнопки для обработки нажатий
        val restartButton = dialog.findViewById<Button>(R.id.btn_restart)
        val exitButton = dialog.findViewById<Button>(R.id.btn_exit)

        // Если нажата кнопка о повторе игры, то возвращаем змейку в начальную позицию,
        // изменяем значение работы игры на "true", запускаем таймер, диалоговое окно закрываем
        restartButton.setOnClickListener {
            soundPool.play(soundIdClick, 5f, 5f, 0, 0, 1f)
            score = 0
            textScore?.text = score.toString()
            snake.toBeginState()
            apple.generateNewApple(snake.snake_body)
            onStart()
            dialog.dismiss()
        }
        // Если нажата кнопка о заверщении, завершаем работу приложения
        exitButton.setOnClickListener {
            soundPool.play(soundIdClick, 5f, 5f, 0, 0, 1f)
            exitProcess(0)
        }
    }

    /**
     * Метод отрисовки игры
     *
     * Запускает отрисовку змейки через соответствующий метод
     */
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        apple.draw(canvas)
        snake.draw(canvas)
    }

    /**
     * Метод остановки игры
     */
    fun onPause() {
        is_running = false
        timer.cancel()
    }

    /**
     * Метод запуска игры
     */
    fun onStart() {
        is_running = true
        timer.start()
    }

    /**
     * Метод уничтожения объекта
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        soundPool.release()
    }
}