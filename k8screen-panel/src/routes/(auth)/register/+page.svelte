<script lang="ts">
  import { goto } from '$app/navigation';
  import IconGithub from '$lib/components/icons/IconGithub.svelte';
  import IconGoogle from '$lib/components/icons/IconGoogle.svelte';
  import Spinner from '$lib/components/spinner.svelte';
  import { Button } from '$lib/components/ui/button/index.js';
  import { Input } from '$lib/components/ui/input/index.js';
  import Label from '$lib/components/ui/label/label.svelte';
  import type { UserForm } from '$lib/model/user/UserForm';
  import { authAPI } from '$lib/service/auth-service';
  import { setTokens } from '$lib/service/storage-manager';
  import { toastService } from '$lib/service/toast-service';
  import { writable } from 'svelte/store';
  import { z } from 'zod';

  let loading = false;
  let loadingGoogle = false;
  let loadingGithub = false;
  let disabled = false;
  let errors = writable<Record<string, string>>({});

  const userRegister: UserForm = {
    username: '',
    email: '',
    password: '',
    avatarUrl: ''
  };

  const handleValidation = (): Promise<void> => {
    return new Promise((resolve) => {
      try {
        schema.parse(userRegister);
        resolve();
      } catch (err: any) {
        const errorMessages: Record<string, string> = {};
        err.errors.forEach((e: any) => {
          errorMessages[e.path[0]] = e.message;
        });
        errors.set(errorMessages);
        disabled = true;
      }
    });
  };

  const register = () => {
    handleValidation().then(() => {
      loading = true;
      authAPI
        .register(userRegister)
        .then((res) => {
          setTokens(res.data.access_token, res.data.refresh_token);
          goto('/');
        })
        .catch((err) => {
          toastService.show(err.message, 'error');
        })
        .finally(() => (loading = false));
    });
  };

  const loginGoogle = () => {
    const width = 500;
    const height = 600;
    const left = window.screenX + (window.outerWidth - width) / 2;
    const top = window.screenY + (window.outerHeight - height) / 2;

    const frontendBaseUrl = window.location.origin;

    window.open(
      'http://localhost:8080/oauth2/authorization/google?action=login',
      'googleLogin',
      `width=${width},height=${height},left=${left},top=${top},resizable,scrollbars`
    );

    const messageHandler = (event: MessageEvent) => {
      if (event.origin !== frontendBaseUrl) {
        return;
      }

      if (event.data.status === 'google-auth-success') {
        window.removeEventListener('message', messageHandler);
        window.location.href = frontendBaseUrl;
      } else if (event.data.status === 'google-auth-error') {
        window.removeEventListener('message', messageHandler);
        toastService.show(event.data.error, 'error');
      }
    };

    window.addEventListener('message', messageHandler);
  };

  const loginGithub = () => {
    const width = 500;
    const height = 600;
    const left = window.screenX + (window.outerWidth - width) / 2;
    const top = window.screenY + (window.outerHeight - height) / 2;

    const frontendBaseUrl = window.location.origin;

    window.open(
      'http://localhost:8080/oauth2/authorization/github?action=login',
      'githubLogin',
      `width=${width},height=${height},left=${left},top=${top},resizable,scrollbars`
    );

    const messageHandler = (event: MessageEvent) => {
      if (event.origin !== frontendBaseUrl) {
        return;
      }

      if (event.data.status === 'github-auth-success') {
        window.removeEventListener('message', messageHandler);
        window.location.href = frontendBaseUrl;
      } else if (event.data.status === 'github-auth-error') {
        window.removeEventListener('message', messageHandler);
        toastService.show(event.data.error, 'error');
      }
    };

    window.addEventListener('message', messageHandler);
  };

  const schema = z.object({
    username: z
      .string()
      .min(1, { message: 'Username is required' })
      .max(50, { message: 'Username cannot exceed 50 characters' }),
    email: z
      .string()
      .min(1, { message: 'Email is required' })
      .email({ message: 'Invalid email format' })
      .max(100, { message: 'Email cannot exceed 100 characters' }),
    password: z
      .string()
      .min(1, { message: 'Password is required' })
      .max(50, { message: 'Password cannot exceed 50 characters' })
  });

  const validate = (field: keyof UserForm) => {
    if (Object.keys($errors).length === 0) {
      disabled = false;
    }

    try {
      schema.pick({ [field]: true } as any).parse({ [field]: userRegister[field] });

      errors.update((currentErrors) => {
        const { [field]: _, ...rest } = currentErrors;
        return rest;
      });
    } catch (e: any) {
      const error = e.errors[0];
      errors.update((currentErrors) => ({
        ...currentErrors,
        [field]: error.message
      }));

      disabled = true;
    }
  };
</script>

<div class="min-h-screen w-full lg:grid lg:grid-cols-2">
  <div class="flex items-center justify-center py-12">
    {#if !loading}
      <div class="mx-auto grid w-[350px] gap-6">
        <div class="grid gap-2 text-center">
          <h1 class="text-3xl font-bold">Register</h1>
          <p class="text-balance text-muted-foreground">Register your account</p>
        </div>
        <div class="grid gap-4">
          <div class="grid gap-2">
            <Label for="username">Username</Label>
            <Input
              id="username"
              type="text"
              oninput={() => validate('username')}
              bind:value={userRegister.username}
              placeholder="username"
              required
            />
            <span
              class="mb-2 h-2 text-sm text-red-500 transition-all duration-300 ease-in-out"
              style="opacity: {$errors.username ? 1 : 0};"
            >
              {#if $errors.username}
                {$errors.username}
              {/if}
            </span>
          </div>
          <div class="grid gap-2">
            <Label for="email">Email</Label>
            <Input
              id="email"
              type="email"
              oninput={() => validate('email')}
              bind:value={userRegister.email}
              placeholder="m@example.com"
              required
            />
            <span
              class="mb-2 h-2 text-sm text-red-500 transition-all duration-300 ease-in-out"
              style="opacity: {$errors.email ? 1 : 0};"
            >
              {#if $errors.email}
                {$errors.email}
              {/if}
            </span>
          </div>
          <div class="grid gap-2">
            <Label for="password">Password</Label>
            <Input
              id="password"
              type="password"
              oninput={() => validate('password')}
              bind:value={userRegister.password}
              placeholder="******"
              required
            />
            <span
              class="mb-2 h-2 text-sm text-red-500 transition-all duration-300 ease-in-out"
              style="opacity: {$errors.password ? 1 : 0};"
            >
              {#if $errors.password}
                {$errors.password}
              {/if}
            </span>
          </div>
          <Button class="w-full" disabled={disabled || loading} onclick={register}>
            {#if loading}
              <Spinner class="m-auto" />
            {:else}
              Register
            {/if}
          </Button>
          <div class="relative flex justify-center text-xs uppercase">
            <span class="bg-background px-2 text-muted-foreground"> Or continue with </span>
          </div>
          <Button variant="outline" class="w-full" disabled={loading || loadingGoogle} onclick={loginGoogle}>
            {#if loadingGoogle}
              <Spinner class="m-auto" color="black" />
            {:else}
              <IconGoogle class="mb-0.5" /> Google
            {/if}
          </Button>

          <Button variant="outline" class="w-full" disabled={loading || loadingGithub} onclick={loginGithub}>
            {#if loadingGithub}
              <Spinner class="m-auto" color="black" />
            {:else}
              <IconGithub class="mb-0.5" /> Github
            {/if}
          </Button>
        </div>
        <div class="mt-4 text-center text-sm">
          Do you have an account?
          <a href="/login" class="underline"> Login </a>
        </div>
      </div>
    {:else}
      <div class="flex h-full w-full items-center justify-center">
        <div class="h-10 w-10">
          <svg
            class="animate-spin text-black"
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <circle cx="12" cy="12" r="10" class="opacity-25"></circle>
            <path d="M4 12a8 8 0 0 1 16 0" class="opacity-75"></path>
          </svg>
        </div>
      </div>
    {/if}
  </div>
  <div class="hidden w-full items-center justify-center bg-muted lg:flex lg:justify-center">
    <div class="lg:w-[450px] xl:w-[500px] 2xl:w-[500px]">
      <img src="/k8screen-logo.png" alt="k8screen-logo" width="1024" height="1024" class="object-cover" />
    </div>
  </div>
</div>
