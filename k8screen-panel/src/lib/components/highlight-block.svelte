<script lang="ts">
  import hljs from 'highlight.js';
  import 'highlight.js/styles/monokai-sublime.css';

  export let code: string;
  export let language: string;

  $: highlighted = hljs.highlight(code, { language }).value;
</script>

<pre class="max-w-full whitespace-pre-wrap break-words">
  <code class="language-{language}">
    {@html highlighted}
  </code>
</pre>
