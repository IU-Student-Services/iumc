<#import "layout.ftl" as layout>

<@layout.layout title="Home">
    <header class="container">
        <hgroup>
            <h1>IU Minecraft</h1>
            <h2>Welcome to IU Minecraft! The ultimate minecraft survival server exclusively designed for IU students.
            </h2>
        </hgroup>
    </header>
    <main class="container">
        <div class="grid">
            <div class="card">
                <div class="container">
                    <h4><b>Status</b></h4>
                    <#if server.open>
                        <p><span class="circle active" style="margin-right: 20px;"></span>Open</p>
                    <#else>
                        <p><span class="circle" style="margin-right: 20px;"></span>Closed</p>
                    </#if>
                </div>
            </div>
            <div class="card">
                <div class="container">
                    <h4><b>IP</b></h4>
                    <p>${server.publicIp}</p>
                </div>
            </div>
            <div class="card">
                <div class="container">
                    <h4><b>Players online</b></h4>
                    <p>${server.onlinePlayers}/${server.maxPlayers}</p>
                </div>
            </div>
        </div>
    </main>
</@layout.layout>