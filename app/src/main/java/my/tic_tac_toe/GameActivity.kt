package my.tic_tac_toe

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import my.tic_tac_toe.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private lateinit var gameField: Array<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)

        binding.toPopupMenu.setOnClickListener {
            showPopupMenu()
        }

        binding.Exit.setOnClickListener {
            onBackPressed()
        }

        binding.field11.setOnClickListener {
            makeStepOfUser(1, 1)
        }

        binding.field12.setOnClickListener {
            makeStepOfUser(1, 2)
        }

        binding.field13.setOnClickListener {
            makeStepOfUser(1, 3)
        }

        binding.field21.setOnClickListener {
            makeStepOfUser(2, 1)
        }

        binding.field22.setOnClickListener {
            makeStepOfUser(2, 2)
        }

        binding.field23.setOnClickListener {
            makeStepOfUser(2, 3)
        }

        binding.field31.setOnClickListener {
            makeStepOfUser(3, 1)
        }

        binding.field32.setOnClickListener {
            makeStepOfUser(3, 2)
        }

        binding.field33.setOnClickListener {
            makeStepOfUser(3, 3)
        }

        setContentView(binding.root)

        initGameField()
    }

    private fun initGameField() {
        gameField = Array(3){ Array(3){" "} }
    }

    private fun makeStep(row: Int, column: Int, symbol: String) {
        gameField[row][column] = symbol

        makeStepUI("$row$column", symbol)
    }

    private fun makeStepUI(position: String, symbol: String) {
        val resId = when (symbol) {
            "X" -> R.drawable.cross
            "0" -> R.drawable.zero
            else -> return
        }

        when (position) {
            "11" -> binding.field11.setImageResource(resId)
            "12" -> binding.field12.setImageResource(resId)
            "13" -> binding.field13.setImageResource(resId)
            "21" -> binding.field21.setImageResource(resId)
            "22" -> binding.field22.setImageResource(resId)
            "23" -> binding.field23.setImageResource(resId)
            "31" -> binding.field31.setImageResource(resId)
            "32" -> binding.field32.setImageResource(resId)
            "33" -> binding.field33.setImageResource(resId)
        }
    }

    private fun makeStepOfUser(row: Int, column: Int) {
        if (isEmptyField(row, column)) {
            makeStep(row, column, "X")

            val status = checkGameField(row, column, "X")

            if (status.status) {
                showGameStatus(STATUS_PLAYER_WIN)
                return
            }

            if (!isFilledField()) {
                makeStepOfAI()
                val statusAI = checkGameField(row, column, "0")
                if (statusAI.status) {
                    showGameStatus(STATUS_PLAYER_LOSE)
                    return
                }
            } else {
                showGameStatus(STATUS_PLAYER_DRAW)
                return
            }

            if (isFilledField()) {
                showGameStatus(STATUS_PLAYER_DRAW)
                return
            }

        } else {
            Toast.makeText(this, "Field is already occupied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isEmptyField(row: Int, column: Int) : Boolean {
        return gameField[row][column] == " "
    }

    private fun makeStepOfAI() {
        var randRow = 0
        var randCol = 0

        do {
            randRow = (0..2).random()
            randCol = (0..2).random()
        } while (!isEmptyField(randRow, randCol))

        makeStep(randRow, randCol, "0")
    }

    private fun checkGameField(row: Int, column: Int, symbol: String) : StatusInfo {
        var sum_row = 0; var sum_column = 0; var mainDiagonal = 0; var secDiagonal = 0
        val n = gameField.size

        for (i in 0..2) {
            if (gameField[row][i] == symbol) sum_column++
            else if (gameField[i][column] == symbol) sum_row++
            else if (gameField[i][i] == symbol) mainDiagonal++
            else if (gameField[i][n - 1 - i] == symbol) secDiagonal++
        }

        return if (sum_column == n || sum_row == n || mainDiagonal == n || secDiagonal == n) StatusInfo(true, symbol)
        else StatusInfo(false, " ")
    }

    data class StatusInfo(val status: Boolean, val side: String)

    private fun showGameStatus(status: Int) {
        val dialog = Dialog(this, R.style.Theme_TicTacToe)
        with(dialog) {
            window?.setBackgroundDrawable(ColorDrawable(Color.argb(50, 0, 0, 0)))
            setContentView(R.layout.dialog_popup_status_game)
            setCancelable(true)
        }

        val image = dialog.findViewById<ImageView>(R.id.dialogImage)
        val text = dialog.findViewById<TextView>(R.id.dialogText)
        val button = dialog.findViewById<TextView>(R.id.dialogOK)

        button.setOnClickListener {
            onBackPressed()
        }

        when (status) {
            STATUS_PLAYER_WIN -> {
                image.setImageResource(R.drawable.win)
                text.text = getString(R.string.dialog_status_win)
            }
            STATUS_PLAYER_LOSE -> {
                image.setImageResource(R.drawable.lose)
                text.text = getString(R.string.dialog_status_lose)
            }
            STATUS_PLAYER_DRAW -> {
                image.setImageResource(R.drawable.draw)
                text.text = getString(R.string.dialog_status_draw)
            }
        }

        dialog.show()
    }

    private fun showPopupMenu() {
        val dialog = Dialog(this, R.style.Theme_TicTacToe)
        with(dialog) {
            window?.setBackgroundDrawable(ColorDrawable(Color.argb(50, 0, 0, 0)))
            setContentView(R.layout.dialog_popup_menu)
            setCancelable(true)
        }

        val toGame = dialog.findViewById<TextView>(R.id.contnueButton)
        val toSettings = dialog.findViewById<TextView>(R.id.settingsButton)
        val toExit = dialog.findViewById<TextView>(R.id.exitButton)

        toGame.setOnClickListener {
            dialog.hide()
        }

        toSettings.setOnClickListener {
            dialog.hide()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        toExit.setOnClickListener {
            dialog.dismiss()
            onBackPressed()
        }

        dialog.show()
    }

    private fun isFilledField() : Boolean {
        gameField.forEach { strings ->
            if (strings.find { it == " " } != null) return false }
        return true
    }

    companion object {
        const val STATUS_PLAYER_WIN = 1
        const val STATUS_PLAYER_LOSE = 2
        const val STATUS_PLAYER_DRAW = 3
    }
}