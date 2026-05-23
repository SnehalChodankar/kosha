package com.locker.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.locker.R

data class Brand(
    val id: String,
    val name: String,
    val color: Color,
    @DrawableRes val logoRes: Int,
    val defaultUrl: String? = null
)

object BrandCatalog {
    val allBrands = listOf(
        // Social & Comms
        Brand("brand_facebook", "Facebook", Color(0xFF1877F2), R.drawable.brand_facebook, "facebook.com"),
        Brand("brand_instagram", "Instagram", Color(0xFFE1306C), R.drawable.brand_instagram, "instagram.com"),
        Brand("brand_twitter", "Twitter/X", Color(0xFF000000), R.drawable.brand_twitter, "twitter.com"),
        Brand("brand_linkedin", "LinkedIn", Color(0xFF0A66C2), R.drawable.brand_linkedin, "linkedin.com"),
        Brand("brand_snapchat", "Snapchat", Color(0xFFFFFC00), R.drawable.brand_snapchat, "snapchat.com"),
        Brand("brand_discord", "Discord", Color(0xFF5865F2), R.drawable.brand_discord, "discord.com"),
        Brand("brand_slack", "Slack", Color(0xFF4A154B), R.drawable.brand_slack, "slack.com"),
        Brand("brand_gmail", "Gmail", Color(0xFFEA4335), R.drawable.brand_gmail, "gmail.com"),

        // Finance & Banking (Global + India)
        Brand("brand_chase", "Chase", Color(0xFF117ACA), R.drawable.brand_chase, "chase.com"),
        Brand("brand_bankofamerica", "Bank of Amer.", Color(0xFFE31837), R.drawable.brand_bankofamerica, "bankofamerica.com"),
        Brand("brand_paypal", "PayPal", Color(0xFF003087), R.drawable.brand_paypal, "paypal.com"),
        Brand("brand_coinbase", "Coinbase", Color(0xFF0052FF), R.drawable.brand_coinbase, "coinbase.com"),
        Brand("brand_amex", "Amex", Color(0xFF002663), R.drawable.brand_amex, "americanexpress.com"),
        Brand("brand_sbi", "SBI", Color(0xFF004990), R.drawable.brand_sbi, "onlinesbi.sbi"),
        Brand("brand_hdfc", "HDFC", Color(0xFF004B8D), R.drawable.brand_hdfc, "hdfcbank.com"),
        Brand("brand_icici", "ICICI", Color(0xFFF37021), R.drawable.brand_icici, "icicibank.com"),
        Brand("brand_bankofbaroda", "Bank of Baroda", Color(0xFFF1592A), R.drawable.brand_bankofbaroda, "bankofbaroda.in"),
        Brand("brand_paytm", "Paytm", Color(0xFF002970), R.drawable.brand_paytm, "paytm.com"),
        Brand("brand_phonepe", "PhonePe", Color(0xFF5F259F), R.drawable.brand_phonepe, "phonepe.com"),
        Brand("brand_groww", "Groww", Color(0xFF00D09C), R.drawable.brand_groww, "groww.in"),
        Brand("brand_zerodha", "Zerodha", Color(0xFF387ED1), R.drawable.brand_zerodha, "zerodha.com"),

        // Work & Tech
        Brand("brand_google", "Google", Color(0xFFDB4437), R.drawable.brand_google, "google.com"),
        Brand("brand_microsoft", "Microsoft", Color(0xFF00A4EF), R.drawable.brand_microsoft, "microsoft.com"),
        Brand("brand_github", "GitHub", Color(0xFF24292E), R.drawable.brand_github, "github.com"),
        Brand("brand_aws", "AWS", Color(0xFFFF9900), R.drawable.brand_aws, "aws.amazon.com"),
        Brand("brand_icloud", "iCloud", Color(0xFF555555), R.drawable.brand_icloud, "icloud.com"),
        Brand("brand_windows", "Windows", Color(0xFF00A4EF), R.drawable.brand_windows, "windows.com"),
        Brand("brand_apple", "Apple", Color(0xFF444444), R.drawable.brand_apple, "apple.com"),

        // Entertainment & Shopping
        Brand("brand_netflix", "Netflix", Color(0xFFE50914), R.drawable.brand_netflix, "netflix.com"),
        Brand("brand_amazon", "Amazon", Color(0xFFFF9900), R.drawable.brand_amazon, "amazon.com"),
        Brand("brand_spotify", "Spotify", Color(0xFF1DB954), R.drawable.brand_spotify, "spotify.com"),
        Brand("brand_uber", "Uber", Color(0xFF000000), R.drawable.brand_uber, "uber.com"),
        Brand("brand_hotstar", "Hotstar", Color(0xFF141B29), R.drawable.brand_hotstar, "hotstar.com"),
        Brand("brand_primevideo", "Prime Video", Color(0xFF00A8E1), R.drawable.brand_primevideo, "primevideo.com"),
        Brand("brand_amazonmusic", "Amazon Music", Color(0xFF00A8E1), R.drawable.brand_amazonmusic, "music.amazon.com"),
        Brand("brand_flipkart", "Flipkart", Color(0xFF2874F0), R.drawable.brand_flipkart, "flipkart.com"),
        Brand("brand_myntra", "Myntra", Color(0xFFFF3F6C), R.drawable.brand_myntra, "myntra.com")
    )

    fun getBrandById(id: String): Brand? = allBrands.find { it.id == id }
    
    // For fuzzy matching when adding manually
    fun findBrandByTitle(title: String): Brand? {
        val lowerTitle = title.lowercase()
        return allBrands.firstOrNull {
            lowerTitle.contains(it.name.lowercase()) || it.name.lowercase().contains(lowerTitle)
        }
    }
}
