<#macro layout title>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@next/css/pico.min.css">
    <title>IU MC - ${title}</title>
    <style>
    <#include "style.css">
    </style>
</head>
<body data-theme="dark">
<#nested>
<footer class="container">
    <small>Running <a href="https://github.com/JSH32/iumc">IUMC</a> • ${version_hash}</small>
</footer>
</body>
</html>
</#macro>