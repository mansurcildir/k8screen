<script lang="ts">
  import IconGithub from '$lib/components/icons/IconGithub.svelte';
  import IconGoogle from '$lib/components/icons/IconGoogle.svelte';
  import * as Card from '$lib/components/ui/card/index.js';
  import Button from '$lib/components/ui/button/button.svelte';
  import { accountAPI } from '$lib/service/account-service';
  import { toastService } from '$lib/service/toast-service';
  import { onMount } from 'svelte';
  import type { Account } from '$lib/model/account/Account';
  import { z } from 'zod';

  let googleAccounts: Account[] = [];
  let githubAccounts: Account[] = [];

  onMount(() => {
    getGoogleAccounts();
    getGithubAccounts();
  });

  const getGoogleAccounts = () => {
    accountAPI
      .getGoogleAccounts()
      .then((res) => {
        googleAccounts = res.data;
      })
      .catch((err) => {
        toastService.show(err.message, 'error');
      });
  };

  const getGithubAccounts = () => {
    accountAPI
      .getGithubAccounts()
      .then((res) => {
        githubAccounts = res.data;
      })
      .catch((err) => {
        toastService.show(err.message, 'error');
      });
  };

  const deleteAccountByUuid = (accountUuid: string) => {
    accountAPI
      .deleteAccountByUuid(accountUuid)
      .then(() => {
        window.location.href = '/profile';
      })
      .catch((err) => {
        toastService.show(err.message, 'error');
      });
  };

  const connectGoogleAccount = () => {
    const width = 500;
    const height = 600;
    const left = window.screenX + (window.outerWidth - width) / 2;
    const top = window.screenY + (window.outerHeight - height) / 2;

    const frontendBaseUrl = window.location.origin;

    const token = localStorage.getItem('access-token');

    window.open(
      `http://localhost:8080/oauth2/authorization/google?action=connect&token=${token}`,
      'googleConnect',
      `width=${width},height=${height},left=${left},top=${top},resizable,scrollbars`
    );

    const messageHandler = (event: MessageEvent) => {
      if (event.origin !== frontendBaseUrl) {
        return;
      }

      if (event.data.status === 'google-auth-success') {
        window.removeEventListener('message', messageHandler);
        window.location.href = `${frontendBaseUrl}/profile`;
      } else if (event.data.status === 'google-auth-error') {
        window.removeEventListener('message', messageHandler);
        toastService.show(event.data.error, 'error');
      }
    };

    window.addEventListener('message', messageHandler);
  };

  const connectGithubAccount = () => {
    const width = 500;
    const height = 600;
    const left = window.screenX + (window.outerWidth - width) / 2;
    const top = window.screenY + (window.outerHeight - height) / 2;

    const frontendBaseUrl = window.location.origin;

    const token = localStorage.getItem('access-token');

    window.open(
      `http://localhost:8080/oauth2/authorization/github?action=connect&token=${token}`,
      'githubConnect',
      `width=${width},height=${height},left=${left},top=${top},resizable,scrollbars`
    );

    const messageHandler = (event: MessageEvent) => {
      if (event.origin !== frontendBaseUrl) {
        return;
      }

      if (event.data.status === 'github-auth-success') {
        window.removeEventListener('message', messageHandler);
        window.location.href = `${frontendBaseUrl}/profile`;
      } else if (event.data.status === 'github-auth-error') {
        window.removeEventListener('message', messageHandler);
        toastService.show(event.data.error, 'error');
      }
    };

    window.addEventListener('message', messageHandler);
  };

  const fileSchema = z.instanceof(File, {
    message: 'You must upload a valid file.'
  });
</script>

<h1>Accounts</h1>
<Card.Root class="mx-auto w-full border border-primary bg-secondary hover:border">
  <Card.Content class="space-y-6 text-sm">
    <!-- Connected Accounts -->
    <div class="mx-auto space-y-4">
      <!-- Google Card -->
      <div class="rounded-lg border border-gray-200 p-2 transition hover:shadow-md">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <Button class="p-2" variant="outline" disabled>
              <IconGoogle class="w-[100px]" /> Google
            </Button>
          </div>
          <button onclick={connectGoogleAccount} class="text-sm hover:underline">Connect</button>
        </div>

        {#each googleAccounts as account}
          <div class="mt-4 flex items-center justify-between gap-3">
            <div class="flex items-center gap-x-2">
              <img src={account.avatarUrl} class="h-10 w-10 rounded-full" alt="Google User" />
              <div class="min-w-[200px] break-words">
                <p class="text-sm font-medium text-gray-800">{account.username}</p>
                <p class="text-xs text-gray-600">{account.email}</p>
              </div>
            </div>
            <button onclick={() => deleteAccountByUuid(account.uuid)} class="text-sm text-red-500 hover:underline"
              >Disconnect</button
            >
          </div>
        {/each}
      </div>

      <!-- GitHub Card -->
      <div class="rounded-lg border border-gray-200 p-2 transition hover:shadow-md">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-x-2">
            <Button class="p-2" variant="outline" disabled>
              <IconGithub class="w-[100px]" /> Github
            </Button>
          </div>

          <button onclick={connectGithubAccount} class="text-sm hover:underline">Connect</button>
        </div>

        {#each githubAccounts as account}
          <div class="mt-4 flex items-center justify-between gap-3">
            <div class="flex items-center gap-2">
              <img src={account.avatarUrl} class="h-10 w-10 rounded-full" alt="Google User" />
              <div class="min-w-[200px] break-words">
                <p class="text-sm font-medium text-gray-800">{account.username}</p>
                <p class="text-xs text-gray-600">{account.email}</p>
              </div>
            </div>
            <button onclick={() => deleteAccountByUuid(account.uuid)} class="text-sm text-red-500 hover:underline"
              >Disconnect</button
            >
          </div>
        {/each}
      </div>
    </div>
  </Card.Content>
</Card.Root>
