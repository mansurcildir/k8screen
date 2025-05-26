<script lang="ts">
  import { Button } from '$lib/components/ui/button/index.js';
  import { Input } from '$lib/components/ui/input/index.js';
  import Label from '$lib/components/ui/label/label.svelte';
  import type { UserForm } from '$lib/model/user/UserForm';
  import { authAPI } from '$lib/service/auth-service';
  import { setTokens } from '$lib/service/storage-manager';
  import { writable } from 'svelte/store';
  import { z } from 'zod';

  let loading = false;
  let errors = writable<Record<string, string>>({});

  const userRegister: UserForm = {
    username: '',
    email: '',
    password: '',
    picture: ''
  };

  const register = () => {
    loading = true;
    authAPI
      .register(userRegister)
      .then((data: { access_token: string; refresh_token: string }) => {
        setTokens(data.access_token, data.refresh_token);
        window.location.href = '/login';
      })
      .finally(() => (loading = false));
  };

  const loginGoogle = () => {
    loading = true;
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  const schema = z.object({
    username: z.string().min(1, { message: 'Username is required' }).max(50),
    email: z.string().min(1, { message: 'Email is required' }).max(50),
    password: z.string().min(1, { message: 'Password is required' }).max(50)
  });

  const validate = (field: keyof UserForm) => {
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
    {#if !loading}
      <form on:submit|preventDefault={register}>
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
              <span class="mb-2 h-2 text-sm text-red-500">
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
              <span class="mb-2 h-2 text-sm text-red-500">
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
              <span class="mb-2 h-2 text-sm text-red-500">
                {#if $errors.password}
                  {$errors.password}
                {/if}
              </span>
            </div>
            <Button type="submit" class="w-full">Register</Button>
            <div class="relative flex justify-center text-xs uppercase">
              <span class="bg-background px-2 text-muted-foreground"> Or continue with </span>
            </div>
            <Button variant="outline" class="w-full" onclick={() => loginGoogle()}>Google</Button>
          </div>
          <div class="mt-4 text-center text-sm">
            Do you have an account?
            <a href="/login" class="underline"> Login </a>
          </div>
        </div>
      </form>
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
