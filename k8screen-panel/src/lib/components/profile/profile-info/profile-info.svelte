<script lang="ts">
  import * as Card from '$lib/components/ui/card/index.js';
  import Button from '$lib/components/ui/button/button.svelte';
  import { toastService } from '$lib/service/toast-service';
  import { onMount } from 'svelte';
  import type { UserInfo } from '$lib/model/user/UserInfo';
  import { z } from 'zod';
  import type { ProfileForm } from '$lib/model/user/ProfileForm';
  import { writable } from 'svelte/store';
  import { userAPI } from '$lib/service/user-service';
  import Input from '$lib/components/ui/input/input.svelte';

  let disabled = false;
  let readonly = true;
  let loading = false;
  let avatarSrc: string;

  let errors = writable<Record<string, string>>({});
  const fileError = writable<string | null>(null);

  let file: File;
  let userInfo: UserInfo;
  const profileForm: ProfileForm = { username: '', email: '' };

  onMount(() => {
    getAvatar();
    getProfile();
  });

  const getAvatar = async () => {
    await userAPI
      .getAvatar()
      .then((buffer) => {
        if (!buffer || buffer.byteLength === 0) {
          return;
        }
        const base64 = btoa(new Uint8Array(buffer).reduce((data, byte) => data + String.fromCharCode(byte), ''));
        avatarSrc = `data:image/png;base64,${base64}`;
      })
      .catch((err) => {
        toastService.show(err.message, 'error');
      });
  };

  const uploadAvatar = () => {
    const validation = fileSchema.safeParse(file);
    if (!validation.success) {
      fileError.set(validation.error.errors[0].message);
    } else {
      fileError.set(null);
      uploadFile(file);
    }
  };

  const uploadFile = (file: File) => {
    const formData = new FormData();
    formData.append('config', file);

    userAPI
      .uploadAvatar(formData)
      .then(() => {
        window.location.href = '/profile';
      })
      .catch((err) => {
        toastService.show(err.message, 'error');
      });
  };

  const handleValidation = (): Promise<void> => {
    return new Promise((resolve) => {
      try {
        schema.parse(profileForm);
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

  const resetForm = () => {
    profileForm.username = userInfo.username;
    profileForm.email = userInfo.email;

    errors = writable<Record<string, string>>({});
    readonly = true;
    disabled = false;
  };

  const saveProfile = () => {
    loading = true;
    handleValidation().then(() => {
      userAPI
        .updateProfile(profileForm)
        .then(() => {
          window.location.href = '/profile';
        })
        .catch((err) => {
          toastService.show(err.message, 'error');
        })
        .finally(() => (loading = false));
    });
  };

  const getProfile = () => {
    userAPI
      .getProfile()
      .then((res) => {
        userInfo = res.data;
        profileForm.username = userInfo.username;
        profileForm.email = userInfo.email;
      })
      .catch((err) => {
        toastService.show(err.message, 'error');
      });
  };

  const schema = z.object({
    username: z
      .string()
      .min(1, { message: 'Username is required' })
      .max(50, { message: 'Username cannot exceed 20 characters' }),
    email: z
      .string()
      .min(1, { message: 'Email is required' })
      .email({ message: 'Invalid email format' })
      .max(100, { message: 'Email cannot exceed 100 characters' })
  });

  const validate = (field: keyof ProfileForm) => {
    if (Object.keys($errors).length === 0) {
      disabled = false;
    }

    try {
      schema.pick({ [field]: true } as any).parse({ [field]: profileForm[field] });

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

  const fileSchema = z.instanceof(File, {
    message: 'You must upload a valid file.'
  });

  const handleFileChange = (event: any) => {
    file = event.target.files[0];

    const validation = fileSchema.safeParse(file);
    if (!validation.success) {
      fileError.set(validation.error.errors[0].message);
    } else {
      fileError.set(null);
    }
  };
</script>

<h1>Profile</h1>
<Card.Root class="mx-auto mb-4 w-full border border-primary bg-secondary hover:border">
  <Card.Content class="flex flex-col items-center space-y-3 text-sm">
    <!-- Header -->
    <div class="flex flex-col items-center space-y-3">
      <img
        src={avatarSrc || '/favicon.png'}
        alt="avatar"
        class="h-28 w-28 rounded-full border-2 border-primary shadow-sm"
      />

      <form onsubmit={uploadAvatar}>
        <div class="grid gap-4">
          <div class="grid gap-2">
            <Input type="file" id="name" accept="image/*" class="cursor-pointer" onchange={handleFileChange} />
            <span
              class="mb-2 h-2 text-sm text-red-500 transition-all duration-300 ease-in-out"
              style="opacity: {$fileError ? 1 : 0};"
            >
              {#if $fileError}
                {$fileError}
              {/if}
            </span>
          </div>
          <Button type="submit" class="w-full">Upload</Button>
        </div>
      </form>
    </div>

    <!-- Username -->
    <div class="flex w-full flex-col gap-2">
      <label class="text-sm font-medium text-gray-600" for="username">Username</label>
      <Input
        ondblclick={() => (readonly = false)}
        id="username"
        type="text"
        readonly={readonly}
        oninput={() => validate('username')}
        bind:value={profileForm.username}
        class={`mt-1 w-full rounded-lg border border-gray-300 bg-gray-100 px-3 py-2 text-sm text-gray-800`}
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

    <!-- Email -->
    <div class="flex w-full flex-col gap-2">
      <label class="text-sm font-medium text-gray-600" for="email">Email</label>
      <Input
        ondblclick={() => (readonly = false)}
        id="email"
        type="email"
        readonly={readonly}
        oninput={() => validate('email')}
        bind:value={profileForm.email}
        class={`mt-1 w-full rounded-lg border border-gray-300 bg-gray-100 px-3 py-2 text-sm text-gray-800`}
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

    <span class="ml-auto flex gap-2">
      <Button onclick={resetForm} class={`${readonly ? 'invisible' : ''} w-[100px]`}>Reset</Button>

      <Button onclick={saveProfile} disabled={disabled || loading} class={`${readonly ? 'invisible' : ''} w-[100px]`}
        >Save</Button
      >
    </span>
  </Card.Content>
</Card.Root>
