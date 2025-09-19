<script lang="ts">
  import * as Card from '$lib/components/ui/card/index.js';
  import Button from '$lib/components/ui/button/button.svelte';
  import { writable } from 'svelte/store';
  import Input from '$lib/components/ui/input/input.svelte';
  import type { ResetPasswordForm } from '$lib/model/user/ResetPasswordForm';
  import { z } from 'zod';
  import { authAPI } from '$lib/service/auth-service';
  import { toastService } from '$lib/service/toast-service';

  const form: ResetPasswordForm = { password: '', confirmPassword: '' };

  let loading = false;
  let disabled = false;
  let errors = writable<Record<string, string>>({});

  const schema = z.object({
    password: z
      .string()
      .min(1, { message: 'Password is required' })
      .max(50, { message: 'Password cannot exceed 50 characters' }),

    confirmPassword: z
      .string()
      .min(1, { message: 'Confirm password is required' })
      .max(50, { message: 'Confirm password cannot exceed 50 characters' })
  });

  const handleValidation = (): Promise<void> => {
    return new Promise((resolve) => {
      try {
        schema.parse(form);
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
        .resetPassword(form)
        .then((res) => {
          resetForm();
          toastService.show(res.message, 'success');
        })
        .catch((err) => {
          toastService.show(err.message, 'error');
        })
        .finally(() => (loading = false));
    });
  };

  const resetForm = () => {
    form.password = '';
    form.confirmPassword = '';

    errors = writable<Record<string, string>>({});
    disabled = false;
  };

  const validate = (field: keyof ResetPasswordForm) => {
    if (Object.keys($errors).length === 0) {
      disabled = false;
    }

    try {
      schema.pick({ [field]: true } as any).parse({ [field]: form[field] });

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

<h1>Reset Your Password</h1>
<Card.Root class="mx-auto mb-4 w-full border border-primary bg-secondary hover:border">
  <Card.Content class="flex flex-col items-center space-y-3 text-sm">
    <!-- Password -->
    <div class="flex w-full flex-col gap-2">
      <label class="text-sm font-medium text-gray-600" for="password">Password</label>
      <Input
        id="password"
        type="password"
        oninput={() => validate('password')}
        bind:value={form.password}
        class={`mt-1 w-full rounded-lg border border-gray-300 bg-gray-100 px-3 py-2 text-sm text-gray-800`}
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

    <!-- Confirm Password -->
    <div class="flex w-full flex-col gap-2">
      <label class="text-sm font-medium text-gray-600" for="confirm-password">Confirm Password</label>
      <Input
        id="confirm-password"
        type="password"
        oninput={() => validate('confirmPassword')}
        bind:value={form.confirmPassword}
        class={`mt-1 w-full rounded-lg border border-gray-300 bg-gray-100 px-3 py-2 text-sm text-gray-800`}
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

    <span class="ml-auto flex gap-2">
      <Button disabled={!form.password && !form.confirmPassword} onclick={resetForm} class={`w-[100px]`}>Reset</Button>
      <Button
        onclick={resetPassword}
        disabled={disabled || loading || !(form.password && form.confirmPassword)}
        class={`w-[100px]`}>Save</Button
      >
    </span>
  </Card.Content>
</Card.Root>
