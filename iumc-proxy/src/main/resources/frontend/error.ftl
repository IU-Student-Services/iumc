<#import "layout.ftl" as layout>

<@layout.layout title="Error">
    <header class="container">
        <hgroup>
            <h1>Error</h1>
            <h2>An error occurred while executing your request</h2>
        </hgroup>
    </header>
    <main class="container">
        <blockquote>${message}</blockquote>
    </main>
</@layout.layout>