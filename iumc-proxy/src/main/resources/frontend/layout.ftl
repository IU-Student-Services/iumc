<#macro layout title>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>IU MC - ${title}</title>
    <link rel="stylesheet" href="/static/style.css"/>
</head>
    <body data-theme="dark">
        <nav class="container">
            <ul>
                <li><strong><a href="/" class="contrast">IU + Craft</a></strong></li>
            </ul>
            <ul>
                <li><a href="/">Home</a></li>
            </ul>
        </nav>
        <#nested>
        <footer class="container">
            <small>Running <a href="https://github.com/JSH32/iumc">IUMC</a> • ${versionHash}</small>
        </footer>
    </body>
</html>
</#macro>