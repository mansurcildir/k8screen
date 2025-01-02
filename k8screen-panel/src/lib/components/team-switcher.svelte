<script lang="ts">
  import { goto } from '$app/navigation';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu/index.js';
  import * as Sidebar from '$lib/components/ui/sidebar/index.js';
  import { useSidebar } from '$lib/components/ui/sidebar/index.js';
  import type { ConfigItem } from '$lib/model/config/ConfigItem';
  import ChevronsUpDown from 'lucide-svelte/icons/chevrons-up-down';
  import GalleryVerticalEnd from 'lucide-svelte/icons/gallery-vertical-end';
  import Plus from 'lucide-svelte/icons/plus';

  // This should be `Component` after lucide-svelte updates types
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  let {
    configs,
    user,
    updateConfig
  }: {
    configs: ConfigItem[];
    user: { name: string; email: string; avatar: string; config: string };
    updateConfig: (config: string) => void;
  } = $props();
  const sidebar = useSidebar();
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
              class="bg-sidebar-primary text-sidebar-primary-foreground flex aspect-square size-8 items-center justify-center rounded-lg"
            >
              <GalleryVerticalEnd class="size-4" />
            </div>
            <div class="grid flex-1 text-left text-sm leading-tight">
              <span class="truncate font-semibold">
                {#if user}
                  {user.config}
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
        <DropdownMenu.Label class="text-muted-foreground text-xs">Configs</DropdownMenu.Label>
        {#each configs as config, index (config.name)}
          <DropdownMenu.Item
            onSelect={() => {
              updateConfig(config.name);
              user.config != config.name && goto('/namespaces');
            }}
            class="gap-2 p-2"
          >
            <div class="flex size-6 items-center justify-center rounded-sm border">
              <GalleryVerticalEnd class="size-4 shrink-0" />
            </div>
            {config.name}
            <DropdownMenu.Shortcut>{index + 1}</DropdownMenu.Shortcut>
          </DropdownMenu.Item>
        {/each}
        <DropdownMenu.Separator />
        <DropdownMenu.Item class="gap-2 p-2">
          <div class="bg-background flex size-6 items-center justify-center rounded-md border">
            <Plus class="size-4" />
          </div>
          <div class="text-muted-foreground font-medium">Add config file</div>
        </DropdownMenu.Item>
      </DropdownMenu.Content>
    </DropdownMenu.Root>
  </Sidebar.MenuItem>
</Sidebar.Menu>
