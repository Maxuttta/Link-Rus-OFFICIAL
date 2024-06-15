package link.download.ru

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import link.download.ru.databinding.ActivityChatWithGptBinding

class ChatWithGPT : AppCompatActivity() {
    private lateinit var binding: ActivityChatWithGptBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatWithGptBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.webView.webViewClient = WebViewClient()
        binding.webView.loadUrl("https://link-messenger.ru/gpt/")

        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true

    }
}