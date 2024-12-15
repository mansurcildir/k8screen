<script lang="ts">
  import "../../app.css";
  import { page } from "$app/stores";
</script>

<svelte:head>
  <title>{$page.data.title}</title>
</svelte:head>

<slot />

