$brands = @(
    @("Bank of India", "brand_bankofindia", "bankofindia.co.in", "0xFF000000"),
    @("Canara Bank", "brand_canarabank", "canarabank.com", "0xFF000000"),
    @("Central Bank of India", "brand_centralbankofindia", "centralbankofindia.co.in", "0xFF000000"),
    @("Union Bank of India", "brand_unionbankofindia", "unionbankofindia.co.in", "0xFF000000"),
    @("Axis Bank", "brand_axisbank", "axisbank.com", "0xFF000000"),
    @("Kotak Mahindra Bank", "brand_kotak", "kotak.com", "0xFF000000"),
    @("India Post Payments Bank", "brand_india_post", "ippbonline.com", "0xFF000000"),
    @("Meesho", "brand_meesho", "meesho.com", "0xFF000000"),
    @("BlinkIt", "brand_blinkit", "blinkit.com", "0xFF000000"),
    @("Swiggy", "brand_swiggy", "swiggy.com", "0xFF000000"),
    @("Zomato", "brand_zomato", "zomato.com", "0xFF000000"),
    @("Croma", "brand_croma", "croma.com", "0xFF000000"),
    @("Reliance Digital", "brand_reliance_digital", "reliancedigital.in", "0xFF000000"),
    @("Ola", "brand_ola", "olacabs.com", "0xFF000000"),
    @("Rapido", "brand_rapido", "rapido.bike", "0xFF000000"),
    @("Google Pay", "brand_google_pay", "pay.google.com", "0xFF000000"),
    @("CRED", "brand_cred", "cred.club", "0xFF000000"),
    @("MakeMyTrip", "brand_makemytrip", "makemytrip.com", "0xFF000000"),
    @("Goibibo", "brand_goibibo", "goibibo.com", "0xFF000000"),
    @("RedBus", "brand_redbus", "redbus.in", "0xFF000000"),
    @("IRCTC", "brand_irctc", "irctc.co.in", "0xFF000000"),
    @("TikTok", "brand_tiktok", "tiktok.com", "0xFF000000"),
    @("Reddit", "brand_reddit", "reddit.com", "0xFF000000"),
    @("Telegram", "brand_telegram", "telegram.org", "0xFF000000"),
    @("WhatsApp", "brand_whatsapp", "whatsapp.com", "0xFF000000"),
    @("ZEE5", "brand_zee5", "zee5.com", "0xFF000000"),
    @("Sony LIV", "brand_sonyliv", "sonyliv.com", "0xFF000000"),
    @("Steam", "brand_steam", "store.steampowered.com", "0xFF000000"),
    @("Epic Games", "brand_epicgames", "epicgames.com", "0xFF000000"),
    @("Xbox", "brand_xbox", "xbox.com", "0xFF000000"),
    @("Microsoft Teams", "brand_microsoft_teams", "teams.microsoft.com", "0xFF000000"),
    @("GitLab", "brand_gitlab", "gitlab.com", "0xFF000000"),
    @("Stack Overflow", "brand_stackoverflow", "stackoverflow.com", "0xFF000000"),
    @("Postman", "brand_postman", "postman.com", "0xFF000000"),
    @("Docker", "brand_docker", "docker.com", "0xFF000000"),
    @("CodePen", "brand_codepen", "codepen.io", "0xFF000000"),
    @("Vercel", "brand_vercel", "vercel.com", "0xFF000000"),
    @("Netlify", "brand_netlify", "netlify.com", "0xFF000000"),
    @("Heroku", "brand_heroku", "heroku.com", "0xFF000000"),
    @("Supabase", "brand_supabase", "supabase.com", "0xFF000000"),
    @("Jira", "brand_jira", "atlassian.com", "0xFF000000"),
    @("Trello", "brand_trello", "trello.com", "0xFF000000"),
    @("Notion", "brand_notion", "notion.so", "0xFF000000"),
    @("INDmoney", "brand_indmoney", "indmoney.com", "0xFF000000"),
    @("Sharekhan", "brand_sharekhan", "sharekhan.com", "0xFF000000"),
    @("Udemy", "brand_udemy", "udemy.com", "0xFF000000")
)

$outDir = "app\src\main\res\drawable"
$kotlinCode = ""

foreach ($brand in $brands) {
    $name = $brand[0]
    $id = $brand[1]
    $domain = $brand[2]
    $color = $brand[3]
    
    $url = "https://www.google.com/s2/favicons?domain=$domain&sz=128"
    $outFile = "$outDir\$id.png"
    
    try {
        Invoke-WebRequest -Uri $url -OutFile $outFile -UseBasicParsing
        $kotlinCode += "        Brand(`"$id`", `"$name`", Color($color), R.drawable.$id, `"$domain`"),`r`n"
    } catch {
        Write-Host "Failed to download $name"
    }
}

Set-Content -Path "brands_snippet.txt" -Value $kotlinCode
Write-Host "Done"
