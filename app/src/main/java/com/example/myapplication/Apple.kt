package com.example.myapplication

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Apple(field_block_size: Int) {

    // Размер блока
    val block_size: Int = field_block_size

    // Положение яблока
    var position: Pair<Int, Int> = Pair(0, 0)

    // Границы поля
    var field_width: Int = 0
    var field_heigth: Int = 0

    fun setFieldDimensions(wigth: Int, heigth: Int) {
        field_width = wigth
        field_heigth = heigth
    }

    fun generateNewApple(bad_positions: ArrayList<Pair<Pair<Int, Int>, Direction>>) {
        var new_x: Int = 0
        var new_y: Int = 0
        if (field_width * field_heigth > 2 * bad_positions.size) {
            var not_create_new: Boolean = true
            while (not_create_new) {
                new_x = (0..field_width - 1).random()
                new_y = (0..field_heigth - 1).random()
                not_create_new = false
                for (el in bad_positions) {
                    if (el.first == Pair(new_x, new_y)) {
                        not_create_new = true
                        break
                    }
                }
            }
        } else {
            var not_used_positions: ArrayList<Pair<Int, Int>> = arrayListOf()
            for (i in 0..field_width - 1) {
                for (j in 0..field_heigth - 1) {
                    var bad_pos: Boolean = false
                    for (el in bad_positions) {
                        if (el.first == Pair(i, j)) {
                            bad_pos = true
                        }
                    }
                    if (bad_pos)
                        continue
                    not_used_positions.add(Pair(i, j))
                }
            }
            val rand_id = (0..not_used_positions.size - 1).random()
            new_x = not_used_positions[rand_id].first
            new_y = not_used_positions[rand_id].second
        }
        position = Pair(new_x, new_y)
    }

    fun draw(canvas: Canvas) {
        val x: Float = position.first.toFloat()
        val y: Float = position.second.toFloat()
        val paint: Paint = Paint().apply {
            color = Color.argb(0xFF, 0x80, 0, 0)
        }
        canvas.drawRect(x * block_size, y * block_size, (x + 1) * block_size, (y + 1) * block_size, paint)
    }
}
