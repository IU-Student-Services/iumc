<#import "layout.ftl" as layout>

<@layout.layout title="Account already linked">
    <header class="container">
        <hgroup>
            <h1>Wrong IU computing account!</h1>
            <h2>This is the wrong IU account for this Minecraft.</h2>
        </hgroup>
    </header>
    <main class="container">
        <p>Hi, ${account.givenName}. This IU computing account is not linked to this minecraft account (${username})</p>
        <p>Unlink your accounts if you'd like to re-link this Minecraft account. To unlink accounts, run <i>/unlink</i> in-game.</p>
    </main>
</@layout.layout>