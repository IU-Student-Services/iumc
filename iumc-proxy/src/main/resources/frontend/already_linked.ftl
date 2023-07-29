<#import "layout.ftl" as layout>

<@layout.layout title="Account already linked">
    <header class="container">
        <hgroup>
            <h1>IU account already linked!</h1>
            <h2>Your IU account and a Minecraft account is already linked.</h2>
        </hgroup>
    </header>
    <main class="container">
        <p>Hi, ${account.firstName}. Your IU computing account is already linked to the minecraft account (${username})</p>
        <p>You can only have one Minecraft account linked at a time. To unlink accounts, run <i>/unlink</i> in-game.</p>
    </main>
</@layout.layout>