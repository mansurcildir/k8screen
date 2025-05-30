<script lang="ts">
  import hljs from 'highlight.js';
  import 'highlight.js/styles/monokai-sublime.css';

  export let code: string;
  export let language: string;

  $: highlighted = hljs.highlight(code, { language }).value;
</script>

<div class="grid h-full gap-5 sm:grid-cols-2">
  <textarea
    bind:value={code}
    class="h-full resize-none bg-[#020817] p-5 p-5 text-sm outline-none"
    placeholder="Kodunuzu buraya yazın..."
  ></textarea>

  <pre class="max-w-full whitespace-pre-wrap break-words text-sm">
    <code class="language-{language}">
      {@html highlighted}
    </code>
  </pre>
</div>
