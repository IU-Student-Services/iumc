<#import "layout.ftl" as layout>

<@layout.layout title="Successfully linked">
    <header class="container">
        <hgroup>
            <h1>Successfully refreshed account!</h1>
            <h2>${account.firstName}, Your IU account data has been refreshed.</h2>
        </hgroup>
    </header>
    <main class="container">
        <p>Your IU account has been refreshed</p>
        <p>This will update all your IU computing account details if they had changed since you linked accounts.</p>
    </main>
</@layout.layout>