<script lang="ts">
  import { page } from '$app/stores';
  import { onMount } from 'svelte';
  import { authAPI } from '$lib/service/auth-service';

  let isAuthenticated: boolean | undefined = false;
  let isLoading = true;

  onMount(async () => {
    const code = new URLSearchParams(window.location.search).get('code');
    await authAPI
      .authenticate(code)
      .then((data) => {
        isAuthenticated = data;
      })
      .finally(() => {
        isLoading = false;
      });
  });
</script>

<svelte:head>
  <title>{$page.data.title}</title>
</svelte:head>

{#if isAuthenticated}
  <div class="drawer min-h-screen bg-base-200 lg:drawer-open">
    <input id="my-drawer" type="checkbox" class="drawer-toggle" />
    <slot />
  </div>
{:else if isLoading}
  <div class="flex flex-col items-center justify-center min-h-screen">
    <span class="loading loading-spinner loading-lg"></span>
  </div>
{/if}
