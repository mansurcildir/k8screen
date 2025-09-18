<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';
  import Spinner from '$lib/components/spinner.svelte';
  import { Button } from '$lib/components/ui/button/index.js';
  import { Input } from '$lib/components/ui/input/index.js';
  import Label from '$lib/components/ui/label/label.svelte';
  import type { ResetPasswordForm } from '$lib/model/user/ResetPasswordForm';
  import { authAPI } from '$lib/service/auth-service';
  import { toastService } from '$lib/service/toast-service';
  import { writable } from 'svelte/store';
  import { z } from 'zod';

  let loading = false;
  let disabled = false;
  let errors = writable<Record<string, string>>({});

  $: code = $page.params.code;
  $: resetPasswordForm.code = code;

  const resetPasswordForm: ResetPasswordForm = {
    password: '',
    confirmPassword: '',
    code: ''
  };

  const handleValidation = (): Promise<void> => {
    return new Promise((resolve) => {
      try {
        schema.parse(resetPasswordForm);
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

  const resetPassword = () => {
    handleValidation().then(() => {
      loading = true;
      authAPI
        .resetPassword(resetPasswordForm)
        .then(() => {
          goto('/login');
        })
        .catch((err) => {
          toastService.show(err.message, 'error');
        })
        .finally(() => (loading = false));
    });
  };

  const validate = (field: keyof ResetPasswordForm) => {
    if (Object.keys($errors).length === 0) {
      disabled = false;
    }

    try {
      schema.pick({ [field]: true } as any).parse({ [field]: resetPasswordForm[field] });

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

  const schema = z.object({
    password: z
      .string()
      .min(1, { message: 'Password is required' })
      .max(50, { message: 'Password cannot exceed 50 characters' }),
    confirmPassword: z
      .string()
      .min(1, { message: 'Password is required' })
      .max(50, { message: 'Password cannot exceed 50 characters' }),
    code: z
      .string()
      .regex(/^\d{6}$/, { message: 'Code must be 6 digits' })
      .transform((val) => Number(val))
  });
</script>

<div class="min-h-screen w-full lg:grid lg:grid-cols-2">
  <div class="flex items-center justify-center py-12">
    {#if !loading}
      <div class="mx-auto grid w-[350px] gap-6">
        <div class="grid gap-2 text-center">
          <h1 class="text-3xl font-bold">Reset Password</h1>
          <p class="text-balance text-muted-foreground">Reset your password with verification code</p>
        </div>
        <div class="grid gap-4">
          <div class=" flex flex-col gap-2">
            <Label for="password">Enter new password</Label>
            <Input
              oninput={() => validate('password')}
              bind:value={resetPasswordForm.password}
              id="password"
              type="password"
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
          <div class=" flex flex-col gap-2">
            <Label for="confirm-password">Confirm new password</Label>
            <Input
              oninput={() => validate('confirmPassword')}
              bind:value={resetPasswordForm.confirmPassword}
              id="confirm-password"
              type="password"
              placeholder="******"
              required
            />
            <span
              class="mb-2 h-2 text-sm text-red-500 transition-all duration-300 ease-in-out"
              style="opacity: {$errors.confirmPassword ? 1 : 0};"
            >
              {#if $errors.confirmPassword}
                {$errors.confirmPassword}
              {/if}
            </span>
          </div>
          <div class="flex flex-col gap-2">
            <Label for="code">Verification code</Label>
            <Input
              id="code"
              type="text"
              oninput={() => validate('code')}
              bind:value={resetPasswordForm.code}
              placeholder="123456"
              required
            />
            <span
              class="mb-2 h-2 text-sm text-red-500 transition-all duration-300 ease-in-out"
              style="opacity: {$errors.code ? 1 : 0};"
            >
              {#if $errors.code}
                {$errors.code}
              {/if}
            </span>
          </div>
          <Button class="w-full" disabled={disabled || loading} onclick={() => resetPassword()}>
            {#if loading}
              <Spinner class="m-auto" />
            {:else}
              Reset
            {/if}
          </Button>
        </div>
        <div class="mt-4 text-center text-sm">
          <a href="/login" class="underline"> Login </a>
          or
          <a href="/register" class="underline"> Register </a>
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
