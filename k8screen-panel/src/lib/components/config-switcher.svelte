<script lang="ts">
  import { goto } from '$app/navigation';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu/index.js';
  import * as Sidebar from '$lib/components/ui/sidebar/index.js';
  import { useSidebar } from '$lib/components/ui/sidebar/index.js';
  import type { ConfigInfo } from '$lib/model/config/ConfigInfo';
  import ChevronsUpDown from 'lucide-svelte/icons/chevrons-up-down';
  import GalleryVerticalEnd from 'lucide-svelte/icons/gallery-vertical-end';
  import Plus from 'lucide-svelte/icons/plus';
  import * as Dialog from '$lib/components/ui/dialog';
  import { Input } from '$lib/components/ui/input/index.js';
  import { Button } from '$lib/components/ui/button/index.js';
  import X from 'lucide-svelte/icons/x';
  import { z } from 'zod';
  import { writable } from 'svelte/store';

  // This should be `Component` after lucide-svelte updates types
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  let {
    configs,
    user,
    updateConfig,
    uploadFile,
    deleteFile
  }: {
    configs: ConfigInfo[];
    user: { name: string; email: string; avatar: string; active_config: string };
    updateConfig: (config: string) => void;
    uploadFile: (file: File) => Promise<void>;
    deleteFile: (fileName: string) => Promise<void>;
  } = $props();

  const sidebar = useSidebar();
  const fileError = writable<string | null>(null);
  let file: File;

  const fileSchema = z.instanceof(File, {
    message: 'You must upload a valid file.'
  });

  function handleFileChange(event: any) {
    file = event.target.files[0];

    const validation = fileSchema.safeParse(file);
    if (!validation.success) {
      fileError.set(validation.error.errors[0].message);
    } else {
      fileError.set(null);
    }
  }

  const submit = () => {
    const validation = fileSchema.safeParse(file);
    if (!validation.success) {
      fileError.set(validation.error.errors[0].message);
    } else {
      fileError.set(null);
      uploadFile(file);
    }
  };
</script>

<Sidebar.Menu>
  <Sidebar.MenuItem>
    <DropdownMenu.Root>
      <DropdownMenu.Trigger>
        {#snippet child({ props })}
          <Sidebar.MenuButton
            {...props}
            size="lg"
            class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
          >
            <div
              class="flex aspect-square size-8 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground"
            >
              <GalleryVerticalEnd class="size-4" />
            </div>
            <div class="grid flex-1 text-left text-sm leading-tight">
              <span class="truncate font-semibold">
                {#if user}
                  {user.active_config}
                {:else}
                  No config available
                {/if}
              </span>
            </div>
            <ChevronsUpDown class="ml-auto" />
          </Sidebar.MenuButton>
        {/snippet}
      </DropdownMenu.Trigger>
      <DropdownMenu.Content
        class="w-[--bits-dropdown-menu-anchor-width] min-w-56 rounded-lg"
        align="start"
        side={sidebar.isMobile ? 'bottom' : 'right'}
        sideOffset={4}
      >
        <DropdownMenu.Label class="text-xs text-muted-foreground">Configs</DropdownMenu.Label>
        <style>
          .dropdown-item {
            display: flex;
            align-items: center;
            position: relative;
          }

          .dropdown-item button {
            opacity: 0;
            transition: opacity 0.2s ease-in-out;
          }

          .dropdown-item:hover button {
            opacity: 1;
          }
        </style>

        {#each configs as config, index (config.name)}
          <DropdownMenu.Item
            onSelect={() => {
              updateConfig(config.name);
              user.active_config != config.name && goto('/namespaces');
            }}
            class="dropdown-item"
          >
            <div class="flex size-6 items-center justify-center rounded-sm border">
              <GalleryVerticalEnd class="size-4 shrink-0" />
            </div>
            {config.name}
            <Button
              onclick={() => {
                deleteFile(config.name);
              }}
              type="button"
              class="ms-auto h-8 w-5"
              variant="outline"
              size="sm"
            >
              <X />
            </Button>
          </DropdownMenu.Item>
        {/each}

        <DropdownMenu.Separator />
        <div class="flex w-full justify-between">
          <Dialog.Root>
            <Dialog.Trigger class="w-full">
              <div class="flex items-center justify-start gap-2 rounded-md border bg-background p-2">
                <Plus class="size-4" />
                <div class="font-medium text-muted-foreground">Upload config file</div>
              </div>
            </Dialog.Trigger>
            <Dialog.Content>
              <Dialog.Header>
                <Dialog.Title>Upload your config file</Dialog.Title>
                <Dialog.Description>
                  <br />
                  <form onsubmit={submit}>
                    <div class="grid gap-4">
                      <div class="grid gap-2">
                        <Input type="file" id="name" class="col-span-3 cursor-pointer" onchange={handleFileChange} />
                        {#if $fileError}
                          <div class="mt-2 text-sm text-red-400">{$fileError}</div>
                        {/if}
                      </div>
                      <Button type="submit" class="w-full">Upload</Button>
                    </div>
                  </form>
                </Dialog.Description>
              </Dialog.Header>
            </Dialog.Content>
          </Dialog.Root>
        </div>
      </DropdownMenu.Content>
    </DropdownMenu.Root>
  </Sidebar.MenuItem>
</Sidebar.Menu>
