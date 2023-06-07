package my.tic_tac_toe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import my.tic_tac_toe.databinding.ActivityMainBinding

// this is main class (test comment)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.toPlay.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        binding.toSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        setContentView(binding.root)
    }

    private fun getInfoAboutGame(): InfoGame {
        with(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)) {
            val time = getLong(GameActivity.PREF_TIME, 0L)
            val gameField = getString(GameActivity.PREF_GAME_FIELD, "")

            return if (gameField != null) {
                InfoGame(time, gameField)
            } else {
                InfoGame(0L, "")
            }
        }
    }

    fun ciFuncTest(): Int {
        return 0
    }

    data class InfoGame(val time: Long, val gameField: String)

    companion object {
        fun wowItsTestFunc(username: String): String {
            return "Hello $username!"
        }
    }
}
