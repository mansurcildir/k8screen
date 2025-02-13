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

  const userForm: UserForm = {
    username: '',
    email: '',
    password: '',
    picture: ''
  };

  const register = () => {
    loading = true;
    authAPI
      .register(userForm)
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
    password: z.string().min(1, { message: 'Password is required' }).max(50)
  });

  const validate = (field: keyof UserForm) => {
    try {
      schema.pick({ [field]: true } as any).parse({ [field]: userForm[field] });

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

<form on:submit|preventDefault={register}>
  <div class="w-full lg:grid lg:min-h-[600px] lg:grid-cols-2 xl:min-h-[800px]">
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
              <Input id="username" type="text" bind:value={userForm.username} placeholder="username" required />
            </div>
            <div class="grid gap-2">
              <Label for="email">Email</Label>
              <Input id="email" type="email" bind:value={userForm.email} placeholder="m@example.com" required />
            </div>
            <div class="grid gap-2">
              <div class="flex items-center">
                <Label for="password">Password</Label>
              </div>
              <Input id="password" type="password" bind:value={userForm.password} placeholder="******" required />
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
    <div class="hidden bg-muted p-60 lg:block">
      <img
        src="/k8s-logo.png"
        alt="placeholder"
        width="1920"
        height="1080"
        class="h-full w-full object-cover dark:brightness-[0.2] dark:grayscale"
      />
    </div>
  </div>
</form>
