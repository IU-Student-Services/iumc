<#import "layout.ftl" as layout>

<@layout.layout title="Successfully linked">
    <header class="container">
        <hgroup>
            <h1>Welcome ${account.firstName}!</h1>
            <h2>Your Minecraft and IU accounts have been linked.</h2>
        </hgroup>
    </header>
    <main class="container">
        <p>Your IU account is linked with your minecraft account, <b>${player.username}</b>.</p>
        <p>You can only have one Minecraft account linked at a time. To unlink accounts, run <i>/unlink</i> in-game.</p>
    </main>
</@layout.layout>