package com.example.myapplication

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.collections.ArrayList
import kotlin.math.min

class Snake(field_block_size: Int) {

    // Размер блока тела змейки
    val block_size: Int = field_block_size
    // Начальная длина змейки
    val begin_length = 3
    // Максимальная длина змейки
    var max_length = 0

    // Начальное положение змейки
    var begin_x: Int = 0
    var begin_y: Int = 0

    // Размеры поля
    var field_width: Int = 0
    var field_heigth: Int = 0

    /* Массив блоков тела змейки - нулевой элемент является головой
     * (изначально заполняется тремя блоками)
     *
     * Блок состоит из пары - Положение (x и y) и Направление блока
     */
    var snake_body: ArrayList<Pair<Pair<Int, Int>, Direction>> = arrayListOf()

    // Массив блоков тела змейки для бекапа
    var backup_snake_body: ArrayList<Pair<Pair<Int, Int>, Direction>> = arrayListOf()

    init {
        toBeginState()
    }

    /**
     * Установка размеров поля
     *
     * Получаем размеры - присваиваем их в соответствующие переменные,
     * устанавливаем максимальную длину змейки - (площадь поля - 1)
     * вызываем установку начального положения змейки
     */
    fun setFieldDimensions(wigth: Int, heigth: Int) {
        field_width = wigth
        field_heigth = heigth
        max_length = wigth * heigth - 1
        setBeginPosition()
    }

    /**
     * Установка начального положения змейки по размерам поля
     *
     * Голова ставиться примерно в центр поля, вызываем конструктор тела змейки
     * из начального положения
     */
    fun setBeginPosition() {
        begin_x = field_width / 2
        begin_y = (field_heigth - 1) / 2
        toBeginState()
    }

    /**
     * Возвращение змейки в начальное положение
     *
     * Задается тело из begin_length блоков, голова будет перемещена
     * примерно в центр поля, тело будет создано ниже головы, как будто
     * змейка "смотрит" вверх
     */
    fun toBeginState() {
        snake_body = arrayListOf(Pair(Pair(begin_x, begin_y), Direction.stop))
        repeat(begin_length - 1) {
            val previous_x: Int = snake_body.last().first.first
            val previous_y: Int = snake_body.last().first.second
            snake_body.add(Pair(Pair(previous_x, previous_y + 1), Direction.up))
        }
    }

    /**
     * Метод отрисовки змейки
     */
    fun draw(canvas: Canvas) {
        var green_l = 70
        var green_r = 70 + min(50, 2 * snake_body.size)
        for (i in 0..snake_body.size - 1) {
            val x: Float = snake_body[i].first.first.toFloat()
            val y: Float = snake_body[i].first.second.toFloat()
            val paint: Paint = Paint().apply {
                color = Color.argb(0xFF, 0x01, green_l + (green_r - green_l) * (i + 1) / snake_body.size, 0x20)
            }
            canvas.drawRect(x * block_size, y * block_size, (x + 1) * block_size, (y + 1) * block_size, paint)
        }
    }

    /**
     * Метод задания направления змейки
     *
     * обработка исключения:
     * если идет изменение направления на 180 градусов - не меняем направление
     * если направление совпадает, то тоже ничего не меняем и возвращаем "false"
     *
     * Если все ok возвращаем "true"
     */
    fun moveTo(d : Direction): Boolean {
        if (d == snake_body[0].second ||
            d == Direction.left && snake_body[1].second == Direction.right ||
            d == Direction.right && snake_body[1].second == Direction.left ||
            d == Direction.down && snake_body[1].second == Direction.up ||
            d == Direction.up && snake_body[1].second == Direction.down) {
            return false
        }
        snake_body[0] = Pair(snake_body[0].first, d)
        return true
    }

    /**
     * Метод перемещения змейки
     *
     * Все блоки тела кроме головы копируют координаты блока перед ними
     * Голова сдвигается вверх, вниз, вправо или влево в зависимости от направления direct
     *
     * Если direct - stop то метод прерывается, возвращается "true"
     *
     * Если при перемещении змейка пересечется со своим телом или границой карты
     * метод вернет "false" и вернет изначальное положение змейки, направление
     * direct изменит на stop
     *
     * Если ошибок не возникло возвращает "true"
     */
    fun move(): Boolean {
        if (snake_body[0].second == Direction.stop) {
            return true
        }

        // Сдвиг блоков тела, кроме головы
        backup_snake_body = ArrayList(snake_body)
        for (i in (snake_body.size - 1).downTo(1)) {
            snake_body[i] = snake_body[i - 1].copy()
        }

        // Сдвиг головы
        var new_head_x: Int = snake_body[0].first.first
        var new_head_y: Int = snake_body[0].first.second
        val direct: Direction = snake_body[0].second
        if (direct == Direction.left) {
            new_head_x -= 1
        }
        if (direct == Direction.right) {
            new_head_x += 1
        }
        if (direct == Direction.up) {
            new_head_y -= 1
        }
        if (direct == Direction.down) {
            new_head_y += 1
        }
        snake_body[0] = Pair(Pair(new_head_x, new_head_y), direct)

        // Обработка исключений

        // Проверка выхода за границы карты
        if (new_head_x < 0 || new_head_x >= field_width || new_head_y < 0 || new_head_y >= field_heigth) {
            snake_body = backup_snake_body
            return false
        }

        // Проверка пересечения головы с телом
        for (i in 1..snake_body.size - 1) {
            if (snake_body[0].first == snake_body[i].first) {
                snake_body = backup_snake_body
                return false
            }
        }

        // Если всё ok, возвращаем "true"
        return true
    }

    /**
     * Метод увеличения хвоста змейки
     *
     * Из backup_snake_body берется положения хвоста и присоединяется к новому телу
     *
     * Если достигнута максимальная длина змейки, то метод прерывается
     *
     * Будем считать что пересечений с хвостом не возникнет, т.к. если хвост появился
     * то он может столкнуться только с головой, но голова только что съела яблоко
     * тогда значит хвост до этого уже был в точке яблока - чего быть не может
     */
    fun increaseTail() {
        if (snake_body.size == max_length) {
            return
        }
        snake_body.add(backup_snake_body.last())
    }

}