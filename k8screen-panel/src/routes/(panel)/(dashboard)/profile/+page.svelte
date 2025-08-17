<script lang="ts">
  import IconGithub from '$lib/components/icons/IconGithub.svelte';
  import IconGoogle from '$lib/components/icons/IconGoogle.svelte';
  import * as Card from '$lib/components/ui/card/index.js';
  import Button from '$lib/components/ui/button/button.svelte';
  import { accountAPI } from '$lib/service/account-service';
  import { toastService } from '$lib/service/toast-service';
  import { onMount } from 'svelte';
  import type { Account } from '$lib/model/account/Account';
  import { authAPI } from '$lib/service/auth-service';
  import type { UserInfo } from '$lib/model/user/UserInfo';

  let googleAccounts: Account[] = [];
  let githubAccounts: Account[] = [];

  let userInfo: UserInfo;

  onMount(() => {
    getProfile();
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

  const getProfile = () => {
    authAPI
      .getProfile()
      .then((res) => {
        userInfo = res.data;
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
</script>

<h1>Profile Info</h1>
<Card.Root class="mb-4 w-full border border-primary bg-secondary hover:border">
  <Card.Content class="space-y-6 text-sm">
    <!-- Header -->
    <div class="flex flex-col items-center space-y-3">
      <img
        src={userInfo?.avatarUrl ? userInfo.avatarUrl : '/favicon.png'}
        alt="avatar"
        class="h-28 w-28 rounded-full border-4 border-indigo-500 shadow-sm"
      />
    </div>

    <!-- Username -->
    <div class="mx-auto max-w-lg">
      <label class="text-sm font-medium text-gray-600" for="username">Username</label>
      <input
        id="username"
        type="text"
        readonly
        value={userInfo?.username}
        class="mt-1 w-full cursor-not-allowed rounded-lg border border-gray-300 bg-gray-100 px-3 py-2 text-sm text-gray-800"
      />
    </div>

    <!-- Email -->
    <div class="mx-auto max-w-lg">
      <label class="text-sm font-medium text-gray-600" for="email">Email</label>
      <input
        id="email"
        type="email"
        readonly
        value={userInfo?.email}
        class="mt-1 w-full cursor-not-allowed rounded-lg border border-gray-300 bg-gray-100 px-3 py-2 text-sm text-gray-800"
      />
    </div>
  </Card.Content>
</Card.Root>

<h1>Accounts</h1>
<Card.Root class="w-full border border-primary bg-secondary hover:border">
  <Card.Content class="space-y-6 text-sm">
    <!-- Connected Accounts -->
    <div class="mx-auto max-w-lg space-y-4">
      <h3 class="text-sm font-medium text-gray-600">Accounts</h3>
      <!-- Google Card -->
      <div class="rounded-lg border border-gray-200 p-4 transition hover:shadow-md">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <Button variant="outline" disabled>
              <IconGoogle /> Google
            </Button>
          </div>
          <button on:click={connectGoogleAccount} class="text-sm hover:underline">Connect</button>
        </div>

        {#each googleAccounts as account}
          <div class="mt-4 flex items-center justify-between gap-3">
            <div class="flex items-center gap-2">
              <img src={account.avatarUrl} class="h-10 w-10 rounded-full" alt="Google User" />
              <div>
                <p class="text-sm font-medium text-gray-800">{account.username}</p>
                <p class="text-xs text-gray-600">{account.email}</p>
              </div>
            </div>
            <button on:click={() => deleteAccountByUuid(account.uuid)} class="text-sm text-red-500 hover:underline"
              >Disconnect</button
            >
          </div>
        {/each}
      </div>

      <!-- GitHub Card -->
      <div class="rounded-xl border border-gray-200 p-4 transition hover:shadow-md">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <Button variant="outline" disabled>
              <IconGithub /> Github
            </Button>
          </div>

          <button on:click={connectGithubAccount} class="text-sm hover:underline">Connect</button>
        </div>

        {#each githubAccounts as account}
          <div class="mt-4 flex items-center justify-between gap-3">
            <div class="flex items-center gap-2">
              <img src={account.avatarUrl} class="h-10 w-10 rounded-full" alt="Google User" />
              <div>
                <p class="text-sm font-medium text-gray-800">{account.username}</p>
                <p class="text-xs text-gray-600">{account.email}</p>
              </div>
            </div>
            <button on:click={() => deleteAccountByUuid(account.uuid)} class="text-sm text-red-500 hover:underline"
              >Disconnect</button
            >
          </div>
        {/each}
      </div>
    </div>
  </Card.Content>
</Card.Root>
