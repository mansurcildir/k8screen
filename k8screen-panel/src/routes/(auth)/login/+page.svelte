<script lang="ts">
  import { goto } from '$app/navigation';
  import IconGoogle from '$lib/components/icons/IconGoogle.svelte';
  import Spinner from '$lib/components/spinner.svelte';
  import { Button } from '$lib/components/ui/button/index.js';
  import { Input } from '$lib/components/ui/input/index.js';
  import Label from '$lib/components/ui/label/label.svelte';
  import type { LoginReq } from '$lib/model/user/LoginReq';
  import type { LoginRes } from '$lib/model/user/LoginRes';
  import { authAPI } from '$lib/service/auth-service';
  import { setTokens } from '$lib/service/storage-manager';
  import { toastService } from '$lib/service/toast-service';
  import { writable } from 'svelte/store';
  import { z } from 'zod';

  let loading = false;
  let loadingGoogle = false;
  let errors = writable<Record<string, string>>({});

  const userRegister: LoginReq = {
    username: '',
    password: ''
  };

  const login = () => {
    try {
      schema.parse(userRegister);
      loading = true;
      authAPI
        .login(userRegister)
        .then((data: LoginRes) => {
          setTokens(data.access_token, data.refresh_token);
          goto('/');
        })
        .catch((e) => {
          toastService.show(e.message, 'error');
        })
        .finally(() => (loading = false));
    } catch (e: any) {
      const errorMessages: Record<string, string> = {};
      e.errors.forEach((err: any) => {
        errorMessages[err.path[0]] = err.message;
      });

      errors.set(errorMessages);
    }
  };

  const loginGoogle = () => {
    loadingGoogle = true;
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  const schema = z.object({
    username: z.string().min(1, { message: 'Username is required' }).max(50),
    password: z.string().min(1, { message: 'Password is required' }).max(50)
  });

  const validate = (field: keyof LoginReq) => {
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
    }
  };
</script>

<div class="min-h-screen w-full lg:grid lg:grid-cols-2">
  <div class="flex items-center justify-center py-12">
    <div class="mx-auto grid w-[350px] gap-6">
      <div class="grid gap-2 text-center">
        <h1 class="text-3xl font-bold">Login</h1>
        <p class="text-balance text-muted-foreground">Login to your account</p>
      </div>
      <div class="grid gap-4">
        <div class="grid gap-2">
          <Label for="username">Username</Label>
          <Input
            id="username"
            type="text"
            placeholder="username"
            oninput={() => validate('username')}
            bind:value={userRegister.username}
            required
          />
          <span class="mb-2 h-2 text-sm text-red-500">
            {#if $errors.username}
              {$errors.username}
            {/if}
          </span>
        </div>
        <div class="grid gap-2">
          <div class="flex items-center">
            <Label for="password">Password</Label>
            <a href="##" class="ml-auto inline-block text-sm underline"> Forgot your password? </a>
          </div>
          <Input
            id="password"
            type="password"
            placeholder="******"
            oninput={() => validate('password')}
            bind:value={userRegister.password}
            required
          />
          <span class="mb-2 h-2 text-sm text-red-500">
            {#if $errors.password}
              {$errors.password}
            {/if}
          </span>
        </div>
        <Button class="w-full" disabled={loading || loadingGoogle} onclick={() => login()}>
          {#if loading}
            <Spinner class="m-auto" />
          {:else}
            Login
          {/if}
        </Button>
        <div class="relative flex justify-center text-xs uppercase">
          <span class="bg-background px-2 text-muted-foreground"> Or continue with </span>
        </div>
        <Button variant="outline" class="w-full" disabled={loading || loadingGoogle} onclick={() => loginGoogle()}>
          {#if loadingGoogle}
            <Spinner class="m-auto" color="black" />
          {:else}
            <IconGoogle /> Google
          {/if}
        </Button>
      </div>
      <div class="mt-4 text-center text-sm">
        Don&apos;t you have an account?
        <a href="register" class="underline"> Register </a>
      </div>
    </div>
  </div>
  <div class="hidden w-full items-center justify-center bg-muted lg:flex lg:justify-center">
    <div class="lg:w-[450px] xl:w-[500px] 2xl:w-[500px]">
      <img src="/k8screen-logo.png" alt="k8screen-logo" width="1024" height="1024" class="object-cover" />
    </div>
  </div>
</div>
