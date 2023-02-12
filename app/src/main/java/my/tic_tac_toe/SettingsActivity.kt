package my.tic_tac_toe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import my.tic_tac_toe.databinding.ActivitySettingsBinding

const val PREF_SOUND_VALUE = "my.tick_tac_toe.SOUND"
const val PREF_DIFFICULTY = "my.tick_tac_toe.DIFFICULTY"
const val PREF_RULES = "my.tick_tac_toe.RULES"

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private var currentSoundLevel = 0
    private var currentDifficulty = 0
    private var currentRules = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)

        val settings = getSettingsInfo()

        currentSoundLevel = settings.soundLevel
        currentRules = settings.rules
        currentDifficulty = settings.difficulty

        when(currentRules) {
            1 -> binding.VerticalWin.isChecked = true
            2 -> binding.HorizontalWin.isChecked = true
            3 -> {
                binding.VerticalWin.isChecked = true
                binding.HorizontalWin.isChecked = true
            }
            4 -> binding.DiagonalWin.isChecked = true
            5 -> {
                binding.DiagonalWin.isChecked = true
                binding.VerticalWin.isChecked = true
            }
            6 -> {
                binding.DiagonalWin.isChecked = true
                binding.HorizontalWin.isChecked = true
            }
            7 -> {
                binding.VerticalWin.isChecked = true
                binding.HorizontalWin.isChecked = true
                binding.DiagonalWin.isChecked = true
            }
        }

        binding.difficultyTitle.text = resources.getStringArray(R.array.game_difficulty)[currentDifficulty]
        binding.VolumeBar.progress = currentSoundLevel

        binding.toMainMenu.setOnClickListener {
            setResult(RESULT_OK)
            updateDifficulty(currentDifficulty)
            updateRules(currentRules)
            updateSoundValue(currentSoundLevel)
            onBackPressed()
        }

        binding.previousArrow.setOnClickListener {
            currentDifficulty--

            if (currentDifficulty == 0) {
                binding.previousArrow.visibility = View.INVISIBLE
            } else if (currentDifficulty == 1) {
                binding.nextArrow.visibility = View.VISIBLE
            }

            binding.difficultyTitle.text = resources.getStringArray(R.array.game_difficulty)[currentDifficulty]

            updateDifficulty(currentDifficulty)
        }

        binding.nextArrow.setOnClickListener {
            currentDifficulty++

            if (currentDifficulty == 2) {
                binding.nextArrow.visibility = View.INVISIBLE
            } else if (currentDifficulty == 1) {
                binding.previousArrow.visibility = View.VISIBLE
            }

            binding.difficultyTitle.text = resources.getStringArray(R.array.game_difficulty)[currentDifficulty]

            updateDifficulty(currentDifficulty)
        }

        if (currentDifficulty == 0) {
            binding.previousArrow.visibility = View.INVISIBLE
        } else if (currentDifficulty == 2) {
            binding.nextArrow.visibility = View.INVISIBLE
        }

        binding.VolumeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                currentSoundLevel = value
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                updateSoundValue(currentSoundLevel)
            }
        })

        binding.HorizontalWin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) currentRules++
            else currentRules--

            updateRules(currentRules)
        }

        binding.VerticalWin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) currentRules += 2
            else currentRules -= 2

            updateRules(currentRules)
        }

        binding.DiagonalWin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) currentRules += 4
            else currentRules -= 4

            updateRules(currentRules)
        }

        setContentView(binding.root)
    }

    private fun updateSoundValue(value: Int) {
        with(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit()) {
            putInt(PREF_SOUND_VALUE, value)
            apply()
        }

        setResult(RESULT_OK)
    }

    private fun updateDifficulty(difficulty: Int) {
        with(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit()) {
            putInt(PREF_DIFFICULTY, difficulty)
            apply()
        }

        setResult(RESULT_OK)
    }

    private fun updateRules(rules: Int) {
        with(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit()) {
            putInt(PREF_RULES, rules)
            apply()
        }

        setResult(RESULT_OK)
    }

    private fun getSettingsInfo(): InfoSettings {
        this.getSharedPreferences("game", MODE_PRIVATE).apply {

            val sound = getInt(PREF_SOUND_VALUE, 100)
            val level = getInt(PREF_DIFFICULTY, 1)
            val rules = getInt(PREF_RULES, 7)

            return InfoSettings(sound, level, rules)
        }
    }

    data class InfoSettings(val soundLevel: Int, val difficulty: Int, val rules: Int)
}