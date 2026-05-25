$brands = @(
    @("Bank of India", "brand_bankofindia", "bankofindia.co.in", "0xFF000000"),
    @("Central Bank of India", "brand_centralbankofindia", "centralbankofindia.co.in", "0xFF000000"),
    @("Union Bank of India", "brand_unionbankofindia", "unionbankofindia.co.in", "0xFF000000"),
    @("India Post Payments Bank", "brand_india_post", "ippbonline.com", "0xFF000000"),
    @("Ola", "brand_ola", "olacabs.com", "0xFF000000"),
    @("IRCTC", "brand_irctc", "irctc.co.in", "0xFF000000")
)

$outDir = "app\src\main\res\drawable"
$kotlinCode = ""

foreach ($brand in $brands) {
    $name = $brand[0]
    $id = $brand[1]
    $domain = $brand[2]
    $color = $brand[3]
    
    $urlName = $name.Replace(" ", "+")
    $url = "https://ui-avatars.com/api/?name=$urlName&background=random&size=128&format=png"
    $outFile = "$outDir\$id.png"
    
    try {
        Invoke-WebRequest -Uri $url -OutFile $outFile -UseBasicParsing
        $kotlinCode += "        Brand(`"$id`", `"$name`", Color($color), R.drawable.$id, `"$domain`"),`r`n"
    } catch {
        Write-Host "Failed UI Avatars: $name"
    }
}

Add-Content -Path "brands_snippet.txt" -Value $kotlinCode
Write-Host "Done"
