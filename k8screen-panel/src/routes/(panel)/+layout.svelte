<script lang="ts">
  import { page } from '$app/stores';
  import { onMount } from 'svelte';
  import { authAPI } from '$lib/service/auth-service';

  let isAuthenticated: boolean | undefined = false;
  let loading = true;

  onMount(async () => {
    const code = new URLSearchParams(window.location.search).get('code');
    await authAPI
      .authenticate(code)
      .then((data) => {
        isAuthenticated = data;
      })
      .finally(() => {
        loading = false;
      });
  });
</script>

<svelte:head>
  <title>{$page.data.title}</title>
</svelte:head>

{#if isAuthenticated}
  <div class="drawer bg-base-200 lg:drawer-open min-h-screen">
    <input id="my-drawer" type="checkbox" class="drawer-toggle" />
    <slot />
  </div>
{:else if loading}
  <div class="flex min-h-screen flex-col items-center justify-center">
    <span class="loading loading-spinner loading-lg"></span>
  </div>
{/if}
