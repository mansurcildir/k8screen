<script lang="ts">
  import Spinner from '$lib/components/spinner.svelte';
  import { Button } from '$lib/components/ui/button/index.js';
  import { Input } from '$lib/components/ui/input/index.js';
  import Label from '$lib/components/ui/label/label.svelte';
  import type { EmailForm } from '$lib/model/user/EmailForm';
  import { authAPI } from '$lib/service/auth-service';
  import { toastService } from '$lib/service/toast-service';
  import { writable } from 'svelte/store';
  import { z } from 'zod';

  let loading = false;
  let disabled = false;
  let errors = writable<Record<string, string>>({});

  const emailForm: EmailForm = {
    email: ''
  };

  const handleValidation = (): Promise<void> => {
    return new Promise((resolve) => {
      try {
        schema.parse(emailForm);
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

  const sendEmail = () => {
    handleValidation().then(() => {
      loading = true;
      authAPI
        .sendPasswordRecovery(emailForm)
        .then((res) => {
          toastService.show(res.message, 'success');
        })
        .catch((err) => {
          toastService.show(err.message, 'error');
        })
        .finally(() => (loading = false));
    });
  };

  const validate = (field: keyof EmailForm) => {
    if (Object.keys($errors).length === 0) {
      disabled = false;
    }

    try {
      schema.pick({ [field]: true } as any).parse({ [field]: emailForm[field] });

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
    email: z
      .string()
      .min(1, { message: 'Email is required' })
      .email({ message: 'Invalid email format' })
      .max(100, { message: 'Email cannot exceed 100 characters' })
  });
</script>

<div class="min-h-screen w-full lg:grid lg:grid-cols-2">
  <div class="flex items-center justify-center py-12">
    {#if !loading}
      <div class="mx-auto grid w-[350px] gap-6">
        <div class="grid gap-2 text-center">
          <h1 class="text-3xl font-bold">Password Recovery</h1>
          <p class="text-balance text-muted-foreground">Reset your password</p>
        </div>
        <div class="grid gap-4">
          <div class="flex flex-col gap-2">
            <Label for="email">Email</Label>
            <Input
              oninput={() => validate('email')}
              bind:value={emailForm.email}
              id="email"
              type="email"
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
          <Button class="w-full" disabled={disabled || loading} onclick={() => sendEmail()}>
            {#if loading}
              <Spinner class="m-auto" />
            {:else}
              Send Verification
            {/if}
          </Button>
        </div>
        <div class="mt-4 text-center text-sm">
          <a href="login" class="underline"> Login </a>
          or
          <a href="register" class="underline"> Register </a>
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
