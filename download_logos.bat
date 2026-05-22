@echo off
echo ============================================
echo  Locker App - Brand Logo Downloader v2
echo  Source: Google Favicon API (sz=128)
echo ============================================
echo.

set OUT=D:\locker\app\src\main\res\drawable
set BASE=https://www.google.com/s2/favicons?sz=128^&domain=

echo Downloading logos to: %OUT%
echo.

echo [SOCIAL ^& COMMS]
curl -L -o "%OUT%\brand_facebook.png"      "%BASE%facebook.com"
curl -L -o "%OUT%\brand_instagram.png"     "%BASE%instagram.com"
curl -L -o "%OUT%\brand_twitter.png"       "%BASE%x.com"
curl -L -o "%OUT%\brand_linkedin.png"      "%BASE%linkedin.com"
curl -L -o "%OUT%\brand_snapchat.png"      "%BASE%snapchat.com"
curl -L -o "%OUT%\brand_discord.png"       "%BASE%discord.com"
curl -L -o "%OUT%\brand_slack.png"         "%BASE%slack.com"
curl -L -o "%OUT%\brand_gmail.png"         "%BASE%gmail.com"
echo.

echo [FINANCE ^& BANKING]
curl -L -o "%OUT%\brand_chase.png"         "%BASE%chase.com"
curl -L -o "%OUT%\brand_bankofamerica.png" "%BASE%bankofamerica.com"
curl -L -o "%OUT%\brand_paypal.png"        "%BASE%paypal.com"
curl -L -o "%OUT%\brand_coinbase.png"      "%BASE%coinbase.com"
curl -L -o "%OUT%\brand_amex.png"          "%BASE%americanexpress.com"
curl -L -o "%OUT%\brand_sbi.png"           "%BASE%onlinesbi.sbi"
curl -L -o "%OUT%\brand_hdfc.png"          "%BASE%hdfcbank.com"
curl -L -o "%OUT%\brand_icici.png"         "%BASE%icicibank.com"
curl -L -o "%OUT%\brand_bankofbaroda.png"  "%BASE%bankofbaroda.in"
curl -L -o "%OUT%\brand_paytm.png"         "%BASE%paytm.com"
curl -L -o "%OUT%\brand_phonepe.png"       "%BASE%phonepe.com"
curl -L -o "%OUT%\brand_groww.png"         "%BASE%groww.in"
curl -L -o "%OUT%\brand_zerodha.png"       "%BASE%zerodha.com"
echo.

echo [WORK ^& TECH]
curl -L -o "%OUT%\brand_google.png"        "%BASE%google.com"
curl -L -o "%OUT%\brand_microsoft.png"     "%BASE%microsoft.com"
curl -L -o "%OUT%\brand_github.png"        "%BASE%github.com"
curl -L -o "%OUT%\brand_aws.png"           "%BASE%aws.amazon.com"
curl -L -o "%OUT%\brand_icloud.png"        "%BASE%icloud.com"
curl -L -o "%OUT%\brand_windows.png"       "%BASE%microsoft.com"
curl -L -o "%OUT%\brand_apple.png"         "%BASE%apple.com"
echo.

echo [ENTERTAINMENT ^& SHOPPING]
curl -L -o "%OUT%\brand_netflix.png"       "%BASE%netflix.com"
curl -L -o "%OUT%\brand_amazon.png"        "%BASE%amazon.com"
curl -L -o "%OUT%\brand_spotify.png"       "%BASE%spotify.com"
curl -L -o "%OUT%\brand_uber.png"          "%BASE%uber.com"
curl -L -o "%OUT%\brand_hotstar.png"       "%BASE%hotstar.com"
curl -L -o "%OUT%\brand_primevideo.png"    "%BASE%primevideo.com"
curl -L -o "%OUT%\brand_amazonmusic.png"   "%BASE%music.amazon.in"
curl -L -o "%OUT%\brand_flipkart.png"      "%BASE%flipkart.com"
curl -L -o "%OUT%\brand_myntra.png"        "%BASE%myntra.com"
echo.

echo ============================================
echo  Done downloading all logos!
echo ============================================
pause
