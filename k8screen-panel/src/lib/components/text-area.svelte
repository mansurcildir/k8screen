<script lang="ts">
  import hljs from 'highlight.js';
  import 'highlight.js/styles/monokai-sublime.css';

  export let code: string;
  export let language: string;

  $: highlighted = hljs.highlight(code, { language }).value;
</script>

<div class="grid sm:grid-cols-2 gap-5 h-full">
  <textarea
    bind:value={code}
    class="p-5 bg-[#020817] p-5 outline-none resize-none text-sm h-full"
    placeholder="Kodunuzu buraya yazın..."
  ></textarea>

  <pre class="max-w-full text-sm whitespace-pre-wrap break-words">
    <code class="language-{language}">
      {@html highlighted}
    </code>
  </pre>
</div>
