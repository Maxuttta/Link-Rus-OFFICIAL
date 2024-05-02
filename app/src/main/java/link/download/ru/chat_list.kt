package link.download.ru

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import link.download.ru.databinding.ActivityChatListBinding

class chat_list : AppCompatActivity() {
    private lateinit var binding: ActivityChatListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}