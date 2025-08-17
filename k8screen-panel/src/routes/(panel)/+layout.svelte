<script lang="ts">
  import { page } from '$app/stores';
</script>

<svelte:head>
  <title>{$page.data.title}</title>
</svelte:head>

<div class="drawer bg-base-200 lg:drawer-open min-h-screen">
  <input id="my-drawer" type="checkbox" class="drawer-toggle" />
  <slot />
</div>
