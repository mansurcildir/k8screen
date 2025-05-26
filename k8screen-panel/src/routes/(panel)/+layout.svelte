<script lang="ts">
  import { page } from '$app/stores';
  import { onMount } from 'svelte';
  import { authAPI } from '$lib/service/auth-service';
  import { toastService } from '$lib/service/toast-service';
  import Spinner from '$lib/components/spinner.svelte';

  let isAuthenticated: boolean | undefined = false;
  let loading = true;

  onMount(async () => {
    const code = new URLSearchParams(window.location.search).get('code');
    await authAPI
      .authorize(code)
      .then(() => {
        isAuthenticated = true;
      })
      .catch((e) => {
        toastService.show(e.message, 'error');
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
  <div class="drawer bg-base-200 lg:drawer-open min-h-screen">
    <Spinner class="h-10 w-10" color="black" />
  </div>
{/if}
