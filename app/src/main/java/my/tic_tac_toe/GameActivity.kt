package my.tic_tac_toe

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import my.tic_tac_toe.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private lateinit var gameField: Array<Array<String>>

    private lateinit var settingsInfo: SettingsActivity.InfoSettings

    private lateinit var mediaPlayer: MediaPlayer

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
            makeStepOfUser(0, 0)
        }

        binding.field12.setOnClickListener {
            makeStepOfUser(0, 1)
        }

        binding.field13.setOnClickListener {
            makeStepOfUser(0, 2)
        }

        binding.field21.setOnClickListener {
            makeStepOfUser(1, 0)
        }

        binding.field22.setOnClickListener {
            makeStepOfUser(1, 1)
        }

        binding.field23.setOnClickListener {
            makeStepOfUser(1, 2)
        }

        binding.field31.setOnClickListener {
            makeStepOfUser(2, 0)
        }

        binding.field32.setOnClickListener {
            makeStepOfUser(2, 1)
        }

        binding.field33.setOnClickListener {
            makeStepOfUser(2, 2)
        }

        setContentView(binding.root)

        initGameField()

        settingsInfo = getSettingsInfo()

        mediaPlayer = MediaPlayer.create(this, R.raw.sound)
        mediaPlayer.isLooping = true
        setVolumeMediaPlayer(settingsInfo.soundLevel)

        binding.Chronometr.start()
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.release()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_POPUP_MENU) {
            if (resultCode == RESULT_OK) {
                mediaPlayer = MediaPlayer.create(this, R.raw.sound)
                mediaPlayer.isLooping = true
                val settingsInfo = getSettingsInfo()
                setVolumeMediaPlayer(settingsInfo.soundLevel)

                binding.Chronometr.start()
                mediaPlayer.start()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setVolumeMediaPlayer(soundVolume: Int) {
        val volume = soundVolume * 0.01
        mediaPlayer.setVolume(volume.toFloat(), volume.toFloat())
    }

    private fun initGameField() {
        gameField = arrayOf()

        for (i in 0..2) {
            var array = arrayOf<String>()
            for (j in 0..2) {
                array += " "
            }
            gameField += array
        }
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
            "00" -> binding.field11.setImageResource(resId)
            "01" -> binding.field12.setImageResource(resId)
            "02" -> binding.field13.setImageResource(resId)
            "10" -> binding.field21.setImageResource(resId)
            "11" -> binding.field22.setImageResource(resId)
            "12" -> binding.field23.setImageResource(resId)
            "20" -> binding.field31.setImageResource(resId)
            "21" -> binding.field32.setImageResource(resId)
            "22" -> binding.field33.setImageResource(resId)
        }
    }

    private fun makeStepOfUser(row: Int, column: Int) {
        if (isEmptyField(row, column)) {
            makeStep(row, column, PLAYER_SYMBOL)

            if (checkGameField(row, column, PLAYER_SYMBOL)) {
                showGameStatus(STATUS_PLAYER_WIN)
            } else if (!isFilledField()) {
                val stepOfAI = makeStepToAI()

                if (checkGameField(stepOfAI.row, stepOfAI.column, BOT_SYMBOL)) {
                    showGameStatus(STATUS_PLAYER_LOSE)
                } else if (isFilledField()) {
                    showGameStatus(STATUS_PLAYER_DRAW)
                }
            } else {
                showGameStatus(STATUS_PLAYER_DRAW)
            }
        } else {
            Toast.makeText(this, "Field is already occupied!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isEmptyField(row: Int, column: Int) : Boolean {
        return gameField[row][column] == " "
    }

    private fun makeStepToAI(): CellGameField {
        val settingsInfo = getSettingsInfo()
        return when (settingsInfo.difficulty) {
            0 -> makeEasyStepOfAI()
            1 -> makeNormalStepOfAI()
            2 -> makeHardStepOfAI()
            else -> CellGameField(0, 0)
        }
    }

    private fun makeEasyStepOfAI() : CellGameField {
        var randomRow: Int
        var randomColumn: Int

        do {
            randomRow = (0..2).random()
            randomColumn = (0..2).random()
        } while (!isEmptyField(randomRow, randomColumn))

        makeStep(randomRow, randomColumn, BOT_SYMBOL)

        return CellGameField(randomRow, randomColumn)
    }

    private fun makeNormalStepOfAI(): CellGameField {
        val randomChoice: Int = (0.. 2).random()
        return if (randomChoice == 0) {
            makeEasyStepOfAI()
        } else {
            makeHardStepOfAI()
        }
    }

    private fun makeHardStepOfAI(): CellGameField {
        var bestScore = Double.NEGATIVE_INFINITY
        var moveCell = CellGameField(0, 0)

        val board = gameField.map { it.clone() }.toTypedArray()

        board.forEachIndexed { indexRow, columns ->
            columns.forEachIndexed { indexColumn, _ ->
                if (board[indexRow][indexColumn] == " ") {
                    board[indexRow][indexColumn] = BOT_SYMBOL
                    val score = minimax(board, false)
                    board[indexRow][indexColumn] = " "
                    if (score > bestScore) {
                        bestScore = score
                        moveCell = CellGameField(indexRow, indexColumn)
                    }
                }
            }
        }

        makeStep(moveCell.row, moveCell.column, BOT_SYMBOL)

        return moveCell
    }

    private fun minimax(board: Array<Array<String>>, isMaximizing: Boolean): Double {
        val result = checkWinner(board)
        result?.let {
            return scores[result]!!
        }

        if (isMaximizing) {
            var bestScore = Double.NEGATIVE_INFINITY
            board.forEachIndexed { indexRow, columns ->
                columns.forEachIndexed { indexColumn, _ ->
                    if (board[indexRow][indexColumn] == " ") {
                        board[indexRow][indexColumn] = BOT_SYMBOL
                        val score = minimax(board, false)
                        board[indexRow][indexColumn] = " "
                        if (score > bestScore) {
                            bestScore = score
                        }
                    }
                }
            }
            return bestScore
        } else {
            var bestScore = Double.POSITIVE_INFINITY
            board.forEachIndexed { indexRow, columns ->
                columns.forEachIndexed { indexColumn, _ ->
                    if (board[indexRow][indexColumn] == " ") {
                        board[indexRow][indexColumn] = PLAYER_SYMBOL
                        val score = minimax(board, true)
                        board[indexRow][indexColumn] = " "
                        if (score < bestScore) {
                            bestScore = score
                        }
                    }
                }
            }
            return bestScore
        }
    }

    private fun checkGameField(x: Int, y: Int, symbol: String): Boolean {
        var col = 0
        var row = 0
        var diag = 0
        var rdiag = 0
        val n = gameField.size

        for (i in 0..2) {
            if (gameField[x][i] == symbol)
                col++
            if (gameField[i][y] == symbol)
                row++
            if (gameField[i][i] == symbol)
                diag++
            if (gameField[i][n - i - 1] == symbol)
                rdiag++
        }

        val settings = getSettingsInfo()
        return when (settings.rules) {
            1 -> {
                col == n
            }
            2 -> {
                row == n
            }
            3 -> {
                col == n || row == n
            }
            4 -> {
                diag == n || rdiag == n
            }
            5 -> {
                col == n || diag == n || rdiag == n
            }
            6 -> {
                row == n || diag == n || rdiag == n
            }
            7 -> {
                col == n || row == n || diag == n || rdiag == n
            }
            else -> {
                false
            }
        }
    }

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
            startActivityForResult(intent, REQUEST_POPUP_MENU)
            settingsInfo = getSettingsInfo()
            setVolumeMediaPlayer(settingsInfo.soundLevel)
        }

        toExit.setOnClickListener {
            val timePassed = SystemClock.elapsedRealtime() - binding.Chronometr.base
            saveGame(timePassed, convertGameFieldIntoString(gameField))
            dialog.dismiss()
            onBackPressed()
        }

        dialog.show()
    }

    private fun checkWinner(board: Array<Array<String>>): Int? {
        var countRowsUser = 0
        var countRowsAI = 0
        var countLeftDiagonalUser = 0
        var countLeftDiagonalAL = 0
        var countRightDiagonalUser = 0
        var countRightDiagonalAI = 0

        board.forEachIndexed { indexRow, columns ->
            if (columns.all { it == PLAYER_SYMBOL })
                return STATUS_PLAYER_WIN
            else if (columns.all { it == BOT_SYMBOL })
                return STATUS_PLAYER_LOSE

            countRowsUser = 0
            countRowsAI = 0

            columns.forEachIndexed { indexColumn, cell ->
                if (board[indexColumn][indexRow] == PLAYER_SYMBOL)
                    countRowsUser++
                else if (board[indexColumn][indexRow] == BOT_SYMBOL)
                    countRowsAI++

                if (indexRow == indexColumn && board[indexRow][indexColumn] == PLAYER_SYMBOL)
                    countLeftDiagonalUser++
                else if (indexRow == indexColumn && board[indexRow][indexColumn] == BOT_SYMBOL)
                    countLeftDiagonalAL++

                if (indexRow == 2 - indexColumn && board[indexRow][indexColumn] == PLAYER_SYMBOL)
                    countRightDiagonalUser++
                else if (indexRow == 2 - indexColumn && board[indexRow][indexColumn] == BOT_SYMBOL)
                    countRightDiagonalAI++
            }

            if (countRowsUser == 3 || countLeftDiagonalUser == 3 || countRightDiagonalUser == 3)
                return STATUS_PLAYER_WIN
            else if (countRowsAI == 3 || countLeftDiagonalAL == 3 || countRightDiagonalAI == 3)
                return STATUS_PLAYER_LOSE
        }

        board.forEach {
            if (it.find { it == " " } != null)
                return null
        }

        return STATUS_PLAYER_DRAW
    }

    private fun isFilledField() : Boolean {
        gameField.forEach { strings ->
            if (strings.find { it == " " } != null) return false }
        return true
    }

    private fun convertGameFieldIntoString(gameField: Array<Array<String>>) : String {
        val tempArray = arrayListOf<String>()
        gameField.forEach { tempArray.add(it.joinToString(";")) }
        return tempArray.joinToString("\n")
    }

    private fun saveGame(time: Long, gameField: String) {
        with(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit()) {
            putLong(PREF_TIME, time)
            putString(PREF_GAME_FIELD, gameField)
            apply()
        }
    }

    private fun getSettingsInfo(): SettingsActivity.InfoSettings{
        this.getSharedPreferences("game", MODE_PRIVATE).apply {

            val sound = getInt(PREF_SOUND_VALUE, 100)
            val level = getInt(PREF_DIFFICULTY, 1)
            val rules = getInt(PREF_RULES, 7)

            return SettingsActivity.InfoSettings(sound, level, rules)
        }
    }

    data class CellGameField(val row: Int, val column: Int)

    companion object {
        const val STATUS_PLAYER_WIN = 1
        const val STATUS_PLAYER_LOSE = 2
        const val STATUS_PLAYER_DRAW = 3

        const val PREF_TIME = "pref_time"
        const val PREF_GAME_FIELD = "pref_game_field"

        const val REQUEST_POPUP_MENU = 123

        val scores = hashMapOf(
            Pair(STATUS_PLAYER_WIN, -1.0), Pair(STATUS_PLAYER_LOSE, 1.0), Pair(STATUS_PLAYER_DRAW, 0.0)
        )

        const val PLAYER_SYMBOL = "X"
        const val BOT_SYMBOL = "0"
    }
}