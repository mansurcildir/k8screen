<script lang="ts">
  import { page } from "$app/stores";
  import { Button } from "$lib/components/ui/button/index.js";
  import { Input } from "$lib/components/ui/input/index.js";
  import Label from "$lib/components/ui/label/label.svelte";
  import type { LoginReq } from "$lib/model/user/LoginReq";
  import { authAPI } from "$lib/service/auth-service";
  import { setTokens } from "$lib/service/storage-manager";
  import { onMount } from "svelte";

  let loading = false;


  onMount(async () => {
    const code = new URLSearchParams(window.location.search).get('code');
    await authAPI
      .authenticate(code)
  });

  const userForm: LoginReq = {
    username: "",
    password: "",
  };

  const login = () => {
    loading = true;
    authAPI
      .login(userForm)
      .then((data: { access_token: string, refresh_token: string }) => {
        setTokens(data.access_token, data.refresh_token);
        window.location.href = "/namespaces";
      })
      .finally(() => (loading = false));
  };

  const loginGoogle = () => {
    loading = true;
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };
</script>

<form>
  <div class="w-full lg:grid lg:min-h-[600px] lg:grid-cols-2 xl:min-h-[800px]">
    <div class="flex items-center justify-center py-12">
      {#if !loading}
        <div class="mx-auto grid w-[350px] gap-6">
          <div class="grid gap-2 text-center">
            <h1 class="text-3xl font-bold">Login</h1>
            <p class="text-muted-foreground text-balance">
              Login to your account
            </p>
          </div>
          <div class="grid gap-4">
            <div class="grid gap-2">
              <Label for="username">Username</Label>
              <Input
                id="username"
                type="text"
                bind:value={userForm.username}
                placeholder="username"
                required
              />
            </div>
            <div class="grid gap-2">
              <div class="flex items-center">
                <Label for="password">Password</Label>
                <a href="##" class="ml-auto inline-block text-sm underline">
                  Forgot your password?
                </a>
              </div>
              <Input
                id="password"
                type="password"
                placeholder="******"
                bind:value={userForm.password}
                required
              />
            </div>
            <Button type="submit" class="w-full" onclick={() => login()}>Login</Button>
            <Button variant="outline" class="w-full" onclick={() => loginGoogle()}>Login with Google</Button>
          </div>
          <div class="mt-4 text-center text-sm">
            Don&apos;t you have an account?
            <a href="register" class="underline"> Register </a>
          </div>
        </div>
      {:else}
        <div class="h-full w-full flex items-center justify-center">
          <div class="h-10 w-10">
            <svg
              class="animate-spin text-black"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <circle cx="12" cy="12" r="10" class="opacity-25" />
              <path d="M4 12a8 8 0 0 1 16 0" class="opacity-75" />
            </svg>
          </div>
        </div>
      {/if}
    </div>
    <div class="bg-muted hidden lg:block p-60">
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
