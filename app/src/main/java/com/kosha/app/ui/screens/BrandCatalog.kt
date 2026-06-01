package com.kosha.app.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.kosha.app.R

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
        Brand("brand_myntra", "Myntra", Color(0xFFFF3F6C), R.drawable.brand_myntra, "myntra.com"),
        
        // --- NEWLY ADDED BRANDS ---
        Brand("brand_canarabank", "Canara Bank", Color(0xFF000000), R.drawable.brand_canarabank, "canarabank.com"),
        Brand("brand_axisbank", "Axis Bank", Color(0xFF000000), R.drawable.brand_axisbank, "axisbank.com"),
        Brand("brand_kotak", "Kotak Mahindra Bank", Color(0xFF000000), R.drawable.brand_kotak, "kotak.com"),
        Brand("brand_meesho", "Meesho", Color(0xFF000000), R.drawable.brand_meesho, "meesho.com"),
        Brand("brand_blinkit", "BlinkIt", Color(0xFF000000), R.drawable.brand_blinkit, "blinkit.com"),
        Brand("brand_swiggy", "Swiggy", Color(0xFF000000), R.drawable.brand_swiggy, "swiggy.com"),
        Brand("brand_zomato", "Zomato", Color(0xFF000000), R.drawable.brand_zomato, "zomato.com"),
        Brand("brand_croma", "Croma", Color(0xFF000000), R.drawable.brand_croma, "croma.com"),
        Brand("brand_reliance_digital", "Reliance Digital", Color(0xFF000000), R.drawable.brand_reliance_digital, "reliancedigital.in"),
        Brand("brand_rapido", "Rapido", Color(0xFF000000), R.drawable.brand_rapido, "rapido.bike"),
        Brand("brand_google_pay", "Google Pay", Color(0xFF000000), R.drawable.brand_google_pay, "pay.google.com"),
        Brand("brand_cred", "CRED", Color(0xFF000000), R.drawable.brand_cred, "cred.club"),
        Brand("brand_makemytrip", "MakeMyTrip", Color(0xFF000000), R.drawable.brand_makemytrip, "makemytrip.com"),
        Brand("brand_goibibo", "Goibibo", Color(0xFF000000), R.drawable.brand_goibibo, "goibibo.com"),
        Brand("brand_redbus", "RedBus", Color(0xFF000000), R.drawable.brand_redbus, "redbus.in"),
        Brand("brand_tiktok", "TikTok", Color(0xFF000000), R.drawable.brand_tiktok, "tiktok.com"),
        Brand("brand_reddit", "Reddit", Color(0xFF000000), R.drawable.brand_reddit, "reddit.com"),
        Brand("brand_telegram", "Telegram", Color(0xFF000000), R.drawable.brand_telegram, "telegram.org"),
        Brand("brand_whatsapp", "WhatsApp", Color(0xFF000000), R.drawable.brand_whatsapp, "whatsapp.com"),
        Brand("brand_zee5", "ZEE5", Color(0xFF000000), R.drawable.brand_zee5, "zee5.com"),
        Brand("brand_sonyliv", "Sony LIV", Color(0xFF000000), R.drawable.brand_sonyliv, "sonyliv.com"),
        Brand("brand_steam", "Steam", Color(0xFF000000), R.drawable.brand_steam, "store.steampowered.com"),
        Brand("brand_epicgames", "Epic Games", Color(0xFF000000), R.drawable.brand_epicgames, "epicgames.com"),
        Brand("brand_xbox", "Xbox", Color(0xFF000000), R.drawable.brand_xbox, "xbox.com"),
        Brand("brand_microsoft_teams", "Microsoft Teams", Color(0xFF000000), R.drawable.brand_microsoft_teams, "teams.microsoft.com"),
        Brand("brand_gitlab", "GitLab", Color(0xFF000000), R.drawable.brand_gitlab, "gitlab.com"),
        Brand("brand_stackoverflow", "Stack Overflow", Color(0xFF000000), R.drawable.brand_stackoverflow, "stackoverflow.com"),
        Brand("brand_postman", "Postman", Color(0xFF000000), R.drawable.brand_postman, "postman.com"),
        Brand("brand_docker", "Docker", Color(0xFF000000), R.drawable.brand_docker, "docker.com"),
        Brand("brand_codepen", "CodePen", Color(0xFF000000), R.drawable.brand_codepen, "codepen.io"),
        Brand("brand_vercel", "Vercel", Color(0xFF000000), R.drawable.brand_vercel, "vercel.com"),
        Brand("brand_netlify", "Netlify", Color(0xFF000000), R.drawable.brand_netlify, "netlify.com"),
        Brand("brand_heroku", "Heroku", Color(0xFF000000), R.drawable.brand_heroku, "heroku.com"),
        Brand("brand_supabase", "Supabase", Color(0xFF000000), R.drawable.brand_supabase, "supabase.com"),
        Brand("brand_jira", "Jira", Color(0xFF000000), R.drawable.brand_jira, "atlassian.com"),
        Brand("brand_trello", "Trello", Color(0xFF000000), R.drawable.brand_trello, "trello.com"),
        Brand("brand_notion", "Notion", Color(0xFF000000), R.drawable.brand_notion, "notion.so"),
        Brand("brand_indmoney", "INDmoney", Color(0xFF000000), R.drawable.brand_indmoney, "indmoney.com"),
        Brand("brand_sharekhan", "Sharekhan", Color(0xFF000000), R.drawable.brand_sharekhan, "sharekhan.com"),
        Brand("brand_udemy", "Udemy", Color(0xFF000000), R.drawable.brand_udemy, "udemy.com"),
        Brand("brand_bankofindia", "Bank of India", Color(0xFF000000), R.drawable.brand_bankofindia, "bankofindia.co.in"),
        Brand("brand_centralbankofindia", "Central Bank of India", Color(0xFF000000), R.drawable.brand_centralbankofindia, "centralbankofindia.co.in"),
        Brand("brand_unionbankofindia", "Union Bank of India", Color(0xFF000000), R.drawable.brand_unionbankofindia, "unionbankofindia.co.in"),
        Brand("brand_india_post", "India Post Payments Bank", Color(0xFF000000), R.drawable.brand_india_post, "ippbonline.com"),
        Brand("brand_ola", "Ola", Color(0xFF000000), R.drawable.brand_ola, "olacabs.com"),
        Brand("brand_irctc", "IRCTC", Color(0xFF000000), R.drawable.brand_irctc, "irctc.co.in")
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
